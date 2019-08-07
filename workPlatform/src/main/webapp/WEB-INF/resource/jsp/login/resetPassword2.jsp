<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>
<body class="project vertical-scroll">
<%@ include file="headRegister.jsp" %>
<div data-account="${account }" class="popup-content-wrapper" id="resetPassword2Div">
	<div class="popup-content login">
		<form>
        	<input id="password" class="form-control code" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')"  placeholder="输入新密码"  type="password" />
            <input id="rePassword" class="form-control code" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')"  placeholder="重复输入新密码"  type="password" />
            <button class="btn green btn-common"  id="resetPassword">重设密码</button>
             <a class="pull-right green" href="${context}/user/toLogin.htm">返回登录</a>
        </form>
	</div>


</div>
<%@ include file = "../home/scriptload.jsp" %>
<script src="${context }/static/plugins/md5/md5.js"></script>
<script src="${context }/static/script/login/resetPasssword2.js"></script>
</body>