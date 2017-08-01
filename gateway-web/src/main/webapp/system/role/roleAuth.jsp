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
<title>群组权限分配</title>
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<!-- <script type="text/javascript" src="resource/js/jquery-ui.js"></script>
<link href="resource/css/jquery-ui.css" rel="stylesheet" type="text/css" /> -->
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/utils.js"></script>
<script>
$(function () {
  
  $("#add").click(function () {  
    $("#unChooseModelList option:selected").appendTo("#chooseModelList");  
});  
   
  $("#remove").click(function () {  
    $("#chooseModelList option:selected").appendTo("#unChooseModelList");  
  }); 
 
}); 
function save(){
	var chooseServiceList=new Array();
	$("input[name='serviceList']:checkbox:checked").each(function(){
		chooseServiceList.push($(this).val());
	});
	$("#chooseServices").val(chooseServiceList.toString());
	$.ajax({
		type: "POST",
		url: "system/role_saveRoleAuthInfo.do",
 		data: $("#roleAuthForm").serialize(),
		success: function(data){
			if (data.state == 'success') {
				alert("授权成功！");
			} else {
				alert(data.message);
			}
		}
   	});
}

function tableSort(sort){
	var asc = $("#asc").val();
	if(asc=='' || asc=='desc' || sort!=$("#sort").val()) asc = "asc";	//选择的排序字段与原来的排序字段不相同，则使用asc排序
	else asc = "desc";
	$("#asc").val(asc);
	$("#sort").val(sort);
	
	var chooseServices = new Array();	//已选择项
	$("[name=serviceList]:checked").each(function(){
		chooseServices.push($(this).val());
	});

	var url = "system/role_roleAuth.do #tableDiv"
	util.load("tableDiv",url,$("#roleAuthForm").serialize(),function(){
		//设置已选择项
		$("[name=serviceList]").prop("checked",false);
		for(var i=0;i<chooseServices.length;i++){
			$("[name=serviceList][value="+chooseServices[i]+"]").prop("checked",true);
		}
	 	//设置上下箭头样式
		if(asc == 'asc') $("[sortCode="+sort+"]").addClass("tableSortUp");
		else if(asc == 'desc') $("[sortCode="+sort+"]").addClass("tableSortDown");
	});
}
</script>
</head>

<body class="FrameMain">
<form id="roleAuthForm" name="roleAuthForm">
<input type="hidden" name="chooseServices" id="chooseServices"/>
<input type="hidden" name="roleVO.roleCode" id="roleVO.roleCode" value="${roleVO.roleCode}"/>
<input type="hidden" name="roleVO.roleName"  value="${roleVO.roleName}"/>
<input type="hidden" name="asc" id="asc" value=""/>
<input type="hidden" name="sort" id="sort" value=""/>

<div class="main_title">
	<b>群组权限分配</b>
</div>

<div class="main_info_title">群组信息</div>
<div class="main_infomation">
	<table width="100%" border="0">
        <tr>
            <td>群组编码：${roleVO.roleCode}</td>
            <td>群组名称：${roleVO.roleName}</td>
     
        </tr>
        <tr>
            <td>描述：${role.roleDesc}</td>
        </tr>
    </table>
</div>

<div class="main_info_title" style="margin-top:0px;margin-bottom:0px">服务分配</div>
  <div class="main_infomation">
	<div class="main_list" id="tableDiv">
    	<table width="100%" border="0" style="text-align:center">
    		<tr>
        	<th width="90"></th>
        	<th width="150"><a class="tableSort" onclick="tableSort('service_code')" sortCode="service_code">服务编码</a></th>
        	<th width="150"><a class="tableSort" onclick="tableSort('service_name')" sortCode="service_name">服务名称</a></th>
        	<th width="150"><a class="tableSort" onclick="tableSort('model_name')" sortCode="model_name">模型名称</a></th>
        	<th width="150"><a class="tableSort" onclick="tableSort('service_type')" sortCode="service_type">服务类型</a></th>
        	<th width="150"><a class="tableSort" onclick="tableSort('cycle_type')" sortCode="cycle_type">服务周期</a></th>
        	<th width="150">查看</th>
      		</tr>
      		
      		<c:forEach var="serviceView" items="${serviceList}" varStatus="vs">
      		<tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
      		
      		<td><input name="serviceList" type="checkbox" id="serviceList" value="${serviceView.serviceId}" ${serviceView.roleCode != "" ? "checked" : ""}/></td>
      		<td>${serviceView.serviceCode}</td>
      		<td>${serviceView.serviceName}</td>
      		<td>${serviceView.modelName}</td>
      		<td><g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${serviceView.serviceType }"/></td>
      		<td>
      			<c:if test="${serviceView.serviceType=='1'}">
			    	每<g:sysDict dictCode="DICT_CYCLE_TYPE" dictKey="${serviceView.cycleType}"/>
			    	<c:choose>
			    		<c:when test="${serviceView.cycleType=='1'}">第${serviceView.cycleDay}天</c:when>
			    		<c:when test="${serviceView.cycleType=='2'}">第${serviceView.cycleDay}天</c:when>
			    		<c:when test="${serviceView.cycleType=='3'}">${serviceView.cycleDay}日</c:when>
			    		<c:when test="${serviceView.cycleType=='4'}">${serviceView.cycleDay}</c:when>
			    		<c:when test="${serviceView.cycleType=='5'}"></c:when>
			    	</c:choose>
	    		</c:if>
      		</td>
      		<td><a href="service/serviceInfo_searchService.do?serviceView.serviceId=${serviceView.serviceId}" target="_blank">查看</a></td>
      		</tr>
      		</c:forEach>
      	</table>
	</div>
</div>

</br>
</br>
<div align="center">
	<input type="button" onclick="save()" value="保存"/>
	<input type="button" onclick="history.go(-1)" value="返回"/>
</div>
</form>
</body>
</html>
