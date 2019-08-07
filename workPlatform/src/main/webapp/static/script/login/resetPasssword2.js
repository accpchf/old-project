/**
 * 重置密码js
 */

$(function($){
	var resetPasswordForm = $("#resetPassword2Div"),
		account = resetPasswordForm.data("account"),
		password = $('#password', resetPasswordForm),
		rePassword = $('#rePassword', resetPasswordForm),
		resetPassword = $('#resetPassword', resetPasswordForm),
		passwordValida = new InputValidate(password, true, 30, 0, 'password', null),
		confirmPwdValida = new InputValidate(rePassword, true, 30, 0, 'password', null, password);
	
	//重置密码
	resetPassword.on('click', function(evt){
		//阻止表单按钮点击的默认行为
		EventUtil.preventDefault(evt);
		var dataPram = {
				password:hex_hmac_md5(account,password.val()),
				account:account
			},
			self = this;
		$(self).prop("disabled",true);
		$.ajax({
			type:'post',
			url:context + '/user/updatePassword.htm',
			data:dataPram,
			beforeSend:function(){
				if(!passwordValida.checkValidate() || !confirmPwdValida.checkValidate()){
					$(self).prop("disabled",false);
					return false;
				}
				return true;
			},
			success:function(resultData){
				if(resultData && resultData.success){
					window.location = context + "/user/passwordSuccess.htm";
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