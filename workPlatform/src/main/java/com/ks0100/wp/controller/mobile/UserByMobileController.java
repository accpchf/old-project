package com.ks0100.wp.controller.mobile;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.crypto.interfaces.DHPublicKey;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.common.ehcache.CacheUtil;
import com.ks0100.common.util.coder.Coder;
import com.ks0100.common.util.coder.DHCoder;
import com.ks0100.wp.MobileUser;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.entity.UserByMobile;
import com.ks0100.wp.service.CommonForMobileService;
import com.ks0100.wp.service.LoginService;
import com.ks0100.wp.service.ProjectService;
import com.ks0100.wp.service.UserByMobileService;

@Controller
@RequestMapping("/userByMobile")
public class UserByMobileController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private LoginService loginService;
	@Autowired
	private UserByMobileService userByMobileService;
	@Autowired
	private CommonForMobileService commonForMobileService;
	@Autowired
	public ProjectService projectService;
	
	
	private static String NO_ACCOUNT="NO_ACCOUNT";//用户名输入错误
	private static String RE_LOAD_SERVICE_KEY="RE_LOAD_SERVICE_KEY";//错误，重新发送服务器公钥
	private static String RETURN_LOGIN_RANDOM="RETURN_LOGIN_RANDOM";//session过期，返回随机数
	private static String ERROR_AND_RE_LOAD="ERROR_AND_RE_LOAD";//登录验证通过后，手机请求后不能正常的返回数据
	private static String LOGIN_ERROR="LOGIN_ERROR";//登录失败，用户名密码不匹配
	
	
	/**
	 * 根据手机公钥产生服务器密钥对和登录随机数，并且返回
	 * @param account
	 * @param mobilePublicKey
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/loadServiceKey/{account}/{mobilePublicKey}", method = RequestMethod.GET)
	public  Map<String, Object> loadServiceKey(@PathVariable("account")String account,
			@PathVariable("mobilePublicKey")String mobilePublicKey)throws Exception {
		logger.info("account:"+account);
		logger.info("mobilePublicKey:"+mobilePublicKey);
		User user = loginService.findUserByAccount(account);
		if(user == null) return ResultDataJsonUtils.errorResponseResult(NO_ACCOUNT);
			
		DHCoder serviceDH=new DHCoder();
		serviceDH.initWithPG();
		byte[] serviceKeysByte=serviceDH.genSecretKey(mobilePublicKey);
		String loginRandom=UUID.randomUUID().toString();
        
        DHPublicKey servicePubkey = (DHPublicKey)serviceDH.getKeyPair().getPublic();
        String servicePublicKey=servicePubkey.getY().toString(16);
        
		UserByMobile um=new UserByMobile();
		um.setUserId(user.getUserId());
		um.setServiceKeys(serviceKeysByte);
		
		um.setLoginRandom(loginRandom);
	//	um.setServicePublicKey(servicePublicKey);
		userByMobileService.save(um);
        logger.info("serviceByte:"+Coder.encryptBASE64(serviceKeysByte));
       
     //   logger.info("给手机的key:"+servicePublicKey);
        
        Map<String,String> map=new HashMap<String,String>();
        map.put("servicePublicKey", servicePublicKey);
        map.put("loginRandom", loginRandom);
        logger.info("servicePublicKey:"+servicePublicKey+";loginRandom:"+loginRandom);
    	return ResultDataJsonUtils.successResponseResult(map);
	}
	
	/**
	 * 告知mobileSessionId过期，并且返回新的随机数
	 * @param account
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/sessionInvlid/{account}")
	public  Map<String, Object> sessionInvlid(@PathVariable("account")String account)throws Exception{
		System.out.println("告知mobileSessionId过期，并且返回新的随机数");
		UserByMobile um=userByMobileService.findUserByMobileByAccount(account);
		String loginRandom=UUID.randomUUID().toString();
		um.setLoginRandom(loginRandom);
		userByMobileService.save(um);
		return ResultDataJsonUtils.errorResponseResultData(RETURN_LOGIN_RANDOM,loginRandom);
	}
	
	
	/**
	 * 处理登录请求，成功后返回sessionid
	 * 
	 * @param user
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/loginIn/{account}/{jsonStr}", method = RequestMethod.GET)
	public Map<String, Object> loginIn(@PathVariable("account")String account,
			@PathVariable("jsonStr")String jsonStr) throws Exception{
		logger.info("jsonStr:"+jsonStr);
		//把json字符串中'_'替换回'/'
		jsonStr=jsonStr.replaceAll("_", "/");
		jsonStr=jsonStr.replaceAll("-", "+");
		jsonStr=jsonStr.replace("!", "\\");
		//根据用户名取出密钥对serviceKeys,如果取不出，返回失败，要求客户端重新发起生成密钥对。
		UserByMobile um=userByMobileService.findUserByMobileByAccount(account);
		if(um==null||um.getServiceKeys()==null||um.getServiceKeys().length==0) 
			return ResultDataJsonUtils.errorResponseResult(RE_LOAD_SERVICE_KEY);
		
		//logger.info("serviceByte:"+Coder.encryptBASE64(um.getServiceKeys()));
		logger.info("jsonStr:"+jsonStr);
		//json字符串转换成map
		ObjectMapper om=new ObjectMapper();
		@SuppressWarnings("unchecked")
		Map<String,String> maps=om.readValue(jsonStr, Map.class);
		String loginRandom=maps.get("loginRandom");
		String password=maps.get("password");//密码需要手机端用HMAC加密，key是account字符串转base64。
		logger.info("ServiceKeys:"+Coder.encryptBASE64(um.getServiceKeys()));
		//对随机数和密码dh解密
		loginRandom=new String(DHCoder.decrypt(Coder.decryptBASE64(loginRandom), um.getServiceKeys(), BusinessConstant.IV));
		byte[] password_byte=DHCoder.decrypt(Coder.decryptBASE64(password), um.getServiceKeys(), BusinessConstant.IV);
		logger.info("loginRandom："+loginRandom);

		//logger.info("dh解密后的password new String()："+new String(password_byte));
		logger.info("dh解密后的password encryptBASE："+Coder.encryptBASE64(password_byte));
		logger.info("dh解密后的password new String()："+new String(password_byte));

		
		//如果随机数不对，返回失败，要求客户端重新发起生成密钥对。
		if(StringUtils.isBlank(loginRandom)||!um.getLoginRandom().equals(loginRandom.trim()))
			return ResultDataJsonUtils.errorResponseResult(RE_LOAD_SERVICE_KEY);
		logger.info("开始登录------------------");
		User user1 = loginService.findUserByAccount(account);
		String password_cipherText ="";
		if (user1 != null) {
			// 组合username,两次迭代，对密码进行加密
			password=Coder.bytesToHexString(Coder.decryptBASE64(new String(password_byte)));
			password_cipherText = new Md5Hash(password, account + user1.getSalt(), 2).toBase64();
			logger.info("password_cipherText------------------:"+password_cipherText);
		}
		// 收集身份/凭据信息
		AuthenticationToken token = new UsernamePasswordToken(account, password_cipherText.toCharArray());
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			subject.logout();
		}
		try {
			if (!subject.isAuthenticated()) {
				// 提交身份/凭据信息
				subject.login(token);
			}
		} catch (UnknownAccountException e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(LOGIN_ERROR);
		} catch (IncorrectCredentialsException e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(LOGIN_ERROR);
		} catch (Exception e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(LOGIN_ERROR);
		}
		subject.hasRole("system");
		logger.info("登录成功------------------");
		//登录成功后返回加密的sessionid
		String sessionid=subject.getSession().getId().toString();
		logger.info("sessionid:"+sessionid);
	//	byte[] out = DHCoder.encrypt(sessionid.getBytes(),um.getServiceKeys(), BusinessConstant.IV);
	  //  String sessionidEncry=Coder.encryptBASE64(out);
	  //  logger.info("sessionidEncry:"+sessionidEncry);
		// 获取ehcache配置文件中的一个cache
		Cache sample =(Cache)CacheUtil.getEhcahce();
		MobileUser mu=new MobileUser();
		mu.setShiroUser((ShiroUser)SecurityUtils.getSubject().getPrincipal());
		mu.setUserByMobile(um);
		Element element = new Element(sessionid, mu);
		sample.put(element);
		return ResultDataJsonUtils.successResponseResult(sessionid);
	}
	
	@ResponseBody
	@RequestMapping(value = "/loginInTest/{account}", method = RequestMethod.GET)
	public Map<String, Object> loginInTest(@PathVariable("account")String account) throws Exception{
		
		//根据用户名取出密钥对serviceKeys,如果取不出，返回失败，要求客户端重新发起生成密钥对。
		UserByMobile um=userByMobileService.findUserByMobileByAccount(account);
		if(um==null||um.getServiceKeys()==null||um.getServiceKeys().length==0) 
			return ResultDataJsonUtils.errorResponseResult(RE_LOAD_SERVICE_KEY);		
		
		User user1 = loginService.findUserByAccount(account);
		
		// 收集身份/凭据信息
		AuthenticationToken token = new UsernamePasswordToken(account, user1.getPassword().toCharArray());
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			subject.logout();
		}
		try {
			if (!subject.isAuthenticated()) {
				// 提交身份/凭据信息
				subject.login(token);
			}
		} catch (UnknownAccountException e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(LOGIN_ERROR);
		} catch (IncorrectCredentialsException e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(LOGIN_ERROR);
		} catch (Exception e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(LOGIN_ERROR);
		}
		subject.hasRole("system");
		logger.info("登录成功------------------");
		//登录成功后返回加密的sessionid
		String sessionid=subject.getSession().getId().toString();
		logger.info("sessionid:"+sessionid);
	//	byte[] out = DHCoder.encrypt(sessionid.getBytes(),um.getServiceKeys(), BusinessConstant.IV);
	  //  String sessionidEncry=Coder.encryptBASE64(out);
	  //  logger.info("sessionidEncry:"+sessionidEncry);
		// 获取ehcache配置文件中的一个cache
		Cache sample =(Cache)CacheUtil.getEhcahce();
		MobileUser mu=new MobileUser();
		mu.setShiroUser((ShiroUser)SecurityUtils.getSubject().getPrincipal());
		mu.setUserByMobile(um);
		Element element = new Element(sessionid, mu);
		sample.put(element);
		return ResultDataJsonUtils.successResponseResult(sessionid);
	}
	
	
	/**
	 * 根据类型和id查找图片
	 * logoType 1代表项目 2代表用户 3代表任务执行者
	 * @param id
	 * @param MOBILESESSIONID
	 * @param logoType
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/findLogo")
	public void findLogo(String id,String MOBILESESSIONID,String logoType,HttpServletResponse response) throws Exception{
		id = commonForMobileService.getDecryptStr(id, MOBILESESSIONID);
		logoType = commonForMobileService.getDecryptStr(logoType, MOBILESESSIONID);
		int type = Integer.parseInt(logoType);
		int oId = Integer.parseInt(id);
		byte[] logo = null;
		switch(type){
		  case 1: logo = projectService.findLogoByProUuidForMobile(oId);break;
		  case 2: logo = Coder.decryptBASE64(loginService.findUser(oId).getLogo().replaceAll("data:image/gif;base64,", ""));break;
		  case 3: logo = Coder.decryptBASE64(loginService.findUserLogoByTaskId(oId).replaceAll("data:image/gif;base64,", ""));break;
		}
		response.setContentType("image/png");
		OutputStream outputStream  = response.getOutputStream();
		outputStream.flush();
		outputStream.write(logo);
		outputStream.close();
	}
	
	/**
	 * 根据userId获取用户详细信息
	 * @param userId
	 * @param MOBILESESSIONID
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/findUserDetail")
	public Map<String, Object> findUserDetailByUserId(String userId,String MOBILESESSIONID) throws Exception{
		userId = commonForMobileService.getDecryptStr(userId, MOBILESESSIONID);
		if(StringUtils.isBlank(userId)){
			return ResultDataJsonUtils.errorResponseResult(ERROR_AND_RE_LOAD);
		}
		return commonForMobileService.successResponseMapForMobile(userByMobileService.findUserByUserId(Integer.parseInt(userId)), MOBILESESSIONID);
	}
	
	/**
	 * 获取当前登录用户详细信息
	 * @param MOBILESESSIONID
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/getMyInfo")
	public Map<String, Object> findUserDetailByUserId(String MOBILESESSIONID) throws Exception{
		return commonForMobileService.successResponseMapForMobile(userByMobileService.findUserByUserId(commonForMobileService.getShiroUser(MOBILESESSIONID).getUserId()), MOBILESESSIONID);
	}
	
	@ResponseBody
	@RequestMapping("/loginout")
	public Map<String, Object> loginOut(String MOBILESESSIONID) throws Exception{
		Cache sample = (Cache)CacheUtil.getEhcahce();
		sample.remove(MOBILESESSIONID);
		return commonForMobileService.successResponseMapForMobile("loginOut success", MOBILESESSIONID);
	}
	
}
