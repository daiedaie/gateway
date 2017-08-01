<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ include file="/common/include.jsp"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
<base href="<%=basePath %>"/>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript">
  $(function(){
	  
      var dlhead = $("dl").has("li"); 
	  //dlhead.children("dd").hide();
	  dlhead.find(".open").hide();
	  dlhead.each(function(){
	  	  $(this).children("dd").hide();
		  $(this).find(".close").hide();
		  $(this).find(".open").show();
	  });
	  
	  dlhead.children("dt").click(function(){
		  $(this).find(".close").toggle();
		  $(this).find(".open").toggle(); 
		  $(this).next("dd").toggle();
	  }) 
	  
	  
	  $("li").click(function(){
	  	  $("li").removeClass();
	  	  $(this).addClass("selected");
	  });
  })

</script>
</head>

<body class="FrameMenu">

	<dl>
    	<dt class="ind"><a href="system/func_searchMain.do" target="mainFrame">首页</a></dt>
    </dl>
	
	<dl>
    <c:forEach var="p" items="${SESSION_ATTRIBUTE_USER_FUNC}">
    	<c:if test="${not empty p.value}">
    	<dt><span class="open">展开</span><span class="close">收缩</span>${p.value.funcName }</dt>
	    	<c:if test="${not empty p.value.viewList}">
	   		<dd>
	        	<ul>
		    	<c:forEach var="v" items="${p.value.viewList}">
	            	<li><a href="${v.funcUrl }" target="mainFrame">${v.funcName }</a></li>
		    	</c:forEach>
	            </ul>
	        </dd>
	    	</c:if>
    	</c:if>
    </c:forEach>
	</dl>
</body>
</html>
