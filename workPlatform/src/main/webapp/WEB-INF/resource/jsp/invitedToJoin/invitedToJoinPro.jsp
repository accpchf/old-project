<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>
<body class="project vertical-scroll">
<%@ include file="../login/headLogin.jsp" %>
<div class="popup-content-wrapper">
	<div class="popup-content organization">
    	<h3>欢迎加入workboard</h3>
            您已经被邀请加入项目： <h5 id="projectname"><span class="green">${pro.name}</span></h5>
        <a><span>邀请人：</span><i class="icon-user"></i> ${inviter }</a>
        <div class="login">
            <form id="login_form_pro" novalidate>
            	<c:if test="${isLogin}">
            		<button id="agree_btn_pro" class="btn green btn-common">同意</button>
            	</c:if>
            	<c:if test="${!isLogin}">
            		<input onkeyup="this.value=this.value.replace(/^ +| +$/g,'')" id="login_user_input_pro" class="form-control user" placeholder="邮箱"  type="text" />
		            <input onkeyup="this.value=this.value.replace(/^ +| +$/g,'')" id="login_password_input_pro" class="form-control code" placeholder="密码"  type="password"/>
		            <button id="login_agree_btn_pro" class="btn green btn-common">同意并登录</button>
	                <a class="pull-right green" href="${context }/user/toRegister.htm?name=addProject">注册</a>
            	</c:if>
            </form>
        </div>
	</div>
</div>
<%@ include file = "../home/scriptload.jsp" %>
<script type="text/javascript">
	var isLogin = '${isLogin}',
		code = '${code}';
</script>
<script src="${context }/static/plugins/md5/md5.js"></script>
<script src="${context }/static/script/invitedToJoin/invitedToJoinPro.js"></script>
</body>
<%@ include file="../home/footer.jsp" %>