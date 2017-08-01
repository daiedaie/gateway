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
function pass(){
	var form=$("#verifyForm");
	document.getElementById("passTag").value="2";
	form.submit();
}
</script>
<title>过期数据清理</title>
</head>
  <body class="FrameMain">
	
	<div class="main_title">
		<b>过期数据清理</b>
	</div>
	<form  id="verifyForm" method="post" action="system/workPlan_cleanData.do">
	<table width="100%" border="0">
		  <tr>
		  		<th>创建时间：</th>
		  		<td>${workPlan.createTime}</td>
		  </tr>
		  <tr>
		  		<th>待办标题：</th>
		  		<td>${workPlan.planTitle}</td>
		  </tr>
		  <tr>
		  		<th>待办内容：</th>
		  		<td>${workPlan.planContent}</td>
		  </tr>
	</table>
    <input type="hidden" id="planId" name="planId" value="${workPlan.planId}"/>
    <input type="hidden" id="passTag" name="passTag" />
    <div style="margin:20px 0px 0px 350px">
		<input type="button" value="立即处理" onclick="pass()"/>
		<input type="button" value="取消" onclick="history.go(-1)"/>
	</div>
	 <div class="main_title">
			<b>过期文件明细</b>
    	</div>
    	<div class="main_list">
    	<table width="100%" border="0">
				<tr>
					<th>序号</th>
					<th>文件名称</th>
					<th>文件路径</th>
					<th>文件创建时间</th>
					<th>文件创建人</th>
				</tr>
				<c:forEach var="file" items="${files}" varStatus="status">
				<tr>
					<td>${status.count}</td>
					<td>${file.fileName}</td>
					<td>${file.filePath}</td>
					<td><fmt:formatDate value="${file.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td>${file.createUser}</td>
				</tr>
				</c:forEach>
		</table>
		</div>
	</form>
  </body>
</html>
