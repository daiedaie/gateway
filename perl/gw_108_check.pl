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


#��ʱɨ��ʱ��(��λ����)
my $CHECK_CYCLE=30;

#��־·��
my $LOG_PATH="/home/gateway/perl/logs";

#ORACLE����
##########����###########################################
my $dbname="GATEWAY_52";
my $user = "gateway";
my $passwd = "gateway1234QWER";

##########����#########################################
#my $dbname="GATEWAY_133";
#my $user = "gateway";
#my $passwd = "gateway666";

main:{
	#������־ 
  SetLogPath($LOG_PATH);
  SetLogLevel('D','D');  #�ļ����ն���־����'D', 'I', 'W', 'E', 'F', 'C'
  SetLogSize(50000);#�ļ��л�����
  SetLogHead("108_CHECK","108_CHECK");#�ļ�����־ ǰ׺
  OpenLog;
  WriteInfo("===================��ʼ����========================\n");
  WriteInfo("LOG_PATH:$LOG_PATH\n");	 
  WriteInfo("CHECK_CYCLE:$CHECK_CYCLE\n");	
  WriteInfo("Oracle:$user/$dbname\n");
	while(1){
		my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";
		
		#��ѯ���д���������
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
				#�����������Ƿ����
				my $cacheTable=$dbh->prepare("select count(*) from all_tables where table_name = 'GW_SERVICE_CHECK_$serviceId'");
				$cacheTable->execute;
				my $cacheCount=$cacheTable->fetchrow_array();
				$cacheTable->finish();
									
				#�����������ڣ��򴴽���
				if($cacheCount == 0){
					WriteInfo("The cache table GW_SERVICE_CHECK_$serviceId not exist,must to create.\n"); 		 	
					
					#�����ȡ���ֶ���Ϣ��Ϊ�����������
					my $tableStr = "create table GW_SERVICE_CHECK_$serviceId($gatewayRowId number,$gatewayTaskId number,$gatewayRowData varchar2(4000)) ";
					WriteInfo("Cache table sql:$tableStr\n");	
					my $sth_createTab=$dbh->prepare($tableStr);   
					$sth_createTab->execute;	 
					$sth_createTab->finish(); 
					
					#����������
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
				
				#������
				my $checkRule=$dbh->prepare(
					"select t.reorder,t.check_type,d.dict_value,t.check_rule,t.field_code,t.field_name ".
					"from gw_service_check_rule t ".
					"left join gw_sys_dict d on d.dict_code='DICT_CHECK_RULE_TYPE' and d.dict_key=t.check_type ".
					"where t.user_id=$userId and t.service_id=$serviceId and t.check_batch=$maxCheckBatch ".
					"order by t.reorder");
				$checkRule->execute;
				
				my %checkTypeHash;
				my %checkRuleHash;
				my %fieldHash;		#����ֶ�ID������reorder���fieldId
				my $checkRuleStr="";	#�������ַ��������ڱ���������־
				my @fieldCodes;			#��������ֶΣ����ڱ���������־
				my $fieldCount=0;
				my $uFpath="/home/gateway/users/$loginName/result";
				while(my ($reorder,$checkType,$checkTypeName,$checkRule,$fieldCode,$fieldName) = $checkRule->fetchrow_array()){
					$fieldCount++;	#�ܺϹ����ֶ���
					
					$checkTypeHash{$reorder}=$checkType;
					if($checkType=="1"){	#���ȼ��
						$checkRuleHash{$reorder}=$checkRule;
					}elsif ($checkType=="2"){ #�ֵ���ֵ���
						my @values = split /;/ ,$checkRule;
						$checkRuleHash{$reorder."_checkType2"}=$checkRule;
						foreach(@values){
							$checkRuleHash{$reorder."_".$_}=1;
						}
					}elsif ($checkType=="3"){ #��Χ�ڼ��
						my @values = split /;/ ,$checkRule;
						$checkRuleHash{$reorder."_checkType3"}=$checkRule;
						$checkRuleHash{$reorder."_begin"}=$values[0];
						$checkRuleHash{$reorder."_end"}=$values[1];
					}
					
					push(@fieldCodes,"$fieldCode");
					$checkRuleStr = $checkRuleStr."$fieldCode($fieldName)=������ͣ�$checkTypeName,������$checkRule;\n";
				}
				$checkRule->finish();
				
				my $gzFile_path="$filePath/$fileName";
				my $txtFile_path=$filePath."/".$unzipName;
				
				if(!-e $txtFile_path){
					my $gzOutputFile=$filePath.'/'.$fileName;
					if(!-e $gzOutputFile){
						WriteInfo("$gzFile_path ���������gz�ļ�������!\n"); 
						next;
					}
			  	
			    #��ѹgz�ļ�
			    my $gunzip = 'gunzip -c '.$gzFile_path." > ".$txtFile_path;
			    WriteInfo("file gunzip $gzFile_path start.\n");
			  	system($gunzip);
			  	WriteInfo("file gunzip $gzFile_path finish.\n");
			  }
			  
				if (!-e $txtFile_path) {WriteInfo("$txtFile_path �ļ�������!\n"); next;}
				WriteInfo("check file=".$txtFile_path."\n");
				
				#�޸��������ݾ���״̬���Ϲ�����
				my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=7,DOWNLOAD_START_TIME=sysdate,check_file_id=$checkFileId,check_batch=$maxCheckBatch,CHECK_AUDIT='$checkAudit',OUTPUT_NUM=$outputNum where task_id=$taskId");
				$task->execute;
				$task->finish;
				WriteInfo("update gw_model_data_fetch_task set data_progress_status=7,DOWNLOAD_START_TIME=sysdate,check_file_id=$checkFileId,check_batch=$maxCheckBatch,CHECK_AUDIT='$checkAudit',OUTPUT_NUM=$outputNum where task_id=$taskId.\n");
				
				my $irregularTxtFile="Irregular_".$unzipName;
				my $irregularTxtFile_Path = "$uFpath/$irregularTxtFile";
				#���Ϲ����txt�ļ���һ�㲻����
				if (-f $irregularTxtFile_Path) {
					WriteInfo("delete irregular txt file $irregularTxtFile_Path start!\n");
			 		unlink $irregularTxtFile_Path;
			 		WriteInfo("delete irregular txt file $irregularTxtFile_Path Success!\n");		
			 	}
				WriteInfo("irregularTxtFile=$uFpath/$irregularTxtFile.\n");
				WriteInfo("taskId=$taskId file rule check start. fileName=$txtFile_path.\n");
				
				my $rowNum = 0;
				my $rowIrregularCount = 0;	#���Ϲ�������������
				my $dbLimit = 1000;					#���Ϲ������ݲ��뵽���ݿ�������������������ٲ��뵽����
				my $irregularFileNum = 0;		#���Ϲ��ļ������
				my $rowData = "";
				my $checkResult=0;	#��������
				open(IRREGULAR_FILE,"> $uFpath/$irregularTxtFile") or die "Open file $uFpath/$irregularTxtFile error.\n";
				open CHECK_FILE,$txtFile_path;
				while(<CHECK_FILE>){
					$rowNum++;
					#chomp;
					s/[\r\n]//g;	#ȥ�����з�
					$rowData = decode('gbk',$_);
					
					my @fieldValues = split /\|/, $rowData;
					my $fieldNum = 0;		#fieldNum������ļ��е��ֶ�˳��ţ�Ҳ�����ݿ��з�������ֶε��ֶ�˳���
					my $rowIrregular = 0;	#��ǰ���Ƿ��в��Ϲ�ļ�¼
					my $irregularField = "";		#���Ϲ��ֶΣ��磺���Ϲ��ֶ����:���Ϲ��ֶ�ֵ|���Ϲ��ֶ����:���Ϲ��ֶ�ֵ
					foreach(@fieldValues){
						$fieldNum++;
						#����δ�����������ֶΣ���ʾ���ֶ�Ϊ�����(δ�����)�ķǷ��ֶ�
						if(!defined $checkTypeHash{$fieldNum}){
							$checkResult=3;
							WriteInfo("file exists illegal data!rowNum=$rowNum,rule fieldCount=$fieldCount, fieldNum=$fieldNum.\n");
							last;
						}
						
						my $type = $checkTypeHash{$fieldNum};
						my $fieldIrregular = "";	#��ǰ�ֶ����ݲ��Ϲ�ļ�¼����Ϊ�����ʾ���ֶε����ݲ��Ϲ�
						if($type eq "2"){	#�ֵ�ֵ���
							my $key = encode("gbk",$fieldValues[$fieldNum-1]);
							if(!defined $checkRuleHash{$fieldNum."_".$key}){
								$fieldIrregular = $checkRuleHash{$fieldNum."_checkType2"};
							}
						}elsif($type eq "3"){ #��Χ���
							if($fieldValues[$fieldNum-1] < $checkRuleHash{$fieldNum."_begin"} || $fieldValues[$fieldNum-1] > $checkRuleHash{$fieldNum."_end"}){
								$fieldIrregular = $checkRuleHash{$fieldNum."_checkType3"};
							}
						}elsif($type eq "1"){	#���ȼ��
							if(length($fieldValues[$fieldNum-1]) > $checkRuleHash{$fieldNum}){
								$fieldIrregular = $checkRuleHash{$fieldNum};
							}
						}
						#���ڲ��Ϲ���ֶ�
						if($fieldIrregular ne ""){
							$rowIrregular = 1;	#��ǰ�д��ڲ��Ϲ�ļ�¼
							$irregularField = $irregularField.(($irregularField eq ""?"":"|").$fieldNum.":".$fieldValues[$fieldNum-1]);
							
							#�����Ϲ���ֶμ�¼���浽���ݿ�,�������ټ������뵽���ݿ�
							if($rowIrregularCount <= $dbLimit){
								my $checkRecord=$dbh->prepare("insert into gw_service_check_record(record_id,row_id,task_id,service_id,check_type,check_rule,FIELD_SORT) ".
																					"values(seq_gw_service_check_record.nextval,$rowNum,$taskId,$serviceId,'$type','$fieldIrregular',$fieldNum)");
								$checkRecord->execute;
								$checkRecord->finish;
							}
						}
					}
					
					#�ж�����ֶ��Ƿ���ڻ����ڼ�����
					my $warnType=0;
					if($checkResult==3){#����ļ��ֶζ��ڼ�����
						$warnType=1;	
					}elsif($fieldCount!=$fieldNum){#����ļ��ֶ����ڼ�����
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
					
					#���ڲ��Ϲ�����ݣ��������ݲ������ݿ�
					if($rowIrregular == 1){
						$rowIrregularCount++;
						$irregularFileNum++;
						
						#�������ټ������뵽���ݿ�
						if($rowIrregularCount <= $dbLimit){
							my $str = $_;
							#$str =~ s/\",\"/\',\'/g; #�滻","Ϊ',' ---csv����txt����Ҫȥ������
							
							#���ָ���|ת�ɶ��ţ��������ţ������ֶζ����ϵ����ţ�ת���ַ������뻺���
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
					
					#���Ϲ������������������������������˳����ټ������
					if($maxCheckNum != 0 && $rowIrregularCount > $maxCheckNum){
						WriteInfo("taskId=$taskId���Ϲ�������($rowIrregularCount)�������Ϲ����������($maxCheckNum)���Զ��˳����.\n");
						last;
					}
					next;
				}
				close CHECK_FILE;
				close IRREGULAR_FILE;
				WriteInfo("taskId=$taskId file rule check end.\n");
				
				#���ڲ��Ϲ�����ݣ������Ϲ�����ļ�ѹ��
				if($rowIrregularCount > 0){
					my $irregularGzFile = $irregularTxtFile.".gz";
					#ѹ�����Ϲ������ļ�
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
				if($checkResult==0){	#��������û���������Ƿ����
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
				
				#���ڲ��Ϲ�����ݣ����ߺϹ������Ҳ������ˣ�������˴�������
				if($dataProgressStatus==8){
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
					if($checkResult==0 && $rowIrregularCount==0){
						$planContent="�����û���$loginName����ķ���(�������=$serviceCode,��������=$serviceName,��������=$fieldValue)����ļ��Ϲ���ͨ��������ˡ�";
					}
	
					#���Ӵ�������
					my $workPlan=$dbh->prepare("insert into gw_work_plan(plan_id,plan_title,plan_type,plan_content,plan_state,create_time,create_user_id) ".
														"values($planId,'�Ϲ������',19,'$planContent',1,sysdate,1)");
					$workPlan->execute;
					$workPlan->finish;
					if($checkResult!=0 || $rowIrregularCount>0){
						WriteInfo("���ڲ��Ϲ����ݣ�������˴��졣planId=$planId.\n");
					}else{
						WriteInfo("�Ϲ���ͨ������������������˴��졣planId=$planId.\n");
					}
				}
				
				if($checkResult==0&&$rowIrregularCount==0){#ȫ���Ϲ���ļ�����Ҫ�������������������������һ������ļ�
					my $timeStr =strftime("%Y%m%d%H%M%S", localtime(time));
					my $outputGzFile = substr($fileName,0,index($fileName,".txt.gz"))."_".$loginName."_$timeStr.txt.gz";
									
					#ȫ�������ֱ�Ӹ���ԭ�ļ�
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
						#����ļ���ȫ·��
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
							#�����������������˳�
							if($outRowNum>$outputNum){
								$outRowNum--;
								WriteInfo("output file $uFpath/$unzipName achieve max row,outputNum=$outputNum.\n");
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
						WriteInfo("create output file $outputTxtFile_path end. outputNum=$outputNum.\n");
						
						#ѹ������ļ�
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
				
					#�����п����ѱ��޸Ĺ������ΪʧЧ,pushǰ���һ��
					if($taskStatus==1){
						#���ͨ���ģ����ļ�push��ָ��ftp���Ҽ�¼������־
						if($dataProgressStatus==10){
							my $userFtpInfo=$dbh->prepare("select push_ftp,ftp_ip,ftp_port,ftp_username,ftp_password,ftp_path from gw_user where user_id=$userId");
			      	$userFtpInfo->execute;
			      	my ($pushFtp,$ftpIp,$ftpPort,$ftpUsername,$ftpPassword,$ftpPath)=$userFtpInfo->fetchrow_array();
			      	$userFtpInfo->finish;
							
							if(defined $pushFtp && $pushFtp==1){
							 	#�ϴ��ļ�
								my $ftp_flag=1; #0:down  1:up
								my $rst = FTPFile($ftpIp,$ftpUsername,$ftpPassword,$uFpath,$ftpPath,$outputGzFile,$ftp_flag);
								if($rst != 0 ){
									$dataProgressStatus=13;	#������ʧ�ܣ�����quartz���Զ��ط�
								}else{
									$dataProgressStatus=12;	#�������ʹ�
								 	WriteInfo("Upload file to $ftpIp succ!\n");
								 	
								 	#���ڸ����û�����ʱ��
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
											
				#�޸��������ݾ���״̬
				$task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=$dataProgressStatus,data_num=$dataNum,check_result=$checkResult,max_check_num=$maxCheckNum,CHECK_IRREGULAR_NUM=$rowIrregularCount,check_num=$rowNum $downloadEndTime $downloadTime where task_id=$taskId");
				$task->execute;
				$task->finish;
				WriteInfo("update gw_model_data_fetch_task set data_progress_status=$dataProgressStatus,data_num=$dataNum,check_result=$checkResult,max_check_num=$maxCheckNum,CHECK_IRREGULAR_NUM=$rowIrregularCount,check_num=$rowNum $downloadEndTime $downloadTime where task_id=$taskId.\n");
				
				#�����ɣ�ɾ����ѹ���txt�ļ�
			 	#if (-f $txtFile_path) {
			 		#unlink $txtFile_path;
			 		#WriteInfo("delete gunzip output txt file $txtFile_path Success!\n");		
			 	#}
				WriteInfo("taskId=$taskId task rule check end.\n");
			};#eval end
			
			if ($@){
				#�Ϲ�������г������⣬�޸��������ݾ���״̬Ϊ����ʧ��
				my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId");
				$task->execute;
				$task->finish;
				WriteInfo("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId.\n");
				
				WriteError("taskId=$taskId task rule check error!.\n");
				WriteError("$@.\n");
			}
		}#task end
		
		#2��ʾ����/���ʱ����
	  my $cycleConfig=$dbh->prepare("select config_unit,config_value from gw_sys_cnfig where config_type=2");
	  $cycleConfig->execute;
		my ($cycleType,$cycleValue)=$cycleConfig->fetchrow_array();
		$cycleConfig->finish;
		if(!defined $cycleValue){
		 	$cycleValue=$CHECK_CYCLE;
		}else{
		  #1��ʾ��λΪ��
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

#�źŲ�ץ����,kill ��ctrl+c
sub handle{
	CloseLog;
	exit(0);
}
$SIG{TERM}=$SIG{INT}=\&handle;


sub FTPFile(){  
	my ($FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag) = @_;
	#WriteInfo("FTP�ļ���Ϣ��$FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag. \n");
	eval{    
		  	
		my $ftp=Net::FTP->new("$FTP_ADDR",Passive=>1,Debug=>0,Timeout=>30) or die "Could not connect.\n"; 
               
		#��¼��FTP                                                                      
		$ftp->login($Fuser,$Fpassword) or die "Could not login.$! \n";            
		$ftp->binary() or die "binary failed. \n";
		#�л�Ŀ¼                                                                       
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