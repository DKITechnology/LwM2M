package com.dkitec.lwm2m.domain;

import java.util.Date;

import org.eclipse.leshan.server.registration.Registration;

import com.dkitec.lwm2m.common.util.PageDateParmVO;

public class DeviceInfoVO extends PageDateParmVO{

	/** 단말아이디 **/
	private String devcId;
	
	/** 서비스 아이디  **/
	private String infraAeId;
	
	/** 단말 모델 코드 **/
	private String devcModlCd;
	
	/** 단말 모델 명 **/
	private String devcModlNm;
	
	/** 단말 유형 코드 **/
	private String devcTypeCd;
	
	/** 단말 유형 코드 명 **/
	private String devcTypeCdStr;
	
	/** 단말 펌웨어 아이디 **/
	private String fiwrId;
	
	/** 단말 시리얼 번호 **/	
	private String serialNo;
	
	/** 단말 토큰 정보 **/
	private String attcToken;
	
	/** 단말 명 **/
	private String devcNm;
	
	/** 단말 설명 **/
	private String devcDesc;
	
	/** ip 주소 **/
	private String ipAddr;
	
	/** 단말 URL 주소 **/
	private String devcUrl;
	
	/** 단말 인증 유형 **/
	private String attcType;
	
	/** 단말 패스워드 **/
	private String attcPw;
	
	/** 단말 상태 코드 **/
	private String devcStatcd;
	
	/** 게이트 웨이 단말 아이디 **/
	private String gwDevcId;
	
	/** 단말 마지막 통신 시간 **/
	private Date lastConnDatm;
	
	/** Lwm2m 단말 등록 정보 **/
	private Registration registration;
	
	/** 단말 포트 정보 **/
	private String devcPort;
	
	/** 단말 서버 아이디 **/
	private String devcServId;
	
	public String getDevcId() {
		return devcId;
	}
	public void setDevcId(String devcId) {
		this.devcId = devcId;
	}
	public String getInfraAeId() {
		return infraAeId;
	}
	public void setInfraAeId(String infraAeId) {
		this.infraAeId = infraAeId;
	}
	public String getDevcModlCd() {
		return devcModlCd;
	}
	public void setDevcModlCd(String devcModlCd) {
		this.devcModlCd = devcModlCd;
	}
	public String getDevcModlNm() {
		return devcModlNm;
	}
	public void setDevcModlNm(String devcModlNm) {
		this.devcModlNm = devcModlNm;
	}
	public String getDevcTypeCd() {
		return devcTypeCd;
	}
	public void setDevcTypeCd(String devcTypeCd) {
		this.devcTypeCd = devcTypeCd;
	}
	public String getDevcTypeCdStr() {
		return devcTypeCdStr;
	}
	public void setDevcTypeCdStr(String devcTypeCdStr) {
		this.devcTypeCdStr = devcTypeCdStr;
	}
	public String getFiwrId() {
		return fiwrId;
	}
	public void setFiwrId(String fiwrId) {
		this.fiwrId = fiwrId;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public String getAttcToken() {
		return attcToken;
	}
	public void setAttcToken(String attcToken) {
		this.attcToken = attcToken;
	}
	public String getDevcNm() {
		return devcNm;
	}
	public void setDevcNm(String devcNm) {
		this.devcNm = devcNm;
	}
	public String getDevcDesc() {
		return devcDesc;
	}
	public void setDevcDesc(String devcDesc) {
		this.devcDesc = devcDesc;
	}
	public String getIpAddr() {
		return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}
	public String getDevcUrl() {
		return devcUrl;
	}
	public void setDevcUrl(String devcUrl) {
		this.devcUrl = devcUrl;
	}
	public String getAttcType() {
		return attcType;
	}
	public Registration getRegistration() {
		return registration;
	}
	public String getDevcPort() {
		return devcPort;
	}
	public void setDevcPort(String devcPort) {
		this.devcPort = devcPort;
	}
	public String getDevcServId() {
		return devcServId;
	}
	public void setDevcServId(String devcServId) {
		this.devcServId = devcServId;
	}
	public void setRegistration(Registration registration) {
		this.registration = registration;
	}
	public void setAttcType(String attcType) {
		this.attcType = attcType;
	}
	public String getAttcPw() {
		return attcPw;
	}
	public void setAttcPw(String attcPw) {
		this.attcPw = attcPw;
	}
	public String getDevcStatcd() {
		return devcStatcd;
	}
	public void setDevcStatcd(String devcStatcd) {
		this.devcStatcd = devcStatcd;
	}
	public String getGwDevcId() {
		return gwDevcId;
	}
	public void setGwDevcId(String gwDevcId) {
		this.gwDevcId = gwDevcId;
	}
	public Date getLastConnDatm() {
		return lastConnDatm;
	}
	public void setLastConnDatm(Date lastConnDatm) {
		this.lastConnDatm = lastConnDatm;
	}
}
