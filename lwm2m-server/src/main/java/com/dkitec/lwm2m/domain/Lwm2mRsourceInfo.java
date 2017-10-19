package com.dkitec.lwm2m.domain;

import org.eclipse.leshan.core.model.ResourceModel.Type;

public class Lwm2mRsourceInfo {

	/** resource ID **/
	private int rscId;
	
	/** resource Value **/
	private Object rscValue;
	
	/** resource Type */
	private Type rscType;
	
	private String rscDtFormat;
	
	public int getRscId() {
		return rscId;
	}
	public void setRscId(int rscId) {
		this.rscId = rscId;
	}
	public Object getRscValue() {
		return rscValue;
	}
	public void setRscValue(Object rscValue) {
		this.rscValue = rscValue;
	}	
	public Type getRscType() {
		return rscType;
	}
	public void setRscType(Type rscType) {
		this.rscType = rscType;
	}	
	public String getRscDtFormat() {
		return rscDtFormat;
	}
	public void setRscDtFormat(String rscDtFormat) {
		this.rscDtFormat = rscDtFormat;
	}
}
