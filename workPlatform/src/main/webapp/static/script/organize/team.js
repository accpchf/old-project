require(["jquery","layer"],function(){
	var team={
			container : $("#organizationContainer")	
	};
	
	team.init=function(){
		team.options();
		team.add();
		team.user();
	};

	
	team.user=function()
	{
		var self = this;
		$("#teamList").on("click",".userList",function(){
			var id = $(this).parent().data("team-id");
			layer.closeAll();
			self.container.load(context+"/team/userList.htm?id="+id);
			return false;
		});
		 
	};
	team.add=function(){
		$("#addTeam").click(function(){
			var addFormHtml =$("<div></div>");
			addFormHtml.load(context + "/team/add.htm",function(){
				$.layer({
				    type: 1,
				    shadeClose: false,
				    title: false,
				    closeBtn: [0, false],
				    shade: [0.3, '#000'],
				    border: [0],
				    offset: ['200px',''],
				    area: ['450px', '203px'],
				    page: {html:addFormHtml.html()},
				    success:function(layero){
				    	$(layero).on("click",function(){
				    		return false;
				    	});
				    }
				}); 
			});
			return false;
		})
	},
	
	team.options=function(){
		$("#teamList").on("click",".icon-down-open-big",function(){
			var userIsExist = $(this).closest('div').find('img[data-userid = "'+ loginUserInfo.userId+'"]').length;
			layer.closeAll();
			
			var parent = $(this).parent();
			
			team.teamId = parent.data("team-id");
			team.teamName = parent.find("h4").data("team-name");
            var html = $("<div></div>");
            
            html.load(context + "/team/loadTeamMenu.htm?teamId="+team.teamId,function(){
            	$.layer({
				    type: 1,
				    shade: [0],
				    area: ['auto', '175px'],
				    title: false,
				    border: [0],
				    closeBtn: false,
				    offset:[top,left],
				    fix:false,
				    page: {html:html.html()},
					success:function(layero)
					{
						
						$(layero).on("click",".icon-cancel-circled-outline",function(){
							layer.closeAll();
						});
						
						$(layero).on("click",function(){
				    		return false;
				    	});
						
						$(layero).on("click","#update",function()
						{
							var dom = $("#layerDiv");
							dom.empty();
							dom.html(update);
						});
						
						
						$(layero).on("click","#exit",function()
						{
							var dom = $("#layerDiv");
							dom.empty();
							dom.html(exit);
							
						});
						
						$(layero).on("click","#delete",function(){
							var dom = $("#layerDiv");
							dom.empty();
							dom.html(del);
							
						});
						
						$(layero).on("click",".icon-left-open-big",function(){
							var dom = $("#layerDiv");
							dom.empty();
							dom.html(option);
							
						});
						
						$(layero).on("click","#updateBtn",function(){
							
							var inputName = $("#updateName");
							
							var valiName = new InputValidate(inputName, true, 30, 0, '', null);
							
							var params={
									teamId:team.teamId,
									name:$("#updateName").val()
							};
							
							$.ajax({
								url:context + "/team/update.htm",
								data:params,
								type:"post",
								beforeSend:function(){
									if(!valiName.checkValidate()){
											return false;
										}
										return true;
								},
								success:function(resultData)
								{
									if(resultData && resultData.success){
										layer.closeAll();
										var teamName = resultData.resultData;
										var realName ;
										if(teamName.length>10){
											realName = teamName.substring(0,10)+"...";
										}else{
											realName = teamName;
										}
										parent.find("h4").html(realName).attr("title",teamName).data("team-name",teamName);
									}else if(resultData && resultData.errormsg){
										layer.alert(resultData.errormsg, 1);
									}else{
										layer.alert('请求失败', 1);
									}
									
								}
							
							});
						});
						
						$(layero).on("click","#exitBtn",function(){
								$.ajax({
									url:context + "/team/exit.htm",
									data:{id:team.teamId},
									type:"post",
									success:function(resultData)
									{
										if(resultData && resultData.success){
											var num =parent.find(".userNum").text().replace(TEXT_CONFIG.chengyuan,'').replace('(','').replace(')','')-1;
											parent.find(".userNum").html(TEXT_CONFIG.chengyuan+'('+num+')');
											var userId = loginUserInfo.userId;
											parent.find("img[data-userid='"+userId+"']").remove();
											
											if(parent.find("div.user-portrait").children("img"))
												{
												parent.find("div.user-portrait").remove();
												}
											layer.closeAll();
										}else if(resultData && resultData.errormsg){
											layer.alert(resultData.errormsg, 1);
										}else{
											layer.alert('请求失败', 1);
										}
									}
								});
						});
						
						$(layero).on("click","#delBtn",function(){
							
							$.ajax({
								url:context + "/team/del.htm",
								data:{id:team.teamId},
								type:"post",
								success:function(resultData)
								{
									if(resultData && resultData.success){
										parent.empty();
										parent.remove("div");
										layer.closeAll();
									}else if(resultData && resultData.errormsg){
										layer.alert(resultData.errormsg, 1);
									}else{
										layer.alert('请求失败', 1);
									}
								}
							});
					});
						
						
						
						
						
						var option=
							"<div class='layer-lg boardBackground floatLayer'>"+
							"<div class='layer-title'>"+
								    	"<h5>"+TEXT_CONFIG.tuandui+"菜单</h5>"+
								    	"<i class='icon-cancel-circled-outline'></i>"+
								    	"</div>"+
								    	"<ul>"+
								    	"<li><a id='update'> <i class='icon-pencil'></i>编辑"+TEXT_CONFIG.tuandui+"名称</a></li>"+
								    	"<li><a id='exit'> <i class='icon-user-outline'></i>退出"+TEXT_CONFIG.tuandui+"</a></li>"+
								    	"<li><a id='delete'> <i class='icon-trash'></i>删除"+TEXT_CONFIG.tuandui+"</a></li>"+
								    	"</ul>"+
								    	"</div>"
						var update=
							'<div class="layer-lg boardBackground floatLayer">'+
							'<div class="layer-title">'+
					    '<h5>编辑'+TEXT_CONFIG.tuandui+'名称</h5>'+
					    '<i class=" icon-left-open-big"></i>'+
					    '<i class="icon-cancel-circled-outline"></i>'+
					'</div>'+
					'<ul>'+
					    '<li><input id="updateName" class=" form-control" type="text" value='+team.teamName+' placeholder="团队名称"></li>'+
					    '<li><button id="updateBtn" class="btn green btn-common">保存</button></li>'+
					'</ul>'+
					'</div>';
						
						var exit=
							'<div class="layer-lg boardBackground floatLayer">'+
					    '<div class="layer-title">'+
					    '<h5>退出'+TEXT_CONFIG.tuandui+'</h5>'+
					    '<i class=" icon-left-open-big"></i>'+
					    '<i class="icon-cancel-circled-outline"></i>'+
					'</div>'+
					'<ul>'+
					    '<li>你确定要退出这个'+TEXT_CONFIG.tuandui+'吗？</li>'+
					    '<li><button id="exitBtn" class="btn red btn-common">退出</button></li>'+
					'</ul>'+
					'</div>';

						var del=
							'<div class="layer-lg boardBackground floatLayer">'+
					    '<div class="layer-title">'+
					    '<h5>删除'+TEXT_CONFIG.tuandui+'</h5>'+
					        '<i class=" icon-left-open-big"></i>'+
					        '<i class="icon-cancel-circled-outline"></i>'+
					'</div>'+
					'<ul>'+
					    '<li>你确定要删除这个'+TEXT_CONFIG.tuandui+'吗？</li>'+
					    '<li><button id="delBtn" class="btn red btn-common">删除</button></li>'+
					'</ul>'+
					'</div>';
					
					}
					
			});
			return false;
            });
//			if(userIsExist <= 0){
//				html1 = html1+html3;
//			}else{
//				html1 = html1+html2+html3;
//			}
			
			var top = ($(this).offset().top - document.body.scrollTop)+"px";
			var left = $(this).offset().left+"px";	
		});
	}
	
	
	
	team.layerResize=function(dom) { //浏览器大小变化时弹出层位置变化
		$(window).resize(function() {
			var top = dom.offset().top-8+"px";
			var left = dom.offset().left+25+"px";
			  return { top: top,
                  left : left
          };
		})
	};

	
	team.init();
});