package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwModifyRecord entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwModifyRecordVO implements java.io.Serializable {

	// Fields

	private Long recordId;
	private String recordCode;
	private String tableCode;
	private String columsCode;
	private String beforeValue;
	private String afterValue;
	private String status;
	private Date createTime;
	private String creator;
	private Date modifyTime;
	private String modifier;
	private Integer batchId;

	// Constructors

	/** default constructor */
	public GwModifyRecordVO() {
	}

	/** full constructor */
	public GwModifyRecordVO(String recordCode, String tableCode,
			String columsCode, String beforeValue, String afterValue,
			String status, Date createTime, String creator, Date modifyTime,
			String modifier,Integer batchId) {
		this.recordCode = recordCode;
		this.tableCode = tableCode;
		this.columsCode = columsCode;
		this.beforeValue = beforeValue;
		this.afterValue = afterValue;
		this.status = status;
		this.createTime = createTime;
		this.creator = creator;
		this.modifyTime = modifyTime;
		this.modifier = modifier;
		this.batchId = batchId;
	}

	// Property accessors

	public Long getRecordId() {
		return this.recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public String getRecordCode() {
		return this.recordCode;
	}

	public void setRecordCode(String recordCode) {
		this.recordCode = recordCode;
	}

	public String getTableCode() {
		return this.tableCode;
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}

	public String getColumsCode() {
		return this.columsCode;
	}

	public void setColumsCode(String columsCode) {
		this.columsCode = columsCode;
	}

	public String getBeforeValue() {
		return this.beforeValue;
	}

	public void setBeforeValue(String beforeValue) {
		this.beforeValue = beforeValue;
	}

	public String getAfterValue() {
		return this.afterValue;
	}

	public void setAfterValue(String afterValue) {
		this.afterValue = afterValue;
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

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}
}