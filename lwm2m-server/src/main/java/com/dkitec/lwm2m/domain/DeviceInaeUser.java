package com.dkitec.lwm2m.domain;

import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.PropertiesUtil;

public class DeviceInaeUser {

	/** 단말 아이디 또는 서비스 아이디*/
	private String deviceId;
	
	/** 서비스 아이디*/
	private String inaeId;
	
	/** 사용자 아이디*/
	private String userId;
	
	/** transaction Id*/	
	private Long transationID;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getInaeId() {
		return inaeId;
	}

	public void setInaeId(String inaeId) {
		this.inaeId = inaeId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Long getTransationID() {
		return transationID;
	}

	public void setTransationID(Long transationID) {
		this.transationID = transationID;
	}
	
	public void setUndefinedServiceUser(){
		this.inaeId = CommonUtil.nvl(PropertiesUtil.get("serverconfig", "message.serviceId.unknown"), "unknown");
		this.userId = CommonUtil.nvl(PropertiesUtil.get("serverconfig", "message.userId.unknown"), "unknown");
	}
}
