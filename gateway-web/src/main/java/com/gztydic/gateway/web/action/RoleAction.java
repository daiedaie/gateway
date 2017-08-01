package com.gztydic.gateway.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.AjaxResult;
import com.gztydic.gateway.core.common.util.AppHelper;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.view.FuncAndButtonView;
import com.gztydic.gateway.core.view.GwServiceView;
import com.gztydic.gateway.core.vo.GwModelVO;
import com.gztydic.gateway.core.vo.GwRoleVO;
import com.gztydic.gateway.core.vo.GwServiceVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.OperationLogService;
import com.gztydic.gateway.system.RoleAuthService;
import com.gztydic.gateway.system.RoleService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

@Controller
@Scope("prototype")
public class RoleAction extends BaseAction {
	
	private static final long serialVersionUID = 1L;
	private GwRoleVO roleVO;
	private String roleCode;
    private String chooseServices;
    private String chooseFuncs;
    private String chooseButtons;
    private GwModelVO modelVO;
	@Resource(name="roleServiceImpl")
	private RoleService roleService;
	@Resource(name="roleAuthServiceImpl")
	private RoleAuthService roleAuthService;
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	//群组新增
	public void saveRole() throws Exception{
		AjaxResult ajaxResult = null;
		try {
			if(roleService.checkAddRole(roleVO)){
				ajaxResult = AjaxResult.FAILURE(null, "群组已经存在");
			}else{
				roleService.saveRole(roleVO,getLoginUser());
				ajaxResult = AjaxResult.SUCCESS(null,"群组新增成功！");
				
				//写操作日志
				GwUserVO loginUser = getLoginUser();
				operationLogService.saveOperationLog(loginUser.getLoginName(), loginUser.getLoginName(), OperateTypeConstent.ADD_ROLE, loginUser.getLoginName()+"新增群组:"+roleVO.getRoleName()+"！");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(群组新增),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "群组新增发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "群组新增");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null,"新增发生错误！原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
		
	}
	
	// 群组修改
	public void updateRole() throws Exception{
		AjaxResult ajaxResult = null;
		try {
			if(roleService.checkUpdateRole(roleVO)){
				ajaxResult = AjaxResult.FAILURE(null, "群组已经存在!");
			}else{
				roleService.updateRole(roleVO,getLoginUser());
				ajaxResult = AjaxResult.SUCCESS(null,"群组修改成功！");
				
				//写操作日志
				GwUserVO loginUser = getLoginUser();
				operationLogService.saveOperationLog(loginUser.getLoginName(), loginUser.getLoginName(), OperateTypeConstent.UPDATE_ROLE, loginUser.getLoginName()+"修改群组:"+roleVO.getRoleName()+"！");
			}
			
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(群组修改),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "群组修改发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "群组修改");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null,"修改发生错误!原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	//群组列表显示
	public String searchRoleList() throws Exception{
		try{
			if(pageObject == null) pageObject = new PageObject();
			if(roleVO == null)roleVO=new GwRoleVO();
			pageObject= roleService.searchRoleList(roleVO, pageObject);
		}catch (Exception e) {
			
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询群组列表),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看群组列表发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询群组列表");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			throw e;
		}
		return "roleList";
	}

	// 群组删除
	public void deleteRole() throws Exception{
		AjaxResult ajaxResult = null;
		try{
			GwRoleVO role=roleService.searchRoleByRoleCode(roleCode).get(0);
			roleService.deleteRole(roleCode);
			ajaxResult = AjaxResult.SUCCESS(null,"群组删除成功！");
			//写操作日志
			GwUserVO loginUser = getLoginUser();
			operationLogService.saveOperationLog(loginUser.getLoginName(), loginUser.getLoginName(), OperateTypeConstent.DELETE_ROLE, loginUser.getLoginName()+"删除群组:"+role.getRoleName()+"！");
			
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(群组删除),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "删除群组发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "群组删除");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null,"群组删除发生错误!原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}	
	
	//修改页面群组信息
	public String searchRoleByRoleCode() throws Exception{
		try{
			GwRoleVO role=roleService.searchRoleByRoleCode(roleVO.getRoleCode()).get(0);
			request.setAttribute("role", role);
		}catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(群组修改),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看群组信息发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "群组修改");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			throw e;
		}
		return "updateRole";
		
	}
	
	//群组明细
	public String searchRoleDetail() throws Exception{
		try{
			roleVO= roleService.searchRoleByRoleCode(roleVO.getRoleCode()).get(0);
			List<GwServiceVO> serviceList=roleService.searchServiceListByRoleCode(roleVO.getRoleCode());
			List<FuncAndButtonView> funcAndButtonViewList=roleService.searchFuncAndBtnListByRoleCode(roleVO.getRoleCode());
			request.setAttribute("roleVO",roleVO);
			request.setAttribute("serviceList",serviceList);
			request.setAttribute("funcAndButtonViewList",funcAndButtonViewList);
			
		}catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查看群组信息),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看群组信息发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查看群组信息");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			throw e;
		}
		return "searchRoleDetail";
	}
	
	
	//群组授权页面数据
	public String roleAuth()throws Exception{
		try{
			String sort = request.getParameter("sort");
			String asc = request.getParameter("asc");			
			
			roleVO= roleService.searchRoleByRoleCode(roleVO.getRoleCode()).get(0);
//			List<FuncAndButtonView> viewList=roleAuthService.searchFuncAndBtnListByRoleCode(roleVO.getRoleCode());
			List<GwServiceView> serviceList=roleAuthService.searchServiceListByRoleCode(roleVO.getRoleCode(),sort,asc);
			request.setAttribute("roleVO",roleVO);
//			request.setAttribute("viewList",viewList);
			request.setAttribute("serviceList",serviceList);
		}catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(群组授权),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "群组授权发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "群组授权");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			throw e;
		}
		return "roleAuth";
	}
	
	//保存群组授权信息
	public void saveRoleAuthInfo()throws Exception{
		AjaxResult ajaxResult = null;
		try {
			System.out.println(chooseServices);
			roleAuthService.updateRoleAuthService(roleVO.getRoleCode(), chooseServices);
			ajaxResult = AjaxResult.SUCCESS();
			
			//写操作日志
			GwUserVO loginUser = getLoginUser();
			operationLogService.saveOperationLog(loginUser.getLoginName(), loginUser.getLoginName(), OperateTypeConstent.PERMISSED_ROLE, loginUser.getLoginName()+"给群组:"+roleVO.getRoleName()+"进行服务分配！");
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(群组授权),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "群组授权发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "群组授权");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null,"保存群组授权失败，原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	public GwRoleVO getRoleVO() {
		return roleVO;
	}

	public void setRoleVO(GwRoleVO roleVO) {
		this.roleVO = roleVO;
	}
	public GwModelVO getModelVO() {
		return modelVO;
	}

	public void setModelVO(GwModelVO modelVO) {
		this.modelVO = modelVO;
	}

	public String getChooseServices() {
		return chooseServices;
	}

	public void setChooseServices(String chooseServices) {
		this.chooseServices = chooseServices;
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

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}



}
