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
<title>用户明细_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">
	function cancelUser(userId,loginName,userType){
		if(userType=='orgUser'){
			if(!confirm(loginName+"是机构用户，注销机构用户会将该机构下的所有数据用户也一起注销，确定注销?"))
				return false;
		}else if(!confirm("确定将"+loginName+"用户注销?")){
			return false;
		}
		
		url = "system/user_cancelUser.do?userId="+userId;
		$.post(url,null,function(json){
			alert(json.message);
			if(json.state=='success'){
				$("#cancelUser,#cancelAudit").toggle();
			}
		});
	}
	
</script>
</head>
<body>
<div class="main_title">
	<b>用户明细</b>
	<div class="main_tt_right fr">
		<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['cancelUser']!=null && user.confirmStatus == '2'}">
		<c:choose>
			<c:when test="${cancelAudit=='cancelAudit' }">
				<a href="javascript:void(0)" disabled style="color:gray">注销审核中</a>
			</c:when>
			<c:otherwise>
				<a href="javascript:void(0)" disabled style="color:gray;display:none" id="cancelAudit">注销审核中</a>
		        <a href="javascript:void(0)" onclick="cancelUser('${user.userId }','${user.loginName }','${user.userType }')" id="cancelUser">申请注销</a>
			</c:otherwise>
		</c:choose>
		</c:if>
    </div>
</div>
	<div class="main_form" method="post">
		<form id="updateUserForm" name="updateUserForm">
			<input type="hidden" name="realName" id="realName"/>
			<input type="hidden" name="filePath" id="filePath"/>
			<div class="main_info_title">用户信息</div>
			<div class="main_infomation">
			<table width="100%" border="0">
				<tr>				
					<td>用户类型：<g:sysDict dictCode="DICT_USER_TYPE" dictKey="${user.userType }"/></td>
					<td>登录帐号：${user.loginName}</td>
					<td>姓名：${user.userName}</td>
				</tr>
				<tr>
					<td>联系电话：${user.moblie}</td>
					<td>身份证号：${user.certNo}</td>
					<td>邮箱：${user.email}</td>
				</tr>
				<tr>
					<td>备注：${user.remark}</td>
					<td>联系地址：${user.addr}</td>
					<td>证件扫描附件:
						<c:if test="${user.fileId != null }">
      	  	  	    		<a href="system/file_downLoadFile.do?fileId=${user.fileId}" target="_blank">
      	  	  	    		<span class="c_red">${fileVo.realName}</span></a>
      	  	  	  		</c:if>
      	  	         </td>
				</tr>
				<c:if test="${user.ftpType == '2'}">
				
				<tr>
					<td>PUSH FTP：<c:if test="${user.pushFtp=='1'}">需要</c:if><c:if test="${user.pushFtp!='1'}">不需要</c:if> </td>
					<td>FTP 下载IP：${GwSysFtpVo.ftpIp}</td>
					<td>FTP 下载端口：${GwSysFtpVo.ftpPort}</td>
				</tr>
				<tr>
					<td>FTP 下载用户名：登录账号+"_down"</td>
					<td colspan=2>FTP 下载密码：登录密码+"_down"</td>
				</tr>
				<tr>
					<td>FTP 上传IP：${GwSysFtpVo2.ftpIp}</td>
					<td>FTP 上传端口：${GwSysFtpVo2.ftpPort}</td>
				</tr>
				</c:if>
				<c:if test="${user.ftpType == '1'}">
				<tr>
					<td>PUSH FTP：<c:if test="${user.pushFtp=='1'}">需要</c:if><c:if test="${user.pushFtp!='1'}">不需要</c:if> </td>
					<td>FTP 下载IP：${user.ftpIp}</td>
					<td>FTP 下载端口：${user.ftpPort}</td>
				</tr>
				<tr>
					<td>FTP 下载目录路径：${user.ftpPath}</td>
					<td>FTP 下载用户名：${user.ftpUsername}</td>
					<td colspan=2>FTP 下载密码：${user.ftpPassword}</td>
				</tr>
				</c:if>
				<tr>
					<td>webservice URL：${user.webserviceUrl}</td>
					<td>webservice 方法名：${user.webserviceMethod}</td>
					<td>webservice 包路径：${user.baseWsdl}</td>
				</tr>
				<tr><td style="color:red">提示：FTP上传用户名、密码与gateway系统登录用户名、密码一致。</td></tr>
			</table>
			</div>
				<c:if test="${user.userType=='dataUser' || user.userType=='orgUser' }">
				<div class="main_info_title">机构信息</div>
				<div class="main_infomation">
				<table width="100%" border="0">
					<tr>
						<td>机构名称：${org.orgName}</td>
						<td>法人名称：${org.orgHeadName}</td>
						<td>法人身份证号码：${org.certNo}</td>
					</tr>
					<tr>					
						<td>工商编码：${org.regCode}</td>
						<td>联系电话：${org.orgTel}</td>
						<td>公司地址：${org.orgAddr}</td>
					</tr>
					
				</table>
				</div>
				</c:if>
				<div class="main_infomation" style="text-align:center">
				<table width="100%" border="0">
					<tr>
						<td colspan=2>
							<c:if test="${user.confirmStatus == '1'}">
					 			<input name="" type="button" value="注册待审" onclick="" style="color:gray" />
					 		</c:if>
					 		<c:if test="${user.confirmStatus == '0'}">
					 			<input name="" type="button" value="审核未通过" onclick="" style="color:gray" />
					 		</c:if>
							<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['updateUser']!=null && user.confirmStatus=='2'}">
						 	<c:choose>
								<c:when test="${updateMap[user.userId] == null }">
									<input name="" type="button" value="修改" onclick="location.href='system/user_searchSinglerUser.do?user.userId=${user.userId}'" />
								</c:when>
								<c:otherwise>
									<input name="" type="button" value="修改待审" onclick="" style="color:gray" />
								</c:otherwise>
							</c:choose>
							</c:if>
							<c:if test="${sourceFlag==2}">
								<input name="" type="button" value="返回" onclick="history.go(-1)" />
							</c:if>
						</td>
					</tr>				
				</table>
				</div>
		</form>
	</div>
</body>
</html>
