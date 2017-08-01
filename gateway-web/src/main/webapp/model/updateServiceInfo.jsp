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
<title>修改服务_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript">	
$(function(){
	$("#serviceType option[value=${serviceVO.serviceType}]").attr("selected",true);
	toggleServiceType(${serviceVO.serviceType});
	
	$("#serviceType").on("change",function(){
		toggleServiceType();
	});
}) 

	function toggleServiceType(_serviceType){
		var serviceType = $("#serviceType :selected").val();
		if(_serviceType) serviceType = _serviceType;
		if(serviceType==0){
			$("tr[name=serviceType1] select,tr[name=serviceType1] input").prop("disabled",true);
			$("tr[name=serviceType1] span").hide();
		}else{
			$("tr[name=serviceType1] select,tr[name=serviceType1] input").prop("disabled",false);
			$("tr[name=serviceType1] span").show();
		}
	}
	function saveService(){
		if(check()){
			$("#saveServiceBtn,#saveingServiceBtn").toggle();
			util.ajax($("#serviceForm").attr("action"),$("#serviceForm").serialize(),function(json){
				$("#saveServiceBtn,#saveingServiceBtn").toggle();
				if(json.state=='success'){
					alert("修改服务成功");
					goBack();
				}else{
					alert(json.message);				
				}
			});
		}
	}
	
	function goBack(){
		location.href="<%=basePath %>service/serviceInfo_searchServiceList.do";
	}
	
	function check(){
		$("#serviceForm input").each(function(){
			$(this).val($.trim($(this).val()));
		});
		
		if($("#serviceCode").val()==''){
			alert("服务编码不能为空");
			$("#serviceCode").focus();
			return false;
		}
		if(!/^[0-9a-zA-Z]+$/.test($("#serviceCode").val())){
			alert("服务编码只能为字母和数字，请重新输入");
			$("#serviceCode").focus();
			return false;
		}
		if($("#serviceName").val()==''){
			alert("服务名称不能为空");
			$("#serviceName").focus();
			return false;
		}
		if($("#serviceCode").val()==''){
			alert("服务编码不能为空");
			$("#serviceCode").focus();
			return false;
		}
		var serviceType = $("#serviceType :selected").val();
		if(serviceType == 1){
			if($("#cycleType :selected").val()==''){
				alert("请选择周期类型");
				return false;
			}
			if($("#cycleDay").val()==''){
				alert("周期天数不能为空");
				$("#cycleDay").focus();
				return false;
			}
			if(isNaN($("#cycleDay").val())){
				alert("周期天数只能为数字");
				$("#cycleDay").focus();
				return false;
			}
			if($("#cycleDay").val()<1 || $("#cycleDay").val()>31){
				alert("周期天数只能在1-31之间");
				$("#cycleDay").focus();
				return false;
			}
		}
		return true;
	}
</script>
</head>	
<body>
	<div class="main_form" method="post">
	<form id="serviceForm" name="serviceForm" action="service/serviceInfo_updateServiceInfo.do">
		<input type="hidden" name="serviceVO.serviceId" value="${serviceVO.serviceId}"/>
		<input type="hidden" name="oldServiceCode" value="${serviceVO.serviceCode}"/>
        <div class="main_title">
            <b>修改服务</b>
        </div>
		
		<table width="50%" border="0" style="padding:20px">
			<tr>
			    <th>服务编码：</th>
			    <td><input name="serviceVO.serviceCode" id="serviceCode" type="text" value="${serviceVO.serviceCode}" maxlength="15"/><span class="c_red">*</span></td>
			</tr>
			<tr>
			    <th>服务名称：</th>
			    <td><input name="serviceVO.serviceName" id="serviceName" type="text" value="${serviceVO.serviceName}"/><span class="c_red">*</span></td>
			</tr>
			<tr>
			    <th>服务类型：</th>
			    <td>
			     <select name="serviceVO.serviceType" id="serviceType">
			    	<option value="1">离线</option>
			    	<option value="0">实时</option>
			    </select>
				<span class="c_red">*</span>
			    </td>
			</tr>
			<tr name="serviceType1">
			    <th>周期类型：</th>
			    <td><g:sysDictList dictCode="DICT_CYCLE_TYPE" tagType="select" tagId="cycleType" tagName="serviceVO.cycleType" defaultValue="${serviceVO.cycleType}"/><span class="c_red">*</span></td>
			</tr>
			<tr name="serviceType1">
			    <th>周期天数：</th>
			    <td><input name="serviceVO.cycleDay" id="cycleDay" type="text" value="${serviceVO.cycleDay}"/><span class="c_red">*</span></td>
			</tr>
			<tr>
				<td colspan=2  style="text-align:center">
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['editService']!=null}">
						<input name="saveServiceBtn" id="saveServiceBtn" type="button" value="保存" onclick="saveService()"/>
						<input name="saveingServiceBtn" type="button" value="保存中" id="saveingServiceBtn" style="display:none;color:gray" disabled/>
						<input name="backBtn" id="backBtn" type="button" value="返回" onclick="goBack()"/>
					</c:if>
				</td>
			</tr>
		</table>
	</form>
	</div>

</body>
</html>
