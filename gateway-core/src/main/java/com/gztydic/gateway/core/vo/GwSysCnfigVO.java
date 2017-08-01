package com.gztydic.gateway.core.vo;

public class GwSysCnfigVO {
	private Long configId;
	private String configType;
	private Long configValue;
	private String configUnit;
	public GwSysCnfigVO(){
		
	}
	public Long getConfigId() {
		return configId;
	}
	public void setConfigId(Long configId) {
		this.configId = configId;
	}
	public String getConfigType() {
		return configType;
	}
	public void setConfigType(String configType) {
		this.configType = configType;
	}
	public Long getConfigValue() {
		return configValue;
	}
	public void setConfigValue(Long configValue) {
		this.configValue = configValue;
	}
	public String getConfigUnit() {
		return configUnit;
	}
	public void setConfigUnit(String configUnit) {
		this.configUnit = configUnit;
	}
}
