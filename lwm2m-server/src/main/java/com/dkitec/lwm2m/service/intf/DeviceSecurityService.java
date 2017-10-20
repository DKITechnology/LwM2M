package com.dkitec.lwm2m.service.intf;

import com.dkitec.lwm2m.domain.SecurityDataInfo;

public interface DeviceSecurityService {
	
	/**
	 * 단말 보안 정보를 업데이트 한다.
	 * @param sectvo
	 * @return
	 */
	public String deviceSecurityUpdate(SecurityDataInfo sectvo);
	
	/**
	 * 단말 보안 정보를 삭제한다.
	 * @param deviceId
	 * @return
	 */
	public String deviceSecurityDelete(String deviceId);
}
