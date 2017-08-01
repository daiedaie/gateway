package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwRole entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwRoleVO implements java.io.Serializable {

	// Fields

	private String roleCode;
	private String roleName;
	private String roleDesc;
	private String status;
	private Date createTime;
	private String creator;
	private Date modifyTime;
	private String modifier;
	private String remark;

	// Constructors

	/** default constructor */
	public GwRoleVO() {
	}

	/** full constructor */
	public GwRoleVO(String roleName, String roleDesc, String status,
			Date createTime, String creator, Date modifyTime, String modifier,
			String remark) {
		this.roleName = roleName;
		this.roleDesc = roleDesc;
		this.status = status;
		this.createTime = createTime;
		this.creator = creator;
		this.modifyTime = modifyTime;
		this.modifier = modifier;
		this.remark = remark;
	}

	// Property accessors

	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDesc() {
		return this.roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
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

}