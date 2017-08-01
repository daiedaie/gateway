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
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<title>用户修改审核</title>
</head>
<script type="text/javascript">

   function pass(){
      if(confirm("通过用户修改审核")){
	      var form1=document.getElementById("verifyForm");
	      document.getElementById("passTag").value="2";
	      form1.submit();
      }
   }
   
   function noPass(){
      if(confirm("不通过用户修改审核？")){
	      var form1=document.getElementById("verifyForm");
	      document.getElementById("passTag").value="0";
	      form1.submit();
      }
   }
</script>
  <body class="FrameMain">
	
	<div class="main_title">
		<b>用户修改审核</b>
	</div>
	<form id="verifyForm" name="verifyForm" action="system/workPlan_verifyUpdateUser.do">
	<input type="hidden" id="planId" name="planId" value="${planId}"/>
	<input type="hidden" id="userId" name="userId" value="${userId }"/>
	<input type="hidden" id="passTag" name="passTag" />
	<input type="hidden" id="extenTableKey" name="extenTableKey" value="${workPlanVO.extenTableKey }"/>
	<table width="100%" border="0">
	      <tr>
		  		<td style="text-align:right">创建人：</td>
		  		<td>${createUser.loginName}</td>
		  </tr>
		  <tr>
		  		<td style="text-align:right">创建时间：</td>
		  		<td>${workPlanVO.createTime}</td>
		  </tr>
		  <tr>
		  		<td style="text-align:right">待办标题：</td>
		  		<td>${workPlanVO.planTitle}</td>
		  </tr>
		  <tr>
		  		<td style="text-align:right">待办内容：</td>
		  		<td>${workPlanVO.planContent}</td>
		  </tr>
		  <tr>
		  		<td style="text-align:right" valign="top">审核意见：</td>
		  		<td><s:textarea id="suggestion" name="suggestion" rows="6" cols="100" /></td>
		  </tr>
	</table>
	<div class="main_list">
	<table width="60%" border="0">
		  <tr>
				<th>属性名称</th>
				<th>旧属性值</th>
				<th>新属性值</th>
			</tr>
			<c:forEach var="modifyRecordVO" items="${gwModifyRecordVOList}" >
			  	<tr>
			  		<td><g:sysDict dictCode="DICT_USER_COLUMN" dictKey="${modifyRecordVO.columsCode}"/></td>
					<td>
						<c:choose>
							<c:when test="${modifyRecordVO.columsCode=='user_fileid' }">
								<a href="system/file_downLoadFile.do?fileId=${modifyRecordVO.beforeValue}" target="_blank">
				      	  	  	   <span class="c_red">${beforeFile.realName }</span>
				      	  	  	</a>
							</c:when>
							<c:when test="${modifyRecordVO.columsCode=='push_ftp' }">
								<c:if test="${modifyRecordVO.beforeValue=='1'}">需要</c:if>
								<c:if test="${modifyRecordVO.beforeValue!='1'}">不需要</c:if>
							</c:when>
							<c:otherwise>
								${modifyRecordVO.beforeValue}
							</c:otherwise>
						</c:choose>
					</td>
					<td>
						<c:choose>
							<c:when test="${modifyRecordVO.columsCode=='user_fileid' }">
								 <a href="system/file_downLoadFile.do?fileId=${modifyRecordVO.afterValue}" target="_blank">
				      	  	  	    <span class="c_red">${afterFile.realName }</span>
				      	  	  	 </a>
							</c:when>
							<c:when test="${modifyRecordVO.columsCode=='push_ftp' }">
								<c:if test="${modifyRecordVO.afterValue=='1'}">需要</c:if>
								<c:if test="${modifyRecordVO.afterValue!='1'}">不需要</c:if>
							</c:when>
							<c:otherwise>
								${modifyRecordVO.afterValue}
							</c:otherwise>
						</c:choose>
					</td>
			  	</tr>
		  	</c:forEach>
	</table>
	</div>
	<div style="padding:20px 0 0 20%">
	    	<input name="" type="button" value="通过" onclick="pass()"/>
	
    	&nbsp;
	    <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['noPassUser']!=null}">
	    	<input name="" type="button" value="不通过" onclick="noPass()"/>
	    </c:if>
	</div>
  </form>
</body>
</html>
