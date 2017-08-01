package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwModelDataFetchTask entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwModelDataFetchTaskVO implements java.io.Serializable {

	// Fields

	private Long taskId;
	private Long fetchId;
	private Long userId;
	private Long modelId;
	private Long serviceId;
	private String preDataProgressStatus;
	private String dataProgressStatus;
	private String dataStatus;
	private Long dataNum;
	private String taskStatus;
	private String createUser;
	private Date auditTime;
	private Date createTime;	//任务创建时间
	private Date endTime;
	private Date downloadStartTime;	//任务开始时间
	private Date downloadEndTime;	//任务结束时间
	private Date downloadTime;		//用户下载时间
	private String dataSource;
	private String dataType;
	private String fieldCode;
	private String fieldValue;
	private String checkResult;
	private Long maxCheckNum;
	private Long checkNum;
	private Long checkIrregularNum;
	private Long checkFileId;
	private Long checkBatch;
	private String checkAudit;
	private Long outputNum;
	private String redoTag;
	private Integer pushCount;
	private Long processId;
	// Constructors

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	/** default constructor */
	public GwModelDataFetchTaskVO() {
	}

	/** minimal constructor */
	public GwModelDataFetchTaskVO(Long userId, Long modelId, String createUser,
			Date createTime,Long processId) {
		this.userId = userId;
		this.modelId = modelId;
		this.createUser = createUser;
		this.createTime = createTime;
		this.processId = processId;
	}

	/** full constructor */
	public GwModelDataFetchTaskVO(Long fetchId, Long userId, Long modelId,
			String dataProgressStatus, String dataStatus, Long dataNum,
			String taskStatus,String createUser, Date createTime, Date endTime,
			Date downloadStartTime, Date downloadEndTime, String dataSource,
			String dataType) {
		this.fetchId = fetchId;
		this.userId = userId;
		this.modelId = modelId;
		this.dataProgressStatus = dataProgressStatus;
		this.dataStatus = dataStatus;
		this.dataNum = dataNum;
		this.taskStatus = taskStatus;
		this.createUser = createUser;
		this.createTime = createTime;
		this.endTime = endTime;
		this.downloadStartTime = downloadStartTime;
		this.downloadEndTime = downloadEndTime;
		this.dataSource = dataSource;
		this.dataType = dataType;
	}

	// Property accessors

	public Long getTaskId() {
		return this.taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getFetchId() {
		return this.fetchId;
	}

	public void setFetchId(Long fetchId) {
		this.fetchId = fetchId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getModelId() {
		return this.modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public String getDataProgressStatus() {
		return this.dataProgressStatus;
	}

	public void setDataProgressStatus(String dataProgressStatus) {
		this.dataProgressStatus = dataProgressStatus;
	}

	public String getDataStatus() {
		return this.dataStatus;
	}

	public String getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(String taskStatus) {
		this.taskStatus = taskStatus;
	}

	public void setDataStatus(String dataStatus) {
		this.dataStatus = dataStatus;
	}

	public Long getDataNum() {
		return this.dataNum;
	}

	public void setDataNum(Long dataNum) {
		this.dataNum = dataNum;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getDownloadStartTime() {
		return this.downloadStartTime;
	}

	public void setDownloadStartTime(Date downloadStartTime) {
		this.downloadStartTime = downloadStartTime;
	}

	public Date getDownloadEndTime() {
		return this.downloadEndTime;
	}

	public void setDownloadEndTime(Date downloadEndTime) {
		this.downloadEndTime = downloadEndTime;
	}

	public String getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getDataType() {
		return this.dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
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

	public String getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public Date getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(Date downloadTime) {
		this.downloadTime = downloadTime;
	}

	public String getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}

	public Long getMaxCheckNum() {
		return maxCheckNum;
	}

	public void setMaxCheckNum(Long maxCheckNum) {
		this.maxCheckNum = maxCheckNum;
	}

	public Long getCheckIrregularNum() {
		return checkIrregularNum;
	}

	public void setCheckIrregularNum(Long checkIrregularNum) {
		this.checkIrregularNum = checkIrregularNum;
	}

	public Long getCheckNum() {
		return checkNum;
	}

	public void setCheckNum(Long checkNum) {
		this.checkNum = checkNum;
	}

	public Long getCheckFileId() {
		return checkFileId;
	}

	public void setCheckFileId(Long checkFileId) {
		this.checkFileId = checkFileId;
	}

	public Long getCheckBatch() {
		return checkBatch;
	}

	public void setCheckBatch(Long checkBatch) {
		this.checkBatch = checkBatch;
	}

	public String getCheckAudit() {
		return checkAudit;
	}

	public void setCheckAudit(String checkAudit) {
		this.checkAudit = checkAudit;
	}

	public Long getOutputNum() {
		return outputNum;
	}

	public void setOutputNum(Long outputNum) {
		this.outputNum = outputNum;
	}

	public String getRedoTag() {
		return redoTag;
	}

	public void setRedoTag(String redoTag) {
		this.redoTag = redoTag;
	}

	public Integer getPushCount() {
		return pushCount;
	}

	public void setPushCount(Integer pushCount) {
		this.pushCount = pushCount;
	}

	public String getPreDataProgressStatus() {
		return preDataProgressStatus;
	}

	public void setPreDataProgressStatus(String preDataProgressStatus) {
		this.preDataProgressStatus = preDataProgressStatus;
	}
	
}