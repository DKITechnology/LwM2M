package com.dkitec.lwm2m.domain;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.dkitec.lwm2m.common.code.ComCode;
import com.dkitec.lwm2m.common.util.PageDateParmVO;

public class FwUpdateVO implements Serializable{

	private static final long serialVersionUID = 1L;

	/** 요청 아이디 */
	private String reqId;
	
	/** 요청 유형 : 펌웨어*/
	private String reqType = "001";
	
	/** 요청 유형 설명 */
	private String reqDesc;
	
	/** 서비스 아이디 **/
	private String infraAeId ="";
	
	/** 사용자 아이디 **/
	private String userId ="";
	
	/** 펌에어 아이디**/
	private String fiwrId ="";
	
	/** 요청 일시 */
	private Date reqDatm;
	
	/** 요청일시 */
	private String reqDatmStr;
	
	/** 생성 일시*/
	private Date creDatm;
	
	private String creDatmStr;
	
	/** 진행 상태 코드*/
	private String procCd;
	
	/** 요청 단말 목록 정보*/
	private List<String> deviceIds;
	
	/** 요청 결과 정보*/
	private List<FwUpdateResultVO> reqResults;

	public String getReqId() {
		return reqId;
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public String getReqType() {
		return reqType;
	}

	public String getReqDesc() {
		return reqDesc;
	}

	public void setReqDesc(String reqDesc) {
		this.reqDesc = reqDesc;
	}

	public String getInfraAeId() {
		return infraAeId;
	}

	public void setInfraAeId(String infraAeId) {
		this.infraAeId = infraAeId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFiwrId() {
		return fiwrId;
	}

	public void setFiwrId(String fiwrId) {
		this.fiwrId = fiwrId;
	}

	public Date getReqDatm() {
		return reqDatm;
	}

	public void setReqDatm(Date reqDatm) {
		this.reqDatm = reqDatm;
	}
	
	public void setReqDatmStr(String reqDatm) {
		this.reqDatmStr = reqDatm;
		if(reqDatm.length() == 12){
			SimpleDateFormat transFormat = new SimpleDateFormat(ComCode.DataFormat.DefaultDateStrFormat.getValue());
			try {
				Date reqDate = transFormat.parse(reqDatm);
				this.reqDatm = reqDate;
			} catch (ParseException e) {}
		}
	}

	public String getReqDatmStr() {
		try {
			Date reqDate = this.reqDatm;
			SimpleDateFormat transFormat = new SimpleDateFormat(ComCode.DataFormat.DefaultDateStrFormat.getValue());
			return transFormat.format(reqDate);
		} catch (Exception e) {return null;}
	}

	public Date getCreDatm() {
		return creDatm;
	}
	
	public void setCreDatm(Date creDatm) {
		this.creDatm = creDatm;
	}

	public String getCreDatmStr() {
		try {
			Date creDate = this.creDatm;
			SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMddHHmm");
			return transFormat.format(creDate);
		} catch (Exception e) {return null;}
	}

	public String getProcCd() {
		return procCd;
	}

	public List<String> getDeviceIds() {
		return deviceIds;
	}

	public void setDeviceIds(List<String> deviceIds) {
		this.deviceIds = deviceIds;
	}

	public List<FwUpdateResultVO> getReqResults() {
		return reqResults;
	}

	public void setReqResults(List<FwUpdateResultVO> reqResults) {
		this.reqResults = reqResults;
	}
}
