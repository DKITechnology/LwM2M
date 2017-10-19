package com.dkitec.argosiot.commonapi.domain;

import java.util.List;

/**
 * <b>클래스 설명</b>  : API 입력항목 도메인
 * @author : DKI
 */
public class RequestContent {

	private String name;
	private String type;
	private String defaultValue;
	private FeildValidation validation;
	private List<RequestContent> subKeyList;
	
	public RequestContent() {}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public FeildValidation getValidation() {
		return validation;
	}

	public void setValidation(FeildValidation validation) {
		this.validation = validation;
	}

	public List<RequestContent> getSubKeyList() {
		return subKeyList;
	}

	public void setSubKeyList(List<RequestContent> subKeyList) {
		this.subKeyList = subKeyList;
	}
}
