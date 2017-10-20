package com.dkitec.download.firmware;

public interface FirmwareService {
	
	/**
	 * 펌웨어 정보를 조회한다
	 * @param firmwareId
	 * @return
	 */
	FirmwareVO getFirmware(String firmwareId);

}
