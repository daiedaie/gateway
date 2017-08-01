<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
<base href="<%=basePath %>"/>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>数据网关Gateway管理平台</title>
</head>
<frameset rows="80,*" cols="*" frameborder="no" border="0" framespacing="0">
  <frame src="FrameTitle.jsp" id="topFrame" name="topFrame" scrolling="No" noresize="noresize" />
  <frameset id="FrameStat" cols="200,*" frameborder="no" border="0" framespacing="0">
    <frame src="system/func_searchMenuList.do" id="leftFrame" name="leftFrame" scrolling="yes" />
    <frame src="system/func_searchMain.do" id="mainFrame" name="mainFrame" noresize="noresize" />
  </frameset>
</frameset>
<noframes>
<body>
</body>
</noframes>
</html>
