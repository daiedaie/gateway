package com.gztydic.gateway.gather.webservice.client.data;

import java.util.List;
import java.util.Map;

public class DataResponse{

    private boolean result;
    private String info;
    private String serviceType;			//服务类型：0在线服务；1离线服务
    private String serviceApi;
    private List<Map<String, String>> data;		//解析data后的对象
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getServiceApi() {
		return serviceApi;
	}
	public void setServiceApi(String serviceApi) {
		this.serviceApi = serviceApi;
	}
	public List<Map<String, String>> getData() {
		return data;
	}
	public void setData(List<Map<String, String>> data) {
		this.data = data;
	}
    
}
