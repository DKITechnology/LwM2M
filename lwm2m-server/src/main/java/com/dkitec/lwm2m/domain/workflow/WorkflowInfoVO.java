package com.dkitec.lwm2m.domain.workflow;

public class WorkflowInfoVO {

	private int apiSerno;
	
	private String wkId;
	
	private int execSeq;

	public int getApiSerno() {
		return apiSerno;
	}

	public void setApiSerno(int apiSerno) {
		this.apiSerno = apiSerno;
	}

	public String getWkId() {
		return wkId;
	}

	public void setWkId(String wkId) {
		this.wkId = wkId;
	}

	public int getExecSeq() {
		return execSeq;
	}

	public void setExecSeq(int execSeq) {
		this.execSeq = execSeq;
	}
}
