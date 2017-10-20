package com.dkitec.lwm2m.service.message;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.methods.HttpPost;

import com.dkitec.lwm2m.domain.DeviceInaeUser;
import com.dkitec.lwm2m.domain.message.MessageInfoVO;
import com.dkitec.lwm2m.domain.message.MessageRefInfoVO;

public interface MessageService {

	public void insertMessageVO(MessageInfoVO vo);
	
	public MessageInfoVO insertHttpRcvMsg(String deviceId, String creDatm, HttpServletRequest request, String reqBody, HttpServletResponse response, String resBody);
	
	public MessageInfoVO insertHttpRcvMsg(String deviceId, String creDatm, HttpServletRequest request, String reqBody, HttpServletResponse response, String resBody, MessageRefInfoVO msgRef);
	
	public MessageInfoVO insertHttpRcvMsg(DeviceInaeUser deviceInfo, String creDatm, HttpServletRequest request, String reqBody, HttpServletResponse response, String resBody, MessageRefInfoVO msgRef);
	
	public MessageInfoVO insertHttpNotifyMsg(String tId, String deviceId, String creDatm, HttpPost post, String reqBody, int responseCode, String resBody,  MessageRefInfoVO msgRef);

}