package com.gztydic.gateway.core.common.util;

public class AjaxResult {

	private Object data;
	private String state;
	private String message;

	public static final String SUCCESS = "success";
	public static final String FAILURE = "failure";
	public static final String WARN = "warn";
	public static final String ERROR = "error";

	public static AjaxResult ERROR(Object data, String message) {
		AjaxResult result = new AjaxResult();
		result.data = data;
		result.state = result.ERROR;
		result.message = message;
		return result;
	}

	public static AjaxResult WARN(Object data, String message) {
		AjaxResult result = new AjaxResult();
		result.data = data;
		result.state = result.WARN;
		result.message = message;
		return result;
	}

	public static AjaxResult SUCCESS(Object data, String message) {
		AjaxResult result = new AjaxResult();
		result.data = data;
		result.state = result.SUCCESS;
		result.message = message;
		return result;
	}
	
	public static AjaxResult FAILURE(Object data, String message) {
		AjaxResult result = new AjaxResult();
		result.data = data;
		result.state = result.FAILURE;
		result.message = message;
		return result;
	}
	
	public static AjaxResult FAILURE(){
		return AjaxResult.FAILURE(null, null);
	}

	public static AjaxResult SUCCESS(Object data) {
		return AjaxResult.SUCCESS(data, null);
	}

	public static AjaxResult SUCCESS() {
		return AjaxResult.SUCCESS(null, null);
	}

	public Object getData() {
		return data;
	}

	public String getState() {
		return state;
	}

	public String getMessage() {
		return message;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
