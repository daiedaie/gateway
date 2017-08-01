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
# my $dbname="GATEWAY_52";
# my $user = "gateway";
# my $passwd = "gateway1234QWER";
##########测试#########################################
my $dbname="GATEWAY_133";
my $user = "gateway";
my $passwd = "gateway666";

$ENV{'LANG'}='en_US.UTF-8';
$ENV{'NLS_LANG'}='SIMPLIFIED CHINESE_CHINA.ZHS16GBK';


#获取参数
my $originalFile = $ARGV[0];
my $userId = $ARGV[1];
my $serviceId = $ARGV[2];
my $taskId = $ARGV[3];
my $resultKeyFile = $ARGV[4];
my $serviceSource = $ARGV[5];

my $pid = $$;
my $ppid = getppid();

#print "Child $pid is now processing: $originalFile \n";

my ($key,$msg,$msgid,$msgtype,$buf,$resultFieldCodeStr,$desen_field);
$key = IPC::SysV::ftok($originalFile);
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
#WriteInfo("Oracle:$user/$dbname\n");

#创建数据库连接
my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";
my $checkRule;

#查询脱敏规则
if($serviceSource == 2){
	$checkRule=$dbh->prepare(
	"select reorder,t.check_id,t.field_code,t.field_name,f.rule_type,f.RULE_CONTENT,f.REPLACE_CONTENT,f.CONDITION_TYPE,f.CONDITION_CONTENT ".
	"from gw_service_check_rule t ".
	"inner join gw_desen_service_field f on f.field_id=t.check_id ".
	"where t.user_id=$userId and t.service_id=$serviceId and batch=(select max(batch) from gw_desen_service_field where user_Id=$userId and service_Id=$serviceId) ".
	"order by t.reorder");
}else{
	$checkRule=$dbh->prepare(
	"select f.row_num,f.field_id,f.field_code,f.field_name,t.RULE_TYPE,t.RULE_CONTENT,t.REPLACE_CONTENT,t.CONDITION_TYPE,t.CONDITION_CONTENT  ".
	"from gw_desen_service_field t  ".
	"inner join(select rownum row_num,a.* from gw_service_field a where service_id=33 order by field_id)f on f.field_id=t.field_id  ".
	"where t.user_id=$userId and t.service_id=$serviceId and batch=(select max(batch) from gw_desen_service_field where user_Id=$userId and service_Id=$serviceId)  ".
	"order by t.field_id");
}
$checkRule->execute;

my %checkTypeHash;
my %checkRuleHash;
my %fieldIdHash;			#存放字段ID，根据fieldName获取fieldId
my %fieldNameHash;		#存放字段Code，根据fieldName获取fieldCode
while(my ($reorder,$fieldId,$fieldCode,$fieldName,$ruleType,$ruleContent,$replaceContent,$conditionType,$conditionContent)=$checkRule->fetchrow_array()){	
	$fieldName = decode("gbk",$fieldName);
	$ruleContent = decode("gbk",$ruleContent);
	$replaceContent = decode("gbk",$replaceContent);
	
	#$fieldIdHash{$fieldCode}=$fieldId;
	$fieldNameHash{$reorder}=$fieldName;
	if(defined $ruleType){
		$checkTypeHash{$reorder."_ruleType"}=$ruleType;
		$checkRuleHash{$reorder."_ruleContent"}=$ruleContent;
		$checkRuleHash{$reorder."_replaceContent"}=$replaceContent;
		if($ruleType=="2"){  #范围内替换
			my @contents = split /,/ ,$ruleContent;
			$checkRuleHash{$reorder."_ruleContent"."_begin"}=$contents[0];
			if(@contents>1){
				$checkRuleHash{$reorder."_ruleContent"."_end"}=$contents[1];
			}
		}					
	}
	if(defined $conditionType){
		$checkTypeHash{$reorder."_conditionType"}=$conditionType;
		$checkRuleHash{$reorder."_conditionContent"}=$conditionContent;
		if($conditionType eq "in"){
			my @conditions = split /,/ ,$conditionContent;
			foreach(@conditions){
				$checkRuleHash{$reorder."_conditionContent".$_}=1;
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
my $irregularFileNum = 0;		#不合规文件的序号
#消息处理
while( $loop == 1 ) {
	eval{
			#读取消息队列的行数据
			$msgtype = $msg->rcv($buf, 1024);
			
			if ( $msgtype == 2 ) {
				my @contents = split(::,$buf);
				$rowNum = $contents[0];
				$buf = $contents[1];
				#my @resultFieldCodes = split /,/, $resultFieldCodeStr;
						
		 		if($buf eq ''){
		 			next;
		 		}
		 		#行数据
				my $rowValue = encode("gbk",decode("utf-8",$buf));
				#my $rowValue = $buf;
				#print "=================rowValue===rowValue=$rowValue=.\n";
				
		 		########脱敏处理
		 		my @fieldValues = split /\|/, $rowValue;
		 		my $fieldNum = 0;		#fieldNum是输出文件中的字段顺序号，根据顺序号取得字段编码，再取得字段ID
		 		my @desenRowValue;  #脱敏后的行数据
		 		my $desenFilter = 0;
		 		#print "=================child===fieldValues=@fieldValues=.\n";
		 		foreach(@fieldValues){
					$fieldNum++;
					#my $fieldCode = $resultFieldCodes[$fieldNum-1];
					#print "==========1111111111=========fieldValue=$fieldValues[$fieldNum-1]=.\n";
					#存在脱敏字段配置
					if(defined $fieldNameHash{$fieldNum}){
						my $fieldValue = $fieldValues[$fieldNum-1];
						#print "=========222222222===========fieldValue=$fieldValue=.\n";
						my $rule_type = $checkTypeHash{$fieldNum."_ruleType"};
						my $rule_content = $checkRuleHash{$fieldNum."_ruleContent"};
						my $replace_content = $checkRuleHash{$fieldNum."_replaceContent"};
						my $condition_type = $checkTypeHash{$fieldNum."_conditionType"};
						my $condition_content = $checkRuleHash{$fieldNum."_conditionContent"};
					
						if(defined $condition_type){
							if($condition_type eq "in"){
								if(!defined $checkRuleHash{$fieldNum."_conditionContent".$fieldValue}){
									$desenFilter=1;
								}
							}else{
								if($condition_type eq "=" && $fieldValue != $condition_content){
									$desenFilter=1;
								}elsif($condition_type eq "!=" && $fieldValue == $condition_content){
									$desenFilter=1;
								}elsif($condition_type eq ">" && $fieldValue <= $condition_content){
									$desenFilter=1;
								}elsif($condition_type eq ">=" && $fieldValue < $condition_content){
									$desenFilter=1;
								}elsif($condition_type eq "<" && $fieldValue >= $condition_content){
									$desenFilter=1;
								}elsif($condition_type eq "<=" && $fieldValue > $condition_content){
									$desenFilter=1; 
								}
							}
						}
						
						if($desenFilter==1){
							last;
						}
						
						if(defined $rule_type){
							if($rule_type == 1){	#字符替换
								$fieldValue=~s/$rule_content/$replace_content/g;
							}elsif($rule_type == 2){	#范围内替换
								my $start = $checkRuleHash{$fieldNum."_ruleContent"."_begin"};
								my $end = $checkRuleHash{$fieldNum."_ruleContent"."_end"};
								
								my $startValue="";
								my $endValue="";
								if(length ($fieldValue) >= $start){
									$startValue = substr($fieldValue,0,($start-1));
								}
								
								if(defined $end && length($fieldValue) >= $end){
									$endValue = substr($fieldValue,$end);
								}
								$fieldValue = $startValue.$replace_content.$endValue;
							}
						}
						push(@desenRowValue, $fieldValue);
					}
				}
				
				if($rowNum % 500000 == 0){
					my $row = $rowNum/500000;
					WriteInfo("data desen in $row w. \n");
				}
				#print "=================child===buf=$desenFilter======.\n";
				#未过滤的才输出
				if($desenFilter==0){
					my $desenValue = join("|", @desenRowValue);
					$desenValue = encode("gbk",decode("gbk",$desenValue));
					#print "=================child==desenValue=$desenValue=.\n";
					$resmsgtype = 1; #脱敏后的数据
					$resmsg->snd($resmsgtype, $rowNum."::".$desenValue );
				}
		  }else {
		  	#print "=================child==$msgtype=send the over tag to result=$buf.\n";
				#消息类型为99，处理结束退出子进程
				$resmsgtype = 3; #正常结束
				$resmsg->snd($resmsgtype, $buf );
				$loop = 0;
			}
		};
	  #捕抓到异常，发送异常信号给主进程
		if ($@){
			#发送执行异常规消息给结果子进程
			$resmsgtype = -1;
			$resmsg->snd($resmsgtype, "$rowNum" );
			WriteError("taskId=$taskId task desen error!.\n");
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
