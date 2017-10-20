package com.dkitec.lwm2m.common.util.json;

import java.util.List;

public class JsonResultList<T> {

	private List<T> resultList;
	
	private int resultTotalCnt;

	public List<T> getResultList() {
		return resultList;
	}

	public void setResultList(List<T> resultList) {
		this.resultList = resultList;
	}

	public int getResultTotalCnt() {
		return resultTotalCnt;
	}

	public void setResultTotalCnt(int resultTotalCnt) {
		this.resultTotalCnt = resultTotalCnt;
	}
}
