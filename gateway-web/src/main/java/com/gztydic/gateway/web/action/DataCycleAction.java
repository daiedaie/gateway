package com.gztydic.gateway.web.action;

import java.util.ArrayList;
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
import com.gztydic.gateway.core.vo.GwModelDataCycleVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.DataCycleService;
import com.gztydic.gateway.system.OperationLogService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

@Controller
@Scope("prototype")
public class DataCycleAction extends BaseAction{
	
	private static final long serialVersionUID = 1L;

	private GwModelDataCycleVO sourceCycleVO;
	private GwModelDataCycleVO resultCycleVO;
	
	@Resource(name="dataCycleServiceImpl")
	private DataCycleService dataCycleService;
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	@Resource(name="workPlanServiceImpl")
	private WorkPlanService workPlanService;
	@Resource(name="workPlanParamServiceImpl")
	private WorkPlanParamService workPlanParamService;
	
	//查询周期配置列表
	public String searchList() throws Exception{
		List<GwModelDataCycleVO> cycleList = dataCycleService.searchList();
		for (GwModelDataCycleVO vo : cycleList) {
			if(vo.getDataType().equals("1")){	//原始数据
				sourceCycleVO = vo;
			}else if(vo.getDataType().equals("2")){	//结果数据
				resultCycleVO = vo;
			}
		}
		return "dataCycleConfig";
	}
	
	//新增、修改周期配置
	public void updateDataCycleList() throws Exception{
		AjaxResult ajaxResult = null;
		try {
			List<GwModelDataCycleVO> cycleList = new ArrayList<GwModelDataCycleVO>();
			cycleList.add(sourceCycleVO);
			cycleList.add(resultCycleVO);
			dataCycleService.updateDataCycleList(cycleList, getLoginUser());
			ajaxResult = AjaxResult.SUCCESS();
			
			//写操作日志
			operationLogService.saveOperationLog(getLoginUser().getLoginName(), getLoginUser().getLoginName(), OperateTypeConstent.CYCLE_DATA, getLoginUser().getLoginName()+"修改周期配置！");
		} catch (Throwable e) {
			e.printStackTrace();
			String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(新增、修改周期配置),原因:"+e.getMessage()+"【数据服务网关】";
			GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "周期配置时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
			Map map=new HashMap();
			map.put("userId",String.valueOf(getLoginUser().getUserId()));
			map.put("operFun", "新增、修改周期配置");
			workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
			ajaxResult = AjaxResult.ERROR(null,e.getMessage());
		}
		AppHelper.writeOut(ajaxResult, response);
	}

	public GwModelDataCycleVO getSourceCycleVO() {
		return sourceCycleVO;
	}

	public void setSourceCycleVO(GwModelDataCycleVO sourceCycleVO) {
		this.sourceCycleVO = sourceCycleVO;
	}

	public GwModelDataCycleVO getResultCycleVO() {
		return resultCycleVO;
	}

	public void setResultCycleVO(GwModelDataCycleVO resultCycleVO) {
		this.resultCycleVO = resultCycleVO;
	}

}
