<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp" %>
<div data-id="personalTemplate" class="home-body-wrap boardBackground personal-task">
  <ul id="personalMenu" class="nav nav-tabs nav-justified">
       <li id="myAllTask"><a href="#">所有</a></li>
       <li id="myAssignlTask" ><a href="#">我管理的</a></li>
       <li id="myExecuteTask" ><a href="#">我执行的</a></li>
       <li id="myParticipateTask" ><a href="#">我参与的</a></li>
  </ul>
<c:set var="doing" value="<%=StatusEnums.TaskStatus.DONING.getCode()%>"></c:set>
    <c:set var="toCheck" value="<%=StatusEnums.TaskStatus.TO_CHECK.getCode()%>"></c:set>
    <c:set var="complete" value="<%=StatusEnums.TaskStatus.COMPLETE.getCode()%>"></c:set>
    <c:set var="noPass" value="<%=StatusEnums.TaskStatus.NO_PASS.getCode()%>"></c:set>
    <c:set var="doingText" value="<%=StatusEnums.TaskStatus.DONING.getText()%>"></c:set>
    <c:set var="toCheckText" value="<%=StatusEnums.TaskStatus.TO_CHECK.getText()%>"></c:set>
    <c:set var="completeText" value="<%=StatusEnums.TaskStatus.COMPLETE.getText()%>"></c:set>
    <c:set var="noPassText" value="<%=StatusEnums.TaskStatus.NO_PASS.getText()%>"></c:set>
    <c:set var="level1" value="<%=StatusEnums.TaskPriority.LEVEL1.getCode()%>"></c:set>
    <c:set var="level0" value="<%=StatusEnums.TaskPriority.LEVEL0.getCode()%>"></c:set>
    <c:set var="level1Text" value="<%=StatusEnums.TaskPriority.LEVEL1.getText()%>"></c:set>
    <c:set var="level0Text" value="<%=StatusEnums.TaskPriority.LEVEL0.getText()%>"></c:set>
    <c:set var="itemComplete" value="<%=StatusEnums.TaskCheckListStatus.COMPLETE.getCode()%>"></c:set>
    <c:set var="itemCompleteText" value="<%=StatusEnums.TaskCheckListStatus.COMPLETE.getText()%>"></c:set>
    <c:set var="itemNoComplete" value="<%=StatusEnums.TaskCheckListStatus.NO_COMPLETE.getCode()%>"></c:set>
    <c:set var="itemNoCompleteText" value="<%=StatusEnums.TaskCheckListStatus.NO_COMPLETE.getText()%>"></c:set>
     <c:set var="visibleStatusAllUserCode" value="<%=StatusEnums.TaskVisible.PRJ_MEMBER.getCode()%>"></c:set>
    <c:set var="visibleStatusTaskMemberCode" value="<%=StatusEnums.TaskVisible.TASK_MEMBER.getCode()%>"></c:set>
    <input id="visibleStatusAllUser" value="${visibleStatusAllUserCode}" style="display:none;"/>
   		<input id="visibleStatusTaskMember" value="${visibleStatusTaskMemberCode}" style="display:none;"/>
   		<input id="doingCode" value="${doing}" style="display:none;"/>
		<input id="toCheckCode" value="${toCheck}" style="display:none;"/>
		<input id="completeCode" value="${complete}" style="display:none;"/>
		<input id="noPassCode" value="${noPass}" style="display:none;"/>
		<input id="doingText" value="${doingText}" style="display:none;"/>
		<input id="toCheckText" value="${toCheckText}" style="display:none;"/>
		<input id="completeText" value="${completeText}" style="display:none;"/>
		<input id="noPassText" value="${noPassText}" style="display:none;"/>
		<input id="level1Code" value="${level1}" style="display:none;"/>
		<input id="level0Code" value="${level0}" style="display:none;"/>
		<input id="level1Text" value="${level1Text}" style="display:none;"/>
		<input id="level0Text" value="${level0Text}" style="display:none;"/>
  <div id="personalTaskContainer">
	  	<div class="time-block">
	    	<div class="h-title"><h4>今天的任务</h4></div>
	    	<div  id="todayTaskList" class="taskList"></div>
    	</div>
   	    <div class="time-block">
	    	<div class="h-title"><h4>未来的任务</h4></div>
	    	<div  id="futureTaskList" class="taskList"></div>
    	</div>
 </div>
</div>
<script type="text/javaScript" src="${context }/static/script/personalTask/personalTask.js"></script>
