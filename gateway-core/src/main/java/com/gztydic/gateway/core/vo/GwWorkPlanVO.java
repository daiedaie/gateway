package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwWorkPlan entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwWorkPlanVO implements java.io.Serializable {

	// Fields

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
	private Long smsId;
	private String processId;
	// Constructors

	/** default constructor */
	public GwWorkPlanVO() {
	}

	/** full constructor */
	public GwWorkPlanVO(String planTitle, String planType, String planContent,
			String planState, String planLevel, String extenTableKey,
			Date createTime, Long createUserId, Date daelTime, Long dealUserId,Long parentPlanId,Long smsId,String processId) {
		this.planTitle = planTitle;
		this.planType = planType;
		this.planContent = planContent;
		this.planState = planState;
		this.planLevel = planLevel;
		this.extenTableKey = extenTableKey;
		this.createTime = createTime;
		this.createUserId = createUserId;
		this.daelTime = daelTime;
		this.dealUserId = dealUserId;
		this.parentPlanId = parentPlanId;
		this.smsId=smsId;
		this.processId=processId;
	}

	// Property accessors

	public Long getPlanId() {
		return this.planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public String getPlanTitle() {
		return this.planTitle;
	}

	public void setPlanTitle(String planTitle) {
		this.planTitle = planTitle;
	}

	public String getPlanType() {
		return this.planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public String getPlanContent() {
		return this.planContent;
	}

	public void setPlanContent(String planContent) {
		this.planContent = planContent;
	}

	public String getPlanState() {
		return this.planState;
	}

	public void setPlanState(String planState) {
		this.planState = planState;
	}

	public String getPlanLevel() {
		return this.planLevel;
	}

	public void setPlanLevel(String planLevel) {
		this.planLevel = planLevel;
	}

	public String getExtenTableKey() {
		return this.extenTableKey;
	}

	public void setExtenTableKey(String extenTableKey) {
		this.extenTableKey = extenTableKey;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getCreateUserId() {
		return this.createUserId;
	}

	public void setCreateUserId(Long createUserId) {
		this.createUserId = createUserId;
	}

	public Date getDaelTime() {
		return this.daelTime;
	}

	public void setDaelTime(Date daelTime) {
		this.daelTime = daelTime;
	}

	public Long getDealUserId() {
		return this.dealUserId;
	}

	public void setDealUserId(Long dealUserId) {
		this.dealUserId = dealUserId;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
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

	public Long getSmsId() {
		return smsId;
	}

	public void setSmsId(Long smsId) {
		this.smsId = smsId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}
}