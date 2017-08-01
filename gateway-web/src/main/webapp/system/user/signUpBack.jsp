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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>用户注册_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/form/jquery-1.3.2.js"></script>
<script type="text/javascript" src="resource/js/form/jquery.form.js"></script>
<script type="text/javascript">
/**验证*/
function reSingup() {
	
	if($("input[name='gwUserVo.userName']").val()==""){
		alert("请输入姓名");
		$("input[name='gwUserVo.userName']").focus();
	 	return;
	}
	//对邮箱验证
	var email = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if(!email.test($("input[name='gwUserVo.email']").val())){
		alert("请输入正确的邮箱");
		$("input[name='gwUserVo.email']").focus();
		return;
	}
	var mobile = /^(1\d{10})$/; //手机号码
    var phone = /(^[0-9]{3,4}\-[0-9]{7,8}$)/;			//固话
	if(!mobile.test($("input[name='gwUserVo.moblie']").val()) && !phone.test($("input[name='gwUserVo.moblie']").val())){
 			alert("请输入正确的手机号码或者固定电话。固定电话：区号与号码之间加-");
   		$("input[name='gwUserVo.moblie']").focus();
 	    return ;
	}
	//身份证验证
	var idReg = new RegExp(/^[1-9][0-9]{5}(19[0-9]{2}|200[0-9]|2010)(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[0-9]{3}[0-9xX]$/);
	if(!idReg.test($("input[name='gwUserVo.certNo']").val())){
		alert("请输入正确的身份证号码") ;
		$("input[name='gwUserVo.certNo']").focus();
		return ;
	}
	if($("input[name='gwUserVo.fileId']").val()=="" && $("input[name='upload']").val()==""){
		alert("请上传证件扫描附件");
		$("input[name='upload']").focus();
	 	return;
	}
	
	

		if ($('#userType').val() == 'orgUser') {
			if ($("input[name='gwOrgVo.orgName']").val() == "") {
				alert("请输入机构名称");
				$("input[name='gwOrgVo.orgName']").focus();
				return;
			}

			if ($("input[name='gwOrgVo.orgHeadName']").val() == "") {
				alert("请输入法人名称");
				$("input[name='gwOrgVo.orgHeadName']").focus();
				return;
			}
			//身份证验证

			var idReg = new RegExp(
					/^[1-9][0-9]{5}(19[0-9]{2}|200[0-9]|2010)(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[0-9]{3}[0-9xX]$/);
			if (!idReg.test($("input[name='gwOrgVo.certNo']").val())) {
				alert("请输入正确的法人身份证号码");
				$("input[name='gwOrgVo.certNo']").focus();
				return;
			}
			if ($("input[name='gwOrgVo.regCode']").val() == "") {
				alert("请输入工商编码");
				$("input[name='gwOrgVo.regCode']").focus();
				return;
			}
			//对手机、固话进行判断
			if (!mobile.test($("input[name='gwOrgVo.orgTel']").val())
					&& !phone.test($("input[name='gwOrgVo.orgTel']").val())) {
				alert("请输入正确的手机号码或者固定电话。固定电话：区号与号码之间加-");
				$("input[name='gwOrgVo.orgTel']").focus();
				return;
			}

			var isNotEdit = false;
			if ($("input[name='gwUserVo.userName']").val() == $("input[name='gwUserVo2.userName']").val()
					&& $("input[name='gwUserVo.email']").val() == $("input[name='gwUserVo2.email']").val()
					&& $("input[name='gwUserVo.moblie']").val() == $("input[name='gwUserVo2.moblie']").val()
					&& $("input[name='gwUserVo.certNo']").val() == $("input[name='gwUserVo2.certNo']").val()
					&& $("input[name='gwUserVo.addr']").val() == $("input[name='gwUserVo2.addr']").val()
					&& $("input[name='upload']").val() == ""
					&& $("input[name='gwOrgVo.orgName']").val() == $("input[name='gwOrgVo2.orgName']").val()
					&& $("input[name='gwOrgVo.orgHeadName']").val() == $("input[name='gwOrgVo2.orgHeadName']").val()
					&& $("input[name='gwOrgVo.certNo']").val() == $("input[name='gwOrgVo2.certNo']").val()
					&& $("input[name='gwOrgVo.regCode']").val() == $("input[name='gwOrgVo2.regCode']").val()
					&& $("input[name='gwOrgVo.orgAddr']").val() == $("input[name='gwOrgVo2.orgAddr']").val()
					&& $("input[name='gwOrgVo.orgTel']").val() == $("input[name='gwOrgVo2.orgTel']").val()) {
						isNotEdit = true;
					if (isNotEdit) {
						alert("没有已修改的字段");
						return;
					}
			}

		} else {
			if ($("select[name='gwUserVo.orgId']").is(":selected")) {
				alert("请选择机构");
				return;
			}
			//需要push文件到ftp
			if ($("[name=gwUserVo.pushFtp]")&& $("[name=gwUserVo.pushFtp]:checked").val() == '1') {
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
				if ($("input[name='gwUserVo.ftpPassword']").val() != $("#ftpPassword2").val()) {
					alert("PUSH到FTP的密码和确认密码不一致");
					$("#ftpPassword2").focus();
					return;
				}
			}
			var isNotEdit = false;
			if ($("input[name='gwUserVo.userName']").val() == $("input[name='gwUserVo2.userName']").val()
					&& $("input[name='gwUserVo.email']").val() == $("input[name='gwUserVo2.email']").val()
					&& $("input[name='gwUserVo.moblie']").val() == $("input[name='gwUserVo2.moblie']").val()
					&& $("input[name='gwUserVo.certNo']").val() == $("input[name='gwUserVo2.certNo']").val()
					&& $("input[name='gwUserVo.addr']").val() == $("input[name='gwUserVo2.addr']").val()
					&& $("select[name='gwUserVo.orgId']").val() == $("input[name='gwUserVo2.orgId']").val()
					&& $("input[name='user1.webserviceUrl']").val() == $("input[name='user.webserviceUrl']").val()
					&& $("input[name='user1.webserviceMethod']").val() == $("input[name='user.webserviceMethod']").val()
					&& $("input[name='user1.baseWsdl']").val() == $("input[name='user.baseWsdl']").val()
					&& $("input[name='upload']").val() == "") {
						isNotEdit = true;

					if ($("input[name='gwUserVo2.pushFtp']") && $("input[name='gwUserVo.pushFtp']:checked").val() == '1') {
						if ($("input[name='gwUserVo.ftpIp']").val() == $("input[name='gwUserVo2.ftpIp']").val()
							&& $("input[name='gwUserVo.ftpPort']").val() == $("input[name='gwUserVo2.ftpPort']").val()
							&& $("input[name='gwUserVo.ftpPath']").val() == $("input[name='gwUserVo2.ftpPath']").val()
							&& $("input[name='gwUserVo.ftpUsername']").val() == $("input[name='gwUserVo2.ftpUsername']").val()
							&& $("input[name='gwUserVo.ftpPassword']").val() == $("input[name='gwUserVo2.ftpPassword']").val()) {
								isNotEdit = true;
						} else {
							isNotEdit = false;
						}
					}else{
						if($("input[name='user1.pushFtp']").val() == ""){
						isNotEdit=true
					}
				}
					if (isNotEdit) {
						alert("没有已修改的字段");
						return;
					}
				}
		}
		$("#singupForm")
				.ajaxSubmit(
						{
							type : "POST",
							url : "system/gwUser_reSignUpUser.do",
							data : $("#singupForm").serialize(),
							dataType : "json",
							success : function(json) {
								if (json.state == 'success') {//成功
									alert($("input[name='gwUserVo.loginName']")
											.val()
											+ " 已经注册成功，请等待审核.");
									window.location.href = "system/workPlan_searchByParam.do";
									return;
								} else {
									alert(json.message);
								}
							}
						});
	}

	function delFile(hideSpan, showSpan) {
		if (confirm("是否确定删除附件？")) {
			$.ajax({
				type : "POST",
				url : "system/workPlan_deleteFile.do",
				data : $("#singupForm").serialize(),
				datatype : "json",
				success : function(data) {
					eval("json=" + data);
					if (json.state == 'success') {//成功
						alert("附件删除成功！");
						$("#fileId").val('');
						$("#" + hideSpan + ",#" + showSpan).toggle();
					} else {
						alert(data.message);
					}
				}
			});
		}
	}
	
/**ftp 验证*/
function ftpCheck() {
	//需要push文件到ftp
	if($("[name=gwUserVo.pushFtp]:checked").val()=='1'){
		if($("input[name='gwUserVo.ftpIp']").val()==""){
			alert("请输入将输出文件PUSH到FTP的IP地址");
			$("input[name='gwUserVo.ftpIp']").focus();
		 	return;
		}
		var ipTest =  new RegExp(/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/);
   		if(!ipTest.test($("input[name='gwUserVo.ftpIp']").val())){
   			alert("请输入正确的IP地址");
			$("input[name='gwUserVo.ftpIp']").focus();
		 	return;
   		}
		if(isNaN($("input[name='gwUserVo.ftpPort']").val())){
			alert("请输入正确的FTP端口");
			$("input[name='gwUserVo.ftpPort']").focus();
		 	return;
		}
		if($("input[name='gwUserVo.ftpUsername']").val()==""){
			alert("请输入将输出文件PUSH到FTP的用户名");
			$("input[name='gwUserVo.ftpUsername']").focus();
		 	return;
		}
		if($("input[name='gwUserVo.ftpPassword']").val()==""){
			alert("请输入将输出文件PUSH到FTP的密码");
			$("input[name='gwUserVo.ftpPassword']").focus();
		 	return;
		}
		var ftpIp= $("input[name='gwUserVo.ftpIp']").val();
		var ftpUsername= $("input[name='gwUserVo.ftpUsername']").val();
		var ftpPassword= $("input[name='gwUserVo.ftpPassword']").val();
		
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
		<b>用户注册退回</b>
  </div>
  <form id="singupForm" name="singupForm" >
    <input type="hidden" id="planId" name="planId" value="${planId}"/>
    <input type="hidden" id="oldPlanId" name="oldPlanId" value="${oldPlanId}"/>
	<input type="hidden" id="userId" name="gwUserVo.userId" value="${gwUserVo.userId }"/>
	<input type="hidden" id="userType" name="gwUserVo.userType" value="${gwUserVo.userType }"/>
	<input type="hidden" id="orgId" name="gwOrgVo.orgId" value="${gwOrgVo.orgId }"/>
	<input type="hidden" id="fileId" name="gwUserVo.fileId" value="${gwUserVo.fileId}"/>
	<input type="hidden" id="userIds" name="userId" value="${gwUserVo.userId }"/>
	
	<input type="hidden" name="gwUserVo2.userName" value="${gwUserVo.userName}"/>
	<input type="hidden" name="gwUserVo2.email" value="${gwUserVo.email}"/>
	<input type="hidden" name="gwUserVo2.moblie" value="${gwUserVo.moblie}"/>
	<input type="hidden" name="gwUserVo2.certNo" value="${gwUserVo.certNo}"/>
	<input type="hidden" name="gwUserVo2.addr" value="${gwUserVo.addr}"/>
	<input type="hidden" name="gwUserVo2.orgId" value="${gwUserVo.orgId}"/>

	<input type="hidden" name="gwUserVo2.pushFtp" value="${gwUserVo.pushFtp}"/>
	<input type="hidden" name="gwUserVo2.ftpIp" value="${gwUserVo.ftpIp}"/>
	<input type="hidden" name="gwUserVo2.ftpPort" value="${gwUserVo.ftpPort}"/>
	<input type="hidden" name="gwUserVo2.ftpUsername" value="${gwUserVo.ftpUsername}"/>
	<input type="hidden" name="gwUserVo2.ftpPassword" value="${gwUserVo.ftpPassword}"/>
	<input type="hidden" name="gwUserVo2.ftpPath" value="${gwUserVo.ftpPath}"/>
		
	<input type="hidden" name="gwOrgVo2.orgName" value="${gwOrgVo.orgName}"/>
	<input type="hidden" name="gwOrgVo2.orgHeadName" value="${gwOrgVo.orgHeadName}"/>
	<input type="hidden" name="gwOrgVo2.certNo" value="${gwOrgVo.certNo}"/>
	<input type="hidden" name="gwOrgVo2.regCode" value="${gwOrgVo.regCode}"/>
	<input type="hidden" name="gwOrgVo2.orgAddr" value="${gwOrgVo.orgAddr}"/>
	<input type="hidden" name="gwOrgVo2.orgTel" value="${gwOrgVo.orgTel}"/>
	
   <div class="main_info_title">审核信息</div>
	<table width="100%" border="0">
		  <tr >
		    <td width="10%" align="right" valign="top">审核人：</td>
		    <td width="90%" align="left">${createUser.loginName }</td>
		  </tr>
		  <tr >
		    <td width="10%" align="right" valign="top">审核时间：</td>
		    <td width="90%" align="left">${planVo.createTime }</td>
		  </tr>
		  <tr >
		    <td width="10%" align="right" valign="top">审核意见：</td>
		    <td width="90%" align="left"><s:textarea id="suggestion" name="suggestion" rows="6" cols="100" readonly="true"/></td>
		  </tr>
     </table>
     <div class="main_info_title">用户信息</div>
          <table width="100%" border="0">
              <tr>
      	  	  	  <td width="10%" align="right">用户类型：</td>
      	  	  	  <td width="23%"><g:sysDict dictCode="DICT_USER_TYPE" dictKey="${gwUserVo.userType }"/></td>
      	  	  	  <td width="10%" align="right">登录账号：</td>
      	  	  	  <td width="23%"><input name="gwUserVo.loginName" id="loginName" type="text"  value="${gwUserVo.loginName }" readonly style="color:gray" /></td>
		      	  <td></td>
      	  	  	  <td ></td>
      	  	  </tr>
      	  	  <tr>
      	  	  	  <td width="10%" align="right">姓名：</td>
      	  	  	  <td width="23%"><input name="gwUserVo.userName" id="userName" type="text"  value="${gwUserVo.userName }" /><span class="c_red">*</span></td>
      	  	  	  <td width="10%" align="right">邮箱：</td>
      	  	  	  <td width="23%"><input name="gwUserVo.email" id="email" type="text"  value="${gwUserVo.email }" /><span class="c_red">*</span></td>
		      	  <td width="10%" align="right">联系电话：</td>
      	  	  	  <td width="23%"><input name="gwUserVo.moblie" id="moblie" type="text"  value="${gwUserVo.moblie }" /><span class="c_red">*</span></td>
      	  	  </tr>
      	  	  <c:if test="${gwUserVo.userType == 'dataUser'}">
      	  	   <tr>
				  <th>PUSH FTP：</th>
				  <td>
					<label title="输出文件是否需要PUSH到指定FTP"><input name="gwUserVo.pushFtp" type="checkbox" value="1" ${gwUserVo.pushFtp=='1'?"checked=true":""} onclick="$('input[name=gwUserFtpTr]').toggle();$('tr[name=gwUserFtpTr]').toggle();"/>需要</label>
				  </td>
				  <td><input id="testFtp" name="gwUserFtpTr" type="button" value="测试FTP连接"  style="line-height:5px;padding:0px;font-size: 10px;width:70px;height:30px;" onclick="ftpCheck()"/>
			   </tr>
			   <tr name="gwUserFtpTr" ${gwUserVo.pushFtp=="1"?"":"style='display:none'"}>
				    <th>FTP IP：</th>
				    <td><input type="text" name="gwUserVo.ftpIp" value="${gwUserVo.ftpIp }" maxlength="15"/><span class="c_red">*</span></td>
			   		<th>FTP端口：</th>
				    <td><input type="text" name="gwUserVo.ftpPort" value="${gwUserVo.ftpPort }" maxlength="4"/><span class="c_red">*</span></td>
			   		<th>FTP目录路径：</th>
				    <td><input type="text" name="gwUserVo.ftpPath" value="${gwUserVo.ftpPath }"/></td>
			   </tr>
			   <tr name="gwUserFtpTr" ${gwUserVo.pushFtp=="1"?"":"style='display:none'"}>
				    <th>FTP用户名：</th>
				    <td><input type="text" name="gwUserVo.ftpUsername" value="${gwUserVo.ftpUsername }"/><span class="c_red">*</span></td>
				    <th>FTP密码：</th>
				    <td><input type="password" name="gwUserVo.ftpPassword" value="${gwUserVo.ftpPassword }"/><span class="c_red">*</span></td>
				    <th>FTP密码确认：</th>
				    <td><input type="password" id="ftpPassword2" value="${gwUserVo.ftpPassword}"/><span class="c_red">*</span></td>
			   </tr>
			  <tr name="gwUserTr">
			    <th>webservice URL：</th>
			    <td><input name="gwUserVo.webserviceUrl" type="text"  value="${gwUserVo.webserviceUrl}"/></td>
			    <th valign="top">webservice 方法名：</th>
			    <td><input name="gwUserVo.webserviceMethod" type="text"  value="${gwUserVo.webserviceMethod}"/></td> 
			    <th valign="top">webservice 包路径：</th>
			    <td><input name="gwUserVo.baseWsdl" type="text" value="${gwUserVo.baseWsdl}" /></td>      
			  </tr>
      	  	  </c:if>
      	  	  <tr>
      	  	  	  <td width="10%" align="right">身份证号：</td>
      	  	  	  <td><input name="gwUserVo.certNo" id="certNo" type="text"  value="${gwUserVo.certNo }" /><span class="c_red">*</span></td>
      	  	  	  <td width="10%" align="right">联系地址：</td>
      	  	  	  <td ><input name="gwUserVo.addr" id="addr" type="text"  value="${gwUserVo.addr }" /></td>
      	  	  	  <c:if test="${gwUserVo.userType == 'orgUser'}">
      	  	  	    <td></td>
      	  	  	    <td></td>
      	  	  	  </c:if>
      	  	  	  <c:if test="${gwUserVo.userType != 'orgUser'}">
		      	    <td width="10%" align="right">机构名称：</td>
				    <td><select name="gwUserVo.orgId">
				    <s:iterator id="item" value="#request.gwOrgs">
				        <s:if test="#request.gwUserVo.orgId == #item.orgId">
				        	<option value="<s:property value="orgId"/>"  selected="true"><s:property value="orgName" /></option>
				        </s:if>
				        <s:else>
				             <option value="<s:property value="orgId"/>" ><s:property value="orgName" /></option>
				        </s:else>
				    	
				    </s:iterator>
							</select><span class="c_red">*</span></td>
				  </c:if>
      	  	  </tr>
      	  	  <tr>
      	  	  	  <td width="10%" align="right">证件扫描附件:</td>
      	  	  	  <td>
	      	  	  	  <c:if test="${gwUserVo.fileId != null }">
	      	  	  	  	<span id="deleteFileSpan">
		      	  	  	    <a href="system/file_downLoadFile.do?fileId=${gwUserVo.fileId}" target="_blank">
		      	  	  	    <span class="c_red">${fileVo.realName}</span></a>
		      	  	  	    <a onclick="delFile('deleteFileSpan','uploadFileSpan')"><img src="resource/images/biaoti_f.gif"/></a>
	      	  	  	    </span>
	      	  	  	  </c:if>
	      	  	  	  <span style="${gwUserVo.fileId != null?'display:none':''}" id="uploadFileSpan">
	      	  	  	  	<input type="file" name="upload"  class="inputb" style="width:77%" /><span class="c_red">*</span>
	      	  	  	  </span>
      	  	  	  </td>
      	  	  	  <td></td>
      	  	  	  <td></td>
		      	  <td></td>
      	  	  	  <td></td>
      	  	  </tr>
		  </table>
		  <c:if test="${gwUserVo.userType == 'orgUser'}">
     <div class="main_info_title">机构信息</div>
          <table width="100%" border="0">
      	  	  <tr>
      	  	  	  <td width="10%" align="right">机构名称：</td>
      	  	  	  <td width="23%"><input name="gwOrgVo.orgName" id="orgName" type="text"  value="${gwOrgVo.orgName }" /><span class="c_red">*</span></td>
      	  	  	  <td width="10%" align="right">法人名称：</td>
      	  	  	  <td width="23%"><input name="gwOrgVo.orgHeadName" id="orgHeadName" type="text"  value="${gwOrgVo.orgHeadName }" /><span class="c_red">*</span></td>
		      	  <td width="10%" align="right">法人身份证号码：</td>
      	  	  	  <td width="23%"><input name="gwOrgVo.certNo" id="certNo" type="text"  value="${gwOrgVo.certNo }" /><span class="c_red">*</span></td>
      	  	  </tr>
      	  	  <tr>
      	  	  <td width="10%" align="right">工商编号：</td>
      	  	  	  <td><input name="gwOrgVo.regCode" id="regCode" type="text"  value="${gwOrgVo.regCode }" /><span class="c_red">*</span></td>
      	  	      <td width="10%" align="right">联系公司地址：</td>
      	  	  	  <td><input name="gwOrgVo.orgAddr" id="orgAddr" type="text"  value="${gwOrgVo.orgAddr }" /></td>
      	  	  	   <td width="10%" align="right">联系电话：</td>
      	  	  	  <td><input name="gwOrgVo.orgTel" id="orgTel" type="text"  value="${gwOrgVo.orgTel }" /><span class="c_red">*</span></td>
		      	  
      	  	  </tr>
		  </table>
		  </c:if>
		     <table width="100%" border="0">
      	  	  <tr>
               <td width="100%" align="center"><input name="" type="button" value="注册修改" onclick="reSingup()"/></td>
      	  	  </tr>
      	  	</table>
		  
  </form>
</body>
</html>
