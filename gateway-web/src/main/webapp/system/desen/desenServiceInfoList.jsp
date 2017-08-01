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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>服务信息脱敏配置_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<link rel="stylesheet" href="resource/css/jquery-ui.css" type="text/css"></link>
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/jquery-ui.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript">	
	$(document).ready(function(){
		$("#loginName").val($.trim($("#loginName").val()));
		if($("#loginName").val() != ''){
			$("#searchBtn").trigger("click");
		}
	});
	
	function updateDesenServiceInfoList(){
		$("#updateDesenBtn,#updateDesenBtning").toggle();
		url = "system/desenModel_updateDesenServiceInfoList.do";
	   	util.ajax(url,$("#searchForm").serialize(),function(json){
	   		$("#updateDesenBtn,#updateDesenBtning").toggle();
			if (json.state == 'success') {
				alert("保存成功");
				searchPage($("#curPage").val());
			}else{
				alert("保存失败，原因："+json.message);
			}
	   	});
	}
	
	//查询同一用户、模型下其他服务的配置信息
	function searchModelServiceDesenInfo(userId,modelId){
		var params = {"userId":userId,"modelId":modelId};
		var url = "system/desenModel_searchModelServiceDesenInfo.do #dataList";
		$("#dialog-message").empty();
		util.load("dialog-message",url,params,function(){
			$("#dialog-message").dialog({
				modal: true,
				width: 900,
				height:300,
				maxHeight:500,
				buttons: {
					"确 定": function() {
						$(this).dialog("close");
					}
				}
			});
	   	});
	}
</script>
</head>	
<body class="FrameMain">
<div id="dialog-message" title="同一模型的其他服务配置" style="display:none;"></div>
<form id="searchForm" action="system/desenModel_searchDesenServiceInfoList.do" method="post" loadContainer="pageDataList">
	<div class="main_title">
	<b>服务信息脱敏配置</b>
</div>
<div class="main_search">
	<p>
	登录帐号：<input type="text" name="loginName" id="loginName" value="${param.loginName }"/>
	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['searchInfoDesenList']!=null}">
	<input name="searchBtn" id="searchBtn" type="button" value="查询" onclick="searchPage()"/>
	</c:if>
	</p>
</div>
<div id="pageDataList">
<div class="main_list">
<table width="100%" border="0">
  <tr>
    <th>序号</th>
    <th><a class="tableSort" sort="login_name">登录帐号</a></th>
    <th><a class="tableSort" sort="service_name">服务名称</a></th>
    <th><a class="tableSort" sort="service_code">服务编码</a></th>
    <th><a class="tableSort" sort="service_type">服务类型</a></th>
    <th><a class="tableSort" sort="model_name">模型名称</a></th>
    <th><a class="tableSort" sort="model_code">模型编码</a></th>
    <th>查看</th>
    <th>选择 </th>
  </tr>
  <c:forEach var="s" items="${pageObject.data }" varStatus="vs">
  	  <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
	    <td>${(pageObject.curPage-1)*pageObject.pageSize+vs.count }</td>
	    <td>${s.loginName }</td>
	    <td>${s.serviceName }</td>
	    <td>${s.serviceCode }</td>
	    <td><g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${s.serviceType }"/></td>
	    <td>${s.modelName }</td>
	    <td>${s.modelCode }</td>
	    <td>
	    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['detailModel']!=null}">
	    		<a href="service/serviceInfo_searchService.do?serviceView.serviceId=${s.serviceId}" target="_blank" title="查看服务信息">查看服务</a>
	    	</c:if>
	    	<!-- 
	    	<c:if test="${s.modelId != null && s.modelId != ''}">
	    	|
	    	<a href="javascript:void(0)" onclick="searchModelServiceDesenInfo('${s.userId}','${s.modelId}')" title="查看同一模型下其他服务配置">相似服务</a>
	    	</c:if>
	    	 -->
	    </td>
	    <td>
	    	<input type="hidden" name="desenInfoList[${vs.index }].infoDeseId" id="infoDeseId${vs.index }" value="${s.infoDeseId }"/>
	    	<input type="hidden" name="desenInfoList[${vs.index }].userId" id="userId${vs.index }" value="${s.userId }"/>
	    	<input type="hidden" name="desenInfoList[${vs.index }].serviceId" id="service${vs.index }" value="${s.serviceId }"/>
	    	<label><input type="checkbox" name="desenInfoList[${vs.index }].modelInfo" id="modelInfo${vs.index }" value="1" ${s.modelInfo=='1'?"checked":""} ${SESSION_ATTRIBUTE_USER_BUTTON['saveInfoDesenConf']==null?"disabled":""}/>模型信息</label>
	    	<label><input type="checkbox" name="desenInfoList[${vs.index }].serviceInputInfo" id="serviceInputInfo${vs.index }" value="1" ${s.serviceInputInfo=='1'?"checked":""} ${SESSION_ATTRIBUTE_USER_BUTTON['saveInfoDesenConf']==null?"disabled":""}/>输入集</label>
	    </td>
	  </tr>
  </c:forEach>
  <c:if test="${pageObject == null }">
  	  <tr>
	    <td colspan=10>请查询后再配置模型服务信息脱敏</td>
	  </tr>
  </c:if>
  <c:if test="${pageObject != null && (pageObject.data == null || empty pageObject.data)}">
  	  <tr>
	    <td colspan=10>该用户所在机构还没有分配过服务列表</td>
	  </tr>
  </c:if>
</table>
</div>
<g:page pageObject="${pageObject }" />

<c:if test="${pageObject != null && pageObject.data != null && not empty pageObject.data}">
<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['saveInfoDesenConf']!=null}">
<div class="page_wrap clearfix">
	<div style="text-align:center">
		<input name="updateDesenBtning" id="updateDesenBtning" type="button" value="保存中" style="color:gray;display:none" disable/>
		<input name="updateDesenBtn" id="updateDesenBtn" type="button" value="保存" onclick="updateDesenServiceInfoList()"/>
	</div>
</div>
</c:if>
</c:if>
</div>
</form>
</body>
</html>
