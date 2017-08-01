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
	<b>群组明细</b>
</div>

<div class="main_info_title">群组信息</div>
<div class="main_infomation">
	<table width="100%" border="0">
        <tr>
            <td>群组编码：${roleVO.roleCode}</td>
            <td>群组名称：${roleVO.roleName}</td>
        </tr>
        <tr>
            <td>描述：${roleVO.roleDesc}</td>
        </tr>
    </table>
</div>

<div class="main_info_title">拥有服务</div>
<div class="main_infomation">
<div class="main_list">
	<table width="100%" border="0">
        <tr>
            <th>服务编码</th>
            <th>服务名称</th>
            <th>服务类型</th>
            <th>查看</th>
        </tr>
        <c:forEach var="serviceView" items="${serviceList}" varStatus="vs">
        <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
            <td>${serviceView.serviceCode}</td>
            <td>${serviceView.serviceName}</td>
            <td><g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${serviceView.serviceType}"/></td>
            <td>
            <a href="service/serviceInfo_searchService.do?serviceView.serviceId=${serviceView.serviceId}" target="_blank">查看</a>
            </td>
        </tr>
        </c:forEach>
        <c:if test="${serviceList == null || empty serviceList}">
  	  		<tr>
	    		<td colspan=5>查询不到群组拥有的服务信息</td>
	  		</tr>
  		</c:if>
    </table>
</div>
</div>

<%-- <div class="main_info_title">拥有菜单</div>
<div class="main_infomation">
<div class="main_list">
	<table width="100%" border="0">
        <tr>
            <td>菜单编码</td>
            <td>菜单名称</td>
            <td>菜单模块</td>
            <td>描述</td>
            <td>页面操作按钮</td>
        </tr>
        <c:forEach var="funcAndBtnView" items="${funcAndButtonViewList}" varStatus="vs">
        <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
            <td>${funcAndBtnView.funcCode}</td>
            <td>${funcAndBtnView.funcName}</td>
            <td>${funcAndBtnView.parentName}</td>
            <td>${funcAndBtnView.funcDesc}</td>
            <td>
             <c:forEach var="button" items="${funcAndBtnView.buttonVOList}" varStatus="vs">
             ${button.operateDesc}
             </c:forEach>
            </td>
        </tr>
        </c:forEach>
    </table>
</div>
</div>
 --%>
</br>
</br>
<div align="center">
	<input type="button" onclick="history.go(-1)" value="返回"/>
</div>
</body>
</html>
