package com.gztydic.gateway.core.view;

import java.util.Date;

import com.gztydic.gateway.core.common.util.DateUtil;

public class UserModelServiceAppVO implements java.io.Serializable{
	private Long userId;
	private String loginName;
	private Long serviceId;
	private Long modelId;
	private String modelCode;
	private String serviceCode;
	private String serviceName;
	private String serviceType;
	private String fetchType;
	private String cycleType;
	private String cycleDay;
	private Long cycleNum;
	private String auditStatus;
	private Date  auditTime;
	private Long fetchId;
	private String serviceSource;
	public UserModelServiceAppVO(){
		
	}
	
	public String getServiceSource() {
		return serviceSource;
	}

	public void setServiceSource(String serviceSource) {
		this.serviceSource = serviceSource;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getModelCode() {
		return modelCode;
	}
	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	public Long getModelId() {
		return modelId;
	}
	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getFetchType() {
		return fetchType;
	}
	public void setFetchType(String fetchType) {
		this.fetchType = fetchType;
	}
	public String getCycleType() {
		return cycleType;
	}
	public void setCycleType(String cycleType) {
		this.cycleType = cycleType;
	}
	public Long getCycleNum() {
		return cycleNum;
	}
	public void setCycleNum(Long cycleNum) {
		this.cycleNum = cycleNum;
	}
	public String getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getCycleDay() {
		return cycleDay;
	}
	public void setCycleDay(String cycleDay) {
		this.cycleDay = cycleDay;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public Long getFetchId() {
		return fetchId;
	}

	public void setFetchId(Long fetchId) {
		this.fetchId = fetchId;
	}
	
	public String getAuditTimeStr() {
		return DateUtil.DateToString(auditTime, DateUtil.DATEFORMAT5);
	}
	
}
