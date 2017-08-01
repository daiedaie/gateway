package com.gztydic.gateway.web.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.vo.GwSmsVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.SmsServiceImpl;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

@Controller
@Scope("prototype")
public class SmsAction  extends BaseAction{
	private static final long serialVersionUID = 1L;
	private GwSmsVO smsVO;
	
	@Resource(name="smsServiceImpl")
	private SmsServiceImpl smsServiceImpl;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	public String searchSmsList() throws Exception{
		try{
			if(pageObject == null) pageObject = new PageObject();
			if(smsVO == null)smsVO=new GwSmsVO();
			pageObject= smsServiceImpl.searchSMSList(smsVO, pageObject);
		}catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(查询短信列表),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看短信列表发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询短信列表");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			e.printStackTrace();
			throw e;
		}
		return "smsList";
	}
	public GwSmsVO getSmsVO() {
		return smsVO;
	}
	public void setSmsVO(GwSmsVO smsVO) {
		this.smsVO = smsVO;
	}
	
	

}
