package com.dkitec.lwm2m.domain;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.leshan.util.Hex;

public class CoapResponseVO implements Serializable{

	private String code;
	
	private int rtt;
	
	private String type;
	
	private int mid;
	
	private byte[] token;
		
	private Map<String, Object> options;
	
	private byte[] payload;
	
	private String source;
	
	private int sourcePort;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getRtt() {
		return rtt;
	}

	public void setRtt(int rtt) {
		this.rtt = rtt;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getMid() {
		return mid;
	}

	public void setMid(int mid) {
		this.mid = mid;
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}

	public Map<String, Object> getOptions() {
		return options;
	}

	public void setOptions(Map<String, Object> options) {
		this.options = options;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(int sourcePort) {
		this.sourcePort = sourcePort;
	}
	
	public String toStringToken(){
		return new String(token, StandardCharsets.UTF_8);
	}
	
	public String toStringPayload(){
		String strPayload = new String(payload, StandardCharsets.UTF_8);
		if (!StringUtils.isAsciiPrintable(strPayload)) {
			strPayload = "Hex:" + Hex.encodeHexString(payload);
		}
		return strPayload;
	}
}
