package com.gztydic.gateway.gather.webservice;

import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.gather.webservice.client.data.DataResponse;


public interface IDataService {

	public DataResponse getServiceData(GwServiceVO serviceVO,GwModelDataFetchTaskVO taskVO);
}
