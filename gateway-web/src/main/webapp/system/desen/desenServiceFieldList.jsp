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
<title>服务字段脱敏配置_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript">	
	$(document).ready(function(){
		$("#loginName").val($.trim($("#loginName").val()));
		if($("#loginName").val() != ''){
			$("#searchBtn").trigger("click");
		}
	});
</script>
</head>	
<body class="FrameMain">
<form id="searchForm" action="system/desenModel_searchDesenServiceFieldList.do" method="post" loadContainer="pageDataList">
	<div class="main_title">
	<b>服务字段脱敏配置</b>
</div>

<div class="main_search">
	<p>
	登录帐号：<input type="text" name="loginName" id="loginName" value="${param.loginName }"/>
	<input name="searchBtn" id="searchBtn" type="button" value="查询" onclick="searchPage()"/>
	</p>
</div>

<div id="pageDataList">
<div class="main_list">
<table width="100%" border="0">
  <tr>
    <th>序号</th>
    <th><a class="tableSort" sort="login_name">登录帐号</a></th>
    <th><a class="tableSort" sort="service_name">服务名称</a></th>
    <th><a class="tableSort" sort="service_code">服务编码</a></th>
    <th><a class="tableSort" sort="service_type">服务类型</a></th>
    <th><a class="tableSort" sort="cycle_type">周期</a></th>
    <th><a class="tableSort" sort="model_name">模型名称</a></th>
    <th><a class="tableSort" sort="model_code">模型编码</a></th>
    <th>操作 </th>
  </tr>
  <c:forEach var="s" items="${pageObject.data }" varStatus="vs">
  	  <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
  	  	<td>${(pageObject.curPage-1)*pageObject.pageSize+vs.count }</td>
	    <td>${s.loginName }</td>
	    <td>${s.serviceName }</td>
	    <td>${s.serviceCode }</td>
	    <td><g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${s.serviceType }"/></td>
	    <td>
	    	<c:if test="${s.serviceType=='1'}">
	    	每<g:sysDict dictCode="DICT_CYCLE_TYPE" dictKey="${s.cycleType}"/>
	    	<c:choose>
	    		<c:when test="${s.cycleType=='1'}">第${s.cycleDay}天</c:when>
	    		<c:when test="${s.cycleType=='2'}">第${s.cycleDay}天</c:when>
	    		<c:when test="${s.cycleType=='3'}">${s.cycleDay}日</c:when>
	    		<c:when test="${s.cycleType=='4'}">${s.cycleDay}</c:when>
	    		<c:when test="${s.cycleType=='5'}"></c:when>
	    	</c:choose>
    	</c:if>
	    </td>
	    <td>${s.modelName }</td>
	    <td>${s.modelCode }</td>
	    <td>
	    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['detailServiceInfo']!=null}">
	    		<a href="service/service/serviceInfo_searchService.do?serviceView.serviceId=${s.serviceId }" target="_blank" title="查看服务信息">查看服务</a>
	    	</c:if>
	    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['detailDesen']!=null}">
	    		<a href="system/desenModel_desenRuleServiceField.do?source=3&userId=${s.userId}&serviceId=${s.serviceId}&search=detailDesen" title="查看脱敏规则">查看规则</a>
	    	</c:if>
	    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['updateServiceRule']!=null}">
				<a href="javascript:void(0)" onclick="updateRuleOnline('${s.serviceId}','${s.userId}')" >修改</a>
			</c:if>
	    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['ruleDesen']!=null}">
    			<c:set value="${s.userId}" var="userId" scope="request"/>
    			<c:set value="${s.serviceId}" var="serviceId" scope="request"/>
    			<%
    				Long userId = (Long)request.getAttribute("userId");
    				Long serviceId = (Long)request.getAttribute("serviceId");
    				//Long类型的userId、serviceId转为String类型的userServiceKey
    				request.setAttribute("userServiceKey", userId.toString()+","+serviceId.toString());
    			 %>
	    		<c:choose>
					<c:when test="${userServiceMap[userServiceKey] == null || userServiceMap[userServiceKey] == ''}">
						|<a href="system/desenModel_desenRuleServiceField.do?source=3&userId=${s.userId}&serviceId=${s.serviceId}">脱敏规则</a>
					</c:when>
					<c:otherwise>
						 <span>脱敏待审</span>
					</c:otherwise>
				</c:choose>
	    	</c:if>
	    </td>
	  </tr>
  </c:forEach>
  <c:if test="${pageObject == null }">
  	  <tr>
	    <td colspan=10>请查询后再配置模型服务信息脱敏</td>
	  </tr>
  </c:if>
  <c:if test="${pageObject != null && (pageObject.data == null || empty pageObject.data)}">
  	  <tr>
	    <td colspan=10>该用户还没有申请通过任何服务</td>
	  </tr>
  </c:if>
</table>
</div>
<g:page pageObject="${pageObject }" />
</div>
</form>
</body>
</html>
