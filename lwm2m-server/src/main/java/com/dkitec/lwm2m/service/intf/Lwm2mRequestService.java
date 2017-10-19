package com.dkitec.lwm2m.service.intf;

import com.dkitec.lwm2m.domain.Lwm2mObjectInfo;
import com.dkitec.lwm2m.domain.Lwm2mRsourceInfo;
import com.dkitec.lwm2m.domain.ObserveInfoVO;
import com.dkitec.lwm2m.domain.RequestResultVO;

public interface Lwm2mRequestService {
	
	/**
	 * Lwm2m Create 요청
	 * @param format
	 * @param endpoint
	 * @param objectId
	 * @param lwm2mObjInstance
	 * @return
	 */
	RequestResultVO<Lwm2mObjectInfo> requestCreate(String format, String endpoint, int objectId, Lwm2mObjectInfo lwm2mObjInstance);
	
	/**
	 * Lwm2m Read 요청
	 * @param format
	 * @param endpoint
	 * @param objectId
	 * @param objectInstanceId
	 * @return
	 */
	RequestResultVO<String> requestRead(String format, String endpoint, int objectId, int objectInstanceId);
	
	/**
	 * Lwm2m Read 요청
	 * @param format
	 * @param endpoint
	 * @param objectId
	 * @param objectInstanceId
	 * @param resourceId
	 * @return
	 */
	RequestResultVO<String> requestRead(String format, String endpoint, int objectId, int objectInstanceId, int resourceId);
	
	/**
	 * Lwm2m Write 요청
	 * @param format
	 * @param endpoint
	 * @param objectId
	 * @param objectInstanceId
	 * @return
	 */
	RequestResultVO<String> requestWrite(String format, String endpoint, int objectId, int objectInstanceId, Lwm2mObjectInfo lwm2mObj);
	
	/**
	 * Lwm2m Write 요청
	 * @param format
	 * @param endpoint
	 * @param objectId
	 * @param objectInstanceId
	 * @param resouceInfo
	 * @return
	 */
	RequestResultVO<String> requestWrite(String format, String endpoint, int objectId, int objectInstanceId, Lwm2mRsourceInfo resouceInfo);
	
	/**
	 * Lwm2m Delete 요청
	 * @param endpoint
	 * @param objectId
	 * @param objectInstanceId
	 * @return
	 */
	RequestResultVO<String> requestDelete(String endpoint, int objectId, int objectInstanceId);
		
	/**
	 * Lwm2m Observe 요청
	 * @param format
	 * @param endpoint
	 * @param objectId
	 * @param objectInstanceId
	 * @param observeInfo
	 * @return
	 */
	RequestResultVO<ObserveInfoVO> requestObserve(String format, String endpoint, int objectId, int objectInstanceId, ObserveInfoVO observeInfo);
	
	/**
	 * Lwm2m Observe 요청
	 * @param format
	 * @param endpoint
	 * @param objectId
	 * @param objectInstanceId
	 * @param resourceId
	 * @param observeInfo
	 * @return
	 */
	RequestResultVO<ObserveInfoVO> requestObserve(String format, String endpoint, int objectId, int objectInstanceId, int resourceId, ObserveInfoVO observeInfo);
	
	/**
	 * Lwm2m Observe 요청 취소
	 * @param format
	 * @param endpoint
	 * @param objectId
	 * @param objectInstanceId
	 * @param observeInfo
	 * @return
	 */
	RequestResultVO<ObserveInfoVO> cancelObserve(String format, String endpoint, int objectId, int objectInstanceId, ObserveInfoVO observeInfo);
	
	/**
	 * Lwm2m Observe 요청 취소
	 * @param format
	 * @param endpoint
	 * @param objectId
	 * @param objectInstanceId
	 * @param resourceId
	 * @param observeInfo
	 * @return
	 */
	RequestResultVO<ObserveInfoVO> cancelObserve(String format, String endpoint, int objectId, int objectInstanceId, int resourceId, ObserveInfoVO observeInfo);
	
	/**
	 * Lwm2m Execute
	 * @param endpoint
	 * @param objectId
	 * @param objectInstanceId
	 * @param resourceId
	 * @param parm
	 * @param value
	 * @return
	 */
	public RequestResultVO<String> requestExecute(String endpoint, int objectId, int objectInstanceId, int resourceId, String parms);
}
