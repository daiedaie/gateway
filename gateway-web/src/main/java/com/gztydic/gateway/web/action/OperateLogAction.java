package com.gztydic.gateway.web.action;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.gztydic.gateway.core.common.constant.GwUserType;
import com.gztydic.gateway.core.common.util.DateUtil;
import com.gztydic.gateway.core.common.util.PageObject;
import com.gztydic.gateway.system.OperationLogService;
import com.gztydic.gateway.web.action.base.BaseAction;

/** 
 * @ClassName: OperateLogAction 
 * @Description: TODO(系统操作日志相关页面请求控制类) 
 * @author davis
 * @date 2014-11-27 下午12:13:54 
 *  
 */
@Controller
@Scope("prototype")
public class OperateLogAction extends BaseAction{

	private static final long serialVersionUID = 1L;
	
	private String operUserCode;
	
	private String acceptUserCode;
	
	private String operateType;
	
	private String startDate;
	
	private String endDate;
	
	@Resource(name="operationLogServiceImpl")
	private OperationLogService operationLogService;
	
		
	public String getLogList() throws Exception{
		if(getLoginUser() != null){
			String userType = getLoginUser().getUserType();
			
			//只有管理用户可以查询所有日志，其他用户不能查看
			if(GwUserType.AUDIT_USER.equals(userType) || GwUserType.SUPER_USER.equals(userType) || GwUserType.SAFE_USER.equals(userType) || GwUserType.MAINATE_USER.equals(userType)){
				if(pageObject == null) {
					pageObject = new PageObject();
				}
				pageObject = operationLogService.searchAllLog(pageObject);
			}
		}
		return "logList";
	}
	
	public String searchLogList() throws Exception{
		if(getLoginUser() != null){
			String userType = getLoginUser().getUserType();
			
			//只有管理用户可以查询所有日志，其他用户不能查看
			if(GwUserType.AUDIT_USER.equals(userType) || GwUserType.SUPER_USER.equals(userType) || GwUserType.SAFE_USER.equals(userType) || GwUserType.MAINATE_USER.equals(userType)){
				if(pageObject == null) {
					pageObject = new PageObject();
				}
				String endDateStr = endDate;
				if( !StringUtils.isBlank(endDateStr) ){
					endDateStr =  endDateStr+" 23:59:59";
				}
				
				pageObject = operationLogService.searchAllLog(operUserCode,acceptUserCode,operateType,DateUtil.StringTODate(startDate),DateUtil.StringTODate5(endDateStr),pageObject);
			}
		}
		return "logList";
	}


	public String getOperUserCode() {
		return operUserCode;
	}


	public void setOperUserCode(String operUserCode) {
		this.operUserCode = operUserCode;
	}


	public String getAcceptUserCode() {
		return acceptUserCode;
	}


	public void setAcceptUserCode(String acceptUserCode) {
		this.acceptUserCode = acceptUserCode;
	}


	public String getOperateType() {
		return operateType;
	}


	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}



}
