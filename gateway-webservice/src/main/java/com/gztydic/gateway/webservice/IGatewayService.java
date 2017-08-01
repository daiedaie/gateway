package com.gztydic.gateway.webservice;

import javax.jws.WebService;

import com.gztydic.gateway.webservice.client.gateway.ResultResponse;
import com.gztydic.gateway.webservice.client.gateway.ServiceInfoSyncParam;
import com.gztydic.gateway.webservice.client.gateway.ServiceNoticeParam;

@WebService
public interface IGatewayService {
	
	public final static String STATUS_SUCCESS = "1";		//返回状态码，成功
	public final static String STATUS_FAILURE = "0";		//失败
	
	public ResultResponse serviceNotice(ServiceNoticeParam param1);
	
	public ResultResponse serviceInfoSync(ServiceInfoSyncParam param);
}
