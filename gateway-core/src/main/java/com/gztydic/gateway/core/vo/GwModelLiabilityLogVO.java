package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwModelLiabilityLog entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwModelLiabilityLogVO implements java.io.Serializable {

	// Fields

	private Long logId;
	private Long modelId;
	private Long serviceId;
	private Long taskId;
	private Long userId;
	private String modelFields;
	private String desenRuleContent;
	private Long modelDataNum;
	private String createUser;
	private Date createTime;

	// Constructors

	/** default constructor */
	public GwModelLiabilityLogVO() {
	}

	/** minimal constructor */
	public GwModelLiabilityLogVO(Long userId, String modelFields,
			Long modelDataNum, String createUser, Date createTime) {
		this.userId = userId;
		this.modelFields = modelFields;
		this.modelDataNum = modelDataNum;
		this.createUser = createUser;
		this.createTime = createTime;
	}

	// Property accessors

	public Long getLogId() {
		return this.logId;
	}

	public void setLogId(Long logId) {
		this.logId = logId;
	}

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
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

	public String getModelFields() {
		return this.modelFields;
	}

	public void setModelFields(String modelFields) {
		this.modelFields = modelFields;
	}

	public Long getModelDataNum() {
		return this.modelDataNum;
	}

	public void setModelDataNum(Long modelDataNum) {
		this.modelDataNum = modelDataNum;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
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

	public String getDesenRuleContent() {
		return desenRuleContent;
	}

	public void setDesenRuleContent(String desenRuleContent) {
		this.desenRuleContent = desenRuleContent;
	}

}