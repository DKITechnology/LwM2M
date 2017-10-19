package com.dkitec.lwm2m.domain;

public class SecurityDataInfo {
	
	/** 단말 아이디 **/
	private String deviceId;

	/** 단말 보안 유형*/
	private String securityType;
	
	/** PSK 인증, 단말 인증 아이디 : 없는 경우 단말 아이디로 사용*/
	private String identity;
	
	/** PSK 인증, 단말 패스워드*/
	private String key;
	
	/** RPK 인증, X public key part */
	private String xPublicKey;
	
	/** RPK 인증, Y public key part */
	private String yPublicKey;
	
	/** RPK 인증, EC parameters*/
	private String eCParam;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getSecurityType() {
		return securityType;
	}

	public void setSecurityType(String securityType) {
		this.securityType = securityType;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getxPublicKey() {
		return xPublicKey;
	}

	public void setxPublicKey(String xPublicKey) {
		this.xPublicKey = xPublicKey;
	}

	public String getyPublicKey() {
		return yPublicKey;
	}

	public void setyPublicKey(String yPublicKey) {
		this.yPublicKey = yPublicKey;
	}

	public String geteCParam() {
		return eCParam;
	}

	public void seteCParam(String eCParam) {
		this.eCParam = eCParam;
	}
}
