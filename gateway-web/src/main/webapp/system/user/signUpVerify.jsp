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
<title>用户注册_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">

   function pass(){
      if(confirm("通过该用户注册申请？")){
	      var form1=document.getElementById("verifyForm");
	      document.getElementById("passTag").value="2";
	      form1.action="system/workPlan_verifyUser.do";
	      form1.submit();
	      
      }
   }
   
   function passAndAllot(){
      if(confirm("通过该用户注册申请，并配置用户权限？")){
	      var form1=document.getElementById("verifyForm");
	      document.getElementById("passTag").value="2";
	      document.getElementById("allotTag").value="1";
	      form1.action="system/workPlan_verifyUser.do?source=2";
	      form1.submit();
	      
      }
   }
   
   function noPass(){
      if(confirm("不通过该用户注册申请？")){
	      var form1=document.getElementById("verifyForm");
	      document.getElementById("passTag").value="0";
	      form1.action="system/workPlan_verifyUser.do";
	      form1.submit();
      }
   }
</script>
</head>
  
<body>
  <div class="main_title">
		<b>用户注册审核</b>
  </div>
  <form id="verifyForm" name="verifyForm" action="system/workPlan_verifyUser.do"  method="post">
    <input type="hidden" id="planId" name="planId" value="${planId}"/>
	<input type="hidden" id="userId" name="userId" value="${userId }"/>
	<input type="hidden" id="passTag" name="passTag" />
	<input type="hidden" id="allotTag" name="allotTag" />
	<input type="hidden" name="realName" id="realName"/>
	<input type="hidden" name="filePath" id="filePath"/>
   <div class="main_info_title">审核信息</div>
	<table width="100%" border="0">
		  <tr >
		    <td width="10%" align="right" valign="top">审核意见：</td>
		    <td width="90%" align="left"><s:textarea id="suggestion" name="suggestion" rows="6" cols="100" /></td>
		  </tr>
		  <tr>
		  	<td colspan="2" align="center">
		  	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['passUser']!=null}">
		    	<input name="" type="button" value="通过" onclick="pass()"/>
		    </c:if>
		    &nbsp;&nbsp;
		    <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['passUser']!=null}">
		    	<input name="" type="button" value="通过并分配权限" onclick="passAndAllot()"/>
		    </c:if>
		    &nbsp;&nbsp;
		    <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['noPassUser']!=null}">
		    	<input name="" type="button" value="不通过" onclick="noPass()"/>
		    </c:if>
		    </td>
		  </tr>
     </table>
     <div class="main_info_title">用户信息</div>
          <table width="100%" border="0">
      	  	  <tr>
      	  	  	  <td width="10%" align="right">登录账号：</td>
      	  	  	  <td width="23%">${gwUserVo.loginName }</td>
      	  	  	  <td width="10%" align="right">姓名：</td>
      	  	  	  <td width="23%">${gwUserVo.userName }</td>
		      	  <td width="10%" align="right">用户类型：</td>
      	  	  	  <td width="23%">${gwUserVo.userType }</td>
      	  	  </tr>
      	  	  <tr>
      	  	  	  <td width="10%" align="right">身份证号：</td>
      	  	  	  <td>${gwUserVo.certNo }</td>
      	  	  	  <td width="10%" align="right">邮箱：</td>
      	  	  	  <td>${gwUserVo.email }</td>
		      	  <td width="10%" align="right">联系电话：</td>
      	  	  	  <td>${gwUserVo.moblie }</td>
      	  	  </tr>
      	  	  <tr>
      	  	  	  <td width="10%" align="right">证件扫描附件:</td>
      	  	  	  <td>
      	  	  	  <c:if test="${gwUserVo.fileId != null }">
      	  	  	    <a href="system/file_downLoadFile.do?fileId=${gwUserVo.fileId}" target="_blank">
      	  	  	    <span class="c_red">${fileVo.realName}</span></a>
      	  	  	  </c:if>
      	  	  	  </td>
      	  	  	  <td width="10%" align="right">联系地址：</td>
      	  	  	  <td>${gwUserVo.addr }</td>
		      	  <td></td>
      	  	  	  <td></td>
      	  	  </tr>
		  </table>
     <div class="main_info_title">机构信息</div>
          <table width="100%" border="0">
      	  	  <tr>
      	  	  	  <td width="10%" align="right">机构名称：</td>
      	  	  	  <td width="23%">${gwOrgVo.orgName }</td>
      	  	  	  <td width="10%" align="right">法人名称：</td>
      	  	  	  <td width="23%">${gwOrgVo.orgHeadName }</td>
		      	  <td width="10%" align="right">法人身份证号码：</td>
      	  	  	  <td width="23%">${gwOrgVo.certNo }</td>
      	  	  </tr>
      	  	  <tr>
      	  	  	  <td width="10%" align="right">工商编号：</td>
      	  	  	  <td>${gwOrgVo.regCode }</td>
      	  	  	  <td width="10%" align="right">联系公司地址：</td>
      	  	  	  <td>${gwOrgVo.orgAddr }</td>
		      	  <td width="10%" align="right">联系电话：</td>
      	  	  	  <td>${gwOrgVo.orgTel }</td>
      	  	  </tr>
		  </table>
  </form>
</body>
</html>
