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

#�����ļ���ź�csv�ļ�����·��
my $Lpath="/home/gateway/data/platform";

#��־·��
my $LOG_PATH="/home/gateway/perl/logs";

#�ϴ��ļ���FTP��������ַ
my $uFTP_ADDR = "10.1.24.131";
my $uFTP_USERNAME = "root";
my $uFTP_PASSWORD = "123456";

#ORACLE����
my $dbname="GATEWAY_133";
my $user = "gateway";
my $passwd = "gateway666";

sub main{
	 #������־ 
	 SetLogPath($LOG_PATH);
	 SetLogLevel('D','D');  #�ļ����ն���־����'D', 'I', 'W', 'E', 'F', 'C'
	 SetLogSize(50000);#�ļ��л�����
	 SetLogHead("GW_DESEN","GW_DESEN");#�ļ�����־ ǰ׺
	 OpenLog;
   if(!(-d $Lpath && -w $Lpath)){
        WriteError("Lpath is not find. \n");
        CloseLog;
        exit;  
    }
	 WriteInfo("===================��ʼ����========================\n");
   WriteInfo("LOG_PATH:$LOG_PATH\n");	 
   WriteInfo("Lpath:$Lpath\n");	
   WriteInfo("CHECK_CYCLE:$CHECK_CYCLE\n");	
   WriteInfo("uFTP_ADDR:$uFTP_ADDR\n");	
   WriteInfo("Oracle:$user/$dbname\n");	
	 while(1){	
	 	
	 	#ȡ����,��������csv�ļ���:20141210
    my $date = strftime("%Y%m%d",localtime(time));
 	
	 	my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";
	 	#��ȡ״̬Ϊ2��4������,���û����ȼ�����
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
	 		#�Զ�����������ֶ�
	 		my $gatewayCycleField = "gateway_$field_code";
	 		#�Զ�������ID�����ڳ������Ϲ���
			my $gatewayRowId = "GATEWAY_ROW_ID";
	 		#���״̬Ϊ2ʱ��Ҫ�����ļ������뻺���״̬Ϊ4ֱ�ӽ�������
	 		if($data_progress_status == 2){
	 			WriteInfo("Update  GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=3 !\n"); 	
		 		#��������״̬ 3��ʼȡ��
		 		my $sth_u=$dbh->prepare("update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=3 where task_id=$task_id");
		 		$sth_u->execute;
		 		$sth_u->finish();
		 		 			
	 			#�����ļ�
	 			my $ftp_flag=0; #0:down  1:up
	 			my $rst = FTPFile($FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag);
	 			if($rst != 0 ){	 				
	 				next;
	 			}
	 			
		 		#��黺����Ƿ����
		 		my $sth_count=$dbh->prepare("select count(*) from all_tables where table_name = 'GW_SERVICE_$service_id'");
		 		$sth_count->execute;
		 		my $count = $sth_count->fetchrow_array();
		 		$sth_count->finish();
		 		
		 		#�����������ڣ��򴴽���
		 		if($count == 0){
	 				WriteInfo("The cache table GW_SERVICE_$service_id not exist,must to create.\n"); 		 			
			 		#��ȡ���ֶ���Ϣ
			 		my $sth_filed=$dbh->prepare("select FIELD_CODE,FIELD_TYPE from GW_SERVICE_FIELD where GATHER_TYPE='1' and SERVICE_ID='$service_id' order by field_id");
			 		$sth_filed->execute;		 		
		 			my $tableStr = "create table GW_SERVICE_$service_id  ( ";
		 			my @filedList;
		 			
		 			push(@filedList,"$gatewayRowId number");
					push(@filedList,"$gatewayCycleField varchar2(50)");
		 			while(my($field_code,$field_type)=$sth_filed->fetchrow_array){ 
		 				WriteDebug("Field_code:$field_code --> Field_type:$field_type \n");
		 				#����ֶ�δ���壬�򱨴�
		 				#if(!defined $field_code || !defined $field_type ){
		 				#	@filedList=();
		 				#	last;
		 				#}
		 				#if($field_code=="" || $field_type==""){
		 				#	@filedList=();
		 				#	last;					
		 				#}
		 				#���ֶ����������⴦�����ַ������͵�ȫ��ת��ΪVARCHAR2(200)����
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
		 			
		 			#�����ȡ���ֶ���Ϣ��Ϊ�����������
		 			if(@filedList){ 
		 				#�����쳣��Ҫ����			
			 			$tableStr=$tableStr.join(",",@filedList).")";	 	
			 			WriteInfo("Cache table sql:$tableStr\n");	
			
						eval{
							my $sth_createTab=$dbh->prepare($tableStr);   
							$sth_createTab->execute;	 
							$sth_createTab->finish(); 
							
							#����������
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
		 				#����
		 				WriteError("The field of cache table GW_SERVICE_$service_id is err! \n");
		 				next;
		 			}	
		 	  }
		 	  
		 	  $sth_u=$dbh->prepare("select count(1) from GW_SERVICE_$service_id where $gatewayCycleField='$field_value'");
		 	  $sth_u->execute;
		 	  my $cache_count=$sth_u->fetchrow_array;
		 	  $sth_u->finish;
		 	  #������в����ڲŵ��뵽�����
		 	  if($cache_count == 0){
			 		#�ļ�����д�뻺���
					if(-e "$Lpath/$Fname"){
						#��ȡ����������ftp�ļ�	
						open(FETCH_FILE,"$Lpath/$Fname") or print STDERR "Open file error.\n";	
						my $rownum=0;
						my $fail_count=0;
						my $filed;  #�ֶ��б�
						while(<FETCH_FILE>){
			        #�ļ���һ��Ϊ���ݿ���ֶ���,��:colum1,colum2,colum3
			  			if($rownum == 0 ){
				  			$filed = $_;	  			
				  			$filed =~ s/\s//g;#ȥ���ո�
				  			#$filed =~ s/\"//g;#ȥ��"  ---csv����txt����Ҫȥ������	
				  			$rownum ++;		
				  			next;        		        
							}
		
							my $str = $_;
							#$str =~ s/\",\"/\',\'/g; #�滻","Ϊ',' ---csv����txt����Ҫȥ������
							#$str =~ s/^\"/\'/g; #---csv����txt����Ҫȥ������
							#$str =~ s/\"$/\'/g; #---csv����txt����Ҫȥ������
							
							#�����ֶζ����ϵ����ţ�ת���ַ������뻺���
							$str =~ s/,/\',\'/g; 
							#$str =~ s/^/\'/g;
							#$str =~ s/$/\'/g;
							$str ="\'$str\'";
							
							#���в��뵽���ݿ�
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
							
							#���ǰ������10�в������ݿ�ʧ�ܣ����ļ���ʽ�����⣬����������
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
				
				#������ɸ���
				my $curTime = strftime("%Y-%m-%d %H:%M:%S",localtime(time));
	 			$sth_u=$dbh->prepare("update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=4 where task_id=$task_id");
		 		$sth_u->execute;	
		 		$sth_u->finish();	
		 		WriteInfo("Update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=4 !\n"); 
		 		WriteInfo("Download file succ!\n");
	 		}
	 		
	 		#������������������Ƿ����
			my $desenTable=$dbh->prepare("select count(*) from all_tables where table_name = 'GW_SERVICE_DESEN_$service_id'");
			$desenTable->execute;
			my $desenCount=$desenTable->fetchrow_array();
			$desenTable->finish();
			
			if($desenCount==0){
		 		#�������������ݼ�¼��
		 		#��ȡ���ֶ���Ϣ
		 		my $sth_filed=$dbh->prepare("select FIELD_CODE,FIELD_TYPE from GW_SERVICE_FIELD where GATHER_TYPE='1' and SERVICE_ID='$service_id' order by field_id");
		 		$sth_filed->execute;
		 		my $tableStr = "create table GW_SERVICE_DESEN_$service_id  ( ";
		 		my @filedList;
		 		
		 		push(@filedList,"gateway_task_id number");
		 		push(@filedList,"$gatewayRowId number");
				push(@filedList,"$gatewayCycleField varchar2(50)");
		 		while(my ($field_code,$field_type)=$sth_filed->fetchrow_array()){
					#���ֶ����������⴦�����ַ������͵�ȫ��ת��ΪVARCHAR2(200)����
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
		 		
		 		#�����ȡ���ֶ���Ϣ��Ϊ�����������
	 			if(@filedList){ 
	 				#�����쳣��Ҫ����			
		 			$tableStr=$tableStr.join(",",@filedList).")";	 	
		 			WriteInfo("Desen table sql:$tableStr\n");	
		
					eval{
						my $sth_createTab=$dbh->prepare($tableStr);   
						$sth_createTab->execute;	 
						$sth_createTab->finish(); 
						
						#����������
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
	 				#����
	 				WriteError("The field of cache table GW_DESEN_SERVICE_$service_id is err! \n");
	 				next;
	 			}
		 	}
	 	
		 	#�ļ�д�뻺�����ɣ���������״̬ 5��������
			my $sth_u=$dbh->prepare("update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=5 where task_id=$task_id");
			$sth_u->execute;
			$sth_u->finish();
	 		WriteInfo("Update GW_MODEL_DATA_FETCH_TASK set DATA_PROGRESS_STATUS=5!\n"); 
	 				 	
		 	##=============��������
		 	my $sth_field=$dbh->prepare("select GW_GET_DESENS_FILE_HEAD($service_id,$user_id,1) from dual");
			$sth_field->execute;			 	
		 	my @desens_field = $sth_field->fetchrow_array();
		 	$sth_field->finish();
		 	
		 	#�������ݿ⺯�����������SQL��������������ݱ��浽�����������
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
		 	
		 	#�������ݿ⺯�����������SQL��������������ݱ��浽csv�ļ���
			$sth_u=$dbh->prepare("select GW_GET_DESENS_SQL($service_id,$user_id,'$gatewayCycleField','$field_value','') from dual");
			$sth_u->execute;
		 	my $desensSql = $sth_u->fetchrow_array();
		 	$sth_u->finish();
		 	WriteInfo("Desens SQL: $desensSql \n");	
		 	
		 	my $sth_service=$dbh->prepare("select SERVICE_CODE,SERVICE_NAME from GW_SERVICE where SERVICE_ID='$service_id'");
			$sth_service->execute;
		 	my($service_code,$service_name)=$sth_service->fetchrow_array(); 
		 	
		 	#ִ������SQL������CSV�ļ�
		 	my $file=$Fname;
      $file="$service_code"."_$field_value.csv";
                        	
	   	my $desens_count=0;#������¼��
		 	open(CSV_FILE,"> $Lpath/$file") or print STDERR "Open file error.\n";
		 	
		 	#�������ݿ⺯����������������ļ���ͷwordType=1 ȡ�ֶ�Ӣ�ı��룬2 ȡ������
			$sth_u=$dbh->prepare("select GW_GET_DESENS_FILE_HEAD($service_id,$user_id,2) from dual");
			$sth_u->execute;
		 	my @desens_head = $sth_u->fetchrow_array();
		 	$sth_u->finish();
		 	
		 	printf CSV_FILE join(",",@desens_head);
      printf CSV_FILE "\r\n";
		 	eval{
		 		#ִ�����������������ļ�
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
		 	##=============�ϴ��ļ�
		 	#��ȡ�ϴ��ļ����û�������,�����Ǽ��ܵģ���Ҫ����
		 	$sth_u=$dbh->prepare("select LOGIN_NAME,LOGIN_PWD from GW_USER where user_id=$user_id");
			$sth_u->execute;
		 	my($uFuser,$uFpassword)=$sth_u->fetchrow_array();
		 	$sth_u->finish();
		 	
		 	#$uFpassword = get3DESDecrypt($uFpassword,'gateway');#�������
		 	
		 	#����ʹ��
		 	#my($uFuser,$uFpassword)=("test234","123456"); 
		 	
		 	
		 	my $uFpath="/home/users/$uFuser/result";
		 	WriteInfo("uFpath=$uFpath.\n");
		 	#�ϴ��ļ�
			my $ftp_flag=1; #0:down  1:up
			my $rst = FTPFile($uFTP_ADDR,$uFTP_USERNAME,$uFTP_PASSWORD,$Lpath,$uFpath,$file,$ftp_flag);
			if($rst != 0 ){
				next;
			}
		 	WriteInfo("Upload file succ!\n");
		 	
		 	#csv�ϴ���ɣ�ɾ����ʱ�ļ�
		 	if (-f "$Lpath/$file") {
		 		unlink "$Lpath/$file";
		 		WriteInfo("delete temporary file $Lpath/$file Success!\n");		
		 	}
		
		 	#csv�ļ��ϴ���ɣ������ļ�Ϊ������ 2.------��Ϊ�²���һ����¼
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
		 	
		 	#csv�ļ��ϴ���ɣ���������״̬ 6�������,�������ʱ��,��������,��¼��
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

#�źŲ�ץ����,kill ��ctrl+c
sub handle{
	CloseLog;
	exit(0);
}
$SIG{TERM}=$SIG{INT}=\&handle;



sub FTPFile(){  
	my ($FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag) = @_;
	#WriteInfo("FTP�ļ���Ϣ��$FTP_ADDR,$Fuser,$Fpassword,$Lpath,$Fpath,$Fname,$ftp_flag. \n");
	eval{    
		  	
		my $ftp=Net::FTP->new("$FTP_ADDR",Passive=>1,Debug=>0,Timeout=>30) or die "Could not connect.\n"; 
               
		#��¼��FTP                                                                      
		$ftp->login($Fuser,$Fpassword) or die "Could not login.$! \n";            
		$ftp->binary() or die "binary failed. \n";
		#�л�Ŀ¼                                                                       
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
