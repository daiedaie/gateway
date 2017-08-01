package com.gztydic.gateway.core.vo;

/**
 * GwUserFuncId entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwUserFuncVO implements java.io.Serializable {

	// Fields

	private String userType;
	private String funcCode;

	// Constructors

	/** default constructor */
	public GwUserFuncVO() {
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getFuncCode() {
		return this.funcCode;
	}

	public void setFuncCode(String funcCode) {
		this.funcCode = funcCode;
	}
}