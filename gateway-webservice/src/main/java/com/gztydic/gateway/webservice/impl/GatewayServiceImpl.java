package com.gztydic.gateway.webservice.impl;


import javax.annotation.Resource;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.annotations.SchemaValidation;

import com.gztydic.gateway.service.IWsGatewayService;
import com.gztydic.gateway.webservice.IGatewayService;
import com.gztydic.gateway.webservice.client.gateway.ResultResponse;
import com.gztydic.gateway.webservice.client.gateway.ServiceInfoSyncParam;
import com.gztydic.gateway.webservice.client.gateway.ServiceNoticeParam;

@SchemaValidation(enabled = true)
@WebService(targetNamespace="http://webservice.gateway.gztydic.com/",endpointInterface="com.gztydic.gateway.webservice.IGatewayService",serviceName="gatewayService")
public class GatewayServiceImpl implements IGatewayService{

	private static Log logger = LogFactory.getLog(GatewayServiceImpl.class);
	
	@Resource(name="wsGatewayServiceImpl")
	private IWsGatewayService gatherService;
	
	//服务通知
	public ResultResponse serviceNotice(ServiceNoticeParam param){
		ResultResponse response = new ResultResponse();
		try {
			response = gatherService.doServiceNotice(param);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespCode(STATUS_FAILURE);
			response.setRespMsg("接收服务数据生成通知失败："+e.getMessage());
			logger.error(response.getRespMsg(),e);
		}
		return response;
	}
	
	//服务信息同步
	public ResultResponse serviceInfoSync(ServiceInfoSyncParam param){
		ResultResponse response = new ResultResponse();
		try {
			response = gatherService.doServiceInfoSync(param);
		} catch (Exception e) {
			e.printStackTrace();
			response.setRespCode(STATUS_FAILURE);
			response.setRespMsg("接收服务信息同步失败："+e.getMessage());
			logger.error(response.getRespMsg(),e);
		}
		return response;
	}
}
