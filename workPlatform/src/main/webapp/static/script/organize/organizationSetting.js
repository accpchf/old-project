require(["jquery", "layer" ], function() {
	var method = {
			panel : $('#orgSetting')
	};

	
	method.pic =  function() {
		$("#pic").click(function(){
			$("#input_pic_upload").click();
			
			$("#input_pic_upload").change(function(event){
				var $self = $(this);
		    	var files = this.files;
		    	if(files.length != 0){
		    		var file = files[0],
		    		reader = new FileReader();
		    		if(!reader){
		    			layer.alert('该浏览器不支持头像上传');
		    			return;
		    		}
		    		var rules = new RegExp('image/(png|jpeg|jpg|gif|)', 'g');
		    		if(!rules.test(file.type)){
		    			layer.alert('请选择正确格式的图片');
		    			$self.after($self.clone(true).val(''));
		    			$self.remove();
		    			return;
		    		}
		    		if(file.limitSize('1', 'MB')){
		    			$self.after($self.clone(true).val(''));
		    			$self.remove();
		    			layer.alert('头像太大，应1MB以内');
		    			return;
		    		}
		    		reader.onload = function(event){
		    			var target = EventUtil.getTarget(event);
		    			$("#pic").prop('src', target.result);
		    		};
		    		reader.readAsDataURL(file);
		    		}
			});
			return false;
		});
	};
	
	method.init = function() {
		var self = this;
		var path = $("#pic").attr("src");
		
		$(".icon-pencil").click(function() {
			if (!$(this).hasClass("selected")) {
				method.pic();
				$('#inputName').removeAttr("disabled");
				$('#inputContacter').removeAttr("disabled");
				$('#inputPhone').removeAttr("disabled");
				$('#inputDescription').removeAttr("disabled");
				$('#OrgBtn').removeAttr("disabled");
				$(this).addClass("selected");
				$("#input_pic_upload").attr("src",$("#pic").attr("src"));
			} else {
				$('#pic').unbind("click");
				$('#inputContacter').attr("disabled",true);
				$('#inputPhone').attr("disabled",true);
				$('#inputName').attr("disabled", true);
				$('#inputDescription').attr("disabled", true);
				$('#OrgBtn').attr("disabled", true);
				$(this).removeClass("selected");
				$("#pic").attr("src",path);
				$("#input_pic_upload").attr("src",$("#pic").attr("src"));
			}
		});
		
		
		
		$("#exit").click(function(){
			if(confirm("确定要退出该"+TEXT_CONFIG.zuzhi+"吗"))
				{
				$.ajax({
					url:context + "/org/exit.htm",
					data:{orgId:$("#id").val()},
					type:'post',
					success:function(resultData){
						if(resultData&&resultData.success){
							window.location=context+"/project/home.htm";
						}
						else if(result && !result.success && result.errormsg){
							layer.alert(result.errormsg);
						}else{
							layer.alert('请求失败', 1);
						}
					}
				});
				}
		});

		self.panel.on('click','#deleteOrg',function(){
			$.ajax({
				url:context + "/org/deleteOrg.htm",
				data:{orgId:$("#id").val()},
				type:'post',
				success:function(resultData){
					if(resultData&&resultData.success){
						window.location=context+"/project/home.htm";
					}
					else if(result && !result.success && result.errormsg){
						layer.alert(result.errormsg);
					}else{
						layer.alert('请求失败', 1);
					}
				}
			});
		});
		var name = $("#inputName");
				var des = $("#inputDescription");
				var phone = $("#inputPhone");
				var contactor = $("#inputContacter");
				
				var valiArray={
					valiName:new InputValidate(name, true, 30, 0, '', null),
					valiDes:new InputValidate(des, false, 500, 0, '', null),
					valiPhone:new InputValidate(phone,true,15,0,'',null),
					valiContactor:new InputValidate(contactor,true,30,0,'',null)
				};
		$("#OrgBtn").click(function() {
			
				
			
				var selfForm = $(this).closest("form");
				var dataParam = new FormData(selfForm[0]);
				
				$.ajax({
					type:'post',
					url: context + "/org/modify.htm",
					data:dataParam,
					contentType:false,  
			        processData:false,
			        beforeSend:function(){
			        	var validate = false;
						
						$.each(valiArray, function(key, value){
							if(!value.checkValidate()){
								validate = true;
								return false;
							}
						});
						if(validate){
							$(this).prop('disabled', false);
							return false;
						}
						return true;
			        },
					success:function(resultData)
					{
						if(resultData && resultData.success)
							{
							
								$("div.organize-logo").find("h4").html(resultData.resultData.name);
								$("div.organize-logo").find("img").attr("src",resultData.resultData.logo);
								
								$("#inputName").val(resultData.resultData.name);
								$("#inputDescription").val(resultData.resultData.description);
								$('#inputPhone').val(resultData.resultData.phone);
								$('#inputContacter').val(resultData.resultData.contacter);
								
								$('#pic').unbind("click");
								$('#inputPhone').attr("disabled",true);
								$('#inputContacter').attr("disabled",true);
								$('#inputName').attr("disabled", true);
								$('#inputDescription').attr("disabled", true);
								$('#OrgBtn').attr("disabled", true);
								$(this).removeClass("selected");
								$("#input_pic_upload").attr("src",$("#pic").attr("src"));
							}
						else if(result && !result.success && result.errormsg){
							layer.alert(result.errormsg);
						}else{
							layer.alert('请求失败', 1);
						}
					}
				});
				
				return false;
		});
	};


	method.init();

});