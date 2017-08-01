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
my $dataFileId = $ARGV[1]; #数据文件ID
my $processNum = $ARGV[2]; #处理子进程数
my $uFpath = $ARGV[3]; #不合规文件目录
my $irregularTxtFile = $ARGV[4]; #不合规文件名称
my $userId = $ARGV[5]; #用户ID
my $checkAudit = $ARGV[6]; #是否需要审核标记
my $maxCheckNum = $ARGV[7]; #最大检查行数
my $serviceId = $ARGV[8]; #服务ID
my $loginName = $ARGV[9]; #登录名
my $serviceCode = $ARGV[10]; #服务编码
my $serviceName = $ARGV[11]; #服务名称
my $fieldValue = $ARGV[12]; #服务周期
my $fileName = $ARGV[13]; #文件名称
my $filePath = $ARGV[14]; #原文件目录
my $unzipName = $ARGV[15]; #解压后原文件名称
my $outputNum = $ARGV[16]; #输出行数，0表示全部输出
my $maxCheckBatch = $ARGV[17];
my $processId = $ARGV[18];#流程ID
my $nodeCode = $ARGV[19];#节点编码

my $pid = $$;
my $ppid = getppid();

#日志路径
my $LOG_PATH="/home/gateway/perl/logs";
#定时扫描默认时间(单位：秒)
my $CHECK_CYCLE = 30;

my $gatewayRowId = "gateway_row_id";
my $gatewayTaskId = "gateway_task_id";
my $gatewayRowData = "row_data";

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
$ENV{'LANG'}='en_US.UTF-8';
$ENV{'NLS_LANG'}='SIMPLIFIED CHINESE_CHINA.ZHS16GBK';

#设置日志 
SetLogPath($LOG_PATH);
SetLogLevel('D','D');  #文件和终端日志级别：'D', 'I', 'W', 'E', 'F', 'C'
SetLogHead("108_CHECK","108_CHECK");#文件和日志 前缀
OpenLog;
WriteInfo("===================开始运行====result==process=======$processId===========\n");
#WriteInfo("LOG_PATH:$LOG_PATH\n");	 
#WriteInfo("CHECK_CYCLE:$CHECK_CYCLE\n");	
#WriteInfo("Oracle:$user/$dbname\n");

#创建数据库连接
my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";


my $rowIrregularCount = 0; #不合规累计行数
my $dbLimit = 1000;	#不合规行数据插入到数据库的限制数量，超过不再插入到数据
my $moreTag = 0; #是否多列标记，0表示不存在，1表示存在
my $loseTag = 0; #是否少列标记，0表示不存在，1表示存在
my $overTag = 0; #是否超出最大不合规标记，0表示没有超出，1表示已超出
my $endCount = 0; #处理子进程正常结束个数
my $errorTag = 0; #是否执行异常标记，0表示不存在，1表示存在


#服务来源
my $sourceType=$dbh->prepare("select service_source from gw_service where service_id=$serviceId");
$sourceType->execute;
my $serviceSource=$sourceType->fetchrow_array();
$sourceType->finish();


my ($reskey,$resmsg,$resmsgid,$resmsgtype,$resbuf);
$reskey = IPC::SysV::ftok("/home/gateway/perl/check.txt");
#打开结果消息队列
$resmsg = new IPC::Msg( $reskey, 0) or die "Open MQ faile!";

my $loop = 1;
my @contents;
my $totalRow = 0; #文件总行数
my $irregularTxtFile_Path = "$filePath/$irregularTxtFile";
my $rowNum = 0;
#取发ftp用户登录的结果文件目录相对路径
my $remotePath = $uFpath;
$remotePath =~ s/\/home\/gateway//;
open(IRREGULAR_FILE,"> $irregularTxtFile_Path") or die "Open file $irregularTxtFile_Path error.\n";
eval{
while( $loop == 1 ) {
	
	#读取消息队列的行数据
	$resmsgtype = $resmsg->rcv($resbuf, 1024);
	if(defined $resmsgtype){
		#WriteInfo("=result==resmsgtype=$resmsgtype==resbuf======$resbuf===========.\n");
		@contents = split(::,$resbuf);
		#写不合规记录入库
		$rowNum = $contents[0];
		my $str = $contents[1];
		my $data_value = encode('gbk',decode('gbk',$str));
		#WriteInfo("=result===rowNum===$rowNum==resbuf======$data_value===========.\n");
		if ( $resmsgtype == 1 ) {#不合规行数数据
			$rowIrregularCount ++;
			if($rowIrregularCount == 1 && $serviceSource == 1){
				my $sth_field=$dbh->prepare("select GW_GET_DESENS_FILE_HEAD($serviceId,$userId,1) from dual");
				$sth_field->execute;
			 	my $desens_field = $sth_field->fetchrow_array();
			 	$sth_field->finish();
				my $insertSql=$dbh->prepare("insert into gw_service_check_$serviceId($gatewayRowId,$gatewayTaskId,$gatewayRowData) values(0,$taskId,'$desens_field')");
				$insertSql->execute;
				$insertSql->finish();
			}
			my @irregularBuf = split(/\>\>/,$str);
			$str = $irregularBuf[0];
			my $irregularField = $irregularBuf[1];
			#写入不合规文件
			printf IRREGULAR_FILE $rowIrregularCount.",".$rowNum.",".$str.",".$irregularField;
			printf IRREGULAR_FILE "\r\n";
			
			
			#不合规记录数小于等于最大登记数，不合规记录入库
			if($rowIrregularCount <= $dbLimit){
				
				#$str =~ s/\",\"/\',\'/g; #替换","为',' ---csv换成txt后不需要去除引号
				
				#将分隔符|转成逗号，并加引号，所有字段都加上单引号，转成字符串插入缓存表
				#$str =~ s/\|/\',\'/g; 
				#$str ="\'$str\'";
				#$str = encode('gbk',decode('utf-8',$str));
				#WriteInfo("=result==rowIrregular=rowNum===$rowNum==resbuf======$str===========.\n");
				my $insertSql=$dbh->prepare("insert into gw_service_check_$serviceId($gatewayRowId,$gatewayTaskId,$gatewayRowData) values($rowNum,$taskId,'$str')");
				$insertSql->execute;
				$insertSql->finish();
			}
			#不合规记录数超出最大检查数，退出检查
			if($maxCheckNum != 0 && $rowIrregularCount > $maxCheckNum){
				WriteInfo("taskId=$taskId不合规数据量($rowIrregularCount)超出不合规最大检查行数($maxCheckNum)，自动退出检查.\n");
				$overTag = 1;	
				$loop = 0;
			}
		}elsif ( $resmsgtype == 2 ) {#多字段
			$moreTag = 1;
			$rowIrregularCount ++;
			
			my $warnType = 1;
			#清除该任务警告信息
			
			my $warnInfo=$dbh->prepare("delete gw_service_check_warn where task_id=$taskId");
			$warnInfo->execute;
			#插入多列警告信息
			$warnInfo=$dbh->prepare("insert into gw_service_check_warn(warn_id,warn_type,warn_row,task_id,data_file_id,row_data) ".
															"values(seq_gw_service_check_warn.nextval,$warnType,$rowNum,$taskId,$dataFileId,'$data_value')");
			$warnInfo->execute;
			$warnInfo->finish();
			#退出
			$loop = 0;
			
		}	elsif ( $resmsgtype == 3 ) {#少字段
			$loseTag = 1;
			$rowIrregularCount ++;
			
			my $warnType = 2;
			#清除该任务警告信息
			my $warnInfo=$dbh->prepare("delete gw_service_check_warn where task_id=$taskId");
			$warnInfo->execute;
			#插入多列警告信息
			$warnInfo=$dbh->prepare("insert into gw_service_check_warn(warn_id,warn_type,warn_row,task_id,data_file_id,row_data) ".
															"values(seq_gw_service_check_warn.nextval,$warnType,$rowNum,$taskId,$dataFileId,'$data_value')");
			$warnInfo->execute;
			$warnInfo->finish();
			#退出
			$loop = 0;
			
		}	elsif ( $resmsgtype == 4 ) {#异常
			#处理子进程执行异常消息
			$errorTag = 1;
			#合规检查过程中出现问题，修改任务数据就绪状态为处理失败
			my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId");
			$task->execute;
			$task->finish;
			
			#2016-8-19 添加流程跟踪登记
			#数据处理异常
			my $operateContent = "合规检查处理异常";
			$operateContent = encode('gbk',decode('utf-8',$operateContent));
			my $processOper=$dbh->prepare("insert into gw_process_operation (OPERATE_ID,user_Id,operate_content,operate_time,plan_id,progress_status,dealtype,step,process_id) values(seq_gw_process_operation.nextval,null,'$operateContent',sysdate,null,0,'system','34',$processId)");
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
			$endCount ++;
			#正常结束个数
			#WriteInfo("==result===total===$rowNum=========. \n");
			if($endCount == $processNum){
				$totalRow = $rowNum; #获取总行数
				#WriteInfo("========taskId=$taskId the all process run end.\n");
				#$rowNum = $totalRow;
				$loop = 0;
			}				
		}
	}
	
}
close IRREGULAR_FILE;
WriteInfo("taskId=$taskId file rule check end.\n");
#WriteInfo("==reult===totalRow===$totalRow=========. \n");
#删除多列/少列之后行的不合规记录
if($moreTag == 1 || $loseTag == 1 ){
	my $moreErr=$dbh->prepare("select count(*) from gw_service_check_$serviceId where gateway_task_id=$taskId and gateway_row_id > $rowNum");
	$moreErr->execute;
	my $moreCount=$moreErr->fetchrow_array();
	$moreErr->finish();
	if($moreCount > 0){
		my $datel=$dbh->prepare("delete from gw_service_check_$serviceId where gateway_task_id=$taskId and gateway_row_id > $rowNum");
		$datel->execute;
		$datel->finish;
		#扣除已登记单超出多列/少列所在行号的不合规记录行数
		$rowIrregularCount = $rowIrregularCount - $moreCount;
		WriteInfo("delete from gw_service_check_$serviceId where gateway_task_id=$taskId and gateway_row_id > $rowNum.\n");
	}
}
if($moreTag == 1 || $loseTag == 1 || $errorTag == 1 || $overTag == 1){
	#发送异常信号给主进程，结束处理子进程
	print "===================================  send the ABRT signal to parent =======\n";
	kill 'ABRT', $ppid;
}
#数据处理结束
my $operateContent = "服务数据合规检查处理结束";
$operateContent = encode('gbk',decode('utf-8',$operateContent));
my $processOper=$dbh->prepare("insert into gw_process_operation (OPERATE_ID,user_Id,operate_content,operate_time,plan_id,progress_status,dealtype,step,process_id) values(seq_gw_process_operation.nextval,null,'$operateContent',sysdate,null,0,'system','34',$processId)");
$processOper->execute;
$processOper->finish;

if($errorTag != 1){
	
	my $dataNum = $totalRow;
	WriteInfo("=====out num===$dataNum=========. \n");
	if($rowIrregularCount > 0){
		#存在不合规的数据，将不合规输出文件压缩
		my $irregularGzFile = $irregularTxtFile.".gz";
		#压缩不合规的输出文件
		WriteInfo("gzip irregular txt file $filePath/$irregularGzFile start.\n");
		system("cd $filePath && gzip -cf $irregularTxtFile > $irregularGzFile");
		WriteInfo("gzip irregular txt file $filePath/$irregularGzFile end.\n");
		
		if (-f $irregularTxtFile_Path) {
			WriteInfo("delete irregular txt file $irregularTxtFile_Path start!\n");
	 		unlink $irregularTxtFile_Path;
	 		WriteInfo("delete irregular txt file $irregularTxtFile_Path Success!\n");		
	 	}
	 	#2016-9-17 上传不合规文件
	 	my $ftpInfo=$dbh->prepare("select ftp_ip from GW_SYS_FTP t where ftp_type='1' and rownum=1");
		$ftpInfo->execute;
		my $gwftpIp=$ftpInfo->fetchrow_array();
		$ftpInfo->finish;
	 	my $ftp=Net::FTP->new($gwftpIp,Debug => 0) or die "Cannot connect to some host name: $@";
    my $success=$ftp->login($gwFtpUser,$gwFtpPasswd) or die "Cannot login  ",$ftp->message;
    
    #将文件上传到远程ftp结果目录
    $success=$ftp->put("$filePath/$irregularGzFile","$remotePath/$irregularGzFile")  or die "put file failed  ",$ftp->message;
    $ftp->quit;
    #2016-9-17 上传不合规文件
    
    #删除脱敏文件记录
		my $sth_d=$dbh->prepare("delete GW_MODEL_DATA_FILE where TASK_ID = $taskId and FILE_TYPE=3 ");
		$sth_d->execute;
		$sth_d->finish;
	 	#插入任务文件入库
	 	my $irregularDataFile=$dbh->prepare("insert into GW_MODEL_DATA_FILE (FILE_ID,TASK_ID,MODEL_ID,USER_ID,FILE_TYPE,FILE_STATUS,FILE_PATH,FILE_NAME,CREATE_TIME,FTP_IP,FTP_PORT)".
      														 "values (SEQ_MODEL_DATA_FILE.NEXTVAL,$taskId,null,$userId,3,1,'$uFpath','$irregularGzFile',sysdate,null,null)");
	 	$irregularDataFile->execute;
	 	$irregularDataFile->finish;
	 	
	}
	
	#存在不合规的数据，或者合规的数据也必须审核，生成审核待办任务
	if($rowIrregularCount > 0 || $checkAudit == 1){
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
		$serviceName = encode('utf-8',decode('gbk',$serviceName));
		$fieldValue = encode('utf-8',decode('gbk',$fieldValue));
		my $planContent="数据用户：$loginName请求的服务(服务编码=$serviceCode,服务名称=$serviceName,服务周期=$fieldValue)输出文件中存在不合规数据，请审核。";
		if($rowIrregularCount==0){
			$planContent="数据用户：$loginName请求的服务(服务编码=$serviceCode,服务名称=$serviceName,服务周期=$fieldValue)输出文件合规检查通过，请审核。";
		}
		$planContent = encode('gbk',decode('utf-8',$planContent));
		my $plantitle = "合规检查审核";
			$plantitle = encode('gbk',decode('utf-8',$plantitle));
		#增加代办任务
		my $workPlan=$dbh->prepare("insert into gw_work_plan(plan_id,plan_title,plan_type,plan_content,plan_state,create_time,create_user_id) ".
											"values($planId,'$plantitle',19,'$planContent',1,sysdate,1)");
		$workPlan->execute;
		$workPlan->finish;
		if($rowIrregularCount>0){
			WriteInfo("存在不合规数据，生成审核代办。planId=$planId.\n");
		}else{
			WriteInfo("合规检查通过，根据配置生成审核代办。planId=$planId.\n");
		}
		
		#2016-8-19 添加流程跟踪登记
		
		#生成审批待办
		my $process=$dbh->prepare("update gw_process set progress_status='4',STEP_STATUS='1' where process_id=$processId");
		$process->execute;
		$process->finish;
		$operateContent = "生成一次审批待办";
		$operateContent = encode('gbk',decode('utf-8',$operateContent));
		$processOper=$dbh->prepare("insert into gw_process_operation (OPERATE_ID,user_Id,operate_content,operate_time,plan_id,progress_status,dealtype,step,process_id) values(seq_gw_process_operation.nextval,null,'$operateContent',sysdate,$planId,1,'safeUser','35',$processId)");
		$processOper->execute;
		$processOper->finish;
		WriteInfo("insert into gw_process_operation (user_Id,operate_content,operate_time,plan_id,progress_status,dealtype,step,process_id) values(null,'$operateContent',sysdate,$planId,1,'safeUser','35',$processId).\n");
		#2016-8-19 添加流程跟踪登记
		
		#发送短信
		my $mobileStr="";
		my $mobileList=$dbh->prepare("select u.moblie from gw_user u,gw_user_button a,gw_task_type_info b where a.button_code=b.task_type_name and a.user_type=u.user_type and u.online_status=1 and b.task_type_no='19'");
		$mobileList->execute;
		while(my ($moblie) = $mobileList->fetchrow_array()){	
				$mobileStr = "$mobileStr$moblie\|";
		}	
		$mobileList->finish;
		my $sigin = encode('gbk',decode('utf-8',"【数据网关平台】"));
		my $smsContent="$planContent$sigin";
		my $sms=$dbh->prepare("insert into gw_sms(sms_id,sms_mobile,sms_content,send_count,create_time) ".
														"values(seq_gw_sms.nextval,'$mobileStr','$smsContent','0',sysdate)");
		$sms->execute;
		$sms->finish;
	}
	my $dataProgressStatus=($rowIrregularCount==0 && $checkAudit != 1)?10:8;;
	my $downloadTime="";
	#全部合规的文件，需要根据输出行数的配置另外生成一个输出文件
	if($rowIrregularCount == 0){
		my $timeStr =strftime("%Y%m%d%H%M%S", localtime(time));
		#管理员下载的压缩文件名
		my $downGzFile = substr($fileName,0,index($fileName,".txt.gz"))."_$timeStr.txt.gz";
		#数据用户加密后压缩文件名
		my $outputGzFile = substr($fileName,0,index($fileName,".txt.gz"))."_".$loginName."_$timeStr.txt.gz";
						
		#获取加密密码
		my $findPwd=$dbh->prepare("select file_encry_pwd from gw_user t where user_id=$userId");
		$findPwd->execute;
		my $pwd=$findPwd->fetchrow_array();
		$findPwd->finish;
		
		my $txtFile_path=$filePath."/".$unzipName;
		my $gzFile_path="$filePath/$fileName";
		my $result_path=$filePath."/result";
		#全部输出，直接复制原文件
		if($outputNum==0){
			my $cp = 'cp -f '.$txtFile_path." $result_path/".$unzipName;
	    WriteInfo("cp $txtFile_path to $result_path/$unzipName start.\n");
	  	system($cp);
	  	WriteInfo("cp $txtFile_path to $result_path/$unzipName finish.\n");
	  	
	  	#生成管理员下载压缩文件到本地目录
			my $cpZip = 'cp -f '.$gzFile_path." $result_path/".$downGzFile;
	    WriteInfo("cp $gzFile_path to $result_path/$downGzFile start.\n");
	  	system($cpZip);
	  	WriteInfo("cp $gzFile_path to $result_path/$downGzFile finish.\n");
	  	
		}else{#输出部分行
			
			#输出文件的全路径
			my $outputTxtFile_path = $result_path.'/'.$unzipName;
		 	if (-f $outputTxtFile_path) {
		 		unlink $outputTxtFile_path;
		 		WriteInfo("delete temporary file $outputTxtFile_path Success!\n");		
		 	}
		 	WriteInfo("create output file $outputTxtFile_path start.\n");
		 	my $head = 'head -n '.$outputNum." $txtFile_path > $outputTxtFile_path";
		 	system($head);
		 	
			$dataNum=$outputNum;	#设置任务输出文件的数据量
			WriteInfo("create output file $outputTxtFile_path end. outputNum=$outputNum.\n");
			
			#生成管理员下载压缩文件
			WriteInfo("gzip file $result_path/$downGzFile start.\n");
			system("cd $result_path && gzip -cf $unzipName > $downGzFile");
			WriteInfo("gzip file $result_path/$downGzFile end.\n");
		}
		#生成数据用户加密压缩文件
		my $zipPwd;
		if(defined $pwd){
			$zipPwd = 'zip -rP '.$pwd." ".$outputGzFile." ".$downGzFile; 
  	}else{
  		$zipPwd = 'cp -f '.$downGzFile."  ".$outputGzFile;
  	}
  	WriteInfo("execute $zipPwd start.\n");
  	system("cd $result_path && ".$zipPwd);
  	WriteInfo("execute $zipPwd finish.\n");
  	
  	#2016-9-17 上传结果文件
	 	my $ftpInfo=$dbh->prepare("select ftp_ip from GW_SYS_FTP t where ftp_type='1' and rownum=1");
		$ftpInfo->execute;
		my $gwftpIp=$ftpInfo->fetchrow_array();
		$ftpInfo->finish;
	 	my $ftp=Net::FTP->new($gwftpIp,Debug => 0) or die "Cannot connect to some host name: $@";
    my $success=$ftp->login($gwFtpUser,$gwFtpPasswd) or die "Cannot login  ",$ftp->message;
    
    #将用户输出文件上传到远程ftp结果目录
    $success=$ftp->put("$result_path/$outputGzFile","$remotePath/$outputGzFile")  or die "put file failed  ",$ftp->message;
    #将管理员输出文件上传到远程ftp结果目录
    $success=$ftp->put("$result_path/$downGzFile","$remotePath/$downGzFile")  or die "put file failed  ",$ftp->message;
    $ftp->quit;
    #2016-9-17 上传结果文件
    
    #删除脱敏文件记录
		my $sth_d=$dbh->prepare("delete GW_MODEL_DATA_FILE where TASK_ID = $taskId and (FILE_TYPE=2 or FILE_TYPE=5)");
		$sth_d->execute;
		$sth_d->finish;
  	
  	#插入推送文件
		my $dataFile=$dbh->prepare("insert into GW_MODEL_DATA_FILE (FILE_ID,TASK_ID,MODEL_ID,USER_ID,FILE_TYPE,FILE_STATUS,FILE_PATH,FILE_NAME,CREATE_TIME,FTP_IP,FTP_PORT,UNZIP_NAME)".
    														 "values (SEQ_MODEL_DATA_FILE.NEXTVAL,$taskId,null,$userId,2,1,'$uFpath','$outputGzFile',sysdate,null,null,'$unzipName')");
    $dataFile->execute;
    #插入管理员下载文件
		$dataFile=$dbh->prepare("insert into GW_MODEL_DATA_FILE (FILE_ID,TASK_ID,MODEL_ID,USER_ID,FILE_TYPE,FILE_STATUS,FILE_PATH,FILE_NAME,CREATE_TIME,FTP_IP,FTP_PORT,UNZIP_NAME)".
    														 "values (SEQ_MODEL_DATA_FILE.NEXTVAL,$taskId,null,$userId,5,1,'$uFpath','$downGzFile',sysdate,null,null,'$unzipName')");
  	$dataFile->execute;
  	$dataFile->finish;
			
		my $searchTaskStatus=$dbh->prepare("select task_status from gw_model_data_fetch_task where task_id=$taskId");
		$searchTaskStatus->execute;
		my $taskStatus=$searchTaskStatus->fetchrow_array();
		$searchTaskStatus->finish;
	
	 
		#任务有可能已被修改规则后置为失效,push前检查一次
		if($taskStatus==1){
			#检查通过的，不需要审核将文件push到指定ftp，且记录免责日志
			if($checkAudit != 1){
				my $userFtpInfo=$dbh->prepare("select push_ftp,ftp_ip,ftp_port,ftp_username,ftp_password,ftp_path from gw_user where user_id=$userId");
      	$userFtpInfo->execute;
      	my ($pushFtp,$ftpIp,$ftpPort,$ftpUsername,$ftpPassword,$ftpPath)=$userFtpInfo->fetchrow_array();
      	$userFtpInfo->finish;
				
				if(defined $pushFtp && $pushFtp==1){
				 	#上传文件
					my $ftp_flag=1; #0:down  1:up
					my $rst = FTPFile($ftpIp,$ftpUsername,$ftpPassword,$result_path,$ftpPath,$outputGzFile,$ftp_flag);
					if($rst != 0 ){
						$dataProgressStatus=13;	#服务发送失败，会在quartz中自动重发
						#2016-8-19 添加流程跟踪登记
						#数据推送失败
						my $operateContent = "数据推送失败";
						$operateContent = encode('gbk',decode('utf-8',$operateContent));
						my $processOper=$dbh->prepare("insert into gw_process_operation (OPERATE_ID,user_Id,operate_content,operate_time,plan_id,progress_status,dealtype,step,process_id) values(seq_gw_process_operation.nextval,null,'$operateContent',sysdate,null,1,'system','37',$processId)");
						$processOper->execute;
						$processOper->finish;
						my $process=$dbh->prepare("update gw_process set progress_status='6',STEP_STATUS='0' where process_id=$processId");
						$process->execute;
						$process->finish;
						WriteInfo("update gw_process set progress_status='6',STEP_STATUS='0' where process_id=$processId\n");						
						#2016-8-19 添加流程跟踪登记
					}else{
						$dataProgressStatus=12;	#服务已送达
						
						#2016-8-19 添加流程跟踪登记
						#数据推送成功
						my $operateContent = "数据推送成功";
						$operateContent = encode('gbk',decode('utf-8',$operateContent));
						my $processOper=$dbh->prepare("insert into gw_process_operation (OPERATE_ID,user_Id,operate_content,operate_time,plan_id,progress_status,dealtype,step,process_id) values(seq_gw_process_operation.nextval,null,'$operateContent',sysdate,null,0,'system','37',$processId)");
						$processOper->execute;
						$processOper->finish;
						my $process=$dbh->prepare("update gw_process set progress_status='7',STEP_STATUS='0',STATUS='0',END_TIME=sysdate where process_id=$processId");
						$process->execute;
						$process->finish;
						WriteInfo("update gw_process set progress_status='7',STEP_STATUS='0' where process_id=$processId\n");									
						#2016-8-19 添加流程跟踪登记
						
					 	WriteInfo("Upload file to $ftpIp succ!\n");
					 	
					 	#用于更新用户下载时间
					 	$downloadTime=",download_time=sysdate";
					 	#获取检查规则
					 	my @fieldCodes;			#服务输出字段，用于保存免责日志
					 	my $checkRuleStr="";	#检查规则字符串，用于保存免责日志
						my $checkRule=$dbh->prepare(
							"select t.reorder,t.check_type,d.dict_value,t.check_rule,t.field_code,t.field_name ".
							"from gw_service_check_rule t ".
							"left join gw_sys_dict d on d.dict_code='DICT_CHECK_RULE_TYPE' and d.dict_key=t.check_type ".
							"where t.user_id=$userId and t.service_id=$serviceId and t.check_batch=$maxCheckBatch ".
							"order by t.reorder");
						$checkRule->execute;
						while(my ($reorder,$checkType,$checkTypeName,$checkRule,$fieldCode,$fieldName) = $checkRule->fetchrow_array()){
							push(@fieldCodes,"$fieldCode");
							$checkRuleStr = $checkRuleStr."$fieldCode($fieldName)=检查类型：$checkTypeName,检查规则：$checkRule;\n";
						}
						$checkRule->finish();
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
	my $checkResult = 0;
	if($overTag == 0 && $rowIrregularCount > 0){
		$checkResult = 1;
	}
	if($loseTag == 1){
		$checkResult = 4;
	}
	if($moreTag == 1){
		$checkResult = 3;
	}
	if($overTag == 1){
		$checkResult = 2;
	}
	WriteInfo("execute rm -rf ".$filePath."/* clean start.\n");
	system("rm -rf ".$filePath."/*");
	system("mkdir ".$filePath."/result");
	WriteInfo("execute mkdir ".$filePath."/result finish.\n");
	
	my $downloadEndTime=($rowIrregularCount==0 && $checkAudit != 1)?",DOWNLOAD_END_TIME=sysdate":"";
	#修改任务数据就绪状态
	my $taskUp=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=$dataProgressStatus,data_num=$dataNum,check_result=$checkResult,max_check_num=$maxCheckNum,CHECK_IRREGULAR_NUM=$rowIrregularCount,check_num=$rowNum,NODE_STATUS='2' $downloadEndTime $downloadTime where task_id=$taskId");
	$taskUp->execute;
	$taskUp->finish;
	WriteInfo("update gw_model_data_fetch_task set data_progress_status=$dataProgressStatus,data_num=$dataNum,check_result=$checkResult,max_check_num=$maxCheckNum,CHECK_IRREGULAR_NUM=$rowIrregularCount,check_num=$rowNum,NODE_STATUS='2' $downloadEndTime $downloadTime where task_id=$taskId.\n");
				
}
};#eval end	
if($@){
	#合规检查过程中出现问题，修改任务数据就绪状态为处理失败
	my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId");
	$task->execute;
	$task->finish;
	my $operateContent = "合规检查处理异常";
	$operateContent = encode('gbk',decode('utf-8',$operateContent));
	my $processOper=$dbh->prepare("insert into gw_process_operation (OPERATE_ID,user_Id,operate_content,operate_time,plan_id,progress_status,dealtype,step,process_id) values(seq_gw_process_operation.nextval,null,'$operateContent',sysdate,null,0,'system','34',$processId)");
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
}

sub FTPFile
{  
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
	
	
	
	
	
print "the result Child $pid end.\n"
