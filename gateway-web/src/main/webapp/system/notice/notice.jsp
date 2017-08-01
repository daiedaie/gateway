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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>公告信息_数据网关Gateway服务平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
</head>
  
<body>
  <div class="main_title">
	  <b>公告信息查看</b>
  </div>
  <div class="main_info_title">公告信息</div>
  <table width="100%" border="0" cellspacing="10px">
	  <tr>
	    <td width="10%" align="right">创建人：</td>
	    <td width="90%" align="left">${notice.createUser }</td>
	  </tr>
	  <tr>
	    <td width="10%" align="right">创建时间：</td>
	    <td width="90%" align="left"><fmt:formatDate value="${notice.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
	  </tr>
	  <tr>
	    <td width="10%" align="right">公告标题：</td>
	    <td width="90%" align="left">${notice.noticeTitle }</td>
	  </tr>
	  <tr valign="top">
	    <td width="10%" align="right">公告内容：</td>
	    <td width="90%" align="left">
	    	<div style="width:70%">
	    		${notice.noticeContent}
	    	</div>
	    </td>
	  </tr>
  </table>
  <div style="margin:20px 0px 0px 300px">
		<input type="button" value="返回" onclick="history.go(-1)"/>
  </div>
</body>
</html>
