package com.ks0100.common.ehcache;

import net.sf.ehcache.Ehcache;

import com.ks0100.common.util.SpringContextUtil;

public class CacheUtil {
	private static Ehcache ehcahce;
	public static final String PAGE_KEY_LOGIN_SUCCESSTOINDEX="/project/loginSuccessToIndex/";
	
	private static Ehcache pageEhcahce;
	public CacheUtil(){
		ehcahce=(Ehcache)SpringContextUtil.getBean("mobileSessionCache");
		pageEhcahce=(Ehcache)SpringContextUtil.getBean("SimplePageCachingFilter");
		//List l=pageEhcahce.getKeys();
		//pageEhcahce=((EhCacheFactoryBean)SpringContextUtil.getBean("SimplePageCachingFilter")).getObject();
		//CacheManager cacheManager=CacheManager.newInstance(PathUtil.ABSOLUTE_CLASS_PATH+"spring/spring-ehcache.xml");
	//	ehcahce=cacheManager.getCache("mobileSessionCache");
	//	pageEhcahce = new BlockingCache(cacheManager.getEhcache("SimplePageCachingFilter"));
		//System.out.println("------ok----");
	}

	public static Ehcache getEhcahce() {
		return ehcahce;
	}

	public static Ehcache getPageEhcahce() {
		return pageEhcahce;
	}
/*	public static List getPageEhcahceKeys(){
		return pageEhcahce.getKeys();
	}*/
	public static boolean removePageEhcahce(String key){
		return pageEhcahce.remove(key);
	}
	
}
