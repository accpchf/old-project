<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String context = request.getContextPath();
	request.setAttribute("context", context);
%>
<div class='layer-windows boardBackground form-setting' id="addOrgPage">
	<div class='layer-title'>
		<h5>创建<spring:message code="zuzhi"  /></h5>
		<i class="icon-cancel-circled shut"></i>
	</div>
	<ul>
		<li><label><spring:message code="zuzhi"  />名称</label><input autofocus="autofocus" id='orgName' name="name"  class=' form-control' type='text'
			data-type='name' value=''></li>
		<li><label><spring:message code="zuzhi"  />描述</label>
		<textarea name="description" class=' form-control' id='orgDescription'></textarea></li>
		<li><label>联系人</label><input id='orgContacter' name="contancter" class=' form-control' type='text'
			data-type='name' value=''></li>
		<li><label>联系电话</label><input id='orgPhone' name="phone" class=' form-control' type='text'
			data-type='name' value=''></li>
		<li><button id="orgAddBtn" class='btn green btn-common'>确定</button></li>
	</ul>
</div>
<script src="${context }/static/script/organize/addOrg.js"></script>
