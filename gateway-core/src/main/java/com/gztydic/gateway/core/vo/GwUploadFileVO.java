package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwUploadFile entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwUploadFileVO implements java.io.Serializable {

	// Fields

	private Long fileId;
	private String realName;
	private String filePath;
	private String createUser;
	private Date createTime;
	private String fileType;

	// Constructors

	/** default constructor */
	public GwUploadFileVO() {
	}

	/** full constructor */
	public GwUploadFileVO(String realName, String filePath, String createUser,
			Date createTime, String fileType) {
		this.realName = realName;
		this.filePath = filePath;
		this.createUser = createUser;
		this.createTime = createTime;
		this.fileType = fileType;
	}

	// Property accessors

	public Long getFileId() {
		return this.fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public String getRealName() {
		return this.realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
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

	public String getFileType() {
		return this.fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}