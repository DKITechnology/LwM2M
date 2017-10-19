package com.dkitec.lwm2m.common.exception;

public class AuthException extends Exception{
	
	public AuthException(String msg){
		super("[AuthException] "+msg);
	}
}
