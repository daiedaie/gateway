package com.gztydic.gateway.core.view;

import java.util.Date;

/** 
 * @ClassName: ModelTaskView 
 * @Description: TODO(模型取数任务记录实体) 
 * @author davis
 * @date 2014-11-28 下午3:08:05 
 *  
 */ 
public class ModelTaskView implements java.io.Serializable { 

	private Long taskId;
	private Long cycleNum;
	private String cycleType;
	private String userName;
	private String modelCode;
	private String modelName;
	private String dataStatus;
	private Long dataNum;
	private String taskStatus;
	private Date createTime;
	private Date endTime;
	private Date downloadStartTime;
	private Date downloadEndTime;
	private String dataSource;
	private String serviceCode;
	private String serviceName;
	private String serviceType;
	private String serviceSource;
	private Date auditTime;
	private String dataProgressStatus;
	private String preDataProgressStatus;
	private String fileStatus;
	private Long userId;
	private Long serviceId;
	private String desenType;
	private String originalFileStatus;
	private Long checkNum;
	private String checkResult;
	private String fieldValue;
	private String redoTag;
	private Long fieldNum;
	
	public String getCycleType() {
		return cycleType;
	}
	public void setCycleType(String cycleType) {
		this.cycleType = cycleType;
	}
	public Long getTaskId() {
		return taskId;
	}
	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}
	public Long getCycleNum() {
		return cycleNum;
	}
	public void setCycleNum(Long cycleNum) {
		this.cycleNum = cycleNum;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getModelCode() {
		return modelCode;
	}
	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getDataStatus() {
		return dataStatus;
	}
	public void setDataStatus(String dataStatus) {
		this.dataStatus = dataStatus;
	}
	public Long getDataNum() {
		return dataNum;
	}
	public void setDataNum(Long dataNum) {
		this.dataNum = dataNum;
	}
	public String getTaskStatus() {
		return taskStatus;
	}
	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Date getDownloadStartTime() {
		return downloadStartTime;
	}
	public void setDownloadStartTime(Date downloadStartTime) {
		this.downloadStartTime = downloadStartTime;
	}
	public Date getDownloadEndTime() {
		return downloadEndTime;
	}
	public void setDownloadEndTime(Date downloadEndTime) {
		this.downloadEndTime = downloadEndTime;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public Date getAuditTime() {
		return auditTime;
	}
	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}
	public String getDataProgressStatus() {
		return dataProgressStatus;
	}
	public void setDataProgressStatus(String dataProgressStatus) {
		this.dataProgressStatus = dataProgressStatus;
	}
	public String getFileStatus() {
		return fileStatus;
	}
	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
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
	public String getDesenType() {
		return desenType;
	}
	public void setDesenType(String desenType) {
		this.desenType = desenType;
	}
	public String getOriginalFileStatus() {
		return originalFileStatus;
	}
	public void setOriginalFileStatus(String originalFileStatus) {
		this.originalFileStatus = originalFileStatus;
	}
	public String getServiceSource() {
		return serviceSource;
	}
	public void setServiceSource(String serviceSource) {
		this.serviceSource = serviceSource;
	}
	public Long getCheckNum() {
		return checkNum;
	}
	public void setCheckNum(Long checkNum) {
		this.checkNum = checkNum;
	}
	public String getCheckResult() {
		return checkResult;
	}
	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}
	public String getRedoTag() {
		return redoTag;
	}
	public void setRedoTag(String redoTag) {
		this.redoTag = redoTag;
	}
	public Long getFieldNum() {
		return fieldNum;
	}
	public void setFieldNum(Long fieldNum) {
		this.fieldNum = fieldNum;
	}
	public String getPreDataProgressStatus() {
		return preDataProgressStatus;
	}
	public void setPreDataProgressStatus(String preDataProgressStatus) {
		this.preDataProgressStatus = preDataProgressStatus;
	}
	
	
}
