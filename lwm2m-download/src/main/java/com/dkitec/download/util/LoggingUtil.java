package com.dkitec.download.util;

import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;

public class LoggingUtil {
	
	/**
	 * CoAP request 로깅
	 * @param logger
	 * @param request
	 */
	public static void loggingRequest(Logger logger, Request request) {
		
		if(logger.isDebugEnabled()) {
			StringBuilder builder = new StringBuilder();
			
			if(request != null) {
				OptionSet options = request.getOptions();
				builder.append("\n==[ CoAP Request ]=============================================");
				if(options != null) {
					builder.append("\n URI            : " + options.getUriPathString());
				}
				if(request != null) {
					builder.append("\n MID            : " + request.getMID());
					builder.append("\n Token          : " + request.getTokenString());
					builder.append("\n Code           : " + request.getCode());
					builder.append("\n Type           : " + request.getType());
					builder.append("\n Source Address : " + request.getSource());
					builder.append("\n Source Port    : " + request.getSourcePort());
				}
				if(options != null) {
					builder.append("\n Options        : " + options.toString());
				}
				builder.append("\n===============================================================");
			}
			else {
				builder.append("request is null");
			}
		}
	}
	
	/**
	 * CoAP response 로깅
	 * @param logger
	 * @param response
	 */
	public static void loggingResponse(Logger logger, Response response) {
		
		if(logger.isDebugEnabled()) {
			StringBuilder builder = new StringBuilder();
			
			if(response != null) {
				OptionSet options = response.getOptions();
				builder.append("\n==[ CoAP Response ]============================================");
				builder.append("\n MID         : " + response.getMID());
				builder.append("\n Token       : " + response.getTokenString());
				builder.append("\n Type        : " + response.getType());
				builder.append("\n Status      : " + response.getCode());
				if(options != null) {
					builder.append("\n Options     : " + options.toString());
				}
				builder.append("\n PayloadSize : " + response.getPayloadSize());
				builder.append("\n===============================================================");
			}
			else {
				builder.append("response is null");
			}
			logger.debug(builder.toString());
		}		
	}
	
	/**
	 * CoAP exchange(request, response) 로깅
	 * @param logger
	 * @param exchange
	 * @param options
	 */
	public static void loggingExchange(Logger logger, CoapExchange exchange) {
		
		if(exchange != null && logger.isDebugEnabled()) {
			Request request = exchange.advanced().getRequest();
			Response response = exchange.advanced().getResponse();
			
			loggingRequest(logger, request);
			loggingResponse(logger, response);
		}
		else {
			logger.debug("exchange is null");
		}
	}

}
