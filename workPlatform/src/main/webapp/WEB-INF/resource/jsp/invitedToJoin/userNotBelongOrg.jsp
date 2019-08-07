<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>
<body class="project vertical-scroll">
	<div class="header" >
	<a href="${context}/user/toLogin.htm"><div  class="logo pull-left toindex"></div></a>

</div>
<div class="popup-content-wrapper">
	<div class="popup-content organization">
        <i class="icon-cancel-circled red"></i>
    	<h4>请你先加入<spring:message code="xiangmu"  />所属的<spring:message code="zuzhi"  /></h4>
        <h4>再加入<span class="green">${proname }</span><spring:message code="xiangmu"  />！</h4>
        <a class="red" onclick="location_home();">转到首页</a>
        
	</div>


</div>
<%@ include file="../home/footer.jsp" %>
<script type="text/javascript">
 function location_home(){
	 window.location=context + "/project/home.htm";
 }
</script>
</body>