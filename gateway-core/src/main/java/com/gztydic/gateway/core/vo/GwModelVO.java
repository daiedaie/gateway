package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwModel entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwModelVO implements java.io.Serializable {

	// Fields

	private Long modelId;
	private String modelCode;
	private String modelName;
	private String modelVersion;
	private Date startTime;
	private String modelType;
	private String modelDesc;
	private String algType;
	private String algRule;
	private String status;
	private Date createTime;
	private String creator;
	private Date modifyTime;
	private String modifier;
	private String remark;

	// Constructors

	/** default constructor */
	public GwModelVO() {
	}

	/** full constructor */
	public GwModelVO(String modelCode, String modelName, String modelDesc, String status,
			Date createTime, String creator, Date modifyTime, String modifier,
			String remark) {
		this.modelCode = modelCode;
		this.modelName = modelName;
		this.modelDesc = modelDesc;
		this.status = status;
		this.createTime = createTime;
		this.creator = creator;
		this.modifyTime = modifyTime;
		this.modifier = modifier;
		this.remark = remark;
	}

	// Property accessors

	public Long getModelId() {
		return this.modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public String getModelCode() {
		return this.modelCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public String getModelName() {
		return this.modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelDesc() {
		return this.modelDesc;
	}

	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
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

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	public String getModelVersion() {
		return modelVersion;
	}

	public void setModelVersion(String modelVersion) {
		this.modelVersion = modelVersion;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getAlgType() {
		return algType;
	}

	public void setAlgType(String algType) {
		this.algType = algType;
	}

	public String getAlgRule() {
		return algRule;
	}

	public void setAlgRule(String algRule) {
		this.algRule = algRule;
	}
}