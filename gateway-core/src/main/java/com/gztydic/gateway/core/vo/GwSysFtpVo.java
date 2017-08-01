package com.gztydic.gateway.core.vo;

/**
 * GwFunc entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class GwSysFtpVo {

	//Fields
	private Long Id;
	private String ftpIp;
	private String ftpType;
	private String ftpPort;
	// Constructors
	
	/** default constructor */
	public GwSysFtpVo() {
	}
	// Property accessors
	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getFtpIp() {
		return ftpIp;
	}

	public void setFtpIp(String ftpIp) {
		this.ftpIp = ftpIp;
	}

	public String getFtpType() {
		return ftpType;
	}

	public void setFtpType(String ftpType) {
		this.ftpType = ftpType;
	}

	public String getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(String ftpPort) {
		this.ftpPort = ftpPort;
	}
	
}
