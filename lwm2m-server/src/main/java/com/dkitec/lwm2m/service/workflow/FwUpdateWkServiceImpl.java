package com.dkitec.lwm2m.service.workflow;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.dao.FwUpdateDao;
import com.dkitec.lwm2m.domain.FwUpdateResultVO;

@Service("fwUpdateWkService")
public class FwUpdateWkServiceImpl implements FwUpdateWkService{
	
	Logger logger = LoggerFactory.getLogger(FwUpdateWkServiceImpl.class);

	@Autowired
	FwUpdateDao fwUpdateDao;
	
	@Override
	public void updateClientResult(FwUpdateResultVO clientResult){		
		try {
			fwUpdateDao.updateFwClientResult(clientResult);
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}	
	}
	
	@Override
	public void updateInvalidClientResults(List<FwUpdateResultVO> clientResults) {
		try {
			fwUpdateDao.updateFwClientFailResult(clientResults);
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
	}
}
