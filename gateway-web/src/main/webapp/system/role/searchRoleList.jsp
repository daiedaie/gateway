<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
<title>群组查询_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script>
function deleteRole(roleCode){
	if(confirm("确定要删除该群组吗？")){
		$.ajax({
			type: "POST",
			url: "system/role_deleteRole.do",
			data: "roleCode="+roleCode,
			success: function(data){
				if (data.state == 'success') {
					alert(data.message);
					searchPage($("#curPage").size());
				} else {
					alert(data.message);
				}
			}
   		});
	}
}
</script>
</head>
<body class="FrameMain" id="FrameMain">
<form id="searchRoleListForm" name="searchRoleListForm" action="system/role_searchRoleList.do" loadContainer="pageDataList">
	<div class="main_title">
		<b>群组查看</b>
    </div>
	<div class="main_search">
		<p>
			群组编码：<input name="roleVO.roleCode" type="text" />
			群组名称：<input name="roleVO.roleName" type="text" /> 
			<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['findRole']!=null}">
				<input name="searchRoleList" type="button" value="查询" onclick="searchPage()" />
			</c:if>	
		</p>
	</div>

	<div id="pageDataList">
	<div class="main_list" id="main_list">
			<table width="100%" border="0">
				<tr>
					<th>序号</th>
					<th><a class="tableSort" sort="roleCode">群组编码</a></th>
					<th><a class="tableSort" sort="roleName">群组名称</a></th>
					<th><a class="tableSort" sort="roleDesc">群组描述</a></th>
					<th>操作</th>
				</tr>

				<c:forEach var="role" items="${pageObject.data}" varStatus="status">
					<tr ${status.index % 2 == 1? "class='list_bg'" : "" }>
						<td>${(pageObject.curPage-1)*pageObject.pageSize+status.count}</td>
						<td>${role.roleCode}</td>
						<td>${role.roleName}</td>
						<td>${role.roleDesc}</td>
						<td>
							<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['updateRole']!=null}">
							<a href="system/role_searchRoleByRoleCode.do?roleVO.roleCode=${role.roleCode}">修改</a> | 
							</c:if>
							<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['deleteRole']!=null}">
							<a href="javascript:deleteRole('${role.roleCode}')" >删除</a> | 
							</c:if>
						  	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['detailRole']!=null}">
							<a href="system/role_searchRoleDetail.do?roleVO.roleCode=${role.roleCode}">明细</a> |
							</c:if> 
							<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['allotRole']!=null}">
							<a href="system/role_roleAuth.do?roleVO.roleCode=${role.roleCode}">服务分配</a>
							</c:if>
						</td>
					</tr>
				</c:forEach>
				
				<c:if test="${pageObject.data == null || empty pageObject.data}">
  	  				<tr>
	    				<td colspan=5>查询不到群组信息</td>
	  				</tr>
  				</c:if>
			</table>
	</div>
	<g:page pageObject="${pageObject }" />
	</div>
</form>
</body>
</html>
