package com.dkitec.argosiot.commonapi.domain;

import java.util.HashMap;
import java.util.List;

/**
 * <b>클래스 설명</b>  : API 처리결과 도메인
 * @author : DKI
 */
public class ResponseContent {
	
	private String resultType;
	private List<String> resultOrderNoList;
	private List<String> resultNameList;
	private HashMap<String, Object> content;

	public ResponseContent() {}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public List<String> getResultOrderNoList() {
		return resultOrderNoList;
	}

	public void setResultOrderNoList(List<String> resultOrderNoList) {
		this.resultOrderNoList = resultOrderNoList;
	}

	public List<String> getResultNameList() {
		return resultNameList;
	}

	public void setResultNameList(List<String> resultNameList) {
		this.resultNameList = resultNameList;
	}

	public HashMap<String, Object> getContent() {
		return content;
	}

	public void setContent(HashMap<String, Object> content) {
		this.content = content;
	}
}
