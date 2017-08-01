<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="g" uri="/gateway-tags" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath %>"/>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>用户权限分配</title>
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script>

function updateBtnList(func){
	var funcCode=$(func).attr("funcCode");
	$("[name=td_"+funcCode+"] [name=buttonList]").prop("checked",func.checked);
}

function updateFunc(func){
	var funcCode=$(func).attr("funcCode");
	if(func.checked){
		$("input[name=funcList][funcCode="+funcCode+"]").prop("checked",func.checked);
	}else{
		if($("input[name=buttonList][funcCode="+funcCode+"]:checked").length==0){
			$("input[name=funcList][funcCode="+funcCode+"]").prop("checked",false);
		}
	}
}

function saveUserAuth(){
	var chooseFuncList=new Array();
	$("input[name='funcList']:checkbox:checked").each(function(){
		chooseFuncList.push($(this).val());
	});
	$("#chooseFuncs").val(chooseFuncList.toString());
	
	var chooseButtonList=new Array();
	$("input[name='buttonList']:checkbox:checked").each(function(){
		chooseButtonList.push($(this).val());
	});
	$("#chooseButtons").val(chooseButtonList.toString());
	
	$("#saveBtn,#saveBtning").toggle();
	$.ajax({
		type: "POST",
		url: "system/user_saveUserTypeFunc.do",
 		data: $("#userAuthForm").serialize(),
		success: function(data){
			$("#saveBtn,#saveBtning").toggle();
			if (data.state == 'success') {
				alert("授权成功！");
				history.go(-1);
			} else {
				alert(data.message);
			}
		}
   	});
}
</script>
</head>
<body class="FrameMain">
<form id="userAuthForm" name="userAuthForm">
<input type="hidden" name="chooseFuncs" id="chooseFuncs"/>
<input type="hidden" name="chooseButtons" id="chooseButtons"/>
<input type="hidden" name="user.userType" id="userType" value="${user.userType}"/>

<div class="main_title">
	<b>用户类型菜单分配</b>
</div>
<div class="main_search">
	<p>用户类型:<g:sysDict dictCode="DICT_USER_TYPE" dictKey="${user.userType}"/></p>
</div>

<div class="main_info_title">菜单分配</div>
<div class="main_infomation">
    <div class="main_list">
    <table width="100%" border="0">
      <tr>
        <th width="90"></th>
        <th width="150">菜单编码</th>
        <th width="150">菜单名称</th>
        <th width="180">菜单模块</th>
        <th class="font_l">页面操作按钮</th>
      </tr>
       <c:forEach var="view" items="${funcAndBtnList}" varStatus="vs">
       	 <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
        	<td>
        	<c:choose> 
  			<c:when test="${view.funcCode == 'userTypeManage'}">  
  				<c:choose>  
  				<c:when test="${user.userType=='superUser' || user.userType=='mainateUser'}">   
    				<input type="checkbox" funcCode="${view.funcCode}" name="funcList"  value="${view.funcCode}" checked="true" disabled />
    				<input type="checkbox" funcCode="${view.funcCode}" name="funcList"  value="${view.funcCode}" checked="true" style="display:none"/>
  				</c:when>
    			<c:otherwise>   
					<input type="checkbox" funcCode="${view.funcCode}" name="funcList"  value="${view.funcCode}" disabled/>
  				</c:otherwise> 
  				</c:choose>
  			</c:when> 
  			<c:otherwise>   
				<input type="checkbox" funcCode="${view.funcCode}" name="funcList" onclick="updateBtnList(this)" value="${view.funcCode}" ${view.userType !=""  ? "checked" : ""}/>
  			</c:otherwise> 
			</c:choose> 
        	
        	</td>
        	<td>${view.funcCode}</td>
       	 	<td>${view.funcName}</td>
       	 	<td>${view.parentName}</td>
        	<td class="font_l" name="td_${view.funcCode}">
       		 <c:forEach var="button" items="${view.buttonList}" varStatus="vs">
       		 <c:choose> 
	  			<c:when test="${view.funcCode == 'userTypeManage'}">  
	  				<c:choose>  
	  				<c:when test="${user.userType=='superUser'}">   
	  					<input name="buttonList" type="checkbox" funcCode="${view.funcCode}" value="${button.buttonCode}" checked=true style="display:none"/>
	    				<input name="buttonList" type="checkbox" funcCode="${view.funcCode}" value="${button.buttonCode}" checked=true disabled/>
	        	   		${button.operateDesc}
	  				</c:when>
	  				<c:when test="${user.userType=='mainateUser'}">
	    				<input name="buttonList" type="checkbox" funcCode="${view.funcCode}" value="${button.buttonCode}" ${button.buttonCode=='userTypeFuncAuth'?'disabled':''} ${button.userType !=""  ? "checked" : ""}/>
	        	   		${button.operateDesc}
	  				</c:when>
	    			<c:otherwise>   
						<input name="buttonList" type="checkbox" funcCode="${view.funcCode}"  value="${button.buttonCode}" disabled />
	        	   		${button.operateDesc}
	  				</c:otherwise> 
	  				</c:choose>
	  			</c:when> 
	  			<c:otherwise>   
					<input name="buttonList" type="checkbox" funcCode="${view.funcCode}" onclick="updateFunc(this)" value="${button.buttonCode}" ${button.userType !=""  ? "checked" : ""} />
	        	   		${button.operateDesc}
	  			</c:otherwise> 
			 </c:choose> 
       		 </c:forEach>
        	</td>
      	 </tr>
       </c:forEach>
    </table>
    </div>
</div>

</br>
</br>
<div align="center">
	<input type="button" onclick="saveUserAuth()" id="saveBtn" value="保存"/>
	<input type="button" value="保存中" id="saveBtning" style="display:none;color:gray" disabled/>
	<input type="button" onclick="history.go(-1)" value="返回"/>
</div>
</form>
</body>
</html>
