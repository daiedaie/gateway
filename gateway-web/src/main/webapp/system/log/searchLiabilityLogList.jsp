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
<title>免责日志查看_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
</head>
<body class="FrameMain">
  <form id="searchForm" action="system/liability_searchLiabilityLogList.do" method="post" loadContainer="pageDataList">
  <div class="main_title">
		<b>免责日志查看</b>
	</div>
	<div class="main_search">
	  <table width="100%" border="0">
		  <tr>
		    <td width="80%">
		      	 用户编码：<input type="text" id="loginName" name="logView.loginName" value=""/>
				&nbsp;&nbsp;
    			用户名称：<input type="text" id="userName" name="logView.userName" value=""/>
    			&nbsp;&nbsp;
				服务编码：<input type="text" id="serviceCode" name="logView.serviceCode" value=""/>
		    </td>
		  </tr>
		  <tr>
		    <td width="80%">
		    	机构名称：<input type="text" id="orgName" name="logView.orgName" value=""/>
		    	&nbsp;&nbsp;
		    	机构登录帐号：<input type="text" id="orgLoginName" name="logView.orgLoginName" value=""/>
		    	&nbsp;&nbsp;
		    	下载时间：
		    	<input readonly="readonly" class="Wdate" type="text" onfocus="WdatePicker({maxDate:downloadEndTime.value})"  name="logView.downloadStartTime" id="downloadStartTime" value=""/>
		    	至
		    	<input readonly="readonly" class="Wdate" type="text" onfocus="WdatePicker({minDate:downloadStartTime.value})" name="logView.downloadEndTime" id="downloadEndTime" value=""/>
		    	&nbsp;&nbsp;
		    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['findEscapeLog']!=null}">
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
	    <th><a class="tableSort" sort="model_data_num">输出行数</a></th>
	    <th><a class="tableSort" sort="field_num">字段数</a></th>
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
		    <td>${log.fieldNum}</td>
		    <td>${log.downloadTime}</td>
		    <td>
		    <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['detailEscapeLog']!=null}">
		    <a href="system/liability_searchLiabilityLog.do?logView.logId=${log.logId }" target="_blank">查看</a>
		    </c:if>
		    </td>
		 </tr>
	  </c:forEach>
	  <c:if test="${pageObject.data == null || empty pageObject.data}">
	  	  <tr>
		    <td colspan=12>查询不到免责日志数据</td>
		  </tr>
	  </c:if>
	 </table>
	 <c:if test="${pageObject.data != null && not empty pageObject.data}">
        <div style="padding-top:10px">
			输出文件的总行数：${liabilityCountMap['liabilityDataNum'] }&nbsp;&nbsp;
			输出数据量：${liabilityCountMap['liabilityOutputDataNum'] }
		</div>
     </c:if>
	 </div>
	 <g:page pageObject="${pageObject }" />
	 </div>
  </form>
</body>
</html>
