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
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript">
   function forwardNotice(noticeId){
   	  location.href="system/notice_searchNotice.do?notice.noticeId="+noticeId;
   }
   
   function deleteNotice(noticeId){
   		if(confirm("确定删除该公告信息?")){
   			var url = "system/notice_deleteNotice.do";
   			util.ajax(url,{"notice.noticeId":noticeId}, function(json){
   				if(json.state=='success'){
   					searchPage($("#curPage").val());
   				}else{
   					alert("删除失败:"+json.message);
   				}
   			});
   		}
   }
</script>
<title>公告列表</title>
</head>
  <body class="FrameMain">
  <form id="searchForm" action="system/notice_searchNoticeList.do" method="post" loadContainer="pageDataList">
	<div class="main_title">
		<b>公告列表查看</b>
	</div>
	
	<div class="main_search">
		<p>
    		公告标题：<input type="text" id="noticeTitle" name="notice.noticeTitle" value=""/>
	 		&nbsp;&nbsp;
	 		<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['searchNoticeList']!=null}">
			<input name="" type="button" value="查询" onclick="searchPage()"/>
			</c:if>
			<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['addNotice']!=null}">
				<input name="saveBtn" id="saveBtn" type="button" value="新增" onclick="window.location.href='<%=basePath %>system/notice/addNotice.jsp';"/>
			</c:if>
		</p>
	</div>
	<div id="pageDataList">
	<div class="main_list">
	<table width="100%" border="0">
	  <tr>
	    <th>序号</th>
	    <th><a class="tableSort" sort="notice_title">公告标题</a></th>
<!-- 	    <th><a class="tableSort" sort="notice_content">公告内容</a></th> -->
	    <th><a class="tableSort" sort="create_user">创建人</a></th>
	    <th><a class="tableSort" sort="create_time">创建时间</a></th>
	    <th>操作</th>
	  </tr>
	  
	  <c:forEach var="notice" items="${pageObject.data }" varStatus="vs">
  	     <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
	        <td>${(pageObject.curPage-1)*pageObject.pageSize+vs.count }</td>
		    <td>${notice.noticeTitle}</td>
<!-- 		    <td>${notice.noticeContent}</td> -->
		    <td>${notice.createUser}</td>
		    <td><fmt:formatDate value="${notice.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
		    <td>
		    	<a href="javascript:forwardNotice('${notice.noticeId}')">查看</a>
		    	
		    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['updateNotice']!=null}">
		    		<a href="system/notice_editNotice.do?notice.noticeId=${notice.noticeId}">修改</a>
		    	</c:if>
		    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['deleteNotice']!=null}">
		    		<a href="javascript:deleteNotice('${notice.noticeId}')">删除</a>
		    	</c:if>
		    </td>
		 </tr>
	  </c:forEach>
	  <c:if test="${pageObject.data == null || empty pageObject.data}">
	  	  <tr>
		    <td colspan=6>查询不到公告数据</td>
		  </tr>
	  </c:if>
	 </table>
	 </div>
	 	<g:page pageObject="${pageObject }" />
	 </div>
  
  </form>
  </body>
</html>
