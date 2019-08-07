<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>当前<spring:message code="xiangmu"  />版本</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
           当前<spring:message code="xiangmu"  />的版本号为：<%=request.getAttribute("version") %> <br><br>
            版本状态：<%
            if(request.getAttribute("enable")!=null&&request.getAttribute("enable").toString().equals("true")){
            	out.println("可用");
            }else{
            	out.println("不可用，请联系后台开发人员！");
            }
            %>
  </body>
</html>
