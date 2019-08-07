/**
个人信息设置
*/
require(["script/common/dialogAlert",'jquery', 'jqueryForm', 'layer'], function(dialogAlert){
	var personSettingInfo;
	personSettingInfo = {
			//页面初始化
			init:function(){
				//tabs栏（个人设置和安全设置）默认选择个人设置
				this.panel = $("#pesonsetting_div");
				this.settingTypeUl = $("#setting_type_ul", this.panel);
				this.loginUserInfo = loginUserInfo;
				this.setEvent();
				this.personValiArray = {};
				this.safeValiArray = {};
				//为页面input添加事件，当赋值为"null"时，改为""
				this.panel.on("removenull", "input", function(){
					if(!$(this).val() || $(this).val() == "null"){
						$(this).val("");
					}
				});
				//个人设置页面信息初始化
				this.personalPageInit();
				
				
			},
			//添加页面元素事件监听
			setEvent:function(){
				var self = this;
				if(self.loginUserInfo.action == "false"){
					self.panel.find("#activateDiv").show();
				}
				this.settingTypeUl.on('click', 'li', function(){
					var type = $(this).siblings("li").removeClass("active").end()
						   			  .addClass("active").data("type");
					self.hideOrShowSettingPage(type);
				});
				
				/*//头像撤销
				this.panel.on('click', 'i.icon-ccw', function(){
					if(loginUserInfo.logo && loginUserInfo.logo != "null"){
						self.panel.find("#headerpic_img").attr('src',loginUserInfo.logo);
					}else{
						self.panel.find("#headerpic_img").attr('src',context + '/static/images/head-portraits-chart.png');
					}
					var $self = self.panel.find('form.personal_form input[type="file"]');
					$self.after($self.clone(true).val(''));
	    			$self.remove();
	    			return false;
				});*/
				
				//头像选择事件监听
				this.panel.on('change', 'form.personal_form input[type="file"]', function(){
					var $self = $("#input_pic_upload");
			    	var files = this.files;
			    	if(files.length != 0){
			    		var file = files[0],
			    		reader = new FileReader();
			    		if(!reader){
			    			dialogAlert.alert('该浏览器不支持头像上传');
			    			return;
			    		}
			    		var rules = new RegExp('image/(png|jpeg|jpg|gif|)', 'g');
			    		if(!rules.test(file.type)){
			    			dialogAlert.alert('请选择正确格式的图片');
			    			$self.after($self.clone(true).val(''));
			    			$self.remove();
			    			return;
			    		}
			    		if(file.limitSize('1', 'MB')){
			    			$self.after($self.clone(true).val(''));
			    			$self.remove();
			    			dialogAlert.alert('头像太大，应1MB以内');
			    			return;
			    		}
			    		reader.onload = function(event){
			    			var target = EventUtil.getTarget(event);
			    			self.panel.find('#headerpic_img').prop('src', target.result);
			    		};
			    		reader.readAsDataURL(file);
			    	}
				});
				this.panel.on('click', 'form button[name="button"]', function(event){
					EventUtil.preventDefault(event);
					var selfBtn = $(this),
						options = {},
						selfForm = selfBtn.closest("form"),
						isPersionSett = selfForm.hasClass('personal_form');
					selfBtn.prop('disabled', true);
					var dataParam ={};
					if(isPersionSett){
						dataParam =  new FormData(selfForm[0]);
						dataParam.append("account", loginUserInfo.account);
						options.validateArray = self.personValiArray;
						options.url = context + "/login/updatePersionInfo.htm";
					}else{
						dataParam =  new FormData();
						dataParam.append("account", loginUserInfo.account);
						var data = {},
						formData = selfForm.formToArray();
						$.each(formData, function(key, value){
							data[value["name"]] = value.value;
						});
						data.password = hex_hmac_md5(loginUserInfo.account,data.password);
						dataParam.append("password", data.password);
						data.newpwd = hex_hmac_md5(loginUserInfo.account,data.newpwd);
						dataParam.append("newpwd", data.newpwd);
						options.validateArray = self.safeValiArray;
						options.url = context + "/login/updateSafeInfo.htm";
						
					}
					//信息修改提交
					$.ajax({
						type:'post',
						url:options.url,
						data:dataParam,
						contentType:false,  
				        processData:false,
						beforeSend:function(){
							var validate = false;
							$.each(options.validateArray, function(key, value){
								if(!value.checkValidate()){
									validate = true;
									return false;
								}
							});
							if(validate){
								selfBtn.prop('disabled', false);
								return false;
							}
							return true;
						},
						success:function(result){
							if(result && result.success){
								var d = result.resultData;
								var a = self.loginUserInfo;
								if(isPersionSett){
									a.name = d.name;
									a.mobile = d.mobile;
									a.postion = d.positon;
									a.gender = d.gender;
									a.logo = d.logo;
									layer.alert('个人信息修改成功', 1, '信息', function(e){
										window.location = context + "/project/home.htm";
									});
									//layer.close(self.currentLayer);
								}else{
									layer.alert('密码修改成功，请重新登录', 1, '信息', function(a){
										window.location = context + "/login/loginout.htm";
									});
								}
								
							}else if(result && !result.success && result.errormsg){
								dialogAlert.alert(result.errormsg,5);
//								layer.alert(result.errormsg, 1, '信息', function(index){
//									layer.close(index);
//								});
							}else{
								layer.alert('请求失败', 1);
							}
							selfBtn.prop('disabled', false);
						},
						error:function(result){
							layer.alert('请求失败', 1);
							selfBtn.prop('disabled', false);
						}
					});
					
				});
				this.panel.on("click","#activateUser",function(){
					var selfBtn = $(this);
					selfBtn.prop('disabled', false);
					$.ajax({
						type:'post',
						url:context + "/user/activateAccount.htm",
						data:{account:self.loginUserInfo.account},
						success:function(result){
							if(result && result.success){
								var resultData = result.resultData;
								dialogAlert.alert(resultData,9);
								return false;
							}else if(result && !result.success && result.errormsg){
								dialogAlert.alert(result.errormsg,5);
//								layer.alert(result.errormsg, 1, '信息', function(index){
//									layer.close(index);
//								});
							}else{
								layer.alert('请求失败', 1);
							}
							return false;
							selfBtn.prop('disabled', false);
						},
						error:function(result){
							layer.alert('请求失败', 1);
							selfBtn.prop('disabled', false);
						}
					});
					
				});
				
			},
			
			//个人设置页面和安全设置页面切换，type：person和safety
			hideOrShowSettingPage:function(type){
				if(type == "person"){
					this.panel.find(".setting_children").addClass("tohide")
						 .end()
						 .find(".setting_children:eq(0)").removeClass("tohide");
					this.personalPageInit();
					return;
				}else if(type == "safety"){
					this.panel.find(".setting_children").addClass("tohide")
						 .end()
						 .find(".setting_children:eq(1)").removeClass("tohide");
					this.safetyPageInit();
					return;
				}else{
					return;
				};
			},
			//个人设置页面初始化
			personalPageInit:function(){
				var self = this;
				var nameInput = this.panel.find("form.personal_form input[name='name']"),
					genderInput = this.panel.find("form.personal_form select[name='gender']"),
					mobileInput = this.panel.find("form.personal_form input[name='mobile']"),
					positionInput = this.panel.find("form.personal_form input[name='position']");
				//给页面输入框赋值
				nameInput.val(loginUserInfo.name).trigger("removenull");
				mobileInput.val(loginUserInfo.mobile).trigger("removenull");
				positionInput.val(loginUserInfo.position).trigger("removenull");
				//头像选择相关处理
				self.panel.find("span.boardBackground").show();
				self.panel.find(".icon-pencil-1").unbind('click').click(function(event){
					self.panel.find('form.personal_form input[type="file"]').click();
					//return false;
				});
				if(loginUserInfo.logo && loginUserInfo.logo != "null"){
					this.panel.find("#headerpic_img").attr('src',loginUserInfo.logo);
				}else{
					this.panel.find("#headerpic_img").attr('src',context + '/static/images/head-portraits-chart.png');
				}
				var $self = this.panel.find('form.personal_form input[type="file"]');
				$self.after($self.clone(true).val(''));
    			$self.remove();
				
				//添加输入校验
				if(!this.personValiArray.name){
					this.personValiArray.name = new InputValidate(nameInput, true, 30, 0, '', null);
				}else{
					this.personValiArray.name.target.removeClass("error");
				}
				if(!this.personValiArray.mobile){
					this.personValiArray.mobile = new InputValidate(mobileInput, false, 30, 0, 'telphone', null);
				}else{
					this.personValiArray.mobile.target.removeClass("error");
				}
				if(!this.personValiArray.position){
					this.personValiArray.position = new InputValidate(positionInput, false, 30, 0, '', null);
				}else{
					this.personValiArray.position.target.removeClass("error");
				}
				$.post(context + "/personal/listSysStatus.htm", function(result){
					genderInput.empty();
					if(result && result.success){
						var data = result.resultData;
						$.each(data, function(key, value){
							var $optionHtml = $("<option/>").val(value.code).html(value.text);
							genderInput.append($optionHtml);
						});
						genderInput.val(loginUserInfo.gender);
					}
				});
			},
			//安全设置页面初始化
			safetyPageInit:function(){
				var self = this;
				var accountLabel = this.panel.find("form.safety_form label[name='account']"),
					passwordInput = this.panel.find("form.safety_form input[name='password']"),
					newpwdInput = this.panel.find("form.safety_form input[name='newpwd']"),
					confirmpwdInput = this.panel.find("form.safety_form input[name='confirmpwd']");
				//给页面赋值
				accountLabel.empty().html("&nbsp;&nbsp;" + loginUserInfo.account);
				passwordInput.val("");
				newpwdInput.val("");
				confirmpwdInput.val("");
				self.panel.find("span.boardBackground").hide();
				self.headerVlaue = null;
				if(loginUserInfo.logo && loginUserInfo.logo != "null"){
					this.panel.find("#headerpic_img").attr('src',loginUserInfo.logo);
				}else{
					this.panel.find("#headerpic_img").attr('src',context + '/static/images/pic-project2.jpg');
				}
				
				//添加输入校验
				if(!this.safeValiArray.password){
					this.safeValiArray.password = new InputValidate(passwordInput, true, 30, 0, '', null);
				}else{
					this.safeValiArray.password.target.removeClass("error");
				}
				if(!this.safeValiArray.newpwd){
					this.safeValiArray.newpwd = new InputValidate(newpwdInput, true, 30, 0, 'password', null);
				}else{
					this.safeValiArray.newpwd.target.removeClass("error");
				}
				if(!this.safeValiArray.confirmpwd){
					this.safeValiArray.confirmpwd = new InputValidate(confirmpwdInput, true, 30, 0, 'password', null, newpwdInput);
				}else{
					this.safeValiArray.confirmpwd.target.removeClass("error");
				}
			}
		
	};
	personSettingInfo.init();
});