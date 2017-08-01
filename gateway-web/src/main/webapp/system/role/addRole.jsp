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
<title>群组新增</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script>
function add(){
	if($("#roleName").val()==""){
		alert("请输入群组名称！");
		$("#roleName").focus();
	 	return;
	}
	$("#addBtn,#addingBtn").toggle()
	$.ajax({
		type: "POST",
		url: "system/role_saveRole.do",
		data: $("#addRoleForm").serialize(),
		success: function(data){
			if (data.state == 'success') {
				alert("群组新增成功！");
				window.location.href='system/role_searchRoleList.do';
			} else {
				alert(data.message);
			}
			$("#addBtn,#addingBtn").toggle()
		}
   	});
}
</script>
</head>
<link rel="stylesheet" href="style.css" />
<body class="FrameMain">
<div class="main_title">
	<b>群组新增</b>
	
</div>


<div class="main_form">
<form id="addRoleForm" name="addRoleForm">
<table width="100%" border="0">
    <th>群组名称：</th>
    <td><input id="roleName" name="roleVO.roleName" type="text" /><span class="c_red">*</span></td>
  <tr>
  </tr>
  <tr>
    <th>描述：</th>
    <td><input id="roleDesc" name="roleVO.roleDesc" type="text"  /></td>
  </tr>
 
  <tr>
    <td>&nbsp;</td>
    <td>
    <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['saveRole']!=null}">
    <input name="" type="button" value="新增" id="addBtn" onclick="add()" />
    <input name="loginingBtn" type="button" value="正在增加" id="addingBtn" style="display:none;color:gray" disabled/>
    </c:if>
  </tr>
</table>
 </form>

</div>




</html>
