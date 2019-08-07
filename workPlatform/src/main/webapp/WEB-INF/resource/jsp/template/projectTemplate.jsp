<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>

<body class="project vertical-scroll">
<%@ include file="../home/banner.jsp" %>
<div id="menuItems" class="left-menue ">
    <ul>   
       <li><a id="taskBoard"><i class="icon-tasks"></i><i class="right-arrow"></i>任务板</a></li>
        <myshiro:hasPermission name="<%=PermitConstant.PRJ_ACCESS %>" objectId="${projectId}">
            <li><a id="fileLibrary"><i class=" icon-folder"></i><i class="right-arrow" style="display:none"></i>文件库</a>  </li>
        </myshiro:hasPermission>
       <myshiro:hasPermission name="<%=PermitConstant.PRJ_HIGH_ACCESS %>" objectId="${projectId}">
       		<li><a id="dynamic"><i class="icon-back-in-time"></i><i class="right-arrow" style="display:none"></i>动态</a></li>
       	</myshiro:hasPermission>
       <myshiro:hasPermission name="<%=PermitConstant.PRJ_HIGH_ACCESS %>" objectId="${projectId}">
       		<li><a id="weekly"><i class="icon-clipboard-1"></i><i class="right-arrow" style="display:none"></i>周报</a></li>
		</myshiro:hasPermission>
       <myshiro:hasPermission name="<%=PermitConstant.PRJ_ACCESS %>" objectId="${projectId}">
       		<li><a id="meetting"><i class=" icon-mic"></i>会议<i class="right-arrow" style="display:none"></i></a></li>
       	</myshiro:hasPermission>
    </ul>
</div>
<div data-id="projectTemplate" class="main-content" data-projectId="${projectId}">
     
</div>
<script type="text/javascript" src="${context }/static/plugins/require/require.js" data-main="${context }/static/script/framework/main.js" ></script>
</body>

<%@ include file="../home/footer.jsp" %>