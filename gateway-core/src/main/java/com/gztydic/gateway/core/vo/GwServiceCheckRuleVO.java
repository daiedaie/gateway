package com.gztydic.gateway.core.vo;


/**
 * GwServiceField entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwServiceCheckRuleVO implements java.io.Serializable {

	// Fields

	private Long checkId;
	private Long checkBatch;
	private Long userId;
	private Long serviceId;
	private Long reorder;
	private String fieldCode;
	private String fieldName;
	private String fieldType;
	private String checkType;
	private String checkRule;
	
	// Constructors

	/** default constructor */
	public GwServiceCheckRuleVO() {
	}

	public Long getCheckId() {
		return checkId;
	}

	public void setCheckId(Long checkId) {
		this.checkId = checkId;
	}

	public Long getCheckBatch() {
		return checkBatch;
	}

	public void setCheckBatch(Long checkBatch) {
		this.checkBatch = checkBatch;
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

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}

	public String getCheckRule() {
		return checkRule;
	}

	public void setCheckRule(String checkRule) {
		this.checkRule = checkRule;
	}

	public Long getReorder() {
		return reorder;
	}

	public void setReorder(Long reorder) {
		this.reorder = reorder;
	}
}