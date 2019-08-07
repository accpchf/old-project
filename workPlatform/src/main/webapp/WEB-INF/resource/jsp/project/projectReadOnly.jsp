<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp" %>
<div id='projectSetting' class="windows-popup boardBackground form-setting bootstraplayerCenter">
	<c:set var="doing" value="<%=StatusEnums.ProjectSatus.DOING.getCode()%>"></c:set>
    <div class="img-block">
        <h4><spring:message code="xiangmu"  />设置</h4>
       	<img id = "proLogo" src="${pro.logoStr }" class="img-circle prologo"/> 
        <i class="icon-cancel-circled shut" data-dismiss="modal" aria-hidden="true"></i>
    </div>
    <ul class="nav nav-tabs nav-justified">
       <li class="active" data-type="projectInfo" id = "projectInfo" ><a href="#"><spring:message code="xiangmu"  />信息</a></li>
       <li data-type="userMan" id="user_manager"><a href="#" ><spring:message code="chengyuan"  />管理</a></li>
       <c:if test="${pro.status==doing}">
       		<myshiro:hasPermission name="<%=PermitConstant.PRJ_QUIT %>" objectId="${pro.projectId}" >
        		<li data-type="userOption"><a  href="#" id = "user_options">高级设置</a></li>
        	</myshiro:hasPermission>
       </c:if>
    </ul>
    <div class="scroll-area vertical-scroll">
    <form id="updateProjectForm" action="${context }/project/updateProInfo.htm"  method="post" enctype="multipart/form-data">
    <ul id = "proInfo">
    	<input type="file" id="inputLogo" name="inputLogo" style="display:none" disabled="disabled">
    	<input type="text" id = "input_logo" name = "input_logo" style="display:none;" disabled="disabled">
    	<input id="orgId" name = "orgId" type="text" value="${orgId }" hidden>
    	<li><input id="proId" name = "projectId" type="text" value="${pro.projectId }" hidden></li>
        <li><label><spring:message code="xiangmu"  />名称 (<span class="green">${enum:getTextByCode(pro.status)}</span>)</label><input id = "proName" name = "name" class=" form-control" type="text" data-type="name"  value="${pro.name }" disabled="disabled"></li>
        <li class="team-cycle"><label  ><spring:message code="xiangmu"  />周期</label><input disabled="disabled" id="beginTime" class=" form-control " readonly="readonly" type="text" placeholder="开始时间" value="<fmt:formatDate value="${pro.beginTime}" type="date" dateStyle="long" />"  >~<input disabled="disabled" readonly="readonly" class=" form-control" id="endTime" type="text" placeholder="结束时间" value="<fmt:formatDate value="${pro.endTime}" type="date" dateStyle="long"/>"></li>
        <li><label><spring:message code="xiangmu"  />简介</label><textarea name = "description" id = "description" class=" form-control" type="text" disabled="disabled">${pro.description }</textarea></li>
        <input id="input_beginTime" type="text" value="${pro.beginTime}"  name="inputBeginTime"  style="display:none" disabled="disabled">
        <input id="input_endTime" name = "inputEndTime" type="text" value="${pro.endTime}"  style="display:none" disabled="disabled">
    	<c:if test="${projectActionRecords!=null}">
    	<li>
        	<label><spring:message code="xiangmu"  />日志</label>
             <div class=" activity-detail little-portrait log-list corner-6">
            	<ul>
            		<c:forEach items="${projectActionRecords}" var="aRecord" >
            			<li><span>${aRecord.userName}</span>${aRecord.record} <time class="pull-right"><fmt:formatDate value="${aRecord.createdTime}" type="both"/></time> </li>
            		</c:forEach>
                </ul>
             </div>
        </li>
        </c:if>
    </ul>
    </form>
    <form class="user_man" novalidate>
    <ul id = "add-member" class ="tohide">
     

<div class="member-nav">
    <a id="user_list" data-type="userList" class="select"><spring:message code="xiangmu"  /><spring:message code="chengyuan"  />列表</a>
</div>
<div class="teams-list" id ="teams_list">
	<div id = "teamUserList">
		
    </div>
    <ul>
    </ul>
</div>
<div class="teams-list  team-common" id ="addOrgList" hidden>
    <ul> 
        <li class="none-border"><spring:message code="tuandui"  /></li>
        <div id="teams">
        </div>
        <li class="none-border" ><spring:message code="chengyuan"  /></li>
        <div id="userList">
    	</div>
    </ul>
</div>
	</ul>
	</form>
    </ul></form>
    <form class="user_option" novalidate>
	<ul id="advanced-settings" class ="tohide">
	<c:if test="${pro.status==doing}">
	<myshiro:hasPermission name="<%=PermitConstant.PRJ_QUIT %>" objectId="${pro.projectId}" >
      	<div class="advanced-settings" id="exitProDiv">
    		<p>一旦你退出了该<spring:message code="xiangmu"  />，你将不能查看任何关于该<spring:message code="xiangmu"  />的信息。退出<spring:message code="xiangmu"  />后，如果想重新加入，请联系<spring:message code="xiangmu"  />的拥有者或者管理员。</p>
   			<button class="btn red btn-common" id = "exit_pro_btn">退出<spring:message code="xiangmu"  /></button>
   		</div>
    </myshiro:hasPermission>
    </c:if>
    </ul></form>
    </ul></form>
    </div>
    <script type="text/javaScript" src="${context }/static/script/project/projectReadOnly.js"></script>
</div>
