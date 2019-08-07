<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>work board</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta http-equiv="pragma" content="no-cache"/> 
    <meta http-equiv="Cache-Control" content="no-store, must-revalidate"/> 
    <meta http-equiv="expires" content="Wed, 26 Feb 1997 08:21:57 GMT"/>
    <meta http-equiv="expires" content="0"/>
<link href="${context }/static/css/bootstrap.css" type="text/css" rel="stylesheet"/>  
<link href="${context }/static/css/main.css" type="text/css" rel="stylesheet"/> 
<link href="${context }/static/css/chat-style.css" type="text/css" rel="stylesheet"/> 
<link rel="stylesheet" type="text/css" href="${context }/static/css/font-icon.css">
<link href="${context }/static/plugins/jquery-ui/jquery-ui-1.10.4.css" rel="stylesheet" type="text/css">
<link href="${context }/static/plugins/zTree/zTreeStyle.css" rel="stylesheet" type="text/css">
<script type='text/javascript'>
	
	var context='${context}',nodeJsUrl = '${nodeJsUrl}',
		loginUserInfo = {};
	loginUserInfo.userId = '<shiro:principal  property="userId"/>';
	loginUserInfo.account = '<shiro:principal  property="account"/>';
	loginUserInfo.name = '<shiro:principal  property="name"/>';
	loginUserInfo.gender = '<shiro:principal  property="gender"/>';
	loginUserInfo.genderTxt = '<shiro:principal  property="genderTxt"/>';
	loginUserInfo.mobile = '<shiro:principal  property="mobile"/>';
	loginUserInfo.position = '<shiro:principal  property="position"/>';
	
	loginUserInfo.logo = '<shiro:principal  property="logo"/>';
	loginUserInfo.action = '<shiro:principal  property="active"/>';
//	console.log(loginUserInfo);

var TEXT_CONFIG = {
	"global_edition" : "<spring:message code='global.edition' />",
	"xiangmu" : "<spring:message code='xiangmu' />",
	"zuzhi" : "<spring:message code='zuzhi' />",
	"chengyuan" : "<spring:message code='chengyuan' />",
	"tuandui" : "<spring:message code='tuandui' />"
};
</script>
</head>