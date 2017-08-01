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

#定时扫描默认时间(单位：秒)
my $CHECK_CYCLE = 30;
#结果收集子进程公共key文件
my $resultKeyFile = "/home/gateway/perl/check.txt";
#处理子进程数
my $processNum = $ARGV[0];
#日志路径
my $LOG_PATH="/home/gateway/perl/logs";

#ORACLE连接
##########生产###########################################
#my $dbname="GATEWAY_52";
#my $user = "gateway";
#my $passwd = "gateway1234QWER";
##########测试#########################################
my $dbname="GATEWAY_133";
my $user = "gateway";
my $passwd = "gateway666";

#FTP 用户信息
my $gwFtpUser = "gateway";
my $gwFtpPasswd = "gateway666";
my $localPath = "/home/gateway/perl/tempData/check";

my $CHECK_NODE ="check002";

$ENV{'LANG'}='en_US.UTF-8';
$ENV{'NLS_LANG'}='SIMPLIFIED CHINESE_CHINA.ZHS16GBK';


my $dir= "/home/gateway/test";


my $running_child;

#是否继续处理标记，1标记继续，0标记不继续
my $running;
#是否存在不合规数据，1标记存在，0标记不存在
my $irregular;

my @task_pids;

#捕抓子进程结束消息信号
$SIG{CHLD} = sub {
	while( ( my $child = waitpid( -1, POSIX::WNOHANG ) ) > 0 ) {
		print "Get childe $child end signal.\n";
		$running_child --;
	}
};
#捕抓子进程异常消息信号，如果捕抓到则kill所有子进程
$SIG{ABRT} = sub {
	foreach my $child(@task_pids) {
		kill "ABRT", $child;
	}
	$running = 0;
};

#捕抓子进程检查到不合规行消息信号
#$SIG{USR1} = sub {
#	$irregular = 1;
#};

#设置日志 
SetLogPath($LOG_PATH);
SetLogLevel('D','D');  #文件和终端日志级别：'D', 'I', 'W', 'E', 'F', 'C'
SetLogSize(50000);#文件切换行数
SetLogHead("108_CHECK","108_CHECK");#文件和日志 前缀
OpenLog;
WriteInfo("===================开始运行===main====process=================\n");
WriteInfo("LOG_PATH:$LOG_PATH\n");	 
WriteInfo("CHECK_CYCLE:$CHECK_CYCLE\n");	
WriteInfo("Oracle:$user/$dbname\n");
while ( 1 > 0 ) {
	#建立数据库连接
	my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";
	#锁定该节点需要处理的任务
	my $LOCK_TASK=$dbh->prepare("update gw_model_data_fetch_task b set b.NODE_ID='$CHECK_NODE',b.NODE_STATUS='0',b.LOCK_TIME=sysdate  ". 
			" where exists(select * from GW_MODEL_DATA_FILE a,GW_MODEL_DATA_FETCH f where a.task_id = b.task_id and ".
			" f.fetch_id = b.fetch_id and f.audit_status=2 and ((f.desen_type='2' and a.FILE_TYPE = '1') or (f.desen_type='1' and a.FILE_TYPE = '6'))) ".
			" and ((b.DATA_PROGRESS_STATUS='4'  and b.Task_status = '1' and b.NODE_STATUS is null) or (b.DATA_PROGRESS_STATUS='6' and b.NODE_STATUS='2')) and rownum=1 ");
			$LOCK_TASK->execute;
			$LOCK_TASK->finish;
	#查询一条待合规检查的任务
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
     "       t.check_file_id,                             ".
     "       t.PROCESS_ID                             ".
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
     "       f.check_file_id,                             ".
     "       b.PROCESS_ID                            ".
     "from GW_MODEL_DATA_FILE a                          ".    
     "inner join GW_MODEL_DATA_FETCH_TASK b on a.task_id = b.task_id    ".                
     "inner join GW_MODEL_DATA_FETCH f on f.fetch_id = b.fetch_id and f.audit_status=2  ".
     "inner join GW_SERVICE s on s.service_id=b.service_id     										  ".
     "inner join gw_user u on b.user_id = u.user_id and u.status=1                  ".
     "inner join gw_user org on u.org_id = org.org_id and org.user_type='orgUser'   ".
     "where f.desen_type='2' and b.data_progress_status='4' and b.Task_status = '1' and a.FILE_TYPE = '1' and b.node_id ='$CHECK_NODE' and b.node_status='0'	".
     " union ".
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
     "       f.check_file_id,                             ".
     "       b.PROCESS_ID                            ".
     "   from GW_MODEL_DATA_FILE a                      ".
     " inner join GW_MODEL_DATA_FETCH_TASK b on a.task_id = b.task_id ".
     " inner join GW_MODEL_DATA_FETCH f on f.fetch_id = b.fetch_id and f.audit_status=2  ".
     " inner join GW_SERVICE s on s.service_id=b.service_id ".
     " inner join gw_user u on b.user_id = u.user_id and u.status=1 ".
     "  inner join gw_user org on u.org_id = org.org_id and org.user_type='orgUser' ".
     " where f.desen_type='1' and b.data_progress_status='6' and b.Task_status = '1' and a.FILE_TYPE = '6' and b.node_id ='$CHECK_NODE' and b.node_status='0'".
     
     ") tmp ) t  where rnum=1"
	);
	$checkTask->execute;
	
	my $gatewayRowId = "gateway_row_id";
	my $gatewayTaskId = "gateway_task_id";
	my $gatewayRowData = "row_data";
	while(my ($taskId,$serviceId,$serviceCode,$serviceName,$userId,$loginName,$dataProgressStatus,$dataFileId,$ftpIp,$ftpUser,$ftpPassword,$filePath,$fileName,$unzipName,$desenType,$maxCheckNum,$checkAudit,$outputNum,$fieldValue,$checkFileId,$processId) = $checkTask->fetchrow_array()){
		eval{
			WriteInfo("taskId=$taskId rule check start...\n");
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
			
			#取文件
			#2016-9-17 添加ftp远程读取
			my $ftpInfo=$dbh->prepare("select ftp_ip from GW_SYS_FTP t where ftp_type='1' and rownum=1");
			$ftpInfo->execute;
			my $ftpIp=$ftpInfo->fetchrow_array();
			$ftpInfo->finish;
			
			#取发ftp用户登录的相对路径
      my $remotePath = $filePath;
      $remotePath =~ s/\/home\/gateway//;
			my $ftp_flag=0; #0:down  1:up
			my $rst = FTPFile($ftpIp,$gwFtpUser,$gwFtpPasswd,$localPath,$remotePath,$fileName,$ftp_flag);
			if($rst != 0 ){	 				
				next;
			}
			
      #$filePath=$localPath;
			#2016-9-17 添加ftp远程读取
			my $gzFile_path="$localPath/$fileName";
			my $txtFile_path=$localPath."/".$unzipName;
			
			#if(!-e $txtFile_path){
				#my $gzOutputFile=$filePath.'/'.$fileName;
				if(!-e $gzFile_path){
					WriteInfo("$gzFile_path 服务元数据的gz文件不存在!\n"); 
					next;#文件不存在退出该检查
				}
		  	
		    #解压gz文件
		    my $gunzip = 'gunzip -c '.$gzFile_path." > ".$txtFile_path;
		    WriteInfo("file gunzip $gzFile_path start.\n");
		  	system($gunzip);
		  	WriteInfo("file gunzip $gzFile_path finish.\n");
		  #}
		  
			if (!-e $txtFile_path) {
				WriteInfo("$txtFile_path 文件不存在!\n"); 
				next;#文件不存在退出该检查
			}
			WriteInfo("check file=".$txtFile_path."\n");
			
			#修改任务数据就绪状态，合规检查中
			my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=7,DOWNLOAD_START_TIME=sysdate,check_file_id=$checkFileId,check_batch=$maxCheckBatch,CHECK_AUDIT='$checkAudit',OUTPUT_NUM=$outputNum,NODE_STATUS='1' where task_id=$taskId");
			$task->execute;
			$task->finish;
			WriteInfo("update gw_model_data_fetch_task set data_progress_status=7,DOWNLOAD_START_TIME=sysdate,check_file_id=$checkFileId,check_batch=$maxCheckBatch,CHECK_AUDIT='$checkAudit',OUTPUT_NUM=$outputNum where task_id=$taskId.\n");
			
			#2016-8-19 添加流程跟踪登记
			my $process=$dbh->prepare("update gw_process set progress_status='3',STEP_STATUS='1' where process_id=$processId");
			$process->execute;
			$process->finish;
			#my $operateContent = encode('gbk',decode('utf-8',"开始服务数据合规检查处理"));
			my $operateContent = "开始服务数据合规检查处理";
			$operateContent = encode('gbk',decode('utf-8',$operateContent));
			my $processOper=$dbh->prepare("update gw_process_operation set operate_content='$operateContent',operate_time = sysdate ,progress_status = 1 ,dealtype='system' where process_id=$processId and step = '34'");
			$processOper->execute;
			$processOper->finish;
			WriteInfo("update gw_process set progress_status='3',STEP_STATUS='1' where process_id=$processId  \n");		
			#2016-8-19 添加流程跟踪登记
			
			my $uFpath="/home/gateway/users/$loginName/result";
			my $irregularTxtFile="Irregular_".$unzipName;
			my $irregularTxtFile_Path = "$localPath/$irregularTxtFile";
			#不合规输出txt文件，一般不会有
			if (-f $irregularTxtFile_Path) {
				WriteInfo("delete irregular txt file $irregularTxtFile_Path start!\n");
		 		unlink $irregularTxtFile_Path;
		 		WriteInfo("delete irregular txt file $irregularTxtFile_Path Success!\n");		
		 	}
			WriteInfo("irregularTxtFile=$uFpath/$irregularTxtFile.\n");
			WriteInfo("taskId=$taskId file rule check start. fileName=$txtFile_path.\n");
			
			#文件处理
			process_file($txtFile_path,$irregularTxtFile,$uFpath,$userId,$serviceId,$taskId,$loginName,$resultKeyFile,$dataFileId,$processNum,$checkAudit,$maxCheckNum,$serviceCode,$serviceName,$fieldValue,$fileName,$localPath,$unzipName,$outputNum,$maxCheckBatch,$processId);
			
		};#eval end	
		if($@){
			#合规检查过程中出现问题，修改任务数据就绪状态为处理失败
			my $upTask=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId");
			$upTask->execute;
			$upTask->finish;
			
			#2016-8-19 添加流程跟踪登记
			my $operateContent = "合规检查处理异常";
			$operateContent = encode('gbk',decode('utf-8',$operateContent));
			my $processOper=$dbh->prepare("insert into gw_process_operation (OPERATE_ID,user_Id,operate_content,operate_time,plan_id,progress_status,dealtype,step,process_id) values(seq_gw_process_operation.nextval,null,'$operateContent',sysdate,null,0,'system','34',$processId)");
			$processOper->execute;
			$processOper->finish;
			my $process=$dbh->prepare("update gw_process set progress_status='3',STEP_STATUS='1',STATUS='0' where process_id=$processId");
			$process->execute;
			$process->finish;
			#2016-8-19 添加流程跟踪登记
			
			foreach my $child(@task_pids) {
				kill "ABRT", $child;
			}
			WriteInfo("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId.\n");
			
			WriteError("taskId=$taskId task rule check error!.\n");
			WriteError("$@.\n");
		}
	}

  
	#2获取脱敏/检查时间间隔
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



#文件处理函数
sub process_file
{
			
	my ($txtFile_path,$irregularTxtFile,$uFpath,$userId,$serviceId,$taskId,$loginName,$resultKeyFile,$dataFileId,$processNum,$checkAudit,$maxCheckNum,$serviceCode,$serviceName,$fieldValue,$fileName,$localPath,$unzipName,$outputNum,$maxCheckBatch,$processId) = @_;
	my ($key,$msg,$msgid,$msgtype,$buf);
	#print "Processing file $file\n";
	$key = IPC::SysV::ftok($txtFile_path);
	#print "Open MQ with ID: $key\n";
	#创建消息队列
	$msg = new IPC::Msg($key, IPC_SET | S_IRWXU | IPC_CREAT) or die "Create MQ faile!";
  #消息队列id
	$msgid = $msg->id();
	#print "Running task with msgid: ",$msgid,"\n";
	
	my ($reskey,$resmsg,$resmsgid,$resmsgtype,$resbuf);
	$reskey = IPC::SysV::ftok($resultKeyFile);
	#创建结果消息队列
	$resmsg = new IPC::Msg($reskey, IPC_SET | S_IRWXU | IPC_CREAT) or die "Create MQ faile!";

	$running_child = 0;
	#是否继续处理标记，1标记继续，0标记不继续
	$running = 1;
	#是否存在不合规数据
	#$irregular = 0;
	
	@task_pids=();
	my $pid;
	#启动4个子进程，子进程perl脚本为child.pl
	for (my $i=0; $i < $processNum ; $i++) {
		$pid = fork();
		if( $pid < 0 ) {
			die "Failed to fork child ..."; # Fix me: remember to kill all forked child process.
		}
		elsif ( $pid == 0 ) {
			# Child process
			exec("perl ./gw_108_check_child.pl $txtFile_path $userId $serviceId $taskId $resultKeyFile $dataFileId");
			exit(1);
		}
		else {
			#将子进程Pid放到子进程id数组，为某一子进程出错后kill其他所有子进程
			push(@task_pids, $pid);
			#登记子进程数量，用作子进程结束统计
			$running_child ++;
		}
	}
	#创建结果子进程
	$pid = fork();
	if( $pid < 0 ) {
		die "Failed to fork child ..."; # Fix me: remember to kill all forked child process.
	}
	elsif ( $pid == 0 ) {
		# Child process
		exec("perl ./gw_108_check_result.pl $taskId $dataFileId $processNum $uFpath $irregularTxtFile $userId $checkAudit $maxCheckNum $serviceId $loginName $serviceCode $serviceName $fieldValue $fileName $localPath $unzipName $outputNum $maxCheckBatch $processId $CHECK_NODE");
		exit(1);
	}
	else {
		#登记子进程数量，用作子进程结束统计
		$running_child ++;
	}
	my $rowNum = 0;
	my $row = "";
	#Read file
	open( my $FILE, $txtFile_path ) or die $!;
	#定义正常类型消息
	$msgtype = 1;
	#读取文件行内容并发送
	while( <$FILE> ) {
		$rowNum++;
		#chomp $row;
		s/[\r\n]//g;	#去除换行符
		#print "$row\n";
		#WriteInfo("=====rowNum===$rowNum====row===$_==. \n");
		$row = $rowNum."::".$_;
		#发送行内容到消息队列
		
		$msg->snd($msgtype, $row );
		if ( $running == 0 ) {
			last;#子进程出错则结束读取文件(有异常、存在多列/缺列、不合规行数超出)
		}
	}	
	close( FILE);
			
	#非异常结束
	# send EOF message to every child.
	#定义结束类型消息
	$msgtype = 99;
	#WriteInfo("===main==total===$rowNum=========. \n");
	#给每一个子进程发送结束消息
	for( my $i=0; $i < $processNum; $i ++ ) {
		$msg->snd($msgtype, $rowNum);
	}
	# wait all child finish，
	while( $running_child != 0 ) {
		
		next;
		
	}	
	
	print "All task process end.\n";

	@task_pids=();

	$msg->remove();
	$resmsg->remove();
}

sub FTPFile{  
	my ($FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag) = @_;
	#WriteInfo("FTP文件信息：$FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag. \n");
	eval{    
		  	
		my $ftp=Net::FTP->new("$FTP_ADDR",Passive=>1,Debug=>0,Timeout=>30) or die "Could not connect host.\n"; 
               
		#登录到FTP                                                                      
		$ftp->login($Fuser,$Fpassword) or die "Could not login.$! \n", $ftp->message;            
		$ftp->binary() or die "binary failed. \n";
		if(defined $Fpath) {
			#切换目录                                                                       
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


