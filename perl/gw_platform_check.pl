#!/usr/bin/perl -w

use strict;
use DBI();
use Getopt::Long;
use Time::Local;
use POSIX;
use LogFun;
use Encode;

#��ʱɨ��ʱ��(��λ����)
my $CHECK_CYCLE=30;

#��־·��
my $LOG_PATH="/home/gateway/perl/logs";

#ORACLE����
my $dbname="GATEWAY_133";
#my $dbname="orcl";
my $user = "gateway";
my $passwd = "gateway666";

main:{
	#������־ 
  SetLogPath($LOG_PATH);
  SetLogLevel('D','D');  #�ļ����ն���־����'D', 'I', 'W', 'E', 'F', 'C'
  SetLogSize(50000);#�ļ��л�����
  SetLogHead("PLATFORM_CHECK","PLATFORM_CHECK");#�ļ�����־ ǰ׺
  OpenLog;
  WriteInfo("===================��ʼ����========================\n");
  WriteInfo("LOG_PATH:$LOG_PATH\n");	 
  WriteInfo("CHECK_CYCLE:$CHECK_CYCLE\n");	
  WriteInfo("Oracle:$user/$dbname\n");
	while(1){
		my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";
		
		#��ѯ���д���������
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
			#�������������Ƿ����
			my $cacheTable=$dbh->prepare("select count(*) from all_tables where table_name = 'GW_SERVICE_CHECK_$serviceId'");
			$cacheTable->execute;
			my $cacheCount=$cacheTable->fetchrow_array();
			$cacheTable->finish();
			
			#��ȡ���ֶ���Ϣ
	 		my $sth_filed=$dbh->prepare("select FIELD_CODE,FIELD_TYPE from GW_SERVICE_FIELD where GATHER_TYPE='1' and SERVICE_ID='$serviceId' order by field_id");
	 		$sth_filed->execute;		 		
			my $tableStr = "create table GW_SERVICE_CHECK_$serviceId  ( ";
			my @fieldList;	#���ڽ���
		 	
		 	push(@fieldList,"$gatewayRowId number");	#�Զ����кţ����ڼ�¼���Ϲ�����������
			push(@fieldList,"$gatewayTaskId number"); #�Զ��������ֶ�
			while(my ($fieldCode,$fieldType)=$sth_filed->fetchrow_array()){
				#���ֶ����������⴦�����ַ������͵�ȫ��ת��ΪVARCHAR2(200)����
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
			
			#�����������ڣ��򴴽���
			if($cacheCount == 0){
				WriteInfo("The cache table GW_SERVICE_CHECK_$serviceId not exist,must to create.\n"); 		 	
				
				#�����ȡ���ֶ���Ϣ��Ϊ�����������
	 			if(@fieldList){ 
	 				#�����쳣��Ҫ����			
		 			$tableStr=$tableStr.join(",",@fieldList).")";	 	
		 			WriteInfo("Cache table sql:$tableStr\n");	
		
					eval{
						my $sth_createTab=$dbh->prepare($tableStr);   
						$sth_createTab->execute;	 
						$sth_createTab->finish(); 
						
						#����������
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
	 				#����
	 				WriteError("The field of cache table GW_SERVICE_CHECK_$serviceId is err! \n");
	 				next;
	 			}
			}
						
			my $delCheck=$dbh->prepare("delete gw_service_check_$serviceId where $gatewayTaskId=$taskId");
			$delCheck->execute;
			
			$delCheck=$dbh->prepare("delete gw_service_check_record where Task_Id=$taskId");
			$delCheck->execute;
			$delCheck->finish;
			
			#�������Ϲ���,ֱ���������Ĺ������Ϲ���
			my $checkRule=$dbh->prepare(
				"select f.field_id,f.field_code,f.field_name,t.RULE_TYPE,t.RULE_CONTENT,t.REPLACE_CONTENT,t.CONDITION_TYPE,t.CONDITION_CONTENT from gw_desen_service_field t ".
				"inner join gw_service_field f on f.field_id=t.field_id ".
				"where t.user_id=$userId and t.service_id=$serviceId ".
				"order by t.field_id");
			$checkRule->execute;
			
			my %checkTypeHash;
			my %checkRuleHash;
			my %fieldIdHash;			#����ֶ�ID������fieldName��ȡfieldId
			my %fieldCodeHash;		#����ֶ�Code������fieldName��ȡfieldCode
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
					if($ruleType=="2"){  #��Χ���滻
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
			if (!-e $file) {WriteInfo("$file �ļ�������!\n"); next;}
			WriteInfo("check file=".$file."\n");
			
			#�޸��������ݾ���״̬���Ϲ�����
			my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=7 where task_id=$taskId");
			$task->execute;
			$task->finish;
			
			my $serviceFields;
			my @resultFieldNames;
			my @resultFieldCodes;
			my $firstRow = 0;
			my $rowNum = 0;
			my $rowIrregularCount = 0;	#���Ϲ�������������
			open CHECK_FILE,$file;
			while(<CHECK_FILE>){
				s/[\r\n]//g;	#ȥ�����з�
				my $rowValue = decode("gbk",$_);
	
				#��ͷΪ�ֶ�����
				if($firstRow==0){
					@resultFieldNames = split /,/ ,$rowValue; #���ʱ����˳���ȡ���ֶ�������ȡ���ֶ�ID
					my $fieldNum=0;
					foreach(@resultFieldNames){
						$fieldNum++;
						$resultFieldCodes[$fieldNum-1]=$fieldCodeHash{$resultFieldNames[$fieldNum-1]};		#�����ֶ�����ȡ���ֶα��룬��ֵ��@resultFieldCodes
					}
					$serviceFields=join(",",@resultFieldCodes);		#���治�Ϲ�������ʱ���ֶα���
					$firstRow=1;
					next;
				}
				$rowNum++;
				
				#���Ϲ������������������������������˳����ټ������
				if($maxCheckNum != 0 && $rowIrregularCount > $maxCheckNum){
					$rowNum--;
					WriteInfo("taskId=$taskId���Ϲ�������($rowIrregularCount)�������Ϲ����������($maxCheckNum)���Զ��˳����.\n");
					last;
				}
				
				my @fieldValues = split /,/, $rowValue;
				my $fieldNum = 0;		#fieldNum������ļ��е��ֶ�˳��ţ�����˳���ȡ���ֶ����ƣ���ȡ���ֶ�ID
				my $rowIrregular = 0;	#��ǰ���Ƿ��в��Ϲ�ļ�¼
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
						if($rule_type == "1" && index($fieldValue,$checkRuleHash{$fieldName."_ruleContent"}) > -1){		#�ַ��滻,����Ƿ񻹰�����Ҫ�滻���ַ�
								$fieldIrregular = "1";
						}elsif($rule_type == "2"){		#��Χ���滻
							#�滻����ʼλ�����ֶ�ֵ�ĳ��ȷ�Χ��
							if(length($fieldValues[$fieldNum-1]) >= ($checkRuleHash{$fieldName."_ruleContent"."_begin"}-1)){
								my $start = $checkRuleHash{$fieldName."_ruleContent"."_begin"};
								my $end = $checkRuleHash{$fieldName."_ruleContent"."_end"};
								my $value = "";
								if(!defined $end){
									$value = substr($fieldValue,($start-1));
								}else{
									$value = substr($fieldValue,($start-1),($end-$start+1));
								}
								#��2,5 ����2-5����ȡ�������ж��Ƿ�����滻�����ݣ�replaceContent��
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
					
					#�����Ϲ���ֶμ�¼���浽���ݿ�
					if($fieldIrregular != "0"){
						$rowIrregular = 1;	#��ǰ�д��ڲ��Ϲ�ļ�¼
						$rule_content = encode("gbk",$rule_content);
						$replace_content = encode("gbk",$replace_content);
						my $checkRecord=$dbh->prepare("insert into gw_service_check_record(record_id,row_id,task_id,service_id,field_id,rule_type,rule_content,replace_content,condition_type,condition_content,FIELD_SORT) ".
																			"values(seq_gw_service_check_record.nextval,$rowNum,$taskId,$serviceId,'$fieldIdHash{$fieldName}','$rule_type','$rule_content','$replace_content','$condition_type','$condition_content',$fieldNum)");
						$checkRecord->execute;
						$checkRecord->finish;
					}
				}
				#���ڲ��Ϲ�����ݣ��������ݲ������ݿ�
				if($rowIrregular == 1){
					$rowIrregularCount++;
					
					my $str = $_;
					#$str =~ s/\",\"/\',\'/g; #�滻","Ϊ',' ---csv����txt����Ҫȥ������
					
					#���ָ���|ת�ɶ��ţ��������ţ������ֶζ����ϵ����ţ�ת���ַ������뻺���
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
			
			#���ڲ��Ϲ�����ݣ����ߺϹ������Ҳ������ˣ�������˴�������
			if($rowIrregularCount > 0 || $checkAudit == 1){
				my $seq=$dbh->prepare("select seq_gw_work_plan.nextval from dual");
				$seq->execute;
				my $planId=$seq->fetchrow_array();
				$seq->finish();
				
				#���Ӵ����������
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
				
				my $planContent="�����û���$loginName����ķ���(�������=$serviceCode,��������=$serviceName,��������=$fieldValue)����ļ��д��ڲ��Ϲ����ݣ�����ˡ�";
				if($rowIrregularCount==0){
					$planContent="�����û���$loginName����ķ���(�������=$serviceCode,��������=$serviceName,��������=$fieldValue)����ļ��Ϲ���ͨ��������ˡ�";
				}

				#���Ӵ�������
				my $workPlan=$dbh->prepare("insert into gw_work_plan(plan_id,plan_title,plan_type,plan_content,plan_state,create_time,create_user_id) ".
													"values($planId,'�Ϲ������',19,'$planContent',1,sysdate,1)");
				$workPlan->execute;
				$workPlan->finish;
				if($rowIrregularCount>0){
					WriteInfo("���ڲ��Ϲ����ݣ�������˴��졣planId=$planId.\n");
				}else{
					WriteInfo("�Ϲ���ͨ������������������˴��졣planId=$planId.\n");
				}
			}
			#�޸��������ݾ���״̬
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
			
			#ȫ���Ϲ���ļ�����Ҫ�������������������������һ������ļ�
			if($rowIrregularCount==0){
				eval{
					#outputNum==0ȫ�����������Ҫ�ı䡣outputNum!=0��Ҫ��������һ������ļ�
					if($outputNum!=0){
						my $outputCsvFile=substr($fileName,0,index($fileName,".csv"))."_output.csv";
						my $outRowNum=-1;  	#��һ��Ϊ���⣬��������
						open(OUTPUT_FILE,"> $filePath/$outputCsvFile") or print STDERR "Open file $filePath/$outputCsvFile error.\n";
						open DATA_FILE,$file;
						WriteInfo("create output file $filePath/$outputCsvFile start.\n");
						while(<DATA_FILE>){
							$outRowNum++;
							#�����������������˳�
							if($outRowNum>$outputNum){
								$outRowNum--;
								WriteInfo("output file $filePath/$outputCsvFile achieve max row,outputNum=$outputNum.\n");
								last;
							}
							s/[\r\n]//g;	#ȥ�����з�
							
							printf OUTPUT_FILE $_;
							printf OUTPUT_FILE "\r\n";
							
							if($outRowNum % 500000 == 0){
								my $row = $outRowNum/10000;
								WriteInfo("output file in $row w. \n");
							}
						}
						close DATA_FILE;
						close OUTPUT_FILE;
						$dataNum="$outRowNum";	#������������ļ���������
						WriteInfo("create output file $filePath/$outputCsvFile end. outputNum=$outputNum.\n");
						
						#ɾ����ѹ����ļ�����������ļ�������
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

#�źŲ�ץ����,kill ��ctrl+c
sub handle{
	CloseLog;
	exit(0);
}
$SIG{TERM}=$SIG{INT}=\&handle;