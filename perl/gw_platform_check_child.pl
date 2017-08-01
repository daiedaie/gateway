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
my $checkFile = $ARGV[0];
my $userId = $ARGV[1];
my $serviceId = $ARGV[2];
my $taskId = $ARGV[3];
my $resultKeyFile = $ARGV[4];
my $maxCheckBatch = $ARGV[5];

my $pid = $$;
my $ppid = getppid();

print "Child $pid is now processing: $checkFile \n";

my ($key,$msg,$msgid,$msgtype,$buf,$resultFieldNamesStr,$serviceFields);
$key = IPC::SysV::ftok($checkFile);
#打开数据行消息队列
$msg = new IPC::Msg( $key, 0) or die "Open MQ faile!";

my ($reskey,$resmsg,$resmsgid,$resmsgtype,$resbuf);
$reskey = IPC::SysV::ftok($resultKeyFile);
#打开结果消息队列
$resmsg = new IPC::Msg( $reskey, 0) or die "Open MQ faile!";

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

#脱敏并合规检查,直接用脱敏的规则做合规检查
my $checkRule=$dbh->prepare(
	"select f.field_id,f.field_code,f.field_name,t.RULE_TYPE,d.dict_value,t.RULE_CONTENT,t.REPLACE_CONTENT,t.CONDITION_TYPE,t.CONDITION_CONTENT from gw_desen_service_field t ".
	"inner join gw_service_field f on f.field_id=t.field_id ".
	"left join gw_sys_dict d on d.dict_code='DICT_DESEN_RULE_TYPE' and d.dict_key=t.rule_type ".
	"where t.user_id=$userId and t.service_id=$serviceId and batch=$maxCheckBatch ".
	"order by t.field_id");
$checkRule->execute;

my %checkTypeHash;
my %checkRuleHash;
my %fieldIdHash;			#存放字段ID，根据fieldName获取fieldId
my %fieldCodeHash;		#存放字段Code，根据fieldName获取fieldCode
while(my ($fieldId,$fieldCode,$fieldName,$ruleType,$ruleName,$ruleContent,$replaceContent,$conditionType,$conditionContent)=$checkRule->fetchrow_array()){	
	#$fieldName = decode("gbk",$fieldName);
	#$ruleContent = decode("gbk",$ruleContent);
	#$replaceContent = decode("gbk",$replaceContent);
	
	$fieldIdHash{$fieldName}=$fieldId;
	$fieldCodeHash{$fieldName}=$fieldCode;
	if(defined $ruleType){
		$checkTypeHash{$fieldName."_ruleType"}=$ruleType;
		$checkRuleHash{$fieldName."_ruleContent"}=$ruleContent;
		$checkRuleHash{$fieldName."_replaceContent"}=$replaceContent;
		if($ruleType=="2"){  #范围内替换
			my @contents = split /,/ ,$ruleContent;
			$checkRuleHash{$fieldName."_ruleContent"."_begin"}=$contents[0];
			if(@contents>1){
				$checkRuleHash{$fieldName."_ruleContent"."_end"}=$contents[1];
			}
		}
	}
	if(defined $conditionType){
		$checkTypeHash{$fieldName."_conditionType"}=$conditionType;
		$checkRuleHash{$fieldName."_conditionContent"}=$conditionContent;
		if($conditionType eq "in"){
			my @conditions = split /,/ ,$conditionContent;
			foreach(@conditions){
				$checkRuleHash{$fieldName."_conditionContent".$_}=1;
			}
		}
	}
}
$checkRule->finish();

my $loop = 1;

my $gatewayRowId = "gateway_row_id";
my $gatewayTaskId = "gateway_task_id";
my $gatewayRowData = "row_data";

my $rowData = "";#转码后的行内容
my @contents;
my $rowNum=0;
#my $irregularFileNum = 0;		#不合规文件的序号
my $rowIrregularCount = 0;
my $dbLimit = 1000;
my $dataStr;
#消息处理
while( $loop == 1 ) {
	eval{
			#读取消息队列的行数据
			$msgtype = $msg->rcv($buf, 1024);
			if ( $msgtype == 1 ) {
				@contents = split(::,$buf);
				$resultFieldNamesStr = $contents[0];
				$serviceFields = $contents[1];
				$rowNum = $contents[2];
				$buf = $contents[3];
				
				my @resultFieldNames = split /,/, $resultFieldNamesStr;
				#print "===========child ==resultFieldNames===@resultFieldNames======  \n"; 
		 		if($buf eq ''){
		 			next;
		 		}
		 		
				
				#my @fieldValues = split /,/, decode("gbk",$_);
				my @fieldValues = split /,/, $buf;
				#print "===========child ==fieldValues===@fieldValues======  \n"; 
				my $fieldNum = 0;		#fieldNum是输出文件中的字段顺序号，根据顺序号取得字段名称，再取得字段ID
				my $rowIrregular = 0;	#当前行是否有不合规的记录
				my $irregularField = "";		#不合规字段，如：不合规字段序号:不合规字段值|不合规字段序号:不合规字段值
				foreach(@fieldValues){
					$fieldNum++;
					my $fieldValue = $fieldValues[$fieldNum-1];
					my $fieldName = $resultFieldNames[$fieldNum-1];
					my $rule_type = $checkTypeHash{$fieldName."_ruleType"};
					
					my $rule_content = $checkRuleHash{$fieldName."_ruleContent"};
					my $replace_content = $checkRuleHash{$fieldName."_replaceContent"};
					my $condition_type = $checkTypeHash{$fieldName."_conditionType"};
					my $condition_content = $checkRuleHash{$fieldName."_conditionContent"};
					#print "===========child =rule_type=$checkTypeHash{$fieldName.'_ruleType'}==rule_content=$checkRuleHash{$fieldName.'_ruleContent'}====replace_content=$checkRuleHash{$fieldName.'_replaceContent'}=  \n"; 
					my $fieldIrregular = "0";
					if(defined $rule_type){
						#print "===========child =rule_type=$rule_type=========  \n"; 
						if($rule_type == "1" && index($fieldValue,$checkRuleHash{$fieldName."_ruleContent"}) > -1){		#字符替换,检查是否还包含需要替换的字符
								$fieldIrregular = "1";
						}elsif($rule_type == "2"){		#范围内替换
							#替换的起始位置在字段值的长度范围内
							if(length($fieldValues[$fieldNum-1]) >= ($checkRuleHash{$fieldName."_ruleContent"."_begin"}-1)){
								my $start = $checkRuleHash{$fieldName."_ruleContent"."_begin"};
								my $end = $checkRuleHash{$fieldName."_ruleContent"."_end"};
								my $value = "";
								if(!defined $end){
									$value = substr($fieldValue,($start-1));
								}else{
									$value = substr($fieldValue,($start-1),($end-$start+1));
								}
								#如2,5 将第2-5个截取出来，判断是否存在替换的内容（replaceContent）
								if(index($value,$checkRuleHash{$fieldName."_replaceContent"}) == -1){
									$fieldIrregular = "1";
								}
							}
						}
					}
					if($fieldIrregular != "1"){
						$rule_type="";
						$rule_content="";
						$replace_content="";
					}
					
					if(defined $condition_type){
						if($condition_type eq "in"){
							if(!defined $checkRuleHash{$fieldName."_conditionContent".$fieldValue}){
								$fieldIrregular = "2";
							}
						}else{
							if($condition_type eq "=" && $fieldValue != $condition_content){
								$fieldIrregular = "2";
							}elsif($condition_type eq "!=" && $fieldValue == $condition_content){
								$fieldIrregular = "2";
							}elsif($condition_type eq ">" && $fieldValue <= $condition_content){
								$fieldIrregular = "2";
							}elsif($condition_type eq ">=" && $fieldValue < $condition_content){
								$fieldIrregular = "2";
							}elsif($condition_type eq "<" && $fieldValue >= $condition_content){
								$fieldIrregular = "2";
							}elsif($condition_type eq "<=" && $fieldValue > $condition_content){
								$fieldIrregular = "2";
							}
						}
					}
					if($fieldIrregular != "2"){
						$condition_type = "";
						$condition_content = "";
					}
					
					#将不合规的字段记录保存到数据库
					if($fieldIrregular != "0"){
						$rowIrregular = 1;	#当前行存在不合规的记录
						$irregularField = $irregularField.(($irregularField eq ""?"":",").$fieldNum.":".encode("gbk",$fieldValues[$fieldNum-1]));
						
						if($rowIrregularCount <= $dbLimit){
							$rule_content = encode("gbk",$rule_content);
							$replace_content = encode("gbk",$replace_content);
							my $checkRecord=$dbh->prepare("insert into gw_service_check_record(record_id,row_id,task_id,service_id,rule_type,rule_content,replace_content,condition_type,condition_content,FIELD_SORT) ".
																				"values(seq_gw_service_check_record.nextval,$rowNum,$taskId,$serviceId,'$rule_type','$rule_content','$replace_content','$condition_type','$condition_content',$fieldNum)");
							$checkRecord->execute;
							$checkRecord->finish;
						}
					}
				}
				#存在不合规的数据，将行数据插入数据库
				if($rowIrregular == 1){
					
					#发送不合规消息给结果子进程
					$dataStr = $rowNum."::".$serviceFields."::".$irregularField."::".$buf;
					$resmsg->snd(1, $dataStr);	#不合规的行数据
				}
				
				if($rowNum % 500000 == 0){
					my $row = $rowNum/500000;
					WriteInfo("rule checking in $row w. irregular row count $rowIrregularCount.\n");
				}
		  }elsif($msgtype == 2){	#异常，退出检查
		  	$resmsg->snd(-1,$rowNum);
		  	$loop = 0;
		  }else {
				#消息类型为99，处理结束退出子进程
				$resmsgtype = 5; #正常结束
				$resmsg->snd($resmsgtype, $buf );
				$loop = 0;
			}
		};
	  #捕抓到异常，发送异常信号给主进程
		if ($@){
			#发送执行异常规消息给结果子进程
			$resmsgtype = -1;
			my $messgeError = $rowNum."::error";
			$resmsg->snd($resmsgtype, $messgeError );
			WriteError("taskId=$taskId task rule check error!.\n");
			WriteError("$@.\n");
		}
}
WriteInfo("Child $pid end.\n");
CloseLog;

#信号捕抓处理,kill 或ctrl+c
sub handle{
	CloseLog;
	exit(0);
}
