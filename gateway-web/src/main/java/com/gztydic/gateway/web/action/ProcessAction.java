package com.gztydic.gateway.web.action;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.constant.OperateTypeConstent;
import com.gztydic.gateway.core.common.util.AjaxResult;
import com.gztydic.gateway.core.common.util.AppHelper;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.core.dao.GwSmsDAO;
import com.gztydic.gateway.core.dao.GwUserButtonDAO;
import com.gztydic.gateway.core.dao.GwUserDAO;
import com.gztydic.gateway.core.vo.GwProcessOperationVO;
import com.gztydic.gateway.core.vo.GwProcessVO;
import com.gztydic.gateway.core.vo.GwSmsVO;
import com.gztydic.gateway.core.vo.GwUserVO;
import com.gztydic.gateway.system.OperationLogService;
import com.gztydic.gateway.system.ProcessOperationService;
import com.gztydic.gateway.system.ProcessService;
import com.gztydic.gateway.system.UserService;
import com.gztydic.gateway.web.action.base.BaseAction;

@Controller
@Scope("prototype")
public class ProcessAction extends BaseAction{
	
	private static final long serialVersionUID = 1L;
	private String processType;
	private String processStatus;
	private String status;
	private String processId;
	private String stepStatus;
	@Resource(name="processServiceImpl")
	private ProcessService processService;
	
	@Resource(name="processOperationServiceImpl")
	private ProcessOperationService processOperationService;
	@Resource(name = "userServiceImpl")
	private UserService userService;
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	@Resource
	private GwSmsDAO gwSmsDAO;
	/** 
	 * @Title: searchProcessList 
	 * @Description: TODO(流程进度列表查看) 
	 * @param @param processType 流程类型
	 * @param @param status 流程状态 
	 * @return void    返回类型 
	 * @throws 
	 */
	public String searchProcessList() throws Exception{
		if(getLoginUser() != null){
			String userType = getLoginUser().getUserType();
			String pType = "";
			String pStatus = "";
			if(pageObject == null) {
				pageObject = new PageObject();
			}
			if( !StringUtils.isBlank(processType) ){
				pType = processType;
			}
			if( !StringUtils.isBlank(status) ){
				pStatus = status;
			}
			if(GwUserType.ORG_USER.equals(userType) || GwUserType.DATA_USER.equals(userType)){
				//List list = processOperationService.searchProcessByUserId(getLoginUser().getUserId());
				PageObject pageObject2=processService.searchProcessByUserId(getLoginUser().getUserId(), pageObject,pType,pStatus);
				
				/*if(list != null && list.size() > 0){
					for(int i = 0; i < list.size(); i++){
						processId = Long.valueOf(list.get(i).toString());
						List process = processService.searchUserProcess(processId, pType, pStatus, pageObject);
						if(process != null && process.size() > 0){
							processIds.add(process.get(0));
						}
					}
				}*/
				//写操作日志
				operationLogService.saveOperationLog(getLoginUser().getLoginName(), getLoginUser().getLoginName(), OperateTypeConstent.SEARCH_PLAN, "查询自己已办流程列表，数据量："+pageObject.getDataCount()+"条。");
				
				request.setAttribute("pageObject", pageObject2);
			}else if(GwUserType.MAINATE_USER.equals(userType) || GwUserType.AUDIT_USER.equals(userType)
					 || GwUserType.SAFE_USER.equals(userType) || GwUserType.SUPER_USER.equals(userType)){
				pageObject = processService.searchAllProcess(pType, pStatus, pageObject);
				//写操作日志
				operationLogService.saveOperationLog(getLoginUser().getLoginName(), getLoginUser().getLoginName(), OperateTypeConstent.SEARCH_PLAN, "查询数据和机构用户已办流程列表，数据量："+pageObject.getDataCount()+"条。");
				
				request.setAttribute("pageObject", pageObject);
			}
		}
		return "processList";
	}
	/** 
	 * @Title: searchProcessOperation 
	 * @Description: TODO(流程进度列表查看) 
	 * @param @param processType 流程类型
	 * @param @param status 流程状态 
	 * @return void    返回类型 
	 * @throws 
	 */
	public String searchProcessOperation() throws Exception{
		if(getLoginUser() != null){
			String userType = getLoginUser().getUserType();
			String pId = "";
			String pType = "";
			String procesStatus = "";//流程步骤状态
			String status = "";//流程状态
			Boolean showNote = false;
			String stepStatus = request.getParameter("stepStatus");
			//只有运维人员才能督促
			if(GwUserType.MAINATE_USER.equals(userType)){
				showNote = true;
			}
			request.setAttribute("showNote", showNote);
			if(pageObject == null) {
				pageObject = new PageObject();
			}
			if( !StringUtils.isBlank(processId) ){
				pId =  processId;
			}
			
			pageObject = processOperationService.searchProcessOperation(Long.valueOf(pId),pageObject);
			//写操作日志
			//operationLogService.saveOperationLog(getLoginUser().getLoginName(), getLoginUser().getLoginName(), OperateTypeConstent.SEARCH_PLAN, "查询自己已办流程进度详情："+pageObject.getDataCount()+"条。");
			List<GwProcessOperationVO> list = pageObject.getData();
			List<GwProcessOperationVO>  voList = new ArrayList<GwProcessOperationVO>();
			if(list != null && list.size() > 0){
				for ( GwProcessOperationVO vo : list) {
					GwProcessOperationVO processOperationVo = new GwProcessOperationVO();
					BeanUtils.copyProperties(vo, processOperationVo);
					Long userId = vo.getUserId();
					if(vo.getDealType().equals(GwUserType.DATA_USER)&&userId != null){
						processOperationVo.setUserIdName(userService.searchUserDetail(vo.getUserId()).getUserName());
					}else if(vo.getDealType().equals(GwUserType.SAFE_USER)){
						processOperationVo.setUserIdName("数据安全管理员");
					}else if(vo.getDealType().equals(GwUserType.AUDIT_USER)){
						processOperationVo.setUserIdName("审核人员");
					}else{
						processOperationVo.setUserIdName("system");
					}				
					voList.add(processOperationVo);						
				}
			}				
			request.setAttribute("voList", voList);
			if( !StringUtils.isBlank(processType) ){
				pType = processType;
				request.setAttribute("pType", pType);
			}
			if( !StringUtils.isBlank(processStatus) ){
				procesStatus = new String( processStatus.getBytes("iso-8859-1"), "UTF-8");
				request.setAttribute("procesStatus", procesStatus);
			}
			if( !StringUtils.isBlank(status) ){
				status = new String( status.getBytes("iso-8859-1"), "UTF-8");
				request.setAttribute("status", status);
			}
			if( !StringUtils.isBlank(stepStatus) ){
				stepStatus = new String( stepStatus.getBytes("iso-8859-1"), "UTF-8");
				request.setAttribute("stepStatus", stepStatus);
			}
		}
		return "processOperation";
	}
	
	/** 
	 * @Title: noteHandleProcess 
	 * @Description: TODO(督促操作员处理待办任务) 
	 * @param @param processType 流程类型
	 * @param @param status 流程状态 
	 * @return void    返回类型 
	 * @throws 
	 */
	public void noteHandleProcess() throws Exception{
		AjaxResult ajaxResult = null;
		try {
			//String dealUserId = request.getParameter("dealUserId");
			String userType = request.getParameter("dealType");
			List<GwUserVO> list = userService.searchOnlineUser(userType);			
			for(int i=0;i<list.size();i++){
				GwUserVO userVO = list.get(i);
				GwSmsVO gwSmsVO=new GwSmsVO();
				gwSmsVO.setSendStatus(null);
				gwSmsVO.setSendResult(null);
				gwSmsVO.setSendCount(0);
				gwSmsVO.setCreateTime(new Date());
				gwSmsVO.setSendTime(null);
				gwSmsVO.setSmsContent("用户："+getLoginUser().getLoginName()+"督促了任务，请及时处理【数据网关平台】");
				String moblieStr="";
				moblieStr=userVO.getMoblie();
				gwSmsVO.setSmsMobile(moblieStr);
				gwSmsDAO.saveOrUpdate(gwSmsVO);
			}
			ajaxResult = AjaxResult.SUCCESS(null,"督促成功!");
		}catch (Exception e) {
			e.printStackTrace();
			ajaxResult = AjaxResult.ERROR(null,"督促失败！原因："+e.getMessage());
		}
		
		AppHelper.writeOut(ajaxResult, response);
	}
	
	public String getProcessType() {
		return processType;
	}
	public void setProcessType(String processType) {
		this.processType = processType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getProcessStatus() {
		return processStatus;
	}
	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}
	
	
}
