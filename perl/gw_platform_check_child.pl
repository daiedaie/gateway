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

#��־·��
my $LOG_PATH="/home/gateway/perl/logs";

#ORACLE����
##########����###########################################
#my $dbname="GATEWAY_52";
#my $user = "gateway";
#my $passwd = "gateway1234QWER";
##########����#########################################
my $dbname="GATEWAY_133";
my $user = "gateway";
my $passwd = "gateway666";

#��ȡ����
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
#����������Ϣ����
$msg = new IPC::Msg( $key, 0) or die "Open MQ faile!";

my ($reskey,$resmsg,$resmsgid,$resmsgtype,$resbuf);
$reskey = IPC::SysV::ftok($resultKeyFile);
#�򿪽����Ϣ����
$resmsg = new IPC::Msg( $reskey, 0) or die "Open MQ faile!";

#������־ 
SetLogPath($LOG_PATH);
SetLogLevel('D','D');  #�ļ����ն���־����'D', 'I', 'W', 'E', 'F', 'C'
SetLogHead("GW_DESEN","GW_DESEN");#�ļ�����־ ǰ׺
OpenLog;
WriteInfo("===================��ʼ����========================\n");
WriteInfo("LOG_PATH:$LOG_PATH\n");	 	
WriteInfo("Oracle:$user/$dbname\n");

#�������ݿ�����
my $dbh=DBI->connect("dbi:Oracle:$dbname",$user,$passwd) or die "can't connect to database";

#�������Ϲ���,ֱ���������Ĺ������Ϲ���
my $checkRule=$dbh->prepare(
	"select f.field_id,f.field_code,f.field_name,t.RULE_TYPE,d.dict_value,t.RULE_CONTENT,t.REPLACE_CONTENT,t.CONDITION_TYPE,t.CONDITION_CONTENT from gw_desen_service_field t ".
	"inner join gw_service_field f on f.field_id=t.field_id ".
	"left join gw_sys_dict d on d.dict_code='DICT_DESEN_RULE_TYPE' and d.dict_key=t.rule_type ".
	"where t.user_id=$userId and t.service_id=$serviceId and batch=$maxCheckBatch ".
	"order by t.field_id");
$checkRule->execute;

my %checkTypeHash;
my %checkRuleHash;
my %fieldIdHash;			#����ֶ�ID������fieldName��ȡfieldId
my %fieldCodeHash;		#����ֶ�Code������fieldName��ȡfieldCode
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
		if($ruleType=="2"){  #��Χ���滻
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

my $rowData = "";#ת����������
my @contents;
my $rowNum=0;
#my $irregularFileNum = 0;		#���Ϲ��ļ������
my $rowIrregularCount = 0;
my $dbLimit = 1000;
my $dataStr;
#��Ϣ����
while( $loop == 1 ) {
	eval{
			#��ȡ��Ϣ���е�������
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
				my $fieldNum = 0;		#fieldNum������ļ��е��ֶ�˳��ţ�����˳���ȡ���ֶ����ƣ���ȡ���ֶ�ID
				my $rowIrregular = 0;	#��ǰ���Ƿ��в��Ϲ�ļ�¼
				my $irregularField = "";		#���Ϲ��ֶΣ��磺���Ϲ��ֶ����:���Ϲ��ֶ�ֵ|���Ϲ��ֶ����:���Ϲ��ֶ�ֵ
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
						if($rule_type == "1" && index($fieldValue,$checkRuleHash{$fieldName."_ruleContent"}) > -1){		#�ַ��滻,����Ƿ񻹰�����Ҫ�滻���ַ�
								$fieldIrregular = "1";
						}elsif($rule_type == "2"){		#��Χ���滻
							#�滻����ʼλ�����ֶ�ֵ�ĳ��ȷ�Χ��
							if(length($fieldValues[$fieldNum-1]) >= ($checkRuleHash{$fieldName."_ruleContent"."_begin"}-1)){
								my $start = $checkRuleHash{$fieldName."_ruleContent"."_begin"};
								my $end = $checkRuleHash{$fieldName."_ruleContent"."_end"};
								my $value = "";
								if(!defined $end){
									$value = substr($fieldValue,($start-1));
								}else{
									$value = substr($fieldValue,($start-1),($end-$start+1));
								}
								#��2,5 ����2-5����ȡ�������ж��Ƿ�����滻�����ݣ�replaceContent��
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
					
					#�����Ϲ���ֶμ�¼���浽���ݿ�
					if($fieldIrregular != "0"){
						$rowIrregular = 1;	#��ǰ�д��ڲ��Ϲ�ļ�¼
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
				#���ڲ��Ϲ�����ݣ��������ݲ������ݿ�
				if($rowIrregular == 1){
					
					#���Ͳ��Ϲ���Ϣ������ӽ���
					$dataStr = $rowNum."::".$serviceFields."::".$irregularField."::".$buf;
					$resmsg->snd(1, $dataStr);	#���Ϲ��������
				}
				
				if($rowNum % 500000 == 0){
					my $row = $rowNum/500000;
					WriteInfo("rule checking in $row w. irregular row count $rowIrregularCount.\n");
				}
		  }elsif($msgtype == 2){	#�쳣���˳����
		  	$resmsg->snd(-1,$rowNum);
		  	$loop = 0;
		  }else {
				#��Ϣ����Ϊ99����������˳��ӽ���
				$resmsgtype = 5; #��������
				$resmsg->snd($resmsgtype, $buf );
				$loop = 0;
			}
		};
	  #��ץ���쳣�������쳣�źŸ�������
		if ($@){
			#����ִ���쳣����Ϣ������ӽ���
			$resmsgtype = -1;
			my $messgeError = $rowNum."::error";
			$resmsg->snd($resmsgtype, $messgeError );
			WriteError("taskId=$taskId task rule check error!.\n");
			WriteError("$@.\n");
		}
}
WriteInfo("Child $pid end.\n");
CloseLog;

#�źŲ�ץ����,kill ��ctrl+c
sub handle{
	CloseLog;
	exit(0);
}
