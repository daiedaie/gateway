package com.gztydic.gateway.web.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.ServletActionContext;

import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.SessionConstant;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.FuncAuthUtil;
import com.gztydic.gateway.core.vo.GwFuncVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.UserAuthService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AuthInterceptor extends AbstractInterceptor {
	
	private final Log log = LogFactory.getLog(AuthInterceptor.class);
	
	@Resource(name="userAuthServiceImpl")
	private UserAuthService userAuthService;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;

	public String intercept(ActionInvocation invocation) throws Exception {
		HttpServletRequest request = (HttpServletRequest)invocation.getInvocationContext().get(ServletActionContext.HTTP_REQUEST);
		String url = request.getServletPath().substring(1);	//取得访问连接并去掉url中的第一个 /
		
		if(FuncAuthUtil.menuMap==null || FuncAuthUtil.menuAuthMap==null){
			userAuthService.initFuncData();
		}
		
		GwFuncVO funcVO = FuncAuthUtil.menuMap.get(url);
		if(funcVO != null){//访问的是菜单权限管理中的url
			GwUserVO userVO = (GwUserVO)request.getSession().getAttribute(SessionConstant.SESSION_ATTRIBUTE_USER_INFO);
			if(!GwUserType.SUPER_USER.equals(userVO.getUserType())){
				String funcCode = FuncAuthUtil.menuAuthMap.get(userVO.getUserType()+"_"+funcVO.getFuncCode());
				if(funcCode==null){
					String sms="系统后台错误:操作人("+userVO.getLoginName()+"),操作功能(待办查询),原因:您没有权限操作该页面【数据服务网关】";
					GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "访问页面时发生错误！原因:没有权限操作该页面", WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
					Map map=new HashMap();
					map.put("userId",String.valueOf(userVO.getUserId()));
					map.put("operFun", "待办查询");
					workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
					log.error("无权限，已拒绝用户"+userVO.getLoginName()+"访问"+url+"页面");
					throw new Exception("抱歉，您没有权限操作该页面");
				}
			}
		}
		return invocation.invoke();
	}
}
