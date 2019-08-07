<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp" %>
<body class="project vertical-scroll">
	<div class="windows-popup boardBackground form-setting">
	    <div class="img-block addMeeting">
	        <c:if test="${meeting.meetingId > 0 }">
	        	<h4>修改会议</h4>
	        </c:if>
	        <c:if test="${meeting.meetingId <= 0 }">
	        	<h4>新增会议</h4>
	        </c:if>
	        <i class="icon-cancel-circled shut"></i>
	    </div>
	    <ul>
	    	<form method="post" action="${context}/meeting/addmeeting.htm" class="meeting_form" novalidate>
	    		<input name="projectId" value="${projectId }" type="hidden" style="display:none"/>
	    		<input name="meetingId" value="${meeting.meetingId }" type="hidden" style="display:none"/>
		        <li><label>会议标题</label><input name="title" class=" form-control" type="text" placeholder="会议标题" value="${meeting.title }"></li>
		        <li><label>会议时间</label><input readonly="readonly" name="beginTimeString" class="updateEndDate form-control" type="datetime" placeholder="如：2014.12.10 10:10" value='<fmt:formatDate value="${meeting.beginTime}" pattern="yyyy年MM月dd日  HH时mm分"/>'>
		        
		        </li>
		        <li><label>地点</label><input name="place" class=" form-control" type="text" placeholder="会议地点" value="${meeting.place }"></li>
		        <li><label>描述</label><textarea name="detail" class=" form-control  textareaShort" type="text" placeholder="会议描述">${meeting.detail }</textarea></li>
		        <%-- <li><label>纪要</label><textarea name="record" class=" form-control" type="text" placeholder="会议纪要">${meeting.record }</textarea></li> --%>
		        <button class="submit_btn btn green btn-common">确定</button>
	        </form>
	    </ul>
	</div>
</body>