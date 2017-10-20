package com.dkitec.lwm2m.service.workflow;

import java.util.List;

import com.dkitec.lwm2m.domain.FwUpdateResultVO;

public interface FwUpdateWkService {

	public void updateClientResult(FwUpdateResultVO clientResult);
	
	public void updateInvalidClientResults(List<FwUpdateResultVO> clientResults);
}
