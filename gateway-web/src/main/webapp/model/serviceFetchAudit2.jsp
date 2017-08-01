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
<title>用户模型服务取数审核</title>
<style type="text/css">
.divImg{width:15px;height:12px;cursor:pointer}
</style>
</head>
<script type="text/javascript">

		

   function pass(){
	   if($("input[name='status']").val()=="0"){
			alert("模型服务无效无法创建取数任务。");
		 	return;
		}
      if(confirm("确定创建取数？")){
	      var form1=document.getElementById("verifyForm");
	      document.getElementById("passTag").value="2";
	      form1.submit();
	      
      }
   }
   
   function noPass(){
      if(confirm("确定不同意创建？")){
	      var form1=document.getElementById("verifyForm");
	      document.getElementById("passTag").value="0";
	      form1.submit();
      }
   }
   
   function reModifyRole(){
	   if(confirm("确定重新修改规则？")){
		      var form1=document.getElementById("verifyForm");
		      document.getElementById("passTag").value="3";
		      form1.submit();
	      }
   }
   
   function showPreDesen(div,openImg,closeImg){
		$("#"+div+",#"+openImg+",#"+closeImg).toggle();
	}
</script>
  <body class="FrameMain">
	<div class="main_title">
		<b>服务创建并申请审批</b>
	</div>
	<form id="verifyForm" name="verifyForm" action="service/modelDataApp_serviceFetchAuditDeal.do">
	<input type="hidden" id="planId" name="planId" value="${planId}"/>
	<input type="hidden" id="serviceId" name="serviceId" value="${serviceVO.serviceId }"/>
	<input type="hidden" id="userId" name="userId" value="${createUser.userId }"/>	
	<input type="hidden" id="fetchId" name="fetchId" value="${fetchId}"/>
	<input type="hidden" id="passTag" name="passTag" />
	<div class="main_info_title">审核信息</div>
	<table width="100%" border="0">
		  <tr>
		   	 <td width="10%" align="right">创建人：</td>
			 <td width="90%" align="left">${createUser.loginName}</td>
		  </tr>
		  <tr>
		    <td width="10%" align="right">创建时间：</td>
		    <td width="90%" align="left">${workPlanVO.createTime }</td>
		  </tr>
		  <tr>
		   	 <td width="10%" align="right">任务标题：</td>
			 <td width="90%" align="left">${workPlanVO.planTitle }</td>
		  </tr>
		  <tr>
		    <td width="10%" align="right">任务内容：</td>
		    <td width="90%" align="left">${workPlanVO.planContent }</td>
		  </tr>
		  <tr >
		    <td width="10%" align="right" valign="top">审核意见：</td>
		    <td width="90%" align="left"><s:textarea id="suggestion" name="suggestion" rows="6" cols="100" /></td>
		  </tr>
		  <tr>
		  	<td colspan="2" align="center">
		    	<input name="" type="button" value="不同意创建" onclick="noPass()"/>
		   
		    &nbsp;&nbsp;
		    &nbsp;&nbsp;
		 
		    	<input name="" type="button" value="同意创建并取数" onclick="pass()"/>

		    &nbsp;&nbsp;
		    &nbsp;&nbsp;
		    	<input name="" type="button" value="重新修改规则" onclick="reModifyRole()"/>
		    </td>
		  </tr>
     </table>
     <c:if test="${modelVo != null}">
	<div class="main_info_title">模型信息</div>
	<table width="100%" border="0">
		  <tr >
		    <th width="10%">模型名称：</th>
		    <td width="25%">${modelVo.modelName }</td>
		    <th width="10%">模型编码：</th>
		    <td width="25%">${modelVo.modelCode }</td>
		    <th width="10%">模型状态：</th>
		    <td width="25%"><g:sysDict dictCode="DICT_DATA_STATE" dictKey="${modelVo.status}"/></td>
		  </tr>
		  <tr >
		    <th width="10%">模型细节：</th>
		    <td colspan="5">${modelVo.modelDesc }</td>
		  </tr>
    </table>
    </c:if>
    <c:if test="${modelVo == null}">
    <div class="main_info_title">该服务没有模型信息</div>
    </c:if>
    <div class="main_info_title">服务信息</div>
	<table width="100%" border="0">
		  <tr >
		    <th width="10%">服务名称：</th>
		    <td width="25%">${serviceVO.serviceName }</td>
		    <th width="10%">服务状态：</th>
		    <td width="25%"><g:sysDict dictCode="DICT_DATA_STATE" dictKey="${serviceVO.status}"/></td>
		    <th width="10%">服务编码：</th>
		    <td width="25%">${serviceVO.serviceCode }</td>
		  </tr>
		  <tr >
		    <th width="10%">服务类型：</th>
		    <td><g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${serviceVO.serviceType}"/></td>
		    <th width="10%">服务周期：</th>
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
    		<th width="10%"></th>
		    <td width="25%"></td>
		  </tr>
    </table>
    <div id="ruleDiv" >
		<div class="main_info_title" style="margin-bottom:10px">服务脱敏配置信息</div>
		<div class="main_list" style="padding:0 20px 0 20px">
						
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
