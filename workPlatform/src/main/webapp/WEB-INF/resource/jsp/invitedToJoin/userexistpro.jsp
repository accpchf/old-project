<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>
<body class="project vertical-scroll">
	<div class="header" >
	<a href="${context}/user/toLogin.htm"><div  class="logo pull-left toindex"></div></a>

</div>
<div class="popup-content-wrapper">
	<div class="popup-content organization">
        <i class="icon-warning yellow"></i>
    	<h4>你已经存在</h4>
    	<h4><span class="green">${proname }</span><spring:message code="xiangmu"  />中</h4>
        <a onclick="location_home();" class="red">转到首页</a>
	</div>


</div>
<%@ include file="../home/footer.jsp" %>
<script type="text/javascript">
	function location_home(){
	 window.location=context + "/project/home.htm";
 }
</script>
</body>