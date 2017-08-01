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
<script>
function endWorkPlan(){
    check();
    $.ajax({
			type: "POST",
			url: "service/modelDataApp_createNewServiceTask.do",
			data: $("#verifyForm").serialize(),
			success: function(data){
				if (data.state == 'success') {
					alert(data.message);
					location.href="system/func_searchMain.do";
				} else {
					alert(data.message);
				}
				
			}
   		});
}

function check(){
	if($("#serviceId").val()==''){
			alert("请选择匹配的服务！");
			return false;
	}
}
function changService(param){
	var sendValue = ""; 
	
	sendValue = "param="+param;
	$.ajax({
	    type:"POST",
	    url:"service/serviceInfo_getServiceInfo.do",
	    data:sendValue,				
	    success:function(data){
	        var serviceObject=data.data.serviceVO;
	        
	        var choosedService = data.data.serviceVO.serviceName+"("+serviceObject.serviceCode+")";
	        $("#choosedService").html(choosedService);
	        var serviceTypeDictMap = data.data.serviceTypeDictMap;
	        var cycleTypeDictMap = data.data.cycleTypeDictMap;
	        
	        $("#userId").val(serviceObject.userId);
	        
	        $("#serviceId").val(serviceObject.serviceId);
	        $("#fetchId").val(serviceObject.fetchId);
	        //alert($("#fetchId").val()+"===="+$("#auditTime").val());
	        $("#auditTime").val(serviceObject.auditTimeStr);
	        	
	        $("#serviceInfo").empty();
	       	var str ="";
		        str ="<tr>"+
						"<th width='10%'>服务名称：</th>"+
						"<td width='25%'>"+serviceObject.serviceName+"</td>"+
						"<th width='10%'>服务状态：</th>"+
						"<td width='15%'>有效<td>"+
						"<th width='10%'>服务编码：</th>"+
						"<td width='30%'>"+serviceObject.serviceCode+"</td>"+
					  "</tr>";
				 str+="<tr >"+
 							"<th >服务类型：</th>"+
 							"<td>"+serviceTypeDictMap[serviceObject.serviceType].dictValue+"</td>"+
 							"<th >服务周期：</th>"+
 							"<td>";
 				 if(serviceObject.serviceType=='1'){
	        		 str+="每"+cycleTypeDictMap[serviceObject.cycleType].dictValue;
	        		 if(serviceObject.cycleType=='1'){
	        		 	str+="第"+serviceObject.cycleDay+"天";
	        		 }else if(serviceObject.cycleType=='2'){
	        		 	str+="第"+serviceObject.cycleDay+"天";
	        		 }else if(serviceObject.cycleType=='3'){
	        		 	str+=serviceObject.cycleDay+"日";
	        		 }else if(serviceObject.cycleType=='4'){
	        		 	str+=serviceObject.cycleDay;
	        		 }
	        	 }	
	        
 					str+="</td>"+
				    		"<th ></th>"+
						    "<td ></td>"+
						  "</tr>";
    		$("#serviceInfo").append(str);	
    		
    		
    		//
    		var RuleList= data.data.checkRuleList;
    		var checkDictMap = data.data.checkDictMap;
    		$("#ruleInfo").empty();
    		var ruleStr ="";
	        	 ruleStr ="<table width='100%' border='0'>"+
				        	 "<tr>"+
								"<th>字段编码</th>"+
								"<th>字段名称</th>"+
								"<th>字段类型</th>"+
								"<th>检查类型</th>"+
								"<th>检查规则</th>"+
							  "</tr>";
	        	 if(RuleList == null||RuleList.size == 0){
	        		 ruleStr ="<tr><td colspan=9>查询不到该服务的字段信息</td></tr>";
	        	 }else{
	        	    
	        		 $.each(RuleList,function(i,ele){
	 					ruleStr+="<tr name='fieldTr'>"+
	 							"<td>"+ele.fieldCode+"</td>"+
	 							"<td>"+ele.fieldName+"<input type='hidden' name='fieldName' value="+ele.fieldName+"/></td>"+
	 							"<td>"+ele.fieldType+"<input type='hidden' name='fieldType' value="+ele.fieldType+"/></td>"+
	 							"<td>"+checkDictMap[ele.checkType].dictValue+"</td>"+
	 							"<td>"+ele.checkRule+"</td>"+
	 						 "</tr>";
	 				})

	        	 }				
				ruleStr+="</table>";
	        	$("#ruleInfo").append(ruleStr);	
	        	       
	    },
	    error:function(){
	    	alert("系统异常");
	    }
	});
}
</script>
<title>创建实时取数任务确认</title>
</head>
  <body class="FrameMain">
	
	<div class="main_title">
		<b>创建实时取数任务确认</b>
	</div>
	<form  id="verifyForm" method="post" action="service/modelDataApp_createNewServiceTask.do">
	<input name="planId" id="planId" type ="hidden" value="${workPlan.planId}"/>
	<input name="userId" id="userId" type ="hidden" value="${serviceVO.userId}"/>
	<input name="serviceId" id="serviceId" type ="hidden" value="${serviceVO.serviceId}"/>
	<input name="fetchId" id="fetchId" type ="hidden" value="${serviceVO.fetchId}"/>
	<input name="auditTime" id="auditTime" type ="hidden" value="<fmt:formatDate value="${serviceVO.auditTime}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
	<input name="fileName" id="fileName" type ="hidden" value="${fileName}"/>
	<table width="100%" border="0">
		  <tr>
		  		<td align="right">创建人：</td>
		  		<td>system</td>
		  </tr>
		  <tr>
		  		<td align="right">创建时间：</td>
		  		<td><fmt:formatDate value="${workPlan.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
		  </tr>
		  <tr>
		  		<td align="right">待办标题：</td>
		  		<td>${workPlan.planTitle}</td>
		  </tr>
		  <tr>
		  		<td align="right">待办内容：</td>
		  		<td>${workPlan.planContent}</td>
		  </tr>
		  <tr>
		  		<td align="right">已匹配服务：</td>
		  		<td id='choosedService'>
		  		<c:if test="${serviceVO != null}">
		  			<c:if test="${serviceVO.serviceCode != null}">
		  			${serviceVO.serviceName }（${serviceVO.serviceCode}）
		  			</c:if>
		  			<c:if test="${serviceVO.serviceCode == null}">
		  			无匹配服务
		  			</c:if>
		  		</c:if>
		  		
		  		</td>
		  </tr>
		  <tr>
		  		<td align="right">选择其他服务：</td>
		  		<td >
		    	<c:if test="${serviceList != null && not empty serviceList}">
		    		<select id="serviceList" name="serviceList" onchange="changService(this.value)">
					<option value=''>-请选择-</option>
			    	<c:forEach var="g" items="${serviceList}">			    	   
			    		<option value="${g.serviceId},${g.userId}">${g.serviceName }（${g.serviceCode}）</option>
			    	</c:forEach>
			    	</select>
		    	</c:if>
		    </td>
		  </tr>
		  
	</table>
    <input type="hidden" id="planId" name="planId" value="${workPlan.planId}"/>
    <div style="margin:20px 0px 0px 300px">
		<input type="button" value="创建任务" onclick="endWorkPlan()"/>
	</div>
	<div  class="main_info_title">服务信息</div>
	<table width="100%" border="0" id="serviceInfo">
		  <tr >
		    <th width="10%">服务名称：</th>
		    <td width="25%">${serviceVO.serviceName }</td>
		    <th width="10%">服务状态：</th>
		    <td width="15%">有效<td>
		    <th width="10%">服务编码：</th>
		    <td width="30%">${serviceVO.serviceCode }</td>
		  </tr>
		  <tr >
		    <th >服务类型：</th>
		    <td><g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${serviceVO.serviceType}"/></td>
		    <th >服务周期：</th>
		    <td>
		    	<c:if test="${serviceVO.serviceType=='1'}">
			    	每<g:sysDict dictCode="DICT_CYCLE_TYPE" dictKey="${serviceVO.cycleType}"/>
			    	<c:choose>
			    		<c:when test="${serviceVO.cycleType=='1'}">第${serviceVO.cycleDay}天</c:when>
			    		<c:when test="${serviceVO.cycleType=='2'}">第${serviceVO.cycleDay}天</c:when>
			    		<c:when test="${serviceVO.cycleType=='3'}">${serviceVO.cycleDay}日</c:when>
			    		<c:when test="${serviceVO.cycleType=='4'}">${serviceVO.cycleDay}</c:when>
			    		<c:when test="${serviceVO.cycleType=='5'}"></c:when>
			    	</c:choose>
		    	</c:if>
    		</td>
    		<th ></th>
		    <td ></td>
		  </tr>
    </table>
    <div id="ruleDiv" >
		<div class="main_info_title" style="margin-bottom:10px">服务合规检查规则信息</div>
		<div class="main_list" style="padding:0 20px 0 20px" id="ruleInfo">
						
				<table width="100%" border="0">
					<tr>
						<th>字段编码</th>
						<th>字段名称</th>
						<th>字段类型</th>
						<th>检查类型</th>
						<th>检查规则</th>
					</tr>
					<c:forEach var="o" items="${checkRuleList}" varStatus="status">
						<tr name="fieldTr">
							<td>${o.fieldCode }</td>
							<td>${o.fieldName }<input type="hidden" name="fieldName" value="${o.fieldName }" />
							</td>
							<td>${o.fieldType }<input type="hidden" name="fieldType" value="${o.fieldType }" />
							</td>
							<td><g:sysDict dictCode="DICT_CHECK_RULE_TYPE" dictKey="${o.checkType}"/>
							</td>
							<td>${o.checkRule}</td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>
	</form>
  </body>
</html>
