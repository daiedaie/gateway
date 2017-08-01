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
<title>查看规则_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript">	
	function ApplyUpdate(){
		$("#updateServiceBtn").hide();
		$("#old_titile").hide();
		$("#new_titile").show();		
		$("#saveServiceBtn").show();
		$("#uploadFile").show();
		$("#oldRuleDetail").show();
		
	}
	
	function uploadExcel(){
		$("#uploadButton,#uploadingButton").toggle();
		ruleCheck();
		var formData = new FormData($( "#uploadFile" )[0]);
		formData.append("fileId",fileId);
	    $.ajax({  
	         url: "service/modelDataApp_showRuleFile.do",
	         type: 'POST',  
	         data: formData, 
	         cache: false,  
	         contentType: false,  
	         processData: false, 
	         success: function (data) {
	         	$("#uploadTable").empty();
	         	var RuleList=new Array();
	        	 var datas = JSON.parse(data);
	        	 var RuleList= datas.data.checkRuleList;
	        	 var checkDictMap=new Array();
	        	 var checkDictMap = datas.data.checkDictMap;
	        	 $("#fileId").val(datas.data.fileVo.fileId);
	        	 var str ="";
	        	 str ="<table width='100%' border='0'>"+
				        	 "<tr>"+
								"<th>字段编码</th>"+
								"<th>字段名称</th>"+
								"<th>字段类型</th>"+
								"<th>检查类型</th>"+
								"<th>检查规则</th>"+
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
	        	$("#uploadTable").append(str);
	        	$("#uploadButton,#uploadingButton").toggle(); 	      	
	         },  
	         error: function (returndata) {  
	             alert("上传失败");  
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
	
	
	function saveApply(){
		/****
		if(ruleCheck()){
			var formData = new FormData($( "#uploadFile" )[0]);
			formData.append("fileId",fileId);		
			formData.append("serviceId",serviceId);		
			$.ajax({  
	         url: "service/modelDataApp_updateServiceRule.do",
	         type: 'POST',  
	         data: formData,  
	         cache: false,  
	         contentType: false,  
	         processData: false,  
	         success: function (data) {
	        	 window.location.reload(true);
	        	//$("#uploadFile").append(str);	        	
	         },  
	         error: function (returndata) {  
	             alert("提交修改服务规则失败!");  
	         }  
	    });		
		}****/
		if(ruleCheck()){
			var s = $("#serviceRuleForm").serialize();
			$("#saveServiceBtn,#saveServiceBtning").toggle();
			util.ajax($("#serviceRuleForm").attr("action"),$("#serviceRuleForm").serialize(),function(json){
				$("#saveServiceBtn,#saveServiceBtning").toggle();
				if(json.state=='success'){
					alert("修改服务规则成功");
				}else{
					alert(json.message);				
				}
			});
		}
		
	}
	
	var serviceId = ${ serviceId };
	var userId = ${ userId };
	function queryOldServiceRule(){
		var checkBatch = $("#checkBatch").val();
		location.href="service/modelDataApp_queryOldServiceRule.do?checkBatch=${serviceId}";
	}
</script>
</head>	
<body>
  <div class="main_upload" method="post">
  <form id="serviceRuleForm" action="service/modelDataApp_updateServiceRule.do" method="post" >	
		<input name="fileId" id="fileId" type ="hidden" value=""/>
		<input name="prefileName" id="prefileName" type ="hidden" value="${fileName}"/>
		<input name="serviceId" id="serviceId" type ="hidden" value="${serviceId}"/>
		<input name="userId" id="userId" type ="hidden" value="${userId}"/>
		<div class="main_title">
	         	<b id="old_titile">合规检查规则查看</b>
	         	<b id="new_titile" style="display: none;">合规检查规则修改</b>
		</div>
		<div class="main_info_title">服务基本信息</div>
			<div class="main_search" >
				<p>服务编码:${gwServiceVO.serviceCode}&nbsp;服务名称:${gwServiceVO.serviceName}&nbsp;用户编码:${userId}&nbsp;用户名称:${loginName}</p>
			</div>
	</form>	
	<div class="main_info_title">合规检查规则配置</div>
		
	<div class="ruleCheckDiv" >
		<div class="main_search" style="padding:10px 0 0 10px;margin-left:30px">
			<div class="main_list" style="padding:0 20px 0 20px">
				合规检查规则规范文件下载：
					<a href='system/file_downLoadFile.do?fileId=474'><span class="c_red">规则模板.xmls</span></a><br/><br/>
				已上传的合规检查规则文件：
					<a href='system/file_downLoadFile.do?fileId=${fileId}'><span class="c_red">${fileName}</span></a><br/><br/>
					<a href="service/modelDataApp_queryOldServiceRule.do?checkBatch=${pageObject.data[0].checkBatch }" target="_blank" style="display: none;cursor:pointer;" id="oldRuleDetail"><span class="c_red">查看</span></a><br/><br/>
				<form id="uploadFile" style="display: none;">		
				合规检查规则文件：<input type="file" name="upload" style="width:250px"/>
							<input id="uploadButton" type="button" onclick='uploadExcel()' value="上传"/>
							<input id="uploadingButton" style='display:none;color:gray;' type="button" value="上传中"/>
							
				</form>
			</div>
		</div>	
		</div>
	</div>
	<div id="pageDataList">
		<div class="main_list">
			<table width="100%" border="0" id="uploadTable">
			  <tr>
			    <th>字段编码</th>
			    <th>字段名称</th>
			    <th>字段类型</th>
			    <th>检查类型</th>
			    <th>检查规则</th>
			  </tr>
	  
			  <c:forEach var="rule" items="${pageObject.data }" varStatus="vs">
		  	     <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
				    <td>${rule.fieldCode}</td>
				    <td>${rule.fieldName}</td>
				    <td>${rule.fieldType}</td>
				    <td>${checkDictMap[rule.checkType].dictValue }</td>
				    <td>${rule.checkRule}</td>
				 </tr>
				 <input name="checkBatch" id="checkBatch" type ="hidden" value="${rule.checkBatch}"/>
			  </c:forEach>
			  <c:if test="${pageObject.data == null || empty pageObject.data}">
			  	  <tr>
				    <td colspan=5>查询不到服务规则信息</td>
				  </tr>
			  </c:if>
	 		</table>
		</div>
		<!--  
		<g:page pageObject="${pageObject }" />
		-->
	</div>
	<div>
		<input id="updateServiceBtn" type="button" onclick='ApplyUpdate()' value="修改"/>
		<input id="saveServiceBtn" type="button"  style='display:none;'  onclick='saveApply()' value="保存"/>
		<input id="saveServiceBtning" type="button" disabled style='display:none;'  onclick='saveApply()' value="保存中"/>
		<input type="button" onclick='javascript:history.go(-1);' value="返回"/>
	  
  	</div>
</body>
</html>
