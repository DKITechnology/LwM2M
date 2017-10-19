package com.dkitec.lwm2m.dao.test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.SortParameters.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.query.SortQueryBuilder;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

public abstract class DefaultRedisDaoImpl<E> implements DefaultRedisDao<E> {

	@Autowired
	protected RedisTemplate<String, String> redisTemplate;
	
	protected String REDIS_KEY;
	
	private Class<E> entityClass;

	public DefaultRedisDaoImpl() {
		System.out.println("genericSuperclass : " + getClass().getGenericSuperclass());
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		Type type = genericSuperclass.getActualTypeArguments()[0];
		if (type instanceof ParameterizedType) {
			this.entityClass = (Class) ((ParameterizedType) type).getRawType();
		} else {
			this.entityClass = (Class) type;
		}
	}
		
	@Override
	public abstract void putData(E obj);

	@Override
	public void deleteData(String key) {
		redisTemplate.opsForHash().delete(REDIS_KEY, key);
	}

	@Override
	public List<E> getObjectList() {
		return getObjectList(0, 10);
	}

	@Override
	public List<E> getObjectList(long offset, long count) {
		Gson gson = new Gson();
		List<E> objs = new ArrayList<E>();		
		SortQueryBuilder<String> builder = SortQueryBuilder.sort("tId");
		builder.alphabetical(true);
		builder.order(Order.DESC);
		builder.limit(offset, count);
		List<String> keys = redisTemplate.sort(builder.build());		
		List<String> results = redisTemplate.opsForValue().multiGet(keys);		
		for (String item : results) {
			objs.add(gson.fromJson(item, entityClass));
		}
		return objs;
	}

	@Override
	public E getData(String key) {
		String result = redisTemplate.opsForValue().get(key);
		return new Gson().fromJson(result, entityClass);
	}
}
