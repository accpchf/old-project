$(function(){
	var reForm = $("#register_form"),
    reUNinput = $("#register_username_input", reForm),
    reNameInput = $("#register_name_input", reForm),
    rePwdInput = $("#register_password_input", reForm),
    reCPwdInput = $("#register_confirmpwd_input", reForm),
    reIsCrOrChec = $("#is_createorganize_checkbox", reForm),
    reOrNaInput = $("#organize_name_input", reForm),
    registerBtn = $("#register_btn", reForm);
	var userNameValida = new InputValidate(reUNinput, true, 30, 0, 'email', null),
		nameValida = new InputValidate(reNameInput, true, 30, 0, '', null),
		passwordValida = new InputValidate(rePwdInput, true, 30, 0, 'password', null),
		confirmPwdValida = new InputValidate(reCPwdInput, true, 30, 0, 'password', null, rePwdInput),
		organizeNameValida = new InputValidate(reOrNaInput, true, 30, 0, '', null);
	    
	//点击是否创建组织按钮，确定是否创建组织
	reIsCrOrChec.on("click", function(){
		var self = $(this);
		if(self.find("i.tohide").length > 0){
			self.find("i").removeClass("tohide");
			reOrNaInput.removeClass("tohide error").val("");
		}else{
			self.find("i").addClass("tohide");
			reOrNaInput.addClass("tohide");
		}
	});
	
	
	registerBtn.on("click", function(evt){
			//阻止表单按钮点击的默认行为
		EventUtil.preventDefault(evt);
		var dataParam = {},
			formData = reForm.formToArray(),
			self = this;
		$(self).prop("disabled",true);
		$.each(formData, function(key, value){
			dataParam[value["name"]] = value["value"];
		});
		dataParam.password = hex_hmac_md5(dataParam.account,dataParam.password);
			$.ajax({
			type:'post',
			url:context + '/user/registerIn.htm',
			data:dataParam,
			beforeSend:function(){
				if(!userNameValida.checkValidate() 
					|| !nameValida.checkValidate()
					|| !passwordValida.checkValidate()
					|| !confirmPwdValida.checkValidate()
					|| (reIsCrOrChec.find("i.tohide").length <= 0 && !organizeNameValida.checkValidate())){
					$(self).prop("disabled",false);
					return false;
				}
				
				return true;
			},
			success:function(resultData){
				if(resultData && resultData.success){
					window.location = context + "/user/registerSuccess.htm?account=" + reUNinput.val();
				}else if(resultData && !resultData.success && resultData.errormsg){
					layer.alert(resultData.errormsg, 1);
				}else{
					layer.alert('请求失败', 1);
				}
				$(self).prop("disabled", false);
			},
			error:function(){
				layer.alert('请求失败', 1);
				$(self).prop("disabled", false);
			}
		});
	});
});