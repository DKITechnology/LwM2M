package com.dkitec.argosiot.commonapi.domain;

import java.util.List;

/**
 * <b>클래스 설명</b>  : MongoDB + Rdb 결과 병합 도메인
 * @author : DKI
 */
public class MergeContent {
		
	/**
	 * 참조할 Rdb 처리 순서 번호(1~) 
	 */
	private String rdbOrderNo;
	
	public String getRdbOrderNo() {
		return rdbOrderNo;
	}

	public void setRdbOrderNo(String rdbOrderNo) {
		this.rdbOrderNo = rdbOrderNo;
	}

	public String getMongodbOrderNo() {
		return mongodbOrderNo;
	}

	public void setMongodbOrderNo(String mongodbOrderNo) {
		this.mongodbOrderNo = mongodbOrderNo;
	}

	public ReferMapInfo getReferMapInfo() {
		return referMapInfo;
	}

	public void setReferMapInfo(ReferMapInfo referMapInfo) {
		this.referMapInfo = referMapInfo;
	}

	public IncludeMapInfo getIncludeMapInfo() {
		return includeMapInfo;
	}

	public void setIncludeMapInfo(IncludeMapInfo includeMapInfo) {
		this.includeMapInfo = includeMapInfo;
	}

	public List<String> getExcludeMongodbFields() {
		return excludeMongodbFields;
	}

	public void setExcludeMongodbFields(List<String> excludeMongodbFields) {
		this.excludeMongodbFields = excludeMongodbFields;
	}

	/**
	 * 참조할 MongoDB 처리 순서 번호(1~)
	 */
	private String mongodbOrderNo;
	
	/**
	 * 병합처리 참조를 위한 MongoDB-Rdb간 키 항목 1:1 매핑 객체
	 */
	private ReferMapInfo referMapInfo;
	
	/**
	 * 결과로 출력될 MongoDB-Rdb간 키 항목 1:1 매핑 객체 ReferMapInfo와 1:1 매핑됨
	 */
	private IncludeMapInfo includeMapInfo; 
	
	/**
	 * 결과 출력시 제외될 MongoDB 키 리스트 
	 */
	private List<String> excludeMongodbFields;

	public MergeContent() {}
	
	/**
	 * <b>클래스 설명</b>  : 병합처리 참조를 위한 MongoDB-Rdb간 키 항목 1:1 매핑 객체
	 * @author : DKI
	 */
	public class ReferMapInfo {
		/**
		 * MongoDB 키 리스트
		 */
		private List<String> mongoFields;
		
		/**
		 * Rdb 키 리스트 
		 */
		private List<String> rdbFields;

		public List<String> getMongoFields() {
			return mongoFields;
		}

		public void setMongoFields(List<String> mongoFields) {
			this.mongoFields = mongoFields;
		}

		public List<String> getRdbFields() {
			return rdbFields;
		}

		public void setRdbFields(List<String> rdbFields) {
			this.rdbFields = rdbFields;
		}
	}
	
	/**
	 * <b>클래스 설명</b>  : 결과로 출력될 MongoDB-Rdb간 키 항목 1:1 매핑 객체 ReferMapInfo와 1:1 매핑됨
	 * @author : DKI
	 */
	public class IncludeMapInfo {
		/**
		 * MongoDB 키 리스트
		 */
		private List<List<String>> mongoFields;
		
		/**
		 * Rdb 키 리스트 
		 */
		private List<List<String>> rdbFields;

		public List<List<String>> getMongoFields() {
			return mongoFields;
		}

		public void setMongoFields(List<List<String>> mongoFields) {
			this.mongoFields = mongoFields;
		}

		public List<List<String>> getRdbFields() {
			return rdbFields;
		}

		public void setRdbFields(List<List<String>> rdbFields) {
			this.rdbFields = rdbFields;
		}
	}
}
