require(["jquery", context+"/static/script/taskBoard/installHtml.js?_T="+(new Date()).valueOf()],function($,installHtml){

	var panel = $("div[data-id='personalTemplate']");
	var menuDom = $("#personalMenu",panel);
	var containerDom = $("#personalTaskContainer",panel);
	var taskPriorityUrgent = "00101";//任务优先级 紧急
	 var taskDoneStatus = $("#completeCode").val();//任务已经完成的状态
	var taskNotDoneStatus = $("#doingCode").val();//任务未完成的状态
	var taskWaitAuditStatus = $("#toCheckCode").val();//任务待审核的状态
	var taskNotPassStatus = $("#noPassCode").val();//任务不通过的状态
	
	var personalMenu = {
		init : function(){
			this.todayTask = $("#todayTaskList", panel);
			this.futureTask = $("#futureTaskList", panel);
			this.setEvent();
			menuDom.find("#myAllTask").trigger('click');
		},
		getMyAllTask : function(type){
			var self = this;
			$.ajax({
				type : "post",
				url : context+"/personal/findAllProjectTask.htm",
				data : {roleType : type}
			}).done(function(result){
				if(result && result.success && result.resultData){
					if(result.resultData.todayProjects.length > 0){
						self.todayTask.empty().append(self.projectList(result.resultData.todayProjects));
					}else{
						self.todayTask.empty().append('<div class="none-content"><i class="icon-tasks"></i><br /><a>目前还没有任务</a></div>');
					}
					if(result.resultData.futureProjects.length > 0){
						self.futureTask.empty().append(self.projectList(result.resultData.futureProjects));
					}else{
						self.futureTask.empty().append('<div class="none-content"><i class="icon-tasks"></i><br /><a>目前还没有任务</a></div>');
					}
					
				}else{
					layer.msg(result.errormsg);
				}
				menuDom.prop("disabled",false);
			}).fail(function(){
				layer.msg("请求失败");
				menuDom.prop("disabled",false);
			});
		},
		projectList : function(data){
			var self = this;
			var str = "";
			$.each(data, function(key,value){
				str += self.createModel(value);
			});
			return str;
		},
		createModel : function(data){
			var str = "";
			str += "<div class='task-project' data-id='"+data.projectId+"'>";
			str += "<h4><i class='icon-tasks-1'></i>"+data.name +"</h4>";
			str += "<ul>";
			$.each(data.tasks, function(key, value){
				str += installHtml.myTaskHtml(value);
			});
			
			str += "</ul>";
			str += "</div>";
			return str;
		},
		setEvent : function(){
			var self = this;
			menuDom.on("click","#myAllTask",function(){
				if($(this).hasClass('active')){
					return;
				}
				
				menuDom.prop("disabled",true);
				$(this).addClass('active').siblings().removeClass('active');
				self.getMyAllTask(4);
			});
			menuDom.on("click","#myAssignlTask",function(){
				if($(this).hasClass('active')){
					return;
				}
				
				menuDom.prop("disabled",true);
				$(this).addClass('active').siblings().removeClass('active');
				self.getMyAllTask(3);
			});
			menuDom.on("click","#myExecuteTask",function(){
				if($(this).hasClass('active')){
					return;
				}
				menuDom.prop("disabled",true);
				$(this).addClass('active').siblings().removeClass('active');
				self.getMyAllTask(1);
			});
			menuDom.on("click","#myParticipateTask",function(){
				if($(this).hasClass('active')){
					return;
				}
				menuDom.prop("disabled",true);
				$(this).addClass('active').siblings().removeClass('active');
				self.getMyAllTask(2);
			});
			containerDom.on("click","li",function(){
				installHtml.openTaskInfo($(this));
				self.updateTaskEvent($(this));
			});
			//checkbox选中取消
			/*containerDom.on("click","span.check-box",function(event){

            	var select;
                var dom=$(this).find("i");
                var taskId = $(this).closest("li").data("id");
                var disableBtn = $(this);
                disableBtn.prop("disabled",true);

                if(dom.is(":hidden")){
                    select = true;
                    dom.show();
                }else{
                    select = false;
                    dom.hide();
                }
                
                $.ajax({
    				type:'post',
    				data:{taskId: taskId, status: select},
    				url:context + '/task/updateTaskStatus.htm'
    			}).done(function(data){
    				if(data && data.success){
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
		},
		refreshTask : function(dom){
			var porjectDom = dom.closest("div.task-project");
			if(porjectDom.find("li").length <= 1){
				if(porjectDom.closest('div.taskList').children('div.task-project').length <= 1){
					porjectDom.closest('div.taskList').html('<div class="none-content"><i class="icon-tasks"></i><br /><a>目前还没有任务</a></div>');
				}else{
					porjectDom.remove();
				}
			}else{
				dom.remove();
			}
		},
		updateTaskEvent : function(dom){
        	var self = this;
        	dom.off("deleteEvent").on("deleteEvent",function(){//任务删除，任务版的事件
        		self.refreshTask(dom);
        	});
        	dom.off("taskCheckBoxSelectEvent").on("taskCheckBoxSelectEvent",function(event, params){//任务checkBox选中事件
        		var task = containerDom.find("li[data-id='"+params.taskId+"']");
        		if(params.select){
        			task.find("i").show();
        		}else{
        			task.find("i").hide();
        		}
        	});
        	dom.off("taskContentEvent").on("taskContentEvent",function(event, params){//任务内容选中事件
        		dom.find("p").text(params.content);
        	});
        	dom.off("taskStatusEvent").on("taskStatusEvent",function(event, params){//任务状态
        		if(params.status == taskWaitAuditStatus){
        			dom.find(".taskPriority").removeClass("border-red").addClass("border-yellow").attr("title",$("#toCheckText").val());
				}else if(params.status == taskNotDoneStatus){
					dom.find(".taskPriority").removeClass("border-yellow border-red").removeAttr("title");
				}else if(params.status == taskNotPassStatus){
					dom.find(".taskPriority").removeClass("border-yellow").addClass("border-red").attr("title",$("#noPassText").val());
				}else if(params.status == taskDoneStatus){
					dom.find(".taskPriority").removeClass("border-yellow border-red").addClass("border-green").attr("title",$("#completeText").val());
				}
        	});
        	
        	dom.off("taskPriorityEvent").on("taskPriorityEvent",function(event, params){//任务优先级
        		if(params.priority == taskPriorityUrgent){
        			dom.addClass("block-green").attr("title",$("#level1Text").val());
    			}else{
    				dom.removeClass("block-green").removeAttr("title");
    			}
        	});
        }
		
	};
	personalMenu.init();
});