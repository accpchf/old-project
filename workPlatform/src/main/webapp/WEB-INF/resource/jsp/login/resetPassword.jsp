<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>
<body class="project vertical-scroll">
<%@ include file="headRegister.jsp" %>
<div class="popup-content-wrapper" id="resetPasswordDiv">
	<div class="popup-content login">
		<form>
        	<input onkeyup="this.value=this.value.replace(/^ +| +$/g,'')" class="form-control code" id="account" placeholder="输入用户名"  type="text" />
            <button class="btn green btn-common" id="resetPassword">发送邮件重置密码</button>
        </form>
	</div>


</div>
<%@ include file = "../home/scriptload.jsp" %>
<script src="${context }/static/script/login/resetPassword.js"></script>
</body>