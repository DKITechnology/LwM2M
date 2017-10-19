package com.dkitec.argosiot.commonapi.domain;

/**
 * <b>클래스 설명</b>  : Rdb 처리 도메인 
 * @author : DKI
 */
public class RdbContent {
		
	private String sqlType;	
	private String sql;

	public RdbContent() {}

	public String getSqlType() {
		return sqlType;
	}

	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}
}
