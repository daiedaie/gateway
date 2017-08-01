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

#执行参数取值
my $taskId = $ARGV[0]; #任务ID
my $modelId = $ARGV[1];
my $userId = $ARGV[2];
my $serviceId = $ARGV[3]; #服务ID
my $dataUserPath = $ARGV[4];
my $outputDesenFile = $ARGV[5]; #脱敏文件
my $gatewayCycleField = $ARGV[6];
my $gatewayRowId = $ARGV[7];
my $gatewayTaskId = $ARGV[8];
my $fieldValue = $ARGV[9];
my $processNum = $ARGV[10]; #处理子进程数
my $localPath = $ARGV[11];#本地脱敏临时文件目录
my $desen_field = $ARGV[12];#本地脱敏临时文件目录
my $processId = $ARGV[13];

my $pid = $$;
my $ppid = getppid();

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
my $ftpUser = "gateway";
my $ftpPasswd = "gateway666";

$ENV{'LANG'}='en_US.UTF-8';
$ENV{'NLS_LANG'}='SIMPLIFIED CHINESE_CHINA.ZHS16GBK';

#设置日志 
SetLogPath($LOG_PATH);
SetLogLevel('D','D');  #文件和终端日志级别：'D', 'I', 'W', 'E', 'F', 'C'
SetLogHead("GW_DESEN","GW_DESEN");#文件和日志 前缀
OpenLog;
WriteInfo("===================result运行======$pid==================\n");
WriteInfo("LOG_PATH:$LOG_PATH\n");	
#WriteInfo("Oracle:$user/$dbname\n");

#创建数据库连接
my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";

my ($reskey,$resmsg,$resmsgid,$resmsgtype,$resbuf);
$reskey = IPC::SysV::ftok("/home/gateway/perl/desen.txt");
#打开结果消息队列
$resmsg = new IPC::Msg( $reskey, 0) or die "Open MQ faile!";

my $loop = 1;
my @contents;
my $bufstr = "";
my $totalRow = 0; #文件总行数
my $rowNum = 0;
my $desens_count = 0;
my $dbLimit = 1000;	#行数据插入到数据库的限制数量，超过不再插入到数据库
my $endCount = 0; #处理子进程正常结束个数
my $errorTag = 0; #是否执行异常标记，0正常，1表示异常
#my $desen_field;
my $localResultP = "$localPath/result";
open(DESEN_FILE,"> $localResultP/$outputDesenFile") or die "Open desen file $dataUserPath/$outputDesenFile error.\n";
#print "=================result===open file=$dataUserPath/$outputDesenFile.\n";
while( $loop == 1 ) {
	#读取消息队列的行数据
	$resmsgtype = $resmsg->rcv($resbuf, 1024);
	if(defined $resmsgtype){
		#print "=================result==resmsgtype=$resmsgtype=the result buffer is=$resbuf.\n";
		@contents = split(::,$resbuf);
		#写不合规记录入库
		$rowNum = $contents[0];
		$bufstr = $contents[1];
		$bufstr = encode("gbk",decode("gbk",$bufstr));
		if ( $resmsgtype == 1 ) {#脱敏后的数据
			#######输出行数据
			
			#print "=================result==file=$dataUserPath/$outputDesenFile=.\n";
			my $writStr = encode("utf-8",decode("gbk",$bufstr));
			#print "=================writStr==file=$writStr=.\n";
			printf DESEN_FILE $writStr;
			printf DESEN_FILE "\r\n";
			#print "=================result==close file=$dataUserPath/$outputDesenFile=.\n";
			
	  	$desens_count++; #脱敏后记录数
	  	#print "=================result==desens_count=$desens_count=.\n";
	 		if($desens_count <= $dbLimit){
	 			$bufstr =~ s/\|/\',\'/g;
	 			
		 		my $desenInsert=$dbh->prepare("insert into GW_SERVICE_DESEN_$serviceId(gateway_task_id,gateway_row_id,$gatewayCycleField,$desen_field) ".
		 		"values($taskId,$rowNum,'$fieldValue','$bufstr')");
				$desenInsert->execute;
				$desenInsert->finish;
				#print "=================result==insert $str finish=.\n";
			}
		}elsif ( $resmsgtype == 2 ) {#表头
			#printf DESEN_FILE encode("gbk",$resbuf);
			#printf DESEN_FILE $resbuf;
			#printf DESEN_FILE "\r\n";
			#脱敏后的文件需要进行合规检查，但合规检查数据文件是没有表头的。
		}	elsif ( $resmsgtype == -1 ) {#异常
			#处理子进程执行异常消息
			$errorTag = 1;
			#合规检查过程中出现问题，修改任务数据就绪状态为处理失败
			my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId");
			$task->execute;
			$task->finish;
			
			#2016-8-19 添加流程跟踪登记
			#数据处理异常
			my $operateContent = "数据脱敏处理异常";
			$operateContent = encode('gbk',decode('utf-8',$operateContent));
			my $processOper=$dbh->prepare("insert into gw_process_operation (OPERATE_ID,user_Id,operate_content,operate_time,plan_id,progress_status,dealtype,step,process_id) values(seq_gw_process_operation.nextval,null,'$operateContent',sysdate,null,0,'system','33',$processId)");
			$processOper->execute;
			$processOper->finish;
			my $process=$dbh->prepare("update gw_process set progress_status='3',STEP_STATUS='1',STATUS='0' where process_id=$processId");
			$process->execute;
			$process->finish;
									
			#2016-8-19 添加流程跟踪登记
			
		  #写log
			WriteInfo("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId.\n");
			WriteError("taskId=$taskId task rule check error!.\n");
			WriteError("$@.\n");
			$loop = 0;
		}else {
			WriteInfo("=======result====get the end tag =$resbuf.\n");
			$endCount ++;
			#正常结束个数
			if($endCount == $processNum){
				$totalRow = $bufstr; #获取总行数
				WriteInfo("=======result====taskId=$taskId the all process run end.\n");
				$rowNum = $totalRow;
				$loop = 0;
			}				
		}
	}
}
close DESEN_FILE;
#print "=================result===close file=$dataUserPath/$outputDesenFile.\n";
WriteInfo("==result=taskId=$taskId file rule check end.\n");

#数据处理结束
my $operateContent = "服务数据脱敏处理结束";
$operateContent = encode('gbk',decode('utf-8',$operateContent));
my $processOper=$dbh->prepare("insert into gw_process_operation (OPERATE_ID,user_Id,operate_content,operate_time,plan_id,progress_status,dealtype,step,process_id) values(seq_gw_process_operation.nextval,null,'$operateContent',sysdate,null,0,'system','33',$processId)");
$processOper->execute;
$processOper->finish;

if($errorTag == 1){
	#2016-8-19 添加流程跟踪登记
	my $operateContent = "数据脱敏处理异常";
	$operateContent = encode('gbk',decode('utf-8',$operateContent));
	my $processOper=$dbh->prepare("insert into gw_process_operation (OPERATE_ID,user_Id,operate_content,operate_time,plan_id,progress_status,dealtype,step,process_id) values(seq_gw_process_operation.nextval,null,'$operateContent',sysdate,null,0,'system','33',$processId)");
	$processOper->execute;
	$processOper->finish;
	my $process=$dbh->prepare("update gw_process set progress_status='3',STEP_STATUS='1',STATUS='0' where process_id=$processId");
	$process->execute;
	$process->finish;
	#2016-8-19 添加流程跟踪登记
	#发送异常信号给主进程，结束处理子进程
	print "===================================  send the ABRT signal to parent =======\n";
	kill 'ABRT', $ppid;
}
if($errorTag != 1){
	#csv文件生成完，设置文件为已脱敏 6接下来要进行合规检查.------改为新插入一条记录
	
	#压缩gz文件
	my $gzFileName = $outputDesenFile.".gz";
  my $zipfile = "gzip -cf ".$localResultP."\/".$outputDesenFile." > ".$localResultP."\/".$gzFileName;
  WriteInfo("file zip $outputDesenFile start.\n");
	system($zipfile);
	WriteInfo("file zip $outputDesenFile finish.\n");
	
	#删除脱敏文件记录
	my $sth_d=$dbh->prepare("delete GW_MODEL_DATA_FILE where TASK_ID = $taskId and FILE_TYPE=6 ");
	$sth_d->execute;
	$sth_d->finish;
	#$sth_u=$dbh->prepare("update GW_MODEL_DATA_FILE set FILE_TYPE = '2' where task_id=$task_id");
	my $curTime = strftime("%Y-%m-%d %H:%M:%S",localtime(time));
	my $sth_u=$dbh->prepare(
		"insert into GW_MODEL_DATA_FILE (FILE_ID,TASK_ID,MODEL_ID,USER_ID,FILE_TYPE,FILE_STATUS,FILE_PATH,FILE_NAME,CREATE_TIME,FTP_IP,FTP_PORT,unzip_name)".
    "values (SEQ_MODEL_DATA_FILE.NEXTVAL,$taskId,$modelId,$userId,6,1,'$dataUserPath','$gzFileName',to_date('$curTime', 'yyyy-mm-dd hh24:mi:ss'),'',null,'$outputDesenFile')"
  );
	$sth_u->execute;	
	$sth_u->finish(); 
	WriteInfo("Insert GW_MODEL_DATA_FILE finished!\n");
	
	#2016-9-17 上传脱敏后文件到gateway ftp服务器
	#获取gateway ftp 服务器ip
	my $ftpInfo=$dbh->prepare("select ftp_ip from GW_SYS_FTP t where ftp_type='1' and rownum=1");
	$ftpInfo->execute;
	my $ftpIp=$ftpInfo->fetchrow_array();
	$ftpInfo->finish;
	#取发ftp用户登录的相对路径
  my $remotePath = $dataUserPath;
  $remotePath =~ s/\/home\/gateway//;
	my $ftp_flag=1; #0:down  1:up
	my $rst = FTPFile($ftpIp,$ftpUser,$ftpPasswd,$localResultP,$remotePath,$gzFileName,$ftp_flag);
	if($rst != 0 ){	 				
		next;
	}
	#2016-9-17 上传脱敏后文件到gateway ftp服务器
		
 	
 	#csv脱敏完成，设置任务状态 6结果生成,任务结束时间,数据类型,记录数
 	$curTime = strftime("%Y-%m-%d %H:%M:%S",localtime(time));
	$sth_u=$dbh->prepare("update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=6,NODE_STATUS=2,DATA_TYPE=1,DATA_NUM=$desens_count where task_id=$taskId");
	$sth_u->execute;	
	$sth_u->finish();  	
	WriteInfo("Update GW_MODEL_DATA_FETCH_TASK DATA_PROGRESS_STATUS=6,DATA_TYPE=1,DATA_NUM=$desens_count where task_id=$taskId.\n");		
  WriteInfo("The task is finished.\n");
  #2016-8-19 添加流程跟踪登记
	#脱敏处理结束
	
	my $operateContent = "待合规检查";
	$operateContent = encode('gbk',decode('utf-8',$operateContent));
	$processOper=$dbh->prepare("insert into gw_process_operation (OPERATE_ID,user_Id,operate_content,operate_time,plan_id,progress_status,dealtype,step,process_id) values(seq_gw_process_operation.nextval,null,'$operateContent',sysdate,null,1,'system','34',$processId)");
	$processOper->execute;
	$processOper->finish;
	WriteInfo("insert into gw_process_operation (user_Id,operate_content,operate_time,plan_id,progress_status,dealtype,step,process_id) values(null,'$operateContent',sysdate,null,1,'system','35',$processId).\n");
	#2016-8-19 添加流程跟踪登记
	
	WriteInfo("execute rm -rf ".$localPath."/* clean start.\n");
	#system("rm -rf ".$localPath."/*");
	#system("mkdir ".$localPath."/result");
	WriteInfo("execute mkdir ".$localPath."/result finish.\n");
}

print "the result Child $pid end.\n";

sub FTPFile{  
	my ($FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag) = @_;
	#WriteInfo("FTP文件信息：$FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag. \n");
	eval{    
		  	
		my $ftp=Net::FTP->new("$FTP_ADDR",Passive=>1,Debug=>0,Timeout=>30) or die "Could not connect.\n"; 
               
		#登录到FTP                                                                      
		$ftp->login($Fuser,$Fpassword) or die "Could not login.$! \n";            
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
