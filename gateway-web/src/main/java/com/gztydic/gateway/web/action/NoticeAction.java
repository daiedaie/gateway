package com.gztydic.gateway.web.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.AjaxResult;
import com.gztydic.gateway.core.common.util.AppHelper;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.vo.GwNoticeVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.NoticeService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

@Controller
@Scope("prototype")
public class NoticeAction extends BaseAction{
	
	private static final Log log = LogFactory.getLog(NoticeAction.class);
	
	private static final long serialVersionUID = 1L;
	
	private GwNoticeVO notice;

	@Resource(name="noticeServiceImpl")
	private NoticeService noticeService;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	public String searchNotice() throws Exception{
		notice = noticeService.searchNoticeVO(notice.getNoticeId());
		return "notice";
	}
	
	public String searchNoticeList() throws Exception{
		if(pageObject == null) {
			pageObject = new PageObject();
		}
		pageObject = noticeService.searchNoticeList(notice, pageObject);
		return "noticeList";
	}
	
	public void saveNotice() throws Exception{
		AjaxResult ajaxResult = null;
		try {
			GwUserVO loginUserVO = getLoginUser();
			noticeService.saveNotice(notice, loginUserVO);
			ajaxResult = AjaxResult.SUCCESS();
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(保存公告信息),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "保存公告信息");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			log.error("保存公告信息错误:"+e.getMessage());
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null, e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	public String editNotice() throws Exception{
		notice = noticeService.searchNoticeVO(notice.getNoticeId());
		return "updateNotice";
	}
	
	public void deleteNotice() throws Exception{
		AjaxResult ajaxResult = null;
		try {
			noticeService.delete(notice);
			ajaxResult = AjaxResult.SUCCESS();
		} catch (Exception e) {
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(删除公告信息),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "删除公告信息");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			log.error("删除公告信息错误:"+e.getMessage());
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null, e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}

	public GwNoticeVO getNotice() {
		return notice;
	}

	public void setNotice(GwNoticeVO notice) {
		this.notice = notice;
	}
}
