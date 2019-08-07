<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp" %>
<div id='projectSetting' class="windows-popup boardBackground form-setting bootstraplayerCenter">
    
    <c:set var="doing" value="<%=StatusEnums.ProjectSatus.DOING.getCode()%>"></c:set>
    <c:set var="delete" value="<%=StatusEnums.ProjectSatus.DELETE.getCode()%>"></c:set>
    <c:set var="complete" value="<%=StatusEnums.ProjectSatus.COMPLETE.getCode()%>"></c:set>
    <c:set var="stop" value="<%=StatusEnums.ProjectSatus.STOP.getCode()%>"></c:set>
    
    <div class="img-block">
    <c:if test="${pro.status==doing}">
        <h4><spring:message code="xiangmu"  />设置</h4>
        <a class="person-header img-alter"><img src="${pro.logoStr }" class="img-circle img120" /><span class="boardBackground"><i id ="proLogo" class="icon-pencil-1"></i></span></a>
        <i class="icon-cancel-circled shut" data-dismiss="modal" aria-hidden="true"></i>
     </c:if>
     <c:if test="${pro.status!=doing}">
        <h4><spring:message code="xiangmu"  />设置</h4>
        <img src="${pro.logoStr }" class="img-circle img120" />
        <i class="icon-cancel-circled shut" data-dismiss="modal" aria-hidden="true"></i>
     </c:if>
    </div>
    <ul class="nav nav-tabs nav-justified">
       <li class="active" data-type="projectInfo" id = "projectInfo" ><a href="#"><spring:message code="xiangmu"  />信息</a></li>
       <li data-type="userMan" id="user_manager"><a href="#" ><spring:message code="chengyuan"  />管理</a></li>
       <c:if test="${pro.status!=delete&&pro.status!=complete}">
       <li data-type="userOption"><a  href="#" id = "user_options">高级设置</a></li>
       </c:if>
    </ul>
    <div class="scroll-area vertical-scroll">
    <form id="updateProjectForm" action="${context }/project/updateProInfo.htm"  method="post" enctype="multipart/form-data">
    <ul id = "proInfo">
    	<c:if test="${pro.status==doing}">
    	<input type="file" id="inputLogo" name="inputLogo" style="display:none">
    	<input type="text" id = "input_logo" name = "input_logo" style="display:none;">
    	<input id="orgId" name = "orgId" type="text" value="${orgId }" hidden>
    	<li><input id="proId" name = "projectId" type="text" value="${pro.projectId }" hidden></li>
        <li><label><spring:message code="xiangmu"  />名称 <span id="status" hidden>${pro.status}</span></label><input id = "proName" name = "name" class=" form-control" type="text" data-type="name"  value="${pro.name }"></li>
        <li><label><spring:message code="xiangmu"  />状态</label><span class="green" style="margin-left:10px">${enum:getTextByCode(pro.status)}</span></li>
        <li class="team-cycle"><label  ><spring:message code="xiangmu"  />周期</label><input id="beginTime" name="beginString" class=" form-control " onFocus="WdatePicker({maxDate:'#F{$dp.$D(\'endTime\')}',dateFmt:'yyyy年MM月dd日'})" type="text" placeholder="开始时间" value="<fmt:formatDate value="${pro.beginTime}" type="date" dateStyle="long" />"   >~<input class=" form-control" id="endTime" name="endString" type="text" placeholder="结束时间" onFocus="WdatePicker({minDate:'#F{$dp.$D(\'beginTime\')}',dateFmt:'yyyy年MM月dd日'})"  value="<fmt:formatDate value="${pro.endTime}" type="date" dateStyle="long" />" ></li>
        <li><label><spring:message code="xiangmu"  />简介</label><textarea name = "description" id = "description" class=" form-control" type="text" >${pro.description }</textarea></li>
        <button class="btn green btn-common" id = "update_pro_info">确定</button>
    	</c:if>
    	<c:if test="${pro.status!=doing}">
    	<input type="file" id="inputLogo" name="inputLogo" style="display:none" disabled="disabled">
    	<input type="text" id = "input_logo" name = "input_logo" style="display:none;" disabled="disabled">
    	<input id="orgId" name = "orgId" type="text" value="${orgId }" hidden>
    	<li><input id="proId" name = "projectId" type="text" value="${pro.projectId }" hidden></li>
        <li><label><spring:message code="xiangmu"  />名称 <span id="status" hidden>$(pro.status)</span></label><input id = "proName" name = "name" class=" form-control" type="text" data-type="name"  value="${pro.name }" disabled="disabled"></li>
        <li><label><spring:message code="xiangmu"  />状态</label><span class="green">${enum:getTextByCode(pro.status)}</span></li>
        <li class="team-cycle"><label  ><spring:message code="xiangmu"  />周期</label><input disabled="disabled" id="beginTime" class=" form-control " readonly="readonly" type="text" placeholder="开始时间" value="<fmt:formatDate value="${pro.beginTime}" type="date" dateStyle="long" />"  >~<input disabled="disabled" readonly="readonly" class=" form-control" id="endTime" type="text" placeholder="结束时间" value="<fmt:formatDate value="${pro.endTime}" type="date" dateStyle="long"/>"></li>
        <li><label><spring:message code="xiangmu"  />简介</label><textarea name = "description" id = "description" class=" form-control" type="text" disabled="disabled">${pro.description }</textarea></li>
    	</c:if>
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
     
    <div class="row add-member">
    <c:if test="${pro.status==doing}">
    <div class="col-xs-6">
        <h5>
            通过邮箱添加新<spring:message code="chengyuan"  />
        </h5>
            <input class="form-control" id="add_user_ByEmail" type="text"
            placeholder="输入邮箱 回车确认" >
    </div>
    <div class="col-xs-6">
        <h5>
            通过链接添加新<spring:message code="chengyuan"  /><a id = "link_btn">关闭链接</a>
        </h5>
             <p id = "close_link" style="display:none">把链接通过QQ、微信、邮件组等方式分享给<spring:message code="tuandui"  /><spring:message code="chengyuan"  />，他们即可点击链接直接加入当前<spring:message code="tuandui"  />。</p>
             <input id = "open_link"  class="form-control user" placeholder="" type="text" value="http://xxxxxx" style="display:block">
    </div>
    </c:if>

</div>

<div class="member-nav">
    <a id="user_list" data-type="userList" class="select"><spring:message code="xiangmu"  /><spring:message code="chengyuan"  />列表</a><a data-type="addOrgUser" id="addOrgUser">导入<spring:message code="zuzhi"  /><spring:message code="chengyuan"  /></a>
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
        	<!-- <li class="little-portrait"><h4>团队 3.5</h4><img src="../../images/head-pic.jpg" class="img-circle" /><img src="../../images/head-pic.jpg" class="img-circle" /><img src="../../images/head-pic.jpg" class="img-circle" /><span class="  pull-right  ">已导入</span> </li> -->
        </div>
        <li class="none-border" ><spring:message code="chengyuan"  /></li>
        <div id="userList">
    	</div>
    </ul>
</div>
	</ul>
	</form>
	<form class="user_option" novalidate>
	<ul id="advanced-settings" class ="tohide">
	<c:if test="${pro.status==doing}">
	<myshiro:hasPermission name="<%=PermitConstant.PRJ_QUIT %>" objectId="${pro.projectId}" >
      	<div class="advanced-settings" id="exitProDiv">
    		<p>一旦你退出了该<spring:message code="xiangmu"  />，你将不能查看任何关于该<spring:message code="xiangmu"  />的信息。退出<spring:message code="xiangmu"  />后，如果想重新加入，请联系<spring:message code="xiangmu"  />的拥有者或者管理员。</p>
   			<button class="btn red btn-common" id = "exit_pro_btn">退出<spring:message code="xiangmu"  /></button>
   		</div>
    </myshiro:hasPermission>
    <myshiro:hasPermission name="<%=PermitConstant.PRJ_SET %>" objectId="${pro.projectId}" >
    <div class="advanced-settings" id="suspendProDiv">
    	<p>暂停<spring:message code="xiangmu"  />后，<spring:message code="xiangmu"  />的任务板，文件库，动态等模块都禁止访问，<spring:message code="xiangmu"  />的设置和成员管理也禁止操作。暂停后，在高级设 置里面出现删除和重启按钮。</p>
   		<button class="btn red btn-common" id = "suspend_pro_btn">暂停<spring:message code="xiangmu"  /></button>
    </div>
    <div class="advanced-settings" id="finishProDiv">
    	<p>一旦你完成了<spring:message code="xiangmu"  />，所有与<spring:message code="xiangmu"  />有关的信息将会被永久封存，不可查看。可以在统计数据中体现。
这是一个不可恢复的操作，请谨慎对待！</p>
   		<button class="btn red btn-common" id = "finish_pro_btn">完成<spring:message code="xiangmu"  /></button>
    </div>
     </myshiro:hasPermission>
    </c:if>
   <c:if test="${pro.status==stop}">
   <myshiro:hasPermission name="<%=PermitConstant.PRJ_SET %>" objectId="${pro.projectId}" >
    <div class="advanced-settings" id="deleteProDiv">
    	<p>一旦你删除了<spring:message code="xiangmu"  />，所有与<spring:message code="xiangmu"  />有关的信息将会被永久删除。这是一个不可恢复的操作，请谨慎对待！</p>
   		<button class="btn red btn-common" id = "delete_pro_btn">删除<spring:message code="xiangmu"  /></button>
    </div>
    <div class="advanced-settings" id="restartProDiv">
    	<p>把<spring:message code="xiangmu"  />的状态由“暂停”恢复到“进行中”，使可以正常操作该<spring:message code="xiangmu"  /></p>
   		<button class="btn red btn-common" id = "restart_pro_btn">重启<spring:message code="xiangmu"  /></button>
    </div>
    </myshiro:hasPermission>
    </c:if>
    </ul></form>
    </ul></form></div>
    <script type="text/javaScript" src="${context }/static/script/project/projectSetting.js"></script>
</div>
