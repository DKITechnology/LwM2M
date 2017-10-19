package com.dkitec.lwm2m.common.code;

/**
 * 공통 코드
 */
public class ComCode {
	
	public static final String OAMORIGIN = "ArgosPlatformOAM";

	/**
	 * Activity Work flow Variable Name 정의
	 */
	public static enum VariableKey
	{
		FwUpdateValues("FwUpdateValues");
		
		private final String value;	
		
		private VariableKey(String value) {
            this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
		
	/**
	 * Date Format 형식
	 */
	public static enum DataFormat{
		DefaultDateStrFormat("yyyyMMddHHmm"),
		DateStrFormat("yyyyMMddHHmmss");
		private final String value;	
		
		private DataFormat(String value) {
            this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return value;
		}	
	}
	
	//RDB 019
	public static enum ErrorClassCode
	{
		//RECEIVE ERROR
		Critical("001"),
		Major("002"),
		Minor("003"),
		Warning("004");	
				
		private final String value;	
		
		private ErrorClassCode(String value) {
            this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	/**
	 * 송수신 패킷 (메시지) 정보 유형
	 */
	public static enum MessageType
	{
		RECIEVE("R"),
		HTTP("H"),
		PUSH("P"),
		SMS("S"),
		EMAIL("E"),
		Coap("C"),;
		
		private final String value;	
		
		private MessageType(String value) {
            this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	//RDB 020
	public static enum ErrorTypeCode
	{
		//RECEIVE ERROR
		RCV_GN_ERROR("001"),
		RCV_ERROR("002"),
		RCV_CONVS_ERROR("003"),	
		RCV_PUSH_ERROR("004"),			
		RCV_DECD_ERROR("005"),
		RCV_AUTH_ERROR("006"),
		RCV_RES_RES_ERROR("007"),
		RCV_NOTI_ERROR("008"),
		
		//SEND ERROR
		SND_GN_ERROR("011"),
		SND_CONV_ERROR("012"),
		SND_ENCD_ERROR("013"),
		SND_PUSH_ERROR("014"),
		SND_AUTH_ERROR("015"),
		SND_ERROR("016"),
		SND_RES_ERROR("017"),
		SND_NOTI_ERROR("018"),
		SYS_ERROR("019");
		
		private final String value;	
		
		private ErrorTypeCode(String value) {
            this.value = value;
		}
		
		public String getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return value;
		}
	}
	
	///////////////////////////////////Lwm2m 공통 코드
		
	public static enum ResponseHeader
	{
		coapResultCode("CoapResultCd");
		
		private final String value;
		private ResponseHeader(String value) {
            this.value = value;
		}		
		public String getValue() {
			return value;
		}
	}
	
	public static enum Lwm2mKey
	{
		subscriptionURL("subURL");
		
		private final String value;
		private Lwm2mKey(String value) {
            this.value = value;
		}		
		public String getValue() {
			return value;
		}
	}
	
	public static enum SecurityTypeCd
	{
		RPK("1"),
		PSK("2"),
		CERTY("3");
		
		private final String value;
		private SecurityTypeCd(String value) {
            this.value = value;
		}		
		public String getValue() {
			return value;
		}
	}
	
	public static enum ProcessCode
	{
		NOTSTART("0"),
		PROCEEDING("1"),
		COMPLETED("2");
		
		private final String value;
		private ProcessCode(String value) {
            this.value = value;
		}		
		public String getValue() {
			return value;
		}
	}
	
	public static enum RequestResCode
	{
		SUCCESS("001"),
		FAIL("002");
		
		private final String value;
		private RequestResCode(String value) {
            this.value = value;
		}		
		public String getValue() {
			return value;
		}
	}
	
	public static enum MessageProtocol
	{
		HTTP("H"),
		HTTPS("HS"),
		COAP("C"),
		COAPS("CS"),
		MQTT("M");
		
		private final String value;
		private MessageProtocol(String value) {
            this.value = value;
		}		
		public String getValue() {
			return value;
		}
	}
	
	public static enum MessageAppProtocol
	{
		oneM2M("O"),
		lwM2M("L"),
		PCD01("P"),
		FHIR("F"),
		OPENAPI("O");
		
		private final String value;
		private MessageAppProtocol(String value) {
            this.value = value;
		}		
		public String getValue() {
			return value;
		}
	}
}
