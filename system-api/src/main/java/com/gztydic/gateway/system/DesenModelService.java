package com.gztydic.gateway.system;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.interfaces.GeneralService;
import com.gztydic.gateway.core.view.GwDesenRuleServiceFieldView;
import com.gztydic.gateway.core.view.GwDesenServiceInfoView;
import com.gztydic.gateway.core.view.GwRuleCheckServiceFieldView;
import com.gztydic.gateway.core.view.GwServiceView;
import com.gztydic.gateway.core.vo.GwDesenServiceFieldVO;
import com.gztydic.gateway.core.vo.GwDesenServiceInfoVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;

/**
 * 服务脱敏配置
 * @author wangwei
 *
 */
public interface DesenModelService extends GeneralService<GwDesenServiceInfoVO>{

	/**
	 * 查询模型服务信息脱敏配置
	 * @return PageObject
	 * @throws Exception
	 */
	public PageObject searchDesenServiceInfoList(String loginName,PageObject pageObject) throws Exception;
	
	/**
	 * 查询同一用户、模型下其他服务的配置信息
	 * @param userId
	 * @param modelId
	 * @return
	 * @throws Exception
	 */
	public List<GwDesenServiceInfoView> searchModelServiceDesenInfo(Long userId,Long modelId) throws Exception;
	
	/**
	 * 查询模型字段脱敏配置
	 * @return PageObject
	 * @throws Exception
	 */
	public PageObject searchDesenServiceFieldList(String loginName,PageObject pageObject) throws Exception;
	
	/**
	 * 新增、修改模型服务信息脱敏配置
	 */
	public void updateDesenServiceInfoList(List<GwDesenServiceInfoVO> list, GwUserVO userVO) throws Exception;
	
	/**
	 * 新增、修改模型服务字段脱敏配置
	 */
	public void updateDesenServiceFieldList(Long workPlanId,List<GwDesenServiceFieldVO> list, GwUserVO userVO,GwModelDataFetchVO fetchVO,GwUploadFileVO fileVO, File ruleCheckFile,String ruleUpdateType,String processId,String type) throws Exception;
	
	public GwDesenServiceInfoVO searchDesenServiceInfo(Long userId,Long serviceId) throws Exception;
	
	/**
	 * 查询userId的serviceId的字段脱敏规则列表
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public List<GwDesenRuleServiceFieldView> searchDesenRuleServiceFieldList(Long userId,Long serviceId) throws Exception;
											 
	public List<GwDesenRuleServiceFieldView> search108DesenRuleServiceFieldList(Long userId,Long serviceId) throws Exception;

	
	public List<GwDesenRuleServiceFieldView> searchDesenRuleListByBatch(GwModelDataFetchTaskVO taskVO) throws Exception;
	
	/**
	 * 查询userId的serviceId的字段脱敏规则列表
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public List<GwDesenRuleServiceFieldView> searchDesenRuleServiceFieldConfig(Long userId, Long serviceId) throws Exception;
	
	
	/**
	 * 查询userId的serviceId的字段检查规则列表
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public List<GwRuleCheckServiceFieldView> searchCheckRuleServiceFieldConfig(Long userId, Long serviceId) throws Exception;
	
	/**
	 * 查询服务所属模型下其他服务的字段脱敏规则列表
	 * @param userId
	 * @param serviceId
	 * @return
	 * @throws Exception
	 */
	public List searchDesenRuleOtherServiceFieldList(Long userId,GwServiceVO service) throws Exception;
	
	/**
	 * 申请查看服务输入集信息
	 * @param loginUser
	 * @param serviceView
	 * @throws Exception
	 */
	public void appViewService(GwUserVO loginUser,GwServiceView serviceView) throws Exception;
	
	/**
	 * 申请查看模型信息
	 * @param loginUser
	 * @param serviceView
	 * @throws Exception
	 */
	public void appViewModel(GwUserVO loginUser,GwServiceView serviceView) throws Exception;
	
	public Map<String, Integer> searchTaskCheckCount(Long serviceId,Long userId) throws Exception;

	public List<GwServiceCheckRuleVO> parseServiceCheckRule(File ruleCheckFile, GwServiceVO serviceVO, GwUserVO userVO) throws Exception;

	public void savaRuleAndWorkPlan(GwModelDataFetchVO fetchVO,GwServiceVO serviceVO, GwUserVO loginUser,GwUploadFileVO fileVo, File upload, String string, String processId,String processType)throws Exception;

	public void updateServiceFetchFieldList(Long workPlanId,List<GwDesenServiceFieldVO> list, GwUserVO loginUser,GwModelDataFetchVO viewFetchVO, GwUploadFileVO fileVO, File ruleCheckFile,String ruleUpdateType)throws Exception;
	
	/** 
	 * @Title: searchServiceCheckRule 
	 * @Description: TODO(服务合规规则配置查看) 
	 * @param serviceId 服务编码
	 * @param userId 用户编码 
	 * @return List    返回类型 
	 * @throws 
	 */
	public PageObject searchServiceCheckRule(Long serviceId,Long userId,PageObject pageObject) throws Exception;
	public void savaRule(GwUserVO loginUser ,GwModelDataFetchVO fetchVO ,String workPlanId, String serviceId, String fetchId,
			String userId, File ruleCheckFile,String fileId,String processId)throws Exception;

	/**
	 * 
	 * @Title: searchLastCheckRuleList 
	 * @Description: TODO(查询服务最新的合规检查规则) 
	 * @param @param userId
	 * @param @param serviceId
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwServiceCheckRuleVO>    返回类型 
	 * @throws
	 */
	public List<GwServiceCheckRuleVO> searchLastCheckRuleList(Long userId, Long serviceId) throws Exception;
	
	}


