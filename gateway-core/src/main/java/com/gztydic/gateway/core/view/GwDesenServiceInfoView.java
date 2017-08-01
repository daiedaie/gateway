package com.gztydic.gateway.core.view;


public class GwDesenServiceInfoView implements java.io.Serializable {

	private Long userId;
	private String loginName;
	private String userName;
	private Long modelId;
	private Long serviceId;
	private String serviceCode;
	private String serviceName;
	private String serviceType;
	private String cycleType;
	private Long cycleDay;
	private String modelCode;
	private String modelName;
	private Long infoDeseId;
	private String modelInfo;	//模型信息权限
	private String serviceInputInfo;	//服务输入权限
	
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
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
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getModelCode() {
		return modelCode;
	}
	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public Long getInfoDeseId() {
		return infoDeseId;
	}
	public void setInfoDeseId(Long infoDeseId) {
		this.infoDeseId = infoDeseId;
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
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getModelId() {
		return modelId;
	}
	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}
	public String getCycleType() {
		return cycleType;
	}
	public void setCycleType(String cycleType) {
		this.cycleType = cycleType;
	}
	public Long getCycleDay() {
		return cycleDay;
	}
	public void setCycleDay(Long cycleDay) {
		this.cycleDay = cycleDay;
	}
}