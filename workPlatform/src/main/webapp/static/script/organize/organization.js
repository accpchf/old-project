require(["jquery","layer"],function(){
	var menu = {
		dom : $("#organizationMenu"),
		container : $("#organizationContainer"),
		init : function(){
			this.setEvent();
			this.dom.find("#person").trigger('click');
		},
		setEvent :function(){
			var self = this;
			self.dom.on("click","#person",function(){
				
				layer.closeAll();
				
				if($(this).hasClass('active')){
					return;
				}
		        $(this).addClass("active").siblings().removeClass("active");
		        self.container.load(context+"/org/userList.htm?id="+$("#id").val());
		        return false;
	        })
	        self.dom.on("click","#team",function(){
	        	
	        	layer.closeAll();
	        	
	        	if($(this).hasClass('active')){
					return;
				}
		        self.container.load(context+"/team/list.htm?id="+$("#id").val());
		        $(this).addClass("active").siblings().removeClass("active");
		        return false;
	        })
	        self.dom.on("click","#statistics",function(){
	        	
	        	layer.closeAll();
	        	
	        	if($(this).hasClass('active')){
					return;
				}
		        $(this).addClass("active").siblings().removeClass("active");
		        self.container.load(context+"/org/organizeStatistics.htm?orgId="+$("#id").val());
		        return false;
	        })
	        self.dom.on("click","#set",function(){
	        	
	        	layer.closeAll();
	        	
	        	if($(this).hasClass('active')){
					return;
				}
		        self.container.load(context+"/org/show.htm?id="+$("#id").val());
		        $(this).addClass("active").siblings().removeClass("active");
		        return false;
	        })
		}
	}
	menu.init();
	
})