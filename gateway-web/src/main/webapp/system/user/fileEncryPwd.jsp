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
	if($("[name=needFilePwd]:checked").val()=='1'){
		if("${gwUser.fileEncryPwd}" != null){
			if($("input[name='oldPwd']").val()==""){
				alert("请输入原始密码");
				$("input[name='oldPwd']").focus();
	 			return;
			}
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
	
	}
	
	$.ajax({
		type: "POST",
		url: "system/gwUser_updatefileEncryPwd.do",
		data: $("#pwdForm").serialize(),
		datatype: "json",
		success: function(data){
			if (data.state == 'success') {//成功
				alert("文件加密密码修改成功!" );
				history.go(-1);
			} else {
				alert(data.message);
			}
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
	<th>输出文件密码设置：</th>
	<td>
	<label title="输出文件是否需要密码设置"><input name="needFilePwd" type="checkbox" value="1" ${gwUser.needFilePwd=='1'?"checked=true":""} onclick="$('tr[name=gwUserFilePwdTr]').toggle()"/>需要</label>
	</td>
	</tr>
	<c:if test="${gwUser.fileEncryPwd !=null }">
	<tr name="gwUserNeedFilePwdTr" ${gwUser.needFilePwd=="1"?"":"style='display:none'"}>
    <th>原始文件加密密码：</th>
    <td><input name="oldPwd" type="password"  /><span class="c_red">*</span></td>
  	</tr>
	</c:if>
  <tr name="gwUserFilePwdTr" ${gwUser.needFilePwd=="1"?"":"style='display:none'"}>
    <th>新密码：</th>
    <td><input name="newPwd" type="password"  /><span class="c_red">*</span></td>
  </tr>
  <tr name="gwUserFilePwdTr" ${gwUser.needFilePwd=="1"?"":"style='display:none'"}>
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
