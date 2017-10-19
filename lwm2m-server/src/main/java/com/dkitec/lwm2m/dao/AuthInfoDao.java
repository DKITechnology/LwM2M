package com.dkitec.lwm2m.dao;

import org.springframework.stereotype.Repository;

import com.dkitec.lwm2m.domain.AuthInfo;

@Repository
public interface AuthInfoDao {

	public AuthInfo selectInfAEAuth(String authId);
	
	public AuthInfo selectAdminAuth(String authId); 
}
