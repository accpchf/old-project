require(["jquery"],function(){
	var team={
			dom:$(".team-block corner-6"),
			container : $("#organizationContainer"),
			init:function(){
				this.setEvent();
			},
			setEvent:function(){
				var self = this;
				$("#exit").click(function(){
					layer.closeAll();
				});
				$("#add").click(function(){
					var params={
							id:$("#id").val(),
							name:$("#name").val()
					};
					
					var inputName = $("#name");
					
					var valiName = new InputValidate(inputName, true, 30, 0, '', null); 
					
					$.ajax({
						url: context+"/team/create.htm",
						type: 'post',
						data: params,
						beforeSend:function(){
							if(!valiName.checkValidate()){
									return false;
								}
								return true;
						},
						success:function(resultData){
							if(resultData && resultData.success){
								/*var dom = $("#teamList");
								dom.append('<div class="team-block corner-6" data-team-id="'+resultData.resultData.team.teamId+'">'+
										'<a class="userList"><h4 class="select" data-team-name='+resultData.resultData.team.name+'>'+$("#name").val()+'</h4> <span class="userNum">'+TEXT_CONFIG.chengyuan+'(1)</span><span>'+TEXT_CONFIG.xiangmu+'(0)</span> </a>'+ 
										' <div class="user-portrait corner-3">'+
										'<img title="'+loginUserInfo.name+'" data-userid='+loginUserInfo.userId+' src='+resultData.resultData.logo+' class="img-circle"/>'+
										'</div>'+
									'<i id="btn" class="icon-down-open-big"></i>'+
								'</div>');*/
								layer.closeAll();
								self.container.load(context+"/team/list.htm?id="+$("#id").val());
							}else if(resultData && resultData.errormsg){
								layer.alert(resultData.errormsg, 1);
							}else{
								layer.alert('请求失败', 1);
							}
						}
					});
				});
			}
	};
	team.init();
});
