package com.gztydic.gateway.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.SessionConstant;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.vo.GwDocHelpVO;
import com.gztydic.gateway.core.vo.GwNoticeVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.DocHelpService;
import com.gztydic.gateway.system.FuncService;
import com.gztydic.gateway.system.NoticeService;
import com.gztydic.gateway.system.UserButtonService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

@Controller
@Scope("prototype")
public class FuncAction extends BaseAction{
	
	private static final long serialVersionUID = 1L;
	
	@Resource(name="funcServiceImpl")
	private FuncService funcService;
	
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	
	@Resource(name="userButtonServiceImpl")
	private UserButtonService userButtonService;
	
	@Resource(name="noticeServiceImpl")
	private NoticeService noticeService;
	
	@Resource(name="docHelpServiceImpl")
	private DocHelpService docHelpService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	public String searchMenuList() throws Exception{
		try {
			Map parentFuncMap = (Map)getSession().getAttribute(SessionConstant.SESSION_ATTRIBUTE_USER_FUNC);
			if(parentFuncMap==null){
				parentFuncMap = funcService.searchMenuList(getLoginUser());
				getSession().setAttribute(SessionConstant.SESSION_ATTRIBUTE_USER_FUNC, parentFuncMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(询菜单查列表),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查询菜单列表时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "查询菜单查列表");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw e;
		}
		return "frameMenu";
	}
	
	/** 
	 * @Title: searchMain 
	 * @Description: TODO(查询主页显示内容) 
	 * @param @return
	 * @param @throws Exception    设定文件 
	 * @return String    返回类型 
	 * @throws 
	 */
	public String searchMain() throws Exception{
		GwUserVO userVO = getLoginUser();
		//查询待办信息
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
				workList = workPlanService.searchUserWorkPlan(userVO.getUserId());
			}
			request.setAttribute("workList", workList);
			
			int maxRow = 5;
			
			//公告信息
			List<GwNoticeVO> noticeList = noticeService.searchNoticeList(maxRow);
			request.setAttribute("noticeList", noticeList);
			
			//公告信息
			List<GwDocHelpVO> docList = docHelpService.searchDocHelpList(maxRow);
			request.setAttribute("docList", docList);
		}
		return "frameMain";
	}
}
