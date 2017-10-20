package com.dkitec.argosiot.commonapi;

import java.util.HashMap;
import java.util.List;

import com.dkitec.argosiot.commonapi.domain.ProcessContent;

public interface DynamicProc {
	
	/**
	 * 통합 동적 API 처리
	 * 
	 * @param ProcessContent processContent
	 * @param HashMap<String, Object> bodyMap
	 * @return HashMap<String, Object>
	 */	
	Object dynamicApiProcess(ProcessContent processContent, HashMap<String, Object> bodyMap) throws Exception;
	
	/**
	 * 파라미터 생성
	 * 
	 * @param ProcessContent processContent
	 * @param HashMap<String, Object> bodyMap
	 * @return List<List<Object>>
	 */	
	List<List<Object>> paramMake(ProcessContent processContent, HashMap<String, Object> bodyMap) throws Exception;
	
	/**
	 * 파라미터 Array 추출
	 * 
	 * @param String param, 
	 * @param HashMap<String, Object> bodyMap
	 * @param int arrayNo
	 * @return HashMap<String, Object>
	 */	
	HashMap<String, Object> paramArrayMake(String param, HashMap<String, Object> bodyMap, int arrayNo) throws Exception;
	
	/**
	 * 결과 검증
	 * 
	 * @param ProcessContent processContent
	 * @param HashMap<String, Object> resultMap
	 * @return boolean
	 */	
	boolean resultValidationProcess(ProcessContent processContent, HashMap<String, Object> resultMap) throws Exception;

	/**
	 * RDB 쿼리 실행
	 * 
	 * @param ProcessContent processContent
	 * @param List<Object> paramList
	 * @return Object
	 */	
	Object rdbQueryProcess(ProcessContent processContent, List<Object> paramList) throws Exception;

	/**
	 * map Key 제거
	 * 
	 * @param String field
	 * @param HashMap<String, Object> map
	 * @return HashMap<String, Object>
	 */	
	HashMap<String, Object> excludeField(String field, HashMap<String, Object> map) throws Exception;
	
	/**
	 * insert 항목 Key 바인딩
	 * 
	 * @param String insertKey
	 * @param HashMap<String, Object> insertKeyMap
	 * @param Object value
	 * @return HashMap<String, Object>
	 */	
	HashMap<String, Object> insertKeyParse(String insertKey, HashMap<String, Object> insertKeyMap, Object value) throws Exception;
	
	/**
	 * 결과 항목 Key 바인딩
	 * 
	 * @param String resultKey
	 * @param HashMap<String, Object> resultKeyMap
	 * @param HashMap<String, Object> valueMap
	 * @return HashMap<String, Object>
	 */	
	HashMap<String, Object> resultKeyParse(String resultKey, HashMap<String, Object> resultKeyMap, HashMap<String, Object> valueMap) throws Exception;
	
	/**
	 * MongoDB 쿼리 실행
	 * 
	 * @param ProcessContent processContent
	 * @param List<Object> paramList
	 * @return Object
	 */	
	Object mongodbQueryProcess(ProcessContent processContent, List<Object> paramList) throws Exception;
}
