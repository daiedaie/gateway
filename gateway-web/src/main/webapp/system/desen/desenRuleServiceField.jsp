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
<title>服务字段脱敏配置_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<link rel="stylesheet" href="resource/css/ui-dialog.css" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/form/jquery.form.3.51.0.js"></script>
<script type="text/javascript" src="resource/js/artDialog/dialog-min.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<style type="text/css">
.divImg{width:15px;height:12px;cursor:pointer}
</style>
<script type="text/javascript">	
	var fieldDict = ${fieldDict};
	var dictMap = ${dictMap};
	
	function loadDesenRule(){
		var checkRuleArray=new Array()
		var dictArray = new Array()
		var str ="";
		var checkRuleArray =$("td[name=checkRule]")
		for(var i=0;i<checkRuleArray.length;i++){
			var dictStr = $("td[name=checkRule]").eq(i).text();
			dictArray = dictStr.split(";");
			for(var n=0;n<dictArray.length;n++){
				str+="<input type='checkbox'  name='dictInput"+n+"' value='"+dictArray[n]+"'>"+dictArray[n]+"</input>"
			}
			$("td[id$=condition]").eq(i).append(str)
			str="";
		}
	}
	$(document).ready(function(){
		loadDesenRule();
		$("#inputRadio input").change(function() {			
			$("#ruleCheck,#desenCheck").toggle();
		}); 
		
		if("${fetch.desenType}"=="2"){
			$("#ruleCheck").show();
			$("#desenCheck").hide();
		}else{
			$("#ruleCheck").hide();
			$("#desenCheck").show();
		}
		$("#ruleCheckDiv").show();

	
		$("#mainServiceDiv tr[name=fieldTr]").each(function(){
			var fieldId = $(this).find("[name$=fieldId]").val();	//字段id
			eval("dictCode = fieldDict.fieldId" + fieldId);
			chooseDict(this,dictCode,true);	//设置字典默认值
			
			//设置脱敏类型的提示信息
			$(this).find("[name$=ruleType]").bind("change",function(){
				var value = $(this).val();
				var tr = $(this).parent().parent();
				if(value==''){
					$(tr).find("[name$=ruleContent],[name$=replaceContent]").attr("title","").attr("placeholder","");
					return false;
				}
				if(value == 1){
					$(tr).find("[name$=ruleContent]").attr("title","请输入需要替换的内容").attr("placeholder","请输入需要替换的内容");
				}else if(value == 2){
					$(tr).find("[name$=ruleContent]").attr("title","如：2,5 表示第二到第五个。5 表示从第五个到最后").attr("placeholder","如：2,5表示第二到第五个。5 表示从第五个到最后");
				}
				$(tr).find("[name$=replaceContent]").attr("title","请输入替换后的内容，可以为空").attr("placeholder","请输入替换后的内容，可以为空");
			});
		});
		
		$("#otherServiceDiv tr[name=fieldTr]").each(function(){	//同一模型其他服务的字典设置
			var fieldId = $(this).find("[name$=fieldId]").val();	//字段id
			eval("dictCode = fieldDict.fieldId" + fieldId);
			chooseDict(this,dictCode,true);	//设置字典默认值
		});
		$("#otherServiceDiv input,#otherServiceDiv select").attr("disabled",true);
		
		if("${search}"=='detailDesen'){
			$("#mainServiceDiv input[type=checkbox],#mainServiceDiv input[type=text],#mainServiceDiv select").attr("disabled",true);
			$("input[name=desenTypeRadio],input[name=upload],input[name='fetchVO.pushDataWay']").attr("disabled",true);
			$("#maxCheckNum,#checkAudit").attr("disabled",true);
		}
	});
	
	function chooseRuleDict(td,tr){
		 
		var str ="";
		var length = $(td).find("[type=checkbox]").size();
		for(i=0;i<length;i++){
			var inputVal = $(td).find("[name=dictInput"+i+"]:checked").val();
			if(!inputVal)continue;
			str=str+inputVal+";"
		}
		 $(tr).find("input[name$=conditionContent]").attr("value",str);
	}
	
	function chooseDict(tr,dictCode,isDefault){
		var objectName = $(tr).find("[name=objectName]").val();
		var fieldType = $(tr).find("[name=fieldType]").val().toUpperCase();
		var td = $(tr).find("[name=conditionTd]");	//当前行的td条件区
		td.empty();
		//条件默认值
		var conditionType = $(tr).find("[name=conditionTypeDefault]").val();
		var conditionContent = $(tr).find("[name=conditionContentDefault]").val();
		if(dictCode && dictCode != ''){//关联字典dictCode
			eval("dictList = dictMap." + dictCode); 	//根据字段关联的dictCode得到字典列表
			if(dictList && dictList.length>0){
				var objectName = $(tr).find("[name=objectName]").val();	//当前行的objectName，作为动态生成元素的对象名，保证与同行的对象名一样
				$(tr).find("[name$=dictCode] option[value="+dictCode+"]").attr("selected",true);	//根据fieldId关联的字典，设置字典默认选中
				var defaultDictCode = $(tr).find("[name$=dictCode]").attr("defaultDictCode");
				if(isDefault){	//初始化时，设置默认值属性
					$(tr).find("[name$=dictCode]").attr("defaultDictCode",dictCode);
					defaultDictCode = dictCode;
				}
			
				td.append("<input type='hidden' name='"+objectName+".conditionType' value='in'/>");
				for(i=0;i<dictList.length;i++){
					td.append("<label><input type='checkbox' name='"+objectName+".conditionContent' value='"+dictList[i].dictKey+"'/>"+dictList[i].dictValue+"</label>");
				}
			}
			//切换字典下拉框后，需要和原始默认值相同才给字典选中
			if(defaultDictCode==dictCode && conditionContent!='' && conditionType=='in'){
				var conditionContents = conditionContent.split(",");
				for(i=0;i<conditionContents.length;i++){
					$(tr).find("[type=checkbox][name$=conditionContent][value='"+conditionContents[i]+"']").attr("checked",true);
				}
			}
		}else if(fieldType=='INTEGER' || fieldType=='INT' || fieldType=='LONG' || fieldType=='NUMBER' || fieldType=='DOUBLE'){	//没关联字典，且为数据类型
			var html = "<select name='"+objectName+".conditionType'>"+
   					   "<option value=''>请选择</option>"+
   					   "<option value='='>=</option>"+
   					   "<option value='!='>!=</option>"+
   					   "<option value='>'>></option>"+
   					   "<option value='>='>>=</option>"+
   					   "<option value='<'><</option>"+
   					   "<option value='<='><=</option>"+
   					   "</select>";
			html += "<input type='text' name='"+objectName+".conditionContent' value='' style='width:100px' maxlength='20' placeholder='只能输入数字'/>";
			td.append(html);
  				
			//设置默认值
			if(conditionContent != '' && conditionType!='in'){
				$(tr).find("select[name$=conditionType] option[value='"+conditionType+"']").attr("selected",true);
	   			$(tr).find("[type=text][name$=conditionContent]").val(conditionContent);
			}
		}
	}
	
	function desenRuleCheck(data){
		 
		if("${service.serviceSource}" == "2"){
			return true;
		}

		if($("[name$=fieldId]:checked").length !=  data.length){
			alert("合规规则表字段数目必须与挖掘平台已勾选的字段数目相等")
			$("input[name=upload]").val("");
			return false;
		}
		for(i=0;i<data.length;i++){
			
			if(data[i].fieldName !=  $("td input[name$=fieldId]:checked").parent().next().eq(i).text()){
				alert("合规规则表字段名称必须与挖掘平台字段名称相同");
				$("input[name=upload]").val("");
				return false;
			}
			if(data[i].fieldType != $("td input[name$=fieldId]:checked").parent().next().next().next().eq(i).text()){
				alert("合规规则表字段类型必须与挖掘平台字段类型相同");
				$("input[name=upload]").val("");
				return false;
			}
		}
		return true
	}
	//合规
	function uploadExcel2(){
	
		$("#uploadButton2").hide()
		$("#uploadingButton2").show();
		if(!ruleCheck2()) return;
		var formData = new FormData($("#desenRuleForm")[0]);
	    $.ajax({  
	         url: "system/desenModel_showRuleFile.do", 
	         type: 'POST',  
	         data: formData,  
	         cache: false,  
	         contentType: false,  
	         processData: false,  
	         success: function (data) {
	       
	        	 $("#uploadButton2").show()
	     		 $("#uploadingButton2").hide();
	        	 $("#ruleCheckTable2").empty();
	        	 var RuleList=new Array();
	        	 var checkDictMap=new Array();
	        	 var datas = JSON.parse(data);
				 if(datas.state =="error"){
	        		 
	        		 $("#uploadButton2,#uploadingButton2").toggle();
	        		 alert(datas.message);
	        		 $("input[name=upload]").val("");
	        		 return;
	        	 }
	        	 var RuleList= datas.data.checkRuleList;	
			     $("#ruleLength").attr("value",RuleList.length);
	        	 $("#fileId").val(datas.data.fileVo.fileId);
	        	 var checkDictMap = datas.data.checkDictMap;
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
	 					str+="<tr name='ruleFieldTr'>"+
	 							"<td>"+ele.fieldCode+"</td>"+
	 							"<td>"+ele.fieldName+"<input type='hidden' name='fieldName' value="+ele.fieldName+"/></td>"+
	 							"<td>"+ele.fieldType+"<input type='hidden' name='fieldType' value="+ele.fieldType+"/></td>"+
	 							"<td>"+checkDictMap[ele.checkType].dictValue+"</td>"+
	 							"<td name='checkRule'>"+ele.checkRule+"</td>"+
	 						 "</tr>";
	 				})
	        	 }				
				str+="</table>";
	        	$("#ruleCheckTable2").append(str);
	        	//updateDesenRule(RuleList);
	        	//$("#uploadedName2").text(datas.data.fileVo.realName);
	        	$("#fileId").val(datas.data.fileVo.fileId);
	         },  
	         error: function (returndata) {  
	     		$("#uploadButton2","#uploadingButton2").toggle();

	             alert("上传失败");  
	         }  
	    });  
	}
	
	//108的脱敏并合规
	function uploadExcel(){
		 
		$("#uploadButton").hide()
		$("#uploadingButton").show();
		
		if(!ruleCheck()) return;
		var formData = new FormData($("#desenRuleForm")[0]);
	    $.ajax({  
	         url: "system/desenModel_showRuleFile.do", 
	         type: 'POST',  
	         data: formData,  
	         cache: false,  
	         contentType: false,  
	         processData: false,  
	         success: function (data) {
	        	 $("#uploadButton").show()
	     		 $("#uploadingButton").hide();
	        	 $("#ruleCheckTable").empty();
	        	 var RuleList=new Array();
	        	 var checkDictMap=new Array();
	        	 var datas = JSON.parse(data);
                 if(datas.state =="error"){	        		 
	        		 $("#uploadButton,#uploadingButton").toggle();
	        		 alert(datas.message);
	        		 $("input[name=upload]").val("");
	        		 return;
	        	 }
	        	 var RuleList= datas.data.checkRuleList;	 
				 
	        	 if(!(RuleList))return;
	        	 $("#fileId").val(datas.data.fileVo.fileId);
				 				 $("#ruleLength").attr("value",RuleList.length);

	        	 var checkDictMap = datas.data.checkDictMap;
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
	 					str+="<tr name='ruleFieldTr'>"+
	 							"<td>"+ele.fieldCode+"</td>"+
	 							"<td>"+ele.fieldName+"<input type='hidden' name='fieldName' value="+ele.fieldName+"/></td>"+
	 							"<td>"+ele.fieldType+"<input type='hidden' name='fieldType' value="+ele.fieldType+"/></td>"+
	 							"<td>"+checkDictMap[ele.checkType].dictValue+"</td>"+
	 							"<td name='checkRule'>"+ele.checkRule+"</td>"+
	 						 "</tr>";
	 				})
	        	 }				
				str+="</table>";
	        	$("#ruleCheckTable").append(str);
	        	//$("#uploadedName").text(datas.data.fileVo.realName);
	        	$("#fileId").val(datas.data.fileVo.fileId);
	        	updateDesenRule(RuleList);

	         },  
	         error: function (returndata) {  
	     		$("#uploadButton","#uploadingButton").toggle();

	             alert("上传失败");  
	         }  
	    });  
		
	}
	//wajue的脱敏并合规
	function uploadExcel3(){
		
		$("#uploadButton").hide()
		$("#uploadingButton").show();
		
		if(!ruleCheck()) return;
		var formData = new FormData($("#desenRuleForm")[0]);
	    $.ajax({  
	         url: "system/desenModel_showRuleFile.do", 
	         type: 'POST',  
	         data: formData,  
	         cache: false,  
	         contentType: false,  
	         processData: false,  
	         success: function (data) {
	        	 $("#uploadButton").show()
	     		 $("#uploadingButton").hide();
	        	 $("#ruleCheckTable").empty();
	        	 var RuleList=new Array();
	        	 var checkDictMap=new Array();
	        	 var datas = JSON.parse(data);
                 if(datas.state =="error"){	        		 
	        		 $("#uploadButton,#uploadingButton").toggle();
	        		 alert(datas.message);
	        		 $("input[name=upload]").val("");
	        		 return;
	        	 }
	        	 var RuleList= datas.data.checkRuleList;
				 $("#ruleLength").attr("value",RuleList.length);
	        	 if(!desenRuleCheck(RuleList))return;
	        	 $("#fileId").val(datas.data.fileVo.fileId);
	        	 var checkDictMap = datas.data.checkDictMap;
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
	 					str+="<tr name='ruleFieldTr'>"+
	 							"<td>"+ele.fieldCode+"</td>"+
	 							"<td>"+ele.fieldName+"<input type='hidden' name='fieldName' value="+ele.fieldName+"/></td>"+
	 							"<td>"+ele.fieldType+"<input type='hidden' name='fieldType' value="+ele.fieldType+"/></td>"+
	 							"<td>"+checkDictMap[ele.checkType].dictValue+"</td>"+
	 							"<td name='checkRule'>"+ele.checkRule+"</td>"+
	 						 "</tr>";
	 				})
	        	 }				
				str+="</table>";
	        	$("#ruleCheckTable").append(str);
	        	//$("#uploadedName").text(datas.data.fileVo.realName);
	        	$("#fileId").val(datas.data.fileVo.fileId);
	        	//updateDesenRule(RuleList);

	         },  
	         error: function (returndata) {  
	     		$("#uploadButton","#uploadingButton").toggle();

	             alert("上传失败");  
	         }  
	    });  
		
	}
	
	function updateDesenRule(RuleList){
		 $("#desenRule").empty();
		 var str ="";
    	 str ="<table width='100%' border='0'>"+
		        	 "<tr>"+
						"<th>选择</th>"+
						"<th>字段名称</th>"+
						"<th>字段类型</th>"+
						"<th>脱敏类型</th>"+
						"<th>处理定位</th>"+
						"<th>替换字符</th>"+
						"<th>字典条件</th>"+
					  "</tr>";
    	 if(RuleList == null||RuleList.size == 0){
    		 str ="<tr><td colspan=9>查询不到该服务的字段信息</td></tr>";
    	 }else{
    		 $.each(RuleList,function(i,ele){
					str+="<tr name='fieldTr'>"+
							"<td>"+
				  	  		"<input type='hidden' name='objectName'  value='desenFieldList["+i+"]'/>"+   	
				  	  		"<input type='checkbox' name='desenFieldList["+i+"].fieldId' value='-1'/>"+
				  	  		"<input type='hidden' name='desenFieldList["+i+"].userId' value='${fetch.userId }'/>"+
				  	  		"<input type='hidden' name='desenFieldList["+i+"].serviceId' value='${fetch.serviceId }'/>"+
				  	  		"<input type='hidden' name='desenFieldList["+i+"].conditionContent' />"+
				  	  		"<input type='hidden' name='desenFieldList["+i+"].conditionType'/>"+
				  	  		"</td>"+
					   		"<td>"+ele.fieldName+"<input type='hidden' name='fieldName' value="+ele.fieldName+"/></td>"+
					    	"<td>"+ele.fieldType+"<input type='hidden' name='fieldType' value="+ele.fieldType+"/></td>"+
					    	'<td><g:sysDictList dictCode='DICT_DESEN_RULE_TYPE' tagType='select' tagName="desenFieldList['+i+'].ruleType" tagId="ruleType'+i+'"/></td>'+
					    	"<td><input type='text' id='ruleContent"+i+"' name='desenFieldList["+i+"].ruleContent' value='' style='width:100px'/></td>"+
					    	"<td><input type='text' id='replaceContent"+i+"' name='desenFieldList["+i+"].replaceContent' value='' style='width:100px'/></td>"+
					    	"<td id='desenFieldList["+i+"].condition'onchange='chooseRuleDict(this,this.parentNode)'>"+					    	
						    "</td>"+
						 "</tr>";
			})
    	 }				
		str+="</table>";
    	$("#desenRule").append(str);
    	$("#desenRule tr[name=fieldTr]").each(function(){
			var fieldId = $(this).find("[name$=fieldId]").val();	//字段id
			eval("dictCode = fieldDict.fieldId" + fieldId);
			chooseDict(this,dictCode,true);	//设置字典默认值
			
			//设置脱敏类型的提示信息
			$(this).find("[name$=ruleType]").bind("change",function(){
				var value = $(this).val();
				var tr = $(this).parent().parent();
				if(value==''){
					$(tr).find("[name$=ruleContent],[name$=replaceContent]").attr("title","").attr("placeholder","");
					return false;
				}
				if(value == 1){
					$(tr).find("[name$=ruleContent]").attr("title","请输入需要替换的内容").attr("placeholder","请输入需要替换的内容");
				}else if(value == 2){
					$(tr).find("[name$=ruleContent]").attr("title","如：2,5 表示第二到第五个。5 表示从第五个到最后").attr("placeholder","如：2,5表示第二到第五个。5 表示从第五个到最后");
				}
				$(tr).find("[name$=replaceContent]").attr("title","请输入替换后的内容，可以为空").attr("placeholder","请输入替换后的内容，可以为空");
			});
		});
    	loadDesenRule();
	}
	//不同意创建服务
	function ruleNotPass(workPlanId,serviceId,userId){
		 if(confirm("确定要不同意创建服务？")){
			 var sendValue = "serviceId="+serviceId+"&userId="+userId+"&workPlanId="+workPlanId;	    
				$.ajax({
				    type:"POST",
					url: "system/desenModel_ruleNotPass.do",
				    data:sendValue,				
				    success: function(data){
						goBack();
					},
					error:function(d){
						alert(d);
					}
				});
		 }			    
	}
	
	function updateDesenRuleServiceField(){
			
		if($("input[name=desenTypeRadio]:checked").val()=="1"){
			
			if($("input[name$=fieldId]:checked").length != $("#ruleLength").val()){
				alert("合规规则表字段数目必须与挖掘平台已勾选的字段数目相等");
				return false;
			}
			if(!check()){
				return false;
			}
		}
		
		$("#mainServiceDiv input[type!=file]").each(function(){
			$(this).val($.trim($(this).val()));
		});
	
		if($("#maxCheckNum").val()==''){
			alert("请输入不合规最大检查行数！");
			$("#maxCheckNum").focus();
			return false;
		}
		if(isNaN($("#maxCheckNum").val())){
			alert("不合规最大检查行数只能为数字！");
			$("#maxCheckNum").focus();
			return false;
		}
		if(parseFloat($("#maxCheckNum").val()) < 0){
			alert("不合规最大检查行数必须大于或等于零！");
			$("#maxCheckNum").focus();
			return false;
		}
		
		if($("#outputNum").val()==''){
			alert("请输入合规文件输出行数！");
			$("#outputNum").focus();
			return false;
		}
		if(isNaN($("#outputNum").val())){
			alert("合规文件输出行数只能为数字！");
			$("#outputNum").focus();
			return false;
		}
		if(parseFloat($("#outputNum").val()) < 0){
			alert("合规文件输出行数必须大于或等于零！");
			$("#outputNum").focus();
			return false;
		}
		
		//var desenType = $("input[name='fetchVO.desenType']:checked").val();

		//if(desenType=='1' && !check()) return false;		//脱敏并合规检查，只检查脱敏规则
		//else if(desenType=='2' && !ruleCheck()) return false;	//合规检查，只检查合规检查规则文件
		var Type = $("input[name=desenTypeRadio]:checked").val();
		
		$("input[name$=desenType]").val(Type);
		if(Type=='2'){
			if(!ruleCheck2()){
				return false;
			}
		}
		if(Type=='1'){
			if(!check()||!ruleCheck()){
				return false;
			}
		}
		$("#updateDesenBtn,#updateDesenBtning").toggle();
		util.ajax("system/desenModel_searchTaskCheckCount.do",{"userId":$("#userId").val(),"serviceId":$("#serviceId").val()},function(json){
			var checkCount = json.data.checkCount;
			var auditCount = json.data.auditCount;
			if(checkCount=='0' && auditCount=='0'){
				updateDesenConfig();
			}else{
				var content= '当前正在检查数：'+json.data.checkCount+'个，&nbsp;&nbsp;&nbsp; 等待审核数：'+json.data.auditCount+'个<p/><br/><label><input type="radio" name="updateType" value=1>立即生效</label>&nbsp;<label><input type="radio" name="updateType" value=2>下次生效</label>';
				var d = dialog({
				    title: '是否立即生效',
				    content: content,
				    height:50,
				    width:300,
				    ok: function () {
				        $("#ruleUpdateType").val($("input[name='updateType']:checked").val());
				        if($("#ruleUpdateType").val()==''){
				        	alert("请选择是否立即生效");
				        	return false;
				        }
				        updateDesenConfig();
				        this.close();
				        this.remove();
				    },
				    cancel: function () {
				    	$("#updateDesenBtn,#updateDesenBtning").toggle();
				        this.close();
				        this.remove();
				    }
				});
				d.show();
			}
		});
	}
	
	function updateDesenConfig(){
		$("#desenRuleForm").ajaxSubmit({
			type: "POST",
			url: "system/desenModel_updateDesenRuleServiceField.do",
			dataType:"json",
			success: function(data){
				$("#updateDesenBtn,#updateDesenBtning").toggle();
				if(data.state=='success'){
					alert("保存成功，等待审核人员审核");
					goBack();
				}else{
					alert(data.message);
				}
			},
			error:function(d){
				alert(d);
			}
	   	});
	}
	
	//合规检查，js验证
	function ruleCheck2(){
		if($("#upload").val() !=''){
		    if(confirm("你已在规则配置类型的脱敏并合规上存了合规检查规则表，是否要重新上传？")){
				$("#upload").val("");			
				$("#uploadButton2").show()
				$("#uploadingButton2").hide();
		    }else{
				$("#upload2").val("");
				$("#uploadButton2").show()
				$("#uploadingButton2").hide();
				return false; 
			}
		}			
		
		if($("input[id='upload2']").val() !='' || "${fetch.checkFileId}"!=''){
/* 			if(!confirm("已存在合规规则文件，确定重新上传？")){
				return false;
			} */
		}
		if($("input[id='upload2']").val()=='' && "${fetch.checkFileId}"==''){
			alert("请选择合规检查的规则文件！");
			$("input[id='upload2']").focus();
			return false;
		}
		 
		if (!/\.(xlsx|xls)$/.test($("input[id='upload2']").val())&&!"${fetch.checkFileId}") {
			alert("合规检查的规则文件只能为Excel文件！");
			$("input[name='upload2']").focus();
			return false;
		}
		
		return true;
	}
	
	function ruleCheck(){
		if($("#upload2").val() !=''){
			 if(confirm("你已在规则配置类型的合规检查上存了合规检查规则表，是否要重新上传？")){
				$("#upload2").val("");
				$("#uploadButton2").show()
				$("#uploadingButton2").hide();
			 }else{
				$("#upload").val("");
				$("#uploadButton2").show()
				$("#uploadingButton2").hide();
				return false; 
			}	
		}
		
		if($("input[name='upload']").val() !='' || "${fetch.checkFileId}"!=''){
/* 			if(!confirm("已存在合规规则文件，确定重新上传？")){
				return false;
			} */
		}
	
		if($("input[name='upload']").val()==''&&$("#uploadedName").text()==''){
			alert("请选择合规检查的规则文件！");
			$("input[name='upload']").focus();
			return false;
		}
		 
		if (!/\.(xlsx|xls)$/.test($("input[name='upload']").val())&&$("#uploadedName").text()=='') {
			alert("合规检查的规则文件只能为Excel文件！");
			$("input[name='upload']").focus();
			return false;
		}
		return true;
	}
	
	//脱敏并合规检查，js验证
	function check(){
		 
		if($("#upload2").val() !=''){
			if(confirm("你已在规则配置类型的合规检查上存了合规检查规则表,是否重新上传？")){
				$("#upload2").val()=='';
			}else{
				$("#upload").val()=='';
				return false;
			}			
		}
		
		if($("input[name$=fieldId]:checked").length==0){
			alert("您未选择字段，无法保存");
			return false;
		}
	
		$("input[type=text]").each(function(){
		 	$(this).val($.trim($(this).val()));
		});
		
		var check = true;
		$("#mainServiceDiv tr[name=fieldTr]").each(function(){
			var ruleType = $(this).find("[name$=ruleType]").val();
			var ruleContent = $(this).find("[name$=ruleContent]").val();
			var fieldName = $(this).find("[name$=fieldName]").val();
			var fieldType = $(this).find("[name$=fieldType]").val().toUpperCase();
			
			if(ruleContent != '' && ruleType==''){
				alert(fieldName+"字段的处理定位内容不为空时，脱敏类型选项为必选");
				check = false;
				return false;
			}
			if(ruleType == 1 && ruleContent==''){	//字符替换
				alert("您选择的脱敏类型为字符替换，处理定位中请输入需要替换的字符");
				$(this).find("[name$=ruleContent]").focus();
				check = false;
				return false;
			}else if(ruleType == 2){	//范围内替换
				if(ruleContent==''){
					alert("您选择的脱敏类型为范围内替换，处理定位中请输入需要替换的范围");
					$(this).find("[name$=ruleContent]").focus();
					check = false;
					return false;
				}
				ruleContent = ruleContent.replace("，",",");
				$(this).find("[name$=ruleContent]").val(ruleContent)
				var ruleContents = ruleContent.split(",");
				if(ruleContents.length>2){
					alert("您选择的脱敏类型为范围内替换，处理定位中值不正确");
				 	$(this).find("[name$=ruleContent]").select();
					check = false;
					return false;
				}
				for(i=0;i<ruleContents.length;i++){
					if(ruleContents[i]==''){
					 	alert("逗号前后不能有空值");
					 	$(this).find("[name$=ruleContent]").select();
						check = false;
						return false;
					}else if(ruleContents[i] && isNaN(ruleContents[i])){
						alert("您选择的脱敏类型为范围内替换，处理定位中只能输入数字和逗号");
						$(this).find("[name$=ruleContent]").select();
						check = false;
						return false;
					}
				}
				if(ruleContents.length==2 && parseFloat(ruleContents[1])<=parseFloat(ruleContents[0])){
					alert("您选择的脱敏类型为范围内替换，处理定位中结束位置不能小于等于开始位置");
				 	$(this).find("[name$=ruleContent]").select();
					check = false;
					return false;
				}
			}
			var conditionType = $(this).find("[name$=conditionType]").val();
			var conditionContent = $(this).find("[name$=conditionContent]").val();

			if(conditionType=='in'){
				conditionContent = $(this).find("[name$=conditionContent]:checked").val();
			}
			if((fieldType=='INTEGER' || fieldType=='INT' || fieldType=='LONG' || fieldType=='NUMBER' || fieldType=='DOUBLE') && conditionType!='in' && conditionContent && conditionContent != '' && isNaN(conditionContent)){
				alert(fieldName+"字段为数值类型，条件只能输入数字");
				$(this).find("[name$=conditionContent]").select();
				check = false;
				return false;
			}
			if($("input[name=desenTypeRadio]:checked").val()!="1"){//挖掘平台才有
				if(conditionContent!='' && conditionType==''){
					alert(fieldName+"字段的过滤条件不为空时，条件类型选项为必选");
					$(this).find("[name$=conditionContent]").focus();
					check = false;
					return false;
				}
				if(conditionContent=='' && conditionType!=''&& conditionType!=undefined){
					alert(fieldName+"字段的条件类型选项不为空时，过滤条件为必填");
					$(this).find("[name$=conditionContent]").focus();
					check = false;
				return false;
				}
			}
		 
			if(((conditionContent && conditionContent != '') || ruleContent!='') && !$(this).find("[name$=fieldId]:checked").val() && !$(this).find("[name$=conditionContent]").val() ){
				
				if(!confirm(fieldName+"字段的处理定位内容或过滤条件不为空，但该字段没有选中，脱敏设置将不起作用。确定？")){
					check = false;
					return false;
				}
			}
			
			if(ruleContent!='' && !$(this).find("[name$=fieldId]:checked").val() && !$(this).find("[name$=conditionContent ]").val() ){				
				if(!confirm(fieldName+"字段的处理定位内容或过滤条件不为空，但该字段没有选中，脱敏设置将不起作用。确定？")){
					check = false;
					return false;
				}
			}
		})
		return check;
	}
	
	function goBack(){
		if($("#source").val()=="1"||$("#source").val()=="2"){
			location.href="system/workPlan_searchByParam.do";
		}else{
			location.href="system/desenModel_searchDesenServiceFieldList.do";
		}
	}
	
	function showOtherService(div,openImg,closeImg){
		$("#"+div+",#"+openImg+",#"+closeImg).toggle();
	}
</script>
</head>	
<body>
<div id="mainServiceDiv">
<form id="desenRuleForm" name="desenRuleForm" method="post" enctype="multipart/form-data">
<input type="hidden" name="workPlanId" value="${workPlanId }"/>
<input type="hidden" name="fetchVO.fetchId" value="${fetchId }"/>
<input type="hidden" id="source" value="${source }"/>
<input type="hidden" name="ruleUpdateType" id="ruleUpdateType" value=""/>
<input type="hidden" name="fetchVO.userId" id="userId" value="${fetch.userId }"/>
<input type="hidden" name="fetchVO.serviceId" id="serviceId" value="${fetch.serviceId }"/>
<input type="hidden" name="fetchVO.checkFileId" id="checkFileId" value="${fetch.checkFileId }"/>
<input name="fileId" id="fileId" type ="hidden" value=""/>
<input name="fetchVO.desenType" value=""  id="fetchVO.desenType" type ="hidden"/>
<input name="preFileId" id="preFileId" value="${fetch.checkFileId}" type="hidden"/>
<input name="ruleLength" id="ruleLength" value="${ruleLength}" type="hidden">
<div class="main_title">
	<b>服务字段脱敏配置</b>
</div>
<c:if test="${preWorkPlanVO != null}">
<div class="main_info_title">审核信息</div>
	<table width="100%" border="0">
		 <tr>
		    <td width="10%" align="right">创建人：</td>
		    <td width="90%" align="left">${createUser.loginName}</td>
		 </tr>
		 <tr>
		    <td width="10%" align="right">创建时间：</td>
		    <td width="90%" align="left">${preWorkPlanVO.createTime }</td>
		  </tr>
		 <tr>
		    <td width="10%" align="right">任务标题：</td>
		    <td width="90%" align="left">${preWorkPlanVO.planTitle }</td>
		 </tr>
		 <tr>
		    <td width="10%" align="right">任务内容：</td>
		    <td width="90%" align="left">${preWorkPlanVO.planContent }</td>
		  </tr>
		  <tr>
		    <td width="10%" align="right">审核状态：</td>
		    <td width="90%" align="left"><g:sysDict dictCode="DICT_PLAN_STATE" dictKey="${preWorkPlanVO.planState }"/></td>
		  </tr>
		 <tr>
		    <td width="10%" align="right">审核意见：</td>
		    <td width="90%" align="left"><textarea id="suggestion" name="workPlan.suggestion" rows="6" cols="100" readonly>${preWorkPlanVO.suggestion }</textarea></td>
		 </tr>
     </table>
</c:if>
<div class="main_search">
	<p>
	服务编码：${service.serviceCode } &nbsp;&nbsp; 服务名称：${service.serviceName } &nbsp;
	<c:if test="${model != null }"> 
	模型名称：${model.modelName } &nbsp; 模型编码：${model.modelCode } &nbsp;&nbsp;
	</c:if>
	用户编码：${user.loginName } &nbsp; 用户名称：${user.userName }
	</p>
</div>

<div class="main_info_title" style="margin:0 0 0 0">合规检查设置</div>
<div class="main_search" style="padding:5px 0 5px 10px">
	<span style="color:red">*</span>不合规最大检查行数：<input type="text" name="fetchVO.maxCheckNum" id="maxCheckNum" value="${fetch.maxCheckNum==null?0:fetch.maxCheckNum}" style="width:120px"/>&nbsp;&nbsp;&nbsp;
	合规检查中的不合规数据最大检查行数，超过不再继续检查。0 表示不管不合规的行数有多少，都需要全部扫描完文件
	<br /><div style="height:8px"></div>
	<span style="color:red">*</span>合规文件输出行数：<input type="text" name="fetchVO.outputNum" id="outputNum" value="${fetch.outputNum==null?0:fetch.outputNum}" style="width:120px"/>&nbsp;&nbsp;&nbsp;
	合规检查中的合规文件的输出行数。0 表示全部输出
	<br /><div style="height:8px"></div>
	合法文件是否需要审核：<label style="vertical-align:middle; margin-bottom:36px; "><input type="checkbox" name="fetchVO.checkAudit" id="checkAudit" value="1" ${fetch.checkAudit==1?"checked":"" }/>需要</label>&nbsp;&nbsp;&nbsp;
	文件检查通过后，也需要管理员审核通过后数据用户才能下载
</div>
<br/>
<div id ="inputRadio">
	<lable>&nbsp;规则配置类型：       </lable>
	<c:if test="${fetch.desenType == null}">
	<input type="radio" name="desenTypeRadio" value="2" />合规检查</label>
 	<input type="radio" name="desenTypeRadio" value="1" checked />脱敏并合规检查</label>
	</c:if>
	
	<input type="radio" name="desenTypeRadio" value="2" <c:if test="${fetch.desenType != null && fetch.desenType =='2'}">checked</c:if> />合规检查</label>
	<input type="radio" name="desenTypeRadio" value="1" <c:if test="${fetch.desenType != null && fetch.desenType =='1'}">checked</c:if> />脱敏并合规检查</label>
	
</div>

<div ${service.serviceType=="1"?"style='display:none'":"" }>
	<lable>&nbsp;数据推送方式：       </lable>
	<input type="radio" name="fetchVO.pushDataWay" value="1" ${fetch.pushDataWay=="1"?"checked":"" } />ftp</label>
	<input type="radio" name="fetchVO.pushDataWay" value="2" ${fetch.pushDataWay=="2"?"checked":"" } />webservice</label>
</div>
<br/>
<c:if test="${ service.serviceSource=='1'}">
<div id="desenCheck">
	<!-- 挖掘的脱敏并合规检查开始 -->
	
	<div class="main_info_title" style="margin:5px 0 0 0">
		<img src="resource/images/open.png" title="展开" onclick="showOtherService('ruleCheckDiv','showRuleCheckImg','closeRuleCheckImg')" id="showRuleCheckImg" style="display:none;" class="divImg"/>
		<img src="resource/images/close.png" title="收缩" onclick="showOtherService('ruleCheckDiv','showRuleCheckImg','closeRuleCheckImg')" id="closeRuleCheckImg" class="divImg"/>
		<label>合规检查</label>
	</div>
	<div id="ruleCheckDiv">
		<div class="main_list" style="padding:5px 0 0 10px;">
			合规检查规则规范文件下载：		
			<a href='system/file_downLoadFile.do?fileId=474'><span class="c_red">规则模板.xmls</span></a>
			<br/>
			<br/>
				已上传的合规检查规则文件：
					<a id="down2" href="system/file_downLoadFile.do?fileId=${fetch.checkFileId}" target="_blank">
					<span class="c_red" id ="uploadedName">
					<c:if test="${fetch.desenType == 1}">
						${fileVo.realName}
					</c:if></span></a>			
				<br/>
			<div style="margin-bottom:10px">		
			<br/>
			合规检查规则文件：<input id="upload" type="file" name="upload" style="width:250px"/>
			<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['ruleDesen']!=null && search != 'detailDesen'}">
			<input id="uploadButton" type="button" onclick='uploadExcel3()' value="上传"/>
			<input id="uploadingButton" style='display:none;color:gray;' type="button"  value="上传中"/>	
			</c:if>	
			</div>
			<div id="ruleCheckTable" >
			<c:if test="${fetch.desenType == 1}">
					<table width="100%" border="0">
					  <tr>
						<th>字段编码</th>
						<th>字段名称</th>
						<th>字段类型</th>
						<th>检查类型</th>
						<th>检查规则</th>
					  </tr>
					  <c:forEach var="o" items="${checkRuleList}" varStatus="status">
						<tr name="ruleFieldTr">
							<td>${o.fieldCode }</td>
							<td>${o.fieldName }<input type="hidden" name="fieldName" value="${o.fieldName }" />
							</td>
							<td>${o.fieldType }<input type="hidden" name="fieldType" value="${o.fieldType }" />
							</td>
							<td><g:sysDict dictCode="DICT_CHECK_RULE_TYPE" dictKey="${o.checkType}"/>
							</td>
							<td name="checkRule">${o.checkRule}</td>
						</tr>
					  </c:forEach>
					</table>
			</c:if>	
			</div>
		</div>
	</div>
	
	
	<div class="main_info_title" style="margin:0 0 0 0">
	<img src="resource/images/open.png" title="展开" onclick="showOtherService('desenCheckDiv','showDesenCheckImg','closeDesenCheckImg')" id="showDesenCheckImg" style="display:none;" class="divImg"/>
	<img src="resource/images/close.png" title="收缩" onclick="showOtherService('desenCheckDiv','showDesenCheckImg','closeDesenCheckImg')" id="closeDesenCheckImg" class="divImg"/>
	<label>脱敏并合规检查</label></div>
	<div id="desenCheckDiv">
	<div class="main_search" style="padding-top:5px">
		<span style="color:red;">
		一、脱敏类型使用说明：<br />
		1.范围内替换(根据处理定位中的位置范围替换为替换字符中的内容，如：2,5表示将第二到第五的内容替换为替换字符中的内容，5表示从第五个到最后)<br />
		2.字符替换(将处理定位中的内容替换为替换字符字段中的内容) 
		<br/>
		二、条件使用说明：<br />
		选择字典后,根据字典过滤数据，如省份、地市。没选字典且字段类型为数字类型可以配置数值判断条件
		</span>
	</div>
	<div class="main_list" style="padding:5px 0 0 10px;">
	<table width="100%" border="0">
	  <tr>
	    <th>选择</th>
	    <th>字段编码</th>
	    <th>字段名称</th>
	    <th>字段类型</th>
	    <th>脱敏类型</th>
	    <th>处理定位 </th>
	    <th>替换字符 </th>
	    <th>字典 </th>
	    <th>条件 </th>
	  </tr>
	  <c:forEach var="v" items="${ruleViewList}" varStatus="vs">
	  	  <tr name="fieldTr">
	  	  	<td>
	  	  		<!-- conditionTd -->
	  	  		<input type="hidden" name="objectName"  value="desenFieldList[${vs.index}]"/>
	  	  		<input type="hidden" name="conditionTypeDefault" id="conditionType${vs.index}" value="${v.conditionType}"/>
		    	<input type="hidden" name="conditionContentDefault" id="conditionContent${vs.index}" value="${v.conditionContent}"/>
		    	
		    	<!-- desenType==1为脱敏并检查，才有选中 -->
		    	<c:if test="${fetch.desenType=='1'}">
	  	  			<input  type="checkbox" name="desenFieldList[${vs.index}].fieldId" value="${v.fieldId }" ${v.fieldDeseId+0==0?"":"checked=true" }/>
	  	  		</c:if>
	  	  		<c:if test="${fetch.desenType!='1'}">
	  	  			<input type="checkbox" name="desenFieldList[${vs.index}].fieldId" value="${v.fieldId }"/>
	  	  		</c:if>
	  	  		<input type="hidden" name="desenFieldList[${vs.index}].fieldDeseId" value="${v.fieldDeseId }"/>
	  	  		<input type="hidden" name="desenFieldList[${vs.index}].userId" value="${v.userId }"/>
	  	  		<input type="hidden" name="desenFieldList[${vs.index}].serviceId" value="${v.serviceId }"/>
	  	  	</td>
		    <td>${v.fieldCode }<input type="hidden" name="desenFieldList[${vs.index}].fieldCode" value="${v.fieldCode }"/></td>
		    <td>${v.fieldName }<input type="hidden" name="fieldName" value="${v.fieldName }"/></td>
		    <td>${v.fieldType }<input type="hidden" name="fieldType" value="${v.fieldType }"/></td>
		    <td><g:sysDictList dictCode="DICT_DESEN_RULE_TYPE" defaultValue="${v.ruleType}" tagType="select" tagName="desenFieldList[${vs.index}].ruleType" tagId="ruleType${vs.index}"/></td>
		    <td><input type="text" id="ruleContent${vs.index}" name="desenFieldList[${vs.index}].ruleContent" value="${v.ruleContent }" style="width:100px"/></td>
		    <td><input type="text" id="replaceContent${vs.index}" name="desenFieldList[${vs.index}].replaceContent" value="${v.replaceContent }" style="width:100px"/></td>
		    <td>
		    	<c:if test="${groupDict != null && not empty groupDict}">
		    		<select name="desenFieldList[${vs.index}].dictCode" onchange="chooseDict(this.parentNode.parentNode,this.value,false)">
					<option value=''>-请选择-</option>
			    	<c:forEach var="g" items="${groupDict}">
			    		<option value="${g.dictCode}">${g.dictName}</option>
			    	</c:forEach>
			    	</select>
		    	</c:if>
		    </td>
		    <td name="conditionTd">
		    	
		    </td>
		  </tr>
	  </c:forEach>
	  <c:if test="${ruleViewList == null || empty ruleViewList}">
	  	  <tr>
		    <td colspan=9>查询不到该服务的字段信息</td>
		  </tr>
	  </c:if>
	</table>
	</div>
	</div>
</div>	
</c:if>
<!-- 挖掘的 脱敏并合规检查结束 -->
<c:if test="${ service.serviceSource=='2'}">

<!-- 108的 脱敏并合规检查开始 -->	
<div id="desenCheck" ${service.serviceSource=="1"?"style='display:none'":""}}>
	<div class="main_info_title" style="margin:5px 0 0 0">
		<img src="resource/images/open.png" title="展开" onclick="showOtherService('ruleCheckDiv','showRuleCheckImg','closeRuleCheckImg')" id="showRuleCheckImg" style="display:none;" class="divImg"/>
		<img src="resource/images/close.png" title="收缩" onclick="showOtherService('ruleCheckDiv','showRuleCheckImg','closeRuleCheckImg')" id="closeRuleCheckImg" class="divImg"/>
		<label>合规检查</label>
	</div>
	<div id="ruleCheckDiv">
		<div class="main_list" style="padding:5px 0 0 10px;">
			合规检查规则规范文件下载：		
			<a href='system/file_downLoadFile.do?fileId=474'><span class="c_red">规则模板.xmls</span></a>
			<br/>
			<br/>
				已上传的合规检查规则文件：
					<a id="down" href="system/file_downLoadFile.do?fileId=${fetch.checkFileId}" target="_blank">
					<span id="uploadedName" class="c_red">
					<c:if test="${fetch.desenType == 1}">
						${fileVo.realName}
					</c:if>
					</span></a>			
				<br/>
			<div style="margin-bottom:10px">		
			<br/>
			合规检查规则文件：<input id="upload" type="file" name="upload" style="width:250px"/>
			<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['ruleDesen']!=null && search != 'detailDesen'}">		
			<input id="uploadButton" type="button" onclick='uploadExcel()' value="上传"/>
			<input id="uploadingButton" style='display:none;color:gray;' type="button"  value="上传中"/>	
			</c:if>
			</div>
			<div id="ruleCheckTable">
			<c:if test="${fetch.desenType == 1}">
				
					<table width="100%" border="0">
					  <tr>
						<th>字段编码</th>
						<th>字段名称</th>
						<th>字段类型</th>
						<th>检查类型</th>
						<th>检查规则</th>
					  </tr>
					  <c:forEach var="o" items="${checkRuleList}" varStatus="status">
						<tr name="ruleFieldTr">
							<td>${o.fieldCode }</td>
							<td>${o.fieldName }<input type="hidden" name="fieldName" value="${o.fieldName }" />
							</td>
							<td>${o.fieldType }<input type="hidden" name="fieldType" value="${o.fieldType }" />
							</td>
							<td><g:sysDict dictCode="DICT_CHECK_RULE_TYPE" dictKey="${o.checkType}"/>
							</td>
							<td name="checkRule">${o.checkRule}</td>
						</tr>
					  </c:forEach>
					</table>				
			</c:if>
			</div>
		</div>
	</div>
	
	<div class="main_info_title" style="margin:0 0 0 0">
	<img src="resource/images/open.png" title="展开" onclick="showOtherService('desenCheckDiv','showDesenCheckImg','closeDesenCheckImg')" id="showDesenCheckImg" style="display:none;" class="divImg"/>
	<img src="resource/images/close.png" title="收缩" onclick="showOtherService('desenCheckDiv','showDesenCheckImg','closeDesenCheckImg')" id="closeDesenCheckImg" class="divImg"/>
	
	<label>脱敏并合规检查</label></div>
	<div id="desenCheckDiv">
	<div class="main_search" style="padding-top:5px">
		<span style="color:red;">
		一、脱敏类型使用说明：<br />
		1.范围内替换(根据处理定位中的位置范围替换为替换字符中的内容，如：2,5表示将第二到第五的内容替换为替换字符中的内容，5表示从第五个到最后)<br />
		2.字符替换(将处理定位中的内容替换为替换字符字段中的内容) 
		<br/>
		二、条件使用说明：<br />
		选择字典后,根据字典过滤数据，如省份、地市。没选字典且字段类型为数字类型可以配置数值判断条件
		</span>
	</div>
	<div id="desenRule"class="main_list" style="padding:5px 0 0 10px;">
	<table width="100%" border="0">
	  <tr>
	    <th>选择</th>
	    <th>字段名称</th>
	    <th>字段类型</th>
	    <th>脱敏类型</th>
	    <th>处理定位 </th>
	    <th>替换字符 </th>
	    <th>字典条件 </th>
	  </tr>
		<c:if test="${fetch.desenType == 1}">
		<c:forEach var="o" items="${ruleViewList}" varStatus="status">
				<tr name='fieldTr'>
							<td>
				  	  		<input type='hidden' name='objectName'  value='desenFieldList[${status.index}]'/>  	
				  	  		<input type='checkbox' name='desenFieldList[${status.index}].fieldId' value='-1' checked />
				  	  		<input type='hidden' name='desenFieldList[${status.index}].userId' value='${fetch.userId }'/>
				  	  		<input type='hidden' name='desenFieldList[${status.index}].serviceId' value='${fetch.serviceId }'/>
				  	  		<input type='hidden' name='desenFieldList[${status.index}].conditionContent' />
				  	  		<input type='hidden' name='desenFieldList[${status.index}].conditionType' value='in'/>
				  	  		</td>
					   		<td>${o.fieldName}<input type='hidden' name='fieldName' value="${o.fieldName}"/></td>
					    	<td>${o.fieldName}<input type='hidden' name='fieldType' value="${o.fieldName}"/></td>
					    	<td><g:sysDictList dictCode='DICT_DESEN_RULE_TYPE' tagType='select' tagName="desenFieldList[${status.index}].ruleType" defaultValue="${o.ruleType}" tagId="ruleType${status.index}"/></td>
					    	<td><input type='text' id='ruleContent${status.index}' name='desenFieldList[${status.index}].ruleContent' value='${o.ruleContent}' style='width:100px'/></td>
					    	<td><input type='text' id='replaceContent${status.index}' name='desenFieldList[${status.index}].replaceContent' value='${o.replaceContent}' style='width:100px'/></td>
					    	<td id='desenFieldList[${status.index}].condition'onchange='chooseRuleDict(this,this.parentNode)'>				    	
						    </td>
						 </tr>
			</c:forEach>
		</c:if>

	</table>
	</div>
	</div>
	
</div>
</c:if>
<!-- 108的 脱敏并合规检查 结束 -->

<!-- 108和挖掘的 合规检查 开始-->
<div id="ruleCheck" style='display:none'}>
<div class="main_info_title" style="margin:5px 0 0 0">
<img src="resource/images/open.png" title="展开" onclick="showOtherService('ruleCheckDiv','showRuleCheckImg','closeRuleCheckImg')" id="showRuleCheckImg" style="display:none;" class="divImg"/>
<img src="resource/images/close.png" title="收缩" onclick="showOtherService('ruleCheckDiv','showRuleCheckImg','closeRuleCheckImg')" id="closeRuleCheckImg" class="divImg"/>
<label>合规检查</label></div>
<div id="ruleCheckDiv">
<div class="main_list" style="padding:5px 0 0 10px;">
	合规检查规则规范文件下载：		
	<a href='system/file_downLoadFile.do?fileId=474'><span class="c_red">规则模板.xmls</span></a>
	<br/>
	<br/>
	已上传的合规检查规则文件：
			<a id="down3" href="system/file_downLoadFile.do?fileId=${fetch.checkFileId}" target="_blank">
			<span id="uploadedName2" class="c_red"><c:if test="${fetch.desenType == 2}">
						${fileVo.realName}
					</c:if></span></a>			
	<br/>
	<div style="margin-bottom:10px">		
	<br/>
	合规检查规则文件：<input id="upload2" type="file" name="upload" style="width:250px"/>
		<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['ruleDesen']!=null && search != 'detailDesen'}">

		<input id="uploadButton2" type="button" onclick='uploadExcel2()' value="上传"/>
		<input id="uploadingButton2" style='display:none;color:gray;' type="button"  value="上传中"/>
		</c:if>
	</div>
	<div id="ruleCheckTable2">	
	<c:if test="${fetch.desenType == 2}">

	<table width="100%" border="0">
	  <tr>
		<th>字段编码</th>
		<th>字段名称</th>
		<th>字段类型</th>
		<th>检查类型</th>
		<th>检查规则</th>
	  </tr>
	  <c:forEach var="o" items="${checkRuleList}" varStatus="status">
		<tr name="ruleFieldTr">
			<td>${o.fieldCode }</td>
			<td>${o.fieldName }<input type="hidden" name="fieldName" value="${o.fieldName }" />
			</td>
			<td>${o.fieldType }<input type="hidden" name="fieldType" value="${o.fieldType }" />
			</td>
			<td><g:sysDict dictCode="DICT_CHECK_RULE_TYPE" dictKey="${o.checkType}"/>
			</td>
			<td name="checkRule">${o.checkRule}</td>
		</tr>
	  </c:forEach>
	  <c:if test="${checkRuleList == null || empty checkRuleList}">
	  	  <tr>
		    <td colspan=9>查询不到该服务的字段信息</td>
		  </tr>
	  </c:if>
	</table>
	</c:if>
	</div>

</div>
</div>

</div>
<!-- 合规检查 结束-->

<div class="page_wrap clearfix">
	<div style="text-align:center">
		<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['ruleNoPass']!=null && search != 'detailDesen' && isServiceFetch == 'isServiceFetch'}">
			<input name="updateDesenBtn" id="updateDesenBtn" type="button" value="不同意创建" onclick="ruleNotPass(${workPlanId},${service.serviceId },${fetch.userId })"/>
		</c:if>
		<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['ruleDesen']!=null && search != 'detailDesen'}">
			<input name="updateDesenBtning" id="updateDesenBtning" type="button" value="保存中" style="color:gray;display:none" disable/>
			<input name="updateDesenBtn" id="updateDesenBtn" type="button" value="${preWorkPlanVO.planState==0?'重新提交':'保存'}" onclick="updateDesenRuleServiceField()"/>
		</c:if>
	
		<input name="back" id="back" type="button" value="返回" onclick="goBack()"/>
	</div>
</div>
</form>
</div>

<c:if test="${otherServiceDesenMap != null && not empty otherServiceDesenMap}">
<div class="main_info_title" style="margin:0 0 0 0">
<img src="resource/images/open.png" title="展开" onclick="showOtherService('otherServiceDiv','showServiceImg','closeServiceImg')" id="showServiceImg" class="divImg"/>
<img src="resource/images/close.png" title="收缩" onclick="showOtherService('otherServiceDiv','showServiceImg','closeServiceImg')" id="closeServiceImg" style="display:none;" class="divImg"/>
同一模型的其他服务脱敏配置</div>
<div id="otherServiceDiv" style="display:none">
<c:forEach var="other" items="${otherServiceDesenMap}" varStatus="vs">
<div class="main_info_title" style="margin:10px 0px 0px 10px ;">
	<img src="resource/images/open.png" title="展开" onclick="showOtherService('otherServiceDiv${other.key}','showServiceImg${other.key}','closeServiceImg${other.key}')" id="showServiceImg${other.key}" class="divImg"/>
	<img src="resource/images/close.png" title="收缩" onclick="showOtherService('otherServiceDiv${other.key}','showServiceImg${other.key}','closeServiceImg${other.key}')" id="closeServiceImg${other.key}" style="display:none;" class="divImg"/>
	服务编码：${serviceMap[other.key].serviceCode } &nbsp;&nbsp; 服务名称：${serviceMap[other.key].serviceName } &nbsp; 
	<c:if test="${model != null }"> 
	模型编码：${model.modelCode } &nbsp;&nbsp; 模型名称：${model.modelName } &nbsp;
	</c:if>
</div>
<div style="margin:8px 0px 6px 20px;">
	规则类型：<g:sysDict dictCode="DICT_DESEN_TYPE" dictKey="${desenTypeMap[other.key]}"/>
</div>
<c:if test="${desenTypeMap[other.key]=='1' }">
	<div class="main_list" id="otherServiceDiv${other.key}" style="display:none;padding:5px 0 0 10px;">
<table width="100%" border="0">
  <tr>
    <th>选择</th>
    <th>字段编码</th>
    <th>字段名称</th>
    <th>字段类型</th>
    <th>脱敏类型</th>
    <th>处理定位 </th>
    <th>替换字符 </th>
    <th>字典 </th>
    <th>条件 </th>
  </tr>
  <c:forEach var="o" items="${other.value}" varStatus="status">
  	  <tr name="fieldTr">
  	  	<td>
  	  		<!-- conditionTd -->
  	  		<input type="hidden" name="objectName"  value="desenFieldList[${status.index}]"/>
  	  		<input type="hidden" name="conditionTypeDefault" id="conditionType${status.index}" value="${o.conditionType}"/>
	    	<input type="hidden" name="conditionContentDefault" id="conditionContent${status.index}" value="${o.conditionContent}"/>
	    	
  	  		<input type="checkbox" name="desenFieldList[${status.index}].fieldId" value="${o.fieldId }" ${o.fieldDeseId+0==0?"":"checked=true" }/>
  	  		<input type="hidden" name="desenFieldList[${status.index}].fieldDeseId" value="${o.fieldDeseId }"/>
  	  		<input type="hidden" name="desenFieldList[${status.index}].userId" value="${o.userId }"/>
  	  		<input type="hidden" name="desenFieldList[${status.index}].serviceId" value="${o.serviceId }"/>
  	  	</td>
	    <td>${o.fieldCode }</td>
	    <td>${o.fieldName }<input type="hidden" name="fieldName" value="${o.fieldName }"/></td>
	    <td>${o.fieldType }<input type="hidden" name="fieldType" value="${o.fieldType }"/></td>
	    <td><g:sysDictList dictCode="DICT_DESEN_RULE_TYPE" defaultValue="${o.ruleType}" tagType="select" tagName="desenFieldList[${status.index}].ruleType" tagId="ruleType${status.index}"/></td>
	    <td><input type="text" id="ruleContent${status.index}" name="desenFieldList[${status.index}].ruleContent" value="${o.ruleContent }" style="width:100px"/></td>
	    <td><input type="text" id="replaceContent${status.index}" name="desenFieldList[${status.index}].replaceContent" value="${o.replaceContent }" style="width:100px"/></td>
	    <td>
	    	<c:if test="${groupDict != null && not empty groupDict}">
	    		<select name="desenFieldList[${status.index}].dictCode" onchange="chooseDict(this.parentNode.parentNode,this.value)">
				<option value=''>-请选择-</option>
		    	<c:forEach var="g" items="${groupDict}">
		    		<option value="${g.dictCode}">${g.dictName}</option>
		    	</c:forEach>
		    	</select>
	    	</c:if>
	    </td>
	    <td name="conditionTd">
	    	
	    </td>
	  </tr>
  </c:forEach>
</table>
</div>
</c:if>

<c:if test="${desenTypeMap[other.key]=='2' }">
	<div class="main_list" id="otherServiceDiv${other.key}" style="display:none;padding:5px 0 0 10px;">
<table width="100%" border="0">
  <tr>
    <th>字段编码</th>
    <th>字段名称</th>
    <th>字段类型</th>
    <th>检查类型</th>
    <th>检查规则</th>
  </tr>
  <c:forEach var="o" items="${other.value}" varStatus="status">
  	  <tr name="ruleFieldTr">
	    <td>${o.fieldCode }</td>
	    <td>${o.fieldName }<input type="hidden" name="fieldName" value="${o.fieldName }"/></td>
	    <td>${o.fieldType }<input type="hidden" name="fieldType" value="${o.fieldType }"/></td>
	    <td><g:sysDict dictCode="DICT_CHECK_RULE_TYPE" dictKey="${o.checkType}"/></td>
		<td>${o.checkRule}</td>
	  </tr>
  </c:forEach>
</table>
</div>
</c:if>
</c:forEach>
</div>
</c:if>
</body>
</html>
