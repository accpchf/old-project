/**
 * 添加，推送的html组合模块
 */
define(["jquery"],function(){
	//操作区
	var htmlMoudle={
			stageHtml : function(stageData, currentBoard){//列的模板代码
				var self = this, html;
				var doingCount = (stageData && stageData.tasks)?stageData.tasks.length:0;
				var overCount = (stageData && stageData.doneTasks)?stageData.doneTasks.length:0;
				var allTaskCout = doingCount+overCount;
				var allTaskCoutStr = allTaskCout == 0 ? "":("· "+allTaskCout);
				html = "<li class='vertical-scroll' data-id='" + stageData.taskLineId +"'>"+
                            "<div class='top'>"+
                                "<h4 class='pull-left'><span class='stageName' title='"+stageData.name+"'>"+self.getStrActualLen(stageData.name,20)+"</span><span class='taskCount'>"+allTaskCoutStr+"</span></h4>";
                if(!window.readOnly && $.inArray('TASK_LINE:EDIT', currentBoard.permits) >= 0){
                    html += "<a href='#' class='pull-right stageMenu'><i class='icon-down-open-big'></i></a>";
                }
                var addButtonHtml =window.readOnly?"": "<div class='add-button corner-6 addTaskButton'>"+
                "<a><i class=' icon-plus-circled'></i>新建任务</a>"+
                "</div>";
                html += "</div>"+
                            "<div class='taskAccordion'>"+
                                "<h3>未完成的任务<span class='taskCount'>· "+doingCount+"</span></h3>"+
                                "<div class='isNoOverWork'>"+
                                    "<div class='taskList'>"+
                                        this.getTaskListData(stageData.tasks)+
                                    "</div>"+addButtonHtml+
                                    
                                    "<div class='addTaskForm'></div>"+
                                "</div>"+
                                "<h3>已完成的任务<span class='taskCount'>· "+overCount+"</span></h3>"+
                                "<div class='taskDoneList'>"+
                                this.getDoneTaskListData(stageData.doneTasks)+
                                "</div>"+
                            "</div>"+
                        "</li>";
	            return html;
	        },
	        getTaskListData : function(taskListData){//列的未完成的数据
	            var taskHtml = "";
	            if(!taskListData){
	            	return taskHtml;
	            }
	            for(var i = 0 ; i < taskListData.length ;i++){
	                var taskData = taskListData[i];
	                taskHtml += this.taskHtml("0",taskData);
	            }
	            return taskHtml;
	        },
	        updateTaskCount:function(dom){//更新任务个数
	        	if($(dom).length > 0){
	        		var doing = $(dom).find(".taskList").find(".block").length;
		        	var overCount = $(dom).find(".taskDoneList").find(".block").length;
		        	var all = doing+overCount;
		        	if(all == 0){
		        		$(dom).find(".top").find("span.taskCount").text("");
		        	}else{
		        		$(dom).find(".top").find("span.taskCount").text("· "+all);
		        	}
		        	
		        	$(dom).find(".taskAccordion").find("h3:eq(0)").find("span").text("· "+doing);
		        	$(dom).find(".taskAccordion").find("h3:eq(1)").find("span").text("· "+overCount);
	        	}
	        },
	        refreshAllTaskCount : function(){//刷新所有列的任务数据
	        	var self = this;
	        	var panel = $("div.main-content[data-id='projectTemplate']");
	        	if(panel.length > 0){
	        		$(".main-task",panel).find("li.vertical-scroll").each(function(){
		        		self.updateTaskCount($(this));
		        	});
	        	}
	        },
	        getDoneTaskListData : function(taskDoneListData){//列的完成的数据
	            var taskHtml = "";
	            if(!taskDoneListData){
	            	return taskHtml;
	            }
	            for(var i = 0 ; i < taskDoneListData.length ;i++){
	                var taskData = taskDoneListData[i];
	                taskHtml += this.taskHtml("1",taskData);
	            }
	            return taskHtml;
	        },
	        taskHtml : function (type,taskData){//任务模板的代码
	        	var relUser = {logo:"",name:""};
	        	var userId = taskData.executor;
	        	var user = window.projectUserList[userId];
	        	if(userId>0){
	        		if(user){
	        			relUser.logo = user.logo;
	        			relUser.name = user.name;
					}
	        	}else{
	        		relUser.logo = context+"/static/images/pic-shelters.png";
	        		relUser.name = "待认领";
	        	}
	        	var html = taskData.priority!=$("#level0Code").val()?"<div title="+$("#level1Text").val()+" class='block "+ this.decideTaskPriority(taskData.priority)+"'":"<div class='block "+ this.decideTaskPriority(taskData.priority)+"'";
	        	//var html = "<div class='block "+ this.decideTaskPriority(taskData.priority)+"'";
	        	if($.inArray('TASK:SORT', taskData.permits) >= 0 || type == "add"){
                    html += " data-sort='sort'";
                }
                if($.inArray('TASK:MOVE_TO_LINE', taskData.permits) >= 0 || type == "add"){
                    html += " data-line='line'";
                }
	        	html += " data-id='" + taskData.taskId +"'>"+this.decideTaskStatus(taskData.status)+
                //"<span class='check-box pull-left'><i class='icon-ok' "+(type=="1"?"":"style='display:none'")+"></i></span>"+
                "<a href='#' class='pull-left person-space'  style='margin-left: 20px;'><img src='"+relUser.logo+"' class='img-circle' title='"+relUser.name+"'/></a>"+
                "<div class='word' style='margin-left: 55px;'>"+taskData.content+"</div>";
                if($.inArray('TASK_CHAT:VIEW', taskData.permits) >= 0 || type == "add"){
                    html += "<i class='icon-chat' title='聊天'></i>";
                }
                html += "</div>";
	            return html;
	        },
	        myTaskHtml : function (data){//我的任务模板的代码
	        	return "<li data-id='" + data.taskId +"' class='"+this.decideTaskPriority(data.priority)+"'>" +this.decideTaskStatus(data.status)+
        		// "<span class='check-box'><i class='icon-ok'"+(data.status=="00000"?"style='display:none'":"")+"></i></span>" +
        		"<p style='margin-left:14px'>"+data.content+"</p></li>";
	            
	        },
	        childTaskHtml : function(userList,data){//任务中的子任务
	        	var logo = userList[data.executor] ? userList[data.executor].logo:"";
	        	var name = userList[data.executor] ? userList[data.executor].name:"";
	        	var taskUserLogo = data.executor? logo:(context+"/static/images/pic-shelters.png");
	        	return 	 "<li data-id='"+data.taskId+"'><img data-id='"+data.executor+"' title='"+name+"' src='"+taskUserLogo+"' class='img-circle'><span>"+data.content+"</span></li>";
	        },
	        decideTaskPriority : function(priority){//判断任务难度，00100：一般，00101：紧急
	            if(priority == $("#level0Code").val()){
	                return "block";
	            }else{
	                return "block block-green";
	            }
	        },
	        decideTaskStatus : function(status){//判断任务状态，00100：一般，00101：紧急
	            if(status == $("#toCheckCode").val()){
	                return "<div class='taskPriority border-yellow' title="+$("#toCheckText").val()+"></div>";
	            }else if(status == $("#noPassCode").val()){
	                return "<div class='taskPriority border-red' title="+$("#noPassText").val()+"></div>";
	            }else if(status == $("#completeCode").val()){
	                return "<div class='taskPriority border-green' title="+$("#completeText").val()+"></div>";
	            }else {
	                return "<div class='taskPriority'></div>";
	            }
	        },
	        openTaskInfo : function(dom){//打开任务详情
	        	layer.closeAll();
            	var taskId = $(dom).data("id");
            	if(!taskId){
            		return false;
            	}
            	var disableBtn = $(dom);
                disableBtn.prop("disabled",true);
                //var value = disableBtn.closest(".main-task").prev(".project-chart").find("i.icon-ok:visible").closest("li").data("value");
            	$.ajax({
    				type: 'post',
    				data: {taskId: taskId},
    				url: context + '/task/findTaskData.htm'
    			}).done(function(data){
    				if(data && data.success){
    					var taskInfo = data.resultData;
                    	if(taskInfo){
                    		disableBtn.trigger("taskStatusEvent",[{"status":taskInfo.status}]);
                    		var pageName = context + "/task/loadTaskInfo.htm",type = true;
                    		if(taskInfo.status == $("#completeCode").val() | taskInfo.status == $("#toCheckCode").val()){
                    			pageName = context + "/task/loadReadOnlyTaskInfo.htm";
                    			type = false;
                    		}
                    		if(window.readOnly){
                    			pageName = context + "/task/loadReadOnlyTaskInfo.htm";
                    			type = false;
                    		}
                    		var taskHtml = $("<div></div>");
                            taskHtml.load(pageName, function(){
                                $.layer({
            	                    type: 1,
            	                    title:false,
            	                    area: ['auto', 'auto'],
            	                    shade: [0],
            	                    zIndex : 6000,
            	                    border: [0],
            	                    closeBtn: [0],
            	                    page: {
            	                    	 html: taskHtml.html()
            					    },
                                    success: function(layero){
                                    	if(window.readOnly){
                                    		$(layero).find("#pass,.pass").hide();
                                    		$(layero).find("#waitAudit,.waitAudit").hide();
                                    		$(layero).find("#notPass,.nopass").hide();
                                    		 $(layero).find("#taskMenu").hide();
                                		}
                                        if($.inArray('TASK:DELETE', data.resultData.permits) == -1){
                                            $(layero).find("#taskMenu").hide();
                                        }
                                        if($.inArray('TASK:COMMIT', data.resultData.permits) == -1){
                                        	$(layero).find("#waitAudit,.waitAudit").hide();
                                        }
                                        if($.inArray('TASK_BOARD:ADMIN_ACCESS', data.resultData.permits) == -1 &&
                                        		$.inArray('PRJ:ADMIN_ACCES', data.resultData.permits) == -1 && 
                                        		$.inArray('TASK:PASS', data.resultData.permits) == -1){
                                        	$(layero).find("#pass,.pass").hide();
                                        }
                                        if($.inArray('TASK_BOARD:ADMIN_ACCESS', data.resultData.permits) == -1 &&
                                        		$.inArray('PRJ:ADMIN_ACCES', data.resultData.permits) == -1 && 
                                        		$.inArray('TASK:NOT_PASS', data.resultData.permits) == -1){
                                        	$(layero).find("#notPass,.nopass").hide();
                                        }
                                        if($.inArray('TASK:SET', data.resultData.permits) == -1){
                                            $(layero).find("#addChildTask").hide();
                                            $(layero).find("#uploadFileBtn").hide();
                                            $(layero).find("#discussContent").hide();
                                            $(layero).find("#updateTaskRightLayer").find(".panel-content").css("bottom","0px");
                                        }
                                        if($.inArray('TASK:UPDATE_MEMBER', data.resultData.permits) == -1){
                                            $(layero).find("#selectPartner").hide();
                                        }
                                        if(data.resultData.status == $("#completeCode").val()){
                                        	$(layero).find("#waitAudit,.waitAudit").hide();	
                                        	$(layero).find("#pass,.pass").hide();
                                        	$(layero).find("#notPass,.nopass").hide();
                                        }
                                        if(data.resultData.status == $("#doingCode").val() || data.resultData.status == $("#noPassCode").val()){
                                        	$(layero).find("#pass,.pass").hide();
                                        	$(layero).find("#notPass,.nopass").hide();
                                        }
                                        if(data.resultData.status == $("#toCheckCode").val()){
                                        	$(layero).find("#waitAudit,.waitAudit").hide();	
                                        }
                                        if(type){
                                        	$(layero).find("#updateTaskRightLayer").data("taskInfo",data.resultData);
                                        }else{
                                        	$(layero).find("#updateReadOnlyTaskRightLayer").data("taskInfo",data.resultData);
                                        }
            					    	
            					    	// $(layero).find("#updateTaskRightLayer").data("type",value);
                                    }
                              });
                            });
                    		
                    	}
                    }else if(data && !data.success){
                        layer.msg(data.errormsg);
                    }else{
                        layer.msg("请求失败");
                    }
                    disableBtn.prop("disabled",false);
    			});
	        },
	        getStrActualLen : function(str,len){ //字母汉字长度截取
				
				var resultStr="";
				if(str==null ||str.length==0){
					return "";
				}
				if(str.replace(/[^\x00-\xff]/g,"xx").length > len){
					for(var i=0;resultStr.replace(/[^\x00-\xff]/g, "xx").length<len;i++){
						resultStr = str.substring(0,i);
					}	
					resultStr = resultStr + "...";
				}else{
					resultStr = str;
				}
				return resultStr;
			}
	};
	 return htmlMoudle;
});