package com.dkitec.lwm2m.common.util;

import java.util.List;

public class JsonResultList<T> {

	private List<T> resultList;
	
	private String resultTotalCnt;

	public List<T> getResultList() {
		return resultList;
	}

	public void setResultList(List<T> resultList) {
		this.resultList = resultList;
	}

	public String getResultTotalCnt() {
		return resultTotalCnt;
	}

	public void setResultTotalCnt(String resultTotalCnt) {
		this.resultTotalCnt = resultTotalCnt;
	}
}
