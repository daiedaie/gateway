package com.gztydic.gateway.core.view;

import java.io.Serializable;
import java.util.Date;

/**
 * GwFunc entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwProcessView implements Serializable {

	// Fields

	private Long processId;
	private String processType;
	private Date createTime;
	private Date endTime;
	private String status;
	private String progressStatus;

	// Constructors
	
	/** default constructor */
	public GwProcessView() {
	}
	
	// Property accessors

	public Long getProcessId() {
		return processId;
	}	

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProgressStatus() {
		return progressStatus;
	}

	public void setProgressStatus(String progressStatus) {
		this.progressStatus = progressStatus;
	}
	
}