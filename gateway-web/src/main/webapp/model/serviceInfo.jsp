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
<title>模型服务明细信息_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script>
function appViewService(){
	if(confirm("确定要申请查看服务输入集吗？")){
		$.ajax({
			type: "POST",
			url: "service/serviceInfo_appViewService.do",
			data: $("#serviceInfoForm").serialize(),
			success: function(data){
				if (data.state == 'success') {
					alert(data.message);
					$("#appViewService,#unAppViewService").toggle();
				} else {
					alert(data.message);
				}
				
			}
   		});
	}
}

function appViewModel(){
	if(confirm("确定要申请查看模型基本信息吗？")){
			$.ajax({
				type: "POST",
				url: "service/serviceInfo_appViewModel.do",
				data: $("#serviceInfoForm").serialize(),
				success: function(data){
					if (data.state == 'success') {
						alert(data.message);
						$("#appViewModel,#unAppViewModel").toggle();
					} else {
						alert(data.message);
					}
				}
	   		});
		}
}
</script>
</head>	
<body>
<form id="serviceInfoForm">
<div class="main_title">
	<b>模型服务明细信息</b>
</div>
<input type="hidden" name="serviceView.serviceCode" value="${serviceView.serviceCode }"/>
<input type="hidden" name="serviceView.serviceId" value="${serviceView.serviceId }"/>
<input type="hidden" name="serviceView.serviceName" value="${serviceView.serviceName }"/>
<%-- <input type="hidden" name="serviceView.modelId" value="${serviceView.modelId }"/> --%>
<input type="hidden" name="serviceView.modelName" value="${serviceView.modelName }"/>
	<div class="main_info_title">服务基本信息</div>
				<table width="100%" border="0">
		      	  	  <tr>
		      	  	  	  <td style="width:90px;text-align:right">服务编码：</td>
		      	  	  	  <td style="width:150px;">${serviceView.serviceCode }</td>
		      	  	  	  <td style="width:90px;text-align:right">服务名称：</td>
		      	  	  	  <td style="width:150px;">${serviceView.serviceName }</td>
  		      	  	  	  <td style="width:90px;text-align:right">服务类型：</td>
		      	  	  	  <td style="width:150px;"><g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${serviceView.serviceType }"/></td>
		      	  	  </tr>
		      	  	  <tr>
		      	  	  	  <td style="width:90px;text-align:right">服务周期：</td>
		      	  	  	  <td colspan=5>
<!-- 		      	  	  	  	<g:sysDict dictCode="DICT_CYCLE_TYPE" dictKey="${serviceView.cycleType }"/> -->
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
		      	  	  </tr>
		      	  </table>      
		<div class="main_info_title" style="margin-bottom:0px">服务输出集</div>
  		<div class="main_infomation">
		<div class="main_list">
			<c:choose>
				<c:when test="${outputList !=null && not empty outputList }">
				<table width="100%" border="0">
	   				<tr>
							<th  style="width:10%;text-align:center">字段编码</th>
							<th  style="width:10%;text-align:center">字段名称</th>
							<th  style="width:10%;text-align:center">字段备注</th>
							<th  style="width:10%;text-align:center">是否可空</th>
							<th  style="width:10%;text-align:center">数据类型</th>
					</tr>
					<c:forEach var="serviceField" items="${outputList }">
						<tr>
							<td  style="width:10%;text-align:center">${serviceField.fieldCode }</td>
	  					<td  style="width:10%;text-align:center">${serviceField.fieldName }</td>
	  					<td  style="width:10%;text-align:center">${serviceField.fieldDesc }</td>
	  					<td  style="width:10%;text-align:center">${serviceField.nullable }</td>
	  					<td  style="width:10%;text-align:center">${serviceField.fieldType }</td>
						</tr>
					</c:forEach>
				</table>
	 			</c:when>
	 			<c:otherwise>
	 				<div align="center">查询不到服务输出集信息</div>
	 			</c:otherwise>
			</c:choose>
  		</div>
  		</div>
	   <%-- 	<div class="main_info_title">服务输入集 
	   		<c:if test="${(user.userType == 'dataUser' || user.userType=='orgUser') && desenModelInfo.serviceInputInfo != '1' }">
				<c:choose>
					<c:when test="${serviceMap[user.userId] == null }">
						<span style="float:right" id="unAppViewService">	(无权限查看可以通过申请查看)<input name=""  id="" type="button" value="申请查看信息" onclick="appViewService();" style="font-size:12px"/></span>
						<span style="float:right;display:none" id="appViewService">已申请，请等待审核</span>
					</c:when>
					<c:otherwise>
						<span style="float:right">已申请，请等待审核</span>
					</c:otherwise>
				 </c:choose>
	   		</c:if>
	   	</div>
	   	</div>
	   	<div class="main_infomation">
		<div class="main_list">
					<c:choose>
				          	<c:when test="${(user.userType != 'dataUser' && user.userType!='orgUser') || desenModelInfo.serviceInputInfo == '1' }">
				          			<c:choose>
				          					<c:when test="${inputList !=null && not empty inputList }">
											<table width="100%" border="0">	
							      				<tr>
						    						<th  style="width:10%;text-align:center">字段编码</th>
						    						<th  style="width:10%;text-align:center">字段名称</th>
						    						<th  style="width:10%;text-align:center">字段备注</th>
						    						<th  style="width:10%;text-align:center">是否可空</th>
						    						<th  style="width:10%;text-align:center">数据类型</th>
						  						</tr>
						  						<c:forEach var="serviceField" items="${inputList }">
						  						<tr>
						  							<td  style="width:10%;text-align:center">${serviceField.fieldCode }</td>
							    					<td  style="width:10%;text-align:center">${serviceField.fieldName }</td>
							    					<td  style="width:10%;text-align:center">${serviceField.fieldDesc }</td>
							    					<td  style="width:10%;text-align:center">${serviceField.nullable }</td>
							    					<td  style="width:10%;text-align:center">${serviceField.fieldType }</td>
						  						</tr>
						  						</c:forEach>
									     	</table>
				          					</c:when>
				          					<c:otherwise>
											      <div align="center">查询不到服务输入集信息</div>
				          					</c:otherwise>
				          			</c:choose>
			  				</c:when>
				        	<c:otherwise>
								您无权限查看输入集信息，请先申请查看权限
				         	</c:otherwise>
			     	</c:choose>
	     	</div>
  		</div>
		<div class="main_info_title">模型基本信息
			<c:if test="${(user.userType == 'dataUser' || user.userType=='orgUser') && desenModelInfo.modelInfo != '1' }">
				 <c:choose>
						<c:when test="${modelMap[user.userId] == null }">
							<span style="float:right" id="unAppViewModel" >(无权限查看可以通过申请查看)<input name="" id="" type="button" value="申请查看信息" onclick="appViewModel();" style="font-size:12px"/></span>
							<span style="float:right;display:none" id="unAppViewModel">已申请，请等待审核</span>
						</c:when>
						<c:otherwise>
						 	<span style="float:right">已申请，请等待审核</span>
						</c:otherwise>
				 </c:choose>
			</c:if>
		</div>
		 <c:choose>
	          	<c:when test="${(user.userType != 'dataUser' && user.userType!='orgUser') || desenModelInfo.modelInfo == '1' }">
  				<table width="100%" border="0">
  					<tr>
		      	  	  	  <td style="width:90px;text-align:right">模型编码：</td>
		      	  	  	  <td style="width:150px;">${serviceView.modelCode }</td>
		      	  	  	  <td style="width:90px;text-align:right">模型名称：</td>
		      	  	  	  <td style="width:150px;">${serviceView.modelName }</td>
  		      	  	  	  <td style="width:90px;text-align:right">版本号：</td>
		      	  	  	  <td style="width:150px;">${serviceView.modelVersion }</td>
		      	  	  </tr>
		      	  	  <tr>
		      	  	  	  <td style="width:90px;text-align:right">模型类型：</td>
		      	  	  	  <td style="width:150px;" colspan=5>${serviceView.modelType }</td>		      	  	  	 
		      	  	  </tr>
		      	  	  <tr>
		      	  	  	  <td style="width:90px;text-align:right">算法类型：</td>
		      	  	  	  <td style="width:150px;" colspan=5>${serviceView.algType }</td>		      	  	  	 
		      	  	  </tr>
		      	  	  <tr>
		      	  	  	  <td style="width:90px;text-align:right">算法规则：</td>
		      	  	  	  <td style="width:150px;" colspan=5>${serviceView.algRule }</td>		      	  	  	 
		      	  	  </tr>		      	  	  
  				</table>
  				</c:when>
	          	<c:otherwise>
	          		<div class="main_infomation">
					您无权限查看模型基本信息，请先申请查看权限
					</div>
					<div style="height:30px"></div>
	          	</c:otherwise>
	          </c:choose> --%>
	</form>
</body>
</html>
