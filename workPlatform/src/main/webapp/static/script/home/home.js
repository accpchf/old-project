require(["jquery", 'layer'],function($){
	var homeInfo;
	
	var global_edition = TEXT_CONFIG.global_edition.toLowerCase();
	if(global_edition == "enterprice") {
		$("#gerenxiangmu").remove();
	}
	
	homeInfo = {
			dom : $("div[data-id='projectHomePage']"),
			init:function(){
				this.panel = $("#home_body");
				this.organizationDiv = this.panel.find(".organizationprojects");
				this.pprojectDiv = this.panel.find(".personprojects");
				this.cprojectDiv = this.panel.find(".commomuseprojects");
				this.setEvent();
				this.findOtherProject();
				this.createProject();
			},
			setEvent : function(){
				var self = this;
				self.dom.on("click",".project-block",function(){//
					var projectId = $(this).data('projectid');
					var status = $(this).data('status');
					var uuid = $(this).data('uuid');
					var deletestatus = self.dom.data("deletestatus");
					if(status == deletestatus){//判断是否为已删除
						return false;
					}
					location.href = context + "/project/"+uuid+"/gotoProject.htm";
			        return false;
			    });
				 var projectLayerIndex;
			        self.dom.on("click",".icon-pencil",function(event){
			        	var div1 = $(this).closest('div.project-block'),
		        		projectId = div1.data('projectid');
			        	var projectSettingBootstrapLayer = $("#projectSettingBootstrapLayer");
			        	projectSettingBootstrapLayer.load(context + "/project/projectSetting.htm?proId=" +projectId,function(){
			        		projectSettingBootstrapLayer.modal('show');
			        	});
			        	return false;
			        });
		        //点击星标按钮设置项目是否常用
				self.dom.on('click', '.icon-star', function(evt){
					var div1 = $(this).closest('div.project-block'),
						projectId = div1.data('projectid'),
						projectName = div1.find("h3").html(),
						titleName = div1.find("h3").attr('title'),
						statusType =div1.data('status'),
						status = div1.find('.describe').html();
						uuid = div1.data('uuid'),
						commonUse = $(this).hasClass('select');	
					var doingType = self.dom.data("deletestatus");
					
					if(statusType == doingType){//判断是否为删除
						return false;
					}
					var bg=div1.css('background-image'); //取到背景，但是会有url('....jpg');
					var bg_url=bg.replace('url(','').replace(')','');//替换掉多余的字符
					$.ajax({
						type:'post',
						url:context + '/project/isCoummonUse.htm',
						data:{"projectId":projectId ,"isCommonUse":!commonUse ? true : false}
					}).done(function(){
						var param = {
							logoStr	 :bg_url,
							projectId:	projectId,
							name :projectName,
							statusType:statusType,
							uuid:uuid,
							status:status,
							commonUse:true,
							title:titleName
						};
						if(commonUse){
							self.cprojectDiv.find('div[data-projectid="'+ projectId+'"]').remove();
						}else{
							self.panel.find(".commomuseprojects").append(self.getCommonUseProjectHtml(param));
						}
						var $icon = self.dom.find('div.project-block[data-projectid="'+projectId+'"] .icon-star');
						if(commonUse){
							$icon.removeClass('select');
						}else{
							$icon.addClass('select');
						}
					});
					return false;
				});
				self.dom.on('click', '.goOrganization', function(evt){
					var uuid = $(this).data('uuid');
					window.location=context+"/org/"+uuid+"/organization.htm";
				});
				
			},
			getProjectInitData:function(){
				var self = this;
				self.pprojectDiv.empty();
				self.cprojectDiv.empty();
				self.organizationDiv.empty();
				$.ajax({       
				      type: "post",       
				      url: context + "/project/initProject.htm", 
				      success: function (result) {
				    	  if(result && result.success){
				    		  var data = result.resultData;
				    		  self.initProjectModule(data);
				    	  }else if(result && !result.success){
								layer.msg(result.errormsg);
						  }else{
								layer.msg("请求失败");
						  };
				    					  
				    	  
				      },       
				      error: function (errorThrown) {  
				      }       
				});   
			},
			getCommonUseProjectHtml:function(value){
				var html =  $('<div class="project-block common corner-6" data-uuid = "'+value.uuid+'" data-status = "'+value.statusType+'" data-projectid="'+ value.projectId +'" style= background-image:url('+value.logoStr +');>'+
				  '<div class="operate"><em class="icon-pencil"></em><em class=" icon-star"></em></div>'+
				      '<h3 title="'+value.title+'">'+ value.name+'</h3>'+ 
				      '<div class="describe">'+ value.status +
				      '</div>'+
				'</div>');
				var projectDiv = html.find(".describe");
				if(value.commonUse){
					html.find('.icon-star').addClass('select');
				}
				if(value.logo == location.href){
					html.append('<i class="icon-inbox"></i>');
				}
				return html;
			},
			createProject:function(){
				var self = this;
				var addProjectLayerIndex;
		        self.dom.on("click",".creatOrgProject",function(event){
		        	var html = $("<div></div>");
		        	var orgid = $(this).closest("div").prevAll('h4').data("orgid");
		        	var projectSettingBootstrapLayer = $("#addProjectBootstrapLayer");
		        	projectSettingBootstrapLayer.load(context + "/project/addProject.htm",function(){
		        		projectSettingBootstrapLayer.modal('show');
		        		if(orgid){
		        			projectSettingBootstrapLayer.find("#organizationId").val(orgid);
		        		}
		        	});
		        	
		        	return false;
		        });
				
			},
			findOtherProject: function(){
				var self = this;
				$(document).on("click", function(event) {//关闭所有的浮出层
					self.dom.find(".project-state-larer").hide();
	            });
				//点击状态
				self.dom.on('click','.project-state',function(e){
					$(this).next().show();
					EventUtil.stopPropagation(e);
				});
				self.dom.find('.project-state-larer').on('click','li',function(){
					var projectStateLarer = $(this).closest('.project-state-larer');
					projectStateLarer.find('li i').hide();
					$(this).find('i').show();
					projectStateLarer.prev().html($(this).text()+'<i class="icon-down-open-big"></i>');
					projectStateLarer.hide();
					var orgId = 0;
					if(projectStateLarer.siblings("h4.goOrganization").length >0){
						orgId = $(projectStateLarer.siblings("h4.goOrganization")[0]).data('orgid');
					}
					projectStateLarer.nextAll("div").hide();
					var status = $(this).data("status");
					var projectHtml = $("<div></div>");
						projectHtml.load(context + '/project/findProjectByStatus.htm',{status:status, orgId :orgId},function(layer){
							projectStateLarer.after(layer);
						});
				});
				
			}
		};
	homeInfo.init();
});