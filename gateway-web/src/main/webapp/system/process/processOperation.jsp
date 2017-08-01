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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>流程进度明细_数据网关Gateway服务平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="resource/css/process.css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/page.js"></script>
<script type="text/javascript">
function sendMsg(dealType){
	$.ajax({
	    type:"POST",
	    url:"system/process_noteHandleProcess.do?",
	    data:"dealType="+dealType,
	    cache: false, 		
	    success:function(json){
	        if(json.state=='success'){
	        	alert("督促成功");
	        }else {
				alert(json.message);
			}
	    },
	    error:function(){
	    	alert("系统异常，督促失败");
	    }
	});
}
   

</script>
</head>
  
<body class="FrameMain">
  <div class="main_title">
	  <b>流程进度明细查看</b>
  </div>
  <ul class="steps-list">
        
        <c:if test="${pType=='1' }">
        	<c:choose>
        		 <c:when test="${processStatus=='1'&&stepStatus=='1' && status=='1'}">
	        		<li class="fist ing">1.申请取数</li>
			        <li>2.一次审核</li>
			        <li class="last">3.二次审核</li>
	        	</c:when>
	        	<c:when test="${procesStatus=='1'&&stepStatus=='0' && status=='1'}">
	        		<li class="fist done">1.申请取数</li>
			        <li >2.一次审核</li>
			        <li class="last">3.二次审核</li>
	        	</c:when>

	        	<c:when test="${procesStatus=='2'&&stepStatus=='1' && status=='1'}">
	        		<li class="fist done">1.申请取数</li>
			        <li class="ing">2.一次审核</li>
			        <li class="last">3.二次审核</li>
	        	</c:when>
	        	<c:when test="${procesStatus=='2'&&stepStatus=='0' && status=='1'}">
	        		<li class="fist done">1.申请取数</li>
			        <li class="done">2.一次审核</li>
			        <li class="last">3.二次审核</li>
	        	</c:when>
	        	<c:when test="${procesStatus=='2' && status=='0'}">
	        		<li class="fist done">1.申请取数</li>
			        <li >2.一次审核</li>
			        <li class="last ">3.二次审核</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='3'&&stepStatus=='1' && status=='1'}">
	        		<li class="fist done">1.申请取数</li>
			        <li class="done">2.一次审核</li>
			        <li class="last ing">3.二次审核</li>
	        	</c:when>
	        	<c:when test="${procesStatus=='3'&&stepStatus=='0' && status=='0'}">
	        		<li class="fist done">1.申请取数</li>
			        <li class="done">2.一次审核</li>
			        <li class="last done">3.二次审核</li>
	        	</c:when>
	        	<c:when test="${procesStatus=='3'&&stepStatus=='0' && status=='1'}">
	        		<li class="fist done">1.申请取数</li>
			        <li class="done">2.一次审核</li>
			        <li class="last done">3.二次审核</li>
	        	</c:when>
	        	<c:when test="${procesStatus=='3'&& status=='0'}">
	        		<li class="fist done">1.申请取数</li>
			        <li class="done">2.一次审核</li>
			        <li class="last ">3.二次审核</li>
	        	</c:when>
        	</c:choose>
        </c:if>
        <c:if test="${pType=='2' }">
        	<c:choose>
        		 <c:when test="${processStatus=='1'&&stepStatus=='1' && status=='1'}">
	        		<li class="fist ing">1.修改规则</li>
			        <li>2.一次审核</li>
			        <li class="last">3.二次审核</li>
	        	</c:when>
	        	<c:when test="${procesStatus=='1'&&stepStatus=='0' && status=='1'}">
	        		<li class="fist done">1.修改规则</li>
			        <li >2.一次审核</li>
			        <li class="last">3.二次审核</li>
	        	</c:when>

	        	<c:when test="${procesStatus=='2'&&stepStatus=='1' && status=='1'}">
	        		<li class="fist done">1.修改规则</li>
			        <li class="ing">2.一次审核</li>
			        <li class="last">3.二次审核</li>
	        	</c:when>
	        	<c:when test="${procesStatus=='2'&&stepStatus=='0' && status=='1'}">
	        		<li class="fist done">1.修改规则</li>
			        <li class="done">2.一次审核</li>
			        <li class="last">3.二次审核</li>
	        	</c:when>
	        	<c:when test="${procesStatus=='2' && status=='0'}">
	        		<li class="fist done">1.修改规则</li>
			        <li >2.一次审核</li>
			        <li class="last ">3.二次审核</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='3'&&stepStatus=='1' && status=='0'}">
	        		<li class="fist done">1.修改规则</li>
			        <li class="done">2.一次审核</li>
			        <li class="last ing">3.二次审核</li>
	        	</c:when>
	        	<c:when test="${procesStatus=='3'&&stepStatus=='0' && status=='1'}">
	        		<li class="fist done">1.修改规则</li>
			        <li class="done">2.一次审核</li>
			        <li class="last done">3.二次审核</li>
	        	</c:when>
	        	<c:when test="${procesStatus=='3'&& status=='0'}">
	        		<li class="fist done">1.修改规则</li>
			        <li class="done">2.一次审核</li>
			        <li class="last ">3.二次审核</li>
	        	</c:when>
        	</c:choose>
        </c:if>
        <c:if test="${pType=='3' }">
        	<c:choose>
	        	<c:when test="${procesStatus=='1' && status=='1'&&stepStatus=='1'}">
	        		<li class="fist ing">1.创建</li>
			        <li>2.取数</li>
			        <li>3.数据处理</li>
			        <li>4.一次审批</li>
			        <li>5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='1' && status=='1'&&stepStatus=='0'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li>3.数据处理</li>
			        <li>4.一次审批</li>
			        <li>5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='1' && status=='0'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li>3.数据处理</li>
			        <li>4.一次审批</li>
			        <li>5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='2' && status=='1'&&stepStatus=='1'}">
	        		<li class="fist done">1.创建</li>
			        <li class="ing">2.取数</li>
			        <li>3.数据处理</li>
			        <li>4.一次审批</li>
			        <li>5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	<c:when test="${procesStatus=='2' && status=='1'&&stepStatus=='0'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li>3.数据处理</li>
			        <li>4.一次审批</li>
			        <li>5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='2' && status=='0'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li>3.数据处理</li>
			        <li>4.一次审批</li>
			        <li>5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='3' && status=='1'&&stepStatus=='1'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="ing">3.数据处理</li>
			        <li>4.一次审批</li>
			        <li>5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='3' && status=='1'&&stepStatus=='0'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="done">3.数据处理</li>
			        <li>4.一次审批</li>
			        <li>5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='3' && status=='0'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="done">3.数据处理</li>
			        <li>4.一次审批</li>
			        <li>5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='4' && status=='1'&&stepStatus=='1'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="done">3.数据处理</li>
			        <li class="ing">4.一次审批</li>
			        <li>5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='4' && status=='1'&&stepStatus=='0'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="done">3.数据处理</li>
			        <li class="done">4.一次审批</li>
			        <li>5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='4' && status=='0'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="done">3.数据处理</li>
			        <li class="done">4.一次审批</li>
			        <li>5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='5' && status=='1'&&stepStatus=='1'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="done">3.数据处理</li>
			        <li class="done">4.一次审批</li>
			        <li class="ing">5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	
	        	<c:when test="${procesStatus=='5' && status=='1'&&stepStatus=='0'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="done">3.数据处理</li>
			        <li class="done">4.一次审批</li>
			        <li class="done">5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='5' && status=='0'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="done">3.数据处理</li>
			        <li class="done">4.一次审批</li>
			        <li class="done">5.二次审批</li>
			        <li>6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='6' && status=='1'&&stepStatus=='1'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="done">3.数据处理</li>
			        <li class="done">4.一次审批</li>
			        <li class="done">5.二次审批</li>
			        <li class="ing">6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='6' && status=='1'&&stepStatus=='0'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="done">3.数据处理</li>
			        <li class="done">4.一次审批</li>
			        <li class="done">5.二次审批</li>
			        <li class="done">6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='6' && status=='0'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="done">3.数据处理</li>
			        <li class="done">4.一次审批</li>
			        <li class="done">5.二次审批</li>
			        <li class="done">6.数据推送</li>
			        <li  class="last">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='7' && status=='1'&&stepStatus=='1'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="done">3.数据处理</li>
			        <li class="done">4.一次审批</li>
			        <li class="done">5.二次审批</li>
			        <li class="done">6.数据推送</li>
			        <li  class="last ing">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='7' && status=='1'&&stepStatus=='0'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="done">3.数据处理</li>
			        <li class="done">4.一次审批</li>
			        <li class="done">5.二次审批</li>
			        <li class="done">6.数据推送</li>
			        <li  class="last done">7.已完成</li>
	        	</c:when>
	        	
	        	<c:when test="${procesStatus=='7' && status=='0'}">
	        		<li class="fist done">1.创建</li>
			        <li class="done">2.取数</li>
			        <li class="done">3.数据处理</li>
			        <li class="done">4.一次审批</li>
			        <li class="done">5.二次审批</li>
			        <li class="done">6.数据推送</li>
			        <li  class="last done">7.已完成</li>
	        	</c:when>
        	</c:choose>
        </c:if>
        
  </ul>
  <div id="pageDataList">
	<div class="main_list">
	  <table width="100%" border="0" >
		  <tr>
		    <th>操作人</th>
		    <th>操作时间</th>
		    <th>操作描述</th>
		    <th>督促</th>
		  </tr>
		  <c:forEach var="process" items="${voList }" varStatus="vs">
	  	     <tr ${vs.index % 2 == 1? "class='list_bg'" : "" }>
	  	     	<!--  <td>${(pageObject.curPage-1)*pageObject.pageSize+vs.count }</td> -->
	  	     	
  	     		<td>${process.userIdName}</td>	  	     			    
			    <td><fmt:formatDate value="${process.operateTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			    <td>${process.operateContent}</td>
			    <td></td>			    
			    <c:choose>
		
			    	<c:when test="${process.operateContent==null  && showNote==true}">
			    		<td>
			    			<a href="javascript:sendMsg('${process.dealType}')">督促</a>
			    		</td>
			    	</c:when>
			    	<c:when test="${process.operateContent==null &&  showNote==false}">
			    		<td>
			    			
			    		</td>
			    	</c:when>
			    </c:choose>
			    
			 </tr>
		  </c:forEach>
		  <c:if test="${voList == null || empty voList}">
		  	  <tr>
			    <td colspan=5>查询不到流程进度明细信息</td>
			  </tr>
		  </c:if>
	  </table>
  	</div>
  	<!--<g:page pageObject="${pageObject }" />-->
  </div>
</body>
</html>
