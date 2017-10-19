package com.dkitec.lwm2m.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FwUpdateReqInfoVO extends FwUpdateReqVO{

	private static final long serialVersionUID = 1L;

	/**
	 * 요청 진행 상태가 진행 되지 않았거나 진행 중인 개수 
	 */
	private int procIngCnt;
	
	/**
	 * 요청 진행 상태가 완료된 갯수
	 */
	private int procComplCnt;
	
	/**
	 * 요청 결과가 성공한 개수
	 */
	private int resSucCnt;
	
	/**
	 * 요청 결과가 실패한 개수
	 */
	private int resFailCnt;
	
	public int getProcIngCnt() {
		return procIngCnt;
	}

	public void setProcIngCnt(int procIngCnt) {
		this.procIngCnt = procIngCnt;
	}

	public int getProcComplCnt() {
		return procComplCnt;
	}

	public void setProcComplCnt(int procComplCnt) {
		this.procComplCnt = procComplCnt;
	}

	public int getResSucCnt() {
		return resSucCnt;
	}

	public void setResSucCnt(int resSucCnt) {
		this.resSucCnt = resSucCnt;
	}

	public int getResFailCnt() {
		return resFailCnt;
	}

	public void setResFailCnt(int resFailCnt) {
		this.resFailCnt = resFailCnt;
	}
}
