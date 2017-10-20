package com.dkitec.download.firmware;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FirmwareServiceImpl implements FirmwareService {

	/**
	 * mapper
	 */
	@Autowired
	private FirmwareMapper firmwareMapper;
	
	/**
	 * 펌웨어 정보를 조회한다
	 */
	@Override
	public FirmwareVO getFirmware(String firmwareId) {
		return firmwareMapper.getFirmware(firmwareId);
	}

}