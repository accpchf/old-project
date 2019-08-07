require(['jquery'], function($){
	var userWeekReportInfo;
	userWeekReportInfo = {
			init:function(){
				this.projectId = 0;
				this.panel = $('#userWeekRep');//个人周报容器
				this.allPanel = $('#myPersonalInfo');//我的任务 个人周报 设置容器
				this.setEvent();
				this.updateUWReportInfo();
			},
			setEvent:function(){
				var self = this;
				$.ajax({
					type:'post',
					url:context + '/weeklyReport/initUserWeekReportInfo.htm'
				}).done(function(result){
	                  if(result && result.success && result.resultData){
	                	 var data = result.resultData;
	                	 var i = 0;
	                	 if(data.dateList && data.dateList.length > 0){
	                		 self.panel.find("#userWeeklyDisplay").show();
	                		 self.panel.find('#selectWeek').empty();
								$.each(data.dateList, function(key, value){
									var d = new Date(),
			                    	nowYear = d.getFullYear();
									var reg=new RegExp(nowYear+".","g");
									var regexp=new RegExp(nowYear-1+".","g");
									var v = value.replace(reg,"").replace(regexp,"");
									i++;
									if(i == 1){
										self.panel.find('#selectWeek_btn').append('<a data-time = "'+value+'"><i class="icon-calendar"></i>'+v+'<i class=" icon-down-open-big"></i></a>');
									}
									var $optionHtml = $('<li data-time="'+value+'"><a>'+v+'</a><i class=" icon-ok"></i></li>');
									self.panel.find('#selectWeek').append($optionHtml);
								});
	                	 }else{
	                		 self.panel.html(' <div class="none-content"><i class="icon-clipboard-1"></i><br /><a>暂无个人周报</a></div>');
	                	 }
	                	 if(data.uwReport){
	                		  self.panel.find('#selectProject').empty();
		                	  self.panel.find('#selectProject').append('<li data-projectid="0"><a>'+TEXT_CONFIG.xiangmu+'选择</a><i class=" icon-ok"></i></li>')
	                		 $.each(data.uwReport,function(index,value){
	                			 self.panel.find('#selectProject').append('<li data-projectid="'+value.project.projectId+'"><a>'+value.project.name+'</a><i class=" icon-ok"></i></li>');
	                			 self.panel.find('#uwReportInfo').append(self.userWReportInfo(value));
	                		 })
	                	 }
	                  }else if(result && !result.success){
	                      layer.msg(result.errormsg);
	                  }else{
	                      layer.msg("请求失败");
	                  };
	            });
				//选择项目
				var projectList = self.panel.find('#selectProject');
				self.panel.find("button#selectProject_btn").on("click",function(){
					self.panel.find('#selectWeek').hide();
					 var projectId = self.projectId;;
					 self.panel.find('#selectProject li').each(function(){
						 if($(this).data('projectid') == projectId){
							 $(this).find('i').show();
						 }else{
							 $(this).find('i').hide();
						 }
					 });
					 projectList.show();
					 return false;
				});
				projectList.on("click","li",function(event){
					var menuBtn = self.panel.find("#selectProject_btn");
					var projectId = $(this).data('projectid');
					projectList.find("i").hide();
	            	$(this).find("i").show();
//	            	menuBtn.data("projectid",projectId);
	            	menuBtn.html('<a>'+$(this).text()+'<i class=" icon-down-open-big"></i></a> ');	
	            	projectList.hide();
	            	self.projectId = projectId;
	            	if(projectId > 0){
						var div = $("h4[data-projectid= '"+projectId+"']",self.panel).closest('div.time-block');
						div.siblings("div.time-block").hide().end().
							show();
					}else{
						$('div.time-block').show();
					}
				});
				var weekList = self.panel.find('#selectWeek');
				//选择周期
				self.panel.find("#selectWeek_btn").on("click",function(){
					var yearAndWeek = $(this).find('a').data('time');
					self.panel.find('#selectProject').hide();
					weekList.find('li').each(function(){
						 if($(this).data('time') == yearAndWeek){
							 $(this).find('i').show();
						 }else{
							 $(this).find('i').hide();
						 }
					 });
					weekList.show();
					 return false;
				});
				weekList.on("click","li",function(event){
					var menuBtn = self.panel.find("#selectWeek_btn");
					var time = $(this).data('time');
					weekList.find("i").hide();
	            	$(this).find("i").show();
	            	menuBtn.find('a').data("time",time);
	            	menuBtn.find('a').html('<i class="icon-calendar"></i>'+$(this).text()+'<i class=" icon-down-open-big"></i>');	
	            	weekList.hide();
					var strs = time.split("~");
					 $.ajax({
			        		type:'post',
			        		data: {monday:strs[0]},
			        		url:context + '/weeklyReport/findUWReport.htm'
			        	}).done(function(result){
			                  if(result && result.success && result.resultData){
			                	  var data = result.resultData;
			                	  self.panel.find("#uwReportInfo").empty();
			                	  if(data.length <= 0){
			                		  return false;
			                	  }
			                	  self.panel.find('#selectProject').empty();
	            				  self.panel.find('#selectProject_btn').html('<a>'+TEXT_CONFIG.xiangmu+'选择<i class=" icon-down-open-big"></i></a> ');
			                	  self.panel.find('#selectProject').html('<li data-projectid=0><a>'+TEXT_CONFIG.xiangmu+'选择</a><i class=" icon-ok"></i></li>');
			                	  $.each(data,function(index, value){
			                		  self.panel.find("#uwReportInfo").append(self.userWReportInfo(value));
			                		  self.panel.find('#selectProject').append('<li data-projectid="'+value.project.projectId+'"><a>'+value.project.name+'</a><i class=" icon-ok"></i></li>');
			                	  });
			                	  self.projectId = 0;
			                	  //self.updateUWReportInfo();
			                	  return false;
			                  }else if(result && !result.success){
			                      layer.msg(result.errormsg);
			                  }else{
			                      layer.msg("请求失败");
			                  };
			                  return false;
			            });
				});
			},
			
			updateUWReportInfo:function(){
				var self = this,content="";
				//修改周报内容
				self.allPanel.on('click',function(){
					self.panel.find('#selectWeek').hide();
					self.panel.find('#selectProject').hide();
				})
				self.panel.on('click','.icon-pencil',function(){
					$(this).prop('disabled',true);
					var comment_div = $(this).closest('h6').next('div.comment_div');
					var comment = comment_div.find('p').html();
		          	var reg=new RegExp("<br>","g");
		            var c = comment.replace(reg,"\n");
					comment_div.empty();
					comment_div.append('<textarea class="form-control noborder">'+c+'</textarea>');
					comment_div.find('textarea').focus();
				});
				//保存任务总结
				self.panel.on('blur','textarea.noborder',function(){
					content = $(this);
					var contentValidate;
					if(!contentValidate){
						contentValidate = new InputValidate(content, false, 500, 0, 'textarea', null);
					}else{
						contentValidate.target.removeClass("error");
					}
					if(!contentValidate.checkValidate()){
						return;
					}
					var updatebtn = $(this).closest('div').prev('h6').find('i');
					var commentData = $(this).closest('div');
					var comment= $(this).val();
					var userweekrepId = $(this).closest('div.time-block').data('userweekrepid');
					$.ajax({
		        		type:'post',
		        		data: {userWeekReportId:userweekrepId,comment:comment },
		        		url:context + '/weeklyReport/updateUserWRep.htm'
		        	}).done(function(resultData){
		                  if(resultData && resultData.success){
		                	  commentData.empty();
		                	  var reg=new RegExp("\n","g");
		                	  var c = comment.replace(reg,"<br>");
		                	  commentData.append('<p>'+c+'</p>');
		                	  updatebtn.prop('disabled',false);
		                  }else if(resultData && !resultData.success){
		                      layer.msg(resultData.errormsg);
		                  }else{
		                      layer.msg("请求失败");
		                  };
		            });
				});
				//刷新未完成任务 完成任务和总的任务
				self.panel.on("click",".icon-arrows-ccw",function(){
					var update = $(this).closest('h3');
					var uwreportId = $(this).closest('div.time-block').data('userweekrepid');
					 $.ajax({
			        		type:'post',
			        		data: {uwreportId:uwreportId},
			        		url:context + '/weeklyReport/updateuwReportTask.htm'
			        	}).done(function(result){
			                  if(result && result.success && result.resultData){
			                	  var data = result.resultData;
			                	  update.siblings('h3').find('span.red').html(data.unCompleteQuality);
			                	  update.siblings('h3').find('span.green').html(data.completeQuality);
			                	  update.siblings('h3').find('span.gray').html(data.totalQuality);
			                	  update.closest("div.weekly-project").find("ul.userCritic").empty();
			                	  if(data.uwReportDisscuss){
			                		  $.each(data.uwReportDisscuss, function(k, obj){
					  						var critic = $('<li><img src="'+obj.criticUser.logo+'" class="img-circle" title = "'+obj.criticUser.name+'"> '+obj.content+'</li>');
					  						update.closest("div.weekly-project").find("ul.userCritic").append(critic);
					  					});
			                		  update.closest("div.weekly-project").children("a").html("<i class=' icon-chat green'></i>评论（"+data.uwReportDisscuss.length+"）");
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
					var btnSure = $(this),textareaInfo = "";
					var textareaContent = btnSure.prev('textarea');
					if(!textareaInfo){
						textareaInfo = new InputValidate(textareaContent, false, 500, 0, 'textarea', null);
					}else{
						textareaInfo.target.removeClass("error");
					}
					if(!textareaInfo.checkValidate() || textareaContent.val().length == 0){
	                	return false;
					}
					btnSure.prop("disabled",true);
					var uwreportId = $(this).closest('div.time-block').data('userweekrepid');
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
			                	  btnSure.prop('disabled',false);
			                	  textarea.val("");
			                	  var criticContent = btnSure.closest('div').prev('ul');
			  					  var criticQuantity = btnSure.closest('div.weekly-chat').prev('a');
			                      var userNum = criticQuantity.text().replace("）","").replace("评论（","");
			                	  criticContent.append('<li><a><img title="'+loginUserInfo.name+'" src="'+loginUserInfo.logo+'" class="img-circle"> '+content+'</a></li>');
			                	  criticQuantity.html('<i class=" icon-chat green"></i>评论（ '+ (userNum*1+1) +'）');
			                  }else if(result && !result.success){
			                      layer.msg(result.errormsg);
			                  }else{
			                      layer.msg("请求失败");
			                  };
			            });
				});
			},
			userWReportInfo:function(value){
          	  var comment = "";
          	  if(value.comment){
          		  var reg=new RegExp("\n","g");
            	  comment = value.comment.replace(reg,"<br>");
          	  }
				var $html=$('<div class="time-block corner-3 " data-userweekrepid = "'+value.userWeekReportId+'">'+
				'<div class="weekly-project">'+
				 '<h4 data-projectid = "'+value.project.projectId+'"><i class="icon-tasks-1"></i>'+value.project.name+'</h4>'+
				'<h6>本周任务</h6><div class="weekly-task">'+
				'<h3><span class="red">'+value.unCompleteQuality+'</span><br>未完成任务</h3>'+
				'<h3><span class="green">'+value.completeQuality+'</span><br>任务完成</span></h3>'+
				'<h3><span class="gray">'+value.totalQuality+'</span><br> 任务总数 </h3>'+
				 '<h3><i class=" icon-arrows-ccw" title="刷新"></i></h3>'+
				'</div>'+
				'<h6>任务总结<i class=" icon-pencil" title="修改"></i></h6><div class="comment_div"> <p>'+comment+'</p></div>'+
				' <a  class="green"><i class=" icon-chat green"></i>评论（'+value.uwReportDisscuss.length+'）</a>'+
				 '<div class=" little-portrait weekly-chat">'+
				 '<ul class="userCritic"></ul>'+
				 '<div class="comment-area"><textarea placeholder="说点什么吧" class="form-control textareaComments"></textarea>'+
				 '<button class="btn btn-sure"> 回复 </button>'+
                 '</div>'+
				'</div>'+
				 '</div>'+
				'</div> ');
				if(value.uwReportDisscuss.length > 0){
					$.each(value.uwReportDisscuss, function(k, obj){
						var critic = $('<li><a><img src="'+obj.criticUser.logo+'" class="img-circle" title="'+obj.criticUser.name+'"> '+obj.content+'</a></li>');
						$html.find(".userCritic").append(critic);
					});
				}
				return $html;
			}
	};
	userWeekReportInfo.init();
});