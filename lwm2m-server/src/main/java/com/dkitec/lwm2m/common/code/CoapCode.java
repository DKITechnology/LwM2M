package com.dkitec.lwm2m.common.code;

import java.util.HashMap;
import java.util.Map;

/**
 * CoAPCode 관련 정보
 */
public class CoapCode {

	/**
	 * CoAP 응답 코드 
	 */
	public static enum CoapRespCode {
		// Success: "", 2.01 - 2.31
		SUCCESS_CODE("SUCCESS_CODE", 2.00), // undefined -- only used to identify class
		CREATED("CREATED", 2.01),
		DELETED("DELETED", 2.02),
		VALID("VALID", 2.03),
		CHANGED("CHANGED", 2.04),
		CONTENT("CONTENT", 2.05),

		// Client error: "", 4.00 - 4.31
		BAD_REQUEST("BAD_REQUEST", 4.00),
		UNAUTHORIZED("UNAUTHORIZED", 4.01),
		BAD_OPTION("BAD_OPTION", 4.02),
		FORBIDDEN("BAD_OPTION", 4.03),
		NOT_FOUND("NOT_FOUND", 4.04),
		METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", 4.05),
		NOT_ACCEPTABLE("NOT_ACCEPTABLE", 4.06),
		REQUEST_ENTITY_INCOMPLETE("REQUEST_ENTITY_INCOMPLETE", 4.08),
		PRECONDITION_FAILED("PRECONDITION_FAILED", 4.12),
		REQUEST_ENTITY_TOO_LARGE("REQUEST_ENTITY_TOO_LARGE", 4.13),
		UNSUPPORTED_CONTENT_FORMAT("UNSUPPORTED_CONTENT_FORMAT", 4.15),

		// Server error: 5.00 - 5.31
		INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR",5.00),
		NOT_IMPLEMENTED("NOT_IMPLEMENTED",5.01),
		BAD_GATEWAY("BAD_GATEWAY",5.02),
		SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE",5.03),
		GATEWAY_TIMEOUT("GATEWAY_TIMEOUT",5.04),
		PROXY_NOT_SUPPORTED("PROXY_NOT_SUPPORTED",5.05);
		
		public String coapResultCd;
		public double coapStatusCd;
		
		 private static Map<String, Double> valueMap;
		
		private CoapRespCode(String coapResultCd, double coapStatusCd){
			this.coapResultCd = coapResultCd;
			this.coapStatusCd = coapStatusCd;
		}
		
		public double getStatusCd(String coapResultCd){
			return coapStatusCd;
		}
		
		public static Double getStatusCdToResult(String coapResultCd){
			if(valueMap == null)
				initValueMap();
			return valueMap.get(coapResultCd);
		}
		
		private static void initValueMap(){
			valueMap = new HashMap<String, Double>();
			for(CoapRespCode code : values()){
				valueMap.put(code.coapResultCd, code.coapStatusCd);
			}
		}
	}
	
//	public static void main(String[] args) {
//		System.out.println(CoapRespCode.getStatusCdToResult("CONTENT"));
//	}
}
