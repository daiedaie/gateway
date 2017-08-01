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
my $userId = $ARGV[1]; #用户ID
my $serviceId = $ARGV[2]; #服务ID
my $serviceCode = $ARGV[3]; #服务编码
my $serviceName = $ARGV[4];#服务名称
my $fieldValue = $ARGV[5];#服务周期
my $processNum = $ARGV[6]; #处理子进程数
my $filePath = $ARGV[7]; #用户文件目录\不合规文件目录
my $fileName = $ARGV[8]; #脱敏结果文件=输出文件
my $irregularCsvFile = $ARGV[9]; #不合规文件名称
my $checkAudit = $ARGV[10]; #是否需要审核
my $loginName = $ARGV[11]; #数据用户登录名
my $maxCheckNum = $ARGV[12]; #最大检查行数
my $outputNum = $ARGV[13]; #输出行数，0表示全部输出
my $fileId = $ARGV[14];#数据文件ID
my $maxCheckBatch = $ARGV[15];

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

my $gatewayRowId = "gateway_row_id";
my $gatewayTaskId = "gateway_task_id";
my $gatewayRowData = "row_data";

#设置日志 
SetLogPath($LOG_PATH);
SetLogLevel('D','D');  #文件和终端日志级别：'D', 'I', 'W', 'E', 'F', 'C'
SetLogHead("GW_DESEN","GW_DESEN");#文件和日志 前缀
OpenLog;
WriteInfo("===================开始运行========================\n");
WriteInfo("LOG_PATH:$LOG_PATH\n");	
WriteInfo("Oracle:$user/$dbname\n");

#创建数据库连接
my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";

my ($reskey,$resmsg,$resmsgid,$resmsgtype,$resbuf);
$reskey = IPC::SysV::ftok("/home/gateway/perl/desen_check.txt");
#打开结果消息队列
$resmsg = new IPC::Msg( $reskey, 0) or die "Open MQ faile!";


my $loop = 1;
my $rowIrregularCount = 0; #不合规累计行数
my $dbLimit = 1000;	#不合规行数据插入到数据库的限制数量，超过不再插入到数据
#my $moreTag = 0; #是否多列标记，0表示不存在，1表示存在
#my $loseTag = 0; #是否少列标记，0表示不存在，1表示存在
my $overTag = 0; #是否超出最大不合规标记，0表示没有超出，1表示已超出
my $endCount = 0; #处理子进程正常结束个数
my $errorTag = 0; #是否执行异常标记，0表示不存在，1表示存在
my $totalRow = 0; #文件总行数
my $rowNum = 0;
my @contents;
my $irregularCsvFilePath = "$filePath/$irregularCsvFile"; #不合规文件路径
my $str;
my $irregularField;
my $serviceFields;

open(IRREGULAR_FILE,"> $irregularCsvFilePath") or die "Open file $irregularCsvFilePath error.\n";
while( $loop == 1 ) {
	#读取消息队列的行数据
	$resmsgtype = $resmsg->rcv($resbuf, 1024);
	if(defined $resmsgtype){
		if ( $resmsgtype == 1 ) {#检查不合规的行数据
			@contents = split(::,$resbuf);
			$rowNum = $contents[0];
			$serviceFields = $contents[1];
			$irregularField = $contents[2];
			$str = $contents[3];
		
			#$errorTag = 1;
			$rowIrregularCount++;
			#第一条数据存放表头
			if($rowIrregularCount == 1){
				my $insertSql=$dbh->prepare("insert into gw_service_check_$serviceId($gatewayRowId,$gatewayTaskId,$gatewayRowData) values(0,$taskId,'$serviceFields')");
				$insertSql->execute;
				$insertSql->finish();
				
				#将表头写入不合规文件
				printf IRREGULAR_FILE "序号,行号,".$serviceFields.",不合规字段";
				printf IRREGULAR_FILE "\r\n";
			}
			#写入不合规文件
			printf IRREGULAR_FILE $rowIrregularCount.",".$rowNum.",".$str.",".$irregularField;
			printf IRREGULAR_FILE "\r\n";
			
			if($rowIrregularCount <= $dbLimit){
				
				#$str =~ s/\",\"/\',\'/g; #替换","为',' ---csv换成txt后不需要去除引号
				
				#将分隔符|转成逗号，并加引号，所有字段都加上单引号，转成字符串插入缓存表
				#$str =~ s/,/\',\'/g; 
				#$str ="\'$str\'";
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
			
		}elsif ( $resmsgtype == -1 ) {#异常
			#处理子进程执行异常消息
			$errorTag = 1;
			$rowNum = $resbuf;
			#合规检查过程中出现问题，修改任务数据就绪状态为处理失败
			my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId");
			$task->execute;
			$task->finish;
			
		  #写log
			WriteInfo("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId.\n");
			WriteError("taskId=$taskId task rule check error!.\n");
			WriteError("$@.\n");
			$loop = 0;
		}else {
			$endCount ++;
			#正常结束个数
			if($endCount == $processNum){
				@contents = split(::,$resbuf);
				$serviceFields = $contents[0];
				$rowNum = $contents[1];
				$totalRow = $rowNum; #获取总行数
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
	#发送异常信号给主进程，结束处理子进程
	WriteInfo("===================================  send the ABRT signal to parent =======\n");
	kill 'ABRT', $ppid;
}
if($errorTag != 1){
	
	my $dataNum = $totalRow;
	
	#存在不合规的数据，将不合规输出文件压缩
	if($rowIrregularCount > 0){
		my $irregularGzFile = $irregularCsvFile.".gz";
		#压缩不合规的输出文件
		WriteInfo("gzip irregular txt file $filePath/$irregularGzFile start.\n");
		system("cd $filePath && gzip -cf $irregularCsvFile > $irregularGzFile");
		WriteInfo("gzip irregular txt file $filePath/$irregularGzFile finish.\n");
		
		#将不合规临时输出文件删除
		if (-f $irregularCsvFilePath) {
			WriteInfo("delete irregular txt file $irregularCsvFilePath start!\n");
			unlink $irregularCsvFilePath;
			WriteInfo("delete irregular txt file $irregularCsvFilePath finish!\n");		
		}
		#插入任务文件入库
	 	my $irregularDataFile=$dbh->prepare("insert into GW_MODEL_DATA_FILE (FILE_ID,TASK_ID,MODEL_ID,USER_ID,FILE_TYPE,FILE_STATUS,FILE_PATH,FILE_NAME,CREATE_TIME,FTP_IP,FTP_PORT)".
	    														 "values (SEQ_MODEL_DATA_FILE.NEXTVAL,$taskId,null,$userId,3,1,'$filePath','$irregularGzFile',sysdate,null,null)");
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
		
		my $planContent="数据用户：$loginName请求的服务(服务编码=$serviceCode,服务名称=$serviceName,服务周期=$fieldValue)输出文件中存在不合规数据，请审核。";
		if($rowIrregularCount==0){
			$planContent="数据用户：$loginName请求的服务(服务编码=$serviceCode,服务名称=$serviceName,服务周期=$fieldValue)输出文件合规检查通过，请审核。";
		}
	
		#增加代办任务
		my $workPlan=$dbh->prepare("insert into gw_work_plan(plan_id,plan_title,plan_type,plan_content,plan_state,create_time,create_user_id) ".
											"values($planId,'合规检查审核',19,'$planContent',1,sysdate,1)");
		$workPlan->execute;
		$workPlan->finish;
		if($rowIrregularCount>0){
			WriteInfo("存在不合规数据，生成审核代办。planId=$planId.\n");
		}else{
			WriteInfo("合规检查通过，根据配置生成审核代办。planId=$planId.\n");
		}
	}
	
	my $dataProgressStatus=($rowIrregularCount==0 && $checkAudit != 1)?10:8;;
	my $downloadTime="";
	#全部合规的文件，需要根据输出行数的配置另外生成一个输出文件
	if($rowIrregularCount == 0){
		my $timeStr =strftime("%Y%m%d%H%M%S", localtime(time));
		#管理员下载的压缩文件名
		my $downGzFile = substr($fileName,0,index($fileName,".csv"))."_$timeStr.csv.gz";
		#数据用户加密后压缩文件名
		my $outputGzFile = substr($fileName,0,index($fileName,".csv"))."_".$loginName."_$timeStr.csv.gz";
				
		#获取加密密码
		my $findPwd=$dbh->prepare("select file_encry_pwd from gw_user t where user_id=$userId");
		$findPwd->execute;
		my $pwd=$findPwd->fetchrow_array();
		
		
		my $txtFile_path=$filePath."/".$fileName;
		my $outUnzipName = $fileName;
		#my $gzFile_path="$filePath/$fileName";
		#outputNum==0全部输出，不需要改变。outputNum!=0需要另外生成一个输出文件
		#全部输出，直接复制原文件
		if($outputNum!=0){#输出部分行
			
			#输出文件的全路径
			$outUnzipName = "part_".$fileName;
			my $outputTxtFile_path = $filePath.'/'.$outUnzipName;
		 	if (-f $outputTxtFile_path) {
		 		unlink $outputTxtFile_path;
		 		WriteInfo("delete temporary file $outputTxtFile_path Success!\n");		
		 	}
		 	$dataNum=$outputNum;	#设置任务输出文件的数据量
		 	#添加表头一行
		 	$outputNum ++;
		 	WriteInfo("create output file $outputTxtFile_path start.\n");
		 	my $head = 'head -n '.$outputNum." $txtFile_path > $outputTxtFile_path";
		 	system($head);
			WriteInfo("create output file $outputTxtFile_path end. outputNum=$outputNum.\n");
	
		}
		#生成管理员下载压缩文件
		WriteInfo("gzip file $filePath/$downGzFile start.\n");
		system("cd $filePath && gzip -cf $outUnzipName > $downGzFile");
		WriteInfo("gzip file $filePath/$downGzFile end.\n");	
		#生成数据用户加密压缩文件
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
  	
  	
		
  	#更新推送文件
  	my $updateFile=$dbh->prepare("update gw_model_data_file set file_name='$outputGzFile',UNZIP_NAME='$outUnzipName' where file_id=$fileId");
		$updateFile->execute;
		$updateFile->finish;
		
    #插入管理员下载文件
		my $dataFile=$dbh->prepare("insert into GW_MODEL_DATA_FILE (FILE_ID,TASK_ID,MODEL_ID,USER_ID,FILE_TYPE,FILE_STATUS,FILE_PATH,FILE_NAME,CREATE_TIME,FTP_IP,FTP_PORT,UNZIP_NAME)".
    														 "values (SEQ_MODEL_DATA_FILE.NEXTVAL,$taskId,null,$userId,5,1,'$filePath','$downGzFile',sysdate,null,null,'$outUnzipName')");
  	$dataFile->execute;
  	$dataFile->finish;
		
		#任务有可能已被修改规则后置为失效,push前检查一次	
		my $searchTaskStatus=$dbh->prepare("select task_status from gw_model_data_fetch_task where task_id=$taskId");
		$searchTaskStatus->execute;
		my $taskStatus=$searchTaskStatus->fetchrow_array();
		$searchTaskStatus->finish;
		
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
					my $rst = FTPFile($ftpIp,$ftpUsername,$ftpPassword,$filePath,$ftpPath,$outputGzFile,$ftp_flag);
					if($rst != 0 ){
						$dataProgressStatus=13;	#服务发送失败，会在quartz中自动重发
					}else{
						$dataProgressStatus=12;	#服务已送达
					 	WriteInfo("Upload file to $ftpIp succ!\n");
					 						 	
					 	#脱敏并合规检查,直接用脱敏的规则做合规检查
						my $checkRule=$dbh->prepare(
							"select f.field_id,f.field_code,f.field_name,t.RULE_TYPE,d.dict_value,t.RULE_CONTENT,t.REPLACE_CONTENT,t.CONDITION_TYPE,t.CONDITION_CONTENT from gw_desen_service_field t ".
							"inner join gw_service_field f on f.field_id=t.field_id ".
							"left join gw_sys_dict d on d.dict_code='DICT_DESEN_RULE_TYPE' and d.dict_key=t.rule_type ".
							"where t.user_id=$userId and t.service_id=$serviceId and batch=(select max(batch) from gw_desen_service_field where user_Id=$userId and service_Id=$serviceId) ".
							"order by t.field_id");
						$checkRule->execute;
						
						my $ruleTypeCN = decode("gbk","脱敏类型");
						my $ruleContentCN = decode("gbk","处理定位");
						my $replaceContentCN = decode("gbk","替换字符");
						my $conditionTypeCN = decode("gbk","条件类型");
						my $conditionContentCN = decode("gbk","条件内容");
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
				 	
				 		#用于更新用户下载时间
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
