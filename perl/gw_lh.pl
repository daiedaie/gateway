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

#108����ļ���FTP��������ַ
my $uFTP_ADDR = "10.110.10.52";
my $uFTP_USER = "108project";
my $uFTP_PASSWD = "1234QWER2015";
#��������ftp�ļ����Ŀ¼
my $Fpath="/home/108project";

#ȡ����,��������csv�ļ���:20141210
my $date = strftime("%Y%m%d",localtime(time));

#ORACLE����
my $dbname="gateway_52";
my $user = "gateway";
my $passwd = "gateway1234QWER";


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
   WriteInfo("uFTP_ADDR:$uFTP_ADDR\n");
   while(1){
   my @rst= GetFTPFileName($uFTP_ADDR,$uFTP_USER,$uFTP_PASSWD,$Fpath);
   my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can not connect to database";
	print "connetc success!\n";
   foreach my $file (@rst){
	print $file,"\n";
   	WriteInfo("file name:$file\n");	
    #tz�ļ��ϴ������Ҫ����һ����Done-��ͷ�ı�ʶ�ļ�����ʾ�ļ����ϴ����
    #�ļ����ƽ�ȡDone_��ͷ���ַ���������ļ��Ƿ����
    my $fileName = substr($file,5);
    $fileName = substr($fileName,0,index($fileName,".txt")).".tz";		#Done-10002-3-201502.txt, 10002-3-201502.tz
    $file = "$Fpath/$fileName";
    if (!-e $file) {
    	WriteInfo("local file $fileName is not exists!\n"); 
    	next;
    }

		  #�ļ����Ƹ�ʽ���������+��-��+��������+��-������ֵ.txt
		  my @nameTemp = split(/\./, $fileName);
		  #��ȡ���鳤��
		  my $leng=@nameTemp;
		  #���鳤��Ϊ3���ǺϷ��ļ�
		  if($leng == 2){
			  #��ȡ�������
			  my @temp = split(/-/, $nameTemp[0]);
			  my $service_code = $temp[0];
			  my $cycle_type = $temp[1];
			  my $field_value = $temp[2];
			  WriteInfo("service code is:$service_code\n");
			  #��ȡ��������Ӧ�ķ���ID
	      my $service=$dbh->prepare("select service_id from GW_SERVICE where service_code = '$service_code'");
			  $service->execute;
			  my $service_id = $service->fetchrow_array();
			  $service->finish();
			  #�жϷ����Ƿ���ڣ���������������
			  if($service_id){
				 my $cycle_value = "";
				 if($cycle_type == 1){
					$cycle_value = "year_id";
				 }else{
					if($cycle_type == 2){
						$cycle_value = "quarter_id";
					}else{
						if($cycle_type == 3){
							$cycle_value = "month_id";
						}else{
							if($cycle_type == 4){
								$cycle_value = "week_id";
							}else{
								if($cycle_type == 5){
									$cycle_value = "day_id";
								}
							}
						}
					}
				 }
				 WriteInfo("service id is $service_id.\n");
				  if($service_id){		 			 	 		
						#��ѯ��ǰ������������������
						#WriteInfo("select * from GW_MODEL_DATA_FETCH_TASK where task_status=1 and service_id='$service_id' and FIELD_CODE='$cycle_value' and FIELD_VALUE='$field_value'. \n");
						my $task_list=$dbh->prepare("select t.task_id,t.user_id from ".
																				"GW_MODEL_DATA_FETCH_TASK t  ".
																				"inner join gw_model_data_fetch f on f.fetch_id=t.fetch_id and f.desen_type='2' ".
																				"where t.data_progress_status is null and t.task_status=1 and t.service_id='$service_id' and t.FIELD_CODE='$cycle_value' and t.FIELD_VALUE='$field_value'");
						$task_list->execute;
						
						my $task_num = 0;
						my $taskIds;
						#���¶�Ӧȡ����������ݾ���״̬Ϊ4����ʾ����������
						while(my ($task_id,$user_id) = $task_list->fetchrow_array()){
							$taskIds = $taskIds.$task_id.",";
							my $data_file=$dbh->prepare("insert into gw_model_data_file(file_id,task_id,user_id,file_type,file_status,file_path,file_name,create_time) ".
																					"values(SEQ_MODEL_DATA_FILE.nextval,$task_id,$user_id,1,1,'$Fpath','$fileName',sysdate)");
							$data_file->execute;
							$data_file->finish;
							WriteInfo("insert into gw_model_data_file(file_id,task_id,user_id,file_type,file_status,file_path,file_name,create_time) ".
												"values(SEQ_MODEL_DATA_FILE.nextval,$task_id,$user_id,1,1,'$Fpath','$fileName',sysdate).\n");
							
							my $task_u=$dbh->prepare("update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=4 where task_id='$task_id'");
							$task_u->execute;	
							$task_u->finish();
							WriteInfo("update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=4 where task_id='$task_id' and task_status=1 !\n"); 
							
							$task_num++;
						}
						$task_list->finish();
								  
						if ($@){
							WriteError("The insertting log is fail.\n");
						}else{
							WriteInfo("update GW_MODEL_FETCH_TASK DATA_PROGRESS_STATUS finish.taskIds=$taskIds.\n");
						}
					 #service_id ����
				  }else{
				  	WriteInfo("service code is:$service_code is not exists.\n");
				  }
			  }
			}
   }
   $dbh->disconnect;

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

&main;
