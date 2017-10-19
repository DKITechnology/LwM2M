package com.dkitec.lwm2m.rdao.common;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;

import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.domain.message.MessageInfoVO;
import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

public class MessageRedisDao {
	
	static Logger logger = LoggerFactory.getLogger(MessageRedisDao.class);
	
	protected static JedisPool jedisPool;
	
	CommonRedisPoolDAO commRedisDao;
	
	public MessageRedisDao(CommonRedisPoolDAO commRedisDao) {
		this.commRedisDao = commRedisDao;
	}

	public String putMessagePacket(MessageInfoVO messageInfo, int cacheSeconds) {
		final String time = String.valueOf(new Date().getTime());
		final String tId = messageInfo.getTid();
		final String redisType = "messageInfo";

		final String value = new Gson().toJson(messageInfo);
		String key = time + "." + tId + "_" + redisType + "_" + new Random().nextInt(100);
		
		String result = null;
        try {
        	result = commRedisDao.setValue(key, value, cacheSeconds);
        } catch (Exception e) {
        	LoggerPrint.printErrorLogExceptionrMsg(logger, e);
        }
        return result;		
	}
	
}
