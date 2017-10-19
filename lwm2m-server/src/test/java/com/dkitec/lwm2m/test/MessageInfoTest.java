package com.dkitec.lwm2m.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dkitec.lwm2m.common.util.ApplicationContextProvider;
import com.dkitec.lwm2m.dao.MessageRedisMongoDao;
import com.dkitec.lwm2m.domain.message.MessageInfoVO;
import com.dkitec.lwm2m.rdao.common.CommonRedisPoolDAO;
import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations = { "/spring/root-context.xml", "/spring/appServlet/servlet-context.xml" })
public class MessageInfoTest {
	
	@Test
	public void getRedisMessage(){
		ApplicationContext context = ApplicationContextProvider.getApplicationContext();
		
		MessageRedisMongoDao msgDao = context.getBean(MessageRedisMongoDao.class);
		Iterator<MessageInfoVO> result = msgDao.getMessageInfo("MSG:*");
		List<MessageInfoVO> resultList = new ArrayList<MessageInfoVO>();
		while (result.hasNext())
			resultList.add(result.next());
		
		String data = new Gson().toJson(resultList);
		System.out.println("############## get Redis list : "+ data);
		System.out.println("############## get Redis size : "+ resultList.size());
		
	}
	
}
