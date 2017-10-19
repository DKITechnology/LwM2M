package com.dkitec.lwm2m.server.message;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.eclipse.californium.core.coap.EmptyMessage;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionNumberRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.network.interceptors.MessageInterceptor;
import org.eclipse.leshan.util.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.dkitec.lwm2m.common.code.ComCode;
import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.common.util.PropertiesUtil;
import com.dkitec.lwm2m.domain.message.MessageInfoVO;
import com.dkitec.lwm2m.domain.message.MessageRefInfoVO;
import com.dkitec.lwm2m.domain.message.MessageRequestVO;
import com.dkitec.lwm2m.domain.message.MessageResponseVO;
import com.dkitec.lwm2m.rdao.common.CommonRedisPoolDAO;
import com.google.gson.Gson;

/**
 * 단말에서 CoAP으로 주고 받는 통신 메시지를 처리한다.
 * 해당 메시지를 Request와 Response Message로 저장한다.
 */
public class MessageTracer implements MessageInterceptor{

	Logger logger = LoggerFactory.getLogger(MessageTracer.class);
	
	public static final String MSG_PREFIX = "MSG:";
		
	private Map<String, String> listeners = new HashMap<String, String>();

	CommonRedisPoolDAO commonRedis;
	
	public MessageTracer(CommonRedisPoolDAO commonRedis) {
		this.commonRedis = commonRedis;
	}
	
	//clients
	public String getEndpoint(String addressInfo){
		return listeners.get(addressInfo);
	}
	
	public void addEndpoint(String addressInfo, String endpoint) {
		listeners.put(addressInfo, endpoint);
    }
	
	public void deleteEndpoint(String addressInfo) {
		listeners.remove(addressInfo);
    }

	/** Client Address to String*/
	public String toStringAddress(InetAddress clientAddress, int clientPort) {
		try {
			return clientAddress.toString() + ":" + clientPort;
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
			return null;
		}
	}
		
	// 서버에서 요청 
	@Override
    public void sendRequest(Request request) {
		try {
			String endPoint = listeners.get(toStringAddress(request.getDestination(), request.getDestinationPort()));
			insertRequsetMsg(request.getDestination(), request.getDestinationPort(), endPoint, request, "001");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	// 서버에서 요청에 대한 응답
    @Override
    public void sendResponse(Response response) {
    	try {
			String endPoint = listeners.get(toStringAddress(response.getDestination(), response.getDestinationPort()));
			insertResponseMsg(response.getDestination(), endPoint, response, "001");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // 서버에서 empty 요청
    @Override
    public void sendEmptyMessage(EmptyMessage message) {
    	//server to device ping
    }

    // 단말에서 요청
    @Override
    public void receiveRequest(Request request) {
    	try {
			String endPoint = listeners.get(toStringAddress(request.getSource(), request.getSourcePort()));
			insertRequsetMsg(request.getSource(), request.getSourcePort(), endPoint, request, "002");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   	
    }

    // 단말 에서 요청에 대한 응답
    @Override
    public void receiveResponse(Response response) {
    	try {
			String endPoint = listeners.get(toStringAddress(response.getSource(), response.getSourcePort()));
			insertResponseMsg(response.getSource(), endPoint, response, "002");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 	
    }

    // 단말에서 보낸 empty 메시지 수신
    @Override
    public void receiveEmptyMessage(EmptyMessage message) {
    	//device to sever ping
    }
        
    private void insertRequsetMsg(InetAddress requestIp, int clientPort, String endPoint, Request request, String direction){
    	SimpleDateFormat sf = new SimpleDateFormat(ComCode.DataFormat.DateStrFormat.getValue());
    	String creDatm = sf.format(new Date());
    	String clientIp = requestIp.getHostAddress();
    	String token = request.getTokenString();
		Map<String, String> headers = getOptionsToMap(request);
    	if(endPoint == null || endPoint.isEmpty()){
    		endPoint = findEndpoint(headers.get("Uri-Query"));
    		//addEndpoint(toStringAddress(requestIp, clientPort), endPoint);
    	}
    	MessageInfoVO messageInfo = new MessageInfoVO();
		messageInfo.setServerId();
		messageInfo.setUndefinedServiceUser();
    	try {
			String tId = CommonUtil.makeMessageTid(clientIp, request.getMID()+"", token);
			messageInfo.setTid(tId);
			messageInfo.setCreDatm(creDatm);
			messageInfo.setDeviceId(endPoint);
			if(direction.equals("001")){
				messageInfo.setMessageType(ComCode.MessageType.Coap.getValue());
			}else{
				messageInfo.setMessageType(ComCode.MessageType.RECIEVE.getValue());
			}

			if(request.getSenderIdentity() != null){
				messageInfo.setProtocol(ComCode.MessageProtocol.COAPS.getValue());
			}else{
				messageInfo.setProtocol(ComCode.MessageProtocol.COAP.getValue());
			}
			messageInfo.setAppProtocol(ComCode.MessageAppProtocol.lwM2M.getValue());
			
			MessageRequestVO requestVO = new MessageRequestVO();    	
			requestVO.setCreDatm(creDatm);    	
			requestVO.setMethod(request.getCode().toString()); // CoAP method
			requestVO.setUri(makeMessageUrl(request,direction));
			if(request.getPayload() != null){
				String strPayload = new String(request.getPayload(), StandardCharsets.UTF_8);
		        System.out.println("[payload] : "+strPayload);
	            if (!StringUtils.isAsciiPrintable(strPayload)) {
	            	strPayload = "Hex:" + Hex.encodeHexString(request.getPayload());
		        }
		    	requestVO.setBody(strPayload);
			}
			requestVO.setHeaders(headers);
			messageInfo.setRequest(requestVO);	
			MessageRefInfoVO refInfo = new MessageRefInfoVO();
			refInfo.setTokenId(token);
			refInfo.setCoapReqType(request.getType().toString());
			messageInfo.setRefInfo(refInfo);
			
			String redisKey = MSG_PREFIX+tId;
			messageInfo.setRedisKey(redisKey);
			commonRedis.setObjectValue(redisKey, messageInfo, 0);
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
    }
    
    /**
     * 
     * @param requestIp
     * @param endPoint
     * @param response
     * @param direction : Server To device "001",  Device To server "002"
     */
	private void insertResponseMsg(InetAddress requestIp, String endPoint, Response response, String direction) {
		SimpleDateFormat sf = new SimpleDateFormat(ComCode.DataFormat.DateStrFormat.getValue());
		String creDatm = sf.format(new Date());
		String clientIp = requestIp.getHostAddress();
		if (endPoint == null || endPoint.isEmpty()) {
			endPoint = clientIp;
		}

		MessageInfoVO messageInfo = null;		
		try {
			String token = response.getTokenString();
			String tId = CommonUtil.makeMessageTid(clientIp, response.getMID()+"", token);
			if (response.getType().toString().equals("ACK")) {
				// messageInfo get
				String legacyMsg = commonRedis.getStringValue(MSG_PREFIX+tId);
				messageInfo = new Gson().fromJson(legacyMsg, MessageInfoVO.class);									
			}	
			if(messageInfo == null){
				messageInfo = new MessageInfoVO();
				messageInfo.setCreDatm(creDatm);
				messageInfo.setDeviceId(endPoint);
				messageInfo.setMessageType(ComCode.MessageType.Coap.getValue());
			}
			messageInfo.setTid(tId);
			messageInfo.setServerId();
			messageInfo.setUndefinedServiceUser();
			
			MessageResponseVO responseVO = new MessageResponseVO();
			responseVO.setCreDatm(creDatm);
			responseVO.setResponseCode(response.getCode().toString());
			responseVO.setHeaders(getOptionsToMap(response));
			if (response.getPayload() != null) {
				String strPayload = new String(response.getPayload(), StandardCharsets.UTF_8);
				System.out.println("[payload] : " + strPayload);
				if (!StringUtils.isAsciiPrintable(strPayload)) {
					strPayload = "Hex:" + Hex.encodeHexString(response.getPayload());
				}
				responseVO.setBody(strPayload);
			}
			messageInfo.setResponse(responseVO);
			MessageRefInfoVO refInfo = messageInfo.getRefInfo();
			if(refInfo == null){
				refInfo = new MessageRefInfoVO();
				refInfo.setTokenId(token);
			}
			refInfo.setCoapResType(response.getType().toString());			
			messageInfo.setRefInfo(refInfo);
			
			String redisKey = MSG_PREFIX+tId;
			messageInfo.setRedisKey(redisKey);
			commonRedis.setObjectValue(redisKey, messageInfo, 0);
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
	}
    	
    public Map<String, String> getOptionsToMap(Request request){
    	if (request.getOptions() != null) {
    		return this.getOptionsToMap(request.getOptions());
        }else{
        	return null;
        }
    }
        
    public Map<String, String> getOptionsToMap(Response response){
    	if (response.getOptions() != null) {
    		return this.getOptionsToMap(response.getOptions());
        }else{
        	return null;
        }
    }
    
    private Map<String, String> getOptionsToMap(OptionSet options){
    	Map<String, String> headers = null;
    	if (options != null) {
    		headers = new HashMap<String, String>();
            List<Option> opts = options.asSortedList();
            if (!opts.isEmpty()) {
                Map<String, List<String>> optMap = new HashMap<>();
                for (Option opt : opts) {
                    String strOption = OptionNumberRegistry.toString(opt.getNumber());
                    List<String> values = optMap.get(strOption);
                    if (values == null) {
                        values = new ArrayList<>();
                        optMap.put(strOption, values);
                    }
                    values.add(opt.toValueString());
                }
                for (Entry<String, List<String>> e : optMap.entrySet()) {
                	String values = "";
                	for(int i=0; i < e.getValue().size(); i++){
                		if( i == 0 ){
                			values = values + e.getValue().get(i);
                		}else{
                			values = values + ","+ e.getValue().get(i);
                		}                			
                	}
                	headers.put(e.getKey(), values);                   
                }
            }  
        }
    	return headers;
    }
        
    private String findEndpoint(String uriQuery){
    	String endpoint = null;
    	if(uriQuery != null){    		
    		String[] contents = uriQuery.split(",");
    		for (String entry : contents) {
    			  String[] keyValue = entry.split("=");
    			 if(keyValue[0].contains("ep")){
    				 endpoint = keyValue[1].replaceAll("\"", "");
    				 return endpoint;
    			 }
    		}    		
    	}
    	return endpoint;
    }
    
    private String makeMessageUrl(Request request, String direction){
    	String requestUrl = request.getURI();
    	if(requestUrl.contains("localhost")){    
    		String realRequest;
    		realRequest = "localhost";
			try {				
				if(direction.equals("001")){    			
					//server => device    	
					realRequest = request.getDestination().getHostAddress() + ":" + request.getDestinationPort();
				}else{
					//device => server
					String serverAddress = "";
					String serverPort	 = "";
					if(request.getSenderIdentity() != null){
						serverAddress = PropertiesUtil.get("serverconfig", "lwm2m.server.secureAddress");
						serverPort = PropertiesUtil.get("serverconfig", "lwm2m.server.secureCaopPort");
					}else{
						serverAddress = PropertiesUtil.get("serverconfig", "lwm2m.server.address");
						serverPort = PropertiesUtil.get("serverconfig", "lwm2m.server.coapPort");
					}
					realRequest = serverAddress + ":" + serverPort;
				}
			} catch (Exception e) {
				LoggerPrint.printErrorLogExceptionrMsg(logger, e);
			}
			requestUrl = requestUrl.replaceAll("localhost", realRequest);
    	}
    	return requestUrl;
    }
}
