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

#本地文件存放和csv文件生成路径
my $Lpath="/home/gateway/data/platform";

#日志路径
my $LOG_PATH="/home/gateway/perl/logs";

#上传文件的FTP服务器地址
my $uFTP_ADDR = "10.1.24.131";
my $uFTP_USERNAME = "root";
my $uFTP_PASSWORD = "123456";

#ORACLE连接
my $dbname="GATEWAY_133";
my $user = "gateway";
my $passwd = "gateway666";

sub main{
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
   WriteInfo("uFTP_ADDR:$uFTP_ADDR\n");	
   WriteInfo("Oracle:$user/$dbname\n");	
	 while(1){	
	 	
	 	#取日期,用于生成csv文件名:20141210
    my $date = strftime("%Y%m%d",localtime(time));
 	
	 	my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";
	 	#获取状态为2和4的任务,按用户优先级处理
	 	my $sth=$dbh->prepare(	 	
			"select t.task_id,                                              ".
			"       t.service_id,                                           ".
			"       t.user_id,                                              ".
			"       t.data_progress_status,                                 ".
			"       t.ftp_ip,                                               ".
			"       t.ftp_user,                                             ".
			"       t.FTP_PASSWORD,                                         ".
			"       t.FILE_PATH,                                            ".
			"       t.FILE_NAME,                                            ".
			"       decode(t.MODEL_ID,null,'null',t.MODEL_ID)as MODEL_ID,   ".
			"       t.FIELD_CODE,                                           ".
			"       t.FIELD_VALUE                                           ".
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
			"               b.FIELD_CODE,                                   ".
			"               b.FIELD_VALUE,                                  ".
			"               b.download_start_time                           ".
			"          from GW_MODEL_DATA_FILE a, GW_MODEL_DATA_FETCH_TASK b".
			"         where b.DATA_PROGRESS_STATUS = '2'                    ".
			"           and b.Task_status = '1'                             ".
			"           and a.task_id = b.task_id                           ".
			"           and a.FILE_TYPE = '1'                               ".
			"        union                                                  ".
			"        select b.task_id,                                      ".
			"               b.fetch_id,                                     ".
			"               b.service_id,                                   ".
			"               b.user_id,                                      ".
			"               b.data_progress_status,                         ".
			"               null                   as ftp_ip,               ".
			"               null                   as ftp_user,             ".
			"               null                   as FTP_PASSWORD,         ".
			"               null                   as FILE_PATH,            ".
			"               null                   as FILE_NAME,            ".
			"               b.MODEL_ID,                                     ".
			"               b.FIELD_CODE,                                   ".
			"               b.FIELD_VALUE,                                  ".
			"               b.download_start_time                           ".
			"          from GW_MODEL_DATA_FETCH_TASK b                      ".
			"         where b.DATA_PROGRESS_STATUS = '4'                    ".
			"           and b.Task_status = '1') t                          ".
			"				inner join gw_model_data_fetch f on f.fetch_id=t.fetch_id and f.audit_status=2 and f.desen_type='1' ".
			"       inner join gw_user u on t.user_id = u.user_id and u.status=1 ".
			" 			inner join gw_user org on u.org_id = org.org_id and org.user_type='orgUser'  ".
			" order by t.data_progress_status,org.run_level,u.run_level     "										
	 	);                                                       
	 	$sth->execute;
	 	while(my ($task_id,$service_id,$user_id,$data_progress_status,$FTP_ADDR,$Fuser,$Fpassword,$Fpath,$Fname,$model_id,$field_code,$field_value) = $sth->fetchrow_array()){
	 		#WriteInfo("Info:task_id|service_id|user_id|data_progress_status|FTP_ADDR|Fuser|Fpassword|Fpath|Fname|model_id|field_code|field_value\n");
	 		WriteInfo("Info:$task_id|$service_id|$user_id|$data_progress_status|$FTP_ADDR|$Fuser|$Fpassword|$Fpath|$Fname|$model_id|$field_code|$field_value\n");
	 		#自定义服务周期字段
	 		my $gatewayCycleField = "gateway_$field_code";
	 		#自定义主键ID，用于抽样、合规检查
			my $gatewayRowId = "GATEWAY_ROW_ID";
	 		#如果状态为2时需要下载文件并插入缓存表，状态为4直接进行脱敏
	 		if($data_progress_status == 2){
	 			WriteInfo("Update  GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=3 !\n"); 	
		 		#设置任务状态 3开始取数
		 		my $sth_u=$dbh->prepare("update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=3 where task_id=$task_id");
		 		$sth_u->execute;
		 		$sth_u->finish();
		 		 			
	 			#下载文件
	 			my $ftp_flag=0; #0:down  1:up
	 			my $rst = FTPFile($FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag);
	 			if($rst != 0 ){	 				
	 				next;
	 			}
	 			
		 		#检查缓存表是否存在
		 		my $sth_count=$dbh->prepare("select count(*) from all_tables where table_name = 'GW_SERVICE_$service_id'");
		 		$sth_count->execute;
		 		my $count = $sth_count->fetchrow_array();
		 		$sth_count->finish();
		 		
		 		#如果缓存表不存在，则创建表
		 		if($count == 0){
	 				WriteInfo("The cache table GW_SERVICE_$service_id not exist,must to create.\n"); 		 			
			 		#获取表字段信息
			 		my $sth_filed=$dbh->prepare("select FIELD_CODE,FIELD_TYPE from GW_SERVICE_FIELD where GATHER_TYPE='1' and SERVICE_ID='$service_id' order by field_id");
			 		$sth_filed->execute;		 		
		 			my $tableStr = "create table GW_SERVICE_$service_id  ( ";
		 			my @filedList;
		 			
		 			push(@filedList,"$gatewayRowId number");
					push(@filedList,"$gatewayCycleField varchar2(50)");
		 			while(my($field_code,$field_type)=$sth_filed->fetchrow_array){ 
		 				WriteDebug("Field_code:$field_code --> Field_type:$field_type \n");
		 				#如果字段未定义，则报错
		 				#if(!defined $field_code || !defined $field_type ){
		 				#	@filedList=();
		 				#	last;
		 				#}
		 				#if($field_code=="" || $field_type==""){
		 				#	@filedList=();
		 				#	last;					
		 				#}
		 				#对字段类型做特殊处理，非字符串类型的全部转换为VARCHAR2(200)类型
		 				$field_type = uc($field_type);
		 				if($field_type =~ /CHAR/ || $field_type =~ /LOB/){
		 					push(@filedList,"$field_code $field_type");
		 			  }else{
		 			  	WriteDebug("Field_type:$field_type change to VARCHAR2(200) \n");
		 			  	$field_type="VARCHAR2(200)";
		 			  	push(@filedList,"$field_code $field_type");
		 			  }
		 			}
		 			$sth_filed->finish();
		 			
		 			#如果获取到字段信息不为空则建立缓存表
		 			if(@filedList){ 
		 				#建表异常需要处理			
			 			$tableStr=$tableStr.join(",",@filedList).")";	 	
			 			WriteInfo("Cache table sql:$tableStr\n");	
			
						eval{
							my $sth_createTab=$dbh->prepare($tableStr);   
							$sth_createTab->execute;	 
							$sth_createTab->finish(); 
							
							#建表后加索引
							my $sth_createIndex=$dbh->prepare("create unique index uq_gw_service_$service_id on gw_service_$service_id ($gatewayCycleField, $gatewayRowId)");
							$sth_createIndex->execute;
							$sth_createIndex->finish();
	          };
	
	          if ($@){
	          	WriteError("Cache table GW_SERVICE_$service_id create fail!\n");
	          	next;	
	          }else{
	          	WriteInfo("Cache table GW_SERVICE_$service_id create succ.\n");
	          }
	
		 			}else{
		 				#报错
		 				WriteError("The field of cache table GW_SERVICE_$service_id is err! \n");
		 				next;
		 			}	
		 	  }
		 	  
		 	  $sth_u=$dbh->prepare("select count(1) from GW_SERVICE_$service_id where $gatewayCycleField='$field_value'");
		 	  $sth_u->execute;
		 	  my $cache_count=$sth_u->fetchrow_array;
		 	  $sth_u->finish;
		 	  #缓存表中不存在才导入到缓存表
		 	  if($cache_count == 0){
			 		#文件内容写入缓存表
					if(-e "$Lpath/$Fname"){
						#读取下载下来的ftp文件	
						open(FETCH_FILE,"$Lpath/$Fname") or print STDERR "Open file error.\n";	
						my $rownum=0;
						my $fail_count=0;
						my $filed;  #字段列表
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
							$str =~ s/,/\',\'/g; 
							#$str =~ s/^/\'/g;
							#$str =~ s/$/\'/g;
							$str ="\'$str\'";
							
							#逐行插入到数据库
							my $curSql="insert into GW_SERVICE_$service_id($gatewayRowId,$gatewayCycleField,$filed) values($rownum,$field_value,$str)";
							#print "$curSql \n";
												
							eval{
								my $sth_in=$dbh->prepare($curSql);   
								$sth_in->execute;	 
								$sth_in->finish();
		          };
		          
		          if ($@){
		          	WriteError("The file $rownum rows insert cache table fail.\n");
		          	$fail_count++;
		          }
							
							#如果前面连续10行插入数据库失败，则文件格式有问题，跳出不处理
							if($rownum == 10 && $rownum==$fail_count){
								WriteError("The file lasted 10 rows insert fail,maby the file err!break!\n");
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
					WriteInfo("cache table GW_SERVICE_$service_id cache data $cache_count rows.\n");					
				}
				
				#下载完成更新
				my $curTime = strftime("%Y-%m-%d %H:%M:%S",localtime(time));
	 			$sth_u=$dbh->prepare("update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=4 where task_id=$task_id");
		 		$sth_u->execute;	
		 		$sth_u->finish();	
		 		WriteInfo("Update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=4 !\n"); 
		 		WriteInfo("Download file succ!\n");
	 		}
	 		
	 		#检查检查脱敏结果缓存表是否存在
			my $desenTable=$dbh->prepare("select count(*) from all_tables where table_name = 'GW_SERVICE_DESEN_$service_id'");
			$desenTable->execute;
			my $desenCount=$desenTable->fetchrow_array();
			$desenTable->finish();
			
			if($desenCount==0){
		 		#创建脱敏后数据记录表
		 		#获取表字段信息
		 		my $sth_filed=$dbh->prepare("select FIELD_CODE,FIELD_TYPE from GW_SERVICE_FIELD where GATHER_TYPE='1' and SERVICE_ID='$service_id' order by field_id");
		 		$sth_filed->execute;
		 		my $tableStr = "create table GW_SERVICE_DESEN_$service_id  ( ";
		 		my @filedList;
		 		
		 		push(@filedList,"gateway_task_id number");
		 		push(@filedList,"$gatewayRowId number");
				push(@filedList,"$gatewayCycleField varchar2(50)");
		 		while(my ($field_code,$field_type)=$sth_filed->fetchrow_array()){
					#对字段类型做特殊处理，非字符串类型的全部转换为VARCHAR2(200)类型
					$field_type = uc($field_type);
	 				if($field_type =~ /CHAR/ || $field_type =~ /LOB/){
	 					push(@filedList,"$field_code $field_type");
	 			  }else{
	 			  	WriteDebug("Field_type:$field_type change to VARCHAR2(200) \n");
	 			  	$field_type="VARCHAR2(200)";
	 			  	push(@filedList,"$field_code $field_type");
	 			  }
				}
		 		$sth_filed->finish;
		 		
		 		#如果获取到字段信息不为空则建立缓存表
	 			if(@filedList){ 
	 				#建表异常需要处理			
		 			$tableStr=$tableStr.join(",",@filedList).")";	 	
		 			WriteInfo("Desen table sql:$tableStr\n");	
		
					eval{
						my $sth_createTab=$dbh->prepare($tableStr);   
						$sth_createTab->execute;	 
						$sth_createTab->finish(); 
						
						#建表后加索引
						my $sth_createIndex=$dbh->prepare("create unique index uq_gw_service_desen_$service_id on gw_service_desen_$service_id (gateway_task_id, $gatewayRowId)");
						$sth_createIndex->execute;
						$sth_createIndex->finish();
          };

          if ($@){
          	WriteError("Cache table GW_SERVICE_DESEN_$service_id create fail!\n");
          	next;	
          }else{
          	WriteInfo("Cache table GW_SERVICE_DESEN_$service_id create succ.\n");
          }
	 			}else{
	 				#报错
	 				WriteError("The field of cache table GW_DESEN_SERVICE_$service_id is err! \n");
	 				next;
	 			}
		 	}
	 	
		 	#文件写入缓存表完成，设置任务状态 5脱敏处理
			my $sth_u=$dbh->prepare("update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=5 where task_id=$task_id");
			$sth_u->execute;
			$sth_u->finish();
	 		WriteInfo("Update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=5!\n"); 
	 				 	
		 	##=============脱敏处理
		 	my $sth_field=$dbh->prepare("select GW_GET_DESENS_FILE_HEAD($service_id,$user_id,1) from dual");
			$sth_field->execute;			 	
		 	my @desens_field = $sth_field->fetchrow_array();
		 	$sth_field->finish();
		 	
		 	#调用数据库函数，获得脱敏SQL，将脱敏后的数据保存到脱敏结果表中
			$sth_u=$dbh->prepare("select GW_GET_DESENS_SQL($service_id,$user_id,'$gatewayCycleField','$field_value','$gatewayRowId,$gatewayCycleField') from dual");
			$sth_u->execute;
		 	my $desensSql = $sth_u->fetchrow_array();
		 	$sth_u->finish();
		 	WriteInfo("Cache desens SQL: $desensSql \n");	
		 	
		 	eval{
		 		$sth_u=$dbh->prepare("delete GW_SERVICE_DESEN_$service_id where $gatewayCycleField='$field_value'");
		 		$sth_u->execute;
		 		$sth_u->finish;
		 		
			 	my $desen_field = join(",",@desens_field);
			 	my $sth_desens=$dbh->prepare($desensSql);
			 	$sth_desens->execute;	
			 	while(my @curValue = $sth_desens->fetchrow_array()){
			 		my $desenValue = join(",",@curValue);
			 		$desenValue =~ s/\,/\',\'/g; 
					$desenValue ="\'$desenValue\'";
					my $desenInsert=$dbh->prepare("insert into GW_SERVICE_DESEN_$service_id(gateway_task_id,gateway_row_id,$gatewayCycleField,$desen_field) values($task_id,$desenValue)");
					$desenInsert->execute;
					$desenInsert->finish;
			 	}
			 	$sth_desens->finish;
			};
		 	if ($@){
	    	WriteError("Cache Execute Desens SQL fail!\n");
	    	next;
	    }
	    WriteInfo("Cache desens finished.\n");		
		 	
		 	#调用数据库函数，获得脱敏SQL，将脱敏后的数据保存到csv文件中
			$sth_u=$dbh->prepare("select GW_GET_DESENS_SQL($service_id,$user_id,'$gatewayCycleField','$field_value','') from dual");
			$sth_u->execute;
		 	my $desensSql = $sth_u->fetchrow_array();
		 	$sth_u->finish();
		 	WriteInfo("Desens SQL: $desensSql \n");	
		 	
		 	my $sth_service=$dbh->prepare("select SERVICE_CODE,SERVICE_NAME from GW_SERVICE where SERVICE_ID='$service_id'");
			$sth_service->execute;
		 	my($service_code,$service_name)=$sth_service->fetchrow_array(); 
		 	
		 	#执行脱敏SQL，生成CSV文件
		 	my $file=$Fname;
      $file="$service_code"."_$field_value.csv";
                        	
	   	my $desens_count=0;#脱敏记录数
		 	open(CSV_FILE,"> $Lpath/$file") or print STDERR "Open file error.\n";
		 	
		 	#调用数据库函数，获得脱敏生产文件表头wordType=1 取字段英文编码，2 取中文名
			$sth_u=$dbh->prepare("select GW_GET_DESENS_FILE_HEAD($service_id,$user_id,2) from dual");
			$sth_u->execute;
		 	my @desens_head = $sth_u->fetchrow_array();
		 	$sth_u->finish();
		 	
		 	printf CSV_FILE join(",",@desens_head);
      printf CSV_FILE "\r\n";
		 	eval{
		 		#执行脱敏，生成脱敏文件
			 	my $sth_desens=$dbh->prepare($desensSql);
				$sth_desens->execute;	 
			 	while(my @rowValue = $sth_desens->fetchrow_array()){
			 		printf CSV_FILE join(",",@rowValue);
			 		printf CSV_FILE "\r\n";
			 		$desens_count ++;
			 	}
			 	$sth_desens->finish();
		 	};
	    if ($@){
	    	WriteError("Execute Desens SQL fail!\n");
	    	next;
	    }	 	
		 	close CSV_FILE;
		 	WriteInfo("Desens finished,result file $file.\n");		
		 	##=============上传文件
		 	#获取上传文件的用户名密码,密码是加密的，需要解密
		 	$sth_u=$dbh->prepare("select LOGIN_NAME,LOGIN_PWD from GW_USER where user_id=$user_id");
			$sth_u->execute;
		 	my($uFuser,$uFpassword)=$sth_u->fetchrow_array();
		 	$sth_u->finish();
		 	
		 	#$uFpassword = get3DESDecrypt($uFpassword,'gateway');#密码解密
		 	
		 	#测试使用
		 	#my($uFuser,$uFpassword)=("test234","123456"); 
		 	
		 	
		 	my $uFpath="/home/users/$uFuser/result";
		 	WriteInfo("uFpath=$uFpath.\n");
		 	#上传文件
			my $ftp_flag=1; #0:down  1:up
			my $rst = FTPFile($uFTP_ADDR,$uFTP_USERNAME,$uFTP_PASSWORD,$Lpath,$uFpath,$file,$ftp_flag);
			if($rst != 0 ){
				next;
			}
		 	WriteInfo("Upload file succ!\n");
		 	
		 	#csv上传完成，删除临时文件
		 	if (-f "$Lpath/$file") {
		 		unlink "$Lpath/$file";
		 		WriteInfo("delete temporary file $Lpath/$file Success!\n");		
		 	}
		
		 	#csv文件上传完成，设置文件为已脱敏 2.------改为新插入一条记录
			#$sth_u=$dbh->prepare("update GW_MODEL_DATA_FILE set FILE_TYPE = '2' where task_id=$task_id");
		 	eval{
				my $curTime = strftime("%Y-%m-%d %H:%M:%S",localtime(time));
				$sth_u=$dbh->prepare(
					"insert into GW_MODEL_DATA_FILE (FILE_ID,TASK_ID,MODEL_ID,USER_ID,FILE_TYPE,FILE_STATUS,FILE_PATH,FILE_NAME,CREATE_TIME,FTP_IP,FTP_PORT)".
		      "values (SEQ_MODEL_DATA_FILE.NEXTVAL,$task_id,$model_id,$user_id,2,1,'$uFpath','$file',to_date('$curTime', 'yyyy-mm-dd hh24:mi:ss'),'$uFTP_ADDR',22)"
		    );
				$sth_u->execute;	
				$sth_u->finish(); 
		 	};
	    if ($@){
	    	WriteError("Insert GW_MODEL_DATA_FILE fail!\n");
	    	next;
	    }else{
	    	WriteInfo("Insert GW_MODEL_DATA_FILE finished!\n"); 	
	    }	 					
		 	
		 	#csv文件上传完成，设置任务状态 6结果生成,任务结束时间,数据类型,记录数
		 	my $curTime = strftime("%Y-%m-%d %H:%M:%S",localtime(time));
			$sth_u=$dbh->prepare("update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=6, DATA_TYPE=1,DATA_NUM=$desens_count where task_id=$task_id");
			$sth_u->execute;	
			$sth_u->finish();  	
			WriteInfo("Update GW_MODEL_DATA_FETCH_TASK DATA_PROGRESS_STATUS=6,DATA_TYPE=1,DATA_NUM=$desens_count.\n");		
	    WriteInfo("The task is finished.\n");		
    }
		if(defined $sth ){
		  $sth->finish(); 
		}
		$dbh->disconnect;
		WriteInfo("Scan and Excu finished, sleep $CHECK_CYCLE s. \n");
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



sub FTPFile(){  
	my ($FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag) = @_;
	#WriteInfo("FTP文件信息：$FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag. \n");
	eval{    
		  	
		my $ftp=Net::FTP->new("$FTP_ADDR",Passive=>1,Debug=>0,Timeout=>30) or die "Could not connect.\n"; 
               
		#登录到FTP                                                                      
		$ftp->login($Fuser,$Fpassword) or die "Could not login.$! \n";            
		$ftp->binary() or die "binary failed. \n";
		#切换目录                                                                       
		$ftp->cwd($Fpath) or die "Cannot change working directory ", $ftp->message;   
		

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
  	}	
  	
  	if($ftp_flag == 1){
  		WriteError("FTP up file fail! host:$FTP_ADDR,user:$Fuser,remoPath:$Fpath,localfile:$Fname\n");
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


&main;
