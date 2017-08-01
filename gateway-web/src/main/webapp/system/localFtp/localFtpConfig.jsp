<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath %>" />
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>本地Ftp配置_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">	
function saveLocalFtp() {
	$("input[type=text]").each(function(){
		$(this).val($.trim($(this).val()));
	});

	var boolean = true;
	$("#localUploadFtpIP,#localDownloadFtpIP").each(function(){
		var IP = $(this).val();
		if(IP==''){
			alert("IP不能为空");
			$(this).focus();
			boolean = false;
			return false;
		}
	
		if(IP.search(/^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/)<0) {
				alert("请填写合法的IP地址,如192.168.1.1！");
				$(this).focus();
				boolean = false;
				return false;
			}
	}); 
	$("#localUploadFtpPort,#localDownFtpPort").each(function(){
		if($(this).val()==''){
			alert("端口号不能为空");
			$(this).focus();
			boolean = false;
			return false;
		}
	
		if(isNaN($(this).val())){
			alert("端口号只能为数字");
			$(this).select();	
			boolean = false;		
			return false;
		}
	});
	if(!boolean) return false;
	
	$("#saveLocalFtpBtn").attr("disabled",true);
	url = "system/local_localFtpComfig.do";
   	$.post(url,$("#localFtpConfigForm").serialize(),function(json){
   		$("#saveLocalFtpBtn").attr("disabled",false);
		if (json.state == 'success') {
			alert("保存成功");
		}else{
			alert("保存失败，原因："+json.message);
		}
   	});
}

</script>
</head>	
<body>
	<div class="main_form" method="post">
	<form id="localFtpConfigForm" name="localFtpConfigForm">
            <div class="main_title">
                <b>本地Ftp配置</b>
            </div>
		
		<table width="50%" border="0" style="padding:20px">
			<tr>
				<td style="width:30%"><font color='red'>*</font>类型</td>
				<td style="width:35%"><font color='red'>*</font>IP地址</td>
				<td style="width:35%"><font color='red'>*</font>端口号</td>
			</tr>
			<tr>
				<td>本地上传Ftp地址</td>
				<td>
					<input  type="hidden" id="localUploadId" name="localUploadId" value="${Id}"/>
					<input  type="hidden" id="localUploadFtpType" name="localUploadFtpType" value="localUpVO.ftpType"/>
					<input style="width:60%" type="text" id="localUploadFtpIP" name="localUpVO.ftpIp" maxlength="15" value="${localUpVO.ftpIp }"/>
				</td>
				<td><input style="width:30%" type="text" id="localUploadFtpPort" name="localUpVO.ftpPort" value="${localUpVO.ftpPort }"/></td>
			</tr>
			<tr>
				<td>本地下载Ftp地址</td>
				<td>
					<input  type="hidden" id="localDownloadId" name="localDownloadId" value="${Id2}"/>
					<input  type="hidden" id="localDownloadFtpType" name="localDownloadFtpType" value="localDownVO.ftpType"/>
					<input  style="width:60%" type="text" id="localDownloadFtpIP" name="localDownVO.ftpIp" maxlength="15" value="${localDownVO.ftpIp }"/>
				</td>
				<td><input style="width:30%" type="text" id="localDownFtpPort" name="localDownVO.ftpPort" value="${localDownVO.ftpPort }"/></td>
			</tr>
			<tr>
				<td colspan=3 style="height:20px"></td>
			</tr>
			<tr>
				<td colspan=3  style="text-align:center">
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['saveDataCycle']!=null}">
						<input name="saveLocalFtpBtn" id="saveLocalFtpBtn" type="button" value="保存" onclick="saveLocalFtp()"/>
					</c:if>
				</td>
			</tr>
		</table>
	</form>
	</div>

</body>
</html>
