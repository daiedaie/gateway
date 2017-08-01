package com.gztydic.gateway.system;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.config.SysDictManage;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.DataProgressStatus;
import com.gztydic.gateway.core.common.constant.DataTypeConstent;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.constant.SessionConstant;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.Endecrypt;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.common.util.ShellUtil;
import com.gztydic.gateway.core.dao.GwModelDataFetchTaskDAO;
import com.gztydic.gateway.core.dao.GwModelDataFileDAO;
import com.gztydic.gateway.core.dao.GwModifyRecordDAO;
import com.gztydic.gateway.core.dao.GwSmsDAO;
import com.gztydic.gateway.core.dao.GwUserButtonDAO;
import com.gztydic.gateway.core.dao.GwUserDAO;
import com.gztydic.gateway.core.dao.GwWorkPlanDAO;
import com.gztydic.gateway.core.interfaces.GeneralServiceImpl;
import com.gztydic.gateway.core.view.GwWorkPlanView;
import com.gztydic.gateway.core.view.UserAuditView;
import com.gztydic.gateway.core.view.WorkPlanView;
import com.gztydic.gateway.core.vo.GwDesenServiceInfoVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelDataFileVO;
import com.gztydic.gateway.core.vo.GwModifyRecordVO;
import com.gztydic.gateway.core.vo.GwOrgVO;
import com.gztydic.gateway.core.vo.GwProcessOperationVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwSmsVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanParamVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;

/** 
 * @ClassName: WorkPlanServiceImpl 
 * @Description: TODO(系统待办任务实现方法类) 
 * @author davis
 * @date 2014-11-20 下午04:38:10 
 *  
 */
@Service
@SuppressWarnings("unchecked")
public class WorkPlanServiceImpl  extends GeneralServiceImpl<GwWorkPlanVO> implements WorkPlanService {
	
	private static final Log log = LogFactory.getLog(WorkPlanServiceImpl.class);
	
	@Resource
	private GwWorkPlanDAO gwWorkPlanDAO;
	
	@Resource
	private GwUserDAO gwUserDAO;
	
	@Resource
	private GwModelDataFileDAO gwModelDataFileDAO;
	
	@Resource
	private GwModifyRecordDAO gwModifyRecordDAO;
	
	@Resource
	private GwModelDataFetchTaskDAO gwModelDataFetchTaskDAO;
	
	@Resource
	private GwSmsDAO gwSmsDAO;
	
	@Resource
	private GwUserButtonDAO gwUserButtonDAO;
	
	@Resource(name="userServiceImpl")
	private UserService userService;
	@Resource(name="orgServiceImpl")
	private OrgService orgService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	@Resource(name="gwServiceServiceImpl")
	private GwServiceService gwServiceService;
	@Resource(name="desenModelServiceImpl")
	private DesenModelService desenModelService;
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	@Resource(name="liabilityServiceImpl")
	private LiabilityService liabilityService;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="processServiceImpl")
	private ProcessService processService;
	@Resource(name="processOperationServiceImpl")
	private ProcessOperationService processOperationService; 
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
	public GwWorkPlanVO saveWorkPlan(String planTitle,String planType,String planContent,String planState,String planLevel,String extenTableKey,Long createUserId,Long dealUserId,Long parentPlanId,String msgContent,String processId) throws Exception{
/*		if(createUserId == null){
			createUserId = Long.valueOf(10010);//没有待办创建人默认为10010号，表示系统创建
		}*/
		String userType = "";
		GwSmsVO gwSmsVO=new GwSmsVO();
		gwSmsVO.setSendStatus(null);
		gwSmsVO.setSendResult(null);
		gwSmsVO.setSendCount(0);
		gwSmsVO.setCreateTime(new Date());
		gwSmsVO.setSendTime(null);
		gwSmsVO.setSmsContent(msgContent);
		String moblieStr="";
		if(dealUserId == null){
			//根据待办类型得出用户类型
			if(WorkPlanConstent.REGISTE_AUDIT.equals(planType) || WorkPlanConstent.CANCEL_AUDIT.equals(planType) || WorkPlanConstent.GET_DATA_AUDIT.equals(planType) || WorkPlanConstent.UPDATE_AUDIT.equals(planType)){
				userType = GwUserType.AUDIT_USER;
			}else if(WorkPlanConstent.PERMISS_ALLOT.equals(planType) || WorkPlanConstent.FIELD_DESEN_CONF.equals(planType) || WorkPlanConstent.INFO_DESEN_CONF.equals(planType)){
				userType = GwUserType.SAFE_USER;
			}
			//查询某类型管理用户
			List<GwUserVO> userList = gwUserDAO.findByUserType(userType);
			if(userList != null && userList.size()>0){
				GwUserVO userVo = userList.get(0);
				dealUserId = userVo.getUserId();
			}
			
			//保存短信记录
			List<String> userMobilesList=gwUserButtonDAO.searchUserByPlanType(planType);
			for(String moblie : userMobilesList){
				moblieStr+=(moblieStr==""?"":"|");
				moblieStr+=moblie;
			}
		}else{
			GwUserVO userVO=userService.searchUserDetail(dealUserId);
			moblieStr=userVO.getMoblie();
		}
		gwSmsVO.setSmsMobile(moblieStr);
		gwSmsDAO.saveOrUpdate(gwSmsVO);
		GwWorkPlanVO gwWorkPlanVO;
		//保存待办
		System.out.print("--------------processId11:"+processId);

		if(processId != null && processId !=""){
			gwWorkPlanVO = new GwWorkPlanVO(planTitle, planType,planContent ,planState,planLevel,extenTableKey,new Date(),createUserId,null,dealUserId,parentPlanId,gwSmsVO.getSmsId(),processId);
		}else{
			gwWorkPlanVO = new GwWorkPlanVO(planTitle, planType,planContent ,planState,planLevel,extenTableKey,new Date(),createUserId,null,dealUserId,parentPlanId,gwSmsVO.getSmsId(),null);
		}
		System.out.print("--------------processId22:"+processId);

		gwWorkPlanDAO.save(gwWorkPlanVO);
		return gwWorkPlanVO;
	}
		
	public GwWorkPlanVO saveWorkPlan(String planTitle,String planType,String planContent,String planState,String planLevel,String extenTableKey,Long createUserId,Long dealUserId,Long parentPlanId,Map<String, String> paramMap,String msgContent) throws Exception{
		GwWorkPlanVO planVO = saveWorkPlan(planTitle, planType, planContent, planState, planLevel, extenTableKey, createUserId, dealUserId, parentPlanId,msgContent,null);
		if(paramMap != null){
			workPlanParamService.saveParamMap(planVO.getPlanId(), paramMap);
		}
		
		return planVO;
	}
	
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
	public GwWorkPlanVO updateWorkPlanState(Long planId,String suggestion,String planState,Long dealUserId)throws Exception{
		GwWorkPlanVO gwWorkPlanVO = gwWorkPlanDAO.findById(planId);
		if(gwWorkPlanVO != null && !gwWorkPlanVO.getPlanState().equals(WorkPlanConstent.WAIT_FOR_DEAL)){
			throw new Exception("任务已处理完，不能重复处理");
		}else if(gwWorkPlanVO != null){
			gwWorkPlanVO.setPlanState(planState);
			gwWorkPlanVO.setDaelTime(new Date());
			gwWorkPlanVO.setSuggestion(suggestion);
			if(dealUserId != null) gwWorkPlanVO.setDealUserId(dealUserId);
			gwWorkPlanDAO.update(gwWorkPlanVO);
		}
		return gwWorkPlanVO;
	}
	
	/** 
	 * @Title: searchUserWorkPlan 
	 * @Description: TODO(查询某用户待办任务列表) 
	 * @param @param dealUserId 用户ID
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwWorkPlanVO>    返回类型 
	 * @throws 
	 */
	public List<GwWorkPlanVO> searchUserWorkPlan(Long dealUserId)throws Exception{
		
		return gwWorkPlanDAO.findByDealUserId(dealUserId);
	}
	
	/** 
	 * @Title: searchUserWorkPlan 
	 * @Description: TODO(查询某用户待办任务列表) 
	 * @param planTypes 待办类型
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwWorkPlanVO>    返回类型 
	 * @throws 
	 */
	public List<GwWorkPlanVO> searchWorkPlanByType(String planTypes)throws Exception{
		
		return gwWorkPlanDAO.findByPlanType(planTypes);
	}
	
	public List<GwWorkPlanVO> findByCreateTime(String createTime)throws Exception{
		
		return gwWorkPlanDAO.findByCreateTime(createTime);
	}
	/** 
	 * @Title: searchByParam 
	 * @Description: TODO(根据多条件查询待办任务列表) 
	 * @param @param planType
	 * @param @param planTitle
	 * @param @param planContent
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwWorkPlanVO>    返回类型 
	 * @throws 
	 */
	public PageObject searchByParam(String  planTypes,String  planType,String planTitle,String planContent,PageObject pageObject,String planState)throws Exception{
		
		return gwWorkPlanDAO.findByParam(planTypes,planType, planTitle, planContent,pageObject,planState);
	}
	
	/** 
	 * @Title: searchByParam 
	 * @Description: TODO(根据多条件查询某用户待办任务列表) 
	 * @param @param planType
	 * @param @param planTitle
	 * @param @param planContent
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return List<GwWorkPlanVO>    返回类型 
	 * @throws 
	 */
	public PageObject searchByParam(Long  userId,String  planType,String planTitle,String planContent,PageObject pageObject,String planState)throws Exception{
		
		return gwWorkPlanDAO.findByParam(userId,planType, planTitle, planContent,pageObject,planState);
	}
	
	/** 
	 * @Title: searchById 
	 * @Description: TODO(根据关键字查询待办对象) 
	 * @param @param planId
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return GwWorkPlanVO    返回类型 
	 * @throws 
	 */
	public GwWorkPlanVO searchById(Long planId) throws Exception{
		return gwWorkPlanDAO.findById(planId);
	}


	public List searchUpdateUserVerify(Integer batchId)throws Exception{
		return gwModifyRecordDAO.findByBatchId(batchId);
	}
	
	public GwWorkPlanVO searchWorkPlan(Long planId) throws Exception{
		return gwWorkPlanDAO.findById(planId);
	}
	

	/**
	 * 待办审核，用户注销
	 * @param loginUser
	 * @param cancelUser
	 * @param workPlanVO
	 * @return
	 * @throws Exception
	 */
	public int cancelUserAudit(GwUserVO loginUser,GwUserVO cancelUser,GwWorkPlanVO workPlanVO) throws Exception{
		updateWorkPlanState(workPlanVO.getPlanId(), workPlanVO.getSuggestion(), workPlanVO.getPlanState(),loginUser.getUserId());
		return userService.cancelUser(loginUser, cancelUser);
	}
	
	/**
	 * 用户注销，审核退回
	 * @param loginUser
	 * @param cancelUser
	 * @param workPlanVO
	 * @return
	 * @throws Exception
	 */
	public void cancelUserRollback(GwUserVO loginUser,GwUserVO cancelUser,GwWorkPlanVO workPlanVO) throws Exception{
		GwWorkPlanVO plan = updateWorkPlanState(workPlanVO.getPlanId(), workPlanVO.getSuggestion(), workPlanVO.getPlanState(),loginUser.getUserId());
		
		String content = cancelUser.getLoginName()+"申请的用户注销审核不通过。";
		String msgContent=cancelUser.getLoginName()+"申请的用户注销审核不通过。【数据网关平台】";
		saveWorkPlan("用户注销申请退回", WorkPlanConstent.CANCEL_BACKT, content, WorkPlanConstent.WAIT_FOR_DEAL, null,
				plan.getPlanId().toString(), loginUser.getUserId(), plan.getCreateUserId(),workPlanVO.getPlanId(),msgContent,null);
	}
	
	/**
	 * 查询待办任务
	 * @param planType=待办类型
	 * @param planState=待办状态
	 * @param extenTableKey=外部主键id
	 * @throws Exception 
	 */
	public List<GwWorkPlanVO> searchWorkPlan(String planType,String planState,String[] extenTableKey) throws Exception{
		String keys = "";
		for (String key : extenTableKey) {
			keys += ("".equals(keys) ? "" : ",") + "'"+key+"'"; 
		}
		return gwWorkPlanDAO.searchWorkPlan(planType, planState, keys);
	}
	public List<WorkPlanView> searchWorkPlanByUpdate(String planType,String planState,String[] paramValue) throws Exception{
		String keys = "";
		for (String key : paramValue) {
			keys += ("".equals(keys) ? "" : ",") + "'"+key+"'"; 
		}
		return gwWorkPlanDAO.searchWorkPlanByUpdate(planType, planState, keys);
	}

	public List<WorkPlanView> searchWorkPlanByForService(String[] planType,String planState,String paramValue,String serviceIds,String planSource) throws Exception{
		return gwWorkPlanDAO.searchWorkPlanByForService(planType, planState, paramValue,serviceIds,planSource);
	}
	
	public List<WorkPlanView> searchWorkPlanForDesenService(String[] planType,String planState,String[] paramValue,String[] serviceIds) throws Exception{
		String keys = "";
		for (String key : paramValue) {
			keys += ("".equals(keys) ? "" : ",") + "'"+key+"'"; 
		}
		String serviceId = "";
		for (String service : serviceIds) {
			serviceId += ("".equals(serviceId) ? "" : ",") + "'"+service+"'"; 
		}
		if(StringUtils.isEmpty(keys) || StringUtils.isEmpty(serviceId))
			return new ArrayList<WorkPlanView>();
		return gwWorkPlanDAO.searchWorkPlanForDesenService(planType, planState, keys,serviceId);
	}
	
	/**
	 *用户审核列表
	 */
	public PageObject findUserAuditByParam(UserAuditView userVO,PageObject pageObject,String planTypes)throws Exception{
		return gwWorkPlanDAO.findUserAuditByParam(userVO.getLoginName(), userVO.getUserName(), userVO.getUserType(), userVO.getOrgName(), pageObject,planTypes);
	}

	
	public void verifyUpdateUser(GwUserVO user,GwOrgVO org,GwModifyRecordVO modifyRecord, GwUserVO loginUser,
			String passTag,String planId,String suggestion)throws Exception{			 
			GwWorkPlanVO workPlanVO = updateWorkPlanState(Long.valueOf(planId), suggestion,passTag,loginUser.getUserId());
			GwWorkPlanParamVO workPlanParamVO = workPlanParamService.searchParam(Long.valueOf(planId),"batchId");
			String batchId = workPlanParamVO.getParamValue();
			String modifyColumns="";
			List  gwModifyRecordVOList= searchUpdateUserVerify(Integer.valueOf(batchId));
			for(int i=0;i<gwModifyRecordVOList.size();i++){
				modifyRecord=(GwModifyRecordVO)gwModifyRecordVOList.get(i);
				String column=SysDictManage.getSysDict("DICT_USER_COLUMN",modifyRecord.getColumsCode()).getDictValue();
				modifyColumns+=("".equals(modifyColumns)?"":",")+column;
			}
			if(CommonState.PASS.equals(passTag)){
				for(int i=0;i<gwModifyRecordVOList.size();i++){
					modifyRecord=(GwModifyRecordVO)gwModifyRecordVOList.get(i);
					String column=modifyRecord.getColumsCode();
					if(column.equals("user_name")){
						user.setUserName(modifyRecord.getAfterValue());
					}else if(column.equals("user_moblie")){
						user.setMoblie(modifyRecord.getAfterValue());
					}else if(column.equals("user_email")){
						user.setEmail(modifyRecord.getAfterValue());
					}else if(column.equals("user_certno")){
						user.setCertNo(modifyRecord.getAfterValue());
					}else if(column.equals("user_remark")){
						user.setRemark(modifyRecord.getAfterValue());
					}else if(column.equals("user_addr")){
						user.setAddr(modifyRecord.getAfterValue());
					}else if(column.equals("user_fileid")){
						user.setFileId(Long.parseLong(modifyRecord.getAfterValue()));
					}else if(column.equals("org_name")){
						org.setOrgName(modifyRecord.getAfterValue());
					}else if(column.equals("cert_type")){
						org.setCertType(modifyRecord.getAfterValue());
					}else if(column.equals("cert_no")){
						org.setCertNo(modifyRecord.getAfterValue());
					}else if(column.equals("org_head_name")){
						org.setOrgHeadName(modifyRecord.getAfterValue());
					}else if(column.equals("org_tel")){
						org.setOrgTel(modifyRecord.getAfterValue());
					}else if(column.equals("reg_code")){
						org.setRegCode(modifyRecord.getAfterValue());
					}else if(column.equals("org_addr")){
						org.setOrgAddr(modifyRecord.getAfterValue());
					}else if(column.equals("push_ftp")){
						user.setPushFtp(modifyRecord.getAfterValue());
					}else if(column.equals("ftp_ip")){
						user.setFtpIp(modifyRecord.getAfterValue());
					}else if(column.equals("ftp_port")){
						user.setFtpPort(modifyRecord.getAfterValue());
					}else if(column.equals("ftp_path")){
						user.setFtpPath(modifyRecord.getAfterValue());
					}else if(column.equals("ftp_username")){
						user.setFtpUsername(modifyRecord.getAfterValue());
					}else if(column.equals("ftp_password")){
						user.setFtpPassword(modifyRecord.getAfterValue());
					}else if(column.equals("webservice_url")){
						user.setWebserviceUrl(modifyRecord.getAfterValue());
					}else if(column.equals("webservice_method")){
						user.setWebserviceMethod(modifyRecord.getAfterValue());
					}else if(column.equals("base_wsdl")){
						user.setBaseWsdl(modifyRecord.getAfterValue());
					}
				}
				orgService.updateOrgUser(user, loginUser);
				if(org!=null){
					 orgService.updateOrg(org, loginUser);
				}
						
			}else {
				String loginName = user.getLoginName();
				//生成审核待办
				String planContentStr = "您提交修改申请的用户："+loginName+"审核不通过，请及时修改重新提交。";
				String extenTableKeyBack = String.valueOf(workPlanVO.getPlanId())+","+batchId;
				
				GwUserVO dealUserVO=userService.searchUserDetail(workPlanVO.getCreateUserId());
				String msgContent="您提交的用户："+dealUserVO.getLoginName()+"修改申请审核不通过，请重新修改重新提交。【数据网关平台】";
				saveWorkPlan("用户修改退回",WorkPlanConstent.UPDATE_BACK , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
						null, extenTableKeyBack, loginUser.getUserId(),workPlanVO.getCreateUserId(),workPlanVO.getPlanId(),msgContent,null);
			}
			//写操作日志
			operationLogService.saveOperationLog(loginUser.getLoginName(), user.getLoginName(), OperateTypeConstent.USER_UPDATE_AUDIT, "用户修改审核(修改的字段有:"+modifyColumns+"),审核结果："+SysDictManage.getSysDict("DICT_AUDIT_STATE",passTag).getDictValue());
	}
	
	public void verifyServiceInfoApp(String planType,GwDesenServiceInfoVO desenModelInfo,GwWorkPlanVO workPlan,
			String suggestion,GwUserVO loginUser,String passTag,String userId,String serviceId)throws Exception{			 
		GwWorkPlanVO workPlanVO = updateWorkPlanState(workPlan.getPlanId(), suggestion,passTag,loginUser.getUserId());
		GwServiceVO serviceVO = gwServiceService.searchService(Long.parseLong(serviceId));
		if(CommonState.PASS.equals(passTag)){
			GwUserVO userVO = userService.searchUserDetail(Long.parseLong(userId));
			String userType = loginUser.getUserType();
			String info = "viewService".equals(planType)?"服务输入集":"viewModel".equals(planType)?"模型基本信息":"";
			String planContent= GwUserType.getUserTypeName(userType)+"用户："+loginUser.getLoginName()+"审核通过了"+userVO.getLoginName()+"查看服务("+serviceVO.getServiceName()+")的"+info+"的申请，请审核！";
			//待办入库
			String msgContent=userVO.getLoginName()+"申请查看服务("+serviceVO.getServiceName()+")的"+info+"，请审核！【数据网关平台】";
			GwWorkPlanVO gwWorkPlanVO=saveWorkPlan("服务信息脱敏配置审核",WorkPlanConstent.INFO_DESEN_CONF_AUDIT, planContent, WorkPlanConstent.WAIT_FOR_DEAL, null, null, loginUser.getUserId(),null,workPlan.getPlanId(),msgContent,null);
			Map map=new HashMap();
			map.put("userId",userId.toString());
			map.put("serviceId", serviceId.toString());
			map.put("planType", planType);
			workPlanParamService.saveParamMap(gwWorkPlanVO.getPlanId(), map);
		}else {
			//生成审核待办
			String info = "viewService".equals(planType)?"服务输入集":"viewModel".equals(planType)?"模型基本信息":"";
			String planContentStr = "您提交申请查看服务(服务编码="+serviceVO.getServiceCode()+"，服务名称="+serviceVO.getServiceName()+")的"+info+"审核不通过!";
			String msgContent="您提交查看服务("+serviceVO.getServiceName()+")的"+info+"的申请审核不通过！【数据网关平台】";
			GwWorkPlanVO gwWorkPlanVO=saveWorkPlan("服务信息申请查看退回",WorkPlanConstent.INFO_DESEN_CONF_BACK , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
					null, null,loginUser.getUserId(),Long.valueOf(userId), workPlan.getPlanId(),msgContent,null);
			Map map=new HashMap();
			map.put("userId",userId);
			map.put("serviceId", serviceId);
			if("viewService".equals(planType)){
				map.put("planType", "viewService");
			}
			if("viewModel".equals(planType)){
				map.put("planType", "viewModel");
			}
			workPlanParamService.saveParamMap(gwWorkPlanVO.getPlanId(), map);
		}
	}
	
	public void verifyServiceInfoAuditApp(String planType,GwDesenServiceInfoVO desenModelInfo,GwWorkPlanVO workPlan,
			String suggestion,GwUserVO loginUser,String passTag,String userId,String serviceId)throws Exception{
		GwWorkPlanVO workPlanVO = updateWorkPlanState(workPlan.getPlanId(), suggestion,passTag,loginUser.getUserId());
		if(CommonState.PASS.equals(passTag)){
			if(desenModelInfo==null){
				desenModelInfo=new GwDesenServiceInfoVO();
				desenModelInfo.setUserId(Long.parseLong(userId));
				desenModelInfo.setServiceId(Long.parseLong(serviceId));
			}
			if("viewService".equals(planType)){
				desenModelInfo.setServiceInputInfo("1");
			}
			if("viewModel".equals(planType)){
				desenModelInfo.setModelInfo("1");
			}
			List<GwDesenServiceInfoVO> list=new ArrayList<GwDesenServiceInfoVO>();
			list.add(desenModelInfo);
			desenModelService.updateDesenServiceInfoList(list, loginUser);
		}else {
			GwServiceVO serviceVO = gwServiceService.searchService(Long.parseLong(serviceId));
			//生成审核待办
			String info = "viewService".equals(planType)?"服务输入集":"viewModel".equals(planType)?"模型基本信息":"";
			String msgContent="您提交查看服务("+serviceVO.getServiceName()+")的"+info+"的申请审核不通过！【数据网关平台】";
			String planContentStr = "您提交申请查看服务(服务编码="+serviceVO.getServiceCode()+"，服务名称="+serviceVO.getServiceName()+")的"+info+"审核不通过!";
			GwWorkPlanVO gwWorkPlanVO=saveWorkPlan("服务信息申请查看退回",WorkPlanConstent.INFO_DESEN_CONF_BACK , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
					null, null,loginUser.getUserId(),workPlanVO.getCreateUserId(), workPlanVO.getPlanId(),msgContent,null);
			Map map=new HashMap();
			map.put("userId",userId);
			map.put("serviceId", serviceId);
			if("viewService".equals(planType)){
				map.put("planType", "viewService");
			}
			if("viewModel".equals(planType)){
				map.put("planType", "viewModel");
			}
			workPlanParamService.saveParamMap(gwWorkPlanVO.getPlanId(), map);
		}
	}
	
	public void verifyServiceInfoAuditBackApp(String planType,GwDesenServiceInfoVO desenModelInfo,String planId,
			String suggestion,GwUserVO loginUser,String userId,String serviceId)throws Exception{
		GwWorkPlanVO workPlanVO = updateWorkPlanState(Long.parseLong(planId), suggestion,CommonState.NO_PASS,loginUser.getUserId());
		if(desenModelInfo==null){
				desenModelInfo=new GwDesenServiceInfoVO();
				desenModelInfo.setUserId(Long.parseLong(userId));
				desenModelInfo.setServiceId(Long.parseLong(serviceId));
			}
			if("viewService".equals(planType)){
				desenModelInfo.setServiceInputInfo("0");
			}
			if("viewModel".equals(planType)){
				desenModelInfo.setModelInfo("0");
			}
			List<GwDesenServiceInfoVO> list=new ArrayList<GwDesenServiceInfoVO>();
			list.add(desenModelInfo);
			String serviceName = gwServiceService.searchService(Long.parseLong(serviceId)).getServiceName();
			//生成审核待办
			String info = "viewService".equals(planType)?"服务输入集":"viewModel".equals(planType)?"模型基本信息":"";
			String planContentStr = "您提交申请查看服务("+serviceName+")的"+info+"审核不通过!";
			String msgContent="您提交查看服务("+serviceName+")的"+info+"的申请审核不通过！【数据网关平台】";
			GwWorkPlanVO gwWorkPlanVO=saveWorkPlan("服务信息查看申请退回",WorkPlanConstent.INFO_DESEN_CONF_BACK , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
					null, null,loginUser.getUserId(),Long.parseLong(userId), workPlanVO.getPlanId(),msgContent,null);
			Map map=new HashMap();
			map.put("userId",userId);
			map.put("serviceId", serviceId);
			if("viewService".equals(planType)){
				map.put("planType", "viewService");
			}
			if("viewModel".equals(planType)){
				map.put("planType", "viewModel");
			}
			workPlanParamService.saveParamMap(gwWorkPlanVO.getPlanId(), map);
	}
	
	
	//数据安全管理员合规检查审核
	public void verifyRuleCheckAuditForSafeUser(String passTag,String planId,String suggestion,String taskId,String acceptUserId,String serviceId,GwUserVO loginUser)throws Exception{
		GwWorkPlanVO workPlanVO;
		if("".equals(passTag)){
			workPlanVO = updateWorkPlanState(Long.valueOf(planId), "",CommonState.PASS,loginUser.getUserId());
		}else{
			workPlanVO = updateWorkPlanState(Long.valueOf(planId), suggestion,passTag,loginUser.getUserId());
		}
		GwUserVO acceptUser=userService.searchUserDetail(Long.parseLong(acceptUserId));
		GwServiceVO serviceVO=gwServiceService.searchService(Long.parseLong(serviceId));
		GwModelDataFetchTaskVO taskVO = gwModelDataFetchTaskDAO.findById(Long.parseLong(taskId));
		String checkResult = "0".equals(taskVO.getCheckResult())?"":"存在不合规数据，";
		
		//更新流程状态为审核人员第一次审核执行中
		String processId = workPlanService.searchById(Long.valueOf(planId)).getProcessId();
		processService.updateProcessStatus(Long.valueOf(processId), DataTypeConstent.F_FIRSTAUDIT, DataTypeConstent.EXCUTING, null,null);
		if(CommonState.PASS.equals(passTag) || "".equals(passTag)){
		
			//--start--获取当前待办的流程ID并更新流程及更新流程操作过程
			GwProcessOperationVO gwVO = processOperationService.searchProcessOperationByStep(Long.valueOf(processId), DataTypeConstent.F_FIFTH);
			gwVO.setOperateContent("已完成一次审核");
			gwVO.setOperateTime(new Date());
			gwVO.setProgressStatus(DataTypeConstent.COMPLETED);
			gwVO.setDealType(GwUserType.SAFE_USER);
			gwVO.setPlanId(Long.valueOf(planId));
			processOperationService.update(gwVO);
			//新增数据安全管理员第二次审核待处理的流程步骤
			processOperationService.saveProcessOperation(Long.valueOf(processId), null, null, null, null,null,GwUserType.AUDIT_USER,DataTypeConstent.F_SIXTH);
			//第一次审核结束
			processService.updateProcessStatus(Long.valueOf(processId), DataTypeConstent.F_FIRSTAUDIT, DataTypeConstent.COMPLETED, null,null);
			//--end--
			
			//给审核管理员生成待办       
			String planContentStr=acceptUser.getLoginName()+"请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")输出文件"+checkResult+(CommonState.PASS.equals(passTag)?"已被数据安全管理员审核通过！请审核！":"已被数据安全管理员阅读！请阅读！");
			String msgContent="数据用户："+acceptUser.getLoginName()+"请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")输出文件合规审阅通过，请审阅。【数据网关平台】";
			GwWorkPlanVO workPlan=saveWorkPlan("合规检查审核",WorkPlanConstent.RULE_CHECK_AUDIT_2 , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
					null, null,loginUser.getUserId(),null, workPlanVO.getPlanId(),msgContent,processId);
			Map map=new HashMap();
			map.put("userId",acceptUserId);
			map.put("taskId", taskId);
			map.put("serviceId", serviceId);
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			
			taskVO.setDownloadEndTime(new Date());
			taskVO.setDataProgressStatus(DataProgressStatus.RULE_CHECK_AUDIT_SECOND);
			gwModelDataFetchTaskDAO.update(taskVO);
			
			
		}else{
			//--start--结束流程
			processService.updateProcess(Long.valueOf(processId), DataTypeConstent.COMPLETED, new Date());
			//--end--
			taskVO.setDownloadEndTime(new Date());
			taskVO.setDataProgressStatus(DataProgressStatus.RULE_CHECK_INVALID);
			gwModelDataFetchTaskDAO.update(taskVO);
			//给数据用户生成待办
			String planContentStr = "您请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")输出文件"+checkResult+"已被数据安全管理员审核不通过，不能取数!";
			String msgContent="您请求的服务(服务编码="+serviceVO.getServiceCode()+"，服务名称="+serviceVO.getServiceName()+")输出文件中存在不合规的数据，已被"+SysDictManage.getSysDict("DICT_USER_TYPE", loginUser.getUserType()).getDictValue()+"退回，不能取数。【数据网关平台】";
			saveWorkPlan("合规检查审核退回",WorkPlanConstent.RULE_CHECK_AUDIT_BACK , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
					null, null,loginUser.getUserId(),Long.parseLong(acceptUserId), workPlanVO.getPlanId(),msgContent,null);
		}
		//操作日志
		String operateContent=loginUser.getLoginName()+(CommonState.PASS.equals(passTag)?"审核通过":(CommonState.NO_PASS.equals(passTag)?"审核不通过":"已阅"))+acceptUser.getLoginName()+"请求的服务(服务编码="+serviceVO.getServiceCode()+"，服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")" +
				"输出文件的数据！";
		operationLogService.saveOperationLog(loginUser.getLoginName(), acceptUser.getLoginName(), OperateTypeConstent.RESULT_OUPUT_FIRST_AUDIT,operateContent);
	}
	
	//审核管理员合规检查审核
	public GwModelDataFetchTaskVO verifyRuleCheckAuditForAuditUser(String passTag,String planId,String suggestion,String taskId,String userId,String serviceId,GwUserVO loginUser)throws Exception{
		GwWorkPlanVO workPlanVO;
		if("".equals(passTag)){
			workPlanVO = updateWorkPlanState(Long.valueOf(planId), "",CommonState.PASS,loginUser.getUserId());
		}else{
			workPlanVO = updateWorkPlanState(Long.valueOf(planId), suggestion,passTag,loginUser.getUserId());
		}
		GwUserVO acceptUser=userService.searchUserDetail(Long.parseLong(userId));
		GwServiceVO serviceVO=gwServiceService.searchService(Long.parseLong(serviceId));
		GwModelDataFetchTaskVO taskVO = gwModelDataFetchTaskDAO.findById(Long.parseLong(taskId));
		GwModelDataFileVO dataFileVO = gwModelDataFileDAO.searchDataFile(taskVO.getTaskId(), "2");
		String checkResult = "0".equals(taskVO.getCheckResult())?"":"存在不合规数据，";
		taskVO.setDownloadEndTime(new Date());
		//更新流程状态为审核人员二次审核执行中
		String processId = workPlanService.searchById(Long.valueOf(planId)).getProcessId();
		processService.updateProcessStatus(Long.valueOf(processId),DataTypeConstent.F_SECONDAUDIT, DataTypeConstent.EXCUTING, null,null);
		
		/*Boolean pushTag = true;*/
		if(CommonState.PASS.equals(passTag)){
			
			//--start--获取当前待办的流程ID并更新流程及更新流程操作过程
			GwProcessOperationVO gwVO = processOperationService.searchProcessOperationByStep(Long.valueOf(processId), DataTypeConstent.F_SIXTH);
			gwVO.setOperateContent("已完成二次审核");
			gwVO.setOperateTime(new Date());
			gwVO.setProgressStatus(DataTypeConstent.COMPLETED);
			gwVO.setDealType(GwUserType.AUDIT_USER);
			gwVO.setPlanId(Long.valueOf(planId));
			processOperationService.update(gwVO);
			//新增系统数据推送的流程步骤
			processOperationService.saveProcessOperation(Long.valueOf(processId), null, null, null, null,null,"system",DataTypeConstent.F_SEVENTH);
			//第二次审核流程结束
			processService.updateProcessStatus(Long.valueOf(processId), DataTypeConstent.F_SECONDAUDIT, DataTypeConstent.COMPLETED, null,null);
			//--end--		
			
			if(!"0".equals(taskVO.getCheckResult())) throw new Exception("存在不合规的数据，不能审核通过");
			taskVO.setDataProgressStatus(DataProgressStatus.RULE_CHECK_SUCCESS);
			
			if(CommonState.SERVICE_SOURCE_108.equals(serviceVO.getServiceSource()) && "0".equals(taskVO.getCheckResult())){
				//审核管理员审核通过后，需要将文件push到指定ftp
				/*dataFileVO.setFtpIp(acceptUser.getFtpIp());
				dataFileVO.setFtpUser(acceptUser.getFtpUsername());
				dataFileVO.setFtpPassword(acceptUser.getFtpPassword());
				if(PushFtpUtil.pushFileToFtp(dataFileVO)){
					log.info("taskId="+taskVO.getTaskId()+" 审核通过，push文件("+dataFileVO.getFilePath()+"/"+dataFileVO.getFileName()+")到ftp("+ConfigConstants.FTP_108_IP+")成功");
					taskVO.setDownloadTime(new Date());
					taskVO.setDataProgressStatus(DataProgressStatus.RULE_CHECK_PUSHED);
					liabilityService.saveLiabilityLog(taskVO, acceptUser);
				}else {
					taskVO.setDataProgressStatus(DataProgressStatus.RULE_CHECK_REPUSH);
					log.error("taskId="+taskVO.getTaskId()+" 审核通过，push文件("+dataFileVO.getFilePath()+"/"+dataFileVO.getFileName()+")到ftp("+ConfigConstants.FTP_108_IP+")失败");
					pushTag = false;
				}*/
				taskVO.setDataProgressStatus(DataProgressStatus.RULE_CHECK_PUSHING);
			}
		}else if(CommonState.NO_PASS.equals(passTag)){
			taskVO.setDataProgressStatus(DataProgressStatus.RULE_CHECK_INVALID);
			//给数据用户生成待办
			String planContentStr = "您请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")输出文件"+checkResult+"已被审核管理员审核不通过，不能取数!";
			String msgContent="您请求的服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")输出文件已被"+SysDictManage.getSysDict("DICT_USER_TYPE", loginUser.getUserType()).getDictValue()+"退回，不能取数。【数据网关平台】";
			saveWorkPlan("合规检查审核退回",WorkPlanConstent.RULE_CHECK_AUDIT_BACK , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
					null, null,loginUser.getUserId(),Long.parseLong(userId), workPlanVO.getPlanId(),msgContent,null);
		}else{
			//已阅
			taskVO.setDataProgressStatus(DataProgressStatus.RULE_CHECK_STOP);
		}
		gwModelDataFetchTaskDAO.update(taskVO);
		//操作日志
		String operateContent=loginUser.getLoginName()+(CommonState.PASS.equals(passTag)?"审核通过":(CommonState.NO_PASS.equals(passTag)?"审核不通过":"已阅"))+acceptUser.getLoginName()+"请求的服务(服务编码="+serviceVO.getServiceCode()+"，服务名称="+serviceVO.getServiceName()+",服务周期="+taskVO.getFieldValue()+")输出文件的数据！";
		operationLogService.saveOperationLog(loginUser.getLoginName(), acceptUser.getLoginName(), OperateTypeConstent.RESULT_OUPUT_SECOND_AUDIT,operateContent);
		/*if(!pushTag){
			throw new RuntimeException("push文件到FTP("+ConfigConstants.FTP_108_IP+")失败。");
		}
		*/
		return taskVO;
	}
	
	//将未处理的不合规检查审核待办设为失效
	public int updateCheckWorkPlanInvalid(Long userId,Long serviceId) throws Exception{
		return gwWorkPlanDAO.updateCheckWorkPlanInvalid(userId, serviceId);
	}
	
	//新增服务审核
	/*public void auditAddModelService(Long planId,String suggestion,GwUserVO loginUser,GwServiceVO serviceVO, GwUserVO acceptUser,String passTag) throws Exception{
		GwWorkPlanVO workPlanVO = updateWorkPlanState(planId, suggestion,passTag,loginUser.getUserId());
		if(CommonState.PASS.equals(passTag)){
			gwServiceService.updateServiceStatus(serviceVO.getServiceId());
		}else{
			//生成退回待办
			String planContentStr = "您请求的新增服务(服务编码="+serviceVO.getServiceCode()+",服务名称="+serviceVO.getServiceName()+")已被"+loginUser.getLoginName()+"审核不通过！";
			saveWorkPlan("服务新增退回",WorkPlanConstent.ADD_MODEL_SERVICE_BACK , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
					null, null,loginUser.getUserId(),null, workPlanVO.getPlanId());
			
		}
		//写操作日志
		String operateContent=loginUser.getLoginName()+(CommonState.PASS.equals(passTag)?"审核通过":"审核不通过")+acceptUser.getLoginName()+"请求的服务新增(服务编码="+serviceVO.getServiceCode()+"，服务名称="+serviceVO.getServiceName()+")";
		operationLogService.saveOperationLog(loginUser.getLoginName(), acceptUser.getLoginName(), OperateTypeConstent.ADD_SERVICE,operateContent);
	}
	
	//修改服务审核
	public void verifyUpdateService(GwServiceVO serviceVO,GwUserVO loginUser,
			String passTag,String planId,String suggestion)throws Exception{
		GwWorkPlanVO workPlanVO = updateWorkPlanState(Long.valueOf(planId), suggestion,passTag,loginUser.getUserId());
		Map<String, String> ParamMap = workPlanParamService.searchParamMap(Long.parseLong(planId));
		String batchId = ParamMap.get("batchId");
		String userId=ParamMap.get("userId");
		GwUserVO acceptUser=userService.searchUserDetail(Long.valueOf(userId));
		String modifyColumns="";
		List  gwModifyRecordVOList= searchUpdateUserVerify(Integer.valueOf(batchId));
		GwModifyRecordVO modifyRecord;
		for(int i=0;i<gwModifyRecordVOList.size();i++){
			modifyRecord=(GwModifyRecordVO)gwModifyRecordVOList.get(i);
			String column=SysDictManage.getSysDict("DICT_SERVICE_COLUMN",modifyRecord.getColumsCode()).getDictValue();
			modifyColumns+=("".equals(modifyColumns)?"":",")+column;
		}
		if(CommonState.PASS.equals(passTag)){
			for(int i=0;i<gwModifyRecordVOList.size();i++){
				modifyRecord=(GwModifyRecordVO)gwModifyRecordVOList.get(i);
				String column=modifyRecord.getColumsCode();
				if(column.equals("service_code")){
					serviceVO.setServiceCode(modifyRecord.getAfterValue());
				}
				if(column.equals("service_name")){
					serviceVO.setServiceName(modifyRecord.getAfterValue());
				}
				if(column.equals("cycle_type")){
					serviceVO.setCycleType(modifyRecord.getAfterValue());
				}
				if(column.equals("cycle_day")){
					serviceVO.setCycleDay(Long.valueOf(modifyRecord.getAfterValue()));
				}
				
			}
					
		}else {
			//生成退回待办
			String planContentStr = "您提交修改申请的服务："+serviceVO.getServiceId()+"审核不通过，请及时修改重新提交。";
			saveWorkPlan("服务修改退回",WorkPlanConstent.UPDATE_MODEL_SERVICE_BACK , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
					null, null, loginUser.getUserId(),null,workPlanVO.getPlanId());
		}
		//写操作日志
		operationLogService.saveOperationLog(loginUser.getLoginName(), acceptUser.getLoginName(), OperateTypeConstent.UPDATE_SERVICE, "服务修改审核(修改的字段有:"+modifyColumns+"),审核结果："+SysDictManage.getSysDict("DICT_AUDIT_STATE",passTag).getDictValue());
	}
	
	//服务删除审核
	public void auditDeleteService(Long planId,String passTag,String suggestion,GwServiceVO serviceVO,GwUserVO loginUser,GwUserVO acceptUser)throws Exception{
		GwWorkPlanVO workPlanVO = updateWorkPlanState(planId, suggestion,passTag,loginUser.getUserId());
		if(CommonState.PASS.equals(passTag)){
			gwServiceService.delete(serviceVO);
		}else{
			//生成退回待办
			String planContentStr = "您提交删除申请的服务："+serviceVO.getServiceId()+"审核不通过。";
			saveWorkPlan("服务删除退回",WorkPlanConstent.DELETE_MODEL_SERVICE_BACK , planContentStr, WorkPlanConstent.WAIT_FOR_DEAL,
					null, null, loginUser.getUserId(),null,workPlanVO.getPlanId());
		}
		//写操作日志
		operationLogService.saveOperationLog(loginUser.getLoginName(), acceptUser.getLoginName(), OperateTypeConstent.UPDATE_SERVICE, "服务删除审核,审核结果："+SysDictManage.getSysDict("DICT_AUDIT_STATE",passTag).getDictValue());
	}
	*/
	//查询历史待办记录
	public List<GwWorkPlanView> searchHistoryWorkPlan(Long planId)throws Exception{
		return gwWorkPlanDAO.searchHistoryWorkPlan(planId);
		
	}
	
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
	public GwWorkPlanVO verifyRegisteUserPass(Long createUserId,Long parentPlanId, GwUserVO operUserVO) throws Exception{
		String userLoginName = operUserVO != null?operUserVO.getLoginName():"none";
		String extenTableKey = operUserVO != null?operUserVO.getUserId().toString():null;
		//生成权限分配待办
		String planTitle ="用户权限分配处理";
		String userTypeName = GwUserType.DATA_USER.equals(operUserVO.getUserType())?"数据":GwUserType.ORG_USER.equals(operUserVO.getUserType())?"机构":"";
		String planContentStr = "注册"+userTypeName+"用户："+userLoginName+"审核通过，请及时配置用户权限。";
		String msgContent="注册"+userTypeName+"用户："+userLoginName+"审核通过，请及时配置用户权限。【数据网关平台】";
		GwWorkPlanVO nextPlanVO = saveWorkPlan(planTitle,WorkPlanConstent.PERMISS_ALLOT, planContentStr, WorkPlanConstent.WAIT_FOR_DEAL, null, extenTableKey, createUserId,null,parentPlanId,msgContent,null);
		if(GwUserType.DATA_USER.equals(operUserVO.getUserType())){
			String command =ConfigConstants.FTP_CREATE_PATH_SHELL+ "  "+userLoginName;
			ShellUtil.execCmd(command, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, ConfigConstants.FTP_SERVER_IP);
			
			//添加合规检查ftp虚拟用户，供合规检查源文件上传
			//查询机构用户登录名称
			List<GwUserVO> orgList = userService.searchOrgUser(operUserVO.getOrgId());
			String orgName = "common";
			if(orgList != null && orgList.size()>0){
				GwUserVO orgUserVO = (GwUserVO)orgList.get(0);
				orgName = orgUserVO.getLoginName();
			}
			//解密密码
			Endecrypt endecrypt = new Endecrypt();
			String password = operUserVO != null?operUserVO.getLoginPwd():"";
			String enPassword = endecrypt.get3DESDecrypt(password, SessionConstant.SPKEY_PASSWORD);
			
			//生成ftp虚拟用户配置命令执行通知
			planTitle ="FTP虚拟用户配置命令执行通知";
			planContentStr = "请用root登录ftp服务器-"+ConfigConstants.FTP_SERVER_IP+"，执行命令：chown -R root.root /etc/vsftpd/vuser_conf/"+userLoginName;
			String FtpType= operUserVO.getFtpType();
			//生成命令
			command = ConfigConstants.VUSER_FTP_CREATE_SHELL + " "+userLoginName +" "+enPassword +" "+orgName;
			ShellUtil.execCmd(command, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, ConfigConstants.FTP_SERVER_IP);
			planTitle ="FTP虚拟用户配置命令执行通知";
			planContentStr = "请用root登录ftp服务器-"+ConfigConstants.FTP_SERVER_IP+"，执行命令：chown -R root.root /etc/vsftpd/vuser_conf/"+userLoginName;
		
			if(FtpType.equals("2")){
				//生成下载ftp用户
				command = ConfigConstants.VUSER_FTP_CREATE_SHELL + " "+userLoginName+"_down "+enPassword+"_down "+orgName;
				ShellUtil.execCmd(command, ConfigConstants.FTP_SERVER_USER, ConfigConstants.FTP_SERVER_PASSWORD, ConfigConstants.FTP_SERVER_IP);
				planContentStr = planContentStr+"和 chown -R root.root /etc/vsftpd/vuser_conf/"+userLoginName+"_down";
			}
				
			//生成ftp虚拟用户配置命令执行通知
			String msg=SysDictManage.getSysDict("DICT_USER_TYPE", operUserVO.getUserType()).getDictValue()+"："+operUserVO.getLoginName()+"注册审核通过，请用root登录ftp服务器-10.110.10.52，创建ftp虚拟用户。【数据网关平台】";
			saveWorkPlan(planTitle,WorkPlanConstent.FTP_VUSER_COMMAND_EXE, planContentStr, WorkPlanConstent.WAIT_FOR_DEAL, null, extenTableKey, createUserId,null,parentPlanId,msg,null);
		}
		return nextPlanVO;
	}

	//让任务重新往ftp推送文件
	public void doTaskRepush(Long planId,GwUserVO loginUser) throws Exception {
		updateWorkPlanState(planId, null, WorkPlanConstent.DEAL_PASS, loginUser.getUserId());
		
		Map<String, String> paramMap = workPlanParamService.searchParamMap(planId);
		String taskId = paramMap.get("taskId");
		
		GwModelDataFetchTaskVO taskVO = gwModelDataFetchTaskDAO.findById(Long.valueOf(taskId));
		taskVO.setPushCount(0);
		gwModelDataFetchTaskDAO.update(taskVO);
		log.info(loginUser.getLoginName()+"处理planId="+planId+",taskId="+taskId+"重复推送失败待办任务，重新推送");
	}
	
	//查看未处理的合规检查待办
	public Long searchCheckWorkPlan(Long taskId)throws Exception{
		
		return gwWorkPlanDAO.searchCheckWorkPlan(taskId);
	}
	
	public GwWorkPlanVO updateWorkPlanState(Long planId,Long smsId)throws Exception{
		GwWorkPlanVO gwWorkPlanVO = gwWorkPlanDAO.findById(planId);
		if(gwWorkPlanVO != null){
			gwWorkPlanVO.setPlanState("-1");
			gwWorkPlanVO.setReason("运维手工终止");
			gwWorkPlanVO.setSmsId(smsId);
			gwWorkPlanDAO.update(gwWorkPlanVO);
		}
		return gwWorkPlanVO;
	}
	
	
	public PageObject findAllByPage(PageObject pageObject) throws Exception {
		return gwWorkPlanDAO.findAllByPage(pageObject);
}
	
}
