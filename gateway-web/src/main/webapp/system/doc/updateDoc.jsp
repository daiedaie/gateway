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
<title>帮助信息_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/form/jquery-1.3.2.js"></script>
<script type="text/javascript" src="resource/js/form/jquery.form.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script>	
	function updateDoc(){
		if(check()){
			var action = $("#updateForm").attr("action");
			$("#updateDocBtn,#updateingDocBtn").toggle();
			$("#updateForm").ajaxSubmit({
				type : "POST",
				url : action,
				data : $("#updateForm").serialize(),
				dataType : "json",
				success : function(json) {
					$("#updateDocBtn,#updateingDocBtn").toggle();
					if(json.state=='success'){
						alert("修改成功");
						location.href="system/doc_searchDocList.do";
					}else{
						alert("修改失败："+json.message);
					}
				},
				error : function() {
					$("#addDocBtn,#addingDocBtn").toggle();
					alert("修改失败，请联系管理员");
				}
			});
		}
	}
	
	function check(){
		if($("#upload").val()==''){
			alert("文件不能为空");
			return false;
		}
		return true;
	}
</script>
</head>	
<body>
<div class="main_title">
	<b>修改帮助文档信息</b>
</div>
	<form id="updateForm" method="post" action="system/doc_saveDoc.do">
		<input type="hidden" name="doc.docId" value="${doc.docId}"/>
		<input type="hidden" name="preFileId" value="${doc.fileVO.fileId}"/>
		<table width="100%" border="0" cellspacing="10px">
			<tr>
				<th>修改前文档：</th>
				<td>
					<a name="preFileId" value="${doc.fileVO.fileId}" href="system/file_downLoadFile.do?fileId=${doc.fileVO.fileId}" target="_blank" style="color:blue">${doc.fileVO.realName}</a>
				</td>
			</tr>
			<tr>
				<th>文档：</th>
				<td>
					<input type="file" name="upload"/><span class="c_red">*</span>
				</td>
			</tr>
			<tr>
				<th valign="top">帮助说明：</th>
				<td>
				<textarea rows="10" cols="30" style="width:500px;height:160px" name="doc.docDesc" id="docDesc">${doc.docDesc}</textarea>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['updateDoc']!=null}">
					<input name="updateDocBtn" id="updateDocBtn" type="button" value="确定" onclick="updateDoc()"/>
					<input name="updateingDocBtn" id="updateingDocBtn" type="button" value="正在保存" style="display:none;color:gray" disabled/>
					<input type="button" value="返回" onclick="history.go(-1)"/>
					</c:if>
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
