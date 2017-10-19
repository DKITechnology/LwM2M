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
import org.springframework.web.bind.annotation.RestController;

import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.json.JsonResult;
import com.dkitec.lwm2m.domain.SecurityDataInfo;
import com.dkitec.lwm2m.service.intf.DeviceSecurityService;
import com.dkitec.lwm2m.service.message.MessageService;
import com.google.gson.Gson;

@RequestMapping(value = "securitys", produces = "application/json; charset=utf-8")
@RestController
public class DeviceSecurityController {
	
	Logger logger = LoggerFactory.getLogger(DeviceSecurityController.class);
	
	@Autowired
	DeviceSecurityService leshanSecurityService;
	
	@Autowired
	MessageService messageService;
	
	@RequestMapping(value="", method=RequestMethod.PUT)
	public JsonResult deviceSecurityInfoUpdate(@RequestBody SecurityDataInfo securityInfo, HttpServletRequest req, HttpServletResponse res){
		JsonResult responseMsg = new JsonResult(); 
		String result = leshanSecurityService.deviceSecurityUpdate(securityInfo);
		try {
			if(!result.equals("success")){
				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				responseMsg.setResult("fail");
				responseMsg.setErrorMsg(result);
			}else{
				responseMsg.setResult(result);
			}
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			responseMsg.setResult("fail");
			responseMsg.setErrorMsg(result);
		} finally {
			messageService.insertHttpRcvMsg(securityInfo.getDeviceId(), CommonUtil.nowToDefaultStr(), req, new Gson().toJson(securityInfo), res, new Gson().toJson(responseMsg));
		}
		return responseMsg;
	}
	
	@RequestMapping(value="{deviceId:.*}", method=RequestMethod.DELETE)
	public JsonResult deviceSecurityInfoDelete(@PathVariable("deviceId")String deviceId, HttpServletRequest req, HttpServletResponse res){
		JsonResult responseMsg = new JsonResult(); 
		try {
			String result = leshanSecurityService.deviceSecurityDelete(deviceId);
			if(!result.equals("success") && !result.equals("notfound")){
				res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				responseMsg.setResult("fail");
				responseMsg.setErrorMsg(result);
			}else{
				responseMsg.setResult(result);
			}
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			responseMsg.setResult("fail");
			responseMsg.setErrorMsg(e.getMessage());
		} finally {
			messageService.insertHttpRcvMsg(deviceId, CommonUtil.nowToDefaultStr(), req, null, res, new Gson().toJson(responseMsg));
		}
		return responseMsg;
	}
}
