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
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>修改密码_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">

	
/**验证*/
function savePassword() {
	
	if($("input[name='oldPwd']").val()==""){
		alert("请输入原始密码");
		$("input[name='oldPwd']").focus();
	 	return;
	}
	if($("input[name='newPwd']").val()==""){
		alert("请输入新密码");
		$("input[name='newPwd']").focus();
	 	return;
	}
	if($("input[name='reNewPwd']").val()==""){
		alert("请输入确认密码");
		$("input[name='reNewPwd']").focus();
	 	return;
	}
	if($("input[name='newPwd']").val() != $("input[name='reNewPwd']").val()){
		alert("输入密码不一致");
		$("input[name='newPwd']").focus();
	 	return;
	}
	
	$.ajax({
		type: "POST",
		url: "password.do",
		data: $("#pwdForm").serialize(),
		datatype: "json",
		success: function(data){
			if (data.state == 'success') {//成功
				alert("密码修改成功, 请重新登录系统。" );
				top.location.href="<%=basePath %>login.jsp";
				return;
			} else {
				alert(data.message);
			}
			//重置验证码
			resetValcode();
		}
   	});
}
</script>
</head>

<body class="login_body">
	<div class="lg_wraper">
		<div class="login_box">
            <form id="pwdForm" name="pwdForm">
			<table>
  <tr>
    <th>原始密码：</th>
    <td><input name="oldPwd" type="password"  /><span class="c_red">*</span></td>
  </tr>
  <tr>
    <th>新密码：</th>
    <td><input name="newPwd" type="password"  /><span class="c_red">*</span></td>
  </tr>
  <tr>
    <th>确认密码：</th>
    <td><input name="reNewPwd" type="password"  /><span class="c_red">*</span></td>
  </tr>
  
  <tr>
   	<th></th>
    <td><input name="" type="button" value="修改密码" onclick="savePassword()"/></td>
  </tr>
            </table>
            </form>
		</div>
	</div>
</body>
</html>
