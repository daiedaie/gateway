package com.gztydic.gateway.system;

import java.util.List;
import java.util.Map;

import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwServiceDictVO;
import com.gztydic.gateway.core.vo.GwServiceFieldDictVO;
import com.gztydic.gateway.core.vo.GwServiceVO;


public interface ServiceDictService extends GeneralService<GwServiceDictVO>{

	/**
	 * 字典表合成字典组
	 * @return
	 * @throws Exception
	 */
	public List<GwServiceDictVO> searchGroupDict() throws Exception;
	
	/**
	 * 查询所有字典
	 * @return
	 * @throws Exception
	 */
	public Map<String, List<GwServiceDictVO>> searchDictMap() throws Exception;
	
	/**
	 * 查询字段与字典关联表数据
	 * 优先根据模型查询，否则根据服务
	 * @param serviceVO
	 * @return
	 * @throws Exception
	 */
	public Map searchFieldDictMap(Long userId,GwServiceVO serviceVO) throws Exception;
	
	public Map searchFieldDictMapByBatch(Long batch) throws Exception;
	
	public Map searchFieldDictAuditMap(Long batch) throws Exception;
}
