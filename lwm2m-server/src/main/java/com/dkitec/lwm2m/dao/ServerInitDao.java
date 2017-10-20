package com.dkitec.lwm2m.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.dkitec.lwm2m.domain.ObjectModelInfoVO;

@Repository
public interface ServerInitDao {

	public void insertDefaultModelInfo();
	
	public List<ObjectModelInfoVO> selectAdditionObjects();
}
