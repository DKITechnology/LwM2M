package com.dkitec.lwm2m.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import com.dkitec.lwm2m.domain.FwUpdateReqVO;
import com.dkitec.lwm2m.service.intf.DeviceInfoService;
import com.dkitec.lwm2m.service.intf.FwUpdateService;
import com.dkitec.lwm2m.service.workflow.WorkFlowService;

/**
 * LwM2M 플랫폼에 필요한 Scheduling 업무를 수행한다. 
 * 1. 펌웨어 업데이트 요청 스케줄링
 * 2. 단말 무응답 상태 정보 변경 스케줄링
 */
public class ServerConfSchedule {

	Logger logger = LoggerFactory.getLogger(ServerConfSchedule.class);
	
	@Autowired
	FwUpdateService fwUpdateService;
	
	@Autowired
	WorkFlowService wokflowService;
	
	@Autowired
	DeviceInfoService deviceService;
	
	@Value("#{serverConfigProp['device.noConn.min']}")
	private int minutes;
	
	@Scheduled(fixedRate=1000)
	public void execute()
	{
		logger.debug("##Start Schedule 1 seconds");
		
		// 펌웨어 요청 스케줄링
		List<FwUpdateReqVO> fwUpdateList = fwUpdateService.selectFwReqList();
		if(fwUpdateList != null && fwUpdateList.size() > 0){
			fwUpdateService.updateRwReqStat(fwUpdateList);
			wokflowService.callWorkflowFarmware(fwUpdateList);
		}
		
		// 단말 상태 정보 업데이트  스케줄링
		deviceService.updateNoConDeviceStatus(minutes);
		
	}
}
