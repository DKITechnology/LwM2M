package com.dkitec.argosiot.commonapi.domain;

import java.util.List;

/**
 * <b>클래스 설명</b>  : MongoDB 실행 프로세스 도메인
 * @author : DKI
 */
public class MongodbContent {
		
	/**
	 * NoSQL 처리 유형(selectOne, selectList, update, insert, delete, count)
	 */
	private String nosqlType;	
	
	/**
	 * 참조할 MongoDB 콜렉션 명
	 */
	private String collectionName;
	
	/**
	 * NoSQL 처리를 위한 조건 및 조건 키 정보 객체 리스트
	 */
	private List<Nosql> nosql;
	
	/**
	 * 조회 결과로 출력될 키 리스트 (처리유형이 selectOne, selectList 만 참조)
	 */
	private List<String> includeFields;
	
	/**
	 * 조회 결과에 제외될 키 리스트 (처리유형이 selectOne, selectList 만 참조)
	 */
	private List<String> excludeFields;
	
	public String getNosqlType() {
		return nosqlType;
	}

	public void setNosqlType(String nosqlType) {
		this.nosqlType = nosqlType;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}

	public List<Nosql> getNosql() {
		return nosql;
	}

	public void setNosql(List<Nosql> nosql) {
		this.nosql = nosql;
	}

	public List<String> getIncludeFields() {
		return includeFields;
	}

	public void setIncludeFields(List<String> includeFields) {
		this.includeFields = includeFields;
	}

	public List<String> getExcludeFields() {
		return excludeFields;
	}

	public void setExcludeFields(List<String> excludeFields) {
		this.excludeFields = excludeFields;
	}

	/**
	 * <b>클래스 설명</b>  : NoSQL 처리를 위한 조건 및 조건 키 정보 객체
	 * @author : DKI
	 */
	public class Nosql {
		
		/**
		 * 조건 부호 (regex, is, ne, in, gt, gte, lt, lte, insert, update, limit, offset, sortDesc, sortAsc)
		 */
		String conditionType;
		
		/**
		 * 조건 키 
		 */
		Object conditionKey;
		
		public Nosql() {}

		public String getConditionType() {
			return conditionType;
		}

		public void setConditionType(String conditionType) {
			this.conditionType = conditionType;
		}

		public Object getConditionKey() {
			return conditionKey;
		}

		public void setConditionKey(Object conditionKey) {
			this.conditionKey = conditionKey;
		}
	}

	public MongodbContent() {}
}
