package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwFunc entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwFuncVO implements java.io.Serializable {

	// Fields

	private String funcCode;
	private String funcName;
	private String funcUrl;
	private String iconUrl;
	private String funcDesc;
	private String status;
	private Date createTime;
	private String creator;
	private Date modifyTime;
	private String modifier;
	private String remark;
	private String parentCode;
	private Long funcSort;

	// Constructors

	/** default constructor */
	public GwFuncVO() {
	}

	/** full constructor */
	public GwFuncVO(String funcName, String funcUrl, String iconUrl,
			String funcDesc, String status, Date createTime, String creator,
			Date modifyTime, String modifier, String remark) {
		this.funcName = funcName;
		this.funcUrl = funcUrl;
		this.iconUrl = iconUrl;
		this.funcDesc = funcDesc;
		this.status = status;
		this.createTime = createTime;
		this.creator = creator;
		this.modifyTime = modifyTime;
		this.modifier = modifier;
		this.remark = remark;
	}

	// Property accessors

	public String getFuncCode() {
		return this.funcCode;
	}

	public void setFuncCode(String funcCode) {
		this.funcCode = funcCode;
	}

	public String getFuncName() {
		return this.funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public String getFuncUrl() {
		return this.funcUrl;
	}

	public void setFuncUrl(String funcUrl) {
		this.funcUrl = funcUrl;
	}

	public String getIconUrl() {
		return this.iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getFuncDesc() {
		return this.funcDesc;
	}

	public void setFuncDesc(String funcDesc) {
		this.funcDesc = funcDesc;
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

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public Long getFuncSort() {
		return funcSort;
	}

	public void setFuncSort(Long funcSort) {
		this.funcSort = funcSort;
	}
}