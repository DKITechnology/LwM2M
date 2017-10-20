package com.dkitec.argosiot.commonapi.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomJdbcDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public HashMap<String, Object> queryForMap(String query) {
		return (HashMap<String, Object>) jdbcTemplate.queryForMap(query);
	}
	
	public HashMap<String, Object> queryForMap(String query, Object[] paramList) {
		return (HashMap<String, Object>) jdbcTemplate.queryForMap(query, paramList);
	}
	
	public List<Map<String, Object>> queryForList(String query) {
		return jdbcTemplate.queryForList(query);
	}
	
	public List<Map<String, Object>> queryForList(String query, Object[] paramList) {
		return jdbcTemplate.queryForList(query, paramList);
	}
	
	public Object update(String query) {
		return jdbcTemplate.update(query);
	}
	
	public Object update(String query, Object[] paramList) {
		return jdbcTemplate.update(query, paramList);
	}
	
}
