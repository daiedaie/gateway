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
<title>数据周期配置_数据网关Gateway管理平台</title>
<link rel="stylesheet" href="resource/css/style.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">	
function saveCycle() {
	$("input[type=text]").each(function(){
		$(this).val($.trim($(this).val()));
	});

	if($("#sourceCycleType option:selected").val()=='' || $("#resultCycleType option:selected").val()==''){
		alert("请选择周期类型");
		return false;
	}
	
	var boolean = true;
	$("#sourceCycleNum,#resultCycleNum").each(function(){
		if($(this).val()==''){
			alert("周期数不能为空");
			$(this).focus();
			boolean = false;
			return false;
		}
	
		if(isNaN($(this).val())){
			alert("周期数只能为数字");
			$(this).select();	
			boolean = false;		
			return false;
		}
	});
	if(!boolean) return false;
	
	$("#saveCycleBtn").attr("disabled",true);
	url = "system/dataCycle_updateDataCycleList.do";
   	$.post(url,$("#dataCycleForm").serialize(),function(json){
   		$("#saveCycleBtn").attr("disabled",false);
		if (json.state == 'success') {
			alert("保存成功");
		}else{
			alert("保存失败，原因："+json.message);
		}
   	});
}

</script>
</head>	
<body>
	<div class="main_form" method="post">
	<form id="dataCycleForm" name="dataCycleForm">
            <div class="main_title">
                <b>数据周期配置</b>
            </div>
		
		<table width="50%" border="0" style="padding:20px">
			<tr>
				<td style="width:30%"><font color='red'>*</font>数据类型</td>
				<td style="width:35%"><font color='red'>*</font>周期类型</td>
				<td style="width:35%"><font color='red'>*</font>周期数</td>
			</tr>
			<tr>
				<td>原始数据</td>
				<td>
					<input type="hidden" name="sourceCycleVO.cycleId" value="${sourceCycleVO.cycleId }"/>
					<input type="hidden" name="sourceCycleVO.dataType" value="1"/>
					<g:sysDictList dictCode="DICT_CYCLE_TYPE" defaultValue="${sourceCycleVO.cycleType }" tagType="select" tagName="sourceCycleVO.cycleType" tagId="sourceCycleType"/>
				</td>
				<td style="width:30%"><input type="text" id="sourceCycleNum" name="sourceCycleVO.cycleNum" value="${sourceCycleVO.cycleNum }"/></td>
			</tr>
			<tr>
				<td>结果数据</td>
				<td>
					<input type="hidden" name="resultCycleVO.cycleId" value="${resultCycleVO.cycleId }"/>
					<input type="hidden" name="resultCycleVO.dataType" value="2"/>
					<g:sysDictList dictCode="DICT_CYCLE_TYPE" defaultValue="${resultCycleVO.cycleType }" tagType="select" tagName="resultCycleVO.cycleType" tagId="resultCycleType"/>
				</td>
				<td><input type="text" id="resultCycleNum" name="resultCycleVO.cycleNum" value="${resultCycleVO.cycleNum }"/></td>
			</tr>
			<tr>
				<td colspan=3 style="height:20px"></td>
			</tr>
			<tr>
				<td colspan=3  style="text-align:center">
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['saveDataCycle']!=null}">
						<input name="saveCycleBtn" id="saveCycleBtn" type="button" value="保存" onclick="saveCycle()"/>
					</c:if>
				</td>
			</tr>
		</table>
	</form>
	</div>

</body>
</html>
