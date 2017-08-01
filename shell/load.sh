#!/bin/bash


userName="gateway";
passwd="gateway666";
dbName="GATEWAY_133";

export ORACLE_HOME=/opt/oraclient/instantclient_11_2
export ORACLE_SID=gwdb
export TNS_ADMIN=$ORACLE_HOME
export LD_LIBRARY_PATH=$ORACLE_HOME:${LD_LIBRARY_PATH}
export PATH=$TNS_ADMIN:$ORACLE_HOME:$ORACLE_HOME/sdk:$PATH
export LANG=zh_CN.utf8
export NLS_LANG="SIMPLIFIED CHINESE_CHINA.ZHS16GBK"

data_result="";
sqlStr="";
function getSourceData()
{
  old_data=`/opt/oraclient/instantclient_11_2/sqlplus -s $userName/$passwd@$dbName<<EOF
  set echo off feedback off heading off underline off;
  $sqlStr
  exit;
EOF`;   #必须顶格写
  data_result=$old_data;  #去除字符串最后一个字符
}


FILE_LIST=`find $1 -cmin -10 -name Done*`

for file in ${FILE_LIST}
do
 echo $file;
 #取上传的源数据文件名称
 fileName=${file##*Done-};
 #压缩的源文件名称
 gzFileName=$fileName".gz";
 #echo $fileName;
 #获取文件目录
 path=${file%/*};
 #获取虚拟ftp用户名称
 user=${path##*/}
 #echo "user:"$user;
 #定义源文件全路径
 newFile=$path"/"$gzFileName;
 #定义源文件全路径
 ungzFileName=$path"/"$fileName;
 #echo "new:"$newFile;
 #根据文件命名规则获取服务编码
 service=${fileName%%-*}
 #echo "service :"$service;
 if [ -f $newFile ]; then
      #源数据文件存在,解压文件
      gunzip -c $newFile > $ungzFileName;
      #echo "File exists!";
      #查询用户Id
      sqlStr="select user_id,MOBLIE from gw_user where login_name='$user';";
      getSourceData
      userId=`echo $data_result | awk -F' ' '{print $1}'`;
      moblie=`echo $data_result | awk -F' ' '{print $2}'`;
      #echo "user:"$data_result;
      #查找服务是否审核通过
      sqlStr="select audit_status,service_id from GW_MODEL_DATA_FETCH t where user_id=$userId and service_id=(select service_id from gw_service where service_code='$service');";
      #echo "sql :"$sqlStr;
      getSourceData
      #echo ${#data_result};
      status=`echo $data_result | awk -F' ' '{print $1}'`;
      serviceId=`echo $data_result | awk -F' ' '{print $2}'`;
      
      #echo "result:"$status"",service_id :"$serviceId"，user_id :"$userId";
      mateService="";
      if [ "$status" = "2" ];then
          #审核通过           
          #echo "审核通过；";
	  mateService=$serviceId;
      else
          #没有找到匹配的服务
          echo "没有找到匹配的服务!";
      fi
      #查询待办ID
      sqlStr="select SEQ_GW_WORK_PLAN.nextval from dual;";
      getSourceData
      workPlanId=`echo $data_result | awk -F' ' '{print $1}'`;
      #echo "workId:"$workPlanId;
      #查询短信ID
      sqlStr="select SEQ_GW_SMS.nextval from dual;";
      getSourceData
      smsId=`echo $data_result | awk -F' ' '{print $1}'`;
      #定义待办属性信息
      planContent="系统检测到您新上传数据源文件:"$fileName".gz，请确认对应的取数服务。";
      #根据数据库字符集转码，使入库后不乱码
      planContent=`echo $planContent|/usr/bin/iconv -f utf-8 -t gbk`;
      title=`echo "创建实时取数任务确认"|/usr/bin/iconv -f utf-8 -t gbk`;
      #title="创建实时取数任务确认";
      #待办主表数据入库
      /opt/oraclient/instantclient_11_2/sqlplus -s $userName/$passwd@$dbName <<EOF
      insert into  GW_WORK_PLAN values($workPlanId,'$title','32','$planContent','1',null,null,sysdate,0,null,$userId,null,null,null,$smsId,null);
			commit;
			exit;
EOF
			#待办参数表数据入库
      /opt/oraclient/instantclient_11_2/sqlplus -s $userName/$passwd@$dbName <<EOF
      insert into GW_WORK_PLAN_param values(SEQ_GW_WORK_PLAN_PARAM.nextval,$workPlanId,'serviceId','$mateService');
      insert into GW_WORK_PLAN_param values(SEQ_GW_WORK_PLAN_PARAM.nextval,$workPlanId,'userId','$userId');
      insert into GW_WORK_PLAN_param values(SEQ_GW_WORK_PLAN_PARAM.nextval,$workPlanId,'fileName','$gzFileName');
			commit;
			exit;
EOF
			
			#短信表数据入库
			smsContent="系统检测到您新上传数据源文件:"$fileName".gz，请确认对应的取数服务。【数据网关平台】";
                        #echo "smsContent1="$smsContent;
			smsContent=`echo $smsContent|/usr/bin/iconv -f utf-8 -t gbk`;
                        #echo "smsContent2="$smsContent;
      /opt/oraclient/instantclient_11_2/sqlplus -s $userName/$passwd@$dbName <<EOF
      insert into GW_SMS values($smsId,$moblie,'$smsContent',null,null,0,sysdate,null);
			commit;
			exit;
EOF
			

 else
      #源文件不存在，不生成确认待办
      echo "source File not exist!"
 fi
done

