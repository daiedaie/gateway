function utils(){
	//获取访问地址根目录
	this.getRootPath = function(){
	    //获取当前网址，如： http://localhost:8083/uimcardprj/share/meun.jsp
	    var curWwwPath=window.document.location.href;
	    //获取主机地址之后的目录，如： uimcardprj/share/meun.jsp
	    var pathName=window.document.location.pathname;
	    var pos=curWwwPath.indexOf(pathName);
	    //获取主机地址，如： http://localhost:8083
	    var localhostPaht=curWwwPath.substring(0,pos);
	    //获取带"/"的项目名，如：/uimcardprj
	    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
	    return localhostPaht+projectName;
	}
	
	//ajax统一用这个方法，增加session失效时的页面跳转
	this.ajax = function(url,params,func){
		$.ajax({
			url:url,
			type:"post",
			data:params,
			success:function(data){
				if(jQuery.isFunction(func))
					func(data); 
			},
			error:function(XMLHttpRequest, textStatus, errorThrown){
				if(textStatus == 'timeout'){
					alert("网络连接超时，请检查网络");
				}else if(XMLHttpRequest.status==999){	//999在SessionFilter中定义，session失效时跳转到登录
					location.href = getRootPath()+"/toLogin.jsp";
				}else{
					alert("系统异常，请联系管理员");
				}
			}
		});
	};
	
	this.load = function(id,url,params,func){
		$("#"+id).load(url,params,function(response,status,xmlRequest){
			if(xmlRequest.status==999){	//session失效，重新登录
				location.href = getRootPath()+"/toLogin.jsp";
			}else if(status != 'success' && status != 'notmodified'){
				alert("系统查询异常，请联系管理员");
			}else{
				if(jQuery.isFunction(func))
					func(status); 
			}
		});
	};
}
var util = new utils();