package com.ks0100.wp.service;

import java.util.Map;

import com.ks0100.wp.ShiroUser;

public interface CommonForMobileService {
	String decrypt(String enCryptStr,String mobileSessionId);
	String encrypt(String str,String mobileSessionId);
	ShiroUser getShiroUser(String MOBILESESSIONID);
	Map<String, Object> successResponseMapForMobile(Object data,String MOBILESESSIONID) throws Exception;
	String getDecryptStr(String str,String MOBILESESSIONID) throws Exception;
}
