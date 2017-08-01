package com.gztydic.gateway.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.constant.SessionConstant;
import com.gztydic.gateway.core.common.constant.WorkPlanConstent;
import com.gztydic.gateway.core.common.util.AjaxResult;
import com.gztydic.gateway.core.common.util.AppHelper;
import com.gztydic.gateway.core.vo.GwModelDataCycleVO;
import com.gztydic.gateway.core.vo.GwSysFtpVo;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.core.vo.GwWorkPlanVO;
import com.gztydic.gateway.system.DataCycleService;
import com.gztydic.gateway.system.LocalFtpConfigService;
import com.gztydic.gateway.system.OperationLogService;
import com.gztydic.gateway.system.WorkPlanParamService;
import com.gztydic.gateway.system.WorkPlanService;
import com.gztydic.gateway.web.action.base.BaseAction;

@Controller
@Scope("prototype")
public class LocalFtpConfigAction extends BaseAction{

		private static final long serialVersionUID = 2L;
	
		private GwSysFtpVo localUpVO;
		private GwSysFtpVo localDownVO;
		
		@Resource(name="localFtpConfigServiceImpl")
		private LocalFtpConfigService localFtpConfigService;
		@Resource(name="operationLogServiceImpl")
		private OperationLogService operationLogService;
		@Resource(name="workPlanServiceImpl")
		private WorkPlanService workPlanService;
		@Resource(name="workPlanParamServiceImpl")
		private WorkPlanParamService workPlanParamService;
		public GwUserVO getLoginUser(){
			return (GwUserVO)getSession().getAttribute(SessionConstant.SESSION_ATTRIBUTE_USER_INFO);
		}
		//本地Ftp配置页面
		public String FtpConfig() throws Exception{
			List<GwSysFtpVo> ftpList = localFtpConfigService.searchList();
			for (GwSysFtpVo vo : ftpList) {
				if(vo.getFtpType().equals("1")){	//上传
					localUpVO = vo;
					request.setAttribute("Id", vo.getId());	
				}else if(vo.getFtpType().equals("2")){	//下载
					localDownVO = vo;
					request.setAttribute("Id2", vo.getId());	
				}
			}
			return "localFtpConfig";
		}
		//本地FTP配置信息的新增或修改
		public void localFtpComfig() throws Exception{
			AjaxResult ajaxResult = null;
			try {
				List<GwSysFtpVo> gwSysFtp = new ArrayList<GwSysFtpVo>();
				String Id = request.getParameter("localUploadId");
				if (Id!=null&&Id!=""){
					localUpVO.setId(Long.parseLong(Id));//上传
				}
				String Id2 = request.getParameter("localDownloadId");
				if (Id!=null&&Id!=""){
					localDownVO.setId(Long.parseLong(Id2));//下载
				}
				localUpVO.setFtpType("1");//上传
				localDownVO.setFtpType("2");//下载
				gwSysFtp.add(localUpVO);
				gwSysFtp.add(localDownVO);
				localFtpConfigService.updateLocalFtpConfig(gwSysFtp, getLoginUser());
				ajaxResult = AjaxResult.SUCCESS();
				
				//写操作日志
				operationLogService.saveOperationLog(getLoginUser().getLoginName(), getLoginUser().getLoginName(), OperateTypeConstent.CYCLE_DATA, getLoginUser().getLoginName()+"修改周期配置！");
			} catch (Throwable e) {
				e.printStackTrace();
				String sms="系统后台错误:操作人("+getLoginUser().getLoginName()+"),操作功能(新增、修改本地FTP配置),原因:"+e.getMessage()+"【数据服务网关】";
				GwWorkPlanVO workPlan=workPlanService.saveWorkPlan("系统后台错误", WorkPlanConstent.SYSTEM_BACKSTAGE_ERROR, "本地FTP配置时发生错误！原因"+e.getMessage(), WorkPlanConstent.WAIT_FOR_DEAL, null, null, null, null, null,sms,null);
				Map map=new HashMap();
				map.put("userId",String.valueOf(getLoginUser().getUserId()));
				map.put("operFun", "新增、修改本地FTP配置");
				workPlanParamService.saveParamMap(workPlan.getPlanId(), map);
				ajaxResult = AjaxResult.ERROR(null,e.getMessage());
			}
			AppHelper.writeOut(ajaxResult, response);
		}
		public GwSysFtpVo getLocalUpVO() {
			return localUpVO;
		}
		public void setLocalUpVO(GwSysFtpVo localUpVO) {
			this.localUpVO = localUpVO;
		}
		public GwSysFtpVo getLocalDownVO() {
			return localDownVO;
		}
		public void setLocalDownVO(GwSysFtpVo localDownVO) {
			this.localDownVO = localDownVO;
		}
}
