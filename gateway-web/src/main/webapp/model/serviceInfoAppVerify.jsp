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
<title>服务信息查看申请审核_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">
   function pass(){
      if(confirm("通过该用户查看${planType=='viewService'?'服务输入集':'模型基本信息'}申请？")){
	      var form1=document.getElementById("verifyForm");
	      document.getElementById("passTag").value="2";
	      form1.submit();
      }
   }
   
   function noPass(){
      if(confirm("退回该用户查看${planType=='viewService'?'服务输入集':'模型基本信息'}申请？")){
	      var form1=document.getElementById("verifyForm");
	      document.getElementById("passTag").value="0";
	      form1.submit();
      }
   }
</script>
</head>
  
<body>
  <div class="main_title">
		<b>服务信息申请查看</b>
  </div>
  <form id="verifyForm" name="verifyForm" action="system/workPlan_verifyServiceInfoApp.do"  method="post">
	    <input type="hidden" id="planId" name="workPlan.planId" value="${workPlan.planId}"/>
	    <input type="hidden" id="planState" name="workPlan.planState" value="${workPlan.planState}"/>
	    <input type="hidden" id="passTag" name="passTag" />
	    <input type="hidden" id="userId" name="userId" value="${userVO.userId}"/>
	   	<div class="main_info_title">审核信息</div>
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
			    <td width="90%" align="left"><textarea id="suggestion" name="suggestion" rows="6" cols="100"></textarea></td>
			  </tr>
	     </table>
  		<br />
     	<div align="center">
			    <input name="" type="button" value="通过" onclick="pass()"/>
			    <input name="" type="button" value="不通过" onclick="noPass()"/>
     	</div>
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
  		
  		<div class="main_info_title" style="margin-bottom:0px">服务输入集</div>
  		<div class="main_infomation">
			<div class="main_list">
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
			
	     	</div>
  		</div>
  		
  		<div class="main_info_title" style="margin-bottom:0px">模型基本信息</div>
     	<div class="main_infomation">
			<div class="main_list">
				<c:choose>
				<c:when test="${serviceView !=null && not empty serviceView }">
				<table width="100%" border="0">
  					<tr>
		      	  	  	  <td style="width:90px;text-align:right">模型编码：</td>
		      	  	  	  <td style="width:150px;text-align:left">${serviceView.modelCode }</td>
		      	  	  	  <td style="width:90px;text-align:right">模型名称：</td>
		      	  	  	  <td style="width:150px;text-align:left">${serviceView.modelName }</td>
  		      	  	  	  <td style="width:90px;text-align:right">版本号：</td>
		      	  	  	  <td style="width:150px;text-align:left">${serviceView.modelVersion }</td>
		      	  	  </tr>
		      	  	  <tr>
		      	  	  	  <td style="width:90px;text-align:right">模型类型：</td>
		      	  	  	  <td style="width:150px;text-align:left" colspan=5>${serviceView.modelType }</td>		      	  	  	 
		      	  	  </tr>
		      	  	  <tr>
		      	  	  	  <td style="width:90px;text-align:right">算法类型：</td>
		      	  	  	  <td style="width:150px;text-align:left" colspan=5>${serviceView.algType }</td>		      	  	  	 
		      	  	  </tr>
		      	  	  <tr>
		      	  	  	  <td style="width:90px;text-align:right">算法规则：</td>
		      	  	  	  <td style="width:150px;text-align:left" colspan=5>${serviceView.algRule }</td>		      	  	  	 
		      	  	  </tr>		      	  	  
  				</table>
				</c:when>
				<c:otherwise>
					<div align="center">查询不到模型基本信息</div>
				</c:otherwise>
				</c:choose>
			
	     	</div>
  		</div>
  </form>
</body>
</html>
