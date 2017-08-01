package com.gztydic.gateway.web.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
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
import com.gztydic.gateway.core.dao.GwServiceCheckRuleAuditDAO;
import com.gztydic.gateway.core.dao.GwServiceCheckRuleDAO;
import com.gztydic.gateway.core.dao.GwUploadFileDAO;
import com.gztydic.gateway.core.view.GwDesenRuleServiceFieldView;
import com.gztydic.gateway.core.view.GwDesenServiceFieldView;
import com.gztydic.gateway.core.view.GwDesenServiceInfoView;
import com.gztydic.gateway.core.view.WorkPlanView;
import com.gztydic.gateway.core.vo.GwDesenServiceFieldVO;
import com.gztydic.gateway.core.vo.GwDesenServiceInfoVO;
import com.gztydic.gateway.core.vo.GwModelDataFetchVO;
import com.gztydic.gateway.core.vo.GwModelVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleAuditVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwSysDictVO;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.model.ModelDataAppServiceImpl;
import com.gztydic.gateway.model.ModelInfoService;
import com.gztydic.gateway.system.DesenModelService;
import com.gztydic.gateway.system.GwServiceService;
import com.gztydic.gateway.system.OperationLogService;
import com.gztydic.gateway.system.ServiceDictService;
import com.gztydic.gateway.system.UploadFileService;
import com.gztydic.gateway.system.UserAuthService;
import com.gztydic.gateway.system.UserService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;
import com.jcraft.jsch.Session;

@Controller
@Scope("prototype")
public class DesenModelAction extends BaseAction{
	
	private static final long serialVersionUID = 1L;
	
	@Resource(name="desenModelServiceImpl")
	private DesenModelService desenModelService;
	
	@Resource(name="gwServiceServiceImpl")
	private GwServiceService serviceService;
	
	@Resource(name="modelInfoServiceImpl")
	private ModelInfoService modelInfoService;
	
	@Resource(name="serviceDictServiceImpl")
	private ServiceDictService dictService;
	
	@Resource(name="userServiceImpl")
	private UserService userService;
	
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	@Resource(name="modelDataAppServiceImpl")
	private ModelDataAppServiceImpl modelDataAppService;
	
	@Resource(name="uploadFileServiceImpl")
	private UploadFileService uploadFileService;
	
	@Resource
	private GwServiceCheckRuleDAO checkRuleDAO;
	
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	
	@Resource(name="userAuthServiceImpl")
	private UserAuthService userAuthService;
	
	@Resource
	private GwServiceCheckRuleAuditDAO checkRuleAuditDAO;
	
	private List<GwDesenServiceInfoVO> desenInfoList;
	private List<GwDesenServiceFieldVO> desenFieldList;
	private Long workPlanId;
	private Long fetchId;
	private String suggestion;//审核意见
	private GwModelDataFetchVO fetchVO;
	private GwServiceVO serviceVO;
	private Long fileId;
	
	//查询模型服务信息脱敏配置列表
	public String searchDesenServiceInfoList() throws Exception{
		try {
			if(pageObject == null) pageObject = new PageObject();
			String loginName = request.getParameter("loginName");
			pageObject = desenModelService.searchDesenServiceInfoList(loginName, pageObject);
		} catch (Exception e) {
			e.printStackTrace();
			workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "系统后台错误：查询模型服务信息脱敏配置列表发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,"系统后台错误：查询模型服务信息脱敏配置列表发生错误！原因"+e.getMessage()+"【数据服务网关】",null);			throw e;
		}
		return "desenServiceInfoList";
	}
	
	//查询同一用户、模型下其他服务的配置信息
	public String searchModelServiceDesenInfo() throws Exception{
		try {
			Long userId = Long.parseLong(request.getParameter("userId"));
			Long modelId = Long.parseLong(request.getParameter("modelId"));
			List<GwDesenServiceInfoView> list = desenModelService.searchModelServiceDesenInfo(userId, modelId);
			request.setAttribute("desenList", list);
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询同一用户、模型下其他服务的配置信息),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查询同一用户、模型下其他服务的配置信息时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询同一用户、模型下其他服务的配置信息");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw e;
		}
		return "searchModelServiceDesenInfo";
	}
	
	//新增、修改模型信息脱敏配置
	public void updateDesenServiceInfoList() throws Exception{
		AjaxResult ajaxResult = null;
		try {
			desenModelService.updateDesenServiceInfoList(desenInfoList, getLoginUser());
			ajaxResult = AjaxResult.SUCCESS();
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(新增、修改模型信息脱敏配置),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "服务信息脱敏配置发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "新增、修改模型信息脱敏配置");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.FAILURE(null,e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	//查询模型字段脱敏规则
	public String searchDesenServiceFieldList() throws Exception{
		try {
			if(pageObject == null) pageObject = new PageObject();
			String loginName = request.getParameter("loginName");
			pageObject = desenModelService.searchDesenServiceFieldList(loginName, pageObject);
			List<GwDesenServiceFieldView> viewList=pageObject.getData();
			if(viewList!=null){
				String[] userIds = new String[viewList.size()];
				for (int i = 0; i < userIds.length; i++) {
					userIds[i] = String.valueOf(viewList.get(i).getUserId()); 
				}
				String[] serviceIds = new String[viewList.size()];
				for (int i = 0; i < serviceIds.length; i++) {
					serviceIds[i] = String.valueOf(viewList.get(i).getServiceId()); 
				}
				String[] planType=new String[2];
				planType[0]=WorkPlanConstent.FIELD_DESEN_CONF_AUDIT;
				planType[1]=WorkPlanConstent.FIELD_DESEN_CONF_AUDIT_BACK;
				Map userServiceMap = new HashMap();
				List<WorkPlanView> workList = workPlanService.searchWorkPlanForDesenService(planType, WorkPlanConstent.WAIT_FOR_DEAL, userIds,serviceIds);
				for (WorkPlanView p : workList) {
					String userServiceStr=p.getUserId().toString()+","+p.getServiceId().toString();
					userServiceMap.put(userServiceStr, p.getPlanId());
				}
				request.setAttribute("userServiceMap", userServiceMap);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询服务字段脱敏规则),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查询服务字段脱敏规则发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询服务字段脱敏规则");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw e;
		}
		return "desenServiceFieldList";
	}
	
	//查询模型字段脱敏规则配置
	public String desenRuleServiceField() throws Exception{
		Long userId = Long.parseLong(request.getParameter("userId"));
		Long serviceId = Long.parseLong(request.getParameter("serviceId"));
		String workPlanId = request.getParameter("workPlanId");
		String source=request.getParameter("source");
		request.setAttribute("source", source);
		String ruleBatch = null;
		String isServiceFetch = null;
		if(workPlanId != null && workPlanId != ""){
			Map<String, String> paramMap = workPlanParamService.searchParamMap(Long.valueOf(workPlanId));	
			ruleBatch = paramMap.get("ruleBatch");
			isServiceFetch = paramMap.get("isServiceFetch");
		}
		GwServiceVO serviceVO = serviceService.searchService(serviceId);
		if(serviceVO.getModelId() != null){
			GwModelVO model = modelInfoService.searchModelById(serviceVO.getModelId());
			request.setAttribute("model", model);
			
			//查询其他服务的字段脱敏配置列表，并且查询其对应的脱敏类型
			List ruleList =desenModelService.searchDesenRuleOtherServiceFieldList(userId, serviceVO);
			Map<Long,List<GwDesenRuleServiceFieldView>> otherServiceMap =(Map<Long,List<GwDesenRuleServiceFieldView>>)ruleList.get(0);
			Map<Long,String> desenTypeMap=(Map<Long,String>)ruleList.get(1);
			request.setAttribute("otherServiceDesenMap", otherServiceMap);	//其他服务的字段脱敏配置
			request.setAttribute("desenTypeMap", desenTypeMap);//其他服务的字段脱敏类型
			if(!otherServiceMap.isEmpty()){	//其他服务的服务信息
				Map serviceMap = serviceService.searchServiceMapByModel(model);
				request.setAttribute("serviceMap", serviceMap);
			}
		}
		//取数审核退回待办取审核意见
		if(workPlanId != null){
			GwWorkPlanVO workPlanVO = workPlanService.searchById(Long.valueOf(workPlanId));
			if(workPlanVO.getParentPlanId() != null && WorkPlanConstent.GET_DATA_BACK.equals(workPlanVO.getPlanType())){
				GwWorkPlanVO preWorkPlanVO = workPlanService.searchById(workPlanVO.getParentPlanId());
				GwUserVO createUser=userService.searchUserDetail(preWorkPlanVO.getCreateUserId());
				request.setAttribute("preWorkPlanVO", preWorkPlanVO);
				request.setAttribute("createUser", createUser);
			}
			request.setAttribute("workPlanVO", workPlanVO);
		}
		GwModelDataFetchVO fetch=modelDataAppService.searchUserServiceAppInfo(userId, serviceId);

		String userType = getLoginUser().getUserType();
		
		List<GwServiceCheckRuleVO> checkRuleList = checkRuleDAO.searchLastCheckRuleList(userId,serviceId);
		request.setAttribute("checkRuleList", checkRuleList);
		
		if(serviceVO.getServiceSource().equals("2")){//108
			List<GwDesenRuleServiceFieldView> ruleViewList = desenModelService.search108DesenRuleServiceFieldList(userId, serviceId);
			request.setAttribute("ruleLength", ruleViewList.size());
			request.setAttribute("ruleViewList", ruleViewList);
		}else{
			List<GwDesenRuleServiceFieldView> ruleViewList = desenModelService.searchDesenRuleServiceFieldList(userId, serviceId);
			request.setAttribute("ruleViewList", ruleViewList);
		}
		
		
		request.setAttribute("fieldDict", JSONObject.fromObject(dictService.searchFieldDictMap(userId,serviceVO)));	//字段、字典关联数据
		request.setAttribute("groupDict", dictService.searchGroupDict());	//字典组
		request.setAttribute("dictMap", JSONObject.fromObject(dictService.searchDictMap()));		//字典
		request.setAttribute("service", serviceVO);
		request.setAttribute("user", userService.searchUserDetail(userId));
		request.setAttribute("fetch", fetch);
		Long fileId = fetch.getCheckFileId();
		if(fileId != null){
			GwUploadFileVO fileVo  = uploadFileService.findById(fileId);
			request.setAttribute("fileVo",fileVo);
		}
		request.setAttribute("search", request.getParameter("search"));
		return "desenRuleServiceField";
	}
	
	
	
	//修改模型字段脱敏规则配置
	public void updateDesenRuleServiceField() throws Exception {
		AjaxResult ajaxResult = null;
		GwUploadFileVO fileVO = null;
		GwUserVO loginUserVO = getLoginUser();
		String type = request.getParameter("desenTypeRadio");
		String preFileId = request.getParameter("preFileId");
		if(workPlanId != null){
			Map<String, String> paramMap = workPlanParamService.searchParamMap(workPlanId);
		}
		try {		
			fileVO = this.upLoadFile("02", loginUserVO.getUserId().toString());
			if(fileVO.getFilePath() == null){
				fileVO = uploadFileService.findById(Long.valueOf(preFileId));
			}
			//}
			String ruleUpdateType = request.getParameter("ruleUpdateType");			
			desenModelService.updateDesenServiceFieldList(workPlanId,desenFieldList, loginUserVO, fetchVO, fileVO, upload,ruleUpdateType,setProcessId(),type);
			ajaxResult = AjaxResult.SUCCESS();
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(字段脱敏规则配置),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "修改字段脱敏规则配置时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "字段脱敏规则配置");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null, "保存字段脱敏规则配置失败。原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, AppHelper.CONTENT_TYPE_HTML, response);
	}
	
	public void searchTaskCheckCount() throws Exception {
		AjaxResult ajaxResult = null;
		try {
			Long serviceId = Long.parseLong(request.getParameter("serviceId"));
			Long userId = Long.parseLong(request.getParameter("userId"));
			Map<String, Integer> taskCountMap = desenModelService.searchTaskCheckCount(serviceId, userId);
			ajaxResult = AjaxResult.SUCCESS(taskCountMap);
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询任务检查次数),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询任务检查次数");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null, "系统异常，请联系管理员");
		}
		AppHelper.writeOut(ajaxResult, response);
	}	

	//***********数据用户合规检查规则文件解析展示************
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
				operationLogService.saveOperationLog(loginUser.getLoginName(), loginUser.getLoginName(),OperateTypeConstent.ADD_SERVICE, loginUser.getLoginName()+"上存合规检查规则文件！");
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
	//**********重新申请服务取数*************	
	public void reApplyService() throws Exception{
		AjaxResult ajaxResult = null;
		String fileId = request.getParameter("fileId");
		String preFileId = request.getParameter("preFileId");
		String pushDataWay = request.getParameter("pushDataWay");
		GwUploadFileVO fileVo = null;
		GwUserVO loginUser = getLoginUser();
		
		try {
			if((fileId == ""||fileId==null)&&(preFileId==""||preFileId==null))throw new Exception("您还没配置规则文件！");
			//若fileId是新上传的规则表ID，若没上传就用preFileId之前的规则表
			if(fileId ==null||fileId ==""){
				fileVo=uploadFileService.findById(Long.valueOf(preFileId));//
			}else{
			    fileVo = uploadFileService.findById(Long.valueOf(fileId));
			}
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
				if(StringUtils.isNotBlank(pushDataWay)){
					fetchVO.setPushDataWay(Long.valueOf(pushDataWay));
				}
				modelDataAppService.save(fetchVO);	
				fetchVO = modelDataAppService.searchUserServiceAppInfo(fetchVO.getUserId(), fetchVO.getServiceId());
			}else {
				//把取数状态改回待审核
				fetchVO.setAuditStatus(CommonState.WAIT_AUDIT);
				fetchVO.setUpdateTime(new Date());
				fetchVO.setCheckFileId(fileVo.getFileId());
				fetchVO.setPushDataWay(Long.valueOf(pushDataWay));
				modelDataAppService.update(fetchVO);
				//更新服务
				modelInfoService.updateServiceInfo(serviceVO,getLoginUser());
			}

			//解析合规检查表并给数据安全管理员一条审核记录
			desenModelService.savaRuleAndWorkPlan(fetchVO,serviceVO, loginUser, fileVo, upload, "1",setProcessId(),DataTypeConstent.SHENQINGQUSHU);	
			ajaxResult = AjaxResult.SUCCESS();
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(新增服务),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "服务新增发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "新增服务");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null, "新增服务失败，原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	//**********保存规则表并提交给审核人员************
	public void saveRuleAndCommit()throws Exception{
		AjaxResult ajaxResult = null;
		String workPlanId = request.getParameter("workPlanId");
		String fileId = request.getParameter("fileId");
		String preFileId = request.getParameter("preFileId");
		String serviceId = request.getParameter("serviceId");
		String userId = request.getParameter("userId");
		GwUploadFileVO fileVo = null;
		GwUserVO loginUser = getLoginUser();
		GwServiceVO serviceVO = serviceService.searchService(Long.valueOf(serviceId));

		try {
			if((fileId == ""||fileId==null)&&(preFileId==""||preFileId==null))throw new Exception("您还没配置规则文件！");
			//若fileId是新上传的规则表ID，若没上传就用preFileId之前的规则表
			if(fileId ==null||fileId ==""){
				fileVo=uploadFileService.findById(Long.valueOf(preFileId));//
			}else{
			    fileVo = uploadFileService.findById(Long.valueOf(fileId));
			}
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
			}else {
				//把取数状态改回待审核
				fetchVO.setAuditStatus(CommonState.WAIT_AUDIT);
				fetchVO.setUpdateTime(new Date());
				fetchVO.setCheckFileId(fileVo.getFileId());
				modelDataAppService.update(fetchVO);
				//更新服务
				modelInfoService.updateServiceInfo(serviceVO,getLoginUser());
			}
			//解析合规检查表并给审核人员一条审核记录
			String processId = setProcessId();
			desenModelService.savaRule(loginUser,fetchVO,workPlanId,serviceId,String.valueOf(fetchId),userId,upload,fileId,processId);
			ajaxResult = AjaxResult.SUCCESS();
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(新增服务),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "服务新增发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "新增服务");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null, "新增服务失败，原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);

	}
	
	
	public String toAddServiceAndFetch()throws Exception{
		GwUserVO loginUserVO = getLoginUser();
		request.setAttribute("appUserVO", loginUserVO);
		return "addServiceAndFetch";
	}
	
	//**********新增服务取数*************
	public void applyService()throws Exception{
		AjaxResult ajaxResult = null;
		String fileId = request.getParameter("fileId");
		String preFileId = request.getParameter("preFileId");
		String pushDataWay = request.getParameter("pushDataWay");
		GwUploadFileVO fileVo = null;
		GwUserVO loginUser = getLoginUser();
		GwModelDataFetchVO fetchVO;
		try {
			if((fileId == ""||fileId==null))throw new Exception("您还没配置规则文件！");
			//若fileId是新上传的规则表ID，若没上传就用preFileId之前的规则表
		    fileVo = uploadFileService.findById(Long.valueOf(fileId));
			//解析上存文件路径
			String fileVoPathStr[] = fileVo.getFilePath().split("/");
			imageFileName = fileVoPathStr[fileVoPathStr.length-1];
			String filePath = ConfigConstants.BASE_UPLOAD_FILE_PATH+"/uploadFile/"+"desenManage"+"/";
			File upload =new File(filePath + File.separator + imageFileName);
			int count = modelInfoService.searchServiceCount(serviceVO.getServiceCode(), CommonState.SERVICE_SOURCE_108);
			if(count == 0 ){
				//新增服务
				modelInfoService.updateServiceInfo(serviceVO,getLoginUser());
			    //用户服务授权
				userAuthService.saveUserService(getLoginUser().getUserId(),serviceVO.getServiceId());	
				//新增待审核的取数，状态为1
				fetchVO = new GwModelDataFetchVO();
				fetchVO.setUserId(getLoginUser().getUserId());
				fetchVO.setModelId(serviceVO.getModelId());
				fetchVO.setServiceId(serviceVO.getServiceId());
				fetchVO.setCreateTime(new Date());
				fetchVO.setCreateUser(getLoginUser().getLoginName());
				fetchVO.setAuditStatus(CommonState.WAIT_AUDIT);
				fetchVO.setCheckFileId(fileVo.getFileId());
				fetchVO.setDesenType("2");
				fetchVO.setCheckFileId(fileVo.getFileId());
				if(StringUtils.isNotBlank(pushDataWay)){
					fetchVO.setPushDataWay(Long.valueOf(pushDataWay));
				}
				modelDataAppService.save(fetchVO);
				//解析合规检查表并给数据安全管理员一条审核记录
				desenModelService.savaRuleAndWorkPlan(fetchVO,serviceVO, loginUser, fileVo, upload, "1",setProcessId(),DataTypeConstent.SHENQINGQUSHU);				
				ajaxResult = AjaxResult.SUCCESS();
			}else {
				ajaxResult = AjaxResult.FAILURE(null, "已存在服务编码为"+serviceVO.getServiceCode()+"的服务，请修改后再保存");
			}
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(新增服务),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "服务新增发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "新增服务");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null, "新增服务失败，原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
   
	
	public List<GwDesenServiceInfoVO> getDesenInfoList() {
		return desenInfoList;
	}

	public void setDesenInfoList(List<GwDesenServiceInfoVO> desenInfoList) {
		this.desenInfoList = desenInfoList;
	}

	public List<GwDesenServiceFieldVO> getDesenFieldList() {
		return desenFieldList;
	}

	public void setDesenFieldList(List<GwDesenServiceFieldVO> desenFieldList) {
		this.desenFieldList = desenFieldList;
	}

	public Long getWorkPlanId() {
		return workPlanId;
	}

	public void setWorkPlanId(Long workPlanId) {
		this.workPlanId = workPlanId;
	}

	public Long getFetchId() {
		return fetchId;
	}

	public void setFetchId(Long fetchId) {
		this.fetchId = fetchId;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	public GwModelDataFetchVO getFetchVO() {
		return fetchVO;
	}

	public void setFetchVO(GwModelDataFetchVO fetchVO) {
		this.fetchVO = fetchVO;
	}
	
	public GwServiceVO getServiceVO() {
		return serviceVO;
	}

	public void setServiceVO(GwServiceVO serviceVO) {
		this.serviceVO = serviceVO;
	}
}
