package com.dkitec.argosiot.commonapi.domain;

/**
 * <b>클래스 설명</b>  : 입력 항목 검증 도메인
 * @author : DKI
 */
public class FeildValidation {
	
	/**
	 * 최대 입력 길이
	 */
	private String length;
	
	/**
	 * Null 허용 여부 (Y|N) 
	 */
	private String nullable;
	
	/**
	 * 정규식 
	 */
	private String regex;
	
	/**
	 * 참조키(해당 참조키 값이 존재하면 현재 항목 필수 처리) 
	 */
	private String referKey;
	
	/**
	 * 참조키의 값(해당 참조키가 존재하고 현재 항목의 값이 해당 값과 일치하면 필수 처리) 
	 */
	private Object referKeyValue;
	
	public FeildValidation() {}

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getNullable() {
		return nullable;
	}

	public void setNullable(String nullable) {
		this.nullable = nullable;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public String getReferKey() {
		return referKey;
	}

	public void setReferKey(String referKey) {
		this.referKey = referKey;
	}

	public Object getReferKeyValue() {
		return referKeyValue;
	}

	public void setReferKeyValue(Object referKeyValue) {
		this.referKeyValue = referKeyValue;
	}
}
