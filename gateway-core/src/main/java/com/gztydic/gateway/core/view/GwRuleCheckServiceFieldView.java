package com.gztydic.gateway.core.view;

public class GwRuleCheckServiceFieldView implements java.io.Serializable{
	private String recorder;
	private String checkType;
	private String dictValue;
	private String checkRule;
	private String fieldCode;
	private String fieldName;
	public String getRecorder() {
		return recorder;
	}
	public void setRecorder(String recorder) {
		this.recorder = recorder;
	}
	public String getCheckType() {
		return checkType;
	}
	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}
	public String getDictValue() {
		return dictValue;
	}
	public void setDictValue(String dictValue) {
		this.dictValue = dictValue;
	}
	public String getCheckRule() {
		return checkRule;
	}
	public void setCheckRule(String checkRule) {
		this.checkRule = checkRule;
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
	
}
