package com.gztydic.gateway.system;

import java.util.Map;

import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwWorkPlanParamVO;

/** 
 * 系統待办任务参数接口类
 *  
 */
public interface WorkPlanParamService extends GeneralService<GwWorkPlanParamVO>{

	public GwWorkPlanParamVO searchParam(Long planId,String paramName) throws Exception;
	
	public Map<String, String> searchParamMap(Long planId) throws Exception;
	
	public void saveParamMap(Long planId,Map<String, String> paramMap) throws Exception; 
}
