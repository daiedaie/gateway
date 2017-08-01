package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwModelFileCycle entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwModelDataCycleVO implements java.io.Serializable {

	// Fields

	private Long cycleId;
	private String dataType;
	private String cycleType;
	private Long cycleNum;
	private Date createTime;
	private String createUser;
	private Date updateTime;
	private String updateUser;

	// Constructors

	/** default constructor */
	public GwModelDataCycleVO() {
	}

	/** minimal constructor */
	public GwModelDataCycleVO(String fileType, String cycleType, Long cycleNum,
			Date createTime, String createUser) {
		this.dataType = dataType;
		this.cycleType = cycleType;
		this.cycleNum = cycleNum;
		this.createTime = createTime;
		this.createUser = createUser;
	}

	/** full constructor */
	public GwModelDataCycleVO(String dataType, String cycleType, Long cycleNum,
			Date createTime, String createUser, Date updateTime,
			String updateUser) {
		this.dataType = dataType;
		this.cycleType = cycleType;
		this.cycleNum = cycleNum;
		this.createTime = createTime;
		this.createUser = createUser;
		this.updateTime = updateTime;
		this.updateUser = updateUser;
	}

	// Property accessors

	public Long getCycleId() {
		return this.cycleId;
	}

	public void setCycleId(Long cycleId) {
		this.cycleId = cycleId;
	}

	public String getDataType() {
		return this.dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
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

}