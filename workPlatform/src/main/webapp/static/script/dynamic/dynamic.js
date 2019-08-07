require(['script/common/personList','jquery', 'layer'], function(personList){
	var dynamicInfo = {
		init:function(){
			this.panel = $('#dynamic_view');
			this.projectId = this.panel.closest("div[data-id='projectTemplate']").data("projectId");
			this.users = window.projectUserList;
			this.initView();
			this.setEvent();
		},
		setEvent:function(){
			var self = this;
			var menuUl = self.panel.find('#selectWeek');
			self.panel.on("click","#selected",function(){//
				menuUl.show();
				var yearAndWeek = $(this).data('time');
				menuUl.find('li').each(function(){
					if($(this).data('time')==yearAndWeek){
						$(this).find('i').show();
					}else{
						$(this).find('i').hide();
					}
				});
				return false;
	         });
			menuUl.on("click","li",function(event){//
				var menuBtn = self.panel.find("#selected");

		        menuUl.find("i").hide();
		        $(this).find("i").show();
		        menuBtn.data("time",$(this).data('time'));
		        menuBtn.html($(this).text()+'<i class=" icon-down-dir"></i>');	
		        menuUl.hide();
		        var strs = menuBtn.data('time').split("~");
				 var userIds = "",types = "";
				 self.panel.find(".check-box").each(function(){
					if($(this).children('i').length > 0 && $(this).closest('li').data('userid')>0){
						userIds += $(this).closest('li').data('userid')+",";
					}
				 });
				 self.panel.find("#dynamicType li").each(function(){
					if($(this).find('span').children('i').length > 0){
						types += $(this).data('type')+",";
					}
				});
				if(types.length > 0 ){
					types = types.substring(0,types.length-1);
				}
				if(userIds.length >0){
					userIds = userIds.substring(0,userIds.length-1);
				}
				 $.ajax({
		        		type:'post',
		        		data: {monday:strs[0],sunday:strs[1], userIds:userIds, projectId:self.projectId,types:types },
		        		url:context + '/dynamic/findProjectDynamic.htm'
		        	}).done(function(result){
		                  if(result && result.success && result.resultData){
		                	  var data = result.resultData;
		                	  self.panel.find("#dynamic_content").empty();
		                	 if(data){
		                		$.each(data,function(key,value){
		                			self.panel.find("#dynamic_content").append(self.initProjectDynamic(key,value));
		                		});
		                	}
		                	 self.addPicture();
		                	 return ;
		                  }else if(result && !result.success){
		                      layer.msg(result.errormsg);
		                  }else{
		                      layer.msg("请求失败");
		                  };
		            });
			});
			self.panel.find("#userList .check-box").on("click", function(){
				var userId = $(this).closest('li').data('userid');
				var time = self.panel.find("#selected").data('time').split("~"),
					types = "";
				self.panel.find("#dynamicType li").each(function(){
					if($(this).find('span').children('i').length > 0){
						types += $(this).data('type')+",";
					}
				});
				if(types.length > 0 ){
					types = types.substring(0,types.length-1);
				}
				if(userId <= 0){
					//如果点击的是所有成员
					if($(this).children('i').length <= 0){
						var userIds = "";
						self.panel.find("#userList .check-box").each(function(){
							if($(this).children('i').length <= 0 ){
								$(this).append('<i class="icon-ok"></i>');
							}
							if($(this).children('i').length > 0 && $(this).closest('li').data('userid') >0){
								userIds += $(this).closest('li').data('userid')+",";
							}
						});
						userIds = userIds.substring(0,userIds.length-1);
						$.ajax({
							type: "post",       
							url: context + "/dynamic/findProjectDynamic.htm", 
							data: {
								userIds: userIds,
								projectId: self.projectId,
								monday: time[0],
								sunday:time[1],
								types:types
							},
							success: function (result) { 
								if(result && result.success && result.resultData){
									var data = result.resultData;
									self.panel.find("#dynamic_content").empty();
									if(data){
				                		$.each(data,function(key,value){
				                			self.panel.find("#dynamic_content").append(self.initProjectDynamic(key,value));
				                		});
									}
									self.addPicture();
				                	return false;
								}
							}
						});
					}else{
						self.panel.find("#userList .check-box").each(function(){
							$(this).find('i').remove();
						});
						self.panel.find("#dynamic_content").empty();
					}
				}else{
					//如果点击的是某个成员
					if($(this).children('i').length <= 0){
						$(this).append('<i class="icon-ok"></i>');
						var userIds = "";
						var isAllUser = true;
						$(".check-box").each(function(){
							if($(this).children('i').length > 0 && $(this).closest('li').data('userid')>0){
								userIds += $(this).closest('li').data('userid')+",";
							}
							if($(this).children('i').length <=  0 && $(this).closest('li').data('userid') >0){
								isAllUser = false;
							}
						});
						userIds = userIds.substring(0,userIds.length-1);
						if(isAllUser){
							self.panel.find('#allUser').append('<i class="icon-ok"></i>');
						}
						$.ajax({
							type: "post",       
							url: context + "/dynamic/findProjectDynamic.htm", 
							data: {
								userIds: userIds,
								projectId: self.projectId,
								monday: time[0],
								sunday:time[1],
								types:types
							},
							success: function (result) { 
								if(result && result.success && result.resultData){
									var data = result.resultData;
									if(data){
										self.panel.find("#dynamic_content").empty();
				                		$.each(data,function(key,value){
				                			self.panel.find("#dynamic_content").append(self.initProjectDynamic(key,value));
				                		});
				                		self.addPicture();
					                	 return ;
									}
								}
							}
						});
					}else{
						$(this).find('i').remove();
						if(self.panel.find('#allUser').children('i').length > 0){
							self.panel.find('#allUser').empty();
						}
						self.panel.find('div[data-userid="'+userId+'"]').remove();
						self.panel.find('#dynamic_content .time-block').each(function(){
							if($(this).children('div.list-block').length <= 0){
								$(this).closest('ul').remove();
							}
						});
					}
				}
				self.addPicture();
			});
			self.panel.find('#dynamicType').on('click','.check-box',function(){
				var time = self.panel.find("#selected").data('time').split("~");
				var typeContent = $(this).closest('li').text();
				var types="",userIds = "";
				if($(this).children('i').length <=0){
					$(this).append('<i class="icon-ok"></i>');
					self.panel.find("#dynamicType li").each(function(){
						if($(this).find('span').children('i').length > 0){
							types += $(this).data('type')+",";
						}
					});
					$("#userList .check-box").each(function(){
						if($(this).children('i').length > 0 && $(this).closest('li').data('userid')>0){
							userIds += $(this).closest('li').data('userid')+",";
						}
					});
					if(types.length > 0 ){
						types = types.substring(0,types.length-1);
					}
					if(userIds.length >0){
						userIds = userIds.substring(0,userIds.length-1);
					}
					$.ajax({
						type: "post",       
						url: context + "/dynamic/findProjectDynamic.htm", 
						data: {
							userIds: userIds,
							projectId: self.projectId,
							monday: time[0],
							sunday:time[1],
							types:types
						},
						success: function (result) { 
							if(result && result.success && result.resultData){
								var data = result.resultData;
								if(data){
									self.panel.find("#dynamic_content").empty();
			                		$.each(data,function(key,value){
			                			self.panel.find("#dynamic_content").append(self.initProjectDynamic(key,value));
			                		});
								}
								self.addPicture();
								return;
							}
						}
					});
				}else{
					$(this).find('i').remove();
					self.panel.find('#dynamic_content div.list-block').each(function(){
						if($(this).find('.task-content').text()==typeContent){
							$(this).remove();
						}
					});
					self.panel.find('#dynamic_content .time-block').each(function(){
						if($(this).children('div.list-block').length <= 0){
							$(this).closest('ul').remove();
						}
					});
				}
				self.addPicture();
			});
			
		},
		initView:function(){
			var self = this,userIds = "",types="";
			var users = self.users;
			self.panel.find("#userList").empty();
			self.panel.on('click',function(){
				self.panel.find('#selectWeek').hide();
			});
			self.panel.find("#userList").append('<li data-userid="0"><span class="check-box" id="allUser"><i class="icon-ok"></i></span> <img class="img-circle" src="'+context+'/static/images/head-portraits2.png'+'" /><a>所有'+TEXT_CONFIG.chengyuan+'</a></li>');
			$.each(users, function(key, user){
				var username=user.name;
				if(username.length > 5){
					username = username.substr(0,5)+"...";
				}
				self.panel.find("#userList").append('<li data-userid = "'+user.userId+'"><span class="check-box"><i class="icon-ok"></i></span>  <img class="img-circle" src="'+user.logo+'" /><a title="'+user.name+'">'+username+'</a></li>');
			});
			self.panel.find("#userList li").each(function(){
				if($(this).find('span').children('i').length > 0 && $(this).data('userid') >0){
					userIds += $(this).data('userid')+",";
				}
			});
			self.panel.find("#dynamicType li").each(function(){
				if($(this).find('span').children('i').length > 0){
					types += $(this).data('type')+",";
				}
			});
			if(userIds.length > 0 ){
				userIds = userIds.substring(0,userIds.length-1);
			}
			if(types.length > 0 ){
				types = types.substring(0,types.length-1);
			}
			$.ajax({
				type: "post",       
				url: context + "/dynamic/initProjectDynamic.htm", 
				data:{
					userIds:userIds,
					projectId:self.projectId,
					types:types
				},
				success: function (result) { 
					if(result && result.success && result.resultData){
						var data = result.resultData;
						var i = 0;
						if(data && data.dataList && data.dataList.length > 0 ){
							self.panel.find('#selectWeek').empty();
							$.each(data.dataList, function(key, value){
								var d = new Date(),
		                    	nowYear = d.getFullYear();
								var reg=new RegExp(nowYear+".","g");
								var regexp=new RegExp(nowYear-1+".","g");
								var v = value.replace(reg,"").replace(regexp,"");
								i++;
								if(i == 1){
									self.panel.find('#selected_time').append('<a id="selected" data-time = "'+value+'">'+v+'<i class=" icon-down-dir"></i></a>');
								}
								var $optionHtml = $('<li data-time="'+value+'"><a>'+v+'</a><i class=" icon-ok"></i></li>');
								self.panel.find('#selectWeek').append($optionHtml);
							});
						}
	                	if(data && data.pActionRecord && data.pActionRecord != null){
	                		self.panel.find("#dynamic_content").empty();
	                		$.each(data.pActionRecord,function(key,value){
	                			self.panel.find("#dynamic_content").append(self.initProjectDynamic(key,value));
	                		});
	                	}	 
	                	self.addPicture();
	                	return;
					}
				}
			});
			self.addPicture();
		},
		initProjectDynamic:function(key,value){
			var self = this;
			var keydate = new Date(Date.parse(key.replace(/-/g, '/')));
			var c= keydate.toLocaleDateString();
			var d= new Date();
			var nowdate = d.toLocaleDateString();
			var year = d.getFullYear();
			key = key.replace(year+".","");
			if(c == nowdate){
				key = "今天";
			}
			var $html=$( '<ul><li><span class="time">'+key+'</span></li>'+
					'<li><a class="select"><i class="icon-calendar"></i> <span class="left-dynamic-arrow"></span></a></li>'+
					'<li class="size-auto">'+
					 '<div class="time-block corner-3 dynamicInfo">'+
					'</div></li> </ul>');
			if(value.length > 0){
				$.each(value,function(key,obj){
					var type="";
					if((obj.useage <= "00618" && obj.useage >= "00600") || (obj.useage >= "00651" && obj.useage <= "00653")|| (obj.useage >= "00654" && obj.useage <= "00661") ){
						type="任务";
					}else if(obj.useage <= "00622" && obj.useage >= "00619"){
						type="文件库";
					}else if(obj.useage == "00623" || obj.useage == "00624" ||obj.useage == "00642" ||obj.useage == "00643" ){
						type="会议";
					}else if(obj.useage <= "00626" && obj.useage >= "00625"){
						type="周报";
					}else if(( obj.useage <= "00641" && obj.useage >= "00627") || (obj.useage >= "00644" && obj.useage <= "00650")){
						type=TEXT_CONFIG.xiangmu+"设置";
					}
					var image= "",username=obj.userName, record = obj.record;
					$.each(self.users,function(k,o){
						if(o.userId==obj.userId){
							image = o.logo;
						}
					});
					if(username.length > 5){
						username = username.substr(0,5)+"...";
					}
					if(record.length > 30){
						record = record.substr(0,30)+"...";
					}
					var $data = $('<div class="list-block" data-userid="'+obj.userId+'">'+
							'<p class="user"><img src="'+image+'" class=" img-circle">'+
							'<span title="'+obj.userName+'">'+username+'</span><span title="'+obj.record+'">'+record+'</span></p>'+
							'<p class="task-content"><i class="icon-tasks"></i>'+type+'</p>'+
							'<time>'+obj.createdTimeString+'</time></div>');
					$html.find('.dynamicInfo').append($data);
				});
				
			}
			return $html;
		},
		addPicture:function(){
			var self = this;
			if(self.panel.find('#dynamic_content').children('ul').length == 0){
				self.panel.find("div#dynamic_content").html('<div class="none-content-project" style="margin-top:60px;"><em class="icon-back-in-time"></em><br /><p>目前还没有动态</p></div> ');
			}
		}
	};
	dynamicInfo.init();
});