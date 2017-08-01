package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwFunc entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwProcessOperationVO implements java.io.Serializable {

	// Fields
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9047341275794628801L;
	private Long operateId;
	private Long processId;
	private Long userId;
	private String operateContent;
	private Date operateTime;
	private Long planId;
	private String userIdName;
	private String progressStatus;
	private String dealType;
	private String step;
	// Constructors

	/** default constructor */
	public GwProcessOperationVO() {
	}


	/** full constructor */
	public GwProcessOperationVO(Long processId,
			Long userId, String operateContent, Date operateTime,
			Long planId,String progressStatus,String dealType,String step) {
		super();
		this.processId = processId;
		this.userId = userId;
		this.operateContent = operateContent;
		this.operateTime = operateTime;
		this.planId = planId;
		this.progressStatus = progressStatus;		
		this.dealType = dealType;
		this.step=step;
	}
	
	public GwProcessOperationVO(Long processId,
			Long userId) {
		super();
		this.processId = processId;
		this.userId = userId;	
	}
	// Property accessors
	public Long getOperateId() {
		return operateId;
	}

	public void setOperateId(Long operateId) {
		this.operateId = operateId;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getOperateContent() {
		return operateContent;
	}

	public void setOperateContent(String operateContent) {
		this.operateContent = operateContent;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	public Long getPlanId() {
		return planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public String getUserIdName() {
		return userIdName;
	}

	public void setUserIdName(String userIdName) {
		this.userIdName = userIdName;
	}

	public String getProgressStatus() {
		return progressStatus;
	}

	public void setProgressStatus(String progressStatus) {
		this.progressStatus = progressStatus;
	}

	public String getDealType() {
		return dealType;
	}


	public void setDealType(String dealType) {
		this.dealType = dealType;
	}
	
	public String getStep() {
		return step;
	}


	public void setStep(String step) {
		this.step = step;
	}
}