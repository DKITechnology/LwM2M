package com.dkitec.lwm2m.domain;

import java.util.List;
import java.util.Map;

import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.LwM2mSingleResource;

public class Lwm2mObjectInfo {
	/**instanceID*/
	private String instanceId;
	/**LwM2mResource*/
	List<Lwm2mRsourceInfo> resources;
	/**오브젝트 인스턴스생성 위치 정보**/
	String locationPath;
	
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public List<Lwm2mRsourceInfo> getResources() {
		return resources;
	}
	public void setResources(List<Lwm2mRsourceInfo> resources) {
		this.resources = resources;
	}
	public String getLocationPath() {
		return locationPath;
	}
	public void setLocationPath(String locationPath) {
		this.locationPath = locationPath;
	}
}
