package com.ks0100.common.ehcache;

import java.util.Enumeration;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.constructs.web.filter.SimplePageCachingFilter;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ks0100.wp.ShiroUser;

public class PageEhCacheFilter extends SimplePageCachingFilter {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final static String FILTER_URL_PATTERNS = "patterns";
	private static String[] cacheURLs;

	private void init() throws CacheException {
		String patterns = filterConfig.getInitParameter(FILTER_URL_PATTERNS);
		cacheURLs = StringUtils.split(patterns, ",");
	}

	@Override
	protected void doFilter(final HttpServletRequest request,final HttpServletResponse response, final FilterChain chain)throws Exception {
		if (cacheURLs == null) {
			init();
		}
		String url = request.getRequestURI();

		//判断请求的url是否和web.xml的匹配，如果匹配走缓存
		boolean flag = false;
		if (cacheURLs != null && cacheURLs.length > 0) {
			for (String cacheURL : cacheURLs) {
				if (url.contains(cacheURL.trim()) ) {
					ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
					if(user==null||!url.contains(user.getAccount())){
						((HttpServletRequest)request).getRequestDispatcher("/login/loginout.htm").forward(request, response);
						return;
					}else{
						flag = true;
						break;
					}
				}
			}
		}
		// 如果包含我们要缓存的url 就缓存该页面，否则执行正常的页面转向
		if (flag) {
			String query = request.getQueryString();
			if (query != null) {
				query = "?" + query;
			}else{
				query="";
			}
			logger.info("-----------------------当前请求被缓存：" + url + query);
			super.doFilter(request, response, chain);
		} else {
			chain.doFilter(request, response);
		}

	}

	@Override
    protected String calculateKey(HttpServletRequest httpRequest) {
        StringBuffer stringBuffer = new StringBuffer();
        String queryString=httpRequest.getQueryString()==null?"":httpRequest.getQueryString();
    //    String a=httpRequest.getServletPath();
      //  String b=httpRequest.getContextPath();
        stringBuffer.append(httpRequest.getServletPath()).append(queryString);
        String key = stringBuffer.toString();
        logger.info("缓存的key：" + key);
        return key;
    }
    
	private boolean headerContains(final HttpServletRequest request,final String header, final String value) {

		logRequestHeaders(request);
		@SuppressWarnings("rawtypes")
		Enumeration accepted = request.getHeaders(header);

		while (accepted.hasMoreElements()) {
			String headerValue = (String) accepted.nextElement();
			if (headerValue.indexOf(value) != -1) {
				return true;
			}
		}
		return false;

	}

	/**
	 * 
	 * @see net.sf.ehcache.constructs.web.filter.Filter#acceptsGzipEncoding(javax.servlet.http.HttpServletRequest)
	 * 
	 *      <b>function:</b> 兼容ie6/7 gzip压缩
	 * 
	 * @author hoojo
	 * 
	 * @createDate 2012-7-4 上午11:07:11
	 */

	@Override
	protected boolean acceptsGzipEncoding(HttpServletRequest request) {
		boolean ie6 = headerContains(request, "User-Agent", "MSIE 6.0");
		boolean ie7 = headerContains(request, "User-Agent", "MSIE 7.0");
		return acceptsEncoding(request, "gzip") || ie6 || ie7;

	}
}
