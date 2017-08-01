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
<script type="text/javascript">
function checkAndSave(){
    if($("#fetchType option:selected").val()==''){
	alert("请选择提数类型");
	return false;
	}
	if($("#cycleType option:selected").val()==''){
	alert("请选择周期类型");
	return false;
	}
	var boolean = true;
	$("#cycleNum").each(function(){
		if($(this).val()==''){
			alert("周期数不能为空");
			$(this).focus();
			boolean = false;
			return false;
		}
	
		if(isNaN($(this).val())){
			alert("周期数只能为数字");
			$(this).select();	
			boolean = false;		
			return false;
		}
		
		if($("#fetchType").val()==2 && $(this).val()==0){
		   alert("提数类型为周期，周期数不能为零");
		   $(this).focus();
			boolean = false;
			return false;
		}
	});
	if(!boolean) return false;
	
	document.getElementById("appForm").submit(); 
	
}
function goBack(){
    location.href="../service/modelDataApp_getServiceAppList.do";
}

	$(document).ready(function(){
		$("#fetchType").bind("change",function(){
		   //alert($(this).val());
		   if($(this).val()==1){
		   	  $("#cycleType").val(5);
		   	  $("#tr_cycle").hide();
		   	  $("#cycleNum").val(0);
		   	  $("#cycleNum").attr("readonly",true);
		   }else{
		   	  $("#tr_cycle").show();
		      $("#cycleNum").attr("disabled",false);
		   }
		});
	});
</script>
<title>用户模型取数申请填写</title>
</head>
<body class="FrameMain">
<form  id="appForm" name="appForm" action="service/modelDataApp_saveApp.do">
<input type="hidden" id="fetchVO.fetchId" name="fetchVO.fetchId" value="${fetchVO.fetchId}"/>
<input type="hidden" id="fetchVO.userId" name="fetchVO.userId" value="${fetchVO.userId}"/>
<input type="hidden" id="fetchVO.serviceId" name="fetchVO.serviceId" value="${fetchVO.serviceId}"/>

	<div class="main_title">
		<b>模型服务取数申请填写</b>
	</div>
	<div class="main_form">
		<table width="100%" border="0">
		  <tr>
		    <th>服务名称：</th>
		    <td>${serviceVo.serviceName}</td>
		  </tr>
		  <tr>
		    <th>提数类型：</th>
		    <td>
		    <g:sysDictList defaultValue="${fetchVO.fetchType}" dictCode="DICT_GET_DATA_TYPE" tagType="select" tagName="fetchVO.fetchType" tagId="fetchType"/>
		    <span class="c_red">*</span>
		   </td>
		  </tr>
		  <tr id="tr_cycle" >
		    <th>周期类型：</th>
		    <td>
		    <g:sysDictList defaultValue="${fetchVO.cycleType}" dictCode="DICT_CYCLE_TYPE" tagType="select" tagName="fetchVO.cycleType" tagId="cycleType"/>
		    <span class="c_red">*</span>
		   </td>
		  </tr>
		  <tr>
		    <th>周期数：</th>
		    <td><input id="cycleNum" name="fetchVO.cycleNum" type="text" value="${fetchVO.cycleNum}"/><span class="c_red">*</span></td>
		  </tr>
		  <tr>
		    <td>&nbsp;</td>
		    <td>
		    <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['appData']!=null}">
		    	<input name="" type="button" value="申请" onclick="checkAndSave()"/>
		    </c:if>
		    &nbsp;&nbsp;&nbsp;&nbsp;
		    <input name="" type="button" value="返回" onclick="goBack()"/>
		    </td>
		  </tr>
		</table>
	</div>
	</form>
</body>

</html>