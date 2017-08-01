function searchPage(pageNum){
	if(!pageNum) pageNum = 1;
	jumpPage(pageNum);
}

function jumpPage(pageNum,func){
	var frm = $("form")[0];
	if(!dataCheck(frm)){
		return false;
	}
	pageNum = parseInt(pageNum);
	var pageCount = parseInt($("#pageCount").val());
	if(!pageNum || pageNum==''){
		alert("跳转页数不能为空");
		return false;
	}
	if(pageNum != 1 && pageNum > pageCount){
		alert("跳转页数不能大于总页数");
		return false;
	}
	if(pageNum <= 0){
		alert("跳转页数不能小于等于0");
		return false;
	}
	$("#curPage").val(pageNum);	//跳转目标页
	var url = addTimestamp(frm.action);
	loadContainer = $(frm).attr("loadContainer");
	if(loadContainer && loadContainer!='') url += " #"+loadContainer;	//form表单的loadContainer属性不为空时,追加指定容器的内容到pageDataList
	$("#pageDataList").load(url,$(frm).serialize(),function(response,status,xmlRequest){
		if(status == 'timeout'){
			alert("网络连接超时，请检查网络");
		}else if(xmlRequest.status==999){	//session失效，重新登录
			location.href = util.getRootPath()+"/toLogin.jsp";
		}else if(status != 'success' && status != 'notmodified'){
			alert("系统查询异常，请联系管理员");
		}
		if($.isFunction(func)) func();
		showTableSort();
	});
}

//加时间戳
function addTimestamp(url){
	var symbol = "?";
	if(url.indexOf("?") > 0)
		symbol = "&";
	url = url + symbol + "timestamp="+new Date().getTime();
	return url;
}

function dataCheck(frm){
	var boolean = true;
	$(frm).find("input[type=text],textarea").each(function(){	//有notEmpty属性的需要验证非空
		$(this).val($.trim($(this).val()));
		if($(this).attr("notEmpty") && $(this).val()==''){
			alert($(this).attr("notEmpty")); 
			$(this).focus();
			boolean = false;
			return false;
		}
	});
	if(!boolean) return false;
	return true;
}

//列表排序
function orderby(th){
	var sort = $(th).attr("sort");	//选择的排序字段
	var asc = $("#asc").val();		//原来的排序方式 asc or desc
	if(!asc || asc=='desc' || sort!=$("#sort").val()) asc = "asc";	//选择的排序字段与原来的排序字段不相同，则使用asc排序
	else asc = "desc";
	$("#sort").val(sort);	//设置排序字段，后台pageObjct获取
	$("#asc").val(asc);		//设置排序类型，后台pageObjct获取
	searchPage();
}

//表头排序设置箭头
function showTableSort(){
	//分页查询后设置排序字段的上下箭头
	var sort = $("#sort").val();	//选择的排序字段
	if(sort && sort != ''){
		var asc = $("#asc").val();
		$("[sort="+sort+"]").removeClass();
		if(asc == 'asc') $("[sort="+sort+"]").addClass("tableSortUp");
		else if(asc == 'desc') $("[sort="+sort+"]").addClass("tableSortDown");
	}
}

//表头排序绑定事件
$(document).ready(function(){
	$(document).on("click",".tableSort,.tableSortUp,.tableSortDown",function(){
		orderby(this);
	});
});
