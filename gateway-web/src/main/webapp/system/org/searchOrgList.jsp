<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
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
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>机构查看_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
</head>
<body class="FrameMain">
<form id="searchOrgListForm" name="searchOrgListForm" action="system/org_searchOrgList.do" loadContainer="pageDataList">
	<div class="main_title">
		<b>机构查看</b>
    </div>
    <div class="main_search">
		<p>
				机构名称：<input name="orgVO.orgName" type="text" /> 
				<!-- 证件类型：
				<select	name="orgVO.certType">
						<option value="">请选择</option>
						<option value="0">身份证</option>
						<option value="1">军人证</option>
						<option value="2">护照</option>
				</select>  -->
				证件编号：<input name="orgVO.certNo" type="text" /> 
				法人名称：<input name="orgVO.orgHeadName" type="text" /> 
				<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['findOrg']!=null}">
					<input name="searchRoleList" type="button" value="查询" onclick="searchPage()" />
				</c:if>
		</p>
	</div>
	<div id="pageDataList">
	<div class="main_list" id="main_list">
	<table width="100%" border="0">
		<tr>
			<th>序号</th>
			<th><a class="tableSort" sort="login_name">机构用户</a></th>
			<th><a class="tableSort" sort="org_name">机构名称</a></th>
			<th><a class="tableSort" sort="confirm_status">审核状态</a></th>
			<th><a class="tableSort" sort="cert_no">证件编号</a></th>
			<th><a class="tableSort" sort="org_head_name">法人名称</a></th>
			<th>操作</th>
		</tr>

		<c:forEach var="org" items="${pageObject.data}" varStatus="status">
			<tr ${status.index % 2 == 1? "class='list_bg'" : "" }>
				<td>${(pageObject.curPage-1)*pageObject.pageSize+status.count}</td>
				<td>${org.loginName}</td>
				<td>${org.orgName}</td>
				<td><g:sysDict dictCode="DICT_AUDIT_STATE" dictKey="${org.confirmStatus}"/></td>
				<td>${org.certNo}</td>
				<td>${org.orgHeadName}</td>
				<td>
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['detailOrg']!=null}">
						<a href="system/org_searchOrgDetail.do?orgVO.orgId=${org.orgId}&sourceFlag=2">明细</a> | 
					</c:if>
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['updateOrg']!=null}">
						<c:choose>
					 	<c:when test="${updateMap[org.userId]==null}">
							<a href="system/org_updateOrgPage.do?orgVO.orgId=${org.orgId}">修改</a>
					 	</c:when>
					 	<c:otherwise>
					 		修改待审
					 	</c:otherwise>
					 	</c:choose>
					</c:if>
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['allotPermissionUserOrg']!=null && org.confirmStatus=='2'}">
					 	<a href="system/user_userAuthPage.do?user.userId=${org.userId}" >分配权限</a> |
					 </c:if>
				</td>
			</tr>
		</c:forEach>
		<c:if test="${pageObject.data == null || empty pageObject.data}">
  	  			<tr>
	    			<td colspan=7>查询不到机构信息</td>
	  			</tr>
  		</c:if>
	</table>
	</div>
	<g:page pageObject="${pageObject }" />
	</div>
	</form>
</body>
</html>
