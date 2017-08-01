package com.gztydic.gateway.gather.webservice.client.data;

/***
 * 数据特区主动推送服务数据响应
 * @author lenovo
 *
 */
public class ResponseVO {
	private String resultFlag;
	private String message;
	public String getResultFlag() {
		return resultFlag;
	}
	public void setResultFlag(String resultFlag) {
		this.resultFlag = resultFlag;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
