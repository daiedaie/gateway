package com.gztydic.gateway.core.vo;

/**
 * GwRoleModelId entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwRoleServiceVO implements java.io.Serializable {

	// Fields

	private String roleCode;
	private Long serviceId;

	// Constructors

	/** default constructor */
	public GwRoleServiceVO() {
	}

	/** full constructor */
	public GwRoleServiceVO(String roleCode, Long serviceId) {
		this.roleCode = roleCode;
		this.serviceId = serviceId;
	}

	// Property accessors

	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public Long getServiceId() {
		return serviceId;
	}

	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}

}