package com.dkitec.lwm2m.domain;

public class AuthInfo {

	/**
	 * 서비스 아이디 또는 관리자 아이디
	 */
	private String authId;
	
	/**
	 * 서비스 토큰 또는 관리지 토큰
	 */
	private String authPwd;

	public String getAuthId() {
		return authId;
	}

	public void setAuthId(String authId) {
		this.authId = authId;
	}

	public String getAuthPwd() {
		return authPwd;
	}

	public void setAuthPwd(String authPwd) {
		this.authPwd = authPwd;
	}
}
