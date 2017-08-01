package com.gztydic.gateway.system;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.dao.GwServiceDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.vo.GwModelVO;
import com.gztydic.gateway.core.vo.GwServiceVO;

@Service
public class GwServiceServiceImpl extends GeneralServiceImpl<GwServiceVO> implements GwServiceService {

	@Resource
	private GwServiceDAO serviceDAO;
	
	public GwServiceVO searchService(Long serviceId) throws Exception {
		return super.search(GwServiceVO.class, serviceId);
	}
	
	public Map<Long, GwServiceVO> searchServiceMapByModel(GwModelVO model) throws Exception{
		List<GwServiceVO> list = serviceDAO.searchServiceByModel(model.getModelId());
		Map<Long, GwServiceVO> map = new HashMap<Long, GwServiceVO>();
		for (GwServiceVO vo : list) {
			map.put(vo.getServiceId(), vo);
		}
		return map;
	}
}
