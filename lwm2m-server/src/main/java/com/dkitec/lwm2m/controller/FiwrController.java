package com.dkitec.lwm2m.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.common.util.PageDateParmVO;
import com.dkitec.lwm2m.common.util.json.JsonResult;
import com.dkitec.lwm2m.common.util.json.JsonResultList;
import com.dkitec.lwm2m.domain.FwUpdateReqInfoVO;
import com.dkitec.lwm2m.domain.FwUpdateVO;
import com.dkitec.lwm2m.service.intf.FwUpdateService;
import com.dkitec.lwm2m.service.message.MessageService;
import com.google.gson.Gson;

/**
 * FirmwareUpdate Controller
 *
 */

@RequestMapping(value="firmwares" , produces = "application/json; charset=utf-8")
@RestController
public class FiwrController {

	Logger logger = LoggerFactory.getLogger(FiwrController.class);
	
	@Autowired
	FwUpdateService fwupdateService;
	
	@Autowired
	MessageService messageService;
	
	@RequestMapping(value="upgrades", method=RequestMethod.POST)
	public JsonResult reqFirmwareUpgrade(@RequestBody FwUpdateVO fwvo ,HttpServletRequest req, HttpServletResponse res){
		JsonResult responseMsg = new JsonResult();
		String result = "fail";
		try {
			if(CommonUtil.isEmpty(fwvo.getFiwrId()))
				throw new Exception("Firmware ID is not null");
			if(fwvo.getDeviceIds() == null || fwvo.getDeviceIds().size() < 1)
				throw new Exception("Device is not null");
			result = fwupdateService.reqFirmwareUpgradeSave(fwvo);
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			responseMsg.setErrorMsg(e.getMessage());
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			responseMsg.setResult(result);
			messageService.insertHttpRcvMsg(null, CommonUtil.nowToDefaultStr(), req, new Gson().toJson(fwvo), res, new Gson().toJson(responseMsg));
		}		
		return responseMsg;
	}
	
	@RequestMapping(value="upgrades/{requestID}", method=RequestMethod.PUT)
	public JsonResult reqFrimwareUpgradeUpdate(@PathVariable("requestID")String requestID, @RequestBody FwUpdateVO fwvo, HttpServletRequest req, HttpServletResponse res){
		JsonResult responseMsg = new JsonResult();
		String result = "fail";
		try {
			fwvo.setReqId(requestID);
			if(fwvo.getDeviceIds() == null || fwvo.getDeviceIds().size() < 1)
				throw new Exception("Device is not null");
			result = fwupdateService.reqFirmwareUpgradeUpdate(fwvo);
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			responseMsg.setResult(result);
			responseMsg.setErrorMsg(e.getMessage());
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			if(result.equals("success"))
				res.setStatus(HttpServletResponse.SC_NO_CONTENT);
			responseMsg.setResult(result);
			messageService.insertHttpRcvMsg(null, CommonUtil.nowToDefaultStr(), req, new Gson().toJson(fwvo), res, new Gson().toJson(responseMsg));
		}
		return responseMsg;
	}
	
	@RequestMapping(value="upgrades/{requestID}", method=RequestMethod.DELETE)
	public JsonResult reqFrimwareUpgradeCancel(@PathVariable("requestID")String requestID, HttpServletRequest req, HttpServletResponse res){
		JsonResult responseMsg = new JsonResult();
		String result = "fail";
		try {
			result = fwupdateService.reqFirmwareUpgradeUpdateCancel(requestID);
		} catch (NullPointerException e) {
			res.setStatus(HttpServletResponse.SC_NOT_FOUND);			
			responseMsg.setErrorMsg("Firmware upgrade Request Not found.");
		}catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);			
			responseMsg.setErrorMsg(e.getMessage());
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}  finally {
			responseMsg.setResult(result);
			messageService.insertHttpRcvMsg(null, CommonUtil.nowToDefaultStr(), req, null, res, new Gson().toJson(responseMsg));
		}		
		return responseMsg;
	}
	
	@RequestMapping(value = "upgrades", method = RequestMethod.GET)
	public JsonResultList<FwUpdateReqInfoVO> reqFimwareUpgradeList(
			@RequestParam(value = "limit", required = false, defaultValue = "10") String limit,
			@RequestParam(value = "offset", required = false, defaultValue = "0") String offset, 
			@RequestParam(value = "reqStartDate", required = false) String startDate,
			@RequestParam(value = "reqEndDate", required = false) String endDate,
			HttpServletRequest req, HttpServletResponse res) {
		JsonResultList<FwUpdateReqInfoVO> fwUpdateList = new JsonResultList<FwUpdateReqInfoVO>();
		try {
			PageDateParmVO searchDevc = new PageDateParmVO();
			searchDevc.setLimit(Integer.parseInt(limit));
			searchDevc.setOffset(Integer.parseInt(offset));

			if(!CommonUtil.isEmpty(startDate)){
				if(startDate.length() == 8){
					startDate = startDate + "0000";
				}else if(startDate.length() > 12){
					startDate = startDate.substring(0, 12);
				}					
				searchDevc.setStartDate(startDate);
			}				
			if(!CommonUtil.isEmpty(endDate)){
				if(endDate.length() == 8){
					endDate = endDate + "2359";
				}else if(endDate.length() > 12){
					endDate = endDate.substring(0, 12);
				}
				searchDevc.setEndDate(endDate);
			}				
			fwUpdateList.setResultList(fwupdateService.selectFwUpgradeList(searchDevc));
			fwUpdateList.setResultTotalCnt(fwupdateService.selectFwUpgradeListCnt(searchDevc));
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(null, CommonUtil.nowToDefaultStr(), req, null, res, new Gson().toJson(fwUpdateList));
		}		
		return fwUpdateList;
	}
	
	
	@RequestMapping(value="upgrades/{requestID}", method=RequestMethod.GET)
	public FwUpdateVO reqFimwareUpgradeDetail(@PathVariable("requestID")String requestID,
			HttpServletRequest req, HttpServletResponse res){
		FwUpdateVO fwInfo = new FwUpdateVO();
		try {
			fwInfo = fwupdateService.selectFwUpgradeResultDetail(requestID);
			if(fwInfo == null){
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(null, CommonUtil.nowToDefaultStr(), req, null, res, new Gson().toJson(fwInfo, FwUpdateVO.class));
		}		
		return fwInfo;
	}
	
	@RequestMapping(value="downloads/{firmwId}", method=RequestMethod.GET)
	public void reqFimwareDownload(@PathVariable("firmwId")String firmwId,
			HttpServletRequest req, HttpServletResponse res){
		try {
			fwupdateService.firmwareFileDownload(firmwId, res);
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
	}
}
