package com.gztydic.gateway.system;

import java.util.Map;

import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwModelVO;
import com.gztydic.gateway.core.vo.GwServiceVO;

public interface GwServiceService extends GeneralService<GwServiceVO>{
	
	public GwServiceVO searchService(Long serviceId) throws Exception;
	
	public Map<Long, GwServiceVO> searchServiceMapByModel(GwModelVO model) throws Exception;
}
