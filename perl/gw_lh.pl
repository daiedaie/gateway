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

#定时扫描时间(单位：秒)
my $CHECK_CYCLE=30;

#日志路径
my $LOG_PATH="/home/gateway/perl/logs";

#108输出文件的FTP服务器地址
my $uFTP_ADDR = "10.110.10.52";
my $uFTP_USER = "108project";
my $uFTP_PASSWD = "1234QWER2015";
#数据特区ftp文件存放目录
my $Fpath="/home/108project";

#取日期,用于生成csv文件名:20141210
my $date = strftime("%Y%m%d",localtime(time));

#ORACLE连接
my $dbname="gateway_52";
my $user = "gateway";
my $passwd = "gateway1234QWER";


sub main{
	 #设置日志 
	 SetLogPath($LOG_PATH);
	 SetLogLevel('D','D');  #文件和终端日志级别：'D', 'I', 'W', 'E', 'F', 'C'
	 SetLogSize(50000);#文件切换行数
	 SetLogHead("GW_108","GW_108");#文件和日志 前缀
	 OpenLog;
	 WriteInfo("===================开始运行========================\n");
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
    #tz文件上传完后，需要增加一个以Done-开头的标识文件，表示文件已上传完成
    #文件名称截取Done_开头的字符串，检查文件是否存在
    my $fileName = substr($file,5);
    $fileName = substr($fileName,0,index($fileName,".txt")).".tz";		#Done-10002-3-201502.txt, 10002-3-201502.tz
    $file = "$Fpath/$fileName";
    if (!-e $file) {
    	WriteInfo("local file $fileName is not exists!\n"); 
    	next;
    }

		  #文件名称格式：服务编码+“-”+周期类型+“-”周期值.txt
		  my @nameTemp = split(/\./, $fileName);
		  #获取数组长度
		  my $leng=@nameTemp;
		  #数组长度为3才是合法文件
		  if($leng == 2){
			  #获取服务编码
			  my @temp = split(/-/, $nameTemp[0]);
			  my $service_code = $temp[0];
			  my $cycle_type = $temp[1];
			  my $field_value = $temp[2];
			  WriteInfo("service code is:$service_code\n");
			  #获取服务编码对应的服务ID
	      my $service=$dbh->prepare("select service_id from GW_SERVICE where service_code = '$service_code'");
			  $service->execute;
			  my $service_id = $service->fetchrow_array();
			  $service->finish();
			  #判断服务是否存在，存在则下载数据
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
						#查询当前服务周期请求任务数
						#WriteInfo("select * from GW_MODEL_DATA_FETCH_TASK where task_status=1 and service_id='$service_id' and FIELD_CODE='$cycle_value' and FIELD_VALUE='$field_value'. \n");
						my $task_list=$dbh->prepare("select t.task_id,t.user_id from ".
																				"GW_MODEL_DATA_FETCH_TASK t  ".
																				"inner join gw_model_data_fetch f on f.fetch_id=t.fetch_id and f.desen_type='2' ".
																				"where t.data_progress_status is null and t.task_status=1 and t.service_id='$service_id' and t.FIELD_CODE='$cycle_value' and t.FIELD_VALUE='$field_value'");
						$task_list->execute;
						
						my $task_num = 0;
						my $taskIds;
						#更新对应取数任务的数据就绪状态为4，表示数据已下载
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
					 #service_id 存在
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

#信号捕抓处理,kill 或ctrl+c
sub handle{
	CloseLog;
	exit(0);
}
$SIG{TERM}=$SIG{INT}=\&handle;



sub GetFTPFileName(){  
	my ($uFTP_ADDR,$uFTP_USER,$uFTP_PASSWD,$Fpath) = @_;
	eval{    
		  	
		my $ftp=Net::FTP->new("$uFTP_ADDR",Passive=>1,Debug=>0,Timeout=>30) or die "Could not connect.\n"; 
               
		#登录到FTP                                                                      
		$ftp->login($uFTP_USER,$uFTP_PASSWD) or die "Could not login.$! \n";            
		$ftp->binary() or die "binary failed. \n";
		#切换目录                                                                       
		$ftp->cwd($Fpath) or die "Cannot change working directory ", $ftp->message;   
		
		my @allfiles = $ftp->ls('Done-*.txt');
		
	  $ftp->quit; 
	  return @allfiles;
	};
  	 	 	
}

&main;
