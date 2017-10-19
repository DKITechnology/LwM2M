package com.dkitec.lwm2m.service.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dkitec.lwm2m.common.code.ComCode;
import com.dkitec.lwm2m.dao.WorkflowDao;
import com.dkitec.lwm2m.domain.FwUpdateReqVO;
import com.dkitec.lwm2m.domain.FwUpdateVO;
import com.dkitec.lwm2m.domain.workflow.WorkflowInfoVO;

/**
 * 센서 데이터를 처리 하는 work flow Service
 *
 */
@Service
public class WorkFlowServiceImpl implements WorkFlowService{
	
	static final Logger logger = LoggerFactory.getLogger(WorkFlowService.class);
	
	private String FW_WK_ID = "fwUpdateEvt"; 
	
	@Autowired
	RuntimeService runtimeService;
	
	@Autowired
	WorkflowDao workflowDao;
	
	@Override
	public void callWorkflowFarmware(List<FwUpdateReqVO> fwupdates){
		Map<String, Object> variableMap = new HashMap<String, Object>();		
		//variableMap.put(key, value);
		List<String> workflowIds = new ArrayList<String>(Arrays.asList(new String[]{FW_WK_ID}));
		variableMap.put(ComCode.VariableKey.FwUpdateValues.getValue(), fwupdates);
		startEventProcess(workflowIds, variableMap);
	}
	
	@Override
	public void callWorkFlowByApi(int apiSerno, Map<String, Object> variableMap){
		try {
			/**common variableMap**/
			if(variableMap == null)
				variableMap = new HashMap<String, Object>();
			List<WorkflowInfoVO> workflowInfo = workflowDao.selectWkByApi(apiSerno);
			List<String> workflowIds = new ArrayList<String>();
			if(workflowInfo != null && workflowInfo.size() > 0){
				for(WorkflowInfoVO wk : workflowInfo){
					workflowIds.add(wk.getWkId());
				}
				startEventProcess(workflowIds, variableMap);
			}			
		} catch (Exception e) {
			logger.error(""+e.getMessage(),e);
		}
	}
	
	public void startEventProcess(List<String> workflowIds, Map<String, Object> variableMap) {
		try {
			if(workflowIds == null || workflowIds.size() == 0){
				logger.info("No Strat Event process ID.");
			}else{
				for(String workflowId : workflowIds){
					try {
						ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(workflowId, variableMap);	
					} catch (Exception e) {
						logger.error("Event Process Exception error :"+e.getMessage(), e);
					}
				}
			}
		} catch (Exception e) {
			logger.info("Start Event Process Error : "+e.getMessage(),e);
		}
	}
}
