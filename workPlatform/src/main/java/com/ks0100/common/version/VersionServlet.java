package com.ks0100.common.version;


import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ks0100.common.constant.CommonConstant;
import com.ks0100.common.util.ReadPropertiesUtil;

// from branches version
public class VersionServlet extends HttpServlet {

	private static final long serialVersionUID = 4567568568908365343L;
//	private Logger log = Logger.getLogger(VersionServlet.class);
	
	public void init(ServletConfig config) throws ServletException {
		CommonConstant.VERSION = (String)ReadPropertiesUtil.getContextProperty("wp.version");
		super.init(config);
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("version", CommonConstant.VERSION) ;
		request.setAttribute("enable",true) ;
		request.getRequestDispatcher("/WEB-INF/resource/jsp/version/version.jsp")
				.forward(request, response);
	}
}
