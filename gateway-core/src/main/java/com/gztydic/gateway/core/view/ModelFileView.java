package com.gztydic.gateway.core.view;

import java.util.Date;

public class ModelFileView implements java.io.Serializable {
	private Long fileId;
	private String filePath;
	private String fileName;
	private String unzipName;
	private Long taskId;
	private String fieldCode;
	private String fieldValue;
	private Long serviceId;
	private String serviceType;
	private String serviceSource;
	private Date createTime;
	private String createUser;
	public ModelFileView(){
		
	}
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
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
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getServiceSource() {
		return serviceSource;
	}
	public void setServiceSource(String serviceSource) {
		this.serviceSource = serviceSource;
	}
	public String getUnzipName() {
		return unzipName;
	}
	public void setUnzipName(String unzipName) {
		this.unzipName = unzipName;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	
}
