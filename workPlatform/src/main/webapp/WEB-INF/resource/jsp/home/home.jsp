<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>
<body id="home_body" class="project vertical-scroll">
<%@ include file="../home/banner.jsp" %>
<c:set var="doing" value="<%=StatusEnums.ProjectSatus.DOING.getCode()%>"></c:set>
<c:set var="deletestatus" value="<%=StatusEnums.ProjectSatus.DELETE.getCode()%>"></c:set>
<div id='projectSettingBootstrapLayer' data-backdrop="static" class="modal fade"  style="width:100%"></div>
<div data-status="${doing }" data-deletestatus="${ deletestatus}"  data-id="projectHomePage" class="main-content">
    <div  class="index-body-wrap">
      <div class="main-project">
        <ul>
                <li><span>常用<spring:message code="xiangmu"  />
                </span></li>
                <li><a class="select"><i class="icon-inbox"></i> <span class="left-arrow-big"></span></a></li>
                <li>
                    <div class="project-bg corner-6 commomuseprojects">
                     <c:if test="${commonProjects!=null}">
                     	 <c:forEach items="${commonProjects}" var="project">
                     		  <div class="project-block common corner-6" 
                     		  	 style=" background-image:url('${project.logoStr }');"
                     		  <%-- data-isArchive = "${project.archive}" --%> data-uuid="${project.uuid }" data-status="${project.status}"  data-projectid="${project.projectId  }" >
	                              <div class="operate"><em class="icon-pencil"></em><em class=" icon-star select"></em></div>
	                              <c:if test="${project.logoStr==null}"> <i class="icon-inbox"></i></c:if>
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
                     </c:if>
                    </div>
                </li>
            </ul>
            
          <ul  id="gerenxiangmu">
                <li><span>个人<spring:message code="xiangmu"  />
                </span></li>
                <li><a class="select"><i class="icon-user"></i> <span class="left-arrow-big"></span></a></li>
                <li>
                	<div class="project-bg corner-6" id="personalProject">
                	<span class="project-state">进行中<i class="icon-down-open-big"></i></span>
                          <div style="display: none;"  class="layer-xsm boardBackground floatLayer little-portrait project-state-larer">
                                <ul> 
                                    <li data-status = "00700">进行中<i class=" icon-ok"></i></li>
                                    <li data-status = "00701">已完成<i style="display:none" class=" icon-ok"></i></li>
                                    <li data-status = "00702">已暂停<i style="display:none" class=" icon-ok"></i></li>
                                    <li data-status = "00703">已删除<i style="display:none" class=" icon-ok"></i></li>
                                </ul>
                            </div>
                		<c:choose>
							<c:when test="${!empty personProjects}">
	                     	 <c:forEach items="${personProjects}" var="project">
	                     	 	<div class="project-block user corner-6" 
	                     	 	 style=" background-image:url('${project.logoStr }');"
	                     	 	<%-- data-isArchive = "${project.archive}" --%>  data-uuid="${project.uuid }" data-status="${project.status}" data-projectid="${project.projectId }" >
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
								<div class="title">
          	    	          	<button class="btn green-word creatOrgProject"> <em class=" icon-plus-1"></em>创建<spring:message code="xiangmu"  /></button>
                          		</div>
						</c:otherwise>
                    	 </c:choose>
                    </div>
                </li>
            </ul>
            
              <ul>
                <li><span><spring:message code="zuzhi"  /><spring:message code="xiangmu"  />
                </span></li>
                <li><a class="select"><i class="icon-users  users"></i> <span class="left-arrow-big"></span></a></li>
                <li class = "organizationprojects" >
                	 <c:choose>
                		<c:when test ="${!empty orgProjects}" >
                			<c:forEach items="${orgProjects}" var="entry"> 
                				<myshiro:hasPermission name="<%=PermitConstant.ORG_ADMIN_ACCESS %>" objectId="${entry.value.org.id}" >
      								 <div class="project-bg corner-6 "><h4 class="goOrganization" data-uuid = "${entry.value.org.uuid}" data-orgid = "${entry.value.org.id}">${entry.value.org.name}<em class=" icon-right-open-big"></em></h4>
					            <span class="project-state">进行中<i class="icon-down-open-big"></i></span>
                          <div style="display: none;"  class="layer-xsm boardBackground floatLayer little-portrait project-state-larer">
                                <ul> 
                                    <li data-status = "00700">进行中<i class=" icon-ok"></i></li>
                                    <li data-status = "00701">已完成<i style="display:none" class=" icon-ok"></i></li>
                                    <li data-status = "00702">已暂停<i style="display:none" class=" icon-ok"></i></li>
                                    <li data-status = "00703">已删除<i style="display:none" class=" icon-ok"></i></li>
                                </ul>
                            </div>
					            <c:choose>
									<c:when test="${fn:length(entry.value.projectList) >0 }">
										 <c:forEach items="${entry.value.projectList}" var="project">
							            	  <div class="project-block teams corner-6 "  style=" background-image:url('${project.logoStr }');"
							            	   <%-- data-isArchive = "${project.archive}" --%>  data-uuid="${project.uuid }"  data-status="${project.status}" data-projectid="${project.projectId }">
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
											<div class="title">
	          	    	          				<button class="btn green-word creatOrgProject"> 
	          	    	          				<em class=" icon-plus-1"></em>创建<spring:message code="xiangmu"  />
	          	    	          				</button>
                          					</div>
									</c:otherwise>
								</c:choose>
								</div>  
      							</myshiro:hasPermission>
					            <myshiro:hasPermission name="<%=PermitConstant.ORG_ACCESS %>" objectId="${entry.value.org.id}" >
      								 <div class="project-bg corner-6 "><h4 class="goOrganization" data-uuid = "${entry.value.org.uuid}" data-orgid = "${entry.value.org.id}">${entry.value.org.name}<em class=" icon-right-open-big"></em></h4>
					            <span class="project-state">进行中<i class="icon-down-open-big"></i></span>
                          <div style="display: none;"  class="layer-xsm boardBackground floatLayer little-portrait project-state-larer">
                                <ul> 
                                    <li data-status = "00700">进行中<i class=" icon-ok"></i></li>
                                    <li data-status = "00701">已完成<i style="display:none" class=" icon-ok"></i></li>
                                    <li data-status = "00702">已暂停<i style="display:none" class=" icon-ok"></i></li>
                                    <li data-status = "00703">已删除<i style="display:none" class=" icon-ok"></i></li>
                                </ul>
                            </div>
					            <c:choose>
									<c:when test="${fn:length(entry.value.projectList) >0 }">
										 <c:forEach items="${entry.value.projectList}" var="project">
							            	  <div class="project-block teams corner-6 "  style=" background-image:url('${project.logoStr }');"
							            	   <%-- data-isArchive = "${project.archive}" --%>  data-uuid="${project.uuid }"  data-status="${project.status}" data-projectid="${project.projectId }">
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
											<div class="title">
          	    	          				<button class="btn green-word creatOrgProject"> <em class=" icon-plus-1"></em>
创建<spring:message code="xiangmu"  /></button>
                          					</div>
									</c:otherwise>
								</c:choose>
								</div>  
      							</myshiro:hasPermission>
					     </c:forEach>  
                		</c:when>
                		<c:otherwise>
                			<div class="project-bg corner-6 "></div>
						</c:otherwise>
                	</c:choose> 
					    
                
                  
                </li>
            </ul>
        </div>
        
    </div> 
</div>
<script type="text/javascript" src="${context }/static/plugins/require/require.js" data-main="${context }/static/script/framework/main.js" ></script>
</body>
<%@ include file="../home/footer.jsp" %>
