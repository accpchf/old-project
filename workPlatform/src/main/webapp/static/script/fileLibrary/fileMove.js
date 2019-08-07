define(["jquery","script/common/dialogAlert", 'jqueryZtree', 'layer'], function($,dialogAlert){
	var fileMoveInfo = {
		init:function(fileMoveLayer, folderId, projectId, parentFolerId, callback){
			this.panel = fileMoveLayer;
			this.projectId = projectId;
			this.folderId = folderId;
			this.callback = callback;
			this.parentFolerId = parentFolerId;
			this.setEvent();
			this.viewInit();
		},
		setEvent:function(){
			var self = this;
				
			self.panel.on('click', 'button.btn_submit', function(){
				var selfBtn = $(this),
				fileTree = self.fileTree;
				selfBtn.prop('disabled', true);
				var nodes = fileTree.getSelectedNodes();
				if(!nodes || nodes.length != 1){
					dialogAlert.alert('移动目录选择错误');
					selfBtn.prop('disabled', false);
					return;
				}
				$.ajax({
					type:'post',
					url:context + '/filelibrary/movefile.htm',
					data:{fId:self.folderId, pId:nodes[0].id}
				}).done(function(resultData){
					if(resultData && resultData.success){
						layer.alert("文件移动成功", 1, '信息', function(index){
							self.callback(nodes[0].id);
							layer.close(index);
						});
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
		},
		viewInit:function(){
			var self = this;
			$.ajax({
				type:"post",
				url:context + '/filelibrary/listfolders.htm',
				data:{projectId:self.projectId, parentFolerId:self.parentFolerId}
			}).done(function(resultData){
				if(resultData && resultData.success){
					self.refreshFoldersTree(resultData.resultData);
				}else if(resultData && !resultData.success && resultData.errormsg){
					layer.alert(resultData.errormsg, 1);
				}else{
					layer.alert('请求失败', 1);
				};
			}).fail(function(){
				layer.alert('请求失败', 1);
			});
		},
		refreshFoldersTree:function(result){
			var self = this;
			if(!result){
				return;
			}
			var setting = {
		        data:{
		        	simpleData: {
						enable: true,
						rootPid:null
					}
		        },
		        view: {
		    		selectedMulti: false
		    	}
		    };  
			$.each(result, function(key, value){
				if(value.id == 58){
					value.open = true;
				}
			});
			self.fileTree = $.fn.zTree.init(self.panel.find('.folder_tree_div'), setting, result);
		}
	};
	return fileMoveInfo;
});