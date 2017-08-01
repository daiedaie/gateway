<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="g" uri="/gateway-tags" %>
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
<title>机构修改页</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/form/jquery-1.3.2.js"></script>
<script type="text/javascript" src="resource/js/form/jquery.form.js"></script>
<script>
function checkOrg(){
	$.ajax({
			type : "POST",
			url : "system/org_searchOrgName.do",
			data : $("#updateOrgForm").serialize(),
			success : function(data) {		
				if (data.state == 'failure') {
					alert(data.message);
				} else {
				}				
			}			
		});
}
function updateOrg(){
	if($("input[name='userVO.userName']").val()==""){
		alert("请输入姓名！");
		$("input[name='userVO.userName']").focus();
		 return;
	}
	//验证手机号码是否符合格式
	var mobile = /^(1\d{10})$/;    //手机号码
	var phone = /(^[0-9]{3,4}\-[0-9]{7,8}$)/;   //固话
	
	if(!mobile.test($("input[name='userVO.moblie']").val()) && !phone.test($("input[name='userVO.moblie']").val())){
 			alert("请输入正确的手机号码或者固定电话。固定电话：区号与号码之间加-");
   		$("input[name='gwUserVo.moblie']").focus();
 	    return ;
	}
	//验证身份证号是否符合格式
	var idReg = new RegExp(/^[1-9][0-9]{5}(19[0-9]{2}|200[0-9]|2010)(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[0-9]{3}[0-9xX]$/);
	if(!idReg.test($("input[name='userVO.certNo']").val())){
		alert("请输入正确的身份证号码！") ;
		$("input[name='userVO.certNo']").focus();
		return ;
	}
	//验证邮箱
	var email = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if(!email.test($("input[name='userVO.email']").val())){
		alert("请输入正确的邮箱！");
		$("input[name='userVO.email']").focus();
		return;
	}
	if($("input[name='orgVO.orgName']").val()==""){
		alert("请输入机构名称！");
		$("input[name='orgVO.orgName']").focus();
		return;
	}
	
	if(!idReg.test($("input[name='orgVO.certNo']").val())){
		alert("请输入正确的证件编号！") ;
		$("input[name='orgVO.certNo']").focus();
		return ;
	}
	if($("input[name='orgVO.orgHeadName']").val()==""){
		alert("请输入法人名称！");
		$("input[name='orgVO.orgHeadName']").focus();
		 return;
	}
	
	//上传图片验证
	if ($("input[name='upload']").val()!="" && (!/\.(rar|zip)$/.test($("input[name='upload']").val()))) {
		alert("证件扫描附件必须是压缩打包文件，且打包类型必须是.RAR,ZIP中的一种！");
		$("input[name='upload']").focus();
		return;
	}
	
	
	if(!mobile.test($("input[name='orgVO.orgTel']").val()) && !phone.test($("input[name='orgVO.orgTel']").val())){
 			alert("请输入正确的手机号码或者固定电话。固定电话：区号与号码之间加-");
   		$("input[name='orgVO.orgTel']").focus();
 	    return ;
	}
	if($("input[name='orgVO.regCode']").val()==""){
		alert("请输入工商编码");
		$("input[name='orgVO.regCode']").focus();
		return;
	}
	if($("input[name='userVO2.userName']").val() == $("input[name='userVO.userName']").val()&&
			$("input[name='userVO2.moblie']").val() == $("input[name='userVO.moblie']").val()&&
			$("input[name='userVO2.certNo']").val() == $("input[name='userVO.certNo']").val()&&
			$("input[name='userVO2.email']").val() == $("input[name='userVO.email']").val()&&
			$("input[name='userVO2.addr']").val() == $("input[name='userVO.addr']").val()&&
			
			$("input[name='orgVO2.orgName']").val() == $("input[name='orgVO.orgName']").val()&&
			$("input[name='orgVO2.certNo']").val() == $("input[name='orgVO.certNo']").val()&&
			$("input[name='orgVO2.orgHeadName']").val() == $("input[name='orgVO.orgHeadName']").val()&&
			$("input[name='orgVO2.orgTel']").val() == $("input[name='orgVO.orgTel']").val()&&
			$("input[name='orgVO2.regCode']").val() == $("input[name='orgVO.regCode']").val()&&
			$("input[name='orgVO2.orgAddr']").val() == $("input[name='orgVO.orgAddr']").val()&&
			$("input[name='upload']").val() ==""){
				alert("请修改字段！");
	 			return;
	}
	$("#update,#updateIng").toggle();
	$("#updateOrgForm").ajaxSubmit({
		type : "POST",
		url : "system/org_updateOrg.do",
		data : $("#updateOrgForm").serialize(),
		dataType:"json",
		success : function(data) {
			if (data.state == 'success') {
				if($("#isAuth").val()=="true"){
					$("#update,#updateIng").toggle();
					alert("修改成功!");
				}else{
					$("#update,#updateIng").hide();
					$("#updateWaitAudit").show();
					alert("申请修改成功，等待审核！");
				}
			} else {
				$("#update,#updateIng").toggle();
				alert(data.message);
			}
		}			
	});
}

function delFile(hideSpan,showSpan){
	$("#"+hideSpan+",#"+showSpan).toggle();
	return;
    if(confirm("是否确定删除附件？")){
       $.ajax({
		type: "POST",
		url: "system/workPlan_deleteFile.do",
		data: $("#singupForm").serialize(),
		datatype: "json",
		success: function(data){
		    eval("json="+data);
			if (json.state == 'success') {//成功
				alert("附件删除成功！" );
				$("#"+hideSpan+",#"+showSpan).toggle();
			} else {
				alert(data.message);
			}
		}
   	});
    }
}
</script>
</head>
<body class="FrameMain">

<div class="main_title">
	<b>用户修改申请</b>
</div>
<form id="updateOrgForm" name="updateOrgForm" method="post" enctype="multipart/form-data">
<div class="main_info_title">机构用户信息</div>

<input name="userVO.userId" type="hidden" value="${userVO.userId}" />
<input name="userVO.orgId" type="hidden" value="${userVO.orgId}" />
<input name="orgVO.orgId" type="hidden" value="${orgVO.orgId}" />
<input name="userVO2.userName" type="hidden" value="${userVO.userName}" />
<input name="userVO2.moblie" type="hidden" value="${userVO.moblie}" />
<input name="userVO2.certNo" type="hidden" value="${userVO.certNo}" />
<input name="userVO2.email" type="hidden" value="${userVO.email}" />
<input name="userVO2.addr" type="hidden" value="${userVO.addr}" />

<input name="orgVO2.orgName" type="hidden" value="${orgVO.orgName}" />
<input name="orgVO2.certType" type="hidden" value="${orgVO.certType}" />
<input name="orgVO2.certNo" type="hidden" value="${orgVO.certNo}" />
<input name="orgVO2.orgHeadName" type="hidden" value="${orgVO.orgHeadName}" />
<input name="orgVO2.orgTel" type="hidden" value="${orgVO.orgTel}" />

<input name="orgVO2.regCode" type="hidden" value="${orgVO.regCode}" />
<input name="orgVO2.orgAddr" type="hidden" value="${orgVO.orgAddr}" />
<input id="isAuth" type="hidden" value="${isAuth}" />
	<table width="100%" border="0">
        <tr>
            <td width="10%" align="right">登录账号：</td>
            <td width="23%"><input name="userVO.loginName" type="text" value="${userVO.loginName}" onfocus="this.blur()"style="border:0"/></td>
            <td width="10%" align="right">用户类型：</td>
            <td width="23%"><input name="userVO.userType" type="text" value="<g:sysDict dictCode="DICT_USER_TYPE" dictKey="${userVO.userType}"/>" onfocus="this.blur()"style="border:0"/></td>
         	<td></td>
      	  	<td ></td>
        </tr>
        <tr>
        	<td width="10%" align="right">姓名：</td>
      	  	<td width="23%"><input id="userName" name="userVO.userName" type="text" value="${userVO.userName}" /><span class="c_red">*</span></td>
			<td width="10%" align="right">联系电话：</td>
      	  	<td width="23%"><input id="moblie" name="userVO.moblie" type="text" value="${userVO.moblie}" /><span class="c_red">*</span></td>
			<td width="10%" align="right">身份证号：</td>
      	  	<td><input name="userVO.certNo" type="text" value="${userVO.certNo}" /><span class="c_red">*</span></td>
			
         
        </tr>
        <tr>
        	<td width="10%" align="right">邮箱：</td>
      	  	<td width="23%"><input name="userVO.email" type="text" value="${userVO.email}" /><span class="c_red">*</span></td>
            <td width="10%" align="right">联系地址：</td>
      	  	<td ><input name="userVO.addr" type="text" value="${userVO.addr}" /></td>
        </tr>
        <tr>
        	<td width="10%" align="right">原有证件扫描附件：</td>
    		<td width="23%">
    			<c:if test="${userVO.fileId != null }">
      	  	  	    <a href="system/file_downLoadFile.do?fileId=${userVO.fileId}" target="_blank">
      	  	  	    <span class="c_red">${fileVo.realName}</span></a>
      	  	  	</c:if>
    		</td>
    		<td width="10%" align="right">修改证件扫描附件：</td>
    		<td width="23%">
    			<input type="file" name="upload"  class="" style="width:80%" /><span class="c_red">*</span>
    		</td>
        </tr>
    </table>


<div class="main_info_title">机构信息</div>
	<table width="100%" border="0">
        <tr>
        	<td width="10%" align="right">机构名称：</td>
      	  	<td width="23%"><input id="orgName" name="orgVO.orgName" type="text" value="${orgVO.orgName}" onblur="checkOrg();" /><span class="c_red">*</span></td>
            <td width="10%" align="right">法人名称：</td>
      	  	<td width="23%"><input id="orgHeadName" name="orgVO.orgHeadName" type="text" value="${orgVO.orgHeadName}" /><span class="c_red">*</span></td>
        	<td width="10%" align="right">证件编号：</td>
      	  	<td width="23%"><input id="certNo" name="orgVO.certNo" type="text" value="${orgVO.certNo}" /><span class="c_red">*</span></td>
          
        </tr>
        <tr>
        	
           	<td width="10%" align="right">联系电话：</td>
      	  	<td width="23%"><input id="orgTel" name="orgVO.orgTel" type="text" value="${orgVO.orgTel}" /><span class="c_red">*</span></td>
            <td width="10%" align="right">工商编码：</td>
      	  	<td width="23%" colspan=3><input name="orgVO.regCode" type="text" value="${orgVO.regCode}" /><span class="c_red">*</span></td>
        </tr>
        <tr>
         	<td width="10%" align="right">公司地址：</td>
      	  	<td colspan=5><input name="orgVO.orgAddr" type="text" value="${orgVO.orgAddr}" style="width:55%"/></td>
        </tr>
    </table>

</br>
</br>
<div align="center">
	<input type="button" onclick="updateOrg()" ${isAuth==false? "value='申请修改'":"value='修改'"} id="update"/>
	<input type="button" onclick="" value="正在修改" id="updateIng" disabled style="color:gray;display:none"/>
	<input type="button" onclick="" value="修改待审" id="updateWaitAudit" disabled style="color:gray;display:none"/>
	<input type="button" onclick="history.go(-1)" value="返回"/>
</div>
</form>
</body>

</html>
