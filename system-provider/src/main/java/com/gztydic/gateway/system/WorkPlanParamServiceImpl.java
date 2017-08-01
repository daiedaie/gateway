package com.gztydic.gateway.system;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.dao.GwWorkPlanParamDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.vo.GwWorkPlanParamVO;

/** 
 * 系统待办任务参数实现方法类 
 *  
 */
@Service
public class WorkPlanParamServiceImpl  extends GeneralServiceImpl<GwWorkPlanParamVO> implements WorkPlanParamService {

	@Resource
	private GwWorkPlanParamDAO gwWorkPlanParamDAO;
	
	public GwWorkPlanParamVO searchParam(Long planId, String paramName)	throws Exception {
		return gwWorkPlanParamDAO.findByPlan(planId, paramName);
	}

	public Map<String, String> searchParamMap(Long planId) throws Exception {
		List<GwWorkPlanParamVO> list = gwWorkPlanParamDAO.findByPlan(planId);
		Map<String, String> map = new HashMap<String, String>();
		for (GwWorkPlanParamVO vo : list) {
			map.put(vo.getParamName(), vo.getParamValue());
		}
		return map;
	}

	public void saveParamMap(Long planId, Map<String, String> paramMap)	throws Exception {
		Iterator<String> it = paramMap.keySet().iterator();
		GwWorkPlanParamVO vo = null;
		while (it.hasNext()) {
			String key = it.next();
			vo = new GwWorkPlanParamVO();
			vo.setPlanId(planId);
			vo.setParamName(key);
			vo.setParamValue(paramMap.get(key));
			gwWorkPlanParamDAO.save(vo);
		}
	}
	
}
