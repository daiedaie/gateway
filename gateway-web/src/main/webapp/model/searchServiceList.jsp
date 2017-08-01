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
<title>模型服务信息查看列表_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script>
function showBg(ct,content){
    var bH=document.documentElement.clientHeight;
    var bW=$("body").width()+16;
    var objWH=getObjWh(ct);
    var tbT=objWH.split("|")[0]+"px";
    var tbL=objWH.split("|")[1]+"px";
    $("#"+ct).css({top:tbT,left:tbL,display:"block"});
}

function getObjWh(obj){
    var st=document.documentElement.scrollTop;
    var sl=document.documentElement.scrollLeft;
    var ch=document.documentElement.clientHeight;
    var cw=document.documentElement.clientWidth;
    var objH=$("#"+obj).height();
    var objW=$("#"+obj).width();
    var objT=Number(st)+(Number(ch)-Number(objH))/2+100;
    var objL=Number(sl)+(Number(cw)-Number(objW))/2+100;
    return objT+"|"+objL;
}

function closeBg(){
    $("#dialog").css("display","none");
}

function searchService(){
	showBg('dialog','dialog_content');
}

function editServiceInfo(serviceId,fetchCount){
	if(fetchCount>0 && !confirm("该服务正在被用户使用，确认修改?")){
		return;
	}
	location.href="service/serviceInfo_editServiceInfo.do?serviceVO.serviceId="+serviceId;
}

function deleteServiceInfo(serviceId){
	if(confirm("确定删除该服务")){
		var url = "service/serviceInfo_deleteServiceInfo.do?serviceVO.serviceId="+serviceId;
		util.ajax(url,null,function(json){
			if(json.state=='success'){
				searchPage($("#curPage").val());
			}else{
				alert(json.message);
			}
		});
	}
}
    
</script>
</script>
</head>	
<body class="FrameMain">
<form id="searchForm" action="service/serviceInfo_searchServiceList.do" method="post" loadContainer="pageDataList">
<input type="hidden" name="loginName" value="${loginName }"/>
<div class="main_title">
	<b>模型服务查看</b>
</div>
<div class="main_search">
	<p>
	服务编码：<input type="text" name="serviceView.serviceCode" id="serviceCode" value=""/>
	服务名称：<input type="text" name="serviceView.serviceName" id="serviceName" value=""/>
	模型名称：<input type="text" name="serviceView.modelName" id="modelName" value=""/>
	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['findModelInfo']!=null}">
		<input name="searchBtn" id="searchBtn" type="button" value="查询" onclick="searchPage()"/>
	</c:if>
	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['addService']!=null}">
		<input name="searchBtn" id="searchBtn" type="button" value="新增" onclick="window.location.href='<%=basePath %>model/addServiceInfo.jsp';"/>
	</c:if>
	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['addServiceAndFetch']!=null}">
		<input name="searchBtn" id="searchBtn" type="button" value="新增并申请取数" onclick="window.location.href='system/desenModel_toAddServiceAndFetch.do';"/>
	</c:if>
	</p>
</div>
<div id="dialog">  
  <div id="dialog_content">
    <img src="resource/images/load.gif"/>
  </div>  
</div>
<div id="pageDataList">
<div class="main_list">
<table width="100%" border="0">
  <tr>
    <th>序号</th>
    <th><a class="tableSort" sort="service_code">服务编码</a></th>
    <th><a class="tableSort" sort="service_name">服务名称</a></th>
    <th><a class="tableSort" sort="model_name">模型名称</a></th>
    <th><a class="tableSort" sort="service_type">服务类型</a></th>
    <th>查看</th>
  </tr>
  <c:forEach var="serviceView" items="${pageObject.data }" varStatus="vs">
  	  <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
	    <td>${(pageObject.curPage-1)*pageObject.pageSize+vs.count }</td>
	    <td>${serviceView.serviceCode }</td>
	    <td>${serviceView.serviceName }</td>
	    <td>${serviceView.modelName }</td>
	    <td><g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${serviceView.serviceType}"/></td>
	    <td>
	    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['detailModelInfo']!=null}">
	    		<a href="service/serviceInfo_searchService.do?serviceView.serviceId=${serviceView.serviceId}" target="_blank">查看</a>
	    	</c:if>
	    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['editService']!=null && serviceView.serviceSource=='2'}">
	    		<a href="javascript:void()" onclick="editServiceInfo('${serviceView.serviceId}','${serviceView.fetchCount }')">修改</a>
	    	</c:if>
	    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['deleteService']!=null && serviceView.serviceSource=='2' && serviceView.fetchCount=='0'}">
	    		<a href="javascript:void()" onclick="deleteServiceInfo(${serviceView.serviceId})">删除</a>
	    	</c:if>
	    </td>
	  </tr>
  </c:forEach>
  <c:if test="${pageObject.data == null || empty pageObject.data}">
  	  <tr>
	    <td colspan=6>查询不到服务信息</td>
	  </tr>
  </c:if>
</table>
</div>
<g:page pageObject="${pageObject }" />
</div>
</form>
</body>
</html>
