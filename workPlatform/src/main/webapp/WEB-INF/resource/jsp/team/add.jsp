<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp" %>
<div class="windows-popup sm boardBackground form-setting ">
    	<div class="img-block">
        	<h4>新建<spring:message code="tuandui"  /></h4>
        </div>
        <div class="team-input"><input id="name" class=" form-control" type="text" placeholder="团队名称"></div>
        <div class="windows-button">
            <a id="exit">取消</a>
        	<button id="add" class="btn green short-btn">确定</button>
        </div>        
</div>
<script src="${context }/static/script/organize/addTeam.js"></script>
