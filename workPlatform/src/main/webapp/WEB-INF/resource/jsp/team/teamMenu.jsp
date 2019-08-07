<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>
<div id='layerDiv'>
<div class='layer-lg  floatLayer'>
<div class='layer-title'>
<h5><spring:message code="tuandui"  />菜单</h5>
<i class='icon-cancel-circled-outline'></i>
</div>
		<ul>
			<myshiro:hasPermissionByList name="hasPermissionByList" permitCodeAndIds="${permitCodeAndIds}" >
      			<li><a id='update'> <i class='icon-pencil'></i>编辑<spring:message code="tuandui"  /></a></li>
      		</myshiro:hasPermissionByList>
      		<myshiro:hasPermission name="<%=PermitConstant.ORG_ACCESS %>" objectId="${orgId}" >
      			<li id='exitTeam'><a id='exit'> <i class='icon-user-outline'></i>退出<spring:message code="tuandui"  /></a></li>
      		</myshiro:hasPermission>
			<myshiro:hasPermissionByList name="hasPermissionByList" permitCodeAndIds="${permitCodeAndIds}" >
      			<li><a id='delete'> <i class='icon-trash'></i>删除<spring:message code="tuandui"  /></a></li>
      		</myshiro:hasPermissionByList>
            
            
        </ul>
    </div>
    </div>