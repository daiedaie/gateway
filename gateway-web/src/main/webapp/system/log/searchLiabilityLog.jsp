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
<title>免责日志查看_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
</head>	
<body>
<div class="main_title">
	<b>免责日志查看</b>
</div>
<div class="FrameMain">
	  <div class="main_info_title">用户信息</div>
   	  <table width="100%" cellspacing="0">   	  
   	  	  <tr>
   	  	  	  <td style="width:15%;text-align:right">用户编码：</td>
   	  	  	  <td style="width:20%;">${logView.loginName }</td>
   	  	  	  <td style="width:15%;text-align:right">用户名称：</td>
   	  	  	  <td style="width:20%;">${logView.userName }</td>
   	  	  	  <td style="width:15%;text-align:right">机构名称：</td>
   	  	  	  <td style="width:20%;">${logView.orgName }</td>
   	  	  </tr>
   	  </table>
   	  <div class="main_info_title">模型信息</div>
   	  <c:choose>
   	  <c:when test="${logView.modelId!='' }">
   	  <table width="100%" cellspacing="0">
   	  	  <tr>
   	  	  	  <td style="width:15%;text-align:right">模型编码：</td>
   	  	  	  <td style="width:20%;">${logView.modelCode }</td>
   	  	  	  <td style="width:15%;text-align:right">模型名称：</td>
   	  	  	  <td colspan=3>${logView.modelName }</td>
   	  	  </tr>
   	  	  <tr>
   	  	  	  <td style="width:15%;text-align:right">模型类型：</td>
   	  	  	  <td colspan=5>${logView.modelType }</td>
   	  	  </tr>
   	  	   <tr>
   	  	  	  <td style="width:15%;text-align:right">模型版本：</td>
   	  	  	  <td style="width:20%;">${logView.modelVersion }</td>
   	  	  	  <td style="width:15%;text-align:right">上线时间：</td>
   	  	  	  <td colspan=3>${logView.startTime }</td>
   	  	   </tr>
   	  	   <tr>
   	  	  	  <td style="width:15%;text-align:right">算法类型：</td>
   	  	  	  <td colspan=5>${logView.algType }</td>
   	  	  </tr>
   	  	  <tr>
   	  	  	  <td style="width:15%;text-align:right">算法规则：</td>
   	  	  	  <td colspan=5>${logView.algRule }</td>
   	  	  </tr>
   	  	    <tr>
   	  	  	  <td style="width:15%;text-align:right" valign="top">模型描述：</td>
   	  	  	  <td style="width:20%;" colspan=5>${logView.modelDesc }</td>
   	  	  </tr>
   	  </table>
   	  </c:when>
   	  	<c:otherwise>
   	  		<div>没有查询到相关模型信息</div>
   	  	</c:otherwise>
   	  </c:choose>
   	  <div class="main_info_title">服务信息</div>
   	  <table width="100%" cellspacing="0">
   	  	   <tr>
   	  	      <td style="width:15%;text-align:right">服务编码：</td>
   	  	  	  <td style="width:20%;">${logView.serviceCode }</td>
   	  	   	  <td style="width:15%;text-align:right">服务名称：</td>
   	  	  	  <td style="width:20%;">${logView.serviceName }</td>
   	  	  	  <td style="width:15%;text-align:right">服务类型：</td>
   	  	  	  <td style="width:20%;"><g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${logView.serviceType }"/></td>		
   	  	   </tr>
   	  	   <tr>
   	  	      <td style="width:15%;text-align:right">周期类型：</td>
   	  	  	  <td style="width:20%;"><g:sysDict dictCode="DICT_CYCLE_TYPE" dictKey="${logView.cycleType }"/></td>
   	  	      <td style="width:15%;text-align:right">服务周期：</td>
   	  	  	  <td style="width:20%;">
					<c:if test="${logView.serviceType=='1'}">
				    	每<g:sysDict dictCode="DICT_CYCLE_TYPE" dictKey="${logView.cycleType}"/>
				    	<c:choose>
				    		<c:when test="${logView.cycleType=='1'}">第${logView.cycleDay}天</c:when>
				    		<c:when test="${logView.cycleType=='2'}">第${logView.cycleDay}天</c:when>
				    		<c:when test="${logView.cycleType=='3'}">${logView.cycleDay}日</c:when>
				    		<c:when test="${logView.cycleType=='4'}">${logView.cycleDay}</c:when>
				    		<c:when test="${logView.cycleType=='5'}"></c:when>
				    	</c:choose>
			    	</c:if>
			  </td>
   	  	  	  <td style="width:15%;text-align:right">数据量：</td>
   	  	  	  <td style="width:20%;">${logView.modelDataNum }</td>   	  	  	  
			</tr>
				<td style="width:15%;text-align:right">下载时间：</td>
   	  	  	  	<td style="width:20%;">${logView.downloadTime }</td>
			<tr>
			</tr>
			<tr>
   	  	  	  <td style="text-align:right" valign="top">结果集字段：</td>
   	  	  	  <td colspan=5><textarea readonly class="" style="width:60%">${logView.modelFields }</textarea></td>
   	  	  </tr>
   	  	  <tr>
   	  	  	  <td style="text-align:right" valign="top">脱敏规则描述：</td>
   	  	  	  <td colspan=5><textarea readonly class="" style="width:60%">${logView.desenRuleContent }</textarea></td>
   	  	  </tr>
			<tr>
   	  	   	  <td style="width:15%;text-align:right">服务备注：</td>
   	  	  	  <td style="width:20%;">${logView.serviceRemark }</td>
   	  	   </tr>  	  	   
   	  </table>
</div>
</body>
</html>
