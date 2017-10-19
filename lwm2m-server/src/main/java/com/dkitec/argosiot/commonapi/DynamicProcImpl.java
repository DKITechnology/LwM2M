package com.dkitec.argosiot.commonapi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dkitec.argosiot.commonapi.dao.CommonApiMongoDao;
import com.dkitec.argosiot.commonapi.dao.CustomJdbcDao;
import com.dkitec.argosiot.commonapi.domain.ProcessContent;
import com.dkitec.argosiot.commonapi.util.CommonApiUtil;
import com.dkitec.argosiot.commonapi.domain.MongodbContent.Nosql;
import com.mongodb.BasicDBObject;

@Component
@Transactional(rollbackFor = Exception.class)
public class DynamicProcImpl implements DynamicProc, CommonApiCode {
	
	private final Logger logger = LoggerFactory.getLogger(DynamicProcImpl.class);
	
	@Autowired
	private CustomJdbcDao customJdbcTemplate;
	
	@Autowired
	private CommonApiMongoDao commonMongoDAO;

	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	public Object dynamicApiProcess(ProcessContent processContent, HashMap<String, Object> bodyMap) throws Exception { 
		String logStep = "[동적 API 실행]";
		
		try {
			Object resultObject = new Object();
			
			// 필요 키값 체크
			if ( processContent.getRequiredConditionKey() != null && processContent.getRequiredConditionKey().trim().length() > 0 ) {  
				
				// 필요 키 value가 NULL인 경우 프로세스를 스킵함
				if ( bodyMap.get(processContent.getRequiredConditionKey()) == null 
						|| (bodyMap.get(processContent.getRequiredConditionKey()) instanceof List && ((List<Object>)bodyMap.get(processContent.getRequiredConditionKey())).isEmpty())) {
					return null;
				}
				
				// 필요 키 값이 일치하지 않는 경우 프로세스를 스킵함
				if ( processContent.getRequiredConditionValue() != null 
						&& processContent.getRequiredConditionValue().compareTo(bodyMap.get(processContent.getRequiredConditionKey()).toString()) != 0 ) {
					return null;
				}
			}			
			
			List<List<Object>> paramArrayList = paramMake(processContent, bodyMap);	
			
			if ( processContent.getProcessType().compareTo("rdb") == 0 ) {
				
				if ( paramArrayList.size() == 0) {					
					resultObject = rdbQueryProcess(processContent, null);
				}
				else {				

					for ( List<Object> paramList : paramArrayList ) {
						resultObject = rdbQueryProcess(processContent, paramList);
					}
				}
				
			}
			else if ( processContent.getProcessType().compareTo("mongodb") == 0 ) {

				for ( List<Object> paramList : paramArrayList ) {
					resultObject = mongodbQueryProcess(processContent, paramList);
				}

			}
			else if ( processContent.getProcessType().compareTo("method") == 0 ) {
						
				Method method = null;
				List<Class> methodParamClassList = new ArrayList<Class>();
				
				ClassLoader classLoader = this.getClass().getClassLoader();
				Class clsOrg = classLoader.loadClass(processContent.getMethodContent().getClassName());			
				
				Object[] args = null;

				if ( paramArrayList.size() > 0 ) { 
					for ( Object obj : paramArrayList.get(0) ) {
						if ( obj.getClass().toString().contains("HashMap") ) {
							methodParamClassList.add(HashMap.class);
						}
						else {
							methodParamClassList.add(obj.getClass());
						}
					}
				}
	
				Class[] methodParamClassArray = methodParamClassList.toArray(new Class[methodParamClassList.size()]);
				args = new Object[methodParamClassArray.length];
				
				for (int i=0; i<methodParamClassArray.length; i++) {
					args[i] = paramArrayList.get(0).get(i);
				}
				
				try {
					// Bean 등록된 경우					
					method = SpringApplicationContext.getBean(clsOrg).getClass().getMethod(processContent.getMethodContent().getMethodName(), methodParamClassArray);
					resultObject = method.invoke(SpringApplicationContext.getBean(clsOrg), args);
					
				} catch (NoSuchBeanDefinitionException e) {
					
					try {
						// 일반 클래스인 경우
						method = clsOrg.getMethod(processContent.getMethodContent().getMethodName(), methodParamClassArray);
						Object clsInstance = clsOrg.newInstance();
						
						resultObject = method.invoke(clsInstance, args);
					} catch (Exception e2) {
						throw new CommonApiException(e2, logStep, e2.getLocalizedMessage());
					}
					
				} catch (Exception e) {
					throw new CommonApiException(e, logStep, e.getLocalizedMessage());
				}				
			}
			else {
				throw new CommonApiException(null, logStep, 
						ERROR_INTERNALSERVERERROR_CODE, 
						CommonApiUtil.getMessage("comAPI.error.internalServerError.msg.module"));
			}

			return resultObject;

		} catch (CommonApiException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}	
	}
	
	@Override
	public List<List<Object>> paramMake(ProcessContent processContent, HashMap<String, Object> bodyMap) throws Exception { 
		String logStep = "[Parameter 검증 및 매핑]";
		try {
			List<List<Object>> paramArrayList = new ArrayList<List<Object>>();			
			int paramIndex = 0;
			int totalArrayNo = 0;
			int arrayNo = 0;
			
			if ( processContent.getParamList() == null ) {
				return paramArrayList;
			}
			
			if ( processContent.getParamList().size() > 0 
					&& processContent.getParamTypeList().size() > 0 
					) {
				
				do {
					List<Object> paramList = new ArrayList<Object>();

					for ( String param : processContent.getParamList() ) {												
						
						if ( processContent.getParamTypeList().get(paramIndex).compareTo("Integer") == 0 && bodyMap.get(param) != null) {
							paramList.add(Integer.valueOf(bodyMap.get(param).toString()));
						}
						else if ( processContent.getParamTypeList().get(paramIndex).compareTo("Float") == 0 && bodyMap.get(param) != null) {
							paramList.add(Float.valueOf(bodyMap.get(param).toString()));
						}					
						else if ( processContent.getParamTypeList().get(paramIndex).compareTo("Array") == 0) {													

							HashMap<String, Object> arrayMap = paramArrayMake(param, bodyMap, arrayNo);
							paramList.add(arrayMap.get("resultParam"));
							totalArrayNo = Integer.valueOf(arrayMap.get("totalArrayNo").toString());

						}
						else if ( bodyMap.get(param) != null ) {
							paramList.add(bodyMap.get(param));

						}
						else {
							paramList.add(null);
						}
						
						paramIndex++;

					}

					if ( arrayNo < totalArrayNo ) {
						arrayNo++;
					}
					
					paramArrayList.add(paramList);
					paramIndex = 0;
					
				} while ( arrayNo < totalArrayNo );
			}

			return paramArrayList;
		} catch (CommonApiException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}	
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> paramArrayMake(String param, HashMap<String, Object> bodyMap, int arrayNo) throws Exception { 
		String logStep = "[Parameter Array Value 추출]";
		
		try {
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			
			if ( param.contains(".") ) {
				int paramIndex = 0;
				String[] paramSplit = param.split("[.]", 2);
				param = paramSplit[paramIndex];
				
				if ( bodyMap.get(param) == null || 
						(bodyMap.get(param) instanceof List && ((List<Object>)bodyMap.get(param)).isEmpty())) {
					resultMap.put("resultParam", "");
					resultMap.put("totalArrayNo", 0);
					return resultMap;
				}
				else {

					if ( bodyMap.get(param) instanceof ArrayList ) {												
						
						List<HashMap<String,Object>> tmpMap = (List<HashMap<String, Object>>) bodyMap.get(param);
						
						paramIndex++;
						param = paramSplit[paramIndex];
						
						if ( param.contains(".") ) {
							return paramArrayMake(param, tmpMap.get(arrayNo), arrayNo);
						}

						resultMap.put("resultParam", tmpMap.get(arrayNo).get(param));
						resultMap.put("totalArrayNo", tmpMap.size());
					}
					else if (  bodyMap.get(param) instanceof HashMap ) {

						HashMap<String,Object> tmpMap = (HashMap<String, Object>) bodyMap.get(param);
						
						paramIndex++;
						param = paramSplit[paramIndex];
						
						if ( param.contains(".") ) {
							return paramArrayMake(param, tmpMap, arrayNo);
						}
						
						resultMap.put("resultParam", tmpMap.get(param));
						resultMap.put("totalArrayNo", 1);
					}
					else {
						throw new CommonApiException(null, logStep, 
								ERROR_FIELDVALIDATION_CODE, 
								CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.regex"));
					}
				}				
			}

			return resultMap;
		} catch (CommonApiException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}	
	}
	
	@Override
	public boolean resultValidationProcess(ProcessContent processContent, HashMap<String, Object> resultMap) throws Exception {	
		String logStep = "[쿼리 결과 검증]";
		try {
			
			if ( resultMap == null ) {
				throw new CommonApiException(null, logStep, 
						ERROR_NODATAFOUND_CODE, 
						CommonApiUtil.getMessage("comAPI.error.noDataFound.msg"));
			}
			
			if ( processContent.getValidationYn().compareTo("Y") == 0 ) {
				
				Object compareKey = processContent.getSuccessValue();
				Object compareTaget = resultMap.get( processContent.getCompareKey() == null ? "compareKey" : processContent.getCompareKey() ).toString();
				
				if( processContent.getSuccessCondition().equalsIgnoreCase("eq") ) {
					if ( !compareKey.equals(compareTaget) ) {
						throw new CommonApiException(null, logStep, 
								ERROR_FIELDVALIDATION_CODE, 
								processContent.getProcessName() + " " + CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.valid"));
					}
				} else if( processContent.getSuccessCondition().equalsIgnoreCase("ne") ) {
					if ( compareKey.equals(compareTaget) ) {
						throw new CommonApiException(null, logStep, 
								ERROR_FIELDVALIDATION_CODE, 
								processContent.getProcessName() + " " + CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.valid"));
					}
				} else if( processContent.getSuccessCondition().equalsIgnoreCase("gt") ) {
					if ( !(Integer.valueOf(compareTaget.toString()) > Integer.valueOf(compareKey.toString())) ) {
						throw new CommonApiException(null, logStep, 
								ERROR_FIELDVALIDATION_CODE, 
								processContent.getProcessName() + " " + CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.valid"));
					}
				} else if( processContent.getSuccessCondition().equalsIgnoreCase("gte") ) {
					if ( !(Integer.valueOf(compareTaget.toString()) >= Integer.valueOf(compareKey.toString())) ) {
						throw new CommonApiException(null, logStep, 
								ERROR_FIELDVALIDATION_CODE, 
								processContent.getProcessName() + " " + CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.valid"));
					}
				} else if( processContent.getSuccessCondition().equalsIgnoreCase("lt") ) {
					if ( !(Integer.valueOf(compareTaget.toString()) < Integer.valueOf(compareKey.toString())) ) {
						throw new CommonApiException(null, logStep, 
								ERROR_FIELDVALIDATION_CODE, 
								processContent.getProcessName() + " " + CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.valid"));
					}
				} else if( processContent.getSuccessCondition().equalsIgnoreCase("lte") ) {
					if ( !(Integer.valueOf(compareTaget.toString()) <= Integer.valueOf(compareKey.toString())) ) {
						throw new CommonApiException(null, logStep, 
								ERROR_FIELDVALIDATION_CODE, 
								processContent.getProcessName() + " " + CommonApiUtil.getMessage("comAPI.error.fieldValidation.msg.valid"));
					}
				} else {
					throw new CommonApiException(null, logStep, 
							ERROR_INTERNALSERVERERROR_CODE, 
							CommonApiUtil.getMessage("comAPI.error.internalServerError.msg.resultModule"));
				}
			}
			
			return true;
		}
		catch (CommonApiException e) {
			throw e;
		} 
		catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}
	}
	
	
	@SuppressWarnings("unused")
	@Override
	public Object rdbQueryProcess(ProcessContent processContent, List<Object> paramList) throws Exception {	
		String logStep = "[RDB 쿼리 처리]";

		try {
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			List<HashMap<String, Object>> resultMapList = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> sqlResultMap = new HashMap<String, Object>();			
			List<Map<String, Object>> sqlResultMapList = new ArrayList<Map<String, Object>>();
			Object resultObject = new Object();

			logStep = "[RDB 쿼리 처리 - 실행]";
			if ( processContent.getRdbContent().getSqlType().compareTo("selectOne") == 0 ) {
				
				try {
					if ( paramList == null ) {
						sqlResultMap = customJdbcTemplate.queryForMap(processContent.getRdbContent().getSql());
					}
					else {
						sqlResultMap = customJdbcTemplate.queryForMap(processContent.getRdbContent().getSql(), paramList.toArray());
					}					
				} catch (EmptyResultDataAccessException e) {
					return null;
				}
				
				logStep = "[RDB 쿼리 처리 - 결과 항목 매핑]";
				if ( processContent.getResultList()!= null ) {
					for ( String key : processContent.getResultList() ) {
						if ( sqlResultMap.containsKey(key) ) {
							resultMap.put(key, sqlResultMap.get(key));
						}
					}
					
					resultValidationProcess(processContent, resultMap);
				}
				else {
					resultMap = sqlResultMap;
					resultValidationProcess(processContent, resultMap);
				}
				
				resultObject = resultMap;
			}
			else if ( processContent.getRdbContent().getSqlType().compareTo("selectList") == 0 ) {
				
				if ( paramList == null ) {
					sqlResultMapList = customJdbcTemplate.queryForList(processContent.getRdbContent().getSql());
				}
				else {
					sqlResultMapList = customJdbcTemplate.queryForList(processContent.getRdbContent().getSql(), paramList.toArray());
				}
				
				if ( sqlResultMap == null ) {
					return null;
				}
				
				logStep = "[RDB 쿼리 처리 - 결과 항목 매핑]";
				if ( processContent.getResultList() != null ) {
					for (Map<String, Object> tmpMap : sqlResultMapList) {	
						HashMap<String, Object> tmpResultMap = new HashMap<String, Object>();
						for ( String key : processContent.getResultList() ) {
							
							if ( tmpMap.containsKey(key) ) {
								tmpResultMap.put(key, tmpMap.get(key));
							}		
						}
						resultValidationProcess(processContent, tmpResultMap);
						resultMapList.add(tmpResultMap);
					}
					resultObject = resultMapList;
				}
				else {
					for (Map<String, Object> tmpMap : sqlResultMapList) {
						HashMap<String, Object> tmpResultMap = new HashMap<String, Object>();
						tmpResultMap = (HashMap<String, Object>) tmpMap;
						resultValidationProcess(processContent, tmpResultMap);
					}
					
					resultObject = sqlResultMapList;
				}
								
			}
			else if ( processContent.getRdbContent().getSqlType().compareTo("insert") == 0 
					|| processContent.getRdbContent().getSqlType().compareTo("update") == 0
					|| processContent.getRdbContent().getSqlType().compareTo("delete") == 0
					) {
				if ( paramList == null ) {
					resultObject = customJdbcTemplate.update(processContent.getRdbContent().getSql());
				}
				else {
					resultObject = customJdbcTemplate.update(processContent.getRdbContent().getSql(), paramList.toArray());
				}
				
				resultMap.put("compareKey", resultObject);
				resultValidationProcess(processContent, resultMap);
			}
			else {
				throw new CommonApiException(null, logStep, 
						ERROR_INTERNALSERVERERROR_CODE, 
						CommonApiUtil.getMessage("comAPI.error.internalServerError.msg.resultModule"));
			}

			return resultObject;
		}
		catch (CommonApiException e) {
			throw e;
		} 
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> excludeField(String field, HashMap<String, Object> map) throws Exception {
		String logStep = "[Map Key 제거]";
		
		try {
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			
			if ( field.contains(".") ) {
				int paramIndex = 0;
				String[] paramSplit = field.split("[.]", 2);
				field = paramSplit[paramIndex];
				
				if ( map.get(field) == null ) {
					return resultMap;
				}
				else {

					if ( map.get(field) instanceof ArrayList ) {												
						
						List<HashMap<String,Object>> tmpMapList = (List<HashMap<String, Object>>) map.get(field);
						List<HashMap<String,Object>> tmpMapResultList = new ArrayList<HashMap<String, Object>>();
						
						paramIndex++;
						field = paramSplit[paramIndex];
						
						for ( HashMap<String,Object> tmpMap : tmpMapList ) {
							if ( field.contains(".") ) {
								tmpMap = excludeField(field, tmpMap);
							}
							else {
								tmpMap.remove(field);
							}
							tmpMapResultList.add(tmpMap);
						}
						map.put(paramSplit[paramIndex-1], tmpMapResultList);

					}
					else if (  map.get(field) instanceof HashMap ) {

						HashMap<String,Object> tmpMap = (HashMap<String, Object>) map.get(field);
						
						paramIndex++;
						field = paramSplit[paramIndex];
						
						if ( field.contains(".") ) {
							return excludeField(field, tmpMap);
						}

						tmpMap.remove(field);
						map.put(paramSplit[paramIndex-1], tmpMap);
					}
				}				
			}
			else {
				map.remove(field);
			}

			return map;
		} catch (CommonApiException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}	
	}

	@Override
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> insertKeyParse(String insertKey, HashMap<String, Object> insertKeyMap, Object value) throws Exception { 
		String logStep = "[MongoDB Insert Array Key 추출]";

		try {
			Object tmpObj = new Object();
			
			if ( insertKey.contains(".") ) {
				int insertKeyIndex = 0;
				String[] paramSplit = insertKey.split("[.]", 2);
				insertKey = paramSplit[insertKeyIndex];
				
				if (value instanceof HashMap || value instanceof Map) {
					HashMap<String, Object> tmpMap = (HashMap<String, Object>) value;
					tmpObj = tmpMap.get(insertKey);
				}
				else {
					return insertKeyMap;
				}
				
				insertKeyIndex++;
				insertKey = paramSplit[insertKeyIndex];
				
				return insertKeyParse(insertKey, insertKeyMap, tmpObj);
			}
			else {
				insertKeyMap.put(insertKey, value);
			}

			return insertKeyMap;
		} catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}	
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> resultKeyParse(String resultKey, HashMap<String, Object> resultKeyMap, HashMap<String, Object> valueMap) throws Exception { 
		String logStep = "[MongoDB 결과 Array Key 매핑]";

		try {
			HashMap<String, Object> tmpMap = new HashMap<String, Object>();
			
			if ( resultKey.contains(".") ) {
				int resultKeyIndex = 0;
				String[] paramSplit = resultKey.split("[.]", 2);
				resultKey = paramSplit[resultKeyIndex];
				
				if ( valueMap.containsKey(resultKey) ) {
					tmpMap = (HashMap<String, Object>) valueMap.get(resultKey);
				}
				else {
					return resultKeyMap;
				}				
				
				resultKeyIndex++;
				resultKey = paramSplit[resultKeyIndex];
				
				return resultKeyParse(resultKey, resultKeyMap, tmpMap);
			}
			else if ( valueMap.containsKey(resultKey) ) {
				resultKeyMap.put(resultKey, valueMap.get(resultKey));
			}

			return resultKeyMap;
		} catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}	
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Object mongodbQueryProcess(ProcessContent processContent, List<Object> paramList) throws Exception {	
		String logStep = "[MongoDB 쿼리 처리]";
		try {
			HashMap<String, Object> resultMap = new HashMap<String, Object>();
			List<HashMap<String, Object>> resultMapList = new ArrayList<HashMap<String, Object>>();
			HashMap<String, Object> sqlResultMap = new HashMap<String, Object>();			
			List<Map<String, Object>> sqlResultMapList = new ArrayList<Map<String, Object>>();
			Object resultObject = new Object();
			Query query = new Query();
			Update update = new Update();
			HashMap<String, Object> insertMap = new HashMap<String, Object>();
			int paramIndex = -1;
			int limit = 10;
			int offset = 0;

			commonMongoDAO.setCls(HashMap.class);
			
			commonMongoDAO.setCollectionName(processContent.getMongodbContent().getCollectionName());
			
			if ( paramList == null && processContent.getMongodbContent().getNosqlType().compareTo("insert") != 0 ) {
				throw new CommonApiException(null, logStep, 
						ERROR_INTERNALSERVERERROR_CODE, 
						CommonApiUtil.getMessage("comAPI.error.internalServerError.msg.module"));
			}
			
			logStep = "[MongoDB 쿼리 처리 - 조건문 생성]";
			if ( paramList != null ) {
				for ( Nosql nosql : processContent.getMongodbContent().getNosql() ) {
					paramIndex++;
					
					// paramList 크기를 넘어갈 경우 종료
					if(paramList.size() <= paramIndex) {
						break;
					}
					// 공백문자 또는 null인 경우 검색 조건 생성에서 제외
					if(paramList.get(paramIndex) == null || "".equals(paramList.get(paramIndex).toString())) {
						continue;
					}
					
					BasicDBObject dbo = new BasicDBObject();
					BasicQuery queryTmp = new BasicQuery(dbo);
					
					try {
						if (nosql.getConditionType().equalsIgnoreCase("regex")) {
							query.addCriteria(Criteria.where(nosql.getConditionKey().toString()).regex(paramList.get(paramIndex).toString()));
							continue;
						} else if (nosql.getConditionType().equalsIgnoreCase("is")) {
							query.addCriteria(Criteria.where(nosql.getConditionKey().toString()).is(paramList.get(paramIndex).toString()));
							continue;
						} else if (nosql.getConditionType().equalsIgnoreCase("ne")) {
							query.addCriteria(Criteria.where(nosql.getConditionKey().toString()).ne(paramList.get(paramIndex).toString()));
							continue;
						} else if (nosql.getConditionType().equalsIgnoreCase("in")) {
							query.addCriteria(Criteria.where(nosql.getConditionKey().toString()).in(paramList.get(paramIndex)));
							continue;
						} else if (nosql.getConditionType().equalsIgnoreCase("gt")
								|| nosql.getConditionType().equalsIgnoreCase("gte")
								|| nosql.getConditionType().equalsIgnoreCase("lt")
								|| nosql.getConditionType().equalsIgnoreCase("lte")) {
						
							dbo = (BasicDBObject) query.getQueryObject();
							
							if ( query.getQueryObject().containsField(nosql.getConditionKey().toString()) ) {
																
								BasicDBObject dbo2 = (BasicDBObject) query.getQueryObject().get(nosql.getConditionKey().toString());
								
								dbo2.append("$"+nosql.getConditionType(), paramList.get(paramIndex).toString());
								dbo.put(nosql.getConditionKey().toString(), dbo2);

							}
							else {
								dbo.append(nosql.getConditionKey().toString(),
										new BasicDBObject("$"+nosql.getConditionType(), paramList.get(paramIndex).toString())
										);
							}							
							
							queryTmp = new BasicQuery(dbo);
							query = queryTmp;							
							continue;
						} else if (nosql.getConditionType().equalsIgnoreCase("sortDesc") 
								|| nosql.getConditionType().equalsIgnoreCase("sortAsc")) {
							dbo = (BasicDBObject) query.getQueryObject();
							
							BasicDBObject dbo2 = (BasicDBObject) query.getSortObject();
							if(dbo2 == null) {
								dbo2 = new BasicDBObject();
							}
							
							dbo2.append(nosql.getConditionKey().toString(), nosql.getConditionType().contains("sortDesc")?-1:1 );							
							queryTmp = new BasicQuery(dbo);
							queryTmp.setSortObject(dbo2);
							query = queryTmp;
							continue;
						} else if (nosql.getConditionType().equalsIgnoreCase("limit")) {
							if ( processContent.getMongodbContent().getNosqlType().compareTo("selectList") == 0 ) {
								limit = Integer.valueOf(paramList.get(paramIndex).toString());
							}
							//else {
							//	throw new CommonApiException(null, logStep, 
							//			commonApiProp.getProperty("comAPI.error.internalServerError.code"), 
							//			CommonUtil.getMessage("comAPI.error.internalServerError.msg.resultModule"));
							//}
							continue;
						} else if (nosql.getConditionType().equalsIgnoreCase("offset")) {
							if ( processContent.getMongodbContent().getNosqlType().compareTo("selectList") == 0 ) {
								offset = Integer.valueOf(paramList.get(paramIndex).toString());
							}
							//else {
							//	throw new CommonApiException(null, logStep, 
							//			commonApiProp.getProperty("comAPI.error.internalServerError.code"), 
							//			CommonUtil.getMessage("comAPI.error.internalServerError.msg.resultModule"));
							//}
							continue;
						} else if (nosql.getConditionType().equalsIgnoreCase("update")) {
							if ( processContent.getMongodbContent().getNosqlType().compareTo("update") == 0 ) {
								update.set(nosql.getConditionKey().toString(), paramList.get(paramIndex));
							}
							//else {
							//	throw new CommonApiException(null, logStep, 
							//			commonApiProp.getProperty("comAPI.error.internalServerError.code"), 
							//			CommonUtil.getMessage("comAPI.error.internalServerError.msg.resultModule"));
							//}
							continue;
						} else if (nosql.getConditionType().equalsIgnoreCase("insert")) {
							if ( processContent.getMongodbContent().getNosqlType().compareTo("insert") == 0 ) {
								insertMap.putAll(insertKeyParse(nosql.getConditionKey().toString(), insertMap, paramList.get(paramIndex)));
							}
							//else {
							//	throw new CommonApiException(null, logStep, 
							//			commonApiProp.getProperty("comAPI.error.internalServerError.code"), 
							//			CommonUtil.getMessage("comAPI.error.internalServerError.msg.resultModule"));
							//}
							continue;						
						} else  {
							throw new CommonApiException(null, logStep, 
									ERROR_INTERNALSERVERERROR_CODE, 
									CommonApiUtil.getMessage("comAPI.error.internalServerError.msg.resultModule"));
						}
					} catch (NullPointerException e) {
						logger.debug("NULL: " + e.getMessage());
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			
			logStep = "[MongoDB 쿼리 처리 - 조회 필드 처리]";
			if ( processContent.getMongodbContent().getNosqlType().compareTo("selectOne") == 0 
					|| processContent.getMongodbContent().getNosqlType().compareTo("selectList") == 0
					) {
				
				if ( processContent.getMongodbContent().getIncludeFields() != null ) {
					for (String field : processContent.getMongodbContent().getIncludeFields()) {
						query.fields().include(field);
					}
				}
			}
			
			logStep = "[MongoDB 쿼리 처리 - 실행]";
			if ( processContent.getMongodbContent().getNosqlType().compareTo("count") == 0 ) {	            
				resultObject = commonMongoDAO.count(query);
			}
			else if ( processContent.getMongodbContent().getNosqlType().compareTo("selectOne") == 0 ) {
				sqlResultMap = (HashMap<String, Object>) commonMongoDAO.findOne(query);
			}
			else if ( processContent.getMongodbContent().getNosqlType().compareTo("selectList") == 0 ) {
				sqlResultMapList = (List<Map<String, Object>>) commonMongoDAO.find(query.limit(limit).skip(offset));	
			}
			else if ( processContent.getMongodbContent().getNosqlType().compareTo("insert") == 0 ) {
				commonMongoDAO.insert(insertMap);
				resultObject = 1;
			}
			else if ( processContent.getMongodbContent().getNosqlType().compareTo("update") == 0 ) {
				resultObject =commonMongoDAO.upsert(query, update);
			}
			else if ( processContent.getMongodbContent().getNosqlType().compareTo("delete") == 0 ) {
				resultObject = commonMongoDAO.remove(query);
			}
			else {
				throw new CommonApiException(null, logStep, 
						ERROR_INTERNALSERVERERROR_CODE, 
						CommonApiUtil.getMessage("comAPI.error.internalServerError.msg.resultModule"));
			}
			
			logStep = "[MongoDB 쿼리 처리 - 결과 항목 매핑]";
			if ( processContent.getMongodbContent().getNosqlType().compareTo("selectOne") == 0 ) {

				if ( processContent.getResultList() != null ) {
					for ( String key : processContent.getResultList() ) {												
						if ( key.contains(".")) {
							resultMap.putAll(resultKeyParse(key, resultMap, sqlResultMap));
						}
						else if ( sqlResultMap.containsKey(key) ) {
							resultMap.put(key, sqlResultMap.get(key));
						}
					}
					
					resultValidationProcess(processContent, resultMap);
				}
				else {
					resultMap = sqlResultMap;
					resultValidationProcess(processContent, resultMap);
				}
				
				if ( processContent.getMongodbContent().getExcludeFields() != null ) {
					for ( String field : processContent.getMongodbContent().getExcludeFields() ) {
						excludeField(field, resultMap);
					}
				}
				
				resultObject = resultMap;
			}
			else if ( processContent.getMongodbContent().getNosqlType().compareTo("selectList") == 0 ) {
				if ( processContent.getResultList() != null ) {
					for (Map<String, Object> tmpMap : sqlResultMapList) {	
						HashMap<String, Object> tmpResultMap = new HashMap<String, Object>();
						for ( String key : processContent.getResultList() ) {
							
							if ( key.contains(".")) {
								tmpResultMap.putAll(resultKeyParse(key, tmpResultMap, (HashMap<String, Object>) tmpMap));
							}
							else if ( tmpMap.containsKey(key) ) {
								tmpResultMap.put(key, tmpMap.get(key));
							}
						}
						resultValidationProcess(processContent, tmpResultMap);
						
						if ( processContent.getMongodbContent().getExcludeFields() != null ) {
							for ( String field : processContent.getMongodbContent().getExcludeFields() ) {
								excludeField(field, tmpResultMap);
							}
						}
						
						resultMapList.add(tmpResultMap);
					}
					resultObject = resultMapList;
				}
				else {
					for (Map<String, Object> tmpMap : sqlResultMapList) {
						HashMap<String, Object> tmpResultMap = new HashMap<String, Object>();
						tmpResultMap = (HashMap<String, Object>) tmpMap;
						resultValidationProcess(processContent, tmpResultMap);
						
						if ( processContent.getMongodbContent().getExcludeFields() != null ) {
							for ( String field : processContent.getMongodbContent().getExcludeFields() ) {
								excludeField(field, tmpResultMap);
							}
						}	
						resultMapList.add(tmpResultMap);
					}
					
					resultObject = resultMapList;
				}
			}
			else {				
				resultMap.put("compareKey", resultObject);
			}
			
			logStep = "[MongoDB 쿼리 처리 - 결과 항목 검증]";
			resultValidationProcess(processContent, resultMap);

			return resultObject;
		}
		catch (CommonApiException e) {
			throw e;
		} 
		catch (Exception e) {
			throw new CommonApiException(e, logStep, e.getLocalizedMessage());
		}
	}
}
