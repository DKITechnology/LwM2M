package com.dkitec.lwm2m.domain;

public class FwInfoVO {

	/** 펌웨어 아이디 */
	private int fiwrId;
	
	/** 단말 모델 코드 */
	private String devcModlCd;
	
	/** 펌웨어 업데이트 이후 버전  */
	private String afFiwrVer;
	
	/** 펌웨어 업데이트 이전 버전 */
	private String bfFiwrVer;
	
	/** 펌웨어 패키지 */
	private byte[] fiwrPkg;
	
	/** 펌웨어 패키지 URI */
	private String fiwrPkgUri;

	public int getFiwrId() {
		return fiwrId;
	}

	public void setFiwrId(int fiwrId) {
		this.fiwrId = fiwrId;
	}

	public String getDevcModlCd() {
		return devcModlCd;
	}

	public void setDevcModlCd(String devcModlCd) {
		this.devcModlCd = devcModlCd;
	}

	public String getAfFiwrVer() {
		return afFiwrVer;
	}

	public void setAfFiwrVer(String afFiwrVer) {
		this.afFiwrVer = afFiwrVer;
	}

	public String getBfFiwrVer() {
		return bfFiwrVer;
	}

	public void setBfFiwrVer(String bfFiwrVer) {
		this.bfFiwrVer = bfFiwrVer;
	}

	public byte[] getFiwrPkg() {
		return fiwrPkg;
	}

	public void setFiwrPkg(byte[] fiwrPkg) {
		this.fiwrPkg = fiwrPkg;
	}

	public String getFiwrPkgUri() {
		return fiwrPkgUri;
	}

	public void setFiwrPkgUri(String fiwrPkgUri) {
		this.fiwrPkgUri = fiwrPkgUri;
	}
}
