package com.gztydic.gateway.core.vo;

/**
 * GwUserRoleId entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwUserRoleVO implements java.io.Serializable {

	// Fields

	private Long userId;
	private String roleCode;

	// Constructors

	/** default constructor */
	public GwUserRoleVO() {
	}

	/** full constructor */
	public GwUserRoleVO(Long userId, String roleCode) {
		this.userId = userId;
		this.roleCode = roleCode;
	}

	// Property accessors

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof GwUserRoleVO))
			return false;
		GwUserRoleVO castOther = (GwUserRoleVO) other;

		return ((this.getUserId() == castOther.getUserId()) || (this
				.getUserId() != null
				&& castOther.getUserId() != null && this.getUserId().equals(
				castOther.getUserId())))
				&& ((this.getRoleCode() == castOther.getRoleCode()) || (this
						.getRoleCode() != null
						&& castOther.getRoleCode() != null && this
						.getRoleCode().equals(castOther.getRoleCode())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getUserId() == null ? 0 : this.getUserId().hashCode());
		result = 37 * result
				+ (getRoleCode() == null ? 0 : this.getRoleCode().hashCode());
		return result;
	}

}