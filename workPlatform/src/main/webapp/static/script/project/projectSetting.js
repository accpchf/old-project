require(['jquery',"script/common/dialogAlert",'jqueryForm', 'layer','my97'], function($,dialogAlert){
	var projectSettingHtml;
	projectSettingHtml = {
			init:function(){
				this.prologo = "";
				this.projectSetting = $("#projectSetting");//项目设置容器
				this.navJustified = $(".nav-justified",this.projectSetting);
				this.panel = $(".main-project");//主界面
				this.proInfo = $("#proInfo",this.projectSetting);
				this.projectId = this.proInfo.find("#proId");
				this.linkBtn = $("#link_btn",this.projectSetting);
				this.closeLink = $("#close_link",this.projectSetting);
				this.openLink = $("#open_link",this.projectSetting);
				this.email = $("#add_user_ByEmail",this.projectSetting);
				this.teamsList = $("#teams_list ul",this.projectSetting);
				this.orgId = this.projectSetting.find("#orgId").attr("value");
				this.proInfoValiArray = {};
				this.initProInfo();
				this.setEvent();
				this.userManBtn();//成员管理界面按钮
				this.orgProUserMan();
			},
			initProInfo:function(){//项目设置页面初始化以及项目设置页面的点击事件
				var self = this,
					prologo = $("#proLogo", this.projectSetting),
					updateProInfo = $("#update_pro_info",  this.projectSetting),
					//inputLogo = $("#inputLogo",this.projectSetting),
				    name = self.proInfo.find("#proName"),
					description = self.proInfo.find("#description");
				self.prologo = $(".img120", this.projectSetting).attr("src");
				if(!self.proInfoValiArray.name){
					self.proInfoValiArray.name = new InputValidate(name, true, 30, 0, 'text', null);
				}else{
					self.proInfoValiArray.name.target.removeClass("error");
				}
				if(!self.proInfoValiArray.description){
					self.proInfoValiArray.description = new InputValidate(description, false, 500, 0, '', null);
				}else{
					self.proInfoValiArray.description.target.removeClass("error");
				}
				//logo切换事件
				prologo.on("click",function(event){
					$(this).prop('disabled', true);
					$("#inputLogo",self.projectSetting).trigger("click");
				});
				$("#inputLogo",this.projectSetting).change(function(event){
					var $self = $(this);
			    	var files = this.files;
			    	if(files.length != 0){
			    		var file = files[0],
			    		reader = new FileReader();
			    		if(!reader){
			    			dialogAlert.alert('该浏览器不支持头像上传');
			    			return false;
			    		}
			    		var rules = new RegExp('image/(png|jpeg|jpg|gif|img|)', 'g');
			    		if(!rules.test(file.type)){
			    			dialogAlert.alert('请选择正确格式的图片');
			    			$self.after($self.clone(true).val(''));
			    			$self.remove();
			    			$(".img120", this.projectSetting).attr("src",self.prologo);
			    			return false;
			    		}
			    		if(file.limitSize('65', 'KB')){
			    			$self.after($self.clone(true).val(''));
			    			$self.remove();
			    			dialogAlert.alert('图片太大，应65KB以内');
			    			$(".img120", this.projectSetting).attr("src",self.prologo);
			    			return false;
			    		}
			    		reader.onload = function(event){
			    			var target = EventUtil.getTarget(event);
			    			$(".img120").prop('src', target.result);
			    		};
			    		reader.readAsDataURL(file);
			    		}
			    	prologo.prop('disabled', false);
				});
				updateProInfo.on("click" , function(){
					//var project = self.panel.find("div.project-block[data-projectid='"+self.projectId.attr("value")+"']");
					var options = {
						    beforeSubmit: function() {
						    	var validate = false;
								$.each(self.proInfoValiArray, function(key, value){
									if(!value.checkValidate()){
										validate = true;
										return false;
									}
								});
								if(validate){
									updateProInfo.prop('disabled', false);
									return false;
								}
								return true;
						    },
							success : function(resultData){
								if(resultData && resultData.success){
									window.location = context + "/project/home.htm";
									return false;
								}else if(resultData && !resultData.success){
									layer.alert(resultData.errormsg, 1);
								}else{
									layer.alert(resultData, 1);
								};
							},
							error:function(){
								layer.alert('请求失败', 1);
							}
					};
					$('#updateProjectForm',self.projectSetting).ajaxSubmit(options);
					return false;
				});
				$("#input_logo").val($("#proLogo").attr('src'));
				return false;
			},
			setEvent:function(){
				var self = this;
				self.navJustified.on('click', 'li', function(){
					if($(this).hasClass('active')){
						return false;
					}
					var type = $(this).siblings("li").removeClass("active").end()
						   			  .addClass("active").data("type");
					self.hideOrShowSettingPage(type);
					return false;
				});
			},
			//项目信息 成员管理 高级设置之间的切换
			hideOrShowSettingPage:function(type){
				var self = this,
					advancedSettings = self.projectSetting.find("#advanced-settings"),
					addMember = self.projectSetting.find("#add-member");
					
				if(type == "projectInfo"){
					self.projectSetting.find("span.boardBackground").show();
					self.proInfo.removeClass("tohide");
					addMember.addClass("tohide");
					advancedSettings.addClass("tohide");
					return false;
				}else if(type == "userMan"){
					self.projectSetting.find("span.boardBackground").hide();
					self.proInfo.addClass("tohide");
					advancedSettings.addClass("tohide");
					self.initUserMana();//初始化成员管理界面--关闭或者打开链接
					self.usersList();//项目人员信息显示
					addMember.removeClass("tohide");
					if(self.orgId == 0 ){//个人项目
						self.projectSetting.find("#user_list").removeClass("select");
						self.projectSetting.find("#addOrgUser").addClass("tohide");
						self.projectSetting.find("#teamUserList").addClass("tohide");
					}else{//组织项目
						self.projectSetting.find("#teamUserList").removeClass("tohide");
						self.projectSetting.find("#user_list").addClass("select");
						self.projectSetting.find("#addOrgUser").removeClass("tohide");
						self.initTeam();
						if($('#addOrgUser').hasClass('select')){
							$('#user_list').removeClass('select');
						}
						return false;
					}
					
					return false;
				}else{
					self.projectSetting.find("span.boardBackground").hide();
//					$("#projectSetting").find("span.boardBackground").hide();
					advancedSettings.removeClass("tohide");
					self.proInfo.addClass("tohide");
					addMember.addClass("tohide");
					self.userOption();
					return false;
				}
			},
			//高级设置页面操作
			userOption:function(){
				var self = this,
					exitPro = $("#exit_pro_btn",self.projectSetting),
					delePro = $("#delete_pro_btn",self.projectSetting),
					suspendPro = $("#suspend_pro_btn",self.projectSetting),
					restartPro = $("#restart_pro_btn",self.projectSetting),
					finishPro = $("#finish_pro_btn",self.projectSetting),
					proId = self.projectId.val();
				finishPro.on("click",function(){
					layer.confirm('是否要完成该项目',function(index){
						layer.close(index);
						$.ajax({
							type:'post',
							url:context + '/project/finishPro.htm',
							data:{proId:proId},
							success:function(result){
								if(result && result.success){
									$('#projectSettingBootstrapLayer').modal('hide');
									window.location = context + "/project/home.htm";
									return false;
								}else{
									layer.msg("请求失败");
								}
							},
							error:function(result){
								layer.alert('请求失败', 1);
							}
						});
					},function(index){
						layer.close(index);
					});
					return false;
				});
				restartPro.on("click",function(){
					layer.confirm('是否要重启该项目',function(index){
						layer.close(index);
						$.ajax({
							type:'post',
							url:context + '/project/restartPro.htm',
							data:{proId:proId},
							success:function(result){
								if(result && result.success){
									$('#projectSettingBootstrapLayer').modal('hide');
									window.location = context + "/project/home.htm";
									return false;
								}else{
									layer.msg("请求失败");
								}
							},
							error:function(result){
								layer.alert('请求失败', 1);
							}
						});
					},function(index){
						layer.close(index);
					});
					return false;
				});
				suspendPro.on("click",function(){
					layer.confirm('是否要暂停项目',function(index){
						layer.close(index);
						$.ajax({
							type:'post',
							url:context + '/project/suspendPro.htm',
							data:{proId:proId},
							success:function(result){
								if(result && result.success){
									layer.closeAll();
									$('#projectSettingBootstrapLayer').modal('hide');
									window.location = context + "/project/home.htm";
									return false;
								}else{
									layer.msg("请求失败");
								}
							},
							error:function(result){
								layer.alert('请求失败', 1);
							}
						});
					},function(index){
						layer.close(index);
					});
					return false;
				});
				exitPro.on("click",function(){
					layer.confirm('是否要退出该项目',function(index){
						layer.close(index);
						$.ajax({
			        		type:'post',
							url:context + '/project/exitPro.htm',
							data:{proId:proId},
							success: function (result) {
						    	  if(result && result.success){
						    		  $('#projectSettingBootstrapLayer').modal('hide');
						    		  window.location = context + "/project/home.htm";
						    		  return false;
						    	  }else if(result && !result.success){
										layer.msg(result.errormsg);
								  }else{
										layer.msg("请求失败");
								  }
							},
							error:function(result){
								layer.alert('请求失败', 1);
							}
						});
					},function(index){
						layer.close(index);
					});
					return false;
				});
				delePro.on("click",function(){
					layer.confirm('是否要删除该项目',function(index){
						layer.close(index);
						$.ajax({
			        		type:'post',
							url:context + '/project/deletePro.htm',
							data:{proId:proId},
							success: function (result) {
						    	  if(result && result.success){
						    		  $('#projectSettingBootstrapLayer').modal('hide');
						    		  window.location = context + "/project/home.htm";
										return false;
								  }else{
										layer.msg("请求失败");
									}
						      },
								error:function(result){
									layer.alert('请求失败', 1);
								}      
						});
					},function(index){
						layer.close(index);
					});
					return false;
				});
			},
			initTeam:function(){
				var self = this,
				proId = self.projectId.val();
				//团队人员信息
				$.ajax({
					url : context + "/team/proTeamList.htm",
					type : "post",
					data : {
						projectId : proId
					},
					success : function(result) {
						if(result && result.success){
							var data = result.resultData;
							self.projectSetting.find("#teamUserList").empty();
							if(data.length <=0){
								self.projectSetting.find("#teamUserList").append('<div class="team-number team-common  little-portrait corner-3">还没有'+TEXT_CONFIG.tuandui+'</div>');
								return false;
							}
							$.each(data,function(index, value){
								self.projectSetting.find("#teamUserList").append(self.teamUserList(value));
							});
						}else if(result && !result.success){
							layer.msg(result.errormsg);
						}else{
							layer.msg("请求失败");
						}
					},
					error:function(result){
						layer.alert('请求失败', 1);
					}
					
				});
				return false;
			},
			//成员管理界面
			initUserMana:function(){
				var self = this,
					proId = self.projectId.val();
				$.ajax({
					url : context + "/project/getCode.htm",
					async: false,
					type : "post",
					data : {
						proId : proId
					},
					success : function(result) {
						if(result && result.success){
							var data = result.resultData;
							if(data != "")
							{
								self.linkBtn.text("关闭链接");
								self.openLink.show();
								self.closeLink.hide();
								self.openLink.attr("value",data);
							}else{
								self.linkBtn.text("打开链接");
								self.openLink.hide();
								self.closeLink.show();
							}
						}else if(result && !result.success){
							layer.msg(result.errormsg);
						}else{
							layer.msg("请求失败");
						}
					},
					error:function(result){
						layer.alert('请求失败', 1);
					}
				});
				return false;
			},
			usersList:function(){
				var self = this,userName=loginUserInfo.name,
					proId = self.projectId.val();
				self.teamsList.empty();
				if(loginUserInfo.name.length > 5){
					userName = loginUserInfo.name.substr(0,5)+"...";
				}
				//项目人员信息的显示
				$.ajax({
					url : context + "/project/listProjectUser.htm",
					type : "post",
					data : {
						projectId : proId
					},
					success : function(result) {
						if(result && result.success){
							var data = result.resultData;
							$.each(data,function(index, value){
								if(value.userId != loginUserInfo.userId){
									self.teamsList.append(self.userList(value));
								}else{
									self.teamsList.prepend('<li data-userid = "'+loginUserInfo.userId+'"><img src="'+loginUserInfo.logo+'" class="img-circle headerpic_img" /> <a><span title="'+loginUserInfo.name+'">'+
											userName+'</span><span class="role"></span></a></li>');
									var role = TEXT_CONFIG.chengyuan;
									if(value.prjRoleCode == "PRJ_ADMIN"){
										role = "管理员";
									}else if(value.prjRoleCode == "PRJ_SUPERVISER"){
										role = "监督员";
									}
									self.teamsList.find('li[data-userid="'+loginUserInfo.userId+'"]').find('span.role').html("我（"+role+"）");
								}
							});
						}else if(result && !result.success){
							layer.msg(result.errormsg);
						}else{
							layer.msg("请求失败");
						}
					},
					error:function(result){
						layer.alert('请求失败', 1);
					}
				});
				return false;
			},
			teamUserList:function(value){
				var $team = null;
				$team = $('<div class="team-number team-common  little-portrait corner-3" data-teamid= "'+value.teamId+'"/>');
				if(value.name.length>10){
					$team.append('<i class="icon-trash-empty"></i><h4 title='+value.name+'>'+value.name.substring(0,10)+'...</h4>');
				}else{
					$team.append('<i class="icon-trash-empty"></i><h4 title='+value.name+'>'+value.name+'</h4>');
				}
				
				if(value.users == null){
				}else{
					$.each(value.users, function(k, obj){
						var $html = $('<img title="'+obj.name+'" src="'+obj.logo+'" class="img-circle">');
						$team.append($html);
					});
				}
				return $team;
			},
			userList:function(value){
				var isAdmin = "",isSupervise = "",role = TEXT_CONFIG.chengyuan,userName=value.name;
				if(value.prjRoleCode == "PRJ_ADMIN"){
					role = "管理员";
				}
				if(value.name.length > 5){
					userName = value.name.substr(0,5)+"...";
				}
				if(value.prjRoleCode == "PRJ_SUPERVISER"){
					role = "监督员";
				}	
				var html = $('<li data-userid = "'+value.userId+'"><img src="'+value.logo+'" class="img-circle headerpic_img" /> <a><span title="'+value.name+'">'+
						userName+'</span><span class="role">'+role+'</span></a></li>');
				if(loginUserInfo.userId != value.userId && $("#status").text()=='00700'){
					if(role=="管理员"){
						html.append('<button class="btn pull-right red-outline outer-borer-red removeUser">移除'+TEXT_CONFIG.chengyuan+'</button><span class="pull-right radio-circle setMember"><i class="icon-record-outline"></i>设为成员</span><span class="pull-right radio-circle setSupervise"><i class=" icon-record-outline"></i>设为监督员</span><span class="pull-right radio-circle setAdmin select"><i class="  icon-dot-circled "></i>设为管理员</span>');
					}else if(role=="监督员"){
						html.append('<button class="btn pull-right red-outline outer-borer-red removeUser">移除'+TEXT_CONFIG.chengyuan+'</button><span class="pull-right radio-circle setMember"><i class="icon-record-outline"></i>设为成员</span><span class="pull-right radio-circle setSupervise select"><i class=" icon-dot-circled "></i>设为监督员</span><span class="pull-right radio-circle setAdmin"><i class="icon-record-outline"></i>设为管理员</span>');
					}else{
						html.append('<button class="btn pull-right red-outline outer-borer-red removeUser">移除'+TEXT_CONFIG.chengyuan+'</button><span class="pull-right radio-circle setMember select"><i class="icon-dot-circled"></i>设为成员</span><span class="pull-right radio-circle setSupervise"><i class="icon-record-outline"></i>设为监督员</span><span class="pull-right radio-circle setAdmin"><i class="icon-record-outline"></i>设为管理员</span>')
					}
					
				}
				return html;
			},
			orgProUserMan:function(){
				var self = this;
				$(".member-nav",self.projectSetting).on("click","a",function(){
					if($(this).hasClass('select')){
						return false;
					}
					var userType = $(this).siblings("a").removeClass("select").end()
		   			  .addClass("select").data("type");
					self.hideOrShowUserManaPage(userType);
					return false;
				});
				//添加用户
				$("#userList",self.projectSetting).on("click",".addUserForPro",function(){
					var selfBtn = $(this);
					var userId = $(this).closest("li").data('userid');
					$.ajax({
						url : context + "/project/addUserForPro.htm",
						type : "post",
						data : {
							userId:userId,
							proId:self.projectId.val()
						},
						success : function(result) {
							selfBtn.prop('disabled', false);
							if(result && result.success){
								self.projectSetting.find('li[data-userid="'+ userId+'"] button').remove();
								$('<span class="pull-right">已加入'+TEXT_CONFIG.xiangmu+'</span>').appendTo(self.projectSetting.find('li[data-userid="'+ userId+'"]'));
							}else if(result && !result.success){
								layer.alert(result.errormsg, 1);
							}else{
								layer.alert('请求失败', 1);
							}
							return false;
						},
						error:function(result){
							layer.alert('请求失败', 1);
							selfBtn.prop('disabled', false);
						}
					});
					return false;
				});
				//添加团队
				$("#teams",self.projectSetting).on("click",".addTeamForPro",function(){
					var teamId = $(this).closest("li").data('teamid');
					$.ajax({
						url : context + "/project/addTeamForPro.htm",
						type : "post",
						data : {
							teamId:teamId,
							proId:self.projectId.val()
						},
						success : function(result) {
							if(result && result.success){
								self.projectSetting.find('li[data-teamid="'+ teamId+'"] button').remove();
								self.projectSetting.find('li[data-teamid="'+ teamId+'"]').append('<span class="pull-right  ">已导入</span>');
								return false;
							}else if(result && !result.success){
								layer.alert(result.errormsg, 1);
							}else{
								layer.alert('请求失败', 1);
							}
						},
						error:function(result){
							layer.alert('请求失败', 1);
						}
					});
					return false;
				});
				self.projectSetting.on("click",".addAllUser",function(){
					$.ajax({
						url : context + "/project/addAllUserInOrg.htm",
						type : "post",
						data : {
							orgId: self.orgId,
							proId:self.projectId.val()
						},
						success : function(result) {
							if(result && result.success){
								self.projectSetting.find('div#orgUserList button').remove();
								self.projectSetting.find('div#orgUserList').append('<span class="  pull-right  ">已导入</span>');
							}else if(result && !result.success){
								layer.alert(result.errormsg, 1);
							}else{
								layer.alert('请求失败', 1);
							}
						},
						error:function(result){
							layer.alert('请求失败', 1);
						}
					});
					return false;
				});
			},
			userManBtn:function(){
				var self = this;
				self.linkBtn.off("click");
				self.linkBtn.on("click",function() {
					if($(this).text()== "打开链接"){
						$.ajax({
							url : context + "/project/encrypt.htm",
							type : "post",
							async : false,
							data : {
								proId : self.projectId.val(),
								type : "open"
							},
							success : function(result) {
								if(result && result.success){
									var data = result.resultData;
									self.openLink.show();
									self.closeLink.hide();
									self.openLink.attr("value",data);
									self.linkBtn.text("关闭链接");
								}else if(result && !result.success){
									layer.alert(result.errormsg, 1);
								}else{
									layer.alert('请求失败', 1);
								}
							},
							error:function(result){
								layer.alert('请求失败', 1);
							}
						});
					}else{
						$.ajax({
							url : context + "/project/encrypt.htm",
							type : "post",
							async : false,
							data : {
								proId:self.projectId.val(),
								type : "close"
							},
							success : function(result) {
								if(result && result.success){
									self.openLink.hide();
									self.closeLink.show();
									self.linkBtn.text("打开链接");
								}else if(result && !result.success){
									layer.alert(result.errormsg, 1);
								}else{
									layer.alert('请求失败', 1);
								}
							},
							error:function(result){
								layer.alert('请求失败', 1);
							}
						});
					}
					return false;
				});
				self.email.keydown(function(e){
					if(e.keyCode==13){
						var account = $(this).val();
							$.ajax({
								url : context + "/project/ToJoinProByEmail.htm",
								type : "post",
								data : {
									proId:self.projectId.val(),
									account:account
								},
								success : function(result) {
									if(result && result.success){
										self.email.val("");
										var data = result.resultData;
										self.teamsList.append(self.userList(data));
										return false;
									}else if(result && !result.success){
										layer.alert(result.errormsg);
										self.email.val("");
									}else{
										layer.alert('请求失败', 1);
									}
								},
								error:function(result){
									layer.alert('请求失败', 1);
								}
							});
						return false;
					}
				});
				//移除用户
				self.teamsList.on("click","button.removeUser",function(){
					var userId = $(this).closest("li").data('userid');
					$.ajax({
						url : context + "/project/removeUserByPro.htm",
						type : "post",
						data : {
							proId:self.projectId.val(),
							userId:userId
						},
						success : function(result) {
							if(result && result.success){
								 self.projectSetting.find('li[data-userid="'+ userId+'"]').remove();
							}else if(result && !result.success){
								layer.alert(result.errormsg, 1);
							}else{
								layer.alert('请求失败', 1);
							}
						},
						error:function(result){
							layer.alert('请求失败', 1);
						}
					});
					return false;
				});
				//设置为管理员
				self.teamsList.on("click","span.setAdmin",function(){
					var item = $(this);
					var userId = item.closest('li').data('userid');
					var projectId = self.projectId.val();
					$.ajax({
						url : context + "/project/setAdmin.htm",
						type : "post",
						data : {
							userId:userId,
							projectId:projectId,
						},
						success : function(result) {
							if(result && result.success){
								var parent = item.closest('li');
								parent.children('span').removeClass('select').children().removeClass(' icon-dot-circled ').addClass('icon-record-outline');
								parent.find('span.role').html('管理员');
								item.addClass("select");
								item.children().removeClass('icon-record-outline').addClass(' icon-dot-circled ');
							}else if(result && !result.success){
								layer.alert(result.errormsg, 1);
							}else{
								layer.alert('请求失败', 1);
							}
						},
						error:function(result){
							layer.alert('请求失败', 1);
						}
					});
					return false;
				});
				//设置为监督员
				self.teamsList.on("click","span.setSupervise",function(){
					var item = $(this);
					var userId = item.closest('li').data('userid');
					var projectId = self.projectId.val();
					$.ajax({
						url : context + "/project/setSupervise.htm",
						type : "post",
						data : {
							userId:userId,
							projectId:projectId,
						},
						success : function(result) {
							if(result && result.success){
								var parent = item.closest('li');
								parent.children('span').removeClass('select').children().removeClass(' icon-dot-circled ').addClass('icon-record-outline');
								parent.find('span.role').html('监督员');
								item.addClass("select");
								item.children().removeClass('icon-record-outline').addClass(' icon-dot-circled ');
							}else if(result && !result.success){
								layer.alert(result.errormsg, 1);
							}else{
								layer.alert('请求失败', 1);
							}
						},
						error:function(result){
							layer.alert('请求失败', 1);
						}
					});
					return false;
				});
				//设置为成员
				self.teamsList.on("click","span.setMember",function(){
					var item = $(this);
					var userId = item.closest('li').data('userid');
					var projectId = self.projectId.val();
					$.ajax({
						url : context + "/project/setMember.htm",
						type : "post",
						data : {
							userId:userId,
							projectId:projectId,
						},
						success : function(result) {
							if(result && result.success){
								var parent = item.closest('li');
								parent.children('span').removeClass('select').children().removeClass(' icon-dot-circled ').addClass('icon-record-outline');
								parent.find('span.role').html('成员');
								item.addClass("select");
								item.children().removeClass('icon-record-outline').addClass(' icon-dot-circled ');
							}else if(result && !result.success){
								layer.alert(result.errormsg, 1);
							}else{
								layer.alert('请求失败', 1);
							}
						},
						error:function(result){
							layer.alert('请求失败', 1);
						}
					});
					return false;
				});
				self.projectSetting.on("click",".icon-trash-empty",function(){
					var teamId = $(this).closest("div").data('teamid') ;
					$.ajax({
						url : context + "/project/removeTeamByPro.htm",
						type : "post",
						data : {
							teamId:teamId,
							proId:self.projectId.val()
						},
						success : function(result) {
							if(result && result.success){
								self.projectSetting.find('div[data-teamid="'+ teamId+'"]').remove();
								var child = self.projectSetting.find('#teamUserList').children(".team-number").length;
								if(child <=0){
									self.projectSetting.find("#teamUserList").append('<div class="team-number team-common  little-portrait corner-3">还没有'+TEXT_CONFIG.tuandui+'</div>');
								}
							}else if(result && !result.success){
								layer.alert(result.errormsg, 1);
							}else{
								layer.alert('请求失败', 1);
							}
						},
						error:function(result){
							layer.alert('请求失败', 1);
						}
					});
					return false;
				});
			},
			hideOrShowUserManaPage:function(userType){
				var self = this;
				var teamList = $("#teams_list",self.projectSetting);
				var addOrgList = $("#addOrgList",self.projectSetting);
				if(userType == "userList"){
					addOrgList.hide();
					teamList.show();
					self.initTeam();
					self.usersList();
					return false;
				}else if(userType == "addOrgUser"){
					teamList.hide();
					self.addOrgUserList();
					addOrgList.show();
					return false;
				}
			},
			//查询组织中的人员
			addOrgUserList:function(){
				var self = this;
				$.ajax({
					url : context + "/project/userOrgList.htm",
					type : "post",
					data : {
						proId:self.projectId.val(),
						orgId:self.orgId
					},
					success : function(result) {
						self.projectSetting.find("#orgUserList").empty();
						self.projectSetting.find("#orgUserList").append('<em></em>');
						self.projectSetting.find("#userList").empty();
						if(result && result.success){
							var data = result.resultData;
							var length = -1;
							$.each(data,function(index, value){
								length ++;
							});
							$.each(data,function(index, value){
								if(value == true || value ==false){
									return false;
								}
								self.projectSetting.find("#userList").append(self.AllUserList(value));
							});
						}else if(result && !result.success){
							layer.alert(result.errormsg, 1);
						}else{
							layer.alert('请求失败', 1);
						}
					},
					error:function(result){
						layer.alert('请求失败', 1);
					}
				});
				//查询当前组织的所有团队
				$.ajax({
					url : context + "/team/teamList.htm",
					type : "post",
					data : {
						orgId:self.orgId,
						proId:self.projectId.val()
					},
					success : function(result) {
						if(result && result.success){
							self.projectSetting.find("#teams").empty();
							var data = result.resultData;
							$.each(data,function(index, value){
								self.projectSetting.find("#teams").append(self.teams(value));
							});
						}else if(result && !result.success){
							layer.alert(result.errormsg, 1);
						}else{
							layer.alert('请求失败', 1);
						}
					},
					error:function(result){
						layer.alert('请求失败', 1);
					}
				});
				return false;
			},
			AllUserList:function(value){
				var userName=value.user.name;
				if(value.user.name.length > 5){
					userName = value.user.name.substr(0,5)+"...";
				}
				var $html = $('<li data-userid="'+value.user.id+'"><img src="'+value.user.logo+'" class="img-circle" /> <a title="'+value.user.name+'">'+userName+' </a></li>');
				if(value.existPro){
					$html.append('<span class="  pull-right  ">已加入'+TEXT_CONFIG.xiangmu+'</span>' );
				}else{
					if($("#status").text()=='00700'){
						$html.append('<button class="btn pull-right outer-borer addUserForPro">加入'+TEXT_CONFIG.xiangmu+'</button> ' );
					}
				}
				return $html;
			},
			teams:function(value){
				var $team = null;
				$team = $('<li data-teamid="'+value.team.teamId+'" class="little-portrait"><h4 title='+value.team.name+'>'+value.team.name.substring(0,20)+'...</h4> <em></em></li>');
				if(value.existPro){
					$team.append('<span class="  pull-right  ">已导入</span> ');
				}else{
					if($("#status").text()=='00700'){
						$team.append('<button class="btn pull-right outer-borer addTeamForPro">导入</button>');
					}
				}
				
				if(value.team.users == null){
				}else{
					$.each(value.team.users, function(k, obj){
						var $html = $('<img title="'+obj.name+'" src="'+obj.logo+'" class="img-circle">');
						$team.find('em').append($html);
					});
				}
				return $team;
			}
	};
	projectSettingHtml.init();
});

