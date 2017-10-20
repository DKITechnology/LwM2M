package com.dkitec.lwm2m.common.util;

import org.slf4j.Logger;

public class LoggerPrint {

	public static void printInfoLogMsg(Logger logger, String msg){
		logger.info(msg);
	}
	
	public static void printErrorLogMsg(Logger logger, String msg){
		logger.error(msg);
	}
	
	public static void printErrorLogExceptionrMsg(Logger logger, Exception e){
		logger.error(e.getMessage(), e);
	}
	public static void printErrorLogExceptionrMsg(Logger logger, Exception e, String prefixMsg){
		logger.error("["+prefixMsg+"]"+e.getMessage(), e);
	}
}
