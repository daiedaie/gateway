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
	function addDoc(){
		if(check()){
			var action = $("#addForm").attr("action");
			$("#addDocBtn,#addingDocBtn").toggle();
			$("#addForm").ajaxSubmit({
				type : "POST",
				url : action,
				data : $("#addForm").serialize(),
				dataType : "json",
				success : function(json) {
					$("#addDocBtn,#addingDocBtn").toggle();
					if(json.state=='success'){
						alert("保存成功");
						location.href="system/doc_searchDocList.do";
					}else{
						alert("保存失败："+json.message);
					}
				},
				error : function() {
					$("#addDocBtn,#addingDocBtn").toggle();
					alert("保存失败，请联系管理员");
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
	<b>新增帮助文档信息</b>
</div>
	<form id="addForm" method="post" action="system/doc_saveDoc.do">
		<table width="100%" border="0" cellspacing="10px">
			<tr>
				<th>文档：</th>
				<td>
					<input type="file" name="upload" id="upload"/><span class="c_red">*</span>
				</td>
			</tr>
			<tr>
				<th valign="top">帮助说明：</th>
				<td>
				<textarea rows="10" cols="30" style="width:500px;height:200px" name="doc.docDesc" id="docDesc"></textarea>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['addDoc']!=null}">
					<input name="addDocBtn" id="addDocBtn" type="button" value="确定" onclick="addDoc()"/>
					<input name="addingDocBtn" id="addingDocBtn" type="button" value="正在保存" style="display:none;color:gray" disabled/>
					<input type="button" value="返回" onclick="history.go(-1)"/>
					</c:if>
				</td>
			</tr>
		</table>
	</form>
</body>
</html>
