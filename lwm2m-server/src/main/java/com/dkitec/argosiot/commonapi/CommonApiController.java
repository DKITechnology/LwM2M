package com.dkitec.argosiot.commonapi;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.dkitec.argosiot.commonapi.domain.CommonApiInfo;
import com.dkitec.lwm2m.common.code.ComCode;
import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.domain.DeviceInaeUser;
import com.dkitec.lwm2m.domain.message.MessageRefInfoVO;
import com.dkitec.lwm2m.service.message.MessageService;
import com.google.gson.Gson;

/**
 * <b>클래스 설명 </b> : 공통 API 컨트롤러 클래스
 * @author DKI
 * @see java.lang.Exception
 */
@RestController
@RequestMapping(value = "/commonAPI", produces = "application/json; charset=utf-8")
public class CommonApiController { 
	
	@Autowired
	CommonApiService commonApiService;

    @Autowired
    @Qualifier("commonApiProp") 
    private Properties commonApiProp;
    
    @Autowired
    MessageService messageService;
    
	/**
	 * <b>메서드 설명</b> 			: 동적 API 컨트롤러 메서드 POST, PUT 전용 (BODY로 입력 인자 수신)
	 * @param mainModuleName 	: uri메인모듈
	 * @param version 			: api버전
	 * @param subModuleName 	: uri서브모듈
	 * @param bodyMap 			: JSON Body
	 * @param headers			: 요청 헤더
	 * @param request			: HttpServletRequest Object
	 * @return Object API 실행 결과
	 * @throws Exception
	 */
	@RequestMapping(value = "/{mainModuleName}/{version}/{subModuleName}", method={RequestMethod.POST,RequestMethod.PUT})
	@ResponseStatus(HttpStatus.OK)	
	@ResponseBody
	public Object commonApiBodyProc(@PathVariable ("mainModuleName") String mainModuleName,
			@PathVariable ("version") String version,
			@PathVariable ("subModuleName") String subModuleName,
			@RequestBody HashMap<String, Object> bodyMap,
			@RequestHeader HttpHeaders headers,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception
			{

		final HashMap<String, Object> validMap = new HashMap<String, Object>();
		validMap.put("mainModuleName", mainModuleName);
		validMap.put("version", version);
		validMap.put("subModuleName", subModuleName);
		validMap.put("method", request.getMethod());
		validMap.put("headers", headers);
		validMap.put("bodyMap", bodyMap);
		validMap.put("hostUrl", CommonUtil.getURL(request));
		
		//openAPI 메시지 추가
		String creDatm 			= CommonUtil.getNow(ComCode.DataFormat.DateStrFormat.getValue());
		Object result 			= null;
		String userId 			= headers.get("userId").get(0).toString();
		MessageRefInfoVO msgRef = new MessageRefInfoVO();	
		// 제어인 경우 Async
		try {
			CommonApiInfo apiInfo = commonApiService.selectCommonApiInfo(validMap);
			msgRef.setApiSerno(String.valueOf(apiInfo.getSerialNo()));
			validMap.put(CommonApiService.COMMONAPIINFO, apiInfo);
			if ( subModuleName.compareTo(commonApiProp.getProperty("comAPI.control.subModuleName")) == 0 ) {
				@SuppressWarnings("unchecked")
				HashMap<String, Object> resultMap = (HashMap<String, Object>) commonApiService.commonApiProcess(validMap);
				String resourceId = (String) resultMap.get(commonApiProp.getProperty("comAPI.control.resourceId"));
				result = commonApiService.controlResultStandBy(resourceId, resultMap, 0);
			} else {				
				result =  commonApiService.commonApiProcess(validMap);
			}	
		} catch (CommonApiException comExcp){
			//int errorCode = comExcp.getCode();
			msgRef.setErrorCode(ComCode.ErrorTypeCode.RCV_ERROR.getValue());
			msgRef.setErrorMessage(comExcp.getMsg());
			HashMap<String, Object> responseMap = new HashMap<String, Object>();
			responseMap.put("code", comExcp.getCode());
			responseMap.put("msg", comExcp.getMsg());
			throw comExcp;
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			DeviceInaeUser deviceInfo = new DeviceInaeUser();
			deviceInfo.setUserId(userId);		
			messageService.insertHttpRcvMsg(deviceInfo, creDatm, request, new Gson().toJson(bodyMap), response, new Gson().toJson(result), msgRef);
		}
		return result;
	}
	
	/**
	 * <b>메서드 설명</b> 			: 동적 API 컨트롤러  메서드 GET, DELET 전용 (HEADER로 입력 인자 수신)
	 * @param mainModuleName 	: uri메인모듈
	 * @param version 			: api버전
	 * @param subModuleName 	: uri서브모듈
	 * @param headers			: 요청 헤더
	 * @param request			: HttpServletRequest Object
	 * @return Object API 실행 결과
	 * @throws Exception
	 */
	@RequestMapping(value = "/{mainModuleName}/{version}/{subModuleName}", method={RequestMethod.GET, RequestMethod.DELETE})
	@ResponseStatus(HttpStatus.OK)	
	@ResponseBody
	public Object commonApiHeadersProc(@PathVariable ("mainModuleName") String mainModuleName,
			@PathVariable ("version") String version,
			@PathVariable ("subModuleName") String subModuleName,
			@RequestHeader HttpHeaders headers,
			HttpServletRequest request,
			HttpServletResponse response
			)  throws Exception {

		HashMap<String, Object> validMap = new HashMap<String, Object>();
		validMap.put("mainModuleName", mainModuleName);
		validMap.put("version", version);
		validMap.put("subModuleName", subModuleName);
		validMap.put("method", request.getMethod());
		validMap.put("headers", headers);
		validMap.put("bodyMap", null);
		validMap.put("hostUrl", CommonUtil.getURL(request));
		/***** urlParameters *****/
		validMap.put("urlParameters", CommonUtil.getUrlParameters(request));
		
		//openAPI 메시지 추가
		String creDatm 			= CommonUtil.getNow(ComCode.DataFormat.DateStrFormat.getValue());
		Object result 			= null;
		String userId 			= headers.get("userId").get(0).toString();
		MessageRefInfoVO msgRef = new MessageRefInfoVO();	
		try {			
			CommonApiInfo apiInfo = commonApiService.selectCommonApiInfo(validMap);
			msgRef.setApiSerno(String.valueOf(apiInfo.getSerialNo()));
			validMap.put(CommonApiService.COMMONAPIINFO, apiInfo);
			result = commonApiService.commonApiProcess(validMap);
		} catch (CommonApiException comExcp){
			//int errorCode = comExcp.getCode();
			msgRef.setErrorCode(ComCode.ErrorTypeCode.RCV_ERROR.getValue());
			msgRef.setErrorMessage(comExcp.getMsg());
			HashMap<String, Object> responseMap = new HashMap<String, Object>();
			responseMap.put("code", comExcp.getCode());
			responseMap.put("msg", comExcp.getMsg());
			throw comExcp;
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			DeviceInaeUser deviceInfo = new DeviceInaeUser();
			deviceInfo.setUserId(userId);		
			messageService.insertHttpRcvMsg(deviceInfo, creDatm, request, null, response, new Gson().toJson(result), msgRef);
		}
		return result;	
	}
}