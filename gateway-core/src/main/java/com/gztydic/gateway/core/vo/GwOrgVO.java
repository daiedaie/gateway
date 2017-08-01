package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwOrg entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwOrgVO implements java.io.Serializable {

	// Fields

	private Long orgId;
	private String orgName;
	private String orgHeadName;
	private String certType;
	private String certNo;
	private String regCode;
	private String orgAddr;
	private String orgTel;
	private String status;
	private Date createTime;
	private String creator;
	private Date modifyTime;
	private String modifier;
	private String remark;
	private String recordCode;

	// Constructors

	/** default constructor */
	public GwOrgVO() {
	}

	/** full constructor */
	public GwOrgVO(String orgName, String orgHeadName, String certType,
			String certNo, String regCode, String orgAddr, String orgTel,
			String status, Date createTime, String creator, Date modifyTime,
			String modifier, String remark, String recordCode) {
		this.orgName = orgName;
		this.orgHeadName = orgHeadName;
		this.certType = certType;
		this.certNo = certNo;
		this.regCode = regCode;
		this.orgAddr = orgAddr;
		this.orgTel = orgTel;
		this.status = status;
		this.createTime = createTime;
		this.creator = creator;
		this.modifyTime = modifyTime;
		this.modifier = modifier;
		this.remark = remark;
		this.recordCode = recordCode;
	}

	// Property accessors

	public Long getOrgId() {
		return this.orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return this.orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgHeadName() {
		return this.orgHeadName;
	}

	public void setOrgHeadName(String orgHeadName) {
		this.orgHeadName = orgHeadName;
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

	public String getRegCode() {
		return this.regCode;
	}

	public void setRegCode(String regCode) {
		this.regCode = regCode;
	}

	public String getOrgAddr() {
		return this.orgAddr;
	}

	public void setOrgAddr(String orgAddr) {
		this.orgAddr = orgAddr;
	}

	public String getOrgTel() {
		return this.orgTel;
	}

	public void setOrgTel(String orgTel) {
		this.orgTel = orgTel;
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

}