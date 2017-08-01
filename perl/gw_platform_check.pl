#!/usr/bin/perl -w

use strict;
use DBI();
use Getopt::Long;
use Time::Local;
use POSIX;
use LogFun;
use Encode;

#定时扫描时间(单位：秒)
my $CHECK_CYCLE=30;

#日志路径
my $LOG_PATH="/home/gateway/perl/logs";

#ORACLE连接
my $dbname="GATEWAY_133";
#my $dbname="orcl";
my $user = "gateway";
my $passwd = "gateway666";

main:{
	#设置日志 
  SetLogPath($LOG_PATH);
  SetLogLevel('D','D');  #文件和终端日志级别：'D', 'I', 'W', 'E', 'F', 'C'
  SetLogSize(50000);#文件切换行数
  SetLogHead("PLATFORM_CHECK","PLATFORM_CHECK");#文件和日志 前缀
  OpenLog;
  WriteInfo("===================开始运行========================\n");
  WriteInfo("LOG_PATH:$LOG_PATH\n");	 
  WriteInfo("CHECK_CYCLE:$CHECK_CYCLE\n");	
  WriteInfo("Oracle:$user/$dbname\n");
	while(1){
		my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";
		
		#查询所有待检查的任务
		my $checkTask=$dbh->prepare(
			 "select t.task_id,																			".	
			 "			 t.service_id,       														".	                             
       "			 t.service_code,                          			".	
       "       t.service_name,                                ".  
       "			 t.user_id,                                     ".  
       "			 t.login_name,                                  ".  
       "			 t.data_progress_status,                        ". 
       "			 t.file_id,																			". 
       "			 t.ftp_ip,                                      ".  
       "			 t.ftp_user,                                    ".  
       "			 t.FTP_PASSWORD,                                ".  
       "			 t.FILE_PATH,                                   ".  
       " 			 t.FILE_NAME,                                   ".  
       "			 t.desen_type,                                  ".  
       "			 t.max_check_num,																		".
       "			 t.check_audit,																	".
       "			 t.output_num,																	".
       "			 t.field_value																	".
       "from (																							  ".
       "select tmp.*,rownum rnum															".			
       "from (																								".	
			 "select b.task_id,                                     ".  
       "			 b.service_id,                                  ".  
       "			 s.service_code,                                ".  
       "			 s.service_name,                                ".  
       "			 b.user_id,																			".
       "       u.login_name,                                   ".			
       "			 b.data_progress_status,                        ".  
       "			 a.file_id,                                      ". 
       "			 a.ftp_ip,                                      ".  
       "	 		 a.ftp_user,                                    ".  
       "	  	 a.FTP_PASSWORD,                                ".  
       "			 a.FILE_PATH,                                   ".  
       "			 a.FILE_NAME,                                   ".  
       "			 f.desen_type,                                  ".  
       "			 f.max_check_num,                                		".     
       "       decode(f.check_audit,1,1,0) check_audit,       ".
       "			 f.output_num,																	".
       "       b.field_value                               		". 
       "from GW_MODEL_DATA_FILE a                        			".	
       "inner join GW_MODEL_DATA_FETCH_TASK b on a.task_id = b.task_id                      ".							 	
       "inner join GW_MODEL_DATA_FETCH f on f.fetch_id = b.fetch_id and f.audit_status=2   	".								
       "inner join GW_SERVICE s on s.service_id=b.service_id																".
       "inner join gw_user u on b.user_id = u.user_id and u.status=1                  			".
       "inner join gw_user org on u.org_id = org.org_id and org.user_type='orgUser' 				".																		
       "where f.desen_type='1' and b.DATA_PROGRESS_STATUS = '6' and b.Task_status = '1' and a.FILE_TYPE = '2'  ".
       "order by b.data_progress_status,org.run_level,u.run_level    ".
       ") tmp ) t where rnum=1 "
		);
		$checkTask->execute;
		
		my $gatewayRowId = "gateway_row_id";
		my $gatewayTaskId = "gateway_task_id";
		while(my ($taskId,$serviceId,$serviceCode,$serviceName,$userId,$loginName,$dataProgressStatus,$fileId,$ftpIp,$ftpUser,$ftpPassword,$filePath,$fileName,$desenType,$maxCheckNum,$checkAudit,$outputNum,$fieldValue) = $checkTask->fetchrow_array()){			
			WriteInfo("taskId=$taskId rule check start...\n");
			WriteInfo("Rule Check Info:taskId=$taskId,serviceId=$serviceId,serviceCode=$serviceCode,userId=$userId,dataProgressStatus=$dataProgressStatus,filePath=$filePath,fileName=$fileName,desenType=$desenType,checkNum=$maxCheckNum\n");
			#检查检查结果缓存表是否存在
			my $cacheTable=$dbh->prepare("select count(*) from all_tables where table_name = 'GW_SERVICE_CHECK_$serviceId'");
			$cacheTable->execute;
			my $cacheCount=$cacheTable->fetchrow_array();
			$cacheTable->finish();
			
			#获取表字段信息
	 		my $sth_filed=$dbh->prepare("select FIELD_CODE,FIELD_TYPE from GW_SERVICE_FIELD where GATHER_TYPE='1' and SERVICE_ID='$serviceId' order by field_id");
	 		$sth_filed->execute;		 		
			my $tableStr = "create table GW_SERVICE_CHECK_$serviceId  ( ";
			my @fieldList;	#用于建表
		 	
		 	push(@fieldList,"$gatewayRowId number");	#自定义行号，用于记录不合规数据所在行
			push(@fieldList,"$gatewayTaskId number"); #自定义任务字段
			while(my ($fieldCode,$fieldType)=$sth_filed->fetchrow_array()){
				#对字段类型做特殊处理，非字符串类型的全部转换为VARCHAR2(200)类型
				$fieldType = uc($fieldType);
				if($fieldType =~ /CHAR/){
					push(@fieldList,"$fieldCode varchar2(2000)");
			  }elsif ($fieldType =~ /LOB/){
			  	push(@fieldList,"$fieldCode $fieldType");
			  }else{
			  	$fieldType="VARCHAR2(200)";
			  	push(@fieldList,"$fieldCode $fieldType");
			  }
			}
			$sth_filed->finish();
			
			#如果缓存表不存在，则创建表
			if($cacheCount == 0){
				WriteInfo("The cache table GW_SERVICE_CHECK_$serviceId not exist,must to create.\n"); 		 	
				
				#如果获取到字段信息不为空则建立缓存表
	 			if(@fieldList){ 
	 				#建表异常需要处理			
		 			$tableStr=$tableStr.join(",",@fieldList).")";	 	
		 			WriteInfo("Cache table sql:$tableStr\n");	
		
					eval{
						my $sth_createTab=$dbh->prepare($tableStr);   
						$sth_createTab->execute;	 
						$sth_createTab->finish(); 
						
						#建表后加索引
						my $sth_createIndex=$dbh->prepare("create unique index uq_gw_service_check_$serviceId on gw_service_check_$serviceId ($gatewayTaskId, $gatewayRowId)");
						$sth_createIndex->execute;
						$sth_createIndex->finish();
	        };
	
	        if ($@){
	        	WriteError("Cache table GW_SERVICE_CHECK_$serviceId create fail!\n");
	        	next;	
	        }else{
	        	WriteInfo("Cache table GW_SERVICE_CHECK_$serviceId create succ.\n");
	        }
	 			}else{
	 				#报错
	 				WriteError("The field of cache table GW_SERVICE_CHECK_$serviceId is err! \n");
	 				next;
	 			}
			}
						
			my $delCheck=$dbh->prepare("delete gw_service_check_$serviceId where $gatewayTaskId=$taskId");
			$delCheck->execute;
			
			$delCheck=$dbh->prepare("delete gw_service_check_record where Task_Id=$taskId");
			$delCheck->execute;
			$delCheck->finish;
			
			#脱敏并合规检查,直接用脱敏的规则做合规检查
			my $checkRule=$dbh->prepare(
				"select f.field_id,f.field_code,f.field_name,t.RULE_TYPE,t.RULE_CONTENT,t.REPLACE_CONTENT,t.CONDITION_TYPE,t.CONDITION_CONTENT from gw_desen_service_field t ".
				"inner join gw_service_field f on f.field_id=t.field_id ".
				"where t.user_id=$userId and t.service_id=$serviceId ".
				"order by t.field_id");
			$checkRule->execute;
			
			my %checkTypeHash;
			my %checkRuleHash;
			my %fieldIdHash;			#存放字段ID，根据fieldName获取fieldId
			my %fieldCodeHash;		#存放字段Code，根据fieldName获取fieldCode
			while(my ($fieldId,$fieldCode,$fieldName,$ruleType,$ruleContent,$replaceContent,$conditionType,$conditionContent)=$checkRule->fetchrow_array()){
				$fieldName = decode("gbk",$fieldName);
				$ruleContent = decode("gbk",$ruleContent);
				$replaceContent = decode("gbk",$replaceContent);
				
				$fieldIdHash{$fieldName}=$fieldId;
				$fieldCodeHash{$fieldName}=$fieldCode;
				if(defined $ruleType){
					$checkTypeHash{$fieldName."_ruleType"}=$ruleType;
					$checkRuleHash{$fieldName."_ruleContent"}=$ruleContent;
					$checkRuleHash{$fieldName."_replaceContent"}=$replaceContent;
					if($ruleType=="2"){  #范围内替换
						my @contents = split /,/ ,$ruleContent;
						$checkRuleHash{$fieldName."_ruleContent"."_begin"}=$contents[0];
						if(@contents>1){
							$checkRuleHash{$fieldName."_ruleContent"."_end"}=$contents[1];
						}
					}
				}
				if(defined $conditionType){
					$checkTypeHash{$fieldName."_conditionType"}=$conditionType;
					$checkRuleHash{$fieldName."_conditionContent"}=$conditionContent;
					if($conditionType eq "in"){
						my @conditions = split /,/ ,$conditionContent;
						foreach(@conditions){
							$checkRuleHash{$fieldName."_conditionContent".$_}=1;
						}
					}
				}
			}
			$checkRule->finish();
			
			my $file="D:\\935_20150202.csv";
			$file="$filePath/$fileName";
			if (!-e $file) {WriteInfo("$file 文件不存在!\n"); next;}
			WriteInfo("check file=".$file."\n");
			
			#修改任务数据就绪状态，合规检查中
			my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=7 where task_id=$taskId");
			$task->execute;
			$task->finish;
			
			my $serviceFields;
			my @resultFieldNames;
			my @resultFieldCodes;
			my $firstRow = 0;
			my $rowNum = 0;
			my $rowIrregularCount = 0;	#不合规数据行数据量
			open CHECK_FILE,$file;
			while(<CHECK_FILE>){
				s/[\r\n]//g;	#去除换行符
				my $rowValue = decode("gbk",$_);
	
				#表头为字段名称
				if($firstRow==0){
					@resultFieldNames = split /,/ ,$rowValue; #检查时根据顺序号取得字段名，再取得字段ID
					my $fieldNum=0;
					foreach(@resultFieldNames){
						$fieldNum++;
						$resultFieldCodes[$fieldNum-1]=$fieldCodeHash{$resultFieldNames[$fieldNum-1]};		#根据字段名称取得字段编码，赋值给@resultFieldCodes
					}
					$serviceFields=join(",",@resultFieldCodes);		#保存不合规行数据时的字段编码
					$firstRow=1;
					next;
				}
				$rowNum++;
				
				#不合规的行数据量超出最大检查数据量，则退出不再继续检查
				if($maxCheckNum != 0 && $rowIrregularCount > $maxCheckNum){
					$rowNum--;
					WriteInfo("taskId=$taskId不合规数据量($rowIrregularCount)超出不合规最大检查行数($maxCheckNum)，自动退出检查.\n");
					last;
				}
				
				my @fieldValues = split /,/, $rowValue;
				my $fieldNum = 0;		#fieldNum是输出文件中的字段顺序号，根据顺序号取得字段名称，再取得字段ID
				my $rowIrregular = 0;	#当前行是否有不合规的记录
				foreach(@fieldValues){
					$fieldNum++;
					my $fieldValue = $fieldValues[$fieldNum-1];
					my $fieldName = $resultFieldNames[$fieldNum-1];
					my $rule_type = $checkTypeHash{$fieldName."_ruleType"};
					my $rule_content = $checkRuleHash{$fieldName."_ruleContent"};
					my $replace_content = $checkRuleHash{$fieldName."_replaceContent"};
					my $condition_type = $checkTypeHash{$fieldName."_conditionType"};
					my $condition_content = $checkRuleHash{$fieldName."_conditionContent"};
					
					my $fieldIrregular = "0";
					if(defined $rule_type){
						if($rule_type == "1" && index($fieldValue,$checkRuleHash{$fieldName."_ruleContent"}) > -1){		#字符替换,检查是否还包含需要替换的字符
								$fieldIrregular = "1";
						}elsif($rule_type == "2"){		#范围内替换
							#替换的起始位置在字段值的长度范围内
							if(length($fieldValues[$fieldNum-1]) >= ($checkRuleHash{$fieldName."_ruleContent"."_begin"}-1)){
								my $start = $checkRuleHash{$fieldName."_ruleContent"."_begin"};
								my $end = $checkRuleHash{$fieldName."_ruleContent"."_end"};
								my $value = "";
								if(!defined $end){
									$value = substr($fieldValue,($start-1));
								}else{
									$value = substr($fieldValue,($start-1),($end-$start+1));
								}
								#如2,5 将第2-5个截取出来，判断是否存在替换的内容（replaceContent）
								if(index($value,$checkRuleHash{$fieldName."_replaceContent"}) == -1){
									$fieldIrregular = "1";
								}
							}
						}
					}
					if($fieldIrregular != "1"){
						$rule_type="";
						$rule_content="";
						$replace_content="";
					}
					
					if(defined $condition_type){
						if($condition_type eq "in"){
							if(!defined $checkRuleHash{$fieldName."_conditionContent".$fieldValue}){
								$fieldIrregular = "2";
							}
						}else{
							if($condition_type eq "=" && $fieldValue != $condition_content){
								$fieldIrregular = "2";
							}elsif($condition_type eq "!=" && $fieldValue == $condition_content){
								$fieldIrregular = "2";
							}elsif($condition_type eq ">" && !$fieldValue > $condition_content){
								$fieldIrregular = "2";
							}elsif($condition_type eq ">=" && !$fieldValue >= $condition_content){
								$fieldIrregular = "2";
							}elsif($condition_type eq "<" && !$fieldValue < $condition_content){
								$fieldIrregular = "2";
							}elsif($condition_type eq "<=" && !$fieldValue <= $condition_content){
								$fieldIrregular = "2";
							}
						}
					}
					if($fieldIrregular != "2"){
						$condition_type = "";
						$condition_content = "";
					}
					
					#将不合规的字段记录保存到数据库
					if($fieldIrregular != "0"){
						$rowIrregular = 1;	#当前行存在不合规的记录
						$rule_content = encode("gbk",$rule_content);
						$replace_content = encode("gbk",$replace_content);
						my $checkRecord=$dbh->prepare("insert into gw_service_check_record(record_id,row_id,task_id,service_id,field_id,rule_type,rule_content,replace_content,condition_type,condition_content,FIELD_SORT) ".
																			"values(seq_gw_service_check_record.nextval,$rowNum,$taskId,$serviceId,'$fieldIdHash{$fieldName}','$rule_type','$rule_content','$replace_content','$condition_type','$condition_content',$fieldNum)");
						$checkRecord->execute;
						$checkRecord->finish;
					}
				}
				#存在不合规的数据，将行数据插入数据库
				if($rowIrregular == 1){
					$rowIrregularCount++;
					
					my $str = $_;
					#$str =~ s/\",\"/\',\'/g; #替换","为',' ---csv换成txt后不需要去除引号
					
					#将分隔符|转成逗号，并加引号，所有字段都加上单引号，转成字符串插入缓存表
					$str =~ s/,/\',\'/g; 
					$str ="\'$str\'";
					my $insertSql=$dbh->prepare("insert into gw_service_check_$serviceId($gatewayRowId,$gatewayTaskId,$serviceFields) values($rowNum,$taskId,$str)");
					$insertSql->execute;
					$insertSql->finish();
				}
				
				if($rowNum % 100000 == 0){
					my $row = $rowNum/10000;
					WriteInfo("rule checking in $row w. irregular row count $rowIrregularCount.\n");
				}
				next;
			}
			close CHECK_FILE;
			
			#存在不合规的数据，或者合规的数据也必须审核，生成审核待办任务
			if($rowIrregularCount > 0 || $checkAudit == 1){
				my $seq=$dbh->prepare("select seq_gw_work_plan.nextval from dual");
				$seq->execute;
				my $planId=$seq->fetchrow_array();
				$seq->finish();
				
				#增加代办任务参数
				my $workPlanParam=$dbh->prepare("insert into gw_work_plan_param(param_id,plan_id,param_name,param_value) ".
																	 "values(seq_gw_work_plan_param.nextval,$planId,'taskId','$taskId')");
				$workPlanParam->execute;
				$workPlanParam=$dbh->prepare("insert into gw_work_plan_param(param_id,plan_id,param_name,param_value) ".
																	 "values(seq_gw_work_plan_param.nextval,$planId,'serviceId','$serviceId')");
				$workPlanParam->execute;
				$workPlanParam=$dbh->prepare("insert into gw_work_plan_param(param_id,plan_id,param_name,param_value) ".
																	 "values(seq_gw_work_plan_param.nextval,$planId,'userId','$userId')");
				$workPlanParam->execute;
				$workPlanParam->finish;
				
				my $planContent="数据用户：$loginName请求的服务(服务编码=$serviceCode,服务名称=$serviceName,服务周期=$fieldValue)输出文件中存在不合规数据，请审核。";
				if($rowIrregularCount==0){
					$planContent="数据用户：$loginName请求的服务(服务编码=$serviceCode,服务名称=$serviceName,服务周期=$fieldValue)输出文件合规检查通过，请审核。";
				}

				#增加代办任务
				my $workPlan=$dbh->prepare("insert into gw_work_plan(plan_id,plan_title,plan_type,plan_content,plan_state,create_time,create_user_id) ".
													"values($planId,'合规检查审核',19,'$planContent',1,sysdate,1)");
				$workPlan->execute;
				$workPlan->finish;
				if($rowIrregularCount>0){
					WriteInfo("存在不合规数据，生成审核代办。planId=$planId.\n");
				}else{
					WriteInfo("合规检查通过，根据配置生成审核代办。planId=$planId.\n");
				}
			}
			#修改任务数据就绪状态
			my $dataProgressStatus=$rowIrregularCount>0||$checkAudit==1?8:10;
			my $downloadEndTime=$dataProgressStatus==10?",DOWNLOAD_END_TIME=sysdate":"";
			my $dataNum=$rowNum;	
			my $checkResult=0;
			if($rowIrregularCount==0){
				$checkResult=0;
			}elsif(($maxCheckNum==0 && $rowIrregularCount>0) || ($maxCheckNum != 0 && $rowIrregularCount>0 && $rowIrregularCount<=$maxCheckNum)){
				$checkResult=1;
			}else{
				$checkResult=2;
				$dataNum="null";
			}
			
			#全部合规的文件，需要根据输出行数的配置另外生成一个输出文件
			if($rowIrregularCount==0){
				eval{
					#outputNum==0全部输出，不需要改变。outputNum!=0需要另外生成一个输出文件
					if($outputNum!=0){
						my $outputCsvFile=substr($fileName,0,index($fileName,".csv"))."_output.csv";
						my $outRowNum=-1;  	#第一行为标题，不算行数
						open(OUTPUT_FILE,"> $filePath/$outputCsvFile") or print STDERR "Open file $filePath/$outputCsvFile error.\n";
						open DATA_FILE,$file;
						WriteInfo("create output file $filePath/$outputCsvFile start.\n");
						while(<DATA_FILE>){
							$outRowNum++;
							#超过最大输出行数，退出
							if($outRowNum>$outputNum){
								$outRowNum--;
								WriteInfo("output file $filePath/$outputCsvFile achieve max row,outputNum=$outputNum.\n");
								last;
							}
							s/[\r\n]//g;	#去除换行符
							
							printf OUTPUT_FILE $_;
							printf OUTPUT_FILE "\r\n";
							
							if($outRowNum % 500000 == 0){
								my $row = $outRowNum/10000;
								WriteInfo("output file in $row w. \n");
							}
						}
						close DATA_FILE;
						close OUTPUT_FILE;
						$dataNum="$outRowNum";	#设置任务输出文件的数据量
						WriteInfo("create output file $filePath/$outputCsvFile end. outputNum=$outputNum.\n");
						
						#删除解压后的文件，并将输出文件重命名
					 	my $outputFile = "$filePath/$outputCsvFile";
					 	my $newFile = "$filePath/$fileName";
					 	if (-f $newFile) {
					 		unlink $newFile;
					 		WriteInfo("delete temporary file $newFile Success!\n");		
					 	}
					 	rename $outputFile, $newFile or warn "rename $outputFile to $newFile failed:$!\n" and next;
					 	WriteInfo("rename $outputFile to $newFile success!.\n");
					}
				};
				
				if($@){
					WriteError("create output file error!.\n");
					WriteError("$@.\n");
					next;
				}
			}
			
			$task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=$dataProgressStatus,data_num=$dataNum,check_result=$checkResult,max_check_num=$maxCheckNum,CHECK_IRREGULAR_NUM=$rowIrregularCount,check_num=$rowNum $downloadEndTime where task_id=$taskId");
			$task->execute;
			$task->finish;
			WriteInfo("update gw_model_data_fetch_task set data_progress_status=$dataProgressStatus,data_num=$dataNum,check_result=$checkResult,max_check_num=$maxCheckNum,CHECK_IRREGULAR_NUM=$rowIrregularCount,check_num=$rowNum $downloadEndTime where task_id=$taskId.\n");
			WriteInfo("taskId=$taskId rule check end.\n");
		}
		$dbh->disconnect;
		WriteInfo("Scan rule check finished, sleep $CHECK_CYCLE s. \n");
		sleep $CHECK_CYCLE;
	}
	CloseLog;
}

#信号捕抓处理,kill 或ctrl+c
sub handle{
	CloseLog;
	exit(0);
}
$SIG{TERM}=$SIG{INT}=\&handle;