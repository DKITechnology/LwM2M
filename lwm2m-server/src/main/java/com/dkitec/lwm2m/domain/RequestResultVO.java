package com.dkitec.lwm2m.domain;

import com.dkitec.lwm2m.common.code.CoapCode;

public class RequestResultVO<T> {
	
	/** coap 응답 코드 : ex : content*/
	String coapResultCd;
	
	/** coap 응답 상태 코드 : status code 2.01 */
	String coapStatusCd;
	
	/** rest 응답 메시지**/
	String resultMsg;
	
	/** 응답 결과 정보*/
	T resultData;

	public String getCoapResultCd() {
		return coapResultCd;
	}

	public void setCoapResultCd(String coapResultCd) {
		coapStatusCd = String.valueOf(CoapCode.CoapRespCode.getStatusCdToResult(coapResultCd));
		this.coapResultCd = coapResultCd;
	}

	public String getCoapStatusCd() {
		return coapStatusCd;
	}

	public void setCoapStatusCd(String coapStatusCd) {
		this.coapStatusCd = coapStatusCd;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public T getResultData() {
		return resultData;
	}

	public void setResultData(T resultData) {
		this.resultData = resultData;
	}
}
