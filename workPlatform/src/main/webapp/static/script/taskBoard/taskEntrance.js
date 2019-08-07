/*
 * 项目对应的任务版界面的入口
 */
define(["jquery",
        "script/common/personList",
        "script/taskBoard/addTaskForm",
        context+"/static/script/taskBoard/installHtml.js?_T="+(new Date()).valueOf(),
        "script/taskBoard/chart",
        "script/chat/startChat",
        "jqueryUI",
        "layer"],function($,personList,addTaskForm,installHtml,chart,startChat){

    //操作区
	var panel = $("div.main-content[data-id='projectTemplate']");
    
	var taskPriorityUrgent = $("#level1Code").val();//任务优先级 紧急
    var stageSortSuccess = false;//判断列排序是否成功
    
    var taskSortSuccess = false;//判断任务排序是否成功
    var taskSortData = {};//任务排序的任务
    
    var taskDoneStatus = $("#completeCode").val();//任务已经完成的状态
	var taskNotDoneStatus = $("#doingCode").val();//任务未完成的状态
	var taskWaitAuditStatus = $("#toCheckCode").val();//任务待审核的状态
	var taskNotPassStatus = $("#noPassCode").val();//任务不通过的状态
    /**
     * 页面存储的数据
     * 0。当前项目ID panel。data("projectId")
     * 1.项目的所有用户 window.projectUserList
     * 2.当前任务版额所有列和任务的数据 projectTaskContainer.data("listTaskLinesData");
     * 3.项目的所有任务版 switchButton.data("taskBoardList"); 当前任务版的id switchButton.data("id")
     * 4.任务的详细信息 $("#updateTaskRightLayer").data("taskInfo");
     */
    //任务模块
    var taskBoardModel = {
        init : function(){
        	//图表区
            this.projectChartContainer = $(".project-chart",panel);
            //任务区
            this.projectTaskContainer = $(".main-task",panel);
            this.switchButton = panel.find("#switchTaskBoard");
        	this.getListTaskBoard();
        	this.windowEvent();
        	switchTaskBoard.init();
        	chart.init();
        },
        windowEvent : function(){
        	var self =this;
            $(document).on("click",function(){//关闭新建任务form
            	self.projectTaskContainer.find(".addTaskForm").empty();
            	self.projectTaskContainer.find(".addTaskButton").show();
            });
            $(window).resize(function() { //浏览器变化时工作区变化
            	self.resizeStageHeight();
            });
          //图表展开收起操作
            self.projectChartContainer.on("click","#upOrDown",function(){
                if($(this).hasClass("up_arrow")){
                	self.projectChartContainer.find(".chart-wrap").hide();
                    $(this).removeClass("up_arrow").addClass("down_arrow");
                    self.projectTaskContainer.css("top",105);
                    $(this).next("span").text("展开图表");
                }else{
                	self.projectChartContainer.find(".chart-wrap").show();
                    $(this).removeClass("down_arrow").addClass("up_arrow");
                    self.projectTaskContainer.css("top",300);
                    $(this).next("span").text("收起图表");
                }
                self.resizeStageHeight();
            });
        },
        getListTaskBoard : function(){//获得项目的所有任务版
        	var self = this;
        	var projectId = panel.data("projectId");
        	$.ajax({
				type:'post',
				data:{projectId: projectId},
				url:context + '/task/listTaskBoard.htm'
			}).done(function(data){
				if(data && data.success){
                	if(data.resultData && data.resultData.length){
                		self.switchButton.data("taskBoardList",data.resultData);
                		var taskBoardData = data.resultData[0];
            			self.getTaskLineData(taskBoardData.name,taskBoardData.remark,taskBoardData.taskBoardId);
                	}
                }else if(data && !data.success){
                    layer.msg(data.errormsg);
                }else{
                    layer.msg("请求失败");
                }
			});	
        },
        getTaskLineData : function(name,desc,boardId){//获得任务列数据
        	var self = this;
			$.ajax({
				type:'post',
				data:{boardId: boardId, roleType: 0},
				url:context + '/task/listTaskLines.htm'
			}).done(function(data){
				if(data && data.success){
                	if(data.resultData){
                		
                	    var boardList = self.switchButton.data("taskBoardList"), currentBoard = null;
                        $.each(boardList, function(index, temp) {
                            if(temp.taskBoardId == boardId){
                                currentBoard = temp;
                            }
                        });
                        self.switchButton.data("currentBoard",currentBoard);
                		self.getInitData(data.resultData, currentBoard);
                		self.projectTaskContainer.data("listTaskLinesData",data.resultData);
                		self.switchButton.find("span.name").text(installHtml.getStrActualLen(name,24)).attr("title",name);
                		// self.switchButton.find("span.desc").text(installHtml.getStrActualLen(desc,24)).attr("title",desc);
                		self.switchButton.data("id",boardId);
                	}
                }else if(data && !data.success){
                    layer.msg(data.errormsg);
                }else{
                    layer.msg("请求失败");
                }
			});
        },
        getInitData : function(resultData, currentBoard){//获得任务版的相关数据
            this.createTaskBoardModel(resultData, currentBoard);
        },
        createTaskBoardModel : function(data, currentBoard){
            var boarHtml = "";
            for(var i = 0 ; i< data.length ; i++){
                var stageData = data[i];
                boarHtml += installHtml.stageHtml(stageData, currentBoard);
                this.projectChartContainer.find(".check-select").find("i.icon-ok").hide();
                this.projectChartContainer.find(".check-select").find("li#all").find("i.icon-ok").show();
            }
            this.projectTaskContainer.find("ul").empty().append(boarHtml);
            this.setEvent();
            this.resizeStageHeight();
        },
        stageMenuHtml : function(){//列主菜单内容
            return "<div class='layer-lg boardBackground floatLayer'><div class='layer-title'><h5>任务列菜单</h5><i class='icon-cancel-circled-outline'></i></div>" +
                   "<ul><li id='updateStage'><a><i class='icon-pencil'></i>编辑</a></li>" +
                   "<li id='addStage'><a> <i class='icon-user-outline'></i>在此后添加任务列</a></li>" +
                   "<li id='deleteStage'><a><i class='icon-trash'></i>删除</a></li></ul></div>";
        },
        setStageEvent : function(layero,stageId){//列菜单操作事件
            var self = this;
            var stageDom = self.projectTaskContainer.find("li[data-id='"+stageId+"']");
            var checkNameInput = null;
            $(layero).on("click",function(event){//阻止关闭当前层
              return false;
            });
            $(layero).on("click","#updateStage",function(){//修改页面
                $(layero).find(".floatLayer").html("<div class='layer-title'>"+
                                    "<h5>编辑</h5>"+
                                    "<i class=' icon-left-open-big'></i>"+
                                    "<i class='icon-cancel-circled-outline'></i>"+
                                "</div>"+
                                "<ul>"+
                                    "<li><input class='form-control' type='text'  placeholder='请输入任务列名称' value='"+stageDom.find(".top").find("span.stageName").attr("title")+"'></li>"+
                                    "<li><button id='updateSave' class='btn green btn-common'>保存</button></li>"+
                                "</ul>");
                checkNameInput = new InputValidate($(layero).find("input"), true, 40);
            });
            $(layero).on("click","#addStage",function(){//添加页面
                $(layero).find(".floatLayer").html("<div class='layer-title'>"+
                                    "<h5>在此后添加任务列</h5>"+
                                    "<i class=' icon-left-open-big'></i>"+
                                    "<i class='icon-cancel-circled-outline'></i>"+
                                "</div>"+
                                "<ul>"+
                                    "<li><p>新任务列将被添加在当前任务列之后</p></li>"+
                                    "<li><input class=' form-control' type='text' placeholder='请输入任务列名称'></li>"+
                                    "<li><button id='addSave' class='btn green btn-common'>添加</button></li>"+
                                "</ul>");
                checkNameInput = new InputValidate($(layero).find("input"), true, 40);
            });
            $(layero).on("click","#deleteStage",function(){//删除页面
                $(layero).find(".floatLayer").html("<div class='layer-title'>"+
                                                        "<h5>删除</h5>"+
                                                        "<i class=' icon-left-open-big'></i>"+
                                                        "<i class='icon-cancel-circled-outline'></i>"+
                                                    "</div>"+
                                                    "<ul>"+
                                                        "<li>您确定要删除这个任务列吗?</li>"+
                                                        "<li><button id='deleteSave' class='btn red btn-common'>删除</button></li>"+
                                                    "</ul>");
            });
            $(layero).on("click",".icon-left-open-big",function(){//返回操作
                $(layero).find(".floatLayer").replaceWith(self.stageMenuHtml());
            });
            $(layero).on("click",".icon-cancel-circled-outline",function(){//关闭操作
            	layer.closeAll();
            });
            $(layero).on("click","#updateSave",function(){//修改操作
                var stageName = $(this).closest("ul").find(".form-control").val();
                if(!checkNameInput.checkValidate()){
                	return false;
                }
                var disableBtn = $(this);
                disableBtn.prop("disabled",true);
                $.ajax({
        			type:'post',
        			data: {taskLineId: stageId, name: stageName},
        			url:context + '/task/updateLineName.htm'
        		}).done(function(resultData){
                    if(resultData && resultData.success){
                        stageDom.find(".top").find("span.stageName").text(installHtml.getStrActualLen(stageName,20)).attr("title",stageName);
                        self.updateLineData("update", {taskLineId: stageId, name: stageName});
                        layer.closeAll();
                    }else if(resultData && !resultData.success){
                        layer.msg(resultData.errormsg);
                    }else{
                        layer.msg("请求失败");
                    };
                    disableBtn.prop("disabled",false);
                }).fail(function(){
                	layer.msg("请求失败");
                	disableBtn.prop("disabled",false);
                });
            });
            $(layero).on("click","#addSave",function(){//添加操作
                var stageName = $(this).closest("ul").find(".form-control").val();
                
                if(!checkNameInput.checkValidate()){
                	return false;
                }
                var disableBtn = $(this);
                disableBtn.prop("disabled",true);
                $.ajax({
        			type:'post',
        			data: {targetLineId: stageId, name: stageName},
        			url:context + '/task/addLine.htm'
        		}).done(function(resultData){
                    if(resultData && resultData.success){
                        var board = self.switchButton.data("currentBoard");
                        var newStage = installHtml.stageHtml({"taskLineId":resultData.resultData,"name":stageName,"tasks": [],"doneTasks": []}, board);
                        self.updateLineData("add", {"targetLineId": stageId,"taskLineId":resultData.resultData,"name":stageName});
                        $(newStage).insertAfter(self.projectTaskContainer.find("ul li[data-id='"+stageId+"']"));
                        self.taskAccordion();
                        self.taskSortable();
                        self.resizeStageHeight();
                        layer.closeAll();
                    }else if(resultData && !resultData.success){
                        layer.msg(resultData.errormsg);
                    }else{
                        layer.msg("请求失败");
                    };
                    disableBtn.prop("disabled",false);
                }).fail(function(){
                	layer.msg("请求失败");
                	disableBtn.prop("disabled",false);
                });
            });
            $(layero).on("click","#deleteSave",function(){//删除操作
            	var disableBtn = $(this);
                disableBtn.prop("disabled",true);
            	 $.ajax({
         			type:'post',
         			data: {taskLineId: stageId},
         			url:context + '/task/delLine.htm'
         		}).done(function(resultData){
                    if(resultData && resultData.success){
                        stageDom.remove();
                        self.updateLineData("delete", {taskLineId: stageId});
                        layer.close(layer.index);
                    }else if(resultData && !resultData.success){
                        layer.msg(resultData.errormsg);
                    }else{
                        layer.msg("请求失败");
                    };
                    disableBtn.prop("disabled",false);
                }).fail(function(){
                	layer.msg("请求失败");
                	disableBtn.prop("disabled",false);
                });
            });
        },
        updateLineData : function(type,data){
        	var self = this;
        	var lineList = this.projectTaskContainer.data("listTaskLinesData");
        	var taksboardId = self.switchButton.data("id");
        	var boardList = this.switchButton.data("taskBoardList");
        	if(boardList){
        		var count = boardList.length ;
        		for(var i=0 ;i<count; i++){
            		if(boardList[i].taskBoardId == taksboardId){
            			if(type == "add"){
            				$.each(boardList[i].taskLines,function(k,v){
                    			if(data.targetLineId == v.taskLineId){
                    				boardList[i].taskLines.splice(k+1, 0, data);
                    				return false;
                    			}
                    		});
            			}else if(type == "update"){
            				$.each(boardList[i].taskLines,function(key,value){
            					if(data.taskLineId == value.taskLineId){
            						boardList[i].taskLines[key].name = data.name;
                    				return false;
                    			}
            				});
            			}else if(type == "delete"){
            				$.each(boardList[i].taskLines,function(k,v){
                    			if(data.taskLineId == v.taskLineId){
                    				boardList[i].taskLines.splice(k, 1);
                    				return false;
                    			}
                    		});
        				}
            			this.switchButton.data("taskBoardList",boardList);
            			break;
            		}
            	}
        	}
        	
        	if( type == "add"){//{"targetLineId": stageId,"taskLineId":resultData.resultData,"name":stageName}
        		$.each(lineList,function(k,v){
        			if(data.targetLineId == v.taskLineId){
        				lineList.splice(k+1, 0, data);
        				return false;
        			}
        		});
        	}else if( type == "update" ){
        		$.each(lineList,function(k,v){
        			if(data.taskLineId == v.taskLineId){
        				lineList[k].name = data.name;
        				return false;
        			}
        		});
        	}else if( type == "delete" ){
        		$.each(lineList,function(k,v){
        			if(data.taskLineId == v.taskLineId){
        				lineList.splice(k, 1);
        				return false;
        			}
        		});
        	}else if( type == "sort"){//{oneselfId: oneselfId, targetId: targetId == undefined ? 0 : targetId}
        		
        		var oneselfData = "",oneselfIndex = "",targetIndex = "";
    			$.each(lineList,function(k,v){
        			if(data.oneselfId == v.taskLineId){
        				oneselfData = v;
        				oneselfIndex = k;
        			}
        			if(data.targetId == v.taskLineId){
        				targetIndex = k;
        			}
        		});
    			if(oneselfData != ""){
    				if(data.targetId > 0){
            			lineList.splice(oneselfIndex, 1);
            			if(oneselfIndex > targetIndex){
            				lineList.splice(targetIndex+1, 0, oneselfData);
            			}else{
            				lineList.splice(targetIndex, 0, oneselfData);
            			}
            			
            		}else{
            			lineList.splice(oneselfIndex, 1);
            			lineList.splice(0, 0, oneselfData);
            		}
    			}
        	}
        	this.projectTaskContainer.data("listTaskLinesData",lineList);
        },
        layerPosition : function(dom){

            var createLayerPosition = $(dom).offset();
            var createLayerTop = createLayerPosition.top+$(dom).height()+10 + "px";
            var createLayerLeft = createLayerPosition.left-120 + "px";

            return { top: createLayerTop,
                    left : createLayerLeft
            };
        },
        resizeStageHeight : function(){
        	var height = this.projectTaskContainer.height()-145;
        	this.projectTaskContainer.find(".isNoOverWork").height(height);
        	this.projectTaskContainer.find(".taskDoneList").height(height);
        },
        taskSortable : function(){
            //未分配任务的分类
        	var self = this;
        	taskSortSuccess = false;
        	self.projectTaskContainer.find(".taskList").sortable({
                connectWith: ".taskList",
                cursor: 'move',
                items:".block",
                dropOnEmpty:true,
                placeholder: "portlet-placeholder",
                cancel: ".addTaskButton,.top,.check-box",
                update : function(){
                	taskSortSuccess= true;
                },
                start : function(event, ui){
                	taskSortData.oneselfLineId = ui.item.closest("li").data("id");
                },
                stop : function(event, ui){
                	var thisTaskModule = $(this);
                	if(window.readOnly){
                		thisTaskModule.sortable("cancel");
                		return;
                	}
                    if(taskSortSuccess){
                    	var oneselfId = ui.item.data("id"), sort = ui.item.data("sort"), line = ui.item.data("line"), targetId = ui.item.prev(".block").data("id"), board = self.switchButton.data("currentBoard");
                    	taskSortData.targetLineId = ui.item.closest("li").data("id");
                    	taskSortData.oneselfId = oneselfId;
                    	taskSortData.targetId = targetId == undefined ? 0 : targetId;
                    	if(taskSortData.oneselfLineId == taskSortData.targetLineId){
                            if(!sort){
                                layer.msg('没有权限');
                                thisTaskModule.sortable("cancel");
                                return;
                            }
                        } else {
                            if(!line){
                                layer.msg('没有权限');
                                thisTaskModule.sortable("cancel");
                                return;
                            }
                        }
                        $.ajax({
            				type:'post',
            				data:taskSortData,
            				url:context + '/task/moveTask.htm'
            			}).done(function(data){
            				if(data && data.success){
            					installHtml.updateTaskCount(ui.item.closest("li"));
            					if(taskSortData.oneselfLineId != taskSortData.targetLineId){
            						installHtml.updateTaskCount(self.projectTaskContainer.find("li[data-id='"+taskSortData.oneselfLineId+"']"));
            					}
                            }else if(data && !data.success){
                            	thisTaskModule.sortable("cancel");//出错取消排序
                                layer.msg(data.errormsg);
                            }else{
                            	thisTaskModule.sortable("cancel");//出错取消排序
                                layer.msg("请求失败");
                            }
            				taskSortData = {};
            			}).fail(function(){
            				thisTaskModule.sortable("cancel");//出错取消排序
            				layer.msg("请求失败");
            				taskSortData = {};
            			});
                    }
                    taskSortSuccess= false;
                 }
            });
        },
        taskAccordion : function(){
            //任务收缩分类
        	this.projectTaskContainer.find( ".taskAccordion" ).accordion({
              collapsible: true,
              autoHeight: true,
              heightStyle: "content"
            });
        },
        filterTask : function(roleType){
        	var self = this;
        	var board = this.switchButton.data("currentBoard");
        	$.ajax({
				type : "post",
				url : context + '/task/listTaskLines.htm',
				data:{boardId: board.taskBoardId, roleType: roleType}
			}).done(function(data){
				if(data && data.resultData){
					var len = data.resultData.length;
					var str = "";
					for(var i = 0 ; i< len ; i++){
		                var stageData = data.resultData[i];
		                str += installHtml.stageHtml(stageData, board);
		            }
					self.projectTaskContainer.find("ul").empty().append(str);
					self.setEvent();
                    self.resizeStageHeight();
                }else if(data && !data.success){
                    layer.msg(data.errormsg);
                }else{
                    layer.msg("请求失败");
                }
				self.projectChartContainer.find(".check-select").find("li").prop("disabled",false);
			}).fail(function(){
				layer.msg("请求失败");
				self.projectChartContainer.find(".check-select").find("li").prop("disabled",false);
			});
        },
        setEvent : function(){
        	var self = this;
        	//任务筛选
        	self.projectChartContainer.find(".check-select").off("click","li");//解除绑定事件
        	self.projectChartContainer.find(".check-select").on("click","li",function(){
        		var value = $(this).data("value");
        		if($(this).find("i.icon-ok").is(":visible")){
        			return false;
        		}else{
        			self.projectChartContainer.find(".check-select").find("li").prop("disabled",true);
        			$(this).find("i.icon-ok").show().end().siblings().find("i.icon-ok").hide();
        		}
        		self.filterTask(value);
        	});
            //列菜单操作
        	self.projectTaskContainer.off("click","a.stageMenu");
            self.projectTaskContainer.on("click","a.stageMenu",function(event){
                layer.closeAll();
                var layerPosition = self.layerPosition($(this));
                var stageId = $(this).closest("li").data("id");
                self.createLayerIndex = $.layer({
                    type: 1,   //0-4的选择,
                    title: false,
                    area: ['auto', 'auto'],
                    offset: [layerPosition.top, layerPosition.left],
                    border: [0],
                    shade: [0, '#FFFFFF'],
                    closeBtn: [0],
                    page: {
                        html: self.stageMenuHtml()
                    },
                    success: function(layero){
                        self.setStageEvent(layero,stageId);
                    }
                });
                return false;
            });
            self.taskAccordion();
            self.taskSortable();
          //未分配任务的分类
            self.projectTaskContainer.find("ul.horizontal-scroll").sortable({
            	placeholder: "portlet-placeholder",
            	item:".top",
            	cancel: ".addTaskForm,.addTaskButton,.taskList,.taskDoneList",
                update : function(){
                    stageSortSuccess= true;
                },
                stop : function(event, ui){
                	var thisLineModule = $(this), board = self.switchButton.data("currentBoard");
                    if(stageSortSuccess){
                        if($.inArray('TASK_LINE:MOVE', board.permits) == -1){
                            layer.msg("没有权限");
                            thisLineModule.sortable("cancel");// 取消排序
                            return;
                        }
                        if(window.readOnly){
                            thisLineModule.sortable("cancel");// 取消排序
                            return;
                        }
                    	var oneselfId = ui.item.data("id"), targetId = ui.item.prev("li").data("id");
                    	var sendData = {oneselfId: oneselfId, targetId: targetId == undefined ? 0 : targetId};
                        $.ajax({
            				type:'post',
            				data: sendData,
            				url:context + '/task/moveTaskLine.htm'
            			}).done(function(data){
            				if(data && data.success){
            					self.updateLineData("sort", sendData);
                        }else if(data && !data.success){ 
                            	thisLineModule.sortable("cancel");//出错取消排序
                                layer.msg(data.errormsg);
                            }else{
                            	thisLineModule.sortable("cancel");//出错取消排序
                                layer.msg("请求失败");
                            }
            			}).fail(function(){
            				thisLineModule.sortable("cancel");//出错取消排序
            				layer.msg("请求失败");
            			});
                    }
                    stageSortSuccess= false;
                 }
            });
            //checkbox选中取消
            self.projectTaskContainer.find("div.taskList,.taskDoneList").off("click","span.check-box");
            self.projectTaskContainer.find("div.taskList,.taskDoneList").on("click","span.check-box",function(event){
            	if($(this).prev(".taskPriority ").hasClass("border-yellow")){
            		return false;
            	}
            	var select;
                var dom=$(this).find("i");
                var taskId = $(this).closest(".block").data("id");
                var li = $(this).closest("li");
                var disableBtn = $(this);
                disableBtn.prop("disabled",true);

                if(dom.is(":hidden")){
                    select = true;
                }else{
                    select = false;
                }
                var status = $(this).find("i").is(":hidden") ? taskWaitAuditStatus : taskNotDoneStatus;
                $.ajax({
    				type:'post',
    				data:{taskId: taskId, status: status},
    				url:context + '/task/updateTaskStatus.htm'
    			}).done(function(data){
    				if(data && data.success){
    					if(select){
//    						dom.show();
    						disableBtn.prev().addClass("border-yellow");
//    						disableBtn.closest(".block").appendTo(disableBtn.closest("li").find(".taskDoneList"));
    					}else{
    						dom.hide();
    						disableBtn.closest(".block").appendTo(disableBtn.closest("li").find(".taskList"));
    					}
                    	installHtml.updateTaskCount(li);
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
            });
            //新建任务按钮
            self.projectTaskContainer.off("click",".addTaskButton");
            self.projectTaskContainer.on("click",".addTaskButton",function(event){
            	var form = $(this).closest("li").find(".addTaskForm");
                if(form.children().length > 0){
                    return;
                }
                self.projectTaskContainer.find(".addTaskForm").empty();
            	self.projectTaskContainer.find(".addTaskButton").show();
                addTaskForm.init(window.projectUserList,form);
                $(this).closest("li").find(".addTaskButton").hide();
                return false;
            });
            self.projectTaskContainer.off("click",".taskList .block,.taskDoneList .block");
            self.projectTaskContainer.on("click",".taskList .block,.taskDoneList .block",function(event){//点击未完成任务弹出任务详情
            	installHtml.openTaskInfo($(this));
            	self.updateTaskEvent($(this).closest("div.block"));
                return false;
            });
            self.projectTaskContainer.off("click",".block i.icon-chat");
            self.projectTaskContainer.on("click",".block i.icon-chat",function(){
            	var el = $(this).closest(".block");
            	var id = el.data("id");
            	var name = el.text();
            	startChat.init(id,"00301","1","",name);
            	return false;
            });
        },
        updateTaskEvent : function(dom){
        	var self = this;
        	dom.off("deleteEvent").on("deleteEvent",function(){//任务删除，任务版的事件
        		var parent = dom.parent();
        		dom.remove();
        		installHtml.updateTaskCount(parent.closest("li"));
        	});
        	dom.off("moveToOtherBoardEvent").on("moveToOtherBoardEvent",function(event,params){//移动到其他任务版
        		dom.remove();
        		self.getTaskLineData(params.targetLineName, "", params.targetLineId);
//        		self.getListTaskBoard();
        	});
        	dom.off("moveToOtherLineEvent").on("moveToOtherLineEvent",function(event, params){//移动到其他列
        		var li = self.projectTaskContainer.find("ul > li[data-id='"+params.targetLineId+"']");
        		if(params.status == taskDoneStatus){
        			li.find(".taskDoneList").append(dom);
				}else{
					li.find(".taskList").append(dom);
				}
        		installHtml.updateTaskCount(li);
        		installHtml.updateTaskCount(self.projectTaskContainer.find("ul > li[data-id='"+params.selfLineId+"']"));
        	});
        	dom.off("taskCheckBoxSelectEvent").on("taskCheckBoxSelectEvent",function(event, params){//任务checkBox选中事件
        		var li = self.projectTaskContainer.find("ul > li[data-id='"+params.taskLineId+"']");
        		var task = li.find("div.block[data-id='"+params.taskId+"']");
        		if(params.select){
        			task.appendTo(li.find(".taskDoneList"));
        			task.find("i").show();
        		}else{
        			task.find("i").hide();
        			task.appendTo(li.find(".taskList"));
        		}
        		installHtml.updateTaskCount(li);
        	});
        	dom.off("taskContentEvent").on("taskContentEvent",function(event, params){//任务内容选中事件
        		dom.find("div.word").text(params.content);
        	});
        	dom.off("taskExecutorEvent").on("taskExecutorEvent",function(event, params){//任务执行者事件
        		var user = self.getUserInfo(params.executor);
        		dom.find("img").attr("src",user.logo).attr("title",user.name);
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
        	dom.off("taskPriorityEvent").on("taskPriorityEvent",function(event, params){//任务等级
        		if(params.priority == taskPriorityUrgent){
        			dom.addClass("block-green").attr("title",$("#level1Text").val());
				}else{
					dom.removeClass("block-green").removeAttr("title");
				}
        	});
        },
        getUserInfo : function(id){//通过Id获得用户的logo和name
        	var retUser = {logo:"",name:""};
        	var user = window.projectUserList[id];
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
        }
    };
    /**
     * 任务版切换模块
     */
    var switchTaskBoard = {
    		init : function(){
    			this.switchButton = panel.find("#switchTaskBoard");
    			this.setEvent();
    		},
            taskBoardGroupHtml : function(){//新建任务版界面
            	var data = this.switchButton.data("taskBoardList");
            	var str ="";
            	var addTaskBoardBtn = window.readOnly?"":"<a id='addTaskBoardBtn'><i class='icon-plus-circle'></i>添加</a>";
            	str+="<div class='layer-left  boardBackground clear-radius'>"+
			                "<div class='layer-top task-infor '>"+
			                "<h5>任务板</h5>"+
			                addTaskBoardBtn+
			            "</div>"+
			            "<ul id='addTaskBoardGroup' style='display:none'><li>"+
			            "<input class='form-control' type='text' placeholder='任务板名称'>"+
			            "<textarea class='form-control'  placeholder='任务板描述'></textarea>"+
			        	"<button class='btn green btn-common'>新建</button>"+
			            "</li></ul>"+
			           " <ul id='taskBoardList'>";
            	if(data){
            		$.each(data,function(key,value){
            			var retUser = {logo:"",name:""};
            			var user = window.projectUserList[value.boardAdmin];
            			if(user){
            			    retUser.logo = user.logo;
            				retUser.name = user.name;
            			}
            			str+="<li data-id='"+value.taskBoardId+"' title='"+value.name+"'><a>"+installHtml.getStrActualLen(value.name,24)+"</a>";
            			if(!window.readOnly && (($.inArray('TASK_BOARD:SET', value.permits) >= 0 && loginUserInfo.userId==value.boardAdmin)||$.inArray('PRJ:ADMIN_ACCESS', value.permits) >= 0)){
        					str+="<span class='pull-right'><i class='icon-pencil' title='修改'></i><i class='icon-trash' title='删除'></i></span>";
            		    }
            		    str+="<span class='creator'>管理员：<a><img data-id='"+value.boardAdmin+"' src='"+retUser.logo+"' class='img-circle little-pic'>"+retUser.name+"</a></span>" + 
            		         "<span class='desc'>"+(value.remark?value.remark:"")+"</span></li>";
            		});
            	}
            	str+="</ul></div>";
                return str;
            },
            updateTaskBoardGroupHtml : function(){//修改任务版界面
            	return "<ul id='updateTaskBoardGroup'><li>"+
                "<input class='form-control' type='text' placeholder='任务板名称'>"+
                "<textarea class='form-control' placeholder='任务板描述'></textarea>"+
            	"<button class='btn green btn-common'>保存</button></li></ul>";
            	
            },
//            taskboardSort : function(id){//任务版切换数据排序
//            	var data = this.switchButton.data("taskBoardList");
//            	if(data){
//            		var count = data.length ;
//            		for(var i=0 ;i<count; i++){
//                		if(data[i].taskBoardId == id){
//                			var activeBoard = data[i];
//                			data.splice(i, 1);
//                			data.splice(0,0,activeBoard);
//                			this.switchButton.data("taskBoardList",data);
//                			break;
//                		}
//                	}
//            	}
//            },
            updateBoardInfo : function(type ,newData){//任务版信息修改
            	var data = this.switchButton.data("taskBoardList");
            	if(data){
            		var count = data.length ;
            		for(var i=0 ;i<count; i++){
                		if(data[i].taskBoardId == newData.taskBoardId){
                			if(type == "update"){
                				$.each(newData,function(k,v){
                					data[i][k] = v;
                				});
                			}else{
                				data.splice(i, 1);
                			}
                			this.switchButton.data("taskBoardList",data);
                			break;
                		}
                	}
            	}
            },
    		setEvent : function(){
                var self = this;
                self.switchButton.off("click").on("click",function(){//显示任务版
    				layer.closeAll();
                	var projectId = panel.data("projectId");
                	$.ajax({
        				type:'post',
        				data:{projectId: projectId},
        				url:context + '/task/listTaskBoard.htm'
        			}).done(function(data){
        				if(data && data.success){
                        	if(data.resultData && data.resultData.length){
                        		self.switchButton.data("taskBoardList",data.resultData);
                        		$.layer({
                                    type: 1,
                                    title:false,
                                    area: ['auto', 'auto'],
                                    offset: ["50px", "100px"],
                                    shade: [0],
                                    border: [0],
                                    zIndex : 1000,
                                    closeBtn: [0],
                                    shift: 'left',
                                    page: {
                                        html: self.taskBoardGroupHtml()
                                    },
                                    success: function(layero){
                                    	self.layerSetEvent(layero);
                                    }
                              });
                        	}
                        }else if(data && !data.success){
                            layer.msg(data.errormsg);
                        }else{
                            layer.msg("请求失败");
                        }
        			});	
                    return false;
    			});
    		},
    		layerSetEvent : function(layero){//任务版弹出层事件
    			var self = this;
    			var addForm = $(layero).find("#addTaskBoardGroup");
    			var checkBoardNameInput = new InputValidate(addForm.find("input"), true, 40);
    			var checkDescInput = new InputValidate(addForm.find("textArea"), false, 600);
                $(layero).on("click",function(){
                	addForm.hide();
                	$("div#selectUserLayer").closest(".xubox_layer").remove();
                	$(layero).find("#taskBoardList li").show();
                	$(layero).find("#updateTaskBoardGroup").remove();
                    return false;
                });
                $(layero).find("#taskBoardList").on("click","li", function(){//切换任务版事件
                	var id = self.switchButton.data("id");
                	if(id == $(this).data("id")){
                		return false;
                	}
                	//self.taskboardSort($(this).data("id"));
                	taskBoardModel.getTaskLineData($(this).attr("title"),$(this).find("span.desc").text(),$(this).data("id"));
                	layer.closeAll();
                	return false;
                });
                
                addForm.on("click",function(){
                	return false;
                });
                $(layero).on("click","#addTaskBoardBtn",function(){//点击添加任务组按钮
                	$(layero).find("#taskBoardList li").show();
                	$(layero).find("#updateTaskBoardGroup").remove();
                	if(addForm.is(":hidden")){
                		addForm.show();
                	}else{
                		addForm.hide();
                	}
                	return false;
                });
                addForm.on("click","button",function(){//添加任务版
                	var name = addForm.find("input");
                	var des = addForm.find("textarea");
                	var disableBtn = $(this);
                	if(!checkBoardNameInput.checkValidate() || !checkDescInput.checkValidate()){
                		return false;
                	}
                    disableBtn.prop("disabled",true);
                    var projectId = panel.data("projectId");
                	$.ajax({
        				type: 'post',
        				data: {projectId: projectId, name: name.val(), remark: des.val()},
        				url: context + '/task/addTaskBoard.htm'
        			}).done(function(data){
        				if(data && data.success){
                        	if(data.resultData){
                        		layer.closeAll();
                        		taskBoardModel.getListTaskBoard();
                        	}
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
                });
                $(layero).find("#taskBoardList").find(".creator").on("click", "a", function(){ //点击管理员
                    var id = $(this).find("img").data("id").toString(), li = $(this).closest("li");
                    if(li.find(".pull-right").length <= 0) return;
                    var eventType = "updateAdmin";
                    personList.init({
                        searchInput:true,//是否需要搜索框
                        data : window.projectUserList,//人员数据
                        claiming : false,//是否先是待认领,要选中未认领时，selectData有一个值为0就可以了
                        selectData : [id],//要选中的数据
                        dom : $(this),
                        container : $(this),
                        isClose : true,
                        eventType:eventType
                    });
                    $(this).off(eventType).on(eventType, function(event, params){
                        var sendData = {
                            boardId : li.data("id"),
                            boardAdmin : params.ids[0]
                        };
                        $.ajax({
                            type:'post',
                            data: sendData,
                            url:context + '/task/updateBoardAdmin.htm'
                        }).done(function(resultData){
                            if(resultData && resultData.success){
                            	panel.load(context + "/task/loadTaskBoard.htm", {projectId: panel.data("projectId")});
                                params.callback();
                                self.updateBoardInfo("update",{taskBoardId:sendData.boardId,boardAdmin:sendData.boardAdmin});
                                //taskBoardModel.getListTaskBoard();
                            }else if(resultData && !resultData.success){
                                layer.msg(resultData.errormsg);
                            }else{
                                layer.msg("请求失败");
                            };
                        });
                    });
                    return false;
                });
                $(layero).find("#taskBoardList").on("click",".icon-pencil", function(){//修改任务板信息
                	var admin = $(this).closest("li").find("img").data("id");
                	addForm.hide();
                	$(layero).find("#taskBoardList li").show();
                	$(layero).find("#updateTaskBoardGroup").remove();
                	
                	var data = self.switchButton.data("taskBoardList");
                	var li = $(this).closest("li");
                	li.hide();
                	
                	var form = $(self.updateTaskBoardGroupHtml());
                	
                	form.find("input").val(li.attr("title"));
                	var checkBoardNameInput2 = new InputValidate(form.find("input"), true, 40);
        			var checkDescInput2 = new InputValidate(form.find("textArea"), false, 600);
        			
                	var taskBoardId = li.data("id");
                	$.each(data, function(key,value){
                		if(value.taskBoardId == taskBoardId){
                			form.find("textarea").val(value.remark);
                			return false;
                		}
                	});
                	
                	form.insertAfter(li);
                	
                	form.on("click",function(){
                		return false;
                	});
                	form.on("click","button",function(){//修改完点击保存按钮
                		var name = form.find("input").val();
                    	var des = form.find("textarea").val();
                    	
                    	if(!checkBoardNameInput2.checkValidate() || !checkDescInput2.checkValidate()){
                    		return false;
                    	}
                    	
                    	var updateData = {taskBoardId: taskBoardId, name: name, remark: des, admin:admin};
                    	var disableBtn = $(this);
                        disableBtn.prop("disabled",true);
                    	$.ajax({
            				type: 'post',
            				data: updateData,
            				url: context + '/task/updateTaskBoard.htm'
            			}).done(function(data){
            				if(data && data.success){
                        		li.children("a").text(installHtml.getStrActualLen(updateData.name,24)).end().attr("title",updateData.name);
                        		li.find("span.desc").text(des);
                            	$(layero).find("#taskBoardList li").show();
                            	if(updateData.taskBoardId == self.switchButton.data("id")){
                            		self.switchButton.find("span.name").text(installHtml.getStrActualLen(name,24)).attr("title",name);
//                            		self.switchButton.find("span.desc").text(installHtml.getStrActualLen(des,24)).attr("title",des);
                            	}
                            	$(layero).find("#updateTaskBoardGroup").remove();
                            	self.updateBoardInfo("update",updateData);
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
                	});
                	return false;
                });
                $(layero).find("#taskBoardList").on("click",".icon-trash", function(){//点击任务版删除按钮
                	var taskBoardId = $(this).closest("li").data("id");
//                	var admin = $(this).closest("li").find("img").data("id");
                	var dele = $(this);
                	var id = self.switchButton.data("id");
                	if(id == taskBoardId){
                		layer.alert("不能删除当前任务板,请切换任务板之后再操作");
                		return false;
                	}
                	var disableBtn = $(this);
                    disableBtn.prop("disabled",true);
                	$.ajax({
        				type: 'post',
        				data: {taskBoardId: taskBoardId},
        				url: context + '/task/delTaskBoard.htm'
        			}).done(function(data){
        				if(data && data.success){
        					self.updateBoardInfo("delete",{taskBoardId: taskBoardId});
        					$(layero).find("#taskBoardList li").show();
                        	$(layero).find("#updateTaskBoardGroup").remove();
                        	dele.closest("li").remove();
        					addForm.hide();
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
                });
    		}
        };
    	return taskBoardModel;
});