package com.dkitec.lwm2m.domain.message;

import java.util.Map;

public class MessageRequestVO {

	/** 수신 일시 */
	private String creDatm;
	
	/** HTTP method */
	private String  method;
	
	/** 요청 URI 정보*/
	private String uri;
	
	/** 요청 header 정보 */
	private Map<String, String> headers;
	
	/** 요청 body 정보*/
	private String body;
	
	/** 요청 request 주소*/
	private String requestIp;

	public String getCreDatm() {
		return creDatm;
	}

	public void setCreDatm(String creDatm) {
		this.creDatm = creDatm;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
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

	public String getRequestIp() {
		return requestIp;
	}

	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	}
}
