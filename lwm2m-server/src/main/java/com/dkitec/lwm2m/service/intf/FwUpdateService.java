package com.dkitec.lwm2m.service.intf;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.dkitec.lwm2m.common.util.PageDateParmVO;
import com.dkitec.lwm2m.domain.FwUpdateReqInfoVO;
import com.dkitec.lwm2m.domain.FwUpdateReqVO;
import com.dkitec.lwm2m.domain.FwUpdateVO;

public interface FwUpdateService {

	/**
	 * 펌웨어 업그레이드 요청 저장
	 * @param fwvo
	 * @return
	 */
	public String reqFirmwareUpgradeSave(FwUpdateVO fwvo);
	
	/**
	 * 펌웨어 업그레이드 요청 수정
	 * @param fwvo
	 * @return
	 */
	public String reqFirmwareUpgradeUpdate(FwUpdateVO fwvo) throws Exception;
	
	/**
	 * 펌웨어 업그레이드 요청 취소
	 * @param requestID
	 * @return
	 */
	public String reqFirmwareUpgradeUpdateCancel(String requestID) throws Exception;
	
	/**
	 * 펌웨어 업그레이드 요청 목록 조회
	 * @param fwvo
	 * @return
	 */
	public List<FwUpdateReqInfoVO> selectFwUpgradeList(PageDateParmVO fwvo);
	
	/**
	 * 펌웨어 업그레이드 요청 목록 조회 건수
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
	 * 펌웨어 업데이트 실행이 필요한 정보를 조회한다.
	 * @return
	 */
	public List<FwUpdateReqVO> selectFwReqList();
	
	/**
	 * 업데이트가 실행된 펌웨어 요청 정보 상태를 업데이트 한다.
	 * @param fwlist
	 */
	public void updateRwReqStat(List<FwUpdateReqVO> fwlist);
	
	/**
	 * 펌웨어 파일을 다운로드 받는다.
	 * @param firmwrId 펌웨어 아이디
	 */
	public void firmwareFileDownload(String firmwrId, HttpServletResponse res);
}
