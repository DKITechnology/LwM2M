package com.dkitec.lwm2m.domain.workflow;

import java.io.Serializable;

public class ReadResponseVO implements Serializable{

	/** LWM2M Resource ID***/
	int id;
	
	/** LWM2M Resource Value***/
	String value;
	
	/** LWM2M Resource Type***/
	String type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
