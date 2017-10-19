package com.dkitec.lwm2m.common.interceptor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.dkitec.lwm2m.common.exception.AuthException;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.dao.AuthInfoDao;
import com.dkitec.lwm2m.domain.AuthInfo;

public class AuthInterceptor extends HandlerInterceptorAdapter{
	
	Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
	
	@Autowired
	AuthInfoDao authDao;

	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME = "Basic";
	private static final String USERTYPE_OAM = "OAM";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean result = super.preHandle(request, response, handler);
		try {
			
			String authorization = request.getHeader(AUTHORIZATION_PROPERTY);
			
			if(authorization == null || authorization.isEmpty())
			{
				throw new AuthException("Header authorization Empty.");
			}
			
			if(authorization.contains(AUTHENTICATION_SCHEME)){
				String encodedUserPassword = authorization.replaceFirst(AUTHENTICATION_SCHEME + " ", "");
				//decode
				String usernameAndPassword = new String(Base64.decodeBase64(encodedUserPassword));
				
				StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
				String authId = tokenizer.nextToken();
				String password = tokenizer.nextToken();
				String userType = null;
				try {
					userType = tokenizer.nextToken();
				} catch (Exception e) {}
				
				AuthInfo authInfo = null;			
				if(userType  != null && userType.equals(USERTYPE_OAM)){
					//USER 테이블에서 인증 정보 일치 확인
					authInfo = authDao.selectAdminAuth(authId);
					request.setAttribute("userId", authInfo.getAuthId());
				}else{
					//서비스 테이블에서 인증 정보 일치 확인
					authInfo = authDao.selectInfAEAuth(authId);
					request.setAttribute("inaeId", authInfo.getAuthId());
				}
				
				if(authInfo == null){
					throw new AuthException("Auth Information is not found.");
				}else{
					if(!authId.equals(authInfo.getAuthId()) || !password.equals(authInfo.getAuthPwd())){
						throw new AuthException("ID or Password not correct.");
					}
				}
			}else{
				throw new AuthException("Header AUTHENTICATION_SCHEME not found.");
			}			
		}  catch (AuthException ae) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, ae);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			result = false;
		}	catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			result = false;
		}
		
		return result;
	}
}
