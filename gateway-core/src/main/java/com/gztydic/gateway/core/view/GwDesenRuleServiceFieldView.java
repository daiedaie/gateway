package com.gztydic.gateway.core.view;

import java.util.List;


/**
 * GwDesensitizationModelInfo entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwDesenRuleServiceFieldView implements java.io.Serializable {

	// Fields
	private Long fieldId;
	private Long serviceId;
	private Long userId;
	private String fieldCode;
	private String fieldName;
	private String fieldType;
	private String fieldDesc;
	private Long fieldDeseId;
	private String ruleType;
	private String ruleContent;
	private String replaceContent;
	private String conditionType;
	private String conditionContent;
	private String desenType;
	private String checkType;
	private String checkRule;
	
	private List<GwServiceDictView> dictList;
	
	public Long getFieldId() {
		return fieldId;
	}
	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
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
	public Long getFieldDeseId() {
		return fieldDeseId;
	}
	public void setFieldDeseId(Long fieldDeseId) {
		this.fieldDeseId = fieldDeseId;
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
	public List<GwServiceDictView> getDictList() {
		return dictList;
	}
	public void setDictList(List<GwServiceDictView> dictList) {
		this.dictList = dictList;
	}
	public String getFieldDesc() {
		return fieldDesc;
	}
	public void setFieldDesc(String fieldDesc) {
		this.fieldDesc = fieldDesc;
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
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getDesenType() {
		return desenType;
	}
	public void setDesenType(String desenType) {
		this.desenType = desenType;
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
}