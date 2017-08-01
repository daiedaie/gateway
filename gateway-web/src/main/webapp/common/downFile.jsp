<%@page language="java" contentType="application/x-msdownload;charset=UTF-8" pageEncoding="UTF-8" import="java.io.*,java.net.*"%>
<jsp:directive.page import="com.gztydic.gateway.core.common.config.ConfigConstants"/>
<% 
  //关于文件下载时采用文件流输出的方式处理：
  //加上response.reset()，并且所有的％>后面不要换行，包括最后一个；
  response.reset();//可以加也可以不加
  response.setContentType("application/x-download");
  
//application.getRealPath("/main/mvplayer/CapSetup.msi");获取的物理路径
  String filePath = (String)request.getParameter("filePath");
  //String filenamedownload = application.getRealPath(System.getProperty("FileRoot.Path")+filePath);//即将下载的文件的相对路径 
  String filenamedownload = ConfigConstants.BASE_UPLOAD_FILE_PATH+filePath;//绝对路径
 
  //根据不同浏览器区分解决乱码
  String filenamedisplay;
  String agent=request.getHeader("USER-AGENT");
  if(null != agent && -1 != agent.indexOf("Firefox")){//Firefox
  	filenamedisplay = new String(request.getParameter("realName").getBytes("UTF-8"),"iso-8859-1");
  }else{//其他
  	filenamedisplay = URLEncoder.encode(request.getParameter("realName"), "UTF-8");
  }
  //filenamedisplay = URLEncoder.encode(request.getParameter("realName"), "UTF-8");
  response.addHeader("Content-Disposition","attachment;filename=" + filenamedisplay);
  java.io.OutputStream outp = null;
  java.io.FileInputStream in = null;
  try{
	  outp = response.getOutputStream();
	  in = new FileInputStream(filenamedownload);
	
	  byte[] b = new byte[1024];
	  int i = 0;
	
	  while((i = in.read(b)) > 0){
	     outp.write(b, 0, i);
	  }
	  outp.flush(); 
  }catch(Exception e){
     System.out.println("Error!");
     e.printStackTrace();
   }
   finally{
	  if(in != null){
	    in.close();
	    in = null;
	  }
	  
	  if(out != null){
		 out.clear();
		 out = pageContext.pushBody();
	  }
   }
%>
