<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath %>"/>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script>
function endWorkPlan(){
	var form=$("#verifyForm");
	form.submit();
}
</script>
<title>系统后台错误</title>
</head>
  <body class="FrameMain">
	
	<div class="main_title">
		<b>系统后台错误</b>
	</div>
	<form id="verifyForm" name="verifyForm" action="system/workPlan_endPlanStatus.do">
	<table width="100%" border="0">
		  <tr>
		  		<td align="right">待办标题：</td>
		  		<td>${workPlan.planTitle}</td>
		  </tr>
		  <tr>
		  		<td align="right">待办内容：</td>
		  		<td>操作人：${operUser.loginName                 }<br/>
		  			操作功能：${operFun}<br/>
		  			错误信息：${workPlan.planContent}</td>
		  </tr>
	</table>
    <input type="hidden" id="planId" name="planId" value="${workPlan.planId}"/>
    <div style="margin:20px 0px 0px 300px">
		<input type="button" value="确定" onclick="endWorkPlan()"/>
	</div>
	</form>
  </body>
</html>
