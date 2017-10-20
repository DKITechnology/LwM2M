package com.dkitec.lwm2m.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 단말 정보 조회 테스트 ( 목록 및 object 조회 )
 * @author eunJ
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration( locations = { "/spring/root-context.xml", "/spring/appServlet/servlet-context.xml" })
public class ServerConfigTest {
	
	@Value("#{serverConfigProp['lwm2m.server.id']}")
	private String serverId;
	
	@Value("#{serverConfigProp['lwm2m.default.format']}")
	private String defaultFormat;
	
	@Value("#{serverConfigProp['lwm2m.default.timeout']}")
	private int requestTimeout;
	
	@Value("#{serverConfigProp['fwUpdate.read.limtCnt']}")
	private int limtCnt;
	
	@Value("#{serverConfigProp['fwUpdate.read.sleepTime']}")
	private int sleepTime;
	
	@Value("#{serverConfigProp['message.diff.time']}")
	private int messageLimitTime;
	
	
	@Test
	public void severStartTest(){
		System.out.println("########################### serverId "+ serverId);
		System.out.println("########################### defaultFormat "+ defaultFormat);
		System.out.println("########################### requestTimeout "+ requestTimeout);
		System.out.println("########################### limtCnt "+ limtCnt);
		System.out.println("########################### sleepTime "+ sleepTime);		
		System.out.println("########################### limtCnt "+ messageLimitTime);	
		assertNotNull(serverId);
		assertNotNull(defaultFormat);
		assertNotNull(requestTimeout);
		assertNotNull(limtCnt);
		assertNotNull(sleepTime);
		assertNotNull(messageLimitTime);
	}
}
