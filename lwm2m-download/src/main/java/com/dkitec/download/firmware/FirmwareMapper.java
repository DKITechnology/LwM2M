package com.dkitec.download.firmware;

import org.springframework.stereotype.Repository;

@Repository
public interface FirmwareMapper {

	/**
	 * 펌웨어 정보를 조회한다
	 * @param firmwareId
	 * @return
	 */
	FirmwareVO getFirmware(String firmwareId);
	
}