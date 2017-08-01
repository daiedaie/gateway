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

#日志路径
my $LOG_PATH="/home/gateway/perl/logs";
#定时扫描默认时间(单位：秒)
my $CHECK_CYCLE = 30;

#ORACLE连接
##########生产###########################################
#my $dbname="GATEWAY_52";
#my $user = "gateway";
#my $passwd = "gateway1234QWER";
##########测试#########################################
my $dbname="GATEWAY_133";
my $user = "gateway";
my $passwd = "gateway666";

#获取参数
my $filePath = $ARGV[0];
my $userId = $ARGV[1];
my $serviceId = $ARGV[2];
my $taskId = $ARGV[3];
my $resultKeyFile = $ARGV[4];
my $dataFileId = $ARGV[5];

my $pid = $$;
my $ppid = getppid();

print "Child $pid is now processing: $filePath \n";

my ($key,$msg,$msgid,$msgtype,$buf);
$key = IPC::SysV::ftok($filePath);
#打开数据行消息队列
$msg = new IPC::Msg( $key, 0) or die "Open MQ faile!";

my ($reskey,$resmsg,$resmsgid,$resmsgtype,$resbuf);
$reskey = IPC::SysV::ftok($resultKeyFile);
#打开结果消息队列
$resmsg = new IPC::Msg( $reskey, 0) or die "Open MQ faile!";

#设置日志 
SetLogPath($LOG_PATH);
SetLogLevel('D','D');  #文件和终端日志级别：'D', 'I', 'W', 'E', 'F', 'C'
SetLogHead("108_CHECK","108_CHECK");#文件和日志 前缀
OpenLog;
WriteInfo("===================开始运行===child==process===================\n");
#WriteInfo("LOG_PATH:$LOG_PATH\n");	 
#WriteInfo("CHECK_CYCLE:$CHECK_CYCLE\n");	
#WriteInfo("Oracle:$user/$dbname\n");

#创建数据库连接
my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";
#获取检查最大批次数
my $findMaxCheckBatch=$dbh->prepare("select max(check_batch) from gw_service_check_rule where user_id=$userId and service_id=$serviceId");
$findMaxCheckBatch->execute;
my $maxCheckBatch=$findMaxCheckBatch->fetchrow_array();
$findMaxCheckBatch->finish;

#获取检查规则
my $checkRule=$dbh->prepare(
	"select t.reorder,t.check_type,d.dict_value,t.check_rule,t.field_code,t.field_name ".
	"from gw_service_check_rule t ".
	"left join gw_sys_dict d on d.dict_code='DICT_CHECK_RULE_TYPE' and d.dict_key=t.check_type ".
	"where t.user_id=$userId and t.service_id=$serviceId and t.check_batch=$maxCheckBatch ".
	"order by t.reorder");
$checkRule->execute;

#封装检查规则
my %checkTypeHash;
my %checkRuleHash;
my %fieldHash;		#存放字段ID，根据reorder存放fieldId
#my $checkRuleStr="";	#检查规则字符串，用于保存免责日志
#my @fieldCodes;			#服务输出字段，用于保存免责日志
my $fieldCount=0;
while(my ($reorder,$checkType,$checkTypeName,$checkRule,$fieldCode,$fieldName) = $checkRule->fetchrow_array()){
	$fieldCount++;	#总合规检查字段数
	
	$checkTypeHash{$reorder}=$checkType;
	if($checkType=="1"){	#长度检查
		$checkRuleHash{$reorder}=$checkRule;
	}elsif ($checkType=="2"){ #字典数值检查
                $checkRule=decode("gbk",$checkRule);
		my @values = split /;/ ,$checkRule;
		$checkRuleHash{$reorder."_checkType2"}=$checkRule;
		foreach(@values){
			$checkRuleHash{$reorder."_".$_}=1;
		}
	}elsif ($checkType=="3"){ #范围内检查
		my @values = split /;/ ,$checkRule;
		$checkRuleHash{$reorder."_checkType3"}=$checkRule;
		$checkRuleHash{$reorder."_begin"}=$values[0];
		$checkRuleHash{$reorder."_end"}=$values[1];
	}
	
	#push(@fieldCodes,"$fieldCode");
	#$checkRuleStr = $checkRuleStr."$fieldCode($fieldName)=检查类型：$checkTypeName,检查规则：$checkRule;\n";
}
$checkRule->finish();

#WriteInfo("===============rule fieldCount=$fieldCount.\n");

my $loop = 1;

my $gatewayRowId = "gateway_row_id";
my $gatewayTaskId = "gateway_task_id";
my $gatewayRowData = "row_data";


my $rowData = "";#转码后的行内容
my @contents;
my $rowNum = 0;
my $rowIrregularCount = 0;	#不合规数据行数据量
my $dbLimit = 1000;					#不合规行数据插入到数据库的限制数量，超过不再插入到数据
my $irregularFileNum = 0;		#不合规文件的序号
my $checkResult=0;	#任务检查结果
#消息处理
while( $loop == 1 ) {
	eval{
			#读取消息队列的行数据
			$msgtype = $msg->rcv($buf, 1024);
			if ( $msgtype == 1 ) {
				$checkResult=0;
				@contents = split(::,$buf);
				$rowNum = $contents[0];
				$buf = $contents[1];
				#WriteInfo("=======1======row= data===$buf===============\n");
				#正常数据
				
				$rowData = encode("gbk",decode("utf-8",$buf));
				
				#$rowData = $buf;
				#WriteInfo("=======2======row= rowData===$rowData===============\n");
				my @fieldValues = split /\|/, $rowData;
				my $fieldNum = 0;		#fieldNum是输出文件中的字段顺序号，也是数据库中服务输出字段的字段顺序号
				my $rowIrregular = 0;	#当前行是否有不合规的记录
				my $irregularField = "";		#不合规字段，如：不合规字段序号:不合规字段值|不合规字段序号:不合规字段值
				foreach(@fieldValues){
					$fieldNum++;
					#出现未定义检查规则的字段，表示该字段为多出来(未配规则)的非法字段
					if(!defined $checkTypeHash{$fieldNum}){
						$checkResult=3;
						WriteInfo("file exists illegal data!rowNum=$rowNum,rule fieldCount=$fieldCount, fieldNum=$fieldNum.\n");
						last;
					}
					
					my $type = $checkTypeHash{$fieldNum};
					my $fieldIrregular = "";	#当前字段内容不合规的记录，不为空则表示有字段的内容不合规
					if($type eq "2"){	#字典值检查
						#my $key = encode("utf-8",$fieldValues[$fieldNum-1]);
						my $key = $fieldValues[$fieldNum-1];
						if($key =~ /(\d)\.0{1,}$/ ){
							$key = $1;
						}
						if(!defined $checkRuleHash{$fieldNum."_".$key}){
							$fieldIrregular = $checkRuleHash{$fieldNum."_checkType2"};
						}
					}elsif($type eq "3"){ #范围检查
						if($fieldValues[$fieldNum-1] < $checkRuleHash{$fieldNum."_begin"} || $fieldValues[$fieldNum-1] > $checkRuleHash{$fieldNum."_end"}){
							$fieldIrregular = $checkRuleHash{$fieldNum."_checkType3"};
						}
					}elsif($type eq "1"){	#长度检查
						if(length($fieldValues[$fieldNum-1]) > $checkRuleHash{$fieldNum}){
							$fieldIrregular = $checkRuleHash{$fieldNum};
						}
					}
					#存在不合规的字段
					if($fieldIrregular ne ""){
						$rowIrregular = 1;	#当前行存在不合规的记录
						$irregularField = $irregularField.(($irregularField eq ""?"":"|").$fieldNum.":".$fieldValues[$fieldNum-1]);
						
						#将不合规的字段记录保存到数据库,超过不再继续插入到数据库
						if($rowIrregularCount <= $dbLimit){
							my $checkRecord=$dbh->prepare("insert into gw_service_check_record(record_id,row_id,task_id,service_id,check_type,check_rule,FIELD_SORT) ".
																				"values(seq_gw_service_check_record.nextval,$rowNum,$taskId,$serviceId,'$type','$fieldIrregular',$fieldNum)");
							$checkRecord->execute;
							$checkRecord->finish;
						}
					}
				}
				#WriteInfo("===============rule fieldCount=$fieldCount===========file fieldNum=$fieldNum==checkResult==$checkResult==.\n");
				#判断输出字段是否多于或少于检查规则
				my $warnType=0;
				my $dataStr;
				if($checkResult==3){#输出文件字段多于检查规则
					$resmsgtype = 2;
					$dataStr = $rowNum."::".$buf;
					#发送多列/少列消息给结果子进程
	      	$resmsg->snd($resmsgtype, $dataStr );
					
				}elsif($fieldCount!=$fieldNum){#输出文件字段少于检查规则
					$resmsgtype = 3;
					$dataStr = $rowNum."::".$buf;
					#发送多列/少列消息给结果子进程
	      	$resmsg->snd($resmsgtype, $dataStr );
					#$checkResult=4;
					#$warnType=2;
					WriteInfo("file row lack field data!rowNum=$rowNum,rule fieldCount=$fieldCount, file fieldCount=$fieldNum.\n");
				}
				if($checkResult==3 || $checkResult==4){
					
					WriteInfo("task check_result=$checkResult, task abort.\n");
					my $data_value = encode('gbk',decode('utf-8',$buf));
					my $warnInfo=$dbh->prepare("delete gw_service_check_warn where task_id=$taskId");
					$warnInfo->execute;
					
					$warnInfo=$dbh->prepare("insert into gw_service_check_warn(warn_id,warn_type,warn_row,task_id,data_file_id,row_data) ".
																	"values(seq_gw_service_check_warn.nextval,$warnType,$rowNum,$taskId,$dataFileId,'$data_value')");
					$warnInfo->execute;
					$warnInfo->finish();
										
					last;
				}
				#存在不合规的数据，将行数据插入数据库
				if($rowIrregular == 1){
					$rowIrregularCount++;
					#发送不合规消息给结果子进程
					$resmsgtype = 1;
					my $error_value = encode('gbk',decode('utf-8',$buf));
					$dataStr = $rowNum."::".$error_value.">>".$irregularField;
					$resmsg->snd($resmsgtype, $dataStr );
										
				}
				#500000行打印一次日志
				if($rowNum % 500000 == 0){
					my $row = $rowNum/10000;
					WriteInfo("rule checking in $row w. irregular row count $rowIrregularCount.\n");
				}
				
		  }else {
		  	
				#消息类型为99，处理结束退出子进程
				$resmsgtype = 5; #正常结束
				my $messgeStr = $buf."::end";
				#WriteInfo("===child==type=$msgtype=messgeStr=$messgeStr=========. \n");
				$resmsg->snd($resmsgtype, $messgeStr);
				$loop = 0;
			}
		};
	  #捕抓到异常，发送异常信号给主进程
		if ($@){
			
			#发送执行异常规消息给结果子进程
			$resmsgtype = 4;
			my $messgeError = $rowNum."::error";
			$resmsg->snd($resmsgtype, $messgeError);
			
		}
	
}
WriteError("Child $pid end.\n");
CloseLog;

#信号捕抓处理,kill 或ctrl+c
sub handle{
	CloseLog;
	exit(0);
}
