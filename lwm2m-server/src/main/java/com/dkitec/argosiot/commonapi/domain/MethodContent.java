package com.dkitec.argosiot.commonapi.domain;

/**
 * <b>클래스 설명</b>  : 메서드 실행 프로세스 도메인
 * @author : DKI
 */
public class MethodContent {
		
	/**
	 * 호출할 클래스명
	 */
	private String className;
	
	/**
	 * 호출할 메서드명 
	 */
	private String methodName;

	public MethodContent() {}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
}
