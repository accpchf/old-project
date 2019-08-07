require(['script/common/personList','script/meeting/addmeeting', "jqueryForm", "jquery", "layer"], function(personList, addmeeting){
	var meetingInfo = {
		init:function(){
			this.panel = $(".meeting_content");
			this.projectId = this.panel.closest("div[data-id='projectTemplate']").data("projectId");
		
			this.viewInit();
			this.setEvent();
		},
		viewInit:function(){
			var self = this;
			$.each(self.panel.find('textarea.hidden_overflow'), function(key, value){
				$(this).css("height",this.scrollHeight + 'px');
			});
		},
		setEvent:function(){
			var self = this;
			//删除参与者或添加参与者操作的事件类型
			var eventType = "eventSelectUser",
			users = window.projectUserList;
			//添加参与者
			self.panel.on('click','ul li.add_user_li', function(){
				var arr =[];
	        	$(this).prevAll("li").each(function(){
	        		arr.push($(this).data("id").toString());
	        	});
				personList.init({
	                searchInput:true,//是否需要搜索框
	                data : users,
	                claiming : false,//是否先是待认领,要选中未认领时，selectData有一个值为0就可以了
	                selectData : arr,//要选中的数据
	                dom : $(this),
	                container : $(this),
	                isClose : false,
	                eventType:eventType
	            });
				return false;
			});
			//在参与者弹出框里选择或者取消某参与者时触发的事件
			self.panel.on(eventType,'ul li.add_user_li', function(event, params){
				var options = {
					operatorType:params && params.isSelect,
					ids:params && params.ids,
					meetingId:$(this).closest('div.weekly-project').attr('data-meetingId'),
					callback:params && params.callback
				};
				self.operatorJoninUser(options);
			});
			//删除参与者
			self.panel.on("click","a.remove-member-handler", function(event){//参与者人员删除
				layer.closeAll();
				var selfLi = this,
					ids = [];
				ids.push($(selfLi).closest('li').data('id'));
				var	options = {
						operatorType:false,
						ids:ids,
						meetingId:$(selfLi).closest('div.weekly-project').attr('data-meetingId'),
						callback:function(){
							$(selfLi).closest("li").remove();
						}
					};
				self.operatorJoninUser(options);
				return false;
	        });
			
			//删除附件
			self.panel.on('click', 'i.delete_adjunct_i', function(){
				var selfI = this;
				$.ajax({
					type:'post',
					url:context + '/meeting/deleteadjunct.htm',
					data:{fileUrl:$(selfI).closest('span').attr('data-fileurl'), projectId:self.projectId}
				}).done(function(resultData){
					if(resultData && resultData.success){
						$(selfI).closest('span').remove();
					}else if(resultData && !resultData.success && resultData.errormsg){
						layer.alert(resultData.errormsg, 1);
					}else{
						layer.alert('请求失败', 1);
					};
				}).fail(function(){
					layer.alert('请求失败', 1);
				});
				return false;
			});
			
			//点击添加附件按钮触发文件选择input
			self.panel.on('click','a.add_adjunct_a', function(){
				//给meetinginput赋值并祝福fileinput弹出文件选择框
				$('file_meeting_project_id_hidden').val(this.projectId);
				self.panel.find('form.file_upload_form').find('input.file_meetingid_input').attr('value', $(this).closest('div.weekly-project').attr('data-meetingId')).end()
														.find('input.file_upload_input').click();
			});
			
			//file input值改变时(即选择了文件)，触发的事件 
			self.panel.on('change', 'input.file_upload_input', function(){
				var selfInput = this,
		    		files = selfInput.files,
		    		options = {
			    		beforeSubmit:function(){
			    			if (!files || files.length > 0) {
			    				if(files[0].limitSize(500, 'MB')){
			    					layer.alert('附件太大，无法上传，请限制500MB以内');
			    					//把当前的fileinput置为初始
			    					$(selfInput).after($(selfInput).clone(true).val(''));
			    					$(selfInput).remove();
			    					return false;
			    				}
			    				if(files[0].name.length > 80){
			    					layer.alert('附件名字过长，应为80以内，请修改后再上传');
			    					//把当前的fileinput置为初始
			    					$(selfInput).after($(selfInput).clone(true).val(''));
			    					$(selfInput).remove();
			    					return false;
			    				}
			    				return true;
			    			}; 
			    			return false;
			    		},
			    		success:function(resultData){
			    			if(resultData && resultData.success){
			    				var value = resultData.resultData,
			    					meetingId = $(selfInput).siblings('input.file_meetingid_input:first').val(),
			    					$adjunctManage = self.panel.find('div.weekly-project[data-meetingId="'+ meetingId+'"] li.adjunct_manage');
			    				var $adjunctHtml = $('<span data-fileurl="'+ value.fileUrl +'"><a><i class=" icon-doc-text-1"></i>'+ value.fileName +'</a> <i class="icon-cancel-circled delete_adjunct_i" title="删除"></i></span>');
								$adjunctManage.append($adjunctHtml);
							}else if(resultData && !resultData.success && resultData.errormsg){
								layer.alert(resultData.errormsg, 1);
							}else{
								layer.alert('请求失败', 1);
							};
			    		},
			    		error:function(result){
			    			layer.alert('请求失败', 1);
			    		},
			    		complete:function(){
			    			//把当前的fileinput置为初始
			    			$(selfInput).after($(selfInput).clone(true).val(''));
			    			$(selfInput).remove();
			    		}
		    		};
		    	$(selfInput).closest('form.file_upload_form').ajaxSubmit(options);
		    	return false;
			});
			
			//下载附件
			self.panel.on('click', 'li.adjunct_manage span', function(){
				var $form = self.panel.find('form.file_download_form');
				$form.find('input[name="fileUrl"]').attr('value', $(this).attr('data-fileurl'));
				$form.find('input[name="remove_cache"]').attr('value', (new Date()).getTime());
				$form.submit().before($form.clone(true).val("")).remove();
			});
			
			
			//点击新增会议按钮 或者点击编辑会议按钮
			self.panel.on('click', '#add_meeting_btn, i.icon-pencil', function(){
				var html = $("<div></div>"),
					meetingId = -1;
				if($(this).hasClass('icon-pencil')){
					meetingId = $(this).closest('div.weekly-project').attr('data-meetingId');
				}
		        html.load(context + "/meeting/toAddMeeting.htm", {projectId:self.projectId, meetingId: meetingId}, function(){
		        	var addMeetingLayerIndex = -1;
		          	addMeetingLayerIndex = $.layer({
			            type: 1,
			            title: false,
			            area: ['auto', '500px'],
			            border: [0], //去掉默认边框
			            shade: [0.8, '#000'],
			            closeBtn: [0, false], //去掉默认关闭按钮
			            shift: ['top', 500, true], //从左动画弹出
			            offset:['50px',''],
			            page: {
			                html: html.html()
			            },
			            success: function(addMeetngLayer){
			              $(addMeetngLayer).on('click', function(event){
			            	  return false;
			              });
			              $(addMeetngLayer).on("click",".icon-cancel-circled",function(){
			                layer.close(addMeetingLayerIndex);
			              });
			              addmeeting.init(addMeetngLayer, function(meeting){
			            	  //self.addMeetingToView(meeting);
			            	  self.panel.closest("div[data-id='projectTemplate']").empty().load(context + "/meeting/loadmeetingview.htm", {projectId:self.projectId});
			              });
			            }
		          	});
		        });
		        return false;
			});
			
			//点击删除会议按钮
			self.panel.on('click', 'i.icon-trash-empty', function(){
				var meetingId = $(this).closest('div.weekly-project').attr('data-meetingId');
				layer.confirm('确定删除吗', function(index){
					layer.close(index);
					$.ajax({
						url:context + '/meeting/deletemeeting.htm',
						type:'post',
						data:{meetingId:meetingId}
					}).done(function(resultData){
						if(resultData && resultData.success){
							layer.alert("删除成功",1);
							self.panel.closest("div[data-id='projectTemplate']").empty().load(context + "/meeting/loadmeetingview.htm", {projectId:self.projectId});
						}else if(resultData && !resultData.success && resultData.errormsg){
							layer.alert(resultData.errormsg, 1);
						}else{
							layer.alert('请求失败', 1);
						};
					}).fail(function(){
						layer.alert('请求失败', 1);
					});
				},function(index){
					layer.close(index);
				});
				return false;
			});
			var record = "";
			//点击编辑纪要按钮
			self.panel.on('click', 'div.weekly-project button.edit_record', function(){
				var $selfBtn = $(this),
					$textRecord = $selfBtn.closest('div.weekly-project').find('textarea.update_record');
				$textRecord.removeClass('transparent').addClass('form-control').prop('disabled', '');
				$selfBtn.removeClass('edit_record').addClass('save_record').empty().html('保存纪要');
				$textRecord.trigger('change');
				record = new InputValidate($textRecord, false, 3500);
				return false;
			});
			
			//点击保存纪要按钮
			self.panel.on('click', 'div.weekly-project button.save_record', function(){
				if(record && !record.checkValidate()){
					return false;
				}
				var $selfBtn = $(this),
					$textRecord = $selfBtn.closest('div.weekly-project').find('textarea.update_record'),
					meetingId = $(this).closest('div.weekly-project').attr('data-meetingId');
				$.ajax({
					type:'post',
					url:context + '/meeting/updatemeetingrecord.htm',
					data:{meetingId:meetingId, record:$textRecord.val()}
				}).done(function(resultData){
					if(resultData && resultData.success){
						$textRecord.empty().html(resultData.resultData);
						$(this).prop('disabled', false);
					}else if(resultData && !resultData.success && resultData.errormsg){
						layer.alert(resultData.errormsg, 1);
					}else{
						layer.alert('请求失败', 1);
					}
				}).fail(function(){
					layer.alert('请求失败', 1);
				}).always(function(){
					$textRecord.removeClass('form-control').addClass('transparent').prop('disabled', 'disabled').trigger('change');
					$selfBtn.removeClass('save_record').addClass('edit_record').empty().html('编辑纪要');
				});
				return false;
			});
			self.panel.on('change keyup input', 'textarea.hidden_overflow', function(){
				var selfText = $(this);
				selfText.css('height', '38px').css("height",this.scrollHeight + 'px');
			});
			
		},
		//添加或删除参与者
		operatorJoninUser:function(options){
			var dataParams = {
				operatorType:options.operatorType,
				ids:options.ids,
				meetingId:options.meetingId
			};
			$.ajax({
				type:'post',
				data:dataParams,
				url:context + '/meeting/operatorJoninUser.htm'
			}).done(function(resultData){
				if(resultData && resultData.success){
					if(options.callback){
						options.callback();
					}
				}else if(resultData && !resultData.success && resultData.errormsg){
					layer.alert(resultData.errormsg, 1);
				}else{
					layer.alert('请求失败', 1);
				}
			}).fail(function(){
				
			});
		}
	};
	meetingInfo.init();
});