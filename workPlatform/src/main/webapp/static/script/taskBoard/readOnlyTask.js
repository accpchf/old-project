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
	var panel = $("#updateReadOnlyTaskRightLayer");
	var taskLastData = panel.data("taskInfo");
	var taskDoneStatus = $("#completeCode").val();//任务已经完成的状态
	var taskNotDoneStatus = $("#doingCode").val();//任务未完成的状态
	var taskWaitAuditStatus = $("#toCheckCode").val();//任务待审核的状态
	var taskNotPassStatus = $("#noPassCode").val();//任务不通过的状态
	var taskPriorityGeneral = $("#level0Code").val();//任务优先级 一般
	var taskPriorityUrgent = $("#level1Code").val();//任务优先级 紧急
	var checkItemDone = $("#itemComplete").val();//检查项已完成
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
			}
			else if(data.status === taskDoneStatus){
                this.taskTitleBar.find("#pass i").show();
                this.taskTitleBar.find("#taskMenu").hide();
            }
            else if(data.status === taskNotPassStatus){
                this.taskTitleBar.find("#notPass i").show();
            }
			//任务内容
			this.updateTaskContent.find("p").text(data.content);
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
			//可见性
			if(data.visibleStatus == visibleStatusAllUser){
				this.visibilityUser.find("#changeVisibility").html("所有成员可见");
			}else{
				this.visibilityUser.find("#changeVisibility").html("仅参与者可见");
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
					str+="<li data-id='"+id+"' title='"+user2.name+"'><img src='"+user2.logo+"' class='img-circle'></li>";
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
            						checkStr +="<li data-id='"+obj.taskCheckListId+"'><span class='checkItemContent'>"+obj.listContent+"</span></li>";
            					}else{
            						checkStr +="<li data-id='"+obj.taskCheckListId+"'><span class='checkItemContent'>"+obj.listContent+"</span></li>";
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
		groupIndex:"",//移动到其他任务组弹出层index
		stageIndex:"",//移动到其他阶段弹出层index
		stageMenuIndex :"",//任务菜单按钮弹出层index
		closeAllLayer : function(){//关闭相应的弹出层
			$("#selectUserLayer").closest(".xubox_layer").remove();//选择人员层
			panel.find("#alterContentArea").remove();//添加框
//			this.regainRemark(panel.find("#updateRemark").find("p").text());//备注
//			this.updateChildTask.find("#addChildTaskForm").empty();//子任务
			layer.close(this.groupIndex);//组切换层关闭
			layer.close(this.stageIndex);//列切换层关闭
			layer.close(this.stageMenuIndex);//任务菜单层关闭
//			panel.find("#updateCheckItem").find("li").show();//检查项
			this.updateLevel.find("ul").hide();//任务级别
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
                            self.taskBlock.trigger("taskStatusEvent",[{"status":taskDoneStatus}]);
                            var taskDiv = taskBoard.find('.block[data-id='+taskLastData.taskId+']');
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
			this.uploadFile();
			return false;
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
		fileHtml : function(data){
			var self = this;
			var user = self.getUserInfo(data.createdBy);
			var str = "";
				str +="<li>";
				str +="<img data-id='"+data.createdBy+"' src='"+user.logo+"' class='img-circle'><a>"+user.name+"</a> <time class='pull-right'>"+new Date().format("YYYY-MM-dd hh:mm:ss")+"</time>";
				str +="<ul class='accessory-area'>";
				str +="<li>";
				str +="<span name='taskFile' data-fileUrl='"+data.fileUrl+"'><a><i class=' icon-doc-text-1'></i>"+data.fileName+"</a> </span>";
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
			
		}
	};
	//2位，前位补0
	function appendzero (num) {
		return num>=10?num:("0"+num);
     }
	taskModel.init();
	
	return taskModel;
});