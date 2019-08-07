require(["jquery","layer"],function($){

	var projectMenu = {
		menuDom : $("#menuItems"),
		contentDom : $("div[data-id='projectTemplate']"),
		init : function(){
			var projectId = this.contentDom.attr("data-projectId");
			this.projectId = projectId;
			this.contentDom.removeAttr("data-projectId").data("projectId",projectId);
			this.setEvent();
			this.menuDom.find("#taskBoard").trigger('click');
			this.getProjectUser();
		},
		getProjectUser : function(){
			var projectId = this.contentDom.data("projectId");
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
		setEvent :function(){
			var self = this;
	   		self.menuDom.on("click","#taskBoard",function(){
	   			if($(this).hasClass('select')){
	   				return;
	   			}
	   			var projectId = self.contentDom.data("projectId");
	   			self.contentDom.load(context + "/task/loadTaskBoard.htm", {projectId: projectId});
		        $(this).addClass("select").find(".right-arrow").show().closest("li").siblings().find("a").removeClass("select").find("i.right-arrow").hide();
		    });
		    self.menuDom.on("click","#fileLibrary",function(){
		    	if($(this).hasClass('select')){
	   				return;
	   			}
		        self.contentDom.load(context + "/filelibrary/loadfilelibraryview.htm", {projectId:self.projectId});
		        $(this).addClass("select").find(".right-arrow").show().closest("li").siblings().find("a").removeClass("select").find("i.right-arrow").hide();
		    });
		    self.menuDom.on("click","#dynamic",function(){
		    	if($(this).hasClass('select')){
	   				return;
	   			}
		        self.contentDom.load(context+"/dynamic/loadDynamicView.htm?uuid=1");
		        $(this).addClass("select").find(".right-arrow").show().closest("li").siblings().find("a").removeClass("select").find("i.right-arrow").hide();
		    });
		    self.menuDom.on("click","#weekly",function(){
		    	if($(this).hasClass('select')){
	   				return;
	   			}
		       self.contentDom.load(context + "/weeklyReport/loadWeeklyReportview.htm?projectId="+self.projectId);
		       $(this).addClass("select").find(".right-arrow").show().closest("li").siblings().find("a").removeClass("select").find("i.right-arrow").hide();
		    });
		    self.menuDom.on("click","#meetting",function(){
		    	if($(this).hasClass('select')){
	   				return;
	   			}
		        self.contentDom.load(context + "/meeting/loadmeetingview.htm", {projectId:self.projectId});
		        $(this).addClass("select").find(".right-arrow").show().closest("li").siblings().find("a").removeClass("select").find("i.right-arrow").hide();
		    });																	
		}
	};

	projectMenu.init();
});