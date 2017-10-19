package com.dkitec.lwm2m.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.eclipse.leshan.Link;
import org.eclipse.leshan.core.model.LwM2mModel;
import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.registration.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dkitec.lwm2m.common.code.ComCode;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.dao.DeviceInfoDao;
import com.dkitec.lwm2m.domain.DeviceInfoVO;
import com.dkitec.lwm2m.domain.DeviceObjectInfo;
import com.dkitec.lwm2m.domain.DeviceObserveInfoVO;
import com.dkitec.lwm2m.rdao.common.CommonRedisPoolDAO;
import com.dkitec.lwm2m.server.DefaultLwm2mServer;
import com.dkitec.lwm2m.service.intf.DeviceInfoService;
import com.dkitec.lwm2m.service.intf.ServerInitService;
import com.google.gson.Gson;

@Service("deviceInfoService")
public class DeviceInfoServiceImpl implements DeviceInfoService {

	Logger logger = LoggerFactory.getLogger(DeviceInfoServiceImpl.class);
	
	@Autowired
	DeviceInfoDao deviceInfoDao;
	
	@Autowired
	CommonRedisPoolDAO redisPoolDao;
	
	@Resource(name="leshanServer")
	DefaultLwm2mServer leshanServer;
	
	@Autowired
	private ServerInitService serverInitService;
	
	@Override
	public List<DeviceInfoVO> selectDeviceList(DeviceInfoVO searchDevc) {
		return deviceInfoDao.selectDeviceList(searchDevc);
	}

	@Override
	public int selecDeviceTotalCnt(DeviceInfoVO searchDevc) {
		return deviceInfoDao.selecDeviceTotalCnt(searchDevc);
	}

	@Override
	public DeviceInfoVO selectDeviceDetail(String deviceId) {
		//RDB 데이터 조회
		DeviceInfoVO deviceInfo = null;
		try {
			deviceInfo = deviceInfoDao.selectDeviceDetail(deviceId);
			if(deviceInfo == null){
				deviceInfo = new DeviceInfoVO();
				deviceInfo.setDevcId(deviceId);
			}			
			LeshanServer server = leshanServer.getLwm2mServer();
			Registration registration = server.getRegistrationService().getByEndpoint(deviceId);
			if(registration != null)
				deviceInfo.setRegistration(registration);
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
		return deviceInfo;
	}
	
	@Override
	public DeviceObjectInfo selectDeviceObjects(String deviceId) {
		DeviceObjectInfo result = new DeviceObjectInfo();		
		try {
			List<ObjectModel>devcModels = new ArrayList<ObjectModel>();	
			List<String> deviceObjects = new ArrayList<String>();
			List<DeviceObserveInfoVO> deviceObserves = new ArrayList<DeviceObserveInfoVO>();	
			
			LeshanServer server = leshanServer.getLwm2mServer();
			Registration registration = server.getRegistrationService().getByEndpoint(deviceId);
			Link[] objectLinks = registration.getSortedObjectLinks();
			
			Set<Observation> observations = server.getObservationService().getObservations(registration);
			
			for(Observation observe : observations){
				String subUrl = observe.getContext().get(ComCode.Lwm2mKey.subscriptionURL.getValue());
				String url = observe.getPath().toString();
				DeviceObserveInfoVO devObs = new DeviceObserveInfoVO();
				devObs.setSubUrl(subUrl);
				devObs.setUrl(url);
				deviceObserves.add(devObs);
			}
			
			if(registration != null){
				if(objectLinks != null){
					for(Link link : objectLinks){
						String[] linkNos = link.getUrl().split("\\/");
						if(linkNos.length > 1){
							deviceObjects.add(linkNos[1]);
						}					
					}
					//model 중복제거
					HashSet<String> hs = new HashSet<String>();
					hs.addAll(deviceObjects);
					deviceObjects.clear();
					deviceObjects.addAll(hs);
					
					LwM2mModel model = server.getModelProvider().getObjectModel(null);
					ObjectModel[] objmodels = model.getObjectModels().toArray(new ObjectModel[] {});
					for(String objectId : deviceObjects){
						for(ObjectModel objModel : objmodels){
							if(objectId.equals(String.valueOf(objModel.id))){
								devcModels.add(objModel);
								break;
							}
						}
					}
					
					//model과 디바이스 지원 object 모델 정보가 일치 안함
					//추가적인 ObjectModel 재조회
					if(deviceObjects.size() != devcModels.size()){
						try {
							List<ObjectModel> addtionModels = serverInitService.selectLoadObjects();
							for(String objectId : deviceObjects){
								for(ObjectModel addModel : addtionModels){
									if(objectId.equals(String.valueOf(addModel.id))){
										devcModels.add(addModel);
										break;
									}
								}
							}
						} catch (Exception e) { LoggerPrint.printErrorLogExceptionrMsg(logger, e);}
					}						
					
					result.setDevcModels(devcModels);
					result.setObjectLinks(objectLinks);
					result.setObserveInfos(deviceObserves);
				}			
			}
			result.setDevcId(deviceId);
		} catch (NullPointerException e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
			return null;
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
		return result;
	}
	
	@Override
	public void upsertDeviceInfo(DeviceInfoVO deviceInfo){
		try {
			if(deviceInfoDao.updateDeviceInfo(deviceInfo) > 0){
				deviceInfoDao.upsertDeviceInfoSvId(deviceInfo);
				deviceInfoDao.upsertDeviceInfoPort(deviceInfo);
			}			
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}		
	}
	
	@Override
	public void updateDeviceStatus(String deviceId, boolean status) {
		if(status){
			deviceInfoDao.updateDeviceStatusCon(deviceId);			
		}else{
			deviceInfoDao.updateDeviceStatusDisCon(deviceId);
		}
	}
	
	@Override
	public void updateNoConDeviceStatus(int minutes) {
		try {
			deviceInfoDao.updateNoConDeviceStatus(minutes);
		} catch (Exception e) {}
	}
}
