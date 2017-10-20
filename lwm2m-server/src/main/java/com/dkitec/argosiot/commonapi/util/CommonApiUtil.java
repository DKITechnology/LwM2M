package com.dkitec.argosiot.commonapi.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.dkitec.lwm2m.common.util.CommonUtil;

@Component
public class CommonApiUtil {

	private static MessageSource messageSource;
	
	public static MessageSource getMessageSource() {
		return messageSource;
	}
	public static void setMessageSource(MessageSource messageSource) {
		CommonApiUtil.messageSource = messageSource;
	}	
	
	public static HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}
	
	public static Locale getLocale(){
		HttpServletRequest request = getRequest();
		HttpSession session = request.getSession(false);
		Locale locale = null;
		if(session != null){
			locale = (Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		}
		else {
			locale = Locale.getDefault();
		}
		
		return locale;
	}
	
	/**
	 * getLocale
	 * @param request
	 * @return
	 */
	public static Locale getLocale(HttpServletRequest request){
		HttpSession session = request.getSession(false);
		Locale locale = null;
		if(session != null){
			locale = (Locale)session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
			if(locale != null){
				/*LOGGER.debug("######################################");
				LOGGER.debug("Locale From Session: {}", locale);
				LOGGER.debug("######################################");*/
			}
		}
		else {
			locale = Locale.getDefault();
		}
		
		return locale;
	}
	
	/**
	 * getMessage
	 * @param sMsgCode
	 * @param request
	 * @return
	 */
	public static String getMessage (String sMsgCode, HttpServletRequest request) {
		Locale locale = getLocale(request);
		if(locale == null){
			locale = Locale.KOREAN;
		}

		String sMessage = messageSource.getMessage(sMsgCode, null, locale);
		return sMessage;
	}
	
	/**
	 * getMessage
	 * @param sMsgCode
	 * @param sLocale
	 * @return
	 */
	public static String getMessage (String sMsgCode, String sLocale) {
		Locale locale = Locale.KOREAN;
		if(! CommonUtil.isEmpty(sLocale)){
			sLocale = sLocale.toLowerCase();
			if(! "ko".equals(sLocale)){
				if(sLocale.equals("en")){
					locale = Locale.ENGLISH;
//				} else if (sLocale.equals("zh")){
//					locale = Locale.CHINESE;
				}
			}
		}
		String sMessage = messageSource.getMessage(sMsgCode, null, locale);
		return sMessage;
	}
	
	/**
	 * getMessage
	 * @param sMsgCode
	 * @return
	 */
	public static String getMessage (String sMsgCode) {
		Locale locale = getLocale();

		String sMessage = messageSource.getMessage(sMsgCode, null, locale);
		return sMessage;
	}

	/**
	 * getMessage
	 * @param sMsgCode
	 * @param arr
	 * @return
	 */
	public static String getMessage (String sMsgCode, Object[] arr) {
		Locale locale = getLocale();

		String sMessage = messageSource.getMessage(sMsgCode, arr, locale);
		return sMessage;
	}
}
