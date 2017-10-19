package com.dkitec.lwm2m.domain.workflow;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FwUpdateConfig {

	@Value("#{serverConfigProp['fwUpdate.read.limtCnt']}")
	private String limtCnt;
	
	@Value("#{serverConfigProp['fwUpdate.read.sleepTime']}")
	private String sleepTime;

	public String getLimtCnt() {
		return limtCnt;
	}

	public String getSleepTime() {
		return sleepTime;
	}	
}
