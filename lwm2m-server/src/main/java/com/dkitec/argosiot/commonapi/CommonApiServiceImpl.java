package com.dkitec.argosiot.commonapi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dkitec.argosiot.commonapi.dao.CustomJdbcDao;
import com.dkitec.argosiot.commonapi.domain.CommonApiContent;
import com.dkitec.argosiot.commonapi.domain.CommonApiInfo;
import com.dkitec.argosiot.commonapi.domain.ProcessContent;
import com.dkitec.argosiot.commonapi.domain.RequestContent;
import com.dkitec.argosiot.commonapi.util.CommonApiUtil;
import com.dkitec.lwm2m.dao.CommonApiInfoDao;
import com.dkitec.lwm2m.rdao.common.CommonRedisPoolDAO;
import com.dkitec.lwm2m.service.workflow.WorkFlowService;
import com.google.gson.Gson;

/**
 * <b>클래스 설명</b>  : 
 * @author : DKI
 */
@Service
public class CommonApiServiceImpl implements CommonApiService, Serializable, CommonApiCode {

	private static final long serialVersionUID = 1L;
	
	private final Logger logger = LoggerFactory.getLogger(CommonApiServiceImpl.class);
	
    @Autowired
    @Qualifier("commonApiProp") 
    private Properties commonApiProp;

	@Autowired
	private DynamicProc dynamicApiProc;

	@Autowired
	private CustomJdbcDao customJdbcTemplate;
	
	@Autowired
	private CommonRedisPoolDAO commonRedisDAO;

	@Autowired
	private CommonApiInfoDao commonApiInfoDao;
	
	@Autowired
	WorkFlowService WorkFlowService;

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Object commonApiProcess(HashMap<String, Object> reqMap) throws Exception {

		String logStep = "[공통 API 모듈 처리]";
		try {
			logger.debug("reqMap: {}", reqMap);
			CommonApiInfo commonApiInfo = new CommonApiInfo();
//			commonApiInfo.setMainModuleName((String)reqMap.get("mainModuleName"));
//			commonApiInfo.setVersion((String)reqMap.get("version"));
//			commonApiInfo.setSubModuleName((String)reqMap.get("subModuleName"));
			HashMap<String, Object> bodyMap = new HashMap<String, Object>();			
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			HashMap<String, Object> resultObjectMap = new HashMap<String, Object>();
			HttpHeaders headers = new HttpHeaders();
			int orderNo = 1;
			int totalOrderNo = 0;
			
			logStep = "[공통 API 모듈 처리 - API 정보 조회]";
			commonApiInfo = (CommonApiInfo) reqMap.get(COMMONAPIINFO);
			if ( commonApiInfo == null ) {
				throw new CommonApiException(null, logStep
						, ERROR_NOTFOUND_CODE
						, CommonApiUtil.getMessage("comAPI.error.notFound.msg")
						);
			}
			
			if ( commonApiInfo.getMethod().compareTo(reqMap.get("method").toString()) != 0) {
				throw new CommonApiException(null, logStep
						, ERROR_METHODNOTALLOWED_CODE
						, CommonApiUtil.getMessage("comAPI.error.methodNotAllowed.msg")
						);
			}
			
			logStep = "[공통 API 모듈 처리 - Header 추출]";
			if ( reqMap.get("headers") != null ) {
				headers = (HttpHeaders) reqMap.get("headers");
			}
			
			CommonApiContent contentInfo = new Gson().fromJson(commonApiInfo.getJsonContent(), CommonApiContent.class);
			
			logStep = "[공통 API 모듈 처리 - 인증 처리]";
			if ( commonApiInfo.getApiAttcYn().compareTo("Y") == 0 ) {
				
				// 인증 API 인 경우 헤더에 사용자 ID 필수
				if ( headers.get("userId") == null ) {
					throw new CommonApiException(null, logStep
							, ERROR_FIELDVALIDATION_CODE
							, "userId" + CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.none")
							);
				}
				
				String token = "";
				List<Object> paramList = new ArrayList<Object>();
				HashMap<String, Object> tokenMap = new HashMap<String, Object>();
				String query = commonApiProp.getProperty("comAPI.auth.com.query");
				String userId = headers.get("userId").get(0).toString();
				
				// OAM 에서 호출한 경우
				if ( headers.get("SESSION_KEY") != null ) {
					token = headers.get("SESSION_KEY").get(0).toString();
					query = commonApiProp.getProperty("comAPI.auth.oam.query");
				}
				// 기타
				else if ( headers.get("accessToken") != null ) {
					token = headers.get("accessToken").get(0).toString();
				}
				else {
					throw new CommonApiException(null, logStep
							, ERROR_UNAUTHORIZED_CODE
							, CommonApiUtil.getMessage("comAPI.error.unauthorized.msg")
							);
				}
				
				paramList.add(userId);
				paramList.add(token);
				paramList.add(userId);
				tokenMap = customJdbcTemplate.queryForMap(query, paramList.toArray());
				
				if ( tokenMap == null ) {
					throw new CommonApiException(null, logStep
							, ERROR_UNAUTHORIZED_CODE
							, CommonApiUtil.getMessage("comAPI.error.unauthorized.msg")
							);
				}
				else if ( tokenMap.get("TOKEN") == null ) {
					throw new CommonApiException(null, logStep
							, ERROR_INVALIDTOKEN_CODE
							, CommonApiUtil.getMessage("comAPI.error.invalidToken.msg")
							);
				}
				else if ( tokenMap.get("TOKEN_VALIDITY") != null && tokenMap.get("TOKEN_VALIDITY").toString().compareTo("Y") == 0 ) {
					throw new CommonApiException(null, logStep
							, ERROR_EXPIREDTOKEN_CODE
							, CommonApiUtil.getMessage("comAPI.error.expiredToken.msg")
							);
				}

			}
			
			logStep = "[공통 API 모듈 처리 - Body 추출]";
			if ( reqMap.get("bodyMap") != null) {
				bodyMap = (HashMap<String, Object>) reqMap.get("bodyMap");
			}

			switch (commonApiInfo.getMethod()) {
			
			case "GET" : case "DELETE" :
				
				/***** urlParameters *****/
				logStep = "[공통 API 모듈 처리 - URL Parameters 추출]";
				Map<String, String[]> urlParamMap = (Map<String, String[]>)reqMap.get("urlParameters");
				if( urlParamMap != null && urlParamMap.size() > 0 ) {
					bodyMap.putAll(urlParameterValidation(contentInfo.getRequestContent(), urlParamMap));
				}
				
				logStep = "[공통 API 모듈 처리 - Header 추출]";
				if ( reqMap.get("headers") != null ) {
					bodyMap.put("hostUrl", reqMap.get("hostUrl"));
					bodyMap.putAll(headerValidation(contentInfo.getRequestContent(), headers, bodyMap));
				}
				
				try {
					bodyMap = fieldValidation(contentInfo.getRequestContent(), bodyMap);
				}
				catch(CommonApiException e) {
					throw e;
				}
				
				break;
				
			case "PUT" : case "POST" :
				
				logStep = "[공통 API 모듈 처리 - API 요청 항목 검증]";
				
				if ( contentInfo.getRequestContent() != null && bodyMap != null ) {	
					bodyMap.put("hostUrl", reqMap.get("hostUrl"));
					bodyMap.putAll(bodyValidation(contentInfo.getRequestContent(), bodyMap));
					referKeyValueValidation(contentInfo.getRequestContent(), bodyMap);
				}
				else if (contentInfo.getRequestContent() != null && bodyMap == null ) {
					throw new CommonApiException(null, logStep
							, ERROR_FIELDVALIDATION_CODE
							, "Body " + CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.none")
							);
				}
				break;
			}
			
			logStep = "[공통 API 모듈 처리 - 비지니스 모듈 검증]";
			if ( contentInfo.getProcessContent().size() == 0 ) {
				throw new CommonApiException(null, logStep, ERROR_INTERNALSERVERERROR_CODE, CommonApiUtil.getMessage("comAPI.error.internalServerError.msg.module"));
			}
			
			logStep = "[공통 API 모듈 처리 - 결과 처리 모듈 검증]";
			if ( contentInfo.getResponseContent().getResultType().compareTo("custom") == 0 
					&& contentInfo.getResponseContent().getResultOrderNoList().size() == 0
					) {
				throw new CommonApiException(null, logStep, ERROR_INTERNALSERVERERROR_CODE, CommonApiUtil.getMessage("comAPI.error.internalServerError.msg.module"));
			}
			
			if ( contentInfo.getResponseContent().getResultNameList() != null ) {
				if (contentInfo.getResponseContent().getResultOrderNoList().size() != contentInfo.getResponseContent().getResultNameList().size() ) {
					throw new CommonApiException(null, logStep, ERROR_INTERNALSERVERERROR_CODE, CommonApiUtil.getMessage("comAPI.error.internalServerError.msg.resultModule"));
				}
			}
			
			logStep = "[공통 API 모듈 처리 - 비지니스 모듈 처리]";
			totalOrderNo = contentInfo.getProcessContent().size();

			while ( orderNo <= totalOrderNo ) {

				ProcessContent processContent = new ProcessContent();
				for ( ProcessContent processContentTmp : contentInfo.getProcessContent() ) {
					if ( Integer.valueOf(processContentTmp.getOrderNo()) == orderNo ) {
						processContent = processContentTmp;
						break;
					}
				}
				
				logStep = "[공통 API 모듈 처리 - 병합 프로세스 처리]";
				if ( processContent.getProcessType().contains("merge") ) {
					Object rdbObj = resultObjectMap.get(processContent.getMergeContent().getRdbOrderNo());
					Object mongoObj = resultObjectMap.get(processContent.getMergeContent().getMongodbOrderNo());
					
					List<HashMap<String, Object>> rdbMapList = new ArrayList<HashMap<String, Object>>();
					List<HashMap<String, Object>> mongoMapList = new ArrayList<HashMap<String, Object>>();
					int mongoIndex = 0;
					
					if ( rdbObj instanceof ArrayList ) { 
						rdbMapList.addAll( (List<HashMap<String, Object>>) rdbObj); 
					}
					else {
						rdbMapList.add((HashMap<String, Object>) rdbObj);
					}
					
					if ( mongoObj instanceof ArrayList ) {
						mongoMapList.addAll( (List<HashMap<String, Object>>) mongoObj); 
					}
					else {
						mongoMapList.add((HashMap<String, Object>) mongoObj);
					}

					while ( mongoIndex < mongoMapList.size() ) {
						
						int referFieldIndex = 0;
						for ( String mongoField : processContent.getMergeContent().getReferMapInfo().getMongoFields() ) {
							int rdbIndex = 0;
							String rdbField = processContent.getMergeContent().getReferMapInfo().getRdbFields().get(referFieldIndex);
							Object val = existKeyValueCheck(1, mongoField, mongoMapList.get(mongoIndex));							
							
							if ( val != null ) {
							
								if ( val instanceof Double) {
									val = ((Double) val).intValue();
								}
								
								while ( rdbIndex < rdbMapList.size() ) {
									if ( !rdbMapList.get(rdbIndex).containsKey(rdbField) ) {
										throw new CommonApiException(null, logStep, ERROR_NODATAFOUND_CODE, CommonApiUtil.getMessage("comAPI.error.noDataFound.msg"));
									}
									
									if ( rdbMapList.get(rdbIndex).get(rdbField).toString().compareTo(val.toString()) == 0 ) {
										HashMap<String, Object> includeMap = new HashMap<String, Object>();
										
										int includeFieldIndex = 0;
										for ( String rdbField2 : processContent.getMergeContent().getIncludeMapInfo().getRdbFields().get(referFieldIndex) ) {
											String mongoField2 = processContent.getMergeContent().getIncludeMapInfo().getMongoFields().get(referFieldIndex).get(includeFieldIndex);
											
											if ( !rdbMapList.get(rdbIndex).containsKey(rdbField) ) {
												throw new CommonApiException(null, logStep, ERROR_NODATAFOUND_CODE, CommonApiUtil.getMessage("comAPI.error.noDataFound.msg"));
											}
											includeMap.put(rdbField2, rdbMapList.get(rdbIndex).get(rdbField2));
											
											mongoMapList.get(mongoIndex).putAll(
													resultArrayMake(
															mongoField2, 
															rdbField2, 
															includeMap,
															mongoMapList.get(mongoIndex)														
															)
													);
											includeFieldIndex++;
										}
										break;
									}
									rdbIndex++;
								}
							}
							referFieldIndex++;
						}						
						mongoIndex++;
					}
					
					mongoIndex = 0;
					while ( mongoIndex < mongoMapList.size() ) {
						for ( String field : processContent.getMergeContent().getExcludeMongodbFields() ) {
							dynamicApiProc.excludeField(field, mongoMapList.get(mongoIndex));
						}
						mongoIndex++;
					}
					
					resultObjectMap.put(String.valueOf(orderNo), mongoMapList);				
				}else if(processContent.getProcessType().equals("wk")){
					//WorkFlow process 처리
					WorkFlowService.callWorkFlowByApi(commonApiInfo.getSerialNo(), bodyMap);
				}else {
					resultObjectMap.put(String.valueOf(orderNo), dynamicApiProc.dynamicApiProcess(processContent, bodyMap));
				}

				logStep = "[공통 API 모듈 처리 - 처리결과 값 사용 파라미터 매핑]";
				if ( processContent.getResultTypeList() != null && resultObjectMap != null ) {
					if ( processContent.getResultTypeList().contains("ResultValue") ) {
						int paramIndex = 0;
						Object tmpObj = new Object();
						
						if ( resultObjectMap.get(String.valueOf(orderNo)) instanceof HashMap ) {
							tmpObj = ((HashMap<String, Object>) resultObjectMap.get(String.valueOf(orderNo)) ).get(processContent.getResultList().get(paramIndex));
						}
						else {
							tmpObj = resultObjectMap.get(String.valueOf(orderNo));								
						}	

						for ( String paramType : processContent.getResultTypeList() ) {
							if ( paramType.compareTo("ResultValue") == 0 ) {
								bodyMap.put(processContent.getResultList().get(paramIndex), tmpObj);	
							}
							paramIndex++;
						}					
					}		
				}
				
				orderNo++;
			}
			
			logStep = "[공통 API 모듈 처리 - 결과 전달]";
			if ( contentInfo.getResponseContent().getResultType().compareTo("default") == 0 ) {
				return contentInfo.getResponseContent().getContent();
			}
			else {
				resultMap = (HashMap<String, Object>) contentInfo.getResponseContent().getContent();				

				int responseIndex = 0;
				while ( responseIndex < contentInfo.getResponseContent().getResultOrderNoList().size() ) {
					
					if ( resultObjectMap.get(contentInfo.getResponseContent().getResultOrderNoList().get(responseIndex)) == null ) {
						responseIndex++;
						continue;
					}
					
					if ( contentInfo.getResponseContent().getResultNameList() != null ) {
					
						if ( contentInfo.getResponseContent().getResultNameList().get(responseIndex) != null ) {
							
							if ( contentInfo.getResponseContent().getResultNameList().get(responseIndex).contains(".") ) {
								resultMap.putAll(
										resultArrayMake(
												contentInfo.getResponseContent().getResultNameList().get(responseIndex), 
												contentInfo.getResponseContent().getResultOrderNoList().get(responseIndex), 
												resultObjectMap, 
												resultMap
												)
										);
							}
							else if("".equals(contentInfo.getResponseContent().getResultNameList().get(responseIndex))) {
								resultMap.putAll((Map<? extends String, ? extends Object>) resultObjectMap.get(contentInfo.getResponseContent().getResultOrderNoList().get(responseIndex)));
							}
							else {
								resultMap.put(contentInfo.getResponseContent().getResultNameList().get(responseIndex), 
										resultObjectMap.get(contentInfo.getResponseContent().getResultOrderNoList().get(responseIndex)));
							}
						}
						else {
							resultMap.putAll((Map<? extends String, ? extends Object>) resultObjectMap.get(contentInfo.getResponseContent().getResultOrderNoList().get(responseIndex)));
						}
					}
					else {
						resultMap.putAll((Map<? extends String, ? extends Object>) resultObjectMap.get(contentInfo.getResponseContent().getResultOrderNoList().get(responseIndex)));
					}
					responseIndex++;
				}

				return resultMap;
			}

		} catch (CommonApiException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}
	}	
    
	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public HashMap<String, Object> resultArrayMake(String key, String index, HashMap<String, Object> resultObjectMap, HashMap<String, Object> resultMap) throws Exception {	
		String logStep = "[결과 Array 매핑]";
		try {

			HashMap<String, Object> tmpMap = new HashMap<String, Object>();	

			if ( key.contains(".")) {
				String[] splitKey = key.split("[.]", 2);
				
				if ( resultMap.get(splitKey[0]) instanceof ArrayList ) {
					List<HashMap<String, Object>> tmpMapList = (List<HashMap<String, Object>>) resultMap.get(splitKey[0]);
					List<HashMap<String, Object>> tmpMapList2 = new ArrayList<HashMap<String, Object>>();
					for ( HashMap<String, Object> tmpMap2 : tmpMapList ) {
						tmpMapList2.add(resultArrayMake(splitKey[1], index, resultObjectMap, tmpMap2));						
					}
					resultMap.put(splitKey[0], tmpMapList2);
				}
				else {
					tmpMap.putAll((Map<? extends String, ? extends Object>) resultMap.get(splitKey[0]));
					resultMap.put(splitKey[0], resultArrayMake(splitKey[1], index, resultObjectMap, tmpMap));
				}								
			}
			else {
				resultMap.put(key, resultObjectMap.get(index));
			}
			return resultMap;
		}
		catch (CommonApiException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public HashMap<String, Object> headerValidation(List<RequestContent> requestContentList, HttpHeaders headers, HashMap<String, Object> bodyMap) throws Exception {	
		String logStep = "[요청 Header 검증]";
		try {
			//HashMap<String, Object> bodyMap = new HashMap<String, Object>();
			
			if (requestContentList == null ) {
				return bodyMap;
			}
			
			if ( requestContentList.size() > 0 ) {
				for ( RequestContent requestContent : requestContentList ) {

					if(bodyMap.containsKey(requestContent.getName())) {
						continue;
					}
					
					if ( headers.getFirst(requestContent.getName()) == null && requestContent.getDefaultValue() != null ) {
						bodyMap.put(requestContent.getName(), requestContent.getDefaultValue());
					}
					else if ( headers.getFirst(requestContent.getName()) != null) {
						bodyMap.put(requestContent.getName(), headers.getFirst(requestContent.getName()));
					}
					
					// fieldValidation은 urlParameter까지 정리된 후에 한번에 진행
					//bodyMap = fieldValidation(requestContent, bodyMap);						
				}
			}

			return bodyMap;
		}
		catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public HashMap<String, Object> bodyValidation(List<RequestContent> requestContentList, HashMap<String, Object> bodyMap) throws Exception {	
		String logStep = "[요청 Body 검증]";
		try {
			
			for ( RequestContent requestContent : requestContentList ) {
				
				if ( requestContent.getType().compareTo("Array") == 0 
						&& requestContent.getValidation().getNullable().compareTo("N") == 0
						) {
					bodyMap = fieldValidation(requestContent, bodyMap);			
					
					List<HashMap<String, Object>> subKeyMapList = (List<HashMap<String, Object>>) bodyMap.get(requestContent.getName());
					
					if ( subKeyMapList.size() == 0 ) {
						throw new CommonApiException(null, logStep, ERROR_FIELDVALIDATION_CODE, requestContent.getName() + " " + CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.none"));
					}
					
					if ( bodyMap.get(requestContent.getName()) instanceof ArrayList ) {
						List<HashMap<String, Object>> subKeyMapListNtmp = new ArrayList<HashMap<String, Object>>();
						List<HashMap<String, Object>> subKeyMapListN = (List<HashMap<String, Object>>) bodyMap.get(requestContent.getName());
						
						for ( HashMap<String, Object> subKeyMap : subKeyMapListN) {
							subKeyMapListNtmp.add(bodyValidation(requestContent.getSubKeyList(), subKeyMap));
						}
						bodyMap.put(requestContent.getName(), subKeyMapListNtmp);
					}
					else if ( bodyMap.get(requestContent.getName()) instanceof HashMap ) {
						bodyMap.put(requestContent.getName(), bodyValidation(requestContent.getSubKeyList(), (HashMap<String, Object>) bodyMap.get(requestContent.getName())));
					}
				}
				else if ( requestContent.getType().compareTo("Array") == 0 
						&& requestContent.getValidation().getNullable().compareTo("Y") == 0
						&& bodyMap.get(requestContent.getName()) != null ) {					
					
					if ( bodyMap.get(requestContent.getName()) instanceof ArrayList ) {
						List<HashMap<String, Object>> subKeyMapListYtmp = new ArrayList<HashMap<String, Object>>();
						List<HashMap<String, Object>> subKeyMapListY = (List<HashMap<String, Object>>) bodyMap.get(requestContent.getName());
						
						for ( HashMap<String, Object> subKeyMap : subKeyMapListY) {
							subKeyMapListYtmp.add(bodyValidation(requestContent.getSubKeyList(), subKeyMap));							
						}
						bodyMap.put(requestContent.getName(), subKeyMapListYtmp);
					}
					else if ( bodyMap.get(requestContent.getName()) instanceof HashMap ) {
						bodyMap.put(requestContent.getName(), bodyValidation(requestContent.getSubKeyList(), (HashMap<String, Object>) bodyMap.get(requestContent.getName())));							
					}
				}
				else {
					bodyMap = fieldValidation(requestContent, bodyMap);
				}
			}
			
			return bodyMap;
		}
		catch (CommonApiException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}
	}
	
	@Transactional(rollbackFor = Exception.class)
	public HashMap<String, Object> fieldValidation(RequestContent requestContent, HashMap<String, Object> bodyMap) throws Exception {	
		String logStep = "[요청 항목 검증]";
		try {

			// 기본값 적용
			if ( requestContent.getDefaultValue() != null && bodyMap.get(requestContent.getName()) == null ) {
				bodyMap.put(requestContent.getName(), requestContent.getDefaultValue());
			}
			
			// null 허용
			if ( bodyMap.get(requestContent.getName()) == null  
					&& requestContent.getValidation().getNullable().compareTo("N") == 0 ){
				throw new CommonApiException(null, logStep, ERROR_FIELDVALIDATION_CODE, requestContent.getName() + " " + CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.none"));
			}
			else if(requestContent.getValidation().getNullable().compareTo("Y") == 0) {
				return bodyMap;
			}
			
			
			if ( bodyMap.get(requestContent.getName()) != null  					
					&& requestContent.getType().compareTo("String") == 0 ) {
				
				// 길이
				if ( Integer.valueOf(requestContent.getValidation().getLength()) < bodyMap.get(requestContent.getName()).toString().length() ) {
					throw new CommonApiException(null, logStep, ERROR_FIELDVALIDATION_CODE, requestContent.getName() + "항목은 " + requestContent.getValidation().getLength() + " " + CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.length"));
				}
				
				// 정규식
				if ( requestContent.getValidation().getRegex() != null ) {					
					if ( !bodyMap.get(requestContent.getName()).toString().matches(requestContent.getValidation().getRegex()) ) {
						throw new CommonApiException(null, logStep, ERROR_FIELDVALIDATION_CODE, requestContent.getName() + "항목은 " + CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.regex"));
					}
				}
			}
			
			return bodyMap;
		}
		catch (CommonApiException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean referKeyValueValidation(List<RequestContent> requestContentList, HashMap<String, Object> bodyMap) throws Exception {	
		String logStep = "[참조 항목 검색]";
		try {

			for ( RequestContent requestContent : requestContentList ) {

				if ( requestContent.getValidation().getReferKey() != null ) {

					if ( existKeyValueCheck(0, requestContent.getName(), bodyMap).toString().contains("true") && existKeyValueCheck(0, requestContent.getValidation().getReferKey(), bodyMap).toString().contains("false") ) {
						
						if ( requestContent.getValidation().getReferKeyValue() != null ) {
							if ( requestContent.getValidation().getReferKeyValue().toString().compareTo(bodyMap.get(requestContent.getName()).toString()) == 0 ) {							
								throw new CommonApiException(null, logStep, ERROR_FIELDVALIDATION_CODE, requestContent.getValidation().getReferKey() + " " + CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.none"));
							}
						}
						else {
							throw new CommonApiException(null, logStep, ERROR_FIELDVALIDATION_CODE, requestContent.getValidation().getReferKey() + " " + CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.none"));
						}
					}					
				}
				
				if ( requestContent.getType().compareTo("Array") == 0 ) {
					referKeyValueValidation(requestContent.getSubKeyList(), bodyMap);
				}
			}
			
			return true;
		}
		catch (CommonApiException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}
	}
	
	@SuppressWarnings({ "unchecked"})
	@Transactional(rollbackFor = Exception.class)
	public Object existKeyValueCheck(int procFlag, String key, HashMap<String, Object> bodyMap) throws Exception {	
		String logStep = "[Key/Value 검색]";
		try {
			Object resultObj = new Object();

			if ( procFlag == 0 ) {
				resultObj = "false";
			}
			else {
				resultObj = null;
			}
			
			if ( key.contains(".") ) {
				int resultKeyIndex = 0;
				String parseKey = "";
				String[] paramSplit = key.split("[.]", 2);				

				key = paramSplit[resultKeyIndex];

				for ( Entry<String, Object> elem : bodyMap.entrySet() ) {	
					
					if ( elem.getKey().compareTo(key) == 0 
							&& elem.getValue() != null ) {
						parseKey = elem.getKey();
						
						resultKeyIndex++;
					
						if ( elem.getValue() instanceof ArrayList ) {
							for ( HashMap<String, Object> map : (List<HashMap<String, Object>>) elem.getValue() ) {
									
								resultObj = existKeyValueCheck(procFlag, paramSplit[resultKeyIndex], map);
								
								if ( procFlag == 0 && resultObj.toString().contains("false") ) {
									return resultObj;
								}
								else if ( procFlag != 0 && resultObj != null ) {
									return resultObj;
								}
							}
						}
						else if ( elem.getValue() instanceof HashMap ) {
							return existKeyValueCheck(procFlag, paramSplit[resultKeyIndex], (HashMap<String, Object>) elem.getValue());
						}

						break;
					}	
				}				
				
				if ( key.compareTo(parseKey) == 0) {
					return "true";
				}
				else {
					return "false";
				}					
				
			}
			else {
				for ( Entry<String, Object> elem : bodyMap.entrySet() ) {

					if ( elem.getKey().compareTo(key) == 0 
							&& elem.getValue() != null ) {

						if ( procFlag == 0 ) {
							return "true";
						}
						else {
							return elem.getValue();
						}
					}	
					else if ( elem.getValue() instanceof ArrayList ) {
						for ( HashMap<String, Object> map : (List<HashMap<String, Object>>) elem.getValue() ) {
							resultObj = existKeyValueCheck(procFlag, key, map);
							
							if ( procFlag == 0 && resultObj.toString().contains("false") ) {
								return resultObj;
							}
							else if ( procFlag != 0 && resultObj != null ) {
								return resultObj;
							}
						}
					}
					else if ( elem.getValue() instanceof HashMap ) {
						return existKeyValueCheck(procFlag, key, (HashMap<String, Object>) elem.getValue());
					}
				}
			}
			
			return resultObj;
		} catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}
	}
	
	public HashMap<String, Object> controlResultStandBy(String resourceId, HashMap<String, Object> map, int curDelayMils) throws Exception {
		String logStep = "[제어응답 대기]";
		
		if ( resourceId == null ) {
			throw new CommonApiException(null, logStep, ERROR_INTERNALSERVERERROR_CODE, CommonApiUtil.getMessage("comAPI.error.internalServerError.msg"));
		}
		
		Integer resourceIdSuffix = 0;
		if(resourceId != null && resourceId.length() > 4) {
			resourceIdSuffix = Integer.parseInt(resourceId.substring(resourceId.length()-4, resourceId.length()));
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("resourceID : {}, suffix: {}", resourceId, resourceIdSuffix);
		}
		
		Object controlResult = commonRedisDAO.getValue(resourceId);
		if(controlResult == null) {
			controlResult = commonRedisDAO.getValue(resourceIdSuffix == 0 ? resourceId : resourceIdSuffix+"");
		}
		
		int dealyMils = 10000;
		try {
			dealyMils = Integer.parseInt(commonApiProp.getProperty("comAPI.control.maxDelayMils", "10000"));
		}catch (Exception e) {}
		
		if( controlResult != null ) {
			map.put("controlResult", controlResult);
		}
		else if ( curDelayMils <=  dealyMils ) {
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new CommonApiException(null, logStep, ERROR_CONTROLFAIL_CODE, CommonApiUtil.getMessage("comAPI.error.controlFail.msg"));
			}
			
			curDelayMils += 1000;
			return this.controlResultStandBy(resourceId, map, curDelayMils);
		}
		else {
			throw new CommonApiException(null, logStep, ERROR_CONTROLTIMEOUT_CODE, CommonApiUtil.getMessage("comAPI.error.controlTimeout.msg"));
			//if ( controlResult == null ) {			
			//}
			//else if (conrtolResult.toString().compareTo("N") == 0) {
			//	throw new CommonApiException(null, logStep, ERROR_CONTROLFAIL_CODE, CommonApiUtil.getMessage("comAPI.error.controlFail.msg"));
			//}
		}
		
		return map;
	}

	/**
	 * URL Parameter를 RequestContent로부터 검증한다.
	 * @param requestContentList
	 * @param urlParamMap
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> urlParameterValidation(List<RequestContent> requestContentList, Map<String, String[]> urlParamMap) throws Exception {
		String logStep = "[요청 URL Parameters 검증]";
		try {
			HashMap<String, Object> bodyMap = new HashMap<String, Object>();
			
			if(requestContentList == null) {
				return bodyMap;
			}
			
			if(requestContentList.size() > 0) {
				for(RequestContent requestContent : requestContentList) {
					String[] urlParams = urlParamMap.get(requestContent.getName());
					String urlParam = "";
					if(urlParams != null && urlParams.length > 0) {
						urlParam = urlParams[0];
					}
					
					if(urlParam == null && requestContent.getDefaultValue() != null ) {
						bodyMap.put(requestContent.getName(), requestContent.getDefaultValue());
					}
					else if(urlParam != null) {
						bodyMap.put(requestContent.getName(), urlParam);
					}
					
					// fieldValidation은 Header까지 정리된 후에 한번에 진행
					//bodyMap = fieldValidation(requestContent, bodyMap);
				}
			}

			return bodyMap;
		}
		catch(Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}
	}
	
	/**
	 * 
	 * @param requestContents
	 * @param bodyMap
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public HashMap<String, Object> fieldValidation(List<RequestContent> requestContents, HashMap<String, Object> bodyMap) throws Exception {	
		if(requestContents != null && !requestContents.isEmpty()) {
			for(RequestContent requestContent : requestContents) {
				bodyMap = fieldValidation(requestContent, bodyMap);
			}
		}
		return bodyMap;
	}
	
	public CommonApiInfo selectCommonApiInfo(HashMap<String, Object> reqMap){
		CommonApiInfo commonApiInfo = new CommonApiInfo();
		commonApiInfo.setMainModuleName((String)reqMap.get("mainModuleName"));
		commonApiInfo.setVersion((String)reqMap.get("version"));
		commonApiInfo.setSubModuleName((String)reqMap.get("subModuleName"));
		commonApiInfo= commonApiInfoDao.selectCommonApi(commonApiInfo);
		return commonApiInfo;
	}
}
