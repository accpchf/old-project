<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp" %>
<script type="text/javascript">
window.readOnly = true;
<status:permission status="${project.status }">
	window.readOnly = false;
</status:permission>
</script>
<body class="project vertical-scroll">
	<!-- 初始化后台参数 -->
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
   <div class="project-chart" >
   		<input id="visibleStatusAllUser" value="${visibleStatusAllUserCode}" style="display:none;"/>
   		<input id="visibleStatusTaskMember" value="${visibleStatusTaskMemberCode}" style="display:none;"/>
   		<input id="doingCode" value="${doing}" style="display:none;"/>
   		<input id="itemCompleteCode" value="${itemComplete}" style="display:none;"/>
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
       	<div class="chart-wrap" style="display:none">
	        <div class="block countdown">
	        <status:permission status="${project.status }">
				<i id="refreshCountdownStatistics" class="icon-arrows-ccw " title="刷新"></i>
			</status:permission>
	        
	        	 <h2>倒计时</h2>
	        	 <h5 id="projectCountdown"></h5>
	        	 <!-- <h4>计划工时</h4>
	        	 <h5 id="planDay"></h5>
	        	 <h4>实际工时</h4>
	        	 <h5 id="realityDay"></h5> -->
	        	 <p>计划总工时<span id="planDay"></span><br>实际总工时<span id="realityDay">2天</span></p>
	        </div>
	        
	        <div class="block user">
	        	 <h2><spring:message code="xiangmu"  />人数</h2>
	        	 <h5  id="projectUserNum"></h5>
	        </div>
	        <div class="block task-tatistic">
	         	<status:permission status="${project.status }">
	        		<i id="refreshTaskStatistics" class="icon-arrows-ccw " title="刷新"></i>
	        	</status:permission>
	        	<div id="taskStatistics" style="width:350px;height:178px"></div>
	        </div>
	        <div class="block task-progress">
	         	<status:permission status="${project.status }">
	        		<i id="refreshWeekTaskChart" class="icon-arrows-ccw" title="刷新"></i>
	        	</status:permission>
	        	<div id="weekTaskChart" style="width:385px;height:178px"></div>
	        </div>
       	</div>
    	
        <div class="stretch">
        	<div id="switchTaskBoard" class="task-select pull-left"><a><i class="icon-left-dir"></i><span class="name"></span></a><i class="icon-down-open-big"></i></div>
        	<myshiro:hasPermission name="<%=PermitConstant.PRJ_HIGH_ACCESS %>" objectId="${project.projectId }" >
	      		<div class="arrow">
	               <i id="upOrDown" class="Stretch_arrow down_arrow"></i>
	               <span>展开图表</span>
	            </div>
      		</myshiro:hasPermission>
            <ul class="check-select">
                <li data-value="2"><span class="check-box pull-left"><i class="icon-ok" style="display:none;"></i></span> 我参与的</li>
                <li data-value="1"><span class="check-box pull-left"><i class="icon-ok" style="display:none;"></i></span> 我执行的</li>
                <li data-value="3"><span class="check-box pull-left"><i class="icon-ok" style="display:none;"></i></span> 我管理的</li>
                <li id="all" data-value="4"><span class="check-box pull-left"><i class="icon-ok" style="display:none;"></i></span> 所有</li>
            </ul>
        </div>
    </div>
    <div class="main-task">
        <ul class="task horizontal-scroll">
        </ul>
    </div>
<script type="text/javaScript" src="${context }/static/script/taskBoard/taskBoard.js"></script>
</body>