package com.ks0100.common.shiro;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.servlet.OncePerRequestFilter;

import com.ks0100.common.ehcache.CacheUtil;

/**
 * 这个filter作用是判断传过来的MOBILESESSIONID是否在缓存里面过期，如果过期跳转返回session过期的json字符串
 * @author ctx_chen haifeng
 *
 */
public class MyOncePerShiroFilter extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(ServletRequest request,
			ServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		
		String[] paths=((HttpServletRequest)request).getServletPath().split("/");
		if(paths.length>=4){
			//String account=paths[3].substring(0, paths[3].indexOf(".htm")-1);
			String account=paths[3];
			account=account.replaceAll(".htm", "");
			String mobileSessionId=request.getParameter("MOBILESESSIONID");
			if(StringUtils.isNotBlank(mobileSessionId)){
				Cache sample =(Cache)CacheUtil.getEhcahce();
				//sample.get(mobileSessionId).getObjectValue();
				Element element=sample.get(mobileSessionId);
				if(element!=null){
					//MobileUser mu=(MobileUser)element.getObjectValue(); 
					//System.out.println("缓存还在:--------------------:"+mu.getShiroUser().getUserId());
				}else{
					//请求转发，告知mobileSessionId过期，并且返回新的随机数
				//	System.out.println("没有缓存了----------------------account:"+account);
					((HttpServletRequest)request).getRequestDispatcher("/userByMobile/sessionInvlid/"+account+".htm").forward(request, response);
					return;
					
				}
				
			}
		}



		chain.doFilter(request, response);
	}

}
