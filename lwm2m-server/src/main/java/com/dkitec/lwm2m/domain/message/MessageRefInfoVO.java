package com.dkitec.lwm2m.domain.message;

public class MessageRefInfoVO {
	
	/**oneM2M container 명*/
	private String containerName;
	
	/**에러 코드*/
	private String errorCode;

	/**에러 메시지*/
	private String errorMessage;
	
	/**API 식별 아이디*/
	private String apiSerno;
	
	/**CoAP TOKEN ID*/
	private String tokenId;

	/**Request Type : CON, RST*/
	private String coapReqType;
	
	/**Response Type : ACK, RST, NON*/
	private String coapResType;
		
	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getApiSerno() {
		return apiSerno;
	}

	public void setApiSerno(String apiSerno) {
		this.apiSerno = apiSerno;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getCoapReqType() {
		return coapReqType;
	}

	public void setCoapReqType(String coapReqType) {
		this.coapReqType = coapReqType;
	}

	public String getCoapResType() {
		return coapResType;
	}

	public void setCoapResType(String coapResType) {
		this.coapResType = coapResType;
	}

}
