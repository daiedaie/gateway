package com.gztydic.gateway.core.view;

import java.io.Serializable;
import java.util.Date;

public class GwModelView implements Serializable{
	// Fields
	private Long modelId;
	private String modelCode;
	private String modelName;
	private String modelType;
	private String modelDesc;
	private String status;
	private Date createTime;
	private String creator;
	private Date modifyTime;
	private String modifier;
	private String remark;
	private String roleCode;
	public GwModelView() {
		
	}
	public Long getModelId() {
		return modelId;
	}
	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}
	public String getModelCode() {
		return modelCode;
	}
	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getModelType() {
		return modelType;
	}
	public void setModelType(String modelType) {
		this.modelType = modelType;
	}
	public String getModelDesc() {
		return modelDesc;
	}
	public void setModelDesc(String modelDesc) {
		this.modelDesc = modelDesc;
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
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	

}
