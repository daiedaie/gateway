<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
<title>详细页</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
</head>


<body class="FrameMain">

<div class="main_title">
	<b>用户类型菜单明细</b>
</div>

<div class="main_search">
	<p>用户类型：<g:sysDict dictCode="DICT_USER_TYPE" dictKey="${user.userType}"/></p>
</div>

<div class="main_info_title">拥有菜单</div>
<div class="main_infomation">
    <div class="main_list">
    <table width="100%" border="0">
      <tr>
        <th width="90"></th>
        <th width="150">菜单编码</th>
        <th width="150">菜单名称</th>
        <th width="180">菜单模块</th>
        <th class="font_l">页面操作按钮</th>
      </tr>
       <c:forEach var="view" items="${funcAndBtnList}" varStatus="vs">
       	 <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
        	<td>
        		<input type="checkbox" funcCode="${view.funcCode}" name="funcList"  value="${view.funcCode}" ${view.userType !=""  ? "checked" : ""} disabled/>
        	</td>
        	<td>${view.funcCode}</td>
       	 	<td>${view.funcName}</td>
       	 	<td>${view.parentName}</td>
        	<td class="font_l" name="td_${view.funcCode}">
        		 <c:forEach var="button" items="${view.buttonList}" varStatus="vs">
        		 	<input name="buttonList" type="checkbox" funcCode="${view.funcCode}"  value="${button.buttonCode}" ${button.userType !=""  ? "checked" : ""} disabled/>
        	   		${button.operateDesc}
        		 </c:forEach>
        	</td>
      	 </tr>
       </c:forEach>
    </table>
    </div>
</div>

</body>
</html>
