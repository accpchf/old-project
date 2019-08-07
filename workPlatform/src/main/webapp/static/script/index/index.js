define(['jquery'],function(){
	var homePage={
		init : function(){
			this.setEvent();
		},
		dom : $("div[data-id='projectHomePage']"),
		setEvent : function(){
			var self = this;
			self.dom.on("click",".goOrganization",function(){
		        location.href="../template/organizationTemplate.html";
		        return false;
		    });
		    self.dom.on("click",".project-block",function(){
		        location.href = context + "/project/gotoProject.htm";
		        return false;
		    });
		    var projectLayerIndex;
	        var html = $("<div></div>");
	        html.load(context + "/static/html/projectSetting/projectSetting.html");
	        self.dom.on("click",".icon-pencil",function(event){
	          event.stopPropagation ? event.stopPropagation() : event.cancelBubble = true;
	          layer.closeAll();
	          projectLayerIndex = $.layer({
	            type: 1,
	            title: false,
	            area: ['auto', 'auto'],
	            border: [0], //去掉默认边框
	            shade: [0.8, '#000'],
                closeBtn: [0, false], //去掉默认关闭按钮
	            shift: ['top', 500, true], //从左动画弹出
	            page: {
	                html: html.html()
	            },
	            success: function(layero){
	              $(layero).on("click",".icon-cancel-circled",function(){
	                layer.close(projectLayerIndex);
	              });
	              $(layero).on("click",function(e){
	                e.stopPropagation ? e.stopPropagation() : e.cancelBubble = true;
	              });
	            }
	          });
	          return false;
	        });
		}
	};
	
    return  homePage;
});