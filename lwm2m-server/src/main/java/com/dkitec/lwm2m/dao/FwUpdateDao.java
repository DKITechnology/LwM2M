package com.dkitec.lwm2m.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.dkitec.lwm2m.common.util.PageDateParmVO;
import com.dkitec.lwm2m.domain.FwUpdateReqInfoVO;
import com.dkitec.lwm2m.domain.FwUpdateReqVO;
import com.dkitec.lwm2m.domain.FwUpdateResultVO;
import com.dkitec.lwm2m.domain.FwUpdateVO;

@Repository
public interface FwUpdateDao {

	/**
	 * 펌웨어 업그레이드 요청 저장
	 * @param fwvo
	 * @return
	 */
	public int reqFirmwareUpgradeSave(FwUpdateVO fwvo);
	
	/**
	 * 펌웨어 업그레이드 요청 수정
	 * @param fwvo
	 * @return
	 */
	public int reqFirmwareUpgradeUpdate(FwUpdateVO fwvo);
	
	/**
	 * 펌웨어 업그레이드 요청 취소
	 * @param requestID
	 * @return
	 */
	public int reqFirmwareUpgradeUpdateCancel(String requestID);
	
	/**
	 * 펌웨어 업그레이드 요청 목록 조회
	 * @param fwvo
	 * @return
	 */
	public List<FwUpdateReqInfoVO> selectFwUpgradeList(PageDateParmVO fwvo);
	
	/**
	 * 펌웨어 업그레이드 요청 목록 조회
	 * @param fwvo
	 * @return
	 */
	public int selectFwUpgradeListCnt(PageDateParmVO fwvo);
	
	/**
	 * 펌웨어 업그레이드 요청 결과 조회
	 * @param requestID
	 * @return
	 */
	public FwUpdateVO selectFwUpgradeResultDetail(String requestID);
	
	/**
	 * 펌웨어 업그레이드 요청 단말 정보 저장
	 * @param fwvo
	 * @return
	 */
	public int reqfwDevcSave(FwUpdateVO fwvo);
	
	/**
	 * 펌웨어 업그레이드 요청 단말 정보 삭제
	 * @param fiwrId
	 * @return
	 */
	public int reqfwDevicDelete(String fiwrId);	
	
	/**
	 * 펌웨어 스케줄링 목록 조회
	 * @return
	 */
	public List<FwUpdateReqVO> selectFwReqList();
	
	/**
	 * 펌웨어 요청 상태 정보를 수정한다. 
	 * (요청 시작)
	 * @return
	 */
	public int updateFwReqStat(List<FwUpdateReqVO> requestList);
	
	/**
	 * 단말의 펌웨어 상태 정보를 업데이트 한다.
	 * @param clientResults
	 */
	public int updateFwClientResult(FwUpdateResultVO clientResult);
	
	/**
	 * 펌웨어 다운로드 실패 정보를 업데이트 한다.
	 * @param clientResults
	 * @return
	 */
	public int updateFwClientFailResult(List<FwUpdateResultVO> clientResults);

	/**
	 * 펌웨어 정보를 조회한다.
	 * @param firmwrId
	 * @return
	 */
	public FwUpdateReqVO selectFwInfo(String firmwrId);
	
	public List<FwUpdateReqVO> selectFwPkgs(int pkgLimitCnt);
}
