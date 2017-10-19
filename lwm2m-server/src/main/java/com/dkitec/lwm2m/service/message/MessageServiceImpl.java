package com.dkitec.lwm2m.service.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dkitec.lwm2m.common.code.ComCode;
import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.PropertiesUtil;
import com.dkitec.lwm2m.domain.DeviceInaeUser;
import com.dkitec.lwm2m.domain.message.MessageInfoVO;
import com.dkitec.lwm2m.domain.message.MessageRefInfoVO;
import com.dkitec.lwm2m.domain.message.MessageRequestVO;
import com.dkitec.lwm2m.domain.message.MessageResponseVO;
import com.dkitec.lwm2m.mdao.common.SequenceDao;
import com.dkitec.lwm2m.mdao.common.SequenceDao.MovType;
import com.dkitec.lwm2m.mdao.common.SequenceDao.SeqType;
import com.dkitec.lwm2m.rdao.common.CommonRedisPoolDAO;
import com.dkitec.lwm2m.server.message.MessageTracer;

@Service
public class MessageServiceImpl implements MessageService{
	
	Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

	@Autowired
	private SequenceDao seqDao;
	
	@Autowired
	CommonRedisPoolDAO commonRedis;
	
	@Value("#{serverConfigProp['lwm2m.server.address']}")
	private String serverAddress;
		
	/***
	 * MessageInfoVO 객체를 생성해서 저장 하는 경우 호출
	 * @param vo
	 */
	@Override
	public void insertMessageVO(MessageInfoVO vo){
		Long transationID = seqDao.move(MovType.UP, SeqType.MESSAGE);
		vo.setTid(String.valueOf(transationID));
		try {
			//messageDao.insert(vo);
		} catch (Exception e) {
			logger.error("messageInfo insert Error {} ", e.getMessage(), e);
		}
	}
	
	@Override
	public MessageInfoVO insertHttpRcvMsg(String deviceId, String creDatm, HttpServletRequest request, String reqBody,
			HttpServletResponse response, String resBody) {
		return insertHttpRcvMsg(deviceId, creDatm, request, reqBody, response, resBody, null);
	}
	
	@Override
	public MessageInfoVO insertHttpRcvMsg(String deviceId, String creDatm, HttpServletRequest request, String reqBody, HttpServletResponse response, String resBody,
			MessageRefInfoVO msgRef) {
		DeviceInaeUser deviceInfo = new DeviceInaeUser();
		deviceInfo.setDeviceId(deviceId);
		return insertHttpRcvMsg(deviceInfo, creDatm, request, reqBody, response, resBody, msgRef);
	}
	
	@Override
	public MessageInfoVO insertHttpRcvMsg(DeviceInaeUser deviceInfo, String creDatm, HttpServletRequest request, String reqBody, HttpServletResponse response, String resBody,
			MessageRefInfoVO msgRef) {
		MessageInfoVO messageInfo = new MessageInfoVO();
		try {
			Long transationID = seqDao.move(MovType.UP, SeqType.MESSAGE);
			
			messageInfo.setTid(String.valueOf(transationID));
			messageInfo.setCreDatm(creDatm);
			messageInfo.setDeviceId(deviceInfo.getDeviceId());
			messageInfo.setMessageType(ComCode.MessageType.RECIEVE.getValue());
			
			//단말의 서비스 및 사용자 정보 조회
			if(deviceInfo.getInaeId() == null && request.getAttribute("inaeId") == null){
				messageInfo.setUndefinedServiceUser(); //undefined
			}else{
				if(deviceInfo.getInaeId() != null){
					messageInfo.setInaeId(deviceInfo.getInaeId());
				}else{
					messageInfo.setInaeId(request.getAttribute("inaeId").toString());
				}				
			}
			if(deviceInfo.getUserId() != null || request.getAttribute("userId") != null){
				if(deviceInfo.getUserId() != null){
					messageInfo.setUserId(deviceInfo.getUserId());
				}else{
					messageInfo.setUserId(request.getAttribute("userId").toString());
				}
			}
			messageInfo.setServerId();
			String uri = servletGetURL(request);
			if(uri.contains("https")){
				messageInfo.setProtocol(ComCode.MessageProtocol.HTTPS.getValue());
			}else{
				messageInfo.setProtocol(ComCode.MessageProtocol.HTTP.getValue());
			}
			messageInfo.setAppProtocol(ComCode.MessageAppProtocol.OPENAPI.getValue());
			MessageRequestVO requestVO = makeReqMessage(request, reqBody, uri);
			requestVO.setCreDatm(creDatm);
			messageInfo.setRequest(requestVO);
			
			MessageResponseVO responseVO = makeResMessage(response, resBody);
			messageInfo.setResponse(responseVO);		
			
			if(msgRef != null){
				messageInfo.setRefInfo(msgRef);
			}
			
			try {
				String redisKey = MessageTracer.MSG_PREFIX+messageInfo.getTid()+"_RCV"+"_"+new Date().getTime();
				messageInfo.setRedisKey(redisKey);
				commonRedis.setObjectValue(redisKey, messageInfo);
			} catch (Exception e) {
				logger.error("messageInfo insert Error {} ", e.getMessage(), e);
			}
		} catch (Exception e) {
			logger.error("messageInfo Error {} ", e.getMessage(), e);
		}		
		return messageInfo;
	}

	@Override
	public MessageInfoVO insertHttpNotifyMsg(String tId, String deviceId, String creDatm, HttpPost post, String reqBody,
			int responseCode, String resBody, MessageRefInfoVO msgRef) {
		MessageInfoVO messageInfo = new MessageInfoVO();
		try {
			//Long transationID = seqDao.move(MovType.UP, SeqType.MESSAGE);			
			messageInfo.setTid(tId);
			messageInfo.setCreDatm(creDatm);
			messageInfo.setMessageType(ComCode.MessageType.HTTP.getValue());
			messageInfo.setDeviceId(deviceId);
			
			//Server And Service
			messageInfo.setServerId();
			messageInfo.setUndefinedServiceUser();
			
			String uri = post.getURI().toString();
			if(uri.contains("https")){
				messageInfo.setProtocol(ComCode.MessageProtocol.HTTPS.getValue());
			}else{
				messageInfo.setProtocol(ComCode.MessageProtocol.HTTP.getValue());
			}
			messageInfo.setAppProtocol(ComCode.MessageAppProtocol.OPENAPI.getValue());
			
			MessageRequestVO requestVO = makeReqMessage(post, reqBody, uri);
			requestVO.setCreDatm(creDatm);
			messageInfo.setRequest(requestVO);
			
			MessageResponseVO responseVO = new MessageResponseVO();
			responseVO.setResponseCode(responseCode+"");
			responseVO.setBody(resBody);
			responseVO.setCreDatm(CommonUtil.getNow(ComCode.DataFormat.DateStrFormat.getValue()));
			messageInfo.setResponse(responseVO);		
			
			if(msgRef != null){
				messageInfo.setRefInfo(msgRef);
			}
			
			try {
				String redisKey = MessageTracer.MSG_PREFIX+messageInfo.getTid()+"_HN"+"_"+new Date().getTime();
				messageInfo.setRedisKey(redisKey);
				commonRedis.setObjectValue(redisKey, messageInfo);
			} catch (Exception e) {
				logger.error("messageInfo insert Error {} ", e.getMessage(), e);
			}
		} catch (Exception e) {
			logger.error("messageInfo Error {} ", e.getMessage(), e);
		}		
		return messageInfo;
	}
	
	public MessageRequestVO makeReqMessage(HttpServletRequest req, String reqBody, String uri){
		MessageRequestVO requestVO = new MessageRequestVO();
		requestVO.setMethod(req.getMethod());
		requestVO.setUri(uri);
		
		Map<String, String> headersMap = new HashMap<String,String>();
		Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = req.getHeader(key);
            headersMap.put(key, value);
        }
        requestVO.setHeaders(headersMap);
		
		if(reqBody != null){
			requestVO.setBody(reqBody);
		}
		requestVO.setRequestIp(req.getRemoteHost());		
		return requestVO;
	}
	
	public MessageRequestVO makeReqMessage(HttpPost post, String reqBody, String uri){
		MessageRequestVO requestVO = new MessageRequestVO();
		requestVO.setMethod("POST");
		requestVO.setUri(uri);
		
		Map<String, String> headersMap = new HashMap<String,String>();
		Header[] headers = post.getAllHeaders();
        for (Header header : headers) {
        	headersMap.put(header.getName(), header.getValue());
		}
        requestVO.setHeaders(headersMap);
		
		if(reqBody != null){
			requestVO.setBody(reqBody);
		}
		return requestVO;
	}
	
	public MessageResponseVO makeResMessage(HttpServletResponse res, String resBody){		
		MessageResponseVO responseVO = new MessageResponseVO();
		if(res != null){
			Collection<String> headerCollect = res.getHeaderNames();
			List<String> headerNm = new ArrayList<String>(headerCollect);
			Map<String, String> headers = new HashMap<>();
			for(String hd : headerNm){
				headers.put(hd, res.getHeader(hd));				
			}			
			responseVO.setHeaders(headers);			
			responseVO.setResponseCode(res.getStatus()+"");
		}
		if(resBody != null)
			responseVO.setBody(resBody);
		responseVO.setCreDatm(CommonUtil.getNow(ComCode.DataFormat.DateStrFormat.getValue()));
		return responseVO;
	}
	
	public MessageResponseVO makeResMessage(HttpResponse response){		
		MessageResponseVO responseVO = new MessageResponseVO();		
		Map<String, String> headersMap = new HashMap<String, String>();
		Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
        	headersMap.put(header.getName(), header.getValue());
		}
		responseVO.setHeaders(headersMap);
		if(response.getStatusLine() != null){
			responseVO.setResponseCode(String.valueOf(response.getStatusLine().getStatusCode()));			
			HttpEntity entity = response.getEntity();
			try {
				String responseString = EntityUtils.toString(entity, "UTF-8");			
				responseVO.setBody(responseString);
			} catch (Exception e) {} 
		}		
		responseVO.setCreDatm(CommonUtil.getNow(ComCode.DataFormat.DateStrFormat.getValue()));
		return responseVO;
	}
	
	public String servletGetURL(HttpServletRequest req) {
	    try {
			String scheme = req.getScheme();             // http/htts
			String serverName = req.getServerName();     // hostname
			int serverPort = req.getServerPort();        // 8080
			String serverPath = req.getRequestURI();	 // URI path
			String pathInfo = req.getPathInfo();         // /a/b;c=123
			String queryString = req.getQueryString();          // d=789

			// Reconstruct original requesting URL
			StringBuilder url = new StringBuilder();
			url.append(scheme).append("://").append(serverName);

			if (serverPort != 80 && serverPort != 443) {
			    url.append(":").append(serverPort);
			}

			url.append(serverPath);

			if (pathInfo != null) {
			    url.append(pathInfo);
			}
			if (queryString != null) {
			    url.append("?").append(queryString);
			}
			return url.toString();
		} catch (Exception e) {
			return "";
		}
	}
}
