package com.gztydic.gateway.web.action;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.config.SysDictManage;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.DataTypeConstent;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.AjaxResult;
import com.gztydic.gateway.core.common.util.AppHelper;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.GwDesenServiceFieldAuditDAO;
import com.gztydic.gateway.core.dao.GwServiceCheckRuleAuditDAO;
import com.gztydic.gateway.core.dao.GwServiceCheckRuleDAO;
import com.gztydic.gateway.core.view.GwDesenRuleServiceFieldView;
import com.gztydic.gateway.core.view.UserModelServiceAppVO;
import com.gztydic.gateway.core.vo.GwDesenServiceFieldVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchVO;
import com.gztydic.gateway.core.vo.GwModelVO;
import com.gztydic.gateway.core.vo.GwOperationLogVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleAuditVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwSysDictVO;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.model.ModelDataAppService;
import com.gztydic.gateway.model.ModelInfoService;
import com.gztydic.gateway.system.DesenModelService;
import com.gztydic.gateway.system.GwServiceService;
import com.gztydic.gateway.system.OperationLogService;
import com.gztydic.gateway.system.OrgService;
import com.gztydic.gateway.system.ProcessOperationService;
import com.gztydic.gateway.system.ProcessService;
import com.gztydic.gateway.system.ServiceDictService;
import com.gztydic.gateway.system.UploadFileService;
import com.gztydic.gateway.system.UserAuthService;
import com.gztydic.gateway.system.UserButtonService;
import com.gztydic.gateway.system.UserService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

/** 
 * @ClassName: ModelDataAppAction 
 * @Description: TODO(模型取数申请控制类) 
 * @author davis
 * @date 2014-11-21 上午10:17:27 
 *  
 */
@Controller
@Scope("prototype")
public class ModelDataAppAction extends BaseAction{
	private static final long serialVersionUID = 1L;
	
	//用户模型申请记录对象
	private GwModelDataFetchVO fetchVO;
	//用户信息对象
	private GwUserVO gwUser;
	
	//用户模型申请记录列表
//	private List<UserModelAppVo> appList;
	
	private String message;//页面提示信息
	
	private String planId;//待办ID
	
	private Long userId;//页面传输用户ID（非当前登陆用户）
	private String suggestion;//审核意见
	private Long modelId;	//模型ID
	private Long serviceId;//服务ID
	
	private String passTag;//审核结果标记
	
	private UserModelServiceAppVO userModelServiceAppVO;
	
	@Resource(name="modelDataAppServiceImpl")
	private ModelDataAppService modelDataAppService;
	
	@Resource(name="modelInfoServiceImpl")
	private ModelInfoService modelInfoService;
	@Resource(name="gwServiceServiceImpl")
	private GwServiceService serviceService;
	
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService ;
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	
	@Resource(name="desenModelServiceImpl")
	private DesenModelService desenModelService;
	
	@Resource(name="serviceDictServiceImpl")
	private ServiceDictService dictService;
	
	@Resource(name="gwServiceServiceImpl")
	private GwServiceService gwServiceService;
	
	@Resource(name = "userServiceImpl")
	private UserService userService;
	
	@Resource
	private GwServiceCheckRuleDAO checkRuleDAO;
	@Resource(name = "orgServiceImpl")
	private OrgService orgService;
	@Resource(name="userButtonServiceImpl")
	private UserButtonService userButtonService;
	@Resource
	private GwDesenServiceFieldAuditDAO fieldDesenAuditDao;
	@Resource
	private GwServiceCheckRuleAuditDAO checkRuleAuditDAO;
	@Resource(name="uploadFileServiceImpl")
	private UploadFileService uploadFileService;
	@Resource(name="processServiceImpl")
	private ProcessService processService;
	
	@Resource(name="processOperationServiceImpl")
	private ProcessOperationService processOperationService;
	@Resource(name="userAuthServiceImpl")
	private UserAuthService userAuthService;
	private GwServiceVO serviceVO;
	private Long fileId;
	/** 
	 * @Title: searchModelAppList 
	 * @Description: TODO(用户模型取数申请记录列表) 
	 * @param @return    用户模型申请列表页面 
	 * @return String    返回类型 
	 * @throws 
	 */
	public String getAppList()throws Exception{
		message = null;
		gwUser =  getLoginUser();
		if(gwUser != null){
			List<UserModelServiceAppVO> modelServiceAppList = modelDataAppService.searchUserModelServiceAppList(gwUser.getUserId());
			
			request.setAttribute("modelServiceAppList", modelServiceAppList);
			
			//写操作日志
			operationLogService.saveOperationLog(gwUser.getLoginName(), gwUser.getLoginName(), OperateTypeConstent.SEARCH_APP_UM, "查询本人模型提数申请列表，数据量："+modelServiceAppList.size()+"条。");
		}
		
		return "appList";
	}
	
	public String getServiceAppList()throws Exception{
		try {
			gwUser =  getLoginUser();
			request.setAttribute("gwUser", gwUser);
			if(pageObject == null) pageObject = new PageObject();
			if(userModelServiceAppVO == null)userModelServiceAppVO=new UserModelServiceAppVO();
			pageObject = modelDataAppService.searchServiceAppList(gwUser.getUserId(),getLoginUser().getUserType(), userModelServiceAppVO, pageObject);
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询服务申请列表列表),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询服务申请列表列表");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw e;
		}
		return "appList";
	}
	
	/** 
	 * @Title: modelAppInput 
	 * @Description: TODO(模型服务取数申请提交) 
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return String    返回类型 
	 * @throws 
	 */
	public String applyFetch()throws Exception{
		try {
			String serviceIdStr = request.getParameter("serviceId");
			String userIdStr =  request.getParameter("userId");
			Long serviceId = serviceIdStr == null?null:Long.valueOf(serviceIdStr);
			Long appUserId = serviceIdStr == null?null:Long.valueOf(userIdStr);
			GwModelDataFetchVO fetVO = modelDataAppService.searchUserServiceAppInfo(appUserId, serviceId);
			GwUserVO appUserVO=userService.searchUserDetail(appUserId);
			GwServiceVO serviceVO = modelInfoService.searchServiceById(serviceId);
			GwUploadFileVO fileVO = null;
			if(fetVO!=null){
				fileVO = uploadFileService.findById(fetVO.getCheckFileId());
				request.setAttribute("fetVO",fetVO);
				request.setAttribute("auditStatus", "0");
				request.setAttribute("fileVO", fileVO);
			}else{
				request.setAttribute("auditStatus", null);	
			}
			request.setAttribute("appUserVO", appUserVO);
			request.setAttribute("serviceVO", serviceVO);
		}catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(模型服务取数申请提交),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "模型服务取数申请提交");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
		}
		return "applyFetch";
	}
	
	public void modelServiceAppInput()throws Exception{
		AjaxResult ajaxResult = null;
		try {
			String serviceIdStr = request.getParameter("serviceId");
			String userIdStr =  request.getParameter("userId");
			Long serviceId = serviceIdStr == null?null:Long.valueOf(serviceIdStr);
			Long appUserId = serviceIdStr == null?null:Long.valueOf(userIdStr);
			GwUserVO appUserVO=userService.searchUserDetail(appUserId);
			GwServiceVO serviceVo = modelInfoService.searchServiceById(serviceId);
			if(!"1".equals(appUserVO.getPushFtp())){
				ajaxResult = AjaxResult.FAILURE(null,"push ftp信息不全，在用户修改页面补充后才可以申请");
			}else{
				fetchVO = modelDataAppService.searchUserServiceAppInfo(appUserId, serviceId);
				if(fetchVO == null ){
					fetchVO = new GwModelDataFetchVO();
					fetchVO.setUserId(userId);
					fetchVO.setModelId(serviceVo.getModelId());
					fetchVO.setServiceId(serviceId);
					fetchVO.setCreateTime(new Date());
					fetchVO.setCreateUser(getLoginUser().getLoginName());
					fetchVO.setAuditStatus(CommonState.WAIT_AUDIT);
					modelDataAppService.save(fetchVO);
				}else{
					fetchVO.setAuditStatus(CommonState.WAIT_AUDIT);
					fetchVO.setUpdateTime(new Date());
					fetchVO.setUpdateUser(getLoginUser().getLoginName());
					modelDataAppService.updateModelAppVo(fetchVO);
				}
				//生成服务字段脱敏待办
				GwUserVO loginUser = getLoginUser();
				String userType = GwUserType.DATA_USER.equals(loginUser.getUserType())?"数据":GwUserType.ORG_USER.equals(loginUser.getUserType())?"机构":"";
				String planContent = userType+"用户："+loginUser.getLoginName()+"发起服务(服务编码="+serviceVo.getServiceCode()+",服务名称="+serviceVo.getServiceName()+")的取数申请，请配置脱敏规则。";
				//待办关联表主键
				String extenTableKey = fetchVO.getUserId().toString()+","+ fetchVO.getServiceId()+","+fetchVO.getFetchId();
				//待办入库
				String msgContent=loginUser.getLoginName()+"发起服务("+serviceVo.getServiceName()+")的取数申请，请配置脱敏规则。【数据网关平台】";
				GwWorkPlanVO gwWorkPlanVO=workPlanService.saveWorkPlan("服务数据提取脱敏规则配置",WorkPlanConstent.FIELD_DESEN_CONF, planContent, WorkPlanConstent.WAIT_FOR_DEAL, null, extenTableKey, loginUser.getUserId(),null,null,msgContent,null);
				Map map=new HashMap();
				map.put("userId",fetchVO.getUserId().toString() );
				map.put("serviceId", fetchVO.getServiceId().toString());
				map.put("fetchId", fetchVO.getFetchId().toString());
				map.put("userApply", "true");
				workPlanParamService.saveParamMap(gwWorkPlanVO.getPlanId(), map);
				//写操作日志
				operationLogService.saveOperationLog(getLoginUser() .getLoginName(), getLoginUser() .getLoginName(), OperateTypeConstent.APP_UM, "提交用户服务取数申请，服务编码="+ serviceVo.getServiceCode());
				
				ajaxResult = AjaxResult.SUCCESS(null,"服务取数申请成功，请等待管理员审核！");
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(模型服务取数申请提交),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "模型服务取数申请提交");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null,"服务取数申请失败，原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	/** 
	 * @Title: saveApp 
	 * @Description: TODO(模型取数申请提交) ---已作废
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return String    返回类型 
	 * @throws 
	 */
	public String saveApp() throws Exception{
		if(fetchVO != null){
			if(fetchVO.getFetchId() != null){
				GwModelDataFetchVO appVo = modelDataAppService.searchById(fetchVO.getFetchId());
				appVo.setFetchType(fetchVO.getFetchType());
				appVo.setCycleType(fetchVO.getCycleType());
				appVo.setCycleNum(fetchVO.getCycleNum());
				appVo.setAuditStatus(CommonState.WAIT_AUDIT);
				appVo.setUpdateTime(new Date());
				if(getLoginUser() != null){
					appVo.setUpdateUser(getLoginUser().getLoginName());
				}
				modelDataAppService.updateModelAppVo(appVo);
			}else{
				fetchVO.setCreateTime(new Date());
				fetchVO.setAuditStatus(CommonState.WAIT_AUDIT);
				if(getLoginUser() != null){
					fetchVO.setCreateUser(getLoginUser().getLoginName());
				}
				modelDataAppService.save(fetchVO);
			}
			//生成待办
			Long userId = null;
			String userName = null;
			String serviceName = null;
			if(getLoginUser() != null){
				userId = getLoginUser() .getUserId();
				userName =  getLoginUser().getUserName();
			}
			GwServiceVO service = modelInfoService.searchServiceById(fetchVO.getServiceId());
			if(service != null){
				serviceName = service.getServiceName();
			}
			String planContent = "用户："+userName+"发起服务："+serviceName+"取数申请，请配置脱敏规则。";
			//待办关联表主键
			String extenTableKey = fetchVO.getUserId().toString()+","+ fetchVO.getServiceId()+","+fetchVO.getFetchId();
			//待办入库
			String msgContent=userName+"申请服务("+serviceName+"取数，请审核！【数据网关平台】";
			GwWorkPlanVO gwWorkPlanVO=workPlanService.saveWorkPlan("模型数据提取脱敏规则配置",WorkPlanConstent.FIELD_DESEN_CONF, planContent, WorkPlanConstent.WAIT_FOR_DEAL, null, extenTableKey, userId,null,null,msgContent,null);
			Map map=new HashMap();
			map.put("userId",fetchVO.getUserId().toString() );
			map.put("serviceId", fetchVO.getServiceId().toString());
			map.put("fetchId", fetchVO.getFetchId().toString());
			workPlanParamService.saveParamMap(gwWorkPlanVO.getPlanId(), map);
			//写操作日志
			operationLogService.saveOperationLog(getLoginUser() .getLoginName(), getLoginUser() .getLoginName(), OperateTypeConstent.APP_UM, "提交用户模型服务取数申请，服务ID-"+ fetchVO.getServiceId());
		}
		getServiceAppList();
		return "appList";
	}
	
	/** 
	 * @Title: searchVerify 
	 * @Description: TODO(获取模型取数申请待办信息) 
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return String    返回类型 
	 * @throws 
	 */
	public String searchVerify() throws Exception{
		planId = request.getParameter("workPlanId");
		Map<String, String> paramMap = workPlanParamService.searchParamMap(Long.valueOf(planId));
		String userId = paramMap.get("userId");
		String serviceId = paramMap.get("serviceId");
		String fetchId = paramMap.get("fetchId");
		String preBatch = paramMap.get("preBatch");
		String desenPreBatch = paramMap.get("desenPreBatch");
		String desenBatch = paramMap.get("desenBatch");
		String ruleBatch = paramMap.get("ruleBatch");
		String type = paramMap.get("type");
		GwServiceVO serviceVO = gwServiceService.searchService(Long.valueOf(serviceId));
		GwModelVO modelVo = null;
		if(serviceVO.getModelId() != null){
			modelVo = modelInfoService.searchModelById(serviceVO.getModelId());
		}
		//查询脱敏配置信息
		GwModelDataFetchVO modelDataFetchVO=modelDataAppService.searchUserServiceAppInfo(Long.valueOf(userId), Long.valueOf(serviceId));
		request.setAttribute("modelDataFetchVO", modelDataFetchVO);
		//合规
		List<GwServiceCheckRuleAuditVO> checkRuleList = checkRuleAuditDAO.searchCheckRuleList(Long.valueOf(ruleBatch));
		request.setAttribute("checkRuleList", checkRuleList);
		//脱敏并合规
		if(CommonState.SERVICE_SOURCE_108.equals(serviceVO.getServiceSource())){
			List<GwDesenRuleServiceFieldView> ruleViewList = fieldDesenAuditDao.search108DesenRuleServiceFieldList(Long.valueOf(userId), Long.valueOf(serviceId),Long.valueOf(desenBatch),Long.valueOf(ruleBatch));
			request.setAttribute("ruleViewList", ruleViewList);
			request.setAttribute("dictByRule", "1");
		}else{
			List<GwDesenRuleServiceFieldView> ruleViewList = fieldDesenAuditDao.searchDesenRuleServiceFieldList(Long.valueOf(userId), Long.valueOf(serviceId),Long.valueOf(desenBatch));
			request.setAttribute("ruleViewList", ruleViewList);
		}
		
		//前脱敏规则
		if(StringUtils.isNotBlank(desenPreBatch)&& !Integer.valueOf(desenPreBatch).equals(0)){

			List<GwDesenRuleServiceFieldView> preRuleViewList = fieldDesenAuditDao.searchDesenRuleServiceFieldList(Long.valueOf(userId), Long.valueOf(serviceId),Long.valueOf(desenPreBatch));
			request.setAttribute("preRuleViewList", preRuleViewList);
			request.setAttribute("preDesenType", paramMap.get("desenType"));
			request.setAttribute("preMaxCheckNum", paramMap.get("maxCheckNum"));
			request.setAttribute("preCheckAudit", paramMap.get("checkAudit"));
			request.setAttribute("preOutputNum", paramMap.get("outputNum"));
		}
		
		//前合规规则
		if(StringUtils.isNotBlank(preBatch)&& !Integer.valueOf(preBatch).equals(0)){
			/*if(CommonState.SERVICE_SOURCE_108.equals(serviceVO.getServiceSource())){				
					List<GwServiceCheckRuleVO> preCheckRuleList = checkRuleDAO.searchCheckRuleList(Long.valueOf(preBatch));
					request.setAttribute("preCheckRuleList", preCheckRuleList);
			}else{
				GwModelDataFetchTaskVO taskVO = new GwModelDataFetchTaskVO();
				taskVO.setCheckBatch(Long.valueOf(desenPreBatch));
				taskVO.setUserId(Long.valueOf(userId));
				taskVO.setServiceId(Long.valueOf(serviceId));
				List<GwDesenRuleServiceFieldView> preRuleViewList = desenModelService.searchDesenRuleListByBatch(taskVO);
				request.setAttribute("preRuleViewList", preRuleViewList);
			}*/
			List<GwServiceCheckRuleVO> preCheckRuleList = checkRuleDAO.searchCheckRuleList(Long.valueOf(preBatch));
			request.setAttribute("preCheckRuleList", preCheckRuleList);
			request.setAttribute("preDesenType", paramMap.get("desenType"));
			request.setAttribute("preMaxCheckNum", paramMap.get("maxCheckNum"));
			request.setAttribute("preCheckAudit", paramMap.get("checkAudit"));
			request.setAttribute("preOutputNum", paramMap.get("outputNum"));
		}
		
		request.setAttribute("fieldDict", JSONObject.fromObject(dictService.searchFieldDictAuditMap(Long.valueOf(desenBatch))));	//字段、字典关联数据
		request.setAttribute("preFieldDict", JSONObject.fromObject(dictService.searchFieldDictMapByBatch(Long.valueOf(preBatch))));	//字段、字典关联数据
		request.setAttribute("dictMap", JSONObject.fromObject(dictService.searchDictMap()));	
		request.setAttribute("groupDict", dictService.searchGroupDict());
		request.setAttribute("serviceVO", serviceVO);
		request.setAttribute("modelVo", modelVo);
		request.setAttribute("fetchId", "fetchId");
		GwWorkPlanVO preWorkPlanVO=workPlanService.searchById(Long.parseLong(planId));
		GwUserVO createUser=userService.searchUserDetail(preWorkPlanVO.getCreateUserId());
		request.setAttribute("preWorkPlanVO", preWorkPlanVO);
		request.setAttribute("createUser", createUser);
		request.setAttribute("type", type);
		return "appVerify";
	}
	
	//审核取数申请
	public String verifyApp()throws Exception{
		String passTag = request.getParameter("passTag");
		GwUserVO loginUser = getLoginUser();
		
		GwModelDataFetchVO appFetchVO = modelDataAppService.doVerifyApp(passTag, planId, suggestion, loginUser);
		if(appFetchVO != null && appFetchVO.getFetchId() != null){
//			modelDataAppService.createOfflineTask(appFetchVO.getFetchId());
		}
		if(pageObject == null) pageObject = new PageObject();
		//查询待办信息
		String planTypes =userButtonService.searchPlanTypeByUserType(loginUser.getUserType());
		pageObject = workPlanService.searchByParam(planTypes,null, null, null,pageObject,CommonState.WAIT_AUDIT);
		return "workList";
	}

	//实时检查
	public void onlineCheck()throws Exception{
		AjaxResult ajaxResult = null;
		try {
			String userId=request.getParameter("userId");
			String serviceId=request.getParameter("serviceId");
			String fetchId=request.getParameter("fetchId");
			String auditTimeStr=request.getParameter("auditTime");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date auditTime = sdf.parse(auditTimeStr);
			String fileList=request.getParameter("fileList");
			GwServiceVO serviceVO = gwServiceService.searchService(Long.valueOf(serviceId));
			GwOperationLogVO logVO=new GwOperationLogVO();
			GwUserVO loginUser=getLoginUser();
			logVO.setOperationUser(loginUser.getLoginName());
			GwUserVO acceptUser=userService.searchUserDetail(Long.valueOf(userId));
			GwUserVO orgUser=orgService.searchOrgUser(acceptUser.getOrgId());
			logVO.setAcceptUser(acceptUser.getLoginName());
			String operationContent=loginUser.getLoginName()+"创建实时检查任务！";
			logVO.setOperationContent(operationContent);
			logVO.setOperationType(OperateTypeConstent.REDO_SERVICE);
			logVO.setOperationTime(new Date());
			modelDataAppService.onlineCheck(acceptUser, serviceVO,Long.valueOf(fetchId),auditTime,fileList, logVO,loginUser,orgUser);
			ajaxResult = AjaxResult.SUCCESS(null,"实时检查任务创建成功！");
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(实时检查),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "实时检查任务创建时发生错误"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "实时检查");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null,"实时检查任务创建错误！原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	/**********跳转审核人员创建并取数审批页面*******/
	public String serviceFetchAudit()throws Exception{
		Long workPlanId = Long.parseLong(request.getParameter("workPlanId"));
		String ruleBatch = null;
		String fetchId = null;
		if(workPlanId!=null){
			GwWorkPlanVO workPlanVO = workPlanService.searchById(workPlanId);
			Map<String, String> paramMap = workPlanParamService.searchParamMap(workPlanId);
			ruleBatch = paramMap.get("ruleBatch");
			fetchId = paramMap.get("fetchId");
			serviceId= Long.valueOf(paramMap.get("serviceId"));
			request.setAttribute("fetchId", fetchId);
			request.setAttribute("workPlanVO", workPlanVO);
			GwUserVO createUser=userService.searchUserDetail(workPlanVO.getCreateUserId());
			request.setAttribute("createUser", createUser);
			request.setAttribute("planId", workPlanId);
		}
		GwServiceVO serviceVO = serviceService.searchService(serviceId);
		request.setAttribute("serviceVO", serviceVO);
		if(serviceVO.getModelId() != null){
			GwModelVO model = modelInfoService.searchModelById(serviceVO.getModelId());
			request.setAttribute("model", model);
		}
		if(ruleBatch != null){//数据用户上载的规则表
			List<GwServiceCheckRuleAuditVO> checkRuleAuditList = checkRuleAuditDAO.searchCheckRuleList(Long.valueOf(ruleBatch));
			request.setAttribute("checkRuleList", checkRuleAuditList);
		}						
		return "serviceFetchAudit2";
	}
	/**********审核人员创建并取数审批处理*******/
	public String serviceFetchAuditDeal()throws Exception{
		String passTag = request.getParameter("passTag");
		GwUserVO loginUser = getLoginUser();
		GwModelDataFetchVO appFetchVO = modelDataAppService.serviceFetchAuditDeal(passTag, planId, suggestion, loginUser);
		if(appFetchVO != null && appFetchVO.getFetchId() != null){
//			modelDataAppService.createOfflineTask(appFetchVO.getFetchId());
		}
		if(pageObject == null) pageObject = new PageObject();
		//查询待办信息
		String planTypes =userButtonService.searchPlanTypeByUserType(loginUser.getUserType());
		pageObject = workPlanService.searchByParam(planTypes,null, null, null,pageObject,CommonState.WAIT_AUDIT);
		return "workList";
	}
	/** 
	 * @Title: verifyAppRule 
	 * @Description: TODO(服务合规规则配置查看) 
	 * @param @param serviceId 服务编码 
	 * @return void    返回类型 
	 * @throws 
	 */
	public String verifyAppRule() throws Exception{
		if(getLoginUser() != null){
			String serviceId=request.getParameter("serviceId");
			String userId=request.getParameter("userId");
			String loginName=request.getParameter("loginName");
			String serviceName=request.getParameter("serviceName");
			String serviceCode=request.getParameter("serviceCode");
			String userType = getLoginUser().getUserType();
			GwServiceVO gwServiceVO = new GwServiceVO();
			if(GwUserType.AUDIT_USER.equals(userType) || GwUserType.DATA_USER.equals(userType) || GwUserType.SAFE_USER.equals(userType)){
				if(StringUtils.isNotBlank(serviceId)){
					gwServiceVO = serviceService.searchService(Long.valueOf(serviceId));
				}
				
				if(pageObject == null) {
					pageObject = new PageObject();
				}
				if(StringUtils.isNotBlank(serviceId) && StringUtils.isNotBlank(userId)){//规则查看
					pageObject = desenModelService.searchServiceCheckRule(Long.valueOf(serviceId), Long.valueOf(userId),pageObject);
				}
				
				GwModelDataFetchVO gwModelDataFetchVO = modelDataAppService.searchUserServiceAppInfo(Long.valueOf(userId), Long.valueOf(serviceId));
				GwUploadFileVO gwUploadFileVO = uploadFileService.findById(gwModelDataFetchVO.getCheckFileId());
				Map<String, GwSysDictVO> checkDictMap = SysDictManage.getSysDict("DICT_CHECK_RULE_TYPE");
				
				request.setAttribute("checkDictMap",checkDictMap);
				request.setAttribute("fileId", gwModelDataFetchVO.getCheckFileId());
				request.setAttribute("fileName", gwUploadFileVO.getRealName());
				request.setAttribute("filePath", gwUploadFileVO.getFilePath());
				request.setAttribute("pageObject", pageObject);
				request.setAttribute("serviceId", serviceId);
				request.setAttribute("userId", userId);
				request.setAttribute("loginName", loginName);
				//request.setAttribute("serviceCode", serviceCode);
				//request.setAttribute("serviceName", serviceName);
				request.setAttribute("gwServiceVO", gwServiceVO);
			}
		}
		return "serviceRuleDetail";
	}
	
	/** 
	 * @Title: showRuleFile 
	 * @Description: TODO(保存和解析上传的服务合规规则) 
	 * @return void    返回类型 
	 * @throws 
	 */
	public void showRuleFile()throws Exception{
		AjaxResult ajaxResult = null;
		HashMap<String, Object> map = new HashMap<String,Object>();
		try{
			GwUserVO loginUser = getLoginUser();
			GwUploadFileVO fileVo = this.upLoadFile("02",String.valueOf(getLoginUser().getUserId()));
			if(StringUtils.isNotBlank(fileVo.getRealName())){
				//保存附件
				uploadFileService.save(fileVo);
				//写入日志
				operationLogService.saveOperationLog(loginUser.getLoginName(), loginUser.getLoginName(),OperateTypeConstent.UPDATE_SERVICE, loginUser.getLoginName()+"上传合规检查规则文件！");
				//解析Excel文件
				List<GwServiceCheckRuleVO> checkRuleList = desenModelService.parseServiceCheckRule(upload, null, null);
				Map<String, GwSysDictVO> checkDictMap = SysDictManage.getSysDict("DICT_CHECK_RULE_TYPE");
				map.put("checkRuleList", checkRuleList);
				map.put("fileVo", fileVo);
				map.put("checkDictMap",checkDictMap);
				
				ajaxResult=AjaxResult.SUCCESS(map);
			}
			
							
		}catch(Exception e){
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null, "合规检查规则文件上存失败。原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, AppHelper.CONTENT_TYPE_HTML, response);
	}
	
	/** 
	 * @Title: updateServiceRule 
	 * @Description: TODO(服务合规规则配置修改) 
	 * @param @param serviceId 服务编码 
	 * @return void    返回类型 
	 * @throws 
	 */
	public void updateServiceRule() throws Exception{
		AjaxResult ajaxResult = null;
		String fileId = request.getParameter("fileId");
		String prefileId = request.getParameter("prefileId");
		String serviceId = request.getParameter("serviceId");
		GwUploadFileVO fileVo = null;
		GwUserVO loginUser = getLoginUser();
		GwServiceVO serviceVO = serviceService.searchService(Long.valueOf(serviceId));
		try {
			if((fileId == ""||fileId==null))throw new Exception("您还没配置规则文件！");
			//若fileId是新上传的规则表ID，若没上传就用preFileId之前的规则表
		    fileVo = uploadFileService.findById(Long.valueOf(fileId));		    
			//解析上存文件路径
			String fileVoPathStr[] = fileVo.getFilePath().split("/");
			imageFileName = fileVoPathStr[fileVoPathStr.length-1];
			String filePath = ConfigConstants.BASE_UPLOAD_FILE_PATH+"/uploadFile/"+"desenManage"+"/";
			File upload =new File(filePath + File.separator + imageFileName);
			GwModelDataFetchVO fetchVO = modelDataAppService.searchUserServiceAppInfo(loginUser.getUserId(), serviceVO.getServiceId());
			
			if(fetchVO==null){
				fetchVO = new GwModelDataFetchVO();
				fetchVO.setUserId(getLoginUser().getUserId());
				fetchVO.setModelId(serviceVO.getModelId());
				fetchVO.setServiceId(serviceVO.getServiceId());
				fetchVO.setCreateTime(new Date());
				fetchVO.setCreateUser(getLoginUser().getLoginName());
				fetchVO.setAuditStatus(CommonState.WAIT_AUDIT);
				fetchVO.setCheckFileId(fileVo.getFileId());
				fetchVO.setDesenType("2");
				modelDataAppService.save(fetchVO);	
				fetchVO = modelDataAppService.searchUserServiceAppInfo(fetchVO.getUserId(), fetchVO.getServiceId());
			}else{
				fetchVO.setAuditStatus(CommonState.WAIT_AUDIT);
				fetchVO.setUpdateTime(new Date());
				fetchVO.setCheckFileId(fileVo.getFileId());
				modelDataAppService.update(fetchVO);
				//更新服务
				modelInfoService.updateServiceInfo(serviceVO,getLoginUser());
			}
			//解析合规检查表并给数据安全管理员一条审核记录
			desenModelService.savaRuleAndWorkPlan(fetchVO,serviceVO, loginUser, fileVo, upload, "1",setProcessId(),DataTypeConstent.XIUGAIGUIZHESHENPI);				
			ajaxResult = AjaxResult.SUCCESS();
			
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(修改服务合规检查规则),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "修改服务合规检查规则！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "修改服务合规检查规则");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null, "修改服务合规检查规则失败，原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	/****
	 * @Title: createNewServiceTask 
	 * @Description: TODO(创建新的服务取数任务) 
	 * @return void    返回类型 
	 * @throws 
	 */
	public String queryOldServiceRule() throws Exception{
		String checkBatch = request.getParameter("checkBatch");
		String userType = getLoginUser().getUserType();
		if(GwUserType.AUDIT_USER.equals(userType) || GwUserType.DATA_USER.equals(userType) || GwUserType.SAFE_USER.equals(userType)){
			if(checkBatch != "" && checkBatch!=null){//数据用户上载的规则表
				List<GwServiceCheckRuleAuditVO> checkRuleAuditList = checkRuleAuditDAO.searchCheckRuleList(Long.valueOf(checkBatch));
				request.setAttribute("checkRuleAuditList", checkRuleAuditList);
			}
		}
		return "oldServiceRuleDetail";
		
	}
	
	/** 
	 * @Title: createNewServiceTask 
	 * @Description: TODO(创建新的服务取数任务) 
	 * @return void    返回类型 
	 * @throws 
	 */
	public void createNewServiceTask()throws Exception{
		AjaxResult ajaxResult = null;
		try {
			String userId=request.getParameter("userId");
			String serviceId=request.getParameter("serviceId");
			String fetchId=request.getParameter("fetchId");
			String auditTimeStr=request.getParameter("auditTime");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date auditTime = sdf.parse(auditTimeStr);
			String fileName=request.getParameter("fileName");
			String planIds=request.getParameter("planId");
			GwServiceVO serviceVO = gwServiceService.searchService(Long.valueOf(serviceId));
			GwOperationLogVO logVO=new GwOperationLogVO();
			GwUserVO loginUser=getLoginUser();
			logVO.setOperationUser(loginUser.getLoginName());
			GwUserVO acceptUser=userService.searchUserDetail(Long.valueOf(userId));
			GwUserVO orgUser=orgService.searchOrgUser(acceptUser.getOrgId());
			logVO.setAcceptUser(acceptUser.getLoginName());
			String operationContent=loginUser.getLoginName()+"确认创建服务取数任务！";
			logVO.setOperationContent(operationContent);
			logVO.setOperationType(OperateTypeConstent.CREATE_SERVICE);
			logVO.setOperationTime(new Date());
			
			//GwUserVO loginUser=getLoginUser();
			if(!StringUtils.isEmpty(planIds)){
				workPlanService.updateWorkPlanState(Long.valueOf(planIds),null, WorkPlanConstent.DEAL_PASS, loginUser.getUserId());
			}
			modelDataAppService.createNewServiceTask(acceptUser, serviceVO,Long.valueOf(fetchId),auditTime,fileName, logVO,loginUser,orgUser,setProcessId());
			ajaxResult = AjaxResult.SUCCESS(null,"服务取数任务创建成功！");
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(实时检查),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "服务任务创建时发生错误"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "服务任务创建");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null,"服务任务创建错误！原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	public GwModelDataFetchVO getFetchVO() {
		return fetchVO;
	}

	public void setFetchVO(GwModelDataFetchVO fetchVO) {
		this.fetchVO = fetchVO;
	}

	public GwUserVO getGwUser() {
		return gwUser;
	}

	public void setGwUser(GwUserVO gwUser) {
		this.gwUser = gwUser;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getPassTag() {
		return passTag;
	}

	public void setPassTag(String passTag) {
		this.passTag = passTag;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public UserModelServiceAppVO getUserModelServiceAppVO() {
		return userModelServiceAppVO;
	}

	public void setUserModelServiceAppVO(UserModelServiceAppVO userModelServiceAppVO) {
		this.userModelServiceAppVO = userModelServiceAppVO;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public GwServiceVO getServiceVO() {
		return serviceVO;
	}

	public void setServiceVO(GwServiceVO serviceVO) {
		this.serviceVO = serviceVO;
	}
	
	
}
