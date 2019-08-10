package com.ks0100.wp.service.impl;

import java.net.URLDecoder;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.common.ehcache.CacheUtil;
import com.ks0100.common.util.coder.Coder;
import com.ks0100.common.util.coder.DHCoder;
import com.ks0100.wp.MobileUser;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.service.CommonForMobileService;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

@Service
public class CommonForMobileServiceImpl implements CommonForMobileService {

	private Logger logger = LoggerFactory.getLogger(UserByMobileServiceImpl.class);
	private static String ERROR_AND_RE_LOAD = "ERROR_AND_RE_LOAD";// 登录验证通过后，手机请求后不能正常的返回数据
	private static boolean HAS_CRYPT = true;

	public String decrypt(String enCryptStr, String mobileSessionId) {
		logger.info("enCryptStr:" + enCryptStr);
		logger.info("mobileSessionId:" + mobileSessionId);
		String decryptStr = "";
		Cache sample = (Cache) CacheUtil.getEhcahce();
		Element element = sample.get(mobileSessionId);
		if (element != null) {
			MobileUser mu = (MobileUser) element.getObjectValue();
			try {
				enCryptStr = enCryptStr.replaceAll("_", "/");
				enCryptStr = enCryptStr.replaceAll("-", "+");
				logger.info("serviceByte:" + Coder.encryptBASE64(mu.getUserByMobile().getServiceKeys()));
				byte[] bytes = Coder.decryptBASE64(enCryptStr);
				decryptStr = new String(
						DHCoder.decrypt(bytes, mu.getUserByMobile().getServiceKeys(), BusinessConstant.IV));
				decryptStr = decryptStr.trim();
				logger.info("decryptStr:[" + decryptStr + "]");
			} catch (Exception e) {
				logger.error(enCryptStr + " 解密失败");
			}
		}
		return decryptStr;
	}

	public String encrypt(String str, String mobileSessionId) {
		String encryptStr = "";
		Cache sample = (Cache) CacheUtil.getEhcahce();
		Element element = sample.get(mobileSessionId);
		if (element != null) {
			MobileUser mu = (MobileUser) element.getObjectValue();
			try {
				byte[] out = DHCoder.encrypt(str.getBytes(), mu.getUserByMobile().getServiceKeys(),
						BusinessConstant.IV);
				encryptStr = Coder.encryptBASE64(out);
			} catch (Exception e) {
				logger.error(str + " 加密失败");
			}
		}
		return encryptStr;
	}

	public ShiroUser getShiroUser(String MOBILESESSIONID) {
		Cache sample = (Cache) CacheUtil.getEhcahce();
		Element element = sample.get(MOBILESESSIONID);
		if (element != null) {
			MobileUser mu = (MobileUser) element.getObjectValue();
			return mu.getShiroUser();
		}
		return null;
	}

	public Map<String, Object> successResponseMapForMobile(Object data, String MOBILESESSIONID) {
		ObjectMapper om = new ObjectMapper();
		String jsonStr = null;
		try {
			jsonStr = om.writeValueAsString(data);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		logger.info("successResponseMapForMobile ----MOBILESESSIONID:" + MOBILESESSIONID);
		logger.info("---jsonStr:" + jsonStr);
		String encrypt_jsonStr = encrypt(jsonStr, MOBILESESSIONID);
		logger.info("-----encrypt_jsonStr:" + encrypt_jsonStr);
		if (StringUtils.isNotEmpty(encrypt_jsonStr)) {
			return ResultDataJsonUtils.errorResponseResult(ERROR_AND_RE_LOAD);
		}
		return ResultDataJsonUtils.successResponseResult(encrypt_jsonStr);
	}

	public String getDecryptStr(String str, String MOBILESESSIONID) throws Exception {
		if (HAS_CRYPT) {
			str = URLDecoder.decode(str, "utf-8");
			return decrypt(str, MOBILESESSIONID);
		} else {
			return str;
		}

	}

}
