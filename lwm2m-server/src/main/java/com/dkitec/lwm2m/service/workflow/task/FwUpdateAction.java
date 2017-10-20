package com.dkitec.lwm2m.service.workflow.task;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.leshan.core.model.ResourceModel.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.dkitec.lwm2m.common.code.ComCode;
import com.dkitec.lwm2m.common.util.ApplicationContextProvider;
import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.domain.FwUpdateReqVO;
import com.dkitec.lwm2m.domain.FwUpdateResultVO;
import com.dkitec.lwm2m.domain.Lwm2mRsourceInfo;
import com.dkitec.lwm2m.domain.RequestResultVO;
import com.dkitec.lwm2m.domain.workflow.FwUpdateConfig;
import com.dkitec.lwm2m.service.intf.Lwm2mRequestService;
import com.dkitec.lwm2m.service.workflow.FwUpdateExecuteThread;
import com.dkitec.lwm2m.service.workflow.FwUpdateWkService;
import com.google.gson.JsonSyntaxException;

@Service
public class FwUpdateAction implements JavaDelegate{
	
	Logger logger = LoggerFactory.getLogger(FwUpdateAction.class);
	
	ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
		
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		List<FwUpdateReqVO> rstList = execution.getVariable(ComCode.VariableKey.FwUpdateValues.getValue(), new ArrayList<FwUpdateReqVO>().getClass());
		FwUpdateWkService fwUpdateWkService = applicationContext.getBean("fwUpdateWkService", FwUpdateWkService.class);
		
		List<FwUpdateResultVO> deviceErrorResult = new ArrayList<FwUpdateResultVO>();
		for(FwUpdateReqVO reqvo : rstList){
			if(!CommonUtil.isEmpty(reqvo.getFiwrId())){
				List<FwUpdateResultVO> deviceResult = reqvo.getReqResults();
				for(FwUpdateResultVO client : deviceResult){
					// 단말 모델과 요청 펌웨어 모델이 일치 하는지 확인
					if(client.getDeviceModelCd().equals(reqvo.getDevcModlCd())){						
						writeFwUpdateRequest(reqvo.getReqId(), client.getDeviceId(), reqvo.getFiwrPkgUri(), fwUpdateWkService);						
					}else{
						deviceErrorResult.add(client);
					}
				}
			}else{
				logger.info("RequestInfo{} not found Fimware ID" + reqvo.getReqId());
			}
		}
		
		if(deviceErrorResult != null && deviceErrorResult.size() > 0)
			fwUpdateWkService.updateInvalidClientResults(deviceErrorResult);
	}
	
	private void writeFwUpdateRequest(String deviceReqId, String deviceId, String packgeUrl, FwUpdateWkService fwUpdateWkService){
		String format = "TEXT";		
		logger.debug("[Firmware Update] clientID : {} , pakcage URL : " + packgeUrl, deviceId);
		
		FwUpdateConfig fwUpdateConifg = applicationContext.getBean(FwUpdateConfig.class);
		
		Lwm2mRequestService lwm2mRequest = applicationContext.getBean(Lwm2mRequestService.class);		
		Lwm2mRsourceInfo resouceInfo = new Lwm2mRsourceInfo();
		resouceInfo.setRscId(1);
		resouceInfo.setRscType(Type.STRING);
		resouceInfo.setRscValue(packgeUrl);

		FwUpdateResultVO clientResult = new FwUpdateResultVO();
		clientResult.setDeviceReqId(deviceReqId);
		clientResult.setDeviceId(deviceId);
		try {
			//진행 상태 : 진행중 업데이트 
			clientResult.setReqProcCd(ComCode.ProcessCode.PROCEEDING.getValue());
			
			RequestResultVO<String>  resultWrite = lwm2mRequest.requestWrite(format, deviceId, 5, 0, resouceInfo);
			if(resultWrite == null || resultWrite.getCoapStatusCd() == null){
				clientResult.setReqProcCd(ComCode.ProcessCode.COMPLETED.getValue());
				clientResult.setReqResCd(ComCode.RequestResCode.FAIL.getValue());
				clientResult.setReqResMsg("Device Connection Fail.");
			}else{
				if(resultWrite.getCoapStatusCd().equals(ResponseCode.CHANGED.toString())){
					//Execute firmware update
					FwUpdateExecuteThread fwExecute = new FwUpdateExecuteThread(deviceReqId, deviceId, lwm2mRequest, fwUpdateWkService, fwUpdateConifg);
					fwExecute.start();				
				}else{
					//firmware URL write fail.
					clientResult.setReqProcCd(ComCode.ProcessCode.COMPLETED.getValue());
					clientResult.setReqResCd(ComCode.RequestResCode.FAIL.getValue());
					clientResult.setReqResMsg("Write Firmware URL fail.");
				}
			}			
		} catch (Exception e) {
			clientResult.setReqProcCd(ComCode.ProcessCode.COMPLETED.getValue());
			clientResult.setReqResCd(ComCode.RequestResCode.FAIL.getValue());
			clientResult.setReqResMsg(e.getMessage());
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
		fwUpdateWkService.updateClientResult(clientResult);
	}
	
}
