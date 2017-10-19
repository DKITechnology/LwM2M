package com.dkitec.lwm2m.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dkitec.lwm2m.domain.DeviceInfoVO;
import com.dkitec.lwm2m.domain.DeviceObjectInfo;
import com.dkitec.lwm2m.server.DefaultLwm2mServer;
import com.dkitec.lwm2m.service.intf.DeviceInfoService;
import com.google.gson.Gson;

/**
 * 단말 정보 조회 테스트 ( 목록 및 object 조회 )
 * @author eunJ
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations = { "/spring/root-context.xml", "/spring/appServlet/servlet-context.xml" })
public class DeviceInfoTest {

	@Resource(name="leshanServer")
	DefaultLwm2mServer leshanServer;
	
	@Autowired
	DeviceInfoService deviceInfoService;
	
	static String deviceId = "123456789";
	
	@Test
	public void severStartTest(){
		System.out.println("severTest");
		LeshanServer server = leshanServer.getLwm2mServer();
		System.out.println(server.getSecureAddress());		
	}
	
	@Test
	public void selectdeviceList(){
		DeviceInfoVO searchVo = new DeviceInfoVO();
		searchVo.setLimit(1);
		List<DeviceInfoVO> deviceList = deviceInfoService.selectDeviceList(searchVo);
		assertNotNull(deviceList);
		assertEquals(1, deviceList.size());		
	}
	
	@Test
	public void slectDeviceDetail(){
		String deviceId = "0.2.1";
		DeviceInfoVO deviceInfo = deviceInfoService.selectDeviceDetail(deviceId);
		assertNotNull(deviceInfo);
	}
	
	//통신 상태가 연결된 경우에만 테스트 가능
	@Test
	public void selectDeviceObject(){
		DeviceObjectInfo deviceInfo = deviceInfoService.selectDeviceObjects(deviceId);		
		if(deviceInfo != null){
			System.out.println(new Gson().toJson(deviceInfo));
		}
	}
}
