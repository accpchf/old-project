require(["jquery","script/framework/topScript"], function($,topScript){
	var relatedMeModule = {
		init : function(){
			this.panel = $("div[data-id='relatedMeTempalte']");
			
			var moudleName = this.panel.attr("data-moudleName");
			if(moudleName == "task"){
				$("#relatedMeContainer",this.panel).load(context + "/personal/loadPersonalTask.htm");
				$("#relatedMeMenu",this.panel).find("#relateMeTaskMenu").addClass("active");
			}else if(moudleName == "report"){
				$("#relatedMeContainer",this.panel).load(context + "/weeklyReport/loadUserWeeklyReportview.htm");
				$("#relatedMeMenu",this.panel).find("#relateMeWeeklyReportMenu").addClass("active");
			}
			this.setEvent();
		},
		setEvent : function(){
			var self = this;
			this.panel.on("click","#relateMeTaskMenu",function(){
				if(self.panel.attr("data-moudleName") == "task" || $(this).hasClass("active")){
					return false;
				}
				location.href =context + "/personal/personalTask.htm";
			});
			this.panel.on("click","#relateMeWeeklyReportMenu",function(){
				if(self.panel.attr("data-moudleName") == "report" || $(this).hasClass("active")){
					return false;
				}
				location.href =context + "/personal/weeklyReport.htm";
			});
			this.panel.on("click","#relateMeSettingMenu",function(){
				topScript.openPersonSettingLayer();
				return false;
			});
		}
	}
	relatedMeModule.init();
});