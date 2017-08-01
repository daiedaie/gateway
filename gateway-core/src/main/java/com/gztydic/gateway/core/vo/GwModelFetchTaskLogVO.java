package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwModelFetchTaskLog entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwModelFetchTaskLogVO implements java.io.Serializable {

	// Fields

	private Long logId;
	private Long taskId;
	private Long userId;
	private Long modelId;
	private Long serviceId;
	private String action;
	private String logInfo;
	private Date createTime;

	// Constructors

	/** default constructor */
	public GwModelFetchTaskLogVO() {
	}

	/** full constructor */
	public GwModelFetchTaskLogVO(Long userId, Long modelId, String action, String logInfo,
			Date createTime) {
		this.userId = userId;
		this.modelId = modelId;
		this.action = action;
		this.logInfo = logInfo;
		this.createTime = createTime;
	}

	// Property accessors

	public Long getLogId() {
		return this.logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getModelId() {
		return this.modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getLogInfo() {
		return this.logInfo;
	}

	public void setLogInfo(String logInfo) {
		this.logInfo = logInfo;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
}