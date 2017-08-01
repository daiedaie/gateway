package com.gztydic.gateway.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.gztydic.gateway.core.common.util.SpringUtils;
import com.gztydic.gateway.web.quartz.DataPushFtpJob;

public class InitServlet extends HttpServlet {
	
	private final Log log = LogFactory.getLog(InitServlet.class);
	
	public void destroy() {
		
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	
	public void init() throws ServletException {
		ApplicationContext ac = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext());
		SpringUtils.setApplicationContext(ac);
		
		DataPushFtpJob dataPushFtpJob = (DataPushFtpJob)ac.getBean("dataPushFtpJob");
		dataPushFtpJob.start();
		log.info("pushFtp定时器初始化成功");
	}
}
