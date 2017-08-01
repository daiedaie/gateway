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
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<title>用户模型服务取数审核</title>
<style type="text/css">
.divImg{width:15px;height:12px;cursor:pointer}
</style>
</head>
<script type="text/javascript">
   var fieldDict = ${fieldDict};
   var preFieldDict = ${preFieldDict};
   var dictMap = ${dictMap};
   $(document).ready(function(){
		$("#ruleDiv tr[name=fieldTr]").each(function(){
			var fieldId = $(this).find("[name$=fieldId]").val();	//字段id
			eval("dictCode = fieldDict.fieldId" + fieldId);
			chooseDict(this,dictCode,true);	//设置字典默认值
		});
		
		//修改前
		$("#preRuleDiv tr[name=fieldTr]").each(function(){
			var fieldId = $(this).find("[name$=fieldId]").val();	//字段id
			eval("dictCode = preFieldDict.fieldId" + fieldId);
			chooseDict(this,dictCode,true);	//设置字典默认值
		});
		$("#ruleDiv input,#ruleDiv select,#preRuleDiv input,#preRuleDiv select").attr("disabled",true);
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
					td.append("<label><input disabled='disabled' type='checkbox' name='"+objectName+".conditionContent' value='"+dictList[i].dictKey+"'/>"+dictList[i].dictValue+"</label>");
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
			var html = "<select disabled='disabled' name='"+objectName+".conditionType'>"+
   					   "<option value=''>请选择</option>"+
   					   "<option value='='>=</option>"+
   					   "<option value='!='>!=</option>"+
   					   "<option value='>'>></option>"+
   					   "<option value='>='>>=</option>"+
   					   "<option value='<'><</option>"+
   					   "<option value='<='><=</option>"+
   					   "</select>";
			html += "<input type='text' readonly='readonly' name='"+objectName+".conditionContent' value='' style='width:100px' maxlength='20' placeholder='只能输入数字'/>";
			td.append(html);
  				
			//设置默认值
			if(conditionContent != '' && conditionType!='in'){
				$(tr).find("select[name$=conditionType] option[value='"+conditionType+"']").attr("selected",true);
	   			$(tr).find("[type=text][name$=conditionContent]").val(conditionContent);
			}
		}
	}

   function pass(){
	   if($("input[name='status']").val()=="0"){
			alert("模型服务无效无法创建取数任务。");
		 	return;
		}
      if(confirm("通过该服务数据提取申请？")){
	      var form1=document.getElementById("verifyForm");
	      document.getElementById("passTag").value="2";
	      form1.submit();
      }
   }
   
   function noPass(){
      if(confirm("不通过该服务数据提取申请？")){
	      var form1=document.getElementById("verifyForm");
	      document.getElementById("passTag").value="0";
	      form1.submit();
      }
   }
   
   function reModifyRole(){
      if(confirm("重新修改规则？")){
	      var form1=document.getElementById("verifyForm");
	      document.getElementById("passTag").value="3";
	      form1.submit();
      }
   }
   
   function showPreDesen(div,openImg,closeImg){
		$("#"+div+",#"+openImg+",#"+closeImg).toggle();
	}
</script>
  <body class="FrameMain">
	<div class="main_title">
		<b>新增服务并取数规则审核</b>
	</div>
	<form id="verifyForm" name="verifyForm" action="service/modelDataApp_serviceFetchApp.do">
	<input type="hidden" id="planId" name="planId" value="${planId}"/>
	<input type="hidden" id="status" name="status" value="${serviceVO.status }"/>
	<input type="hidden" id="passTag" name="passTag" />
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
		  <tr >
		    <td width="10%" align="right" valign="top">审核意见：</td>
		    <td width="90%" align="left"><s:textarea id="suggestion" name="suggestion" rows="6" cols="100" /></td>
		  </tr>
		  <tr>
		  	<td colspan="2" align="center">
		  	<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['passTask']!=null}">
		    	<input name="" type="button" value="同意取数" onclick="pass()"/>
		    </c:if>
		    &nbsp;&nbsp;
		    &nbsp;&nbsp;
		    <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['noPassTask']!=null}">
		    	<c:if test="${fetchId !=null}">
		    		<input name="" type="button" value="不同意取数" onclick="noPass()"/>
		    	</c:if>
		    </c:if>
		    &nbsp;&nbsp;
		    &nbsp;&nbsp;
		    <c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['reModifyRule']!=null}">
		    	<input name="" type="button" value="重新修改规则" onclick="reModifyRole()"/>
		    </c:if>
		    </td>
		  </tr>
     </table>
     <c:if test="${modelVo != null}">
	<div class="main_info_title">模型信息</div>
	<table width="100%" border="0">
		  <tr >
		    <th width="10%">模型名称：</th>
		    <td width="25%">${modelVo.modelName }</td>
		    <th width="10%">模型编码：</th>
		    <td width="25%">${modelVo.modelCode }</td>
		    <th width="10%">模型状态：</th>
		    <td width="25%"><g:sysDict dictCode="DICT_DATA_STATE" dictKey="${modelVo.status}"/></td>
		  </tr>
		  <tr >
		    <th width="10%">模型细节：</th>
		    <td colspan="5">${modelVo.modelDesc }</td>
		  </tr>
    </table>
    </c:if>
    <c:if test="${modelVo == null}">
    <div class="main_info_title">该服务没有模型信息</div>
    </c:if>
    <div class="main_info_title">服务信息</div>
	<table width="100%" border="0">
		  <tr >
		    <th width="10%">服务名称：</th>
		    <td width="25%">${serviceVO.serviceName }</td>
		    <th width="10%">服务状态：</th>
		    <td width="25%"><g:sysDict dictCode="DICT_DATA_STATE" dictKey="${serviceVO.status}"/></td>
		    <th width="10%">服务编码：</th>
		    <td width="25%">${serviceVO.serviceCode }</td>
		  </tr>
		  <tr >
		    <th width="10%">服务类型：</th>
		    <td><g:sysDict dictCode="DICT_SERVICE_TYPE" dictKey="${serviceVO.serviceType}"/></td>
		    <th width="10%">服务周期：</th>
		    <td>
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
    		<th width="10%"></th>
		    <td width="25%"></td>
		  </tr>
    </table>
    <div id="ruleDiv" >
		<div class="main_info_title" style="margin-bottom:10px">服务脱敏配置信息</div>
		<div class="main_list" style="padding:0 20px 0 20px">
			<div style="margin-bottom:10px">
				规则类型：<g:sysDict dictCode="DICT_DESEN_TYPE" dictKey="${modelDataFetchVO.desenType}"/> <br/>
				不合规最大检查行数：${modelDataFetchVO.maxCheckNum } <br/>
				合规文件输出行数：${modelDataFetchVO.outputNum } <br />
				合法文件是否需要审核：${modelDataFetchVO.checkAudit==1?"是":"否" }
			</div>
			<c:if test="${modelDataFetchVO.desenType=='1' }">
				<table width="100%" border="0">
						<tr>
							<th>选择</th>
							<th>字段编码</th>
							<th>字段名称</th>
							<th>字段类型</th>
							<th>脱敏类型</th>
							<th>处理定位</th>
							<th>替换字符</th>
							<th>字典</th>
							<th>条件</th>
						</tr>
						<c:forEach var="o" items="${ruleViewList}" varStatus="status">
							<tr name="fieldTr">
								<td>
									<!-- conditionTd --> <input type="hidden" name="objectName"
									value="desenFieldList[${status.index}]" /> <input
									type="hidden" name="conditionTypeDefault"
									id="conditionType${status.index}" value="${o.conditionType}" />
									<input type="hidden" name="conditionContentDefault"
									id="conditionContent${status.index}"
									value="${o.conditionContent}" /> <input disabled="disabled"
									type="checkbox" name="desenFieldList[${status.index}].fieldId"
									value="${o.fieldId }" ${o.fieldDeseId+0==0? "":"checked=true " }/>
									<input type="hidden"
									name="desenFieldList[${status.index}].fieldDeseId"
									value="${o.fieldDeseId }" /> <input type="hidden"
									name="desenFieldList[${status.index}].userId"
									value="${o.userId }" /> <input type="hidden"
									name="desenFieldList[${status.index}].serviceId"
									value="${o.serviceId }" /></td>
								<td>${o.fieldCode }</td>
								<td>${o.fieldName }<input type="hidden" name="fieldName"
									value="${o.fieldName }" />
								</td>
								<td>${o.fieldType }<input type="hidden" name="fieldType"
									value="${o.fieldType }" />
								</td>
								<td><g:sysDictList dictCode="DICT_DESEN_RULE_TYPE"
										defaultValue="${o.ruleType}" tagType="select"
										tagName="desenFieldList[${status.index}].ruleType"
										tagId="ruleType${status.index}" />
								</td>
								<td><input readonly="readonly" type="text"
									id="ruleContent${status.index}"
									name="desenFieldList[${status.index}].ruleContent"
									value="${o.ruleContent }" style="width:100px" />
								</td>
								<td><input readonly="readonly" type="text"
									id="replaceContent${status.index}"
									name="desenFieldList[${status.index}].replaceContent"
									value="${o.replaceContent }" style="width:100px" />
								</td>
								<td><c:if test="${groupDict != null && not empty groupDict}">
										<select name="desenFieldList[${status.index}].dictCode"
											onchange="chooseDict(this.parentNode.parentNode,this.value)"
											disabled="disabled">
											<option value=''>-请选择-</option>
											<c:forEach var="g" items="${groupDict}">
												<option value="${g.dictCode}">${g.dictName}</option>
											</c:forEach>
									</select>
								</c:if></td>
							<td name="conditionTd"></td>
						</tr>
					</c:forEach>
				</table>
			</c:if>
			
			<c:if test="${modelDataFetchVO.desenType=='2' }">
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
			</c:if>
			</div>
		</div>	
		
		<div id="preRuleDiv">	
			<div class="main_info_title" style="margin-bottom:10px">
			<img src="resource/images/open.png" title="展开" onclick="showPreDesen('preDesenCheckDiv','showDesenCheckImg','closeDesenCheckImg')" id="showDesenCheckImg" class="divImg"/>
			<img src="resource/images/close.png" title="收缩" onclick="showPreDesen('preDesenCheckDiv','showDesenCheckImg','closeDesenCheckImg')" id="closeDesenCheckImg"  style="display:none;" class="divImg"/>
			修改前服务脱敏配置信息</div>
			<div class="main_list" style="padding:0 20px 0 20px;display:none" id="preDesenCheckDiv">
				<c:if test="${preDesenType!=null}">
					<div style="margin-bottom:10px">
						规则类型：<g:sysDict dictCode="DICT_DESEN_TYPE" dictKey="${preDesenType}"/> <br/>
						不合规最大检查行数：${preMaxCheckNum } <br/>
						合规文件输出行数：${preOutputNum } <br/>
						合法文件是否需要审核：${preCheckAudit==1?"是":"否" }
					</div>
					<c:if test="${modelDataFetchVO.desenType=='1' }">
						<table width="100%" border="0">
								<tr>
									<th>选择</th>
									<th>字段编码</th>
									<th>字段名称</th>
									<th>字段类型</th>
									<th>脱敏类型</th>
									<th>处理定位</th>
									<th>替换字符</th>
									<th>字典</th>
									<th>条件</th>
								</tr>
								<c:forEach var="o" items="${preRuleViewList}" varStatus="status">
									<tr name="fieldTr">
										<td>
											<!-- conditionTd --> <input type="hidden" name="objectName"
											value="desenFieldList[${status.index}]" /> <input
											type="hidden" name="conditionTypeDefault"
											id="conditionType${status.index}" value="${o.conditionType}" />
											<input type="hidden" name="conditionContentDefault"
											id="conditionContent${status.index}"
											value="${o.conditionContent}" /> <input disabled="disabled"
											type="checkbox" name="desenFieldList[${status.index}].fieldId"
											value="${o.fieldId }" ${o.fieldDeseId+0==0? "":"checked=true " }/>
											<input type="hidden"
											name="desenFieldList[${status.index}].fieldDeseId"
											value="${o.fieldDeseId }" /> <input type="hidden"
											name="desenFieldList[${status.index}].userId"
											value="${o.userId }" /> <input type="hidden"
											name="desenFieldList[${status.index}].serviceId"
											value="${o.serviceId }" /></td>
										<td>${o.fieldCode }</td>
										<td>${o.fieldName }<input type="hidden" name="fieldName"
											value="${o.fieldName }" />
										</td>
										<td>${o.fieldType }<input type="hidden" name="fieldType"
											value="${o.fieldType }" />
										</td>
										<td><g:sysDictList dictCode="DICT_DESEN_RULE_TYPE"
												defaultValue="${o.ruleType}" tagType="select"
												tagName="desenFieldList[${status.index}].ruleType"
												tagId="ruleType${status.index}" />
										</td>
										<td><input readonly="readonly" type="text"
											id="ruleContent${status.index}"
											name="desenFieldList[${status.index}].ruleContent"
											value="${o.ruleContent }" style="width:100px" />
										</td>
										<td><input readonly="readonly" type="text"
											id="replaceContent${status.index}"
											name="desenFieldList[${status.index}].replaceContent"
											value="${o.replaceContent }" style="width:100px" />
										</td>
										<td><c:if test="${groupDict != null && not empty groupDict}">
												<select name="desenFieldList[${status.index}].dictCode"
													onchange="chooseDict(this.parentNode.parentNode,this.value)"
													disabled="disabled">
													<option value=''>-请选择-</option>
													<c:forEach var="g" items="${groupDict}">
														<option value="${g.dictCode}">${g.dictName}</option>
													</c:forEach>
											</select>
										</c:if></td>
									<td name="conditionTd"></td>
								</tr>
							</c:forEach>
						</table>
					</c:if>
					
						<table width="100%" border="0">
							<tr>
								<th>字段编码</th>
								<th>字段名称</th>
								<th>字段类型</th>
								<th>检查类型</th>
								<th>检查规则</th>
							</tr>
							<c:forEach var="o" items="${preCheckRuleList}" varStatus="status">
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
				</c:if>
				<c:if test="${preDesenType==null }">
					<div style="text-align:center;">无</div>
				</c:if>
			</div>
		</div>
	  </form>
  </body>
</html>
