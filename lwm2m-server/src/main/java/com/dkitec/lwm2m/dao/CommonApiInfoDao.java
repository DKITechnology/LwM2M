package com.dkitec.lwm2m.dao;

import org.springframework.stereotype.Repository;

import com.dkitec.argosiot.commonapi.domain.CommonApiInfo;


@Repository
public interface CommonApiInfoDao {

	/**
	 * <b>메서드 설명</b> 	: Common Api 조회
	 * @param CommonApiInfo
	 * @return CommonApiInfo
	 */
	CommonApiInfo selectCommonApi(CommonApiInfo apiInfo);
	
}
