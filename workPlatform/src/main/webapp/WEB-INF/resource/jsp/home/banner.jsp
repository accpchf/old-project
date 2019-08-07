<%@ page language="java" pageEncoding="UTF-8"%>
<div class="header" >
  <div id="logoPic" class="logo pull-left"></div>
  <c:if test="${projectName!=null}">
  <div id="switchingProject" class="main-menue pull-left corner-6 project-select">
      <i class="icon-inbox"></i><a><span>${projectName}</span><i class=" icon-down-open-big"></i></a>
      
  </div>
  </c:if>
  <div class="right-function  pull-right">
    <ul>
        <li ><a href="#" id="createObj"><i class=" icon-plus"></i>新建</a></li>
        <li><a href="#" id="personaInfo"><img class="img-circle" src="<shiro:principal  property="logo"/>" /><shiro:principal  property="name"/><i class="icon-down-dir"></i></a></li>
    </ul> 
  </div>
 
</div>
<div id="hideAllProectList" class='layer-big boardBackground floatLayer project-img' style="display:none">
		<div class='menu-input'>
			<input class='form-control' type='text' placeholder='查找<spring:message code="xiangmu"  />'>
		</div>
		<ul class='vertical-scroll-min scroll-area'>
			<li class='title-line'>
常用<spring:message code="xiangmu"  /></li>
			<c:choose>
			<c:when test="${fn:length(commonProjects) >0 }">
				<c:forEach items="${commonProjects}" var="project">
					<li data-id='${project.projectId  }' class='project'>
						<img src='${project.logoStr  }' /><a>${project.name}</a><i class='icon-ok' style='display:none;'></i>
					</li>
				</c:forEach>
			</c:when>
			<c:otherwise><li>无</li></c:otherwise>
			</c:choose>
			<li class='title-line'>个人<spring:message code="xiangmu"  /></li>
			<c:choose>
			<c:when test="${fn:length(personProjects) >0 }">
				<c:forEach items="${personProjects}" var="project">
					<li data-id='${project.projectId  }' class='project'>
						<img src='${project.logoStr  }' /><a>${project.name}</a><i class='icon-ok' style='display:none;'></i>
					</li>
				</c:forEach>
			</c:when>
			<c:otherwise><li>无</li></c:otherwise>
			</c:choose>
			<c:choose>
				<c:when test="${orgProjects != null}">
					<c:forEach items="${orgProjects}" var="entry">
						<li class='title-line'>${entry.value.org.name}</li>
						<c:choose>
							<c:when test="${fn:length(entry.value.projectList) >0 }">
								<c:forEach items="${entry.value.projectList}" var="project">
									<li data-id='${project.projectId  }' class='project'>
										<img src='${project.logoStr  }' /><a>${project.name}</a><i class='icon-ok' style='display:none;'></i>
									</li>
								</c:forEach>
							</c:when>
						</c:choose>
					</c:forEach>
				</c:when>
			</c:choose>
		</ul>
	</div>
 <div id='addProjectBootstrapLayer' data-backdrop="static" class="modal fade"  style="width:100%"></div>