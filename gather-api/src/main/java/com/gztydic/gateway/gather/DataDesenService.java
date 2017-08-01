package com.gztydic.gateway.gather;

import java.util.List;
import java.util.Map;

import com.gztydic.gateway.core.view.GwDesenRuleServiceFieldView;
import com.gztydic.gateway.core.view.GwRuleCheckServiceFieldView;


/** 
 * 数据脱敏处理
 *
 */
public interface DataDesenService {
	
	/**
	 * 实时数据脱敏
	 * @param dataList
	 * @param desenRuleList
	 * @param serviceId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,String>> dataDesen(List<Map<String, String>> dataList,List<GwDesenRuleServiceFieldView> desenRuleList,Long serviceId,Long userId,Map<String, String> ignoreFieldMap) throws Exception;
	
	/**
	 * 数据合规检查
	 * @param dataList
	 * @param checkRuleList
	 * @param serviceId
	 * @param taskId
	 * @return
	 * @throws Exception
	 */
	public String dataCheck(List<Map<String, String>> dataList,List<GwRuleCheckServiceFieldView> checkRuleList,Long serviceId,Long taskId) throws Exception;
}
