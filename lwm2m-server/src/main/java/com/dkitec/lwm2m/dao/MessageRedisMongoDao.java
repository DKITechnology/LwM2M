package com.dkitec.lwm2m.dao;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import com.dkitec.lwm2m.common.code.ComCode;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.domain.message.MessageInfoVO;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

@Repository
public class MessageRedisMongoDao {
	
	Logger logger = LoggerFactory.getLogger(MessageRedisMongoDao.class);

	@Autowired
	private JedisPool jedisPool;
	
	@Value("#{serverConfigProp['message.diff.time']}")
	private long diffTime;
	
	@Autowired
	protected MongoTemplate mongoTemplate;
	
	private String collectionName = "messageInfo";
	
	/**
	 * REDIS SCAN MESSAGE DATA
	 * @param prefix
	 * @return
	 */
	public Iterator<MessageInfoVO> getMessageInfo(String prefix) {
        return new RedisIterator(jedisPool, new ScanParams().match(prefix + "*"));
    }
	
	protected class RedisIterator implements Iterator<MessageInfoVO> {

        private Pool<Jedis> pool;
        private ScanParams scanParams;

        private String cursor;
        private List<MessageInfoVO> scanResult;

        public RedisIterator(Pool<Jedis> p, ScanParams scanParams) {
            pool = p;
            this.scanParams = scanParams;
            // init scan result
            scanNext("0");
        }

        private void scanNext(String cursor) {
            try (Jedis j = pool.getResource()) {
                ScanResult<byte[]> sr = j.scan(cursor.getBytes(), scanParams);

                this.scanResult = new ArrayList<>();
                if (sr.getResult() != null && !sr.getResult().isEmpty()) {                	
                    for (byte[] value : j.mget(sr.getResult().toArray(new byte[][] {}))) {
                    	MessageInfoVO messageInfo = null;
						try {
							String message = new String(value, "UTF-8");
							messageInfo = new Gson().fromJson(message, MessageInfoVO.class);
						} catch (JsonSyntaxException e1) {
							LoggerPrint.printErrorLogExceptionrMsg(logger, e1, "Redis Sting to MessageInfo");
						} catch (UnsupportedEncodingException e) {
							LoggerPrint.printErrorLogExceptionrMsg(logger, e, "Redis Sting to MessageInfo");
						}
                    	if(messageInfo != null){
                    		try {
								SimpleDateFormat dateFormat = new SimpleDateFormat(ComCode.DataFormat.DateStrFormat.getValue(), Locale.getDefault());                    		
								Date date = new Date();
								Date msgDate = dateFormat.parse(messageInfo.getCreDatm());                            
								long timeDf = date.getTime() - msgDate.getTime();
								if(timeDf  > diffTime){
									this.scanResult.add(messageInfo);
								}
							} catch (ParseException e) {
								LoggerPrint.printErrorLogExceptionrMsg(logger, e);
							}
                    	}
                    }
                }

                this.cursor = sr.getStringCursor();
            }
        }

        @Override
        public boolean hasNext() {
            if (!scanResult.isEmpty()) {
                return true;
            }
            if ("0".equals(cursor)) {
                // no more elements to scan
                return false;
            }

            // read more elements
            scanNext(cursor);
            return !scanResult.isEmpty();
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public MessageInfoVO next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return scanResult.remove(0);
        }
    }
	
	public void multiInsertToMongo(List<MessageInfoVO> messageList) throws Exception {
		mongoTemplate.insert(messageList, collectionName);
	}
	
	public void multiDeleteToRedis(String[] keys) throws Exception{
		Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(keys);
        } catch (JedisException e) {
        	LoggerPrint.printErrorLogExceptionrMsg(logger, e);
        	if (jedis!=null)
        		jedis.close();
        	throw  e;
        }
	}
}
