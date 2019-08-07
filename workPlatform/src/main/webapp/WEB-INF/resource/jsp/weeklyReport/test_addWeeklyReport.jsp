<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>
<body>
	<button id="addProWeeklyReport">点击添加<spring:message code="xiangmu"  />周报</button>
   	<button id="addUserWeeklyReport">点击添加个人周报</button>
   	<script type="text/javascript" src="${context}/static/plugins/jquery/jquery-1.11.1.js"></script>
<script type="text/javascript">
	$(function($){
		$("#addProWeeklyReport").on("click",function(){
			$.ajax({
					type :'post',
					url :context + '/weeklyReport/test_addProWeekRep.htm',
					success:function(result){
						if(result!=null ){
							if(result.success){
								alert("<spring:message code="xiangmu"  />周报已经生成");
							}else{
								alert(result.errormsg);
							}
							
						}
					},
					error:function(result){
					}
			});
		});
		$("#addUserWeeklyReport").on("click",function(){
			$.ajax({
					type :'post',
					url :context + '/weeklyReport/test_addUserWeekReport.htm',
					success:function(result){
						if(result && result.success){
							alert("个人周报已经生成");
						}else{
								alert(result.errormsg);
							}
					},
					error:function(result){
					}
			});
		});
	});
</script>
</body>
