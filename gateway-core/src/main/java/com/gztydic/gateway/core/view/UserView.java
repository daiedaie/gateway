package com.gztydic.gateway.core.view;

import java.io.Serializable;
import java.util.Date;

public class UserView implements Serializable{
	private Long orgId;
	private String orgName;
	private Long userId;
	private String userType;
	private String userName;
	private String loginName;
	private String loginPwd;
	private String token;
	private String certType;
	private String certNo;
	private String email;
	private String moblie;
	private String addr;
	private Date loginTim;
	private String confirmStatus;
	private String onlineStatus;
	private Long runLevel;
	private String status;
	private Date createTime;
	private String creator;
	private Date modifyTime;
	private String modifier;
	private String remark;
	private String recordCode;
	private Long fileId;
	public UserView(){
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getLoginPwd() {
		return loginPwd;
	}
	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getCertType() {
		return certType;
	}
	public void setCertType(String certType) {
		this.certType = certType;
	}
	public String getCertNo() {
		return certNo;
	}
	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMoblie() {
		return moblie;
	}
	public void setMoblie(String moblie) {
		this.moblie = moblie;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public Date getLoginTim() {
		return loginTim;
	}
	public void setLoginTim(Date loginTim) {
		this.loginTim = loginTim;
	}
	public String getConfirmStatus() {
		return confirmStatus;
	}
	public void setConfirmStatus(String confirmStatus) {
		this.confirmStatus = confirmStatus;
	}
	public String getOnlineStatus() {
		return onlineStatus;
	}
	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
	}
	public Long getRunLevel() {
		return runLevel;
	}
	public void setRunLevel(Long runLevel) {
		this.runLevel = runLevel;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Date getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getRecordCode() {
		return recordCode;
	}
	public void setRecordCode(String recordCode) {
		this.recordCode = recordCode;
	}
	public Long getFileId() {
		return fileId;
	}
	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
		
	}