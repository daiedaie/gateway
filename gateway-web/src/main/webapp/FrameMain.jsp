<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include.jsp"%>
<jsp:directive.page import="java.util.*"/>
<jsp:directive.page import="com.gztydic.gateway.core.vo.GwWorkPlanVO"/>
<jsp:directive.page import="com.gztydic.gateway.core.common.constant.WorkPlanConstent"/>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
ArrayList workList=(ArrayList)request.getAttribute("workList");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath %>"/>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">
	
	//待办信息处理祥细页面跳转
    function openWorkPlanWindow(planId,planType,extenTableKey){
        var url = null;
        if(<%=WorkPlanConstent.REGISTE_AUDIT%>==planType){//注册审核
            if(extenTableKey != null){
              location.href="system/workPlan_signUpVerify.do?workPlanId="+planId+"&userId="+extenTableKey;
            }
        }else if(<%=WorkPlanConstent.CANCEL_AUDIT%>==planType){//注销审核
			if(extenTableKey != null){
     	      location.href = "system/workPlan_searchCancelUserVerify.do?planId="+planId+"&userId="+extenTableKey;
     	    }
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
              location.href="system/user_userAuthPage.do?workPlanId="+planId+"&user.userId="+extenTableKey+"&source=2";
            }
        }else if(<%=WorkPlanConstent.INFO_DESEN_CONF%>==planType){//服务信息脱敏配置
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
	              location.href="system/desenModel_desenRuleServiceField.do?source=1&workPlanId="+planId+"&userId="+userId+"&serviceId="+serviceId+"&fetchId="+fetchId;
	
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
        }else if(<%=WorkPlanConstent.INFO_DESEN_CONF_BACK%>==planType){
        	location.href="system/workPlan_searchServiceInfoBack.do?planId="+planId;
        }else if(<%=WorkPlanConstent.INFO_DESEN_CONF_AUDIT%>==planType){
        	location.href="system/workPlan_searchServiceInfoVerify.do?planId="+planId;
        }else if(<%=WorkPlanConstent.INFO_DESEN_CONF_AUDIT_BACK%>==planType){
        	location.href="system/workPlan_searchServiceInfoBack.do?planId="+planId;
        }else if(<%=WorkPlanConstent.RULE_CHECK_AUDIT_1%>==planType){
        	location.href="system/workPlan_searchRuleCheckAudit.do?planId="+planId;
        }else if(<%=WorkPlanConstent.RULE_CHECK_AUDIT_2%>==planType){
        	location.href="system/workPlan_searchRuleCheckAudit.do?planId="+planId;
        }else if(<%=WorkPlanConstent.RULE_CHECK_AUDIT_BACK%>==planType){
        	location.href="system/workPlan_searchRuleCheckAuditBack.do?planId="+planId;
        }else if(<%=WorkPlanConstent.DATA_REPUSH_SUCCESS%>==planType){
        	location.href="system/workPlan_ruleCheckResend.do?planId="+planId;
        }else if(<%=WorkPlanConstent.FTP_VUSER_COMMAND_EXE%>==planType){
        	location.href="system/workPlan_toFtpUserNotice.do?planId="+planId;
        }else if(<%=WorkPlanConstent.DATA_REPUSH_FAILURE%>==planType){
        	location.href="system/workPlan_ruleCheckResendFailure.do?planId="+planId;
        }else if(<%=WorkPlanConstent.DATA_FIRST_PUSH_FAILURE%>==planType){
        	location.href="system/workPlan_toPushResult.do?planId="+planId;
        }else if(<%=WorkPlanConstent.DATA_CLEAN%>==planType){
        	location.href="system/workPlan_toDataClean.do?planId="+planId;
        }else if(<%=WorkPlanConstent.DATA_REPUSH_SUCCESS_FOR_DATA_USER%>==planType){
        	location.href="system/workPlan_ruleCheckResend.do?planId="+planId;
        }else if(<%=WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR%>==planType){
        	location.href="system/workPlan_toSystemBackStageError.do?planId="+planId;
        }else if(<%=WorkPlanConstent.SERVICE_FETCH_AUDIT%>==planType){//数据安全管理员服务创建并申请审批
            if(extenTableKey != null){
	        	var values = extenTableKey.split(",");
		            if(values.length==3){
		              var userId = values[0];
		              var serviceId = values[1];
		              var fetchId = values[2];
		            }
            }
            location.href="system/workPlan_serviceFetchAudit.do?workPlanId="+planId+"&serviceId="+serviceId+"&fetchId="+fetchId;	
        }else if(<%=WorkPlanConstent.SERVICE_FETCH_AUDIT2%>==planType){//审核人员服务创建并申请审批
            location.href="<%=basePath %>service/modelDataApp_serviceFetchAudit.do?workPlanId="+planId;	
        }else if(<%=WorkPlanConstent.SERVICE_FETCH_AUDIT_BACK%>==planType){//服务创建并申请审批退回修改
            if(extenTableKey != null){
	        	var values = extenTableKey.split(",");
		            if(values.length==3){
		              var userId = values[0];
		              var serviceId = values[1];
		              var fetchId = values[2];
		            }
            }
            location.href="system/workPlan_serviceFetchAudit.do?workPlanId="+planId+"&serviceId="+serviceId+"&fetchId="+fetchId;
        }else if(<%=WorkPlanConstent.SERVICE_FETCH_CONFIRM%>==planType){//创建实时取数任务确认
            location.href="system/workPlan_newServiceTaskConfirm.do?planId="+planId;
        }else if(<%=WorkPlanConstent.FETCH_APPLY_SUCCESS%>==planType){//取数申请成功并发G鉴权号给数据用户
            location.href="system/workPlan_searchFetchApplySuccess.do?planId="+planId;
        }else if(<%=WorkPlanConstent.WEBSERVICE_RULE_CHECK_FAILURE%>==planType){//webservice合规检查失败
            location.href="system/workPlan_searchWebserviceRuleCheck.do?planId="+planId;
        }
   }
   
   function forwardNotice(noticeId){
   		location.href="system/notice_searchNotice.do?notice.noticeId="+noticeId;
   }
   
   function forwardDoc(docId){
   		location.href="system/doc_searchDoc.do?doc.docId="+docId;
   }
</script>
<title>首页</title>
</head>
<body class="FrameMain">
<table width="100%" cellspacing="0">
    <tr>
      <td width="90%"  valign="top">
	  <div class="left-panel">
	      <div class="tit-red"><span class="tit-pre">待办信息</span><span class="tit-mid"></span><span class="tit-more"><a href="system/workPlan_searchByParam.do" target="mainFrame">更多</a></span></div>
	      <div class="daiban">
	        <ul>
	             <%
	             int num = workList.size();
	             if(num>5){
	                num = 5;
	             }
	             int row = 0;
	            for(int i=0;i<num;i++){  
	          		 GwWorkPlanVO workVo=(GwWorkPlanVO)workList.get(i);
	          		 row = i+1;
	          		 String content = "",title = "";
	          		 %>
	          		 <li>
	          		    <%if(workVo.getPlanContent() != null){ %>
	        		      <% title = workVo.getPlanContent(); %>
						      <% content = row + "、"+workVo.getPlanContent(); %>
	          		   <%}else{ %>
	          		   			<% content = row + "、...";%>
						<%} %>
	          		<a href="javascript:openWorkPlanWindow('<%=workVo.getPlanId() %>','<%=workVo.getPlanType() %>','<%=workVo.getExtenTableKey() %>')" title=<%=title %>><%=content %></a></li>
	          		 <%
	          	  }
	          	  
	          	  if(num==0){
	            %>
	            	<li>暂无待办任务</li>
	            <%} %>
	          </ul>
	      </div>
     </div>
	 </td>
	 <td width="70%">
	 </td> 
   </tr>
   <tr>
      <td width="90%"  valign="top">
	  <div class="left-panel">
	      <div class="tit-red"><span class="tit-pre">公告信息</span><span class="tit-mid"></span><span class="tit-more"><a href="system/notice_searchNoticeList.do" target="mainFrame">更多</a></span></div>
	      <div class="daiban">
	      	  <ul>
	      	  	 <c:choose>
	      	  	 	<c:when test="${not empty noticeList}">
	      	  	 		<c:forEach var="notice" items="${noticeList}" varStatus="vs">
	      	  	 			<li>
	      	  	 				<%-- <c:choose>
		      	  	 				<c:when test="${fn:length(notice.noticeContent) > 75}">
		      	  	 					<a href="javascript:forwardNotice('${notice.noticeId}')" title="${notice.noticeContent}">${vs.count}、${fn:substring(notice.noticeContent,0,75)}...</a>
		      	  	 				</c:when>
		      	  	 				<c:otherwise>
		      	  	 					<a href="javascript:forwardNotice('${notice.noticeId}')" title="${notice.noticeContent}">${vs.count}、${notice.noticeContent}</a>
		      	  	 				</c:otherwise>
	      	  	 				</c:choose> --%>
	      	  	 				<a href="javascript:forwardNotice('${notice.noticeId}')" title="${notice.noticeTitle}">${vs.count}、${notice.noticeTitle}</a>
		      	  	 		</li>
	      	  	 		</c:forEach>
	      	  	 	</c:when>
	      	  	 	<c:otherwise>
	      	  	 		<li>暂无公告</li>
	      	  	 	</c:otherwise>
	      	  	 </c:choose>
	          </ul>
	      </div>
     </div>
	 </td>
	 <td width="70%">
	 </td> 
   </tr>
   
   <tr>
      <td width="90%"  valign="top">
	  <div class="left-panel">
	      <div class="tit-red"><span class="tit-pre">帮助信息</span><span class="tit-mid"></span><span class="tit-more"><a href="system/doc_searchDocList.do" target="mainFrame">更多</a></span></div>
	      <div class="daiban">
	      	  <ul>
	      	  	 <c:choose>
	      	  	 	<c:when test="${not empty docList}">
	      	  	 		<c:forEach var="doc" items="${docList}" varStatus="vs">
	      	  	 			<li>
<!-- 	      	  	 				<a href="javascript:forwardDoc('${doc.docId}')" title="${doc.fileVO.realName}">${vs.count}、${doc.fileVO.realName}</a> -->
								<a href="system/file_downLoadFile.do?fileId=${doc.fileVO.fileId}" target="_blank" style="color:blue">${doc.fileVO.realName}</a>
		      	  	 		</li>
	      	  	 		</c:forEach>
	      	  	 	</c:when>
	      	  	 	<c:otherwise>
	      	  	 		<li>暂无帮助信息</li>
	      	  	 	</c:otherwise>
	      	  	 </c:choose>
	          </ul>
	      </div>
     </div>
	 </td>
	 <td width="70%">
	 </td> 
   </tr>
</table>
</body>
</html>
