require(['jquery',"script/common/dialogAlert",'jqueryUI', 'jqueryForm', 'layer','my97'], function($,dialogAlert){
	var addProjectHtml;
	
	var global_edition = TEXT_CONFIG.global_edition.toLowerCase();
	if(global_edition == "enterprice") {
		$("#selectOrganization").hide();
		$("#myselfOpt").remove();
	}
	
	addProjectHtml = {
			//页面初始化
			init:function(){
				//添加项目信息初始化
				this.addProjectPage = $("#addProject");
				this.proInfoValiArray = {};
//				this.addProjectPageInit();
				this.addProject();
			},
//			//添加项目信息初始化
//			addProjectPageInit:function(){
//				var self = this;
//				var	ownerSelect = self.addProjectPage.find("select[name='organizationId']");
//				$.ajax({   
//					type: "post",       
//					url: context + "/org/initOrgs.htm", 
//					success: function (result) { 
//						if(result && result.success){
//							var data = result.resultData;
//							//console.log(data)
//							$.each(data, function(key, value){
//								if(self.addProjectPage.find("#orgId").val()==value.id){
//									ownerSelect.find("option:selected").val(value.id).html(value.name);
//								}else{
//									var $optionHtml = $("<option/>").val(value.id).html(value.name);
//									ownerSelect.append($optionHtml);
//								}
//								
//							});
//							if(self.addProjectPage.find("#orgId").val() > 0){
//								ownerSelect.append('<option value="0">我自己</option>');
//							}
//							
//						}
//					}, 
//					error: function (errorThrown) {  
//				    } 
//				});  
//			},
			addProject:function(){
				var self = this,
					addLogo = $("#addlogo",self.addProjectPage),
//					logoFile = $("#logoFile",self.addProjectPage),
				    projectName = $("#projectName", self.addProjectPage),
					description = $("#project_description", self.addProjectPage),
					image = $(".pull-left",self.addProjectPage),
					addProjectBtn = $("#add_project_btn", self.addProjectPage);
				if(!self.proInfoValiArray.name){
					self.proInfoValiArray.name = new InputValidate(projectName, true, 30, 0, 'text', null);
				}else{
					self.proInfoValiArray.name.target.removeClass("error");
				}
				if(!self.proInfoValiArray.description){
					self.proInfoValiArray.description = new InputValidate(description, false, 500, 0, '', null);
				}else{
					self.proInfoValiArray.description.target.removeClass("error");
				}
				var imgId="";
				$(".readyimg").each(function(){
					$(this).click(function(){
						var id = $(this);
						imgId=$(this).attr("id");
						$("#imgId").val(imgId);
						image.attr("src",$(this).attr("src"));
						
						$(".readyimg").each(function(){
							if($(this).hasClass("select"))
							{
								$(this).removeClass("select");
							}
							id.addClass("select");
						});
					});
				});
				
				
				addLogo.on("click", function(){
					$("#logoFile",self.addProjectPage).click();
					$("#logoFile",self.addProjectPage).change(function(e){
						var $self = $(this);
				    	var files = this.files;
				    	if(files.length != 0){
				    		var file = files[0],
				    		reader = new FileReader();
				    		if(!reader){
				    			dialogAlert.alert('该浏览器不支持头像上传');
				    			return;
				    		}
				    		var rules = new RegExp('image/(png|jpeg|jpg|gif|img|)', 'g');
				    		if(!rules.test(file.type)){
				    			dialogAlert.alert('请选择正确格式的图片');
				    			$self.after($self.clone(true).val(''));
				    			$self.remove();
				    			return;
				    		}
				    		if(file.limitSize('65', 'KB')){
				    			$self.after($self.clone(true).val(''));
				    			$self.remove();
				    			dialogAlert.alert('图片太大，应65KB以内');
				    			return;
				    		}
				    		reader.onload = function(event){
				    			var target = EventUtil.getTarget(event);
				    			$('#projectLogo').prop('src', target.result);
				    		};
				    		reader.readAsDataURL(file);
				    		}
				    	$(".readyimg").each(function(){
							if($(this).hasClass("select"))
							{
								$(this).removeClass("select");
							}
						});
			       });
				});
				addProjectBtn.on("click", function(){
					addProjectBtn.prop('disabled', true);
					var options = {
							beforeSubmit : function() {
								var validate = false;
								$.each(self.proInfoValiArray, function(key, value){
									if(!value.checkValidate()){
										validate = true;
										return false;
									}
								});
								if(validate){
									addProjectBtn.prop('disabled', false);
									return false;
								}
								return true;
						    },
					          success : function(resultData){
									if(resultData && resultData.success){
										window.location = context + "/project/home.htm";
									}else if(resultData && !resultData.success){
										//alert("---");
										layer.alert(resultData.errormsg, 1);
									}else{
										//console.log(resultData);
										layer.alert(resultData, 1);
									};
								},
								error:function(){
									layer.alert('请求失败', 1);
								},  
					         resetForm:true  
				        };
					 $('#addProjectForm',self.addProjectPage).ajaxSubmit(options);
					 return false;
				});
			}
	};
	addProjectHtml.init();
});