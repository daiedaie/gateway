package com.gztydic.gateway.web.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.config.SysDictManage;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.DataTypeConstent;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.AjaxResult;
import com.gztydic.gateway.core.common.util.AppHelper;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.view.GwServiceView;
import com.gztydic.gateway.core.view.UserModelServiceAppVO;
import com.gztydic.gateway.core.view.WorkPlanView;
import com.gztydic.gateway.core.vo.GwDesenServiceInfoVO;
import com.gztydic.gateway.core.vo.GwModelVO;
import com.gztydic.gateway.core.vo.GwServiceCheckRuleVO;
import com.gztydic.gateway.core.vo.GwServiceFieldVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwSysDictVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.model.ModelDataAppServiceImpl;
import com.gztydic.gateway.model.ModelInfoService;
import com.gztydic.gateway.system.DesenModelService;
import com.gztydic.gateway.system.GwUserService;
import com.gztydic.gateway.system.ProcessOperationService;
import com.gztydic.gateway.system.ProcessService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

/** 
 * @ClassName: ModelInfoAction 
 * @Description: TODO(模型信息页面请求控制类) 
 * @author davis
 * @date 2014-11-19 下午03:18:06 
 *  
 */
@Controller
@Scope("prototype")
public class ServiceInfoAction extends BaseAction{

	private static final long serialVersionUID = 1L;
	
	@Resource(name="modelInfoServiceImpl")
	private ModelInfoService modelInfoService;
	
	@Resource(name="desenModelServiceImpl")
	private DesenModelService desenModelService;
	
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	
	@Resource(name="gwUserServiceImpl")
	private GwUserService userService ;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	private GwModelVO model;
	private GwServiceView serviceView;
	private GwServiceVO serviceVO;
	
	@Resource(name="processServiceImpl")
	private ProcessService processService;
	
	@Resource(name="processOperationServiceImpl")
	private ProcessOperationService processOperationService;
	
	@Resource(name="modelDataAppServiceImpl")
	private ModelDataAppServiceImpl modelDataAppService;
	
	//进入服务查看列表页面
	public String searchServiceList() throws Exception{
		try {
			if(pageObject==null) pageObject = new PageObject();
			String loginName = request.getParameter("loginName");
			GwUserVO userVO = getLoginUser();
			if(StringUtils.isNotBlank(loginName)){	//根据登录帐号查询
				userVO = userService.getGwUser(loginName);
				request.setAttribute("loginName", loginName);
			}
			pageObject = modelInfoService.searchServiceList(userVO, serviceView, pageObject);
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查看服务列表),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看服务列表发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查看服务列表");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw e;
		}
		return "serviceList";
	}
	
	public String searchService() throws Exception{
		try {
			GwUserVO loginUser = getLoginUser();
			List<GwServiceFieldVO> inputList  = modelInfoService.searchServicefieldInput(serviceView.getServiceId());
			List<GwServiceFieldVO> outputList = modelInfoService.searchServicefieldOutput(serviceView.getServiceId());
			serviceView = modelInfoService.findServiceModelById(serviceView.getServiceId());
			GwDesenServiceInfoVO desenModelInfo = desenModelService.searchDesenServiceInfo(loginUser.getUserId(), serviceView.getServiceId());
			Map serviceMap = new HashMap();
			//查询列表中用户的查看申请任务，存在则表示申请了查看
			String[] planType=new String[3];
			planType[0]=WorkPlanConstent.INFO_DESEN_CONF;
			planType[1]=WorkPlanConstent.INFO_DESEN_CONF_AUDIT;
			planType[2]=WorkPlanConstent.INFO_DESEN_CONF_AUDIT_BACK;
			List<WorkPlanView> workList = workPlanService.searchWorkPlanByForService(planType, WorkPlanConstent.WAIT_FOR_DEAL, String.valueOf(getLoginUser().getUserId()),serviceView.getServiceId().toString(),"viewService");
			for (WorkPlanView p : workList) {
				serviceMap.put(p.getUserId(), p.getPlanId());	
			}
			
			Map modelMap = new HashMap();
			List<WorkPlanView> workListSafeUser = workPlanService.searchWorkPlanByForService(planType, WorkPlanConstent.WAIT_FOR_DEAL, String.valueOf(getLoginUser().getUserId()),serviceView.getServiceId().toString(),"viewModel");
			for (WorkPlanView p : workListSafeUser) {
				modelMap.put(p.getUserId(), p.getPlanId());	
			}
			
			request.setAttribute("serviceMap", serviceMap);
			request.setAttribute("modelMap", modelMap);
			request.setAttribute("desenModelInfo",desenModelInfo );
			request.setAttribute("user", loginUser);
			request.setAttribute("inputList", inputList);
			request.setAttribute("outputList", outputList);
			request.setAttribute("serviceView", serviceView);
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查看服务),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看服务发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查看服务");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			throw e;
		}
		return "serviceInfo";
	}

	//生成申请查看服务输入集信息待办
	public void appViewService()throws Exception{
		AjaxResult ajaxResult = null;
		try {
			GwUserVO loginUser = getLoginUser();
			desenModelService.appViewService(loginUser, serviceView);
			
			ajaxResult = AjaxResult.SUCCESS(null,"服务输入集查看申请成功，请等待管理员审核！");
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(服务输入集查看申请),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "服务输入集查看申请发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "服务输入集查看申请");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null,"任务创建失败，服务输入集查看申请不成功！");
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	//生成申请查看模型基本信息待办
	public void appViewModel()throws Exception{
		AjaxResult ajaxResult = null;
		try {
			GwUserVO loginUser = getLoginUser();
			desenModelService.appViewModel(loginUser, serviceView);
			
			ajaxResult = AjaxResult.SUCCESS(null,"模型基本信息查看申请成功，请等待管理员审核！");
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(服务基本信息查看申请),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "服务基本信息查看申请发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "服务基本信息查看申请");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null,"任务创建失败，模型基本信息查看申请不成功！");
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	public void saveServiceInfo() throws Exception{
		AjaxResult ajaxResult = null;

		/***获取流程编码-start***/
		String processIdStr = setProcessId();
		/***获取流程编码-end***/

		try {
			int count = modelInfoService.searchServiceCount(serviceVO.getServiceCode(), CommonState.SERVICE_SOURCE_108);
			if(count == 0){
				modelInfoService.updateServiceInfo(serviceVO,getLoginUser());
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
	
	public String editServiceInfo() throws Exception{
		serviceVO = modelInfoService.searchServiceById(serviceVO.getServiceId());
		return "updateServiceInfo";
	}
	
	public void updateServiceInfo() throws Exception{
		AjaxResult ajaxResult = null;
		try {
			String oldServiceCode = request.getParameter("oldServiceCode");
			//没改serviceCode不需要验证
			int count = oldServiceCode.equals(serviceVO.getServiceCode())?0:modelInfoService.searchServiceCount(serviceVO.getServiceCode(), CommonState.SERVICE_SOURCE_108);
			if(count == 0){
				modelInfoService.updateServiceInfo(serviceVO,getLoginUser());
				ajaxResult = AjaxResult.SUCCESS();
			}else {
				ajaxResult = AjaxResult.FAILURE(null, "已存在服务编码为"+serviceVO.getServiceCode()+"的服务，请修改后再保存");
			}
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(修改服务),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "服务修改发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "修改服务");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null, "修改服务失败，原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	public void deleteServiceInfo() throws Exception{
		AjaxResult ajaxResult = null;
		try {
			modelInfoService.deleteServiceInfo(serviceVO,getLoginUser());
			ajaxResult = AjaxResult.SUCCESS();
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(删除服务),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "服务删除发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "删除服务");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null, "删除服务失败，原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	/**
	 * 
	 * @Title: getServiceInfo 
	 * @Description: TODO(获取服务信息及合规检查规则信息) 
	 * @param @throws Exception    设定文件 
	 * @return void    返回类型 
	 * @throws
	 */
	public void getServiceInfo() throws Exception{
		String param = request.getParameter("param");
		String userId = "";
		String serviceId = "";
		if(!StringUtils.isEmpty(param)){
			String[] params =  param.split(",");
			serviceId = params[0];
			userId = params[1];
		}
		AjaxResult ajaxResult = null;
		HashMap<String, Object> map = new HashMap<String,Object>();
		UserModelServiceAppVO serviceVO = new UserModelServiceAppVO();
		try {
			if(!StringUtils.isEmpty(serviceId)){
				serviceVO = modelDataAppService.searchServiceApp(Long.valueOf(userId),Long.valueOf(serviceId),"2");
				List<GwServiceCheckRuleVO> checkRuleList = desenModelService.searchLastCheckRuleList(Long.valueOf(userId), Long.valueOf(serviceId));
//				request.setAttribute("checkRuleList", checkRuleList);
				map.put("checkRuleList", checkRuleList);
			}
			map.put("serviceVO", serviceVO);
			Map<String, GwSysDictVO> checkDictMap = SysDictManage.getSysDict("DICT_CHECK_RULE_TYPE");
			map.put("checkDictMap", checkDictMap);
			Map<String, GwSysDictVO> serviceTypeDictMap = SysDictManage.getSysDict("DICT_SERVICE_TYPE");
			map.put("serviceTypeDictMap", serviceTypeDictMap);
			Map<String, GwSysDictVO> cycleTypeDictMap = SysDictManage.getSysDict("DICT_CYCLE_TYPE");
			map.put("cycleTypeDictMap", cycleTypeDictMap);
//			request.setAttribute("serviceVO", serviceVO);
//			ajaxResult = AjaxResult.SUCCESS();
			ajaxResult=AjaxResult.SUCCESS(map);
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(删除服务),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "服务删除发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map maps=new HashMap();
			maps.put("userId",String.valueOf(getLoginUser().getUserId()));
			maps.put("operFun", "删除服务");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), maps);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null, "删除服务失败，原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, AppHelper.CONTENT_TYPE_JSON, response);
	}
	
	public GwModelVO getModel() {
		return model;
	}

	public void setModel(GwModelVO model) {
		this.model = model;
	}

	public GwServiceView getServiceView() {
		return serviceView;
	}

	public void setServiceView(GwServiceView serviceView) {
		this.serviceView = serviceView;
	}

	public GwServiceVO getServiceVO() {
		return serviceVO;
	}

	public void setServiceVO(GwServiceVO serviceVO) {
		this.serviceVO = serviceVO;
	}
}
