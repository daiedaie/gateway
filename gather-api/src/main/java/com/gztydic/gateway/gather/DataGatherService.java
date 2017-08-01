package com.gztydic.gateway.gather;

import java.util.List;
import java.util.Map;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleVO;
import com.gztydic.gateway.core.vo.GwServiceFieldVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.gather.webservice.client.data.ResponseVO;
import com.gztydic.gateway.gather.webservice.client.data.DataResponse;


/**
 * 数据获取
 * 
 */
public interface DataGatherService {
	
	/**
	 * 预览实时服务脱敏数据
	 * @param serviceId
	 * @param taskId
	 * @param userVO
	 * @return
	 * @throws Exception 
	 */
	public Map getOnlineServiceData(Long serviceId, GwModelDataFetchTaskVO taskVO, GwUserVO userVO) throws Exception;
	
	/**
	 * 预览实时服务脱敏数据
	 * @param serviceId
	 * @param taskId
	 * @param userVO
	 * @return
	 * @throws Exception 
	 */
	public Map getOnlinePreviewServiceData(GwModelDataFetchTaskVO taskVO) throws Exception;
	
	/**
	 * 获取服务数据
	 * @param serviceId
	 * @param taskId
	 * @param userId
	 * @throws Exception
	 */
	public DataResponse doGetServiceData(GwServiceVO serviceVO, GwModelDataFetchTaskVO taskVO, GwUserVO userVO) throws Exception;
	 
	public ResponseVO doPutServiceData(String authortyGno,String desenType,List<GwServiceFieldVO> columnList,GwServiceVO serviceVO, GwModelDataFetchTaskVO taskVO, GwUserVO userVO,List<Map<String, String>> dataList,String webserviceUrl,String webserviceMethod,String baseWsdl) throws Exception;
	
	public ResponseVO doGetDataAreaServiceData(String desenType,List<GwServiceFieldVO> columnList,GwServiceVO serviceVO, GwModelDataFetchTaskVO taskVO, GwUserVO userVO,String authortyTno,String period,String webserviceUrl,String webserviceMethod,String baseWsdl) throws Exception;	
	/**
	 * 查询合规检查不通过的数据
	 * @param serviceId
	 * @param taskId
	 * @param pageObject
	 * @param ignoreFieldMap
	 * @return
	 * @throws Exception
	 */
	public PageObject searchRuleCheckAuditList(GwServiceVO serviceVO,Long taskId,PageObject pageObject)throws Exception;
	
	/**
	 * 根据taskId,行号查询出所有不合规的字段，并且组装成Map
	 * @param taskId
	 * @param pageObject
	 * @return
	 * @throws Exception
	 */
	public Map searchRuleCheckAuditField(GwModelDataFetchTaskVO taskVO,PageObject pageObject) throws Exception;
	
	/**
	 * 根据check_batch，查询行数
	 * @param taskVO
	 * @return
	 * @throws Exception
	 */
	public int searchRowCount(GwModelDataFetchTaskVO taskVO) throws Exception;
	
	/**
	 * 查询warnRow，rowData
	 * @param taskVO
	 * @param warnType
	 * @return
	 * @throws Exception
	 */
	public Map searchServiceCheckWarn(GwModelDataFetchTaskVO taskVO,long warnType) throws Exception;
	
	/**
	 * 根据checkBatch查询字段，组装表头
	 * @param taskVO
	 * @return
	 * @throws Exception
	 */
	public List<GwServiceCheckRuleVO> searchServiceFieldCode(GwModelDataFetchTaskVO taskVO)throws Exception;
	
	public List<String> searchFieldTitleList(GwServiceVO serviceVO,Long taskId)throws Exception;
}
