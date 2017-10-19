package com.dkitec.lwm2m.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.eclipse.leshan.core.model.ResourceModel.Type;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dkitec.lwm2m.domain.DeviceObjectInfo;
import com.dkitec.lwm2m.domain.Lwm2mObjectInfo;
import com.dkitec.lwm2m.domain.Lwm2mRsourceInfo;
import com.dkitec.lwm2m.domain.ObserveInfoVO;
import com.dkitec.lwm2m.domain.RequestResultVO;
import com.dkitec.lwm2m.server.DefaultLwm2mServer;
import com.dkitec.lwm2m.service.intf.DeviceInfoService;
import com.dkitec.lwm2m.service.intf.Lwm2mRequestService;
import com.google.gson.Gson;

/**
 * 단말 정보 조회 테스트 ( 목록 및 object 조회 )
 * @author eunJ
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations = { "/spring/root-context.xml", "/spring/appServlet/servlet-context.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DeviceRequestTest {

	@Resource(name="leshanServer")
	DefaultLwm2mServer leshanServer;
	
	@Autowired
	DeviceInfoService deviceInfoService;
	
	@Autowired
	Lwm2mRequestService lwmRequest;
	
	static String endpoint = "123456789";
	
	static String format = "JSON";
	
	@Test
	public void a_severStartTest(){
		System.out.println("severTest");
		LeshanServer server = leshanServer.getLwm2mServer();
		System.out.println(server.getSecureAddress());
		
		int i = 0;
		while ( i < 5) {
			DeviceObjectInfo device = deviceInfoService.selectDeviceObjects(endpoint);
			i++;
			if(device == null || device.getDevcId() == null){
				try {
					System.out.println("device wait..............");
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				i = 5;
			}
		}	
	}
	
	@Test
	public void b_createObject(){
		int objectId = 1;
		Lwm2mObjectInfo lwm2mObjInstance = new Lwm2mObjectInfo();
		lwm2mObjInstance.setInstanceId("1");
		List<Lwm2mRsourceInfo> resouceInofs = new ArrayList<Lwm2mRsourceInfo>();
		Lwm2mRsourceInfo rs = new Lwm2mRsourceInfo();
		rs.setRscId(1);
		rs.setRscType(Type.INTEGER);
		rs.setRscValue(new Long(1));
		resouceInofs.add(rs);
		lwm2mObjInstance.setResources(resouceInofs);
		RequestResultVO<Lwm2mObjectInfo> result = lwmRequest.requestCreate(format, endpoint, objectId, lwm2mObjInstance);
		assertNotNull("[createObject]", result);
	}
	
	@Test
	public void c_objectObserve(){
		int objectId = 3;
		int objectInstanceId = 0;
		ObserveInfoVO observeInfo = new ObserveInfoVO();
		observeInfo.setSubURL("http://127.0.0.1:8080");
		RequestResultVO<ObserveInfoVO> result  = lwmRequest.requestObserve(format, endpoint, objectId, objectInstanceId, observeInfo);
		assertNotNull("[objectObserve]", result);
	}
	
	@Test
	public void d_objectObserveCancel(){
		int objectId = 3;
		int objectInstanceId = 0;
		ObserveInfoVO observeInfo = new ObserveInfoVO();
		RequestResultVO<ObserveInfoVO> result  = lwmRequest.cancelObserve(format, endpoint, objectId, objectInstanceId, observeInfo);
		assertNotNull("[objectObserveCancel]", result);
	}
	
	@Test
	public void e_objectDelete(){
		RequestResultVO<String> result = lwmRequest.requestDelete(endpoint, 3303, 0);
		assertEquals("DELETED", result.getCoapResultCd());
	}
	
	@Test
	public void f_objectRead(){
		RequestResultVO<String> result = lwmRequest.requestRead(format, endpoint, 3, 0);
		assertEquals("CONTENT", result.getCoapResultCd());
	}
	
	@Test
	public void g_objectWrite(){
		
		Lwm2mObjectInfo object = new Lwm2mObjectInfo();
		
		List<Lwm2mRsourceInfo> resouceInofs = new ArrayList<Lwm2mRsourceInfo>();
		Lwm2mRsourceInfo rs = new Lwm2mRsourceInfo();
		rs.setRscId(14);
		rs.setRscType(Type.STRING);
		rs.setRscValue("111");
		resouceInofs.add(rs);
		
		object.setResources(resouceInofs);
		RequestResultVO<String> result = lwmRequest.requestWrite(format, endpoint, 3, 0, object);
		
		assertEquals("CHANGED", result.getCoapResultCd());
	}
	
	@Test
	public void h_resourceObserve(){
		int objectId = 3;
		int objectInstanceId = 0;
		ObserveInfoVO observeInfo = new ObserveInfoVO();
		observeInfo.setSubURL("http://127.0.0.1:8080");
		RequestResultVO<ObserveInfoVO> result  = lwmRequest.requestObserve(format, endpoint, objectId, objectInstanceId, 1, observeInfo);
		
		assertEquals("CONTENT", result.getCoapResultCd());
	}
	
	@Test
	public void i_resouceObserverCancel(){
		int objectId = 3;
		int objectInstanceId = 0;
		ObserveInfoVO observeInfo = new ObserveInfoVO();
		RequestResultVO<ObserveInfoVO> result  = lwmRequest.cancelObserve(format, endpoint, objectId, objectInstanceId, 1, observeInfo);		
		//assertEquals("CONTENT", result.getCoapResultCd());
	}
	
	@Test
	public void j_resouceRead(){
		RequestResultVO<String> result = lwmRequest.requestRead(format, endpoint, 3, 0, 1);
		assertNotNull("[resouce Read]", result);		
		assertEquals("CONTENT", result.getCoapResultCd());
	}
	
	@Test
	public void k_resouceWrite(){
		Lwm2mObjectInfo object = new Lwm2mObjectInfo();		
		Lwm2mRsourceInfo rs = new Lwm2mRsourceInfo();
		rs.setRscId(14);
		rs.setRscType(Type.STRING);
		rs.setRscValue("111");
		RequestResultVO<String> result = lwmRequest.requestWrite(format, endpoint, 3, 0, rs);
		assertEquals("CHANGED", result.getCoapResultCd());
	}
	
	@Test
	public void l_resourceExcute(){
		RequestResultVO<String> result = lwmRequest.requestExecute(endpoint, 3, 0, 13, null);
		assertNotNull("[resource Excute]", result);
	}
}
