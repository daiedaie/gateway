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
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>实时服务数据抽样</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<style type="text/css">
.rightSplit td{BORDER-RIGHT: #dfdfdf 1px solid; }
</style>
<script type="text/javascript">
	var fieldDict = ${fieldDict};
	var dictMap = ${dictMap};
	$(document).ready(function(){
		$("#mainServiceDiv tr[name=fieldTr]").each(function(){
			var fieldId = $(this).find("[name$=fieldId]").val();	//字段id
			eval("dictCode = fieldDict.fieldId" + fieldId);
			chooseDict(this,dictCode,true);	//设置字典默认值
		});
		$("#mainServiceDiv input,#mainServiceDiv select").attr("disabled",true);
	});
	
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
		}else if(fieldType=='INTEGER' || fieldType=='INT' || fieldType=='LONG' || fieldType=='NUMBER'){	//没关联字典，且为数据类型
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
	
	function showServiceDesenRule(){
		$("#mainServiceDiv,#showServiceImg,#closeServiceImg").toggle();
	}
</script>
</head>
<body class="FrameMain">
<div class="main_title">
	<b>实时服务数据抽样</b>
</div>
<div class="main_infomation">
	<div class="main_list">
	脱敏前数据条数：${fn:length(dataMap['originalDataMap'])}，字段数：${fn:length(dataMap['dataFieldMap'])}；
	<c:if test="${fn:length(dataMap['originalDataMap']) > 0}">
	脱敏后数据条数：${fn:length(dataMap['desenDataMap'])}，字段数：${dataMap['desenFieldCount']}
	</c:if>
	<table width="100%" border="0">
		<!-- 表结构元数据 -->
		<tr class="rightSplit">
		<c:forEach var="fieldMap" items="${dataMap['dataFieldMap']}" >
			<td colspan=2>${columnMap[fieldMap.value]}(${fieldMap.value})</td>
		</c:forEach>
		</tr>
		<tr class="rightSplit">
		<c:forEach var="fieldMap" items="${dataMap['dataFieldMap']}" >
			<td>脱敏前</td>
			<td>脱敏后</td>
		</c:forEach>
		</tr>
		
		<!--数据  -->
		<c:forEach var="originalDataMap" items="${dataMap['originalDataMap']}" >
			<!-- 脱敏后的数据对象 -->
			<c:set value="${dataMap['desenDataMap'][originalDataMap.key]}" var="desenObject" scope="request"/>
			<tr class="rightSplit">
				<c:forEach var="originalData" items="${originalDataMap.value}">
					<td>${originalData.value}</td>
					<td>${desenObject[originalData.key]}</td>
				</c:forEach>
			</tr>
		</c:forEach>
	</table>
	</div>
</div>

<div class="main_info_title" style="margin:20px 0 0 0;">
<img src="resource/images/open.png" title="展开" onclick="showServiceDesenRule()" id="showServiceImg" style="width:15px;height:12px;cursor:pointer"/>
<img src="resource/images/close.png" title="收缩" onclick="showServiceDesenRule()" id="closeServiceImg" style="display:none;width:15px;height:12px;cursor:pointer"/>
服务脱敏规则</div>
<div id="mainServiceDiv" style="display:none">
<div class="main_title">
	<b>服务字段脱敏配置</b>
</div>
<div class="main_search">
	<p>
	服务编码：${service.serviceCode }&nbsp; 服务名称：${service.serviceName } &nbsp;&nbsp;
	<c:if test="${model != null }"> 
	模型名称：${model.modelName } &nbsp; 模型编码：${model.modelCode } &nbsp;&nbsp;
	</c:if>
	用户编码：${user.loginName } &nbsp; 用户名称：${user.userName }
	</p>
	<span style="color:red;">
	一、脱敏类型使用说明：<br />
	1.范围内替换(根据处理定位中的位置范围替换为替换字符中的内容，如：2,5表示将第二到第五的内容替换为替换字符中的内容，5表示从第五个到最后)<br />
	2.字符替换(将处理定位中的内容替换为替换内容字段中的内容) 
	<br/>
	二、条件使用说明：<br />
	选择字典后,根据字典过滤数据，如省份、地市。没选字典且字段类型为数字类型可以配置数值判断条件
	</span>
</div>
<div class="main_list">
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
	    	
  	  		<input type="checkbox" name="desenFieldList[${vs.index}].fieldId" value="${v.fieldId }" ${v.fieldDeseId+0==0?"":"checked=true" }/>
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
</body>
</html>
