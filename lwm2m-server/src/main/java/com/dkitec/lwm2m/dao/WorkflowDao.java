package com.dkitec.lwm2m.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.dkitec.lwm2m.domain.workflow.WorkflowInfoVO;

@Repository
public interface WorkflowDao {

	public List<WorkflowInfoVO> selectWkByApi(int apiSerno);
}
