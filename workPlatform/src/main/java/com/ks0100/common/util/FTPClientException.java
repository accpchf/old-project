package com.ks0100.common.util;

import org.apache.log4j.Logger;

public class FTPClientException extends Exception{

	public static final Logger LOGGER = Logger.getLogger(FTPClientException.class);
	
	public FTPClientException(String expStr,Exception ex){
		LOGGER.error(expStr, ex);
	}
	
	public FTPClientException(String expStr){
		LOGGER.error(expStr);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -6572650866459283753L;



}
