<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/common/include.jsp"%>
<jsp:directive.page import="com.gztydic.gateway.core.common.constant.WorkPlanConstent"/>

<%@ taglib prefix="g" uri="/gateway-tags" %>
<%@ page isELIgnored="false" %>
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
<title>用户审核列表_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript">
	function openWorkPlanWindow(planId,planType,userId,extenTableKey){
		var url = null;
        if(<%=WorkPlanConstent.REGISTE_AUDIT%>==planType){
            if(userId != null){
              location.href="system/workPlan_signUpVerify.do?workPlanId="+planId+"&userId="+userId;
            }
        }else if(<%=WorkPlanConstent.UPDATE_AUDIT%>==planType){
        	location.href="system/workPlan_searchUpdateUserVerify.do?planId="+planId;
        }else if(<%=WorkPlanConstent.CANCEL_AUDIT%>==planType){
        	location.href = "system/workPlan_searchCancelUserVerify.do?planId="+planId+"&userId="+extenTableKey;
        }else if(<%=WorkPlanConstent.FIELD_DESEN_CONF_AUDIT%>==planType){
        	location.href="<%=basePath%>service/modelDataApp_searchVerify.do?workPlanId="+planId;
        }else if(<%=WorkPlanConstent.INFO_DESEN_CONF_AUDIT%>==planType){
        	location.href="system/workPlan_searchServiceInfoVerify.do?planId="+planId;
        }
	}
	
	$(function(){
		$("#userType").change(function(){
			if($("#userType").val()=='safeUser'){
				$("#orgNameInput").hide();
			}else{
				$("#orgNameInput").show();
			}
		});
		
	})
</script>
</head>
<body class="FrameMain">
<form id="searchUserAuditListForm" name="searchUserAuditListForm" action="system/workPlan_findUserAuditByParam.do" loadContainer="pageDataList">
	<div class="main_title">
		<b>用户审核列表</b>
    </div>
	<div class="main_search">
		<p>
			登录账号：<input name="userVO.loginName" type="text" />
			姓名：<input name="userVO.userName" type="text" /> 
			用户类型:
			<select name="userVO.userType" id="userType">
				<option value="">请选择</option>
				<option value="orgUser">机构用户</option>
				<option value="dataUser">数据用户</option>
				<option value="safeUser">数据安全管理员</option>
			</select>
			<span id="orgNameInput">
			机构名称：<input name="userVO.orgName" type="text" />
			</span>
			<input name="searchUserAuditList" type="button" value="查询" onclick="searchPage()" />
		</p>
	</div>

	<div id="pageDataList">
	<div class="main_list" id="main_list">
			<table width="100%" border="0">
				<tr>
					<th>序号</th>
					<th><a class="tableSort" sort="loginName">登录账号</a></th>
					<th><a class="tableSort" sort="userName">姓名</a></th>
					<th><a class="tableSort" sort="userType">用户类型</a></th>
					<th><a class="tableSort" sort="orgName">机构名称</a></th>
					<th><a class="tableSort" sort="planSatus">状态</a></th>
					<th>操作</th>
				</tr>

				<c:forEach var="userAudit" items="${pageObject.data}" varStatus="status">
					<tr ${status.index % 2 == 1? "class='list_bg'" : "" }>
						<td>${(pageObject.curPage-1)*pageObject.pageSize+status.count}</td>
						<td>${userAudit.loginName}</td>
						<td>${userAudit.userName}</td>
						<td><g:sysDict dictCode="DICT_USER_TYPE" dictKey="${userAudit.userType}"/></td>
						<td>${userAudit.orgName}</td>
						<td><g:sysDict dictCode="DICT_PLAN_STATE" dictKey="${userAudit.planState}"/></td>
						<c:choose>
						<c:when test="${userAudit.userType == 'safeUser'}">
							<td></td>
						</c:when>
						<c:otherwise>
							<td><a href="javascript:openWorkPlanWindow('${userAudit.planId}','${userAudit.planType}','${userAudit.userId}','${userAudit.extenTableKey}')">
						<g:sysDict dictCode="DICT_PLAN_TYPE" dictKey="${userAudit.planType}"/></a></td>
						</c:otherwise>
						</c:choose>
						
					</tr>
				</c:forEach>
				
				<c:if test="${pageObject.data == null || empty pageObject.data}">
  	  				<tr>
	    			<td colspan=7>查询不到用户审核信息</td>
	  			</tr>
  				</c:if>
			</table>
			
	</div>
	<g:page pageObject="${pageObject }" />
	</div>
</form>
</body>
</html>
