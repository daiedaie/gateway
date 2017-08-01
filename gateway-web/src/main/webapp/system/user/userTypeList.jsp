<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath%>" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>用户查询_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
</head>
<body class="FrameMain" id="FrameMain">
<div class="main_title">
	<b>用户菜单管理</b>
</div>
	
	<div id="pageDataList">
	<div class="main_list" id="main_list">
		<table width="100%" border="0">
			<tr>
				<th>序号</th>
				<th>用户类型</th>
				<th>操作</th>
			</tr>

			<c:forEach var="userType" items="${userTypeMap}" varStatus="vs">
				<tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
					<td>${vs.count}</td>
					<td><g:sysDict dictCode="DICT_USER_TYPE" dictKey="${userType.value.dictKey}"/></td>
					<td>
						<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['userTypeFuncDetail']!=null}">
							<a href="system/user_searchUserTypeFuncDetail.do?user.userType=${userType.value.dictKey}">明细</a>  |  
						</c:if>
						<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['userTypeFuncAuth']!=null}">
							<a href="system/user_searchUserTypeFuncPage.do?user.userType=${userType.value.dictKey}">菜单分配</a>
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</table>
	</div>
	</div>
</body>
</html>
