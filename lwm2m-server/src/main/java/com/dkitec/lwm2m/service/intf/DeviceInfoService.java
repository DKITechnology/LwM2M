package com.dkitec.lwm2m.service.intf;

import java.util.List;

import org.eclipse.leshan.server.registration.Registration;

import com.dkitec.lwm2m.domain.DeviceInfoVO;
import com.dkitec.lwm2m.domain.DeviceObjectInfo;

public interface DeviceInfoService {

	/**
	 * 단말 목록 정보를 검색한다. 
	 * @param searchDevc 검색 조건
	 * @return 단말 목록
	 */
	public List<DeviceInfoVO> selectDeviceList(DeviceInfoVO searchDevc);
	
	/**
	 * 단말 전체 갯수를 조회한다.
	 * @param searchDevc
	 * @return
	 */
	public int selecDeviceTotalCnt(DeviceInfoVO searchDevc);
	
	/**
	 * 단말 상세 정보를 조회한다.
	 * @param deviceId
	 * @return
	 */
	public DeviceInfoVO selectDeviceDetail(String deviceId);
	
	/**
	 * 단말의 실제 object 정보를 조회한다.
	 * @param deviceId
	 * @return
	 */
	public DeviceObjectInfo selectDeviceObjects(String deviceId);
	
	/**
	 * 단말 Registration 정보를 RDB에 저장한다.
	 * @param deviceInfo
	 */
	public void upsertDeviceInfo(DeviceInfoVO deviceInfo);
	
	/**
	 * 단말 상태 정보를 업데이트 한다.
	 * @param deviceId 단말 아이디 ( endPoint == client )
	 * @param status true 정상 , false 무음답
	 * @return
	 */
	public void updateDeviceStatus(String deviceId, boolean status);
	
	/**
	 * 무응답인 단말의 상태 정보를 업데이트 한다.
	 * @param minutes : m'분' 이상일 경우 단말 상태 정보를 무응답으로 간주한다.
	 */
	public void updateNoConDeviceStatus(int minutes);
}
