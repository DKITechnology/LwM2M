package com.dkitec.lwm2m.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dkitec.lwm2m.rdao.common.CommonRedisPoolDAO;

@Service
public class CommonRedisTestService {

	@Autowired
	CommonRedisPoolDAO redisPoolDao;
	
	public void testService(){
		System.out.println("########## value : "+redisPoolDao.getObjectValue("test"));
	}
}
