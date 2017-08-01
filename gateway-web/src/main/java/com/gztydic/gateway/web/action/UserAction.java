package com.gztydic.gateway.web.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.config.SysDictManage;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.constant.SessionConstant;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.AjaxResult;
import com.gztydic.gateway.core.common.util.AppHelper;
import com.gztydic.gateway.core.common.util.Endecrypt;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.view.FuncAndButtonView;
import com.gztydic.gateway.core.view.RoleView;
import com.gztydic.gateway.core.view.UserView;
import com.gztydic.gateway.core.view.WorkPlanView;
import com.gztydic.gateway.core.vo.GwModelVO;
import com.gztydic.gateway.core.vo.GwModifyRecordVO;
import com.gztydic.gateway.core.vo.GwOrgVO;
import com.gztydic.gateway.core.vo.GwRoleVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwSysDictVO;
import com.gztydic.gateway.core.vo.GwSysFtpVo;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.GwUserService;
import com.gztydic.gateway.system.LocalFtpConfigService;
import com.gztydic.gateway.system.OperationLogService;
import com.gztydic.gateway.system.OrgService;
import com.gztydic.gateway.system.RoleAuthService;
import com.gztydic.gateway.system.RoleService;
import com.gztydic.gateway.system.UploadFileService;
import com.gztydic.gateway.system.UserAuthService;
import com.gztydic.gateway.system.UserService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

@Controller
@Scope("prototype")
public class UserAction extends BaseAction {
	
	@Resource(name="localFtpConfigServiceImpl")
	private LocalFtpConfigService localFtpConfigService;
	@Resource(name = "userServiceImpl")
	private UserService userService;
	@Resource(name = "orgServiceImpl")
	private OrgService orgService;
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	
	@Resource(name="roleServiceImpl")
	private RoleService roleService;
	@Resource(name="roleAuthServiceImpl")
	private RoleAuthService roleAuthService;
	@Resource(name="userAuthServiceImpl")
	private UserAuthService userAuthService;
	@Resource(name = "gwUserServiceImpl")
	private GwUserService gwUserService;
	
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="uploadFileServiceImpl")
	private UploadFileService uploadFileService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	private GwUserVO user;
	private GwSysFtpVo ftpVo;
	private GwOrgVO org;
	private GwRoleVO role; 
	private String message;

    private GwModelVO modelVO;
    private GwServiceVO serviceVO;
    private String chooseRoles;
    private String chooseFuncs;
    private String chooseButtons;
    private String chooseServices;
    private String chooseServiceCodes;
    private String orgUserSortList;
    private String dataUserSortList;
	public GwModelVO getModelVO() {
		return modelVO;
	}

	public void setModelVO(GwModelVO modelVO) {
		this.modelVO = modelVO;
	}

	private GwModifyRecordVO modifyRecordVO;

	public GwModifyRecordVO getModifyRecordVO() {
		return modifyRecordVO;
	}

	public void setModifyRecordVO(GwModifyRecordVO modifyRecordVO) {
		this.modifyRecordVO = modifyRecordVO;
	}


	public GwUserVO getUser() {
		return user;
	}

	public void setUser(GwUserVO user) {
		this.user = user;
	}

	public GwOrgVO getOrg() {
		return org;
	}

	public void setOrg(GwOrgVO org) {
		this.org = org;
	}
	public GwSysFtpVo getFtpVo() {
		return ftpVo;
	}

	public void setFtpVo(GwSysFtpVo ftpVo) {
		this.ftpVo = ftpVo;
	}


	/**
	 * 
	 * @throws Exception
	 */
	public void saveUserByAdmin() throws Exception {
		AjaxResult ajaxResult = null;
		message = null;
		try {
			List<GwUserVO> list = userService.findByLoginName(user.getLoginName());
			if (list != null && list.size() > 0) {
				ajaxResult = AjaxResult.FAILURE(null,
						"登录帐号" + user.getLoginName() + "已经存在!");
			} else {
				Endecrypt endecrypt = new Endecrypt();
				String enPassword = endecrypt.get3DESEncrypt(
						user.getLoginPwd(), SessionConstant.SPKEY_PASSWORD);
				user.setLoginPwd(enPassword);
				user.setCreator(getLoginUser().getLoginName());
				user.setConfirmStatus(CommonState.PASS);
				user.setCreateTime(new Date());
				user.setStatus(CommonState.VALID);
				userService.save(user);
				ajaxResult = AjaxResult.SUCCESS();
				
				//写操作日志
				GwUserVO loginUser = getLoginUser();
				operationLogService.saveOperationLog(loginUser.getLoginName(), user.getLoginName(), OperateTypeConstent.ADD_USER, loginUser.getLoginName()+"新增用户："+user.getLoginName()+"!");
			}
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(用户新增),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "用户新增发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "用户新增");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null, "新增失败");
		}
		AppHelper.writeOut(ajaxResult, response);
	}

	@SuppressWarnings("unchecked")
	public String searchUserList() throws Exception {
		GwUserVO userVO = getLoginUser();
		List<UserView> userList;
		try {
			if (pageObject == null)
				pageObject = PageObject.getInstance(request);
			
			if(!userVO.getUserType().equals(GwUserType.ORG_USER)){
				userList = userService.searchUserList(user, org, pageObject);
			}else {
				userList = userService.searchUserListByOrg(user, org, pageObject,userVO);
			}
			
			if(userList != null){
				String[] userIds = new String[userList.size()];
				for (int i = 0; i < userIds.length; i++) {
					userIds[i] = String.valueOf(userList.get(i).getUserId()); 
				}
				Map cancelMap = new HashMap();
				//查询列表中用户的注销待办任务，存在则表示申请了注销
				List<GwWorkPlanVO> workList = workPlanService.searchWorkPlan(WorkPlanConstent.CANCEL_AUDIT, WorkPlanConstent.WAIT_FOR_DEAL, userIds);
				for (GwWorkPlanVO p : workList) {//注销待审列表
					cancelMap.put(Long.parseLong(p.getExtenTableKey()), p.getPlanId());	//extenTableKey=userId,不为空则表示已申请注销待审
				}
				request.setAttribute("cancelMap", cancelMap);
				Map updateMap = new HashMap();
				//查询列表中用户的修改待办任务，存在则表示申请了修改
				List<WorkPlanView> workList1 = workPlanService.searchWorkPlanByUpdate(WorkPlanConstent.UPDATE_AUDIT, WorkPlanConstent.WAIT_FOR_DEAL, userIds);
				for (WorkPlanView p : workList1) {//修改待审列表
					updateMap.put(Long.valueOf(p.getParamValue()), p.getPlanId());	//paramvalue=userId,不为空则表示已申请修改待审
				}
				request.setAttribute("updateMap", updateMap);
			}
			request.setAttribute("userVO", userVO);
			request.setAttribute("userList", userList);
			return "searchUserList";
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查看用户列表),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看用户列表发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查看用户列表");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			throw e;
		}
	}

	public String searchSinglerUser() throws Exception {
		try {
			user = userService.searchUserDetail(user.getUserId());
			Long fileId = user.getFileId();
			if(fileId != null){
				GwUploadFileVO fileVo  = uploadFileService.findById(fileId);
				request.setAttribute("fileVo",fileVo);
			}
			if(user.getUserType() .equals(GwUserType.DATA_USER)|| user.getUserType() .equals(GwUserType.ORG_USER)){
				org = orgService.searchOrg(user.getOrgId());
			}
			request.setAttribute("loginUser", getLoginUser());
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查看用户信息),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看用户信息发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查看用户信息");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			throw e;
		}
		return "updateUser";
	}

	public String searchUserAccess() throws Exception {
		GwUserVO userVO = getLoginUser();
		//数据用户直接进入个人修改页面
		if (userVO.getUserType().equals(GwUserType.DATA_USER) ) {
			return searchUserDetail();
		}  else {
		//其他用户查询所有列表
			return searchUserList();
		}
	}
	
	public String searchUserDetail() throws Exception {
		try {
			String sourceFlag = request.getParameter("sourceFlag");	// 1 为菜单直接进入 2为查询列表进入
			Map updateMap = new HashMap();
			String[] userIds = new String[5];
			if(user==null){
				user = getLoginUser();
			}
			userIds[0]= user.getUserId().toString();
			//查询列表中用户的修改待办任务，存在则表示申请了修改
			List<WorkPlanView> workList1 = workPlanService.searchWorkPlanByUpdate(WorkPlanConstent.UPDATE_AUDIT, WorkPlanConstent.WAIT_FOR_DEAL, userIds);
			for (WorkPlanView p : workList1) {//修改待审列表
				updateMap.put(Long.valueOf(p.getParamValue()), p.getPlanId());	//paramvalue=userId,不为空则表示已申请修改待审
			}
			request.setAttribute("updateMap", updateMap);
			if(ftpVo != null){				
				ftpVo.setFtpType("2");
			}else{
				ftpVo = new GwSysFtpVo();
				ftpVo.setFtpType("2");
			}
			List<GwSysFtpVo> GwSysFtpVoList =localFtpConfigService.searchFtpDetail(ftpVo.getFtpType());
			if (GwSysFtpVoList.size()>0){
				for (GwSysFtpVo vo:GwSysFtpVoList){
					vo.setId(GwSysFtpVoList.get(0).getId());
					vo.setFtpIp(GwSysFtpVoList.get(0).getFtpIp());
					vo.setFtpPort(GwSysFtpVoList.get(0).getFtpPort());
					vo.setFtpType(GwSysFtpVoList.get(0).getFtpType());
					request.setAttribute("GwSysFtpVo", vo);
				}
				
			}
			//上传
			if(ftpVo != null){				
				ftpVo.setFtpType("1");
			}else{
				ftpVo = new GwSysFtpVo();
				ftpVo.setFtpType("1");
			}
			List<GwSysFtpVo> GwSysFtpVoList2 =localFtpConfigService.searchFtpDetail(ftpVo.getFtpType());
			if (GwSysFtpVoList2.size()>0){
				for (GwSysFtpVo vo:GwSysFtpVoList2){
					vo.setId(GwSysFtpVoList2.get(0).getId());
					vo.setFtpIp(GwSysFtpVoList2.get(0).getFtpIp());
					vo.setFtpPort(GwSysFtpVoList2.get(0).getFtpPort());
					vo.setFtpType(GwSysFtpVoList2.get(0).getFtpType());
					request.setAttribute("GwSysFtpVo2", vo);
				}
				
			}
			
			//ftpVo = localFtpConfigService.searchFtpDetail(ftpVo.getFtpType());
			if("1".equals(sourceFlag)){
				user = userService.searchUserDetail(user.getUserId());
				List<GwWorkPlanVO> workList = workPlanService.searchWorkPlan(WorkPlanConstent.CANCEL_AUDIT, WorkPlanConstent.WAIT_FOR_DEAL, new String[]{String.valueOf(user.getUserId())});
				if(workList != null && workList.size()>0){	//是否注销待审
					request.setAttribute("cancelAudit", "cancelAudit");
				}
				
			}else{
				user = userService.searchUserDetail(user.getUserId());
			}
			Long fileId = user.getFileId();
			if(fileId != null){
				GwUploadFileVO fileVo  = uploadFileService.findById(fileId);
				request.setAttribute("fileVo",fileVo);
			}
			
			if(user.getUserType().equals(GwUserType.DATA_USER)|| user.getUserType().equals(GwUserType.ORG_USER)){
				org = orgService.searchOrg(user.getOrgId());
			}
			request.setAttribute("sourceFlag",sourceFlag);
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查看用户明细),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看用户明细发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查看用户明细");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			throw e;
		}
		return "searchUserDetail";
	}
	

	/**
	 * 注销用户
	 * @throws Exception 
	 */
	public void cancelUser() throws Exception{
		AjaxResult ajaxResult = null;
		GwUserVO loginUser = getLoginUser(),cancelUser = null;
		try {
			Long userId = Long.parseLong(request.getParameter("userId"));
			cancelUser = userService.searchUserDetail(userId);
			if(cancelUser == null){
				ajaxResult = AjaxResult.ERROR(null, "注销用户userId="+userId+"失败，用户不存在。");
			}else{
				//系统管理员或审核管理员或非（数据用户、机构用户）可以直接注销，其他需要生成待办
				if(GwUserType.SUPER_USER.equals(loginUser.getUserType()) || 
						GwUserType.AUDIT_USER.equals(loginUser.getUserType()) ||
						(!GwUserType.DATA_USER.equals(loginUser.getUserType()) && !(GwUserType.ORG_USER.equals(loginUser.getUserType())))){
					int cancelCount = userService.cancelUser(loginUser,cancelUser);	//直接注销
					message = cancelUser.getLoginName()+"用户注销成功。";
					if(GwUserType.ORG_USER.equals(cancelUser.getUserType())){
						message += cancelUser.getLoginName()+"是机构用户，和机构下的数据用户一共注销了"+cancelCount+"个用户。";
					}
				}else {
					userService.applyCancelUser(loginUser,cancelUser);	//申请注销
					message = "注销申请成功，请等待管理员审核";
				}
				ajaxResult = AjaxResult.SUCCESS(null,message);
			}
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(用户注销),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "用户注销发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "用户注销");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null,"系统异常，注销失败。原因："+e.getMessage());
		} finally{
			try {
				operationLogService.saveOperationLog(loginUser.getLoginName(), cancelUser.getLoginName(), OperateTypeConstent.CANCEL_USER, ajaxResult.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		AppHelper.writeOut(ajaxResult, response);
	}

	//进入用户权限分配页面
	public String userAuthPage()throws Exception{
		String planId = request.getParameter("workPlanId");
		request.setAttribute("planId", planId);
		try{
			user=(GwUserVO)userService.searchUserDetail(user.getUserId());
			if(user.getOrgId()!=null){
				GwOrgVO orgVO=orgService.searchOrg(user.getOrgId());
				GwUserVO orgUser=orgService.searchOrgUser(orgVO.getOrgId());
				List<GwServiceVO> serviceList=userAuthService.searchChooseServiceList(orgUser.getUserId());
				request.setAttribute("orgVO", orgVO);
				request.setAttribute("serviceList", serviceList);
			}
			List<RoleView> roleList=userAuthService.searchRoleListByUserId(user.getUserId());
			List<GwServiceVO> unchooseServiceList=userAuthService.searchUnchooseServiceList(user.getUserId());
			List<GwServiceVO> chooseServiceList=userAuthService.searchChooseServiceList(user.getUserId());
			Map<String, List> serviceMap=roleAuthService.searchServiceListByAllRole();
			List<UserView> orgUserList=userService.searchOrgUserList();
			List<UserView> dataUserList=userService.searchDataUserList(user.getOrgId());
			
			//source为了区分是从用户列表进到分配权限页面，还是从待办列表进到分配权限页面：1表示从用户列表进到分配权限页面
			String source=request.getParameter("source");
			request.setAttribute("source", source);
			request.setAttribute("user", user);
			request.setAttribute("roleList", roleList);
			request.setAttribute("unchooseServiceList", unchooseServiceList);
			request.setAttribute("chooseServiceList", chooseServiceList);
			request.setAttribute("serviceMap", JSONObject.fromObject(serviceMap));
			request.setAttribute("orgUserList", orgUserList);
			request.setAttribute("dataUserList", dataUserList);
		}catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(用户权限分配),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "用户权限分配发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "用户权限分配");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			throw e;
		}
		
		if("dataUser".equals(user.getUserType())){
			return "dataUserAuthPage";
		}else{
			return "userAuthPage";
		}
		
	}
	
	//根据条件查询未被授权的用户服务
	public void searchUnchooseServiceList()throws Exception{
		AjaxResult ajaxResult = null;
		try{
			List<GwServiceVO> servicelist=userAuthService.searchServiceListByService(serviceVO);
			List<GwServiceVO> hiddenServiceList=userAuthService.searchServiceListByModelId(serviceVO);
			Map map=new HashMap();
			map.put("servicelist", servicelist);
			map.put("hiddenServiceList", hiddenServiceList);
			ajaxResult = AjaxResult.SUCCESS(map);
		}catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(用户权限分配),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "用户权限分配：发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "用户权限分配");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null,"查询模型服务列表异常，原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	//保存用户群组，服务授权分配
	public void saveUserAuthInfo()throws Exception{
		AjaxResult ajaxResult = null;
		try {
			String planId = request.getParameter("planId");
			Long workPlanId = StringUtils.isNotBlank(planId)?Long.valueOf(planId):null;
			GwUserVO userVo = getLoginUser();
			Long operUserId = userVo != null?userVo.getUserId():null;
			userAuthService.saveUserAuth(user.getUserType(),workPlanId,user.getUserId(),orgUserSortList, dataUserSortList,chooseRoles, chooseServices,chooseServiceCodes,operUserId);
			ajaxResult = AjaxResult.SUCCESS();
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(用户群组，服务授权分配),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "用户群组，服务授权分配发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "用户群组，服务授权分配");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null,"保存群组授权失败，原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}

	public void upateOnlineStatus() throws Exception{
		AjaxResult ajaxResult = null;
		try {
			String userId = request.getParameter("userId");
			String onlineStatus = request.getParameter("onlineStatus");
			String userType = request.getParameter("userType");
			GwUserVO loginUser = getLoginUser();
			String operUser = loginUser.getLoginName();
			
			//状态转换
			if(!CommonState.ONLINE.equals(onlineStatus)){
				onlineStatus = CommonState.ONLINE;// 原来空白也转值班
			}else {
				onlineStatus = CommonState.NOONLINE;
			}
			
			//转为非值班状态，且为唯一值班状态的，不能转换状态
			if(CommonState.NOONLINE.equals(onlineStatus) && userService.searchOtherOnlineUser(Long.parseLong(userId),userType) == 0){
				ajaxResult = AjaxResult.ERROR(null, "该用户是唯一一个处于值班状态，更新失败！");
				AppHelper.writeOut(ajaxResult, response);
			}else{
				gwUserService.updateOnlineStatus(Long.valueOf(userId), onlineStatus, operUser);
				ajaxResult = AjaxResult.SUCCESS(null, "值班状态更新成功！");
			}
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(修改值班状态),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "修改值班状态发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "修改值班状态");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null,"系统异常，更新失败。原因:"+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	public GwRoleVO getRole() { 
		return role;
	}

	public void setRole(GwRoleVO role) {
		this.role = role;
	}

	public void updateUser() throws Exception {
		AjaxResult ajaxResult = null;
		try {
			GwUserVO loginUser = getLoginUser();
			Map<String, String> map = new HashMap<String, String>();
			map.put("userType", loginUser.getUserType());
			ajaxResult = AjaxResult.SUCCESS(map);
			GwUploadFileVO fileVo = this.upLoadFile("01",user.getLoginName());
			if(StringUtils.isNotBlank(fileVo.getRealName())){
				uploadFileService.save(fileVo);//保存附件
				user.setFileId(fileVo.getFileId());
			}
			userService.updateUser(user, loginUser);
			
			//写操作日志
			operationLogService.saveOperationLog(loginUser.getLoginName(), user.getLoginName(), OperateTypeConstent.UPDATE_USER, loginUser.getLoginName()+"修改用户信息！");
		} catch (Exception e) {
			workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "系统后台错误：修改用户发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,"系统后台错误：修改用户发生错误！原因"+e.getMessage()+"【数据服务网关】",null);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null, "修改失败,原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult,AppHelper.CONTENT_TYPE_HTML, response);
	}
	
	//查询用户类型列表
	public String searchUserTypeList()throws Exception{
		Map<String, GwSysDictVO> userTypeMap=SysDictManage.getSysDict("DICT_USER_TYPE");
		request.setAttribute("userTypeMap", userTypeMap);
		return "userTypeList";
	}
	
	//进入用户类型菜单,按钮分配页面
	public String searchUserTypeFuncPage()throws Exception{
		try{
			List<FuncAndButtonView> funcAndBtnList=userAuthService.searchFuncAndBtnList(user.getUserType(),"userTypeFuncAuth");
			request.setAttribute("funcAndBtnList", funcAndBtnList);
			request.setAttribute("user", user);
		}catch (Exception e) {
			workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "系统后台错误：查看用户类型菜单发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,"系统后台错误：查看用户类型菜单发生错误！原因"+e.getMessage()+"【数据服务网关】",null);
			e.printStackTrace();
			throw e;
		}
		
		return "userTypeFuncAuth";
	}
	
	//用户类型菜单明细页面
	public String searchUserTypeFuncDetail()throws Exception{
		try{
			List<FuncAndButtonView> funcAndBtnList=userAuthService.searchFuncAndBtnList(user.getUserType(),"userTypeFuncDetail");
			request.setAttribute("funcAndBtnList", funcAndBtnList);
			request.setAttribute("user", user);
		}catch (Exception e) {
			workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "系统后台错误：查看用户类型菜单发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,"系统后台错误：查看用户类型菜单发生错误！原因"+e.getMessage()+"【数据服务网关】",null);
			e.printStackTrace();
			throw e;
		}
		return "userTypeFuncDetail";
	}
	
	//保存用户类型菜单,按钮分配
	public void saveUserTypeFunc()throws Exception{
		AjaxResult ajaxResult = null;
		try {
			userAuthService.saveUserTypeFunc(user.getUserType(), chooseFuncs, chooseButtons);
			ajaxResult = AjaxResult.SUCCESS();
			
			//写操作日志
			GwUserVO loginUser = getLoginUser();
			operationLogService.saveOperationLog(loginUser.getLoginName(), user.getUserType(), OperateTypeConstent.USERTYPR_MANAGE, loginUser.getLoginName()+"对用户类型："+user.getUserType()+"进行菜单、按钮分配!");
		} catch (Exception e) {
			workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "系统后台错误：用户类型菜单,按钮分配发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,"系统后台错误：用户类型菜单,按钮分配发生错误！原因"+e.getMessage()+"【数据服务网关】",null);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null,"保存用户类型菜单按钮分配失败，原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	public String getChooseRoles() {
		return chooseRoles;
	}

	public void setChooseRoles(String chooseRoles) {
		this.chooseRoles = chooseRoles;
	}

	public String getChooseFuncs() {
		return chooseFuncs;
	}

	public void setChooseFuncs(String chooseFuncs) {
		this.chooseFuncs = chooseFuncs;
	}

	public String getChooseButtons() {
		return chooseButtons;
	}

	public void setChooseButtons(String chooseButtons) {
		this.chooseButtons = chooseButtons;
	}

	public String getChooseServices() {
		return chooseServices;
	}

	public void setChooseServices(String chooseServices) {
		this.chooseServices = chooseServices;
	}

	public GwServiceVO getServiceVO() {
		return serviceVO;
	}

	public void setServiceVO(GwServiceVO serviceVO) {
		this.serviceVO = serviceVO;
	}

	public String getOrgUserSortList() {
		return orgUserSortList;
	}

	public void setOrgUserSortList(String orgUserSortList) {
		this.orgUserSortList = orgUserSortList;
	}

	public String getDataUserSortList() {
		return dataUserSortList;
	}

	public void setDataUserSortList(String dataUserSortList) {
		this.dataUserSortList = dataUserSortList;
	}

	public String getChooseServiceCodes() {
		return chooseServiceCodes;
	}

	public void setChooseServiceCodes(String chooseServiceCodes) {
		this.chooseServiceCodes = chooseServiceCodes;
	}
	
}
