package com.dkitec.lwm2m.domain.message;

import java.util.Map;

public class MessageResponseVO {

	/** 응답 일시 */
	private String creDatm;
	
	/** 응답 코드 */
	private String responseCode;
	
	/** 응답 헤더 정보 */
	private Map<String, String> headers;
	
	/** 응답 body 정보*/
	private String body;

	public String getCreDatm() {
		return creDatm;
	}

	public void setCreDatm(String creDatm) {
		this.creDatm = creDatm;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
}
