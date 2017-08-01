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
<title>新增服务_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script type="text/javascript">	
var uploadTimes = 0;
	function addAndApply(){
		
		if($("input[name='upload']").val()==''){
			alert("你还没配置规则文件，请先配置")
			return
		}
		if($("#cycleType").val() == '0'){
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
			$("#addServiceBtn,#addingServiceBtn").toggle();
			util.ajax($("#serviceForm").attr("action"),$("#serviceForm").serialize(),function(json){
				$("#addServiceBtn,#addingServiceBtn").toggle();
				if(json.state=='success'){
					alert("新增服务成功");
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
	  
	        	 uploadTimes+=1;
	        	 $("#uploadTable").empty();
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
	        	 var filoVO = datas.data.fileVo;
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
	        	if(uploadTimes>1){
	        		$("#uploaded").css("display","block");
		        	$("#uploaded span").text($("input[name='prefileName']").val());
		        	$("#uploaded a").attr("onclick","toPreRuleInfo("+$('#fileId').val()+")");
	        	}	      	  
	        	$("input[name='prefileName']").val(filoVO.realName);
	        	$("#fileId").val(filoVO.fileId);
				$("#uploadButton,#uploadingButton").toggle();

	         },  
	         error: function (data) {  
	       
	             alert("上传失败,请检查");  
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
	
	function toPreRuleInfo(fileId){
		location.href="system/workPlan_preRuleInfo.do?workPlanId=${planId}&serviceId=${serviceVO.serviceId}&fileId="+fileId+"&isModify=0"
	}
</script>
</head>	
<body>
	<div class="main_upload" method="post">
	<form id="serviceForm" name="serviceForm" action="system/desenModel_applyService.do">
		<input name="fileId" id="fileId" type ="hidden" value=""/>
		<input name="prefileName" id="prefileName" type ="hidden" value=""/>
		<input name="prefileId" id="prefileId" type ="hidden" value=""/>
        <div class="main_title">
            <b>新增服务并申请取数</b>
        </div>
		<div class="content_title">
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
			    <td><input name="serviceVO.serviceCode" id="serviceCode" type="text" maxlength="15"/><span class="c_red">*</span></td>
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
			    	<input type="radio" name="pushDataWay" value="1" /><label>ftp</label>
					<input type="radio" name="pushDataWay" value="2" /><label>webservice</label>
				</td>
			</tr>
			
		</table>
		
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
			
			<div  id="uploaded" style="margin-bottom:10px;display:none" >
				已上传的合规检查规则文件：
			<a style="cursor:pointer"onclick="" target="_blank">
			<span class="c_red" id="fileRealName"></span></a>
			</div> 
			
			<form id="uploadFile">
			合规检查规则文件：<input type="file" name="upload" style="width:300px"/>
			<input id="uploadButton" type="button" onclick='uploadExcel()' value="上传"/>
			<input id="uploadingButton" style='display:none;color:gray;' type="button" value="上传中"/>								
			</div>
			</form>							
				</div>
		</div>
	</div>
	<div id="uploadTable" style="padding:10px 10px 0 20px;margin-top:5px;" class="main_list" ></div>
	<div>
	<input id="addServiceBtn" type="button" style='margin-left:40%;margin-top:40px'onclick='addAndApply()' value="新增并申请"/>
				<input id="addingServiceBtn" type="button"  disabled style='display:none;color:gray;margin-left:30%'onclick='addAndApply()' value="新增中"/>					
				<input type="button" onclick='history.back(-1)' value="返回"/>
</div>

</body>
</html>
