package com.dkitec.lwm2m.rdao.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.leshan.server.registration.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.domain.message.MessageInfoVO;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

public class CommonRedisPoolDAO<E> {
	
	static Logger logger = LoggerFactory.getLogger(CommonRedisPoolDAO.class);
	
    protected static JedisPool jedisPool;
	
    CommonRedisPoolDAO(JedisPool jedisPool){
    	this.jedisPool = jedisPool;
    }
    
	public String test(){
		return jedisPool.getResource().ping();
	}
	
	public Jedis getResource() throws JedisException {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
           // jedis.select(Integer.parseInt(DB_INDEX));
        } catch (JedisException e) {
        	LoggerPrint.printErrorLogExceptionrMsg(logger, e);
        	if (jedis!=null)
        		jedis.close();
        	throw  e;
        }
        return jedis;
    }
	
	public String getValue(String key) {
        String value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.get(key);
                value = CommonUtil.isEmpty(value) && !"nil".equalsIgnoreCase(value) ? value : null;
                logger.debug("get {} = {}", key, value);
            }
        } catch (Exception e) {
        	LoggerPrint.printErrorLogExceptionrMsg(logger, e);
        } finally {
            //returnResource(jedis);
        }
        return value;
    }
	
	public String setValue(String key, String value, int cacheSeconds) {
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.set(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
            logger.debug("set {} = {}", key, value);
        } catch (Exception e) {
        	LoggerPrint.printErrorLogExceptionrMsg(logger, e);
            //returnResource(jedis);
        }
        return result;
    }
	
	public String setObjectValue(String key, Object value){
		return this.setObjectValue(key, value, 0);
	}
	
	public String setObjectValue(String key, Object value, int cacheSeconds){
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.set(key, new Gson().toJson(value));
            if (cacheSeconds!=0){
                jedis.expire(key,cacheSeconds);
            }
            logger.debug("setObject {}={}",key,value);
        } catch (Exception e) {
        	LoggerPrint.printErrorLogExceptionrMsg(logger, e);
        } finally {
            jedis.close();
        }
        return result;
    }
	
	public Object getObjectValue(String key){
        Object value = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            String result = jedis.get(key);
            value =  new Gson().fromJson(result, Object.class);
            logger.debug("getObject {}={}",key,value);
        } catch (Exception e) {
            logger.warn("getObject {}：{}",key,e.getMessage());
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return value;
    }
	
	public String getStringValue(String key){
        String result = null;
        Jedis jedis = null;
        try {
            jedis = getResource();
            result = jedis.get(key);
        } catch (Exception e) {
            logger.warn("getObject {}：{}",key,e.getMessage());
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return result;
    }
	
	public List<String> getStringValues(String keys){
		Jedis jedis = null;
		List<String> result = null;
		try {
			jedis  = getResource();
			result = jedis.mget(keys);
		}  catch (Exception e) {
            
        } finally {
            jedis.close();
        }		
		return result;
	}
}
