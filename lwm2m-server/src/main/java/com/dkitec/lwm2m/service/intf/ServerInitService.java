package com.dkitec.lwm2m.service.intf;

import java.util.List;

import org.eclipse.leshan.core.model.ObjectModel;

public interface ServerInitService {

	public void insertDefaultModelInfo();
	
	public List<ObjectModel> selectLoadObjects();
}
