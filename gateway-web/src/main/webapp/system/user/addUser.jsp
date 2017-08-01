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
<title>用户新增_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script>	
	/**添加用户验证*/
function save() {
	$("input[type=text],textarea").each(function(){
		$(this).val($.trim($(this).val()));
	});

	if($("input[name='user.loginName']").val()==""){
		alert("请输入登录帐号");
		$("input[name='user.loginName']").focus();
	 	return;
	}
	if($("input[name='user.loginPwd']").val()==""){
		alert("请输入登录密码");
		$("input[name='user.loginPwd']").focus();
	 	return;
	}
	if($("input[name='user.reLoingPwd']").val()==""){
		alert("请输入确认密码");
		$("input[name='user.reLoingPwd']").focus();
	 	return;
	}
	if($("input[name='user.loginPwd']").val() != $("input[name='user.reLoingPwd']").val()){
		alert("输入密码不一致");
		$("input[name='user.loginPwd']").focus();
	 	return;
	}
	if($("input[name='user.userName']").val()==""){
		alert("请输入姓名");
		$("input[name='user.userName']").focus();
	 	return;
	}
	
	/* if($("input[name='user.certNo']").val()==""){
		alert("请输入身份证号");
		$("input[name='user.certNo']").focus();
	 	return;
	} */
	//对密码位数验证
	var pwd =/^([\s\S]{6,16})$/;
	if(!pwd.test($("input[name='user.loginPwd']").val())){
		alert("请输入6-16位密码");
		$("input[name='user.loginPwd']").focus();
	 	return;
	}
	//对手机、固话进行判断
	var mobile = /^(1\d{10})$/; //手机号码
    var phone = /(^[0-9]{3,4}\-[0-9]{7,8}$)/;			//固话
	if(!mobile.test($("input[name='user.moblie']").val()) && !phone.test($("input[name='user.moblie']").val())){
 			alert("请输入正确的手机号码或者固定电话。固定电话：区号与号码之间加-");
   		$("input[name='user.moblie']").focus();
 	    return ;
	}
	//对邮箱验证
	var email = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if(!email.test($("input[name='user.email']").val())){
		alert("请输入正确的邮箱");
		$("input[name='user.email']").focus();
		return;
	}
	
	$("#addUserBtn,#addingUserBtn").toggle();
	$.ajax({
		type: "POST",
		url: "system/user_saveUserByAdmin.do",
		data: $("#addUserForm").serialize(),
		datatype: "json",
		success: function(data){
 			$("#addUserBtn,#addingUserBtn").toggle();
			if (data.state == 'success') {
					alert("新增成功");
			}else{
				alert(data.message);
 			}
 		}
   	});
}

</script>
</head>	
<body>
<div class="main_title">
	<b>用户新增</b>
</div>

	<div class="main_form" method="post">
	<form id= "addUserForm" name="addUserForm">
		<table width="100%" border="0">
			<tr>
				<th>用户类型：</th>
				<td><select name="user.userType" id="userType">
						<option value="safeUser">数据安全管理员</option>
						<option value="auditUser">审核人员</option>
						<option value="mainateUser">运维人员</option>
				</select><span class="c_red">*</span>
				</td>
			</tr>
			<tr>
				<th>登录帐号：</th>
				<td><input name="user.loginName" type="text" /><span class="c_red">*</span>
				</td>
			</tr>
			<tr>
				<th>登录密码：</th>
				<td><input name="user.loginPwd" type="password" /><span class="c_red">*</span>
				</td>
			</tr>
			<tr>
				<th>确认密码：</th>
				<td><input name="user.reLoingPwd" type="password" /><span class="c_red">*</span>
				</td>
			</tr>
			<tr>
				<th>姓名：</th>
				<td><input name="user.userName" type="text" /><span class="c_red">*</span>
				</td>
			</tr>
			<tr>
				<th>联系电话：</th>
				<td><input name="user.moblie" type="text" /><span class="c_red">*</span>
				</td>
			</tr>
			<tr>
				<th>邮箱：</th>
				<td><input name="user.email" type="text" /><span class="c_red">*</span>
				</td>
			</tr>
			<tr>
				<th>身份证号：</th>
				<td><input name="user.certNo" type="text" /></span>
				</td>
			</tr>
			<tr>
				<th>值班状态：</th>
				<td><select name="user.onlineStatus">
						<option selected="selected" value="0">非值班</option>
						<option value="1">值班</option>
						<span class="c_red">*</span>
				</td>
			</tr>
			<tr>
				<th>备注：</th>
				<td><textarea name="user.remark"  id ="remark"  cols="" rows=""></textarea>
				</td>
			</tr>
			<tr>
				<th>联系地址：</th>
				<td>
					<textarea name="user.addr"  id ="addr"  cols="" rows=""></textarea>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['saveManager']!=null}">
					<input name="addUserBtn" id="addUserBtn" type="button" value="确定" onclick="save()"/>
					<input name="addingUserBtn" type="button" value="正在新增" id="addingUserBtn" style="display:none" disabled/>
					</c:if>
				</td>
			</tr>
		</table>
	</form>
	</div>

</body>
</html>
