package com.gztydic.gateway.system;

import java.util.Map;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.view.GwModelLiabilityLogView;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelLiabilityLogVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/** 
 * 免责日志接口类 
 *  
 */
public interface LiabilityService extends GeneralService<GwModelLiabilityLogVO>{

	/**
	 * 查询免责日志列表
	 * @param view
	 * @param pageObject
	 * @return
	 * @throws Exception
	 */
	public PageObject searchLiabilityLogList(GwModelLiabilityLogView view,PageObject pageObject) throws Exception;
	
	/**
	 * 根据logId查询日志
	 * @param logId
	 * @return
	 * @throws Exception
	 */
	public GwModelLiabilityLogView searchLiabilityLog(GwModelLiabilityLogView view) throws Exception;
	
	/**
	 * 敏感信息追溯列表
	 * @param view
	 * @param pageObject
	 * @return
	 * @throws Exception
	 */
	public PageObject searchDesenList(GwModelLiabilityLogView view,PageObject pageObject,String searchBy) throws Exception;
	
	public GwModelLiabilityLogVO saveLiabilityLog(GwModelDataFetchTaskVO taskVO,GwUserVO loginUser)throws Exception ;
	
	/** 查询免责日志统计信息 */
	public Map searchLiabilityCount(GwModelLiabilityLogView view) throws Exception;
	
	//下载文件，点击“取消”，数据量置0
	public void updateDataNum(Long logId)throws Exception;
}
