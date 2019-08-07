/**
 * 通过url加入组织
 * */
$(function(){
	var loginForm = $("#login_form"),
		loginUserInput = $('#login_user_input', loginForm),
		loginPasswordInput = $('#login_password_input', loginForm),
		agreeBtn = $('#agree_btn', loginForm),//同意按钮
		agreeLoginBtn = $('#login_agree_btn', loginForm),//同意并登录按钮
		userInputV = new InputValidate(loginUserInput, true, 30, 0, 'email', null),
		passwordInputV = new InputValidate(loginPasswordInput, true, 30, 0, '', null);
	var orgName  = $('h5#orgName').text();
	if(!isLogin && agreeLoginBtn.length >= 1){
		//同意并登录
		agreeLoginBtn.on('click', function(evt){
			//阻止表单按钮点击的默认行为
			EventUtil.preventDefault(evt);
			var dataPram = { 
					type:"nologin",
					code:code,
					account:loginUserInput.val(),
					password:hex_hmac_md5(loginUserInput.val(),loginPasswordInput.val()) 
				},
				self = this;
			$(self).prop("disabled",true);
			$.ajax({
				type:'post',
				url:context + '/user/agreetojoinorg.htm',
				data:dataPram,
				beforeSend:function(){
					if(!userInputV.checkValidate() || !passwordInputV.checkValidate()){
						$(self).prop("disabled",false);
						return false;
					}
					return true;
				},
				success:function(resultData){
					if(resultData && resultData.success){
						window.location = context + "/login/tojoinsuccess.htm?name="+orgName;
					}else if(resultData && !resultData.success){
						if(resultData.errorType == "error_1"){
							window.location = context + "/login/tojoinfailure.htm";
							return;
						}
						if(resultData.errorType == "error_2"){
							window.location = context + "/login/userexistOrg.htm?name="+orgName;
							return false;
						}
						layer.alert(resultData.errormsg, 1);
					}else{
						layer.alert('请求失败', 1);
					};
					$(self).prop("disabled",false);
				},
				error:function(){
					layer.alert('请求失败', 1);
					$(self).prop("disabled",false);
				}
			});
		});
	}
	if(isLogin && agreeBtn.length >= 1){
		//同意
		agreeBtn.on('click', function(evt){
			//阻止表单按钮点击的默认行为
			EventUtil.preventDefault(evt);
			var self = this;
			$(self).prop("disabled",true);
			$.ajax({
				type:'post',
				url:context + '/login/agreetojoinorg.htm',
				data:{code:code, type:"haslogin"},
				success:function(resultData){
					if(resultData && resultData.success){
						window.location = context + "/login/tojoinsuccess.htm?name="+orgName;
					}else if(resultData && !resultData.success){
						if(resultData.errorType == "error_1"){
							window.location = context + "/login/tojoinfailure.htm";
							return;
						}
						if(resultData.errorType == "error_2"){
							window.location = context + "/login/userexistOrg.htm?name="+orgName;
							return false;
						}
						layer.alert(resultData.errormsg, 1);
					}else{
						layer.alert('请求失败', 1);
					};
					$(self).prop("disabled",false);
				},
				error:function(){
					layer.alert('请求失败', 1);
					$(self).prop("disabled",false);
				}
			});
		});
	}
});