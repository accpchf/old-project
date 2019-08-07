
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp"%>
<div class="wrap organization-common">
	<myshiro:hasPermission name="<%=PermitConstant.ORG_TEAM_CREATE %>" objectId="${orgId}" >
         <div class="title">
		<button id="addTeam" class="btn pull-right green-word">
			<i class="icon-plus-1"></i>新建<spring:message code="tuandui"  />
		</button>
		</div>
    </myshiro:hasPermission>
	
	<div id="teamList">
		<c:forEach items="${list}" var="item">
			<c:if test="${item.name!=null}">
				<div class="team-block corner-6" data-team-id="${item.teamId}">
					<c:if test = "${fn:length(item.name) >10}">
						<a class="userList"><h4 class="select" title="${item.name}" data-team-name="${item.name}">
								<c:out value="${fn:substring(item.name, 0, 10)}..."></c:out>
							</h4> <span class="userNum"><spring:message code="chengyuan"  />(${fn:length(item.users)})</span></span><span><spring:message code="xiangmu"  /><span
							class="projectNum">(${item.projectNum})</span></span>
						</a>
					</c:if>
					<c:if test = "${fn:length(item.name) <10}">
						<a class="userList"><h4 class="select" title="${item.name}" data-team-name="${item.name}">
								<c:out value="${item.name }"></c:out>
							</h4> <span class="userNum"><spring:message code="chengyuan"  />(${fn:length(item.users)})</span></span><span><spring:message code="xiangmu"  /><span
							class="projectNum">(${item.projectNum})</span></span>
						</a>
					</c:if>
					<c:if test="${!empty item.users}">
						<div class="user-portrait corner-3">
							<c:forEach items="${item.users}" var="user">
								<img title="${user.name }" data-userid="${user.userId}" src="${user.logo}" class="img-circle" />	
							</c:forEach>
						</div>
					</c:if>
					<i class="icon-down-open-big"></i>
				</div>
			</c:if>
		</c:forEach>
	</div>
</div>
<script src="${context }/static/script/organize/team.js"></script>
