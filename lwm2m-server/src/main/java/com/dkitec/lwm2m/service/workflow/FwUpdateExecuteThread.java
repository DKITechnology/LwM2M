package com.dkitec.lwm2m.service.workflow;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.dkitec.lwm2m.common.code.ComCode;
import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.domain.FwUpdateResultVO;
import com.dkitec.lwm2m.domain.RequestResultVO;
import com.dkitec.lwm2m.domain.workflow.FwUpdateConfig;
import com.dkitec.lwm2m.domain.workflow.ReadResponseVO;
import com.dkitec.lwm2m.service.intf.Lwm2mRequestService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


public class FwUpdateExecuteThread extends Thread{

	Logger logger = LoggerFactory.getLogger(FwUpdateExecuteThread.class);
	
	private String deviceReqId;
	
	private String deviceId;
	
	private Lwm2mRequestService lwm2mRequest;
	
	private FwUpdateWkService fwUpdateWkService;
	
	private FwUpdateConfig fwUpdateConfig;
	
	public FwUpdateExecuteThread(String deviceReqId, String deviceId, Lwm2mRequestService lwm2mRequest,
			FwUpdateWkService fwUpdateWkService) {
		this.deviceReqId = deviceReqId;
		this.deviceId = deviceId;
		this.lwm2mRequest = lwm2mRequest;
		this.fwUpdateWkService = fwUpdateWkService;
	}
	
	public FwUpdateExecuteThread(String deviceReqId, String deviceId, Lwm2mRequestService lwm2mRequest,
			FwUpdateWkService fwUpdateWkService, FwUpdateConfig fwUpdateConfig) {
		this.deviceReqId = deviceReqId;
		this.deviceId = deviceId;
		this.lwm2mRequest = lwm2mRequest;
		this.fwUpdateWkService = fwUpdateWkService;
		this.fwUpdateConfig = fwUpdateConfig;
	}
	
	@Override
	public void run() {
		String logTitle = "firmware update Task";
		logger.debug("[Excute Fimware Update] START");
		int replayStatCnt 	= 0;
		int replayResultCnt = 0;
		String updateResult = ComCode.RequestResCode.FAIL.getValue(); //fail
		String resMsg = "firmware update fail.";
		int limtCnt = 5;
		int sleepTime = 3000;
		
		if(fwUpdateConfig != null){
			if(!CommonUtil.isEmpty(fwUpdateConfig.getLimtCnt())){
				limtCnt = Integer.valueOf(fwUpdateConfig.getLimtCnt());
			}
			if(!CommonUtil.isEmpty(fwUpdateConfig.getSleepTime())){
				sleepTime = Integer.valueOf(fwUpdateConfig.getSleepTime());
			}
		}
		
		try {
			for(replayStatCnt = 0; replayStatCnt < limtCnt; replayStatCnt++){
				try {
					RequestResultVO<String> readDownloadResult = lwm2mRequest.requestRead("TEXT", deviceId, 5, 0, 3);
					Map<String, Object> readResultMap = new HashMap<String, Object>();
					//Read Download Result
					if(readDownloadResult.getResultMsg() != null){
						readResultMap = new Gson().fromJson(readDownloadResult.getResultMsg(), readResultMap.getClass());
						if(readResultMap.get("result") != null){
							ReadResponseVO resp = new Gson().fromJson(String.valueOf(readResultMap.get("result")), ReadResponseVO.class);
							String downloadStat = resp.getValue();
							logger.debug("Firmware Download state (5/0/3) : " +  downloadStat +", 재시도 횟수 : " + replayStatCnt);
							if(downloadStat.equals("2") || downloadStat.equals("2.0")){
								logger.info("[Fimware Update Excute] start.");
								//Execute Firmware
								RequestResultVO<String> firmwareUpdate = lwm2mRequest.requestExecute(deviceId, 5, 0, 2, null);							
								logger.info("[Fimware Update Excute (5/0/2)] Result : " + firmwareUpdate.getCoapResultCd());
								if(firmwareUpdate.getCoapStatusCd().equals(ResponseCode.CHANGED.toString())){
									//updateResult = ComCode.RequestResCode.SUCCESS.getValue();
									//resMsg = "firmware update Excute success.";
									//Read Update Result
									while (replayResultCnt < limtCnt) {
										try {
											RequestResultVO<String> readUpdateResult = lwm2mRequest.requestRead("TEXT", deviceId, 5, 0, 5);
											logger.info("[Fimware Update Result (5/0/5)] Result : " + readUpdateResult.getCoapResultCd());
											if(readUpdateResult != null && readUpdateResult.getResultMsg() != null){
												readResultMap = new Gson().fromJson(readUpdateResult.getResultMsg(), readResultMap.getClass());
												if(readResultMap.get("result") != null){
													ReadResponseVO updateRes = new Gson().fromJson(String.valueOf(readResultMap.get("result")), ReadResponseVO.class);
													String fwUpdateResult = updateRes.getValue();
													if(fwUpdateResult.equals("1") || fwUpdateResult.equals("1.0")){
														updateResult = ComCode.RequestResCode.SUCCESS.getValue();
														resMsg = "firmware update Reulst success.";
													}else {
														resMsg = "firmware update Result fail, udpate Result : " + fwUpdateResult;
													}
													break;												
												}
											}
										} catch (Exception e) {
											LoggerPrint.printErrorLogExceptionrMsg(logger, e, logTitle);
										}
										Thread.sleep(sleepTime);
										replayResultCnt++;										
									}
								}else{
									resMsg = "firmware update Excute Fail.";
								}
								break; // 한번만 시행
							}else{
								resMsg = "firmware status not completed";
								// 3초후 다시 수행
								try {
									Thread.sleep(sleepTime);
								} catch (InterruptedException e) {logger.debug("[FwUpdateExcuteThread InterruptedException]");}								
							}
						}
					}
				} catch (JsonSyntaxException e) {
					LoggerPrint.printErrorLogExceptionrMsg(logger, e, logTitle);
				}
			}
		} catch (Exception e) {
			resMsg = "firmware update Unknown error : " + e.getMessage();
			LoggerPrint.printErrorLogExceptionrMsg(logger, e, logTitle);
		} finally {
			//complete Request
			FwUpdateResultVO clientResult = new FwUpdateResultVO();
			clientResult.setDeviceReqId(deviceReqId);
			clientResult.setDeviceId(deviceId);
			clientResult.setReqProcCd(ComCode.ProcessCode.COMPLETED.getValue());
			clientResult.setReqResCd(updateResult);
			clientResult.setReqResMsg(resMsg);
			fwUpdateWkService.updateClientResult(clientResult);	
		}			
	}
}
