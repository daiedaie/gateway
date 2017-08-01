package com.gztydic.gateway.core.view;


public class GwServiceDictView implements java.io.Serializable {

	private Long fieldId;
	private String dictCode;
	private String dictName;
	private String dictKey;
	private String dictValue;
	
	public Long getFieldId() {
		return fieldId;
	}
	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}
	public String getDictCode() {
		return dictCode;
	}
	public void setDictCode(String dictCode) {
		this.dictCode = dictCode;
	}
	public String getDictName() {
		return dictName;
	}
	public void setDictName(String dictName) {
		this.dictName = dictName;
	}
	public String getDictKey() {
		return dictKey;
	}
	public void setDictKey(String dictKey) {
		this.dictKey = dictKey;
	}
	public String getDictValue() {
		return dictValue;
	}
	public void setDictValue(String dictValue) {
		this.dictValue = dictValue;
	}
}