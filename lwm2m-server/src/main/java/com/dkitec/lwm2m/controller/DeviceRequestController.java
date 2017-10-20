package com.dkitec.lwm2m.controller;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dkitec.lwm2m.common.code.ComCode;
import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.common.util.json.JsonResult;
import com.dkitec.lwm2m.domain.Lwm2mObjectInfo;
import com.dkitec.lwm2m.domain.Lwm2mRsourceInfo;
import com.dkitec.lwm2m.domain.ObserveInfoVO;
import com.dkitec.lwm2m.domain.RequestResultVO;
import com.dkitec.lwm2m.service.intf.Lwm2mRequestService;
import com.dkitec.lwm2m.service.message.MessageService;
import com.google.gson.Gson;

import org.eclipse.leshan.core.model.ResourceModel.Type;

/**
 * 서버에서 LWM2M 단말로 요청 controller
 * produce = CONTENT-TYPE
 */
@RequestMapping(value = "requests/{deviceID:.*}", produces = "application/json; charset=utf-8")
@RestController
public class DeviceRequestController {
	
	Logger logger = LoggerFactory.getLogger(DeviceRequestController.class);
	
	@Autowired
	Lwm2mRequestService lwm2mRequest;
	
	@Autowired
	MessageService messageService;
	
	public static class ResouceTypeConverter extends PropertyEditorSupport {
		@Override
		public void setAsText(String text) throws IllegalArgumentException {
			setAsText(text.toUpperCase());
		}
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Type.class, new ResouceTypeConverter());
	}
	
	/**
	 * objectInstance Create
	 * @param deviceId deviceID
	 * @param objectId 오브젝트 ID
	 * @param format format 형식 ( TLV 또는 JSON 입력 )
	 * @param crtObjInst objectInstace 생성 정보
	 * @param req
	 * @param resp
	 * @return
	 */
	@RequestMapping(value="/{objectId}", method=RequestMethod.POST)
	public Lwm2mObjectInfo deviceRequestObjInstCreate(@PathVariable("deviceID") String deviceId,
			@PathVariable("objectId") int objectId,
			@RequestParam(value = "format", required = false) String format,
			@RequestBody Lwm2mObjectInfo crtObjInst,
			HttpServletRequest req, HttpServletResponse resp) {
		if(logger.isDebugEnabled()){logger.debug("###clinet Request {} create", deviceId);}
		Lwm2mObjectInfo resultInfo = new Lwm2mObjectInfo();
		try {
			RequestResultVO<Lwm2mObjectInfo> result = lwm2mRequest.requestCreate(format, deviceId, objectId, crtObjInst);
			if(result == null){
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{
				String coapResultCd = result.getCoapResultCd();
				resp.setHeader(ComCode.ResponseHeader.coapResultCode.getValue(), coapResultCd);
				if( coapResultCd.equals("CREATED") ){
					resp.setStatus(HttpServletResponse.SC_CREATED);
				}else{
					resp.setStatus(CommonUtil.coapResultToHttpCode(coapResultCd));
				}
				resultInfo = result.getResultData();
			}
		} catch (NullPointerException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(deviceId, CommonUtil.nowToDefaultStr(), req, new Gson().toJson(crtObjInst), resp, new Gson().toJson(resultInfo));
		}
		return resultInfo;
	}
	
	@RequestMapping(value="/{objectId}/{objectInstanceId}", method=RequestMethod.POST , params = {"reqType=observe"})
	public ObserveInfoVO deviceRequestObjInstObserve(@PathVariable("deviceID") String deviceId,
			@PathVariable("objectId") int objectId,
			@PathVariable("objectInstanceId") int objectInstanceId,
			@RequestParam(value = "format", required = false) String format,
			@RequestBody ObserveInfoVO observeInfo,
			HttpServletRequest req, HttpServletResponse resp) {
		if(logger.isDebugEnabled()){logger.debug("###clinet Request {} observe", deviceId);}
		ObserveInfoVO response = new ObserveInfoVO();
		try {
			logger.debug("Request observe");
			RequestResultVO<ObserveInfoVO> result = lwm2mRequest.requestObserve(format, deviceId, objectId, objectInstanceId, observeInfo);
			if(result == null){
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{					
				String coapResultCd = result.getCoapResultCd();
				resp.setHeader(ComCode.ResponseHeader.coapResultCode.getValue(), coapResultCd);
				response = result.getResultData();
				if( coapResultCd.equals("CONTENT") ){
					resp.setStatus(HttpServletResponse.SC_CREATED);
				}else{
					resp.setStatus(CommonUtil.coapResultToHttpCode(coapResultCd));
				}
			}
		} catch (NullPointerException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(deviceId, CommonUtil.nowToDefaultStr(), req, new Gson().toJson(observeInfo), resp, new Gson().toJson(response));
		}
		return response;
	}
	
	@RequestMapping(value="/{objectId}/{objectInstanceId}", method=RequestMethod.PUT , params = {"reqType=observe"})
	public JsonResult deviceRequestObjInstObserveCancel(@PathVariable("deviceID") String deviceId,
			@PathVariable("objectId") int objectId,
			@PathVariable("objectInstanceId") int objectInstanceId,
			@RequestParam(value = "format", required = false) String format,
			@RequestBody ObserveInfoVO observeInfo,
			HttpServletRequest req, HttpServletResponse resp) {
		if(logger.isDebugEnabled()){logger.debug("###clinet Request {} observe cancel", deviceId);}
		JsonResult response = new JsonResult();
		try {
			logger.debug("Request observe");
			RequestResultVO<ObserveInfoVO> result = lwm2mRequest.cancelObserve(format, deviceId, objectId, objectInstanceId, observeInfo);
			if(result == null){
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{
				response.setResult(result.getResultData().getCancelCnt()+"");
				if(result.getResultData().getCancelCnt().equals("0")){
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}else{
					resp.setStatus(HttpServletResponse.SC_OK);					
				}				
			}		
		} catch (NullPointerException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(deviceId, CommonUtil.nowToDefaultStr(), req, new Gson().toJson(observeInfo), resp, new Gson().toJson(response));
		}
		return response;
	}
	
	@RequestMapping(value="/{objectId}/{objectInstanceId}", method=RequestMethod.GET)
	public String deviceRequestObjInstRead(@PathVariable("deviceID") String deviceId,
			@PathVariable("objectId") int objectId,
			@PathVariable("objectInstanceId") int objectInstanceId,
			@RequestParam(value = "format", required = false) String format, 
			HttpServletRequest req, HttpServletResponse resp) {
		if(logger.isDebugEnabled()){logger.debug("###clinet Request {} read", deviceId);}
		String response = null;
		try {
			RequestResultVO<String> result = lwm2mRequest.requestRead(format, deviceId, objectId, objectInstanceId);	
			if(result == null){
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{					
				String coapResultCd = result.getCoapResultCd();
				resp.setHeader(ComCode.ResponseHeader.coapResultCode.getValue(), coapResultCd);
				response = result.getResultMsg();
				if( coapResultCd.equals("CONTENT") ){
					resp.setStatus(HttpServletResponse.SC_OK);
				}else{
					resp.setStatus(CommonUtil.coapResultToHttpCode(coapResultCd));
				}
			}			
		} catch (NullPointerException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(deviceId, CommonUtil.nowToDefaultStr(), req, null, resp, response, null);
		}
		return response;
	}
	
	@RequestMapping(value="/{objectId}/{objectInstanceId}", method=RequestMethod.PUT)
	public String deviceRequestObjInstWrite(@PathVariable("deviceID") String deviceId,
			@PathVariable("objectId") int objectId,
			@PathVariable("objectInstanceId") int objectInstanceId,
			@RequestParam(value = "format", required = false) String format,
			@RequestBody Lwm2mObjectInfo lwm2mObj,
			HttpServletRequest req, HttpServletResponse resp) {
		if(logger.isDebugEnabled()){logger.debug("###clinet Request {} write", deviceId);}
		String response = null;
		try {
			if(lwm2mObj == null)
				throw new Exception("lwm2m write object null");
			RequestResultVO<String> result = lwm2mRequest.requestWrite(format, deviceId, objectId, objectInstanceId, lwm2mObj);
			if(result == null){
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{					
				String coapResultCd = result.getCoapResultCd();
				resp.setHeader(ComCode.ResponseHeader.coapResultCode.getValue(), coapResultCd);
				response = result.getResultMsg();
				if( coapResultCd.equals("CHANGED") ){
					resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
				}else{
					resp.setStatus(CommonUtil.coapResultToHttpCode(coapResultCd));
				}
			}			
		} catch (NullPointerException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(deviceId, CommonUtil.nowToDefaultStr(), req, new Gson().toJson(lwm2mObj), resp, response, null);
		}
		return response;
	}
	
	@RequestMapping(value="/{objectId}/{objectInstanceId}", method=RequestMethod.DELETE)
	public String deviceRequestObjInstDelete(@PathVariable("deviceID") String deviceId,
			@PathVariable("objectId") int objectId,
			@PathVariable("objectInstanceId") int objectInstanceId,
			HttpServletRequest req, HttpServletResponse resp) {
		if(logger.isDebugEnabled()){logger.debug("###clinet Request {} delete", deviceId);}
		String response = null;
		try {
			RequestResultVO<String> result = lwm2mRequest.requestDelete(deviceId, objectId, objectInstanceId);
			if(result == null){
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{					
				String coapResultCd = result.getCoapResultCd();
				resp.setHeader(ComCode.ResponseHeader.coapResultCode.getValue(), coapResultCd);
				response = result.getResultMsg();
				if( coapResultCd.equals("DELETED") ){
					resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
				}else{
					resp.setStatus(CommonUtil.coapResultToHttpCode(coapResultCd));
				}
			}			
		} catch (NullPointerException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(deviceId, CommonUtil.nowToDefaultStr(), req, null, resp, response);
		}
		return response;
	}	
	
	/*** Resource에 대한 요청 ***/

	@RequestMapping(value="/{objectId}/{objectInstanceId}/{resourceId}", method=RequestMethod.POST , params = {"reqType=observe"})
	public ObserveInfoVO deviceRequestResourceObserve(@PathVariable("deviceID") String deviceId,
			@PathVariable("objectId") int objectId,
			@PathVariable("objectInstanceId") int objectInstanceId,
			@PathVariable("resourceId") int resourceId,
			@RequestParam(value = "format", required = false) String format,
			@RequestBody ObserveInfoVO observeInfo,
			HttpServletRequest req, HttpServletResponse resp) {
		if(logger.isDebugEnabled()){logger.debug("###clinet Request {} observe", deviceId);}
		ObserveInfoVO response = new ObserveInfoVO();
		try {
			logger.debug("Request observe");
			RequestResultVO<ObserveInfoVO> result = lwm2mRequest.requestObserve(format, deviceId, objectId, objectInstanceId, resourceId, observeInfo);
			if(result == null){
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{
				String coapResultCd = result.getCoapResultCd();
				resp.setHeader(ComCode.ResponseHeader.coapResultCode.getValue(), coapResultCd);
				if( coapResultCd.equals("CONTENT") ){
					resp.setStatus(HttpServletResponse.SC_CREATED);
				}else{
					resp.setStatus(CommonUtil.coapResultToHttpCode(coapResultCd));
				}
				response = result.getResultData();
			}		
		} catch (NullPointerException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(deviceId, CommonUtil.nowToDefaultStr(), req, new Gson().toJson(observeInfo), resp, new Gson().toJson(response));
		}
		return response;
	}
	
	@RequestMapping(value="/{objectId}/{objectInstanceId}/{resourceId}", method=RequestMethod.PUT , params = {"reqType=observe"})
	public JsonResult deviceRequestResouceObserveCancel(@PathVariable("deviceID") String deviceId,
			@PathVariable("objectId") int objectId,
			@PathVariable("objectInstanceId") int objectInstanceId,
			@PathVariable("resourceId") int resourceId,
			@RequestParam(value = "format", required = false) String format,
			@RequestBody ObserveInfoVO observeInfo,
			HttpServletRequest req, HttpServletResponse resp) {
		if(logger.isDebugEnabled()){logger.debug("###clinet Request {} observe cancel", deviceId);}
		JsonResult response = new JsonResult();
		try {
			logger.debug("Request observe");
			RequestResultVO<ObserveInfoVO> result = lwm2mRequest.cancelObserve(format, deviceId, objectId, objectInstanceId, resourceId, observeInfo);
			if(result == null){
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{
				if(result.getResultData().getCancelCnt().equals("0")){
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}else{				
					response.setResult(result.getResultData().getCancelCnt()+"");
				}				
			}		
		} catch (NullPointerException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(deviceId, CommonUtil.nowToDefaultStr(), req, new Gson().toJson(observeInfo), resp, new Gson().toJson(response));
		}
		return response;
	}
	
	@RequestMapping(value="/{objectId}/{objectInstanceId}/{resourceId}", method=RequestMethod.GET)
	public String deviceRequestResourceRead(@PathVariable("deviceID") String deviceId,
			@PathVariable("objectId") int objectId,
			@PathVariable("objectInstanceId") int objectInstanceId,
			@PathVariable("resourceId") int resourceId,
			@RequestParam(value = "format", required = false) String format, 
			HttpServletRequest req, HttpServletResponse resp) {
		if(logger.isDebugEnabled()){logger.debug("###clinet Request {} read", deviceId);}
		String response = null;
		try {
			RequestResultVO<String> result = lwm2mRequest.requestRead(format, deviceId, objectId, objectInstanceId, resourceId);	
			if(result == null){
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{					
				String coapResultCd = result.getCoapResultCd();
				resp.setHeader(ComCode.ResponseHeader.coapResultCode.getValue(), coapResultCd);
				response = result.getResultMsg();
				if( coapResultCd.equals("CONTENT") ){
					resp.setStatus(HttpServletResponse.SC_OK);
				}else{
					resp.setStatus(CommonUtil.coapResultToHttpCode(coapResultCd));
				}
			}			
		} catch (NullPointerException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(deviceId, CommonUtil.nowToDefaultStr(), req, null, resp, response);
		}
		return response;
	}
	
	@RequestMapping(value="/{objectId}/{objectInstanceId}/{resourceId}", method=RequestMethod.PUT)
	public String deviceRequestResouceWrite(@PathVariable("deviceID") String deviceId,
			@PathVariable("objectId") int objectId,
			@PathVariable("objectInstanceId") int objectInstanceId,
			@PathVariable("resourceId") int resourceId,
			@RequestParam(value = "format", required = false) String format,
			@RequestBody Lwm2mRsourceInfo resouceInfo,
			HttpServletRequest req, HttpServletResponse resp) {
		if(logger.isDebugEnabled()){logger.debug("###clinet Request {} write", deviceId);}
		String response = null;
		try {
			if(resouceInfo == null){
				throw new Exception("lwm2m write object null");
			}else{
				resouceInfo.setRscId(resourceId);
			}				
			RequestResultVO<String> result = lwm2mRequest.requestWrite(format, deviceId, objectId, objectInstanceId, resouceInfo);
			if(result == null){
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{					
				String coapResultCd = result.getCoapResultCd();
				resp.setHeader(ComCode.ResponseHeader.coapResultCode.getValue(), coapResultCd);
				response = result.getResultMsg();
				if( coapResultCd.equals("CHANGED") ){
					resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
				}else{
					resp.setStatus(CommonUtil.coapResultToHttpCode(coapResultCd));
				}
			}			
		} catch (NullPointerException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(deviceId, CommonUtil.nowToDefaultStr(), req, new Gson().toJson(resouceInfo), resp, response);
		}
		return response;
	}
	
	@RequestMapping(value="/{objectId}/{objectInstanceId}/{resourceId}", method=RequestMethod.POST, params = {"reqType=execute"})
	public String deviceRequestObjInstExecute(@PathVariable("deviceID") String deviceId,
			@PathVariable("objectId") int objectId,
			@PathVariable("objectInstanceId") int objectInstanceId,
			@PathVariable("resourceId") int resourceId,
			@RequestParam(value = "format", required = false) String format,
			@RequestBody Lwm2mRsourceInfo excuteValue,
			HttpServletRequest req, HttpServletResponse resp) {
		String response = null;
		try {
			if(logger.isDebugEnabled()){logger.debug("###clinet Request {} execute", deviceId);}
			String value = null;
			if(excuteValue != null && excuteValue.getRscValue() != null )
				value = String.valueOf(excuteValue.getRscValue());
			RequestResultVO<String> result = lwm2mRequest.requestExecute(deviceId, objectId, objectInstanceId, resourceId, value);
			if(result == null){
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}else{
				String coapResultCd = result.getCoapResultCd();
				resp.setHeader(ComCode.ResponseHeader.coapResultCode.getValue(), coapResultCd);
				response = result.getResultMsg();
				if( coapResultCd.equals("CHANGED") ){
					resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
				}else{
					resp.setStatus(CommonUtil.coapResultToHttpCode(coapResultCd));
				}
			}
		} catch (NullPointerException e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} finally {
			messageService.insertHttpRcvMsg(deviceId, CommonUtil.nowToDefaultStr(), req, new Gson().toJson(excuteValue), resp, response);
		}
		return response;
	}
}
