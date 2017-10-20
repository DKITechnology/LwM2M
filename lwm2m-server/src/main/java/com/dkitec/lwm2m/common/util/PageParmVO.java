package com.dkitec.lwm2m.common.util;

public class PageParmVO {

	/** 목록 제한 개수 : default 20**/
	private int limit = 20;
	
	/** 목록 시작 위치 : default 0**/
	private int offset = 0;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
}
