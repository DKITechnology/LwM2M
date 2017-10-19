package com.dkitec.lwm2m.common.util;

public class PageDateParmVO {

	/** 목록 제한 개수 : default 20**/
	private int limit = 10;
	
	/** 목록 시작 위치 : default 0**/
	private int offset = 0;
	
	/** 검색 조건 시작 일시(YYYYMMDD)*/
	private String startDate;
	
	/** 검색 조건 종료 일시(YYYYMMDD)*/
	private String endDate;

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

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}
