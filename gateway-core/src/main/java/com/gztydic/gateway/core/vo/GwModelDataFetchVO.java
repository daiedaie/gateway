package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwModelDataFetch entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwModelDataFetchVO implements java.io.Serializable {

	// Fields

	private Long fetchId;
	private Long modelId;
	private Long userId;
	private Long serviceId;
	private String fetchType;
	private String cycleType;
	private Long cycleNum;
	private String auditStatus;
	private Date auditTime;
	private Date createTime;
	private String createUser;
	private Date updateTime;
	private String updateUser;
	private String desenType;
	private Long checkFileId;
	private Long maxCheckNum;
	private String checkAudit;
	private Long outputNum;
	private Long pushDataWay;
	private String authorityGno;
	// Constructors



	/** default constructor */
	public GwModelDataFetchVO() {
	}

	/** minimal constructor */
	public GwModelDataFetchVO(Long modelId, Long userId, String fetchType,
			String cycleType, Long cycleNum, String auditStatus,
			Date createTime, String createUser,String authorityGno) {
		this.modelId = modelId;
		this.userId = userId;
		this.fetchType = fetchType;
		this.cycleType = cycleType;
		this.cycleNum = cycleNum;
		this.auditStatus = auditStatus;
		this.createTime = createTime;
		this.createUser = createUser;
		this.authorityGno=authorityGno;
	}

	/** full constructor */
	public GwModelDataFetchVO(Long modelId, Long userId, String fetchType,
			String cycleType, Long cycleNum, String auditStatus,
			Date createTime, String createUser, Date updateTime,
			String updateUser) {
		this.modelId = modelId;
		this.userId = userId;
		this.fetchType = fetchType;
		this.cycleType = cycleType;
		this.cycleNum = cycleNum;
		this.auditStatus = auditStatus;
		this.createTime = createTime;
		this.createUser = createUser;
		this.updateTime = updateTime;
		this.updateUser = updateUser;
	}

	// Property accessors

	public Long getFetchId() {
		return this.fetchId;
	}

	public void setFetchId(Long fetchId) {
		this.fetchId = fetchId;
	}

	public Long getModelId() {
		return this.modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getFetchType() {
		return this.fetchType;
	}

	public void setFetchType(String fetchType) {
		this.fetchType = fetchType;
	}

	public String getCycleType() {
		return this.cycleType;
	}

	public void setCycleType(String cycleType) {
		this.cycleType = cycleType;
	}

	public Long getCycleNum() {
		return this.cycleNum;
	}

	public void setCycleNum(Long cycleNum) {
		this.cycleNum = cycleNum;
	}

	public String getAuditStatus() {
		return this.auditStatus;
	}

	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUser() {
		return this.updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public Date getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public String getDesenType() {
		return desenType;
	}

	public void setDesenType(String desenType) {
		this.desenType = desenType;
	}

	public Long getCheckFileId() {
		return checkFileId;
	}

	public void setCheckFileId(Long checkFileId) {
		this.checkFileId = checkFileId;
	}

	public Long getMaxCheckNum() {
		return maxCheckNum;
	}

	public void setMaxCheckNum(Long maxCheckNum) {
		this.maxCheckNum = maxCheckNum;
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

	public Long getPushDataWay() {
		return pushDataWay;
	}

	public void setPushDataWay(Long pushDataWay) {
		this.pushDataWay = pushDataWay;
	}
	
	public String getAuthorityGno() {
		return authorityGno;
	}

	public void setAuthorityGno(String authorityGno) {
		this.authorityGno = authorityGno;
	}

}