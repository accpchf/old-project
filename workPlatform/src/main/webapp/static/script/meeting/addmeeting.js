define(["script/common/dialogAlert","jqueryForm", "jquery", "layer", "my97"], function(dialogAlert){
	var addMeetingInfo = {
			init:function(addMeetngLayer, callback){
				this.panel = addMeetngLayer;
				this.setEvent();
				this.valiArray = {};
				this.addMeetingViewInit();
				this.callback = callback;
			},
			setEvent:function(){
				var self = this;
				//修改或添加会议的提交
				this.panel.on('click', 'button.submit_btn', function(event){
					var selfBtn = $(this);
					selfBtn.prop('disabled', true);
					var options = {
						beforeSubmit:function(){
							var validate = false;
							$.each(self.valiArray, function(key, value){
								if(value && !value.checkValidate()){
									validate = true;
									return false;
								}
							});
							if(validate){
								selfBtn.prop('disabled', false);
								return false;
							}
							return true;
						},
						success:function(result){
							if(result && result.success){
								layer.alert("信息保存成功", 1, '信息', function(index){
									layer.close(index);
								});
								self.callback(result.resultData);
							}else if(result && !result.success && result.errormsg){
								dialogAlert.alert(result.errormsg,5);
//								layer.alert(result.errormsg, 1, '信息', function(index){
//									layer.close(index);
//								});
							}else{
								layer.alert('请求失败', 1);
							}
							selfBtn.prop('disabled', false);
						},
						error:function(){
							layer.alert('请求失败', 1);
							selfBtn.prop('disabled', false);
						}
					};
					selfBtn.closest('form.meeting_form').ajaxSubmit(options);
					return false;
				});
				
				//开始时间的选择
				self.panel.on('click', 'input[name="beginTimeString"]', function(){
					var  date = new Date();
					var mytime=date.toLocaleDateString()+" "+ date.getHours()+"时"+date.getMinutes()+"分"+date.getSeconds()+"秒";
					var selfInput = $(this);
					WdatePicker({
						errDealMode:1,
						dateFmt  : 'yyyy年MM月dd日  HH时mm分',
						qsEnabled : false,
						isShowOK : false,
						minDate :mytime,
						autoPickDate:true,
						onclearing:function(){
							self.panel.find('.updateEndDate').val('');
							$dp.hide();
							sendData.value = null;
							return true;
						}
					});
					return false;
				});
			},
			//会议界面初始化，主要是表单验证的添加
			addMeetingViewInit:function(){
				var self = this;
				var titleInput = this.panel.find('form.meeting_form input[name="title"]'),
					beginTimeInput = this.panel.find('form.meeting_form input[name="beginTimeString"]'),
					placeInput = this.panel.find('form.meeting_form input[name="place"]'),
					detailInput = this.panel.find('form.meeting_form textarea[name="detail"]'),
					recordInput = this.panel.find('form.meeting_form textarea[name="record"]');
				self.valiArray.title = new InputValidate(titleInput, true, 30);
				self.valiArray.beginTime = new InputValidate(beginTimeInput, true);
				self.valiArray.place = new InputValidate(placeInput, false, 30);
				self.valiArray.detail = new InputValidate(detailInput, false, 180);
				self.valiArray.record = new InputValidate(recordInput, false, 3500);
			}
	};
	return addMeetingInfo;
});