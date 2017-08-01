package com.gztydic.gateway.web.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.view.GwModelLiabilityLogView;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.LiabilityService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

@Controller
@Scope("prototype")
public class LiabilityAction extends BaseAction{
	
	private static final long serialVersionUID = 1L;

	@Resource(name="liabilityServiceImpl")
	private LiabilityService liabilityService;
	
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	private GwModelLiabilityLogView logView;
	private String searchBy;
	
	//查询免责日志列表
	public String searchLiabilityLogList() throws Exception{
		try {
			if(pageObject == null) pageObject = new PageObject();
			pageObject = liabilityService.searchLiabilityLogList(logView, pageObject);
			
			Map liabilityCountMap = liabilityService.searchLiabilityCount(logView);
			request.setAttribute("liabilityCountMap", liabilityCountMap);
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询免责日志列表),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查询免责日志列表时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询免责日志列表");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw e;
		}
		return "searchLiabilityLogList";
	}
	
	//查询免责日志
	public String searchLiabilityLog() throws Exception{
		try {
			logView = liabilityService.searchLiabilityLog(logView);
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询免责日志),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询免责日志");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);

			throw e;
		}
		return "searchLiabilityLog";
	}
	
	//查询敏感信息追溯列表
	public String searchDesenList() throws Exception{
		try {
			if(pageObject == null) pageObject = new PageObject();
			pageObject = liabilityService.searchDesenList(logView, pageObject,searchBy);
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询敏感信息追溯列表),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询敏感信息追溯列表");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw e;
		}
		return "searchDesenList";
	}
	
	public GwModelLiabilityLogView getLogView() {
		return logView;
	}

	public void setLogView(GwModelLiabilityLogView logView) {
		this.logView = logView;
	}

	public String getSearchBy() {
		return searchBy;
	}

	public void setSearchBy(String searchBy) {
		this.searchBy = searchBy;
	}
	
}
