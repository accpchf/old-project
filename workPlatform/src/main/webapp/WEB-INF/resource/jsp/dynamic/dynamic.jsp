<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp" %>
<body class="project vertical-scroll">
<div class="main-content" id="dynamic_view">
    <div class="home-body-wrap">
        <div class="dynamic-content">
            <div class="layout-left">
                <div class="time-bg" style="position:fixed;width:250px;" id="selectTimeList">
                    <div class="layer-group open-layer layer-inblock" id="selected_time">
                    <ul id="selectWeek" class="boardBackground   space-item2  floatLayer vertical-scroll-min" style= "display:none">
                       <li><a>12.15-12.19</a> <i class=" icon-ok"></i></li>
                       <li><a>12.15-12.19</a></li>
                       <li><a>12.15-12.19</a></li>
                    </ul>
                 </div>
                </div>
                <ul id="userList" style="margin-top:50px">
                	<li><span class="check-box"><i class="icon-ok"></i></span><img class="img-circle" src="" /><a>所有<spring:message code="chengyuan" /></a></li>
                    <li><span class="check-box"><i class="icon-ok"></i></span><img class="img-circle" src="" /><a>所有<spring:message code="chengyuan" /></a></li>
                </ul>
            </div>
        	<div class="layout-right">
            	<div class="dynamic-state">
                    <h4>动态 </h4>
                    <ul class="check-select" id="dynamicType">
                        <li data-type="1"><span class="check-box pull-left"><i class="icon-ok"></i></span>任务</li>
                        <li data-type="2"><span class="check-box pull-left"></span>文件库</li>
                        <li data-type="3"><span class="check-box pull-left"></span>会议</li>
                        <li data-type="4"><span class="check-box pull-left"></span>周报</li>
                        <li data-type="5"><span class="check-box pull-left"></span><spring:message code="xiangmu"  />设置</li>
                    </ul>
                    <div id="dynamic_content">
               		</div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javaScript" src="${context }/static/script/dynamic/dynamic.js"></script>
</body>