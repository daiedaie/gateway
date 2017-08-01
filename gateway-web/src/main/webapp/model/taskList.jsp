<%@ page language="java" contentType="text/html; charset=utf-8"%>
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
<script type="text/javascript" src="<%=request.getContextPath() %>/resource/js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript" src="resource/js/artDialog/dialog-min.js"></script>
<script type="text/javascript">
	function checkAndSubmit() {
		var startDate = $("#startDate").val();
		var endDate = $("#endDate").val();
		var d1 = new Date(startDate.replace(/\-/g, "\/"));
		var d2 = new Date(endDate.replace(/\-/g, "\/"));

		if (startDate != "" && endDate != "" && d1 > d2) {
			alert("开始时间不能大于结束时间！");
			return false;
		} else {
			searchPage();
		}

	}

	function stopTask(taskId) {
		if (confirm("确定要终止模型取数任务？")) {
			location.href = "service/modelTask_stopTask.do?taskId=" + taskId;
		}
	}

	function reCheckRule(taskId,userId,serviceId,fieldValue) {
		if (confirm("将要创建新检查任务？")) {
			$.ajax({
				type : "POST",
				url : "service/modelTask_reCheckRule.do",
				data : "taskId="+taskId+"&userId="+userId+"&serviceId="+serviceId+"&fieldValue="+fieldValue,
				success : function(data) {
					if (data.state == 'success') {
						alert("任务创建成功！");
						window.location.href = 'service/modelTask_searchTaskList.do';
					} else {
						alert(data.message);
					}
				}
			});
		}
	}
	
function showBg(ct,content){
    var bH=document.documentElement.clientHeight;
    var bW=$("body").width()+1000;
    var objWH=getObjWh(ct);
      $("#fullbg").css({width:bW,height:bH+1000,display:"block"});
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
	
function openDownLoadDialog(taskId, fileType) {
	showBg('dialog','dialog_content');
	$.ajax({
		type : "POST",
		url : "service/modelTask_searchFileRows.do",
		data : "taskId="+taskId+"&fileType="+fileType,
		success : function(data) {
			if (data.state == 'success') {
				closeBg();
				var rows=data.message;
				var params = "?taskId=" + taskId + "&fileType=" + fileType+"&rows="+rows;
				var d = dialog({
					title : "",
					autoOpen : false,
					modal : true,
					content : "<iframe frameborder='0'  src='model/downLoadDialog.jsp"+params+"\'"+" height='300' width='400' ></iframe>"
				});
				d.show();
			} else {
				alert(data.message);
			}
			
		}
	});

}
</script>
<title>服务任务列表</title>
</head>
  <body class="FrameMain">
	<div id="fullbg"></div>
	<div id="dialog">
		<div id="dialog_content">
			<img src="resource/images/load.gif" /> <br />
			<div>查询文件行数中...</div>
		</div>
	</div>
	<form id="searchForm" action="service/modelTask_searchTaskList.do" method="post" loadContainer="pageDataList">
	<input type="hidden" id = "taskStatus" name="m.taskStatus" valuse="${m.taskStatus} "/>
	<input type="hidden" id = "userId" name="m.userId" valuse="${m.userId} "/>
	<input type="hidden" id = "serviceId" name="m.serviceId" valuse="${m.serviceId} "/>
  <div class="main_title">
                <b>服务任务查看</b>
        </div>
        <div class="main_search">
          <table width="100%" border="0">
                  <tr>
                    <td width="80%">
                    	<c:if test="${loginUser.userType!='dataUser' }">
							登录账号：<input type="text" name="loginName" value="${loginName}"/>
							&nbsp;&nbsp;
						</c:if>
						<!--  
						模型编码：<input type="text" id="modelCode" name="modelCode" value="${modelCode}"/>
                        &nbsp;&nbsp;
                        -->
						模型名称：<input type="text" id="modelName" name="modelName" value="${modelName}"/>
                    </td>
                    <td colspan="2"></td>
                  </tr>
                  <tr>
                    <td width="90%">
						服务编码：<input type="text" id= "serviceCode" name="serviceCode" value="${serviceCode}"/>
                    	&nbsp;&nbsp;
                    	数据就绪状态 ：
                        <g:sysDictList dictCode="DICT_DATA_WAIT_STATUS" defaultValue="${dataProgressStatus}" tagType="select" tagName="dataProgressStatus" tagId="dataProgressStatus"/> 
						&nbsp;&nbsp;
						开始时间：
						<input readonly="readonly" class="Wdate" type="text" onclick="WdatePicker()"  name="startDate" id="startDate" value="${startDate}"/>
						 至&nbsp;<input class="Wdate" readonly="readonly" type="text" onclick="WdatePicker()" name="endDate" id="endDate" value="${endDate}"/>
                            &nbsp;&nbsp;
                                                  
                            <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['findTask']!=null}">
                            	<input name="" type="button" value="查询" onclick="checkAndSubmit()"/> 
                            </c:if> 
                                               
                    </td>
                    <td></td>
                  </tr>
          </table>
         </div>
         <div id="pageDataList">
         <div class="main_list">
        <table width="100%" border="0">
          <tr>
            <th>序号</th>
            <th><a class="tableSort" sort="login_Name">用户名称</a></th>
            <th><a class="tableSort" sort="model_Name">模型名称</a></th>
            <th><a class="tableSort" sort="service_Code">服务编码</a></th>
            <th><a class="tableSort" sort="service_Name">服务名称</a></th>
            <th><a class="tableSort" sort="service_Type">服务类型</a></th>
            <th><a class="tableSort" sort="cycle_Type">服务周期</a></th>
            <th><a class="tableSort" sort="field_value">任务账期</a></th>
            <th><a class="tableSort" sort="audit_Time">申请通过时间</a></th>
            <th><a class="tableSort" sort="data_progress_Status">数据就绪状态</a></th>
            <th><a class="tableSort" sort="data_Num">输出行数</a></th>
            <th><a class="tableSort" sort="field_num">字段数</a></th>
            <th><a class="tableSort" sort="check_Num">检查行数</a></th>
            <th><a class="tableSort" sort="file_Status">文件状态</a></th>
            <th><a class="tableSort" sort="task_Status">任务状态</a></th>
            <th><a class="tableSort" sort="create_Time">任务开始时间</a></th>
            <th><a class="tableSort" sort="download_End_Time">任务结束时间</a></th>
            <th>操作</th>
          </tr>
          <c:forEach var="m" items="${pageObject.data }" varStatus="vs">
               <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
                	<td>${(pageObject.curPage-1)*pageObject.pageSize+vs.count }</td>
                    <td>${m.userName}</td>
                    <td>${m.modelName}</td>
                    <td>${m.serviceCode}</td>
                    <td>${m.serviceName}</td>
                    <td><g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${m.serviceType }"/></td>
                    <td>
<!--                     	<g:sysDict dictCode="DICT_CYCLE_TYPE" dictKey="${m.cycleType }"/> -->
                    	<c:if test="${m.serviceType=='1'}">
					    	每<g:sysDict dictCode="DICT_CYCLE_TYPE" dictKey="${m.cycleType}"/>
					    	<c:choose>
					    		<c:when test="${m.cycleType=='1'}">第${m.cycleNum}天</c:when>
					    		<c:when test="${m.cycleType=='2'}">第${m.cycleNum}天</c:when>
					    		<c:when test="${m.cycleType=='3'}">${m.cycleNum}日</c:when>
					    		<c:when test="${m.cycleType=='4'}">${m.cycleNum}</c:when>
					    		<c:when test="${m.cycleType=='5'}"></c:when>
					    	</c:choose>
			    		</c:if>
                    </td>
                    <td>${m.fieldValue }</td>
                    <td><fmt:formatDate value="${m.auditTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td>
                    	<c:choose>
                    		<c:when test="${m.dataProgressStatus=='-1'}">
                    			<label style="color:red"><g:sysDict dictCode="DICT_DATA_WAIT_STATUS" dictKey="${m.dataProgressStatus}"/></label>
                    		</c:when>
                    		<c:otherwise><g:sysDict dictCode="DICT_DATA_WAIT_STATUS" dictKey="${m.dataProgressStatus}"/>
                    			<c:if test="${m.preDataProgressStatus != '' && m.preDataProgressStatus != null}">
                    				(<g:sysDict dictCode="DICT_DATA_WAIT_STATUS" dictKey="${m.preDataProgressStatus}"/>)
                    			</c:if>
                    		</c:otherwise>
                    	</c:choose>
                    </td>
                    <td>
                    	<c:choose>
                    		<c:when test="${m.checkResult!='0' || m.dataProgressStatus=='11'|| m.dataProgressStatus=='9'}">
                    			N/A
                    		</c:when>
                    		<c:otherwise>${m.dataNum}</c:otherwise>
                    	</c:choose>
                    </td>
                    <td>
                    	<c:choose>
                    		<c:when test="${m.fieldNum==0}">
                    			N/A
                    		</c:when>
                    		<c:otherwise>${m.fieldNum}</c:otherwise>
                    	</c:choose>
                    </td>
                    <td>${m.checkNum}</td>
                    <td><g:sysDict dictCode="DICT_FILE_STATUS" dictKey="${m.fileStatus}"/></td>
                    <td><g:sysDict dictCode="DICT_TASK_STATUS" dictKey="${m.taskStatus}"/></td>
                    <td><fmt:formatDate value="${m.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td><fmt:formatDate value="${m.downloadEndTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                    <td>
                    <c:if test="${(m.dataProgressStatus >= 8 && m.dataProgressStatus !=15) || (m.dataProgressStatus ==15 && m.preDataProgressStatus >= 8) }">
						<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['ruleCheckResult']!=null }">
		    				<a href="service/modelTask_previewRuleCheck.do?taskId=${m.taskId}&serviceId=${m.serviceId}&userId=${m.userId}" target="_blank">检查结果</a>
						</c:if>
					</c:if>
                    
                   <c:if test="${m.dataProgressStatus == 11 && SESSION_ATTRIBUTE_USER_BUTTON['reDoService']!=null}">
						<c:if test="${m.redoTag == '1' }">
		    				重新检查
						</c:if>
						<c:if test="${m.redoTag != '1' }">
		    				<a href="javascript:reCheckRule('${m.taskId}','${m.userId}','${m.serviceId}','${m.fieldValue }')">重新检查</a>
						</c:if>
					</c:if>
                    <c:if test="${m.dataStatus == '1'}">

                    	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['downloadData']!=null && m.taskStatus=='1' && m.checkResult=='0' && (m.dataProgressStatus == 14 || m.dataProgressStatus == 13||m.dataProgressStatus == 12||m.dataProgressStatus == 10||m.dataProgressStatus == 9||m.dataProgressStatus == 8 ||m.dataProgressStatus == 16 || m.preDataProgressStatus == 14 || m.preDataProgressStatus == 13||m.preDataProgressStatus == 12||m.preDataProgressStatus == 10||m.preDataProgressStatus == 9||m.preDataProgressStatus == 8 ||m.preDataProgressStatus == 16)}">	                      
							<c:if test="${loginUser.userType == 'dataUser'}">
								<a href="service/modelTask_downloadFtpFile.do?taskId=${m.taskId}&fileType=2" target="_blank">下载</a>
							</c:if>
							<c:if test="${loginUser.userType != 'dataUser'}">
								<a href="service/modelTask_downloadFtpFile.do?taskId=${m.taskId}&fileType=5" target="_blank">下载</a>
							</c:if>
						</c:if>
						<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['downloadOriginalData']!=null && ((m.dataProgressStatus >= 4 && m.dataProgressStatus != 15) || (m.preDataProgressStatus >= 4 && m.dataProgressStatus == 15)) && m.originalFileStatus=='1'}">	                      
							<a href="service/modelTask_downloadFtpFile.do?taskId=${m.taskId}&fileType=1" target="_blank">原件下载</a>
						</c:if>
						
						<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['sampleDownloadData']!=null && m.taskStatus=='1' && m.checkResult=='0' && (m.dataProgressStatus == 14 || m.dataProgressStatus == 13||m.dataProgressStatus == 12||m.dataProgressStatus == 10||m.dataProgressStatus == 9||m.dataProgressStatus == 8 ||m.dataProgressStatus == 16|| m.preDataProgressStatus == 14 || m.preDataProgressStatus == 13||m.preDataProgressStatus == 12||m.preDataProgressStatus == 10||m.preDataProgressStatus == 9||m.preDataProgressStatus == 8 ||m.preDataProgressStatus == 16)}">	                      
							<c:if test="${loginUser.userType == 'dataUser'}">
								<a href="javascript:openDownLoadDialog('${m.taskId}','2')">抽样下载</a>
							</c:if>
							<c:if test="${loginUser.userType != 'dataUser'}">
								<a href="javascript:openDownLoadDialog('${m.taskId}','5')">抽样下载</a>
							</c:if>
						</c:if>
						<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['sampleDownloadOriginalData']!=null  && ((m.dataProgressStatus >= 4 && m.dataProgressStatus != 15) || (m.preDataProgressStatus >= 4 && m.dataProgressStatus == 15)) && m.originalFileStatus=='1'}">	                      
							<a href="javascript:openDownLoadDialog('${m.taskId}','1')">抽样原件下载</a>
						</c:if>
						
						<c:if test="${m.desenType=='1' && m.dataProgressStatus == 10}">
							<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['sample']!=null && m.serviceType == '1'}">
								<a href="service/modelTask_offlinePreviewServiceData.do?taskId=${m.taskId}" target="_blank">抽样</a>
							</c:if>
							<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['sample']!=null && m.serviceType == '0'}">
			    				<a href="service/modelTask_onlinePreviewServiceData.do?taskId=${m.taskId}" target="_blank">抽样</a>
							</c:if>
						</c:if>
					</c:if>
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['cancelTask']!=null}">
						<c:if test="${m.taskStatus == 1 && (m.dataProgressStatus != 7 && m.dataProgressStatus != 9 && m.dataProgressStatus != 11 && m.dataProgressStatus != 12 && m.dataProgressStatus != 13 && m.dataProgressStatus != 14 && m.dataProgressStatus != 15) }">  
							<a href=" javascript:stopTask(${m.taskId}) ">终止</a> 
						</c:if>
                    </c:if>
                    </td>
                 </tr>
          </c:forEach>
          <c:if test="${pageObject.data == null || empty pageObject.data}">
            	 <tr>
                    <td colspan=18>查询不到服务任务数据</td>
                 </tr>
          </c:if>
         </table>
         <c:if test="${pageObject.data != null && not empty pageObject.data}">
	        <div style="padding-top:10px">
				合规检查总行数：${taskCountMap['checkRowNum'] }	&nbsp;&nbsp;
				检查合规的文件数：${taskCountMap['checkPassCount'] }	&nbsp;&nbsp;
				输出合规文件的总行数：${taskCountMap['passDataNum'] }	&nbsp;&nbsp;
				输出数据量：${taskCountMap['outputDataNum'] }
			</div>
         </c:if>
         </div>
         	<g:page pageObject="${pageObject }" />
         </div>
  </form>
  </body>
</html>