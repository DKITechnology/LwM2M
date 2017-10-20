package com.dkitec.argosiot.commonapi;

public interface CommonApiCode {
	public static final String ERROR_UNAUTHORIZED_CODE = "401"; 
	public static final String ERROR_NOTFOUND_CODE = "404";
	public static final String ERROR_METHODNOTALLOWED_CODE = "405";
	public static final String ERROR_INTERNALSERVERERROR_CODE = "500";
	public static final String ERROR_NOSERVICE_CODE = "1001";
	public static final String ERROR_NOUSER_CODE = "1002";
	public static final String ERROR_FIELDVALIDATION_CODE = "1003";
	public static final String ERROR_NODATAFOUND_CODE = "1004";
	public static final String ERROR_NODEVICE_CODE = "1005";
	public static final String ERROR_NOIDMISSCODE_CODE = "1006";
	public static final String ERROR_DUPLICATE_CODE = "1007";
	public static final String ERROR_NOSERVICEREGUSER_CODE = "1008";
	public static final String ERROR_INVALIDPASSWORD_CODE = "1009";
	public static final String ERROR_NOAUTH_CODE = "1010";
	public static final String ERROR_INVALIDTOKEN_CODE = "1011";
	public static final String ERROR_EXPIREDTOKEN_CODE = "1012";
	public static final String ERROR_CONTROLFAIL_CODE = "1013";
	public static final String ERROR_CONTROLTIMEOUT_CODE = "1014";
	public static final String ERROR_ETC_CODE = "9999";
}
