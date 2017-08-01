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
var serviceMap=${serviceMap};
function addRoleService(role){
	var roleCode=role.value;
	eval("serviceList = serviceMap." + roleCode);
	for(i=0;i<serviceList.length;i++){
		if(role.checked){
			if(!isExistOption(serviceList[i].serviceId)){
				$("#chooseServiceList").append("<option value="+serviceList[i].serviceId+" serviceCode="+serviceList[i].serviceCode+">["+serviceList[i].serviceCode+"] "+serviceList[i].serviceName+"</option>");
				$("#unchooseServiceList option[value="+serviceList[i].serviceId+"]").remove();
			}
		}else{
			if(!isExistService(serviceList[i].serviceId,roleCode)){
				$("#chooseServiceList option[value="+serviceList[i].serviceId+"]").remove();
				$("#unchooseServiceList").append("<option value="+serviceList[i].serviceId+" serviceCode="+serviceList[i].serviceCode+">["+serviceList[i].serviceCode+"] "+serviceList[i].serviceName+"</option>");
			}
		}
    }
}
$(function () { 
 	$("#addService").click(function () { 
 		$("#unchooseServiceList option:selected").appendTo("#chooseServiceList");  
	});  

   $("#addModelService").click(function () { 
 		$("#unchooseServiceList option:selected").appendTo("#chooseServiceList");  
    	$("#unchooseServiceList option").each(function(){
    		if($(this).attr("modelId") == $("#chooseServiceList option:selected").attr("modelId")){
    			$(this).appendTo("#chooseServiceList");
    		}
		});
	});  
	
 	$("#removeService").click(function () { 
 		$("#chooseServiceList option:selected").appendTo("#unchooseServiceList"); 
 	}); 
}); 

function isExistOption(value) {
	return $("#chooseServiceList option[value="+value+"]").length>0;
}  

function isExistUnchooseOption(value) {
	return $("#unchooseServiceList option[value="+value+"]").length>0;
} 

//判断要删除的服务是否存在于其他选中的服务中
function isExistService(serviceId,roleCode) {
	var roleCodeArray=[];
	$("input[name='roleList']:checkbox:checked").each(function(){
		roleCodeArray.push($(this).val());
	});
	var exist = false;
	for(a=0;a<roleCodeArray.length;a++){
		if(roleCode==roleCodeArray[a]) continue;
		eval("roleServiceList = serviceMap." + roleCodeArray[a]);
		if(roleServiceList){
			for(b=0;b<roleServiceList.length;b++){
				if(roleServiceList[b].serviceId==serviceId){
				 exist = true;
				 break;
				}
			}
		}
		if(exist) break;
	}
	return exist;
} 
 
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
 
function queryService(){
  $.ajax({
		type: "POST",
		url: "system/user_searchUnchooseServiceList.do",
 		data: $("#userAuthForm").serialize(),
		success: function(data){
			$("#unchooseServiceList option").remove();
			if (data.state == 'success') {
				for(i=0;i<data.data.servicelist.length;i++){
				    if(!isExistOption(data.data.servicelist[i].serviceId)){
				    $("#unchooseServiceList").append("<option value="+data.data.servicelist[i].serviceId+" modelId="+data.data.servicelist[i].modelId+" serviceCode="+data.data.servicelist[i].serviceCode+">["+data.data.servicelist[i].serviceCode+"] "+data.data.servicelist[i].serviceName+"</option>");
				   }	
				}
				for(i=0;i<data.data.hiddenServiceList.length;i++){
				    if(!isExistOption(data.data.hiddenServiceList[i].serviceId) && isExistUnchooseOption(data.data.hiddenServiceList[i].serviceId)){
				    $("#unchooseServiceList").append("<option  style='display:none' value="+data.data.hiddenServiceList[i].serviceId+" modelId="+data.data.servicelist[i].modelId+" serviceCode="+data.data.hiddenServiceList[i].serviceCode+">["+data.data.hiddenServiceList[i].serviceCode+"] "+data.data.hiddenServiceList[i].serviceName+"</option>");
				   }	
				}
				
			} else {
				alert(data.message);
			}
		}
   	});
}

function saveUserAuth(){
	var orgUserSortArray=new Array();
	$("[name='orgUserSortArray']").each(function(){
		orgUserSortArray.push($(this).val());
	});
	$("#orgUserSortList").val(orgUserSortArray.toString());
	
	var chooseRoleList=new Array();
	$("input[name='roleList']:checkbox:checked").each(function(){
		chooseRoleList.push($(this).val());
	});
	$("#chooseRoles").val(chooseRoleList.toString());
	
	var chooseServiceList = new Array();
	var chooseServiceCodeList=new Array();
	$("#chooseServiceList option").each(function(){
		chooseServiceList.push($(this).val());
		chooseServiceCodeList.push($(this).attr("serviceCode"));
	});
	$("#chooseServices").val(chooseServiceList.toString());
	$("#chooseServiceCodes").val(chooseServiceCodeList.toString());
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
		location.href='system/user_searchUserList.do';
	}else{
		//location.href='system/workPlan_searchByParam.do';调回待办任务列表页
		window.location.href='javascript:history.go(-1);'
	}
}
</script>
</head>
<body class="FrameMain">
<form id="userAuthForm" name="userAuthForm">

<input type="hidden" name="chooseRoles" id="chooseRoles"/>
<input type="hidden" name="chooseFuncs" id="chooseFuncs"/>
<input type="hidden" name="chooseButtons" id="chooseButtons"/>
<input type="hidden" name="chooseServices" id="chooseServices"/>
<input type="hidden" name="chooseServiceCodes" id="chooseServiceCodes"/>
<input type="hidden" name="orgUserSortList" id="orgUserSortList"/>
<input type="hidden" name="user.userId" id="user.userId" value="${user.userId}"/>
<input type="hidden" name="user.userType"  value="${user.userType}"/>
<input type="hidden" id="source"  value="${source}"/>

<input type="hidden" id="planId" name="planId" value="${planId}"/>

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
      <c:forEach var="user" items="${orgUserList}" varStatus="vs">
        <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
        <td name="index">${vs.count}</td>
        <td><input name="orgUserSortArray" type="hidden" value="${user.loginName}" />${user.loginName}</td>
        <td>${user.userName}</td>
        <td>${user.orgName}</td>
        <td><a href="javascript:void(0)" onclick="up(this)">↑</a>  <a href="javascript:void(0)" onclick="down(this)">↓</a></td>
      </tr>
      </c:forEach>
    </table>
    </div>
</div>

<div class="main_info_title">群组分配</div>
<div class="main_infomation">
    <div class="main_list">
    <table width="100%" border="0">
      <tr>
        <th width="90"></th>
        <th width="220">群组编码</th>
        <th width="220">群组名称</th>
        <th>描述</th>
      </tr>
      <c:forEach var="role" items="${roleList}" varStatus="vs">
        <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
        <td><input name="roleList" type="checkbox" onclick="addRoleService(this)" value="${role.roleCode}" ${role.userId !=0 ? "checked" : ""} /></td>
        <td>${role.roleCode}</td>
        <td>${role.roleName}</td>
        <td>${role.roleDesc}</td>
      </tr>
      </c:forEach>
    </table>
    </div>
</div>

<div class="main_info_title">服务分配</div>
<div class="main_infomation">
    <div class="main_search">
        <p>
        服务编码：<input name="serviceVO.serviceCode" type="text" />
        服务名称：<input name="serviceVO.serviceName" type="text" />
        <input name="" type="button" value="查询" onclick="queryService()"/>
        </p>
    </div>
    
    <div class="main_empower">
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="40%">
            <div class="em_title">请选择服务</div>
            <div class="em_content">
            <select size="2" multiple="" name="unchooseServiceList" id="unchooseServiceList">
              <c:forEach var="service" items="${unchooseServiceList}" varStatus="vs">
              <option value="${service.serviceId}" modelId="${service.modelId}" serviceCode="${service.serviceCode }">[${service.serviceCode}] ${service.serviceName}</option>
              </c:forEach>
            </select>
            </div>
        </td>
        <td width="20%">
            <div class="em_center">
            	<input name=""  id="addService" type="button" value="授权>>" /><br /><br />
            	<input name=""  id="addModelService" type="button" value="批量授权>>" title="授权同一模型下所有服务"/><br /><br />
                <input name="" id="removeService" type="button" value="<<取消" />
            </div>
        </td>
        <td width="40%">
            <div class="em_title">已选择服务</div>
            <div class="em_content">
            <select size="2" multiple="" name="chooseServiceList" id="chooseServiceList">
            
              <c:forEach var="service" items="${chooseServiceList}" varStatus="vs">
               <option value="${service.serviceId}" modelId="${service.modelId }" serviceCode="${service.serviceCode }">[${service.serviceCode}] ${service.serviceName}</option>
              </c:forEach>
            </select>
            </div>
        </td>
      </tr>
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
