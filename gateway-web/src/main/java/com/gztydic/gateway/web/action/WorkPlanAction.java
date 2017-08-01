package com.gztydic.gateway.web.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import com.gztydic.gateway.core.common.util.BeanUtil;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.GwServiceCheckRuleAuditDAO;
import com.gztydic.gateway.core.view.GwDesenRuleServiceFieldView;
import com.gztydic.gateway.core.view.GwServiceView;
import com.gztydic.gateway.core.view.GwWorkPlanView;
import com.gztydic.gateway.core.view.ModelFileView;
import com.gztydic.gateway.core.view.RoleView;
import com.gztydic.gateway.core.view.UserAuditView;
import com.gztydic.gateway.core.view.UserModelServiceAppVO;
import com.gztydic.gateway.core.view.UserView;
import com.gztydic.gateway.core.vo.GwDesenServiceInfoVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchTaskVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchVO;
import com.gztydic.gateway.core.vo.GwModelDataFileVO;
import com.gztydic.gateway.core.vo.GwModelVO;
import com.gztydic.gateway.core.vo.GwModifyRecordVO;
import com.gztydic.gateway.core.vo.GwOrgVO;
import com.gztydic.gateway.core.vo.GwProcessOperationVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleAuditVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleVO;
import com.gztydic.gateway.core.vo.GwServiceFieldVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwSysDictVO;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.gather.DataGatherService;
import com.gztydic.gateway.model.ModelDataAppServiceImpl;
import com.gztydic.gateway.model.ModelDataTaskService;
import com.gztydic.gateway.model.ModelInfoService;
import com.gztydic.gateway.system.DataCycleService;
import com.gztydic.gateway.system.DesenModelService;
import com.gztydic.gateway.system.GwOrgService;
import com.gztydic.gateway.system.GwServiceService;
import com.gztydic.gateway.system.GwUserService;
import com.gztydic.gateway.system.OperationLogService;
import com.gztydic.gateway.system.OrgService;
import com.gztydic.gateway.system.ProcessOperationService;
import com.gztydic.gateway.system.ProcessService;
import com.gztydic.gateway.system.RoleAuthService;
import com.gztydic.gateway.system.UploadFileService;
import com.gztydic.gateway.system.UserAuthService;
import com.gztydic.gateway.system.UserButtonService;
import com.gztydic.gateway.system.UserService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

/** 
 * @ClassName: WorkPlanAction 
 * @Description: TODO(待办任务页面访问控制类) 
 * @author davis
 * @date 2014-11-24 下午10:54:36 
 *  
 */
@Controller
@Scope("prototype")
public class WorkPlanAction  extends BaseAction{
	
	private final Log log = LogFactory.getLog(WorkPlanAction.class);
	
	private static final long serialVersionUID = 1L;
	
	private String message;
	
	private String planType;
	
	private String planTitle;
	
	private String planContent;
	
	private String planId;//待办ID
	private Long userId;//页面传输用户ID（非当前登陆用户）
	private String suggestion;//审核意见
	private GwWorkPlanVO workPlan;
	private UserAuditView userVO;
	private GwUserVO user;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	
	@Resource(name = "userServiceImpl")
	private UserService userService;
	@Resource(name = "orgServiceImpl")
	private OrgService orgService;
	@Resource(name="gwUserServiceImpl")
	private GwUserService gwUserService;
	@Resource(name="gwOrgServiceImpl")
	private GwOrgService gwOrgService;
	@Resource(name="userAuthServiceImpl")
	private UserAuthService userAuthService;
	@Resource(name="modelDataAppServiceImpl")
	private ModelDataAppServiceImpl modelDataAppService;
	@Resource(name="uploadFileServiceImpl")
	private UploadFileService uploadFileService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	@Resource(name="roleAuthServiceImpl")
	private RoleAuthService roleAuthService;
	
	@Resource(name="modelInfoServiceImpl")
	private ModelInfoService modelInfoService;
	@Resource(name="desenModelServiceImpl")
	private DesenModelService desenModelService;
	private GwModifyRecordVO modifyRecord;
	@Resource(name="dataGatherServiceImpl")
	private DataGatherService dataGatherService;
	@Resource(name="modelDataTaskServiceImpl")
	private ModelDataTaskService modelDataTaskService;
	@Resource(name="gwServiceServiceImpl")
	private GwServiceService gwServiceService;
	
	@Resource(name="userButtonServiceImpl")
	private UserButtonService userButtonService;
	
	@Resource(name="dataCycleServiceImpl")
	private DataCycleService dataCycleService;
	
	@Resource(name="gwServiceServiceImpl")
	private GwServiceService serviceService;
	@Resource(name="processServiceImpl")
	private ProcessService processService;
	@Resource(name="processOperationServiceImpl")
	private ProcessOperationService processOperationService; 
	@Resource
	private GwServiceCheckRuleAuditDAO checkRuleAuditDAO;
	
	//无分页查询
	public String searchAllPlan() throws Exception{
		GwUserVO userVO = getLoginUser();
		userVO = userService.searchUserDetail(userVO.getUserId());
		if(userVO != null){
			String userType = userVO.getUserType();
			List<GwWorkPlanVO> workList = new ArrayList<GwWorkPlanVO>();
			if(GwUserType.AUDIT_USER.equals(userType) ||GwUserType.SAFE_USER.equals(userType)||GwUserType.MAINATE_USER.equals(userType)||GwUserType.SUPER_USER.equals(userType)){
				if(CommonState.ONLINE.equals(userVO.getOnlineStatus()) ){
					//String planTypes = WorkPlanConstent.getPlanTypes(userType); 管理用户任务类型可配置
					String planTypes =userButtonService.searchPlanTypeByUserType(userType);
					workList = workPlanService.searchWorkPlanByType(planTypes);
				}
			}else {
				//查询待办信息
				workList = workPlanService.searchUserWorkPlan(userVO.getUserId());
			}
			request.setAttribute("workList", workList);
		}
		return "workList";
	}
	
	
	//条件分页查询
	public String searchByParam() throws Exception{
		GwUserVO userVO = getLoginUser();
		if(userVO != null){
			userVO = userService.searchUserDetail(userVO.getUserId());
			String userType = userVO.getUserType();
			if(pageObject == null) {
				pageObject = new PageObject();
			}			
			if(GwUserType.AUDIT_USER.equals(userType) ||GwUserType.SAFE_USER.equals(userType)||GwUserType.MAINATE_USER.equals(userType)||GwUserType.SUPER_USER.equals(userType)){
				if(CommonState.ONLINE.equals(userVO.getOnlineStatus()) ){
					//String planTypes = WorkPlanConstent.getPlanTypes(userType); 管理用户任务类型可配置
					String planTypes =userButtonService.searchPlanTypeByUserType(userType);
					pageObject = workPlanService.searchByParam(planTypes,planType, planTitle, planContent,pageObject,CommonState.WAIT_AUDIT);
				}
			}else {
				pageObject = workPlanService.searchByParam(userVO.getUserId(),planType, planTitle, planContent,pageObject,CommonState.WAIT_AUDIT);
			}
			//写操作日志
			operationLogService.saveOperationLog(userVO.getLoginName(), userVO.getLoginName(), OperateTypeConstent.SEARCH_PLAN, "查询自己待办任务列表，数据量："+pageObject.getDataCount()+"条。");
		}
		return  "workList";
	}
	
	//条件查询已办事项
	public String searchWorkPlanByParam() throws Exception{
		GwUserVO userVO = getLoginUser();
		if(userVO != null){
			String userType = userVO.getUserType();
			if(pageObject == null) {
				pageObject = new PageObject();
			}	
			String planStateString=CommonState.PASS+","+CommonState.NO_PASS;
			
			pageObject = workPlanService.searchByParam(userVO.getUserId(),planType, planTitle, planContent,pageObject,planStateString);
			
			//写操作日志
			operationLogService.saveOperationLog(userVO.getLoginName(), userVO.getLoginName(), OperateTypeConstent.SEARCH_PLAN, "查询自己已办任务列表，数据量："+pageObject.getDataCount()+"条。");
		}
		return  "dealAreadyWorkList";
	}
	
	//条件查询撤回事项
	public String searchRevokeWorkPlanByParam() throws Exception{
		GwUserVO userVO = getLoginUser();
		if(userVO != null){
			String userType = userVO.getUserType();
			if(pageObject == null) {
				pageObject = new PageObject();
			}	
			if(GwUserType.AUDIT_USER.equals(userType) ||GwUserType.SAFE_USER.equals(userType)||GwUserType.MAINATE_USER.equals(userType)||GwUserType.SUPER_USER.equals(userType)){
				if(CommonState.ONLINE.equals(userVO.getOnlineStatus()) ){
					//String planTypes = WorkPlanConstent.getPlanTypes(userType); 管理用户任务类型可配置
					String planTypes =userButtonService.searchPlanTypeByUserType(userType);
					pageObject = workPlanService.searchByParam(planTypes,planType, planTitle, planContent,pageObject,"-1");
				}
			}else {
				pageObject = workPlanService.searchByParam(userVO.getUserId(),planType, planTitle, planContent,pageObject,"-1");
			}
			//写操作日志
			operationLogService.saveOperationLog(userVO.getLoginName(), userVO.getLoginName(), OperateTypeConstent.SEARCH_PLAN, "查询撤回待办任务列表，数据量："+pageObject.getDataCount()+"条。");
		}
		return  "revokeWorkPlanList";
	}
		
	//查看待办详情
	public String searchWorkPlanDetail()throws Exception{
		GwUserVO loginUser=getLoginUser();
		String planId=request.getParameter("planId");
		String source=request.getParameter("source");//区分已办和撤回
		GwWorkPlanVO workPlan = workPlanService.searchById(Long.parseLong(planId));
		if(WorkPlanConstent.RULE_CHECK_AUDIT_1.equals(workPlan.getPlanType()) ||WorkPlanConstent.RULE_CHECK_AUDIT_BACK.equals(workPlan.getPlanType())||
				WorkPlanConstent.RULE_CHECK_AUDIT_2.equals(workPlan.getPlanType())){
			Map<String, String> paramMap;
			if(workPlan.getParentPlanId()==null){
				paramMap = workPlanParamService.searchParamMap(Long.valueOf(planId));
			}else{
				paramMap = workPlanParamService.searchParamMap(workPlan.getParentPlanId());
			}
			String userId=paramMap.get("userId");
			String taskId=paramMap.get("taskId");
			String serviceId=paramMap.get("serviceId");
			GwUserVO dataUserVO=  gwUserService.searchById(Long.valueOf(userId));
			GwUserVO orgUserVO=orgService.searchOrgUser(dataUserVO.getOrgId());
			request.setAttribute("dataUserVO", dataUserVO);
			request.setAttribute("orgUserVO", orgUserVO);
			GwModelDataFetchTaskVO taskVO = modelDataTaskService.searchById(Long.parseLong(taskId));
			request.setAttribute("taskVO", taskVO);
			GwServiceVO service = gwServiceService.searchService(Long.parseLong(serviceId));
			request.setAttribute("service", service);
			GwModelDataFileVO fileVO=modelInfoService.searchModelDataFile(Long.valueOf(taskId), "1");
			request.setAttribute("fileVO", fileVO);
			//审核记录
			List<GwWorkPlanView> hisWorkList = workPlanService.searchHistoryWorkPlan(workPlan.getPlanId());
			request.setAttribute("preWorkPlan", hisWorkList);
		}
		GwUserVO createUser=userService.searchUserDetail(workPlan.getCreateUserId());
		request.setAttribute("workPlan", workPlan);
		request.setAttribute("createUser", createUser);
		request.setAttribute("source", source);
		request.setAttribute("loginUser", loginUser);
		return "workPlanDetail";
	}
	
    //跳转到用户注册审核页面
	public String signUpVerify() throws Exception {
		planId = request.getParameter("workPlanId");
		
		GwUserVO gwUserVo = userService.searchUserDetail(userId);
		GwOrgVO gwOrgVo = orgService.searchOrg(gwUserVo.getOrgId());
		Long fileId = gwUserVo.getFileId();
		if(fileId != null){
			GwUploadFileVO fileVo  = uploadFileService.findById(fileId);
			request.setAttribute("fileVo",fileVo);
		}
		 request.setAttribute("gwUserVo",gwUserVo);
		 request.setAttribute("gwOrgVo",gwOrgVo);
		return "verify";
	}
	
	//跳转到用户注册退回处理页面
	public String signUpBack() throws Exception {
		planId = request.getParameter("workPlanId");
		String oldPlanId = request.getParameter("oldPlanId");
		GwWorkPlanVO planVo = workPlanService.searchById(Long.valueOf(planId));
		GwWorkPlanVO oldPlanVo = workPlanService.searchById(Long.valueOf(oldPlanId));
		GwUserVO createUser=userService.searchUserDetail(planVo.getCreateUserId());
		if(planVo != null){
			suggestion = oldPlanVo.getSuggestion();
		}
		GwUserVO gwUserVo = userService.searchUserDetail(userId);
		GwOrgVO gwOrgVo = orgService.searchOrg(gwUserVo.getOrgId());
		Long fileId = gwUserVo.getFileId();
		if(fileId != null){
			GwUploadFileVO fileVo  = uploadFileService.findById(fileId);
			request.setAttribute("fileVo",fileVo);
		}
		
		List<GwOrgVO> gwOrgs = gwOrgService.getGwOrgListByStatus();
		request.setAttribute("oldPlanId", oldPlanId);
		request.setAttribute("gwOrgs", gwOrgs);
		request.setAttribute("gwUserVo",gwUserVo);
		request.setAttribute("gwOrgVo",gwOrgVo);
		request.setAttribute("createUser", createUser);
		request.setAttribute("planVo", planVo);
		return "signBack";
	}
	
     //用户注册退回文件删除
	public void deleteFile() throws Exception {
		String oldPlanId = request.getParameter("oldPlanId");
		GwWorkPlanVO planVo = workPlanService.searchById(Long.valueOf(oldPlanId));
		if(planVo != null){
			suggestion = planVo.getSuggestion();
		}
		GwUserVO gwUserVo = userService.searchUserDetail(userId);
		gwUserVo.setFileId(null);
		gwUserService.updateGwUser(gwUserVo);
		GwOrgVO gwOrgVo = orgService.searchOrg(gwUserVo.getOrgId());
		
		List<GwOrgVO> gwOrgs = gwOrgService.getGwOrgListByStatus();
		request.setAttribute("oldPlanId", oldPlanId);
		request.setAttribute("gwOrgs", gwOrgs);
		 request.setAttribute("gwUserVo",gwUserVo);
		 request.setAttribute("gwOrgVo",gwOrgVo);
		 AjaxResult ajaxResult = AjaxResult.SUCCESS();
			
	     AppHelper.writeOut(ajaxResult, response);
	}
	
	//用户注册审批提交
	public String verifyUser() throws Exception {
		try {
			String passTag = request.getParameter("passTag");
			String allotTag = request.getParameter("allotTag");
			Long workPlanId = planId == null?null:Long.valueOf(planId);
			GwUserVO userVO = getLoginUser();
			//修改待办状态
			workPlanService.updateWorkPlanState(workPlanId, suggestion,passTag,userVO.getUserId());
			Long nextPlanId = null;
			if(userVO != null) {
				GwUserVO operUserVO = userService.searchUserDetail(userId);
				String loginName = operUserVO != null?operUserVO.getLoginName():"none";
				//更新审核结果
				gwUserService.updateConfrimStatus(userId, passTag, userVO.getLoginName());
				//审核部通过生产退回待办
				String userType = GwUserType.DATA_USER.equals(operUserVO.getUserType())?"数据":GwUserType.ORG_USER.equals(operUserVO.getUserType())?"机构":"";
				if(CommonState.NO_PASS.equals(passTag)){
					String planContentStr = "您提交的注册"+userType+"用户："+loginName+"审核不通过，请及时修改重新注册提交。";
					//待办关联表主键
					String extenTableKey = userId.toString()+","+planId;
					String msgContent="您提交"+userType+"："+loginName+"注册审核不通过，请及时修改重新注册提交。【数据网关平台】";
					workPlanService.saveWorkPlan("用户注册退回",WorkPlanConstent.REGISTE_BACK, planContentStr, WorkPlanConstent.WAIT_FOR_DEAL, null, extenTableKey, userVO.getUserId(),userId,workPlanId,msgContent,null);
				}else if(CommonState.PASS.equals(passTag)){
					GwWorkPlanVO nextPlanVO = workPlanService.verifyRegisteUserPass(userVO.getUserId(),workPlanId, operUserVO);
					nextPlanId = nextPlanVO.getPlanId();
				}
				//写操作日志
				operationLogService.saveOperationLog(userVO.getLoginName(), loginName, OperateTypeConstent.USER_REGIST_AUDIT, "用户注册审核,审核结果："+SysDictManage.getSysDict("DICT_AUDIT_STATE", passTag).getDictValue());
				if("1".equals(allotTag)){
					//用户权限分配
					return userAuthPage(userId,nextPlanId);
				}else{
					searchPlanNolog();
				}
			}
			return "workList";
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(用户注册审核),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "用户注册审核时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "用户注册审核");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			log.error("用户注册审核出错:"+e.getMessage(),e);
			e.printStackTrace();
			throw e;
		}
	}
	
	private void searchPlanNolog() throws Exception{
		GwUserVO userVO = getLoginUser();
		if(userVO != null){
			String userType = userVO.getUserType();
			if(pageObject == null) {
				pageObject = new PageObject();
			}			
			if(GwUserType.AUDIT_USER.equals(userType) ||GwUserType.SAFE_USER.equals(userType)){
				String planTypes = WorkPlanConstent.getPlanTypes(userType);
				pageObject = workPlanService.searchByParam(planTypes,planType, planTitle, planContent,pageObject,CommonState.WAIT_AUDIT);
			}else {
				pageObject = workPlanService.searchByParam(userVO.getUserId(),planType, planTitle, planContent,pageObject,CommonState.WAIT_AUDIT);
			}
		}
	}
	
	 //用户权限分配页面数据
	private String userAuthPage(Long userId, Long planId)throws Exception{
		if(planId != null)
	    request.setAttribute("planId", planId.toString());
	    GwUserVO user=(GwUserVO)userService.searchUserDetail(userId);
		List<RoleView> roleList=userAuthService.searchRoleListByUserId(user.getUserId());
		List<GwServiceVO> unchooseServiceList=userAuthService.searchUnchooseServiceList(user.getUserId());
		List<GwServiceVO> chooseServiceList=userAuthService.searchChooseServiceList(user.getUserId());
		
		if(user.getOrgId()!=null){
			GwOrgVO orgVO=orgService.searchOrg(user.getOrgId());
			GwUserVO orgUser=orgService.searchOrgUser(orgVO.getOrgId());
			List<GwServiceVO> serviceList=userAuthService.searchChooseServiceList(orgUser.getUserId());
			request.setAttribute("orgVO", orgVO);
			request.setAttribute("serviceList", serviceList);
		}
		Map<String, List> serviceMap=roleAuthService.searchServiceListByAllRole();
		List<UserView> orgUserList=userService.searchOrgUserList();
		List<UserView> dataUserList=userService.searchDataUserList(user.getOrgId());
		
		//source为了区分是从用户列表进到分配权限页面，还是从待办列表进到分配权限页面：2表示从待办列表进到分配权限页面
		String source=request.getParameter("source");
		request.setAttribute("source", source);
		request.setAttribute("user", user);
		request.setAttribute("roleList", roleList);
		request.setAttribute("unchooseServiceList", unchooseServiceList);
		request.setAttribute("chooseServiceList", chooseServiceList);
		request.setAttribute("serviceMap", JSONObject.fromObject(serviceMap));
		request.setAttribute("orgUserList", orgUserList);
		request.setAttribute("dataUserList", dataUserList);
		if("dataUser".equals(user.getUserType())){
			return "dataUserAuthPage";
		}else{
			return "userAuthPage";
		}
   }
	
	//用户注销审核
	public String searchCancelUserVerify() throws Exception{
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		GwUserVO createUser=userService.searchUserDetail(workPlan.getCreateUserId());
		GwUserVO userVO = userService.searchUserDetail(userId);		//待注销用户
		GwOrgVO orgVO = orgService.searchOrg(userVO.getOrgId());	//用户所属机构
		if(GwUserType.ORG_USER.equals(userVO.getUserType())){	//机构用户下的所有数据用户
			List dataUserList = userService.searchDataUserListByOrg(userVO.getOrgId());
			request.setAttribute("dataUserList", dataUserList);
		}
		request.setAttribute("orgVO", orgVO);
		request.setAttribute("userVO", userVO);
		request.setAttribute("createUser", createUser);
		return "cancelUserVerify";
	}

	//用户注销
	public void cancelUserVerify() throws Exception{
		AjaxResult ajaxResult = null;
		GwUserVO loginUser = getLoginUser();
		GwUserVO cancelUser = null;
		try {
			cancelUser = userService.searchUserDetail(userId);
			if(cancelUser == null){
				ajaxResult = AjaxResult.ERROR(null, "注销用户userId="+userId+"失败，用户不存在。");
			}else {
				if(WorkPlanConstent.DEAL_NOPASS.equals(workPlan.getPlanState())){
					workPlanService.cancelUserRollback(loginUser, cancelUser, workPlan);
					message = cancelUser.getLoginName()+"用户注销申请被退回";
				}else {
					int cancelCount = workPlanService.cancelUserAudit(loginUser, cancelUser, workPlan);
					message = cancelUser.getLoginName()+"用户注销成功。";
					if(GwUserType.ORG_USER.equals(cancelUser.getUserType())){
						message = cancelUser.getLoginName()+"是机构用户，和机构下的数据用户一共注销了"+cancelCount+"个用户。";
					}
				}
				ajaxResult = AjaxResult.SUCCESS(null,message);
			}
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(用户注销),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "用户注销时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "用户注销");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null,"系统异常，用户注销审核失败。原因："+e.getMessage());
		}finally{
			try {
				operationLogService.saveOperationLog(loginUser.getLoginName(), cancelUser.getLoginName(), OperateTypeConstent.AUDIT_USER, ajaxResult.getMessage());
			} catch (Exception e) {
				String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(用户注销),原因:"+e.getMessage()+"【数据服务网关】";
				GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "用户注销时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
				Map map=new HashMap();
				map.put("userId",String.valueOf(getLoginUser().getUserId()));
				map.put("operFun", "用户注销");
				workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
				e.printStackTrace();
			}
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	//跳转到用户修改审核页面
	public String searchUpdateUserVerify() throws Exception{
		try {
			workPlan = workPlanService.searchById(Long.parseLong(planId));
			GwUserVO createUser=userService.searchUserDetail(workPlan.getCreateUserId());
			Map<String, String> ParamMap = workPlanParamService.searchParamMap(Long.parseLong(planId));
			String batchId = ParamMap.get("batchId");
			List<GwModifyRecordVO>  gwModifyRecordVOList= workPlanService.searchUpdateUserVerify(Integer.valueOf(batchId));
			for(GwModifyRecordVO gwModifyRecordVO: gwModifyRecordVOList){
				if("user_fileid".equals(gwModifyRecordVO.getColumsCode())){
					GwUploadFileVO beforeFile =uploadFileService.findById(Long.parseLong(gwModifyRecordVO.getBeforeValue()));
					GwUploadFileVO afterFile =uploadFileService.findById(Long.parseLong(gwModifyRecordVO.getAfterValue()));
					request.setAttribute("beforeFile", beforeFile);
					request.setAttribute("afterFile", afterFile);
				}
			}
			request.setAttribute("gwModifyRecordVOList", gwModifyRecordVOList);
			request.setAttribute("modifyRecordVO", modifyRecord);
			request.setAttribute("workPlanVO", workPlan);
			request.setAttribute("createUser", createUser);
			return "updateUserVerify";
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(用户修改审核),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "跳转到用户修改审核页面时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "用户修改审核");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			throw e;
		}
	}
	public String verifyUpdateUser()throws Exception{
		Map<String, String> ParamMap = workPlanParamService.searchParamMap(Long.parseLong(planId));
		String userId =ParamMap.get("userId");
		String passTag = request.getParameter("passTag");
		GwUserVO loginUser = getLoginUser();
		user =  userService.searchUserDetail(Long.valueOf(userId));
		GwOrgVO orgVO=orgService.searchOrg(user.getOrgId());
		workPlanService.verifyUpdateUser(user,orgVO,modifyRecord,loginUser,passTag,planId,suggestion);		
		return searchByParam();
	}
	
	/**
	 * 查询用户注销回退信息
	 * @return
	 * @throws Exception 
	 */
	public String searchUserCancelBack() throws Exception{
		String prePlanId = request.getParameter("prePlanId");	//注销审核退回的上一个任务
		String planId = request.getParameter("planId");
		GwWorkPlanVO preWorkPlanVO = workPlanService.searchById(Long.parseLong(prePlanId));
		GwUserVO createUser=userService.searchUserDetail(preWorkPlanVO.getCreateUserId());
		Long userId = Long.parseLong(preWorkPlanVO.getExtenTableKey());	//上一个注销申请任务，extenTableKey=待注销userId
		
		GwUserVO userVO = userService.searchUserDetail(userId);		//待注销用户
		GwOrgVO orgVO = orgService.searchOrg(userVO.getOrgId());	//用户所属机构
		if(GwUserType.ORG_USER.equals(userVO.getUserType())){	//机构用户下的所有数据用户
			List dataUserList = userService.searchDataUserListByOrg(userVO.getOrgId());
			request.setAttribute("dataUserList", dataUserList);
		}
		request.setAttribute("orgVO", orgVO);
		request.setAttribute("userVO", userVO);
		request.setAttribute("preWorkPlanVO", preWorkPlanVO);
		request.setAttribute("createUser", createUser);
		request.setAttribute("planId", planId);
		
		return "cancelUserBack";
	}
	
	//结束用户注销退回待办
	public String endUserCancelBack() throws Exception{
		String planId = request.getParameter("planId");	//注销审核退回的上一个任务
		workPlanService.updateWorkPlanState(Long.parseLong(planId), null, WorkPlanConstent.DEAL_PASS, null);	
		return searchByParam();
	}
	//查询用户审核列表
	public String findUserAuditByParam()throws Exception{
		try{
			if(userVO==null) userVO=new UserAuditView();
			if(pageObject==null) pageObject=new PageObject();

			String planTypes =userButtonService.searchPlanTypeByUserType(getLoginUser().getUserType());
			if(StringUtils.isNotBlank(planTypes))
				pageObject=workPlanService.findUserAuditByParam(userVO, pageObject,planTypes);
		}catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询用户审核列表),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查询用户审核列表时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询用户审核列表");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			throw e;
		}
		return "findUserAuditByParam";
		
	}
	
	//跳转用户修改退回页面
	public String searchUserBack() throws Exception{
		String planId = request.getParameter("planId");
		String oldPlanId = request.getParameter("oldPlanId");
		String batchId = request.getParameter("batchId");
		GwWorkPlanVO workPlan = workPlanService.searchById(Long.valueOf(oldPlanId));
		GwUserVO createUser=userService.searchUserDetail(workPlan.getCreateUserId());
		List<GwModifyRecordVO>  gwModifyRecordVOList= workPlanService.searchUpdateUserVerify(Integer.valueOf(batchId));
		for(GwModifyRecordVO gwModifyRecordVO: gwModifyRecordVOList){
			if("user_fileid".equals(gwModifyRecordVO.getColumsCode())){
				GwUploadFileVO beforeFile =uploadFileService.findById(Long.parseLong(gwModifyRecordVO.getBeforeValue()));
				GwUploadFileVO afterFile =uploadFileService.findById(Long.parseLong(gwModifyRecordVO.getAfterValue()));
				request.setAttribute("beforeFile", beforeFile);
				request.setAttribute("afterFile", afterFile);
			}
		}
		request.setAttribute("gwModifyRecordVOList", gwModifyRecordVOList);
		request.setAttribute("modifyRecordVO", modifyRecord);
		request.setAttribute("workPlanVO", workPlan);
		request.setAttribute("createUser", createUser);
		return "updateUserBack";
	}
	
	//结束用户修改退回待办
	public String endUserBack() throws Exception{
		String planId = request.getParameter("planId");
		GwWorkPlanVO workPlan = workPlanService.searchById(Long.valueOf(planId));
		workPlanService.updateWorkPlanState(Long.parseLong(planId), null, CommonState.PASS, getLoginUser().getUserId());
		return searchByParam();
	}

	//服务信息申请查看审核页面（包括数据安全管理员审核页面，审核管理员审核页面）
	public String searchServiceInfoVerify() throws Exception{
		try {
			workPlan = workPlanService.searchById(Long.parseLong(planId));
			GwUserVO createUser=userService.searchUserDetail(workPlan.getCreateUserId());
			Map<String, String> ParamMap = workPlanParamService.searchParamMap(Long.parseLong(planId));
			String planType=ParamMap.get("planType");
			String serviceId = ParamMap.get("serviceId");
			List<GwServiceFieldVO> outputList = modelInfoService.searchServicefieldOutput(Long.parseLong(serviceId));
			GwServiceView serviceView = modelInfoService.findServiceModelById(Long.parseLong(serviceId));
			List<GwServiceFieldVO> inputList= modelInfoService.searchServicefieldInput(Long.parseLong(serviceId));
			request.setAttribute("inputList", inputList);
			request.setAttribute("serviceView", serviceView);
			request.setAttribute("outputList", outputList);
			request.setAttribute("workPlan", workPlan);
			request.setAttribute("createUser", createUser);
			return "serviceInfoAppVerify";
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(服务信息申请审核),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看服务信息申请审核页面时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "服务信息申请审核");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			throw e;
		}
	}
	
	//服务信息申请审核（包括数据安全管理员审核，审核管理员审核）
	public String verifyServiceInfoApp()throws Exception{
		Map<String, String> ParamMap = workPlanParamService.searchParamMap(Long.parseLong(workPlan.getPlanId().toString()));
		String planType =ParamMap.get("planType");
		String userId =ParamMap.get("userId");
		String serviceId =ParamMap.get("serviceId");
		String passTag = request.getParameter("passTag");
		GwUserVO loginUser = getLoginUser();
		GwDesenServiceInfoVO desenModelInfo = desenModelService.searchDesenServiceInfo(Long.parseLong(userId), Long.parseLong(serviceId));
//		if(GwUserType.SAFE_USER.equals(loginUser.getUserType())){
//			workPlanService.verifyServiceInfoApp(planType,desenModelInfo,workPlan,suggestion,loginUser,passTag,userId,serviceId);
//		}
		if(GwUserType.AUDIT_USER.equals(loginUser.getUserType())){
			workPlanService.verifyServiceInfoAuditApp(planType,desenModelInfo,workPlan,suggestion,loginUser,passTag,userId,serviceId);
		}
		return searchByParam();
	}
	
	//跳回到服务信息申请查看回退页面
	public String searchServiceInfoBack()throws Exception{
		GwUserVO loginUser=getLoginUser();
		String planId = request.getParameter("planId");
		GwWorkPlanVO workPlanVO = workPlanService.searchById(Long.parseLong(planId));
		GwUserVO createUser=userService.searchUserDetail(workPlanVO.getCreateUserId());
		Map<String, String> ParamMap = workPlanParamService.searchParamMap(Long.parseLong(planId));
		String userId =ParamMap.get("userId");
		String serviceId =ParamMap.get("serviceId");
		String planType=ParamMap.get("planType");
		GwWorkPlanVO preWorkPlanVO = workPlanService.searchById(workPlanVO.getParentPlanId());
		List<GwServiceFieldVO> outputList = modelInfoService.searchServicefieldOutput(Long.parseLong(serviceId));
		GwDesenServiceInfoVO desenModelInfo = desenModelService.searchDesenServiceInfo(Long.parseLong(userId), Long.parseLong(serviceId));
		GwServiceView serviceView = modelInfoService.findServiceModelById(Long.parseLong(serviceId));
		if("viewService".equals(planType)){
			List<GwServiceFieldVO> inputList= modelInfoService.searchServicefieldInput(Long.parseLong(serviceId));
			request.setAttribute("inputList", inputList);
		}
		request.setAttribute("outputList", outputList);
		request.setAttribute("serviceView", serviceView);
		request.setAttribute("preWorkPlanVO", preWorkPlanVO);
		request.setAttribute("workPlan", workPlanVO);
		request.setAttribute("createUser", createUser);
		request.setAttribute("desenModelInfo", desenModelInfo);
		request.setAttribute("user", loginUser);
		return "serviceInfoBack";
	}
	
	//结束服务信息申请查看回退待办
	public String endServiceInfoBack()throws Exception{
		String planId=request.getParameter("planId");
		workPlanService.updateWorkPlanState(Long.parseLong(planId), null, WorkPlanConstent.DEAL_PASS, getLoginUser().getUserId());
		return searchByParam();
	}
	//跳回到取数申请回退页面
	public String searchDataAppBack()throws Exception{
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		GwUserVO createUser=userService.searchUserDetail(workPlan.getCreateUserId());
		GwWorkPlanVO preWorkPlan=workPlanService.searchById(workPlan.getParentPlanId());
		request.setAttribute("workPlan", workPlan);
		request.setAttribute("suggestion", preWorkPlan.getSuggestion());
		request.setAttribute("createUser", createUser);
		return "dataAppBack";
	}
	
	//跳回到取数申请回退页面
	public String searchFetchApplySuccess()throws Exception{
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		GwUserVO createUser=userService.searchUserDetail(workPlan.getCreateUserId());
		//GwWorkPlanVO preWorkPlan=workPlanService.searchById(workPlan.getParentPlanId());
		request.setAttribute("workPlan", workPlan);
		//request.setAttribute("suggestion", preWorkPlan.getSuggestion());
		request.setAttribute("createUser", createUser);
		return "fetchApplySuccess";
	}
	//结束取数申请成功待办
	public String  enSureFetchApplySuccess()throws Exception{
		String planId=request.getParameter("planId");
		workPlanService.updateWorkPlanState(Long.parseLong(planId),null, WorkPlanConstent.DEAL_PASS, getLoginUser().getUserId());
		return searchByParam();
	}
	//结束取数申请回退待办
	public String endDataAppBack()throws Exception{
		String planId=request.getParameter("planId");
		workPlanService.updateWorkPlanState(Long.parseLong(planId),null, WorkPlanConstent.DEAL_PASS, getLoginUser().getUserId());
		return searchByParam();
	}
	
	//跳转到合规检查不通过后的审核待办页面
	public String searchRuleCheckAudit()throws Exception{
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		GwUserVO createUser=userService.searchUserDetail(workPlan.getCreateUserId());
		Map<String, String> ParamMap = workPlanParamService.searchParamMap(Long.parseLong(planId));
		String taskId =ParamMap.get("taskId");
		String serviceId=ParamMap.get("serviceId");
		String userId=ParamMap.get("userId");
		GwServiceVO service=gwServiceService.searchService(Long.valueOf(serviceId));
		GwUserVO dataUserVO=  gwUserService.searchById(Long.valueOf(userId));
		GwUserVO orgUserVO=orgService.searchOrgUser(dataUserVO.getOrgId());
		request.setAttribute("dataUserVO", dataUserVO);
		request.setAttribute("orgUserVO", orgUserVO);
		request.setAttribute("service", service);
		GwModelDataFileVO fileVO=modelInfoService.searchModelDataFile(Long.valueOf(taskId), "1");
		request.setAttribute("fileVO", fileVO);
		GwUserVO loginUser=getLoginUser();
		if(workPlan.getParentPlanId()!=null){
			List<GwWorkPlanView> hisWorkList = workPlanService.searchHistoryWorkPlan(workPlan.getParentPlanId());
			request.setAttribute("preWorkPlan", hisWorkList);
		}
		request.setAttribute("loginUser", loginUser);
		GwModelDataFetchTaskVO taskVO = modelDataTaskService.searchById(Long.parseLong(taskId));
		request.setAttribute("taskVO", taskVO);
		
		if("3".equals(taskVO.getCheckResult())){
			//根据check_batch，查询行数
			int rowCount=dataGatherService.searchRowCount(taskVO);
			request.setAttribute("rowCount", rowCount);
			//查询warnRow,rowData
			Map map=dataGatherService.searchServiceCheckWarn(taskVO, 1);
			String[] rowData=((String)map.get("rowData")).split("\\|");
			request.setAttribute("warnRow", map.get("warnRow"));
			request.setAttribute("rowData", rowData);
			request.setAttribute("rowDataLength", rowData.length);
		}else if("4".equals(taskVO.getCheckResult())){
			//根据check_batch，查询行数
			int rowCount=dataGatherService.searchRowCount(taskVO);
			request.setAttribute("rowCount", rowCount);
			//查询warnRow,rowData
			Map map=dataGatherService.searchServiceCheckWarn(taskVO, 2);
			request.setAttribute("warnRow", map.get("warnRow"));
			request.setAttribute("rowData", map.get("rowData"));
			String[] rowData=((String)map.get("rowData")).split("\\|");
			request.setAttribute("rowDataLength", rowData.length);
		}
		
		//查询服务输出字段
//		List<GwServiceFieldVO> fieldCodeList=gwServiceFieldDAO.searchServiceOutField(Long.parseLong(serviceId));
//		//将服务输出字段组装成map
//		Map<String,String> fieldCodeMap=new LinkedHashMap<String,String>();
//		for(GwServiceFieldVO gwServiceFieldVO:fieldCodeList){
//			fieldCodeMap.put(gwServiceFieldVO.getFieldCode(),gwServiceFieldVO.getFieldName());
//		}
		
		if(pageObject==null)pageObject=new PageObject();
		pageObject=dataGatherService.searchRuleCheckAuditList(service,taskVO.getTaskId(), pageObject);
		
		//组装表头
		List<GwServiceCheckRuleVO> serviceCodeList=dataGatherService.searchServiceFieldCode(taskVO);
		request.setAttribute("serviceCodeList", serviceCodeList);
		
		//根据taskId,行号查询需要标红的字段
		Map recordMap=dataGatherService.searchRuleCheckAuditField(taskVO,pageObject);
		request.setAttribute("recordMap", recordMap);
		request.setAttribute("workPlan", workPlan);
		request.setAttribute("createUser", createUser);
		String loginUserType=getLoginUser().getUserType();
		request.setAttribute("loginUserType", loginUserType);
		return "ruleCheckAudit";
	}
	
	//合规检查不通过后的待办审核
	public void verifyRuleCheckAudit()throws Exception{
		AjaxResult ajaxResult = null;
		try {
			Map<String, String> ParamMap = workPlanParamService.searchParamMap(Long.parseLong(planId));
			String taskId =ParamMap.get("taskId");
			String userId=ParamMap.get("userId");
			String serviceId=ParamMap.get("serviceId");
			String passTag = request.getParameter("passTag");
			GwUserVO loginUser = getLoginUser();
			GwUserVO loginUserVO=getLoginUser();
			if(userButtonService.searchAuthorization(WorkPlanConstent.RULE_CHECK_AUDIT_1, loginUser.getUserType())){
				workPlanService.verifyRuleCheckAuditForSafeUser(passTag, planId, suggestion, taskId, userId,serviceId,loginUser);
				ajaxResult = AjaxResult.SUCCESS(null,"审核成功!");
			}
			
			if(userButtonService.searchAuthorization(WorkPlanConstent.RULE_CHECK_AUDIT_2, loginUser.getUserType())){
				GwModelDataFetchTaskVO gModelDataFetchTaskVO=workPlanService.verifyRuleCheckAuditForAuditUser(passTag, planId, suggestion, taskId,userId,serviceId,loginUser);
				/*if(DataProgressStatus.RULE_CHECK_PUSHED.equals(gModelDataFetchTaskVO.getDataProgressStatus())){
					ajaxResult = AjaxResult.SUCCESS(null,"文件推送成功!");
				}else if(DataProgressStatus.RULE_CHECK_REPUSH.equals(gModelDataFetchTaskVO.getDataProgressStatus())){
					ajaxResult = AjaxResult.SUCCESS(null,"文件推送失败，系统稍后将重试，成功后以待办任务方式再次通知!");
				}else{
					ajaxResult = AjaxResult.SUCCESS();
				}*/
				ajaxResult = AjaxResult.SUCCESS(null,"文件推送中，推送结果会以待办方式通知你!");
			}
		}catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(合规检查),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看合规检查待办时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "合规检查");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null,"审核发生错误！原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	//合规检查不通过后返回给数据用户的待办
	public String searchRuleCheckAuditBack()throws Exception{
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		GwWorkPlanVO preWorkPlan=workPlanService.searchById(workPlan.getParentPlanId());
		GwUserVO createUser = userService.searchUserDetail(workPlan.getCreateUserId());
		request.setAttribute("workPlan", workPlan);
		request.setAttribute("suggestion", preWorkPlan.getSuggestion());
		request.setAttribute("createUser", createUser);
		return "ruleCheckAuditBack";
	}
	
	//结束合规检查不通过后返回给数据用户的待办
	public String endRuleCheckAuditBack()throws Exception{
		String planId=request.getParameter("planId");
		GwUserVO loginUser=getLoginUser();
		workPlanService.updateWorkPlanState(Long.parseLong(planId),null, WorkPlanConstent.DEAL_PASS, loginUser.getUserId());
		return searchByParam();
	}
	
	//合规数据重新推送成功通知
	public String ruleCheckResend()throws Exception{
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		GwUserVO createUser = userService.searchUserDetail(workPlan.getCreateUserId());
		request.setAttribute("workPlan", workPlan);
		request.setAttribute("createUser", createUser);
		return "ruleCheckResend";
	}
	
	//**********跳转安全管理员创建服务并取数审批页面*************
	public String serviceFetchAudit()throws Exception{
		Long serviceId = Long.parseLong(request.getParameter("serviceId"));
		Long workPlanId = Long.parseLong(request.getParameter("workPlanId"));
		String ruleBatch = null;
		String fetchId = null;
		if(workPlanId!=null){
			GwWorkPlanVO workPlanVO = workPlanService.searchById(workPlanId);
			Map<String, String> paramMap = workPlanParamService.searchParamMap(workPlanId);
			ruleBatch = paramMap.get("ruleBatch");
			fetchId = paramMap.get("fetchId");
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
		return "serviceFetchAudit";
	}
	//*************创建并申请审批处理(数据安全管理员)****************
	public String serviceFetchApply() throws Exception{
		AjaxResult ajaxResult = null;
		String passTag = request.getParameter("passTag");
		String workPlanId = request.getParameter("planId");
		Long serviceId = Long.valueOf(request.getParameter("serviceId"));
		String fetchId = request.getParameter("fetchId");
		String maxCheckNum = request.getParameter("maxCheckNum");
		String outputNum = request.getParameter("outputNum");
		String checkAudit = request.getParameter("checkAudit");
		Long userId = Long.valueOf(request.getParameter("userId"));
		GwUserVO userVO = userService.searchUserDetail(userId);
		GwUserVO loginUser = getLoginUser();
		GwModelDataFetchVO fetchVO = modelDataAppService.searchById(Long.valueOf(fetchId));
		if(maxCheckNum != "" && maxCheckNum != null){
			fetchVO.setMaxCheckNum(Long.valueOf(maxCheckNum));
		}
		if(outputNum != "" && outputNum != null){
			fetchVO.setOutputNum(Long.valueOf(outputNum));
		}
		if(checkAudit != "" && checkAudit != null){
			fetchVO.setCheckAudit(checkAudit);
		}
		Integer preBatch = null;
		String ruleBatch = null;
		Map<String, String> paramMap = workPlanParamService.searchParamMap(Long.valueOf(workPlanId));
		ruleBatch = String.valueOf(paramMap.get("ruleBatch"));//数据用户上传的规则表ID		
		GwServiceVO serviceVO = serviceService.search(GwServiceVO.class,serviceId);
		GwWorkPlanVO workPlanVO = null;
		workPlanService.updateWorkPlanState(Long.valueOf(workPlanId),suggestion, passTag,loginUser.getUserId());

		try{
			
			//--start--获取当前待办的流程ID	
			String processId = workPlanService.searchById(Long.valueOf(workPlanId)).getProcessId();
			//更新流程状态为第一次审核的执行中
			processService.updateProcessStatus(Long.valueOf(processId),DataTypeConstent.FIRSTAUDIT,DataTypeConstent.EXCUTING,null,null);
			
			if(CommonState.PASS.equals(passTag)){
				//通过审核的流程处理
				GwProcessOperationVO gwVO = processOperationService.searchProcessOperationByStep(Long.valueOf(processId), DataTypeConstent.S_SECOND);
				if(gwVO==null){
					gwVO=processOperationService.searchProcessOperationByStep(Long.valueOf(processId), DataTypeConstent.X_SECOND);
					gwVO.setOperateContent("已完成一次审核");
					gwVO.setOperateTime(new Date());
					gwVO.setProgressStatus(DataTypeConstent.COMPLETED);
					gwVO.setDealType(GwUserType.SAFE_USER);
					gwVO.setPlanId(Long.valueOf(workPlanId));
					processOperationService.update(gwVO);
					//新建第三步（审核人员）待处理的步骤
					processOperationService.saveProcessOperation(Long.valueOf(processId), null, null, null, null,null,GwUserType.AUDIT_USER,DataTypeConstent.X_THIRD);
				}else{
					gwVO.setOperateContent("已完成一次审核");
					gwVO.setOperateTime(new Date());
					gwVO.setProgressStatus(DataTypeConstent.COMPLETED);
					gwVO.setDealType(GwUserType.SAFE_USER);
					gwVO.setPlanId(Long.valueOf(workPlanId));
					processOperationService.update(gwVO);
					//新建第三步（审核人员）待处理的步骤
					processOperationService.saveProcessOperation(Long.valueOf(processId), null, null, null, null,null,GwUserType.AUDIT_USER,DataTypeConstent.S_THIRD);
				}
				//更新流程状态为完成了第一次审核
				processService.updateProcessStatus(Long.valueOf(processId),DataTypeConstent.FIRSTAUDIT,DataTypeConstent.COMPLETED,null,null);				
				//--end--
				
				modelDataAppService.update(fetchVO);
				String planContent = GwUserType.getUserTypeName(loginUser.getUserType())+"："+loginUser.getLoginName()+"配置了用户："+userVO.getLoginName()+"的服务(服务编码="+serviceVO.getServiceCode()+", 服务名称="+serviceVO.getServiceName()+")的字段脱敏规则配置，请审核";	
				String msgContent=SysDictManage.getSysDict("DICT_USER_TYPE", loginUser.getUserType()).getDictValue()+"配置了用户："+userVO.getLoginName()+"的服务("+serviceVO.getServiceName()+")的字段脱敏规则配置，请审核！【数据网关平台】";
				workPlanVO = workPlanService.saveWorkPlan("创建服务并申请审核", WorkPlanConstent.SERVICE_FETCH_AUDIT2, planContent, WorkPlanConstent.WAIT_FOR_DEAL, null, null, loginUser.getUserId(), 
						null,workPlanVO!=null?workPlanVO.getPlanId():null,msgContent,processId);
				paramMap.put("userId",String.valueOf(userId));
				paramMap.put("serviceId",String.valueOf(serviceVO.getServiceId()));
				paramMap.put("fetchId", fetchId);
				paramMap.put("preBatch",String.valueOf(preBatch));
				paramMap.put("ruleBatch", ruleBatch);
				workPlanParamService.saveParamMap(workPlanVO.getPlanId(), paramMap);
			    ajaxResult = AjaxResult.SUCCESS("pass");				
			}
			if(CommonState.NO_PASS.equals(passTag)){
				
				//--start--审核不通过，结束流程
				processService.updateProcess(Long.valueOf(processId), DataTypeConstent.COMPLETED, new Date());
				//--end--
				//更新当前待办状态
				workPlanService.updateWorkPlanState(Long.valueOf(workPlanId), suggestion, "0",getLoginUser().getUserId() );
				fetchVO.setAuditStatus("0");
				fetchVO.setAuditTime(new Date());
				fetchVO.setUpdateTime(new Date());
				fetchVO.setUpdateUser(getLoginUser().getLoginName());
				modelDataAppService.update(fetchVO);
				//返回一条待办给数据用户
				String planContentStr = "您申请的服务取数,服务编码="+serviceVO.getServiceCode()+"操作不通过，请重新申请。";
				String msgContent="您申请的服务取数,服务编码="+serviceVO.getServiceCode()+"审核不通过,请知晓。【数据网关平台】";
				workPlanService.saveWorkPlan("服务数据脱敏规则退回",WorkPlanConstent.GET_DATA_BACK, planContentStr, WorkPlanConstent.WAIT_FOR_DEAL, null, null, getLoginUser().getUserId(),userId,Long.valueOf(workPlanId),msgContent,null);
				ajaxResult = AjaxResult.SUCCESS("notPass");				
			}
		}catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(字段脱敏规则配置),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "修改字段脱敏规则配置时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "字段脱敏规则配置");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null, "字段脱敏规则配置失败。原因："+e.getMessage());
		}
		
		return "workList";
	}
	public String preRuleInfo()throws Exception{
			String isModify = request.getParameter("isModify");
			if(isModify != null && isModify != ""){
				request.setAttribute("isModify", isModify);
			}
			String fileId = request.getParameter("fileId");
			GwUploadFileVO FileVo=null;
			FileVo = uploadFileService.findById(Long.valueOf(fileId));
			request.setAttribute("FileVo", FileVo);
			String fileVoPathStr[] = FileVo.getFilePath().split("/");
			imageFileName = fileVoPathStr[fileVoPathStr.length-1];
			String filePath = ConfigConstants.BASE_UPLOAD_FILE_PATH+"/uploadFile/"+"desenManage"+"/";
			File upload =new File(filePath + File.separator + imageFileName);
			List<GwServiceCheckRuleVO> checkRuleList = desenModelService.parseServiceCheckRule(upload, null, null);
			Map<String, GwSysDictVO> checkDictMap = SysDictManage.getSysDict("DICT_CHECK_RULE_TYPE");
			request.setAttribute("checkRuleList", checkRuleList);
			request.setAttribute("checkDictMap",checkDictMap);
			return "ruleInfo";
	}
	//**********规则表查看并修改*************
	public String ruleInfo()throws Exception{
		String serviceId = request.getParameter("serviceId");
		String workPlanId = request.getParameter("workPlanId");
		String isModify = request.getParameter("isModify");
		String  ruleBatch = request.getParameter("ruleBatch");
		if(isModify != null && isModify != ""){
			request.setAttribute("isModify", isModify);
		}
		String fileId = null;
		GwUploadFileVO FileVo=null;
		if(workPlanId!=""&&workPlanId!=null){
			GwWorkPlanVO workPlanVO = workPlanService.searchById(Long.valueOf(workPlanId));
			Map<String, String> paramMap = workPlanParamService.searchParamMap(Long.valueOf(workPlanId));
			ruleBatch = paramMap.get("ruleBatch");
			request.setAttribute("ruleBatch", ruleBatch);
			fileId = paramMap.get("fileId");
			String fetchId = paramMap.get("fetchId");
			request.setAttribute("fetchId", fetchId);
			FileVo = uploadFileService.findById(Long.valueOf(fileId));
			request.setAttribute("FileVo", FileVo);
			request.setAttribute("workPlanVO", workPlanVO);
			GwUserVO createUser=userService.searchUserDetail(workPlanVO.getCreateUserId());
			request.setAttribute("createUser", createUser);
			request.setAttribute("planId", workPlanId);
		}
			GwServiceVO serviceVO = serviceService.searchService(Long.valueOf(serviceId));
			request.setAttribute("serviceVO", serviceVO);
			if(serviceVO.getModelId() != null){
				GwModelVO model = modelInfoService.searchModelById(serviceVO.getModelId());
				request.setAttribute("model", model);
			}
		
		if(ruleBatch != ""&&ruleBatch!=null){//数据用户上载的规则表
			List<GwServiceCheckRuleAuditVO> checkRuleAuditList = checkRuleAuditDAO.searchCheckRuleList(Long.valueOf(ruleBatch));
			request.setAttribute("checkRuleList", checkRuleAuditList);
			request.setAttribute("ruleBatch", ruleBatch);
		}
		return "ruleInfo";
	}
	//合规数据重新推送成功通知
	public String ruleCheckResendFailure() throws Exception{
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		GwUserVO createUser = userService.searchUserDetail(workPlan.getCreateUserId());
		request.setAttribute("workPlan", workPlan);
		request.setAttribute("createUser", createUser);
		return "ruleCheckResendFailure";
	}
	
	//结束合规数据重新推送成功通知
	public String endRuleCheckResend()throws Exception{
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		GwUserVO loginUser=getLoginUser();
		workPlanService.updateWorkPlanState(Long.parseLong(planId),null, WorkPlanConstent.DEAL_PASS, loginUser.getUserId());
		return searchByParam();
	}
	
	public String taskRepush() throws Exception{
		workPlanService.doTaskRepush(Long.valueOf(planId),getLoginUser());
		return searchByParam();
	}
	
	 /**
	  * 
	  * @Title: toFtpUserNotice 
	  * @Description: TODO(跳转到ftp虚拟用户配置命令执行通知页面) 
	  * @param @return    设定文件 
	  * @return String    返回类型 
	  * @throws
	  */
	public String toFtpUserNotice()throws Exception {
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		GwUserVO createUser = userService.searchUserDetail(workPlan.getCreateUserId());
		request.setAttribute("workPlan", workPlan);
		request.setAttribute("createUser", createUser);
		return "ftpUserNotice";
	}
	
	/**
	  * 
	  * @Title: overFtpUserNotice 
	  * @Description: TODO(已阅ftp虚拟用户配置命令执行通知页面) 
	  * @param @return    设定文件 
	  * @return String    返回类型 
	  * @throws
	  */
	public String overFtpUserNotice()throws Exception{
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		GwUserVO loginUser=getLoginUser();
		workPlanService.updateWorkPlanState(Long.parseLong(planId),null, WorkPlanConstent.DEAL_PASS, loginUser.getUserId());
		return searchByParam();
	}
	
	//跳转到推送结果通知待办页面
	public String toPushResult()throws Exception {
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		GwUserVO createUser = userService.searchUserDetail(workPlan.getCreateUserId());
		request.setAttribute("workPlan", workPlan);
		request.setAttribute("createUser", createUser);
		return "pushResult";
	}
	
	//结束代办状态
	public String endPlanStatus()throws Exception{
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		GwUserVO loginUser=getLoginUser();
		workPlanService.updateWorkPlanState(Long.parseLong(planId),null, WorkPlanConstent.DEAL_PASS, loginUser.getUserId());
		return searchByParam();
	}
	
	//跳转到过期数据清理待办页面
	public String toDataClean()throws Exception {
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		Map<String, String> paramMap = workPlanParamService.searchParamMap(Long.valueOf(planId));
		String fileIds=paramMap.get("fileIds");
		List<ModelFileView> files=modelInfoService.searchModelDataFiles(fileIds);
		request.setAttribute("workPlan", workPlan);
		request.setAttribute("files", files);
		return "dataClean";
	}
	
	//过期数据清理
	public String cleanData()throws Exception{
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		String passTag = request.getParameter("passTag");
		dataCycleService.cleanCacheData();
		GwUserVO loginUser=getLoginUser();
		workPlanService.updateWorkPlanState(Long.parseLong(planId),null, passTag, loginUser.getUserId());
		return searchByParam();
	}
	
	//跳转到系统后台错误待办页面
	public String toSystemBackStageError()throws Exception{
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		Map<String, String> paramMap = workPlanParamService.searchParamMap(Long.valueOf(planId));
		String userId=paramMap.get("userId");
		String operFun=paramMap.get("operFun");
		GwUserVO operUser = userService.searchUserDetail(Long.valueOf(userId));
		request.setAttribute("workPlan", workPlan);
		request.setAttribute("operUser", operUser);
		request.setAttribute("operFun", operFun);
		return "sysBackError";
	}
	
	/**
	  * 
	  * @Title: newServiceTaskConfirm 
	  * @Description: TODO(创建实时取数任务确认页面) 
	  * @param @return    设定文件 
	  * @return String    返回类型 
	  * @throws
	  */
	public String newServiceTaskConfirm()throws Exception{
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		Map<String, String> paramMap = workPlanParamService.searchParamMap(Long.valueOf(planId));
		String serviceId=paramMap.get("serviceId");
		String userId=paramMap.get("userId");
		String fileName = paramMap.get("fileName");
		UserModelServiceAppVO serviceVO = new UserModelServiceAppVO();
		if(!StringUtils.isEmpty(serviceId)){
			serviceVO = modelDataAppService.searchServiceApp(Long.valueOf(userId),Long.valueOf(serviceId),"2");
			List<GwServiceCheckRuleVO> checkRuleList = desenModelService.searchLastCheckRuleList(Long.valueOf(userId), Long.valueOf(serviceId));
			request.setAttribute("checkRuleList", checkRuleList);
		}
		List<UserModelServiceAppVO> serviceList = modelDataAppService.searchUserServiceList(Long.valueOf(userId), "2");
		
		request.setAttribute("fileName", fileName);
		request.setAttribute("serviceVO", serviceVO);
		request.setAttribute("serviceList", serviceList);
		request.setAttribute("workPlan", workPlan);
		return "newTaskConfirm";
	}
	
	/**
	 * 跳转到webservice合规检查失败页面
	 * @return
	 * @throws Exception
	 */
	public String searchWebserviceRuleCheck()throws Exception{
		workPlan = workPlanService.searchById(Long.parseLong(planId));
		request.setAttribute("workPlan", workPlan);
		return "webserviceRuleCheck";
	}

	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public String getPlanTitle() {
		return planTitle;
	}

	public void setPlanTitle(String planTitle) {
		this.planTitle = planTitle;
	}

	public String getPlanContent() {
		return planContent;
	}

	public void setPlanContent(String planContent) {
		this.planContent = planContent;
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


	public String getSuggestion() {
		return suggestion;
	}


	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}


	public GwWorkPlanVO getWorkPlan() {
		return workPlan;
	}


	public void setWorkPlan(GwWorkPlanVO workPlan) {
		this.workPlan = workPlan;
	}


	public UserAuditView getUserVO() {
		return userVO;
	}


	public void setUserVO(UserAuditView userVO) {
		this.userVO = userVO;
	}


	public PageObject getPageObject() {
		return pageObject;
	}


	public void setPageObject(PageObject pageObject) {
		this.pageObject = pageObject;
	}
	
}
