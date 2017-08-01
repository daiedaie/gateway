package com.gztydic.gateway.core.vo;

/**
 * GwWorkPlanParam entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwWorkPlanParamVO implements java.io.Serializable {

	// Fields

	private Long paramId;
	private Long planId;
	private String paramName;
	private String paramValue;

	// Constructors

	/** default constructor */
	public GwWorkPlanParamVO() {
	}

	/** full constructor */
	public GwWorkPlanParamVO(Long planId, String paramName, String paramValue) {
		this.planId = planId;
		this.paramName = paramName;
		this.paramValue = paramValue;
	}

	// Property accessors

	public Long getParamId() {
		return this.paramId;
	}

	public void setParamId(Long paramId) {
		this.paramId = paramId;
	}

	public Long getPlanId() {
		return this.planId;
	}

	public void setPlanId(Long planId) {
		this.planId = planId;
	}

	public String getParamName() {
		return this.paramName;
	}

	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	public String getParamValue() {
		return this.paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

}