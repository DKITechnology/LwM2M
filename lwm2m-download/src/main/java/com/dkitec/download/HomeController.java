package com.dkitec.download;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 서버 확인용 HTTP 컨트롤러
 */
@RestController
public class HomeController {
	
	private final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Value("#{commonProps['coap.port']}")
	private String COAP_PORT;
	
	@Value("#{commonProps['coap.downloadpath']}")
	private String DOWNLOAD_PATH;
	
	/**
	 * 서버 확인용 HTTP GET
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale) {
		logger.info("Welcome home! The client locale is {}.", locale);
		
		return "LwM2M firmware download server is now available. CoAP Port : " + COAP_PORT + ", Download Path : " + DOWNLOAD_PATH;
	}
	
}