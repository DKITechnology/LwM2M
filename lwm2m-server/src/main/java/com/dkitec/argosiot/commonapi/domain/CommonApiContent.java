package com.dkitec.argosiot.commonapi.domain;

import java.util.List;

/**
 * <b>클래스 설명</b>  : Common API Value Object Class 
 * @author : DKI
 */
public class CommonApiContent {

	/**
	 * API 입력 항목 정의 객체 리스트
	 */
	private List<RequestContent> requestContent;
	
	/**
	 * API 처리 항목 정의 객체 리스트 
	 */
	private List<ProcessContent> processContent;
	
	/**
	 * API 처리 결과 객체 
	 */
	private ResponseContent responseContent;
	
	public List<RequestContent> getRequestContent() {
		return requestContent;
	}

	public void setRequestContent(List<RequestContent> requestContent) {
		this.requestContent = requestContent;
	}

	public List<ProcessContent> getProcessContent() {
		return processContent;
	}

	public void setProcessContent(List<ProcessContent> processContent) {
		this.processContent = processContent;
	}

	public ResponseContent getResponseContent() {
		return responseContent;
	}

	public void setResponseContent(ResponseContent responseContent) {
		this.responseContent = responseContent;
	}
}
