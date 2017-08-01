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
<title>用户修改_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/form/jquery-1.3.2.js"></script>
<script type="text/javascript" src="resource/js/form/jquery.form.js"></script>
<script>
	function save() {
		$("input[type=text],textarea").each(function(){
			$(this).val($.trim($(this).val()));
		});
		if($("input[name='user.userName']").val()==""){
			alert("请输入姓名");
			$("input[name='user.userName']").focus();
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
		if($("input[name='user.userType']").val()=="dataUser"||$("input[name='user.userType']").val()=="orgUser"){
			var idReg = new RegExp(/^[1-9][0-9]{5}(19[0-9]{2}|200[0-9]|2010)(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[0-9]{3}[0-9xX]$/);
			if(!idReg.test($("input[name='user.certNo']").val())){
				alert("请输入正确的身份证号码") ;
				$("input[name='user.certNo']").focus();
				return ;
			}
		}
		
		//上传图片验证
		if($("#userType").val()=='dataUser'){
			if ($("input[name='upload']").val()!="" && (!/\.(gif|jpg|jpeg|png|GIF|JPG|JPEG|PNG)$/.test($("input[name='upload']").val()))) {
				alert("证件扫描附件必须是.gif,jpeg,jpg,png中的一种！");
				$("input[name='upload']").focus();
				return;
			}
			
			//需要push文件到ftp
			if($("[name=user.pushFtp]") && $("[name=user.pushFtp]:checked").val()=='1'){
				if($("input[name='user.ftpIp']").val()==""){
					alert("请输入将输出文件PUSH到FTP的IP地址");
					$("input[name='user.ftpIp']").focus();
				 	return;
				}
				var ipTest =  new RegExp(/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/);
	    		if(!ipTest.test($("input[name='user.ftpIp']").val())){
	    			alert("请输入正确的IP地址");
					$("input[name='user.ftpIp']").focus();
				 	return;
	    		}
				if($("input[name='user.ftpPort']").val()==""){
					alert("请输入将输出文件PUSH到FTP的端口");
					$("input[name='user.ftpPort']").focus();
				 	return;
				}
				if(isNaN($("input[name='user.ftpPort']").val())){
					alert("请输入正确的FTP端口");
					$("input[name='user.ftpPort']").focus();
				 	return;
				}
				if($("input[name='user.ftpUsername']").val()==""){
					alert("请输入将输出文件PUSH到FTP的用户名");
					$("input[name='user.ftpUsername']").focus();
				 	return;
				}
				if($("input[name='user.ftpPassword']").val()==""){
					alert("请输入将输出文件PUSH到FTP的密码");
					$("input[name='user.ftpPassword']").focus();
				 	return;
				}
				if($("#ftpPassword2").val()==""){
					alert("请输入将输出文件PUSH到FTP的确认密码");
					$("#ftpPassword2").focus();
				 	return;
				}
				if($("input[name='user.ftpPassword']").val()!=$("#ftpPassword2").val()){
					alert("PUSH到FTP的密码和确认密码不一致");
					$("#ftpPassword2").focus();
				 	return;
				}
			}
		}else{
			if ($("input[name='upload']").val()!="" && (!/\.(rar|zip)$/.test($("input[name='upload']").val()))) {
				alert("证件扫描附件必须是压缩打包文件，且打包类型必须是.RAR,ZIP中的一种！");
				$("input[name='upload']").focus();
				return;
			}
		}
		
		var isNotEdit=false;
		if($("input[name='user1.userName']").val() == $("input[name='user.userName']").val()&&
			$("input[name='user1.moblie']").val() == $("input[name='user.moblie']").val()&&
			$("input[name='user1.certNo']").val() == $("input[name='user.certNo']").val()&&
			$("input[name='user1.email']").val() == $("input[name='user.email']").val()&&
			$("input[name='user1.remark']").val() == $("textarea[name='user.remark']").text()&&
			$("input[name='user1.addr']").val() == $("input[name='user.addr']").val()&&
			$("input[name='user1.webserviceUrl']").val() == $("input[name='user.webserviceUrl']").val()&&
			$("input[name='user1.webserviceMethod']").val() == $("input[name='user.webserviceMethod']").val()&&
			$("input[name='user1.baseWsdl']").val() == $("input[name='user.baseWsdl']").val()&&
			$("input[name='upload']").val() ==""){
				isNotEdit=true;
			
				if($("input[name='user.pushFtp']") && $("input[name='user.pushFtp']:checked").val()=='1'){
					if($("input[name='user1.ftpIp']").val() == $("input[name='user.ftpIp']").val()&&
						$("input[name='user1.ftpPort']").val() == $("input[name='user.ftpPort']").val()&&
						$("input[name='user1.ftpPath']").val() == $("input[name='user.ftpPath']").val()&&
						$("input[name='user1.ftpUsername']").val() == $("input[name='user.ftpUsername']").val()&&
						$("input[name='user1.ftpPassword']").val() == $("input[name='user.ftpPassword']").val()){
						isNotEdit=true;
					}else{
						isNotEdit=false;
					}
				}else{
					if($("input[name='user1.pushFtp']").val() == ""){
						isNotEdit=true
					}
				}
				if(isNotEdit){
					alert("没有已修改的字段");
				 	return;
				}
		}

		//对邮箱验证
		var email = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
		if(!email.test($("input[name='user.email']").val())){
			alert("请输入正确的邮箱");
			$("input[name='user.email']").focus();
			return;
		}
		
		if($("[name=user.pushFtp]:checked").val()!='1'){
			$("input[name='user.ftpIp'],input[name='user.ftpPort'],input[name='user.ftpUsername'],input[name='user.ftpPassword'],input[name='user.ftpPath']").val('');
		}

		$("#undateUserBtn,#updatingUserBtn").toggle();
		$("#updateUserForm").ajaxSubmit({
			type : "POST",
			url : "system/user_updateUser.do",
			data : $("#updateUserForm").serialize(),
			dataType:"json",
			success : function(data) {		
				$("#undateUserBtn").attr("disabled", false);
				if (data.state == 'success') {
					if(data.data.userType=='auditUser'||data.data.userType=='superUser' || data.data.userType=='safeUser' || data.data.userType=='mainateUser'){
						alert("修改成功");
					}else{
						alert("申请修改成功，待审核");
					}
					window.location.href='system/user_searchUserAccess.do';
				} else {
					alert(data.message);
				}				
				$("#undateUserBtn,#updatingUserBtn").toggle();
			}			
		});
	}

/**ftp 验证*/
	function ftpCheck() {
		//需要push文件到ftp
		if($("[name=user.pushFtp]:checked").val()=='1'){
			if($("input[name='user.ftpIp']").val()==""){
				alert("请输入将输出文件PUSH到FTP的IP地址");
				$("input[name='user.ftpIp']").focus();
			 	return;
			}
			var ipTest =  new RegExp(/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/);
	   		if(!ipTest.test($("input[name='user.ftpIp']").val())){
	   			alert("请输入正确的IP地址");
				$("input[name='user.ftpIp']").focus();
			 	return;
	   		}
			if(isNaN($("input[name='user.ftpPort']").val())){
				alert("请输入正确的FTP端口");
				$("input[name='user.ftpPort']").focus();
			 	return;
			}
			if($("input[name='user.ftpUsername']").val()==""){
				alert("请输入将输出文件PUSH到FTP的用户名");
				$("input[name='user.ftpUsername']").focus();
			 	return;
			}
			if($("input[name='user.ftpPassword']").val()==""){
				alert("请输入将输出文件PUSH到FTP的密码");
				$("input[name='user.ftpPassword']").focus();
			 	return;
			}
			var ftpIp= $("input[name='user.ftpIp']").val();
			var ftpUsername= $("input[name='user.ftpUsername']").val();
			var ftpPassword= $("input[name='user.ftpPassword']").val();
			
			$.ajax({
				type : "POST",
				url : "ftpCheck.do",
				dataType: 'json',
				data : "ftpIp=" + ftpIp + "&ftpUsername=" + ftpUsername +"&ftpPassword=" +ftpPassword,
				success: function(json){
					alert(json.message);
				},
				error:function(json){
					alert("ftp 测试不通过");
				}
			});
		}
	}	
</script>
</head>
<body>
<div class="main_title">
	<b>用户修改</b>
</div>
	<div class="main_form" method="post">
		<form id="updateUserForm" name="updateUserForm" method="post" enctype="multipart/form-data">
		<input id="userId" name="user.userId" type="hidden" value="${user.userId}" />
		<input id="userType" name="user.userType" type="hidden" value="${user.userType}" />
		<input id="loginName" name="user.loginName" type="hidden" value="${user.loginName}" />
		<input id="onlineStatus" name="user.onlineStatus" type="hidden" value="${user.onlineStatus}" />
		<input id="status" name="user.status" type="hidden" value="${user.status}" />
		<input id="confirmStatus" name="user.confirmStatus" type="hidden" value="${user.confirmStatus}" />
		<input id="createTime" name="user.createTime" type="hidden" value="${user.createTime}" />
		<input id="certType" name="user.certType" type="hidden" value="${user.certType}" />
		<input id="loginPwd" name="user.loginPwd" type="hidden" value="${user.loginPwd}" />
		<input id="orgId" name="user.orgId" type="hidden" value="${user.orgId}" />
		<input id="userName" name="user1.userName" type="hidden" value="${user.userName}" />
		<input id="moblie" name="user1.moblie" type="hidden" value="${user.moblie}" />
		<input id="certNo" name="user1.certNo" type="hidden" value="${user.certNo}" />
		<input id="email" name="user1.email" type="hidden" value="${user.email}" />
		<input id="remark" name="user1.remark" type="hidden" value="${user.remark}" />
		<input id="addr" name="user1.addr" type="hidden" value="${user.addr}" />
		<c:if test="${user.userType=='dataUser' && loginUser.userId==user.userId}">
		<input id="pushFtp" name="user1.pushFtp" type="hidden" value="${user.pushFtp}" />
		<input id="ftpIp" name="user1.ftpIp" type="hidden" value="${user.ftpIp}" />
		<input id="ftpPort" name="user1.ftpPort" type="hidden" value="${user.ftpPort}" />
		<input id="ftpPath" name="user1.ftpPath" type="hidden" value="${user.ftpPath}" />
		<input id="ftpUsername" name="user1.ftpUsername" type="hidden" value="${user.ftpUsername}" />
		<input id="ftpPassword" name="user1.ftpPassword" type="hidden" value="${user.ftpPassword}" />
		<input id="webserviceUrl" name="user1.webserviceUrl" type="hidden" value="${user.webserviceUrl}" />
		<input id="webserviceMethod" name="user1.webserviceMethod" type="hidden" value="${user.webserviceMethod}" />
		</c:if>
			
			<div class="main_info_title">用户信息</div>
			<table width="100%" border="0">
				<tr>
					<th>用户类型：</th>
					<td><g:sysDict dictCode="DICT_USER_TYPE" dictKey="${user.userType }"/></td>
				</tr>
				<tr>
					<th>登录帐号：</th>
					<td>${user.loginName}</td>
				</tr>
				<tr>
					<th>姓名：</th>
					<td><input id="userName" name="user.userName" type="text" value="${user.userName}" /><span class="c_red">*</span>
					</td>
				</tr>
				<tr>
					<th>联系电话：</th>
					<td><input id="moblie" name="user.moblie" type="text"
						value="${user.moblie}" /><span class="c_red">*</span>
					</td>
				</tr>
				<tr>
					<th>身份证号：</th>
					<td><input id="certNo" name="user.certNo" type="text"
						value="${user.certNo}" /><c:if test="${user.userType=='dataUser' || user.userType=='orgUser' }"><span class="c_red">*</span></c:if>
					</td>
				</tr>
				<tr>
					<th>邮箱：</th>
					<td><input id="email" name="user.email" type="text"
						value="${user.email}" /><span class="c_red">*</span>
					</td>
				</tr>
				<c:if test="${user.userType=='dataUser' && loginUser.userId==user.userId}">
				<tr>
					<th>PUSH FTP：</th>
					<td>
					<label title="输出文件是否需要PUSH到指定FTP"><input name="user.pushFtp" type="checkbox" value="1" ${user.pushFtp=='1'?"checked=true":""} onclick="$('tr[name=gwUserFtpTr]').toggle()"/>需要</label>
					</td>
				</tr>
				  <tr name="gwUserFtpTr" ${user.pushFtp=="1"?"":"style='display:none'"}>
				    <th>FTP IP：</th>
				    <td><input type="text" name="user.ftpIp" value="${user.ftpIp }" maxlength="15"/><span class="c_red">*</span></td>
				  </tr>
				  <tr name="gwUserFtpTr" ${user.pushFtp=="1"?"":"style='display:none'"}>
				    <th>FTP端口：</th>
				    <td><input type="text" name="user.ftpPort" value="${user.ftpPort }" maxlength="4"/><span class="c_red">*</span></td>
				  </tr>
				  <tr name="gwUserFtpTr" ${user.pushFtp=="1"?"":"style='display:none'"}>
				    <th>FTP目录路径：</th>
				    <td><input type="text" name="user.ftpPath" value="${user.ftpPath }"/></td>
				  </tr>
				  <tr name="gwUserFtpTr" ${user.pushFtp=="1"?"":"style='display:none'"}>
				    <th>FTP用户名：</th>
				    <td><input type="text" name="user.ftpUsername" value="${user.ftpUsername }"/><span class="c_red">*</span></td>
				  </tr>
				  <tr name="gwUserFtpTr" ${user.pushFtp=="1"?"":"style='display:none'"}>
				    <th>FTP密码：</th>
				    <td><input type="password" name="user.ftpPassword" value="${user.ftpPassword }"/><span class="c_red">*</span></td>
				  </tr>
				  <tr name="gwUserFtpTr" ${user.pushFtp=="1"?"":"style='display:none'"}>
				    <th>FTP密码确认：</th>
				    <td><input type="password" id="ftpPassword2" value="${user.ftpPassword}"/><span class="c_red">*</span></td>
				  </tr>
				  <tr name="gwUserFtpTr" ${user.pushFtp=="1"?"":"style='display:none'"}>
				    <th></th>
				    <td><input id="checkFtp" name="checkFtp" type="button" value="测试FTP连接"  style="line-height:13px;padding:0px;font-size: 10px;width:70px;height:30px;" onclick="ftpCheck()"/>
				    </td>
				  </tr>
				  <tr>
					<th>webservice URL：</th>
					<td><input id="email" name="user.webserviceUrl" type="text" value="${user.webserviceUrl}" /></span>
					</td>
				  </tr>
				  <tr>
					<th>webservice 方法名：</th>
					<td><input id="email" name="user.webserviceMethod" type="text" value="${user.webserviceMethod}" /></span>
					</td>
				  </tr>
				  <tr>
					<th>webservice 包路径：</th>
					<td><input id="email" name="user.baseWsdl" type="text" value="${user.baseWsdl}" /></span>
					</td>
				  </tr>
				  </c:if>
				<tr>
					<th>备注：</th>
					<td><textarea id="remark" name="user.remark" cols="" rows="">${user.remark}</textarea>
					</td>
				</tr>
				<tr>
					<th>联系地址：</th>
					<td><input type="text" id="addr" name="user.addr" value="${user.addr}"/>
					</td>
				</tr>
				<tr>
					<th>原有证件扫描附件：</th>
    				<td>
	      	  	  		<c:if test="${user.fileId != null }">
      	  	  	    		<a href="system/file_downLoadFile.do?fileId=${user.fileId}" target="_blank">
      	  	  	    		<span class="c_red">${fileVo.realName}</span></a>
      	  	  	  		</c:if>
      	  	  	  </td>
    			</tr>
    			<tr>
					<th>修改证件扫描附件：</th>
    				<td>
	      	  	  	  <input type="file" name="upload"  class="" style="width:30%" />
      	  	  	  </td>
    			</tr>
				</table>
			<c:if test="${user.userType=='dataUser' || user.userType=='orgUser' }">
			<div class="main_info_title">机构信息</div>
				<table width="100%" border="0" style="text-align:center">
					<tr>
						<td  align="right">机构名称：</td>
						<td>${org.orgName}</td>
						<td  align="right">法人身份证号码：</td>
						<td>${org.certNo}</td>
						<td  align="right">法人名称：</td>
						<td>${org.orgHeadName}</td>

					</tr>
					<tr>					
						<td align="right">联系电话：</td>
						<td>${org.orgTel}</td>
						<td align="right">公司地址：</td>
						<td>${org.orgAddr}</td>
						<td align="right">工商编码：</td>
						<td>${org.regCode}</td>
					</tr>
				</table>
			</c:if>
			
			<table width="100%" border="0">
				<tr>
					<td colspan=6 align="center">
					 <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['updateUser']!=null}">
						<input name="undateUserBtn" id="undateUserBtn" type="button" value="保存" onclick="save()" />
						<input name="updatingUserBtn" type="button" value="正在保存" id="updatingUserBtn" style="display:none;color:gray" disabled/>
					 </c:if> 
					 <input name="backBtn" id="backBtn" type="button" value="返回" onclick="history.go(-1)" />
					</td>					
				</tr>
			</table>
		</form>
	</div>
</body>
</html>
