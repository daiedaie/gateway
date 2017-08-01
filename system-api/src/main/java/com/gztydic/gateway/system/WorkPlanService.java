package com.gztydic.gateway.system;

import java.util.List;
import java.util.Map;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.view.GwWorkPlanView;
import com.gztydic.gateway.core.view.UserAuditView;
import com.gztydic.gateway.core.view.WorkPlanView;
import com.gztydic.gateway.core.vo.GwDesenServiceInfoVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModifyRecordVO;
import com.gztydic.gateway.core.vo.GwOrgVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;

/** 
 * @ClassName: WorkPlanService 
 * @Description: TODO(系統待办任务接口类) 
 * @author davis
 * @date 2014-11-20 下午04:37:05 
 *  
 */
public interface WorkPlanService extends GeneralService<GwWorkPlanVO>{

	/** 
	 * @Title: saveWorkPlan 
	 * @Description: TODO(创建系统待办任务) 
	 * @param planTitle 标题
	 * @param planType  待办类型
	 * @param planContent 内容
	 * @param planState 待办任务状态，默认为1待处理
	 * @param planLevel 待办级别（扩展字段暂时可以为空）
	 * @param extenTableKey 生成待办数据处理表关键字段值
	 * @param createUserId 创建用户ID，可以为空
	 * @param dealUserId 如果待办是生成给数据用户或者机构用户该参数不能为空，管理员则可以为空
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */
	public GwWorkPlanVO saveWorkPlan(String planTitle,String planType,String planContent,String planState,String planLevel,String extenTableKey,Long createUserId,Long dealUserId,Long parentPlanId,String msgContent,String processId) throws Exception;
	
	public GwWorkPlanVO saveWorkPlan(String planTitle,String planType,String planContent,String planState,String planLevel,String extenTableKey,Long createUserId,Long dealUserId,Long parentPlanId,Map<String, String> paramMap,String msgContent) throws Exception;
	
	/** 
	 * @Title: updateWorkPlanState 
	 * @Description: TODO(待办任务状态更新) 
	 * @param @param planId 待办ID
	 * @param @param planState 新待办状态
	 * @param suggestion 处理意见
	 * @param @param dealUserId 处理用户ID
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws 
	 */
	public GwWorkPlanVO updateWorkPlanState(Long planId,String suggestion,String planState,Long dealUserId)throws Exception;
	
	/** 
	 * @Title: searchUserWorkPlan 
	 * @Description: TODO(查询某用户待办任务列表) 
	 * @param dealUserId 用户ID
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwWorkPlanVO>    返回类型 
	 * @throws 
	 */
	public List<GwWorkPlanVO> searchUserWorkPlan(Long dealUserId)throws Exception;
	
	/** 
	 * @Title: searchUserWorkPlan 
	 * @Description: TODO(查询某用户待办任务列表) 
	 * @param planTypes 待办类型
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwWorkPlanVO>    返回类型 
	 * @throws 
	 */
	public List<GwWorkPlanVO> searchWorkPlanByType(String planTypes)throws Exception;
	
	/** 
	 * @Title: searchByParam 
	 * @Description: TODO(根据多条件查询待办任务列表) 
	 * @param planType
	 * @param planTitle
	 * @param planContent
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwWorkPlanVO>    返回类型 
	 * @throws 
	 */
	public PageObject searchByParam(String  planTypes,String  planType,String planTitle,String planContent,PageObject pageObject,String planState)throws Exception;
	
	/** 
	 * @Title: searchByParam 
	 * @Description: TODO(根据多条件查询某用户待办任务列表) 
	 * @param
	 * @param  planType
	 * @param  planTitle
	 * @param planContent
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwWorkPlanVO>    返回类型 
	 * @throws 
	 */
	public PageObject searchByParam(Long  userId,String  planType,String planTitle,String planContent,PageObject pageObject,String planState)throws Exception;
	
	
	/**
	 * 用户注销，审核通过
	 * @param loginUser
	 * @param cancelUser
	 * @param workPlanVO
	 * @throws Exception
	 */
	public int cancelUserAudit(GwUserVO loginUser,GwUserVO cancelUser,GwWorkPlanVO workPlanVO) throws Exception;
	
	/**
	 * 用户注销，审核回退
	 * @param loginUser
	 * @param cancelUser
	 * @param workPlanVO
	 * @throws Exception
	 */
	public void cancelUserRollback(GwUserVO loginUser,GwUserVO cancelUser,GwWorkPlanVO workPlanVO) throws Exception;
	
	/** 
	 * @Title: searchById 
	 * @Description: TODO(根据关键字查询待办对象) 
	 * @param @param planId
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return GwWorkPlanVO    返回类型 
	 * @throws 
	 */
	public GwWorkPlanVO searchById(Long planId) throws Exception;


	public List searchUpdateUserVerify(Integer batchId)throws Exception;
	
	public void verifyUpdateUser(GwUserVO user,GwOrgVO org, GwModifyRecordVO modifyRecord,GwUserVO loginUser,
			String passTag,String planId,String suggestion)throws Exception;
	
	public List<GwWorkPlanVO> searchWorkPlan(String planType,String planState,String[] extenTableKey) throws Exception;
	
	public PageObject findUserAuditByParam(UserAuditView userVO,PageObject pageObject,String planTypes)throws Exception;
	
	public List<WorkPlanView> searchWorkPlanByUpdate(String planType,String planState,String[] paramValue) throws Exception;
	
	public List<WorkPlanView> searchWorkPlanForDesenService(String[] planType,String planState,String[] paramValue,String[] serviceIds) throws Exception;
	
	//安全管理员服务信息查看审核
	public void verifyServiceInfoApp(String planType,GwDesenServiceInfoVO desenModelInfo,GwWorkPlanVO workPlan,
			String suggestion,GwUserVO loginUser,String passTag,String userId,String serviceId)throws Exception;
	
	//审核管理员服务信息查看审核
	public void verifyServiceInfoAuditApp(String planType,GwDesenServiceInfoVO desenModelInfo,GwWorkPlanVO workPlan,
			String suggestion,GwUserVO loginUser,String passTag,String userId,String serviceId)throws Exception;
	
	public void verifyServiceInfoAuditBackApp(String planType,GwDesenServiceInfoVO desenModelInfo,String planId,
			String suggestion,GwUserVO loginUser,String userId,String serviceId)throws Exception;
	
	public List<WorkPlanView> searchWorkPlanByForService(String[] planType,String planState,String paramValue,String serviceIds,String planSource) throws Exception;
	
	//数据安全管理员合规检查审核
	public void verifyRuleCheckAuditForSafeUser(String passTag,String planId,String suggestion,String taskId,String acceptUserId,String serviceId,GwUserVO loginUser)throws Exception;
	
	//审核管理员合规检查审核
	public GwModelDataFetchTaskVO verifyRuleCheckAuditForAuditUser(String passTag,String planId,String suggestion,String taskId,String userId,String serviceId,GwUserVO loginUser)throws Exception;
	
	//将未处理的不合规检查审核待办设为失效
	public int updateCheckWorkPlanInvalid(Long userId,Long serviceId) throws Exception;
	
	//查询历史待办记录
	public List<GwWorkPlanView> searchHistoryWorkPlan(Long planId)throws Exception;
	
	public List<GwWorkPlanVO> findByCreateTime(String creatTime)throws Exception;
		
	/**
	 * 
	 * @Title: verifyRegisteUserPass 
	 * @Description: TODO(用户注册通过审核处理) 
	 * @param createUserId
	 * @param parentPlanId
	 * @param operUserVO
	 * @throws Exception    设定文件 
	 * @return void    返回类型 
	 */
	public GwWorkPlanVO verifyRegisteUserPass(Long createUserId,Long parentPlanId, GwUserVO operUserVO) throws Exception;
	
	public void doTaskRepush(Long planId,GwUserVO loginUser) throws Exception;
	
	//查看未处理的合规检查待办
	public Long searchCheckWorkPlan(Long taskId)throws Exception;
	
	public GwWorkPlanVO updateWorkPlanState(Long planId,Long smsId)throws Exception;
	//查询所有的待办任务
	public PageObject findAllByPage(PageObject pageObject) throws Exception;
}
