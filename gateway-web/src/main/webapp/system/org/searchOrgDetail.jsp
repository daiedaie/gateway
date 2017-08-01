<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="g" uri="/gateway-tags" %>
<%@ include file="/common/include.jsp"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath%>" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<title>详细页</title>
</head>
<link rel="stylesheet" href="/css/style.css" />
<body class="FrameMain">

<div class="main_title">
	<b>机构明细</b>
</div>
<div class="main_info_title">机构信息
</div>
	<table width="100%" border="0">
        <tr>
        	<td width="10%" align="right">机构名称：</td>
            <td width="23%">${org.orgName}</td>
            <td width="10%" align="right">证件编号：</td>
            <td width="23%">${org.certNo}</td>
         	<td width="10%" align="right">法人名称：</td>
            <td width="23%">${org.orgHeadName}</td>
        </tr>
        <tr>
       		<td width="10%" align="right">联系电话：</td>
            <td width="23%">${org.orgTel}</td>
            <td width="10%" align="right">工商编码：</td>
            <td width="23%">${org.regCode}</td>
         	<td width="10%" align="right">公司地址：</td>
            <td width="23%">${org.orgAddr}</td>
        </tr>
    </table>


<div class="main_info_title">机构用户信息</div>
	<table width="100%" border="0">
        <tr>
        	<td width="10%" align="right">登录账号：</td>
            <td width="23%">${orgUser.loginName}</td>
            <td width="10%" align="right">姓名：</td>
            <td width="23%">${orgUser.userName}</td>
         	<td width="10%" align="right">用户类型：</td>
            <td width="23%"><g:sysDict dictCode="DICT_USER_TYPE" dictKey="${orgUser.userType}"/></td>
        </tr>
        <tr>
        	<td width="10%" align="right">身份证号：</td>
            <td width="23%">${orgUser.certNo}</td>
            <td width="10%" align="right">邮箱：</td>
            <td width="23%">${orgUser.email}</td>
         	<td width="10%" align="right">联系电话：</td>
            <td width="23%">${orgUser.moblie}</td>
        </tr>
        <tr>
        	<td width="10%" align="right">联系地址：</td>
            <td width="23%">${orgUser.addr}</td>
            <td width="10%" align="right">证件扫描附件:</td>
            <td width="23%">
            	<c:if test="${orgUser.fileId != null }">
      	  	  	    <a href="system/file_downLoadFile.do?fileId=${orgUser.fileId}" target="_blank">
      	  	  	    	<span class="c_red">${fileVo.realName}</span></a>
      	  	  	  	</c:if>
      	  	</td>
        </tr>
    </table>


<div class="main_info_title">机构下用户列表</div>
<div class="main_infomation">
	<div class="main_list">
	 	<c:choose>
	 		<c:when test="${dataUserList != null && not empty dataUserList}">
				<table width="100%" border="0">
		        <tr>
		            <th width="20%">登录账号</th>
		            <th width="20%">姓名</th>
		            <th width="20%">用户类型</th>
		            <th width="20%">操作</th>
		        </tr>
		        <c:forEach var="user" items="${dataUserList}" varStatus="vs">
		        <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
		            <td>${user.loginName}</td>
		            <td>${user.userName}</td>
		            <td><g:sysDict dictCode="DICT_USER_TYPE" dictKey="${user.userType}"/></td>
		            <td><a href="system/user_searchUserDetail.do?user.userId=${user.userId}&sourceFlag=2">查看</a></td>
		        </tr>
		        </c:forEach>
			    </table>
	 		</c:when>
	 		<c:otherwise>
	 				<div style="text-align:center">查询不到机构下的数据用户</div>
	 		</c:otherwise>
	 	</c:choose>
	</div>
</div>

</br>
</br>
<div align="center">
	<input type="button" onclick="history.go(-1)" value="返回"/>
	<c:if test="${userVO.userType == 'orgUser'}">
	<c:choose>
		<c:when test="${updateMap[orgUser.userId]==null}">
			<input type="button" onclick="location.href='system/org_updateOrgPage.do?orgVO.orgId=${org.orgId}'" value="修改"/>
		</c:when>
	<c:otherwise>
		<input type="button" onclick="" value="修改待审" disabled style="color:gray"/>
	</c:otherwise>
	</c:choose>
  	</c:if>
</div>
</body>
</html>
