package com.dkitec.argosiot.commonapi.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.dkitec.argosiot.commonapi.CommonApiException;

@Component
public class CommonApiAuthCheckIntcp extends HandlerInterceptorAdapter {

	private static final Log logger = LogFactory.getLog(CommonApiAuthCheckIntcp.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {		
		String logStep = "[Pre Validation]";

		try {
			logger.info("================================= Common API START =================================");
			logger.info("Request URL \t\t: " + request.getRequestURL());
			logger.info("Request CharacterEncoding \t: " + request.getCharacterEncoding());
			logger.info("Request Method \t\t: " + request.getMethod());
			logger.info("Request ContentType \t: " + request.getContentType());			
			logger.info("Request RemoteAddr \t: " + request.getRemoteAddr() + "/" + request.getRemotePort());			
			
			return super.preHandle(request, response, handler);
			
		} catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}		
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
		
		logger.info("================================= Common API END =================================\n");
	}	
}