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
<title>用户注销退回_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script>
function endWorkPlan(){
	var form=$("#verifyForm");
	form.submit();
}
</script>
</head>
  
<body>
  <div class="main_title">
		<b>用户注销退回</b>
  </div>
  <form id="verifyForm" name="verifyForm" action="system/workPlan_endUserCancelBack.do"  method="post">
	   	<div class="main_info_title">审核信息</div>
		<input type="hidden" name="planId" value="${planId }"/>
		<table width="100%" border="0">
			  <tr>
			    <td width="10%" align="right">创建人：</td>
			    <td width="90%" align="left">${createUser.loginName }</td>
			  </tr>
			  <tr>
			    <td width="10%" align="right">创建时间：</td>
			    <td width="90%" align="left">${preWorkPlanVO.createTime }</td>
			  </tr>
			  <tr>
			    <td width="10%" align="right">任务标题：</td>
			    <td width="90%" align="left">${preWorkPlanVO.planTitle }</td>
			  </tr>
			  <tr>
			    <td width="10%" align="right">任务内容：</td>
			    <td width="90%" align="left">${preWorkPlanVO.planContent }</td>
			  </tr>
			  <tr>
			    <td width="10%" align="right">审核状态：</td>
			    <td width="90%" align="left"><g:sysDict dictCode="DICT_PLAN_STATE" dictKey="${preWorkPlanVO.planState }"/></td>
			  </tr>
			  <tr>
			    <td width="10%" align="right" valign="top">处理意见：</td>
			    <td width="90%" align="left"><textarea id="suggestion" name="workPlan.suggestion" rows="6" cols="100" readonly>${preWorkPlanVO.suggestion }</textarea></td>
			  </tr>
	     </table>
	     <div style="margin:20px 0px 0px 250px">
				<input type="button" value="确定" onclick="endWorkPlan()"/>
		  </div>
     	  <div class="main_info_title">待注销用户信息</div>
          <table width="100%" border="0">
      	  	  <tr>
      	  	  	  <td width="10%" align="right">登录账号：</td>
      	  	  	  <td width="30%">${userVO.loginName }</td>
      	  	  	  <td width="10%" align="right">姓名：</td>
      	  	  	  <td width="20%">${userVO.userName }</td>
		      	  <td width="10%" align="right">用户类型：</td>
      	  	  	  <td width="20%"><g:sysDict dictCode="DICT_USER_TYPE" dictKey="${userVO.userType }"/></td>
      	  	  </tr>
      	  	  <tr>
      	  	  	  <td width="10%" align="right">身份证号：</td>
      	  	  	  <td>${userVO.certNo }</td>
      	  	  	  <td width="10%" align="right">邮箱：</td>
      	  	  	  <td>${userVO.email }</td>
		      	  <td width="10%" align="right">联系电话：</td>
      	  	  	  <td>${userVO.moblie }</td>
      	  	  </tr>
      	  	  <tr>
      	  	  	  <td width="10%" align="right">联系地址：</td>
      	  	  	  <td>${userVO.addr }</td>
      	  	  	  <td></td>
      	  	  	  <td></td>
		      	  <td></td>
      	  	  	  <td></td>
      	  	  </tr>
		  </table>
		  
		  <c:if test="${dataUserList != null && not empty dataUserList }">
		  	  <div class="main_info_title">待注销数据用户列表</div>
	          <table width="100%" border="0">
	          	  <c:forEach var="u" items="${dataUserList }">
		      	  	  <tr>
		      	  	  	  <td width="10%" align="right">登录账号：</td>
		      	  	  	  <td width="30%">${u.loginName }</td>
		      	  	  	  <td width="10%" align="right">姓名：</td>
		      	  	  	  <td width="20%">${u.userName }</td>
		      	  	  	  <td width="10%" align="right">身份证号：</td>
		      	  	  	  <td>${u.certNo }</td>
		      	  	  </tr>
		      	  	  <tr>
		      	  	  	  <td width="10%" align="right">邮箱：</td>
		      	  	  	  <td>${u.email }</td>
				      	  <td width="10%" align="right">联系电话：</td>
		      	  	  	  <td>${u.moblie }</td>
		      	  	  	  <td width="10%" align="right">联系地址：</td>
		      	  	  	  <td colspan=5>${u.addr }</td>
		      	  	  </tr>
	          	  </c:forEach>
			  </table>
		  </c:if>
		  
     	  <div class="main_info_title">机构信息</div>
          <table width="100%" border="0">
      	  	  <tr>
      	  	  	  <td width="10%" align="right">机构名称：</td>
      	  	  	  <td width="30%">${orgVO.orgName }</td>
      	  	  	  <td width="10%" align="right">证件类型：</td>
      	  	  	  <td width="20%">${orgVO.certType }</td>
		      	  <td width="10%" align="right">证件编号：</td>
      	  	  	  <td width="20%">${orgVO.certNo }</td>
      	  	  </tr>
      	  	  <tr>
      	  	  	  <td width="10%" align="right">工商编号：</td>
      	  	  	  <td>${orgVO.regCode }</td>
      	  	  	  <td width="10%" align="right">法人名称：</td>
      	  	  	  <td>${orgVO.orgHeadName }</td>
		      	  <td width="10%" align="right">联系电话：</td>
      	  	  	  <td>${orgVO.orgTel }</td>
      	  	  </tr>
      	  	  <tr>
      	  	  	  <td width="10%" align="right">联系公司地址：</td>
      	  	  	  <td>${orgVO.orgAddr }</td>
      	  	  	  <td ></td>
      	  	  	  <td></td>
		      	  <td ></td>
      	  	  	  <td></td>
      	  	  </tr>
		  </table>
  </form>
</body>
</html>
