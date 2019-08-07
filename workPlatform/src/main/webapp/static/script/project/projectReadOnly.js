require(['jquery',"script/common/dialogAlert",'jqueryForm', 'layer','my97'], function($,dialogAlert){
	var projectSettingHtml;
	projectSettingHtml = {
			init:function(){
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
			},
			initProInfo:function(){
				var self = this,
					prologo = $("#proLogo", this.projectSetting),
					updateProInfo = $("#update_pro_info",  this.projectSetting),
					inputLogo = $("#inputLogo",this.projectSetting),
				    name = self.proInfo.find("#proName"),
					description = self.proInfo.find("#description");
				if(!self.proInfoValiArray.name){
					self.proInfoValiArray.name = new InputValidate(name, true, 30, 0, 'text', null);
				}else{
					self.proInfoValiArray.name.target.removeClass("error");
				}
				if(!self.proInfoValiArray.description){
					self.proInfoValiArray.description = new InputValidate(description, false, 500, 0, '', null);
				}else{
					elf.proInfoValiArray.description.target.removeClass("error");
				}
				//logo切换事件
				prologo.on("click",function(event){
					inputLogo.click();
					inputLogo.change(function(event){
						var $self = $(this);
				    	var files = this.files;
				    	if(files.length != 0){
				    		var file = files[0],
				    		reader = new FileReader();
				    		if(!reader){
				    			dialogAlert.alert('该浏览器不支持头像上传');
				    			return;
				    		}
				    		var rules = new RegExp('image/(png|jpeg|jpg|gif|img|)', 'g');
				    		if(!rules.test(file.type)){
				    			dialogAlert.alert('请选择正确格式的图片');
				    			$self.after($self.clone(true).val(''));
				    			$self.remove();
				    			return;
				    		}
				    		if(file.limitSize('65', 'KB')){
				    			$self.after($self.clone(true).val(''));
				    			$self.remove();
				    			dialogAlert.alert('图片太大，应65KB以内');
				    			return;
				    		}
				    		reader.onload = function(event){
				    			var target = EventUtil.getTarget(event);
				    			prologo.prop('src', target.result);
				    		};
				    		reader.readAsDataURL(file);
				    		}
					});
					
				});
				self.projectSetting.on("focus", "#beginTime",function(){//开始时间
					WdatePicker({
						errDealMode:1,
						dateFmt  : 'yyyy年MM月dd日',
						qsEnabled : false,
						isShowOK : false,
						maxDate : self.projectSetting.find('#endTime').val() != null?self.projectSetting.find('#endTime').val():null,
						onpicking : function(dp){
							var realDate = dp.cal.getNewDateStr();
							$(this).val(realDate);
							self.projectSetting.find("#input_beginTime").attr("value",realDate.replace(/(年|月)/g,"-").replace("日",""));
							self.projectSetting.find("#input_beginTime").val(realDate.replace(/(年|月)/g,"-").replace("日",""));
							$dp.hide();
						},
						onclearing:function(){
							$(this).val("");
							$dp.hide();
						}
					});
					return false;
				});
				self.projectSetting.on("focus", "#endTime",function(){//结束时间
					WdatePicker({
						errDealMode:1,
						dateFmt  : 'yyyy年MM月dd日',
						qsEnabled : false,
						isShowOK : false,
						minDate : self.projectSetting.find('#beginTime').val() != null?self.projectSetting.find('#beginTime').val():null,
						onpicking : function(dp){
							var realDate = dp.cal.getNewDateStr();
							$(this).val(realDate);
							self.projectSetting.find("#input_endTime").attr("value",realDate.replace(/(年|月)/g,"-").replace("日",""));
							self.projectSetting.find("#input_endTime").val(realDate.replace(/(年|月)/g,"-").replace("日",""));
							$dp.hide();
						},
						onclearing:function(){
							$(this).val("");
							$dp.hide();
						}
					});
					return false;
				});
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
					return;
				});
			},
			//项目信息 成员管理 高级设置之间的切换
			hideOrShowSettingPage:function(type){
				var self = this,
					advancedSettings = self.projectSetting.find("#advanced-settings"),
					addMember = self.projectSetting.find("#add-member");
					
				if(type == "projectInfo"){
					self.proInfo.removeClass("tohide");
					addMember.addClass("tohide");
					advancedSettings.addClass("tohide");
					//self.initProInfo();
					return;
				}else if(type == "userMan"){
					self.proInfo.addClass("tohide");
					advancedSettings.addClass("tohide");
					self.initUserMana();//初始化成员管理界面
					self.usersList();//项目人员信息显示
					addMember.removeClass("tohide");
					if(self.orgId == 0 ){
						self.projectSetting.find("#user_list").removeClass("select");
						self.projectSetting.find("#addOrgUser").addClass("tohide");
						self.projectSetting.find("#teamUserList").addClass("tohide");
					}else{
						self.projectSetting.find("#teamUserList").removeClass("tohide");
						self.projectSetting.find("#user_list").addClass("select");
						self.projectSetting.find("#addOrgUser").removeClass("tohide");
						self.initTeam();
						self.orgProUserMan();
						if($('#addOrgUser').hasClass('select')){
							$('#user_list').removeClass('select');
						}
					}
					if(self.projectSetting.find("#status").text()=="00700"){
						self.userManBtn();//成员管理界面按钮
					}
					return;
				}else{
					advancedSettings.removeClass("tohide");
					self.proInfo.addClass("tohide");
					addMember.addClass("tohide");
					self.userOption();
					return;
				}
			},
			//高级设置页面操作
			userOption:function(){
				var self = this,
					exitPro = $("#exit_pro_btn",self.projectSetting),
					proId = self.projectId.val();
				exitPro.on("click",function(){
					if(confirm("是否退出该项目")){
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
						      error: function (errorThrown) {  
						      }       
						});
					}
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
					}
				});
			},
			//成员管理界面
			initUserMana:function(){
				var self = this,
					proId = self.projectId.val();
				$.ajax({
					url : context + "/project/getCode.htm",
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
					}
				});
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
					}
				});
			},
			teamUserList:function(value){
				var $team = null;
				$team = $('<div class="team-number team-common  little-portrait corner-3" data-teamid= "'+value.teamId+'"/>');
				$team.append('<h4 title='+value.name+'>'+value.name.substring(0,10)+'...('+value.users.length+')</h4>');
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
					isAdmin = "移除管理员";
					role = "管理员";
				}else {
					isAdmin="设为管理员";
				}
				if(value.name.length > 5){
					userName = value.name.substr(0,5)+"...";
				}
				if(value.prjRoleCode == "PRJ_SUPERVISER"){
					isSupervise = "移除监督员";
					role = "监督员";
				}else {
					isSupervise="设为监督员";
				}	
				var html = $('<li data-userid = "'+value.userId+'"><img src="'+value.logo+'" class="img-circle headerpic_img" /> <a><span title="'+value.name+'">'+
						userName+'</span><span class="role">'+role+'</span></a></li>');
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
					return;
				}else if(userType == "addOrgUser"){
					teamList.hide();
					self.addOrgUserList();
					addOrgList.show();
					return;
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
							self.projectSetting.find("#alluser").text("所有"+TEXT_CONFIG.chengyuan+"  . "+length);
							$.each(data,function(index, value){
								if(value == true || value ==false){
									return false;
								}
								self.projectSetting.find("#orgUserList em").append('<img title="'+value.user.name+'" src="'+value.user.logo+'" class="img-circle" />');
								self.projectSetting.find("#userList").append(self.AllUserList(value));
							});
							if(data.allUserExist){
								self.projectSetting.find("#orgUserList").append('<span class="  pull-right  ">已导入</span>');
							}
						}else if(result && !result.success){
							layer.alert(result.errormsg, 1);
						}else{
							layer.alert('请求失败', 1);
						}
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
					}
				});
			},
			AllUserList:function(value){
				var userName=value.user.name;
				if(value.user.name.length > 5){
					userName = value.user.name.substr(0,5)+"...";
				}
				var $html = $('<li data-userid="'+value.user.id+'"><img src="'+value.user.logo+'" class="img-circle" /> <a title="'+value.user.name+'">'+userName+' </a></li>');
				if(value.existPro){
					$html.append('<span class="  pull-right  ">已加入'+TEXT_CONFIG.xiangmu+'</span>' );
				}
				return $html;
			},
			teams:function(value){
				var $team = null;
				$team = $('<li data-teamid="'+value.team.teamId+'" class="little-portrait"><h4>'+value.team.name+' . '+value.team.users.length+'</h4> <em></em></li>');
				if(value.existPro){
					$team.append('<span class="  pull-right  ">已导入</span> ');
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

