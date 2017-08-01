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
my $resultKeyFile = "/home/gateway/perl/desen.txt";
#处理子进程数
my $processNum = 5;
if($ARGV[0]){
	$processNum = $ARGV[0];
}
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
my $DESEN_NODE ="desen001";
my $localPath = "/home/gateway/perl/tempData/desen";

$ENV{'LANG'}='en_US.UTF-8';
$ENV{'NLS_LANG'}='SIMPLIFIED CHINESE_CHINA.ZHS16GBK';


#本地文件存放和csv文件生成路径
my $Lpath="/home/gateway/data/platform";

my $running_child;

#是否继续处理标记，1标记继续，0标记不继续
my $running;
#是否存在不合规数据，1标记存在，0标记不存在
my $irregular;

my @task_pids;

my $dbh;

#捕抓子进程结束消息信号
$SIG{CHLD} = sub {
	while( ( my $child = waitpid( -1, POSIX::WNOHANG ) ) > 0 ) {
		print "Get childe $child end signal.\n";
		$running_child --;
		#print "Get running_child $running_child .\n";
	}
};
#捕抓子进程异常消息信号，如果捕抓到则kill所有子进程
$SIG{ABRT} = sub {
	foreach my $child(@task_pids) {
		kill "ABRT", $child;
	}
	$running = 0;
};


main:{
	
	#设置日志 
	SetLogPath($LOG_PATH);
	SetLogLevel('D','D');  #文件和终端日志级别：'D', 'I', 'W', 'E', 'F', 'C'
	SetLogSize(50000);#文件切换行数
	SetLogHead("GW_DESEN","GW_DESEN");#文件和日志 前缀
	OpenLog;
  if(!(-d $Lpath && -w $Lpath)){
       WriteError("Lpath is not find. \n");
       CloseLog;
       exit;  
  }
	WriteInfo("===================开始运行========================\n");
  WriteInfo("LOG_PATH:$LOG_PATH\n");	 
  WriteInfo("Lpath:$Lpath\n");	
  WriteInfo("CHECK_CYCLE:$CHECK_CYCLE\n");	
  WriteInfo("Oracle:$user/$dbname\n");
	while ( 1 > 0 ) {
		$dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";
		#锁定该节点需要处理的任务
		my $LOCK_TASK=$dbh->prepare("update gw_model_data_fetch_task b set b.NODE_ID='$DESEN_NODE',b.NODE_STATUS='0',b.LOCK_TIME=sysdate  ". 
			" where task_id in(select task_id from ".
			" (select b.task_id,b.fetch_id from GW_MODEL_DATA_FILE a, GW_MODEL_DATA_FETCH_TASK b  where b.DATA_PROGRESS_STATUS = '2' and b.Task_status = '1'  and a.task_id = b.task_id   and a.FILE_TYPE = '1' ".
			" union ".
			" select b.task_id,b.fetch_id from GW_MODEL_DATA_FETCH_TASK b  where b.DATA_PROGRESS_STATUS = '4'  and b.Task_status = '1') t ".
			" inner join gw_model_data_fetch f on f.fetch_id=t.fetch_id and f.audit_status=2 and f.desen_type='1' ) and NODE_STATUS is null and rownum=1 ");
			$LOCK_TASK->execute;
			$LOCK_TASK->finish;
	 	#获取状态为2和4的任务,按用户优先级处理
	 	my $sth=$dbh->prepare(	 
	 		"select * from (select t2.*,rownum rnum from (									".
			"select t.task_id,                                              ".
			"       t.service_id,                                           ".
			"       t.user_id,                                              ".
			"       u.login_name,                                  	        ".
			"       t.data_progress_status,                                 ".
			"       t.ftp_ip,                                               ".
			"       t.ftp_user,                                             ".
			"       t.FTP_PASSWORD,                                         ".
			"       t.FILE_PATH,                                            ".
			"       t.FILE_NAME,                                            ".
			"       decode(t.MODEL_ID,null,'null',t.MODEL_ID)as MODEL_ID,   ".
			"       t.FIELD_CODE,                                           ".
			"       t.FIELD_VALUE,                                          ".
			"       t.PROCESS_ID,                                           ".
			"       s.service_source                                        ".
			"  from (select b.task_id,                                      ".
			"               b.fetch_id,                                     ".
			"               b.service_id,                                   ".
			"               b.user_id,                                      ".
			"               b.data_progress_status,                         ".
			"               a.ftp_ip,                                       ".
			"               a.ftp_user,                                     ".
			"               a.FTP_PASSWORD,                                 ".
			"               a.FILE_PATH,                                    ".
			"               a.FILE_NAME,                                    ".
			"               a.MODEL_ID,                                     ".
			"               decode(b.FIELD_CODE,null,'0',b.FIELD_CODE)as FIELD_CODE,                                   ".
			"               b.FIELD_VALUE,                                  ".
			"               b.download_start_time,                           ".
			"               b.PROCESS_ID                            ".
			"          from GW_MODEL_DATA_FILE a, GW_MODEL_DATA_FETCH_TASK b".
			"         where (b.DATA_PROGRESS_STATUS = '2' or b.DATA_PROGRESS_STATUS = '4')                   ".
			"           and b.Task_status = '1'                             ".
			"           and a.task_id = b.task_id                           ".
			"           and a.FILE_TYPE = '1'                               ".
			"           and b.node_id ='$DESEN_NODE' and b.node_status='0' ) t   ".
			"				inner join gw_model_data_fetch f on f.fetch_id=t.fetch_id and f.audit_status=2 and f.desen_type='1' ".
			"       inner join gw_user u on t.user_id = u.user_id and u.status=1 ".
			" 			inner join gw_user org on u.org_id = org.org_id and org.user_type='orgUser'  ".
			" 			inner join gw_service s on t.service_id = s.service_id   ".
			" order by t.data_progress_status,org.run_level,u.run_level     ".
			") t2) tmp where tmp.rnum=1"										
	 	);                                                       
	 	$sth->execute;
		
		while(my ($taskId,$serviceId,$userId,$loginName,$dataProgressStatus,$FTP_ADDR,$Fuser,$Fpassword,$Fpath,$Fname,$modelId,$fieldCode,$fieldValue,$processId,$serviceSource) = $sth->fetchrow_array()){
			eval{
				WriteInfo("Info:$taskId|$serviceId|$userId|$dataProgressStatus|$FTP_ADDR|$Fuser|$Fpassword|$Fpath|$Fname|$modelId|$fieldCode|$fieldValue\n");
				#数据用户文件存放目录
		 		my $dataUserPath="/home/gateway/users/$loginName/result";
		 		#自定义服务周期字段
				my $gatewayCycleField = "gateway_$fieldCode";
				#自定义主键ID，用于抽样、合规检查
				my $gatewayRowId = "GATEWAY_ROW_ID";
				my $gatewayTaskId = "GATEWAY_TASK_ID";
				#获取gateway ftp 服务器ip
				my $ftpInfo=$dbh->prepare("select ftp_ip from GW_SYS_FTP t where ftp_type='1' and rownum=1");
				$ftpInfo->execute;
				my $ftpIp=$ftpInfo->fetchrow_array();
				$ftpInfo->finish;
				
				my $remotePath = $Lpath;
		 		$remotePath =~ s/\/home\/gateway//; #获取ftp登录后的相对路径
		 		
		 		#如果状态为2时需要到挖掘平台ftp下载文件并插入缓存表，状态为4直接进行脱敏
		 		if($dataProgressStatus == 2){
		 			WriteInfo("Update  GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=3 !\n"); 	
			 		#设置任务状态 3开始取数
			 		my $sth_u=$dbh->prepare("update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=3 where task_id=$taskId");
			 		$sth_u->execute;
			 		$sth_u->finish();
			 		 			
		 			#从挖掘平台服务器下载文件到本地发perl服务器
		 			my $ftp_flag=0; #0:down  1:up
		 			my $rst = FTPFile($FTP_ADDR,$Fuser,$Fpassword,$localPath,$Fpath,$Fname,$ftp_flag);
		 			if($rst != 0 ){	 				
		 				next;
		 			}
		 			#2016-9-17 添加ftp远程读取，为了提供元文件下载
					
					#取发ftp用户登录的相对路径
					#本地文件存放和csv文件生成路径
					my $allPath="/home/gateway/data/platform/$loginName";
					my $remotePath=$allPath;
		      $remotePath =~ s/\/home\/gateway//;
		      #上传元数据文件到gateway ftp服务器
					$ftp_flag=1; #0:down  1:up
					$rst = FTPFile($ftpIp,$gwFtpUser,$gwFtpPasswd,$localPath,$remotePath,$Fname,$ftp_flag);
					if($rst != 0 ){	 				
						next;
					}
		 			
		 			#2016-9-17 上传元数据文件到gateway ftp服务器
		 			
		 			#新增下载过来的本地原始文件记录
		 			$sth_u=$dbh->prepare(
						"insert into GW_MODEL_DATA_FILE (FILE_ID,TASK_ID,MODEL_ID,USER_ID,FILE_TYPE,FILE_STATUS,FILE_PATH,FILE_NAME,CREATE_TIME,FTP_IP,FTP_PORT,unzip_name)".
			      "values (SEQ_MODEL_DATA_FILE.NEXTVAL,$taskId,$modelId,$userId,4,1,'$allPath','$Fname',sysdate,'$ftpIp',22,'')"
			    );
			 		$sth_u->execute;
			 		$sth_u->finish();
		 			
			 		#检查缓存表是否存在
			 		my $sth_count=$dbh->prepare("select count(*) from all_tables where table_name = 'GW_SERVICE_$serviceId'");
			 		$sth_count->execute;
			 		my $count = $sth_count->fetchrow_array();
			 		$sth_count->finish();
			 		
			 		#如果缓存表不存在，则创建表
			 		if($count == 0){
		 				WriteInfo("The cache table GW_SERVICE_$serviceId not exist,must to create.\n"); 	
		 				my $tableStr = "create table GW_SERVICE_$serviceId  ( ";
			 			my @filedList;
			 			
			 			push(@filedList,"$gatewayRowId number");
						push(@filedList,"$gatewayCycleField varchar2(50)");	 			
				 		#获取表字段信息
				 		if($serviceSource == 1){
					 		my $sth_filed=$dbh->prepare("select FIELD_CODE,FIELD_TYPE from GW_SERVICE_FIELD where GATHER_TYPE='1' and SERVICE_ID='$serviceId' order by field_id");
					 		$sth_filed->execute;		 		
				 			
				 			while(my($fieldCode,$field_type)=$sth_filed->fetchrow_array){ 
				 				WriteDebug("Field_code:$fieldCode --> Field_type:$field_type \n");
				 				#对字段类型做特殊处理，非字符串类型的全部转换为VARCHAR2(200)类型
				 				$field_type = uc($field_type);
				 				if($field_type =~ /CHAR/ || $field_type =~ /LOB/){
				 					push(@filedList,"$fieldCode $field_type");
				 			  }else{
				 			  	WriteDebug("Field_type:$field_type change to VARCHAR2(200) \n");
				 			  	$field_type="VARCHAR2(200)";
				 			  	push(@filedList,"$fieldCode $field_type");
				 			  }
				 			}
				 			$sth_filed->finish();
			 			}else{
			 				for (my $i=1; $i < 50 ; $i++) {
				 				push(@filedList,"COLUMN_$i VARCHAR2(200)");
				 			}
			 			}
			 			
			 			#如果获取到字段信息不为空则建立缓存表
			 			if(@filedList){ 		
				 			$tableStr=$tableStr.join(",",@filedList).")";	 	
				 			WriteInfo("Cache table sql:$tableStr\n");	
							
							my $sth_createTab=$dbh->prepare($tableStr);   
							$sth_createTab->execute;	 
							$sth_createTab->finish(); 
							
							#建表后加索引
							my $sth_createIndex=$dbh->prepare("create unique index uq_gw_service_$serviceId on gw_service_$serviceId ($gatewayCycleField, $gatewayRowId)");
							$sth_createIndex->execute;
							$sth_createIndex->finish();
							
	          	WriteInfo("Cache table GW_SERVICE_$serviceId create succ.\n");
			 			}else{
			 				#报错
			 				WriteError("The field of cache table GW_SERVICE_$serviceId is err! \n");
			 				next;
			 			}	
			 	  }
			 	  
			 	  $sth_u=$dbh->prepare("select count(1) from GW_SERVICE_$serviceId where $gatewayCycleField='$fieldValue'");
			 	  $sth_u->execute;
			 	  my $cache_count=$sth_u->fetchrow_array;
			 	  $sth_u->finish;
			 	  #缓存表中不存在才导入到缓存表
			 	  if($cache_count == 0){
				 		#文件内容写入缓存表
						if(-e "$Lpath/$Fname"){
							#读取下载下来的ftp文件	
							open(FETCH_FILE,"$Lpath/$Fname") or print STDERR "Open Original file $Lpath/$Fname error.\n";	
							my $rownum=0;
							my $fail_count=0;
							my $filed;  #字段列表
							my $importMaxNum = 1000;	#最大导入行数
							while(<FETCH_FILE>){
				        #文件第一行为数据库表字段名,如:colum1,colum2,colum3
				  			if($rownum == 0 ){
					  			$filed = $_;	  			
					  			$filed =~ s/\s//g;#去除空格
					  			#$filed =~ s/\"//g;#去除"  ---csv换成txt后不需要去除引号	
					  			$rownum ++;		
					  			next;        		        
								}
			
								my $str = $_;
								#$str =~ s/\",\"/\',\'/g; #替换","为',' ---csv换成txt后不需要去除引号
								#$str =~ s/^\"/\'/g; #---csv换成txt后不需要去除引号
								#$str =~ s/\"$/\'/g; #---csv换成txt后不需要去除引号
								
								#所有字段都加上单引号，转成字符串插入缓存表
								$str =~ s/\|/\'\|\'/g; 
								#$str =~ s/^/\'/g;
								#$str =~ s/$/\'/g;
								$str ="\'$str\'";
								
								#逐行插入到数据库
								my $curSql="insert into GW_SERVICE_$serviceId($gatewayRowId,$gatewayCycleField,$filed) values($rownum,$fieldValue,$str)";
								my $sth_in=$dbh->prepare($curSql);   
								$sth_in->execute;	 
								$sth_in->finish();
								
								#只导入1000条数据，用于数据脱敏抽样展示
								if($rownum >= $importMaxNum){
									WriteInfo("import data $rownum row, break");
									last;
								}
	
								$rownum ++;
							}
							close FETCH_FILE;
							if($rownum != 10 || $rownum != $fail_count){
								my $data_row=$rownum-1;
								WriteInfo("File read to cache table finished,all $data_row rows,fail $fail_count rows.\n");					 			 			 	 		
			        }
						}else{
							WriteError("$Lpath/$Fname file not find.\n");
							next;
						}
					}else{
						WriteInfo("cache table GW_SERVICE_$serviceId cache data $cache_count rows.\n");					
					}
					
					#下载完成更新
					my $curTime = strftime("%Y-%m-%d %H:%M:%S",localtime(time));
		 			$sth_u=$dbh->prepare("update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=4 where task_id=$taskId");
			 		$sth_u->execute;	
			 		$sth_u->finish();	
			 		WriteInfo("Update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=4 !\n"); 
			 		WriteInfo("Download file succ!\n");
		 		}else{
		 			#服务任务元文件为本地上传的
		 			#2016-9-17 从gateway ftp服务器下载元数据文件到本地
		 			my $remotePath=$Fpath;
		      $remotePath =~ s/\/home\/gateway//;
		 			my $ftp_flag=0; #0:down  1:up
		 			WriteInfo("gateway ftp ip: $ftpIp.\n");
		 			my $rst = FTPFile($ftpIp,$gwFtpUser,$gwFtpPasswd,$localPath,$remotePath,$Fname,$ftp_flag);
		 			if($rst != 0 ){	 				
		 				next;
		 			}
		 			#2016-9-17 从gateway ftp服务器下载元数据文件到本地
		 		}
		 		
		 		#检查检查脱敏结果缓存表是否存在
				my $desenTable=$dbh->prepare("select count(*) from all_tables where table_name = 'GW_SERVICE_DESEN_$serviceId'");
				$desenTable->execute;
				my $desenCount=$desenTable->fetchrow_array();
				$desenTable->finish();
				
				if($desenCount==0){
			 		#创建脱敏后数据记录表
			 		my @filedList;
			 		my $tableStr = "create table GW_SERVICE_DESEN_$serviceId  ( ";
			 		push(@filedList,"gateway_task_id number");
			 		push(@filedList,"$gatewayRowId number");
					push(@filedList,"$gatewayCycleField varchar2(50)");
			 		if($serviceSource == 1){
			 		#获取表字段信息
				 		my $sth_filed=$dbh->prepare("select FIELD_CODE,FIELD_TYPE from GW_SERVICE_FIELD where GATHER_TYPE='1' and SERVICE_ID='$serviceId' order by field_id");
				 		$sth_filed->execute;
				 		while(my ($fieldCode,$field_type)=$sth_filed->fetchrow_array()){
							#对字段类型做特殊处理，非字符串类型的全部转换为VARCHAR2(200)类型
							$field_type = uc($field_type);
			 				if($field_type =~ /CHAR/ || $field_type =~ /LOB/){
			 					push(@filedList,"$fieldCode $field_type");
			 			  }else{
			 			  	WriteDebug("Field_type:$field_type change to VARCHAR2(200) \n");
			 			  	$field_type="VARCHAR2(200)";
			 			  	push(@filedList,"$fieldCode $field_type");
			 			  }
						}
				 		$sth_filed->finish;
			 		}else{
			 			for (my $i=1; $i < 50 ; $i++) {
			 				push(@filedList,"COLUMN_$i VARCHAR2(200)");
			 			}
			 		}
			 		
			 		#如果获取到字段信息不为空则建立缓存表
		 			if(@filedList){ 
		 				#建表异常需要处理			
			 			$tableStr=$tableStr.join(",",@filedList).")";	 	
			 			#WriteInfo("Desen table sql:$tableStr\n");	
			
						my $sth_createTab=$dbh->prepare($tableStr);   
						$sth_createTab->execute;	 
						$sth_createTab->finish(); 
						
						#建表后加索引
						my $sth_createIndex=$dbh->prepare("create unique index uq_gw_service_desen_$serviceId on gw_service_desen_$serviceId (gateway_task_id, $gatewayRowId)");
						$sth_createIndex->execute;
						$sth_createIndex->finish();
	        
	        	WriteInfo("Cache table GW_SERVICE_DESEN_$serviceId create succ.\n");
		 			}else{
		 				#报错
		 				WriteError("The field of cache table GW_DESEN_SERVICE_$serviceId is err! \n");
		 				next;
		 			}
			 	}
		 		
		 		#查找下载过来的本地原始文件
		 		#my $originalFileSql=$dbh->prepare("
		 		#	select f.file_path,f.file_name from gw_model_data_file f
				#	where f.file_type=4 and f.file_status=1 and f.task_id in 
				#	(select task_id from gw_model_data_fetch_task t where t.service_id=$serviceId and t.field_value='$fieldValue')
		 		#");
		 		#$originalFileSql->execute;
		 	  #($Fpath,$Fname) = $originalFileSql->fetchrow_array();
		 	  #$originalFileSql->finish;
		 	  
		 	  #if(!defined $localPath || !defined $Fname){
		 	  #	WriteError("original file db record file_type=4 is not exists!\n");
		 	  #	next;
		 	  #}
		 	  
				my $originalFile="$localPath/$Fname";
				if (!-e $originalFile) {WriteInfo("$originalFile original file is not exists!\n"); next;}
				if(index($Fname,".txt.gz") > 0){
					$originalFile=$localPath."\/".substr($Fname,0,index($Fname,".txt.gz")).".txt";
					#解压gz文件
			    my $gunzip = 'gunzip -c '.$localPath."\/".$Fname." > ".$originalFile;
			    WriteInfo("file gunzip $originalFile start.\n");
			  	system($gunzip);
			  	WriteInfo("file gunzip $originalFile finish.\n");
				}
				WriteInfo("desen file=".$originalFile."\n");
			
				#文件写入缓存表完成，设置任务状态 5脱敏处理
				my $sth_u=$dbh->prepare("update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=5,NODE_STATUS=1 where task_id=$taskId");
				$sth_u->execute;
				$sth_u->finish();
		 		WriteInfo("Update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=5!\n");
		 		
		 		#2016-8-19 添加流程跟踪登记
				my $process=$dbh->prepare("update gw_process set progress_status='3',STEP_STATUS='1' where process_id=$processId");
				$process->execute;
				$process->finish;
				
				my $operateContent = "开始服务数据脱敏处理";
				$operateContent = encode('gbk',decode('utf-8',$operateContent));
				my $processOper=$dbh->prepare("update gw_process_operation set operate_content='$operateContent',operate_time = sysdate ,progress_status = 1 ,dealtype='system' where process_id=$processId and step = '33'");
				$processOper->execute;
				$processOper->finish;
				WriteInfo("update gw_process set progress_status='3',STEP_STATUS='1' where process_id=$processId  \n");		
				#2016-8-19 添加流程跟踪登记
				
				##=============脱敏处理开始===========================
			 	##获得脱敏生产文件字段头（用“,”分隔）表头wordType=1 取字段英文编码，2 取中文名
			 	my $sth_field=$dbh->prepare("select GW_GET_DESENS_FILE_HEAD($serviceId,$userId,1) from dual");
				$sth_field->execute;
			 	my $desens_field = $sth_field->fetchrow_array();
			 	$sth_field->finish();
			 	
			 	#删除脱敏元数据记录
		 		$sth_u=$dbh->prepare("delete GW_SERVICE_DESEN_$serviceId where $gatewayTaskId='$taskId'");
		 		$sth_u->execute;
		 		$sth_u->finish;
		 		
		 		my $sth_service=$dbh->prepare("select SERVICE_CODE,SERVICE_NAME from GW_SERVICE where SERVICE_ID='$serviceId'");
				$sth_service->execute;
			 	my($serviceCode,$serviceName)=$sth_service->fetchrow_array(); 
			 	$sth_service->finish;
			 	
			 	#待生成的脱敏文件
			 	my $timeStr =strftime("%Y%m%d%H%M%S", localtime(time));
			 	my $outputDesenFile="$serviceCode"."_$timeStr.txt";
			 	my $desenFilePath = "$localPath/result/$outputDesenFile";
		   	if (-f $desenFilePath) {
					WriteInfo("delete desen csv file $desenFilePath start!\n");
			 		unlink $desenFilePath;
			 		WriteInfo("delete desen csv file $desenFilePath finish!\n");		
			 	}
			 									
				#文件处理
				process_file($originalFile,$userId,$modelId,$serviceId,$taskId,$loginName,$resultKeyFile,$processNum,$serviceCode,$fieldValue,$dataUserPath,$outputDesenFile,$gatewayCycleField,$gatewayRowId,$gatewayTaskId,$serviceSource,$desens_field,$processId);
			};#eval end
			
			if($@){
				#合规检查过程中出现问题，修改任务数据就绪状态为处理失败
				my $task=$dbh->prepare("update gw_model_data_fetch_task set data_progress_status=-1 where task_id=$taskId");
				$task->execute;
				$task->finish;
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
		#WriteInfo("Scan rule check finished, sleep $cycleValue s. \n");
		sleep $cycleValue;
	}
	CloseLog;
}

#信号捕抓处理,kill 或ctrl+c
sub handle{
	CloseLog;
	exit(0);
}
$SIG{TERM}=$SIG{INT}=\&handle;

sub FTPFile{  
	my ($FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag) = @_;
	WriteInfo("FTP文件信息：$FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag. \n");
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

#文件处理函数
sub process_file
{
	my ($originalFile,$userId,$modelId,$serviceId,$taskId,$loginName,$resultKeyFile,$processNum,$serviceCode,$fieldValue,$dataUserPath,$outputDesenFile,$gatewayCycleField,$gatewayRowId,$gatewayTaskId,$serviceSource,$desens_field,$processId) = @_;
	#my $file = shift @_;
	my ($key,$msg,$msgid,$msgtype,$buf);
	#print "Processing file $originalFile\n";
	$key = IPC::SysV::ftok($originalFile);
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
	$irregular = 0;
	
	@task_pids=();
	
	#启动4个子进程，子进程perl脚本为child.pl
	for (my $i=0; $i < $processNum ; $i++) {
		my $pid = fork();
		if( $pid < 0 ) {
			die "Failed to fork child ..."; # Fix me: remember to kill all forked child process.
		}
		elsif ( $pid == 0 ) {
			# Child process
			#print "=================main===perl ./gw_platform_desen_child.pl $originalFile $userId $serviceId $taskId $resultKeyFile=.\n";
			exec("perl ./gw_platform_desen_child.pl $originalFile $userId $serviceId $taskId $resultKeyFile $serviceSource");
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
	my $pid = fork();
	if( $pid < 0 ) {
		die "Failed to fork child ..."; # Fix me: remember to kill all forked child process.
	}
	elsif ( $pid == 0 ) {
		# Child process
		exec("perl ./gw_platform_desen_result.pl $taskId $modelId $userId $serviceId $dataUserPath $outputDesenFile $gatewayCycleField $gatewayRowId $gatewayTaskId $fieldValue $processNum $localPath $desens_field $processId");
		exit(1);
	}
	else {
		#登记子进程数量，用作子进程结束统计
		$running_child ++;
	}
	
	
	#定义正常类型消息
	$msgtype = 1;
	my $rowNum = 0;
	my $sendRow;
	my $val;
	#读取文件行内容并发送
	#Read file 
	open( FILE, $originalFile ) or die $!;
	while(<FILE>){
		$rowNum++;
		
		s/[\r\n]//g;	#去除换行符
		$sendRow = $rowNum."::".$_;
		my $desenValue = encode("gbk",decode("utf-8",$sendRow));
		$desenValue =~ s/\?//g;
		$desenValue = encode("utf-8",decode("gbk",$desenValue));
		#print "=================main===$desenValue=.\n";
		#发送行内容到消息队列
		$msgtype = 2;
		$msg->snd($msgtype, $desenValue );
		if ( $running == 0 ) {
			last;#子进程出错则结束读取文件(有异常、存在多列/缺列、不合规行数超出)
		}
		next;
	}	
	close( FILE);
			
	#非异常结束
	# send EOF message to every child.
	#定义结束类型消息
	$msgtype = 99;
	
	#给每一个子进程发送结束消息
	for( my $i=0; $i < $processNum; $i ++ ) {
		#print "=================main===send the over tag to child=$rowNum.\n";
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

