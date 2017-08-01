package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwService entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwServiceVO implements java.io.Serializable {

	// Fields

	private Long serviceId;
	private Long modelId;
	private String serviceSource;
	private String serviceCode;
	private String serviceName;
	private String serviceType;
	private String cycleType;
	private Long cycleDay;
	private String status;
	private String inputName;
	private String inputCode;
	private String inputDesc;
	private String outName;
	private String outCode;
	private String outDesc;
	private String remark;
	private String createUser;
	private Date createTime;
	private String updateUser;
	private Date updateTime;
	
	// Constructors

	/** default constructor */
	public GwServiceVO() {
	}

	/** minimal constructor */
	public GwServiceVO(String serviceCode, String serviceName,
			String serviceType, String cycleType, Long cycleDay) {
		this.serviceCode = serviceCode;
		this.serviceName = serviceName;
		this.serviceType = serviceType;
		this.cycleType = cycleType;
		this.cycleDay = cycleDay;
	}

	/** full constructor */
	public GwServiceVO(Long modelId, String serviceCode, String serviceName,
			String serviceType, String cycleType, Long cycleDay, String status,
			String remark) {
		this.modelId = modelId;
		this.serviceCode = serviceCode;
		this.serviceName = serviceName;
		this.serviceType = serviceType;
		this.cycleType = cycleType;
		this.cycleDay = cycleDay;
		this.status = status;
		this.remark = remark;
	}

	// Property accessors

	public Long getServiceId() {
		return this.serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public Long getModelId() {
		return this.modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public String getServiceCode() {
		return this.serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getServiceName() {
		return this.serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceType() {
		return this.serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getCycleType() {
		return this.cycleType;
	}

	public void setCycleType(String cycleType) {
		this.cycleType = cycleType;
	}

	public Long getCycleDay() {
		return this.cycleDay;
	}

	public void setCycleDay(Long cycleDay) {
		this.cycleDay = cycleDay;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getInputName() {
		return inputName;
	}

	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

	public String getInputDesc() {
		return inputDesc;
	}

	public void setInputDesc(String inputDesc) {
		this.inputDesc = inputDesc;
	}

	public String getOutName() {
		return outName;
	}

	public void setOutName(String outName) {
		this.outName = outName;
	}

	public String getOutDesc() {
		return outDesc;
	}

	public void setOutDesc(String outDesc) {
		this.outDesc = outDesc;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getInputCode() {
		return inputCode;
	}

	public void setInputCode(String inputCode) {
		this.inputCode = inputCode;
	}

	public String getOutCode() {
		return outCode;
	}

	public void setOutCode(String outCode) {
		this.outCode = outCode;
	}

	public String getServiceSource() {
		return serviceSource;
	}

	public void setServiceSource(String serviceSource) {
		this.serviceSource = serviceSource;
	}
}