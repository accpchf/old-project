/**
 * 重置密码js
 */

$(function(){
	var resetPasswordForm = $("#resetPasswordDiv"),
		resetPassword = $('#resetPassword', resetPasswordForm),
		accountInput = $('#account', resetPasswordForm),
		userInputV = new InputValidate(accountInput, true, 30, 0, 'email', null);
	
	//重置密码
	resetPassword.on('click', function(evt){
		//阻止表单按钮点击的默认行为
		EventUtil.preventDefault(evt);
		var dataPram = {
				account:accountInput.val()
			},
			self = this;
		$(self).prop("disabled",true);
		$.ajax({
			type:'post',
			url:context + '/user/reset.htm',
			data:dataPram,
			beforeSend:function(){
				if(!userInputV.checkValidate()){
					$(self).prop("disabled",false);
					return false;
				}
				return true;
			},
			success:function(resultData){
				if(resultData && resultData.success){
					window.location = context + "/user/resetPasswordNext.htm?account=" + accountInput.val();
				}else if(resultData && !resultData.success && resultData.errormsg){
					layer.alert(resultData.errormsg);
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