package com.dkitec.lwm2m.service.workflow;

import java.util.List;
import java.util.Map;

import com.dkitec.lwm2m.domain.FwUpdateReqVO;
import com.dkitec.lwm2m.domain.FwUpdateVO;

public interface WorkFlowService {

	/**
	 * 펌웨어 정보 workflow 호출 
	 * @param fwupdates
	 */
	public void callWorkflowFarmware(List<FwUpdateReqVO> fwupdates);
	
	/**
	 * API 에서 workflow 호출
	 * @param apiSerno
	 * @param variableMap
	 */
	public void callWorkFlowByApi(int apiSerno, Map<String, Object> variableMap);
}
