#! /bin/perl -w

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
use IPC::SysV qw(IPC_PRIVATE IPC_SET IPC_CREAT IPC_NOWAIT);
use POSIX ":sys_wait_h";
use IPC::Msg;

#��ʱɨ��Ĭ��ʱ��(��λ����)
my $CHECK_CYCLE = 30;
#����ռ��ӽ��̹���key�ļ�
my $resultKeyFile = "/home/gateway/perl/desen_check.txt";
#�����ӽ�����
my $processNum = 5;
if($ARGV[0]){
	$processNum = $ARGV[0];
}
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


#�����ļ���ź�csv�ļ�����·��
my $Lpath="/home/gateway/data/platform";


my $running_child;

#�Ƿ���������ǣ�1��Ǽ�����0��ǲ�����
my $running;
#�Ƿ���ڲ��Ϲ����ݣ�1��Ǵ��ڣ�0��ǲ�����
my $irregular;

my @task_pids;

#��ץ�ӽ��̽�����Ϣ�ź�
$SIG{CHLD} = sub {
	while( ( my $child = waitpid( -1, POSIX::WNOHANG ) ) > 0 ) {
		print "Get childe $child end signal.\n";
		$running_child --;
	}
};
#��ץ�ӽ����쳣��Ϣ�źţ������ץ����kill�����ӽ���
$SIG{ABRT} = sub {
	foreach my $child(@task_pids) {
		kill "ABRT", $child;
	}
	$running = 0;
};

#��ץ�ӽ��̼�鵽���Ϲ�����Ϣ�ź�
#$SIG{USR1} = sub {
#	$irregular = 1;
#};
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
	while ( 1 > 0 ) {
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
       " 			 t.unzip_name,                                   ".  
       "			 t.desen_type,                                  ".  
       "			 t.max_check_num,																".
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
       "       u.login_name,                                  ".			
       "			 b.data_progress_status,                        ".  
       "			 a.file_id,                                     ". 
       "			 a.ftp_ip,                                      ".  
       "	 		 a.ftp_user,                                    ".  
       "	  	 a.FTP_PASSWORD,                                ".  
       "			 a.FILE_PATH,                                   ".  
       "			 a.unzip_name,                                   ".  
       "			 f.desen_type,                                  ".  
       "			 f.max_check_num,                               ".     
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
       "order by b.data_progress_status,org.run_level,u.run_level desc   ".
       ") tmp ) t where rnum=1 "
		);
		$checkTask->execute;
		
		my $gatewayRowId = "gateway_row_id";
		my $gatewayTaskId = "gateway_task_id";
		my $gatewayRowData = "row_data";
		while(my ($taskId,$serviceId,$serviceCode,$serviceName,$userId,$loginName,$dataProgressStatus,$fileId,$ftpIp,$ftpUser,$ftpPassword,$filePath,$fileName,$desenType,$maxCheckNum,$checkAudit,$outputNum,$fieldValue) = $checkTask->fetchrow_array()){			
			eval{
				WriteInfo("taskId=$taskId rule check start...\n");
				WriteInfo("Rule Check Info:taskId=$taskId,serviceId=$serviceId,serviceCode=$serviceCode,userId=$userId,dataProgressStatus=$dataProgressStatus,filePath=$filePath,fileName=$fileName,desenType=$desenType,checkNum=$maxCheckNum\n");
				#�������������Ƿ����
				my $cacheTable=$dbh->prepare("select count(*) from all_tables where table_name = 'GW_SERVICE_CHECK_$serviceId'");
				$cacheTable->execute;
				my $cacheCount=$cacheTable->fetchrow_array();
				$cacheTable->finish();
				
				#�����������ڣ��򴴽���
				if($cacheCount == 0){
					WriteInfo("The cache table GW_SERVICE_CHECK_$serviceId not exist,must to create.\n"); 		 	
						eval{
							my $tableStr = "create table GW_SERVICE_CHECK_$serviceId($gatewayRowId number,$gatewayTaskId number,$gatewayRowData varchar2(4000)) ";
							WriteInfo("Cache table sql:$tableStr\n");	
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
				}
							
				my $delCheck=$dbh->prepare("delete gw_service_check_$serviceId where $gatewayTaskId=$taskId");
				$delCheck->execute;
				
				$delCheck=$dbh->prepare("delete gw_service_check_record where Task_Id=$taskId");
				$delCheck->execute;
				$delCheck->finish;
		 		
		 		my $checkFile="$filePath/$fileName";
				if (!-e $checkFile) {WriteInfo("$checkFile �ļ�������!\n"); next;}
				WriteInfo("check file=".$checkFile."\n");
				
				#�޸��������ݾ���״̬���Ϲ�����
				my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=7,CHECK_AUDIT='$checkAudit',OUTPUT_NUM=$outputNum where task_id=$taskId");
				$task->execute;
				$task->finish;
				
				#my $irregularCsvFile="Irregular_".substr($fileName,0,index($fileName,".csv")).".txt";
				my $irregularCsvFile="Irregular_".$fileName;
				my $irregularCsvFilePath = "$filePath/$irregularCsvFile";
				#���Ϲ����csv�ļ���һ�㲻����
				if (-f $irregularCsvFilePath){
					WriteInfo("delete irregular csv file $irregularCsvFilePath start!\n");
			 		unlink $irregularCsvFilePath;
			 		WriteInfo("delete irregular csv file $irregularCsvFilePath Success!\n");		
			 	}
				WriteInfo("irregularCsvFile=$filePath/$irregularCsvFile.\n");
				WriteInfo("taskId=$taskId file rule check start. fileName=$checkFile.\n");
		 		
		 		#�ļ�����
				process_file($checkFile,$userId,$serviceId,$serviceCode,$serviceName,$taskId,$loginName,$resultKeyFile,$processNum,$fieldValue,$maxCheckNum,$filePath,$irregularCsvFile,$checkAudit,$outputNum,$fileId,$dbh,$fileName);
				
			};#eval end
			
			if($@){
				#�Ϲ�������г������⣬�޸��������ݾ���״̬Ϊ����ʧ��
				my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId");
				$task->execute;
				$task->finish;
				foreach my $child(@task_pids) {
					kill "ABRT", $child;
				}
				WriteInfo("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId.\n");
				
				WriteError("taskId=$taskId task rule check error!.\n");
				WriteError("$@.\n");
			}
		}
	
	  
		#2��ȡ����/���ʱ����
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


#�ļ�������
sub process_file
{
	my ($checkFile,$userId,$serviceId,$serviceCode,$serviceName,$taskId,$loginName,$resultKeyFile,$processNum,$fieldValue,$maxCheckNum,$filePath,$irregularCsvFile,$checkAudit,$outputNum,$fileId,$dbh,$fileName) = @_;
				
	#my $file = shift @_;
	my ($key,$msg,$msgid,$msgtype,$buf);
	print "Processing file $checkFile\n";
	$key = IPC::SysV::ftok($checkFile);
	print "Open MQ with ID: $key\n";
	#������Ϣ����
	$msg = new IPC::Msg($key, IPC_SET | S_IRWXU | IPC_CREAT) or die "Create MQ faile!";
  #��Ϣ����id
	$msgid = $msg->id();
	print "Running task with msgid: ",$msgid,"\n";
	
	my ($reskey,$resmsg,$resmsgid,$resmsgtype,$resbuf);
	$reskey = IPC::SysV::ftok($resultKeyFile);
	#���������Ϣ����
	$resmsg = new IPC::Msg($reskey, IPC_SET | S_IRWXU | IPC_CREAT) or die "Create MQ faile!";

	$running_child = 0;
	#�Ƿ���������ǣ�1��Ǽ�����0��ǲ�����
	$running = 1;
	#�Ƿ���ڲ��Ϲ�����
	$irregular = 0;
	
	my $checkBatch=$dbh->prepare("select max(batch) from gw_desen_service_field where user_Id=$userId and service_Id=$serviceId");
	$checkBatch->execute;	
	my $maxCheckBatch=$checkBatch->fetchrow_array();
	$checkBatch->finish();
	
	#�������Ϲ���,ֱ���������Ĺ������Ϲ���
	my $checkRule=$dbh->prepare(
		"select f.field_id,f.field_code,f.field_name,t.RULE_TYPE,d.dict_value,t.RULE_CONTENT,t.REPLACE_CONTENT,t.CONDITION_TYPE,t.CONDITION_CONTENT from gw_desen_service_field t ".
		"inner join gw_service_field f on f.field_id=t.field_id ".
		"left join gw_sys_dict d on d.dict_code='DICT_DESEN_RULE_TYPE' and d.dict_key=t.rule_type ".
		"where t.user_id=$userId and t.service_id=$serviceId and batch=$maxCheckBatch ".
		"order by t.field_id");
	$checkRule->execute;
	
	my %fieldCodeHash;		#����ֶ�Code������fieldName��ȡfieldCode
	while(my ($fieldId,$fieldCode,$fieldName,$ruleType,$ruleName,$ruleContent,$replaceContent,$conditionType,$conditionContent)=$checkRule->fetchrow_array()){	
		$fieldCodeHash{$fieldName}=$fieldCode;
	}
	$checkRule->finish();
	
	@task_pids=();
	
	#����4���ӽ��̣��ӽ���perl�ű�Ϊchild.pl
	for (my $i=0; $i < $processNum ; $i++) {
		my $pid = fork();
		if( $pid < 0 ) {
			die "Failed to fork child ..."; # Fix me: remember to kill all forked child process.
		}
		elsif ( $pid == 0 ) {
			# Child process
			exec("perl ./gw_platform_check_child.pl $checkFile $userId $serviceId $taskId $resultKeyFile $maxCheckBatch");
			exit(1);
		}
		else {
			#���ӽ���Pid�ŵ��ӽ���id���飬Ϊĳһ�ӽ��̳����kill���������ӽ���
			push(@task_pids, $pid);
			#�Ǽ��ӽ��������������ӽ��̽���ͳ��
			$running_child ++;
		}
	}
	#��������ӽ���
	my $pid = fork();
	if( $pid < 0 ) {
		die "Failed to fork child ..."; # Fix me: remember to kill all forked child process.
	}
	elsif ( $pid == 0 ) {
		# Child process
		exec("perl ./gw_platform_check_result.pl $taskId $userId $serviceId $serviceCode $serviceName $fieldValue $processNum $filePath $fileName $irregularCsvFile $checkAudit $loginName $maxCheckNum $outputNum $fileId $maxCheckBatch");
		exit(1);
	}
	else {
		#�Ǽ��ӽ��������������ӽ��̽���ͳ��
		$running_child ++;
	}
	
	#��������������Ϣ
	$msgtype = 1;
	
	my $serviceFields;
	my @resultFieldNames;
	my @resultFieldCodes;
	my $resultFieldNamesStr;
	my $rowNum = -1;
	my $sendRow;
	open CHECK_FILE,$checkFile;
	while(<CHECK_FILE>){
		s/[\r\n]//g;	#ȥ�����з�
		
		#��ͷΪ�ֶ�����
		if($rowNum==-1){
			#my $rowValue = decode("gbk",$_);
			my $rowValue = $_;
			@resultFieldNames = split /,/ ,$rowValue; #���ʱ����˳���ȡ���ֶ�������ȡ���ֶ�ID
			$resultFieldNamesStr = join(",",@resultFieldNames);
			my $fieldNum=0;
			foreach(@resultFieldNames){
				$fieldNum++;
				$resultFieldCodes[$fieldNum-1]=$fieldCodeHash{$resultFieldNames[$fieldNum-1]};		#�����ֶ�����ȡ���ֶα��룬��ֵ��@resultFieldCodes
			}
			$serviceFields=join(",",@resultFieldCodes);		#���治�Ϲ�������ʱ���ֶα���
			$rowNum++;
			next;
		}
		$rowNum++;
		
		if( $rowNum > 0){
			$sendRow = $resultFieldNamesStr."::".$serviceFields."::".$rowNum."::".$_;
			$msgtype = 1;
			#���������ݵ���Ϣ����
			$msg->snd($msgtype, $sendRow );
			if ( $running == 0) {
				last;#�ӽ��̳����������ȡ�ļ�(���쳣�����ڶ���/ȱ�С����Ϲ���������)
			}
		}
	}
	close CHECK_FILE;
			
	#���쳣����
	# send EOF message to every child.
	#�������������Ϣ
	$msgtype = 99;
	#��ÿһ���ӽ��̷��ͽ�����Ϣ
	for( my $i=0; $i < $processNum; $i ++ ) {
		$sendRow = $serviceFields."::".$rowNum;
		$msg->snd($msgtype, $sendRow);
	}
	# wait all child finish��
	while( $running_child != 0 ) {
		next;
	}	
	
	print "All task process end.\n";

	@task_pids=();

	$msg->remove();
	$resmsg->remove();
}

