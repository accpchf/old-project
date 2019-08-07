<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>

<body class="project vertical-scroll">
<%@ include file="../home/banner.jsp" %>
<div data-id="organizationTemplate" class="organize-setting" data-organizationId='${organization.id}'>
  <nav>
      <div class="organize-logo pull-left">
          <img src="${organization.logo}"  class="img-circle"/>
          <h4  class="person-h">${organization.name}</h4> 
        </div>  
        <div class="wrap">
          <ul id="organizationMenu" class="nav nav-pills">
          		<myshiro:hasPermission name="<%=PermitConstant.ORG_MEMBER_MENU %>" objectId="${organization.id}" >
      				<li id="person"><a href="#"><spring:message code="chengyuan"  /></a></li>
      			</myshiro:hasPermission>
               <myshiro:hasPermission name="<%=PermitConstant.ORG_TEAM_MENU %>" objectId="${organization.id}" >
    				<li id="team"><a href="#"><spring:message code="tuandui"  /></a></li>
      			</myshiro:hasPermission>
               
               <myshiro:hasPermission name="<%=PermitConstant.ORG_STATISTICS_MENU %>" objectId="${organization.id}" >
      				<li id="statistics"><a href="#">统计</a></li>
      			</myshiro:hasPermission>
               
               <myshiro:hasPermission name="<%=PermitConstant.ORG_SET_MENU %>" objectId="${organization.id}" >
      				<li id="set"><a href="#">设置</a></li>
      			</myshiro:hasPermission>
               
               <li id="user"></li>
          </ul>
        </div>
    </nav>
    <div class="wrap organization-common" id="organizationContainer"></div>
    <div><input type="text" id="id" value="${organization.id}" hidden></div>
</div>
<script type="text/javascript" src="${context }/static/plugins/require/require.js" data-main="${context }/static/script/framework/main.js" ></script>
</body>
