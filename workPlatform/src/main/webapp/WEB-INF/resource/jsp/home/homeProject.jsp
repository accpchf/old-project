<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp" %>
<c:set var="doing" value="<%=StatusEnums.ProjectSatus.DOING.getCode()%>"></c:set>
<body>
	<c:choose>
		<c:when test="${!empty projects}">
			<c:forEach items="${projects}" var="project">
				<div class="project-block teams corner-6 "  style=" background-image:url('${project.logoStr }');"
						data-status="${project.status }" data-uuid="${project.uuid }"  data-projectid="${project.projectId }">
					<div class="operate"><em class="icon-pencil"></em>
						<c:if test = "${!project.commonUse }">
							<em class=" icon-star"></em>
						</c:if>	                          				
						<c:if test = "${project.commonUse }">
						  <em class=" icon-star select"></em>
						</c:if>	                          				
					</div>
					<c:if test="${project.logoStr==null}"> <i class="icon-user"></i></c:if>
					<c:if test = "${fn:length(project.name) >17}">
					<h3 title="${project.name }">${fn:substring(project.name, 0, 17)}...</h3>
					</c:if>
					<c:if test = "${fn:length(project.name) <=17}">
					<h3 title="${project.name }">${project.name }</h3>
					</c:if>
					<div class="describe">
						${enum:getTextByCode(project.status)} 
					</div>     
				</div>   
			</c:forEach>
		</c:when>
		<c:otherwise>
			<c:if test="${status == doing }">
				<div class="title">
          			<button class="btn green-word creatOrgProject"> <em class=" icon-plus-1"></em>创建<spring:message code="xiangmu"  /></button>
           		 </div>
            </c:if>
        </c:otherwise>
	</c:choose>
</body>
