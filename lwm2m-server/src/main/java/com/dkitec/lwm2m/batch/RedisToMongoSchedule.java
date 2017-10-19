package com.dkitec.lwm2m.batch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.dao.MessageRedisMongoDao;
import com.dkitec.lwm2m.domain.message.MessageInfoVO;

/**
 * RedisToMongoSchedule
 * Redis DB 데이터를 Mongo DB 데이터로 이전하는 스케줄링 
 */
public class RedisToMongoSchedule {
	
	Logger logger = LoggerFactory.getLogger(RedisToMongoSchedule.class);
	
	@Autowired
	MessageRedisMongoDao msgDao;
	
	@Scheduled(fixedRate=60000)
	public void mongoToRedisMove(){
		logger.debug("##Start RedisToMongo 1MIN");
		
		try {
			Iterator<MessageInfoVO> result = msgDao.getMessageInfo("MSG:*");
			List<MessageInfoVO> resultList = new ArrayList<MessageInfoVO>();
			List<String> resultKeys = new ArrayList<String>();
			while (result.hasNext()){
				MessageInfoVO msg = result.next();
				resultKeys.add(msg.getRedisKey());
				resultList.add(msg);
			}				
			
			if(resultList != null && resultList.size() > 0){
				//insert mongo
				msgDao.multiInsertToMongo(resultList);
				//Delte redis keys
				String[] keys = new String[resultKeys.size()];
				keys = resultKeys.toArray(keys);
				if(keys != null)
					msgDao.multiDeleteToRedis(keys);
			}
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
	}
}
