package com.dkitec.lwm2m.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObserveInfoVO {
	
	/** 구독 URL 정보**/
	private String subURL;
	
	/** cancel 유형 : reset**/
	private String cancelTpye;
	
	/** observation 취소 개수*/
	private String cancelCnt;

	public String getSubURL() {
		return subURL;
	}

	public void setSubURL(String subURL) {
		this.subURL = subURL;
	}

	public String getCancelTpye() {
		return cancelTpye;
	}

	public void setCancelTpye(String cancelTpye) {
		this.cancelTpye = cancelTpye;
	}

	public String getCancelCnt() {
		return cancelCnt;
	}

	public void setCancelCnt(String cancelCnt) {
		this.cancelCnt = cancelCnt;
	}
}
