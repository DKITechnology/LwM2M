package com.dkitec.lwm2m.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dkitec.lwm2m.common.code.ComCode;

@Component
public class CommonUtil {
	
	/**
	 * GET OEPN API URL
	 * @param url
	 * @return
	 */
	public static String getApiURL(String url) {
		return PropertiesUtil.get("openapi", url);
	}
	
	/**
	 * GET HOST URL
	 * @param req
	 * @return
	 */
	public static String getURL(HttpServletRequest req) {

        String scheme = req.getScheme();
        String serverName = req.getServerName();
        int serverPort = req.getServerPort();
        String contextPath = req.getContextPath();
        String pathInfo = req.getPathInfo();
        StringBuffer url =  new StringBuffer();
        url.append(scheme).append("://").append(serverName);

        if ((serverPort != 80) && (serverPort != 443)) {
            url.append(":").append(serverPort);
        }
        url.append(contextPath);
        if (pathInfo != null) {
            url.append(pathInfo);
        }
        return url.toString();
    }
	
	public static String getNowTimestamp() {
    	String format = "yyyy-MM-dd'T'HH:mm:ssXXX";
    	String now = "";
		
		SimpleDateFormat simpleFomat = new SimpleDateFormat(format);
		Date currentTime = new Date();
		now = simpleFomat.format(currentTime);
		
		return now;
    }
	
	public static String getNow(String format){
		String now = "";
		
		SimpleDateFormat simpleFomat = new SimpleDateFormat(format);
		Date currentTime = new Date();
		now = simpleFomat.format(currentTime);
		
		return now;
	}
	
	/**
     * URL 쿼리 파라미터를 리턴한다.
     * @param req
     * @return
     */
    public static Map<String, String[]> getUrlParameters(HttpServletRequest req) {
    	if(req != null) {
    		return req.getParameterMap();
    	}
    	else {
    		return null;
    	}
    }	
    
    /**
	 * isEmpty
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		if(str == null || str.isEmpty() || str.equals(""))
			return true;
		
		return false;
	}
	
	public static String nvl(String sTarget, String sReplace){
		if(sTarget == null || sTarget.isEmpty()){
			return sReplace;
		}
		return sTarget;
	}
	
	public static int coapResultToHttpCode(String coapResultCd) {
		int resultStatus = HttpServletResponse.SC_BAD_REQUEST;
		switch (coapResultCd) {
		case "UNAUTHORIZED":
			resultStatus = HttpServletResponse.SC_UNAUTHORIZED;
			break;
		case "BAD_REQUEST":
			resultStatus = HttpServletResponse.SC_BAD_REQUEST;
			break;
		case "METHOD_NOT_ALLOWED":
			resultStatus = HttpServletResponse.SC_METHOD_NOT_ALLOWED;
			break;
		case "FORBIDDEN":
			resultStatus = HttpServletResponse.SC_FORBIDDEN;
			break;
		case "NOT_FOUND":
			resultStatus = HttpServletResponse.SC_NOT_FOUND;
			break;
		case "INTERNAL_SERVER_ERROR":
			resultStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			break;
		case "UNSUPPORTED_CONTENT_FORMAT":
			resultStatus = HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE;
			break;
		case "NOT_ACCEPTABLE":
			resultStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
			break;
		default:
			break;
		}
		return resultStatus;
	}	

    public static String makeMessageTid(String clientIp, String mId, String token){
		String tId = clientIp+"_"+mId + ((token==null)?"":"_" + token);
		return tId;
    }
    
    public static String nowToDefaultStr(){
    	SimpleDateFormat sf = new SimpleDateFormat(ComCode.DataFormat.DateStrFormat.getValue());
    	String creDatm = sf.format(new Date());
    	return creDatm;
    }
}
