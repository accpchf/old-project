<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="../home/headerLibs.jsp" %>

<script type="text/javascript">
window.readOnly = false;
var PRJ_ACCESS = false, PRJ_OPERAT = false;
<myshiro:hasPermission name="<%=PermitConstant.PRJ_ACCESS %>" objectId="${project.projectId}">
	PRJ_ACCESS = true;
</myshiro:hasPermission>

<myshiro:hasPermissionByList name="hasPermissionByList" permitCodeAndIds="${permitCodeAndIds}" >
	PRJ_OPERAT = true;
</myshiro:hasPermissionByList>
<status:permission status="${project.status }">
	window.readOnly = true;
</status:permission>
</script>

<body class="project vertical-scroll">
	<div class="main-content filelibrary-content">
		
	    <div class="file-library auto-content boardBackground">
	    	 <div class="title">
	         	 <h4>文件库</h4>　
	             <span class="route"></span>
	             <div class="button-operate">
	             <status:permission status="${project.status }">
	             	<myshiro:hasPermission name="<%=PermitConstant.PRJ_ACCESS %>" objectId="${project.projectId}">
	              		<button class="btn button_new_folder"><a><i class="icon-folder-add"></i>新建文件夹</a></button>
	              	</myshiro:hasPermission>
	              	<myshiro:hasPermission name="<%=PermitConstant.PRJ_ACCESS %>" objectId="${project.projectId}">
	             		<button class="btn button_upload_file"><a><i class=" icon-upload-3"></i>上传</a></button>
	             	</myshiro:hasPermission>
	             </status:permission>
				</div>
	         </div>
	         <table class="table">
	           <thead> 
	              <tr>
	                 <th>名称</th>
	                 <th>创建者</th>
	                 <th>更新时间</th>
	                 <th>操作</th>
	              </tr>
	           </thead>
	           <tbody class="tbody_file_manage">
	           </tbody>
	        </table>　
	    </div>	
		<!-- 添加附件表单 -->
	    <form class="file_upload_form tohide" action="${context}/filelibrary/uploadfile.htm" method="post" enctype="multipart/form-data">
	      	<input type="file" name="upload_file" class="file_upload_input"/>
	      	<input type="text" name="folderId" class="file_library_input"/>
	      	<input type="text" name="projectId"  class="file_project_input"/>
	    </form>
	    <!-- 下载附件表单 -->
	    <form class="file_download_form tohide" action="${context}/filelibrary/downloadfile.htm" method="post">
	    	<input type="hidden" name="remove_cache" value="<%= (new Date()).getTime()%>"/>
	    	<input type="hidden" name="fileUrl"/>
	    </form>
	</div>
<script type="text/javaScript" src="${context}/static/script/fileLibrary/fileLibrary.js"></script>
</body>