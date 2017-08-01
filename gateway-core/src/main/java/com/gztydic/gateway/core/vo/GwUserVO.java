package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwUser entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwUserVO implements java.io.Serializable {

	// Fields

	private Long userId;
	private Long orgId;
	private String userType;
	private String userName;
	private String loginName;
	private String loginPwd;
	private String needFilePwd;
	private String fileEncryPwd;
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
	private String pushFtp;
	private String ftpIp;
	private String ftpPort;
	private String ftpUsername;
	private String ftpPassword;
	private String ftpPath;
	private String webserviceUrl;
	private String webserviceMethod;
	private String baseWsdl;
	private String ftpType;

	// Constructors

	/** default constructor */
	public GwUserVO() {
	}

	/** full constructor */
	public GwUserVO(Long orgId, String userType, String userName,
			String loginName, String loginPwd, String token, String certType,
			String certNo, String email, String moblie, String addr,
			Date loginTim, String confirmStatus, String onlineStatus,
			Long runLevel, String status, Date createTime, String creator,
			Date modifyTime, String modifier, String remark, String recordCode) {
		this.orgId = orgId;
		this.userType = userType;
		this.userName = userName;
		this.loginName = loginName;
		this.loginPwd = loginPwd;
		this.token = token;
		this.certType = certType;
		this.certNo = certNo;
		this.email = email;
		this.moblie = moblie;
		this.addr = addr;
		this.loginTim = loginTim;
		this.confirmStatus = confirmStatus;
		this.onlineStatus = onlineStatus;
		this.runLevel = runLevel;
		this.status = status;
		this.createTime = createTime;
		this.creator = creator;
		this.modifyTime = modifyTime;
		this.modifier = modifier;
		this.remark = remark;
		this.recordCode = recordCode;
	}

	// Property accessors

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getOrgId() {
		return this.orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getUserType() {
		return this.userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLoginName() {
		return this.loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPwd() {
		return this.loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCertType() {
		return this.certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertNo() {
		return this.certNo;
	}

	public void setCertNo(String certNo) {
		this.certNo = certNo;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMoblie() {
		return this.moblie;
	}

	public void setMoblie(String moblie) {
		this.moblie = moblie;
	}

	public String getAddr() {
		return this.addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public Date getLoginTim() {
		return this.loginTim;
	}

	public void setLoginTim(Date loginTim) {
		this.loginTim = loginTim;
	}

	public String getConfirmStatus() {
		return this.confirmStatus;
	}

	public void setConfirmStatus(String confirmStatus) {
		this.confirmStatus = confirmStatus;
	}

	public String getOnlineStatus() {
		return this.onlineStatus;
	}

	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public Long getRunLevel() {
		return this.runLevel;
	}

	public void setRunLevel(Long runLevel) {
		this.runLevel = runLevel;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreator() {
		return this.creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getModifyTime() {
		return this.modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getModifier() {
		return this.modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRecordCode() {
		return this.recordCode;
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

	public String getPushFtp() {
		return pushFtp;
	}

	public void setPushFtp(String pushFtp) {
		this.pushFtp = pushFtp;
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

	public String getFtpUsername() {
		return ftpUsername;
	}

	public void setFtpUsername(String ftpUsername) {
		this.ftpUsername = ftpUsername;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		this.ftpPassword = ftpPassword;
	}

	public String getFtpPath() {
		return ftpPath;
	}

	public void setFtpPath(String ftpPath) {
		this.ftpPath = ftpPath;
	}

	public String getFileEncryPwd() {
		return fileEncryPwd;
	}

	public void setFileEncryPwd(String fileEncryPwd) {
		this.fileEncryPwd = fileEncryPwd;
	}

	public String getNeedFilePwd() {
		return needFilePwd;
	}

	public void setNeedFilePwd(String needFilePwd) {
		this.needFilePwd = needFilePwd;
	}

	public String getWebserviceUrl() {
		return webserviceUrl;
	}

	public void setWebserviceUrl(String webserviceUrl) {
		this.webserviceUrl = webserviceUrl;
	}

	public String getWebserviceMethod() {
		return webserviceMethod;
	}

	public void setWebserviceMethod(String webserviceMethod) {
		this.webserviceMethod = webserviceMethod;
	}

	public String getBaseWsdl() {
		return baseWsdl;
	}

	public void setBaseWsdl(String baseWsdl) {
		this.baseWsdl = baseWsdl;
	}
	public String getFtpType() {
		return ftpType;
	}

	public void setFtpType(String ftpType) {
		this.ftpType = ftpType;
	}
}