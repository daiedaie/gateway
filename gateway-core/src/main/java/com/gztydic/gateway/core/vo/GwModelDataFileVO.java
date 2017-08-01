package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwModelDataFile entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwModelDataFileVO implements java.io.Serializable {

	// Fields

	private Long fileId;
	private Long modelId;
	private Long taskId;
	private Long userId;
	private String fileType;
	private String fileStatus;
	private String filePath;
	private String fileName;
	private Date createTime;
	private Date deleteTime;
	private String ftpIp;
	private String ftpPort;
	private String ftpUser;
	private String ftpPassword;
	private String unzipName;

	// Constructors

	/** default constructor */
	public GwModelDataFileVO() {
	}

	/** minimal constructor */
	public GwModelDataFileVO(Long userId, String filePath, String fileName,
			Date createTime) {
		this.userId = userId;
		this.filePath = filePath;
		this.fileName = fileName;
		this.createTime = createTime;
	}

	/** full constructor */
	public GwModelDataFileVO(Long userId,
			String fileType, String fileStatus, String filePath,
			String fileName, Date createTime, Date deleteTime) {
		this.userId = userId;
		this.fileType = fileType;
		this.fileStatus = fileStatus;
		this.filePath = filePath;
		this.fileName = fileName;
		this.createTime = createTime;
		this.deleteTime = deleteTime;
	}

	// Property accessors

	public Long getFileId() {
		return this.fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFileType() {
		return this.fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileStatus() {
		return this.fileStatus;
	}

	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getDeleteTime() {
		return this.deleteTime;
	}

	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
	}

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getFtpIp() {
		return ftpIp;
	}

	public void setFtpIp(String ftpIp) {
		this.ftpIp = ftpIp;
	}

	public String getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(String ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getFtpUser() {
		return ftpUser;
	}

	public void setFtpUser(String ftpUser) {
		this.ftpUser = ftpUser;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getUnzipName() {
		return unzipName;
	}

	public void setUnzipName(String unzipName) {
		this.unzipName = unzipName;
	}
	
}