<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:directive.page import="com.gztydic.gateway.core.common.constant.GwUserType"/>
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
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript">
	function passOrNoPass(planId,passTag,saveBtn) {
		if(passTag!=""){
			$("#"+saveBtn+",#saveBtning").toggle();
			//数据安全管理员审核合规检查
			if($("#loginUserType").val()=="<%=GwUserType.SAFE_USER%>"){
				if(passTag==2?confirm("确认通过合规检查?"):confirm("确定不通过合规检查？")){
			 		$("#"+saveBtn+",#saveBtning").toggle();
			 		$.ajax({
						type : "POST",
						url : "system/workPlan_verifyRuleCheckAudit.do",
						data : "planId=" + planId + "&passTag=" + passTag +"&suggestion=" +$("#suggestion").val(),
						success : function(json) {
							if(json.state=='success'){
								window.location.href = 'system/workPlan_searchByParam.do';
							}else{
								alert(json.message);					
							}
							$("#"+saveBtn+",#saveBtning").toggle();
						},error:function(){
							alert("系统异常，请联系管理员");
						}
					});
		 		}
		 	}else{//审核人员合规检查审核
		 	    if(passTag==2){
		 	    	if(confirm("将要推送合规数据到用户108的FTP-${dataUserVO.ftpIp}地址，如果选择取消，则表示不通过。")){
		 	    		$.ajax({
							type : "POST",
							url : "system/workPlan_verifyRuleCheckAudit.do",
							data : "planId=" + planId + "&passTag=" + passTag +"&suggestion=" +$("#suggestion").val(),
							success : function(json) {
							if(json.state=='success'){
								alert(json.message);
								window.location.href = 'system/workPlan_searchByParam.do';
							}else{
								alert(json.message);					
							}
							$("#"+saveBtn+",#saveBtning").toggle();
							},error:function(){
								alert("系统异常，请联系管理员");
							}
						});
		 	    	}else{
		 	    		$("#suggestion").val("不同意");
		 	    		passTag = 0;
		 	    		$.ajax({
							type : "POST",
							url : "system/workPlan_verifyRuleCheckAudit.do",
							data : "planId=" + planId + "&passTag=" + passTag +"&suggestion=" +$("#suggestion").val(),
							success : function(json) {
								if(json.state=='success'){
									window.location.href = 'system/workPlan_searchByParam.do';
								}else{
									alert(json.message);					
								}
								$("#"+saveBtn+",#saveBtning").toggle();
							},error:function(){
								alert("系统异常，请联系管理员");
							}
						});
		 	    	}
		 	    	
		 	    }else{
		 	    	if(confirm("确定不通过合规检查？")){
		 				$.ajax({
							type : "POST",
							url : "system/workPlan_verifyRuleCheckAudit.do",
							data : "planId=" + planId + "&passTag=" + passTag +"&suggestion=" +$("#suggestion").val(),
							success : function(json) {
								if(json.state=='success'){
									window.location.href = 'system/workPlan_searchByParam.do';
								}else{
									alert(json.message);					
								}
								$("#"+saveBtn+",#saveBtning").toggle();
							},error:function(){
								alert("系统异常，请联系管理员");
							}
						});
		 			}
		 	    }
		 	
		 	}
		 	
		}else{//合规检查待办已阅
			$.ajax({
				type : "POST",
				url : "system/workPlan_verifyRuleCheckAudit.do",
				data : "planId=" + planId + "&passTag=" + passTag +"&suggestion=" +$("#suggestion").val(),
				success : function(json) {
					if(json.state=='success'){
						window.location.href = 'system/workPlan_searchByParam.do';
					}else{
						alert(json.message);					
					}
				},error:function(){
					alert("系统异常，请联系管理员");
				}
			});
		}
		 
	} 
	
	function exportExcel(){
		var checkNum = "${taskVO.checkNum}";
		if(checkNum==''||checkNum=='0') checkNum = 65535;
		var d = dialog({
			    title: '导出Excel',
			    content: '检查结果导出前&nbsp;<input id="exportNum" value="" style="width:80px" />&nbsp;行。&nbsp;(可以填写 1-'+checkNum+' 之间)',
			    ok: function () {
			        var exportNum = $.trim($('#exportNum').val());
			        if(exportNum==''){
			        	alert("请输出需要导出的行数");
			        	return false;
			        }else if(parseInt(exportNum)<=0){
			        	alert("导出的行数必须大于零");
			        	return false;
			        }else if(parseInt(exportNum) > checkNum){
			        	alert("导出的行数必须在1-"+checkNum+"之间");
			        	return false;
			        }
			        this.close();
			        this.remove();
			        
			        var url = "system/file_downloadCheckResult.do?taskId=${taskVO.taskId}&exportNum="+exportNum;
			        window.open(url,"_blank");
			    }
			});
		d.show();
	}

</script>
<title>合规检查不通过后的审核待办</title>
</head>
  <body class="FrameMain">
	
	<div class="main_title">
		<b>合规检查的审核待办</b>
	</div>
	<form id="verifyForm" name="verifyForm" action="system/workPlan_searchRuleCheckAudit.do" loadContainer="pageDataList">
	<input type="hidden" name="planId" value="${planId }"/>
	<input type="hidden" id="checkResult" value="${taskVO.checkResult }"/>
	<input type="hidden" id="loginUserType" value="${loginUserType }"/>
	<table width="100%" border="0">
		   <tr>
		  		<td align="right">创建人：</td>
		  		<td>${createUser.loginName}</td>
		   </tr>
		   <tr>
		  		<td align="right">创建时间：</td>
		  		<td>${workPlan.createTime}</td>
		  </tr>
		  <tr>
		  		<td align="right">待办标题：</td>
		  		<td>${workPlan.planTitle}</td>
		  </tr>
		  <tr valign="top">
		  		<td align="right">待办内容：</td>
		  		<td>
		  		<p>数据用户登录名：${dataUserVO.loginName}<br/>
		    	数据用户姓名：${dataUserVO.userName}<br/>
		    	所属机构用户登录名：${orgUserVO.loginName}<br/>
		    	所属机构用户姓名：${orgUserVO.userName}<br/><br/>
		    	
		    	服务：${service.serviceCode }<br/>
				服务名称：${service.serviceName }<br/>
				服务类型：<g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${service.serviceType }"/><br/>
				服务周期：
				<c:if test="${service.serviceType=='1'}">
					    每<g:sysDict dictCode="DICT_CYCLE_TYPE" dictKey="${service.cycleType}"/>
					    <c:choose>
					    	<c:when test="${service.cycleType=='1'}">第${service.cycleDay}天</c:when>
					    	<c:when test="${service.cycleType=='2'}">第${service.cycleDay}天</c:when>
					    	<c:when test="${service.cycleType=='3'}">${service.cycleDay}日</c:when>
					    	<c:when test="${service.cycleType=='4'}">${service.cycleDay}</c:when>
					    	<c:when test="${service.cycleType=='5'}"></c:when>
					    </c:choose>
			    </c:if>
				<br/>
				服务任务账期：${taskVO.fieldValue }<br/>
				文件名称：${fileVO.fileName }<br/>
				服务任务开始时间：<fmt:formatDate value="${taskVO.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/><br/>
		  		${workPlan.planContent}</p>
		  		</td>
		  </tr>
		  <c:if test="${taskVO.checkResult=='0'}">
		  <tr>
		  		<td valign="top" align="right">处理意见：</td>
		  		<td><textarea id="suggestion" name="suggestion" rows="6" cols="100"></textarea></td>
		  </tr>
		  </c:if>
	</table>
	
	<c:if test="${taskVO.checkResult!='0'}">
		<div style="padding:20px 0 0 20%">
			<input id="endPlanBtn" type="button" value="已阅" onclick="passOrNoPass('${planId }','','')"/>
		</div>
	</c:if>
	<c:if test="${taskVO.checkResult=='0'}">
		<div style="padding:20px 0 0 20%">
		    <input id="passBtn" type="button" value="通过" onclick="passOrNoPass('${planId }','2','passBtn')"/>
		    <input id="saveBtning" type="button" value="保存中" id="loginingBtn" style="display:none;color:gray" disabled/>
		   	&nbsp;
		    <input id="nopassBtn" type="button" value="不通过" onclick="passOrNoPass('${planId }','0','nopassBtn')"/>
		</div>
	</c:if>
	<c:if test="${loginUser.userType=='auditUser'}">
		<div class="main_title">
			<b>历史处理意见</b>
    	</div>
    	<div class="main_list">
    	<table width="100%" border="0">
				<tr>
					<th>处理人登录名</th>
					<th>处理人姓名</th>
					<th>创建时间</th>
					<th>处理时间</th>
					<th>处理意见</th>
				</tr>
				<c:forEach var="pre" items="${preWorkPlan}" varStatus="status">
				<tr>
					<td>${pre.loginName}</td>
					<td>${pre.userName}</td>
					<td><fmt:formatDate value="${pre.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td><fmt:formatDate value="${pre.daelTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					<td><g:sysDict dictCode="DICT_PLAN_STATE" dictKey="${pre.planState }"/></td>
				</tr>
				</c:forEach>
		</table>
		</div>
	</c:if>
	<div class="main_title">
		<b><a href="service/modelTask_previewRuleCheck.do?taskId=${taskVO.taskId}&serviceId=${service.serviceId}&userId=${dataUserVO.userId}" target="_blank">合规检查结果>></a></b>
    </div>
    <%-- <c:if test="${taskVO.checkResult == '2'}">
    <div class="main_list" style="color:red">
    	不合规行数(${taskVO.checkIrregularNum})超出了最大不合规检查行数(${taskVO.maxCheckNum})，超出不再继续检查
    </div>
    </c:if>
    <c:if test="${taskVO.checkResult != '0'}">
    	<div class="main_list">
                        检查行数：${taskVO.checkNum}, 不合规行数：${taskVO.checkIrregularNum}<br/>
          <c:if test="${taskVO.checkResult == '3'}">
		       <div class="main_list" style="color:red">
		    	 第${taskVO.checkNum}行出现异常非法情况 ，在规定的第${rowCount}个字段后出现额外字段，合规检查中断<br/>
		       </div>
		       <div class="main_list">
					第${warnRow}行信息：
               		<c:forEach var="data" items="${rowData }" varStatus="status">
               			<c:if test="${status.index<rowCount }">
               				<c:if test="${status.index>0}">|</c:if>${data}
               			</c:if>
               			<c:if test="${status.index>=rowCount }">
               				|<label style="color:red">${data}</label>
               			</c:if>
					</c:forEach>
		       </div>
			 </c:if>
	             <c:if test="${taskVO.checkResult == '4'}">
		    <div class="main_list" style="color:red">
		    	第${taskVO.checkNum}行出现异常非法情况 ，未达到规定的${rowCount}个字段，合规检查中断<br/>
		    </div>
		    <div class="main_list">
                        第${warnRow}行信息(实际只有<label style="color:red">${rowDataLength}</label>个字段)：${rowData}<br/>
		    </div>
	            </c:if>
	            <c:if test="${fn:length(pageObject.data) != 0}">
	             	<c:if test="${taskVO.checkResult == '3' || taskVO.checkResult == '4'}">
	            		不合规的前${taskVO.checkIrregularNum-1}行信息：
	            	</c:if>
	            	<c:if test="${taskVO.checkResult != '3' && taskVO.checkResult != '4'}">
	            		不合规的前${taskVO.checkIrregularNum}行信息：
	            	</c:if>
		    		<a href="system/file_downloadCheckResult.do?taskId=${taskVO.taskId}&exportNum=1000" target="_blank" style="color:blue">导出Excel(前1000条)</a>&nbsp;
					<a href="service/modelTask_downloadFtpCheckFile.do?taskId=${taskVO.taskId}" target="_blank" style="color:blue">导出TXT(全部)</a>
				</c:if>
		    </div>
    </c:if>
    <c:if test="${taskVO.checkResult == '0'}">
    	<div class="main_list" style="color:red">
    		数据全部合规，共检查${taskVO.checkNum}行
    	</div>
    </c:if>
    <c:if test="${taskVO.checkResult != '0' && fn:length(pageObject.data) != 0}">
	<div id="pageDataList">
	<div class="main_list" id="main_list">
			<table width="100%" border="0">
				<!-- 表结构元数据 -->
				<tr>
					<th>序号</th>
					<th>行号</th>
					<c:forEach var="field" items="${serviceCodeList }" varStatus="status">
					<th>${field.fieldCode}</th>
					</c:forEach>
				</tr>
				<!--数据  -->
				<c:forEach var="data" items="${pageObject.data}" varStatus="status">
					<c:set value="${data['ROW_ID'] }" var="rowId" scope="request"/>
					<tr ${status.index % 2 == 1? "class='list_bg'" : "" } rowId="${rowId}">
						<td>${(pageObject.curPage-1)*pageObject.pageSize+status.count}</td>
						<c:forEach var="field" items="${data }" varStatus="vs">
							<c:set value="row_${rowId}" var="rowKey"></c:set>
							<c:set value="fieldCode_${field.key}" var="fieldKey"></c:set>
							
							<c:if test="${recordMap['desenType']=='1' }">
								<c:set value="ruleType_${field.key}" var="ruleType"></c:set>
								<c:set value="ruleContent_${field.key}" var="ruleContent"></c:set>
								<c:set value="replaceContent_${field.key}" var="replaceContent"></c:set>
								<c:set value="conditionType_${field.key}" var="conditionType"></c:set>
								<c:set value="conditionContent_${field.key}" var="conditionContent"></c:set>
								<c:if test="${recordMap[rowKey][fieldKey] != null}">
									<c:set value="脱敏类型：${recordMap[rowKey][ruleType] } &#10;处理定位：${recordMap[rowKey][ruleContent]} &#10;替换字符：${recordMap[rowKey][replaceContent]} &#10;" var="ruleTypeStr"></c:set>
									<c:set value="条件类型：${recordMap[rowKey][conditionType] } &#10;过滤条件：${recordMap[rowKey][conditionContent]}" var="conditionTypeStr"></c:set>
									<td style='color:red' title='${recordMap[rowKey][ruleType]==""?"":ruleTypeStr}${recordMap[rowKey][conditionType]==""?"":conditionTypeStr}'>
								</c:if>
								<c:if test="${recordMap[rowKey][fieldKey] == null}">
									<td>
								</c:if>
									${field.value}
								</td>
							</c:if>
							<c:if test="${recordMap['desenType']=='2' }">
								<c:set value="checkType_${field.key}" var="checkType"></c:set>
								<c:set value="checkRule_${field.key}" var="checkRule"></c:set>
								<c:if test="${recordMap[rowKey][fieldKey] != null}">
									<td style='color:red' title='检查类型：${recordMap[rowKey][checkType] } &#10;检查规则：${recordMap[rowKey][checkRule]}'>
								</c:if>
								<c:if test="${recordMap[rowKey][fieldKey] == null}">
									<td>
								</c:if>
									${field.value}
								</td>
							</c:if>
						</c:forEach>
					</tr>
				</c:forEach>
				
				<c:if test="${pageObject.data == null || empty pageObject.data}">
  	  				<tr>
	    				<td colspan=5>查询不到不合规的数据</td>
	  				</tr>
  				</c:if>
			</table>
	</div>
	<g:page pageObject="${pageObject }" />
	</div>
	</c:if> --%>
	</form>
  </body>
</html>
