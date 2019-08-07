<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>
<body class="project vertical-scroll">
<%@ include file="headLogin.jsp" %>
<div class="popup-content-wrapper">
	<div class="popup-content registerSucess" style="width:450px">
       <i class="icon-ok-2"></i>
       <p>重置密码的地址已经通过邮件发送至你的邮箱 ${name }，<br />请跟随邮件里的引导来重设你的登录密码！</p>
       <a class="btn btn-sure" href="${context}/user/toLogin.htm"> 返回登录 </a>
	</div>


</div>