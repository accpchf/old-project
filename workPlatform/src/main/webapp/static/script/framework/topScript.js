//$(function(){
//	alert(TEXT_CONFIG.show_create_organiz);
//});

//TODO 去掉
define(['jquery','layer'],function($){
	var popupLayer = {//新建组织或者项目
		indexUrl : context + "/project/home.htm",
		init : function(){
			this.setEvent();
			this.userInfo = userInfo.init();
			this.createObject = createObject.init();
		},
		setEvent:function(){
			var self = this;
			$("#logoPic").on("click",function(){//logo图标点击回到项目主页
	    		location.href = self.indexUrl;
	    	});
	        $(document).on("click",function(event){//关闭所有的浮出层
	            layer.closeAll();
	        });
	        $(document).on("click","div.xubox_shade",function(){
	            return false;
	        });
	        $(document).on("click","div.xubox_layer[type='dialog']",function(){
	            return false;
	        });
		},
		openPersonSettingLayer : function(){
			 var html = $("<div></div>");
			
		        html.load(context + "/personal/getPersonalSetting.htm", function(){
		          	layer.closeAll();
		          	$.layer({
			            type: 1,
			            title: false,
			            area: ['auto', '500px'],
			            border: [0], //去掉默认边框
			            shade: [0.8, '#000'],
			            closeBtn: [0, false], //去掉默认关闭按钮
			            shift: ['top', 500, true], //从左动画弹出
			            offset:['10px',''],
			            page: {
			                html: html.html()
			            },
			            success: function(layero2){
			              $(layero2).on('click', function(event){
			            	  EventUtil.stopPropagation(event);
			              });
			              $(layero2).on("click",".icon-cancel-circled",function(){
			                layer.closeAll();
			              });
			            }
		          	});
		        });
		}
	};

	var createObject ={
		init : function(){
			this.openCreateLayerEvent();
		},
		dom:$("#createObj"),//当前操作对象
		createLayerIndex : "",//新建弹出层的index
		getLayerHtml:function(){//新建的弹出层的代码
			var global_edition = TEXT_CONFIG.global_edition.toLowerCase();
			var rtnHtml = "<div class='layer-lg boardBackground floatLayer '><ul>"+
        	"<li id='createProject'><h6>创建"+TEXT_CONFIG.xiangmu+"</h6><p>每个"+TEXT_CONFIG.xiangmu+"都有任务板、文件库和日程表等功能。</p></li>";
			
			if(global_edition == "web") {
				rtnHtml += "<li id='createOrganization'  class='border-line'><h6>创建"+TEXT_CONFIG.zuzhi+"</h6><p>"+TEXT_CONFIG.zuzhi+"是"+TEXT_CONFIG.xiangmu+"和人的集合，可以用来管理你的公司、"+TEXT_CONFIG.tuandui+"。</p></li>";
			}
			
			rtnHtml += "</ul></div>";
			return rtnHtml;
		},
		layerResize:function(layero) { //浏览器大小变化时弹出层位置变化
			var self = this;
			$(window).resize(function() {
		        
				var layerPosition = self.layerPosition();
		        $(layero).css({"top":layerPosition.top,"left":layerPosition.left});
			});
		},
		layerPosition : function(){

			var createLayerPosition = this.dom.offset();
	        var createLayerTop = createLayerPosition.top+this.dom.height()-$(window).scrollTop()+10 + "px";
	        var createLayerLeft = createLayerPosition.left-40 + "px";

	        return { top: createLayerTop,
	        		left : createLayerLeft
	        };
		},
		openCreateLayerEvent :function(){//新建的弹出层
			var self = this;
			self.dom.on("click",function(event){
			    layer.closeAll();

			    var layerPosition = self.layerPosition();
			    self.createLayerIndex = $.layer({
				    type: 1,   //0-4的选择,
				    title: false,
				    area: ['auto', 'auto'],
				    offset: [layerPosition.top, (layerPosition.left.replace("px", "")-20)+"px"],
				    border: [0],
				    shade: [0, '#FFFFFF'],
//				    fix:false,
				    closeBtn: [0],
//				    fix:false,
				    page: {
				        html: self.getLayerHtml()
				    },
				    success: function(layero){
				    	self.layerResize(layero);
				    	self.openCreateProjectLayerEvent(layero);
				    	self.openCreateOrganLayerEvent(layero);
				    	document.documentElement.style.overflow = "hidden";
					},
					end : function(layero) {
						document.documentElement.style.overflow = "";
				    }
			  	});
			  	return false;
			});
		},
		openCreateOrganLayerEvent : function(layero){//新建组织弹出层
			var createorganLayerIndex;
			$(layero).on("click","#createOrganization",function(){	
	          layer.closeAll();
	          var aaa = $("<div></div>");
	          aaa.load(context + "/org/add.htm",function(){
	        	  createorganLayerIndex = $.layer({
	  	            type: 1,
	  	            title: false,
	  	            area: ['auto', 'auto'],
	  	            border: [0], //去掉默认边框
	  	            shade: [0.8, '#000'],
	  	            closeBtn: [0, false], //去掉默认关闭按钮
	  	            shift: ['top', 500, true], //从左动画弹出
	  	            page:{
	  	            	html :aaa.html()
	  	            },
	  	            success:function(layero)
	  	            {
	  	            	$(layero).click(function(){
            				return false;
	  	            	});
	  	            }
	  	          });
	          });     
	        });
		},
		openCreateProjectLayerEvent : function(layero){//新建项目弹出层
			var createProjectLayerIndex;
	        
	        $(layero).on("click",function(e){
	        	EventUtil.stopPropagation(e);
	        });
	        $(layero).on("click","#createProject",function(){	
	        	layer.closeAll();
//	        	var addProjectHtml = $("<div></div>");
	        	var projectSettingBootstrapLayer = $("#addProjectBootstrapLayer");
	        	projectSettingBootstrapLayer.load(context + "/project/addProject.htm",function(){
	        		projectSettingBootstrapLayer.modal('show');
//	        		projectSettingBootstrapLayer.find("#orgId").val(orgid);
		        });
	          return false;
	        });
	        
      }
	};
	var userInfo ={//获得用户基本信息
		url:"",
		init : function(){
			this.openPersonaInfoLayerEvent();
		},
		dom : $("#personaInfo"),
		personaInfoIndex : "",
		layerResize : function(layero) { //浏览器大小变化时弹出层位置变化
			var self = this;
			$(window).resize(function() {
	            var layerPosition = self.layerPosition();

	            $(layero).css({"top":layerPosition.top,"left":layerPosition.left});
			});
		},
		layerPosition : function(){

			var personaLayerPosition = this.dom.offset();
            var personaLayerTop = personaLayerPosition.top+this.dom.height()-$(window).scrollTop()+10 + "px";
            var personaLayerLeft = personaLayerPosition.left - 60 + "px";

	        return { top: personaLayerTop,
	        		left : personaLayerLeft
	        };
		},
		getLayerHtml:function(){
			return "<div class='layer-sm boardBackground floatLayer'><ul>"+
					"<li id='myTask'><a>我的任务</a></li>"+
					"<li id='userWeekReport'><a>我的周报</a></li>"+
					"<li id='personSetting'><a>个人设置</a></li>"+
				//	"<li><a>我的消息</a></li>"+
					//"<li><a>帮助中心</a></li>"+
					"<li id='loginout' class='border-line'><a>退出登录</a></li>"+
				"</ul></div>" ;
		},
		getUserInfo : {},

		setEvent : function(layero){
			$(layero).on("click","#myTask",function(){
				var page = $("div[data-id='relatedMeTempalte']");
				if(page.length == 0){
					location.href =context + "/personal/personalTask.htm";
				}else{
					if(page.attr("data-moudleName") == "task" || page.find("#relateMeTaskMenu").hasClass("active")){
						layer.closeAll();
						return false;
					}else{
						location.href =context + "/personal/personalTask.htm";
					}
				}
			});
			$(layero).on("click","#loginout",function(){
				location.href =context + "/login/loginout.htm";
			});
			$(layero).on("click","#userWeekReport",function(){
				var page = $("div[data-id='relatedMeTempalte']");
				if(page.length == 0){
					location.href =context + "/personal/weeklyReport.htm";
				}else{
					if(page.attr("data-moudleName") == "report" || page.find("#relateMeWeeklyReportMenu").hasClass("active")){
						layer.closeAll();
						return false;
					}else{
						location.href =context + "/personal/weeklyReport.htm";
					}
				}
			});

		},
		openPersonaInfoLayerEvent : function(){
			var self = this;
			self.dom.on("click",function(event){

		        layer.closeAll();

		        var layerPosition = self.layerPosition();
		        self.personaInfoIndex = $.layer({
			        type: 1,   //0-4的选择,
			        title: false,
			        area: ['auto', 'auto'],
				    offset: [layerPosition.top, layerPosition.left],
			        border: [0],
			        closeBtn: [0],
			        fix:true,
			        shade: [0, '#FFFFFF'],
			        //shadeClose: true,
			        page: {
			          html: self.getLayerHtml()
			        },
			        success: function(layero){
			        	self.setEvent(layero);
			        	self.layerResize(layero);
			        	self.openPersonaSettingLayerEvent(layero);
			        }
		    	});
		    	return false;
		    });
		},
		openPersonaSettingLayerEvent : function(layero){
			 $(layero).on("click",function(e){
		          return false;
		        });
			 $(layero).on("click","#personSetting",function(){
				 popupLayer.openPersonSettingLayer();
				   	return false;
		      });
	       
	        
		}
	};
	return popupLayer;
});