require(["jquery","layer"],function() {
	var user = {};
	var organizationUserDiv = $("#organize-wrap-left");
	organizationUserDiv.find("div[data-user-id="+loginUserInfo.userId+"]").find(".icon-down-open-big").hide();
	user.init = function() {
		
		user.options();
		
		$.ajax({
			url : context + "/org/getCode.htm",
			type : "post",
			data : {
				id : $("#id").val()
			},
			success : function(data) {
				if(data!="")
					{
						$("#linksInvite").val(data);
					}
				if(data=="")
					{
						$(".open-link").show();
						$(".close-link").hide();
					}
			}
		});
		

		
		
		$("#account").on("keydown",function(e){
			if(e.which==13)
				{
				
					 var params={
							 orgId:$("#id").val(),
							 account:$("#account").val()
					 			
					 }
				
					 $.ajax({
						 url:context + "/org/addUserByEmail.htm",
						 data:params,
						 type:"post",
						 success:function(resultData)
						 {
							 if(resultData && resultData.success){
								 	var name = resultData.resultData.name;
								    if(name.length > 4){
								    	name = name.substr(0,4)+"...";
								    }
								 	$("#account").val("");
									var dom = $(".organize-wrap-left");
									dom.append(
											'<div class="user-block corner-6" data-user-id='+resultData.resultData.userId+'>'+
											'<img src="'+resultData.resultData.logo+'" class="img-circle"  />'+
											'<h4 title="'+resultData.resultData.name+'">'+name+'</h4>'+
											'<i class="icon-down-open-big"></i>'+
											'</div>');
								}else if(resultData && resultData.errormsg){
									layer.alert(resultData.errormsg, 1);
								}else{
									layer.alert('请求失败', 1);
								}
						 }
					 
					 })
				}
		})
		
		
		$('#open').click(function() {
			$.ajax({
				url : context + "/org/encrypt.htm",
				type : "post",
				data : {
					id : $("#id").val(),
					type : "open"
				},
				success : function(resultData) {
					if(resultData && resultData.success){
						$(".open-link").hide();
						$(".close-link").show();
						$("#linksInvite").val(resultData.resultData);
					}else if(resultData && resultData.errormsg){
						layer.alert(resultData.errormsg, 1);
					}else{
						layer.alert('请求失败', 1);
					}
				}
			});
		});

		$('#close').click(function() {
			$.ajax({
				url : context + "/org/encrypt.htm",
				type : "post",
				data : {
					id : $("#id").val(),
					type : "close"
				},
				success : function(data) {
					$(".open-link").show();
					$(".close-link").hide();
				}
			});
		});
	}
	
	user.options=function(){
		var dom = $(".organize-wrap-left");
		
		dom.on("click",".icon-down-open-big",function(){
			
			layer.closeAll();
			
			var parent = $(this).parent();
			user.id = parent.data("user-id");
			user.account = parent.data("account");
			user.active = parent.data("user-active");
			var active = (user.active==true)?"none;":"block;";
			var top = ($(this).offset().top - document.body.scrollTop)+"px";
			var left = $(this).offset().left+"px";
			if(!parent.children().hasClass("owner-mark"))
				{
					$.layer({
					    type: 1,
					    shade: [0],
					    area: ['auto', '176px'],
					    title: false,
					    border: [0],
					    closeBtn: false,
					    offset:[top,left],
					    fix:false,
					    page: {html:"<div id='layerDiv'>"+  
					    	"<div class='layer-lg boardBackground floatLayer'>"+
					        "<div class='layer-title'>"+
				        	"<h5>"+TEXT_CONFIG.chengyuan+"菜单</h5>"+
				            "<i class='icon-cancel-circled-outline'></i>"+
				        "</div>"+
				    	"<ul>"+
				            "<li><a id='admin'> <i class='icon-user-outline'></i>设为管理员</a></li>"+
				            "<li><a id='setAdministrator'> <i class='icon-user-outline'></i>设为监督员</a></li>"+
				            "<li style='display:"+active+"'><a id='resetPd'> <i class='icon-user-outline'></i>重置登录密码</a></li>"+
				            "<li><a id='delete'> <i class='icon-trash'></i>移除出"+TEXT_CONFIG.zuzhi+"</a></li>"+
				        "</ul>"+
				    "</div>"+
				    "</div>"},
				    	success:function(layero)
				    	{
				    		$(layero).click(function(){
				    			return false;
				    		});
				    		
				    		$(layero).on("click",".icon-cancel-circled-outline",function(){
				    			layer.closeAll();
				    		});
				    		
				    		$(layero).on("click","#admin",function(){
				    			$.ajax({
				    				url:context + "/org/setAdmin.htm",
				    				data:{orgId:$("#id").val(),userId:user.id},
				    				type:"post",
				    				success:function(resultData)
				    				{
				    					if(resultData && resultData.success){
				    						layer.closeAll();
				    						parent.append('<a class="owner-mark " title="管理员"><i class="icon-crown"></i></a>');
				    					}else if(resultData && resultData.errormsg){
				    						layer.alert(resultData.errormsg, 1);
				    					}else{
				    						layer.alert('请求失败', 1);
				    					}
				    				}
				    			})
				    		});
				    		$(layero).on("click","#setAdministrator",function(){
				    			$.ajax({
				    				url:context + "/org/setAdministrator.htm",
				    				data:{orgId:$("#id").val(),userId:user.id},
				    				type:"post",
				    				success:function(resultData)
				    				{
				    					if(resultData && resultData.success){
				    						layer.closeAll();
				    						parent.append('<a class="administrator "></a>');
				    						parent.append('<a class="owner-mark " title="监督员"><i class=" icon-user-1"></i></a>');
				    					}else if(resultData && resultData.errormsg){
				    						layer.alert(resultData.errormsg, 1);
				    					}else{
				    						layer.alert('请求失败', 1);
				    					}
				    				}
				    			})
				    		});
				    		
				    		$(layero).on("click","#delete",function(){
				    			var dom = $("#layerDiv");
								dom.empty();
								dom.html(del);
				    		});
				    		
				    		$(layero).on("click",".icon-left-open-big",function(){
								var dom = $("#layerDiv");
								dom.empty();
								dom.html(options);
								
							});
				    		
				    		$(layero).on("click","#resetPd",function(){
				    			var password = Math.round(900000*Math.random()+100000);
				    			var encryptedPassword = hex_hmac_md5(user.account,password+"");
				    			$.ajax({
				    				url:context + "/org/resetPassword.htm",
				    				data:{userId:user.id, password:password, encryptedPassword:encryptedPassword},
				    				type:"post",
				    				success:function(resultData){
				    					if(resultData && resultData.success){
				    						layer.alert("重置密码成功，已发送邮件到您的邮箱",1);
				    					}else{
				    						layer.alert("请求失败",1);
				    					}
				    				}
				    			})
				    		});
				    		
				    		$(layero).on("click","#delUser",function(){
				    			$.ajax({
				    				url:context + "/org/delUser.htm",
				    				data:{orgId:$("#id").val(),userId:user.id},
				    				type:"post",
				    				success:function(resultData)
				    				{
				    					if(resultData && resultData.success){
				    						layer.closeAll();
				    						parent.empty();
											parent.remove("div");
				    					}else if(resultData && resultData.errormsg){
				    						layer.alert(resultData.errormsg, 1);
				    					}else{
				    						layer.alert('请求失败', 1);
				    					}
				    				}
				    				
				    			});
				    			return false;
				    		});
				    		
				    		var options = "<div id='layerDiv'>"+  
					    	"<div class='layer-lg boardBackground floatLayer'>"+
					        "<div class='layer-title'>"+
				        	"<h5>"+TEXT_CONFIG.chengyuan+"菜单</h5>"+
				            "<i class='icon-cancel-circled-outline'></i>"+
				        "</div>"+
				    	"<ul>"+
				            "<li><a id='admin'> <i class='icon-user-outline'></i>设为管理员</a></li>"+
				            "<li><a id='setAdministrator'> <i class='icon-user-outline'></i>设为监督员</a></li>"+
				            "<li style='display:"+active+"'><a id='resetPd'> <i class='icon-user-outline'></i>重置登录密码</a></li>"+
				            "<li><a id='delete'> <i class='icon-trash'></i>移除出"+TEXT_CONFIG.zuzhi+"</a></li>"+
				        "</ul>"+
				    "</div>"+
				    "</div>";
				    		
				    		var del='<div class="layer-lg boardBackground floatLayer ">'+
				            			'<div class="layer-title">'+
				            			'<h5>移除'+TEXT_CONFIG.chengyuan+'</h5>'+
				            			'<i class=" icon-left-open-big"></i>'+
				            			'<i class="icon-cancel-circled-outline"></i>'+
				            			'</div>'+
				            			'<ul>'+
				            			'<li>确认移除该'+TEXT_CONFIG.chengyuan+'？</li>'+
				            			'<li><button id="delUser" class="btn red btn-common">确认移除</button></li>'+
				            			'</ul>'+
				            			'</div>';
				    	}
					});
				}
			else if(parent.find('i.icon-user-1').length > 0) {
				user.active = parent.data("user-active");
				var active = (user.active==true)?"none;":"block;";
				$.layer({
					type: 1,
				    shade: [0],
				    area: ['auto', 'auto'],
				    title: false,
				    border: [0],
				    closeBtn: false,
				    offset:[top,left],
				    fix:false,
				    page: {html:"<div id='layerDiv'>"+  
				    	"<div class='layer-lg boardBackground floatLayer'>"+
				        "<div class='layer-title'>"+
			        	"<h5>"+TEXT_CONFIG.chengyuan+"菜单</h5>"+
			            "<i class='icon-cancel-circled-outline'></i>"+
			        "</div>"+
			    	"<ul>"+
			    		"<li style='display:"+active+"'><a id='resetPd'> <i class='icon-user-outline'></i>重置登录密码</a></li>"+
			            "<li><a id='cancelSuperviser'> <i class='icon-user-outline'></i>取消监督员</a></li>"+
			        "</ul>"+
			    "</div>"+
			    "</div>"},
			    	success:function(layero)
			    	{
			    		$(layero).click(function(){
			    			return false;
			    		});
			    		
			    		$(layero).on("click",".icon-cancel-circled-outline",function(){
			    			layer.closeAll();
			    		});
			    		
			    		$(layero).on("click","#cancelSuperviser",function(){
			    			$.ajax({
			    				url:context + "/org/removeAdministrator.htm",
			    				data:{orgId:$("#id").val(),userId:user.id},
			    				type:"post",
			    				success:function(resultData)
			    				{
			    					if(resultData && resultData.success){
			    						layer.closeAll();
			    						parent.find("a").remove();
			    					}else if(resultData && resultData.errormsg){
			    						layer.alert(resultData.errormsg, 1);
			    					}else{
			    						layer.alert('请求失败', 1);
			    					}
			    				}
			    			})
			    		});
			    	}
			    
				});
			}else{
				user.active = parent.data("user-active");
				var active = (user.active==true)?"none;":"block;";
				$.layer({
					type: 1,
				    shade: [0],
				    area: ['auto', 'auto'],
				    title: false,
				    border: [0],
				    closeBtn: false,
				    offset:[top,left],
				    fix:false,
				    page: {html:"<div id='layerDiv'>"+  
				    	"<div class='layer-lg boardBackground floatLayer'>"+
				        "<div class='layer-title'>"+
			        	"<h5>"+TEXT_CONFIG.chengyuan+"菜单</h5>"+
			            "<i class='icon-cancel-circled-outline'></i>"+
			        "</div>"+
			    	"<ul>"+
			    		"<li style='display:"+active+"'><a id='resetPd'> <i class='icon-user-outline'></i>重置登录密码</a></li>"+
			            "<li><a id='cancel'> <i class='icon-user-outline'></i>取消管理员</a></li>"+
			        "</ul>"+
			    "</div>"+
			    "</div>"},
			    	success:function(layero)
			    	{
			    		$(layero).click(function(){
			    			return false;
			    		});
			    		
			    		$(layero).on("click",".icon-cancel-circled-outline",function(){
			    			layer.closeAll();
			    		});
			    		
			    		$(layero).on("click","#cancel",function(){
			    			$.ajax({
			    				url:context + "/org/cancelAdmin.htm",
			    				data:{orgId:$("#id").val(),userId:user.id},
			    				type:"post",
			    				success:function(resultData)
			    				{
			    					if(resultData && resultData.success){
			    						layer.closeAll();
			    						parent.find("a").empty();
			    						parent.find("a").remove();
			    					}else if(resultData && resultData.errormsg){
			    						layer.alert(resultData.errormsg, 1);
			    					}else{
			    						layer.alert('请求失败', 1);
			    					}
			    				}
			    			})
			    		});
			    	}
			    
				});
				}
			return false;
		})
			
	}
		

	user.init();

});