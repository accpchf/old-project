<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp" %>

<body class="project vertical-scroll">
<div class="main-content meeting_content">
	
    <div class="auto-content meeting boardBackground">
    	 <div class="title">
    	 <status:permission status="${project.status }">
    	 <myshiro:hasPermission name="<%=PermitConstant.PRJ_ACCESS %>" objectId="${project.projectId}">
			<button id="add_meeting_btn" class="btn green-word"><i class="icon-plus"></i>新增会议</button>
		</myshiro:hasPermission>
		</status:permission>
         </div>
         <div class="meeting_manage">
          <c:choose>
			<c:when test ="${!empty mapList}">
         		<c:forEach items="${mapList}" var="map" varStatus="status">
         		
         			<div class="time-block  corner-3">
         				<div class="weekly-project" data-meetingId="${map.meeting.meetingId}">
				       <span class="alter">
				       <status:permission status="${project.status }">
				       	<myshiro:hasPermissionByList name="hasPermissionByList" permitCodeAndIds="${map.permitCodeAndIds_set_meeting}" >

								<jsp:useBean id="now" class="java.util.Date"></jsp:useBean>
								<fmt:formatDate value="${map.meeting.beginTime}" var="beginTime" pattern="yyyy年MM月dd日  HH时mm分" />
								<fmt:formatDate var="varTime" value="${now}" pattern="yyyy年MM月dd日  HH时mm分" />
								<c:if test="${now < map.meeting.beginTime}">
									<i class=" icon-pencil" title="修改"></i>
								</c:if>
								<i class=" icon-trash-empty" title="删除"></i>
						</myshiro:hasPermissionByList>
						</status:permission>
				       
				       </span>
				       <h4 class="update_title">${map.meeting.title}</h4>
				       <p><i class="icon-clock" title="会议时间"></i><temp class="update_beginTime"><fmt:formatDate value="${map.meeting.beginTime}" pattern="yyyy年MM月dd日  HH时mm分"/></temp>
				       <i class="icon-location-1" title="会议地点"></i><temp class="update_place">${map.meeting.place}</temp></p>
				       <label>描述 </label>
				       <p class="update_detail">${map.meeting.detail}</p>
				       <label>纪要: </label>
				       <status:permission status="${project.status }">
						<myshiro:hasPermissionByList name="hasPermissionByList" permitCodeAndIds="${map.permitCodeAndIds_update_meeting}" >
							<button class="btn outer-borer edit_record">编辑纪要</button>
						</myshiro:hasPermissionByList>
						</status:permission>
				       
				       <%-- <p class="update_record p_record">${map.meeting.record}</p> --%>
				       <textarea  class="transparent update_record hidden_overflow" disabled="disabled">${map.meeting.record}</textarea>
				       <label>参与者 </label>
				       <ul class="team_user_manage add-message little-portrait">
				       		<c:if test="${map.meeting.users!=null }">
				       			<c:forEach items="${map.meeting.users }" var="user">
				       				<li title="${user.name}" data-id="${user.userId}"><img src="${user.logo}" class="img-circle">
				       	<status:permission status="${project.status }">
				       	<myshiro:hasPermissionByList name="hasPermissionByList" permitCodeAndIds="${map.permitCodeAndIds_set_meeting}" >
							<a class="remove-member-handler">×</a>
						</myshiro:hasPermissionByList>
						</status:permission>
				       				</li>
				       			</c:forEach>
				       		</c:if>
				       	<status:permission status="${project.status }">
				       		<myshiro:hasPermissionByList name="hasPermissionByList" permitCodeAndIds="${map.permitCodeAndIds_set_meeting}">
								<li class="add_user_li"><i class="icon-plus-circle icon-big" title="添加参与者"></i></li>
							</myshiro:hasPermissionByList>
						</status:permission>
				       		
				       </ul>
				       <status:permission status="${project.status }">
						<myshiro:hasPermissionByList name="hasPermissionByList" permitCodeAndIds="${map.permitCodeAndIds_update_meeting}">
							<a class="add_adjunct_a"> <i class="icon-attach-2"></i>添加附件 </a>
						</myshiro:hasPermissionByList>
						</status:permission>
			           <ul class="accessory-area"><li class="adjunct_manage">
			           		<c:if test="${map.meeting.attachments != null}">
			           			<c:forEach items="${map.meeting.attachments }" var="attachment">
			           				<span data-fileurl="${attachment.fileUrl }"><a><i class=" icon-doc-text-1"></i>${attachment.fileName}</a>
										<status:permission status="${project.status }">
										<myshiro:hasPermissionByList name="hasPermissionByList" permitCodeAndIds="${map.permitCodeAndIds_update_meeting}">
											<i class="icon-cancel-circled delete_adjunct_i" title="删除"></i>
										</myshiro:hasPermissionByList>
										</status:permission>
			           				</span>
			           			</c:forEach>
			           		</c:if>
			           </li></ul>
					   </div>
        			</div> 　
         		</c:forEach>
         	</c:when>
         	<c:otherwise>
	<div class="none-content">
   		<i class="icon-mic"></i>
        <br />
        <a>目前还没有会议</a>
   </div>					
	</c:otherwise>
	</c:choose>
         </div>
    </div>	
    
    <!-- 添加附件表单 -->
    <form class="file_upload_form tohide" action="${context}/meeting/uploadfile.htm" method="post" enctype="multipart/form-data">
      	<input type="file" name="upload_file" class="file_upload_input"/>
      	<input type="text" name="meetingId" class="file_meetingid_input"/>
      	<input type="hidden" name="projectId"  value="${project.projectId}" />
    </form>
    <!-- 下载附件表单 -->
    <form class="file_download_form tohide" action="${context}/filelibrary/downloadfile.htm" method="post">
    	<input type="hidden" name="remove_cache" value="<%= (new Date()).getTime()%>"/>
    	<input type="hidden" name="fileUrl"/>
    </form>
</div>
<script type="text/javaScript" src="${context}/static/script/meeting/meeting.js"></script>
</body>