<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath %>" />
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>重新推送最多次数配置_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">	
$(function(){
	$("#count option[value=${gwSysCnfigVO.configValue}]").attr("selected",true);
	$("#smsCount option[value=${smsCountCnfigVO.configValue}]").attr("selected",true);
}) 
function saveRepushCounr(){
	$("#saveConfigBtn").attr("disabled",true);
	url = "system/config_saveRePushCount.do";
   	$.post(url,$("#configForm").serialize(),function(json){
   		$("#saveConfigBtn").attr("disabled",false);
		if (json.state == 'success') {
			alert("保存成功");
		}else{
			alert("保存失败，原因："+json.message);
		}
   	});
}
</script>
</head>	
<body class="FrameMain">
	<div class="main_form" method="post" align="left">
	<form id="configForm" name="configForm">
		<input type="hidden" name="rePushTimeCnfigVO.configId" value="${rePushTimeCnfigVO.configId }"/>
		<input type="hidden" name="rePushTimeCnfigVO.configType" value="${rePushTimeCnfigVO.configType }"/>
        <input type="hidden" name="rePushTimeCnfigVO.configUnit" value="${rePushTimeCnfigVO.configUnit }"/>
        <input type="hidden" name="smsCountCnfigVO.configId" value="${smsCountCnfigVO.configId }"/>
		<input type="hidden" name="smsCountCnfigVO.configType" value="${smsCountCnfigVO.configType }"/>
        <input type="hidden" name="smsCountCnfigVO.configUnit" value="${smsCountCnfigVO.configUnit }"/>
        <div class="main_title">
            <b>文件、短信重复推送次数配置</b>
        </div>
        
        <div class="main_list">
            <b><span style="color:red">如果输出文件重发次数到达最多重发次数，系统将会自动发通知给管理员</span></b>
        </div>
		<table width="50%" border="0" style="padding:20px">
			<tr>
				<th>文件重发次数：</th>
				<td>
					<select id="count" name="rePushTimeCnfigVO.configValue">
						<option value="0">0</option>
						<option value="1">1</option>
						<option value="2">2</option>
						<option value="3"}>3</option>
						<option value="4"}>4</option>
						<option value="5"}>5</option>
						<option value="6"}>6</option>
						<option value="7"}>7</option>
						<option value="8"}>8</option>
						<option value="9"}>9</option>
						<option value="10"}>10</option>
						<option value="11"}>11</option>
						<option value="12"}>12</option>
						<option value="13"}>13</option>
						<option value="14"}>14</option>
						<option value="15"}>15</option>
						<option value="16"}>16</option>
						<option value="17"}>17</option>
						<option value="18"}>18</option>
						<option value="19"}>19</option>
						<option value="20"}>20</option>
					</select><span>次</span>
				</td>
			</tr>
			<tr>
			<th>短信重复发送次数：</th>
				<td>
					<select id="smsCount" name="smsCountCnfigVO.configValue">
						<option value="0">0</option>
						<option value="1">1</option>
						<option value="2">2</option>
						<option value="3"}>3</option>
						<option value="4"}>4</option>
						<option value="5"}>5</option>
						<option value="6"}>6</option>
						<option value="7"}>7</option>
						<option value="8"}>8</option>
						<option value="9"}>9</option>
						<option value="10"}>10</option>
						<option value="11"}>11</option>
						<option value="12"}>12</option>
						<option value="13"}>13</option>
						<option value="14"}>14</option>
						<option value="15"}>15</option>
						<option value="16"}>16</option>
						<option value="17"}>17</option>
						<option value="18"}>18</option>
						<option value="19"}>19</option>
						<option value="20"}>20</option>
					</select><span>次</span>
				</td>
			</tr>
			<tr align="center">
				<td colspan=2 style="padding-left:20%;">
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['saveRePushCount']!=null}">
						<input name="saveConfigBtn" id="saveConfigBtn" type="button" value="保存" onclick="saveRepushCounr()"/>
					</c:if> 
				</td>
			</tr>
		</table>
	</form>
	</div>
</body>
</html>
