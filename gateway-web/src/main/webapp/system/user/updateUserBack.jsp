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
<script>
function endWorkPlan(){
	var form=$("#verifyForm");
	form.submit();
}
</script>
<title>用户修改退回</title>
</head>
  <body class="FrameMain">
	
	<div class="main_title">
		<b>用户修改退回</b>
	</div>
	<form id="verifyForm" name="verifyForm" action="system/workPlan_endUserBack.do">
	<table width="100%" border="0">
		  <tr>
		  		<td align="right">创建人：</td>
		  		<td>${createUser.loginName}</td>
		  </tr>
		  <tr>
		  		<td align="right">创建时间：</td>
		  		<td>${workPlanVO.createTime}</td>
		  </tr>
		  <tr>
		  		<td align="right">待办标题：</td>
		  		<td>${workPlanVO.planTitle}</td>
		  </tr>
		  <tr>
		  		<td align="right">待办内容：</td>
		  		<td>${workPlanVO.planContent}</td>
		  </tr>
		  <tr>
		  		<td valign="top" align="right">处理意见：</td>
		  		<td><textarea id="suggestion" name="suggestion" rows="6" cols="100" readonly="true"/>${workPlanVO.suggestion}</textarea></td>
		  </tr>
	</table>
	<div style="margin:20px 0px 0px 300px">
		<input type="button" value="确定" onclick="endWorkPlan()"/>
	</div>
    <input type="hidden" id="planId" name="planId" value="${planId}"/>
	<input type="hidden" id="userId" name="userId" value="${userId }"/>
	<input type="hidden" id="passTag" name="passTag" />
	<input type="hidden" id="extenTableKey" name="extenTableKey" value="${workPlanVO.extenTableKey }"/>
	
	<div class="main_list">
	<table width="60%" border="1">
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
	  </form>
  </body>
</html>
