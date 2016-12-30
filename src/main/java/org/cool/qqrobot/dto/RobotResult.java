package org.cool.qqrobot.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

public class RobotResult<T> {
	// 请求是否成功
	private boolean success;
	// 0 成功 -1异常  1session失效
	private int code;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T data;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String error;
	
	public RobotResult(boolean success, int code, String error) {
		super();
		this.success = success;
		this.code = code;
		this.error = error;
	}
	public RobotResult(boolean success, int code, T data) {
		super();
		this.success = success;
		this.code = code;
		this.data = data;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
}
