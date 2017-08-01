package com.gztydic.gateway.core.vo;

/**
 * GwCheckRule entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwCheckRuleVO implements java.io.Serializable {

	// Fields

	private Long ruleId;
	private Long fieldId;
	private Long serviceId;
	private Long userId;
	private String fieldType;
	private String checkType;
	private String checkRule;
	private String fieldCode;

	// Constructors

	/** default constructor */
	public GwCheckRuleVO() {
	}

	/** full constructor */
	public GwCheckRuleVO(Long fieldId, Long serviceId, String fieldType,
			String checkType, String checkRule) {
		this.fieldId = fieldId;
		this.serviceId = serviceId;
		this.fieldType = fieldType;
		this.checkType = checkType;
		this.checkRule = checkRule;
	}

	// Property accessors

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}
	
	public Long getFieldId() {
		return this.fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	public Long getServiceId() {
		return this.serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getFieldType() {
		return this.fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getCheckType() {
		return this.checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	public String getCheckRule() {
		return this.checkRule;
	}

	public void setCheckRule(String checkRule) {
		this.checkRule = checkRule;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}
	
}