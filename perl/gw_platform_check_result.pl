#! /bin/perl

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
use File::Spec;
use File::Basename;
#use IPC::SysV qw(S_IRWXU );
use IPC::Msg;

#ִ�в���ȡֵ
my $taskId = $ARGV[0]; #����ID
my $userId = $ARGV[1]; #�û�ID
my $serviceId = $ARGV[2]; #����ID
my $serviceCode = $ARGV[3]; #�������
my $serviceName = $ARGV[4];#��������
my $fieldValue = $ARGV[5];#��������
my $processNum = $ARGV[6]; #�����ӽ�����
my $filePath = $ARGV[7]; #�û��ļ�Ŀ¼\���Ϲ��ļ�Ŀ¼
my $fileName = $ARGV[8]; #��������ļ�=����ļ�
my $irregularCsvFile = $ARGV[9]; #���Ϲ��ļ�����
my $checkAudit = $ARGV[10]; #�Ƿ���Ҫ���
my $loginName = $ARGV[11]; #�����û���¼��
my $maxCheckNum = $ARGV[12]; #���������
my $outputNum = $ARGV[13]; #���������0��ʾȫ�����
my $fileId = $ARGV[14];#�����ļ�ID
my $maxCheckBatch = $ARGV[15];

my $pid = $$;
my $ppid = getppid();

#��־·��
my $LOG_PATH="/home/gateway/perl/logs";

#ORACLE����
##########����###########################################
#my $dbname="GATEWAY_52";
#my $user = "gateway";
#my $passwd = "gateway1234QWER";
##########����#########################################
my $dbname="GATEWAY_133";
my $user = "gateway";
my $passwd = "gateway666";

my $gatewayRowId = "gateway_row_id";
my $gatewayTaskId = "gateway_task_id";
my $gatewayRowData = "row_data";

#������־ 
SetLogPath($LOG_PATH);
SetLogLevel('D','D');  #�ļ����ն���־����'D', 'I', 'W', 'E', 'F', 'C'
SetLogHead("GW_DESEN","GW_DESEN");#�ļ�����־ ǰ׺
OpenLog;
WriteInfo("===================��ʼ����========================\n");
WriteInfo("LOG_PATH:$LOG_PATH\n");	
WriteInfo("Oracle:$user/$dbname\n");

#�������ݿ�����
my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";

my ($reskey,$resmsg,$resmsgid,$resmsgtype,$resbuf);
$reskey = IPC::SysV::ftok("/home/gateway/perl/desen_check.txt");
#�򿪽����Ϣ����
$resmsg = new IPC::Msg( $reskey, 0) or die "Open MQ faile!";


my $loop = 1;
my $rowIrregularCount = 0; #���Ϲ��ۼ�����
my $dbLimit = 1000;	#���Ϲ������ݲ��뵽���ݿ�������������������ٲ��뵽����
#my $moreTag = 0; #�Ƿ���б�ǣ�0��ʾ�����ڣ�1��ʾ����
#my $loseTag = 0; #�Ƿ����б�ǣ�0��ʾ�����ڣ�1��ʾ����
my $overTag = 0; #�Ƿ񳬳���󲻺Ϲ��ǣ�0��ʾû�г�����1��ʾ�ѳ���
my $endCount = 0; #�����ӽ���������������
my $errorTag = 0; #�Ƿ�ִ���쳣��ǣ�0��ʾ�����ڣ�1��ʾ����
my $totalRow = 0; #�ļ�������
my $rowNum = 0;
my @contents;
my $irregularCsvFilePath = "$filePath/$irregularCsvFile"; #���Ϲ��ļ�·��
my $str;
my $irregularField;
my $serviceFields;

open(IRREGULAR_FILE,"> $irregularCsvFilePath") or die "Open file $irregularCsvFilePath error.\n";
while( $loop == 1 ) {
	#��ȡ��Ϣ���е�������
	$resmsgtype = $resmsg->rcv($resbuf, 1024);
	if(defined $resmsgtype){
		if ( $resmsgtype == 1 ) {#��鲻�Ϲ��������
			@contents = split(::,$resbuf);
			$rowNum = $contents[0];
			$serviceFields = $contents[1];
			$irregularField = $contents[2];
			$str = $contents[3];
		
			#$errorTag = 1;
			$rowIrregularCount++;
			#��һ�����ݴ�ű�ͷ
			if($rowIrregularCount == 1){
				my $insertSql=$dbh->prepare("insert into gw_service_check_$serviceId($gatewayRowId,$gatewayTaskId,$gatewayRowData) values(0,$taskId,'$serviceFields')");
				$insertSql->execute;
				$insertSql->finish();
				
				#����ͷд�벻�Ϲ��ļ�
				printf IRREGULAR_FILE "���,�к�,".$serviceFields.",���Ϲ��ֶ�";
				printf IRREGULAR_FILE "\r\n";
			}
			#д�벻�Ϲ��ļ�
			printf IRREGULAR_FILE $rowIrregularCount.",".$rowNum.",".$str.",".$irregularField;
			printf IRREGULAR_FILE "\r\n";
			
			if($rowIrregularCount <= $dbLimit){
				
				#$str =~ s/\",\"/\',\'/g; #�滻","Ϊ',' ---csv����txt����Ҫȥ������
				
				#���ָ���|ת�ɶ��ţ��������ţ������ֶζ����ϵ����ţ�ת���ַ������뻺���
				#$str =~ s/,/\',\'/g; 
				#$str ="\'$str\'";
				my $insertSql=$dbh->prepare("insert into gw_service_check_$serviceId($gatewayRowId,$gatewayTaskId,$gatewayRowData) values($rowNum,$taskId,'$str')");
				$insertSql->execute;
				$insertSql->finish();
			}
			#���Ϲ��¼����������������˳����
			if($maxCheckNum != 0 && $rowIrregularCount > $maxCheckNum){
				WriteInfo("taskId=$taskId���Ϲ�������($rowIrregularCount)�������Ϲ����������($maxCheckNum)���Զ��˳����.\n");
				$overTag = 1;	
				$loop = 0;
			}
			
		}elsif ( $resmsgtype == -1 ) {#�쳣
			#�����ӽ���ִ���쳣��Ϣ
			$errorTag = 1;
			$rowNum = $resbuf;
			#�Ϲ�������г������⣬�޸��������ݾ���״̬Ϊ����ʧ��
			my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId");
			$task->execute;
			$task->finish;
			
		  #дlog
			WriteInfo("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId.\n");
			WriteError("taskId=$taskId task rule check error!.\n");
			WriteError("$@.\n");
			$loop = 0;
		}else {
			$endCount ++;
			#������������
			if($endCount == $processNum){
				@contents = split(::,$resbuf);
				$serviceFields = $contents[0];
				$rowNum = $contents[1];
				$totalRow = $rowNum; #��ȡ������
				WriteInfo("========taskId=$taskId the all process run end.\n");
				#$rowNum = $totalRow;
				$loop = 0;
			}				
		}
	}
}
close IRREGULAR_FILE;
WriteInfo("taskId=$taskId file rule check end.\n");

if( $errorTag == 1 || $overTag == 1){
	#�����쳣�źŸ������̣����������ӽ���
	WriteInfo("===================================  send the ABRT signal to parent =======\n");
	kill 'ABRT', $ppid;
}
if($errorTag != 1){
	
	my $dataNum = $totalRow;
	
	#���ڲ��Ϲ�����ݣ������Ϲ�����ļ�ѹ��
	if($rowIrregularCount > 0){
		my $irregularGzFile = $irregularCsvFile.".gz";
		#ѹ�����Ϲ������ļ�
		WriteInfo("gzip irregular txt file $filePath/$irregularGzFile start.\n");
		system("cd $filePath && gzip -cf $irregularCsvFile > $irregularGzFile");
		WriteInfo("gzip irregular txt file $filePath/$irregularGzFile finish.\n");
		
		#�����Ϲ���ʱ����ļ�ɾ��
		if (-f $irregularCsvFilePath) {
			WriteInfo("delete irregular txt file $irregularCsvFilePath start!\n");
			unlink $irregularCsvFilePath;
			WriteInfo("delete irregular txt file $irregularCsvFilePath finish!\n");		
		}
		#���������ļ����
	 	my $irregularDataFile=$dbh->prepare("insert into GW_MODEL_DATA_FILE (FILE_ID,TASK_ID,MODEL_ID,USER_ID,FILE_TYPE,FILE_STATUS,FILE_PATH,FILE_NAME,CREATE_TIME,FTP_IP,FTP_PORT)".
	    														 "values (SEQ_MODEL_DATA_FILE.NEXTVAL,$taskId,null,$userId,3,1,'$filePath','$irregularGzFile',sysdate,null,null)");
	 	$irregularDataFile->execute;
	 	$irregularDataFile->finish;
	}
	
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
	
	my $dataProgressStatus=($rowIrregularCount==0 && $checkAudit != 1)?10:8;;
	my $downloadTime="";
	#ȫ���Ϲ���ļ�����Ҫ�������������������������һ������ļ�
	if($rowIrregularCount == 0){
		my $timeStr =strftime("%Y%m%d%H%M%S", localtime(time));
		#����Ա���ص�ѹ���ļ���
		my $downGzFile = substr($fileName,0,index($fileName,".csv"))."_$timeStr.csv.gz";
		#�����û����ܺ�ѹ���ļ���
		my $outputGzFile = substr($fileName,0,index($fileName,".csv"))."_".$loginName."_$timeStr.csv.gz";
				
		#��ȡ��������
		my $findPwd=$dbh->prepare("select file_encry_pwd from gw_user t where user_id=$userId");
		$findPwd->execute;
		my $pwd=$findPwd->fetchrow_array();
		
		
		my $txtFile_path=$filePath."/".$fileName;
		my $outUnzipName = $fileName;
		#my $gzFile_path="$filePath/$fileName";
		#outputNum==0ȫ�����������Ҫ�ı䡣outputNum!=0��Ҫ��������һ������ļ�
		#ȫ�������ֱ�Ӹ���ԭ�ļ�
		if($outputNum!=0){#���������
			
			#����ļ���ȫ·��
			$outUnzipName = "part_".$fileName;
			my $outputTxtFile_path = $filePath.'/'.$outUnzipName;
		 	if (-f $outputTxtFile_path) {
		 		unlink $outputTxtFile_path;
		 		WriteInfo("delete temporary file $outputTxtFile_path Success!\n");		
		 	}
		 	$dataNum=$outputNum;	#������������ļ���������
		 	#��ӱ�ͷһ��
		 	$outputNum ++;
		 	WriteInfo("create output file $outputTxtFile_path start.\n");
		 	my $head = 'head -n '.$outputNum." $txtFile_path > $outputTxtFile_path";
		 	system($head);
			WriteInfo("create output file $outputTxtFile_path end. outputNum=$outputNum.\n");
	
		}
		#���ɹ���Ա����ѹ���ļ�
		WriteInfo("gzip file $filePath/$downGzFile start.\n");
		system("cd $filePath && gzip -cf $outUnzipName > $downGzFile");
		WriteInfo("gzip file $filePath/$downGzFile end.\n");	
		#���������û�����ѹ���ļ�
		#my $zipPwd = 'zip -rP '.$pwd." ".$outputGzFile." ".$outUnzipName;
		my $zipPwd;
		if(defined $pwd){
			$zipPwd = 'zip -rP '.$pwd." ".$outputGzFile." ".$outUnzipName; 
  	}else{
  		$zipPwd = "gzip -cf $outUnzipName > $outputGzFile";
  	}
    WriteInfo("gen passwork to $filePath/$outputGzFile start.\n");
  	system("cd $filePath && ".$zipPwd);
  	WriteInfo("gen passwork to $filePath/$outputGzFile finish.\n");
  	
  	
		
  	#���������ļ�
  	my $updateFile=$dbh->prepare("update gw_model_data_file set file_name='$outputGzFile',UNZIP_NAME='$outUnzipName' where file_id=$fileId");
		$updateFile->execute;
		$updateFile->finish;
		
    #�������Ա�����ļ�
		my $dataFile=$dbh->prepare("insert into GW_MODEL_DATA_FILE (FILE_ID,TASK_ID,MODEL_ID,USER_ID,FILE_TYPE,FILE_STATUS,FILE_PATH,FILE_NAME,CREATE_TIME,FTP_IP,FTP_PORT,UNZIP_NAME)".
    														 "values (SEQ_MODEL_DATA_FILE.NEXTVAL,$taskId,null,$userId,5,1,'$filePath','$downGzFile',sysdate,null,null,'$outUnzipName')");
  	$dataFile->execute;
  	$dataFile->finish;
		
		#�����п����ѱ��޸Ĺ������ΪʧЧ,pushǰ���һ��	
		my $searchTaskStatus=$dbh->prepare("select task_status from gw_model_data_fetch_task where task_id=$taskId");
		$searchTaskStatus->execute;
		my $taskStatus=$searchTaskStatus->fetchrow_array();
		$searchTaskStatus->finish;
		
		if($taskStatus==1){
			#���ͨ���ģ�����Ҫ��˽��ļ�push��ָ��ftp���Ҽ�¼������־
			if($checkAudit != 1){
				my $userFtpInfo=$dbh->prepare("select push_ftp,ftp_ip,ftp_port,ftp_username,ftp_password,ftp_path from gw_user where user_id=$userId");
      	$userFtpInfo->execute;
      	my ($pushFtp,$ftpIp,$ftpPort,$ftpUsername,$ftpPassword,$ftpPath)=$userFtpInfo->fetchrow_array();
      	$userFtpInfo->finish;
				
				if(defined $pushFtp && $pushFtp==1){
				 	#�ϴ��ļ�
					my $ftp_flag=1; #0:down  1:up
					my $rst = FTPFile($ftpIp,$ftpUsername,$ftpPassword,$filePath,$ftpPath,$outputGzFile,$ftp_flag);
					if($rst != 0 ){
						$dataProgressStatus=13;	#������ʧ�ܣ�����quartz���Զ��ط�
					}else{
						$dataProgressStatus=12;	#�������ʹ�
					 	WriteInfo("Upload file to $ftpIp succ!\n");
					 						 	
					 	#�������Ϲ���,ֱ���������Ĺ������Ϲ���
						my $checkRule=$dbh->prepare(
							"select f.field_id,f.field_code,f.field_name,t.RULE_TYPE,d.dict_value,t.RULE_CONTENT,t.REPLACE_CONTENT,t.CONDITION_TYPE,t.CONDITION_CONTENT from gw_desen_service_field t ".
							"inner join gw_service_field f on f.field_id=t.field_id ".
							"left join gw_sys_dict d on d.dict_code='DICT_DESEN_RULE_TYPE' and d.dict_key=t.rule_type ".
							"where t.user_id=$userId and t.service_id=$serviceId and batch=(select max(batch) from gw_desen_service_field where user_Id=$userId and service_Id=$serviceId) ".
							"order by t.field_id");
						$checkRule->execute;
						
						my $ruleTypeCN = decode("gbk","��������");
						my $ruleContentCN = decode("gbk","����λ");
						my $replaceContentCN = decode("gbk","�滻�ַ�");
						my $conditionTypeCN = decode("gbk","��������");
						my $conditionContentCN = decode("gbk","��������");
						my $checkRuleStr;
						while(my ($fieldId,$fieldCode,$fieldName,$ruleType,$ruleName,$ruleContent,$replaceContent,$conditionType,$conditionContent)=$checkRule->fetchrow_array()){	
							$fieldName = decode("gbk",$fieldName);
							$ruleContent = decode("gbk",$ruleContent);
							$replaceContent = decode("gbk",$replaceContent);
							
							if(defined $ruleType){
								$ruleName = decode("gbk",$ruleName);
								$checkRuleStr = $checkRuleStr."$fieldCode($fieldName)=$ruleTypeCN:$ruleName,$ruleContentCN:$ruleContent,$replaceContentCN:$replaceContent;\n";
							}
							if(defined $conditionType){
								$checkRuleStr = $checkRuleStr."$conditionTypeCN:$conditionType,$conditionContentCN:$conditionContent;\n";
							}
						}
						$checkRule->finish();
				 	
				 		#���ڸ����û�����ʱ��
				 		$downloadTime=",download_time=sysdate";
				 		$checkRuleStr = encode("gbk",$checkRuleStr);
					 	
						my $liabilityLog=$dbh->prepare("insert into gw_model_liability_log(log_id,user_id,task_id,model_fields,model_data_num,create_user,create_time,service_id,desen_rule_content) ".
															 "values(seq_model_liability_log.nextval,$userId,$taskId,'$serviceFields',$dataNum,'root',sysdate,$serviceId,'$checkRuleStr')");
						$liabilityLog->execute;
						$liabilityLog->finish;
					}
				}else{
					WriteInfo("According to the configuration pushFtp=$pushFtp push cancel!\n");
				}
			}
		}else{
			WriteInfo("task status Invalid, task abort!\n");
		}
	
	}
	my $checkResult = 0;
	if($overTag == 0 && $rowIrregularCount > 0){
		$checkResult = 1;
	}
	if($overTag == 1){
		$checkResult = 2;
	}
	
	my $downloadEndTime=($rowIrregularCount==0 && $checkAudit != 1)?",DOWNLOAD_END_TIME=sysdate":"";
	my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=$dataProgressStatus,data_num=$dataNum,check_result=$checkResult,max_check_num=$maxCheckNum,CHECK_IRREGULAR_NUM=$rowIrregularCount,check_num=$rowNum,check_batch=$maxCheckBatch $downloadEndTime $downloadTime where task_id=$taskId");
	$task->execute;
	$task->finish;
	WriteInfo("update gw_model_data_fetch_task set data_progress_status=$dataProgressStatus,data_num=$dataNum,check_result=$checkResult,max_check_num=$maxCheckNum,CHECK_IRREGULAR_NUM=$rowIrregularCount,check_num=$rowNum,check_batch=$maxCheckBatch $downloadEndTime $downloadTime where task_id=$taskId.\n");
	WriteInfo("taskId=$taskId rule check end.\n");
}


sub FTPFile
{  
	my ($FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag) = @_;
	#WriteInfo("FTP�ļ���Ϣ��$FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag. \n");
	eval{    
		  	
		my $ftp=Net::FTP->new("$FTP_ADDR",Passive=>1,Debug=>0,Timeout=>30) or die "Could not connect.\n"; 
               
		#��¼��FTP                                                                      
		$ftp->login($Fuser,$Fpassword) or die "Could not login.$! \n";            
		$ftp->binary() or die "binary failed. \n";
		if(defined $Fpath) {
			#�л�Ŀ¼                                                                       
			$ftp->cwd($Fpath) or die "Cannot change working directory ", $ftp->message;   
		}else{
			$Fpath="";
		}
		

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
print "the result Child $pid end.\n"
