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
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>实时服务数据获取</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
</head>
<body class="FrameMain">

<div class="main_title">
	<b>实时服务数据获取</b>
</div>
	<div class="main_infomation">
	<div class="main_list">
	<table width="100%" border="0">
		<!-- 元数据 -->
		<tr>
		<c:forEach var="fieldCodeMap" items="${dataMap['fieldCodeMap']}" >
		<%-- <td>${fieldCodeMap.key }</td> --%>
			<th>${columnMap[fieldCodeMap.key]}</th>
		</c:forEach>
		</tr>
		
		<!--数据  -->
		<c:forEach var="fieldValueList" items="${dataMap['fieldValueList']}" >
			<tr>
				<c:forEach var="fieldValue" items="${fieldValueList}">
					<td>${fieldValue}</td>
				</c:forEach>
			</tr>
		</c:forEach>
		
	</table>
	</div>
	</div>
</body>
</html>
