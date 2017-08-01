<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/common/include.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="<%=basePath %>"/>
<title>服务数据检查结果</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="resource/css/ui-dialog.css" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/artDialog/dialog-min.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<style type="text/css">
.divImg{width:15px;height:12px;cursor:pointer}
</style>
<script type="text/javascript">
	function showRuleTypeDiv(div,openImg,closeImg){
		$("#"+div+",#"+openImg+",#"+closeImg).toggle();
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
	
	
	var fieldDict = '${fieldDict}';
	var dictMap = '${dictMap}';
	$(document).ready(function(){
		$("#desenDiv tr[name=fieldTr]").each(function(){
			var fieldId = $(this).find("[name$=fieldId]").val();	//字段id
			eval("dictCode = fieldDict.fieldId" + fieldId);
			chooseDict(this,dictCode,true);	//设置字典默认值
		});
		$("#desenDiv input[type=checkbox],#desenDiv input[type=text],#desenDiv select").attr("disabled",true);
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
</script>
</head>
<body class="FrameMain">
	<form id="searchRuleCheckAuditForm" name="searchRuleCheckAuditForm" action="service/modelTask_previewRuleCheck.do" loadContainer="pageDataList">
		<div class="main_title">
			<b>服务数据检查结果</b>
		</div>
		<div class="main_list">
				<p>
		    	数据用户登录名：${dataUserVO.loginName}<br/>
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
				任务账期：${taskVO.fieldValue }<br/>
				文件名称：${fileVO.fileName }<br/>
				任务开始时间：<fmt:formatDate value="${taskVO.createTime }" pattern="yyyy-MM-dd HH:mm:ss"/><br/>
				</p>
		    	
		</div>
		<c:if test="${taskVO.checkResult == '2'}">
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
	            		不合规的前${irregularCount-1}行信息：
	            	</c:if>
	            	<c:if test="${taskVO.checkResult != '3' && taskVO.checkResult != '4'}">
	            		不合规的前${irregularCount}行信息：
	            	</c:if>
		    		<a href="system/file_downloadCheckResult.do?taskId=${taskVO.taskId}&exportNum=1000" target="_blank" style="color:blue">导出Excel(前1000条)</a>&nbsp;
					<a href="service/modelTask_downloadFtpCheckFile.do?taskId=${taskVO.taskId}" target="_blank" style="color:blue">导出TXT(全部)</a>
		    	</c:if>
		    </div>
	    </c:if>
	     <c:if test="${taskVO.checkResult == '0' && taskVO.checkIrregularNum != '1'}">
    		<div class="main_list" style="color:red">
    		数据全部合规，共检查${taskVO.checkNum}行
    		</div>
    	</c:if>
    	<c:if test="${taskVO.checkResult != '0' && fn:length(pageObject.data) != 0}">
		<div id="pageDataList">
			<div class="main_list" id="main_list">
				<table width="100%" border="0">
					<input type="hidden" name="taskId" value="${taskId }"/>
					<input type="hidden" name="serviceId" value="${serviceId }"/>
					<input type="hidden" name="userId" value="${userId }"/>
					<!-- 表结构元数据 -->
					<tr>
						<th>序号</th>
						<th>行号</th>
						<c:if test="${service.serviceSource=='1' }">
							<c:forEach var="title" items="${titleList }" varStatus="status">
								<th>${title}</th>
							</c:forEach>
						</c:if>
						<c:if test="${service.serviceSource=='2' }">12
							<c:forEach var="field" items="${serviceCodeList }" varStatus="status">
								<th>${field.fieldCode }</th>
							</c:forEach>
						</c:if>
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
		</c:if>
		</form>
		
		<div class="main_info_title" style="margin:0 0 0 0">
			<img src="resource/images/open.png" title="展开" onclick="showRuleTypeDiv('ruleTypeDiv','showRuleTypeImg','closeRuleTypeImg')" id="showRuleTypeImg" class="divImg" /> 
			<img src="resource/images/close.png" title="收缩" onclick="showRuleTypeDiv('ruleTypeDiv','showRuleTypeImg','closeRuleTypeImg')" id="closeRuleTypeImg" style="display:none;" class="divImg" />
			脱敏规则
		</div>
		<div id="ruleTypeDiv" style="display:none">
			<div class="main_search">
				规则类型：<g:sysDict dictCode="DICT_DESEN_TYPE" dictKey="${fetchVO.desenType}" />
				不合规最大检查行数：${taskVO.maxCheckNum}</br>
				合规文件输出行数：${taskVO.outputNum}</br>
				合规文件是否需要审核：${taskVO.checkAudit=='1'?"是":"否"}
			</div>
			<c:if test="${fetchVO.desenType=='1'}">
				<div class="main_list" style="padding:5px 0 0 10px;" id="desenDiv">
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
						    	<c:if test="${fetchVO.desenType=='1'}">
					  	  			<input type="checkbox" name="desenFieldList[${vs.index}].fieldId" value="${v.fieldId }" ${v.fieldDeseId+0==0?"":"checked=true" }/>
					  	  		</c:if>
					  	  		<c:if test="${fetchVO.desenType!='1'}">
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
			</c:if>
			<c:if test="${fetchVO.desenType=='2'}">
				<div class="main_list" style="padding:5px 0 0 10px;">
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
			</c:if>
		</div>
</body>
</html>
