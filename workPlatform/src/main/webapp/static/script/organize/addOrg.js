require(["jquery","layer"],function(){

	var add = {
			addOrgPage:$('#addOrgPage')
	};
	
	add.init=function()
	{
		add.setEvent();
	};
	add.setEvent=function()
	{	
		var self = this;
			var inputName = self.addOrgPage.find('#orgName'),
			inputDes = self.addOrgPage.find('#orgDescription'),
			inputCon = self.addOrgPage.find('#orgContacter'),
			inputPhone = self.addOrgPage.find('#orgPhone');
			var valiArray={
				valiName:new InputValidate(inputName, true, 30, 0, '', null),
				valiDes:new InputValidate(inputDes, false,500,0,'',null),
				valiCon:new InputValidate(inputCon, false,30,0,'',null),
				valiPhone:new InputValidate(inputPhone,false,15,0,'',null)
			};
			
		self.addOrgPage.on('click','#orgAddBtn',function(){
			var params={
					name:self.addOrgPage.find('#orgName').val().trim(),
					description:self.addOrgPage.find('#orgDescription').val(),
					contacter:self.addOrgPage.find('#orgContacter').val().trim(),
					phone:self.addOrgPage.find('#orgPhone').val().trim()
				};
			$.ajax({
				url:context+ '/org/create.htm',
				type:'post',
				data:params,
				beforeSend:function()
				{
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
					if(resultData && resultData.success){
						layer.closeAll();
						window.open(context + "/org/"+resultData.resultData+"/organization.htm","_parent")
					}else if(resultData && resultData.errormsg){
						layer.alert(resultData.errormsg, 1);
					}else{
						layer.alert('请求失败', 1);
					}
				}
				
			});
		});
		self.addOrgPage.find(".icon-cancel-circled").on("click",function(){
			layer.closeAll();
	    })
	};
	
	add.init();
});

