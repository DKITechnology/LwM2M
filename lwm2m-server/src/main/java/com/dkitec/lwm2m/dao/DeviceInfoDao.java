package com.dkitec.lwm2m.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.dkitec.lwm2m.domain.DeviceInfoVO;
import com.dkitec.lwm2m.domain.SecurityDataInfo;

@Repository
public interface DeviceInfoDao {

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
	 * 단말 정보를 반영한다. ( 단말 아이디가 없는 경우에는 생성 됨 )
	 * @param deviceId
	 * @return
	 */
	public int updateDeviceInfo(DeviceInfoVO deviceInfo);
	
	/**
	 * 단말 포트 정보를 업데이트 한다.
	 * @param deviceInfo
	 * @return
	 */
	public int upsertDeviceInfoPort(DeviceInfoVO deviceInfo);
	
	/**
	 * 단말 서버 아이디 정보를 업데이트 한다.
	 * @param deviceInfo
	 * @return
	 */
	public int upsertDeviceInfoSvId(DeviceInfoVO deviceInfo);
	
	/**
	 * 단말 상태 정보를 업데이트 한다.
	 * @param deviceId
	 * @return
	 */
	public int updateDeviceStatusCon(String deviceId);
	
	/**
	 * 단말 상태 정보를 업데이트 한다.
	 * @param deviceId
	 * @return
	 */
	public int updateDeviceStatusDisCon(String deviceId);
	
	/**
	 * 단말의 Security 정보를 업데이트 한다.
	 * @param securityInfo 보안 정보
	 * @return
	 */
	public int updateDeviceSecurity(SecurityDataInfo securityInfo);
	
	/**
	 * 무응답인 단말의 상태 정보를 업데이트 한다.
	 * @param minutes : m'분' 이상일 경우 단말 상태 정보를 무응답으로 간주한다.
	 */
	public void updateNoConDeviceStatus(int minutes);
}
