package com.gztydic.gateway.system;

import java.util.List;

import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.vo.GwModelDataCycleVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/**
 * 模型数据结果集存放周期配置
 * @author wangwei
 *
 */
public interface DataCycleService extends GeneralService<GwModelDataCycleVO>{

	/**
	 * 查询所有数据存放周期配置
	 * @return List<GwModelDataCycleVO>
	 * @throws Exception
	 */
	public List<GwModelDataCycleVO> searchList() throws Exception;
	
	/**
	 * 新增、修改周期配置
	 */
	public void updateDataCycleList(List<GwModelDataCycleVO> list,GwUserVO userVO) throws Exception;
	
	/**
	 * 根据数据存放周期配置查询过期缓存数据
	 */
	public void searchCleanCacheData() throws Exception;
	
	/**
	 * 根据数据存放周期配置清理过期缓存数据
	 */
	public void cleanCacheData() throws Exception;
}
