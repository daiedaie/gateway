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
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>新增服务_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript">	
	function reApply(){
		if($("input[name='upload']").val()==''){
			alert("你还没配置规则文件，请先配置")
			return
		}
		if(check()){
			var s = $("#serviceForm").serialize();
			$("#reApplyServiceBtn,#applyingServiceBtn").toggle();
			util.ajax($("#serviceForm").attr("action"),$("#serviceForm").serialize(),function(json){
				$("#reApplyServiceBtn,#applyingServiceBtn").toggle();
				if(json.state=='success'){
					alert("保存成功");
					location.href="<%=basePath %>service/serviceInfo_searchServiceList.do";
				}else{
					alert(json.message);				
				}
			});
		}
	}
	   function saveRuleAndCommit(){
		   

				var s = $("#serviceForm").serialize();
				$("#reApplyServiceBtn,#applyingServiceBtn").toggle();
				util.ajax($("#serviceForm").attr("action"),$("#serviceForm").serialize(),function(json){
					$("#reApplyServiceBtn,#applyingServiceBtn").toggle();
					if(json.state=='success'){
						alert("保存成功");
						location.href="<%=basePath %>service/serviceInfo_searchServiceList.do";
					}else{
						alert(json.message);				
					}
				});
		
		  
	   }	
	
	function uploadExcel(){
		$("#uploadButton,#uploadingButton").toggle();

		ruleCheck();
		var formData = new FormData($( "#uploadFile" )[0]);
	    $.ajax({  
	         url: "system/desenModel_showRuleFile.do", 
	         type: 'POST',  
	         data: formData,  
	         cache: false,  
	         contentType: false,  
	         processData: false,  
	         success: function (data) {
		        $("#ruleTable").empty() 
	        	$("#uploadTable").empty();	

	        	 var RuleList=new Array();
	        	 var datas = JSON.parse(data);
	        	 var RuleList= datas.data.checkRuleList;
	        	 var checkDictMap=new Array();
	        	 var checkDictMap = datas.data.checkDictMap;
	        	 var filoVO = datas.data.fileVo;
	        	 $("#fileId").val(filoVO.fileId);
	        	 var str ="";
	        	 str ="<table width='100%' border='0'>"+
				        	 "<tr>"+
								"<th style='text-align:center'>字段编码</th>"+
								"<th style='text-align:center'>字段名称</th>"+
								"<th style='text-align:center'>字段类型</th>"+
								"<th style='text-align:center'>检查类型</th>"+
								"<th style='text-align:center'>检查规则</th>"+
							  "</tr>";
	        	 if(RuleList == null||RuleList.size == 0){
	        		 str ="<tr><td colspan=9>查询不到该服务的字段信息</td></tr>";
	        	 }else{
	        		 $.each(RuleList,function(i,ele){
	 					str+="<tr name='fieldTr'>"+
	 							"<td>"+ele.fieldCode+"</td>"+
	 							"<td>"+ele.fieldName+"<input type='hidden' name='fieldName' value="+ele.fieldName+"/></td>"+
	 							"<td>"+ele.fieldType+"<input type='hidden' name='fieldType' value="+ele.fieldType+"/></td>"+
	 							"<td>"+checkDictMap[ele.checkType].dictValue+"</td>"+
	 							"<td>"+ele.checkRule+"</td>"+
	 						 "</tr>";
	 				})
	        	 }				
				str+="</table>";
	        	$("#ruleTable").append(str);	
	        		$("#uploaded").css("display","block");
		        	$("#uploaded span").text($("input[name='prefileName']").val());
		        	$("#uploaded a").attr("onclick","toPreRuleInfo("+$('#fileId').val()+")");
		        	$("#curRule a").attr("onclick","toDownloadRule("+filoVO.fileId+")");
		        	$("#curRule span").text(filoVO.realName);
    	  
	        	$("input[name='prefileName']").val(filoVO.realName);
	        	$("#fileId").val(filoVO.fileId);
			
				$("#uploadButton,#uploadingButton").toggle();
	         },  
	         
	         error: function (returndata) {  
	             alert("上传失败");  
	 			$("#uploadButton,#uploadingButton").toggle();
	         }  
	    });  
		
	}
	function ruleCheck(){
		if($("input[name='upload']").val()==''){
			alert("请选择合规检查的规则文件！");
			$("input[name='upload']").focus();
			$("#uploadButton,#uploadingButton").toggle();

			return false;
		}
		if (!/\.(xlsx|xls)$/.test($("input[name='upload']").val())) {
			alert("合规检查的规则文件只能为Excel文件！");
			$("input[name='upload']").focus();
			$("#uploadButton,#uploadingButton").toggle();

			return false;
		}
		return true;
	}
	
	function check(){

		$("#serviceForm input").each(function(){
			$(this).val($.trim($(this).val()));
		});
		
		if($("#serviceCode").val()==''){
			alert("服务编码不能为空");
			$("#serviceCode").focus();
			return false;
		}
		if(!/^[0-9a-zA-Z]+$/.test($("#serviceCode").val())){
			alert("服务编码只能为字母和数字，请重新输入");
			$("#serviceCode").focus();
			return false;
		}
		if($("#serviceName").val()==''){
			alert("服务名称不能为空");
			$("#serviceName").focus();
			return false;
		}
		if($("#serviceCode").val()==''){
			alert("服务编码不能为空");
			$("#serviceCode").focus();
			return false;
		}
		if($('#serviceType').val() == '1'){
			if($("#cycleType :selected").val()==''){
				alert("请选择周期类型");
				return false;
			}
			if($("#cycleDay").val()==''){
				alert("周期天数不能为空");
				$("#cycleDay").focus();
				return false;
			}
			if(isNaN($("#cycleDay").val())){
				alert("周期天数只能为数字");
				$("#cycleDay").focus();
				return false;
			}
			if($("#cycleDay").val()<1 || $("#cycleDay").val()>31){
				alert("周期天数只能在1-31之间");
				$("#cycleDay").focus();
				return false;
			}
		}
		
		return true;
	}
	
	$(document).ready(function(){ 
		$("#cycleType").change(function(){
			if($(this).val()==5){
				$("#cycleDay").attr("disabled", true);
			}else{
				$("#cycleDay").attr("disabled", false);
			}
		})
		changeServiceType();
	})
	
	function changeServiceType(){
		if($('#serviceType').val() == '1'){
			$("tr[name=offline]").show();
		}else{
			$("tr[name=offline]").hide();
		}
	}
	function toUrl(){
        location.href="system/workPlan_ruleInfo.do?workPlanId=${planId}&serviceId=${serviceVO.serviceId}&isModify=1";	
	}
	function toRuleInfo(ruleBatch){
		location.href="system/workPlan_ruleInfo.do?workPlanId=${planId}&serviceId=${serviceVO.serviceId}&ruleBatch="+ruleBatch+"&isModify=0"
	}
	function toPreRuleInfo(fileId){
		window.open("system/workPlan_preRuleInfo.do?workPlanId=${planId}&serviceId=${serviceVO.serviceId}&fileId="+fileId+"&isModify=0");
	}
	function toDownloadRule(fileId){
		location.href='system/file_downLoadFile.do?fileId='+fileId;
	}
</script>
</head>	
<body>
	<div class="main_upload" method="post">
	<form id="serviceForm" name="serviceForm" action="system/desenModel_saveRuleAndCommit.do">
	<input name="fileId" id="fileId" type ="hidden" value=""/>
    <input name="preFileId" id="preFileId" type="hidden" value="${fileVO.fileId}"/>
    <input name="serviceId" id="serviceId" type="hidden" value="${serviceVO.serviceId}"/>
    <input name="userId" id="userId" type="hidden" value="${createUser.userId}"/>
    <input name="workPlanId" id="workPlanId" type="hidden" value="${planId}"/>
    <input name="prefileId" id="prefileId" type ="hidden" value="${FileVo.fileId}"/>
    <input name="fetchId" id="fetchId" type ="hidden" value="${fetchId}"/>
      <input name="userId" id="userId" type ="hidden" value="${createUser.userId}"/>
    
	    <div class="main_title">
	        <c:if test="${isModify eq '1'}">
	    		 <b>合规检查规则修改</b>	    	
	    	</c:if>	
	    	<c:if test="${isModify eq '0'}">
	    		 <b>合规检查规则查看</b>	    	
	    	</c:if>
	    </div>
    	<div class="main_info_title">服务基本信息</div>
			<table width="100%" border="0">
		      	<tr>
		      	  	<td style="width:90px;text-align:right">服务编码：</td>
		      	  	<td style="width:150px;">${serviceVO.serviceCode }</td>
		      	  	<td style="width:90px;text-align:right">服务名称：</td>
		      	  	<td style="width:150px;">${serviceVO.serviceName }</td>
  		      	  	<td style="width:90px;text-align:right">服务类型：</td>
		      	  	<td style="width:150px;"><g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${serviceVO.serviceType }"/></td>
		      	</tr>
		      	<tr>
		      	  	<td style="width:90px;text-align:right">服务周期：</td>
		      	  	<td colspan=5>
<!-- 		      	  	  	  	<g:sysDict dictCode="DICT_CYCLE_TYPE" dictKey="${serviceView.cycleType }"/> -->
		      	  	<c:if test="${serviceVO.serviceType=='1'}">
					    	每<g:sysDict dictCode="DICT_CYCLE_TYPE" dictKey="${serviceVO.cycleType}"/>
						  <c:choose>
						    	<c:when test="${serviceVO.cycleType=='1'}">第${serviceVO.cycleDay}天</c:when>
						    	<c:when test="${serviceVO.cycleType=='2'}">第${serviceVO.cycleDay}天</c:when>
						    	<c:when test="${serviceVO.cycleType=='3'}">${serviceVO.cycleDay}日</c:when>
						    	<c:when test="${serviceVO.cycleType=='4'}">${serviceVO.cycleDay}</c:when>
						    	<c:when test="${serviceVO.cycleType=='5'}"></c:when>
						  </c:choose>
				    </c:if>	
		      	  </td>
		       </tr>
		    </table>      
    
    
    
    	<input name="serviceVO.serviceId" id="serviceId" type="hidden" value="${serviceVO.serviceId }"/>
       
	
	</form>
	<div class="content_title">
         合规检查规则配置
	</div>
	
	<div id="ruleCheckDiv">
		<div class="main_search" style="padding:10px 0 0 10px;margin-left:30px">
			<div class="main_list" style="padding:0 20px 0 20px">
						
			合规检查规则规范文件下载：
				<a href='system/file_downLoadFile.do?fileId=474'><span class="c_red">规则模板.xmls</span></a>
			<br/>
			<br/>			
		
				<div style="margin-bottom:10px;display:none" id="uploaded" >
					已上传的合规检查规则文件：
					
				<c:if test="${isModify eq '1'}">
					<a style="cursor:pointer" onclick="toRuleInfo(${ruleBatch})"  target="_blank">
					<span class="c_red" id="fileRealName">${FileVo.realName}</span></a>
				</c:if>
												
				</div>
				<div id="curRule">
				
					<a style="cursor:pointer" onclick="toDownloadRule(${FileVo.fileId})"  >
										当前合规检查规则规范下载：
						<span class="c_red">${FileVo.realName}</span>
					</a>
				</div>
			
			<c:if test="${isModify eq '1'}">
			
			<form id="uploadFile">
			合规检查规则文件：<input type="file" name="upload" value="11"style="width:250px"/>

			<input id="uploadButton" type="button" onclick='uploadExcel()' value="上存"/>
			<input id="uploadingButton" style='display:none;color:gray;'  type="button" onclick='uploadExcel()' value="上存中"/>
			
			</form>	
			
			</c:if>
				<div id="uploadTable" style="padding:10px 10px 0 20px;margin-top:5px;" class="main_list" ></div>
			<div>
			
				</div>
		</div>
	</div>
	<div>
</div>
 <div id="ruleDiv" >
		<div  id="ruleTable" class="main_list" style="padding:0 20px 0 20px">
						
				<table width="100%" border="0">
					<tr>
						<th>字段编码</th>
						<th>字段名称</th>
						<th>字段类型</th>
						<th>检查类型</th>
						<th>检查规则</th>
					</tr>
					<c:forEach var="o" items="${checkRuleList}" varStatus="status">
						<tr name="fieldTr">
							<td>${o.fieldCode }</td>
							<td>${o.fieldName }<input type="hidden" name="fieldName" value="${o.fieldName }" />
							</td>
							<td>${o.fieldType }<input type="hidden" name="fieldType" value="${o.fieldType }" />
							</td>
							<td><g:sysDict dictCode="DICT_CHECK_RULE_TYPE" dictKey="${o.checkType}"/>
							</td>
							<td>${o.checkRule}</td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</div>	
			<c:if test="${isModify eq '1'}">
				<input id="reApplyServiceBtn" type="button" style='margin-left:30%'onclick='saveRuleAndCommit()' value="保存"/>	
				<input id="applyingServiceBtn" type="button"  disabled style='display:none;color:gray;margin-left:30%'onclick='saveRuleAndCommit()' value="保存中"/>														
				<input type="button" onclick='window.close()' value="关闭"/>														
			</c:if>
			<c:if test="${isModify eq '0'}">
				<input type="button" onclick='window.close()'style='margin-left:30%' value="关闭"/>														
			</c:if>
		</div>
	<div>
	
</body>
</html>
