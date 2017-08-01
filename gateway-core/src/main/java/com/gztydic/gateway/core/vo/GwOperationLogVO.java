package com.gztydic.gateway.core.vo;

import java.util.Date;

/**
 * GwOperationLog entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwOperationLogVO implements java.io.Serializable {

	// Fields

	private Long operationId;
	private String operationUser;
	private String acceptUser;
	private String operationType;
	private String operationContent;
	private Date operationTime;

	// Constructors

	/** default constructor */
	public GwOperationLogVO() {
	}

	/** full constructor */
	public GwOperationLogVO(String operationUser, String acceptUser,
			String operationType, String operationContent, Date operationTime) {
		this.operationUser = operationUser;
		this.acceptUser = acceptUser;
		this.operationType = operationType;
		this.operationContent = operationContent;
		this.operationTime = operationTime;
	}

	// Property accessors

	public Long getOperationId() {
		return this.operationId;
	}

	public void setOperationId(Long operationId) {
		this.operationId = operationId;
	}

	

	public String getOperationUser() {
		return operationUser;
	}

	public void setOperationUser(String operationUser) {
		this.operationUser = operationUser;
	}

	public String getAcceptUser() {
		return acceptUser;
	}

	public void setAcceptUser(String acceptUser) {
		this.acceptUser = acceptUser;
	}

	public String getOperationType() {
		return this.operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getOperationContent() {
		return this.operationContent;
	}

	public void setOperationContent(String operationContent) {
		this.operationContent = operationContent;
	}

	public Date getOperationTime() {
		return this.operationTime;
	}

	public void setOperationTime(Date operationTime) {
		this.operationTime = operationTime;
	}

}