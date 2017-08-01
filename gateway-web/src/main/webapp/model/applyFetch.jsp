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
			alert("你还没配置规则文件，请先配置");
			return;
		}
		
		if("${serviceVO.serviceType}"=='0'){
			if($("input[name='pushDataWay']:checked").val() == undefined){
				alert("请选择数据推送方式！");
				return;
			}
			if($("input[name='pushDataWay']:checked").val()=='2'){
				if("${appUserVO.webserviceUrl}" == '' || "${appUserVO.webserviceMethod}" == ''){
					alert("推送方式信息不全，请补充完成再申请");
					return;
				} 
			
			}
		}
		
		if(check()){
			var s = $("#serviceForm").serialize();
			$("#reApplyServiceBtn,#applyingServiceBtn").toggle();
			util.ajax($("#serviceForm").attr("action"),$("#serviceForm").serialize(),function(json){
				$("#reApplyServiceBtn,#applyingServiceBtn").toggle();
				if(json.state=='success'){
					alert("申请成功");
					location.href="<%=basePath %>service/serviceInfo_searchServiceList.do";
				}else{
					alert(json.message);				
				}
			});
		}
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
	        	 var RuleList=new Array();
	        	 var datas = JSON.parse(data);
				 if(datas.state =="error"){
	        		 
	        		 $("#uploadButton,#uploadingButton").toggle();
	        		 alert(datas.message);
	        		 $("input[name=upload]").val("");
	        		 return;
	        	 }
	        	 var RuleList= datas.data.checkRuleList;
	        	 var checkDictMap=new Array();
	        	 var checkDictMap = datas.data.checkDictMap;
	        	 $("#fileId").val(datas.data.fileVo.fileId);
	        	 var str ="";
	        	 str ="<br><br><br><table width='100%' border='0'>"+
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
	        	$("#uploadTable").append(str);	 
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
			if($("#cycleDay").val()==''&&$("#cycleType").val() !='5'){
				alert("周期天数不能为空");
				$("#cycleDay").focus();
				return false;
			}
			if(isNaN($("#cycleDay").val())){
				alert("周期天数只能为数字");
				$("#cycleDay").focus();
				return false;
			}
			if(($("#cycleDay").val()<1 || $("#cycleDay").val()>31) &&$("#cycleType").val() !='5'){
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
			$("tr[name=online]").hide();
		}else{
			$("tr[name=offline]").hide();
			$("tr[name=online]").show();
		}
	}
</script>
</head>	
<body>
	<div class="main_upload" method="post">
	<form id="serviceForm" name="serviceForm" action="system/desenModel_reApplyService.do">
	<input name="fileId" id="fileId" type ="hidden" value=""/>
    <input name="preFileId" id="preFileId" type="hidden" value="${fileVO.fileId}"/>

    
    
    
    <c:if test="${empty auditStatus }">
        <input name="serviceVO.serviceCode" id="serviceCode" type="hidden" value="${serviceVO.serviceCode }"/>
	    <input name="serviceVO.serviceType" id="serviceType" type="hidden" value="${serviceVO.serviceType }"/>
	    <input name="serviceVO.serviceName" id="serviceName" type="hidden" value="${serviceVO.serviceName }"/>
	    <input name="serviceVO.serviceId" id="serviceId" type="hidden" value="${serviceVO.serviceId }"/>
	    <input name="serviceVO.cycleType"  id="cycleType" type="hidden" value="${serviceVO.cycleType }"/>
	    <input name="serviceVO.cycleDay"  id="cycleDay" type="hidden" value="${serviceVO.cycleDay }"/>
	    <div class="main_title">
	            <b>申请取数</b>
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
		       <tr name="online" style="display:none">
				<td style="width:90px;text-align:right">数据推送方式：</td>
					<td>
			    	<input type="radio" name="pushDataWay" value="1" ${fetVO.pushDataWay=="1"?"checked":"" }/><label>ftp</label>
					<input type="radio" name="pushDataWay" value="2" ${fetVO.pushDataWay=="2"?"checked":"" }/><label>webservice</label>
					</td>
				</tr>
		    </table>      
    </c:if>
    
    
    
    <c:if test="${auditStatus eq '0'}">
    	<input name="serviceVO.serviceId" id="serviceId" type="hidden" value="${serviceVO.serviceId }"/>
        <div class="main_title">
            <b>重新申请取数</b>
        </div>
		<div class="main_info_title">
          	 服务基本信息
		</div>
		<table width="50%" border="0" style="padding:20px">
			<tr>
			    <th>服务类型：</th>
			    <td>
			    <select id="serviceType" name="serviceVO.serviceType" onchange="changeServiceType()">
			    	<option value="1">离线</option>
			    	<option value="0">实时</option>
			    </select>
				<span class="c_red">*</span></td>
			</tr>
			<tr>
			    <th>服务编码：</th>
			    <td><input name="serviceVO.serviceCode" dissabled="true" readOnly="true" id="serviceCode" type="text" maxlength="15" value="${serviceVO.serviceCode }"/><span class="c_red">*</span></td>
			</tr>
			<tr>
			    <th>服务名称：</th>
			    <td><input name="serviceVO.serviceName" id="serviceName" type="text"/><span class="c_red">*</span></td>
			</tr>
			<tr name="offline" style="display:none">
			    <th>周期类型：</th>
			    <td><g:sysDictList dictCode="DICT_CYCLE_TYPE" tagType="select" tagId="cycleType" tagName="serviceVO.cycleType"/><span class="c_red">*</span></td>
			</tr>
			<tr name="offline" style="display:none">
			    <th>周期的第：</th>
			    <td><input name="serviceVO.cycleDay" id="cycleDay" type="text"/>日<span class="c_red">*</span></td>
			</tr>
			<tr name="online" style="display:none">
				<th>数据推送方式：</th>
				<td>
			    	<input type="radio" name="pushDataWay" value="1" ${fetVO.pushDataWay=="1"?"checked":"" }/><label>ftp</label>
					<input type="radio" name="pushDataWay" value="2" ${fetVO.pushDataWay=="2"?"checked":"" }/><label>webservice</label>
				</td>
			</tr>
			
		</table>
	</c:if>
	
	</form>

	<div class="main_info_title">
        		 合规检查规则配置
	</div>
	
	<div id="ruleCheckDiv">
		<div class="main_search" style="padding:10px 0 0 10px;margin-left:30px">
			<div class="main_list" style="padding:0 20px 0 20px">
						
			合规检查规则规范文件下载：
				<a href='system/file_downLoadFile.do?fileId=474'><span class="c_red">规则模板.xmls</span></a>
			<br/>
			<br/>			
		
			<c:if test="${auditStatus eq '0'}">
				<div style="margin-bottom:10px">
					已上传的合规检查规则文件：
				<a href="system/file_downLoadFile.do?fileId=${fetch.checkFileId}" target="_blank">
				<span class="c_red">${fileVO.realName}</span></a>
				</div>
			</c:if>
			
			
			<form id="uploadFile">
			合规检查规则文件：<input type="file" name="upload" style="width:250px"/>

			<input id="uploadButton" type="button" onclick='uploadExcel()' value="上传"/>
			<input id="uploadingButton" style='display:none;color:gray;'  type="button" onclick='uploadExcel()' value="上传中"/>
			
			</form>	
				<div id="uploadTable" style="padding:10px 10px 0 20px;margin-top:5px;" class="main_list" ></div>
			<div>
			
				</div>
		</div>
	</div>
	<div>
</div>
<div id="uploadTable" style="padding:10px 10px 0 20px;margin-top:5px;" class="main_list" ></div>
	<div>
	<c:if test="${auditStatus eq '0'}">
					<input id="reApplyServiceBtn" type="button" style='margin-left:30%'onclick='reApply()' value="重新申请"/>								
				</c:if>
				<c:if test="${empty auditStatus }">
					<input id="reApplyServiceBtn" type="button" style='margin-left:30%'onclick='reApply()' value="申请取数"/>								
				</c:if>
				<input id="applyingServiceBtn" type="button"  disabled style='display:none;color:gray;margin-left:30%'onclick='addAndApply()' value="申请中"/>											
				<input type="button" onclick='' value="返回"/>
</div>
</body>
</html>
