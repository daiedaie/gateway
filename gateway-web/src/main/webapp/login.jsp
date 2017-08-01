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
<title>系统登录_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">
$(function(){
    $("#operId").focus();
});

/**登录验证*/
function login() {
	if($("#operId").val()==""){
		alert("请输入登录账号");
	 	$("#operId").focus();
	 	return;
	}
	if($("#operPwd").val()==""){
	 	alert("请输入密码");
	 	$("#operPwd").focus();
	 	return;
	}
	if($("#valCode").val()==""){
	 	alert("请输入验证码");
	 	$("#valCode").focus();
	 	return;
	}
	
	$("#loginBtn,#loginingBtn").toggle();
	//登录验证
	$.ajax({
		type: "POST",
		url: "login.do",
		data: $("#loginForm").serialize(),
		datatype: "json",
		success: function(data){
			if (data.state == 'success') {//登录成功
				window.location.href="main.jsp";
				return;
			} else {
				alert(data.message);
			}
			$("#loginBtn,#loginingBtn").toggle();
			//重置验证码
			resetValcode();
		}
   	});
}

/**回车事件*/
function enterEnvent(evt){
	evt = (evt) ? evt : ((window.event) ? window.event : "")
	keyCode = evt.keyCode ? evt.keyCode : (evt.which ? evt.which : evt.charCode);
	if (keyCode == 13){
		login();
	}
}

/**重置验证码*/
function resetValcode(){
	$("#valImg").attr("src", "ValidateCodeServlet?"+Math.random());
	$("#valCode").val("");
}
</script>
</head>

<body class="login_body">
	<div class="lg_wraper">
		<div style="height:100px; overflow:hidden;"><img src="resource/images/login_logo.gif" /></div>
        <div style="height:80px; overflow:hidden;"><img src="resource/images/login_logo2.gif" /></div>
		<div class="login_box">
			<form id="loginForm" name="loginForm">
			<table>
            	<tr>
                	<th>账号：</th>
                    <td><input id="operId" name="userName" type="text" value="" onkeydown="enterEnvent(event)" placeholder="请输入登录账号" /></td>
                </tr>
                <tr>
                	<th>密码：</th>
                    <td><input id="operPwd" name="password" type="password" value="" onkeydown="enterEnvent(event)" placeholder="请输入密码" /></td>
                </tr>
                <tr>
                	<th>验证码：</th>
                    <td><input id="valCode" name="code" type="text" value="" onkeydown="enterEnvent(event)" placeholder="请输入验证码" maxlength="4" class="ipt_sort" /><img src="ValidateCodeServlet" id="valImg" style="cursor:pointer;" onclick="resetValcode();" alt="点击更新验证码" /></td>
                </tr>
                <tr>
                	<th></th>
                    <td>
                    <input name="loginBtn" type="button" value="登 录" onclick="login()" id="loginBtn"/>
                    <input name="loginingBtn" type="button" value="正在登录" id="loginingBtn" style="display:none;color:gray" disabled/>
                    <input name="registerBtn" type="button" onclick="location.href = 'register.do'" value="注 册" />
                    </td>
                </tr>
            </table>
            </form>
		</div>
	</div>
</body>
</html>
