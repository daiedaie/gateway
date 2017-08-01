package com.gztydic.gateway.core.view;

import java.util.Date;

public class WorkPlanView {
	private Long planId;
	private String planTitle;
	private String planType;
	private String planContent;
	private String planState;
	private String planLevel;
	private String extenTableKey;
	private Date createTime;
	private Long createUserId;
	private String suggestion;
	private Date daelTime;
	private Long dealUserId;
	private Long paramId;
	private String paramName;
	private String paramValue;
	private Long userId;
	private Long serviceId;
	private String planSource;
	private String processId;
	public String getPlanSource() {
		return planSource;
	}

	public void setPlanSource(String planSource) {
		this.planSource = planSource;
	}

	public WorkPlanView() {
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public Long getPlanId() {
		return planId;
	}
	public void setPlanId(Long planId) {
		this.planId = planId;
	}
	public String getPlanTitle() {
		return planTitle;
	}
	public void setPlanTitle(String planTitle) {
		this.planTitle = planTitle;
	}
	public String getPlanType() {
		return planType;
	}
	public void setPlanType(String planType) {
		this.planType = planType;
	}
	public String getPlanContent() {
		return planContent;
	}
	public void setPlanContent(String planContent) {
		this.planContent = planContent;
	}
	public String getPlanState() {
		return planState;
	}
	public void setPlanState(String planState) {
		this.planState = planState;
	}
	public String getPlanLevel() {
		return planLevel;
	}
	public void setPlanLevel(String planLevel) {
		this.planLevel = planLevel;
	}
	public String getExtenTableKey() {
		return extenTableKey;
	}
	public void setExtenTableKey(String extenTableKey) {
		this.extenTableKey = extenTableKey;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Long getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}
	public String getSuggestion() {
		return suggestion;
	}
	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}
	public Date getDaelTime() {
		return daelTime;
	}
	public void setDaelTime(Date daelTime) {
		this.daelTime = daelTime;
	}
	public Long getDealUserId() {
		return dealUserId;
	}
	public void setDealUserId(Long dealUserId) {
		this.dealUserId = dealUserId;
	}
	public Long getParamId() {
		return paramId;
	}
	public void setParamId(Long paramId) {
		this.paramId = paramId;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}
	

}
