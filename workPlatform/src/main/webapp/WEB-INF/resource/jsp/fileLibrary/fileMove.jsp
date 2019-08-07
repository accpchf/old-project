<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp" %>
<body class="project vertical-scroll">
	<div class="windows-popup boardBackground form-setting file_move_content" data-trees="${treeFiles}">
    	<div class="img-block">
        	<p>移动 ${fromFileName}至</p>
        	<i class="icon-cancel-circled shut"></i>
        </div>
        <div class="file-body">
        	<ul id="file_zTree" class="ztree folder_tree_div"></ul>
        </div>
        <div class="windows-button">
             <!-- <a>取消</a> -->
        	<button class="btn green short-btn btn_submit">确定</button>
        </div>
    </div>
</body>