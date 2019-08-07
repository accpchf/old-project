var currentModiFile = null;

require(['script/fileLibrary/fileMove', "jqueryForm", "jquery", "layer"], function(fileMove){
	var fileLibraryinfo = {
		init:function(){
			this.panel = $(".filelibrary-content");
			this.projectId = this.panel.closest("div[data-id='projectTemplate']").data("projectId");
			//文件缓存
			this.fileData = {propertyTemp:"filelibrary_"};
			this.viewInit();
			this.setEvent();
		},
		setEvent:function(){
			var self = this;
			//点击文件夹，打开文件夹
			self.panel.on('click', 'a.a_folder', function(){
				var selfA = $(this),
					fileId = selfA.closest('tr').attr('data-fileId'),
					//判断当前数据是否处于修改状态
					fileNameInput = selfA.find('input.transparency'),
					isFolder = selfA.attr('data-isfolder');
				if(fileNameInput.length <= 0){
					return false;
				}
				if(isFolder == "true"){
					//如果是文件夹，则打开
					self.getFileChidren(fileId);
				}
				if(isFolder == "false"){
					//如果点击的是文件，直接下载 
				}
				return false;
			});
			
			//屏蔽掉文件或文件夹的双击事件
			self.panel.on('dblclick', 'a.a_folder', function(){
				return false;
			});
			
			//点击文件路径中的文件夹，打开相应文件夹
			self.panel.on('click', 'span.route a', function(){
				var selfA = $(this),
				fileId = selfA.closest('a').attr('data-fileId');
				self.getFileChidren(fileId);
				return false;
			});
			var FileName="",inputFileName="" ;
			//点击新建文件夹
			self.panel.on('click', 'button.button_new_folder', function(){
				if(self.panel.find('tbody.tbody_file_manage').children('tr').length <= 0){
					self.panel.find('tbody.tbody_file_manage').empty();
				}
				var $fileManage = self.panel.find('tbody.tbody_file_manage'),
					fileHtml = "";
				fileHtml += '<tr data-fileId="0">';
				fileHtml += '<td><a class="a_folder"><i class="icon-folder-2"></i><input temp="newFolder" class="input_file_name"/></a></td>';
				fileHtml += '<td></td>';
				fileHtml += '<td></td>';
				fileHtml += '<td></td>';
				fileHtml += "</tr>";
				
				var $firstFile = $fileManage.find('tr:first');
				if($firstFile.length > 0){
					$firstFile.before(fileHtml);
				}else{
					$fileManage.append(fileHtml);
				}
				$fileManage.find('input[temp="newFolder"]').focus();
				FileName = self.panel.find('input.input_file_name');
				inputFileName = new InputValidate(FileName, true, 80);
				currentModiFile = "";
				return false;
			});
			
			//点击修改文件或文件夹名称
			self.panel.on('click', 'i.icon-edit', function(){
				var selfI = $(this),
					$input = selfI.closest('tr').find('input.input_file_name');
				$input.removeClass('transparency').prop('readonly', '');
//				if(selfI.closest('tr').find('a.a_folder').data('isfolder')){
//					$input.select();
//				}else{
//					var input = $input.attr('value').split(".");
//					$input[0].selectionStart = 0;
//			        $input[0].selectionEnd = $input[0].value.length - input[input.length-1].length - 1;
//				}
				$input.select();
				
				FileName = self.panel.find('input.input_file_name');
				inputFileName = new InputValidate(FileName, false, 80);
				currentModiFile = $input.attr('value');
				return false;
			});
			
			//名称input失去焦点，则保存数据
			self.panel.on('blur', 'input.input_file_name', function(){
				if(currentModiFile == null) {
					return false;
				}
				var selfInput = $(this),
					//待操作的文件id
					fileId = selfInput.closest('tr').attr('data-fileId'),
					//待操作的文件名称
					fileName = selfInput.closest('tr').find('input.input_file_name').val(),
					//当前的文件目录的id
					folderId = self.panel.find('span.route').attr('data-currentfileid');
				var isFolder = selfInput.closest('tr').find("a[data-isfolder]").attr('data-isfolder');
				//如果文件名称为空，则放弃此次操作
				if(!fileName || fileName.length <= 0 || (isFolder == "false" && (fileName.indexOf(".") <= 0x0))){
					self.getFileChidren(folderId);
					return;
				}
				if(inputFileName && !inputFileName.checkValidate()){
					return false;
				}
				if(fileId > 0x0 && fileName == currentModiFile) {//如果名称未作改变，则放弃并返回
					self.getFileChidren(folderId);
					return false;
				}
				if(isFolder == "false" && fileName.indexOf(".") >= 0x0 
						&& currentModiFile.indexOf(".") >= 0x0 
						&& fileName.split(".")[fileName.split(".").length - 0x1].toLowerCase() != 
							currentModiFile.split(".")[currentModiFile.split(".").length - 0x1].toLowerCase()) {//文件扩展名不可改变
					layer.alert("文件名扩展名不可改变！", 1);
					self.getFileChidren(folderId);
					return false;
				}
				$.ajax({
					type:'post',
					url:context + '/filelibrary/operatorfilelibrary.htm',
					data:{fileId:fileId, fileName:fileName, projectId:self.projectId, folderId:folderId}
				}).done(function(resultData){
					if(resultData && resultData.success){
						return ;
					}else if(resultData && !resultData.success && resultData.errormsg){
						layer.alert(resultData.errormsg, 1);
					}else{
						layer.alert('请求失败', 1);
					};
				}).fail(function(){
					layer.alert('请求失败', 1);
				}).always(function(){
					currentModiFile = null;
					var propertyTemp = self.fileData.propertyTemp;
					self.fileData[propertyTemp + folderId] = null;
					self.getFileChidren(folderId);
				});
			});
			
			//点击上传文件按钮
			self.panel.on('click', 'button.button_upload_file', function(){
				var folderId = self.panel.find('span.route').attr('data-currentfileid');
				self.panel.find('form.file_upload_form').find('input.file_library_input').attr('value', folderId).end()
														.find('input.file_project_input').attr('value', self.projectId).end()
														.find('input.file_upload_input').click();
				return false;
			});
			
			//file input值改变时(即选择了文件)，触发的事件 
			self.panel.on('change', 'input.file_upload_input', function(){
				var loadInfo = layer.load();
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
			    				return true;
			    			}; 
			    			return false;
			    		},
			    		success:function(resultData){
			    			if(resultData && resultData.success){
			    				var propertyTemp = self.fileData.propertyTemp,
				    				//当前的文件目录的id
								folderId = self.panel.find('span.route').attr('data-currentfileid');
								self.fileData[propertyTemp + folderId] = null;
								self.getFileChidren(folderId);
			    				return;
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
			    			layer.close(loadInfo);
			    			$(selfInput).after($(selfInput).clone(true).val(''));
			    			$(selfInput).remove();
			    		}
		    		};
		    	$(selfInput).closest('form.file_upload_form').ajaxSubmit(options);
		    	return false;
			});
			
			//下载文件
			self.panel.on('click', 'i.icon-download', function(){
				var $form = self.panel.find('form.file_download_form');
				$form.find('input[name="fileUrl"]').attr('value', $(this).closest('tr').attr('data-fileUrl'));
				$form.find('input[name="remove_cache"]').attr('value', (new Date()).getTime());
				$form.submit().before($form.clone(true).val("")).remove();
			});
			
			//移动文件
			self.panel.on('click', 'i.icon-move', function(){
				var html = $("<div></div>"),
					selfBtn = $(this),
					name1 = selfBtn.closest('tr').find('a.a_folder').attr('data-isfolder') ? "文件夹" : "文件" ,
					fromFileName = name1 + selfBtn.closest('tr').find('input.input_file_name').val(),
					folderId = $(this).closest('tr').attr('data-fileId');
				if($(this).hasClass('icon-pencil')){
				}
		        html.load(context + "/filelibrary/openfilemoveview.htm", {fromFileName:fromFileName}, function(){
		        	var fileMoveIndex = -1;
		        	fileMoveIndex = $.layer({
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
			            success: function(fileMoveLayer){
			              $(fileMoveLayer).on('click', function(event){
			            	  return false;
			              });
			              $(fileMoveLayer).on("click","i.icon-cancel-circled",function(){
			                layer.close(fileMoveIndex);
			              });
			              fileMove.init(fileMoveLayer, folderId, self.projectId, self.panel.find('span.route').attr('data-currentfileid'), function(pId){
			            	  $.each(self.fileData, function(key, value){
			            		  if(key.indexOf(self.fileData.propertyTemp) > -1){
			            			  self.fileData[key] = null;
			            		  }
			            	  });
			            	  self.getFileChidren(pId);
			              });
			            }
		          	});
		        });
				return false;
			});
			
			//删除文件
			self.panel.on('click', 'i.icon-trash-empty', function(){
				var folderId = $(this).closest('tr').attr('data-fileId');
				layer.confirm('确定删除当前文件或文件夹及其下面的所有文件吗', function(index){
					layer.close(index);
					$.ajax({
						url:context + '/filelibrary/deletefilelibrary.htm',
						type:'post',
						data:{projectId:self.projectId, folderId:folderId}
					}).done(function(resultData){
						if(resultData && resultData.success){
							var propertyTemp = self.fileData.propertyTemp,
		    				//当前的文件目录的id
							folderId = self.panel.find('span.route').attr('data-currentfileid');
							self.fileData[propertyTemp + folderId] = null;
							self.getFileChidren(folderId);
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
		},
		//获取页面初始化数据
		viewInit:function(){
			this.getFileChidren(0);
		},
		//刷新文件库列表
		refreshFileLibraryView:function(result){
			var self = this;
			if(!result){
				return;
			}
			var $filePathManage = self.panel.find('span.route'),
				paths = result.parent,
				pathHtml = "";
			$filePathManage.attr('data-currentfileid',result.attachment.id);
			//文件路径渲染
			for(var i = paths.length - 1; i >= 0; i--){
				var value = paths[i];
				pathHtml += '<a href="#" data-fileId="'+ value.attachment.id +'">'+ value.attachment.fileName +'</a> <i class="icon-right-open-big"></i>';
			}
			$filePathManage.empty().append(pathHtml);
			
			var $fileManage = self.panel.find('tbody.tbody_file_manage'),
				files = result.children,
				fileHtml = "";
			$fileManage.closest('div').find('div.none-content').remove();
			if(files.length > 0){
				//渲染文件
				$.each(files, function(key, value){
					var user = window.projectUserList[value.attachment.createdBy],
						folderClass = value.attachment.folder ? "icon-folder-2" : "icon-doc-text-1";
					var isNeedDownload = value.attachment.folder ? "" : (PRJ_ACCESS ? '<i class=" icon-download" title="下载"></i>' : '');
					var isNeedOpera = PRJ_OPERAT && window.readOnly ? '<i class="icon-edit" title="修改名称"></i><i class="icon-move" title="移动"></i><i class="icon-trash-empty" title="删除"></i>' : '';
					fileHtml += '<tr data-fileId="'+ value.attachment.id +'" data-fileUrl="' + value.attachment.fileUrl+ '">';
					fileHtml += '<td><a class="a_folder" data-isfolder="'+ value.attachment.folder+'"><i class="'+ folderClass  +'"></i><input readonly="readonly" title="'+ value.attachment.fileName +'" value="'+ value.attachment.fileName +'" class="transparency input_file_name"/></a></td>';
					fileHtml += '<td>'+ (user && user.name) || "" +'</td>';
					fileHtml += '<td>'+ new Date(value.attachment.updatedTime).format("yyyy-MM-dd") +'</td>';
					fileHtml += '<td>' + isNeedDownload + isNeedOpera + '</td>';
					fileHtml += "</tr>";
				});
				$fileManage.empty().append(fileHtml);
			}else{
				$fileManage.empty();
				if($fileManage.closest('div').find('div.none-content').length <= 0){
					$fileManage.closest('div').append('<div class="none-content"><i class="icon-folder"></i><br /><a>还没上传文件</a></div>');
				}
			}
		},
		getFileChidren:function(fileId){
			var self = this,
				propertyTemp = self.fileData.propertyTemp,
				result = null;
			//如果缓存中有数据，则直接刷新页面
			if(self.fileData[propertyTemp + fileId]){
				result = self.fileData[propertyTemp + fileId];
				self.refreshFileLibraryView(result);
				return;
			}
			
			//如果缓存中没数据，则请求取得数据并把数据缓存
			$.ajax({
				type:'post',
				url:context +"/filelibrary/listFiles.htm",
				data:{projectId:self.projectId, fileId:fileId}
			}).done(function(resultData){
				if(resultData && resultData.success){
					var file = resultData.resultData;
					self.fileData[self.fileData.propertyTemp + file.attachment.id] = file;
					self.refreshFileLibraryView(file);
					return ;
				}else if(resultData && !resultData.success && resultData.errormsg){
					layer.alert(resultData.errormsg, 1);
				}else{
					layer.alert('请求失败', 1);
				};
			}).fail(function(){
				layer.alert('请求失败', 1);
			});
		}
	};
	fileLibraryinfo.init();
});