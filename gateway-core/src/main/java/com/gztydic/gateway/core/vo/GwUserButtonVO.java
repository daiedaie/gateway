package com.gztydic.gateway.core.vo;

/**
 * GwUserButtonId entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwUserButtonVO implements java.io.Serializable {

	// Fields

	private String userType;
	private String buttonCode;

	// Constructors

	/** default constructor */
	public GwUserButtonVO() {
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getButtonCode() {
		return this.buttonCode;
	}

	public void setButtonCode(String buttonCode) {
		this.buttonCode = buttonCode;
	}

}