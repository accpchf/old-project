<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp"%>


<body class="project vertical-scroll">
<div id="teamAndUserDiv">
         <h4><a id="allTeams">所有<spring:message code="tuandui"  /></a><i class="icon-right-open-big"></i><a class="select-black">所有<spring:message code="chengyuan"  /></a></h4>
    	 <div id="listDiv" class="organize-wrap-left boardBackground team-user-page" data-team-id="${teamId}">
              <h4  class="margin-space"><a class="select" id="teamUser">现有<spring:message code="tuandui"  />人员(<c:out value="${fn:length(users)}"></c:out>)</a> 
              		<myshiro:hasPermissionByList name="hasPermissionByList" permitCodeAndIds="${permitCodeAndIds}" >
      	 				<span>|</span> <a id="orgUser">从<spring:message code="zuzhi"  />中导入<spring:message code="chengyuan"  /></a> 
      				</myshiro:hasPermissionByList>
              </h4> 
	             <c:forEach items="${users}" var="user">
	    	 	  <div class="user-block corner-6" data-userid=${user.userId }>
	                  <img  src="${user.logo }" class="img-circle"  />
	                  <c:if test = "${fn:length(user.name) > 4}">
			          <h4 title="${user.name }">${fn:substring(user.name, 0, 4)}...</h4>
			          </c:if>
			          <c:if test = "${fn:length(user.name) <= 4}">
			            <h4 title="${user.name }">${user.name }</h4>
			          </c:if>
			          <myshiro:hasPermissionByList name="hasPermissionByList" permitCodeAndIds="${permitCodeAndIds}" >
			          	 <c:if test="${user.userRole=='TEAM_ADMIN'}">
                  			<myshiro:hasPermission name="<%=PermitConstant.ORG_MEMBER_SET %>" objectId="${orgId}" >
			          			<i class="icon-down-open-big"></i>
			          		</myshiro:hasPermission>
                		 </c:if>
			          	 <c:if test="${user.userRole!='TEAM_ADMIN'}">
			          		<i class="icon-down-open-big"></i>
                		 </c:if>
      				  </myshiro:hasPermissionByList>
			         
			          <c:if test="${user.userRole=='TEAM_ADMIN'}">
                  		<a class="owner-mark " title="管理员" data-title="拥有者"><i class="icon-crown"></i></a>
                	 </c:if>
	              </div>
	    	 	</c:forEach>
         </div> 
         <myshiro:hasPermissionByList name="hasPermissionByList" permitCodeAndIds="${permitCodeAndIds}" >
      	<div id='teamDiv' class="organize-wrap-right boardBackground h-title p-style">
         	 <h4><spring:message code="tuandui"  />参与的<spring:message code="xiangmu"  />(<c:out value="${fn:length(pro)}"></c:out>)</h4>
             <p><spring:message code="tuandui"  /><spring:message code="chengyuan"  />将直接加入<spring:message code="xiangmu"  />，往<spring:message code="tuandui"  />中添加<spring:message code="chengyuan"  />，自动添加到参与的<spring:message code="xiangmu"  /></p>
             <div id="searchDiv" class="input-search" >
                <input id="search" class="form-control user" placeholder="输入后按下回车键搜索<spring:message code="xiangmu"  />" type="text"><i class="icon-search"></i>
             	<ul id="list" class="project-down boardBackground project-list" hidden>
                </ul>
             </div>
             <div id="info"><p><center>还没参与任何<spring:message code="xiangmu"  /></center></p></div>
            <ul id="show" class="project-list">
            	<c:forEach items="${pro}" var="project">
            		<c:if test="${project.projectId!=null}">
            			<li data-project-id="${project.projectId}"><img src="${project.logoStr}"  class="corner-3"> <a>${project.name}</a><button class="btn pull-right outer-borer" style="display:none">移除</button> </li>
            		</c:if>
            	</c:forEach>
            </ul>
         </div> 
      </myshiro:hasPermissionByList>
       
         </div>
         <script src="${context }/static/script/organize/teamUser.js"></script>
</body>