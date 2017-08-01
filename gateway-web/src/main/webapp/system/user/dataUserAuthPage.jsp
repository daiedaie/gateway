<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="g" uri="/gateway-tags" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath %>"/>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>用户权限分配</title>
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script>
//前移
function up(obj) {
	var objParentTR = $(obj).parent().parent();
	var prevTR = objParentTR.prev();
	if (prevTR.prev().length > 0) {
		prevTR.insertAfter(objParentTR);
		var objParentTD = $(objParentTR).find("[name=index]").text();
		$(objParentTR).find("[name=index]").text($(prevTR).find("[name=index]").text());
		$(prevTR).find("[name=index]").text(objParentTD);
	}
} 

//后移
function down(obj) {
	var objParentTR = $(obj).parent().parent();
	var nextTR = objParentTR.next();
	if (nextTR.length > 0) {
		nextTR.insertBefore(objParentTR);
		var objParentTD = $(objParentTR).find("[name=index]").text();
		$(objParentTR).find("[name=index]").text($(nextTR).find("[name=index]").text());
		$(nextTR).find("[name=index]").text(objParentTD);
	} 
 }
 
function saveUserAuth(){
	
	var dataUserSortArray=new Array();
	$("input[name='dataUserSortArray']").each(function(){
		dataUserSortArray.push($(this).val());
	});
	$("#dataUserSortList").val(dataUserSortArray.toString());
	
	$.ajax({
		type: "POST",
		url: "system/user_saveUserAuthInfo.do",
 		data: $("#userAuthForm").serialize(),
		success: function(data){
			if (data.state == 'success') {
				alert("授权成功！");
				//window.location.href = 'system/workPlan_searchByParam.do';调回待办任务列表页
				window.location.href='javascript:history.go(-1);'
			} else {
				alert(data.message);
			}
		}
   	});
}
function back(){
	if($("#source").val()=='1'){
		location.href='system/user_searchUserList.do'
	}else{
		//location.href='system/workPlan_searchByParam.do';调回待办任务列表页
		window.location.href='javascript:history.go(-1);'
	}
}
</script>
</head>
<body class="FrameMain">
<form id="userAuthForm" name="userAuthForm">

<input type="hidden" name="dataUserSortList" id="dataUserSortList"/>
<input type="hidden" name="user.userId" id="user.userId" value="${user.userId}"/>
<input type="hidden" name="user.userType"  value="${user.userType}"/>
<input type="hidden" id="planId" name="planId" value="${planId}"/>
<input type="hidden" id="source"  value="${source}"/>

<div class="main_title">
	<b>用户权限分配</b>
</div>

<div class="main_info_title">用户信息</div>

	<table width="100%" border="0">
        <tr>
        	<td width="10%" align="right">登录账号：</td>
            <td width="23%">${user.loginName}</td>
            <td width="10%" align="right">姓名：</td>
            <td width="23%">${user.userName}</td>
            <td width="10%" align="right">用户类型：</td>
            <td width="23%"><g:sysDict dictCode="DICT_USER_TYPE" dictKey="${user.userType}"/></td>
        </tr>
        <tr>
        	<td width="10%" align="right">身份证号：</td>
            <td width="23%">${user.certNo}</td>
            <td width="10%" align="right">邮箱：</td>
            <td width="23%">${user.email}</td>
            <td width="10%" align="right">联系电话：</td>
            <td width="23%">${user.moblie}</td>
        </tr>
        <tr>
        	<td width="10%" align="right">机构名称：</td>
            <td width="23%">${orgVO.orgName}</td>
            <td width="10%" align="right">联系地址：</td>
            <td width="23%">${user.addr}</td>
            <td></td>
        </tr>
    </table>

<div class="main_info_title">优先级</div>
<div class="main_infomation">
    <div class="main_list">
    <table width="100%" border="0">
      <tr>
        <th width="90">序号</th>
        <th width="220">登录账号</th>
        <th width="220">用户名</th>
        <th width="220">机构名称</th>
        <th>操作</th>
      </tr>
      <c:forEach var="user" items="${dataUserList}" varStatus="vs">
        <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
        <td name="index">${vs.count}</td>
        <td><input name="dataUserSortArray" type="hidden" value="${user.loginName}" />${user.loginName}</td>
        <td>${user.userName}</td>
        <td>${user.orgName}</td>
       <td><a href="javascript:void(0)" onclick="up(this)">↑</a>  <a href="javascript:void(0)" onclick="down(this)">↓</a></td>
      </tr>
      </c:forEach>
      <c:if test="${dataUserList == null || empty dataUserList}">
  	  	<tr>
	  		<td colspan=5>查询不到该机构下数据用户信息</td>
	  	</tr>
  		</c:if>
    </table>
    </div>
</div>


<div class="main_info_title">服务分配</div>
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
	    		<td colspan=5>查询不到用户拥有的服务信息</td>
	  		</tr>
  		</c:if>
    </table>
</div>
</div>

</br>
</br>
<div align="center">
	<input type="button" onclick="saveUserAuth()" value="保存"/>
	<input type="button" onclick="back()" value="返回"/>

</div>
</form>
</body>
</html>
