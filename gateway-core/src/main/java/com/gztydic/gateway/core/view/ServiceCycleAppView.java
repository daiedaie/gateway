package com.gztydic.gateway.core.view;

/** 
 * @ClassName: ServiceCycleAppView 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author davis
 * @date 2014-12-18 下午4:46:26 
 *  
 */
public class ServiceCycleAppView {

	private Long taskId;
	private Long serviceid;
	private String serviceCode;
	private String fieldCode;
	private String fieldValue;
	
	public Long getServiceid() {
		return serviceid;
	}
	public void setServiceid(Long serviceid) {
		this.serviceid = serviceid;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getFieldCode() {
		return fieldCode;
	}
	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	
}
