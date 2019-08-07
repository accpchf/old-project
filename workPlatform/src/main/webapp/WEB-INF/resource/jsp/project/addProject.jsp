<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp" %>
<body class="project vertical-scroll">
<div id="addProject" class="windows-popup boardBackground form-setting addProject bootstraplayerCenter">
    <div class="img-block ">
        <h4>创建<spring:message code="xiangmu"  /></h4>
        <div class="project-type">
        	<img id="projectLogo" src="${context }/static/images/project_default/pic-project1.jpg" class="img-circle pull-left"/>
            <div class="project-type-right">
            	<img id= "project1" src="${context }/static/images/project_default/pic-project1.jpg" class="readyimg img-circle select"/>
                <img id= "project2" src="${context }/static/images/project_default/pic-project2.jpg" class="readyimg img-circle"/>
                <img id= "project3" src="${context }/static/images/project_default/pic-project3.jpg" class="readyimg img-circle"/>
                <img id= "project4" src="${context }/static/images/project_default/pic-project4.jpg" class="readyimg img-circle"/>
                <img id= "project5" src="${context }/static/images/project_default/pic-project5.jpg" class="readyimg img-circle"/>
                <i class="icon-plus-circle" id= "addlogo"  title="添加新主题"></i>
            </div>
        </div>
        <i class="icon-cancel-circled shut" data-dismiss="modal" aria-hidden="true"></i>
    </div>
    <form id="addProjectForm" action="${context }/project/addProSuccess.htm"  method="post" enctype="multipart/form-data">
    <input type="file" id="logoFile" name="logoFile" style="display:none;">
     <input type="hidden" id = "imgId" name = "imgId" >
     <input type="text" id="orgId" name="orgid" style="display:none">
    <ul>
        <li><label><spring:message code="xiangmu"  />名称</label><input id="projectName" name="name" class=" form-control" type="text"></li>
        <li class="team-cycle"><label  ><spring:message code="xiangmu"  />周期</label><input id="beginTime" name="beginString"  class=" form-control " type="text" placeholder="开始时间"  onFocus="WdatePicker({maxDate:'#F{$dp.$D(\'endTime\')}',dateFmt:'yyyy年MM月dd日'})" />~<input class=" form-control" id="endTime" name="endString" type="text" placeholder="结束时间" onFocus="WdatePicker({minDate:'#F{$dp.$D(\'beginTime\')}',dateFmt:'yyyy年MM月dd日'})" /></li>
        <li><label><spring:message code="xiangmu"  />简介</label>
		<textarea name="description"  class=' form-control' id='project_description'></textarea></li>
        <!-- <li id="beginTime">
          <label>开始时间</label>
          <a><i class="icon-calendar"></i>
            <input placeholder="选择开始时间" id="begin"  readonly class="taskBoardDate"/>
            <input id="input_beginTime" type="text"  name="inputBeginTime"   style="display:none">
          </a>
        </li>
       <li id="endTime">
          <label>结束时间</label>
          <a><i class="icon-calendar"></i>
            <input placeholder="选择结束时间" id="end" readonly class="taskBoardDate"/>
            <input id="input_endTime" name = "inputEndTime" type="text" style="display:none">
          </a>
        </li> -->
        	<li id="selectOrganization"><label>拥有者</label> 
	        <select id="organizationId" class=" form-control" name="organizationId">
	        	<option id="myselfOpt" value="0" selected = "selected">我自己</option>
	        	<c:forEach items="${orgs}" var="org">
	        		<option value="${org.id }" >${org.name }</option>
	        	</c:forEach>
		     </select>  
       		</li>
       
       <!--  <input type="submit" name="add_project_btn"  id="add_project_btn" class="btn green btn-common" value="确定" />   -->
        <button id="add_project_btn" class="btn green btn-common">确定</button> 
    </ul>
    </form>
</div>
<script type="text/javaScript" src="${context }/static/script/project/addProject.js"></script>
</body>