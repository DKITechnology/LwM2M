package com.dkitec.lwm2m.service;

import java.io.InputStreamReader;
import java.security.PublicKey;

import javax.servlet.http.HttpServletResponse;

import org.eclipse.leshan.server.cluster.RedisSecurityStore;
import org.eclipse.leshan.server.security.EditableSecurityStore;
import org.eclipse.leshan.server.security.NonUniqueSecurityInfoException;
import org.eclipse.leshan.server.security.SecurityInfo;
import org.eclipse.leshan.server.security.SecurityStore;
import org.eclipse.leshan.util.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dkitec.lwm2m.common.code.ComCode;
import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.dao.DeviceInfoDao;
import com.dkitec.lwm2m.domain.SecurityDataInfo;
import com.dkitec.lwm2m.server.DefaultLwm2mServer;
import com.dkitec.lwm2m.service.intf.DeviceSecurityService;
import com.google.gson.JsonParseException;

@Service
public class DeviceSecurityServiceImpl implements DeviceSecurityService {

	Logger logger = LoggerFactory.getLogger(DeviceSecurityServiceImpl.class);
			
	@Autowired
	DefaultLwm2mServer leshanServer;
	
	@Autowired
	DeviceInfoDao deviceInfoDao;
	
	@Override
	public String deviceSecurityUpdate(SecurityDataInfo sectvo) {
		String result = "success";
		 try {
			 	RedisSecurityStore store = (RedisSecurityStore) leshanServer.getLwm2mServer().getSecurityStore();

		 		String endpoint = sectvo.getDeviceId();
		 		SecurityInfo info = null;
			 	if(sectvo.getSecurityType().equals(ComCode.SecurityTypeCd.PSK.getValue())){
				 	String identity = (CommonUtil.isEmpty(sectvo.getIdentity()))? endpoint : sectvo.getIdentity();
				 	String securityKey = stringToHex(sectvo.getKey());
				 	logger.debug("String security Key : " + sectvo.getKey());
				 	logger.debug("String to Hex : " + securityKey);
				 	byte[] preSharedKey = Hex.decodeHex(securityKey.toCharArray());
				 	info = SecurityInfo.newPreSharedKeyInfo(endpoint, identity, preSharedKey);
				 	store.add(info);
		            deviceInfoDao.updateDeviceSecurity(sectvo);
			 	} else if(sectvo.getSecurityType().equals(ComCode.SecurityTypeCd.RPK.getValue())){
			 		result = "Security Not supported.";
			 		//info = SecurityInfo.newRawPublicKeyInfo(endpoint, rawPublicKey)
				} else if(sectvo.getSecurityType().equals(ComCode.SecurityTypeCd.CERTY.getValue())){
					result = "Security Not supported.";
					//info = SecurityInfo.newX509CertInfo(endpoint);
				} else {
					result = "Security Type Not found.";
				}
	        } catch (NonUniqueSecurityInfoException e) {
	           LoggerPrint.printErrorLogExceptionrMsg(logger, e, "NonUniqueSecurityInfoException");
	           result = e.getMessage();
	        } catch (RuntimeException e) {
	        	LoggerPrint.printErrorLogExceptionrMsg(logger, e, "RuntimeException");
	        	result = e.getMessage();
	        } catch (Exception e) {
				LoggerPrint.printErrorLogExceptionrMsg(logger, e);
				result = e.getMessage();
			}
		return result;
	}

	@Override
	public String deviceSecurityDelete(String deviceId) {
		try {
			try {
				SecurityDataInfo sectvo = new SecurityDataInfo();
				sectvo.setDeviceId(deviceId);
				sectvo.setSecurityType(null);
				sectvo.setKey(null);
				deviceInfoDao.updateDeviceSecurity(sectvo);
			} catch (Exception e) {}
			RedisSecurityStore store = (RedisSecurityStore) leshanServer.getLwm2mServer().getSecurityStore();
			if (store.remove(deviceId) != null) {
			    return "success";
			} else {
			    return "notfound";
			}
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
			return "fail";
		}
	}

	private String stringToHex(String s) {
		String result = "";
		for (int i = 0; i < s.length(); i++) {
			result += String.format("%02X", (int) s.charAt(i));
		}
		return result;
	}

	// 헥사 접두사 "0x" 붙이는 버전
	private String stringToHex0x(String s) {
		String result = "";

		for (int i = 0; i < s.length(); i++) {
			result += String.format("0x%02X", (int) s.charAt(i));
		}
		return result;
	}
}
