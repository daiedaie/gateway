package com.gztydic.gateway.core.view;

import java.util.List;
import java.util.Map;

public class DataEntity implements java.io.Serializable{
	private String serviceCode;
	private String serviceName;
	private List<Map<String, String>> result;
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public List<Map<String, String>> getResult() {
		return result;
	}
	public void setResult(List<Map<String, String>> result) {
		this.result = result;
	}
}
