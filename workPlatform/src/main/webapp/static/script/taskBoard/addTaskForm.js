/*
 * 增加任务，增加子任务的模块
 */
define(["jquery",
        "script/common/personList",
        context+"/static/script/taskBoard/installHtml.js?_T="+(new Date()).valueOf(),
        "script/taskBoard/taskSocket",
        "layer"],function($,personList,
        		installHtml,taskSocket){
	
	//操作区
	var panel = $("div.main-content[data-id='projectTemplate']");
	var addTaskForm = {
			init : function(userList,dom){//列ID
				this.dom = $(dom);
				this.userList = userList;
				
				if($(dom).hasClass("addTaskForm")){//任务版里添加任务
					this.projectId = panel.data("projectId");
					this.boardId = panel.find("#switchTaskBoard").data("id");
					this.taskLineId = $(dom).closest("li").data("id");
					this.taskList = $(dom).closest("li").find(".taskList");
				}else{//任务里添加子任务
					var taskInfo = $(dom).closest("#updateTaskRightLayer").data("taskInfo");
					this.projectId =  taskInfo.projectId;
					this.boardId = $(dom).closest("#updateTaskRightLayer").data("boardId");
					this.taskLineId =  taskInfo.taskLine.taskLineId;
					if(panel.length > 0){
						this.taskList = panel.find(".main-task").find("ul > li[data-id='"+this.taskLineId+"']").find(".taskList");//任务版中
					}else{
						this.taskList = $("#futureTaskList").find("div.task-project[data-id='"+this.projectId+"']").find("ul");//我的任务界面中
					}
				}
				this.tarkFormDom = $(this.getFormHtml()).appendTo($(dom));
				this.setEvent();
			},
			getFormHtml : function(){//添加任务form表单内容
				return "<div class='block add-task'>"+
	            "<textarea class='task-content-input form-control' rows='2' placeholder='任务内容'></textarea>"+
	            "<label style='margin-left:10px'>执行者</label><br>"+
	            "<div id='changeExecutor' class='task-infor'> <a><img  data-id='"+loginUserInfo.userId+"' src='"+loginUserInfo.logo +"' class='img-circle'> "+loginUserInfo.name+"</a></div>"+
	            "<div class='task-infor add-message'>"+
	                 "<label>参与者</label><br>"+
	                 "<ul><li data-id='"+loginUserInfo.userId+"' title='"+loginUserInfo.name +"'><img src='"+loginUserInfo.logo +"' class='img-circle'><a class='remove-member-handler'>×</a></li>"+
	                 "<li id='selectPartner'><i class='icon-plus-circle icon-big'></i></li></ul>"+
	           "</div>"+
	           "<button id='addTaskBtn' class='btn btn-sure'>新建</button>"+
	           "<div class='layer-group  open-layer'>"+
	           "<ul id='taskVisibleList' class='add-message boardBackground inerLayer floatLayer' style='display:none'>"+
	                "<li><a>所有"+TEXT_CONFIG.chengyuan+"可见</a><i class=' icon-ok'></i></li>"+
	                "<li><a>仅参与者可见</a><i class=' icon-ok'></i></li>"+
	           "</ul>"+
	           "<button id='taskVisible' data-visible=true class='btn gray'><span>所有"+TEXT_CONFIG.chengyuan+"可见</span> <i class='icon-up-open-big'></i></button>"+
	           "</div>"+
	           "</div>";
			},
			setEvent : function(){
				var self =this;
				this.tarkFormDom.find("textarea").focus();//输入框获得焦点
				var checkContent = new InputValidate(this.tarkFormDom.find("textarea"), true, 600);
				
				var menuUl = this.tarkFormDom.find("#taskVisibleList");//任务是否可见菜单
				 this.tarkFormDom.on("click","#addTaskBtn",function(){
					 var data={};
					 var content = self.tarkFormDom.find("textarea").val();
					 if(!checkContent.checkValidate()){
		                	return false;
		             }
					 var arr =[];
					 var partnerListLi = self.tarkFormDom.find("#selectPartner").prevAll("li");
					 partnerListLi.each(function(){
	            		arr.push($(this).data("id").toString());
	            	 });
					 data.content = content;
					 data.projectId = self.projectId;
					 data.taskLineId = self.taskLineId,
					 data.partner = arr.length < 1 ? [0] : arr;
					 data.executor = self.tarkFormDom.find("#changeExecutor img").data("id");
					 data.visible = self.tarkFormDom.find("#taskVisible").data("visible");
					 
					 if($(self.dom).hasClass("addTaskForm")){//任务版里添加任务
						data.parentTaskId = 0;
					 }else{//任务里添加子任务
						 data.parentTaskId = $(self.dom).closest("#updateTaskRightLayer").data("taskInfo").taskId;
					 }
					 $.ajax({
							type:'post',
							data:data,
							url:context + '/task/addTask.htm'
						}).done(function(response){
							if(response && response.success){
			                	if(response.resultData){
			                		data.taskId = response.resultData,
	                				data.priority = "00100",
	                				data.taskBoardId = self.boardId;
			                		if($(self.dom).hasClass("addTaskForm")){//任务版里添加任务
				                		// 向其他客户端推送添加任务的消息
				                		taskSocket.addTask(data);
				                		self.taskList.append(installHtml.taskHtml("add", data));
				                		self.tarkFormDom.find("textarea").val("");
				                		installHtml.updateTaskCount(self.taskList.closest("li"));
										 
									 }else{//任务里添加子任务
										 if(panel.length > 0){
											 $(installHtml.taskHtml("add", data)).appendTo(self.taskList);
										 }else{
											 //属于当前用户的才会显示
											$(installHtml.myTaskHtml(data)).appendTo(self.taskList);
										 }
										
										 self.tarkFormDom.remove();
										 var childTaskContent = $(self.dom).closest("#updateTaskRightLayer").find("#updateChildTask #childTask");
										 if(childTaskContent.find("ul").find("li").length > 0){
											 childTaskContent.find("ul").append(installHtml.childTaskHtml(self.userList,data));	 
										 }else{
											 childTaskContent.find("ul").empty().append(installHtml.childTaskHtml(self.userList,data));	
										 }
										 if(panel.length > 0){
											 installHtml.updateTaskCount(self.taskList.closest("li"));
										 }
									}
	                				
			                	}
			                }else if(response && !response.success){
			                    layer.msg(response.errormsg);
			                }else{
			                    layer.msg("请求失败");
			                }
						});
				 });
				 this.tarkFormDom.on("click", function(){//点击form时关闭层
					$("div#selectUserLayer").closest(".xubox_layer").remove();
	            	menuUl.hide();
					return false;
	             });
				 this.tarkFormDom.find("#taskVisible").on("click",function(event){//切换任务button是否可见菜单事件
					 menuUl.show();
					 if($(this).data("visible")){
	            		menuUl.find("i:eq(0)").show();
	            		menuUl.find("i:eq(1)").hide();
					 }else{
	            		menuUl.find("i:eq(1)").show();
	            		menuUl.find("i:eq(0)").hide();
					 }
					 return false;
	            });
				menuUl.on("click","li",function(event){//切换任务可见的菜单点击事件
	            	var menuBtn = self.tarkFormDom.find("#taskVisible");

	            	menuUl.find("i").hide();
	            	$(this).find("i").show();
	            	menuBtn.data("visible",!Boolean($(this).index()));
	            	menuBtn.find("span").text($(this).text());	
	            	menuUl.hide();
	            	return false;
	            });
				
				this.tarkFormDom.find("#changeExecutor").on("click",function(event){//执行者切换
	            	var id = $(this).find("img").data("id").toString();
	            	var users = $.extend(true, {}, self.userList || {});
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
	                    isClose : true
	                });
	                return false;
	            });
	            
				this.tarkFormDom.on("click",".remove-member-handler", function(){//参与者人员删除
	            	$(this).closest("li").remove();
	            });
	            this.tarkFormDom.find("#selectPartner").on("click",function(event){//选择参与者
	            	var arr =[];
	            	$(this).prevAll("li").each(function(){
	            		arr.push($(this).data("id").toString());
	            	});
	            	personList.init({
	                    searchInput:true,//是否需要搜索框
	                    data : self.userList || {},//人员数据
	                    everyone : true,//是否先是待认领,要选中未认领时，selectData有一个值为0就可以了
	                    selectData : arr,//要选中的数据
	                    dom : $(this),
	                    container : $(this)
	                });
	                return false;
	            });
			}
	    };
	return addTaskForm;
});