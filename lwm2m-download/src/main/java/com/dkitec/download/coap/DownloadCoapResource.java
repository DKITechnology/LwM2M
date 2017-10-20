package com.dkitec.download.coap;

import javax.annotation.PostConstruct;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dkitec.download.firmware.FirmwareService;
import com.dkitec.download.firmware.FirmwareVO;
import com.dkitec.download.util.LoggingUtil;

@Component
public class DownloadCoapResource extends CoapResource {
	
	private final Logger logger = LoggerFactory.getLogger(DownloadCoapResource.class);
	
	/**
	 * firmware 조회 서비스
	 */
	@Autowired
	private FirmwareService firmwareService;
	
	@Value("#{commonProps['coap.downloadpath']}")
	private String DOWNLOAD_PATH;
	
	/**
	 * 생성자
	 */
	public DownloadCoapResource() {
		super("firmware");
	}
	public DownloadCoapResource(String name) {
		super(name);
	}
	
	/**
	 * 
	 */
	@PostConstruct
	private void setPath() {
		this.setName(DOWNLOAD_PATH);
		this.setPath(DOWNLOAD_PATH);
	}
	
	/**
	 * GET 요청을 처리한다.
	 * szx 5 : 512
	 *     6 : 1024
	 *     7 : 2048 (reserved)
	 */
	@Override
	public void handleGET(CoapExchange exchange) {
		
		// response 항목들
		byte[] responsePayload = null;
		int responseContentFormat = MediaTypeRegistry.APPLICATION_OCTET_STREAM;
		OptionSet options = exchange.getRequestOptions();
		Response response = null;
		
		// logging
		LoggingUtil.loggingRequest(logger, exchange.advanced().getRequest());
		
		// coap://{}/firmware/test     --> 서버 응답 테스트용  
		if(options.getUriPath().size() == 2 && "test".equalsIgnoreCase(options.getUriPath().get(1))) {
			responsePayload = new byte[]{	// length 40 짜리 테스트 데이터
				 1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 
				11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
				21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 
				31, 32, 33, 34, 35, 36, 37, 38, 39, 40
			};
			responseContentFormat = MediaTypeRegistry.TEXT_PLAIN;
		}
		// coap://{}/firmware     --> firmware 루트 경로 조회 시 : 4.00
		else if(options.getUriPath().size() < 2) {
			exchange.respond(ResponseCode.BAD_REQUEST);
			return;
		}
		// coap://{}/firmware/1/2/3...   --> firmware 하위 경로 복수개 조회 시 : 4.04
		else if(options.getUriPath().size() > 2) {
			exchange.respond(ResponseCode.NOT_FOUND);
			return;
		}
		// coap://{}/firmware/{firmwareId}     --> 조회 시 정상 처리 가능
		else {
			String firmwareId = options.getUriPath().get(1);
			FirmwareVO firmware = firmwareService.getFirmware(firmwareId);
			
			// DB에 펌웨어 정보가 없는 경우 : 4.04
			if(firmware == null) {
				exchange.respond(ResponseCode.NOT_FOUND);
				return;
			}
			// DB에 펌웨어는 존재하지만 byteArray가 없는 경우 : 4.00
			if(firmware.getPackageArray() == null) {
				exchange.respond(ResponseCode.BAD_REQUEST);
				return;
			}
			responsePayload = firmware.getPackageArray();
		}
		
		// response 처리
		if(responsePayload != null) {
			response = new Response(ResponseCode.CONTENT);
			response.setPayload(responsePayload);
			response.getOptions().setSize2(responsePayload.length);
			response.getOptions().setContentFormat(responseContentFormat);
			exchange.respond(response);
		}
		
		// logging
		LoggingUtil.loggingResponse(logger, response);
	}

}