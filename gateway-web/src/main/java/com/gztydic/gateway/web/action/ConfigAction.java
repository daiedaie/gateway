package com.gztydic.gateway.web.action;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.expr.NewArray;

import javax.annotation.Resource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.config.ConfigConstants;
import com.gztydic.gateway.core.common.constant.CommonState;
import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.AjaxResult;
import com.gztydic.gateway.core.common.util.AppHelper;
import com.gztydic.gateway.core.vo.GwSysCnfigVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.ConfigService;
import com.gztydic.gateway.system.OperationLogService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

@Controller
@Scope("prototype")
public class ConfigAction extends BaseAction {
	
	@Resource(name="configServiceImpl")
	private ConfigService configService;
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	private String hourStr;
	private String minuteStr;
	private GwSysCnfigVO rePushTimeCnfigVO;
	private GwSysCnfigVO taskCheckTimeCnfigVO;
	private GwSysCnfigVO fileScanTimeCnfigVO;
	private GwSysCnfigVO smsCountCnfigVO;
	
	public GwSysCnfigVO getRePushTimeCnfigVO() {
		return rePushTimeCnfigVO;
	}

	public void setRePushTimeCnfigVO(GwSysCnfigVO rePushTimeCnfigVO) {
		this.rePushTimeCnfigVO = rePushTimeCnfigVO;
	}

	public GwSysCnfigVO getTaskCheckTimeCnfigVO() {
		return taskCheckTimeCnfigVO;
	}

	public void setTaskCheckTimeCnfigVO(GwSysCnfigVO taskCheckTimeCnfigVO) {
		this.taskCheckTimeCnfigVO = taskCheckTimeCnfigVO;
	}

	public GwSysCnfigVO getFileScanTimeCnfigVO() {
		return fileScanTimeCnfigVO;
	}

	public void setFileScanTimeCnfigVO(GwSysCnfigVO fileScanTimeCnfigVO) {
		this.fileScanTimeCnfigVO = fileScanTimeCnfigVO;
	}

	public void updateJobTimer()throws Exception{
		AjaxResult ajaxResult = null;
		try {
			GwUserVO loginUser = getLoginUser();
			String logInfo = loginUser.getLoginName()+"配置定时器！时间："+hourStr+"."+minuteStr;
			/*minuteStr=String.valueOf(Math.round((Double.parseDouble(minuteStr)/60*100)));*/
			String times = hourStr+"."+minuteStr;
			configService.updateJobTimer(Long.parseLong(ConfigConstants.DATA_FETCH_TASK_JOB_NO), "GW_CREATE_DATA_TASK;", times);
			rePushTimeCnfigVO.setConfigType(CommonState.CONFIG_REPUSH_INTERVAL);
			taskCheckTimeCnfigVO.setConfigType(CommonState.TASK_CHECK_TIME_INTERVAL);
			fileScanTimeCnfigVO.setConfigType(CommonState.FILE_SACN_INTERVAL);
			configService.saveOrUpdateConfig(rePushTimeCnfigVO);
			configService.saveOrUpdateConfig(taskCheckTimeCnfigVO);
			configService.saveOrUpdateConfig(fileScanTimeCnfigVO);
			ajaxResult = AjaxResult.SUCCESS(null,"修改定时器成功！");
			
			//写操作日志
			operationLogService.saveOperationLog(loginUser.getLoginName(), loginUser.getLoginName(), OperateTypeConstent.TIMER_CONF, logInfo);
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(配置定时器),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "修改定时器时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "配置定时器");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null,"修改定时器发生错误！原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	public String searchNextDate()throws Exception{
		try{
			List<String> list = configService.searchNextDate(Long.parseLong(ConfigConstants.DATA_FETCH_TASK_JOB_NO));
			String nextDate=list.get(0).toString();
			String interval=list.get(1).toString();
			String[] intervalArray = interval.split("\\+");
			String[] timerArray = intervalArray[1].split("/"); // 2.1/24
			
			
			int allMis = Integer.valueOf(timerArray[0]);
			int hour = allMis/60;
			int mis = allMis%60;
			String[] hourArray = timerArray[0].split("\\.");
			/*String hour = hourArray[0];
			String minute = hourArray[1];*/
			//String[] hourArray = timerArray[0].split("\\.");   // 2.1 or 2
//			String hour = hourArray[0].equals("")?"0":hourArray[0];
			/*String minute = hourArray.length>=1?String.valueOf(Math.round((Double.parseDouble(hourArray[1])/100*60))):"0";*/
//			String minute = hourArray.length>=1?hourArray[1]:"0";
			request.setAttribute("nextDate", nextDate);
			request.setAttribute("hour", hour);
			request.setAttribute("minute", mis);
			/*request.setAttribute("minute", minute);*/
			
			//重新推送时间间隔、任务脱敏/检查时间间隔、源文件扫描时间间隔
			GwSysCnfigVO rePushTimeCnfigVO=configService.findByConfigType(CommonState.CONFIG_REPUSH_INTERVAL);
			GwSysCnfigVO taskCheckTimeCnfigVO=configService.findByConfigType(CommonState.TASK_CHECK_TIME_INTERVAL);
			GwSysCnfigVO fileScanTimeCnfigVO=configService.findByConfigType(CommonState.FILE_SACN_INTERVAL);
			request.setAttribute("rePushTimeCnfigVO", rePushTimeCnfigVO);
			request.setAttribute("taskCheckTimeCnfigVO", taskCheckTimeCnfigVO);
			request.setAttribute("fileScanTimeCnfigVO", fileScanTimeCnfigVO);
		}catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(配置定时器),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "查看定时器时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "定时器配置");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			throw e;
		}
		return "updateJobTimer";
	}
	
	public String searchRePushCount()throws Exception{
		GwSysCnfigVO gwSysCnfigVO=configService.findByConfigType(CommonState.CONFIG_REPUSH_COUNT);
		request.setAttribute("gwSysCnfigVO", gwSysCnfigVO);
		GwSysCnfigVO smsCountCnfigVO=configService.findByConfigType(CommonState.CONFIG_SMS_COUNT);
		request.setAttribute("smsCountCnfigVO", smsCountCnfigVO);
		return "rePushCountConfig";
	}
	
	public void saveRePushCount()throws Exception{
		AjaxResult ajaxResult = null;
		try {
			GwUserVO loginUser = getLoginUser();
			String logInfo = loginUser.getLoginName()+"配置重新推送最多次数！次数："+rePushTimeCnfigVO.getConfigValue()+";配置短信重复发送次数！次数："+smsCountCnfigVO.getConfigValue();
			rePushTimeCnfigVO.setConfigType(CommonState.CONFIG_REPUSH_COUNT);
			rePushTimeCnfigVO.setConfigUnit(CommonState.COUNT_UNIT);
			configService.saveOrUpdateConfig(rePushTimeCnfigVO);
			smsCountCnfigVO.setConfigType(CommonState.CONFIG_SMS_COUNT);
			smsCountCnfigVO.setConfigUnit(CommonState.COUNT_UNIT);
			configService.saveOrUpdateConfig(smsCountCnfigVO);
			ajaxResult = AjaxResult.SUCCESS(null,"配置重新推送最多次数,短信重复发送次数成功！");
			
			//写操作日志
			operationLogService.saveOperationLog(loginUser.getLoginName(), loginUser.getLoginName(), OperateTypeConstent.REPUSH_COUNT_CONF, logInfo);
		} catch (Exception e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(重发次数配置),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "系统后台错误：修改重新推送次数发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "重发次数配置");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null,"修改重发次数发生错误！原因："+e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}
	
	public String getHourStr() {
		return hourStr;
	}

	public void setHourStr(String hourStr) {
		this.hourStr = hourStr;
	}

	public String getMinuteStr() {
		return minuteStr;
	}

	public void setMinuteStr(String minuteStr) {
		this.minuteStr = minuteStr;
	}

	public GwSysCnfigVO getSmsCountCnfigVO() {
		return smsCountCnfigVO;
	}

	public void setSmsCountCnfigVO(GwSysCnfigVO smsCountCnfigVO) {
		this.smsCountCnfigVO = smsCountCnfigVO;
	}

	
}
