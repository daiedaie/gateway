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
   function forwardDoc(docId){
   	  location.href="system/doc_searchDoc.do?doc.DocId="+docId;
   }
   
   function deleteDoc(docId){
   		if(confirm("确定删除该帮助信息?")){
   			var url = "system/doc_deleteDoc.do";
   			util.ajax(url,{"doc.docId":docId}, function(json){
   				if(json.state=='success'){
   					searchPage($("#curPage").val());
   				}else{
   					alert("删除失败:"+json.message);
   				}
   			});
   		}
   }
</script>
<title>帮助文档列表</title>
</head>
  <body class="FrameMain">
  <form id="searchForm" action="system/doc_searchDocList.do" method="post" loadContainer="pageDataList">
	<div class="main_title">
		<b>帮助文档列表查看</b>
	</div>
	
	<div class="main_search">
		<p>
    		文档名称：<input type="text" id="realName" name="doc.fileVO.realName" value=""/>
	 		&nbsp;&nbsp;
	 		<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['searchDocList']!=null}">
			<input name="" type="button" value="查询" onclick="searchPage()"/>
			</c:if>
			<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['addDoc']!=null}">
				<input name="saveBtn" id="saveBtn" type="button" value="新增" onclick="window.location.href='<%=basePath %>system/doc/addDoc.jsp';"/>
			</c:if>
		</p>
	</div>
	<div id="pageDataList">
	<div class="main_list">
	<table width="100%" border="0">
	  <tr>
	    <th>序号</th>
	    <th><a class="tableSort" sort="real_name">文档名称</a></th>
	    <th><a class="tableSort" sort="create_user">创建人</a></th>
	    <th><a class="tableSort" sort="create_time">创建时间</a></th>
	    <th>操作</th>
	  </tr>
	  
	  <c:forEach var="doc" items="${pageObject.data }" varStatus="vs">
  	     <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
	        <td>${(pageObject.curPage-1)*pageObject.pageSize+vs.count }</td>
		    <td>
		    <a href="system/file_downLoadFile.do?fileId=${doc.fileVO.fileId}" target="_blank">${doc.fileVO.realName}</a>
		    </td>
		    <td>${doc.createUser}</td>
		    <td><fmt:formatDate value="${doc.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
		    <td>
		    	<a href="javascript:forwardDoc('${doc.docId}')">查看</a>
		    	
		    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['updateDoc']!=null}">
		    		<a href="system/doc_editDoc.do?doc.docId=${doc.docId}">修改</a>
		    	</c:if>
		    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['deleteDoc']!=null}">
		    		<a href="javascript:deleteDoc('${doc.docId}')">删除</a>
		    	</c:if>
		    </td>
		 </tr>
	  </c:forEach>
	  <c:if test="${pageObject.data == null || empty pageObject.data}">
	  	  <tr>
		    <td colspan=5>查询不到帮助信息</td>
		  </tr>
	  </c:if>
	 </table>
	 </div>
	 	<g:page pageObject="${pageObject }" />
	 </div>
  
  </form>
  </body>
</html>
