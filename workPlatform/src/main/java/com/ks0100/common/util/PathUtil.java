package com.ks0100.common.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * 可以通用解决容器路径的问题 尤其是解决weblogic里面war通过getAbsolutePathByContext()方法获取路径为空 的问题
 * 
 * @author Administrator
 * 
 */
public class PathUtil {

	  public static final Logger LOGGER = Logger.getLogger(PathUtil.class);

	  public static String ABSOLUTE_CLASS_PATH;
	  public static String ABSOLUTE_WEB_PATH;

	  // 通过 spring 注入方式或者服务器启动的方式调用构造方法
	  public PathUtil() {
		findAbsolutePathByClass();
		findAbsolutePathByContext();
		LOGGER.info("************ABSOLUTE_WEB_PATH:" + ABSOLUTE_WEB_PATH);
		LOGGER.info("************ABSOLUTE_CLASS_PATH:" + ABSOLUTE_CLASS_PATH);
	  }

	  /**
	   * 通过上下文来取工程路径
	   * 
	   * @return
	   * @throws Exception
	   */
	  protected String findAbsolutePathByContext(HttpServletRequest request) {
		ABSOLUTE_WEB_PATH = request.getSession().getServletContext().getRealPath("/");
		return replacePath(ABSOLUTE_WEB_PATH);
	  }

	  private static String findAbsolutePathByContext() {
		// spring 的方式
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		ServletContext servletContext = webApplicationContext.getServletContext();
		ABSOLUTE_WEB_PATH = servletContext.getRealPath("/");
		return replacePath(ABSOLUTE_WEB_PATH);
	  }

	  /**
	   * 通过类路径来取工程路径
	   * 
	   * @return
	   * @throws Exception
	   */
	  protected static String findAbsolutePathByClass() {
		ABSOLUTE_CLASS_PATH = PathUtil.class.getResource("/").getPath().replaceAll("^\\/", "");
		return replacePath(ABSOLUTE_CLASS_PATH);
	  }

	  private static String replacePath(String path) {
		String nPath = path.replaceAll("[\\\\\\/]WEB-INF[\\\\\\/]classes[\\\\\\/]?", "/");
		nPath = nPath.replaceAll("[\\\\\\/]+", "/");
		nPath = nPath.replaceAll("%20", " ");
		if(!nPath.matches("^[a-zA-Z]:.*?$")) {
		  nPath = "/" + nPath;
		}
		nPath += "/";
		nPath = nPath.replaceAll("[\\\\\\/]+", "/");
		return nPath;
	  }

	  protected String findAbsolutePathByRequest(HttpServletRequest request)
		  throws MalformedURLException, URISyntaxException {
		URL url = request.getSession().getServletContext().getResource("/");
		ABSOLUTE_WEB_PATH = new File(url.toURI()).getAbsolutePath();
		if(!ABSOLUTE_WEB_PATH.endsWith("\\") && !ABSOLUTE_WEB_PATH.endsWith("/")) {
		  ABSOLUTE_WEB_PATH += File.separator;
		}
		return ABSOLUTE_WEB_PATH;
	  }
}
