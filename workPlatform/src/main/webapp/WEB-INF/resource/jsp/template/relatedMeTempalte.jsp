<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>

<body class="project vertical-scroll">
<%@ include file="../home/banner.jsp"%>
<div id="myPersonalInfo" class="organize-setting" data-id='relatedMeTempalte' data-moudleName="${moudleName }">
	<nav>
        <div class="wrap" id="relatedMeMenu">
        	<ul class="nav nav-pills">
               <li id='relateMeTaskMenu'><a href="#">我的任务</a></li>
               <li id='relateMeWeeklyReportMenu'><a href="#">我的周报</a></li>
               <li id='relateMeSettingMenu'><a href="#">个人设置</a></li>
            </ul>
        </div>
    </nav>
    <div id="relatedMeContainer" class="wrap">
    	
    </div>
 </div>
<script type="text/javascript" src="${context }/static/plugins/require/require.js" data-main="${context }/static/script/framework/main.js" ></script>
</body>

<%@ include file="../home/footer.jsp" %>