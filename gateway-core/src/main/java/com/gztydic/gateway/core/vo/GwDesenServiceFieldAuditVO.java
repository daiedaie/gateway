package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwDesensitizationModelField entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwDesenServiceFieldAuditVO implements java.io.Serializable {

	// Fields

	private Long fieldDeseId;
	private Long serviceId;
	private Long userId;
	private Long fieldId;
	private String ruleType;
	private String ruleContent;
	private String replaceContent;
	private String conditionType;
	private String conditionContent;
	private String createUser;
	private Date createTime;
	private String updateUser;
	private Date updateTime;
	private String checkType;
	private String checkRule;
	private Long batch;

	private String fieldCode;
	private String dictCode;
	private String reorder;
	private String fieldName;
	private String fieldType;
	// Constructors

	/** default constructor */
	public GwDesenServiceFieldAuditVO() {
	}

	public Long getFieldDeseId() {
		return fieldDeseId;
	}

	public void setFieldDeseId(Long fieldDeseId) {
		this.fieldDeseId = fieldDeseId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getFieldId() {
		return fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String getRuleContent() {
		return ruleContent;
	}

	public void setRuleContent(String ruleContent) {
		this.ruleContent = ruleContent;
	}

	public String getReplaceContent() {
		return replaceContent;
	}

	public void setReplaceContent(String replaceContent) {
		this.replaceContent = replaceContent;
	}

	public String getConditionType() {
		return conditionType;
	}

	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}

	public String getConditionContent() {
		return conditionContent;
	}

	public void setConditionContent(String conditionContent) {
		this.conditionContent = conditionContent;
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

	public String getDictCode() {
		return dictCode;
	}

	public void setDictCode(String dictCode) {
		this.dictCode = dictCode;
	}

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
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

	public String getReorder() {
		return reorder;
	}

	public void setReorder(String reorder) {
		this.reorder = reorder;
	}

	public Long getBatch() {
		return batch;
	}

	public void setBatch(Long batch) {
		this.batch = batch;
	}
}