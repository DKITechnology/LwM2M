package com.dkitec.lwm2m.dao.test;

import org.springframework.stereotype.Repository;

import com.dkitec.lwm2m.domain.message.MessageInfoVO;
import com.google.gson.Gson;

public class MessageInfoDao extends DefaultRedisDaoImpl<MessageInfoVO> {

	private final static String redisKey = "MESSAGE";
	
	public MessageInfoDao() {
		this.REDIS_KEY = redisKey;
	}	

	@Override
	public void putData(MessageInfoVO obj) {
		String time = String.valueOf(System.currentTimeMillis());
		String key = time +"."+ obj.getTid() + "_" + obj.getMessageType();
		System.out.println("REDS KEY : " + key);
		redisTemplate.opsForValue().set(key, new Gson().toJson(obj));
	}	

}
