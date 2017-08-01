#!/opt/perl_32/bin/perl
##!/usr/bin/perl

use strict;
use Getopt::Long;
use Time::Local;
use Net::FTP;
use DBI();
use Term::ANSIColor qw(:constants);
use POSIX;
use LogFun;
#use CryptDES;

#��ʱɨ��ʱ��(��λ����)
my $CHECK_CYCLE=30;

#��־·��
my $LOG_PATH="/home/gateway/perl/logs";

#�����û�Ŀ¼
my $orgPath="/home/gateway/vuser";

#ȡ����,��������csv�ļ���:20141210
my $date = strftime("%Y%m%d",localtime(time));

#ORACLE����
##########����#############
my $dbname="GATEWAY_52";
my $user = "gateway";
my $passwd = "gateway1234QWER";

##########����#############
#my $dbname="GATEWAY_133";
#my $user = "gateway";
#my $passwd = "gateway666";

sub main{
	 #������־ 
	 SetLogPath($LOG_PATH);
	 SetLogLevel('D','D');  #�ļ����ն���־����'D', 'I', 'W', 'E', 'F', 'C'
	 SetLogSize(50000);#�ļ��л�����
	 SetLogHead("GW_108","GW_108");#�ļ�����־ ǰ׺
	 OpenLog;
	 WriteInfo("===================��ʼ����========================\n");
   WriteInfo("LOG_PATH:$LOG_PATH\n");	 
   WriteInfo("CHECK_CYCLE:$CHECK_CYCLE\n");
   while(1){
   my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";
   
   my $allTasks=$dbh->prepare(
   		"select t.task_id,t.data_progress_status,t.user_id,-1 fileId,'' filePath,'' fileName,org.login_name orgName,u.login_name dataLoginName,".
   		"s.service_code serviceCode,s.cycle_type cycleType,t.field_value cycleValue from gw_model_data_fetch_task t ".
			"inner join gw_model_data_fetch f on f.fetch_id=t.fetch_id ".
			"inner join gw_service s on s.service_id=t.service_id ".
			"inner join gw_user u on u.user_id=t.user_id and u.status='1' ".
			"inner join gw_user org on org.org_id=u.org_id and org.user_type='orgUser' ".
			"where f.audit_status='2' and f.desen_type='2' and t.task_status='1' ".
			"and t.data_progress_status is null ".
			"union all ".
			"select t.task_id,t.data_progress_status,t.user_id,e.file_id,e.file_path filePath,e.file_name fileName,org.login_name orgName,u.login_name dataLoginNam,'' serviceCode,".
			"'' cycleType,'' cycleValue  from gw_model_data_fetch_task t ".
			"inner join gw_model_data_fetch f on f.fetch_id=t.fetch_id ".
			"inner join gw_user u on u.user_id=t.user_id and u.status='1' ".
			"inner join gw_user org on org.org_id=u.org_id and org.user_type='orgUser' ".
			"inner join gw_model_data_file e on e.task_id=t.task_id and e.file_type=1 and e.file_status=1 ".
			"where f.audit_status='2' and f.desen_type='2' and t.task_status='1' ".
			"and t.data_progress_status='0'"
   );
   $allTasks->execute;
   while(my ($taskId,$dataProgressStatus,$userId,$fileId,$filePath,$fileName,$orgName,$dataLoginName,$serviceCode,$cycleType,$cycleValue) = $allTasks->fetchrow_array()){
	   	WriteInfo("taskId=$taskId search file is exists start.\n");	   	
	   	eval{
	   		$filePath = "$orgPath/$orgName/$dataLoginName";
	   		
		   	my $gzFileName = getUserFileName($taskId,$dataProgressStatus,$filePath,$fileName,$serviceCode,$cycleType,$cycleValue,$orgName,$dataLoginName);
		   	#�ļ�������
		   	if($gzFileName eq ''){
		   		WriteInfo("taskId=$taskId service output file is not exists.\n");
		   	}else{
		   		my $gzFilePath=$filePath."/".$gzFileName;
		   		my $txtFile=substr($gzFileName,0,index($gzFileName,".gz"));
					my $txtFilePath=$filePath."/".$txtFile;
					
			    #�ļ��������Ƚ�ѹgz�ļ�
			    my $gunzip = 'gunzip -c '.$gzFilePath." > ".$txtFilePath;
			    WriteInfo("file gunzip $gzFilePath > $txtFile start.\n");
			  	system($gunzip);
			  	WriteInfo("file gunzip $gzFilePath > $txtFile finish.\n");
				  
				  if(-f $txtFilePath and -z _){
				  	WriteError("The search file is exists error.error info=file format is error or file size is null.\n");
				  }else{
						if(!defined $dataProgressStatus){
							my $data_file=$dbh->prepare("insert into gw_model_data_file(file_id,task_id,user_id,file_type,file_status,file_path,file_name,create_time,unzip_name) ".
																					"values(SEQ_MODEL_DATA_FILE.nextval,$taskId,$userId,1,1,'$filePath','$gzFileName',sysdate,'$txtFile')");
							$data_file->execute;
							$data_file->finish;
							WriteInfo("insert into gw_model_data_file(file_id,task_id,user_id,file_type,file_status,file_path,file_name,create_time,unzip_name) ".
												"values(SEQ_MODEL_DATA_FILE.nextval,$taskId,$userId,1,1,'$filePath','$gzFileName',sysdate,'$txtFile').\n");
						}elsif($dataProgressStatus==0){
							my $data_file=$dbh->prepare("update gw_model_data_file set unzip_name='$txtFile' where file_id=$fileId");
							$data_file->execute;
							$data_file->finish;
							WriteInfo("update gw_model_data_file set file_path='$filePath',unzip_name='$txtFile' where file_id=$fileId.\n");
						}
						
						WriteInfo("update GW_MODEL_DATA_FETCH_TASK t set t.data_progress_status='4' where task_id=$taskId. \n");
						my $taskUpdate=$dbh->prepare("update GW_MODEL_DATA_FETCH_TASK t set t.data_progress_status='4' where task_id=$taskId");
						$taskUpdate->execute;
						$taskUpdate->finish;	
				  }
		   	}
	   	};
			
			if ($@){
				WriteError("The search file is exists error.error info=$@.\n");
			}
			WriteInfo("taskId=$taskId search file is exists end.\n");
   }
   $allTasks->finish;
   
   #3��ʾԴ�ļ�ɨ����
   my $cycleConfig=$dbh->prepare("select config_unit,config_value from gw_sys_cnfig where config_type=3");
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
	 #WriteInfo("search file finished, sleep $cycleValue s. \n");
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



sub GetFTPFileName(){  
	my ($uFTP_ADDR,$uFTP_USER,$uFTP_PASSWD,$Fpath) = @_;
	eval{    
		  	
		my $ftp=Net::FTP->new("$uFTP_ADDR",Passive=>1,Debug=>0,Timeout=>30) or die "Could not connect.\n"; 
               
		#��¼��FTP                                                                      
		$ftp->login($uFTP_USER,$uFTP_PASSWD) or die "Could not login.$! \n";            
		$ftp->binary() or die "binary failed. \n";
		#�л�Ŀ¼                                                                       
		$ftp->cwd($Fpath) or die "Cannot change working directory ", $ftp->message;   
		
		my @allfiles = $ftp->ls('Done-*.txt');
		
	  $ftp->quit; 
	  return @allfiles;
	};	 	
}

sub getUserFileName(){
	my ($taskId,$dataProgressStatus,$filePath,$fileName,$serviceCode,$cycleType,$cycleValue,$orgName,$dataLoginName) = @_;
	my $gzFile='';
	my $doneTxt='';
	my $outputFile='';
	#����ָ��Ŀ¼���ļ�����ѯ
	if(defined $dataProgressStatus && $dataProgressStatus==0){
		my $txtName=$fileName;
		$txtName =~ s/.gz//;
		$doneTxt='Done-'.$txtName;
		$outputFile=$fileName;
	}else{
		#ƴ�������û�Ŀ¼��ѯ,����Done-1000-3-201505.txt��ѯ���ٸ���1000-3-201505.txt.gz
		$doneTxt = 'Done-'.$serviceCode.'-'.$cycleType.'-'.$cycleValue.'.txt';
		#gz�ļ��ϴ������Ҫ����һ����Done-��ͷ�ı�ʶ�ļ�����ʾ�ļ����ϴ����
    #�ļ����ƽ�ȡDone-��ͷ���ַ���������ļ��Ƿ����
    $outputFile = substr($doneTxt,5).".gz";		#Done-10002-3-201502.txt, 10002-3-201502.txt.gz
	}
	WriteInfo("taskId=$taskId search done file $filePath/$doneTxt.\n");
	my $doneFile = $filePath.'/'.$doneTxt;
	if(!-e $doneFile){
		WriteInfo("taskId=$taskId done file is not exists.\n");
	}else{
		WriteInfo("taskId=$taskId done file is exists.\n");	
    my $file = "$filePath/$outputFile";
    if (!-e $file) {
    	WriteInfo("taskId=$taskId local gz file is not exists!file name=$file.\n"); 
    }else{
    	WriteInfo("taskId=$taskId local gz file is exists!file name=$file.\n"); 
    	$gzFile = $outputFile;
    }
	}
	return $gzFile;
}

&main;