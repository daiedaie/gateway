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
<title>Title</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" /> 
<script>
function logout(){
	top.location.href = 'clear.do';
}
</script>

</head>

<body class="FrameTitle">
    <div class="frame_top">
        <ul>
            <li class="user">
                <p>欢迎您，<span>${sessionScope.SESSION_ATTRIBUTE_USER_INFO.userName }</span>!</p>
            </li>
            <li>
            <a href="system/user/password1.jsp" target="mainFrame" title="修改登录密码"><img src="resource/images/top_edit.png" />修改登录密码</a></li>
            <c:if test="${sessionScope.SESSION_ATTRIBUTE_USER_INFO.userType == 'dataUser' }">
            	<li><a href="system/gwUser_searchUserFileEncryPwd.do" target="mainFrame" title="修改文件加密密码"><img src="resource/images/top_edit.png" />修改文件加密密码</a></li>
            </c:if>
            <li><a href="javascript:void(0)" onclick="logout()" title="退出登录"><img src="resource/images/top_out.png" />退出登录</a></li>
        </ul>
    </div>
</body>
</html>
