<%@page language="java" contentType="application/x-msdownload;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.enterprisedt.net.ftp.FTPClient"%>
<%@ page import="com.enterprisedt.net.ftp.FTPTransferType"%>
<%@ page import="com.gztydic.gateway.core.common.config.ConfigConstants"%>
<%@ page import="com.gztydic.gateway.core.common.constant.SessionConstant"%>
<%@ page import="com.gztydic.gateway.core.vo.GwUserVO"%>
<%@ page import="com.gztydic.gateway.core.common.util.Endecrypt"%>
<%@ page import="com.gztydic.gateway.system.LiabilityService"%>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="org.springframework.context.ApplicationContext"%>
<%@ page import="java.io.*,com.enterprisedt.net.ftp.FTPConnectMode;"%>

<%
///String path = request.getContextPath();
//String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

GwUserVO userVo = (GwUserVO)request.getAttribute("userVo");
String users = userVo.getLoginName();
String password = userVo.getLoginPwd();
String path = null;   
String filename = null;   
path = (String)request.getAttribute("filePath");
filename = (String)request.getAttribute("fileName");  
//filename = "service1001.csv";
Endecrypt endecrypt = new Endecrypt();
String enPassword = endecrypt.get3DESDecrypt(password, SessionConstant.SPKEY_PASSWORD);
//response.setContentType("application/unknown");
//设置为下载application/x-download   
//其中%20是空格在UTF-8下的编码   
//filename = URLEncoder.encode(filename, "UTF-8");   
//filename = new String(filename.getBytes("gb2312"),"ISO8859-1");   
//response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\";");   
String host = ConfigConstants.FTP_SERVER_IP;    
   
//if (path.trim().length() > 1) {
//    path = path.trim() + "/";   
//}   
String remoteFile = path+ "/" + filename; 

//确定源文件的位置   String downFile = "d:/" + filename; 
//确定源文件的位置   
System.out.println("用户："+users + " 下载文件"+remoteFile+"开始................");
response.addHeader("Content-Disposition","attachment;filename=" + filename);   
try {
	out.clear(); 
    out=pageContext.pushBody();
    OutputStream outputStream = response.getOutputStream();
    FTPClient client = new FTPClient();
    client.setRemoteHost(host);
    //client.setDetectTransferMode(true);
    //client.getAdvancedSettings().setTransferBufferSize(2048);
    //client.getAdvancedSettings().setTransferNotifyInterval(5000);
    //client.getAdvancedSettings().setControlEncoding("GBK");
    System.out.println("连接开始"+host+"................");
    client.connect();
    System.out.println("用户开始登录！登录名："+ConfigConstants.FTP_SERVER_USER+"，密码："+ConfigConstants.FTP_SERVER_PASSWORD);
    client.login(ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD);
    System.out.println("登录成功！开始下载文件"+remoteFile);
    client.setConnectMode(FTPConnectMode.ACTIVE);
    client.setType(FTPTransferType.BINARY);
    
    client.get(outputStream, remoteFile);
    outputStream.flush(); 
    //*方式一：将ftp上的文件取出后，写入到response(outputStream)，以response把文件带到浏览器，由浏览器来提示用户是否愿意保存文件到本 
    //*一直存在中文文件名没有解决的问题    client.get(downFile, remoteFile);
    //*方式二：将FTP上文件取出后，直接下载到D盘下
    outputStream.close();
    client.quit();   
    System.out.println("用户："+users + " 下载文件"+remoteFile+"结束................");
}catch (Exception e) {
	ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
	LiabilityService liabilityService =(LiabilityService) ctx.getBean("liabilityServiceImpl");
	long logId=(Long)request.getAttribute("logId");
	liabilityService.updateDataNum(logId);
	System.out.println("用户："+users + " 下载文件"+remoteFile+"失败,原因："+e.getMessage());
   // throw new Exception("文件下载失败："+e.getMessage());
}
%>
