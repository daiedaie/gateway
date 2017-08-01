package com.gztydic.gateway.webservice.client.gateway;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *	模型服务数据生成通知接口参数 
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceNoticeParam {
	
	@XmlElement(required=true, nillable=false)
	private String taskId;			//服务请求任务ID
	@XmlElement(required=true, nillable=false)
	private String dataStatus;		//数据状态：1，已生成；2，未生成
	@XmlElement(required=true, nillable=false)
	private int dataCount;			//数据条数
	@XmlElement(required=true, nillable=false)
	private String ip;				
	@XmlElement(required=true, nillable=false)
	private String port;
	@XmlElement(required=true, nillable=false)
	private String userName;
	@XmlElement(required=true, nillable=false)
	private String password;
	@XmlElement(required=true, nillable=false)
	private String filePath;
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getDataStatus() {
		return dataStatus;
	}
	public void setDataStatus(String dataStatus) {
		this.dataStatus = dataStatus;
	}
	public int getDataCount() {
		return dataCount;
	}
	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
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
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
