package com.gztydic.gateway.core.vo;

/**
 * GwUserModelId entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwUserServiceVO implements java.io.Serializable {

	// Fields

	private Long userId;
	private Long serviceId;

	// Constructors

	/** default constructor */
	public GwUserServiceVO() {
	}

	/** full constructor */
	public GwUserServiceVO(Long userId, Long serviceId) {
		this.userId = userId;
		this.serviceId = serviceId;
	}

	// Property accessors

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
}