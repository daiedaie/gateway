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
<title>公告信息_数据网关Gateway管理平台</title>
<link href="resource/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="resource/js/jquery-1.11.1.min.js"></script>
<script type="text/javascript" src="resource/js/utils.js"></script>
<script>	
	function updateNotice(){
		$("#noticeContent").val(ue.getContent());
		$("input,textarea").each(function(){
			$(this).val($.trim($(this).val()));
		});
		
		if(check()){
			var action = $("form").attr("action");
			$("#updateNoticeBtn,#updateingNoticeBtn").toggle();
			util.ajax(action, $("form").serialize(), function(json){
				$("#updateNoticeBtn,#updateingNoticeBtn").toggle();
				if(json.state=='success'){
					alert("修改成功");
					location.href="system/notice_searchNoticeList.do";
				}else{
					alert("修改失败："+json.message);
				}
			});
		}
	}
	
	function check(){
		if($("#noticeTitle").val()==''){
			alert("公告标题不能为空！");
			$("#noticeTitle").focus();
			return false;
		}
		if(ue.getContentTxt().trim()==''){
			
			alert("公告内容不能为空！");
			return false;
		}
		return true;
	}
</script>
</head>	
<body>
<div class="main_title">
	<b>修改公告信息</b>
</div>
	<form method="post" action="system/notice_saveNotice.do">
		<input type="hidden" name="notice.noticeId" value="${notice.noticeId}" />
		<input type="hidden" name="notice.noticeContent" id="noticeContent"/>
		<table width="100%" border="0" cellspacing="10px">
			<tr>
				<th>创建人：</th>
				<td>${notice.createUser}</td>
			</tr>
			<tr>
				<th>创建时间：</th>
				<td><fmt:formatDate value="${notice.createTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
			</tr>
			<tr>
				<th>公告标题：</th>
				<td><input name="notice.noticeTitle" id="noticeTitle" type="text" value="${notice.noticeTitle}"/><span class="c_red">*</span>
				</td>
			</tr>
			<tr>
				<th valign="top">公告内容：</th>
				<td>
<!-- 					<textarea rows="10" cols="30" style="width:500px;height:200px" name="notice.noticeContent" id="noticeContent">${notice.noticeContent}</textarea><span class="c_red">*</span> -->
					<script id="ueditorContent" name="ueditorContent" type="text/plain" style="width:800px;height:300px">
						${notice.noticeContent}
					</script>
				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
				<td>
					<c:if test="${SESSION_ATTRIBUTE_USER_BUTTON['updateNotice']!=null}">
					<input name="updateNoticeBtn" id="updateNoticeBtn" type="button" value="确定" onclick="updateNotice()"/>
					<input name="updateingNoticeBtn" id="updateingNoticeBtn" type="button" value="正在保存" style="display:none;color:gray" disabled/>
					<input type="button" value="返回" onclick="history.go(-1)"/>
					</c:if>
				</td>
			</tr>
		</table>
	</form>
</body>
<!-- 配置文件 -->
<script type="text/javascript" src="resource/js/ueditor/ueditor.config.js"></script>
<!-- 编辑器源码文件 -->
<script type="text/javascript" src="resource/js/ueditor/ueditor.all.js"></script>
<script type="text/javascript">
	var ue = UE.getEditor('ueditorContent',{  
      　　toolbars:[['fullscreen', 'source', '|', 'undo', 'redo', '|',
            'bold', 'italic', 'underline', 'fontborder', 'strikethrough', 'superscript', 'subscript', 'removeformat', 'formatmatch', 'autotypeset', 'blockquote', 'pasteplain', '|', 'forecolor', 'backcolor', 'insertorderedlist', 'insertunorderedlist', 'selectall', 'cleardoc', '|',
            'rowspacingtop', 'rowspacingbottom', 'lineheight', '|',
            'customstyle', 'paragraph', 'fontfamily', 'fontsize', '|',
            'directionalityltr', 'directionalityrtl', 'indent', '|',
            'justifyleft', 'justifycenter', 'justifyright', 'justifyjustify', '|', 'touppercase', 'tolowercase', '|',
            'link', 'unlink', 'anchor', '|',
            'pagebreak', 'template', '|',
            'horizontal', 'date', 'time',  '|',
            'inserttable', 'deletetable', 'insertparagraphbeforetable', 'insertrow', 'deleterow', 'insertcol', 'deletecol', 'mergecells', 'mergeright', 'mergedown', 'splittocells', 'splittorows', 'splittocols', '|',
            'preview', 'searchreplace']  
      　　 ],  
          wordCount:true, //开启字数统计  
          elementPathEnabled : false,//是否启用元素路径，默认是显示  
          maximumWords:1000,       //允许的最大字符数  
          initialContent:'',    //初始化编辑器的内容,也可以通过textarea/script给值，看官网例子 
          autoClearinitialContent:false, //是否自动清除编辑器初始内容，注意：如果focus属性设置为true,这个也为真，那么编辑器一上来就会触发导致初始化的内容看不到了  
          pasteplain:true,  //是否默认为纯文本粘贴。false为不使用纯文本粘贴，true为使用纯文本粘贴  
     });  
</script>
</html>
