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
<title>待办查看_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
</head>
  
<body>
  <div class="main_title">
		<b>已办待办查看</b>
  </div>
  <form id="verifyForm" name="verifyForm" action="system/workPlan_endUserCancelBack.do"  method="post">
	   	<div class="main_info_title">审核信息</div>
		<table width="100%" border="0">
			  <tr>
			    <td width="10%" align="right">创建人：</td>
			    <td width="90%" align="left">${createUser.loginName }</td>
			  </tr>
			  <tr>
			    <td width="10%" align="right">创建时间：</td>
			    <td width="90%" align="left">${workPlan.createTime }</td>
			  </tr>
			  <tr>
			    <td width="10%" align="right">任务标题：</td>
			    <td width="90%" align="left">${workPlan.planTitle }</td>
			  </tr>
			  <tr valign="top">
			    <td width="10%" align="right">任务内容：</td>
			    <td width="90%" align="left">
			    <c:if test="${workPlan.planType==19 || workPlan.planType==20 || workPlan.planType==21 }">
			     <p>数据用户登录名：${dataUserVO.loginName}<br/>
		    	数据用户姓名：${dataUserVO.userName}<br/>
		    	所属机构用户登录名：${orgUserVO.loginName}<br/>
		    	所属机构用户姓名：${orgUserVO.userName}<br/><br/>
		    	
		    	服务：${service.serviceCode }<br/>
				服务名称：${service.serviceName }<br/>
				服务类型：<g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${service.serviceType }"/><br/>
				服务周期：
				<c:if test="${service.serviceType=='1'}">
					    每<g:sysDict dictCode="DICT_CYCLE_TYPE" dictKey="${service.cycleType}"/>
					    <c:choose>
					    	<c:when test="${service.cycleType=='1'}">第${service.cycleDay}天</c:when>
					    	<c:when test="${service.cycleType=='2'}">第${service.cycleDay}天</c:when>
					    	<c:when test="${service.cycleType=='3'}">${service.cycleDay}日</c:when>
					    	<c:when test="${service.cycleType=='4'}">${service.cycleDay}</c:when>
					    	<c:when test="${service.cycleType=='5'}"></c:when>
					    </c:choose>
			    </c:if>
				<br/>
				服务任务账期：${taskVO.fieldValue }<br/>
				文件名称：${fileVO.fileName }<br/>
				服务任务开始时间：<fmt:formatDate value="${taskVO.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/><br/>
		  		</p>
			    </c:if>
			    ${workPlan.planContent }
			    </td>
			  </tr>
			  <tr>
			    <td width="10%" align="right">审核状态：</td>
			    <td width="90%" align="left"><g:sysDict dictCode="DICT_PLAN_STATE" dictKey="${workPlan.planState }"/></td>
			  </tr>
			  <c:if test="${source==1 }">
			   	<tr>
			   	 	<td width="10%" align="right" valign="top">处理意见：</td>
			    	<td width="90%" align="left"><textarea rows="6" cols="100" readonly>${workPlan.suggestion }</textarea></td>
			  	</tr>
			  </c:if>
			  <c:if test="${source==2 }">
			  	<tr>
			    	<td width="10%" align="right" valign="top">撤回原因：</td>
			    	<td width="90%" align="left"><textarea rows="6" cols="100" readonly>${workPlan.reason }</textarea></td>
			  	</tr>
			  </c:if>
	     </table>
	     <c:if test="${workPlan.planType==19 || workPlan.planType==20 || workPlan.planType==21 }">
	     <div class="main_title">
			<b>历史处理意见</b>
    	</div>
    	<div class="main_list">
    	<table width="100%" border="0">
				<tr>
					<th>处理人登录名</th>
					<th>处理人姓名</th>
					<th>创建时间</th>
					<th>处理时间</th>
					<th>处理意见</th>
				</tr>
				<c:forEach var="pre" items="${preWorkPlan}" varStatus="status">
				<tr>
					<td>${pre.loginName}</td>
					<td>${pre.userName}</td>
					<td><fmt:formatDate value="${pre.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td><fmt:formatDate value="${pre.daelTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td><g:sysDict dictCode="DICT_PLAN_STATE" dictKey="${pre.planState }"/></td>
				</tr>
				</c:forEach>
		</table>
		</div>
	     <div class="main_title">
	     	 <b><a href="service/modelTask_previewRuleCheck.do?taskId=${taskVO.taskId}&serviceId=${service.serviceId}&userId=${dataUserVO.userId}" target="_blank">合规检查结果>></a></b>
	     </div>
	     </c:if>
		  <div style="margin:20px 0px 0px 300px">
				<input type="button" value="返回" onclick="history.go(-1)"/>
		  </div>
  </form>
</body>
</html>
