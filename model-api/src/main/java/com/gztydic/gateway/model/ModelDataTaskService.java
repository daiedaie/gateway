package com.gztydic.gateway.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.view.ServiceCycleAppView;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchVO;
import com.gztydic.gateway.core.vo.GwModelDataFileVO;
import com.gztydic.gateway.core.vo.GwOperationLogVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwServiceFieldVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/** 
 * @ClassName: ModelDataTaskService 
 * @Description: TODO(模型取数任务相关接口类) 
 * @author davis
 * @date 2014-11-19 下午02:58:47 
 *  
 */
public interface ModelDataTaskService extends GeneralService<GwModelDataFetchTaskVO>{

	/* 
	 * <p>Title: updateTaskStatusByTaskId</p> 
	 * <p>Description: 根据模型取数任务ID修改任务状态</p> 
	 * @param taskId 任务ID
	 * @param taskStatus 目标任务状态
	 * @throws Exception  
	 */
	public void updateTaskStatusByTaskId(Long taskId,String taskStatus,GwUserVO loginUserVO) throws Exception;
	
	/** 
	 * @Title: searchAllPage 
	 * @Description: TODO(全查询模型取数任务列表信息) 
	 * @param @param modelCode
	 * @param @param modelName
	 * @param @param userName
	 * @param @param startDate
	 * @param @param endDate
	 * @param @param pageObject
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return PageObject    返回类型 
	 * @throws 
	 */
	public PageObject searchAllPage(String modelCode,String modelName,String userName,String serviceCode,Date startDate,Date endDate,String dataProgressStatus,PageObject pageObject) throws Exception;
	
	/** 
	 * @Title: searchAllPage 
	 * @Description: TODO(查询某些用户模型取数任务列表信息) 
	 * @param @param userIds
	 * @param @param modelCode
	 * @param @param modelName
	 * @param @param userName
	 * @param @param startDate
	 * @param @param endDate
	 * @param @param pageObject
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return PageObject    返回类型 
	 * @throws 
	 */
	public PageObject searchAllPage(String userIds,String modelCode,String modelName,String userName,String serviceCode,Date startDate,Date endDate,String dataProgressStatus,PageObject pageObject) throws Exception;

	/**
	 * @Title: searchOnlineServiceData 
	 * @Description: TODO(实时取数并脱敏方法) 
	 * @param @param fetchVO
	 * @param @return    设定文件 
	 * @return Map    返回类型 
	 * @throws
	 */
	public Map searchOnlineServiceData(GwModelDataFetchVO fetchVO,GwUserVO loginUser)throws Exception;

	public Map<String, String> searchServiceName(Long serviceId)throws Exception;


	/**
	 * @Title: searchServiceTaskNoLocal 
	 * @Description: TODO(查询相同服务ID相同周期申请任务列表) 
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List    返回类型 
	 * @throws
	 */
	public List<ServiceCycleAppView> searchServiceTaskNoLocal()throws Exception;
	
	/**
	 * 
	 * @Title: getServiceDataAppAndUpdate 
	 * @Description: TODO(某一服务申请并修改不同用户对同服务同周期任务状态修改) 
	 * @param @param appView
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public void doServiceDataAppAndUpdate(ServiceCycleAppView appView) throws Exception;

	public Map<String, Object> searchOnlinePreviewServiceData(GwModelDataFetchTaskVO taskVO) throws Exception;
	
	public Map<String, Object> searchOfflinePreviewServiceData(GwModelDataFetchTaskVO taskVO,PageObject pageObject) throws Exception;
	
	public GwModelDataFetchTaskVO searchById(Long taskId) throws Exception;
	
	public GwModelDataFileVO searchTaskFileInfo(Long taskId,String fileType) throws Exception;
	
	//查询不合规数据，并导出到txt
	public String searchRuleCheckDataForTxt(GwModelDataFetchTaskVO taskVO,List<GwServiceFieldVO> fieldList) throws Exception;
	public HSSFWorkbook exportCheckResult(String taskId,String exportNum) throws Exception;
		
	public GwServiceVO searchServiceByTaskId(String taskId) throws Exception;
	
	/**查询任务统计项*/
	public Map searchTaskCount(String userIds,String modelCode,String modelName,String loginName,String serviceCode,Date startDate,Date endDate) throws Exception;
	
	/**
	 * 108文件发送到ftp失败后重新发送
	 * @throws Exception 
	 */
	public void doRepush108Ftp() throws Exception;
	
	
	/**
	 * 重新做任务检查
	 * @throws Exception 
	 */
	public void redoTask(Long taskId, GwOperationLogVO logVO) throws Exception;
	
	//查询不合规数据量
	public String searchServiceCheckRecord(Long taskId)throws Exception;
}
