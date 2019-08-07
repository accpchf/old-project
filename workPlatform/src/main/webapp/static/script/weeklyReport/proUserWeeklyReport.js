require(['script/common/personList',
         context+'/static/script/weeklyReport/proWeeklyReport.js?_T='+(new Date()).valueOf(),'jquery', 'layer'], function(personList,proReport){
	var proWeekReportInfo = {
		init:function(){
			this.panel = $("#proUserWeekRep");
			this.projectId = this.panel.closest("div[data-id='projectTemplate']").data("projectId");
			this.viewInit();
			this.setEvent();
			proReport.btnClick();
		},
		setEvent:function(){
			var self = this;
			self.panel.on('click',function(){
				self.panel.find('ul#selectWeek').hide();//点击空白处，隐藏掉日期选择框
			});
			//点击查看某个成员的周报
			self.panel.find(".check-box").on("click", function(){
				var userId = $(this).closest('li').data('userid');
				var monday = self.panel.find("#selected").data('time').split("~");
				if(userId <= 0){
					//如果点击的是所有成员
					if($(this).children('i').length <= 0){
						var userIds = "";
						self.panel.find(".check-box").each(function(){
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
							url: context + "/weeklyReport/findUsersWReportByProId.htm", 
							data: {
								userIds: userIds,
								projectId: self.projectId,
								monday: monday[0]
							},
							success: function (result) { 
								if(result && result.success && result.resultData){
									var data = result.resultData;
									self.panel.find('#proUserWReport').empty();
									var refresh = window.readOnly?'':'<i class="icon-arrows-ccw createUserWeekLyReport"></i>';
									$.each(window.projectUserList, function(index,user){
										var $html = '<ul data-userid = "'+user.userId+'"><li><span class="time">'+user.name+'</span></li>'+
					                    '<li><a  class="weekly-header"> <img class="img-circle" src="'+user.logo+'"/><span class="left-arrow"></span></a></li>'+
					                    ' <li class="size-auto"> <div class="time-block corner-3"><div class="weekly-project">'+
				                           	   '<p>系统未生成该'+TEXT_CONFIG.chengyuan+'周报'+refresh+'</p></div>'+
				                        '</div></li>';
										self.panel.find('#proUserWReport').append($html);
									});
									if(data){
										$.each(data, function(key, value){
											self.panel.find('#proUserWReport ul[data-userid="'+value.user.userId+'"]').data("userweekrepid",value.userWeekReportId);
											self.panel.find('#proUserWReport ul[data-userid="'+value.user.userId+'"]').find('.weekly-project').html(self.proUserWReportInfo(value));
										});
									}
								}
							}
						});
					}else{
						self.panel.find(".check-box").each(function(){
							$(this).find('i').remove();
						});
						self.panel.find('#proUserWReport').empty();
					}
				}else{
					//如果点击的是某个成员
					if($(this).children('i').length <= 0){
						$(this).append('<i class="icon-ok"></i>');
						var userIds = "";
						var isAllUser = true;
						self.panel.find(".check-box").each(function(){
							if($(this).children('i').length > 0 ){
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
							url: context + "/weeklyReport/findUsersWReportByProId.htm", 
							data: {
								userIds: userIds,
								projectId: self.projectId,
								monday: monday[0]
							},
							success: function (result) { 
								if(result && result.success && result.resultData){
									var data = result.resultData;
									self.panel.find('#proUserWReport').empty();
									var refresh = window.readOnly?'':'<i class="icon-arrows-ccw createUserWeekLyReport"></i>';
									$.each(window.projectUserList, function(index,user){
										$.each(userIds.split(","), function(index,userId){
											if(user.userId == userId){
												var $html = '<ul data-userid = "'+user.userId+'"><li><span class="time">'+user.name+'</span></li>'+
							                    '<li><a  class="weekly-header"> <img class="img-circle" src="'+user.logo+'"/><span class="left-arrow"></span></a></li>'+
							                    ' <li class="size-auto"> <div class="time-block corner-3"><div class="weekly-project">'+
						                           	   '<p>系统未生成该'+TEXT_CONFIG.chengyuan+'周报'+refresh+'</p></div>'+
						                        '</div></li>';
											}
											self.panel.find('#proUserWReport').append($html);
										});
									});
									if(data){
										$.each(data, function(key, value){
											self.panel.find('#proUserWReport ul[data-userid="'+value.user.userId+'"]').data("userweekrepid",value.userWeekReportId);
											self.panel.find('#proUserWReport ul[data-userid="'+value.user.userId+'"]').find('.weekly-project').html(self.proUserWReportInfo(value));
										});
									}
								}
							}
						});
					}else{
						$(this).find('i').remove();
						if(self.panel.find('#allUser').children('i').length > 0){
							self.panel.find('#allUser').empty();
						}
						self.panel.find('ul[data-userid="'+userId+'"]').remove();
					}
				}
			});
			//刷新未完成任务 完成任务和总的任务
			self.panel.on("click",".refresh",function(){
				var update = $(this).closest('h3');
				var uwreportId = $(this).closest('ul').data('userweekrepid');
				 $.ajax({
		        		type:'post',
		        		data: {uwreportId:uwreportId},
		        		url:context + '/weeklyReport/updateuwReportTask.htm'
		        	}).done(function(result){
		                  if(result && result.success && result.resultData){
		                	  var data = result.resultData;
		                	  if(data){
		                		  update.siblings('h3').find('span.red').html(data.unCompleteQuality);
			                	  update.siblings('h3').find('span.green').html(data.completeQuality);
			                	  update.siblings('h3').find('span.gray').html(data.totalQuality);
			                	  if(data.comment != null){
			                  			var reg=new RegExp("\n","g");
			                    	    data.comment = data.comment.replace(reg,"<br>");
			                  		}else{
			                  			data.comment="该"+TEXT_CONFIG.chengyuan+"周报未填写";
			                  		}
			                	  update.closest("div.weekly-project").find("p").html(data.comment);
			                	  update.closest("div.weekly-project").find("ul.userCritic").empty();
			                	  $.each(data.uwReportDisscuss||[], function(k, obj){
			  						var critic = $('<li><img src="'+obj.criticUser.logo+'" class="img-circle" title = "'+obj.criticUser.name+'"> '+obj.content+'</li>');
			  						update.closest("div.weekly-project").find("ul.userCritic").append(critic);
			  					});
			                	  update.closest("div.weekly-project").children("span").html("<i class=' icon-chat green'></i>评论（"+data.uwReportDisscuss.length+"）");
		                	  }
		                  }else if(result && !result.success){
		                      layer.msg(result.errormsg);
		                  }else{
		                      layer.msg("请求失败");
		                  };
		            });
				 
			});
			//点击刷新某个成员的成员周报
			self.panel.on("click",".createUserWeekLyReport",function(){
				var userId = $(this).closest('ul').data('userid');
				var refresh = $(this);
				var date = self.panel.find('#selected').data('time').split("~");
				 $.ajax({
		        		type:'post',
		        		data: {userId:userId,projectId:self.projectId,monday:date[0],sunday:date[1]},
		        		url:context + '/weeklyReport/createUserWeekLyReport.htm'
		        	}).done(function(result){
		                  if(result && result.success && result.resultData){
		                	  var data = result.resultData;
		                	  if(data){
		                		  $.each(data,function(index,value){
		                			  self.panel.find('#proUserWReport ul[data-userid="'+value.user.userId+'"]').data("userweekrepid",value.userWeekReportId);
		                			  refresh.closest('div.weekly-project').html(self.proUserWReportInfo(value));
		                		  });
		                	  }
		                  }else if(result && !result.success){
		                      layer.msg(result.errormsg);
		                  }else{
		                      layer.msg("请求失败");
		                  };
		            });
				 
			});
			
			//给周报添加评论
			self.panel.on("click",".btn-sure",function(){
				var textareaInfo = "";
				var textareaContent = $(this).prev('textarea');
				if(!textareaInfo){
					textareaInfo = new InputValidate(textareaContent, false, 500, 0, 'textarea', null);
				}else{
					textareaInfo.target.removeClass("error");
				}
				if(!textareaInfo.checkValidate() || textareaContent.val().length == 0){
                	return false;
				}
				var btnSure = $(this);
				btnSure.prop("disabled",true);
				var uwreportId = $(this).closest('ul').data('userweekrepid');
				var content = $(this).prev('textarea').val();
				var textarea = $(this).prev('textarea');
				 $.ajax({
		        		type:'post',
		        		data: {userWeekReportId:uwreportId,content:content},
		        		url:context + '/weeklyReport/adduwReportComment.htm',
		        		beforeSend:function(){
							var validateInfo = false;
							if(!textareaInfo.checkValidate()){
								validateInfo = true;
							}
							if(validateInfo){
								btnSure.prop('disabled', false);
								return false;
							}
							return true;
						}
		        	}).done(function(result){
		                  if(result && result.success){
		                	  textarea.val("");
		                	  var criticContent = btnSure.closest('div').prev('ul');
		      				  var criticQuantity = btnSure.closest('div.weekly-chat').prev('span');
		                      var userNum = criticQuantity.text().replace("）","").replace("评论","").replace("（","");
		                      criticQuantity.html('<i class=" icon-chat green"></i>评论（'+ (userNum*1+1) +'）');
		                	  criticContent.append('<li><img title="'+loginUserInfo.name+'" src="'+loginUserInfo.logo+'" class="img-circle"> '+content+'</li>');
		                	  btnSure.prop('disabled',false);
		                  }else if(result && !result.success){
		                      layer.msg(result.errormsg);
		                  }else{
		                      layer.msg("请求失败");
		                  };
		            });
				 
			});
			//点击周期
			var menuUl = self.panel.find('#selectWeek');
			self.panel.on("click","#selected",function(event){//
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
			//选择某一周
			menuUl.on("click","li",function(event){//
				var menuBtn = self.panel.find("#selected");

            	menuUl.find("i").hide();
            	$(this).find("i").show();
            	menuBtn.data("time",$(this).data('time'));
            	menuBtn.html($(this).text()+'<i class=" icon-down-dir"></i>');		
            	menuUl.hide();
				 var strs = menuBtn.data('time').split("~");
				 var userIds = "";
				 self.panel.find(".check-box").each(function(){
					if($(this).children('i').length > 0 && $(this).closest('li').data('userid')>0){
						userIds += $(this).closest('li').data('userid')+",";
					}
				});
				 if(userIds.length >0){
					 userIds = userIds.substring(0,userIds.length-1);
				 }
				 $.ajax({
		        		type:'post',
		        		data: {monday:strs[0], userIds:userIds, projectId:self.projectId },
		        		url:context + '/weeklyReport/findUsersWReportByProId.htm'
		        	}).done(function(result){
		                  if(result && result.success && result.resultData){
		                	  var data = result.resultData;
		                	  self.panel.find('#proUserWReport').empty();
		                	  var refresh = window.readOnly?'':'<i class="icon-arrows-ccw createUserWeekLyReport"></i>';
								$.each(window.projectUserList, function(index,user){
									$.each(userIds.split(","), function(index,userId){
										if(user.userId == userId){
											var $html = '<ul data-userid = "'+user.userId+'"><li><span class="time">'+user.name+'</span></li>'+
						                    '<li><a  class="weekly-header"> <img class="img-circle" src="'+user.logo+'"/><span class="left-arrow"></span></a></li>'+
						                    ' <li class="size-auto"> <div class="time-block corner-3"><div class="weekly-project">'+
					                           	   '<p>系统未生成该'+TEXT_CONFIG.chengyuan+'周报'+refresh+'</p></div>'+
					                        '</div></li>';
										}
										self.panel.find('#proUserWReport').append($html);
									});
								});
								if(data){
									$.each(data, function(key, value){
										self.panel.find('#proUserWReport ul[data-userid="'+value.user.userId+'"]').data("userweekrepid",value.userWeekReportId);
										self.panel.find('#proUserWReport ul[data-userid="'+value.user.userId+'"]').find('.weekly-project').html(self.proUserWReportInfo(value));
									});
								}
		                  }else if(result && !result.success){
		                      layer.msg(result.errormsg);
		                  }else{
		                      layer.msg("请求失败");
		                  };
		            });
            });
			//点击成员周报和项目周报
			self.panel.find('.nav-tabs').on('click', 'li', function(){
				if($(this).hasClass('active')){
					return false;
				}
				var type = $(this).siblings("li").removeClass("active").end()
					   			  .addClass("active").data("type");
				self.hideOrShowUserWReport(type);
				return;
			});
		},
		viewInit:function(){
			var self = this,userIds = "";
			var users = window.projectUserList;
			self.panel.find("#userList").empty();
			//初始化成员周报界面的人员信息
			if(users){
				$.each(users, function(key, user){
					var username=user.name;
					if(user.name.length > 5){
						username = user.name.substr(0,5)+"...";
					}
					self.panel.find("#userList").append('<li data-userid = "'+user.userId+'"><span class="check-box"><i class="icon-ok"></i></span>  <img class="img-circle" src="'+user.logo+'" /><a title="'+user.name+'">'+username+'</a></li>');
				});
			}
			
			self.panel.find(".check-box").each(function(){
				if($(this).children('i').length > 0 && $(this).closest('li').data('userid') >0){
					userIds += $(this).closest('li').data('userid')+",";
				}
			});
			userIds = userIds.substring(0,userIds.length-1);
			//得到成员周报
			$.ajax({
				type: "post",       
				url: context + "/weeklyReport/initUsersWReportByProId.htm", 
				data:{
					userIds:userIds,
					projectId:self.projectId
				},
				success: function (result) { 
					if(result && result.success && result.resultData){
						var data = result.resultData;
						self.panel.find('#proUserWReport').empty();
						if(data && data.dateList && data.dateList.length > 0){
							self.panel.find('#isOrNotUserReport').hide();
							self.panel.find('#userWeeklyReport  .dynamic-state').show();
							var refresh = window.readOnly?'':'<i class="icon-arrows-ccw createUserWeekLyReport"></i>';
							$.each(users, function(index,user){
								
								var $html = '<ul data-userid = "'+user.userId+'"><li><span class="time">'+user.name+'</span></li>'+
			                    '<li><a  class="weekly-header"> <img class="img-circle" src="'+user.logo+'"/><span class="left-arrow"></span></a></li>'+
			                    ' <li class="size-auto"> <div class="time-block corner-3"><div class="weekly-project">'+
		                           	   '<p>系统未生成该'+TEXT_CONFIG.chengyuan+'周报'+refresh+'</p></div>'+
		                        '</div></li>';
								self.panel.find('#proUserWReport').append($html);
							});
							var i=0;
							self.panel.find('#selectWeek').empty();
							$.each(data.dateList, function(key, value){
								var d = new Date(),
		                    	nowYear = d.getFullYear();
								var reg=new RegExp(nowYear+".","g");
								var regexp=new RegExp(nowYear-1+".","g");
								var v = value.replace(reg,"").replace(regexp,"");
								i++;
								if(i == 1){
									self.panel.find('#selectWeek_div').append('<a id="selected" data-time="'+value+'">'+v+'<i class=" icon-down-dir"></i></a>');
								}
								var $optionHtml = $('<li data-time="'+value+'"><a>'+v+'</a><i class=" icon-ok"></i></li>');
								self.panel.find('#selectWeek').append($optionHtml);
							});
							self.panel.find('div#isOrNotUserReport').text('');
						}else{
							self.panel.find('#displayTime').empty();
							self.panel.find('#userWeeklyReport  .dynamic-state').hide();
							self.panel.find('#isOrNotUserReport').show();
	                		self.panel.find('#isOrNotUserReport').html('<div class="none-content"><i class="icon-clipboard-1"></i><br /><a>暂无成员周报</a></div>');
						}
						if(data && data.uwReport){
							$.each(data.uwReport, function(key, value){
								self.panel.find('#proUserWReport ul[data-userid="'+value.user.userId+'"]').data("userweekrepid",value.userWeekReportId);
								self.panel.find('#proUserWReport ul[data-userid="'+value.user.userId+'"]').find('.weekly-project').html(self.proUserWReportInfo(value));
							});
						}
					}else{
						layer.msg("请求失败");
					}
				} 
			});
		},
		//成员周报
		proUserWReportInfo:function(value){
			var criticHtml =  window.readOnly?'':'<textarea placeholder="说点什么吧" class="form-control"></textarea><button class="btn btn-sure"> 回复 </button>';
			var comment = "";
			if(value.comment != null){
        		var reg=new RegExp("\n","g");
          	    comment = value.comment.replace(reg,"<br>");
        	}else{
        		comment="该"+TEXT_CONFIG.chengyuan+"周报未填写";
        	}
			var refreshTask = window.readOnly?'':'<i class="icon-arrows-ccw refresh" title="刷新"></i>';
				var $html=$('<div class="weekly-task">'+
				'<h6>本周任务</h6>'+
				'<h3><span class="red">'+value.unCompleteQuality+'</span><br>未完成任务</h3>'+
				 '<h3><span class="green">'+value.completeQuality+'</span><br>任务完成</span></h3>'+
				'<h3><span class="gray">'+value.totalQuality+'</span><br> 任务总数 </h3>'+
				'<h3>'+refreshTask+'</h3>'+
				'</div><h6>任务总结 </h6><p >'+comment+'</p>'+
				'<span class="green"><i class=" icon-chat green"></i>评论（' + value.uwReportDisscuss.length+'）</span>'+
				  '<div class=" little-portrait weekly-chat">'+
				  '<ul class="userCritic">'+
				 '</ul>'+
				' <div class="comment-area">'+
				criticHtml+
				 '  </div>'+
				 '</div> ');
				if(value.uwReportDisscuss.length > 0){
					$.each(value.uwReportDisscuss, function(k, obj){
						var critic = $('<li><img src="'+obj.criticUser.logo+'" class="img-circle" title = "'+obj.criticUser.name+'"> '+obj.content+'</li>');
						$html.find(".userCritic").append(critic);
					});
				}
				return $html;
		},
		//成员周报和项目周报的切换
		hideOrShowUserWReport:function(type){
			var self = this,
			userWeeklyReport = self.panel.find("#userWeeklyReport"),
			proWeeklyReport = self.panel.find("#proWeeklyReport");
			if(type == "userWReport"){
				userWeeklyReport.removeClass("tohide");
				proWeeklyReport.addClass("tohide");
				self.panel.find("#selectWeek").hide();
			}else if(type == "proWReport"){
				userWeeklyReport.addClass("tohide");
				proWeeklyReport.removeClass("tohide");
				proReport.initProWeekReport();
//				proReport.btnClick();
			}
		}
		
	};
	proWeekReportInfo.init();
});