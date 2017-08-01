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
<title>服务信息脱敏配置_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript">	
</script>
</head>	
<body>
<div class="main_list" id="dataList">
<table width="100%" border="0">
  <tr>
    <th>序号</th>
    <th>用户编号</th>
    <th>服务名称</th>
    <th>服务编码</th>
    <th>服务类型</th>
    <th>周期</th>
    <th>脱敏配置 </th>
  </tr>
  <c:forEach var="s" items="${desenList }" varStatus="vs">
  	  <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
	    <td>${vs.count }</td>
	    <td>${s.loginName }</td>
	    <td>${s.serviceName }</td>
	    <td>${s.serviceCode }</td>
	    <td><g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${s.serviceType }"/></td>
	    <td>
	    	<c:if test="${s.cycleType != null && s.cycleType != '' }">
	    		每<g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${s.cycleType }"/>
	    		<c:if test="${s.cycleDay != '5' }">
		    		第${s.cycleDay }日
	    		</c:if>
	    	</c:if>
	    </td>
	    <td>
	    	<input type="hidden" name="desenInfoList[${vs.index }].infoDeseId" id="infoDeseId${vs.index }" value="${s.infoDeseId }"/>
	    	<input type="hidden" name="desenInfoList[${vs.index }].userId" id="userId${vs.index }" value="${s.userId }"/>
	    	<input type="hidden" name="desenInfoList[${vs.index }].serviceId" id="service${vs.index }" value="${s.serviceId }"/>
	    	<label><input type="checkbox" name="desenInfoList[${vs.index }].modelInfo" id="modelInfo${vs.index }" value="1" ${s.modelInfo=='1'?"checked":""}/>模型信息</label>
	    	<label><input type="checkbox" name="desenInfoList[${vs.index }].serviceInputInfo" id="serviceInputInfo${vs.index }" value="1" ${s.serviceInputInfo=='1'?"checked":""}/>输入集</label>
	    </td>
	  </tr>
  </c:forEach>
  <c:if test="${desenList == null || empty desenList}">
  	  <tr>
	    <td colspan=10>该用户所在机构还没有分配过服务列表</td>
	  </tr>
  </c:if>
</table>
</div>

</form>
</body>
</html>
