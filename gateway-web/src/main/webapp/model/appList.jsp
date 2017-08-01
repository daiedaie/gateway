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
<link rel="stylesheet" href="resource/css/ui-dialog.css" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/artDialog/dialog-min.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript">

$(document).ready(function(){
	$('#divPageLoading').remove();
    $('#divPageBody').show();
    return;
});

function appService(serviceId,userId){
	/*    url = "service/modelDataApp_modelServiceAppInput.do?serviceId="+serviceId+"&userId="+userId;
	   $.post(url,null,function(json){
			alert(json.message);
			if(json.state=='success'){
				searchPage($("#curPage").val());
			}
		}); */
	window.location.href= "service/modelDataApp_applyFetch.do?serviceId="+serviceId+"&userId="+userId;
}
	
function showBg(ct,content){
    var bH=document.documentElement.clientHeight;
    var bW=$("body").width()+16;
    var objWH=getObjWh(ct);
    $("#fullbg").css({width:bW,height:bH,display:"block"});
    var tbT=objWH.split("|")[0]+"px";
    var tbL=objWH.split("|")[1]+"px";
    $("#"+ct).css({top:tbT,left:tbL,display:"block"});
    if ($("#fullbg") != null) {
        $(window).scroll(function(){resetBg()});
        $(window).resize(function(){resetBg()});
    }
}

function getObjWh(obj){
    var st=document.documentElement.scrollTop;
    var sl=document.documentElement.scrollLeft;
    var ch=document.documentElement.clientHeight;
    var cw=document.documentElement.clientWidth;
    var objH=$("#"+obj).height();
    var objW=$("#"+obj).width();
    var objT=Number(st)+(Number(ch)-Number(objH))/2;
    var objL=Number(sl)+(Number(cw)-Number(objW))/2;
    return objT+"|"+objL;
}

function resetBg(){	
	  if ($("#fullbg") != null) {
        var fullbg=$("#fullbg").css("display");    
        if(fullbg=="block"){
            var bH2=$("body").height();
            var bW2=$("body").width()+16;
            $("#fullbg").css({width:bW2,height:bH2});
            var objV=getObjWh("dialog");
            var tbT=objV.split("|")[0]+"px";
            var tbL=objV.split("|")[1]+"px";
            $("#dialog").css({top:tbT,left:tbL});
        }
  }
}

function closeBg(){
    $("#fullbg").css("display","none");
    $("#dialog").css("display","none");
}
	
function getDataOnline(serviceId,userId){
	var sendValue = "";    
	
	sendValue = "serviceId="+serviceId+"&userId="+userId;
	    
	// Show the loading
	showBg('dialog','dialog_content');
	$.ajax({
	    type:"POST",
	    url:"service/modelTask_onlineGetServiceData.do",
	    data:sendValue,				
	    success:function(msg){
	        closeBg();
	        $('body').html(msg);
	    },
	    error:function(){
	    	closeBg();
	    	alert("系统异常，取数失败");
	    }
	});
    
}

function onlineCheck(serviceId,userId,fetchId,auditTime){
	
	var d = dialog({
			    title: '提示',
			    content: '请以;分隔填入需要实时检查的gz文件（命名规则："服务编码"-"服务周期类型编码"-"周期编码".txt.gz）</br></br><input id="fileList" value="" style="width:800px" /></br></br>',
			    ok: function () {
			        var fileList = $.trim($('#fileList').val());
			        var fileArray=fileList.split(";");
			        var flag=true;
			        for(var i=0;i<fileArray.length;i++){
						if (!/\.txt\.gz$/.test(fileArray[i])) {
							alert("实时检查的gz文件类型必须是.txt.gz！");
							flag=false;
							return;
						}
					}
			        this.close();
			        this.remove();
			        if(flag){
						$.ajax({
							type: "POST",
							url:"service/modelDataApp_onlineCheck.do" ,
							data:"userId="+userId+"&serviceId="+serviceId+"&fetchId="+fetchId+"&auditTime="+auditTime+"&fileList="+fileList ,
							success: function(data){
								alert(data.message);
								window.location.href='service/modelDataApp_getServiceAppList.do';
							}
				   		});
					}
			        
			    }
			});
		d.show();
	}
</script>
<title>用户模型取数申请记录</title>
</head>

<body class="FrameMain">
<div id="fullbg"></div>  
<div id="dialog">  
  <div id="dialog_content">
    <img src="resource/images/loading.jpg"/>
    <br/>
    <div align='center'>Loading...</div>
  </div>  
  <div style="text-align:center;">
    <a href="#"onclick="closeBg();">Close</a>
  </div>  
</div>
<div id="divPageBody">
<form id="searchForm" action="service/modelDataApp_getServiceAppList.do" method="post" loadContainer="pageDataList">
<div class="main_title">
	<b>模型服务取数申请记录</b>
</div>


<div class="main_search">
	<p>
	姓名：${gwUser.userName }
	&nbsp;&nbsp;&nbsp;&nbsp;
	用户类型：<g:sysDict dictCode="DICT_USER_TYPE" dictKey="${gwUser.userType}"/>
	</p>
</div>
<div class="main_search">
	<p>
	<c:if test="${gwUser.userType!='dataUser' }">
		登录账号：<input type="text" name="userModelServiceAppVO.loginName" value="${userModelServiceAppVO.loginName }"/>
	</c:if>
	服务编码：<input type="text" name="userModelServiceAppVO.serviceCode" value=""/>
	服务名称：<input type="text" name="userModelServiceAppVO.serviceName" value=""/>
	模型编码：<input type="text" name="userModelServiceAppVO.modelCode" value=""/>
	审批状态：<select name="userModelServiceAppVO.auditStatus">
				<option value="">-请选择-</option>
				<option value="0">不通过</option>
				<option value="1">待审核</option>
				<option value="2">通过</option>
			 </select>
	<input name="searchBtn" id="searchBtn" type="button" value="查询" onclick="searchPage()"/>
	</p>
</div>

<div id="pageDataList">
<div class="main_list">
<table width="100%" border="0">
  <tr>
    <th>序号</th>
    <c:if test="${gwUser.userType!='dataUser'}">
    	<th><a class="tableSort" sort="login_name">登录账号</a></th>
    </c:if>
    <th><a class="tableSort" sort="service_code">服务编码</a></th>
    <th><a class="tableSort" sort="service_name">服务名称</a></th>
    <th><a class="tableSort" sort="model_code">模型编码</a></th>
    <th><a class="tableSort" sort="service_type">服务类型</a></th>
    <th><a class="tableSort" sort="cycle_type">服务周期</a></th>
    <th><a class="tableSort" sort="audit_status">审批状态</a></th>
    <th><a class="tableSort" sort="audit_time">审批时间</a></th>
    <th>操作</th>
  </tr>
  <c:forEach var="list" items="${pageObject.data}" varStatus="vs">
  	  <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
	<td>${(pageObject.curPage-1)*pageObject.pageSize+vs.count }</td>
	<c:if test="${gwUser.userType!='dataUser'}">
    	<td>${ list.loginName }</td>
    </c:if>
    <td>${ list.serviceCode }</td>
    <td>${ list.serviceName }</td>
    <td>${ list.modelCode }</td>
    <td><g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${list.serviceType}"/></td>
    <td>
    	<c:if test="${list.serviceType=='1'}">
	    	每<g:sysDict dictCode="DICT_CYCLE_TYPE" dictKey="${list.cycleType}"/>
	    	<c:choose>
	    		<c:when test="${list.cycleType=='1'}">第${list.cycleDay}天</c:when>
	    		<c:when test="${list.cycleType=='2'}">第${list.cycleDay}天</c:when>
	    		<c:when test="${list.cycleType=='3'}">${list.cycleDay}日</c:when>
	    		<c:when test="${list.cycleType=='4'}">${list.cycleDay}</c:when>
	    		<c:when test="${list.cycleType=='5'}"></c:when>
	    	</c:choose>
    	</c:if>
    </td>
    <td><g:sysDict dictCode="DICT_AUDIT_STATE" dictKey="${list.auditStatus}"/></td>
    <td><fmt:formatDate value="${list.auditTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
    <td>
    <c:choose> 
		<c:when test="${empty list.auditStatus}">
			<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['appData']!=null}">
				<a href="javascript:void(0)" onclick="appService('${list.serviceId}','${list.userId}')">申请</a>
			</c:if>	
		</c:when>
		<c:when test="${list.auditStatus==1}"> 
			
		</c:when>
		<c:when test="${list.auditStatus==2}">
			<c:if test="${list.serviceType == 0}">
				<c:if test="${list.userId == gwUser.userId}">
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['fetchData']!=null}">
						<c:if test="${list.serviceSource ==1 }">
							<a href="javascript:void(0)" onclick="getDataOnline('${list.serviceId}','${list.userId}')" >取数</a>
						</c:if>
					</c:if>
				</c:if>
			</c:if>
			
			<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['onlineCheck']!=null}">
				<c:if test="${ list.serviceSource=='2' }">
					<a href="javascript:void(0)" onclick="onlineCheck('${list.serviceId}','${list.userId}','${list.fetchId}','<fmt:formatDate value="${list.auditTime }" pattern="yyyy-MM-dd HH:mm:ss"/>')" >实时检查</a>
				</c:if>
			</c:if>
			<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['detailTask']!=null}">
				<a href="javascript:void(0)" onclick="location.href='service/modelTask_searchTaskList.do?serviceCode=${ list.serviceCode }&loginName=${ list.loginName }'">查看任务</a>
			</c:if>
			<c:if test="${gwUser.userType=='dataUser' }">
				<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['checkServiceRule']!=null}">
					<a href="javascript:void(0)" onclick="location.href='service/modelDataApp_verifyAppRule.do?serviceId=${ list.serviceId }&userId=${ list.userId }&serviceCode=${ list.serviceCode }&loginName=${ list.loginName }&serviceName=${ list.serviceName }'" >查看规则</a>
				</c:if>
			</c:if>
		</c:when>
		<c:when test="${list.auditStatus==3}"> 
		
		</c:when>
		<c:otherwise> 
			<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['appData']!=null}">
				<a href="javascript:void(0)" onclick="appService('${list.serviceId}','${list.userId}')">重新申请</a>
			</c:if>
		</c:otherwise>
	</c:choose> 
    </td>
  </tr>
   </c:forEach>
   <c:if test="${pageObject.data == null || empty pageObject.data}">
  	  <tr>
	  	<td colspan=10>查询不到该用户的模型服务申请信息数据</td>
	  </tr>
  </c:if>
  
</table>
</div>
<g:page pageObject="${pageObject }" />
</div>
</form>
</div>
</body>
</html>
