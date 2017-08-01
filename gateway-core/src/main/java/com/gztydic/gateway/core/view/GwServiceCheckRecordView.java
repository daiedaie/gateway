package com.gztydic.gateway.core.view;

public class GwServiceCheckRecordView implements java.io.Serializable{
	private Long recordId;
	private Long rowId;
	private Long taskId;
	private Long serviceId;
	private String checkType;
	private String checkRule;
	private Long fieldSort;
	public Long getRecordId() {
		return recordId;
	}
	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
	public Long getRowId() {
		return rowId;
	}
	public void setRowId(Long rowId) {
		this.rowId = rowId;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
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
	public Long getFieldSort() {
		return fieldSort;
	}
	public void setFieldSort(Long fieldSort) {
		this.fieldSort = fieldSort;
	}
	
}
