package com.dkitec.argosiot.commonapi;

/**
 * <b>클래스 설명</b>	: 공통 API 사용자 정의 예외
 * @author DKI
 * @see java.lang.Exception
 */
public class CommonApiException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5067834423529768818L;

	/**
	 * 사용자 정의 에러 발생 단계
	 */
	private String logStep;

	/**
	 * 사용자 정의 에러 코드
	 */
	private int code;
	
	/**
	 * 사용자 정의 에러 메시지
	 */
	private String msg;
	
	/**
	 * Exception Object
	 */
	private Exception e;

	/**
	 * <b>생성자 설명</b> 	: 공통 API 예외 생성자(정의되지 않은 예외)
	 * @param e			: Exception object
	 * @param logStep	: 사용자 정의 에러 발생 단계
	 * @param msg		: 사용자 정의 에러 메시지
	 */
	public CommonApiException(Exception e, String logStep, String msg) {		
		this.e = e;
        this.code = 9999;
        this.logStep = logStep;
        this.msg = msg;
    }
	
	/**
	 * <b>생성자 설명</b> 	: 공통 API 예외 생성자(사용자 정의 예외)
	 * @param e 		: Exception object
	 * @param logStep 	: 사용자 정의 에러 발생 로그 포인트
	 * @param code		: 사용자 정의 에러 코드
	 * @param msg		: 사용자 정의 에러 메시지
	 */
	public CommonApiException(Exception e, String logStep, int code, String msg) {		
        this.logStep = logStep;
        this.code = code;
        this.msg = msg;
    }
	
	/**
	 * <b>생성자 설명</b> 	: 공통 API 예외 생성자(사용자 정의 예외)
	 * @param e 		: Exception object
	 * @param logStep 	: 사용자 정의 에러 발생 로그 포인트
	 * @param code		: 사용자 정의 에러 코드
	 * @param msg		: 사용자 정의 에러 메시지
	 */
	public CommonApiException(Exception e, String logStep, String code, String msg) {
		this(e, logStep, Integer.parseInt(code), msg);
	}
	
	public Exception getException() { return this.e; }
	public String getLogStep() { return this.logStep; }
	public int getCode() { return this.code; }
	public String getMsg() { return this.msg; }
}
