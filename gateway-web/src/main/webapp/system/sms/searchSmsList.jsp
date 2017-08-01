<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="g" uri="/gateway-tags" %>
<%@ page isELIgnored="false" %>
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
<title>短信记录查询_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script>
</script>
</head>
<body class="FrameMain" id="FrameMain">
<form id="searchSmsListForm" name="searchSmsListForm" action="system/sms_searchSmsList.do" loadContainer="pageDataList">
	<div class="main_title">
		<b>短信记录查看</b>
    </div>
	<div class="main_search">
		<p>
			手机号码：<input name="smsVO.smsMobile" type="text" />
			发送状态：
			<select name="smsVO.sendStatus">
				<option value="">全部</option>
				<option value="-1">待发送</option>
				<option value="1">发送成功</option>
				<option value="0">发送失败</option>
			</select>
			<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['findSMS']!=null}">
				<input name="searchSMSList" type="button" value="查询" onclick="searchPage()" />
			</c:if>	
		</p>
	</div>

	<div id="pageDataList">
	<div class="main_list" id="main_list">
			<table width="100%" border="0">
				<tr>
					<th>序号</th>
					<th><a class="tableSort" sort="smsContent">短信内容</a></th>
					<th><a class="tableSort" sort="smsMobile">短信接收人</a></th>
					<th><a class="tableSort" sort="sendStatus">短信状态</a></th>
					<th><a class="tableSort" sort="sendCount">发送次数</a></th>
					<th><a class="tableSort" sort="createTime">创建时间</a></th>
					<th><a class="tableSort" sort="sendTime">发送时间</a></th>
				</tr>

				<c:forEach var="sms" items="${pageObject.data}" varStatus="status">
					<tr ${status.index % 2 == 1? "class='list_bg'" : "" }>
						<td>${(pageObject.curPage-1)*pageObject.pageSize+status.count}</td>
						<td>${sms.smsContent}</td>
						<td>${sms.smsMobile}</td>
						<td>
						<c:if test="${sms.sendStatus==null}">
							待发送
						</c:if>
						<c:if test="${sms.sendStatus!=null}">
							<g:sysDict dictCode="DICT_SEND_STATUS" dictKey="${sms.sendStatus}"/>
						</c:if>
						</td>
						<td>${sms.sendCount}</td>
						<td>${sms.createTime}</td>
						<td>${sms.sendTime}</td>
					</tr>
				</c:forEach>
				
				<c:if test="${pageObject.data == null || empty pageObject.data}">
  	  				<tr>
	    				<td colspan=7>查询不到短信记录信息</td>
	  				</tr>
  				</c:if>
			</table>
	</div>
	<g:page pageObject="${pageObject }" />
	</div>
</form>
</body>
</html>
