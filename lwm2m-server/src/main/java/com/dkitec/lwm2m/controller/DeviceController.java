package com.dkitec.lwm2m.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.common.util.json.JsonResultList;
import com.dkitec.lwm2m.domain.DeviceInfoVO;
import com.dkitec.lwm2m.domain.DeviceObjectInfo;
import com.dkitec.lwm2m.service.intf.DeviceInfoService;
import com.dkitec.lwm2m.service.message.MessageService;
import com.google.gson.Gson;

@RequestMapping(value = "devices", produces = "application/json; charset=utf-8")
@RestController
public class DeviceController {

	Logger logger = LoggerFactory.getLogger(DeviceController.class);
	
	@Autowired
	DeviceInfoService deviceService;
	
	@Autowired
	MessageService messageService;
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public JsonResultList<DeviceInfoVO> selectDeviceList(@RequestParam(value="devcModlCd", required=false)String devcModlCd, @RequestParam(value="limit", required=false)String limit, @RequestParam(value="offset", required=false) String offset, HttpServletRequest req, HttpServletResponse res){
		JsonResultList<DeviceInfoVO> deviceList = new JsonResultList<DeviceInfoVO>();
		try {
			DeviceInfoVO searchDevc = new DeviceInfoVO();
			searchDevc.setDevcModlCd(devcModlCd);
			searchDevc.setLimit((limit == null)? 20 : Integer.parseInt(limit));
			searchDevc.setOffset((offset == null)? 0 : Integer.parseInt(offset));
			deviceList.setResultList(deviceService.selectDeviceList(searchDevc));
			deviceList.setResultTotalCnt(deviceService.selecDeviceTotalCnt(searchDevc));
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(null, CommonUtil.nowToDefaultStr(), req, null, res, new Gson().toJson(deviceList));
		}
		return deviceList;
	}
	
	@RequestMapping(value="{deviceId:.*}", method=RequestMethod.GET)
	public DeviceInfoVO selectDeviceDetail(@PathVariable("deviceId")String deviceId, HttpServletRequest req, HttpServletResponse res){
		DeviceInfoVO deviceInfo = null;
		try {
			deviceInfo = deviceService.selectDeviceDetail(deviceId);
			if(deviceInfo == null){
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(deviceId, CommonUtil.nowToDefaultStr(), req, null, res, new Gson().toJson(deviceInfo));
		}
		return deviceInfo;
	}
	
	@RequestMapping(value="{deviceId:.*}/objects", method=RequestMethod.GET)
	public DeviceObjectInfo selectDeviceObject(@PathVariable("deviceId")String deviceId, HttpServletRequest req, HttpServletResponse res){
		DeviceObjectInfo result = new DeviceObjectInfo();
		try {
			result = deviceService.selectDeviceObjects(deviceId);
			if(result == null){
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(deviceId, CommonUtil.nowToDefaultStr(), req, null, res, new Gson().toJson(result));
		}
		return result;		
	}
}
