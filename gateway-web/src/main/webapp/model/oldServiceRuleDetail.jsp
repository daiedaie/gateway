<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath %>" />
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>旧服务规则详情页_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>

</head>	
<body>
  <div class="main_upload" method="post">
  <form id="searchForm" action="" method="post" loadContainer="">
		<div class="main_title">
	         	<b id="old_titile">合规检查规则查看</b>
		</div>
	</form>
	</div>
	<div id="ruleDiv">
		<div id="ruleTable" class="main_list" style="padding:0 20px 0 20px">
			<table width="100%" border="0" >
			  <tr>
			    <th>字段编码</th>
			    <th>字段名称</th>
			    <th>字段类型</th>
			    <th>检查类型</th>
			    <th>检查规则</th>
			  </tr>
	  
			  <c:forEach var="rule" items="${checkRuleAuditList}" varStatus="vs">
		  	     <tr name="fieldTr">
				    <td>${rule.fieldCode}</td>
				    <td>${rule.fieldName}</td>
				    <td>${rule.fieldType}</td>
				    <td>${checkDictMap[rule.checkType].dictValue }</td>
				    <td>${rule.checkRule}</td>
				 </tr>
			  </c:forEach>
			  
	 		</table>
		</div>
		</div>	
</body>
</html>
