<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath%>">
<title>错误</title>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
</head>
<body class="FrameMain">
<table align="center" class="error">
    <tr>
      <td valign="middle" height="40"><img src="resource/images/wrong.gif" /></td>
      <td><strong>抱歉，您要访问的页面不存在或已被删除。</strong></td>
    </tr>
    <tr>
      <td></td>
      <td align="center" valign="top"><a href="javascript:history.back(-1);">[返回上一页]</a></td>
    </tr>
</table>
</body>
</html>