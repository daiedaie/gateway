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
<title>服务输入集查看申请退回_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script>
function appViewService(){
	if(confirm("确定要重新申请吗？")){
		$.ajax({
			type: "POST",
			url: "system/workPlan_verifyServiceInfoReApp.do",
			data: $("#verifyForm").serialize()
			
   		});
	}
}

function appViewServiceBack(){
	if(confirm("确定要退回用户服务申请查看吗？")){
			$.ajax({
				type: "POST",
				url: "system/workPlan_searchServiceViewBack.do",
				data: $("#verifyForm").serialize()
				
	   		});
		}
}

function endWorkPlan(){
	var form=$("#verifyForm");
	form.submit();
}
</script>
</head>
  
<body>
  <div class="main_title">
		<b>服务信息申请查看回退</b>
  </div>
  <form id="verifyForm" name="verifyForm"   method="post" action="system/workPlan_endServiceInfoBack.do">
	    <input type="hidden" id="planId" name="planId" value="${workPlan.planId}"/>
	    <input type="hidden" id="planState" name="workPlan.planState" value="${workPlan.planState}"/>
	    <input type="hidden" id="passTag" name="passTag" />
	    <input type="hidden" id="userId" name="userId" value="${userVO.userId}"/>
	   	<div class="main_info_title">服务信息申请查看回退信息</div>
		<table width="100%" border="0">
			  <tr>
			    <td width="10%" align="right">创建人：</td>
			    <td width="90%" align="left">${createUser.loginName}</td>
			  </tr>
			  <tr>
			    <td width="10%" align="right">创建时间：</td>
			    <td width="90%" align="left">${workPlan.createTime }</td>
			  </tr>
			  <tr>
			    <td width="10%" align="right">任务标题：</td>
			    <td width="90%" align="left">${workPlan.planTitle }</td>
			  </tr>
			  <tr>
			    <td width="10%" align="right">任务内容：</td>
			    <td width="90%" align="left">${workPlan.planContent }</td>
			  </tr>
			  <tr>
			    <td width="10%" align="right" valign="top">审核意见：</td>
			    <td width="90%" align="left"><textarea id="suggestion" name="workPlan.suggestion" rows="6" cols="100"  disabled="true">${preWorkPlanVO.suggestion}</textarea></td>
			  </tr>
			  
	     </table>
		 <c:if test="${user.userType=='dataUser' }">
  			<div style="margin:20px 0px 0px 250px">
				<input type="button" value="确定" onclick="endWorkPlan()"/>
			</div>
  		</c:if>
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
		      	  	  	  <td colspan=5><g:sysDict dictCode="DICT_CYCLE_TYPE" dictKey="${serviceView.cycleType }"/></td>
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
  		
  		<div class="main_info_title">服务输入集 </div>
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
  		
  		<div class="main_info_title">模型基本信息</div>
		 <c:choose>
	          	<c:when test="${(user.userType != 'dataUser' &&  user.userType!='orgUser') || desenModelInfo.modelInfo == '1' }">
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
	          	</c:otherwise>
	          </c:choose>
  </form>
	<br/>
  	<br/>
  	<c:if test="${user.userType=='safeUser' }">
  		<div align="center">
			<input type="button" onclick="appViewService()" value="重新申请"/>
			<input type="button" onclick="appViewServiceBack()" value="退回"/>
		</div>
  	</c:if>
</body>
</html>
