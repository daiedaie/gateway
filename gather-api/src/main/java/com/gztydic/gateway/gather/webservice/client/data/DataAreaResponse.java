package com.gztydic.gateway.gather.webservice.client.data;

import java.util.List;
import java.util.Map;

public class DataAreaResponse {
	private String resultFlag;
	private String message;
	private List<Map<String, String>> dataEntity;
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
	public List<Map<String, String>> getDataEntity() {
		return dataEntity;
	}
	public void setDataEntity(List<Map<String, String>> dataEntity) {
		this.dataEntity = dataEntity;
	}

}
