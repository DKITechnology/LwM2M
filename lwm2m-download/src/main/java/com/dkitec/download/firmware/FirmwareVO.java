package com.dkitec.download.firmware;

public class FirmwareVO {
	
	/** 펌웨어 아이디 */
	private String firmwareId;
	
	/** 단말 모델 코드 */
	private String deviceModelCode;
	
	/** 펌웨어 버전(TO) */
	private String toVersion;
	
	/** 펌웨어 버전(FROM) */
	private String fromVersion;
	
	/** 펌웨어 패키지 (byteArray) */
	private byte[] packageArray;

	/** 펌웨어 패키지 URI */
	private String packageUri;
	
	/** 상태 코드 */
	private String statusCode;

	
	public String getFirmwareId() {
		return firmwareId;
	}

	public void setFirmwareId(String firmwareId) {
		this.firmwareId = firmwareId;
	}

	public String getDeviceModelCode() {
		return deviceModelCode;
	}

	public void setDeviceModelCode(String deviceModelCode) {
		this.deviceModelCode = deviceModelCode;
	}

	public String getToVersion() {
		return toVersion;
	}

	public void setToVersion(String toVersion) {
		this.toVersion = toVersion;
	}

	public String getFromVersion() {
		return fromVersion;
	}

	public void setFromVersion(String fromVersion) {
		this.fromVersion = fromVersion;
	}

	public byte[] getPackageArray() {
		return packageArray;
	}

	public void setPackageArray(byte[] packageArray) {
		this.packageArray = packageArray;
	}

	public String getPackageUri() {
		return packageUri;
	}

	public void setPackageUri(String packageUri) {
		this.packageUri = packageUri;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
}