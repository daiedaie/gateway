package com.gztydic.gateway.webservice.impl;


import javax.annotation.Resource;
import javax.jws.WebService;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.annotations.SchemaValidation;

import com.gztydic.gateway.gather.webservice.client.data.DataResponse;
import com.gztydic.gateway.gather.webservice.client.data.ResponseVO;
import com.gztydic.gateway.service.IWsAppService;
import com.gztydic.gateway.webservice.IAppService;

/*
 * 开发给第三方APP的接口
 *
 */
@SchemaValidation(enabled = true)
@WebService(targetNamespace="http://webservice.gateway.gztydic.com/",endpointInterface="com.gztydic.gateway.webservice.IAppService",serviceName="appService")
public class AppServiceImpl implements IAppService{

	private static Log logger = LogFactory.getLog(AppServiceImpl.class);
	
	@Resource(name="wsAppServiceImpl")
	private IWsAppService appService;

	/**
	 * 从挖掘平台获取实时服务数据
	 */
	public String getDataByServiceId(String param) {
		DataResponse dataResponse = new DataResponse();
		try {
			dataResponse = appService.doGetDataByServiceId(param);
		} catch (Exception e) {
			e.printStackTrace();
			dataResponse.setResult(false);
			dataResponse.setInfo("获取服务数据失败："+e.getMessage());
			logger.error(dataResponse.getInfo(),e);
		}
		return JSONObject.fromObject(dataResponse).toString();
	}
	
	
	/**
	 * 数据特区主动推送实时服务数据
	 */
	public String getOnlineServiceData(String param) {
		ResponseVO response = new ResponseVO();
		try {
			response = appService.doGetOnlineServiceData(param);
		} catch (Exception e) {
			e.printStackTrace();
			response.setResultFlag("0");
			response.setMessage("获取服务数据失败："+e.getMessage());
			logger.error(response.getMessage(),e);
		}
		return JSONObject.fromObject(response).toString();
	}
	
	/**
	 * 外网用户申请服务取数接口
	 */
	public String getDataAreaServiceData(String param) {
		ResponseVO response = new ResponseVO();
		try {
			response = appService.doGetDataAreaServiceData(param);
		} catch (Exception e) {
			e.printStackTrace();
			response.setResultFlag("0");
			response.setMessage("获取服务数据失败："+e.getMessage());
			logger.error(response.getMessage(),e);
		}
		return JSONObject.fromObject(response).toString();
	}
	
	/**
	 * 测试接口
	 */
	public String getServiceData(String param) {
		ResponseVO response = new ResponseVO();
		
		return JSONObject.fromObject(response).toString();
	}
	
	
	
}
