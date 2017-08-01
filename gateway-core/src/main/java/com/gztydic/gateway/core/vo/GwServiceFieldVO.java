package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwServiceField entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwServiceFieldVO implements java.io.Serializable {

	// Fields

	private Long fieldId;
	private Long serviceId;
	private String fieldCode;
	private String fieldName;
	private String fieldType;
	private String nullable;
	private String fieldDesc;
	private String gatherType;
	private String gatherCode;
	private Long reorder;
	private String createUser;
	private Date createTime;
	private String updateUser;
	private Date updateTime;
	
	// Constructors

	/** default constructor */
	public GwServiceFieldVO() {
	}

	/** minimal constructor */
	public GwServiceFieldVO(Long serviceId, String fieldCode,
			String fieldName, String fieldType, String fieldDesc) {
		this.serviceId = serviceId;
		this.fieldCode = fieldCode;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.fieldDesc = fieldDesc;
	}

	/** full constructor */
	public GwServiceFieldVO(Long serviceId, String fieldCode,
			String fieldName, String fieldType, String nullable,
			String fieldDesc) {
		this.serviceId = serviceId;
		this.fieldCode = fieldCode;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
		this.nullable = nullable;
		this.fieldDesc = fieldDesc;
	}

	// Property accessors

	public Long getFieldId() {
		return this.fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

	public String getFieldCode() {
		return this.fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public String getFieldName() {
		return this.fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return this.fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getNullable() {
		return this.nullable;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
	}

	public String getFieldDesc() {
		return this.fieldDesc;
	}

	public void setFieldDesc(String fieldDesc) {
		this.fieldDesc = fieldDesc;
	}

	public String getGatherType() {
		return gatherType;
	}

	public void setGatherType(String gatherType) {
		this.gatherType = gatherType;
	}

	public Long getReorder() {
		return reorder;
	}

	public void setReorder(Long reorder) {
		this.reorder = reorder;
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

	public String getGatherCode() {
		return gatherCode;
	}

	public void setGatherCode(String gatherCode) {
		this.gatherCode = gatherCode;
	}
	
}