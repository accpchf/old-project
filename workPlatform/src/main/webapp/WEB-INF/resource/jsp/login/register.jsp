<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>

<body class="project vertical-scroll">
<%@ include file="headLogin.jsp" %>
<div class="popup-content-wrapper">
	<div class="popup-content register">
		<form id="register_form" novalidate>
        	<input id="register_username_input" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')" name="account" class="form-control" value="${user.account}" placeholder="请输入用户名"  type="text" /><c:if test="${error!=null}"><h3>该帐号已存在</h3></c:if>
            <input id="register_name_input" name="name" class="form-control" value="${user.name }" placeholder="请输入姓名"  type="text" />
            <input id="register_password_input" name="password" class="form-control" value="${user.password}" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')" placeholder="请输入密码"  type="password"/>
            <input id="register_confirmpwd_input" class="form-control" value="${user.password}" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')" placeholder="请再次输入密码"  type="password"/>
            <div class="organization-select"> <span id="is_createorganize_checkbox" class="check-box"><i class="icon-ok tohide"></i></span><a> 是否创建<spring:message code="zuzhi"  /></a></div>
            <input id="organize_name_input" name="organizename" class="form-control tohide" placeholder="请输入<spring:message code="zuzhi"  />名称"type="text"/>
            <button class="btn green btn-common" id="register_btn">注册</button>
            <c:if test="${name == 'addProject' }">
            	请注册后重新加入<spring:message code="xiangmu"  />
            </c:if>
            <c:if test="${name == 'addOrg' }">
            	请注册后重新加入<spring:message code="zuzhi"  />
            </c:if>
        </form>
	</div>


</div>
<%@ include file = "../home/scriptload.jsp" %>
<script src="${context }/static/plugins/md5/md5.js"></script>

<script src="${context }/static/script/login/register.js"></script>

</body>
<%@ include file="../home/footer.jsp" %>
