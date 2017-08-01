package com.gztydic.gateway.core.view;

import java.io.Serializable;
import java.util.Date;

/**
 * GwFunc entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwProcessOperationView implements Serializable {

	// Fields
	
	private Long operateId;
	private Long processId;
	private Long userId;
	private String operateContent;
	private Date operateTime;
	private Long planId;
	private String progressStatus;
	// Constructors

	/** default constructor */
	public GwProcessOperationView() {
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


	public String getProgressStatus() {
		return progressStatus;
	}


	public void setProgressStatus(String progressStatus) {
		this.progressStatus = progressStatus;
	}
	
}