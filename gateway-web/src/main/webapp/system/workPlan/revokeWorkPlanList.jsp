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
<title>撤回列表</title>
</head>
  <body class="FrameMain">
  <form id="searchForm" action="system/workPlan_searchRevokeWorkPlanByParam.do" method="post" loadContainer="pageDataList">
	<div class="main_title">
		<b>撤回待办列表查看</b>
	</div>
	
	<div class="main_search">
		<p>
			待办类型：<g:sysDictList dictCode="DICT_PLAN_TYPE" defaultValue="${planType}" tagType="select" tagName="planType" tagId="planType"/>
			&nbsp;&nbsp;
    		待办标题：<input type="text" id="planTitle" name="planTitle" value="${planTitle}"/>
    		&nbsp;&nbsp;
			待办内容：<input type="text" id="planContent" name="planContent" value="${planContent}"/>
	 		 &nbsp;&nbsp;
			<input name="" type="button" value="查询" onclick="searchPage()"/>
		</p>
	</div>
	<div id="pageDataList">
	<div class="main_list">
	<table width="100%" border="0">
	  <tr>
	    <th>序号</th>
	    <th>待办类型</th>
	    <th>待办标题</th>
	    <th>待办内容</th>
	    <th>创建时间</th>
	    <th>待办状态</th>
	    <th>撤回原因</th>
	    <th>操作</th>
	  </tr>
	  
	  <c:forEach var="m" items="${pageObject.data }" varStatus="vs">
  	     <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
	        <td>${(pageObject.curPage-1)*pageObject.pageSize+vs.count }</td>
		    <td><g:sysDict dictCode="DICT_PLAN_TYPE" dictKey="${m.planType}"/></td>
		    <td>${m.planTitle}</td>
		    <td>${m.planContent}</td>
		    <td>${m.createTime}</td>
		    <td><g:sysDict dictCode="DICT_PLAN_STATE" dictKey="${m.planState}"/></td>
		    <td>${m.reason}</td>
		    <td>
		    	<a href="system/workPlan_searchWorkPlanDetail.do?planId=${m.planId }&source=2">查看</a>
		    </td>
		 </tr>
	  </c:forEach>
	  <c:if test="${pageObject.data == null || empty pageObject.data}">
	  	  <tr>
		    <td colspan=7>查询不到待办数据</td>
		  </tr>
	  </c:if>
	 </table>
	 </div>
	 <g:page pageObject="${pageObject }" />
	 </div>
  
  </form>
  </body>
</html>
