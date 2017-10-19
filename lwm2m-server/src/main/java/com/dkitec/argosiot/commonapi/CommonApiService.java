package com.dkitec.argosiot.commonapi;

import java.util.HashMap;

import com.dkitec.argosiot.commonapi.domain.CommonApiInfo;

/**
 * <b>클래스 설명</b>  : 
 * @author : DKI
 */
public interface CommonApiService {

	
	public final String APISERNO = "apiSerno";
	
	public final String COMMONAPIINFO = "commonApiInfo";

	/**
	 * <b>메서드 설명</b> 	: 공통 API 모듈 처리
	 * @param map		: API 입력 항목
	 * @return			: API 처리 결과
	 * @throws Exception
	 */
	Object commonApiProcess(HashMap<String, Object> map) throws Exception;
	
	HashMap<String, Object> controlResultStandBy(String resourceId, HashMap<String, Object> map, int curDelayMils) throws Exception;
	
	/**
	 * CommonAPI 정보 조회
	 * @param reqMap
	 * @return
	 */
	public CommonApiInfo selectCommonApiInfo(HashMap<String, Object> reqMap);
	
}
