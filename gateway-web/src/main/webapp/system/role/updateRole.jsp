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
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>群组修改</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script>
function updateRole(){
	if($("#roleName").val()==""){
		alert("请输入群组名称！");
		$("#roleName").focus();
	 	return;
	}
	if($("#roleVOName").val()==$("#roleName").val() && $("#roleVODesc").val()==$("#roleDesc").val()){
		alert("请修改信息！");
		return;
	}
	$.ajax({
		type: "POST",
		url: "system/role_updateRole.do",
		data: $("#updateRoleForm").serialize(),
		success: function(data){
			if (data.state == 'success') {
				alert(data.message);
				window.location.href='system/role_searchRoleList.do';
			} else {
				alert(data.message);
			} 
		}
   	});
}
</script>
</head>
<link rel="stylesheet" href="style.css" />
<body class="FrameMain">
<div class="main_title">
	<b>群组修改</b>
	
</div>

<div class="main_form">
<form id="updateRoleForm" name="updateRoleForm">
<table width="100%" border="0">
 
  <tr>
    <th>群组编码：</th>
    <td>
    <input id="roleCode" name="roleVO.roleCode" type="text" value="${role.roleCode}" onfocus="this.blur()"style="border:0"/>
    </td>
  </tr>
  <tr>
    <th>群组名称：</th>
    <td>
    <input id="roleName" name="roleVO.roleName" type="text" value="${role.roleName}"  /><span class="c_red">*</span>
    <input id="roleVOName"  type="hidden" value="${role.roleName}"  />
    <input type="hidden" name="roleVO.status" type="text" value="${role.status}"  />
    <input type="hidden" name="roleVO.createTime" type="text" value="${role.createTime}"  />
    <input type="hidden" name="roleVO.creator" type="text" value="${role.creator}"  />
    </td>
  </tr>
  <tr>
    <th>描述：</th>
    <td>
    <input id="roleDesc" name="roleVO.roleDesc" type="text" value="${role.roleDesc}"  />
    <input id="roleVODesc"  type="hidden" value="${role.roleDesc}"  />
    </td>
  </tr>
 
  <tr>
    <td>&nbsp;</td>
    <td><input name="" type="button" value="修改" onclick="updateRole()" />
    <input name="" type="button" value="返回" onclick="history.go(-1)" />
    </td>
    
    
  </tr>
</table>
 </form>

</div>




</html>
