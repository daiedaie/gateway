<%@ page language="java" contentType="text/html; charset=utf-8"%>
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
<title>敏感信息追溯_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
</head>
<body class="FrameMain">
  <form id="searchForm" action="system/liability_searchDesenList.do" method="post" loadContainer="pageDataList">
  <div class="main_title">
		<b>敏感信息追溯</b>
	</div>
	<div class="main_search">
	  <table width="100%" border="0">
		  <tr>
		    <td width="80%">
		   		 分组：
				机构<input name="searchBy" type="radio" value="org" />
				用户<input name="searchBy" type="radio" value="loginName" checked />
				<input type="text" name="logView.loginName" id="" value=""/>
				&nbsp;&nbsp;&nbsp;&nbsp;
		      	 敏感信息字段组合：<input type="text" id="modelFields" name="logView.modelFields" value="" onblur="javascript:this.value=this.value.replace(/，/ig,',');"/>
				&nbsp;&nbsp;&nbsp;&nbsp; 
				<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['findDesenInfo']!=null}">
		    	<input name="" type="button" value="查询" onclick="searchPage()"/>
		    	</c:if>
		    </td>
		  </tr>
	  </table>
	 </div>
	 <div id="pageDataList">
	 <div class="main_list">
	<table width="100%" border="0">
	  <tr>
	    <th>序号</th>
	    <th><a class="tableSort" sort="login_name">用户编码</a></th>
	    <th><a class="tableSort" sort="user_name">用户名称</a></th>
	    <th><a class="tableSort" sort="orgLoginName">机构登录帐号</a></th>
	    <th><a class="tableSort" sort="org_name">机构名称</a></th>
	    <th><a class="tableSort" sort="service_code">服务编码</a></th>
	    <th><a class="tableSort" sort="service_name">服务名称</a></th>
	    <th><a class="tableSort" sort="model_code">模型编码</a></th>
	    <th><a class="tableSort" sort="model_name">模型名称</a></th>
	    <th><a class="tableSort" sort="data_num">数据量</a></th>
	    <th><a class="tableSort" sort="download_time">下载时间</a></th>
	    <th>查看</th>
	  </tr>
	  <c:forEach var="log" items="${pageObject.data }" varStatus="vs">
  	     <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
	        <td>${(pageObject.curPage-1)*pageObject.pageSize+vs.count }</td>
		    <td>${log.loginName}</td>
		    <td>${log.userName}</td>
		    <td>${log.orgLoginName}</td>
		    <td>${log.orgName}</td>
		    <td>${log.serviceCode}</td>
		    <td>${log.serviceName}</td>
		    <td>${log.modelCode}</td>
		    <td>${log.modelName}</td>
		    <td>${log.modelDataNum}</td>
		    <td>${log.downloadTime}</td>
		    <td><a href="system/liability_searchLiabilityLog.do?logView.logId=${log.logId }" target="_blank">查看</a></td>
		 </tr>
	  </c:forEach>
	  <c:if test="${pageObject.data == null || empty pageObject.data}">
	  	  <tr>
		    <td colspan=12>查询不到敏感信息数据</td>
		  </tr>
	  </c:if>
	 </table>
	 </div>
	 <g:page pageObject="${pageObject }" />
	 </div>
  </form>
</body>
</html>
