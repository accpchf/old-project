/**
 * 项目周报
 */
define(['script/common/personList',"jquery","layer"],function(personList){
	var panel = $("#proWeeklyReport");
	var projectId = panel.closest("div[data-id='projectTemplate']").data("projectId");
	var planwRContentValiArray = {},workwRContentValiArray = {},problemwRContentValiArray = {};
	var pwrId = 0;
	var common = {
			//初始化项目周报
			initProWeekReport:function(){
				var self = this,
				users = window.projectUserList,username=loginUserInfo.name;
				panel.find("#usersPro,ul.weekly-item").empty();
				$.each(users, function(key, user){
					if(user.userEnabled){
						panel.find("#usersPro").append('<li class="removepointer"><img src="'+user.logo+'" class="img-circle" title="'+user.name+'"></li>');
					}
				});
				panel.find("#filledUser").html("");
				//panel.find("#filledUser").html('<img  src="'+loginUserInfo.logo+'" class="img-circle removepointer"><span title="'+loginUserInfo.name+'">'+username+'</span></img>')
				$.ajax({
	     			type:'post',
	     			data:{
						projectId:projectId
					},
	     			url:context + '/weeklyReport/initProWeekRep.htm'
	     		}).done(function(result){
	                if(result && result.success && result.resultData){
	                	var data = result.resultData;
	                	var d = new Date(),
	                    	nowYear = d.getFullYear();
	                	if(data && data.dataMap && data.dataMap.length > 0){
	                		var i=0;
	                		$.each(data.dataMap, function(key, value){
	                			var regexp=new RegExp(nowYear+".","g");
	                			var reg=new RegExp((nowYear*1-1)+".","g");
	                			var v = value.replace(reg,"").replace(regexp,"");
	                			i++;
	                			if(i == 1){
	                				panel.find("ul.weekly-item").append('<li class="select" data-weeks="'+value+'"><a><i class="icon-calendar"></i>'+v+'</a></li>');
	                			}else{
	                				panel.find("ul.weekly-item").append('<li data-weeks="'+value+'"><a><i class="icon-calendar"></i>'+v+'</a></li>');
	                			}
	                    		
	    					});
						}else{
	                		panel.find('div#projectWeekReport_div').html('<div class="none-content"><i class="icon-clipboard-1"></i><br /><a>系统未生成本周'+TEXT_CONFIG.xiangmu+'周报数据</a></div>');
						}
	                	if(data && data.pwr){
	                		self.initProWeekReportContent(data.pwr);
	                		
	                	}
	                }else if(result && !result.success){
	                    layer.msg(result.errormsg);
	                }else{
	                    layer.msg("请求失败");
	                };
	            });
			},
			initProWeekReportContent:function(value){
				var self = this;
				if(value.filledUser){
					$.each(window.projectUserList,function(key,data){
						if(data.userId == value.filledUser.userId){
							var name = data.name;
							if(name.length > 5){
								name = name.substr(0,5)+"...";
							}
							panel.find("#filledUser").html('<img  src="'+data.logo+'" class="img-circle removepointer"><span title="'+data.name+'">'+name+'</span></img>')
						}
					})
					
				}
				panel.find("textarea.weekly-control").attr("data-pwrcid",0);
				panel.find("textarea.weekly-control").append('<i class=" icon-pencil" title="修改"></i>');
				panel.find("#weekRisk").html('<tr><th class="width_lastRisk">问题描述</th><th class="width_lastRisk">解决方案</th><th>责任人</th></tr>');
	    		panel.find("#weekWorks").html('<tr><th class="width_number">编号</th><th class="width_lastRisk">工作内容</th><th class="width_number">计划进度（%）</th><th class="width_number">完成情况（%）</th><th>责任人</th></tr>');
	    		panel.find("#lastWeekWork").html('<tr><th class="width_number">编号</th><th >工作内容</th><th class="width_number">计划进度（%）</th><th >完成情况（%）</th><th>责任人</th></tr>');
	    		panel.find("#lastWeekRisk").html('<tr><th>问题描述</th><th>解决方案</th><th>责任人</th></tr>');
	    		panel.find("#nextWeekPlan").html('<tr><th class="width_number">编号</th><th class="width_work">工作内容</th><th>责任人</th></tr>');
//	    		panel.find("div#proWeekReport").attr("data-pwrid",value.projectWeeklyReportId);
	    		pwrId = value.projectWeeklyReportId;
	    		if(value.createdString == null){
	    			value.createdString = "  ";
	    		}
	    		panel.find("#createTime").html('<i class="icon-calendar"></i>'+value.createdString);
	    		if(value.deliveryString == null){
	    			panel.find("#deliveryTime").html('未设置交付日期');
	    		}else{
	    			panel.find("#deliveryTime").html('<i class="icon-calendar"></i>'+value.deliveryString);
	    		}
	    		panel.find("#progress").val(value.progress);
	    		panel.find("div.task-content p").html("");
	    		panel.find("#status").val(value.status).attr('title',value.status);
	    		panel.find("#phase").val(value.phase).attr('title',value.phase);
	    		if(value.listProWRepContents != null ){
	    			var result = value.listProWRepContents;
	    			var a = 0,b = 0;
	    			$.each(result,function(index,obj){
	    				if(obj.type=="00900"){
	    					c++;
	    					panel.find("#weekRisk").append(self.weekRisk(obj));
	    				}else if(obj.type == "00901"){
	    					a++;
	    					panel.find("#weekWorks").append(self.weekWorks(obj,a));
	    				}else if(obj.type == "00903"){
	    					b++;
	        				panel.find("#nextWeekPlan").append(self.nextWeekPlan(obj,b));
	    				}else if(obj.type == "00902"){
	    					var reg=new RegExp("\n","g");
		                	var c = obj.column4Content.replace(reg,"<br>");
	    					panel.find("div.task-content").attr("data-pwrcid",obj.proWRepContentId);
	    					panel.find("div.task-content p").html(c);
	    					panel.find("div.task-content textarea").hide();
	    				}
	    			});
	    		}
	    		if(value.listParentProWRepContents != null ){
	    			var result = value.listParentProWRepContents;
	    			var a = 0,b = 0;
	    			$.each(result,function(index,obj){
	    				if(obj.type == "00901"){
	    					a++;
	    					panel.find("#lastWeekWork").append(self.lastWeekWorks(obj,a));
	    				}else if(obj.type == "00900"){
	    					b++;
	    					panel.find("#lastWeekRisk").append(self.lastWeekRisk(obj));
	    				}
	    				
	    			});
	    			if(a<=0){
	    				panel.find("#lastWeekWork").html('无');
	    			}
	    			if(b<=0){
	    				panel.find("#lastWeekRisk").html('无');
	    			}
	    		}
			},
			//上周工作风险
			lastWeekRisk:function(value){
				var $weekRisk = null;
				$weekRisk = $('<tr data-pwrid="'+value.proWRepContentId+'"><td>'+value.column1Content+'</td><td>'+value.column2Content+'</td><td><ul></ul></td></tr>');
				if(value.users == null){
				}else{
					$.each(value.users, function(k, obj){
						var $html = $('<li data-id="'+obj.userId+'" title="'+obj.name+'"><img src="'+obj.logo+'" class="img-circle">'+
						'<a class="remove-member-handler">×</a></li>');
						$weekRisk.find('ul').append($html);
					});
				}
				return $weekRisk;
			},
			//本周工作风险
			weekRisk:function(value){
				var $weekRisk = null,self = this;
				$weekRisk = $('<tr data-pwrid="'+value.proWRepContentId+'"><td><input  value="'+value.column1Content+'"  class="transparency projectWeekReportContent weekRisk risk1" readonly="true"></td><td><input  value="'+value.column2Content+'"  class="transparency projectWeekReportContent weekRisk risk2" readonly="true"></td><td><ul></ul></td></tr>');
				if(value.users == null){
				}else{
					var permit = panel.find("#addProblem").length > 0 ?true:false;
					$.each(value.users, function(k, obj){
						var $html = $('<li data-id="'+obj.userId+'" title="'+obj.name+'"><img src="'+obj.logo+'" class="img-circle">'+
						'</li>');
						if(permit){
							$html.append('<a class="remove-member-handler">×</a>');
						}
						$weekRisk.find('ul').append($html);
					});
				}
				if(panel.find("#addProblem").length > 0){
					$weekRisk.find('ul').append('<li data-id="0" class="addUser"><i class="icon-plus-circle icon-big"></i></li> <i class="icon-trash-empty" title="删除"></i>');
				}
				
				return $weekRisk;
			},
			//上周工作
			lastWeekWorks:function(value,a){
				var $weekWorks = null;
				$weekWorks = $(' <tr data-pwrid="'+value.proWRepContentId+'"><td>'+a+'</td><td>'+value.column1Content+'</td><td>'+value.column2Content+'</td><td>'+value.column3Content+'</td><td>'+
						'<ul></ul></td></tr>');
				if(value.users == null){
				}else{
					var permit = panel.find("#addProblem").length > 0 ?true:false;
					$.each(value.users, function(k, obj){
						var $html = $('<li data-id="'+obj.userId+'" title="'+obj.name+'"><img src="'+obj.logo+'" class="img-circle">'+
								'</li>');
						if(permit){
							$html.append('<a class="remove-member-handler">×</a>');
						}
						$weekWorks.find('ul').append($html);
					});
				}
				return $weekWorks;
			},
			//本周工作
			weekWorks:function(value,a){
				var $weekWorks = null, self = this;
				$weekWorks = $(' <tr data-pwrid="'+value.proWRepContentId+'"><td>'+a+'</td><td ><input  value="'+value.column1Content+'"  class="transparency projectWeekReportContent risk1" readonly="true"></td><td width="10%"><input  value="'+value.column2Content+'"  class="risk2 transparency projectWeekReportContent" readonly="true"></td><td width="10%"><input  value="'+value.column3Content+'"  class="risk3 transparency projectWeekReportContent" readonly="true"></td><td>'+
						'<ul></ul></td></tr>');
				var permit = panel.find("#addProblem").length > 0 ?true:false;
				if(value.users == null){
				}else{
					
					$.each(value.users, function(k, obj){
						var $html = $('<li title="'+obj.name+'" data-id="'+obj.userId+'"><img src="'+obj.logo+'" class="img-circle">'+
								'</li>');
						if(permit){
							$html.append('<a class="remove-member-handler">×</a>');
						}
						$weekWorks.find('ul').append($html);
					});
				}
				if(panel.find("#addWork").length > 0){
					$weekWorks.find('ul').append('<li  data-id="0" class="addUser"><i class="icon-plus-circle icon-big"></i></li> <i class="icon-trash-empty" title="删除"></i>');
				}
				
				return $weekWorks;
			},
			//下周计划
			nextWeekPlan:function(value,c){
				var $nextWeekPlan = null, self = this;
				$nextWeekPlan = $('<tr data-pwrid="'+value.proWRepContentId+'"><td>'+c+'</td><td><input  value="'+value.column1Content+'"  class="risk1 transparency projectWeekReportContent" readonly="true"></td><td>'+
						' <ul></ul> </td> </tr>');
				if(value.users == null){
				}else{
					$.each(value.users, function(k, obj){
						var $html = $('<li title="'+obj.name+'" data-id="'+obj.userId+'"><img src="'+obj.logo+'" class="img-circle">'+
								'<a class="remove-member-handler">×</a></li>');
						$nextWeekPlan.find('ul').append($html);
					});
				}
				if(panel.find("#addPlan").length > 0){
					$nextWeekPlan.find('ul').append('<li  data-id="0" class="addUser"><i class="icon-plus-circle icon-big"></i></li> <i class="icon-trash-empty" title="删除"></i>');
				}
				
				return $nextWeekPlan;
			},
			//项目周报页面点击事件
			btnClick:function(){
				
				var self = this,progress = "",status = "",phase = "";
				//点击工作总结后面的修改按钮
				panel.on('click','.icon-pencil',function(){
					$(this).prop('disabled',true);
					var comment = $(this).nextAll('p').html();
					var reg=new RegExp("<br>","g");
	          	    var c = comment.replace(reg,"\n");
	          	    $(this).nextAll('textarea').html(c);
	          	    $(this).nextAll('textarea').show();
					$(this).nextAll('textarea').focus();
					$(this).nextAll('p').hide();
				});
				//点击项目状态和阶段使其能够输入
				panel.on("click","input.projectWeekReportContent",function(){
					if(panel.find("#addProblem").length > 0){
						$(this).removeClass('transparency');
						$(this).addClass('form-control');
						$(this).attr("readonly",false);
					}
					
				});
				//项目状态 进度和阶段的验证
				if(panel.find("#progress").hasClass('transparency')){
					var data = panel.find("#progress");
					if(!progress){
						progress = new InputValidate(data, true, 30, 0, 'percentage', null);
					}else{
						progress.target.removeClass("error");
					}
				}
				if(panel.find("#status").hasClass('transparency')){
					var data = panel.find("#status");
					if(!status){
						status = new InputValidate(data, false, 30, 0, '', null);
					}else{
						status.target.removeClass("error");
					}
				}
				if(panel.find("#phase").hasClass('transparency')){
					var data = panel.find("#phase");
					if(!phase){
						phase = new InputValidate(data, false, 30, 0, '', null);
					}else{
						phase.target.removeClass("error");
					}
				}
				//对下周计划  本周工作点击，可以修改
				panel.on("click","input.projectWeekReport",function(){
					if(panel.find("#addProblem").length){
						$(this).removeClass('transparency');
						$(this).addClass('form-control input-width');
						$(this).attr("readonly",false);
					}
					
				});
				//添加计划
				panel.on("click","#addPlan",function(){
					self.planValiArray = {};
					var number = panel.find("#nextWeekPlan .workriskplan1").length;
					if(number >= 1){
						layer.msg("请保存后再添加");
						return false;
					}
					var num = panel.find("#nextWeekPlan tbody").children("tr").length;
					panel.find("#nextWeekPlan").append('<tr data-type="00903" data-pwrid="0"><td>'+num+'</td><td><input  value="" class="form-control workriskplan1 risk1"></td><td>'+
							' <ul>'+
							 '<li  data-id="0" class="addUser"><i class="icon-plus-circle icon-big"></i></li>'+
							 '<button class=" btn btn-sm-outer savePlanWRContent">保存</button>'+
							 '<button class="cancelWRContent btn btn-sm-outer" style="background:#fff; border:1px solid #ddd; color:#808080">取消<tton>'+
							'</ul> </td> </tr>');
					var content1 = panel.find("#nextWeekPlan .workriskplan1");
					content1.focus();
				});
				//添加工作
				panel.on("click","#addWork",function(){
					self.workValiArray = {};
					var number = panel.find("#weekWorks .workriskplan1").length;
					if(number >= 1){
						layer.msg("请保存后再添加");
						return false;
					}
					var num = panel.find("#weekWorks tbody").children("tr").length;
					panel.find("#weekWorks").append('<tr data-pwrid="0" data-type="00901"><td>'+num+'</td><td><input  value="" class="form-control workriskplan1 risk1"></td><td>'+
							'<input  value="" class="form-control  workriskplan2 "></td><td>'+
							'<input  value="" class="form-control workriskplan3"></td><td>'+
							'<ul><li  data-id="0" class="addUser"><i class="icon-plus-circle icon-big"></i></li>'+
							'<button class=" btn btn-sm-outer saveWorkWRContent">保存</button>'+
							 '<button class="cancelWRContent btn btn-sm-outer" style="background:#fff; border:1px solid #ddd; color:#808080">取消<tton>'+
							'</ul> </td> </tr>');
					var content1 = panel.find("#weekWorks .workriskplan1");
					content1.focus();
				});
				//添加问题
				panel.on("click","#addProblem",function(){
					self.riskValiArray = {};
					var number = panel.find("#weekRisk .workriskplan1").length;
					if(number >= 1){
						layer.msg("请保存后再添加");
						return false;
					}
					panel.find("#weekRisk").append('<tr data-pwrid="0" data-type="00900"><td><input value="" class="form-control workriskplan1"></td>'+
							'<td><input value="" class="form-control workriskplan2"></td><td> <ul>'+
							' <li  data-id="0" class="addUser"><i class="icon-plus-circle icon-big"></i></li>'+
							'<button class=" btn btn-sm-outer saveProblemWRContent">保存</button>'+
							 '<button class="cancelWRContent btn btn-sm-outer" style="background:#fff; border:1px solid #ddd; color:#808080">取消<tton>'+
							 '</ul></td></tr>');
					var content1 = panel.find("#weekRisk .workriskplan1");
					content1.focus();
				});
				panel.on("click",".cancelWRContent",function(){
					$(this).closest('tr').remove();
				});
				//保存项目状态和阶段
				panel.on("focusout",".projectWeekReport",function(){
					var data = $(this);
					var type = $(this).attr('id');
					var statusInfo= panel.find("#status").val();
					var phaseInfo= panel.find("#phase").val();
					var progressInfo = panel.find("#progress").val();
//					var pwrId = panel.find('#proWeekReport').data('pwrid');
					$.ajax({
		        		type:'post',
		        		data: {projectWeeklyReportId:pwrId,phase:phaseInfo,status:statusInfo,progress:progressInfo,type:type },
		        		url:context + '/weeklyReport/updateProWeekRep.htm',
		        		beforeSend:function(){
							var validateInfo = false;
							if(type == "phase"){
								if(!phase.checkValidate()){
									validateInfo = true;
									return false;
								}
							}else if(type =="progress"){
								if(!progress.checkValidate()){
									validateInfo = true;
									return false;
								}
							}else{
								if(!status.checkValidate()){
									validateInfo = true;
									return false;
								}
							}
							return true;
						}
		        	}).done(function(resultData){
		                  if(resultData && resultData.success){
		      				data.addClass('transparency');
		      				 if(type !="progress"){
		      					data.attr('title',data.val());
		      				 }
		      				data.removeClass('form-control');
		      				data.attr("readonly",true);
		                  }else if(resultData && !resultData.success){
		                      layer.msg(resultData.errormsg);
		                  }else{
		                      layer.msg("请求失败");
		                  };
		            });
				});
				var eventType = "eventSelectUser";
				//添加参与者
				panel.on('click','ul li.addUser', function(){
					var arr =[];
	            	$(this).prevAll("li").each(function(){
	            		arr.push($(this).data("id").toString());
	            	});
	            	personList.init({
	            		searchInput:true,//是否需要搜索框
		                data : window.projectUserList || {},
		                claiming : false,//是否先是待认领,要选中未认领时，selectData有一个值为0就可以了
		                selectData : arr,//要选中的数据
		                dom : $(this),
		                container : $(this),
		                eventType:eventType
	                });
	                return false;
				});
				//在参与者弹出框里选择或者取消某参与者时触发的事件
				panel.on(eventType,'ul li.addUser', function(event, params){
					//如果是添加的话不用先与后台交互
					if($(this).closest('tr').attr('data-pwrid') <= 0){
						if(params.callback){
							params.callback();
							return false;
						}
						
						return false;
					}
					var userIds="";
					$(this).prevAll("li").each(function(){
	            		userIds += $(this).data("id")+",";
	            	});
					userIds += params.ids;
					var options = {
						userIds:userIds,
						pwrcId:$(this).closest('tr').attr('data-pwrid'),
						callback:params && params.callback
					};
					self.operatorJoninUser(options);
				});
				//移除参与者
				panel.on("click",".remove-member-handler", function(){//参与者人员删除
					if($(this).closest('tr').attr('data-pwrid') <= 0){
						$(this).closest("li").remove();
						layer.closeAll();
						return false;
					}
					var deleteUser = $(this);
					var userIds="";
					$(this).closest('li').siblings("li").each(function(){
						if($(this).data('id') != 0){
							userIds += $(this).data('id')+",";
						}
					});
					if(userIds.length>0 ){
						userIds = userIds.substring(0,userIds.length-1);
					}
					var options = {
							userIds:userIds,
							pwrcId:$(this).closest('tr').attr('data-pwrid'),
							callback:function(){
								deleteUser.closest("li").remove();
							}
						};
					self.operatorJoninUser(options);
	            });
				//更新项目周报内容
				panel.on("focusout",".projectWeekReportContent",function(){
					var validate = "";
					var data = $(this), type = 1;
					if(data.hasClass('risk1')){
						if(!validate){
							validate = new InputValidate($(this), true, 500, 0, '', null);
						}else{
							validate.target.removeClass("error");
						}
					}
					if(data.hasClass('risk2')){
						type = 2;
						if(!validate){
							validate = new InputValidate($(this), false, 500, 0, '', null);
						}else{
							validate.target.removeClass("error");
						}
					}
					if(data.hasClass('risk3')){
						type = 3;
						if(!validate){
							validate = new InputValidate($(this), false, 500, 0, '', null);
						}else{
							validate.target.removeClass("error");
						}
					}
					
					var pwrcId = $(this).closest('tr').data('pwrid');
					var content1,content2,content3;
					content1 = $(this).closest('tr').find('.risk1').val();
					if($(this).closest('tr').find('.risk2').length > 0){
						content2 = $(this).closest('tr').find('.risk2').val();
					}else{
						content2 == null;
					}
					if($(this).closest('tr').find('.risk3').length > 0){
						content3 = $(this).closest('tr').find('.risk3').val();
					}else{
						content3 == null;
					}
					
					$.ajax({
		        		type:'post',
		        		data: {pwrcId:pwrcId,content1:content1,content2:content2,content3:content3,type:type },
		        		url:context + '/weeklyReport/updateProWeekRepContent.htm',
		        		beforeSend:function(){
							var validateInfo = false;
								if(!validate.checkValidate()){
									validateInfo = true;
									return false;
								}
							return true;
						}
		        	}).done(function(resultData){
		                  if(resultData && resultData.success){
		                	  data.addClass('transparency');
		      				  data.removeClass('form-control');
		      				  data.attr("readonly",true);
		                  }else if(resultData && !resultData.success){
		                      layer.msg(resultData.errormsg);
		                  }else{
		                      layer.msg("请求失败");
		                  };
		            });
				});
				//添加本周问题
				panel.on("click",".saveProblemWRContent",function(){
					problemwRContentValiArray = {};
					var btn = $(this);
					btn.prop('disabled', true);
					var thisWRC = btn.closest('tr');
					var userIds = "";
					var type = thisWRC.data('type');
					thisWRC.find('.addUser').prevAll("li").each(function(){
	            		userIds += $(this).data("id")+",";
	            	});
					if(userIds.length > 0){
						userIds = userIds.substring(0,userIds.length-1);
					}
					var data1 = thisWRC.find('.workriskplan1');
//					var pwrId = panel.find('#proWeekReport').data('pwrid');
					var content1,content2;
					content1 = data1.val();
					if(!problemwRContentValiArray.content1){
						problemwRContentValiArray.content1 = new InputValidate(data1, true, 500, 0, '', null);
					}else{
						problemwRContentValiArray.content1.target.removeClass("error");
					}
					var data2 = thisWRC.find('.workriskplan2');
					content2 = data2.val();
					if(!problemwRContentValiArray.content2){
						problemwRContentValiArray.content2 = new InputValidate(data2, false, 500, 0, '', null);
					}else{
						problemwRContentValiArray.content2.target.removeClass("error");
					}
					$.ajax({
		        		type:'post',
		        		data: {pwrId:pwrId,content1:content1,content2:content2,content3:null,type:type,userIds:userIds },
		        		url:context + '/weeklyReport/saveProWeekRepContent.htm',
		        		beforeSend:function(){
							var validate = false;
							$.each(problemwRContentValiArray, function(key, value){
								if(!value.checkValidate()){
									validate = true;
									return false;
								}
							});						
							if(validate){
								btn.prop('disabled', false);
								return false;
							}
							return true;
						},
						success:function(resultData){
							if(resultData && resultData.success && resultData.resultData){
								self.wRContentValiArray = {};
			                	  var data = resultData.resultData;
			                	  data1.addClass('transparency risk1 projectWeekReportContent').removeClass('workriskplan1 form-control').attr("readonly",true);
			      				if(data2.length > 0){
			      					data2.addClass('transparency risk2 projectWeekReportContent').removeClass('form-control workriskplan2').attr("readonly",true);
			      				}
			                	 thisWRC.attr("data-pwrid",data);
			                	 btn.next('button').remove();
			                	 btn.remove();
			                	 thisWRC.find('.addUser').after('<i class="icon-trash-empty" title="删除"></i>');
			                  }else if(resultData && !resultData.success){
			                      layer.msg(resultData.errormsg);
			                  }else{
			                      layer.msg("请求失败");
			                  };
						}
		        	});
				});
				//添加本周工作
				panel.on("click",".saveWorkWRContent",function(){
					workwRContentValiArray = {};
					var btn = $(this);
					btn.prop('disabled', true);
					var thisWRC = btn.closest('tr');
					var userIds = "";
					var type = thisWRC.data('type');
					thisWRC.find('.addUser').prevAll("li").each(function(){
	            		userIds += $(this).data("id")+",";
	            	});
					if(userIds.length > 0){
						userIds = userIds.substring(0,userIds.length-1);
					}
					var data1 = thisWRC.find('.workriskplan1');
//					var pwrId = panel.find('#proWeekReport').data('pwrid');
					
					var content1,content2,content3;
					content1 = data1.val();
					if(!workwRContentValiArray.content1){
						workwRContentValiArray.content1 = new InputValidate(data1, true, 500, 0, '', null);
					}else{
						workwRContentValiArray.content1.target.removeClass("error");
					}
					var data2 = thisWRC.find('.workriskplan2');
					content2 = data2.val();
					if(!workwRContentValiArray.content2){
						workwRContentValiArray.content2 = new InputValidate(data2, false, 500, 0, '', null);
					}else{
						workwRContentValiArray.content2.target.removeClass("error");
					}
					var data3 = thisWRC.find('.workriskplan3');
					content3 = data3.val();
					if(!workwRContentValiArray.content3){
						workwRContentValiArray.content3 = new InputValidate(data3, false, 500, 0, '', null);
					}else{
						workwRContentValiArray.content3.target.removeClass("error");
					}	
					$.ajax({
		        		type:'post',
		        		data: {pwrId:pwrId,content1:content1,content2:content2,content3:content3,type:type,userIds:userIds },
		        		url:context + '/weeklyReport/saveProWeekRepContent.htm',
		        		beforeSend:function(){
							var validate = false;
							$.each(workwRContentValiArray, function(key, value){
								if(!value.checkValidate()){
									validate = true;
									return false;
								}
							});						
							if(validate){
								btn.prop('disabled', false);
								return false;
							}
							return true;
						},
						success:function(resultData){
							if(resultData && resultData.success && resultData.resultData){
								self.wRContentValiArray = {};
			                	  var data = resultData.resultData;
			                	  data1.addClass('transparency risk1 projectWeekReportContent').removeClass('form-control workriskplan1').attr("readonly",true);
			      				data2.addClass('transparency risk2 projectWeekReportContent').removeClass('form-control workriskplan2').attr("readonly",true);
			      				data3.addClass('transparency risk3 projectWeekReportContent').removeClass('form-control workriskplan3').attr("readonly",true);
			                	 thisWRC.attr("data-pwrid",data);
			                	 btn.next('button').remove();
			                	 btn.remove();
			                	 thisWRC.find('.addUser').after('<i class="icon-trash-empty" title="删除"></i>');
			                  }else if(resultData && !resultData.success){
			                      layer.msg(resultData.errormsg);
			                  }else{
			                      layer.msg("请求失败");
			                  };
						}
		        	});
				});
				//保存下周计划
				panel.on("click",".savePlanWRContent",function(){
					planwRContentValiArray = {};
					var btn = $(this);
					btn.prop('disabled', true);
					var thisWRC = btn.closest('tr');
					var userIds = "";
					var type = thisWRC.data('type');
					thisWRC.find('.addUser').prevAll("li").each(function(){
	            		userIds += $(this).data("id")+",";
	            	});
					if(userIds.length > 0){
						userIds = userIds.substring(0,userIds.length-1);
					}
					var data1 = thisWRC.find('.workriskplan1');
//					var pwrId = panel.find('#proWeekReport').data('pwrid');
					var content1;
					content1 = data1.val();
					if(!planwRContentValiArray.content1){
						planwRContentValiArray.content1 = new InputValidate(data1, true, 500, 0, '', null);
					}else{
						planwRContentValiArray.content1.target.removeClass("error");
					}
					$.ajax({
		        		type:'post',
		        		data: {pwrId:pwrId,content1:content1,content2:null,content3:null,type:type,userIds:userIds },
		        		url:context + '/weeklyReport/saveProWeekRepContent.htm',
		        		beforeSend:function(){
							var validate = false;
							$.each(planwRContentValiArray, function(key, value){
								if(!value.checkValidate()){
									validate = true;
									return false;
								}
							});						
							if(validate){
								btn.prop('disabled', false);
								return false;
							}
							return true;
						},
						success:function(resultData){
							if(resultData && resultData.success && resultData.resultData){
								self.wRContentValiArray = {};
			                	  var data = resultData.resultData;
			                	  data1.addClass('transparency risk1 projectWeekReportContent').removeClass('form-control workriskplan1').attr("readonly",true);
			                	 thisWRC.attr("data-pwrid",data);
			                	 btn.next('button').remove();
			                	 btn.remove();
			                	 thisWRC.find('.addUser').after('<i class="icon-trash-empty" title="删除"></i>');
			                  }else if(resultData && !resultData.success){
			                      layer.msg(resultData.errormsg);
			                  }else{
			                      layer.msg("请求失败");
			                  };
						}
		        	});
				});
				//删除计划或者本周工作
				panel.on("click",".icon-trash-empty",function(){
					$(this).prop('disabled',true);
					var pwrc = $(this).closest('tr');
					var pwrcId = pwrc.data('pwrid');
					$.ajax({
		        		type:'post',
		        		data: {pwrcId:pwrcId },
		        		url:context + '/weeklyReport/deletepwrContent.htm'
		        	}).done(function(resultData){
		                  if(resultData && resultData.success){
		                	  var type = pwrc.closest('table');
		                	   pwrc.remove();
		                	  if(type.attr('id') == "weekWorks" || type.attr('id') == "nextWeekPlan"){
		                		  type.find('.risk1').each(function(){
		                			  var num = $(this).closest('tr').prevAll('tr').length ;
		                			  $(this).closest('td').prev('td').text(num);
		                		  });
		                	  }
		                	 
		                  }else if(resultData && !resultData.success){
		                      layer.msg(resultData.errormsg);
		                  }else{
		                      layer.msg("请求失败");
		                  }
		            });
				});
				//保存本周总结
				panel.on("focusout","textarea.weekly-control",function(){
					var content = $(this),contentValidate="";
					if(!contentValidate){
						contentValidate = new InputValidate(content, false, 500, 0, 'textarea', null);
					}else{
						contentValidate.target.removeClass("error");
					}
					if(!contentValidate.checkValidate()){
						return;
					}
//					var pwrId = panel.find('#proWeekReport').data('pwrid');
					var pwrcId = $(this).closest('div').attr("data-pwrcid");
					var content = $(this).val();
					$.ajax({
		        		type:'post',
		        		data: {pwrId:pwrId,content:content,pwrcId:pwrcId },
		        		url:context + '/weeklyReport/updateWorkContent.htm'
		        	}).done(function(resultData){
		                  if(resultData && resultData.success){
		                	  var reg=new RegExp("\n","g");
			                  var c = content.replace(reg,"<br>");
			                  panel.find('div.task-content p').html(c);
		                	  panel.find('div.task-content p').show();
		                	  panel.find('div.task-content textarea').hide();
		                	  var data = resultData.resultData;
		                	  panel.find("div.task-content").attr('data-pwrcid',data);
		                	  panel.find('div.task-content i ').prop('disabled',false);
		                  }else if(resultData && !resultData.success){
		                      layer.msg(resultData.errormsg);
		                  }else{
		                      layer.msg("请求失败");
		                  }
		            });
				});
				//查看某一周的周报
				panel.find('ul.weekly-item').on('click','li',function(){
					$(this).siblings('li').removeClass('select').end()
							.addClass('select');
					var data = $(this).data('weeks').split('~')[0];
					$.ajax({
		        		type:'post',
		        		data: {projectId:projectId,monday:data},
		        		url:context + '/weeklyReport/findProWeekReport.htm'
		        	}).done(function(resultData){
		                  if(resultData && resultData.success){
		                	  var data = resultData.resultData;
		                	  if(data && data.projectWeeklyReportId){
		                		  self.initProWeekReportContent(data);
		                	  }else{
		                		  panel.find('div#projectWeekReport_div').html('<div class="none-content"><i class="icon-clipboard-1"></i><br /><a>系统未生成本周'+TEXT_CONFIG.xiangmu+'周报数据</a></div>');
		                	  }
		                  }else if(resultData && !resultData.success){
		                      layer.msg(resultData.errormsg);
		                  }else{
		                      layer.msg("请求失败");
		                  };
		            });
				});
			},
			//更新项目周报内容的人员
			operatorJoninUser:function(options){
				var dataParams = {
						userIds:options.userIds,
						ids:options.ids,
						pwrcId:options.pwrcId
					};
					$.ajax({
						type:'post',
						data:dataParams,
						url:context + '/weeklyReport/operatorJoninUser.htm'
					}).done(function(resultData){
						if(resultData && resultData.success){
							if(options.callback){
								options.callback();
							}
						}else if(resultData && !resultData.success && resultData.errormsg){
							layer.alert(resultData.errormsg, 1);
						}else{
							layer.alert('请求失败', 1);
						}
					}).fail(function(){
						
					});
			}
	};
	return common;
});