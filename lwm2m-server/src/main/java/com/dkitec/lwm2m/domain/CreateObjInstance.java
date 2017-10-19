package com.dkitec.lwm2m.domain;

import java.util.List;

import org.eclipse.leshan.core.node.LwM2mResource;

public class CreateObjInstance {

	/** Lwm2m object Instance ID **/
	private int instanceId;
	
	/** Lwm2m 리소스 생성 목록**/
	private LwM2mResource resource;
	
	/** Lwm2m 리소스 생성 목록**/
	private List<LwM2mResource> resources;

	public int getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(int instanceId) {
		
		this.instanceId = instanceId;
	}

	public LwM2mResource getResource() {
		return resource;
	}

	public void setResource(LwM2mResource resource) {
		this.resource = resource;
	}

	public List<LwM2mResource> getResources() {
		return resources;
	}

	public void setResources(List<LwM2mResource> resources) {
		this.resources = resources;
	}
}
