package com.gztydic.gateway.model;

import java.util.Date;
import java.util.List;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.view.UserModelServiceAppVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchVO;
import com.gztydic.gateway.core.vo.GwOperationLogVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/** 
 * @ClassName: ModelDataAppService 
 * @Description: TODO(模型取数申请相关接口类) 
 * @author davis
 * @date 2014-11-20 上午10:11:31 
 *  
 */
public interface ModelDataAppService extends GeneralService<GwModelDataFetchVO>{

	/** 
	 * @Title: searchUserModelAppList 
	 * @Description: TODO(查询用户下的模型服务申请记录信息) 
	 * @param @param userId 用户ID
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<UserModelAppVo>    返回类型 
	 * @throws 
	 */
	public List<UserModelServiceAppVO> searchUserModelServiceAppList(Long userId) throws Exception;
	
	
	public PageObject searchServiceAppList(Long userId,String userType,UserModelServiceAppVO view,PageObject pageObject)throws Exception;
	
	
	/** 
	 * @Title: searchUserModelServiceAppInfo 
	 * @Description: TODO(查询用户服务申请记录信息) 
	 * @param @param userId 用户Id
	 * @param @param modeId 服务Id
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return GwModelDataFetchVO    返回类型 
	 * @throws 
	 */
	public GwModelDataFetchVO searchUserServiceAppInfo(Long userId,Long serviceId)throws Exception;
	
	/** 
	 * @Title: updateModelAppState 
	 * @Description: TODO(更新模型取数申请审核状态) 
	 * @param @param userId 用户ID
	 * @param @param modelId 模型ID
	 * @param @param auditStatus 审核状态
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */
	public void updateModelAppState(Long userId,Long modelId,String auditStatus)throws Exception;

	/** 
	 * @Title: searchById 
	 * @Description: TODO(根据表主键查询模型取数对象) 
	 * @param @param fetchId
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return GwModelDataFetchVO    返回类型 
	 * @throws 
	 */
	public GwModelDataFetchVO searchById(Long fetchId)throws Exception;
	
	/** 
	 * @Title: updateModelAppVo 
	 * @Description: TODO(更新模型提数申请对象) 
	 * @param @param modelDataFetchVO
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */
	public void updateModelAppVo(GwModelDataFetchVO modelDataFetchVO)throws Exception;
	
	/**
	 * @Title: createOfflineTask 
	 * @Description: TODO(离线服务审核通过创建取数任务) 
	 * @param @param fetchId
	 * @param @return    设定文件 
	 * @return boolean    返回类型 
	 * @throws
	 */
	public boolean createOfflineTask(Long fetchId);
	
	public GwModelDataFetchVO doVerifyApp(String passTag,String planId,String suggestion,GwUserVO loginUser) throws Exception;
	
	public void onlineCheck(GwUserVO acceptUser, GwServiceVO serviceVO,Long fetchId,Date auditTime,String fileList, GwOperationLogVO logVO,GwUserVO loginUser,GwUserVO orgUser) throws Exception;


	public GwModelDataFetchVO serviceFetchAuditDeal(String passTag,String planId, String suggestion, GwUserVO loginUser)throws Exception ;
	
	/**
	 * 创建新的取数任务
	 * 
	 */
	public void createNewServiceTask(GwUserVO acceptUser, GwServiceVO serviceVO,Long fetchId,Date auditTime,String fileName, GwOperationLogVO logVO,GwUserVO loginUser,GwUserVO orgUser,String processId) throws Exception;
	
	/** 
	 * @Title: searchUserlServiceList 
	 * @Description: TODO(根据用户ID和审核状态查询用户下所有模型服务) 
	 * @param @param userId 用户ID
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<UserModelAppVo>    返回类型 
	 * @throws 
	 */
	public List<UserModelServiceAppVO> searchUserServiceList(Long userId, String auditStatus) throws Exception;
	
	/** 
	 * @Title: searchUserlServiceList 
	 * @Description: TODO(根据用户ID和服务id查询模型服务) 
	 * @param @param userId 用户ID
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<UserModelAppVo>    返回类型 
	 * @throws 
	 */
	public UserModelServiceAppVO searchServiceApp(Long userId,Long serviceId,String auditStatus)  throws Exception;
}
