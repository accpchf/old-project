package com.ks0100.common.exception;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

public class CustomSimpleMappingExceptionResolver extends SimpleMappingExceptionResolver {
	protected Logger log = LoggerFactory.getLogger(getClass());
	
	protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
		String viewName = determineViewName(ex, request);
		PrintWriter writer =null;
		if (viewName != null) {
			//spring-servlet.xml 配置
			if(viewName.indexOf("maxUploadExceeded")>=0){
				try {
					writer = response.getWriter();
					writer.write("文件应不大于 "+ getFileMB(((MaxUploadSizeExceededException) ex).getMaxUploadSize()));
					writer.flush();
				} catch (IOException e) {
					log.error("error:",e);
				}finally{
					if(writer!=null){
						writer.close();
						writer=null;
					}
				}
				return null;
			}
			// JSP格式返回
			if (!(request.getHeader("accept").indexOf("application/json") > -1 || (request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").indexOf("XMLHttpRequest") > -1))) {
				// 如果不是异步请求
				Integer statusCode = determineStatusCode(request, viewName);
				if (statusCode != null) {
					applyStatusCodeIfPossible(request, response, statusCode);
				}
				return getModelAndView(viewName, ex, request);
			} else {
				
				// JSON格式返回
				try {
					StringPrintWriter strintPrintWriter = new StringPrintWriter();
			        ex.printStackTrace(strintPrintWriter);
					 writer = response.getWriter();
					writer.write(strintPrintWriter.getString());
					writer.flush();
				} catch (IOException e) {
					log.error("error:",e);
					//e.printStackTrace();
				}finally{
					if(writer!=null){
						writer.close();
						writer=null;
					}
				}
				return null;
			}
		} else {
			return null;
		}
	}
	
/*	private String getFileKB(long byteFile) {
		if (byteFile == 0)
			return "0KB";
		long kb = 1024;
		return "" + byteFile / kb + "KB";
	}*/
	
	private String getFileMB(long byteFile) {
		if (byteFile == 0)
			return "0MB";
		long mb = 1024 * 1024;
		return "" + byteFile / mb + "MB";
	}
}
