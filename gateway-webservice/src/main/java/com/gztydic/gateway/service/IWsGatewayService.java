package com.gztydic.gateway.service;

import com.gztydic.gateway.webservice.client.gateway.ResultResponse;
import com.gztydic.gateway.webservice.client.gateway.ServiceInfoSyncParam;
import com.gztydic.gateway.webservice.client.gateway.ServiceNoticeParam;

public interface IWsGatewayService {

	public ResultResponse doServiceNotice(ServiceNoticeParam param) throws Exception;
	
	public ResultResponse doServiceInfoSync(ServiceInfoSyncParam param) throws Exception;
	
}
