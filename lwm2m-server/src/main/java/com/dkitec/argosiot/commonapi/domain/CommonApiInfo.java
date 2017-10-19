package com.dkitec.argosiot.commonapi.domain;

/**
 * <b>클래스 설명</b>  : Common API Value Object Class 
 * @author : DKI
 */
public class CommonApiInfo {

	/**
	 * Primary Key 자동생성
	 */
	private int serialNo;
	
	/**
	 * API URI 메인 모듈 명 
	 */
	private String mainModuleName;
	
	/**
	 * API URI 버전 
	 */
	private String version;
	
	/**
	 * API URI 서브 모듈 명 
	 */
	private String subModuleName;
	
	/**
	 * API 호출 허용 메서드(POST, PUT, DELETE, GET) 
	 */
	private String method;
	
	/**
	 * API 인증 여부
	 */
	private String apiAttcYn;
	
	/**
	 * API 처리 내용  
	 */
	private String jsonContent;
	
	public int getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(int serialNo) {
		this.serialNo = serialNo;
	}

	public String getMainModuleName() {
		return mainModuleName;
	}

	public void setMainModuleName(String mainModuleName) {
		this.mainModuleName = mainModuleName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSubModuleName() {
		return subModuleName;
	}

	public void setSubModuleName(String subModuleName) {
		this.subModuleName = subModuleName;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getApiAttcYn() {
		return apiAttcYn;
	}

	public void setApiAttcYn(String apiAttcYn) {
		this.apiAttcYn = apiAttcYn;
	}

	public String getJsonContent() {
		return jsonContent;
	}

	public void setJsonContent(String jsonContent) {
		this.jsonContent = jsonContent;
	}
}
