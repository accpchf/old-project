<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="../home/header.jsp" %>
<div id="pesonsetting_div" class="windows-popup boardBackground form-setting ">
    <div class="img-block person-img">
        <a class="person-header"><img  id="headerpic_img" src="<%=context%>/static/images/head-portraits-chart.png" class="img-circle " /> <span class="boardBackground"><i class="icon-pencil-1" title="修改"></i></span></a>
        <h4>个人设置</h4>
        <i class="icon-cancel-circled shut"></i>
    </div>
    <ul id="setting_type_ul" class="nav nav-tabs nav-justified">
       <li class="active" data-type="person"><a href="#" id="persion_setting_li">个人设置</a></li>
       <li data-type="safety"><a href="#">安全设置</a></li>
    </ul>
    <form class="personal_form" novalidate enctype="multipart/form-data">
	    <ul class="setting_children">
	    	<input type="file" name="headerPic" id="input_pic_upload" accept="image/*" style="display:none"/>
	        <li><label>姓名</label><input name="name" class=" form-control" type="text" placeholder="请输入姓名" ></li>
	        <li><label>性别</label>
	        	<select class=" form-control" name="gender">
	        	</select>
	        </li>
	        <li><label>手机号码</label><input name="mobile" class=" form-control" type="text" placeholder="请输入手机号码" ></li>
	        <li><label>职位</label><input name="position" class=" form-control" type="text" placeholder="请输入职位" ></li>
	        <button name="button" class="btn green btn-common">确定</button>
	    </ul>
    </form>
    <form class="safety_form" novalidate enctype="">
	    <ul class="setting_children tohide">
	        <li><label>用户名</label><label name="account"></label></li>
	        <li><label>原密码</label><input name="password" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')" class=" form-control" type="password" placeholder="请输入原密码" ></li>
	        <li><label>新密码</label><input name="newpwd" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')" class=" form-control" type="password" placeholder="请输入新密码" ></li>
	        <li><label>确认密码</label><input name="confirmpwd" onkeyup="this.value=this.value.replace(/^ +| +$/g,'')"  class=" form-control" type="password" placeholder="请再次输入新密码" ></li>
	        <button name="button" class="btn green btn-common">确定</button>
	    </ul>
    </form>
   <div id="activateDiv" style="display: none">
   <p style="margin-bottom:0; line-height:24px;">亲爱的用户，您的用户名还<span class="red">未激活</span>，<br/>如果忘记密码了，将不能通过邮箱来找回密码，也不能使用邮件提醒功能。 </p>
        <button id="activateUser" class="btn green-word">点击激活</button>
</div> 
    
</div>
<script src="${context }/static/plugins/md5/md5.js"></script>
<script type="text/javascript" src="${context}/static/script/personalSetting/personalSetting.js"></script>
