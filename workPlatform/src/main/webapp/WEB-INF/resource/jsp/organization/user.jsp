<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>
<body class="project vertical-scroll">
    	 <div class="organize-wrap-left" id="organize-wrap-left">
    	 <c:forEach items="${userList}" var="users">
    	 	<c:if test="${users.name!=null}">
    	 		<div class="user-block corner-6" data-user-active="${users.active}" data-account="${users.account }" data-user-id="${users.id}">
    	 		<c:if test="${users.role=='ORG_ADMIN'}">
                  <a class="owner-mark " title="管理员"><i class="icon-crown"></i></a>
                </c:if>
                <c:if test="${users.role=='ORG_SUPERVISER'}">
                  <a class="owner-mark " title="监督员"><i class="icon-user-1"></i></a>
                </c:if>
                  <img src="${users.logo}" class="img-circle"  />
                  <c:if test = "${fn:length(users.name) > 4}">
                  <h4 title="${users.name } ">${fn:substring(users.name, 0, 4)}...</h4>
			      </c:if>
			      <c:if test = "${fn:length(users.name) <= 4}">
                  <h4 title="${users.name } ">${users.name}</h4>
			      </c:if>
                  <myshiro:hasPermission name="<%=PermitConstant.ORG_MEMBER_SET %>" objectId="${orgId}" >
                  	<i title="<spring:message code="chengyuan"  />" class="icon-down-open-big"></i>
                  </myshiro:hasPermission>
              </div>
            </c:if>
    	 </c:forEach>
         </div> 
         <myshiro:hasPermission name="<%=PermitConstant.ORG_MEMBER_SET %>" objectId="${orgId}" >
      					<div class="organize-wrap-right boardBackground h-title">
         	 <h4>添加<spring:message code="chengyuan"  /></h4>
             <span>通过邮箱添加新<spring:message code="chengyuan"  /></span>
             <input class="form-control user" id="account"  placeholder="输入完成后请敲击回车键" type="text">
             <span>通过链接添加新<spring:message code="chengyuan"  /></span>
             <div class="open-link" style="display:none">
   				 <p>把链接通过QQ、微信、邮件组等方式分享，让他们通过点击链接直接加入当前<spring:message code="zuzhi"  />。</p>
                 <a id="open">开启链接</a>					
             </div>
             <div class="close-link" style="display:block">
                 <input id="linksInvite" class="form-control user" placeholder="" type="text" value="">
   				 <p><spring:message code="chengyuan"  />加入<spring:message code="zuzhi"  />后，为了防止链接泄漏可以选择 </p>
                 <a id="close">关闭链接</a>					
             </div>

        	 </div> 
      	</myshiro:hasPermission>
         
</body>
<script src="${context }/static/plugins/md5/md5.js"></script>
<script src="${context }/static/script/organize/organizeUser.js"></script>