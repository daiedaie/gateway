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
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/artDialog/dialog-min.js"></script>
<link rel="stylesheet" href="resource/css/ui-dialog.css" type="text/css" />
<script>
	function getparam() {
		var query = location.search.substring(1);
		var params = query.split("&");
		//taskId参数
		var taskIdStr=params[0];
		var taskIdIndex=taskIdStr.indexOf('=');
		var taskIdValue=taskIdStr.substring(taskIdIndex + 1);
		$("#taskId").val(taskIdValue);
		
		//fileType参数
		var fileTypeStr=params[1];
		var fileTypeIndex=fileTypeStr.indexOf('=');
		var fileTypeValue=fileTypeStr.substring(fileTypeIndex + 1);
		$("#fileType").val(fileTypeValue);
		
		//rows参数
		var rowsStr=params[2];
		var rowsIndex=rowsStr.indexOf('=');
		var rowsValue=rowsStr.substring(rowsIndex + 1);
		$("#rows").val(rowsValue);
	}

	function downLoadData() {
		
		if ($("input[name='startRow']").val() == "") {
			alert("请输入开始行号！");
			$("input[name='startRow']").focus();
			return;
		}
		if ($("input[name='endRow']").val() == "") {
			alert("请输入结束行号！");
			$("input[name='endRow']").focus();
			return;
		}
		if(isNaN($("input[name='startRow']").val())){
			alert("开始行号只能为数字！");
			$("input[name='startRow']").focus();
			return false;
		}
		if(isNaN($("input[name='endRow']").val())){
			alert("结束行号只能为数字！");
			$("input[name='endRow']").focus();
			return false;
		}
		if(Number($.trim($("input[name='startRow']").val())) >=Number($.trim($("input[name='endRow']").val())) ){
			alert("输入有误！(结束行号必须大于开始行号)！");
			return false;
		}
		if($.trim($("input[name='endRow']").val()) - $.trim($("input[name='startRow']").val()) >= $("input[name='rows']").val()){
			alert("输入有误！(抽样行数必须小于文件总行数)！");
			return false;
		}
		if(Number($.trim($("input[name='endRow']").val())) - Number($.trim($("input[name='startRow']").val())) >=100){
			alert("输入有误！(最多抽样100行)！");
			return false;
		}
		if($.trim($("input[name='startRow']").val()) =="0"){
			alert("输入有误！(开始行号必须从1开始)！");
			return false;
		}
		var form=document.getElementById("downLoadForm");
		
	    form.submit();
	}
	
	function closeDialog(){
		parent.location.reload();
	}
</script>
<title></title>
</head>
  <body class="FrameMain" onload = "getparam();">
	
	<div class="main_title">
		<b>请输入抽样条件</b>
	</div>
	<form  id="downLoadForm" method="post" action="service/modelTask_sampleDownloadFtpFile.do">
	<input type="hidden" name="taskId" id="taskId"/>
	<input type="hidden" name="fileType" id="fileType"/>
	<table width="100%" border="0">
		<tr>
			<td>文件总行数 :</td>
			<td><input name="rows" id="rows" type="text" style="border:0px;" readonly/></td>
		</tr>
		<tr>
			<td style="width:200%;">抽样行数<span style="color:red;">(最多抽样100行)</span>：</td>
			<td><input type="text" name="startRow"  style="width:50px;" value=""/>至<input type="text" name="endRow" style="width:50px;" value=""/></td>
		</tr>
	</table>
	<br/>
	<div align="center">
			<input id="startDownLoad" type="button" value="确定" onclick="downLoadData();" />
			<input id="cancelDownLoad" type="button" value="返回" onclick="closeDialog();"/>
	</div>
	</form>
  </body>
</html>
