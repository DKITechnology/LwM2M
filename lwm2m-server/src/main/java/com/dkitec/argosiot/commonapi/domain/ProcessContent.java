package com.dkitec.argosiot.commonapi.domain;

import java.util.List;

/**
 * <b>클래스 설명</b>  : API 비지니스 로직 처리 도메인
 * @author : DKI
 */
public class ProcessContent {
		
	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getRequiredConditionKey() {
		return requiredConditionKey;
	}

	public void setRequiredConditionKey(String requiredConditionKey) {
		this.requiredConditionKey = requiredConditionKey;
	}

	public String getRequiredConditionValue() {
		return requiredConditionValue;
	}

	public void setRequiredConditionValue(String requiredConditionValue) {
		this.requiredConditionValue = requiredConditionValue;
	}

	public RdbContent getRdbContent() {
		return rdbContent;
	}

	public void setRdbContent(RdbContent rdbContent) {
		this.rdbContent = rdbContent;
	}

	public MongodbContent getMongodbContent() {
		return mongodbContent;
	}

	public void setMongodbContent(MongodbContent mongodbContent) {
		this.mongodbContent = mongodbContent;
	}

	public MergeContent getMergeContent() {
		return mergeContent;
	}

	public void setMergeContent(MergeContent mergeContent) {
		this.mergeContent = mergeContent;
	}

	public MethodContent getMethodContent() {
		return methodContent;
	}

	public void setMethodContent(MethodContent methodContent) {
		this.methodContent = methodContent;
	}

	public List<String> getParamList() {
		return paramList;
	}

	public void setParamList(List<String> paramList) {
		this.paramList = paramList;
	}

	public List<String> getParamTypeList() {
		return paramTypeList;
	}

	public void setParamTypeList(List<String> paramTypeList) {
		this.paramTypeList = paramTypeList;
	}

	public List<String> getResultList() {
		return resultList;
	}

	public void setResultList(List<String> resultList) {
		this.resultList = resultList;
	}

	public List<String> getResultTypeList() {
		return resultTypeList;
	}

	public void setResultTypeList(List<String> resultTypeList) {
		this.resultTypeList = resultTypeList;
	}

	public String getValidationYn() {
		return validationYn;
	}

	public void setValidationYn(String validationYn) {
		this.validationYn = validationYn;
	}

	public String getSuccessCondition() {
		return successCondition;
	}

	public void setSuccessCondition(String successCondition) {
		this.successCondition = successCondition;
	}

	public String getCompareKey() {
		return compareKey;
	}

	public void setCompareKey(String compareKey) {
		this.compareKey = compareKey;
	}

	public String getSuccessValue() {
		return successValue;
	}

	public void setSuccessValue(String successValue) {
		this.successValue = successValue;
	}

	/**
	 * 처리 순서 
	 */
	private String orderNo;
	
	/**
	 * 처리 명
	 */
	private String processName;
	
	/**
	 * 처리 유형 (rdb, mongodb, method, merge) 
	 */
	private String processType;
	
	/** 
	 * 처리 필요키, 입력된 값이  requestContent에 없는 경우 처리 스킵
	 */
	private String requiredConditionKey;
	
	/**
	 * 처리 필요값, 입력된 값이 처리 필요키의 값과 다른 경우 처리 스킵
	 */
	private String requiredConditionValue;
	
	private RdbContent rdbContent;
	private MongodbContent mongodbContent;
	private MergeContent mergeContent;	
	private MethodContent methodContent;
	private List<String> paramList;
	private List<String> paramTypeList;
	private List<String> resultList;
	private List<String> resultTypeList;
	private String validationYn;
	private String successCondition;
	private String compareKey;
	private String successValue;
	
	public ProcessContent() {}
}
