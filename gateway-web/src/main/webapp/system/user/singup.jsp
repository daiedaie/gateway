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
<title>用户注册_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/form/jquery-1.3.2.js"></script>
<script type="text/javascript" src="resource/js/form/jquery.form.js"></script>
<script type="text/javascript">
function changeUserType() {
	if($('#userType').val() == 'orgUser'){
		$("tr[name=gwOrgTr]").show();
		$("tr[name=gwUserTr],tr[name=gwUserFtpTr],tr[name=gwUserFtpSelfTr],tr[name=gwUserFilePwd]").hide();
		$('#gw_org_list').hide();
		$("#imageMsg").show();
	}else{
		$("tr[name=gwOrgTr]").hide();
		$("tr[name=gwUserTr]").show();
		if($("[name=gwUserVo.pushFtp]:checked").val()=='1'){
			$("tr[name=gwUserFtpTr]").show();
			//document.getElementById("selfFtp").style.display="none";
		} 
		if($("[name=gwUserVo.pushFtp]:checked").val()=='1') $("tr[name=gwUserFtpSelfTr]").show();
		if($("[name=gwUserVo.needFilePwd]:checked").val()=='1') $("tr[name=gwUserFilePwd]").show();
		$('#gw_org_list').show();
		$("#imageMsg").hide();
	}
}
/**Ftp选中gataWay*/
function Ftp(){
 	var agree = $("input[type='checkbox']").is(':checked');
 	if(agree){
 		$("#ownFtp").hide();
 	}else{
 		$("#ownFtp").show();
 	}
}
/**自有Ftp*/
function selfFtp(){

 	var agree = $("input[type='checkbox']").is(':checked');
 	if(agree){
 		$("#selfFtp").hide();
 	}else{
 		$("#selfFtp").show();
 	}
}
/**验证*/
function singup() {
	
	if($("input[name='gwUserVo.loginName']").val()==""){
		alert("请输入登录帐号");
		$("input[name='gwUserVo.loginName']").focus();
	 	return;
	}
	if($("input[name='gwUserVo.loginPwd']").val()==""){
		alert("请输入登录密码");
		$("input[name='gwUserVo.loginPwd']").focus();
	 	return;
	}
	if($("input[name='reLoingPwd']").val()==""){
		alert("请输入确认密码");
		$("input[name='reLoingPwd']").focus();
	 	return;
	}
	if($("input[name='gwUserVo.loginPwd']").val() != $("input[name='reLoingPwd']").val()){
		alert("输入密码不一致");
		$("input[name='gwUserVo.loginPwd']").focus();
	 	return;
	}
	if($("input[name='gwUserVo.userName']").val()==""){
		alert("请输入姓名");
		$("input[name='gwUserVo.userName']").focus();
	 	return;
	}
	if($("input[name='gwUserVo.moblie']").val()==""){
		alert("请输入联系电话");
		$("input[name='gwUserVo.moblie']").focus();
	 	return;
	}
		if($("input[name='gwUserVo.certNo']").val()==""){
		alert("请输入身份证");
		$("input[name='gwUserVo.certNo']").focus();
	 	return;
	}
	if($("input[name='gwUserVo.email']").val()==""){
		alert("请输入邮箱");
		$("input[name='gwUserVo.email']").focus();
	 	return;
	}
	//对手机、固话进行判断
	var mobile = /^(1\d{10})$/; //手机号码
    var phone = /(^[0-9]{3,4}\-[0-9]{7,8}$)/;			//固话
	if(!mobile.test($("input[name='gwUserVo.moblie']").val()) && !phone.test($("input[name='gwUserVo.moblie']").val())){
 			alert("请输入正确的手机号码或者固定电话。固定电话：区号与号码之间加-");
   		$("input[name='gwUserVo.moblie']").focus();
 	    return ;
	}
	
	//对邮箱验证
	var email = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if(!email.test($("input[name='gwUserVo.email']").val())){
		alert("请输入正确的邮箱");
		$("input[name='gwUserVo.email']").focus();
		return;
	}
	//身份证验证
	var idReg = new RegExp(/^[1-9][0-9]{5}(19[0-9]{2}|200[0-9]|2010)(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[0-9]{3}[0-9xX]$/);
	if(!idReg.test($("input[name='gwUserVo.certNo']").val())){
		alert("请输入正确的身份证号码") ;
		$("input[name='gwUserVo.certNo']").focus();
		return ;
	}

	if($("input[name='upload']").val()==""){
		alert("请上传证件扫描附件");
		$("input[name='upload']").focus();
	 	return;
	}
	//上传图片验证
	if($("#userType").val()=='dataUser'){
		if (!/\.(gif|jpg|jpeg|png|GIF|JPG|JPEG|PNG)$/.test($("input[name='upload']").val())) {
			alert("证件扫描附件必须是.gif,jpeg,jpg,png中的一种！");
			$("input[name='upload']").focus();
			return;
		}
	}else{
		if (!/\.(rar|zip)$/.test($("input[name='upload']").val())) {
			alert("证件扫描附件必须是压缩打包文件，且打包类型必须是.RAR,ZIP中的一种！");
			$("input[name='upload']").focus();
			return;
		}
	}
	
	if($('#userType').val() == 'orgUser'){
		if($("input[name='gwOrgVo.orgName']").val()==""){
			alert("请输入机构名称");
			$("input[name='gwOrgVo.orgName']").focus();
		 	return;
		}
		if($("input[name='gwOrgVo.orgHeadName']").val()==""){
			alert("请输入法人名称");
			$("input[name='gwOrgVo.orgHeadName']").focus();
		 	return;
		}
		//身份证验证
		var idReg = new RegExp(/^[1-9][0-9]{5}(19[0-9]{2}|200[0-9]|2010)(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[0-9]{3}[0-9xX]$/);
		if(!idReg.test($("input[name='gwOrgVo.certNo']").val())){
			alert("请输入正确的法人身份证号码") ;
			$("input[name='gwOrgVo.certNo']").focus();
			return ;
		}
		if($("input[name='gwOrgVo.regCode']").val()==""){
			alert("请输入工商编码");
			$("input[name='gwOrgVo.regCode']").focus();
		 	return;
		}
		if($("input[name='gwOrgVo.orgTel']").val()==""){
			alert("请输入联系电话！");
			$("input[name='gwOrgVo.orgTel']").focus();
		 	return;
		}
		//对手机、固话进行判断
		if(!mobile.test($("input[name='gwOrgVo.orgTel']").val()) && !phone.test($("input[name='gwOrgVo.orgTel']").val())){
 			alert("请输入正确的手机号码或者固定电话。固定电话：区号与号码之间加-");
   			$("input[name='gwOrgVo.orgTel']").focus();
 	    	return ;
		}
	}else{
			//需要文件密码
			if ($("[name=gwUserVo.needFilePwd]:checked").val() == '1') {
				if ($("input[name='gwUserVo.fileEncryPwd']").val() == "") {
					alert("请输入文件加密密码");
					$("input[name='gwUserVo.fileEncryPwd']").focus();
					return;
				}
				if ($("input[name='reFileEncryPwd']").val() == "") {
					alert("请输入文件加密确认密码");
					$("input[name='reFileEncryPwd']").focus();
					return;
				}
				if ($("input[name='gwUserVo.fileEncryPwd']").val() != $("input[name='reFileEncryPwd']").val()) {
					alert("输入密码不一致");
					$("input[name='gwUserVo.fileEncryPwd']").focus();
					return;
				}
			}

			//需要push文件到ftp
			if ($("[name=gwUserVo.pushFtp]").is(":checked")) {//数据用户勾选需要Ftp
			 if(!$(".pushSelfFtp").is(':checked')&&!$(".pushGateWayFtp").is(':checked')){//如果只勾选需要
			 	alert("请输入选择FTP推送方式");
			 		return;
			 }else {
			 //勾选自带Ftp
			  if ($(".pushSelfFtp").is(':checked')){
				if ($("input[name='gwUserVo.ftpIp']").val() == "") {
					alert("请输入将输出文件PUSH到FTP的IP地址");
					$("input[name='gwUserVo.ftpIp']").focus();
					return;
				}
				var ipTest = new RegExp(
						/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/);
				if (!ipTest.test($("input[name='gwUserVo.ftpIp']").val())) {
					alert("请输入正确的IP地址");
					$("input[name='gwUserVo.ftpIp']").focus();
					return;
				}
				if ($("input[name='gwUserVo.ftpPort']").val() == "") {
					alert("请输入将输出文件PUSH到FTP的端口");
					$("input[name='gwUserVo.ftpPort']").focus();
					return;
				}
				if (isNaN($("input[name='gwUserVo.ftpPort']").val())) {
					alert("请输入正确的FTP端口");
					$("input[name='gwUserVo.ftpPort']").focus();
					return;
				}
				if ($("input[name='gwUserVo.ftpUsername']").val() == "") {
					alert("请输入将输出文件PUSH到FTP的用户名");
					$("input[name='gwUserVo.ftpUsername']").focus();
					return;
				}
				if ($("input[name='gwUserVo.ftpPassword']").val() == "") {
					alert("请输入将输出文件PUSH到FTP的密码");
					$("input[name='gwUserVo.ftpPassword']").focus();
					return;
				}
				if ($("#ftpPassword2").val() == "") {
					alert("请输入将输出文件PUSH到FTP的确认密码");
					$("#ftpPassword2").focus();
					return;
				}
				if ($("input[name='gwUserVo.ftpPassword']").val() != $(
						"#ftpPassword2").val()) {
					alert("PUSH到FTP的密码和确认密码不一致");
					$("#ftpPassword2").focus();
					return;
				}
			  }
			 }
			}else{
				alert("请选择FTP数据推送方式");
				return;
			}

			if ($("select[name='gwUserVo.orgId']").is(":selected")) {
				alert("请选择机构");
				return;
			}
		}

		if ($("[name=gwUserVo.pushFtp]:checked").val() != '1') {
			$(
					"input[name='gwUserVo.ftpIp'],input[name='gwUserVo.ftpPort'],input[name='gwUserVo.ftpUsername'],input[name='gwUserVo.ftpPassword'],input[name='gwUserVo.ftpPath']")
					.val('');
		}

		$("#register,#registerIng").toggle();
		$("#singupForm").ajaxSubmit(
				{
					type : "POST",
					url : "addGwUser.do",
					data : $("#singupForm").serialize(),
					dataType : "json",
					success : function(json) {
						$("#register,#registerIng").toggle();
						if (json.state == 'success') {//成功
							alert($("input[name='gwUserVo.loginName']").val()
									+ " 已经注册成功，请等待审核.");
							window.location.href = "login.jsp";
							return;
						} else {
							alert(json.message);
						}
					},
					error : function() {
						$("#register,#registerIng").toggle();
						alert("注册失败，请联系管理员");
					}
				});
	}

	/**ftp 验证*/
	function ftpCheck() {
		//需要push文件到ftp
		if ($("[name=gwUserVo.pushFtp]:checked").val() == '1') {
			if ($("input[name='gwUserVo.ftpIp']").val() == "") {
				alert("请输入将输出文件PUSH到FTP的IP地址");
				$("input[name='gwUserVo.ftpIp']").focus();
				return;
			}
			var ipTest = new RegExp(
					/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/);
			if (!ipTest.test($("input[name='gwUserVo.ftpIp']").val())) {
				alert("请输入正确的IP地址");
				$("input[name='gwUserVo.ftpIp']").focus();
				return;
			}
			if (isNaN($("input[name='gwUserVo.ftpPort']").val())) {
				alert("请输入正确的FTP端口");
				$("input[name='gwUserVo.ftpPort']").focus();
				return;
			}
			if ($("input[name='gwUserVo.ftpUsername']").val() == "") {
				alert("请输入将输出文件PUSH到FTP的用户名");
				$("input[name='gwUserVo.ftpUsername']").focus();
				return;
			}
			if ($("input[name='gwUserVo.ftpPassword']").val() == "") {
				alert("请输入将输出文件PUSH到FTP的密码");
				$("input[name='gwUserVo.ftpPassword']").focus();
				return;
			}
			var ftpIp = $("input[name='gwUserVo.ftpIp']").val();
			var ftpUsername = $("input[name='gwUserVo.ftpUsername']").val();
			var ftpPassword = $("input[name='gwUserVo.ftpPassword']").val();

			$.ajax({
				type : "POST",
				url : "ftpCheck.do",
				dataType : 'json',
				data : "ftpIp=" + ftpIp + "&ftpUsername=" + ftpUsername
						+ "&ftpPassword=" + ftpPassword,
				success : function(json) {
					alert(json.message);
				},
				error : function(json) {
					alert("ftp 测试不通过");
				}
			});
		}
	}
</script>
</head>

<body class="login_body">
<form id="singupForm" name="singupForm" method="post" enctype="multipart/form-data">
	<div class="lg_wraper">
		<div style="height:100px; overflow:hidden;"><img src="resource/images/login_logo.gif" /></div>
        <div style="height:80px; overflow:hidden;"><img src="resource/images/login_logo2.gif" /></div>
		<div class="login_box">
            <div class="main_title">
                <b>用户注册</b>
                <div class="main_tt_right fr">
                    <a href="login.jsp" class="button">返回登录</a>
                </div>
            </div>
			<table>
  <tr>
    <th style="width:25%">用户类型：</th>
    <td><select name="gwUserVo.userType" id="userType" onchange="changeUserType()">
			<option value="dataUser">数据用户</option>
			<option value="orgUser">机构用户</option>
			</select><span class="c_red">*</span></td>
  </tr>
  <tr>
    <th>登录帐号：</th>
    <td><input name="gwUserVo.loginName" id="loginName" type="text"  /><span class="c_red">*</span></td>
  </tr>
  <tr>
    <th>登录密码：</th>
    <td><input name="gwUserVo.loginPwd" type="password"  /><span class="c_red">*</span></td>
  </tr>
  <tr>
    <th>确认密码：</th>
    <td><input name="reLoingPwd" type="password"  /><span class="c_red">*</span></td>
  </tr>
  <tr name="gwUserTr">
    <th>输出文件密码设置：</th>
    <td><label title="输出文件是否需要密码设置"><input type="checkbox" name="gwUserVo.needFilePwd" value="1" onclick="$('tr[name=gwUserFilePwd]').toggle()"/>需要</label>
    </td>
  </tr>
  <tr name="gwUserFilePwd" style="display:none">
    <th>文件加密密码：</th>
    <td><input name="gwUserVo.fileEncryPwd" type="password"  /><span class="c_red">*</span></td>
  </tr>
  <tr name="gwUserFilePwd" style="display:none">
    <th>确认密码：</th>
    <td><input name="reFileEncryPwd" type="password"  /><span class="c_red">*</span></td>
  </tr>
  <tr>
    <th>姓名：</th>
    <td><input name="gwUserVo.userName" type="text"  /><span class="c_red">*</span></td>
  </tr>
  <tr>
    <th>联系电话：</th>
    <td><input name="gwUserVo.moblie" type="text"  /><span class="c_red">*</span></td>
  </tr>
  <tr>
    <th>身份证号：</th>
    <td><input name="gwUserVo.certNo" type="text"  /><span class="c_red">*</span></td>
  </tr>
  <tr>
    <th>证件扫描附件：</th>
    <td><input type="file" name="upload"  class="" style="width:90%" /><span class="c_red">*</span>
    <span class="c_red" id="imageMsg" style="display:none">压缩打包上传，附件中要求信息包括注册人身份证、法人身份证、营业执照</span></td>
  </tr>
  <tr>
    <th>邮箱：</th>
    <td><input name="gwUserVo.email" type="text"  /><span class="c_red">*</span></td>
  </tr>
    <tr name="gwFtpDataTr">
    <th>FTP数据推送</th>
    <td><label title="FTP数据推送"><input type="checkbox"   onclick="$('tr[class=tab]').toggle()" name="gwUserVo.pushFtp" value="1">需要</label>
    </td>
  </tr>
  
 
  <tr class="tab" style="display:none" >
  	<td colspan="2" >
  		<table >
  <tr id="ownFtp" class="radio1">
    <th width="110"></th>
    <td><label title="输出文件是否需要PUSH到指定FTP"><input class="pushSelfFtp" type="radio" name="gwUserVo.ftpType"  value="1"  />自有服务器</label>
    </td>
  </tr>
  <tr  name="" id="selfFtp" >
    <th width="110"></th>
    <td><label title="输出文件是否需要PUSH到指定FTP"><input type="radio" class="pushGateWayFtp"  name="gwUserVo.ftpType" value="2" />使用gateway服务器</label>
    </td>
  </tr>
  <tr name="gwUserFtpTr0" style="display:none">
    <th>FTP 下载IP：</th>
    <td><input type="text" name="gwUserVo.ftpIp" value="" maxlength="15"/><span class="c_red">*</span></td>
  </tr>
  <tr name="gwUserFtpTr0" style="display:none">
    <th>FTP 下载端口：</th>
    <td><input type="text" name="gwUserVo.ftpPort" value="" maxlength="4"/><span class="c_red">*</span></td>
  </tr>
  <tr name="gwUserFtpTr0" style="display:none">
    <th>FTP 下载目录路径：</th>
    <td><input type="text" name="gwUserVo.ftpPath" value=""/></td>
  </tr>
  <tr name="gwUserFtpTr0" style="display:none">
    <th>FTP 下载用户名：</th>
    <td><input type="text" name="gwUserVo.ftpUsername" value=""/><span class="c_red">*</span></td>
  </tr>
  <tr name="gwUserFtpTr0" style="display:none">
    <th>FTP 下载密码：</th>
    <td><input type="password" name="gwUserVo.ftpPassword" value=""/><span class="c_red">*</span>
    </td>
  </tr>
  <tr name="gwUserFtpTr0" style="display:none">
    <th>FTP 下载密码确认：</th>
    <td><input type="password" id="ftpPassword2" value=""/><span class="c_red">*</span></td>
  </tr>
  
  <tr name="gwUserFtpTr0" style="display:none">
    <th></th>
    <td><input id="checkFtp" name="checkFtp" type="button" value="测试FTP连接"  style="line-height:13px;padding:0px;font-size: 10px;width:70px;height:30px;" onclick="ftpCheck()"/>
    </td>
  </tr>

  <tr name="gwUserFtpTr1" style="display:none">
    <th>FTP 下载IP：</th>
    <td>${GwSysFtpVo.ftpIp}</td>
  </tr>
  <tr name="gwUserFtpTr1" style="display:none">
    <th>FTP 下载端口：</th>
    <td>${GwSysFtpVo.ftpPort}</td>
  </tr>
   <tr  name="gwUserFtpTr1" style="display:none">
    <th>FTP 下载用户名：</th>
    <td>登录帐号+"_down"</td>
  </tr>  
    <tr name="gwUserFtpTr1" style="display:none">
    <th>FTP 下载密码：</th>
    <td>登录密码+"_down"
    </td>
  </tr> 
  		</table>
  	
  	</td>  
  </tr>
  <tr name="gwUserTr">
  	<th valign="top">webservice</th>
    <td>推送接口信息</td>
  </tr>
  <tr name="gwUserTr">
    <th valign="top">URL：</th>
    <td><input name="gwUserVo.webserviceUrl" type="text"  /></td>   
  </tr>
  <tr name="gwUserTr">
    <th valign="top">方法名：</th>
    <td><input name="gwUserVo.webserviceMethod" type="text"  /></td>    
  </tr>
  <tr name="gwUserTr">
    <th valign="top">包路径：</th>
    <td><input name="gwUserVo.baseWsdl" type="text"  /></td>    
  </tr>
  <tr>
    <th valign="top">联系地址：</th>
    <td><textarea name="gwUserVo.addr"  id ="addr"  cols="30" rows="20" style="width:300px"></textarea><span class="c_red"></span></td>    
  </tr>
  <tr name="gwOrgTr" style="display:none">
    <th>机构名称：</th>
    <td><input name="gwOrgVo.orgName" type="text"  /><span class="c_red">*</span></td>
  </tr>
  <tr name="gwOrgTr" style="display:none">
    <th>法人名称：</th>
    <td><input name="gwOrgVo.orgHeadName" type="text"  /><span class="c_red">*</span></td>
  </tr>
  <tr name="gwOrgTr" style="display:none">
    <th>法人身份证号码：</th>
    <td><input name="gwOrgVo.certNo" type="text"  /><span class="c_red">*</span></td>
  </tr>
  <tr name="gwOrgTr" style="display:none">
    <th>工商编码：</th>
    <td><input name="gwOrgVo.regCode" type="text"  /><span class="c_red">*</span></td>
  </tr>
  <tr name="gwOrgTr" style="display:none">
    <th>联系电话：</th>
    <td><input name="gwOrgVo.orgTel" type="text"  /><span class="c_red">*</span></td>
  </tr>
  <tr name="gwOrgTr" style="display:none">
    <th>公司地址：</th>
    <td><input name="gwOrgVo.orgAddr" type="text"  /><span class="c_red"></span></td>
  </tr>
  <tr id="gw_org_list">
    <th>机构名称：</th>
    <td><select name="gwUserVo.orgId">
    <s:iterator id="item" value="#request.gwOrgs">
    	<s:if test=""></s:if>
    	<option value="<s:property value="orgId"/>" ><s:property value="orgName" /></option>
    </s:iterator>
			</select><span class="c_red">*</span></td>
  </tr>
  
  <tr>
   	<th></th>
    <td>
    	<input id="register" type="button" value="注 册" onclick="singup()"/>
    	<input id="registerIng" type="button" value="保存中" onclick="" disabled style="color:gray;display:none"/>
    </td>
  </tr>
            </table>
            
             <script>
  	$(function(){
  		$(".tab input[type='radio']").click(function(){
  			$(".tab input[type='radio']").each(function(index){
  				var check = $(this).is(':checked');
  				if(check){
  					$('tr[name=gwUserFtpTr'+index+']').show();
  					$('tr[name=gwUserFtpTr0] input').attr("value","");
  					$('#checkFtp').attr("value","测试FTP连接");
  					
  				}else{
  					$('tr[name=gwUserFtpTr'+index+']').hide();
  					$('tr[name=gwUserFtpTr0] input').attr("value","");
  					$('#checkFtp').attr("value","测试FTP连接");
  				}
  			});
  		});
  	});
  </script>
		</div>
	</div>
</form>
</body>
</html>
