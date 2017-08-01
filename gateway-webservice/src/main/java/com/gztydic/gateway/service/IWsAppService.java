package com.gztydic.gateway.service;

import com.gztydic.gateway.gather.webservice.client.data.DataResponse;
import com.gztydic.gateway.gather.webservice.client.data.ResponseVO;

public interface IWsAppService {

	public DataResponse doGetDataByServiceId(String param) throws Exception;
	
	public ResponseVO doGetOnlineServiceData(String param) throws Exception;
	
	public ResponseVO doGetDataAreaServiceData(String param) throws Exception;
}
