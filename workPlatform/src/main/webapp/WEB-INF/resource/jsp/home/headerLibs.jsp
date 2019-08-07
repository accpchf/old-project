<%@page import="com.zdksii.pms.common.util.ReadPropertiesUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.zdksii.pms.wp.ShiroUser"%>
<%@page import="com.zdksii.pms.wp.constant.BusinessConstant.PermitConstant"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<%@taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="myshiro" uri="http://pms.zdksii.com/mytag"%>
<%@page import="com.zdksii.pms.wp.constant.StatusEnums"%>
<%@ taglib prefix="enum" uri="/enum"  %>
<%@ taglib prefix="status" uri="/status"  %>
<%
	String context = request.getContextPath();
	request.setAttribute("context", context);
	
	String nodeJsUrl = ReadPropertiesUtil.getStringContextProperty("nodeJs.url");
	request.setAttribute("nodeJsUrl", nodeJsUrl);
%>