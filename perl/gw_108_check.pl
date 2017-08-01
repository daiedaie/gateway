#!/usr/bin/perl -w

use strict;
use DBI();
use Getopt::Long;
use Time::Local;
use POSIX;
use LogFun;
use Encode;
use Net::FTP;
use warnings;
use POSIX qw(strftime);


#定时扫描时间(单位：秒)
my $CHECK_CYCLE=30;

#日志路径
my $LOG_PATH="/home/gateway/perl/logs";

#ORACLE连接
##########生产###########################################
my $dbname="GATEWAY_52";
my $user = "gateway";
my $passwd = "gateway1234QWER";

##########测试#########################################
#my $dbname="GATEWAY_133";
#my $user = "gateway";
#my $passwd = "gateway666";

main:{
	#设置日志 
  SetLogPath($LOG_PATH);
  SetLogLevel('D','D');  #文件和终端日志级别：'D', 'I', 'W', 'E', 'F', 'C'
  SetLogSize(50000);#文件切换行数
  SetLogHead("108_CHECK","108_CHECK");#文件和日志 前缀
  OpenLog;
  WriteInfo("===================开始运行========================\n");
  WriteInfo("LOG_PATH:$LOG_PATH\n");	 
  WriteInfo("CHECK_CYCLE:$CHECK_CYCLE\n");	
  WriteInfo("Oracle:$user/$dbname\n");
	while(1){
		my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";
		
		#查询所有待检查的任务
		my $checkTask=$dbh->prepare(
			 "select t.task_id,                                  ".      
       "       t.service_id,                               ".
       "       t.service_code,                             ".
       "       t.service_name,                             ".
       "       t.user_id,                                  ".
       "       t.login_name,                               ".
       "       t.data_progress_status,                     ".
       "       t.file_id,                                   ".
       "       t.ftp_ip,                                   ".
       "       t.ftp_user,                                 ".
       "       t.FTP_PASSWORD,                             ".     
       "       t.FILE_PATH,                                ".
       "       t.FILE_NAME,                                ".  
       "       t.unzip_name,                               ".
       "       t.desen_type,                               ".     
       "			 t.max_check_num, 													 ".
       "			 t.check_audit,															 ".
       "			 t.output_num,															 ".
       "       t.field_value,                              ".
       "       t.check_file_id                             ".
       "from (																						 ".
       "select tmp.*, rownum rnum                          ".
       "from (                                             ".
       "select b.task_id,                                  ".    
       "       b.service_id,                               ".    
       "       s.service_code,                             ".    
       "       s.service_name,                             ".    
       "       b.user_id,     														 ".
       "       u.login_name,                               ".      
       "       b.data_progress_status,                     ".    
       "       a.file_id,                                   ".
       "       a.ftp_ip,                                   ".     
       "       a.ftp_user,                                 ".     
       "       a.FTP_PASSWORD,                             ".     
       "       a.FILE_PATH,                                ".     
       "       a.FILE_NAME,                                ".
       "       a.unzip_name,                                ".     
       "       f.desen_type,                               ".   
       "       f.max_check_num,                            ".     
       "       decode(f.check_audit,1,1,0) check_audit,    ".
       "       f.output_num,    													 ".
       "       b.field_value,                              ".
       "       f.check_file_id                             ".
       "from GW_MODEL_DATA_FILE a                          ".    
       "inner join GW_MODEL_DATA_FETCH_TASK b on a.task_id = b.task_id    ".                
       "inner join GW_MODEL_DATA_FETCH f on f.fetch_id = b.fetch_id and f.audit_status=2  ".
       "inner join GW_SERVICE s on s.service_id=b.service_id     										  ".
       "inner join gw_user u on b.user_id = u.user_id and u.status=1                  ".
       "inner join gw_user org on u.org_id = org.org_id and org.user_type='orgUser'   ".
       "where f.desen_type='2' and b.data_progress_status='4' and b.Task_status = '1' and a.FILE_TYPE = '1'   			".
       "order by b.data_progress_status,org.run_level,u.run_level											".
       ") tmp ) t  where rnum=1"
		);
		$checkTask->execute;
		
		my $gatewayRowId = "gateway_row_id";
		my $gatewayTaskId = "gateway_task_id";
		my $gatewayRowData = "row_data";
		while(my ($taskId,$serviceId,$serviceCode,$serviceName,$userId,$loginName,$dataProgressStatus,$dataFileId,$ftpIp,$ftpUser,$ftpPassword,$filePath,$fileName,$unzipName,$desenType,$maxCheckNum,$checkAudit,$outputNum,$fieldValue,$checkFileId) = $checkTask->fetchrow_array()){			
			eval{
				WriteInfo("taskId=$taskId rule check start...\n");
				WriteInfo("Rule Check Info:taskId=$taskId,serviceId=$serviceId,serviceCode=$serviceCode,userId=$userId,dataProgressStatus=$dataProgressStatus,filePath=$filePath,fileName=$fileName,desenType=$desenType,checkNum=$maxCheckNum,outputNum=$outputNum.\n");
				#检查结果缓存表是否存在
				my $cacheTable=$dbh->prepare("select count(*) from all_tables where table_name = 'GW_SERVICE_CHECK_$serviceId'");
				$cacheTable->execute;
				my $cacheCount=$cacheTable->fetchrow_array();
				$cacheTable->finish();
									
				#如果缓存表不存在，则创建表
				if($cacheCount == 0){
					WriteInfo("The cache table GW_SERVICE_CHECK_$serviceId not exist,must to create.\n"); 		 	
					
					#如果获取到字段信息不为空则建立缓存表
					my $tableStr = "create table GW_SERVICE_CHECK_$serviceId($gatewayRowId number,$gatewayTaskId number,$gatewayRowData varchar2(4000)) ";
					WriteInfo("Cache table sql:$tableStr\n");	
					my $sth_createTab=$dbh->prepare($tableStr);   
					$sth_createTab->execute;	 
					$sth_createTab->finish(); 
					
					#建表后加索引
					my $sth_createIndex=$dbh->prepare("create unique index uq_gw_service_check_$serviceId on gw_service_check_$serviceId ($gatewayTaskId, $gatewayRowId)");
					$sth_createIndex->execute;
					$sth_createIndex->finish();
				}
				
				my $delCheck=$dbh->prepare("delete gw_service_check_$serviceId where $gatewayTaskId=$taskId");
				$delCheck->execute;
				
				$delCheck=$dbh->prepare("delete gw_service_check_record where Task_Id=$taskId");
				$delCheck->execute;
				$delCheck->finish;
				
				my $findMaxCheckBatch=$dbh->prepare("select max(check_batch) from gw_service_check_rule where user_id=$userId and service_id=$serviceId");
				$findMaxCheckBatch->execute;
				my $maxCheckBatch=$findMaxCheckBatch->fetchrow_array();
				
				#检查规则
				my $checkRule=$dbh->prepare(
					"select t.reorder,t.check_type,d.dict_value,t.check_rule,t.field_code,t.field_name ".
					"from gw_service_check_rule t ".
					"left join gw_sys_dict d on d.dict_code='DICT_CHECK_RULE_TYPE' and d.dict_key=t.check_type ".
					"where t.user_id=$userId and t.service_id=$serviceId and t.check_batch=$maxCheckBatch ".
					"order by t.reorder");
				$checkRule->execute;
				
				my %checkTypeHash;
				my %checkRuleHash;
				my %fieldHash;		#存放字段ID，根据reorder存放fieldId
				my $checkRuleStr="";	#检查规则字符串，用于保存免责日志
				my @fieldCodes;			#服务输出字段，用于保存免责日志
				my $fieldCount=0;
				my $uFpath="/home/gateway/users/$loginName/result";
				while(my ($reorder,$checkType,$checkTypeName,$checkRule,$fieldCode,$fieldName) = $checkRule->fetchrow_array()){
					$fieldCount++;	#总合规检查字段数
					
					$checkTypeHash{$reorder}=$checkType;
					if($checkType=="1"){	#长度检查
						$checkRuleHash{$reorder}=$checkRule;
					}elsif ($checkType=="2"){ #字典数值检查
						my @values = split /;/ ,$checkRule;
						$checkRuleHash{$reorder."_checkType2"}=$checkRule;
						foreach(@values){
							$checkRuleHash{$reorder."_".$_}=1;
						}
					}elsif ($checkType=="3"){ #范围内检查
						my @values = split /;/ ,$checkRule;
						$checkRuleHash{$reorder."_checkType3"}=$checkRule;
						$checkRuleHash{$reorder."_begin"}=$values[0];
						$checkRuleHash{$reorder."_end"}=$values[1];
					}
					
					push(@fieldCodes,"$fieldCode");
					$checkRuleStr = $checkRuleStr."$fieldCode($fieldName)=检查类型：$checkTypeName,检查规则：$checkRule;\n";
				}
				$checkRule->finish();
				
				my $gzFile_path="$filePath/$fileName";
				my $txtFile_path=$filePath."/".$unzipName;
				
				if(!-e $txtFile_path){
					my $gzOutputFile=$filePath.'/'.$fileName;
					if(!-e $gzOutputFile){
						WriteInfo("$gzFile_path 服务输出的gz文件不存在!\n"); 
						next;
					}
			  	
			    #解压gz文件
			    my $gunzip = 'gunzip -c '.$gzFile_path." > ".$txtFile_path;
			    WriteInfo("file gunzip $gzFile_path start.\n");
			  	system($gunzip);
			  	WriteInfo("file gunzip $gzFile_path finish.\n");
			  }
			  
				if (!-e $txtFile_path) {WriteInfo("$txtFile_path 文件不存在!\n"); next;}
				WriteInfo("check file=".$txtFile_path."\n");
				
				#修改任务数据就绪状态，合规检查中
				my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=7,DOWNLOAD_START_TIME=sysdate,check_file_id=$checkFileId,check_batch=$maxCheckBatch,CHECK_AUDIT='$checkAudit',OUTPUT_NUM=$outputNum where task_id=$taskId");
				$task->execute;
				$task->finish;
				WriteInfo("update gw_model_data_fetch_task set data_progress_status=7,DOWNLOAD_START_TIME=sysdate,check_file_id=$checkFileId,check_batch=$maxCheckBatch,CHECK_AUDIT='$checkAudit',OUTPUT_NUM=$outputNum where task_id=$taskId.\n");
				
				my $irregularTxtFile="Irregular_".$unzipName;
				my $irregularTxtFile_Path = "$uFpath/$irregularTxtFile";
				#不合规输出txt文件，一般不会有
				if (-f $irregularTxtFile_Path) {
					WriteInfo("delete irregular txt file $irregularTxtFile_Path start!\n");
			 		unlink $irregularTxtFile_Path;
			 		WriteInfo("delete irregular txt file $irregularTxtFile_Path Success!\n");		
			 	}
				WriteInfo("irregularTxtFile=$uFpath/$irregularTxtFile.\n");
				WriteInfo("taskId=$taskId file rule check start. fileName=$txtFile_path.\n");
				
				my $rowNum = 0;
				my $rowIrregularCount = 0;	#不合规数据行数据量
				my $dbLimit = 1000;					#不合规行数据插入到数据库的限制数量，超过不再插入到数据
				my $irregularFileNum = 0;		#不合规文件的序号
				my $rowData = "";
				my $checkResult=0;	#任务检查结果
				open(IRREGULAR_FILE,"> $uFpath/$irregularTxtFile") or die "Open file $uFpath/$irregularTxtFile error.\n";
				open CHECK_FILE,$txtFile_path;
				while(<CHECK_FILE>){
					$rowNum++;
					#chomp;
					s/[\r\n]//g;	#去除换行符
					$rowData = decode('gbk',$_);
					
					my @fieldValues = split /\|/, $rowData;
					my $fieldNum = 0;		#fieldNum是输出文件中的字段顺序号，也是数据库中服务输出字段的字段顺序号
					my $rowIrregular = 0;	#当前行是否有不合规的记录
					my $irregularField = "";		#不合规字段，如：不合规字段序号:不合规字段值|不合规字段序号:不合规字段值
					foreach(@fieldValues){
						$fieldNum++;
						#出现未定义检查规则的字段，表示该字段为多出来(未配规则)的非法字段
						if(!defined $checkTypeHash{$fieldNum}){
							$checkResult=3;
							WriteInfo("file exists illegal data!rowNum=$rowNum,rule fieldCount=$fieldCount, fieldNum=$fieldNum.\n");
							last;
						}
						
						my $type = $checkTypeHash{$fieldNum};
						my $fieldIrregular = "";	#当前字段内容不合规的记录，不为空则表示有字段的内容不合规
						if($type eq "2"){	#字典值检查
							my $key = encode("gbk",$fieldValues[$fieldNum-1]);
							if(!defined $checkRuleHash{$fieldNum."_".$key}){
								$fieldIrregular = $checkRuleHash{$fieldNum."_checkType2"};
							}
						}elsif($type eq "3"){ #范围检查
							if($fieldValues[$fieldNum-1] < $checkRuleHash{$fieldNum."_begin"} || $fieldValues[$fieldNum-1] > $checkRuleHash{$fieldNum."_end"}){
								$fieldIrregular = $checkRuleHash{$fieldNum."_checkType3"};
							}
						}elsif($type eq "1"){	#长度检查
							if(length($fieldValues[$fieldNum-1]) > $checkRuleHash{$fieldNum}){
								$fieldIrregular = $checkRuleHash{$fieldNum};
							}
						}
						#存在不合规的字段
						if($fieldIrregular ne ""){
							$rowIrregular = 1;	#当前行存在不合规的记录
							$irregularField = $irregularField.(($irregularField eq ""?"":"|").$fieldNum.":".$fieldValues[$fieldNum-1]);
							
							#将不合规的字段记录保存到数据库,超过不再继续插入到数据库
							if($rowIrregularCount <= $dbLimit){
								my $checkRecord=$dbh->prepare("insert into gw_service_check_record(record_id,row_id,task_id,service_id,check_type,check_rule,FIELD_SORT) ".
																					"values(seq_gw_service_check_record.nextval,$rowNum,$taskId,$serviceId,'$type','$fieldIrregular',$fieldNum)");
								$checkRecord->execute;
								$checkRecord->finish;
							}
						}
					}
					
					#判断输出字段是否多于或少于检查规则
					my $warnType=0;
					if($checkResult==3){#输出文件字段多于检查规则
						$warnType=1;	
					}elsif($fieldCount!=$fieldNum){#输出文件字段少于检查规则
						$checkResult=4;
						$warnType=2;
						WriteInfo("file row lack field data!rowNum=$rowNum,rule fieldCount=$fieldCount, file fieldCount=$fieldNum.\n");
					}
					if($checkResult==3 || $checkResult==4){
						$rowIrregularCount++;
						WriteInfo("task check_result=$checkResult, task abort.\n");
						
						my $warnInfo=$dbh->prepare("delete gw_service_check_warn where task_id=$taskId");
						$warnInfo->execute;
						
						$warnInfo=$dbh->prepare("insert into gw_service_check_warn(warn_id,warn_type,warn_row,task_id,data_file_id,row_data) ".
																		"values(seq_gw_service_check_warn.nextval,$warnType,$rowNum,$taskId,$dataFileId,'$_')");
						$warnInfo->execute;
						$warnInfo->finish();
						
						last;
					}
					
					#存在不合规的数据，将行数据插入数据库
					if($rowIrregular == 1){
						$rowIrregularCount++;
						$irregularFileNum++;
						
						#超过不再继续插入到数据库
						if($rowIrregularCount <= $dbLimit){
							my $str = $_;
							#$str =~ s/\",\"/\',\'/g; #替换","为',' ---csv换成txt后不需要去除引号
							
							#将分隔符|转成逗号，并加引号，所有字段都加上单引号，转成字符串插入缓存表
							#$str =~ s/\|/\',\'/g; 
							#$str ="\'$str\'";
							my $insertSql=$dbh->prepare("insert into gw_service_check_$serviceId($gatewayRowId,$gatewayTaskId,$gatewayRowData) values($rowNum,$taskId,'$str')");
							$insertSql->execute;
							$insertSql->finish();
						}
						
						printf IRREGULAR_FILE $irregularFileNum.",".$rowNum.",".$_.",".$irregularField;
						printf IRREGULAR_FILE "\r\n";
					}
					
					if($rowNum % 500000 == 0){
						my $row = $rowNum/10000;
						WriteInfo("rule checking in $row w. irregular row count $rowIrregularCount.\n");
					}
					
					#不合规的行数据量超出最大检查数据量，则退出不再继续检查
					if($maxCheckNum != 0 && $rowIrregularCount > $maxCheckNum){
						WriteInfo("taskId=$taskId不合规数据量($rowIrregularCount)超出不合规最大检查行数($maxCheckNum)，自动退出检查.\n");
						last;
					}
					next;
				}
				close CHECK_FILE;
				close IRREGULAR_FILE;
				WriteInfo("taskId=$taskId file rule check end.\n");
				
				#存在不合规的数据，将不合规输出文件压缩
				if($rowIrregularCount > 0){
					my $irregularGzFile = $irregularTxtFile.".gz";
					#压缩不合规的输出文件
					WriteInfo("gzip irregular txt file $uFpath/$irregularGzFile start.\n");
					system("cd $uFpath && gzip -cf $irregularTxtFile > $irregularGzFile");
					WriteInfo("gzip irregular txt file $uFpath/$irregularGzFile end.\n");
					
					if (-f $irregularTxtFile_Path) {
						WriteInfo("delete irregular txt file $irregularTxtFile_Path start!\n");
				 		unlink $irregularTxtFile_Path;
				 		WriteInfo("delete irregular txt file $irregularTxtFile_Path Success!\n");		
				 	}
				 	
				 	my $irregularDataFile=$dbh->prepare("insert into GW_MODEL_DATA_FILE (FILE_ID,TASK_ID,MODEL_ID,USER_ID,FILE_TYPE,FILE_STATUS,FILE_PATH,FILE_NAME,CREATE_TIME,FTP_IP,FTP_PORT)".
			      														 "values (SEQ_MODEL_DATA_FILE.NEXTVAL,$taskId,null,$userId,3,1,'$uFpath','$irregularGzFile',sysdate,null,null)");
				 	$irregularDataFile->execute;
				 	$irregularDataFile->finish;
				}
				
				my $dataProgressStatus=($checkResult!=0||$rowIrregularCount>0||$checkAudit==1)?8:10;
				my $downloadTime="";
				my $downloadEndTime=$dataProgressStatus==10?",DOWNLOAD_END_TIME=sysdate":"";
				my $dataNum=$rowNum;
				if($checkResult==0){	#检查过程中没出现其他非法情况
					if($rowIrregularCount==0){
						$checkResult=0;
					}elsif(($maxCheckNum==0 && $rowIrregularCount>0) || ($maxCheckNum != 0 && $rowIrregularCount>0 && $rowIrregularCount<=$maxCheckNum)){
						$checkResult=1;
						$dataNum="null";
					}else{
						$checkResult=2;
						$dataNum="null";
					}
				}
				
				#存在不合规的数据，或者合规的数据也必须审核，生成审核待办任务
				if($dataProgressStatus==8){
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
					if($checkResult==0 && $rowIrregularCount==0){
						$planContent="数据用户：$loginName请求的服务(服务编码=$serviceCode,服务名称=$serviceName,服务周期=$fieldValue)输出文件合规检查通过，请审核。";
					}
	
					#增加代办任务
					my $workPlan=$dbh->prepare("insert into gw_work_plan(plan_id,plan_title,plan_type,plan_content,plan_state,create_time,create_user_id) ".
														"values($planId,'合规检查审核',19,'$planContent',1,sysdate,1)");
					$workPlan->execute;
					$workPlan->finish;
					if($checkResult!=0 || $rowIrregularCount>0){
						WriteInfo("存在不合规数据，生成审核代办。planId=$planId.\n");
					}else{
						WriteInfo("合规检查通过，根据配置生成审核代办。planId=$planId.\n");
					}
				}
				
				if($checkResult==0&&$rowIrregularCount==0){#全部合规的文件，需要根据输出行数的配置另外生成一个输出文件
					my $timeStr =strftime("%Y%m%d%H%M%S", localtime(time));
					my $outputGzFile = substr($fileName,0,index($fileName,".txt.gz"))."_".$loginName."_$timeStr.txt.gz";
									
					#全部输出，直接复制原文件
					if($outputNum==0){
						my $cp = 'cp -f '.$txtFile_path." $uFpath/".$unzipName;
				    WriteInfo("cp $txtFile_path to $uFpath/$unzipName start.\n");
				  	system($cp);
				  	WriteInfo("cp $txtFile_path to $uFpath/$unzipName finish.\n");
				  	
						my $cpZip = 'cp -f '.$gzFile_path." $uFpath/".$outputGzFile;
				    WriteInfo("cp $gzFile_path to $uFpath/$outputGzFile start.\n");
				  	system($cpZip);
				  	WriteInfo("cp $gzFile_path to $uFpath/$outputGzFile finish.\n");
					}else{
						my $outRowNum=0;
						#输出文件的全路径
						my $outputTxtFile_path = $uFpath.'/'.$unzipName;
					 	if (-f $outputTxtFile_path) {
					 		unlink $outputTxtFile_path;
					 		WriteInfo("delete temporary file $outputTxtFile_path Success!\n");		
					 	}
						open(OUTPUT_FILE,"> $outputTxtFile_path") or die "Open file $outputTxtFile_path error.\n";
						open DATA_FILE,$txtFile_path;
						WriteInfo("create output file $outputTxtFile_path start.\n");
						while(<DATA_FILE>){
							$outRowNum++;
							#超过最大输出行数，退出
							if($outRowNum>$outputNum){
								$outRowNum--;
								WriteInfo("output file $uFpath/$unzipName achieve max row,outputNum=$outputNum.\n");
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
						WriteInfo("create output file $outputTxtFile_path end. outputNum=$outputNum.\n");
						
						#压缩输出文件
						WriteInfo("gzip file $uFpath/$outputGzFile start.\n");
						system("cd $uFpath && gzip -cf $unzipName > $outputGzFile");
						WriteInfo("gzip file $uFpath/$outputGzFile end.\n");
					}
					my $dataFile=$dbh->prepare("insert into GW_MODEL_DATA_FILE (FILE_ID,TASK_ID,MODEL_ID,USER_ID,FILE_TYPE,FILE_STATUS,FILE_PATH,FILE_NAME,CREATE_TIME,FTP_IP,FTP_PORT,UNZIP_NAME)".
		      														 "values (SEQ_MODEL_DATA_FILE.NEXTVAL,$taskId,null,$userId,2,1,'$uFpath','$outputGzFile',sysdate,null,null,'$unzipName')");
	      	$dataFile->execute;
	      	$dataFile->finish;
						
					my $searchTaskStatus=$dbh->prepare("select task_status from gw_model_data_fetch_task where task_id=$taskId");
					$searchTaskStatus->execute;
					my $taskStatus=$searchTaskStatus->fetchrow_array();
					$searchTaskStatus->finish;
				
					#任务有可能已被修改规则后置为失效,push前检查一次
					if($taskStatus==1){
						#检查通过的，将文件push到指定ftp，且记录免责日志
						if($dataProgressStatus==10){
							my $userFtpInfo=$dbh->prepare("select push_ftp,ftp_ip,ftp_port,ftp_username,ftp_password,ftp_path from gw_user where user_id=$userId");
			      	$userFtpInfo->execute;
			      	my ($pushFtp,$ftpIp,$ftpPort,$ftpUsername,$ftpPassword,$ftpPath)=$userFtpInfo->fetchrow_array();
			      	$userFtpInfo->finish;
							
							if(defined $pushFtp && $pushFtp==1){
							 	#上传文件
								my $ftp_flag=1; #0:down  1:up
								my $rst = FTPFile($ftpIp,$ftpUsername,$ftpPassword,$uFpath,$ftpPath,$outputGzFile,$ftp_flag);
								if($rst != 0 ){
									$dataProgressStatus=13;	#服务发送失败，会在quartz中自动重发
								}else{
									$dataProgressStatus=12;	#服务已送达
								 	WriteInfo("Upload file to $ftpIp succ!\n");
								 	
								 	#用于更新用户下载时间
								 	$downloadTime=",download_time=sysdate";
								 	
								 	my $serviceFields = join(",",@fieldCodes);
									my $liabilityLog=$dbh->prepare("insert into gw_model_liability_log(log_id,user_id,task_id,model_fields,model_data_num,create_user,create_time,service_id,desen_rule_content) ".
																		 "values(seq_model_liability_log.nextval,$userId,$taskId,'$serviceFields',$dataNum,'root',sysdate,$serviceId,'$checkRuleStr')");
									$liabilityLog->execute;
									$liabilityLog->finish;
								}
							}
						}
					}else{
						WriteInfo("task status Invalid, task abort!\n");
					}
				}
											
				#修改任务数据就绪状态
				$task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=$dataProgressStatus,data_num=$dataNum,check_result=$checkResult,max_check_num=$maxCheckNum,CHECK_IRREGULAR_NUM=$rowIrregularCount,check_num=$rowNum $downloadEndTime $downloadTime where task_id=$taskId");
				$task->execute;
				$task->finish;
				WriteInfo("update gw_model_data_fetch_task set data_progress_status=$dataProgressStatus,data_num=$dataNum,check_result=$checkResult,max_check_num=$maxCheckNum,CHECK_IRREGULAR_NUM=$rowIrregularCount,check_num=$rowNum $downloadEndTime $downloadTime where task_id=$taskId.\n");
				
				#检查完成，删除解压后的txt文件
			 	#if (-f $txtFile_path) {
			 		#unlink $txtFile_path;
			 		#WriteInfo("delete gunzip output txt file $txtFile_path Success!\n");		
			 	#}
				WriteInfo("taskId=$taskId task rule check end.\n");
			};#eval end
			
			if ($@){
				#合规检查过程中出现问题，修改任务数据就绪状态为处理失败
				my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId");
				$task->execute;
				$task->finish;
				WriteInfo("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId.\n");
				
				WriteError("taskId=$taskId task rule check error!.\n");
				WriteError("$@.\n");
			}
		}#task end
		
		#2表示脱敏/检查时间间隔
	  my $cycleConfig=$dbh->prepare("select config_unit,config_value from gw_sys_cnfig where config_type=2");
	  $cycleConfig->execute;
		my ($cycleType,$cycleValue)=$cycleConfig->fetchrow_array();
		$cycleConfig->finish;
		if(!defined $cycleValue){
		 	$cycleValue=$CHECK_CYCLE;
		}else{
		  #1表示单位为分
			if(defined $cycleType && $cycleType==1){
			 	$cycleValue=$cycleValue*60;
			}
		}
		$dbh->disconnect;
		WriteInfo("Scan rule check finished, sleep $cycleValue s. \n");
		sleep $cycleValue;
	}
	CloseLog;
}

#信号捕抓处理,kill 或ctrl+c
sub handle{
	CloseLog;
	exit(0);
}
$SIG{TERM}=$SIG{INT}=\&handle;


sub FTPFile(){  
	my ($FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag) = @_;
	#WriteInfo("FTP文件信息：$FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag. \n");
	eval{    
		  	
		my $ftp=Net::FTP->new("$FTP_ADDR",Passive=>1,Debug=>0,Timeout=>30) or die "Could not connect.\n"; 
               
		#登录到FTP                                                                      
		$ftp->login($Fuser,$Fpassword) or die "Could not login.$! \n";            
		$ftp->binary() or die "binary failed. \n";
		#切换目录                                                                       
		$ftp->cwd($Fpath) or die "Cannot change working directory ", $ftp->message;   
		

		if($ftp_flag == 0){
		  $ftp->get($Fname, "$Lpath/$Fname") or die "Could not get remotefile:$Fname ", $ftp->message."\n"; 
	  }
		if($ftp_flag == 1){
			#print "$Fpath  =====$FTP_ADDR====== $Lpath/$Fname  \n"; 
		  #$ftp->put("$Lpath/$Fname",$Fpath) or  "Could not put file:$Fname ", $ftp->message."\n"; 
		  $ftp->put("$Lpath/$Fname");
	  }  
	  $ftp->quit; 
	  
	};
  if ($@){
  	if($ftp_flag == 0){
  		WriteError("FTP down file fail! host:$FTP_ADDR,user:$Fuser,remoPath:$Fpath,localfile:$Fname\n");
  		WriteError("$@.\n");
  	}	
  	
  	if($ftp_flag == 1){
  		WriteError("FTP up file fail! host:$FTP_ADDR,user:$Fuser,remoPath:$Fpath,localfile:$Fname\n");
  		WriteError("$@.\n");
  	}	
  	return 1;
  }else{
  	
  	if($ftp_flag == 0){
  		WriteInfo("FTP down file succ! host:$FTP_ADDR,user:$Fuser,remoPath:$Fpath,localfile:$Fname\n");
  	}	
  	
  	if($ftp_flag == 1){
  		WriteInfo("FTP up file succ! host:$FTP_ADDR,user:$Fuser,remoPath:$Fpath,localfile:$Fname\n");
  	}	
  	return 0;
  }	 	 	
}