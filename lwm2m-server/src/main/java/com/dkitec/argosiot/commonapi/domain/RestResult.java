package com.dkitec.argosiot.commonapi.domain;

/**
 * <b>클래스 설명</b>  : API 에러 정보 도메인
 * @author : DKI
 */
public class RestResult {

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private int code;
	private String message;

	public RestResult() {
		
	}
	
	public RestResult(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
