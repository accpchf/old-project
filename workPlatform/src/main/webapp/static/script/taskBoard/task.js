/**
 * 任务主模块
 */
require(["jquery",
         "script/common/personList",
         "script/common/dialogAlert",
         "script/taskBoard/addTaskForm",
         context+"/static/script/taskBoard/installHtml.js?_T="+(new Date()).valueOf(),
         "jqueryUI",
         "layer",
         "my97",
         "jqueryForm"],function($,personList,dialogAlert,addTaskForm,installHtml){
	//任务版界面
	var panel = $("#updateTaskRightLayer");
	var taskLastData = panel.data("taskInfo");
	var taskDoneStatus = $("#completeCode").val();//任务已经完成的状态
	var taskNotDoneStatus = $("#doingCode").val();//任务未完成的状态
	var taskWaitAuditStatus = $("#toCheckCode").val();//任务待审核的状态
	var taskNotPassStatus = $("#noPassCode").val();//任务不通过的状态
	var taskPriorityGeneral = $("#level0Code").val();//任务优先级 一般
	var taskPriorityUrgent = $("#level1Code").val();//任务优先级 紧急
	var checkItemDone = $("#itemCompleteCode").val();//检查项已完成
	var visibleStatusAllUser = $("#visibleStatusAllUser").val();//可见性  所有成员可见
	var visibleStatusTaskMember = $("#visibleStatusTaskMember").val();//可见性  仅参与者可见
	 //操作区
	var taskBoard = $("div.main-content[data-id='projectTemplate']");
	var taskModel = {
		init : function(){
			if($("div.main-content[data-id='projectTemplate']").length > 0){
				this.taskBoardPanel = $("div.main-content[data-id='projectTemplate']");//项目的任务版界面
			}else{
				this.personanTaskPanel = $("#personalTaskContainer");//个人任务界面
			}
			this.taskTitleBar = panel.find("#taskTitleBar");//任务标题栏
			this.updateTaskContent = panel.find("#updateTaskContent");//任务内容
			this.updateRemark = panel.find("#updateRemark");//备注
			this.updateExecutor = panel.find("#updateExecutor");//执行者
			this.updateEndDate = panel.find("#updateEndDate");//截止日期
			this.updateLevel = panel.find("#updateLevel");//任务级别
			this.visibilityUser = panel.find("#visibilityUser");//可见性设置
			this.status = panel.find("#status");//任务级别
			this.admin = panel.find("#admin");//管理员
			this.createdTime = panel.find("#createdTime");//创建时间
			this.planTime = panel.find("#planTime");//计划工时
			this.realityTime = panel.find("#realityTime");//实际工时
			this.updateCheckItem = panel.find("#updateCheckItem");//检查项
			this.updatePartner = panel.find("#updatePartner");//参与者
			this.updateChildTask = panel.find("#updateChildTask");//子任务区域
			this.tabContent = panel.find("#tabContent");//动态附件切换区
			// this.discussContent = panel.find("#discussContent");//任务讨论区
			this.initData(taskLastData);//初始化
			this.setEvent();//事件监听
		},
		initData : function(data){//初始化数据
			if(!data){
				return false;
			}
			var self = this;
			
			this.projectId = data.projectId;
			//当前任务在列中任务对象
			this.userList = {};
			if(this.taskBoardPanel){
				this.taskBlock = this.taskBoardPanel.find(".main-task").find("ul > li[data-id='"+data.taskLine.taskLineId+"']").find(".block[data-id='"+data.taskId+"']");
				this.listTaskLinesData = this.taskBoardPanel.find(".main-task").data("listTaskLinesData");//所有列的数据
				this.boardList = $("#switchTaskBoard",this.taskBoardPanel).data("taskBoardList");//所有版的list
				this.boardId = $("#switchTaskBoard",this.taskBoardPanel).data("id");//当前使用版的id
				panel.data("boardId",this.boardId);
				this.userList = window.projectUserList;
				this.initSpecialData();
			}else{
				this.taskBlock = this.personanTaskPanel.find("li[data-id='"+data.taskId+"']");
			}
			//当前任务所在列的名称
			this.taskTitleBar.find("#moveOtherStage").text(installHtml.getStrActualLen(data.taskLine.name,20)).data("id",data.taskLine.taskLineId);
			//checkBox 任务是否完成
			if(data.status === taskWaitAuditStatus){
				this.taskTitleBar.find("#waitAudit i").show();
			}else if(data.status === taskDoneStatus){
                this.taskTitleBar.find("#pass i").show();
            }
            else if(data.status === taskNotPassStatus){
                this.taskTitleBar.find("#notPass i").show();
            }
			//任务内容
			this.updateTaskContent.find("textarea").val(data.content);
			//备注内容
			if(data.remark && data.remark.length > 0){
				this.updateRemark.find("a").hide();
				this.updateRemark.find("span").show();
				this.updateRemark.find("p").text(data.remark);
			}
			//截止时间
			if(data.dueTime){
				var date = new Date(data.dueTime);
				this.updateEndDate.find("input").val(date.getFullYear()+"年"+appendzero(date.getMonth()+1)+"月"+appendzero(date.getDate())+"日");//截止日期
			}
			//创建时间
			if(data.createdTime){
				var date = new Date(data.createdTime);
				this.createdTime.find("a").text(date.getFullYear()+"年"+appendzero(date.getMonth()+1)+"月"+appendzero(date.getDate())+"日");//创建时间
			}
			//计划工时
			if(data.planTime){
				this.planTime.show();
				this.planTime.find("a").text(data.planTime);//计划工时
			}
			//实际工时
			if(data.realityTime){
				this.realityTime.show();
				this.realityTime.find("a").text(data.realityTime);//实际工时
			}
			//可见性
			if(data.visibleStatus == visibleStatusAllUser){
				this.visibilityUser.find("#changeVisibility").html("所有成员可见");
			}else{
				this.visibilityUser.find("#changeVisibility").html("仅参与者可见");
			}
			//任务优先级
			if(data.priority == taskPriorityGeneral){
				this.updateLevel.find("#changeLevel").html("<i class='icon-record gray'></i>一般");
			}else{
				this.updateLevel.find("#changeLevel").html("<i class='icon-record green'></i>紧急");
			}
			//状态
			if(data.status == taskNotDoneStatus){
				this.status.find("a").text($("#doingText").val());
			}
			else if(data.status == taskDoneStatus){
				this.status.find("a").text($("#completeText").val());
			}
			else if(data.status == taskWaitAuditStatus){
				this.status.find("a").text($("#toCheckText").val());
			}
			else if(data.status == taskNotPassStatus){
				this.status.find("a").text($("#noPassText").val());
			}
			//检查项
			this.loadTaskCheckItem(data.taskId);
			
			//任务上传对应的任务Id
			self.tabContent.find("#fileTaskId").val(data.taskId);
			self.tabContent.find("#uploadProjectId").val(self.projectId);
			self.tabContent.find("#switchTab span").text(data.noPassTimes);
			if(data.noPassReason){
				var noPassReason =  data.noPassReason.split("|");
				$.each(noPassReason, function(index,value){
					var li = $("<li>"+value+"</li>");
					self.tabContent.find("div.nopassReason ul").append(li);
				});
				data.noPassReason.split("|");
			}
			
			//子任务
			this.loadChildTask(data.taskId);
			//任务动态
			this.loadDynamicContent(data.taskId);
			//任务讨论留言
			// this.loadDiscuss(data.taskId);
			//任务附件
			this.loadTaskAttachments(data.taskId);
			//如果是非任务版界面,获取任务相关数据
			
			if(!this.taskBoardPanel){			
				this.getTaskRelatedData();
			}
		},
        getUserInfo : function(id){//通过Id获得用户的logo和name
        	var self = this;
        	var retUser = {logo:"",name:""};
        	var user = self.userList[id];
        	if(id>0){
        		if(user){
        			retUser.logo = user.logo;
        			retUser.name = user.name;
				}
        	}else{
        		retUser.logo = context+"/static/images/pic-shelters.png";
        		retUser.name = "待认领";
        	}
        	return retUser;
        },
		initSpecialData : function(){//初始化任务数据（执行者 任务参与者 当前的任务版名称）
			var self = this;
			var data = taskLastData;
			//执行者
			if(data.executor > 0){
				var user = self.getUserInfo(data.executor);
				self.updateExecutor.find("a").html("<img data-id='"+data.executor+"' src='"+user.logo+"' class='img-circle'>"+user.name);
			}
			if(data.admin > 0){
				var user = self.getUserInfo(data.admin);
				self.admin.find("a").html("<img data-id='"+data.admin+"' src='"+user.logo+"' class='img-circle'>"+user.name);
			}
			//任务参与者
			var str ="";
			if(data.partnerIds && data.partnerIds.length > 0){
				$.each(data.partnerIds, function(k,id){
					var user2 = self.getUserInfo(id);
					str+="<li data-id='"+id+"' title='"+user2.name+"'><img src='"+user2.logo+"' class='img-circle'>";
					if($.inArray('TASK:UPDATE_MEMBER', taskLastData.permits) >= 0){
					    str+="<a class='remove-member-handler'>×</a>";
					}
					str+="</li>";
				});
			}
			this.updatePartner.find("ul").prepend(str);
			//当前的任务版名称
			$.each(self.boardList,function(key,value){
				if(value.taskBoardId == self.boardId){
					self.taskTitleBar.find("#moveOtherTaskgroup").text(installHtml.getStrActualLen(value.name,20)).data("id",self.boardId);
					return false;
				}
			});
		},
		reloadUserPic : function(){//加载没有被加载上的图片
			var self = this;
			if(self.userList){
				panel.find("img.img-circle[src='']").each(function(){
					var user = self.getUserInfo($(this).data("id"));
					$(this).attr("src",user.logo).attr("title",user.name);
					$(this).next("a").text(user.name);
				});
			}
		},
		getTaskRelatedData : function(){
			var self = this;
			$.ajax({
				type : "POST",
				url : context + "/task/findTaskOtherData.htm",
				data : {projectId : self.projectId, taskId:taskLastData.taskId}
			}).done(function(result){
				if(result && result.success && result.resultData){
					self.listTaskLinesData = result.resultData.lineList;//所有列的数据
					self.boardList = result.resultData.boardList;//所有版的list
					self.boardId = result.resultData.boardId;//当前使用版的id
					self.userList = result.resultData.userList;//任务所属的所有人员
					panel.data("boardId",self.boardId);
					
					self.initSpecialData();
					self.reloadUserPic();
				}else{
					layer.msg(result.errormsg);
				}
				
			}).fail(function(){
				layer.msg("请求失败");
			});
		},
		loadDynamicContent : function(taskId){
			var self = this;
			$.ajax({
     			type:'post',
     			data: {taskId: taskId},
     			url:context + '/task/listTaskActionRecords.htm'
     		}).done(function(data){
                if(data && data.success){
                	var str = "";
        			if(data.resultData){
        				for(var i = 0 ; i < data.resultData.length ;i++){
        					str += self.activityHtml(data.resultData[i]);
        				}
        				panel.find("#activityContent").append(str);
        				if(self.taskBoardPanel){
        					self.reloadUserPic();
        				}
        			}
                }else if(resultData && !resultData.success){
                    layer.msg(resultData.errormsg);
                }else{
                    layer.msg("请求失败");
                };
            });
		},
		loadTaskCheckItem : function(taskId){
			var self = this;
			$.ajax({
     			type:'post',
     			data: {taskId: taskId},
     			url:context + '/task/findTaskCheckList.htm'
     		}).done(function(data){
                if(data && data.success){
                	var checkStr = "";
        			var percent = 0;
                	if(data.resultData){
        				var len = data.resultData.length;
        				if(len > 0){
        					var doneCount = 0;
            				for(var i = 0 ;i<len;i++){
            					var obj = data.resultData[i];
            					if(obj.status == checkItemDone){
            						doneCount++;
            						checkStr +="<li data-id='"+obj.taskCheckListId+"'><span class='check-box'><i class='icon-ok'></i></span><span class='checkItemContent'>"+obj.listContent+"</span></li>";
            					}else{
            						checkStr +="<li data-id='"+obj.taskCheckListId+"'><span class='check-box'><i class='icon-ok' style='display:none'></i></span><span class='checkItemContent'>"+obj.listContent+"</span></li>";
            					}
            				}
            				self.updateCheckItem.find("ul.check-item").append(checkStr);
            				percent = parseInt((doneCount/len)*100);
        				}
                	}
                	var progressbar=$( "#progressbar" ).progressbar({value: percent}).css({"height":15,"background": "#CFCFCF"});
        			progressbar.find('.progress-label').html(percent + "%&nbsp;");
                }else if(resultData && !resultData.success){
                    layer.msg(resultData.errormsg);
                }else{
                    layer.msg("请求失败");
                };
            });
		},
		loadChildTask : function(taskId){//加载子任务
			var self = this;
			$.ajax({
     			type:'post',
     			data: {taskId: taskId},
     			url:context + '/task/findParentAndChildTask.htm'
     		}).done(function(data){
                if(data && data.success){
                	if(data.resultData){
        				if(data.resultData.parentTask){//父任务
        					self.updateChildTask.find("#parentTask").find("ul").append(installHtml.childTaskHtml(self.userList,data.resultData.parentTask));
        				}else{
        					self.updateChildTask.find("#parentTask").find("ul").text("无");
        				}
        				var childCount = data.resultData.children.length;
        				if(childCount > 0){
        					var childStr = "";
        					for(var i = 0 ; i < childCount ;i++){//子任务
        						var obj = data.resultData.children[i];
        						childStr +=installHtml.childTaskHtml(self.userList,obj);
        					}
        					self.updateChildTask.find("#childTask").find("ul").append(childStr);
        				}else{
        					self.updateChildTask.find("#childTask").find("ul").text("无");
        				}
        			}else{
        				self.updateChildTask.find("#parentTask").find("ul").text("无");
        				self.updateChildTask.find("#childTask").find("ul").text("无");
        			}
                	if(self.taskBoardPanel){
    					self.reloadUserPic();
    				}
                }else if(resultData && !resultData.success){
                    layer.msg(resultData.errormsg);
                }else{
                    layer.msg("请求失败");
                };
            });
		},
		loadTaskAttachments : function(taskId){//加载附件
			var self = this;
			$.ajax({
     			type:'post',
     			data: {taskId: taskId},
     			url:context + '/task/findAttachments.htm'
     		}).done(function(data){
                if(data && data.success){
                	var attachmentStr = "";
        			if(data.resultData){
        				for(var i = 0 ; i < data.resultData.length ;i++){
        					attachmentStr += self.fileHtml(data.resultData[i]);
        				}
        				panel.find("#taskFileContend").append(attachmentStr);
        				if(self.taskBoardPanel){
        					self.reloadUserPic();
        				}
        			}
                }else if(resultData && !resultData.success){
                    layer.msg(resultData.errormsg);
                }else{
                    layer.msg("请求失败");
                };
            });
		},
		loadDiscuss : function(taskId){//加载讨论
			var self = this;
			$.ajax({
     			type:'post',
     			data: {taskId: taskId},
     			url:context + '/task/findTaskDiscuss.htm'
     		}).done(function(data){
                if(data && data.success){
                	var discussStr = "";
        			if(data.resultData){
        				for(var i = 0 ; i < data.resultData.length ;i++){
        					discussStr += self.discussHtml(data.resultData[i]);
        				}
        				panel.find("#taskMessage").append(discussStr);
        				if(self.taskBoardPanel){
        					self.reloadUserPic();
        				}
        			}
                }else if(resultData && !resultData.success){
                    layer.msg(resultData.errormsg);
                }else{
                    layer.msg("请求失败");
                };
            });
		},
		groupIndex:"",//移动到其他任务组弹出层index
		stageIndex:"",//移动到其他阶段弹出层index
		stageMenuIndex :"",//任务菜单按钮弹出层index
		closeAllLayer : function(){//关闭相应的弹出层
			$("#selectUserLayer").closest(".xubox_layer").remove();//选择人员层
			panel.find("#alterContentArea").remove();//添加框
			this.regainRemark(panel.find("#updateRemark").find("p").text());//备注
			this.updateChildTask.find("#addChildTaskForm").empty();//子任务
			layer.close(this.groupIndex);//组切换层关闭
			layer.close(this.stageIndex);//列切换层关闭
			layer.close(this.stageMenuIndex);//任务菜单层关闭
			panel.find("#updateCheckItem").find("li").show();//检查项
			this.updateLevel.find("ul").hide();//任务级别
		},
		updateTaskLastData : function(name,value){//更新任务数据源
			taskLastData[name] = value;
			panel.data("taskInfo",taskLastData);
		},
		taskMenuHtml : function(){//任务菜单
            return "<div class='layer-lg200 boardBackground floatLayer'>"+
                                "<div class='layer-title'>"+
                                    "<h5>任务菜单</h5>"+
                                    "<i class='icon-cancel-circled-outline'></i>"+
                                "</div>"+
                                "<ul>"+
                                    "<li  id='deleteTask'><a> <i class='icon-pencil'></i>删除任务</a></li>"+
                                "</ul>"+
                            "</div>";
        },
        taskMenuEvent : function(layero){
        	var self = this;
        	$(layero).on("click",function(){
        		return false;
        	});
        	$(layero).on("click",".icon-cancel-circled-outline",function(){//关闭操作
                layer.close(layer.index);
            });
        	$(layero).on("click",".icon-left-open-big",function(){//返回操作
                $(layero).find(".floatLayer").replaceWith(self.taskMenuHtml());
            });
        	$(layero).on("click","li#deleteTask",function(){
        		// var taskId = taskLastData.taskId;
        		$(layero).find(".floatLayer").html("<div class='layer-title'>"+
                        "<h5>删除任务</h5>"+
                        "<i class=' icon-left-open-big'></i>"+
                        "<i class='icon-cancel-circled-outline'></i>"+
                    "</div>"+
                    "<ul>"+
                        "<li>您确定要删除这个任务吗?</li>"+
                        "<li><button id='deleteSave' class='btn red btn-common'>删除</button></li>"+
                    "</ul>");

        		return false;
        	});
        	$(layero).on("click","#deleteSave",function(){//删除任务
        		var taskId = taskLastData.taskId;
        		$.ajax({
		     			type:'post',
		     			data: {taskId: taskId},
		     			url:context + '/task/delTask.htm'
        			}).done(function(resultData){
		                if(resultData && resultData.success){
		                	self.taskBlock.trigger("deleteEvent");
		                	layer.closeAll();
		                }else if(resultData && !resultData.success){
		                    layer.msg(resultData.errormsg);
		                }else{
		                    layer.msg("请求失败");
		            };
        		});
        		return false;
        	});
        },
        updateCheckItemProgressbar : function(){
        	var all = this.updateCheckItem.find("ul.check-item").children().length;
			var selectCount = this.updateCheckItem.find("ul.check-item").find("i.icon-ok:visible").length;
			var value;
			if(all == 0){
				value = 0;
			}else{
				value = parseInt((selectCount/all)*100);
			}
			
			$( "#progressbar" ).progressbar({value: value});
			$( "#progressbar" ).find('.progress-label').html(value + "%&nbsp;");
			return false;
        },
		setEvent : function(){
			var self = this;
			panel.on("click",function(event){//任务版其他区域的事件
				self.closeAllLayer();
				EventUtil.stopPropagation(event);
  	        });
			this.taskTitleBar.find("#taskMenu").on("click",function(){//点击更多，弹出任务操作菜单
	            var top = $(this).offset().top+$(this).height()+5 + "px";
	            var left = $(this).offset().left-120 + "px";
                self.stageMenuIndex = $.layer({
                    type: 1,   //0-4的选择,
                    title: false,
                    area: ['auto', 'auto'],
                    offset: [top, left],
                    border: [0],
                    shade: [0, '#FFFFFF'],
                    closeBtn: [0],
                    page: {
                        html: self.taskMenuHtml()
                    },
                    success: function(layero){
                        self.taskMenuEvent(layero);
                    }
                });
				return false;
			});
			this.taskTitleBar.on("click","#moveOtherTaskgroup",function(){//移动到其他任务版
			    if($.inArray('TASK:MOVE_TO_BOARD', taskLastData.permits) == -1){
			    	return;
			    } 
				self.closeAllLayer();
				var position = $(this).offset();
				var positionTop = position.top + $(this).height() +10 + "px";
				var positionLeft = position.left-50 + "px";
				
				self.groupIndex = $.layer({
					type: 1,
					title: false,
                    area: ['auto', 'auto'],
                    offset: [positionTop, positionLeft],
                    shade: [0],
                    border: [0],
                    closeBtn: [0],
					page : {
						html : self.taskGroupListHtml(self.boardList).html()
					},
					success : function(layero){
						$(layero).on("click",function(){
							return false;
						});
						$(layero).on("click",".panel-heading",function(){
							$(this).closest(".panel-default").siblings().find(".panel-collapse").removeClass("in");
							if($(this).next().hasClass("in")){
								$(this).next().removeClass("in");
							}else{
								$(this).next().addClass("in");
							}
						});
						$(layero).on("click","li",function(){//
							var taskboardId = $(this).closest(".panel-collapse").prev('div').data("id");
							var taskLineId = $(this).data("id");//要移动到其他版的id
							var name = $(this).closest(".panel-collapse").prev('div').text();
							var selfLineId = taskLastData.taskLine.taskLineId;
							
							if(taskLineId != selfLineId){
								var taskId = taskLastData.taskId;//当前任务Id
								$.ajax({
				         			type:'post',
				         			data: {oneselfId: taskId, targetId: 0, oneselfLineId: 0, targetLineId: taskLineId},
				         			url:context + '/task/moveTask.htm'
				         		}).done(function(resultData){
				                    if(resultData && resultData.success){
				                    	self.taskBlock.trigger("moveToOtherBoardEvent",[{targetLineId:taskboardId,targetLineName:name,status:taskLastData.status,selfLineId:selfLineId}]);
										layer.closeAll();
				                    }else if(resultData && !resultData.success){
				                        layer.msg(resultData.errormsg);
				                    }else{
				                        layer.msg("请求失败");
				                    };
				                });
							}else{
								layer.close(layer.index);
							}
							
							return false;
						});
					}
				});
				return false;
			});
			this.taskTitleBar.on("click","#moveOtherStage",function(){//移动到其他列
			    if($.inArray('TASK:MOVE_TO_LINE', taskLastData.permits) == -1) return;
				self.closeAllLayer();
				var position = $(this).offset();
				var positionTop = position.top + $(this).height() +10 + "px";
				var positionLeft = position.left-50 + "px";
				
				self.stageIndex = $.layer({
					type: 1,
					title: false,
                    area: ['auto', 'auto'],
                    offset: [positionTop, positionLeft],
                    shade: [0],
                    border: [0],
                    closeBtn: [0],
					page : {
						html : self.stageListHtml(self.listTaskLinesData)
					},
					success : function(layero){
						$(layero).on("click",function(){
							return false;
						});
						$(layero).on("click","li",function(){
							var movelineId = $(this).data("id");//要移动到其他版的id
							var selfLineId = taskLastData.taskLine.taskLineId;
							if(movelineId != selfLineId){
								var taskId = taskLastData.taskId;//当前任务Id
								var name = $(this).text();
								$.ajax({
				         			type:'post',
				         			data: {oneselfId: taskId, targetId: 0, oneselfLineId: 0, targetLineId: movelineId},
				         			url:context + '/task/moveTask.htm'
				         		}).done(function(resultData){
				                    if(resultData && resultData.success){
				                    	self.taskBlock.trigger("moveToOtherLineEvent",[{targetLineId:movelineId,targetLineName:name,status:taskLastData.status,selfLineId:selfLineId}]);
										layer.closeAll();
				                    }else if(resultData && !resultData.success){
				                        layer.msg(resultData.errormsg);
				                    }else{
				                        layer.msg("请求失败");
				                    };
				                });
							}else{
								layer.close(layer.index);
							}
							return false;
						});
					}
				});
				return false;
			});
			this.taskTitleBar.on("click","#waitAudit",function(){//点击提交审核checkbox
			    if($.inArray('TASK:COMMIT', taskLastData.permits) == -1) {
			        layer.msg("没有权限");
			        return;
			    }
                var checkbox = $(this);
                var status = checkbox.find("i").is(":hidden") ? taskWaitAuditStatus : taskNotDoneStatus;
                $.ajax({
                    type:'post',
                    data:{taskId: taskLastData.taskId, status: status},
                    url:context + '/task/updateTaskStatus.htm'
                }).done(function(data){
                    if(data && data.success){
                        if(status === taskWaitAuditStatus){
                            checkbox.find("i").show();
                        }else{
                            checkbox.find("i").hide();
                        }
                        self.taskBlock.trigger("taskStatusEvent",[{"status":status}]);
                        layer.closeAll();
                    }else if(data && !data.success){
                        layer.msg(data.errormsg);
                    }else{
                        layer.msg("请求失败");
                    }
                }).fail(function(){
                    layer.msg("请求失败");
                });
                return false;
            });
            this.taskTitleBar.on("click","#pass",function(){//点击通过checkbox
            	
                var checkbox = $(this);
                if(!checkbox.find("i").is(":hidden")) return;
                if($.inArray('TASK:PASS', taskLastData.permits) == -1){
                    layer.msg("没有权限");
                    return;
                }
                layer.confirm('是否通过审核？确定后，该任务就完成了，不可再恢复状态了。',function(){
                	$.ajax({
                        type:'post',
                        data:{taskId: taskLastData.taskId, status: taskDoneStatus},
                        url:context + '/task/updateTaskStatus.htm'
                    }).done(function(data){
                        if(data && data.success){
                            checkbox.find("i").show();
                            checkbox.siblings("#notPass").find("i").hide();
                            var taskDiv = taskBoard.find('.block[data-id='+taskLastData.taskId+']');
                            self.taskBlock.trigger("taskStatusEvent",[{"status":taskDoneStatus}]);
                            
                            taskDiv.find('i.icon-ok').show();
                            taskDiv.appendTo(taskDiv.closest("li").find(".taskDoneList"));
                            installHtml.updateTaskCount(taskDiv.closest("li"));
                            layer.closeAll();
                        }else if(data && !data.success){
                            layer.msg(data.errormsg);
                        }else{
                            layer.msg("请求失败");
                        }
                    });
                });
            });
            this.taskTitleBar.on("click","#notPass",function(){//点击不通过checkbox
               var checkbox = $(this);
               if(!checkbox.find("i").is(":hidden")) return;
               if($.inArray('TASK:NOT_PASS', taskLastData.permits) == -1){
                   layer.msg("没有权限");
                   return;
               }
            	var addFormHtml =$("<div></div>");
    			addFormHtml.load(context + "/task/loadNoPassReason.htm",function(){
    				$.layer({
    				    type: 1,
    				    shadeClose: false,
    				    title: false,
    				    closeBtn: [0, false],
    				    shade: [0.3, '#000'],
    				    border: [0],
    				    offset: ['200px',''],
    				    area: ['450px', '203px'],
    				    page: {html:addFormHtml.html()},
    				    success:function(layero){
    				    	$(layero).on("click",function(){
    				    		return false;
    				    	});
    				    	$(layero).on("click",".icon-cancel-circled",function(){
    				    		layer.close(layer.index);
    				    	});
    				    	var textArea = $(layero).find("#reason");
    				    	var valiName = new InputValidate(textArea, true, 500, 0, 'verticalline', null); 
    				    	$(layero).on("click","#addReason",function(){
    				    		if(!valiName.checkValidate()){
    								return false;
    							}
    				    		$.ajax({
    			                    type:'post',
    			                    data:{taskId: taskLastData.taskId, status: taskNotPassStatus,noPassReason:textArea.val()},
    			                    url:context + '/task/updateTaskStatus.htm'
    			                }).done(function(data){
    			                    if(data && data.success){
    			                        checkbox.find("i").show();
    			                        checkbox.siblings("#pass").find("i").hide();
    			                        self.taskBlock.trigger("taskStatusEvent",[{"status":taskNotPassStatus}]);
    			                        layer.closeAll();
    			                    }else if(data && !data.success){
    			                        layer.msg(data.errormsg);
    			                    }else{
    			                        layer.msg("请求失败");
    			                    }
    			                }).fail(function(){
    			                    layer.msg("请求失败");
    			                });
    			                return false;
    				    	});
    				    }
    				}); 
    			});
//                
            });
			/*this.updateTaskContent.on("click","span.check-box",function(){//点击Checkbox是否选中
				self.closeAllLayer();
				var select;
				var checkbox = $(this);
				if($(this).find("i").is(":hidden")){
					select = true;
				}else{
					select = false;
				}
				$.ajax({
    				type:'post',
    				data:{taskId: taskLastData.taskId, status: select},
    				url:context + '/task/updateTaskStatus.htm'
    			}).done(function(data){
    				if(data && data.success){
    					if(select){
    						checkbox.find("i").show();
    						self.updateTaskLastData("status",taskDoneStatus);
    					}else{
    						checkbox.find("i").hide();
    						self.updateTaskLastData("status",taskNotDoneStatus);
    					}
    					self.taskBlock.find("span.check-box").trigger("taskCheckBoxSelectEvent",[{taskLineId:taskLastData.taskLine.taskLineId,select:select,taskId: taskLastData.taskId}]);//选中提交数据
    					
                    	layer.closeAll();
                    }else if(data && !data.success){
                        layer.msg(data.errormsg);
                    }else{
                        layer.msg("请求失败");
                    }
    				disableBtn.prop("disabled",false);
    			}).fail(function(){
                	layer.msg("请求失败");
                	disableBtn.prop("disabled",false);
                });
				
				return false;
			});*/
			//var checkTaskContent = new InputValidate(this.updateTaskContent.find("textarea"), true, 600);
			this.updateTaskContent.on("blur","textarea",function(){//修改任务内容
			    if($.inArray('TASK:SET', taskLastData.permits) == -1){
			        layer.msg("没有权限");
			        return;
			    }
				self.closeAllLayer();
				var content = $(this).val();
				var contentValiArray = {};
					contentValiArray = new InputValidate($(this), true, 500, 0, '', null);
				var sendData = {
						taskId : taskLastData.taskId,
						value : content,
						columnName : "content"
				};
				if(!contentValiArray.checkValidate()){
					return;
				}
				$.ajax({
         			type:'post',
         			data: sendData,
         			url:context + '/task/updateTask.htm'
         		}).done(function(resultData){
         			console.log(resultData)
                    if(resultData && resultData.success){
                    	self.taskBlock.trigger("taskContentEvent",[{"content":content}]);
                    	self.updateTaskLastData("content",content);
                    }else if(resultData && !resultData.success){
                        layer.msg(resultData.errormsg);
                    }else{
                        layer.msg("请求失败");
                    };
                });
				return false;
			});
			this.updateRemark.on("click","a",function(){//点击添加备注事件
				self.closeAllLayer();
				self.alterRemarkEvent("add");
				return false;
			});
			this.updateRemark.on("click","p",function(){//点击备注内容修改事件
				self.closeAllLayer();
				if($(this).text() == ""){
					return false;
				}else{
					self.alterRemarkEvent("edit");
				}
				return false;
			});
			this.admin.on('click','a',function(){
			    if($.inArray('TASK:UPDATE_ADMIN', taskLastData.permits) == -1) return;
//				self.closeAllLayer();
				var id = $(this).find("img").data("id").toString();
				var eventType = "changeExecutor";
                personList.init({
                    searchInput:true,//是否需要搜索框
                    data : self.userList,//人员数据
                    claiming : false,//是否先是待认领,要选中未认领时，selectData有一个值为0就可以了
                    selectData : [id],//要选中的数据
                    dom : $(this),
                    container : $(this),
                    isClose : true,
                    eventType:eventType
                });
                $(this).off(eventType).on(eventType, function(event, params){
                	var sendData = {
    						taskId : taskLastData.taskId,
    						admin : params.ids[0],
    				};
    				$.ajax({
             			type:'post',
             			data: sendData,
             			url:context + '/task/updateTaskAdmin.htm'
             		}).done(function(resultData){
                        if(resultData && resultData.success){
                			self.updateTaskLastData("admin",sendData.admin);
                			params.callback();
                			layer.closeAll();
                        }else if(resultData && !resultData.success){
                            layer.msg(resultData.errormsg);
                        }else{
                            layer.msg("请求失败");
                        };
                    });
                });
				return false;
			});
			this.updateExecutor.on("click","span",function(){//修改执行者
			    if($.inArray('TASK:UPDATE_EXECUTOR', taskLastData.permits) == -1){
                    return;
                }
				self.closeAllLayer();
				var id = $(this).find("img").data("id").toString();
				var eventType = "changeExecutor";
				var users = $.extend(true, {}, self.userList);
				for(var name in users){
					if(users[name].prjRoleCode == "PRJ_SUPERVISER"){
						delete users[name];
					}
				}
                personList.init({
                    searchInput:true,//是否需要搜索框
                    data : users,//人员数据
                    claiming : true,//是否先是待认领,要选中未认领时，selectData有一个值为0就可以了
                    selectData : [id],//要选中的数据
                    dom : $(this),
                    container : $(this),
                    isClose : true,
                    eventType:eventType
                });
                $(this).off(eventType).on(eventType, function(event, params){
                	var sendData = {
    						taskId : taskLastData.taskId,
    						executor : params.ids[0]
    				};
    				$.ajax({
             			type:'post',
             			data: sendData,
             			url:context + '/task/updateTaskExecutor.htm'
             		}).done(function(resultData){
                        if(resultData && resultData.success){
                        	self.taskBlock.trigger("taskExecutorEvent",[{"executor":sendData.executor}]);
                			self.updateTaskLastData("executor",sendData.userId);
                			params.callback();
                			layer.closeAll();
                        }else if(resultData && !resultData.success){
                            layer.msg(resultData.errormsg);
                        }else{
                            layer.msg("请求失败");
                        };
                    });
                });
				return false;
			});
			this.updateEndDate.on("focus", "input",function(){//修改截止日期
				var createdTime = self.createdTime.find("label").text();
			    if($.inArray('TASK:SET_DATE', taskLastData.permits) == -1){
                    return;
                }
				self.closeAllLayer();
				var sendData = {};
				sendData.taskId = taskLastData.taskId;
				sendData.columnName = "due_time";
				WdatePicker({
					errDealMode:1,
					dateFmt  : 'yyyy年MM月dd日',
					qsEnabled : false,
					isShowOK : false,
					minDate :createdTime,
					onpicking : function(dp){
						var realDate = dp.cal.getNewDateStr();
						self.updateEndDate.find("input").val(realDate);
						$dp.hide();
						sendData.value = realDate.replace(/[年月]/g,'-').replace('日','');
						$.ajax({
		         			type:'post',
		         			data: sendData,
		         			url:context + '/task/updateTask.htm'
		         		}).done(function(resultData){
		                    if(resultData && resultData.success){
		                    	self.updateTaskLastData("dueTime",new Date(sendData.value));
		                    }else if(resultData && !resultData.success){
		                        layer.msg(resultData.errormsg);
		                    }else{
		                        layer.msg("请求失败");
		                    };
		                });
						return true;
					},
					onclearing:function(){
						self.updateEndDate.find("input").val("");
						$dp.hide();
						sendData.value = null;
						$.ajax({
		         			type:'post',
		         			data: sendData,
		         			url:context + '/task/updateTask.htm'
		         		}).done(function(resultData){
		                    if(resultData && resultData.success){
		                    	self.updateTaskLastData("dueTime","");
		                    }else if(resultData && !resultData.success){
		                        layer.msg(resultData.errormsg);
		                    }else{
		                        layer.msg("请求失败");
		                    };
		                });
						return true;
					}
				});
				return false;
			});
			this.updateLevel.on("click","#changeLevel",function(){//任务级别设置
			    if($.inArray('TASK:PRIORITY', taskLastData.permits) == -1){
                    return;
                }
				self.closeAllLayer();
				if($(this).text() == "一般"){
					self.updateLevel.find("ul").find("li:eq(0) .icon-ok").show();
					self.updateLevel.find("ul").find("li:eq(1) .icon-ok").hide();
				}else{
					self.updateLevel.find("ul").find("li:eq(1) .icon-ok").show();
					self.updateLevel.find("ul").find("li:eq(0) .icon-ok").hide();
				}
				self.updateLevel.find("ul").show();
				return false;
			});
			this.visibilityUser.on("click","#changeVisibility",function(){//可见性设置
				if($.inArray('TASK:VISIBLE', taskLastData.permits) == -1){
                    return;
                }
				self.closeAllLayer();
				if($(this).text() == "仅参与者可见"){
					self.visibilityUser.find("ul").find("li:eq(0) .icon-ok").show();
					self.visibilityUser.find("ul").find("li:eq(1) .icon-ok").hide();
				}else{
					self.visibilityUser.find("ul").find("li:eq(1) .icon-ok").show();
					self.visibilityUser.find("ul").find("li:eq(0) .icon-ok").hide();
				}
				self.visibilityUser.find("ul").show();
				return false;
			});
			this.visibilityUser.find("ul").on("click","li",function(){//可见性设置
				var li = $(this);
				var sendData = {
						taskId : taskLastData.taskId,
						columnName : "visible_status"
				};
				if($(this).index() != 0){
					sendData.value = visibleStatusAllUser;
				}else{
					sendData.value = visibleStatusTaskMember;
				}
				$.ajax({
         			type:'post',
         			data: sendData,
         			url:context + '/task/updateTask.htm'
         		}).done(function(resultData){
                    if(resultData && resultData.success){
                    	self.updateTaskLastData("visibleStatus",sendData.value);
                    	self.visibilityUser.find("#changeVisibility").html(li.find("a").html());
        				self.visibilityUser.find("ul").hide();
                    }else if(resultData && !resultData.success){
                        layer.msg(resultData.errormsg);
                    }else{
                        layer.msg("请求失败");
                    };
                });
				return false;
			});
			this.updateLevel.find("ul").on("click","li",function(){//任务级别设置
				var li = $(this);
				var sendData = {
						taskId : taskLastData.taskId,
						columnName : "priority"
				};
				if($(this).index() == 0){
					sendData.value = taskPriorityGeneral;
				}else{
					sendData.value = taskPriorityUrgent;
				}
				$.ajax({
         			type:'post',
         			data: sendData,
         			url:context + '/task/updateTask.htm'
         		}).done(function(resultData){
                    if(resultData && resultData.success){
                    	self.taskBlock.trigger("taskPriorityEvent",[{"priority":sendData.value}]);
                    	
                    	self.updateTaskLastData("priority",sendData.value);
                    	self.updateLevel.find("#changeLevel").html(li.find("a").html());
        				self.updateLevel.find("ul").hide();
                    }else if(resultData && !resultData.success){
                        layer.msg(resultData.errormsg);
                    }else{
                        layer.msg("请求失败");
                    };
                });
				return false;
			});
			this.updateCheckItem.on("click","li span.check-box",function(){//选中或者取消检查项
			    if($.inArray('TASK:SET', taskLastData.permits) == -1){
                    layer.msg("没有权限");
                    return;
                }
				self.closeAllLayer();
				var checkItemId = $(this).closest("li").data("id");
				var select;
				if($(this).find("i").is(":hidden")){
					select = true;
					$(this).find("i").show();
				}else{
					select = false;
					$(this).find("i").hide();
				}
				$.ajax({
         			type:'post',
         			data: {taskCheckListId: checkItemId, value: select, columnName: "status"},
         			url:context + '/task/updateTaskCheckList.htm'
         		}).done(function(resultData){
                    if(resultData && resultData.success){
                    	self.updateCheckItemProgressbar();
                    }else if(resultData && !resultData.success){
                        layer.msg(resultData.errormsg);
                    }else{
                        layer.msg("请求失败");
                    };
                });
				return false;
			});
			this.updateCheckItem.on("click","ul > li", function(){//点击检查项
				self.closeAllLayer();
				var eidtCheckItem = $(self.alterAndDeleteAreaHtml()).insertAfter($(this));
				
				var itemDom = $(this);
				var taskCheckListId = itemDom.data("id");//当前要修改的检查项的Id
				
				eidtCheckItem.find("textArea").val(itemDom.text());
				itemDom.hide();
				var checkCheckItem = new InputValidate(eidtCheckItem.find("textarea"), true, 600);
				eidtCheckItem.on("click",".btn-sure",function(){//修改检查项
				    if($.inArray('TASK:SET', taskLastData.permits) == -1){
                        layer.msg("没有权限");
                        return;
                    }
					var value = self.updateCheckItem.find("textarea").val();
					if(!checkCheckItem.checkValidate()){
	                	return false;
					}
					$.ajax({
	         			type:'post',
	         			data: {taskCheckListId: taskCheckListId, value: value, columnName: "list_content"},
	         			url:context + '/task/updateTaskCheckList.htm'
	         		}).done(function(resultData){
	                    if(resultData && resultData.success){
	                    	itemDom.find("span:eq(1)").text(value);
	    					itemDom.show();
	    					eidtCheckItem.remove();
	                    }else if(resultData && !resultData.success){
	                        layer.msg(resultData.errormsg);
	                    }else{
	                        layer.msg("请求失败");
	                    };
	                });
					return false;
				});
				eidtCheckItem.on("click",".btn-cancel",function(){//删除检查项
				    if($.inArray('TASK:SET', taskLastData.permits) == -1){
                        layer.msg("没有权限");
                        return;
                    }
					$.ajax({
	         			type:'post',
	         			data: {taskCheckListId: taskCheckListId},
	         			url:context + '/task/delTaskCheckList.htm'
	         		}).done(function(resultData){
	                    if(resultData && resultData.success){
	                    	itemDom.remove();
	    					eidtCheckItem.remove();
	    					self.updateCheckItemProgressbar();
	                    }else if(resultData && !resultData.success){
	                        layer.msg(resultData.errormsg);
	                    }else{
	                        layer.msg("请求失败");
	                    };
	                });
					return false;
				});
				eidtCheckItem.on("click",function(){
					return false;
				});
				return false;
			});
			this.updateCheckItem.on("click", "a",function(){//添加检查项
				self.closeAllLayer();
				var checkItem = $(self.alterAreaHtml()).appendTo(self.updateCheckItem);
				checkItem.find("textarea").focus();//输入框获得焦点
				panel.find(".panel-content").animate({scrollTop: 230}, 500);
				checkItem.on("click",".btn-sure",function(){//确认按钮押下
					var checkCheckItem = new InputValidate(checkItem.find("textarea"), true, 600);
				    if($.inArray('TASK:SET', taskLastData.permits) == -1){
                        layer.msg("没有权限");
                        return;
                    }
					var textArea = checkItem.find("textarea");
					if(!checkCheckItem.checkValidate()){
	                	return false;
					}
					$.ajax({
						type: "post",
						data: {taskId: taskLastData.taskId, content: textArea.val()},
						url: context + "/task/addCheckList.htm"
					}).done(function(resultData){
	                    if(resultData && resultData.success){
	    					self.updateCheckItem.find(".check-item").append("<li data-id='"+resultData.resultData+"'><span class='check-box'><i class='icon-ok' style='display:none'></i></span><span>"+textArea.val()+"</span></li>");
	    					self.updateCheckItemProgressbar();
	    					textArea.val("");
	                    }else if(resultData && !resultData.success){
	                        layer.msg(resultData.errormsg);
	                    }else{
	                        layer.msg("请求失败");
	                    };
	                });
					return false;
				});
				checkItem.on("click",".btn-cancel",function(){//取消按钮押下
					alterRemark.remove();
				});
				checkItem.on("click",function(){
					return false;
				});
				return false;
			});
			this.updatePartner.on("click","#selectPartner", function(){//选择参与者
				self.closeAllLayer();
				var eventType = "partnerChange";
				var arr =[];
            	$(this).prevAll("li").each(function(){
            		arr.push($(this).data("id").toString());
            	});
            	personList.init({
                    searchInput:true,//是否需要搜索框
                    data : self.userList,//人员数据
                    everyone : true,//是否先是待认领,要选中未认领时，selectData有一个值为0就可以了
                    selectData : arr,//要选中的数据
                    dom : $(this),
                    container : $(this),
                    eventType:eventType
                });
            	$(this).off(eventType).on(eventType, function(event, params){
                	var sendData = {
    						taskId : taskLastData.taskId,
    						partner : params.ids,
    						isSelected : params.isSelect//选中没选中
    				};
                	$.ajax({
             			type:'post',
             			data: sendData,
             			url:context + '/task/updateTaskPartner.htm'
             		}).done(function(resultData){
                        if(resultData && resultData.success){
                        	params.callback();
                        }else if(resultData && !resultData.success){
                            layer.msg(resultData.errormsg);
                        }else{
                            layer.msg("请求失败");
                        };
                    });
                });
            	
            	return false;
			});
			this.updatePartner.on("click", "li .remove-member-handler",function(){//点击X删除参与者
				var li = $(this).closest("li");
				var sendData = {
						taskId : taskLastData.taskId,
						partner : [li.data("id")],
						status : false//选中没选中
				};
				$.ajax({
         			type:'post',
         			data: sendData,
         			url:context + '/task/updateTaskPartner.htm'
         		}).done(function(resultData){
                    if(resultData && resultData.success){
                    	li.remove();
                    }else if(resultData && !resultData.success){
                        layer.msg(resultData.errormsg);
                    }else{
                        layer.msg("请求失败");
                    };
                });
			});
			this.updateChildTask.on("click","#addChildTask", function(){//添加子任务
				self.closeAllLayer();
				addTaskForm.init(self.userList,$(this).closest("div").find("#addChildTaskForm"));
				panel.find(".panel-content").animate({scrollTop: 364}, 500);
				return false;
			});
			this.updateChildTask.on("click","ul.check-item li",function(){
				installHtml.openTaskInfo($(this));
            	return false;
			});
			this.tabContent.find("#switchTab").on("click","li",function(){
				self.closeAllLayer();
				if($(this).hasClass("active")){
					return false;
				}
				$(this).addClass("active").siblings().removeClass("active");
				var index = $(this).index();
				if(index == 0){
					// self.discussContent.hide();
					self.tabContent.find(".activity-detail").show();
					 self.tabContent.find(".nopassReason").hide();
					self.tabContent.find(".tab-accessory").hide();
					panel.find(".panel-content").css("bottom","0px");
				}else if(index == 1){
					// self.discussContent.hide();
					self.tabContent.find(".activity-detail").hide();
					// self.tabContent.find(".task-message").hide();
					self.tabContent.find(".tab-accessory").show();
					self.tabContent.find(".nopassReason").hide();
					panel.find(".panel-content").css("bottom","0px");
				}else if(index == 2){
					self.tabContent.find(".activity-detail").hide();
					self.tabContent.find(".tab-accessory").hide();
					self.tabContent.find(".nopassReason").show();
				}
			});
			this.tabContent.on("click","#uploadFileBtn",function(){
				self.tabContent.find("#uploadFileInput").trigger("click");
				return false;
			});
			this.uploadFile();
			// this.discussEvent();
			return false;
		},
		taskGroupListHtml : function(data){
			
			var str = $('<div><div class="layer-lg boardBackground floatLayer " style="padding:0px 0px"><div class="panel-group fold-group" id="accordion"></div></div></div>');
			if(data && data.length > 0){
				var taskLineId = this.taskTitleBar.find("#moveOtherStage").data("id");//任务列id
				var id = this.taskTitleBar.find("#moveOtherTaskgroup").data("id");//任务板id
				$.each(data,function(key,value){
					var taskBoardDiv = $('<div class="panel panel-default"></div>');
					var href = "collapse"+key;
					if(id == value.taskBoardId){
						taskBoardDiv.append('<div data-id="'+value.taskBoardId+'" class="panel-heading"><h4 class="panel-title"><a data-toggle="collapse" data-parent="#accordion" href="#'+href+'">'+
								value.name +'</a></h4></div><div id="'+href+'" class="panel-collapse collapse in"><div class="panel-body"><ul></ul></div></div>');
					}else{
						taskBoardDiv.append('<div data-id="'+value.taskBoardId+'"class="panel-heading"><h4 class="panel-title"><a data-toggle="collapse" data-parent="#accordion" href="#'+href+'">'+
								value.name+'</a></h4></div><div id="'+href+'" class="panel-collapse collapse"><div class="panel-body"><ul></ul></div></div>');
					}
					
					if(value.taskLines){
						$.each(value.taskLines,function(index,data){
							if(data.taskLineId == taskLineId){
								taskBoardDiv.find('ul').append('<li data-id="'+data.taskLineId+'"><a>'+data.name+'</a><i class=" icon-ok"></i></li>');
							}else{
								taskBoardDiv.find('ul').append('<li data-id="'+data.taskLineId+'"><a>'+data.name+'</a></li>');
							}
						});
					}else{
						taskBoardDiv.find("#"+href).remove();
					}
					str.find("#accordion").append(taskBoardDiv);
				});
			}
			return str;
		},
		stageListHtml : function(data){
			var str = "<div class='layer-sm-noscroll boardBackground floatLayer little-portrait'>";
			str+="<ul class='vertical-scroll-min scroll-area'>";
			if(data){
				var id = this.taskTitleBar.find("#moveOtherStage").data("id");
				$.each(data,function(key,value){
					if(id == value.taskLineId){
						str+="<li data-id='"+value.taskLineId+"'>"+value.name+"<i class='icon-ok''></i></li>";
					}else{
						str+="<li data-id='"+value.taskLineId+"'>"+value.name+"<i class='icon-ok' style='display:none'></i></li>";
					}
				});
			}
			str+="</ul></div>";
			return str;
		},
		regainRemark : function(remarkValue){//重新设置备注结构
			if(remarkValue == ""){
				this.updateRemark.find("span").hide();
				this.updateRemark.find("a").show();
			}else{
				this.updateRemark.find("a").hide();
				this.updateRemark.find("span,p").show();
			}
		},
		alterRemarkEvent : function(type){//备注的相关事件
			var self = this;
			var alterRemark = $(self.alterAreaHtml()).appendTo(this.updateRemark);
			this.updateRemark.find("a,p").hide();
			this.updateRemark.find("span").show();
			if(type == "add"){
			}else{
				alterRemark.find("textarea").val(this.updateRemark.find("p").text());
			}
			var checkRemark = new InputValidate(alterRemark.find("textarea"), false, 600);
			alterRemark.on("click",".btn-sure",function(){//确认按钮押下
			    if($.inArray('TASK:SET', taskLastData.permits) == -1){
			        layer.msg("没有权限");
			        return;
			    }
				var remarkValue = self.updateRemark.find("textarea").val();
				if(!checkRemark.checkValidate()){
                	return false;
				}
				var sendData = {
						taskId : taskLastData.taskId,
						value : remarkValue,
						columnName : "remark"
				};
				$.ajax({
         			type:'post',
         			data: sendData,
         			url:context + '/task/updateTask.htm'
         		}).done(function(resultData){
                    if(resultData && resultData.success){
                    	self.updateRemark.find("p").text(remarkValue);
                    	self.regainRemark(remarkValue);
                    	self.updateTaskLastData("remark",remarkValue);
        				alterRemark.remove();
                    }else if(resultData && !resultData.success){
                        layer.msg(resultData.errormsg);
                    }else{
                        layer.msg("请求失败");
                    };
                });
				return false;
			});
			alterRemark.on("click",".btn-cancel",function(){//取消按钮押下
				if(self.updateRemark.find("p").text().length){
					self.updateRemark.find("span").show();
					self.updateRemark.find("a").hide();
				}else{
					self.updateRemark.find("a").show();
					self.updateRemark.find("span").hide();
				}
				self.updateRemark.find("p").show();
				alterRemark.remove();
			});
			alterRemark.on("click",function(){
				return false;
			});
		},
		alterAreaHtml :function(){
	        return "<div id='alterContentArea'><textarea class='form-control'></textarea><div style='padding-top:3px; height:40px'>"+
                   "<button class='btn btn-cancel'>取消 </button><button class='btn btn-sure'> 确定 </button></div></div>";
		},
		alterAndDeleteAreaHtml :function(){
			return "<div id='alterContentArea'><textarea class='form-control'></textarea><div style='padding-top:3px; height:40px'>"+
			"<button class='btn btn-cancel'>删除 </button><button class='btn btn-sure'> 确定 </button></div></div>";
		},
		activityHtml : function(data){//动态的Html代码
			var self = this;
			var user = self.getUserInfo(data.userId);
			
			var str = "";
				str +="<li>";
				str +="<img data-id='"+data.userId+"' src='"+user.logo+"' class='img-circle'><a>"+user.name+" </a> <time class='pull-right'>"+new Date(data.createdTime).format("YYYY-MM-dd hh:mm:ss")+"</time>";
				str +="<ul class='accessory-area'>";
				str +="<li>";
				str +="<div>"+data.record+"</div>";
				str +="</li>";
				str +="</ul>";
				str +="</li>";
			return str;
		},
		discussHtml : function(data){//讨论回复的Html代码
			var self = this;
			var user = self.getUserInfo(data.createdBy);
			
			var str = "";
				str +="<li>";
				str +="<img data-id='"+data.createdBy+"' src='"+user.logo+"' class='img-circle'><a>"+user.name+" </a> <time class='pull-right'>"+new Date(data.createdTime).format("YYYY-MM-dd hh:mm:ss")+"</time>";
				str +="<ul class='accessory-area'>";
				str +="<li>";
				str +="<div>"+data.content+"</div>";
				str +="</li>";
				str +="</ul>";
				str +="</li>";
			return str;
		},
		/*discussEvent : function(){
			var self = this;
			var checkDiscuss = new InputValidate(this.discussContent.find("textarea"), false, 600);
			this.discussContent.on("click","button.btn-sure",function(){//讨论回复
				var content = self.discussContent.find("textarea");
				// taskLastData.taskId; //任务ID
				if(!checkDiscuss.checkValidate() || content.val().length == 0){
                	return false;
				}
				$.ajax({
         			type:'post',
         			data: {taskId: taskLastData.taskId, content: content.val()},
         			url:context + '/task/addTaskDiscuss.htm'
         		}).done(function(resultData){
                    if(resultData && resultData.success){
                    	if(content.val() == ""){
        					return false;
        				}
        				panel.find("#taskMessage").append(self.discussHtml({createdBy:loginUserInfo.userId,content:content.val(), createdTime: new Date()}));
        				content.val("");
                    }else if(resultData && !resultData.success){
                        layer.msg(resultData.errormsg);
                    }else{
                        layer.msg("请求失败");
                    };
                });
			});
		},*/
		fileHtml : function(data){
			var self = this;
			var user = self.getUserInfo(data.createdBy);
			var str = "";
				str +="<li>";
				str +="<img data-id='"+data.createdBy+"' src='"+user.logo+"' class='img-circle'><a>"+user.name+"</a> <time class='pull-right'>"+new Date().format("YYYY-MM-dd hh:mm:ss")+"</time>";
				str +="<ul class='accessory-area'>";
				str +="<li>";
				str +="<span name='taskFile' data-fileUrl='"+data.fileUrl+"'><a><i class=' icon-doc-text-1'></i>"+data.fileName+"</a> <i class='icon-cancel-circled' title='删除'></i></span>";
				str +="</li>";
				str +="</ul>";
				str +="</li>";
			return str;
		},
		uploadFile : function(){
			var self = this;
			panel.find("#taskFileContend").on('click', "li span[name='taskFile']", function(){//文件下载
				var $form = panel.find('#downLoadFileForm');
				$form.find('input[name="fileUrl"]').val( $(this).data('fileurl'));
				$form.find('input[name="remove_cache"]').val((new Date()).getTime());
				$form.submit().before($form.clone(true).val("")).remove();
			});
			panel.find("#taskFileContend").on('click', "i.icon-cancel-circled", function(){//附件删除
				
				var deleteBtn = $(this);
				$.ajax({
					type:'post',
					url:context + '/task/deleteTaskAdjunct.htm',
					data:{fileUrl:deleteBtn.closest('span').data('fileurl'),projectId:self.projectId}
				}).done(function(resultData){
					if(resultData && resultData.success){
						if(deleteBtn.closest("li").children().length > 1){
							deleteBtn.closest('span').remove();
						}else{
							deleteBtn.closest("ul").closest("li").remove();
						}
					}else if(resultData && !resultData.success && resultData.errormsg){
						layer.alert(resultData.errormsg, 1);
					}else{
						layer.alert('请求失败', 1);
					};
				}).fail(function(){
					layer.alert('请求失败', 1);
				});
				return false;
			});
			
			panel.on("change","#uploadFileInput" , function(){
				var files = this.files;
				var thisInput = $(this);
				var options = {
						beforeSubmit:function(){
			    			if (!files || files.length > 0) {
			    				if(files[0].limitSize(20, 'MB')){
			    					dialogAlert.alert('附件太多，无法上传，请限制20MB以内');
			    					
			    					//把当前的fileinput置为初始
			    					thisInput.val('');
			    					return false;
			    				}
			    				return true;
			    			}; 
			    			return false;
			    		},
			    		success:function(resultData){
			    			if(resultData && resultData.success){
			    				$(self.fileHtml(resultData.resultData)).appendTo(panel.find("#taskFileContend"));
							}else if(resultData && !resultData.success && resultData.errormsg){
								layer.alert(resultData.errormsg, 1);
							}else{
								layer.alert('请求失败', 1);
							};
			    		},
			    		error:function(result){
			    			layer.alert('请求失败', 1);
			    		}
				};
				panel.find("#uploadFileForm").ajaxSubmit(options);
				return false;
			});
		}
	};
	//2位，前位补0
	function appendzero (num) {
		return num>=10?num:("0"+num);
     }
	taskModel.init();
	
	return taskModel;
});