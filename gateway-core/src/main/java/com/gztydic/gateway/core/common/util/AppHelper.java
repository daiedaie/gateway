package com.gztydic.gateway.core.common.util;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

public class AppHelper implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final String CONTENT_TYPE_XML  = "xml";
	public static final String CONTENT_TYPE_HTML = "html";
	public static final String CONTENT_TYPE_JSON = "json";
	

	/**
	 * @description 输入内容到前端页面
	 */
	public static void writeOut(String content , HttpServletResponse response) {
		writeOut(content, CONTENT_TYPE_HTML , response);
	}
	
	public static void writeOut(Object object , HttpServletResponse response) {
		JSONObject json = JSONObject.fromObject(object);
		writeOut(json.toString(), CONTENT_TYPE_JSON , response);
	}
	
	public static void writeOut(AjaxResult ajaxResult , HttpServletResponse response) {
		JSONObject json = JSONObject.fromObject(ajaxResult);
		writeOut(json.toString(), CONTENT_TYPE_JSON , response);
	}
	
	public static void writeOut(AjaxResult ajaxResult , String contentType,HttpServletResponse response) {
		JSONObject json = JSONObject.fromObject(ajaxResult);
		writeOut(json.toString(), contentType , response);
	}
	
	public static void writeOut(String content, String contentType , HttpServletResponse response) {
		if(content == null) content="";

		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		if (CONTENT_TYPE_XML.equalsIgnoreCase(contentType)) {
			response.setContentType("text/xml; charset=UTF-8");
		} else if (CONTENT_TYPE_JSON.equalsIgnoreCase(contentType)) {
			response.setContentType("text/json; charset=UTF-8");
		} else {
			response.setContentType("text/html; charset=UTF-8");
		}
		try {
			response.getWriter().write(content);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
