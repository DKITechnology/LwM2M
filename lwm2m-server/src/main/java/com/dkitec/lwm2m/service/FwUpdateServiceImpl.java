package com.dkitec.lwm2m.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dkitec.lwm2m.common.code.ComCode;
import com.dkitec.lwm2m.common.util.CommonUtil;
import com.dkitec.lwm2m.common.util.LoggerPrint;
import com.dkitec.lwm2m.common.util.PageDateParmVO;
import com.dkitec.lwm2m.dao.FwUpdateDao;
import com.dkitec.lwm2m.domain.FwUpdateReqInfoVO;
import com.dkitec.lwm2m.domain.FwUpdateReqVO;
import com.dkitec.lwm2m.domain.FwUpdateVO;
import com.dkitec.lwm2m.service.intf.FwUpdateService;
import com.google.gson.Gson;

@Service
public class FwUpdateServiceImpl implements FwUpdateService {

	Logger logger = LoggerFactory.getLogger(FwUpdateServiceImpl.class);
	
	@Autowired
	FwUpdateDao fwUpdateDao;
	
	@Value("#{serverConfigProp['fwUpdate.binary.cnt']}")
	private int pkgLimitCnt;
	
	private static Map<String, FwUpdateReqVO> fwPkgMap;
	
	@PostConstruct
    public void selectFwFile() {
		try {
			List<FwUpdateReqVO> fwPkgList = fwUpdateDao.selectFwPkgs(pkgLimitCnt);
			if(fwPkgList != null){
				fwPkgMap = new HashMap<String, FwUpdateReqVO>();
				for(FwUpdateReqVO fwvo : fwPkgList){
					fwPkgMap.put(fwvo.getFiwrId(), fwvo);
				}
			}
		} catch (Exception e) { logger.error(e.getMessage());}
    }
	
	@Transactional(rollbackFor = Exception.class)
	@Override
	public String reqFirmwareUpgradeSave(FwUpdateVO fwvo) {
		if(fwvo.getReqDatmStr() == null || fwvo.getReqDatmStr().equals("")){
			String now = CommonUtil.getNow(ComCode.DataFormat.DefaultDateStrFormat.getValue());
			fwvo.setReqDatmStr(now);
		}			
		if(fwUpdateDao.reqFirmwareUpgradeSave(fwvo) > 0){
			if(fwvo.getDeviceIds() != null)
				fwUpdateDao.reqfwDevcSave(fwvo);
			return "success";
		}else{
			return "fail";
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public String reqFirmwareUpgradeUpdate(FwUpdateVO fwvo) throws Exception {
		FwUpdateVO fwupdate = fwUpdateDao.selectFwUpgradeResultDetail(fwvo.getReqId());
		if(fwupdate.getProcCd().equals(ComCode.ProcessCode.NOTSTART.getValue())){
			if(fwUpdateDao.reqFirmwareUpgradeUpdate(fwvo) >= 0){
				fwUpdateDao.reqfwDevicDelete(fwvo.getReqId());
				if(fwvo.getDeviceIds() != null){				
					fwUpdateDao.reqfwDevcSave(fwvo);
				}
				return "success";
			}else{
				return "fail";
			}
		}else{
			throw new Exception("Process alerady started.");
		}		
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public String reqFirmwareUpgradeUpdateCancel(String requestID) throws Exception {
		FwUpdateVO vo = fwUpdateDao.selectFwUpgradeResultDetail(requestID);
		if(vo.getProcCd().equals(ComCode.ProcessCode.NOTSTART.getValue())){
			fwUpdateDao.reqfwDevicDelete(requestID);
			if(fwUpdateDao.reqFirmwareUpgradeUpdateCancel(requestID) >= 0){
				return "success";
			}else{
				return "fail";
			}			
		}else{
			throw new Exception("Process alerady started.");
		}
	}

	@Override
	public List<FwUpdateReqInfoVO> selectFwUpgradeList(PageDateParmVO fwvo) {
		return fwUpdateDao.selectFwUpgradeList(fwvo);
	}
	
	@Override
	public int selectFwUpgradeListCnt(PageDateParmVO fwvo) {
		return fwUpdateDao.selectFwUpgradeListCnt(fwvo);
	}

	@Override
	public FwUpdateVO selectFwUpgradeResultDetail(String requestID) {
		return fwUpdateDao.selectFwUpgradeResultDetail(requestID);
	}

	@Override
	public List<FwUpdateReqVO> selectFwReqList() {
		List<FwUpdateReqVO> requestList = new ArrayList<FwUpdateReqVO>();
		try {
			requestList = fwUpdateDao.selectFwReqList();			
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
		return requestList;
	}
	
	@Override
	public void updateRwReqStat(List<FwUpdateReqVO> fwlist) {
		try {
			fwUpdateDao.updateFwReqStat(fwlist);
		} catch (Exception e) {
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}		
	}
	
	@Override
	public void firmwareFileDownload(String firmwrId, HttpServletResponse res) {
		try {
			FwUpdateReqVO firmware = null;
			if(fwPkgMap != null && fwPkgMap.get(firmwrId) != null){
				firmware = fwPkgMap.get(firmwrId);
			}else{
				firmware = fwUpdateDao.selectFwInfo(firmwrId);
			}			
			if(firmware != null){
				String fileName = firmware.getDevcModlCd() + "_"
						+ ((CommonUtil.isEmpty(firmware.getBfFiwrVer())) ? "" : firmware.getBfFiwrVer() + "_")
						+ firmware.getAfFiwrVer();
				ByteArrayInputStream bis = new ByteArrayInputStream(firmware.getFiwrPkg());
				res.setHeader("Content-Disposition", "attachment; filename=\""
						 + new String((fileName).getBytes("UTF-8"),"8859_1")+"\"");
				res.setHeader("Content-Type", "application/octet-stream");
				res.setContentLength((int)firmware.getFiwrPkg().length);
				IOUtils.copy(bis, res.getOutputStream());
				res.setStatus(HttpServletResponse.SC_OK);
			}else{
				res.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}				
		} catch (Exception e) {
			res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			LoggerPrint.printErrorLogExceptionrMsg(logger, e);
		}
	}
}
