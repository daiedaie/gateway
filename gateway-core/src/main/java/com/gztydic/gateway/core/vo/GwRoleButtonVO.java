package com.gztydic.gateway.core.vo;

/**
 * GwRoleButtonId entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwRoleButtonVO implements java.io.Serializable {

	// Fields

	private String roleCode;
	private String buttonCode;

	// Constructors

	/** default constructor */
	public GwRoleButtonVO() {
	}

	/** full constructor */
	public GwRoleButtonVO(String roleCode, String buttonCode) {
		this.roleCode = roleCode;
		this.buttonCode = buttonCode;
	}

	// Property accessors

	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getButtonCode() {
		return this.buttonCode;
	}

	public void setButtonCode(String buttonCode) {
		this.buttonCode = buttonCode;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof GwRoleButtonVO))
			return false;
		GwRoleButtonVO castOther = (GwRoleButtonVO) other;

		return ((this.getRoleCode() == castOther.getRoleCode()) || (this
				.getRoleCode() != null
				&& castOther.getRoleCode() != null && this.getRoleCode()
				.equals(castOther.getRoleCode())))
				&& ((this.getButtonCode() == castOther.getButtonCode()) || (this
						.getButtonCode() != null
						&& castOther.getButtonCode() != null && this
						.getButtonCode().equals(castOther.getButtonCode())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getRoleCode() == null ? 0 : this.getRoleCode().hashCode());
		result = 37
				* result
				+ (getButtonCode() == null ? 0 : this.getButtonCode()
						.hashCode());
		return result;
	}

}