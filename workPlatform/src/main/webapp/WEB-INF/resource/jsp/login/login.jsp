<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>
<body class="project vertical-scroll">
<%@ include file="headRegister.jsp" %>
<div class="popup-content-wrapper">
	<div class="popup-content login">
		<form id="login_form" novalidate>
        	<input id="login_user_input" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')" class="form-control user" placeholder="邮箱"  type="text" />
            <input id="login_password_input" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')" class="form-control code" placeholder="密码"  type="password"/>
            <button id="login_submit_btn" class="btn green btn-common">登录</button>
            <a href="${context}/user/resetPassword.htm" class="pull-left">忘记密码？</a> 
            <a class="pull-right green" href="${context}/user/toRegister.htm?name=''">注册</a>
        </form>
	</div>
</div>


<div style="display: none">这是主页</div>
<%@ include file = "../home/scriptload.jsp" %>
<script src="${context }/static/plugins/md5/md5.js"></script>
<script src="${context }/static/script/login/login.js"></script>

</body>
<%@ include file="../home/footer.jsp" %>