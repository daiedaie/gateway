package com.gztydic.gateway.webservice.client.app;


/**
 * 第三方APP获取服务数据参数
 *
 */
public class ServiceRequestParam  implements java.io.Serializable {

    private String userName;
    private String password;
    private String serviceCode;
    
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getServiceCode() {
		return serviceCode;
	}
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

}
