package com.dkitec.lwm2m.server;

import java.util.Collection;

import javax.annotation.PostConstruct;

import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.server.registration.Registration;
import org.eclipse.leshan.server.registration.RegistrationListener;
import org.eclipse.leshan.server.registration.RegistrationUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.domain.DeviceInfoVO;
import com.dkitec.lwm2m.server.json.LwM2mNodeSerializer;
import com.dkitec.lwm2m.server.json.RegistrationSerializer;
import com.dkitec.lwm2m.server.message.MessageTracer;
import com.dkitec.lwm2m.service.intf.DeviceInfoService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 단말에 등록 정보를 업데이트 할 경우 사용된다.
 * RDB에 Device 테이블에 관련 정보를 업데이트 한다.
 * 단말의 상태 정보를 업데이트 한다.
 */
@Service
public class RegListenService implements RegistrationListener {

	private static final Logger logger = LoggerFactory.getLogger(RegListenService.class);
    	
    private int securePort;
   
    @Autowired
    DefaultLwm2mServer leshanServer;
    
    @Autowired
    DeviceInfoService deviceService;
    
    MessageTracer messageTracer;
    
    @PostConstruct
    public void regListenServiceAdd() {
    	leshanServer.getLwm2mServer().getRegistrationService().addListener(this);
    	messageTracer = leshanServer.getMessageTracer();
	}
    
    private Gson getGsonBuild(){
    	GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeHierarchyAdapter(Registration.class, new RegistrationSerializer(securePort));
        gsonBuilder.registerTypeHierarchyAdapter(LwM2mNode.class, new LwM2mNodeSerializer());
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        Gson gson = gsonBuilder.create();
    	return gson;
    }
    
    private String clientAddresInfo(Registration registration){
        return messageTracer.toStringAddress(registration.getAddress(), registration.getPort());
    }
    
    @Override
    public void registered(Registration registration) {
        try {
			//String jReg = getGsonBuild().toJson(registration);
			String deviceId = registration.getEndpoint();
			// 단말 connection 상태 업데이트 및 단말 정보 업데이트 
			DeviceInfoVO deviceInfo = new DeviceInfoVO();
			deviceInfo.setDevcId(deviceId);
			if(registration != null){
				deviceInfo.setIpAddr(registration.getAddress().getHostAddress());
				deviceInfo.setDevcPort(String.valueOf(registration.getPort()));
				deviceInfo.setDevcServId(registration.getId());
			}
			deviceService.upsertDeviceInfo(deviceInfo);
			
			String adrresInfo = clientAddresInfo(registration);
            if(messageTracer.getEndpoint(adrresInfo) == null){
                // endpoint 생성
                messageTracer.addEndpoint(adrresInfo, deviceId);
            } 
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e, "Register");
		}
    }

    @Override
    public void updated(RegistrationUpdate update, Registration updatedRegistration,
            Registration previousRegistration) {
        String jReg = getGsonBuild().toJson(updatedRegistration);
        String deviceId = updatedRegistration.getEndpoint();
        deviceService.updateDeviceStatus(deviceId, true);
        
        String adrresInfo = clientAddresInfo(updatedRegistration);
        if(messageTracer.getEndpoint(adrresInfo) == null){
            // endpoint 생성
            messageTracer.addEndpoint(adrresInfo, deviceId);
        } 
    }

    @Override
    public void unregistered(Registration registration, Collection<Observation> observations, boolean expired) {
        String jReg = getGsonBuild().toJson(registration);
        String deviceId = registration.getEndpoint();
        deviceService.updateDeviceStatus(deviceId, false);
        
        String adrresInfo = clientAddresInfo(registration);
        if(messageTracer.getEndpoint(adrresInfo) != null){
            //저장된 endpoint 삭제
            messageTracer.deleteEndpoint(adrresInfo);
        }
    }
    
}
