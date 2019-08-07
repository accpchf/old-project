<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/resource/jsp/home/header.jsp"%>
<html>
<body class="project vertical-scroll" >
		<div class="wrap organization-common" id="orgSetting">
			<div
				class="organization-left form-setting  organization-border corner-3">
				<div class="img-block">
				 	<myshiro:hasPermission name="<%=PermitConstant.ORG_SET_UPDATE %>" objectId="${detail.id}" >
      					<i class="icon-pencil" title="修改"></i>
      				</myshiro:hasPermission>
					<h4><spring:message code="zuzhi"  />信息</h4>
					<img id="pic" src="${detail.logo}" class="img-circle" />
				</div>
				<form class="org_form" novalidate enctype="multipart/form-data">
					<ul>
						<li><input type="file" name="orgPic" id="input_pic_upload" accept="image/*" style="display:none"/></li>
						<li><input id='inputId' name="id" type="text" value="${detail.id}" style="display:none"></li>
						<li><label><spring:message code="zuzhi"  />名称</label><input name="name" id='inputName' class=" form-control"
							type="text" data-type="name" value="${detail.name}" disabled></li>
						<li><label>联系人</label><input id='inputContacter' name="contacter" class=' form-control' type='text'
						data-type='name' value="${detail.contacter }" disabled></li>
						<li><label>联系电话</label><input id='inputPhone' name="phone" class=' form-control' type='text'
						data-type='name' value="${detail.phone }" disabled></li>
						<li><label><spring:message code="zuzhi"  />简介</label>
						<textarea id='inputDescription' name="description" class=" form-control" disabled>${detail.description}</textarea></li>
						<button disabled id='OrgBtn'  class="btn green btn-common" disabled="disabled">确定</button>
					</ul>
				</form>
			</div>
			<div
				class="organization-right organization-border corner-3 form-setting ">
				<myshiro:hasPermission name="<%=PermitConstant.ORG_SET_QUIT %>" objectId="${detail.id}" >
      					<h4>退出<spring:message code="zuzhi"  /></h4>
						<p>一旦你退出了该<spring:message code="zuzhi"  />，你将不能查看任何关于该<spring:message code="zuzhi"  />的信息。退出<spring:message code="zuzhi"  />后，如果想重新加入，请联系<spring:message code="zuzhi"  />的拥有者或者管理员。</p>
						<button id="exit" class="btn red btn-common">退出<spring:message code="zuzhi"  /></button>
      			</myshiro:hasPermission>
      			<myshiro:hasPermission name="<%=PermitConstant.ORG_SET_DELETE %>" objectId="${detail.id}" >
      					<h4>删除<spring:message code="zuzhi"  /></h4>
						<p>一旦你删除了<spring:message code="zuzhi"  />，<spring:message code="zuzhi"  />内所有<spring:message code="xiangmu"  />、<spring:message code="tuandui"  />、<spring:message code="chengyuan"  />，<spring:message code="xiangmu"  />中所有内容等都将会被永久删除。这是一个不可恢复的操作，请谨慎对待！</p>
						<button id="deleteOrg" class="btn red btn-common">删除<spring:message code="zuzhi"  /></button>
      			</myshiro:hasPermission>
			</div>
		</div>

	<script src="${context }/static/script/organize/organizationSetting.js"></script>
</body>
</html>
<%@ include file="/WEB-INF/resource/jsp/home/footer.jsp"%>
