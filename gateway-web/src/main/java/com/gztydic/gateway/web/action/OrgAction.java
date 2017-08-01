package com.gztydic.gateway.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.AjaxResult;
import com.gztydic.gateway.core.common.util.AppHelper;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.view.OrgView;
import com.gztydic.gateway.core.view.WorkPlanView;
import com.gztydic.gateway.core.vo.GwOrgVO;
import com.gztydic.gateway.core.vo.GwUploadFileVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.OperationLogService;
import com.gztydic.gateway.system.OrgService;
import com.gztydic.gateway.system.UploadFileService;
import com.gztydic.gateway.system.UserButtonService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

@Controller
@Scope("prototype")
public class OrgAction extends BaseAction {
	
	private GwOrgVO orgVO;
	private GwUserVO userVO;
	private OrgView orgView;
	@Resource(name="orgServiceImpl")
	private OrgService orgService;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	@Resource(name="uploadFileServiceImpl")
	private UploadFileService uploadFileService;
	@Resource(name="userButtonServiceImpl")
	private UserButtonService userButtonService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	public String searchOrgAccess() throws Exception {
		GwUserVO userVO = getLoginUser();
		//数据用户直接进入明细
		if (userVO.getUserType().equals(GwUserType.ORG_USER) ) {
			return searchOrgDetail();
		}  else {
		//其他用户查询所有列表
			return searchOrgList();
		}
	}
	
	public String searchOrgList() throws Exception{
		try{
			if(pageObject == null)pageObject=new PageObject();
			if(orgVO==null)orgVO=new GwOrgVO();
			pageObject=orgService.searchOrgList(orgVO,pageObject);
			
			List<OrgView> orgList=pageObject.getData();
			String[] userIds = new String[orgList.size()];
			for(int i=0;i<orgList.size();i++){
				userIds[i] = String.valueOf(orgList.get(i).getUserId());
			}
			Map updateMap = new HashMap();
			//查询列表中机构用户的待办任务，存在则表示申请了修改
			List<WorkPlanView> workList = workPlanService.searchWorkPlanByUpdate(WorkPlanConstent.UPDATE_AUDIT, WorkPlanConstent.WAIT_FOR_DEAL, userIds);
			for (WorkPlanView p : workList) {//机构用户修改待审列表
				updateMap.put(Long.parseLong(p.getParamValue()), p.getPlanId());	
			}
			request.setAttribute("updateMap", updateMap);
		}catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询机构列表),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看机构列表发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询机构列表");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw e;
		}
		return "orgList";
	}
	
	public String searchOrgDetail()throws Exception{
		try{
			String sourceFlag = request.getParameter("sourceFlag");	// 1 为菜单直接进入 2为查询列表进入
			if("1".equals(sourceFlag)){
				userVO=getLoginUser();
				Long fileId = userVO.getFileId();
				if(fileId != null){
					GwUploadFileVO fileVo  = uploadFileService.findById(fileId);
					request.setAttribute("fileVo",fileVo);
				}
				String[] userIds = new String[1];
				userIds[0] = String.valueOf(userVO.getUserId());
				Map updateMap = new HashMap();
				//查询列表中机构用户的待办任务，存在则表示申请了修改
				List<WorkPlanView> workList = workPlanService.searchWorkPlanByUpdate(WorkPlanConstent.UPDATE_AUDIT, WorkPlanConstent.WAIT_FOR_DEAL, userIds);
				for (WorkPlanView p : workList) {//机构用户修改待审列表
					updateMap.put(Long.parseLong(p.getParamValue()), p.getPlanId());	
				}
				request.setAttribute("updateMap", updateMap);
				GwOrgVO org=orgService.searchOrg(userVO.getOrgId());
				GwUserVO orgUser=orgService.searchOrgUser(userVO.getOrgId());
				List<GwUserVO> dataUserList=orgService.searchDataUser(userVO.getOrgId());
				request.setAttribute("userVO", userVO);
				request.setAttribute("org", org);
				request.setAttribute("orgUser", orgUser);
				request.setAttribute("dataUserList", dataUserList);
			}else{
				GwOrgVO org=orgService.searchOrg(orgVO.getOrgId());
				GwUserVO orgUser=orgService.searchOrgUser(orgVO.getOrgId());
				Long fileId = orgUser.getFileId();
				if(fileId != null){
					GwUploadFileVO fileVo  = uploadFileService.findById(fileId);
					request.setAttribute("fileVo",fileVo);
				}
				List<GwUserVO> dataUserList=orgService.searchDataUser(orgVO.getOrgId());
				request.setAttribute("org", org);
				request.setAttribute("orgUser", orgUser);
				request.setAttribute("dataUserList", dataUserList);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询机构信息),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看机构明细发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询机构信息");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw e;
		}
		return "searchOrgDetail";
	}
	
	public String updateOrgPage()throws Exception{
		try{
			boolean isAuth=userButtonService.searchAuthorization(WorkPlanConstent.UPDATE_AUDIT, getLoginUser().getUserType());
			GwUserVO userVO=orgService.searchOrgUser(orgVO.getOrgId());
			Long fileId = userVO.getFileId();
			if(fileId != null){
				GwUploadFileVO fileVo  = uploadFileService.findById(fileId);
				request.setAttribute("fileVo",fileVo);
			}
			orgVO=orgService.searchOrg(orgVO.getOrgId());
			request.setAttribute("orgVO", orgVO);
			request.setAttribute("userVO", userVO);
			request.setAttribute("isAuth", isAuth);
		}catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(修改机构信息),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "系统后台错误：查看机构用户信息发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "修改机构信息");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw e;
		}
		return "updateOrgPage";
	}
	
	public void searchOrgName()throws Exception{
		AjaxResult ajaxResult = null;
		try {
			if(orgService.checkUpdateOrg(orgVO)){
				ajaxResult = AjaxResult.FAILURE(null, "机构已经存在!");
			}else{
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询机构名称),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查询机构名称发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询机构名称");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null,"查询机构名称发生错误!原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	public void updateOrg()throws Exception{
		AjaxResult ajaxResult = null;
		try {
			GwUserVO loginUser = getLoginUser();
			Map<String, String> map = new HashMap<String, String>();
			map.put("userType", loginUser.getUserType());
			ajaxResult = AjaxResult.SUCCESS(map);
			GwUploadFileVO fileVo = this.upLoadFile("01",userVO.getLoginName());
			if(StringUtils.isNotBlank(fileVo.getRealName())){
				uploadFileService.save(fileVo);//保存附件
				userVO.setFileId(fileVo.getFileId());
			}
			orgService.updateOrgAndUser(userVO,orgVO, loginUser);	
			
			//写操作日志
			operationLogService.saveOperationLog(loginUser.getLoginName(), userVO.getLoginName(), OperateTypeConstent.UPDATE_USER, "机构用户"+loginUser.getLoginName()+"修改机构信息！");
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(修改机构信息),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "修改机构用户信息发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "修改机构信息");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null, "修改失败,原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult,AppHelper.CONTENT_TYPE_HTML, response);
	}
	public GwOrgVO getOrgVO() {
		return orgVO;
	}

	public void setOrgVO(GwOrgVO orgVO) {
		this.orgVO = orgVO;
	}

	public GwUserVO getUserVO() {
		return userVO;
	}

	public void setUserVO(GwUserVO userVO) {
		this.userVO = userVO;
	}
	public OrgView getOrgView() {
		return orgView;
	}
	public void setOrgView(OrgView orgView) {
		this.orgView = orgView;
	}

	
}
