define(['jquery'],function($){

	var switchingProject = {
		init : function(){
			this.switchBtn = $("#switchingProject");
			//this.getProjectListStr();
			this.menuItems = $("#menuItems");
			this.setEvent();
			
		},
		layerResize:function(layero) { //浏览器大小变化时弹出层位置变化
			var self = this;
			$(window).resize(function() {
		        
				var layerPosition = self.layerPosition();
		        $(layero).css({"top":layerPosition.top,"left":layerPosition.left});
			});
		},
		layerPosition : function(){

			var createLayerPosition = this.switchBtn.offset();
	        var createLayerTop = createLayerPosition.top+this.switchBtn.height()-$(window).scrollTop()+10 + "px";
	        var createLayerLeft = createLayerPosition.left-40 + "px";

	        return { top: createLayerTop,
	        		left : createLayerLeft
	        };
		},
//		getProjectListStr : function(){
//			var self = this;
//			var str = "";
//			$.ajax({
//				type : "get",
//				url : context+"/project/getProjectsList.htm"
//			}).done(function(result){
//				if(result){
//					var data = result.resultData;
//					str += "<div class='layer-big boardBackground floatLayer project-img'>";
//					str += "<div class='menu-input'><input class='form-control' type='text' placeholder='查找项目' ></div><ul class='vertical-scroll-min scroll-area'>";
//					str += "<li class='title-line'>常用项目</li>";
//					str += self.projectHtml(data.commonProjects);//常用项目
//					str += "<li class='title-line'>个人项目</li>";
//					str += self.projectHtml(data.personProjects);//个人项目
//					if(data.orgProjects){//组织项目
//						for(var k in data.orgProjects){
//							var obj = data.orgProjects[k];
//							str += "<li class='title-line'>"+obj.name+"</li>";
//							str += self.projectHtml(obj.projectList);
//						}
//					}
//					str += "</ul></div>";
//						self.projectListHtml = result;
//			      	}else{
//	                    layer.msg("请求失败");
//	                }
//				}).fail(function(){
//					layer.msg("请求失败");
//				});
//		},
//		projectHtml : function(data){
//			var self =this;
//			var str = "";
//			var id = $("div.main-content[data-id='projectTemplate']").data("projectId");
//			$.each(data,function(key,value){
//				if(value.projectId == id){
//					self.switchBtn.find("span").text(value.name);
//				}
//				str += "<li data-id='"+ value.projectId+"' class='project'><img src='"+value.logoStr+"' /><a>"+value.name+"</a><i class='icon-ok' style='display:none;'></i></li>";
//			});
//				
//			if(str == ""){
//				str = "<li>无</li>";
//			}
//			return str;
//		},
		getProjectUser : function(projectId){
			$.ajax({
				type:'post',
				data: {projectId: projectId},
				url:context + '/project/listProjectAllUser.htm'
			}).done(function(data){
				if(data && data.success){
                	if(data.resultData){
                		window.projectUserList = data.resultData;
                	}
                }else if(data && !data.success){
                    layer.msg(data.errormsg);
                }else{
                    layer.msg("请求失败");
                }
			});
		},
		setEvent : function(){
			var switchingProjectIndex;
			var self = this;
			layer.closeAll();
			this.switchBtn.on("click",function(event){
				var layerPosition = self.layerPosition();
				switchingProjectIndex = $.layer({
	            type: 1,   //0-4的选择,
			    title: false,
			    area: ['auto', 'auto'],
			    offset: [layerPosition.top, layerPosition.left],
			    border: [0],
			    shade: [0, '#FFFFFF'],
			    closeBtn: [0],
			    area: ['120px', '80px'],
	            page: {
	                dom: "#hideAllProectList"
	            },
	            success: function(layero){
	            	self.switchingProjectEvent(layero);
	            	document.documentElement.style.overflow = "hidden";
				},
				end : function(layero) {
					document.documentElement.style.overflow = "";
				}
	          });
	          return false;
			});
		},
		switchingProjectEvent : function(layero){
			var self = this;
			var el = $("div.main-content[data-id='projectTemplate']");
			 self.layerResize(layero);
             $(layero).on("click",function(){
            	 return false; 
             });
             $(layero).find("i.icon-ok").hide();
             $(layero).find("li[data-id='"+el.data("projectId")+"']").find("i.icon-ok").show();
             $(layero).on("change paste keyup", "input", function(){
					var value = $(this).val();
					if(value == ""){
						 $(layero).find("li").show();
					}else{
						$(layero).find("li").hide();
						$(layero).find("li.project:contains('"+value+"')").show();
					}
			 });
             $(layero).on("click","ul li.project",function(){
            	 var el = $("div.main-content[data-id='projectTemplate']");
            	 if($(this).data("id") == el.data("projectId")){
            		 return false;
            	 }else{
            		 el.data("projectId",$(this).data("id"));
                	 self.switchBtn.find("span").text($(this).text());
                	 var type = self.menuItems.find(".select").attr("id");
                	 self.getProjectUser($(this).data("id"));
                	 if(type == "taskBoard"){
                		 el.load(context + "/task/loadTaskBoard.htm", {projectId: $(this).data("id")});
                	 }else if(type == "fileLibrary"){
                		 el.load(context + "/filelibrary/loadfilelibraryview.htm", {projectId:$(this).data("id")});
                	 }else if(type == "dynamic"){
                		 el.load(context+"/dynamic/loadDynamicView.htm?uuid=1");
                	 }else if(type == "weekly"){
                		 el.load(context + "/weeklyReport/loadWeeklyReportview.htm?projectId="+$(this).data("id"));
                	 }else if(type == "meetting"){
                		 el.load(context + "/meeting/loadmeetingview.htm", {projectId:$(this).data("id")});
                	 }
                	 layer.closeAll();
            	 }
            	 
            	 return false; 
             });
		}
	};
	return switchingProject;
});