package com.gztydic.gateway.core.view;

import java.util.Date;

/**
 * GwModelLiabilityLog entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwModelLiabilityLogView implements java.io.Serializable {

	// Fields

	private Long logId;
	private Long modelId;
	private String modelCode;
	private String modelName;
	private Long taskId;
	private Long userId;
	private String userName;
	private String loginName;
	private Long orgId;
	private String orgName;
	private String orgLoginName;	//机构用户登录帐号
	private String modelFields;
	private Long modelDataNum;
	private String modelVersion;
	private String serviceName;
	private String createUser;
	private Date createTime;
	private String downloadTime;
	private String downloadStartTime;
	private String downloadEndTime;
	private String serviceType;
	private String cycleType;
	private String cycleDay;
	private String serviceRemark;
	private String modelType;
	private String startTime;
	private String modelDesc;
	private String serviceCode;
	private String desenRuleContent;
	private String algType;
	private String algRule;
	private Long fieldNum;

	// Constructors

	/** default constructor */
	public GwModelLiabilityLogView() {
	}

	/** minimal constructor */
	public GwModelLiabilityLogView(Long userId, String modelFields,
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(String downloadTime) {
		this.downloadTime = downloadTime;
	}

	public String getDownloadStartTime() {
		return downloadStartTime;
	}

	public void setDownloadStartTime(String downloadStartTime) {
		this.downloadStartTime = downloadStartTime;
	}

	public String getDownloadEndTime() {
		return downloadEndTime;
	}

	public void setDownloadEndTime(String downloadEndTime) {
		this.downloadEndTime = downloadEndTime;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getModelVersion() {
		return modelVersion;
	}

	public void setModelVersion(String modelVersion) {
		this.modelVersion = modelVersion;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getCycleType() {
		return cycleType;
	}

	public void setCycleType(String cycleType) {
		this.cycleType = cycleType;
	}

	public String getCycleDay() {
		return cycleDay;
	}

	public void setCycleDay(String cycleDay) {
		this.cycleDay = cycleDay;
	}

	public String getServiceRemark() {
		return serviceRemark;
	}

	public void setServiceRemark(String serviceRemark) {
		this.serviceRemark = serviceRemark;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getModelDesc() {
		return modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getDesenRuleContent() {
		return desenRuleContent;
	}

	public void setDesenRuleContent(String desenRuleContent) {
		this.desenRuleContent = desenRuleContent;
	}

	public String getAlgType() {
		return algType;
	}

	public void setAlgType(String algType) {
		this.algType = algType;
	}

	public String getAlgRule() {
		return algRule;
	}

	public void setAlgRule(String algRule) {
		this.algRule = algRule;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getOrgLoginName() {
		return orgLoginName;
	}

	public void setOrgLoginName(String orgLoginName) {
		this.orgLoginName = orgLoginName;
	}

	public Long getFieldNum() {
		return fieldNum;
	}

	public void setFieldNum(Long fieldNum) {
		this.fieldNum = fieldNum;
	}
}