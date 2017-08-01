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
<title>新增服务_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript">	
	function saveService(){
		if(check()){
			$("#saveServiceBtn,#saveingServiceBtn").toggle();
			util.ajax($("#serviceForm").attr("action"),$("#serviceForm").serialize(),function(json){
				$("#saveServiceBtn,#saveingServiceBtn").toggle();
				if(json.state=='success'){
					alert("新增服务成功");
					location.href="<%=basePath %>service/serviceInfo_searchServiceList.do";
				}else{
					alert(json.message);				
				}
			});
		}
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
		if($('#serviceType').val() == '1'){
			if($("#cycleType :selected").val()==''){
				alert("请选择周期类型");
				return false;
			}
			if($("#cycleDay").val()==''&&$("#cycleType").val() !='5'){
				alert("周期天数不能为空");
				$("#cycleDay").focus();
				return false;
			}
			if(isNaN($("#cycleDay").val())){
				alert("周期天数只能为数字");
				$("#cycleDay").focus();
				return false;
			}
			if(($("#cycleDay").val()<1 || $("#cycleDay").val()>31) &&$("#cycleType").val() !='5'){
				alert("周期天数只能在1-31之间");
				$("#cycleDay").focus();
				return false;
			}
		}
		
		return true;
	}
	
	$(document).ready(function(){ 
		$("#cycleType").change(function(){
			if($(this).val()==5){
				$("#cycleDay").attr("disabled", true);
			}else{
				$("#cycleDay").attr("disabled", false);
			}
		})
		changeServiceType();
	})
	
	function changeServiceType(){
		if($('#serviceType').val() == '1'){
			$("tr[name=offline]").show();
		}else{
			$("tr[name=offline]").hide();
		}
	}
</script>
</head>	
<body>
	<div class="main_form" method="post">
	<form id="serviceForm" name="serviceForm" action="service/serviceInfo_saveServiceInfo.do">
        <div class="main_title">
            <b>新增服务</b>
        </div>
		
		<table width="50%" border="0" style="padding:20px">
			<tr>
			    <th>服务类型：</th>
			    <td>
			    <select id="serviceType" name="serviceVO.serviceType" onchange="changeServiceType()">
			    	<option value="1">离线</option>
			    	<option value="0">实时</option>
			    </select>
				<span class="c_red">*</span></td>
			</tr>
			<tr>
			    <th>服务编码：</th>
			    <td><input name="serviceVO.serviceCode" id="serviceCode" type="text" maxlength="15"/><span class="c_red">*</span></td>
			</tr>
			<tr>
			    <th>服务名称：</th>
			    <td><input name="serviceVO.serviceName" id="serviceName" type="text"/><span class="c_red">*</span></td>
			</tr>
			<tr name="offline" style="display:none">
			    <th>周期类型：</th>
			    <td><g:sysDictList dictCode="DICT_CYCLE_TYPE" tagType="select" tagId="cycleType" tagName="serviceVO.cycleType"/><span class="c_red">*</span></td>
			</tr>
			<tr name="offline" style="display:none">
			    <th>周期的第：</th>
			    <td><input name="serviceVO.cycleDay" id="cycleDay" type="text"/>日<span class="c_red">*</span></td>
			</tr>
			<tr>
				<td colspan=2  style="text-align:center">
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['addService']!=null}">
						<input name="saveServiceBtn" id="saveServiceBtn" type="button" value="保存" onclick="saveService()"/>
						<input name="saveingServiceBtn" type="button" value="保存中" id="saveingServiceBtn" style="display:none;color:gray" disabled/>
					</c:if>
				</td>
			</tr>
		</table>
	</form>
	</div>

</body>
</html>
