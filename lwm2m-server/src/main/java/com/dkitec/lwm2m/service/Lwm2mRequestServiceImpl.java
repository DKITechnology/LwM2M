package com.dkitec.lwm2m.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.eclipse.leshan.core.model.ObjectModel;
import org.eclipse.leshan.core.model.ResourceModel;
import org.eclipse.leshan.core.model.ResourceModel.Type;
import org.eclipse.leshan.core.node.LwM2mMultipleResource;
import org.eclipse.leshan.core.node.LwM2mNode;
import org.eclipse.leshan.core.node.LwM2mObjectInstance;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.node.LwM2mSingleResource;
import org.eclipse.leshan.core.node.ObjectLink;
import org.eclipse.leshan.core.node.codec.CodecException;
import org.eclipse.leshan.core.observation.Observation;
import org.eclipse.leshan.core.request.ContentFormat;
import org.eclipse.leshan.core.request.CreateRequest;
import org.eclipse.leshan.core.request.DeleteRequest;
import org.eclipse.leshan.core.request.ExecuteRequest;
import org.eclipse.leshan.core.request.ObserveRequest;
import org.eclipse.leshan.core.request.ReadRequest;
import org.eclipse.leshan.core.request.WriteRequest;
import org.eclipse.leshan.core.request.WriteRequest.Mode;
import org.eclipse.leshan.core.request.exception.InvalidRequestException;
import org.eclipse.leshan.core.response.CreateResponse;
import org.eclipse.leshan.core.response.DeleteResponse;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ObserveResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.eclipse.leshan.server.californium.impl.LeshanServer;
import org.eclipse.leshan.server.observation.ObservationService;
import org.eclipse.leshan.server.registration.Registration;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dkitec.lwm2m.common.code.ComCode;
import com.dkitec.lwm2m.common.code.ComCode.Lwm2mKey;
import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.common.util.json.ResponseJson;
import com.dkitec.lwm2m.domain.CreateObjInstance;
import com.dkitec.lwm2m.domain.Lwm2mObjectInfo;
import com.dkitec.lwm2m.domain.Lwm2mRsourceInfo;
import com.dkitec.lwm2m.domain.ObserveInfoVO;
import com.dkitec.lwm2m.domain.RequestResultVO;
import com.dkitec.lwm2m.server.DefaultLwm2mServer;
import com.dkitec.lwm2m.service.intf.Lwm2mRequestService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class Lwm2mRequestServiceImpl implements Lwm2mRequestService {

	Logger logger = LoggerFactory.getLogger(Lwm2mRequestServiceImpl.class);
	
	@Value("#{serverConfigProp['lwm2m.default.format']}")
	private String defaultFormat;
	
	@Value("#{serverConfigProp['lwm2m.default.timeout']}")
	private long timeout;
	
	@Resource(name="leshanServer")
	DefaultLwm2mServer leshanServer;
	
	public Gson getGsonBuilder() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeHierarchyAdapter(LwM2mResponse.class, new ResponseJson());
		Gson gson = gsonBuilder.create();
		return gson;
	}
	
	public LeshanServer getLwm2mServerInstace() {
		LeshanServer server = leshanServer.getLwm2mServer();		
		return server;
	}
	
	public Registration getRegByEndpoint(String endpoint) {
		LeshanServer server = getLwm2mServerInstace();		
		Registration reg = server.getRegistrationService().getByEndpoint(endpoint);
		return reg;
	}
	
	public String getLwm2mPath(int objectId, int objectInstanceId, int resourceId){
		if(resourceId == -1 || resourceId < 0){
			return "/"+objectId+"/"+objectInstanceId;
		}else{
			return "/"+objectId+"/"+objectInstanceId+"/"+resourceId;
		}		
	}
	
	@Override
	public RequestResultVO<Lwm2mObjectInfo> requestCreate(String format, String endpoint, int objectId, Lwm2mObjectInfo lwm2mObj){		
		LeshanServer server = getLwm2mServerInstace();
		Registration reg = getRegByEndpoint(endpoint);
		CreateResponse response = null;		
		
		List<LwM2mResource> resources = new ArrayList<LwM2mResource>();
		for(Lwm2mRsourceInfo rs : lwm2mObj.getResources()){
			try {	
				if(rs.getRscType().equals(Type.INTEGER)) 
						rs.setRscValue(new Integer(String.valueOf(rs.getRscValue())).longValue());
			} catch (NumberFormatException e) {}
			resources.add(LwM2mSingleResource.newResource(rs.getRscId(), rs.getRscValue(), rs.getRscType()));
		}
		if(CommonUtil.isEmpty(lwm2mObj.getInstanceId())){
			try {
				if(format == null){
					format = defaultFormat;
				}
				response = server.send(reg,  new CreateRequest(ContentFormat.fromName(format.toUpperCase()), objectId, resources), timeout);
				String location = response.getLocation();
				lwm2mObj.setInstanceId(location.substring(String.valueOf(objectId).length()+1));
			} catch (InterruptedException e) {
				LoggerPrint.printErrorLogExceptionrMsg(logger, e);
			}
		}else{
			LwM2mObjectInstance objectInst = new LwM2mObjectInstance(Integer.valueOf(lwm2mObj.getInstanceId()), resources);
			try {
				if(format == null){
					format = defaultFormat;
				}
				response = server.send(reg,  new CreateRequest(ContentFormat.fromName(format.toUpperCase()), objectId, objectInst), timeout);						
			} catch (InterruptedException e) {
				LoggerPrint.printErrorLogExceptionrMsg(logger, e);
			}
		}
		//location 정보 응답
		lwm2mObj.setLocationPath(response.getLocation());
		
		RequestResultVO<Lwm2mObjectInfo> result = new RequestResultVO<Lwm2mObjectInfo>();
		result.setCoapResultCd(response.getCode().toString());
		result.setResultMsg(getGsonBuilder().toJson(response));
		result.setResultData(lwm2mObj);
		return result;
	}
	
	@Override	
	public RequestResultVO<String> requestRead(String format, String endpoint, int objectId, int objectInstanceId){
		String path = getLwm2mPath(objectId, objectInstanceId, -1);
		return this.requestRead(format, endpoint, path);
	}
	
	@Override
	public RequestResultVO<String> requestRead(String format, String endpoint, int objectId, int objectInstanceId, int resourceId) {
		String path = getLwm2mPath(objectId, objectInstanceId, resourceId);
		return this.requestRead(format, endpoint, path);
	}
	
	private RequestResultVO<String> requestRead(String format, String endpoint, String path) {
		LeshanServer server = getLwm2mServerInstace();
		Registration reg = getRegByEndpoint(endpoint);
		ReadResponse response = null;
		try {
			if(format == null){
				format = defaultFormat;
			}
			response = server.send(reg, new ReadRequest(ContentFormat.fromName(format.toUpperCase()), path), timeout);
		} catch (InterruptedException e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
		
		RequestResultVO<String> result = new RequestResultVO<String>();
		result.setCoapResultCd(response.getCode().toString());
		result.setResultMsg(getGsonBuilder().toJson(response));		
		return result;
	}
	
	@Override
	public RequestResultVO<String> requestWrite(String format, String endpoint, int objectId, int objectInstanceId, Lwm2mObjectInfo lwm2mObj) {
		LeshanServer server = getLwm2mServerInstace();
		Registration reg = getRegByEndpoint(endpoint);
		WriteResponse response = null;
		List<LwM2mResource> resources = new ArrayList<LwM2mResource>();
		for(Lwm2mRsourceInfo rs : lwm2mObj.getResources()){
			if(rs.getRscType().equals(Type.TIME)){
				String dateStr = String.valueOf(rs.getRscValue());
				SimpleDateFormat spformat = new SimpleDateFormat(ComCode.DataFormat.DateStrFormat.getValue()); 
				if(!CommonUtil.isEmpty(rs.getRscDtFormat())){
					spformat = new SimpleDateFormat(rs.getRscDtFormat());
				}
				Date date = null;
				try {
					date = spformat.parse(dateStr);
					resources.add(LwM2mSingleResource.newResource(rs.getRscId(), date, rs.getRscType()));
				} catch (ParseException e) {}				
			}else{
				resources.add(LwM2mSingleResource.newResource(rs.getRscId(), rs.getRscValue(), rs.getRscType()));
			}			
		}
		try {
			if(format == null){
				format = defaultFormat;
			}
			response = server.send(reg, new WriteRequest(Mode.REPLACE, ContentFormat.fromName(format.toUpperCase()),objectId, objectInstanceId, resources), timeout);
		} catch (InterruptedException e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
		
		RequestResultVO<String> result = new RequestResultVO<String>();
		result.setCoapResultCd(response.getCode().toString());
		result.setResultMsg(getGsonBuilder().toJson(response));		
		return result;
	}
	

	@Override
	public RequestResultVO<String> requestWrite(String format, String endpoint, int objectId, int objectInstanceId, Lwm2mRsourceInfo resouceInfo) {
		LeshanServer server = getLwm2mServerInstace();
		Registration reg = getRegByEndpoint(endpoint);
		WriteResponse response = null;
		RequestResultVO<String> result = new RequestResultVO<String>();
		try {
			response = server.send(reg, createRequestWrite(format, objectId, objectInstanceId, resouceInfo), timeout);
			result.setCoapResultCd(response.getCode().toString());
			result.setResultMsg(getGsonBuilder().toJson(response));		
		} catch (InterruptedException e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} catch (IllegalArgumentException e1){
			LoggerPrint.printErrorLogExceptionrMsg(logger, e1);
			result.setResultMsg(e1.getMessage());
		} catch (CodecException e2) {
			result.setResultMsg(e2.getMessage());
		} catch (Exception e3){
			LoggerPrint.printErrorLogExceptionrMsg(logger, e3);
		}
		return result;		
	}
	

	private WriteRequest createRequestWrite(String format, int objectId, int objectInstanceId, Lwm2mRsourceInfo resouceInfo) throws Exception{
		int resourceId = resouceInfo.getRscId();
		Type resourceType = resouceInfo.getRscType();
		Object value = resouceInfo.getRscValue();
		//writeTypeValid(resourceType, value);
		if(format == null){
			format = defaultFormat;
		}
		ContentFormat contentformat = ContentFormat.fromName(format.toUpperCase());
		
		switch (resourceType) {
		case STRING:
			return new WriteRequest(contentformat, objectId, objectInstanceId, resourceId, (String) value);
		case BOOLEAN:
			return new WriteRequest(contentformat, objectId, objectInstanceId, resourceId,
					((Boolean) value).booleanValue());
		case INTEGER:
			return new WriteRequest(contentformat, objectId, objectInstanceId, resourceId, (Integer) value);
		case FLOAT:
			return new WriteRequest(contentformat, objectId, objectInstanceId, resourceId, (float) value);
		case TIME:
			String dateStr = String.valueOf(value);
			SimpleDateFormat spformat = new SimpleDateFormat(ComCode.DataFormat.DateStrFormat.getValue()); 
			if(!CommonUtil.isEmpty(resouceInfo.getRscDtFormat())){
				spformat = new SimpleDateFormat(resouceInfo.getRscDtFormat());
			}
			Date date = spformat.parse(dateStr);
			long time = date.getTime();
			//WAKAAMA not supported Leshan format
			contentformat = ContentFormat.TEXT;
			return new WriteRequest(contentformat, objectId, objectInstanceId, resourceId, time);
		default: // default String
			return new WriteRequest(contentformat, objectId, objectInstanceId, resourceId, (String) value);
		}      
	}
	
	public void writeTypeValid(Type type, Object value){
		String doesNotMatchMessage = "Resouce Type Error";
		switch (type) {
        case INTEGER:
            if (!(value instanceof Integer))
                throw new IllegalArgumentException(doesNotMatchMessage);
            break;
        case FLOAT:
            if (!(value instanceof Double))
                throw new IllegalArgumentException(doesNotMatchMessage);
            break;
        case BOOLEAN:
            if (!(value instanceof Boolean))
                throw new IllegalArgumentException(doesNotMatchMessage);
            break;
        case OPAQUE:
            if (!(value instanceof byte[]))
                throw new IllegalArgumentException(doesNotMatchMessage);
            break;
        case STRING:
            if (!(value instanceof String))
                throw new IllegalArgumentException(doesNotMatchMessage);
            break;
        case TIME:
            if (!(value instanceof Date))
                throw new IllegalArgumentException(doesNotMatchMessage);
            break;
        case OBJLNK:
            if (!(value instanceof ObjectLink))
                throw new IllegalArgumentException(doesNotMatchMessage);
            break;
        default:
            throw new IllegalArgumentException(String.format("Type %s is not supported", type.name()));
        }
	}
	
	@Override	
	public RequestResultVO<String> requestDelete(String endpoint, int objectId, int objectInstanceId) {
		LeshanServer server = getLwm2mServerInstace();
		Registration reg = getRegByEndpoint(endpoint);
		DeleteResponse response = null;
		try {
			response = server.send(reg, new DeleteRequest(objectId, objectInstanceId), timeout);
		} catch (InterruptedException e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
		
		RequestResultVO<String> result = new RequestResultVO<String>();
		result.setCoapResultCd(response.getCode().toString());
		result.setResultMsg(getGsonBuilder().toJson(response));		
		return result;
	}
	
	@Override
	public RequestResultVO<ObserveInfoVO> requestObserve(String format, String endpoint, int objectId,
			int objectInstanceId, ObserveInfoVO observeInfo) {
		String path = getLwm2mPath(objectId, objectInstanceId, -1);
		return this.requestObserve(format, endpoint, path, observeInfo);
	}
	
	@Override
	public RequestResultVO<ObserveInfoVO> requestObserve(String format, String endpoint, int objectId,
			int objectInstanceId, int resourceId, ObserveInfoVO observeInfo) {
		String path = getLwm2mPath(objectId, objectInstanceId, resourceId);
		return this.requestObserve(format, endpoint, path, observeInfo);
	}
	
	private RequestResultVO<ObserveInfoVO> requestObserve(String format, String endpoint, String path, ObserveInfoVO observeInfo) {
		LeshanServer server = getLwm2mServerInstace();
		Registration reg = getRegByEndpoint(endpoint);
		ObserveResponse response = null;
		try {
			Map<String, String> context = null;
			if(observeInfo != null && !CommonUtil.isEmpty(observeInfo.getSubURL())){
				context = new HashMap<String, String>();
				context.put(Lwm2mKey.subscriptionURL.getValue(), observeInfo.getSubURL());
			}			
			ObserveRequest observeRequest = null;
			if (format == null) {
				format = defaultFormat;
			}
			if(context == null){
				observeRequest = new ObserveRequest(ContentFormat.fromName(format.toUpperCase()), path);
			}else{
				observeRequest = new ObserveRequest(ContentFormat.fromName(format.toUpperCase()), path, context);
			}
			response = server.send(reg, observeRequest, timeout);			
		} catch (InterruptedException e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		} catch (InvalidRequestException e2) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e2);
		}
		RequestResultVO<ObserveInfoVO> result = new RequestResultVO<ObserveInfoVO>();
		result.setCoapResultCd(response.getCode().toString());
		result.setResultData(observeInfo);
		return result;
	}

	@Override
	public RequestResultVO<ObserveInfoVO> cancelObserve(String format, String endpoint, int objectId,
			int objectInstanceId, ObserveInfoVO observeInfo) {
		String cancelPath = getLwm2mPath(objectId, objectInstanceId, -1);
		return this.cancelObserve(format, endpoint, cancelPath, observeInfo);
	}
	
	@Override
	public RequestResultVO<ObserveInfoVO> cancelObserve(String format, String endpoint, int objectId,
			int objectInstanceId, int resourceId, ObserveInfoVO observeInfo) {
		String cancelPath = getLwm2mPath(objectId, objectInstanceId, resourceId);
		return this.cancelObserve(format, endpoint, cancelPath, observeInfo);
	}
	
	private RequestResultVO<ObserveInfoVO> cancelObserve(String format, String endpoint, String cancelPath, ObserveInfoVO observeInfo){
		LeshanServer server = getLwm2mServerInstace();
		Registration reg = getRegByEndpoint(endpoint);
		
		ObservationService observationService = server.getObservationService();		
		//observation 등록 정보 조회
        //Set<Observation> observations = observationService.getObservations(reg);
		
        int nbCancelled = 0;        
        if(CommonUtil.isEmpty(cancelPath)){
        	nbCancelled = observationService.cancelObservations(reg);
        }else{
        	nbCancelled = observationService.cancelObservations(reg, cancelPath);
        }
        observeInfo.setCancelCnt(String.valueOf(nbCancelled));
        RequestResultVO<ObserveInfoVO> result = new RequestResultVO<ObserveInfoVO>();
		result.setResultData(observeInfo);
		return result;
	}

	@Override
	public RequestResultVO<String> requestExecute(String endpoint, int objectId, int objectInstanceId, int resourceId, String parms) {
		LeshanServer server = getLwm2mServerInstace();
		Registration reg = getRegByEndpoint(endpoint);
		ExecuteResponse response = null;
		try {
			if(parms == null || CommonUtil.isEmpty(parms)){
				response = server.send(reg, new ExecuteRequest(objectId, objectInstanceId, resourceId), timeout);
			}else{
				response = server.send(reg, new ExecuteRequest(objectId, objectInstanceId, resourceId, parms), timeout);
			}			
		} catch (InterruptedException e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
		
		RequestResultVO<String> result = new RequestResultVO<String>();
		result.setCoapResultCd(response.getCode().toString());
		result.setResultMsg(getGsonBuilder().toJson(response));		
		return result;
	}
}
