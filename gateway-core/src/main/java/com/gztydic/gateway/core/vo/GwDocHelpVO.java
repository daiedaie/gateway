package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwUser entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwDocHelpVO implements java.io.Serializable {

	// Fields
	private Long docId;
	private String docDesc;
	private Long FileId;
	private GwUploadFileVO fileVO;
	private String createUser;
	private Date createTime;
	private String updateUser;
	private Date updateTime;
	
	public Long getDocId() {
		return docId;
	}
	public void setDocId(Long docId) {
		this.docId = docId;
	}
	public String getDocDesc() {
		return docDesc;
	}
	public void setDocDesc(String docDesc) {
		this.docDesc = docDesc;
	}
	public Long getFileId() {
		return FileId;
	}
	public void setFileId(Long fileId) {
		FileId = fileId;
	}
	public GwUploadFileVO getFileVO() {
		return fileVO;
	}
	public void setFileVO(GwUploadFileVO fileVO) {
		this.fileVO = fileVO;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}