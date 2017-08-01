package com.gztydic.gateway.core.vo;

/**
 * GwRoleFuncId entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwRoleFuncVO implements java.io.Serializable {

	// Fields

	private String roleCode;
	private String funcCode;

	// Constructors

	/** default constructor */
	public GwRoleFuncVO() {
	}

	/** full constructor */
	public GwRoleFuncVO(String roleCode, String funcCode) {
		this.roleCode = roleCode;
		this.funcCode = funcCode;
	}

	// Property accessors

	public String getRoleCode() {
		return this.roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getFuncCode() {
		return this.funcCode;
	}

	public void setFuncCode(String funcCode) {
		this.funcCode = funcCode;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof GwRoleFuncVO))
			return false;
		GwRoleFuncVO castOther = (GwRoleFuncVO) other;

		return ((this.getRoleCode() == castOther.getRoleCode()) || (this
				.getRoleCode() != null
				&& castOther.getRoleCode() != null && this.getRoleCode()
				.equals(castOther.getRoleCode())))
				&& ((this.getFuncCode() == castOther.getFuncCode()) || (this
						.getFuncCode() != null
						&& castOther.getFuncCode() != null && this
						.getFuncCode().equals(castOther.getFuncCode())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getRoleCode() == null ? 0 : this.getRoleCode().hashCode());
		result = 37 * result
				+ (getFuncCode() == null ? 0 : this.getFuncCode().hashCode());
		return result;
	}

}