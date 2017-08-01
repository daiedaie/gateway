package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwDesensitizationModelInfo entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwDesenServiceInfoVO implements java.io.Serializable {

	// Fields

	private Long infoDeseId;
	private Long serviceId;
	private Long userId;
	private String modelInfo;			//模型信息
	private String serviceInputInfo;	//服务输入信息
	private String createUser;
	private Date createTime;
	private String updateUser;
	private Date updateTime;

	// Constructors

	/** default constructor */
	public GwDesenServiceInfoVO() {
	}

	// Property accessors

	public Long getInfoDeseId() {
		return this.infoDeseId;
	}

	public void setInfoDeseId(Long infoDeseId) {
		this.infoDeseId = infoDeseId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getModelInfo() {
		return modelInfo;
	}

	public void setModelInfo(String modelInfo) {
		this.modelInfo = modelInfo;
	}

	public String getServiceInputInfo() {
		return serviceInputInfo;
	}

	public void setServiceInputInfo(String serviceInputInfo) {
		this.serviceInputInfo = serviceInputInfo;
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

	public String getUpdateUser() {
		return this.updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}