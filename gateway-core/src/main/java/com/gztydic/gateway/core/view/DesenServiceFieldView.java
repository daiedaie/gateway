package com.gztydic.gateway.core.view;


/** 
 * @ClassName: DesenServiceFieldView 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author davis
 * @date 2014-12-21 下午9:35:51 
 *  
 */
public class DesenServiceFieldView implements java.io.Serializable {

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 1L;
	private Long fieldDeseId;
	private Long serviceId;
	private Long userId;
	private Long fieldId;
	private String ruleType;
	private String ruleName;
	private String ruleContent;
	private String replaceContent;
	private String conditionType;
	private String conditionContent;
	private String fieldCode;
	private String fieldName;
	private String checkType;
	private String checkTypeName;
	private String checkRule;
	
	public DesenServiceFieldView(){
		
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

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
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

	public String getCheckTypeName() {
		return checkTypeName;
	}

	public void setCheckTypeName(String checkTypeName) {
		this.checkTypeName = checkTypeName;
	}
}
