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
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript">
	function cancelUser(userId,loginName,userType){
		if(userType=='orgUser'){
			if(!confirm(loginName+"是机构用户，注销机构用户会将该机构下的所有数据用户也一起注销，确定注销?"))
				return false;
		}else if(!confirm("确定将"+loginName+"用户注销?")){
			return false;
		}
		
		url = "system/user_cancelUser.do?userId="+userId;
		$.post(url,null,function(json){
			alert(json.message);
			if(json.state=='success'){
				searchPage($("#curPage").val());
			}
		});
	}
	
	function updateOnlineStatus(userId,onlineStatus,userType){
	   url = "system/user_upateOnlineStatus.do?userId="+userId+"&onlineStatus="+onlineStatus+"&userType="+userType;
	   $.post(url,null,function(json){
			alert(json.message);
			if(json.state=='success'){
				searchPage($("#curPage").val());
			}
		});
	}
</script>
</head>
<body class="FrameMain">
<form id="findUserListForm" name="findUserListForm" action="system/user_searchUserList.do" loadContainer="pageDataList">
<div class="main_title">
	<b>用户查看</b>
</div>
	<div class="main_search">
		<p>
			登录帐号：<input name="user.loginName" type="text" /> 
			姓名：<input name="user.userName" type="text" /> 
			状态：<select	name="user.confirmStatus">
							<option value="">请选择</option>
							<option value="1">待审核</option>
							<option value="2">审核通过</option>
							<option value="0">审核不通过</option>
						</select>
			<c:if test="${ userVO.userType!='orgUser'}">
				用户类型：
				<select	name="user.userType">
					<option value="">请选择</option>
					<option value="dataUser">数据用户</option>
					<option value="orgUser">机构用户</option>
					<option value="mainateUser">运维人员</option>
					<option value="auditUser">审核人员</option>
					<option value="safeUser">数据安全管理员</option>
				</select>
				机构名称：<input name="org.orgName" type="text" /> 
			</c:if>
			<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['findUser']!=null}"> 
			<input name="findUserList" type="button" value="查询" onclick="searchPage()" />
			</c:if>
		</p>
	</div>
	<div id="pageDataList">
	<div class="main_list" id="main_list">
		<table width="100%" border="0">
			<tr>
				<th>序号</th>
				<th><a class="tableSort" sort="login_name">登录账号</a></th>
				<th><a class="tableSort" sort="user_name">姓名</a></th>
				<th><a class="tableSort" sort="confirm_status">审核状态</a></th>
				<th><a class="tableSort" sort="user_type">用户类型</a></th>
				<th><a class="tableSort" sort="org_name">机构名称</a></th>
				<th><a class="tableSort" sort="online_status">值班状态</a></th>
				<th>操作</th>
			</tr>

			<c:forEach var="user" items="${userList}" varStatus="vs">
				<tr>
					<td>${(pageObject.curPage-1)*pageObject.pageSize+vs.count}</td>
					<td>${user.loginName}</td>
					<td>${user.userName}</td>
					<td><g:sysDict dictCode="DICT_AUDIT_STATE" dictKey="${user.confirmStatus }"/></td>
					<td><g:sysDict dictCode="DICT_USER_TYPE" dictKey="${user.userType }"/></td>
					<td>${user.orgName}</td>
					<td>
					  <c:if test="${user.userType == 'mainateUser' || user.userType == 'safeUser' || user.userType == 'auditUser'}">
					  	 <a href="javascript:void(0)" onclick="updateOnlineStatus('${user.userId }','${user.onlineStatus }','${user.userType }')">
					  	 <g:sysDict dictCode="DICT_ONLINE_STATE" dictKey="${user.onlineStatus }"/></a>
					  </c:if>
					</td>						
					<td>
				 <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['updateUser']!=null}">
				 <c:choose>
						<c:when test="${updateMap[user.userId] == null }">
						<c:if test="${user.userType=='dataUser' }">
							<a href="system/user_searchSinglerUser.do?user.userId=${user.userId}">修改</a>|
						</c:if>
						<c:if test="${user.userType=='orgUser' }">
							<a href="system/org_updateOrgPage.do?orgVO.orgId=${user.orgId}">修改</a>|
						</c:if>
						</c:when>
						<c:otherwise>
						 		修改待审
						</c:otherwise>
				 </c:choose>
				 </c:if>
				 		 <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['detailUser']!=null}">
						 <a href="system/user_searchUserDetail.do?user.userId=${user.userId}&sourceFlag=2">明细</a>|
						 </c:if>
						 						
						 <c:if test ="${user.confirmStatus=='2' }">
							 <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['allotPermissionUser']!=null && (user.userType=='orgUser'||user.userType=='dataUser')}">
							 	<a href="system/user_userAuthPage.do?user.userId=${user.userId}&source=1" >分配权限</a> |
							 </c:if>
							 <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['searchServiceList']!=null && user.userType=='dataUser'}">
							 	<a href="service/modelDataApp_getServiceAppList.do?userModelServiceAppVO.loginName=${user.loginName}" >查看服务</a> |
							 </c:if>
							 <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['ruleUser']!=null && user.userType=='dataUser'}">
						 		<a href="system/desenModel_searchDesenServiceFieldList.do?loginName=${user.loginName }">字段脱敏</a> |
							 </c:if>
							 <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['serviceInfoDesen']!=null && user.userType=='dataUser'}">
						 		<a href="system/desenModel_searchDesenServiceInfoList.do?loginName=${user.loginName }">信息脱敏</a> |
							 </c:if>
						</c:if>
						<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['cancelUser']!=null}">
						<c:if test="${user.confirmStatus=='2' }">
						 <c:choose>
						 	<c:when test="${cancelMap[user.userId] == null }">
								<a href="javascript:void(0)" onclick="cancelUser('${user.userId }','${user.loginName }','${user.userType }')">注销</a>
						 	</c:when>
						 	<c:otherwise>
						 		注销待审
						 	</c:otherwise>
						 </c:choose>
						</c:if>
						</c:if>
					</td>
				</tr>
			</c:forEach>
			<c:if test="${pageObject.data == null || empty pageObject.data}">
		  	    <tr>
			      <td colspan=7>查询不到用户信息</td>
			    </tr>
		    </c:if>
		</table>
	</div>
	<g:page pageObject="${pageObject }" />
	</div>
</form>
</body>
</html>
