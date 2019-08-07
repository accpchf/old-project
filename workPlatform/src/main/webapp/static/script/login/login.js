/**
 * 登录界面js
 */

$(function($){
	var loginForm = $("#login_form"),
		loginUserInput = $('#login_user_input', loginForm),
		loginPasswordInput = $('#login_password_input', loginForm),
		loginSubmitBtn = $('#login_submit_btn', loginForm),
		userInputV = new InputValidate(loginUserInput, true, 30, 0, 'email', null),
		passwordInputV = new InputValidate(loginPasswordInput, true, 30, 0, '', null);
	
	//登录
	loginSubmitBtn.on('click', function(evt){
		//阻止表单按钮点击的默认行为
		EventUtil.preventDefault(evt);
		
		var dataPram = {
				account:loginUserInput.val(),
				password:hex_hmac_md5(loginUserInput.val(),loginPasswordInput.val())    
			},
			self = this;
		$(self).prop("disabled",true);
		$.ajax({
			type:'post',
			url:context + '/user/loginIn.htm',
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
					window.location = context + "/project/home.htm";
				}else if(resultData && !resultData.success && resultData.errormsg){
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
});