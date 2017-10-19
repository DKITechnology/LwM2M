package com.dkitec.lwm2m.domain.message;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.PropertiesUtil;
import com.dkitec.lwm2m.domain.DeviceInaeUser;

public class MessageInfoVO extends DeviceInaeUser implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String redisKey;
	/** mongo object ID */
	@Id
	String id;
	
	/** 트랜잭션 ID */
	private String tid;
	
	/** 수신 일시 */
	private String creDatm;
	
	/** 메시지 타입 : R(수신) | S(SMS notify) | P(Push notify) | E(Email notify) | H(Http notify) **/
	private String messageType;
	
	/** 서버 아이디 : properties setting*/
	private String serverId;
	
	/** 요청 정보*/
	private MessageRequestVO request;
	
	/** 응답 정보*/
	private MessageResponseVO response;
	
	/** 참조 정보*/
	private MessageRefInfoVO refInfo;
	
	//**2017.07 추가*/
	/** application Protocol : : O(oneM2M) | L(lwM2M) | P(PCD-01) | F(FHIR) | A(OPEN API) */
	private String appProtocol;
	
	/** Protocol : H(HTTP) | HS(HTTPS) | C(COAP) | CS(COAPS) | M(MQTT) */
	private String protocol;	
	
 	public String getRedisKey() {
		return redisKey;
	}

	public void setRedisKey(String redisKey) {
		this.redisKey = redisKey;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getCreDatm() {
		return creDatm;
	}

	public void setCreDatm(String creDatm) {
		this.creDatm = creDatm;
	}

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	public String getServerId(){
		return serverId;
	}

	public void setServerId() {
		try {
			this.serverId = CommonUtil.nvl(PropertiesUtil.get("serverconfig", "lwm2m.server.id"), "");
		} catch (Exception e) {}
	}

	public MessageRequestVO getRequest() {
		return request;
	}

	public void setRequest(MessageRequestVO request) {
		this.request = request;
	}

	public MessageResponseVO getResponse() {
		return response;
	}

	public void setResponse(MessageResponseVO response) {
		this.response = response;
	}

	public MessageRefInfoVO getRefInfo() {
		return refInfo;
	}

	public void setRefInfo(MessageRefInfoVO refInfo) {
		this.refInfo = refInfo;
	}

	public void setAppProtocol(String appProtocol) {
		this.appProtocol = appProtocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getAppProtocol() {
		return appProtocol;
	}

	public String getProtocol() {
		return protocol;
	}
}
