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
<title>修改定时器_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">	
$(function(){
	$("#selectHour option[value=${hour}]").attr("selected",true);
	$("#selectMinute option[value=${minute}]").attr("selected",true);
	$("#rePushTimeUnit option[value=${rePushTimeCnfigVO.configUnit}]").attr("selected",true);
	$("#taskDesenTimeUnit option[value=${taskCheckTimeCnfigVO.configUnit}]").attr("selected",true);
	$("#sourceFileTimeUnit option[value=${fileScanTimeCnfigVO.configUnit}]").attr("selected",true);
}) 
function saveTimer(){
	if($("#selectHour option:selected").val()=='' || $("#selectMinute option:selected").val()==''){
		alert("请选择定时器修改时间(包括小时，分钟)！");
		return false;
	}
	$("#saveTimerBtn").attr("disabled",true);
	url = "system/config_updateJobTimer.do";
   	$.post(url,$("#timerForm").serialize(),function(json){
   		$("#saveTimerBtn").attr("disabled",false);
		if (json.state == 'success') {
			alert("保存成功");
		}else{
			alert("保存失败，原因："+json.message);
		}
   	});
}

</script>
</head>	
<body>
	<div class="main_form" method="post" align="left">
	<form id="timerForm" name="timerForm">
		<input type="hidden" name="rePushTimeCnfigVO.configId" value="${rePushTimeCnfigVO.configId }"/>
		<input type="hidden" name="rePushTimeCnfigVO.configType" value="${rePushTimeCnfigVO.configType }"/>
		<input type="hidden" name="taskCheckTimeCnfigVO.configId" value="${taskCheckTimeCnfigVO.configId }"/>
		<input type="hidden" name="taskCheckTimeCnfigVO.configType" value="${taskCheckTimeCnfigVO.configType }"/>
		<input type="hidden" name="fileScanTimeCnfigVO.configId" value="${fileScanTimeCnfigVO.configId }"/>
		<input type="hidden" name="fileScanTimeCnfigVO.configType" value="${fileScanTimeCnfigVO.configType }"/>
		
        <div class="main_title">
            <b>定时器修改</b>
        </div>
		<table width="50%" border="0" style="padding:20px">
			<tr>
				<td style="width:25%;text-align:right">下次执行时间：</td>
				<td>${nextDate}</td>
			</tr>
			<tr>
				<td style="width:25%;text-align:right">定时器：</td>
				<td>
					<select id="selectHour" name="hourStr">
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
						<option value="21"}>21</option>
						<option value="22"}>22</option>
						<option value="23"}>23</option>
					</select><span>时</span>
					<select id="selectMinute" name="minuteStr">
						<option value="">请选择</option>
						<option value="0"}>0</option>
						<option value="10"}>10</option>
						<option value="20"}>20</option>
						<option value="30"}>30</option>
						<option value="40"}>40</option>
						<option value="50"}>50</option>
					</select><span>分</span>
				</td>
			</tr>
			
			<tr>
			<td style="width:30%;text-align:right">重新推送时间间隔：</td>
			<td>
			<select id="rePushTimeUnit" name="rePushTimeCnfigVO.configUnit">
						<option value="2"}>秒</option>
						<option value="1"}>分钟</option>
			</select>
			<input type="text" name="rePushTimeCnfigVO.configValue" style="width:20%;height:20px;"value="${rePushTimeCnfigVO.configValue }"/>
			</td>
			</tr>
			
			<tr>
			<td style="width:30%;text-align:right">任务脱敏/检查时间间隔：</td>
			<td>
			<select id="taskDesenTimeUnit" name="taskCheckTimeCnfigVO.configUnit">
						<option value="2"}>秒</option>
						<option value="1"}>分钟</option>
			</select>
			<input type="text" name="taskCheckTimeCnfigVO.configValue" style="width:20%;height:20px;"value="${taskCheckTimeCnfigVO.configValue }"/>
			</td>
			</tr>
			
			<tr>
			<td style="width:30%;text-align:right">源文件扫描时间间隔：</td>
			<td>
			<select id="sourceFileTimeUnit" name="fileScanTimeCnfigVO.configUnit">
						<option value="2"}>秒</option>
						<option value="1"}>分钟</option>
			</select>
			<input type="text" name="fileScanTimeCnfigVO.configValue" style="width:20%;height:20px;" value="${fileScanTimeCnfigVO.configValue}"/>
			</td>
			</tr>
			
			<tr>
				<td colspan=2 style="padding-left:20%">
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['saveTimeConfig']!=null}">
						<input name="saveTimerBtn" id="saveTimerBtn" type="button" value="保存" onclick="saveTimer()"/>
					</c:if> 
				</td>
			</tr>
		</table>
	</form>
	</div>
</body>
</html>
