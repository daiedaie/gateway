<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include.jsp"%>
<jsp:directive.page import="com.gztydic.gateway.core.common.constant.WorkPlanConstent"/>
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
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript">
//待办信息处理祥细页面跳转
    function openWorkPlanWindow(planId,planType,extenTableKey){
        var url = null;
        if(<%=WorkPlanConstent.REGISTE_AUDIT%>==planType){//注册审核
            if(extenTableKey != null){
              location.href="system/workPlan_signUpVerify.do?workPlanId="+planId+"&userId="+extenTableKey;
            }
        }else if(<%=WorkPlanConstent.CANCEL_AUDIT%>==planType){//注销审核
     	    location.href = "system/workPlan_searchCancelUserVerify.do?planId="+planId+"&userId="+extenTableKey;
        }else if(<%=WorkPlanConstent.CANCEL_BACKT%>==planType){//注销退回
     	    location.href = "system/workPlan_searchUserCancelBack.do?planId="+planId+"&prePlanId="+extenTableKey;
        }else if(<%=WorkPlanConstent.UPDATE_AUDIT%>==planType){//修改审核
        		  location.href="system/workPlan_searchUpdateUserVerify.do?planId="+planId;
        }else if(<%=WorkPlanConstent.UPDATE_BACK%>==planType){//修改退回
         var values = extenTableKey.split(",");
	            if(values.length==2){
	              var oldPlanId = values[0];
	              var batchId = values[1];
        		  location.href="system/workPlan_searchUserBack.do?planId="+planId+"&oldPlanId="+oldPlanId+"&batchId="+batchId;
        		  }
        }else if(<%=WorkPlanConstent.PERMISS_ALLOT%>==planType){//权限分配
            if(extenTableKey != null){
              location.href="system/user_userAuthPage.do?workPlanId="+planId+"&user.userId="+extenTableKey;
            }
        }else if(<%=WorkPlanConstent.INFO_DESEN_CONF%>==planType){//模型信息配置
            /* alert("=====建设中====");
        	url = "web/workmanage/workorder_getWorkOrder.do?workPlanId="+planId+"&isVerify=true&seProjectId="+projectId;
    		return url; */
    		location.href="system/workPlan_searchServiceInfoVerify.do?planId="+planId;
        }else if(<%=WorkPlanConstent.FIELD_DESEN_CONF%>==planType){//服务字段脱敏规则配置
            if(extenTableKey != null){
	            var values = extenTableKey.split(",");
	            if(values.length==3){
	              var userId = values[0];
	              var serviceId = values[1];
	              var fetchId = values[2];
	              location.href="system/desenModel_desenRuleServiceField.do?source=2&workPlanId="+planId+"&userId="+userId+"&serviceId="+serviceId+"&fetchId="+fetchId;	
	            }
            }
        }else if(<%=WorkPlanConstent.FIELD_DESEN_CONF_AUDIT%>==planType){//字段脱敏配置后申请审核
            location.href="<%=basePath %>service/modelDataApp_searchVerify.do?workPlanId="+planId;
        }else if(<%=WorkPlanConstent.GET_DATA_BACK%>==planType){//取数申请退回
            location.href="system/workPlan_searchDataAppBack.do?planId="+planId;
        }else if(<%=WorkPlanConstent.FIELD_DESEN_CONF_AUDIT_BACK%>==planType){//服务字段脱敏配置审核退回
            if(extenTableKey != null){
	            var values = extenTableKey.split(",");
	            if(values.length==3){
	              var userId = values[0];
	              var serviceId = values[1];
	              var fetchId = values[2];
	              if(fetchId=='null') fetchId = '';
	              location.href="system/desenModel_desenRuleServiceField.do?workPlanId="+planId+"&userId="+userId+"&serviceId="+serviceId+"&fetchId="+fetchId;
	
	            }
            }
        }else if(<%=WorkPlanConstent.REGISTE_BACK%>==planType){//注册退回
            if(extenTableKey != null){
	            var values = extenTableKey.split(",");
	            if(values.length==2){
	              var userId = values[0];
	              var oldPlanId = values[1];
	              location.href="system/workPlan_signUpBack.do?workPlanId="+planId+"&userId="+userId+"&oldPlanId="+oldPlanId;
	
	            }
            }
        }else if(<%=WorkPlanConstent.INFO_DESEN_CONF_BACK%>==planType){//服务信息查看申请退回
        	location.href="system/workPlan_searchServiceInfoBack.do?planId="+planId;
        }
        else if(<%=WorkPlanConstent.INFO_DESEN_CONF_AUDIT%>==planType){//服务信息脱敏配置审核
        	location.href="system/workPlan_searchServiceInfoVerify.do?planId="+planId;
        }else if(<%=WorkPlanConstent.INFO_DESEN_CONF_AUDIT_BACK%>==planType){//服务信息脱敏配置审核退回	数据用户申请后由审核人员审核
        	location.href="system/workPlan_searchServiceInfoBack.do?planId="+planId;
        }else if(<%=WorkPlanConstent.RULE_CHECK_AUDIT_1%>==planType){//数据安全管理员做合规检查审核
        	location.href="system/workPlan_searchRuleCheckAudit.do?planId="+planId;
        }else if(<%=WorkPlanConstent.RULE_CHECK_AUDIT_2%>==planType){//审核人员做合规检查审核
        	location.href="system/workPlan_searchRuleCheckAudit.do?planId="+planId;
        }else if(<%=WorkPlanConstent.RULE_CHECK_AUDIT_BACK%>==planType){//合规检查审核退回
        	location.href="system/workPlan_searchRuleCheckAuditBack.do?planId="+planId;
        }else if(<%=WorkPlanConstent.DATA_REPUSH_SUCCESS%>==planType){//合规数据重新推送成功通知
        	location.href="system/workPlan_ruleCheckResend.do?planId="+planId;
        }else if(<%=WorkPlanConstent.FTP_VUSER_COMMAND_EXE%>==planType){//ftp虚拟用户配置命令执行通知
        	location.href="system/workPlan_toFtpUserNotice.do?planId="+planId;
        }else if(<%=WorkPlanConstent.DATA_REPUSH_FAILURE%>==planType){//合规数据重新推送失败通知
        	location.href="system/workPlan_ruleCheckResendFailure.do?planId="+planId;
        }else if(<%=WorkPlanConstent.DATA_FIRST_PUSH_FAILURE%>==planType){//合规数据首次推送失败通知
        	location.href="system/workPlan_toPushResult.do?planId="+planId;
        }else if(<%=WorkPlanConstent.DATA_CLEAN%>==planType){//过期数据清理
        	location.href="system/workPlan_toDataClean.do?planId="+planId;
        }else if(<%=WorkPlanConstent.DATA_REPUSH_SUCCESS_FOR_DATA_USER%>==planType){//合规数据重新推送成功通知
        	location.href="system/workPlan_ruleCheckResend.do?planId="+planId;
        }else if(<%=WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR%>==planType){//系统后台错误
        	location.href="system/workPlan_toSystemBackStageError.do?planId="+planId;
        }else if(<%=WorkPlanConstent.SERVICE_FETCH_AUDIT%>==planType){//服务创建并申请审批
        	var values = extenTableKey.split(",");
            if(values.length==3){
              var userId = values[0];
              var serviceId = values[1];
              var fetchId = values[2];
            }
            location.href="system/workPlan_serviceFetchAudit.do?workPlanId="+planId+"&serviceId="+serviceId+"&fetchId="+fetchId;	
        }else if(<%=WorkPlanConstent.SERVICE_FETCH_AUDIT2%>==planType){//服务创建并申请审批
            location.href="<%=basePath %>service/modelDataApp_serviceFetchAudit.do?workPlanId="+planId;	
        }else if(<%=WorkPlanConstent.SERVICE_FETCH_CONFIRM%>==planType){//创建实时取数任务确认
            location.href="system/workPlan_newServiceTaskConfirm.do?planId="+planId;
        }else if(<%=WorkPlanConstent.FETCH_APPLY_SUCCESS%>==planType){//取数申请成功并发G鉴权号给数据用户
            location.href="system/workPlan_searchFetchApplySuccess.do?planId="+planId;
        }else if(<%=WorkPlanConstent.WEBSERVICE_RULE_CHECK_FAILURE%>==planType){//webservice合规检查失败
            location.href="system/workPlan_searchWebserviceRuleCheck.do?planId="+planId;
        }
   }	
</script>
<title>待办列表</title>
</head>
  <body class="FrameMain">
  <form id="searchForm" action="system/workPlan_searchByParam.do" method="post" loadContainer="pageDataList">
	<div class="main_title">
		<b>待办列表查看</b>
	</div>
	
	<div class="main_search">
		<p>
			待办类型：<g:sysDictList dictCode="DICT_PLAN_TYPE" defaultValue="${planType}" tagType="select" tagName="planType" tagId="planType"/>
			&nbsp;&nbsp;
    		待办标题：<input type="text" id="planTitle" name="planTitle" value="${planTitle}"/>
    		&nbsp;&nbsp;
			待办内容：<input type="text" id="planContent" name="planContent" value="${planContent}"/>
	 		 &nbsp;&nbsp;
			<input name="" type="button" value="查询" onclick="searchPage()"/>
		</p>
	</div>
	<div id="pageDataList">
	<div class="main_list">
	<table width="100%" border="0">
	  <tr>
	    <th>序号</th>
	    <th>待办类型</th>
	    <th>待办标题</th>
	    <th>待办内容</th>
	    <th>创建时间</th>
	    <th>待办状态</th>
	    <th>操作</th>
	  </tr>
	  
	  <c:forEach var="m" items="${pageObject.data }" varStatus="vs">
  	     <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
	        <td>${(pageObject.curPage-1)*pageObject.pageSize+vs.count }</td>
		    <td><g:sysDict dictCode="DICT_PLAN_TYPE" dictKey="${m.planType}"/></td>
		    <td>${m.planTitle}</td>
		    <td>${m.planContent}</td>
		    <td>${m.createTime}</td>
		    <td><g:sysDict dictCode="DICT_PLAN_STATE" dictKey="${m.planState}"/></td>
		    <td>
		    <a href="javascript:openWorkPlanWindow('${m.planId}','${m.planType}','${m.extenTableKey}')">处理</a>
		    </td>
		 </tr>
	  </c:forEach>
	  <c:if test="${pageObject.data == null || empty pageObject.data}">
	  	  <tr>
		    <td colspan=7>查询不到待办任务数据</td>
		  </tr>
	  </c:if>
	 </table>
	 </div>
	 <g:page pageObject="${pageObject }" />
	 </div>
  
  </form>
  </body>
</html>
