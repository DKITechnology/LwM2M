package com.dkitec.lwm2m.domain;

import java.util.List;

import org.eclipse.leshan.Link;
import org.eclipse.leshan.core.model.ObjectModel;

public class DeviceObjectInfo {
	/** 단말아이디 **/
	private String devcId;
	/** 단말 object model 정보 목록**/
	List<ObjectModel> devcModels;
	/** 단말 objectLink 정보*/
	Link[] objectLinks; 	
	List<DeviceObserveInfoVO> observeInfos;
 	
	public String getDevcId() {
		return devcId;
	}
	public void setDevcId(String devcId) {
		this.devcId = devcId;
	}
	public List<ObjectModel> getDevcModels() {
		return devcModels;
	}
	public void setDevcModels(List<ObjectModel> devcModels) {
		this.devcModels = devcModels;
	}
	public Link[] getObjectLinks() {
		return objectLinks;
	}
	public void setObjectLinks(Link[] objectLinks) {
		this.objectLinks = objectLinks;
	}
	public List<DeviceObserveInfoVO> getObserveInfos() {
		return observeInfos;
	}
	public void setObserveInfos(List<DeviceObserveInfoVO> observeInfos) {
		this.observeInfos = observeInfos;
	}
}
