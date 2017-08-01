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
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript">
   function checkDetail(processId,processType,progressStatus,status,stepStatus){
   	  location.href="system/process_searchProcessOperation.do?processId="+processId+"&processType="+processType+"&processStatus="+progressStatus+"&status="+status+"&stepStatus="+stepStatus;
   }
   

</script>
<title>流程进度列表</title>
</head>
  <body class="FrameMain">
  <form id="searchForm" action="system/process_searchProcessList.do" method="post" loadContainer="pageDataList">
	<div class="main_title">
		<b>流程进度列表</b>
	</div>
	
	<div class="main_search">
		<p>
			流程类型：<g:sysDictList dictCode="DICT_PROCESS_TYPE" defaultValue="${processType}" tagType="select" tagName="processType" tagId="processType"/>
			&nbsp;&nbsp;
			流程状态：<g:sysDictList dictCode="DICT_PROCESS_STATUS" defaultValue="${processStatus}" tagType="select" tagName="status" tagId="status"/>
			&nbsp;&nbsp;
			<input name="" type="button" value="查询" onclick="searchPage()"/>
		</p>
	</div>
	<div id="pageDataList">
		<div class="main_list">
			<table width="100%" border="0">
			  <tr>
			    <th>序号</th>
			    <th>流程编码</th>
			    <th>流程类型</th>
			    <th>流程状态</th>
			    <th>开始时间</th>
			    <th>结束时间</th>
			    <th>操作</th>
			  </tr>
			  
			  <c:forEach var="process" items="${pageObject.data }" varStatus="vs">
		  	     <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
			        <td>${(pageObject.curPage-1)*pageObject.pageSize+vs.count }</td>
				    <td>${process.processId}</td>
				    <td>${process.processTypeName}</td>
				    <td>${process.statusName}</td>
				    <td><fmt:formatDate value="${process.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				    <td><fmt:formatDate value="${process.endTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				    <td>
				    	<a href="javascript:checkDetail('${process.processId}','${process.processType}','${process.progressStatus}','${process.status}','${process.stepStatus}')">查看明细</a>
				    </td>
				 </tr>
			  </c:forEach>
			  <c:if test="${pageObject.data == null || empty pageObject.data}">
			  	  <tr>
				    <td colspan=5>查询不到流程进度信息</td>
				  </tr>
			  </c:if>
			 </table>
		 </div>
	 	<g:page pageObject="${pageObject }" />
	 </div>
  
  </form>
  </body>
</html>
