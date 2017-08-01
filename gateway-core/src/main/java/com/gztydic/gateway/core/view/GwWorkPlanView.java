package com.gztydic.gateway.core.view;

import java.util.Date;

public class GwWorkPlanView {

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
	private Long parentPlanId;
	private String reason;
	private String loginName;
	private String userName;
	public GwWorkPlanView(){
		
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
	public Long getParentPlanId() {
		return parentPlanId;
	}
	public void setParentPlanId(Long parentPlanId) {
		this.parentPlanId = parentPlanId;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
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
	
}
