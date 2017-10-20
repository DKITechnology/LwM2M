package com.dkitec.lwm2m.domain;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dkitec.lwm2m.common.util.CommonUtil;

public class FwUpdateResultVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String deviceReqId;

	private String deviceId;
	
	private String deviceModelCd;
	
	private String devcFiwrId;
	
	private String reqProcCd;
	
	private String reqResCd;
	
	private String reqResMsg;
	
	private Date mdfDatm;
	
	private String mdfDatmStr;
	
	private String deviceSerno;

	public String getDeviceReqId() {
		return deviceReqId;
	}

	public void setDeviceReqId(String deviceReqId) {
		this.deviceReqId = deviceReqId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceModelCd() {
		return deviceModelCd;
	}

	public void setDeviceModelCd(String deviceModelCd) {
		this.deviceModelCd = deviceModelCd;
	}

	public String getDevcFiwrId() {
		if(CommonUtil.isEmpty(devcFiwrId))
			this.devcFiwrId = "";
		return devcFiwrId;
	}

	public void setDevcFiwrId(String devcFiwrId) {
		this.devcFiwrId = devcFiwrId;
	}

	public String getReqProcCd() {
		return reqProcCd;
	}
	
	/**
	 * 요청 진행 상태
	 * @param reqProcCd '0' 진행전, '1' 진행중, '2' 진행 완료
	 */
	public void setReqProcCd(String reqProcCd) {
		this.reqProcCd = reqProcCd;
	}

	public String getReqResCd() {
		return reqResCd;
	}

	/**
	 * 요청 진행 결과
	 * @param reqResCd '001' 성공, '002' 실패
	 */
	public void setReqResCd(String reqResCd) {
		this.reqResCd = reqResCd;
	}

	public String getReqResMsg() {
		return reqResMsg;
	}

	public void setReqResMsg(String reqResMsg) {
		this.reqResMsg = reqResMsg;
	}

	public Date getMdfDatm() {
		return mdfDatm;
	}

	public void setMdfDatm(Date mdfDatm) {
		this.mdfDatm = mdfDatm;
	}

	public String getMdfDatmStr() {
		try {
			Date mdfDate = this.mdfDatm;
			SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMddHHmm");
			return transFormat.format(mdfDate);
		} catch (Exception e) {return null;}
	}
	
	public void setDeviceSerno(String deviceSerno) {
		this.deviceSerno = deviceSerno;
	}
	
	public String getDeviceSerno() {
		return deviceSerno;
	}
}
