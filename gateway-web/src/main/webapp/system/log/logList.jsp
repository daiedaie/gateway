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
<script type="text/javascript" src="<%=request.getContextPath() %>/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript">
function checkAndSubmit(){
    var startDate = $("#startDate").val();
    var endDate = $("#endDate").val();
    var d1 = new Date(startDate.replace(/\-/g, "\/"));  
	 var d2 = new Date(endDate.replace(/\-/g, "\/"));  
	
	  if(startDate!=""&&endDate!=""&&d1 >d2)  
	 {  
	    alert("开始时间不能大于结束时间！");  
	    return false;  
	 }else{
	 	searchPage();
	 }

}
</script>
<title>待办列表</title>
</head>
<body class="FrameMain">
  <form id="searchForm" action="system/operLog_searchLogList.do" method="post" loadContainer="pageDataList">
  <div class="main_title">
		<b>操作日志查看</b>
	</div>
	<div class="main_search">
	  <table width="100%" border="0">
		  <tr>
		    <td width="80%">
		      	 操作用户账号：<input type="text" id="operUserCode" name="operUserCode" value="${operUserCode}"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
    			被处理用户账号：<input type="text" id="acceptUserCode" name="acceptUserCode" value="${acceptUserCode}"/>
    			&nbsp;&nbsp;&nbsp;&nbsp;
				操作类型：<g:sysDictList dictCode="DICT_OPERATE_TYPE" defaultValue="${operateType}" tagType="select" tagName="operateType" tagId="operateType"/>	    
		    </td>
		    <td colspan="2"></td>
		  </tr>
		  <tr>
		    <td width="80%">
		    	操作时间：从<input readonly="readonly" class="Wdate" type="text" onclick="WdatePicker()"  name="startDate" id="startDate" value="${startDate}"/>

		    	&nbsp;&nbsp;至&nbsp;&nbsp;<input class="Wdate" readonly="readonly" type="text" onclick="WdatePicker()" name="endDate" id="endDate" value="${endDate}"/>
		    	&nbsp;&nbsp;&nbsp;&nbsp; 
		    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['findOperLog']!=null}">
		    		<input name="" type="button" value="查询" onclick="checkAndSubmit()"/>		 
		    	</c:if>   	   	
		    </td>
		    <td></td>
		  </tr>
	  </table>
	 </div>
	 <div id="pageDataList">
	 <div class="main_list">
	<table width="100%" border="0">
	  <tr>
	    <th>序号</th>
	    <th><a class="tableSort" sort="operationUser">操作用户编码</a></th>
	    <th><a class="tableSort" sort="acceptUser">被处理用户编码</a></th>
	    <th><a class="tableSort" sort="operationType">操作类型</a></th>
	    <th><a class="tableSort" sort="operationContent">操作内容̬</a></th>
	    <th><a class="tableSort" sort="operationTime">操作时间</a></th>
	  </tr>
	  <c:forEach var="m" items="${pageObject.data }" varStatus="vs">
  	     <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
	        <td>${(pageObject.curPage-1)*pageObject.pageSize+vs.count }</td>
		    <td>${m.operationUser}</td>
		    <td>${m.acceptUser}</td>
		    <td><g:sysDict dictCode="DICT_OPERATE_TYPE" dictKey="${m.operationType}"/></td>
		    <td>${m.operationContent}
		    
		    </td>
		    <td>${m.operationTime}</td>
		 </tr>
	  </c:forEach>
	  <c:if test="${pageObject.data == null || empty pageObject.data}">
	  	  <tr>
		    <td colspan=7>查询不到系统操作日志数据</td>
		  </tr>
	  </c:if>
	 </table>
	 </div>
	 <g:page pageObject="${pageObject }" />
	 </div>
  </form>
</body>
</html>
