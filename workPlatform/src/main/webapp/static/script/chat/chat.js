require(["jquery",'script/chat/startChat','script/chat/chatExpression','script/chat/chatSocket',"script/common/dialogAlert"], function($,startChat,chatExpression,chatSocket,dialogAlert){
	var chatModule = {
			init : function(){
				//聊天窗口
				this.chatWindow = $("#zhcs_OA_chatWindow");
				//聊天窗口顶端
				this.chatWindowTop = $("#zhcs_OA_chatWindowTop",this.chatWindow);
				//聊天窗口最小化时，点击显示窗口的区域
				this.chatWindowMin = $(".chat_mini",this.chatWindow);
				//聊天窗口左边聊天对象
				this.chatWindowLeft = $("#zhcs_OA_chatWindowLeft",this.chatWindow);
				//聊天窗口中间区域
				this.chatWindowCenter = $("#zhcs_OA_chatWindowCenter",this.chatWindow);
				//聊天窗口内容区
				this.chatWindowContent = $("#zhcs_OA_chatWindowContent",this.chatWindow);
				//聊天窗口发送区
				this.chatWindowSendMessage = $("#zhcs_OA_chatWindowSendMessage",this.chatWindow);
				//聊天窗口输入框
				this.chatTextarea = this.chatWindowCenter.find("textarea");
				//聊天窗口表情Table
				this.chatWindowExpression = this.chatWindowCenter.find("#zhcs_OA_chatWindowExpression");
				//聊天窗口发送区
				this.chatWindowRight = $("#zhcs_OA_chatWindowRight",this.chatWindow);
				//初始化表情
				chatExpression.init({tab:this.chatWindowExpression,ele:this.chatTextarea});
				
				this.setEvent();
				var id = this.chatWindowTop.data("id");
				var name = this.chatWindowTop.find(".titles span").text();
				chatSocket.init(id,name);
			},
			setEvent : function(){
				this.moveChatWindow();
				this.closeChatWindow();
				this.minChatWindow();
				this.leftSwitchDialog();
				this.rightSwitchDialog();
				this.removeSingleChatWindow();
				this.openExpression();
				this.sendMessage();
				this.sendPicture();
				this.sendFile();
				this.closeCurrentWindowBtn();
				this.chatHistoryMessage();
			},
			/**
			 * 聊天窗口左边区域内事件(当前正在聊天的对象们)
			 */
			leftSwitchDialog : function(){
				var self = this;
				this.chatWindowLeft.on("click","ul.friends_list li",function(){
					var id = $(this).data("id");
					var type = $(this).data("type");
					var name = $(this).text();
					var groupId = type=="0"?self.chatWindowTop.data("groupId"):id;
					startChat.activatesCurrentDialog(groupId,id,type,name);
					$(this).find("span.badge").text("");
				});
				this.chatWindowLeft.on("mouseover","ul.friends_list li",function(){
					$(this).find("i.icon-cancel-circled").show();
				});
				this.chatWindowLeft.on("mouseout","ul.friends_list li",function(){
					$(this).find("i.icon-cancel-circled").hide();
				});
			},
			/**
			 * 聊天窗口右边群组的区域(群内的成员)
			 */
			rightSwitchDialog : function(){//点击群成员聊天
				var self = this;
				this.chatWindowRight.on("click","li.friends-item",function(){
					var id = $(this).data("id");
					var name = $(this).text();
					var groupId = $(this).closest("div").attr("data-groupId");
					
					if(id == loginUserInfo.userId){//自己和自己不能聊天
						
					}else{
						if(self.chatWindowLeft.find("#chatWindow_left0"+groupId+id).length > 0){//聊天窗口已经存在
							startChat.activatesCurrentDialog(groupId,id,"0",name);
						}else{
							startChat.loadChatWindow(groupId,null,null,"0",name,id);
						}
						$(this).find("span.badge").text("");
					}
				});
			},
			/**
			 * 显示表情层
			 */
			openExpression : function(){
				var self = this;
				this.chatWindowCenter.on("click","a.icon-emo-grin",function(){
					self.chatWindowExpression.removeClass("g-hide");
					return false;
				});
			},
			/**
			 * 上传文件时的滚动条
			 * @param fileName
			 * @param fileSize
			 * @returns {String}
			 */
			fileProgress : function(fileName,fileSize){
				
				fileSize = startChat.fileSizeUnit(fileSize);
				var str= "<div><p>"+fileName+"<a>("+fileSize+")</a></p></div>";
				str += "<div class='progress-chat'>";
				str += "<a class='progress-chat-val'>0%</a>";
				str += "<a class='progress-chat-bar'><em class='progress-chat-in' style='width: 0%'></em> </a></div>";
				str += "<a class='word-cancel'>取消</a>";
				
				return str;
			},
			/**
			 * 上传文件的错误信息
			 * @param dom
			 * @param text
			 */
			sendFileError : function(dom,text){
				dom.find("div.progress-chat,a.word-cancel").remove();
		        dom.find("div.post-content").append("<p style='color:red;'>"+text+"</p>");
			},
			downloadFile : function(fileName, url){
				var aLink = document.createElement("a"),
				evt = document.createEvent("HTMLEvents"),
				isData = url.slice(0, 5) === "data:",
				isPath = url.lastIndexOf(".") > -1;
				if(/firefox/.test(navigator.userAgent.toLowerCase())) {//火狐浏览器特别处理
					evt = document.createEvent("MouseEvents");
				}
				
				// 初始化点击事件
				// 注：initEvent 不加后两个参数在FF下会报错
				evt.initEvent("click",false,false);
	
				// 添加文件下载名
				aLink.download = fileName;
	
				// 如果是 path 或者 dataURL 直接赋值
				// 如果是 file 或者其他内容，使用 Blob 转换
				aLink.href = isPath || isData ? url
							: URL.createObjectURL(new Blob([url]));
				aLink.dispatchEvent(evt);
			},
			/**
			 * 清除file的值
			 * @param el
			 */
			clearInputFile : function(el){
				$(el).after(el.clone(true).val(''));
    			$(el).remove();
			},
			/**
			 * 上传文件
			 */
			sendFile : function(){
				var self = this;
				//下载文件按钮
				this.chatWindowCenter.on("click","button.btn-success",function(event){
					
					var name = $(this).data("name");
					var url = $(this).data("url");
					self.downloadFile(name, context+"/static/uploadfile/"+url);
					return false;
				});
				//点击选择文件按钮
				this.chatWindowCenter.on("click","a#select_file",function(event){
					
					self.chatWindowCenter.find("form#send_file input").click();
					return false;
				});
				this.chatWindowCenter.find("form#send_file").on("change","input",function(event){
					var el = $(this);
					if(!chatSocket.getConnectionIo() || chatSocket.getConnectionIo().disconnected){
						
						self.clearInputFile(el);
						dialogAlert.alert('聊天异常！');
						return false;
					}
					var file = this.files[0];
					if(file){
						var fileSize = file.size;
						var fileName = file.name;
						if(fileSize > 10*1024 * 1024){
							self.clearInputFile(el);
							dialogAlert.alert('文件大小不能大于10MB！');
							return false;
						}
						
						var fd = new FormData();
						fd.append("chatFile",file);
				          
						// 发送聊天消息
		    			var type = self.chatWindowTop.data("type");
						var groupId = "";
						var toUser="";
						var id = self.chatWindowTop.data("id");
						var currentChat;
						if(type == "0"){
							toUser=id; 
							groupId = self.chatWindowTop.data("groupId");
							currentChat= self.chatWindowContent.find("ul#chatWindow_content"+type+groupId+startChat.mergeId(id,loginUserInfo.userId));
						}else{
							currentChat= self.chatWindowContent.find("ul#chatWindow_content"+type+id);
						}
						
						
						var xhr = new XMLHttpRequest(); 
						//开始上传
						xhr.withCredentials = true;
						xhr.open('POST',context+"/task/chatUploadFile.htm",true);
						xhr.onloadstart =  function(){
							self.chatFileProgress = $(startChat.createChatMessageHtml(2,true,new Date(),self.fileProgress(fileName, fileSize),loginUserInfo.userId)).appendTo(currentChat);
							currentChat.animate({scrollTop: currentChat[0].scrollHeight}, 300); 
							self.chatFileProgress.find("a.word-cancel").on("click",function(){
						        xhr.abort();
						        self.sendFileError(self.chatFileProgress, "文件上传被取消");
							});
						};
						//上传完毕
			            xhr.onload = function() {
			            	var data = $.parseJSON(this.response);
			                if (this.status == 200) {
			                	var fileUrl = data.resultData.filePath;
			                	chatSocket.sendMessage({toUser: toUser,content: "", recordType: "2",fileUrl:fileUrl,fileSize:fileSize,fileName:fileName});
			                	self.chatFileProgress.find("a.word-cancel").remove();
			                	var downBtn =$("<button class='btn btn-xs btn-success'>下载</button>");
			                	downBtn.data("url",fileUrl);
			                	downBtn.data("name",fileName);
			                	self.chatFileProgress.find(".progress-chat").replaceWith(downBtn);
			                } else {
			                	self.sendFileError(self.chatFileProgress, "文件上传失败");
			                }
			                self.clearInputFile(el);
			            };
			           //上传文件异常
			            xhr.onerror = function(event) {
			            	self.clearInputFile(el);
			            	self.sendFileError(self.chatFileProgress, "文件上传异常");
			            };
			          //上传文件进度
			            xhr.upload.onprogress = function (ev) { 
			                var percent = 0; 
			                
			                if(ev.lengthComputable) { 
			                    percent = Math.round(ev.loaded * 100 / ev.total)+"%"; 
			                    self.chatFileProgress.find(".progress-chat-val").text(percent);
			                    self.chatFileProgress.find(".progress-chat-in").css("width",percent);
			                } 
			            };
			            xhr.send(fd);
					}
					 
					return false;
				});
			},
			/**
			 * 聊天发送图片
			 */
			sendPicture : function(){
				var self = this;
				//选中图片
				this.chatWindowCenter.on("click","a#select_picture",function(event){
					
					self.chatWindowCenter.find("form#send_picture input").click();
					return false;
				});
				this.chatWindowCenter.find("form#send_picture").on("change","input",function(event){
					var el = $(this);
					if(!chatSocket.getConnectionIo() || chatSocket.getConnectionIo().disconnected){
						
						self.clearInputFile(el);
						dialogAlert.alert('聊天异常！');
						return false;
					}
			    	var files = this.files;
			    	if(files.length != 0){
			    		var file = files[0];
			    		
			    		var reader = new FileReader();
			    		if(!reader){
			    			dialogAlert.alert('该浏览器不支持图片发送！');
			    			self.clearInputFile(el);
			    			return;
			    		}
			    		var rules = new RegExp('.+(.jpeg|.jpg|.gif|.bmp|.png)$', 'gi');
			    		if(!rules.test(file.type)){
			    			dialogAlert.alert('请选择正确格式的图片！');
			    			self.clearInputFile(el);
			    			return;
			    		}
			    		if(file.size > 1024 * 1024){
			    			self.clearInputFile(el);
			    			dialogAlert.alert('图片不能大于1MB！');
							return ;
						}
			    		
			    		//reader.readAsBinaryString(file); 
			    		reader.readAsDataURL(file);
			    		reader.onload = function(event){
			    			var target = EventUtil.getTarget(event);
			    			var content = target.result;
			    			
					        // 发送聊天消息
							var groupId = "";
							var toUser="";
							var id = self.chatWindowTop.data("id");
							
							var type = self.chatWindowTop.data("type");
							var currentChat;
							if(type == 0){
								toUser = id;
								groupId = self.chatWindowTop.data("groupId");
								currentChat = self.chatWindowContent.find("ul#chatWindow_content"+type+groupId+startChat.mergeId(id,loginUserInfo.userId));
							}else{
								currentChat = self.chatWindowContent.find("ul#chatWindow_content"+type+id);
							}
							
							currentChat.append(startChat.createChatMessageHtml(1,true,new Date(),content,loginUserInfo.userId));
							currentChat.animate({scrollTop: currentChat[0].scrollHeight}, 300); 
							
							chatSocket.sendMessage({toUser: toUser,content: content, recordType: "1"});
							self.clearInputFile(el);
			    		};
			    	}
					return false;
				});
			},
			/**
			 * 移动聊天窗口
			 */
			moveChatWindow : function(){
				
				this.chatWindow.draggable({ 
					handle: ".chat_header",
					containment:"window",
					scroll: false
				});
			},
			/**
			 * 关闭单个聊天窗口
			 */
			removeSingleChatWindow : function(){
				var self = this;
				this.chatWindowLeft.on("click","li .icon-cancel-circled",function(){
					
					var li = $(this).closest("li");
					var isSelected = li.hasClass("current");//判断当前是否选中
					var id = li.data("id");
					var type = li.data("type");
					var groupId = type == "0"?self.chatWindowTop.data("groupId"):li.data("id");
					var count = li.siblings("li").length;
					
					li.remove();
					if(type == "0"){
						self.chatWindowContent.find("#chatWindow_content"+type+groupId+startChat.mergeId(id,loginUserInfo.userId)).remove();
					}else{
						self.chatWindowContent.find("#chatWindow_content"+type+id).remove();
					}
					
					self.chatWindowRight.find("#chatWindow_right"+type+groupId+id).remove();
					if(count > 1){
					}else{
						self.chatWindowLeft.hide();
					}
					if(isSelected){
						
						var newLi = self.chatWindowLeft.find("li:eq(0)");
						var newId = newLi.data("id");
						var newType = newLi.data("type");
						var newName = newLi.text();
						startChat.activatesCurrentDialog(groupId,newId,newType,newName);
						
					}
					return false;
				});
			},
			/**
			 * 关闭聊天窗口
			 */
			closeChatWindow : function(){
				var self = this;
				this.chatWindowTop.on("off","a.icon-cancel");
				this.chatWindowTop.on("click","a.icon-cancel",function(){
					self.chatWindow.remove();
					chatSocket.disconnection();
					return false;
				});
			},
			/**
			 * 最小化聊天窗口
			 */
			minChatWindow : function(){
				var self = this;
				this.chatWindowTop.on("click","a.icon-minus",function(){
					self.chatWindow.find(".chat_box").hide();
					self.chatWindowMin.show();
					self.updateChatNum();
					return false;
				});
				this.chatWindowMin.on("click",function(){
					$(this).hide();
					self.chatWindow.find(".chat_box").show();
					return false;
				});
			},
			/**
			 * 关闭当前聊天对象
			 */
			closeCurrentWindowBtn : function(){
				var self = this;
				this.chatWindowSendMessage.on("click","#closeBtn",function(){
					
					var li = self.chatWindowLeft.find("li.current");
					var count = li.siblings("li").length;
					if(count > 0){
						var id = self.chatWindowTop.data("id");
						var type = self.chatWindowTop.data("type");
						var groupId = type == "0"?self.chatWindowTop.data("groupId"):li.data("id");
						li.remove();
						if(type == "0"){
							self.chatWindowContent.find("#chatWindow_content"+type+groupId+startChat.mergeId(id,loginUserInfo.userId)).remove();
						}else{
							self.chatWindowContent.find("#chatWindow_content"+type+id).remove();
						}
						self.chatWindowRight.find("#chatWindow_right"+type+groupId+id).remove();
						if(count == 1){
							self.chatWindowLeft.hide();
						}
						var newLi = self.chatWindowLeft.find("li:eq(0)");
						var newId = newLi.data("id");
						var newType = newLi.data("type");
						var newName = newLi.text();
						startChat.activatesCurrentDialog(groupId,newId,newType,newName);
					}else{
						chatSocket.disconnection();
						self.chatWindow.remove();
					}
				});
			},
			/**
			 * 发送消息
			 */
			sendMessage : function(){
				var self = this;
				this.chatWindowSendMessage.on("click","#sendMessageBtn",function(){
					
					if(!chatSocket.getConnectionIo() || chatSocket.getConnectionIo().disconnected){
						
						dialogAlert.alert('聊天异常！');
						return false;
					}

					var type = self.chatWindowTop.data("type");
					var groupId = "";
					var id = self.chatWindowTop.data("id");
					var content = self.chatTextarea.val();
					var toUser = "";
					if(content == ""){
						return false;
					}
					
					var currentChat;
					if(type == "0"){
						groupId = self.chatWindowTop.data("groupId");
						toUser = id;
						currentChat = self.chatWindowContent.find("#chatWindow_content"+type+groupId+startChat.mergeId(id,loginUserInfo.userId));
					}else{
						currentChat = self.chatWindowContent.find("#chatWindow_content"+type+id);
					}
					currentChat.append(startChat.createChatMessageHtml(0,true,new Date(),content,loginUserInfo.userId));
					self.chatTextarea.val("");
					chatSocket.sendMessage({toUser: toUser,content: content, recordType: "0"});
					currentChat.animate({scrollTop: currentChat[0].scrollHeight}, 300); 

					return false;
				});
			},
			/**
			 * 获得聊天历史记录
			 */
			chatHistoryMessage : function(){
				var self = this;
				this.chatWindowCenter.on("click","li.more-message",function(){
					var el = $(this);
					var roomId = self.chatWindowTop.data("groupId");
					var friendUserName = "";
					if(self.chatWindowTop.data("type") == "0"){
						friendUserName = self.chatWindowTop.data("id");
					}
					$.ajax({
						url:nodeJsUrl+"/getRecords",
						type:"get",
						data:{roomId:roomId,currentUserName :loginUserInfo.userId,friendUserName:friendUserName}
					}).done(function(result){
						if(result.records){
							el.siblings().remove();
							el.after(self.getChatHistoryContent(result.records)).hide();
						}
						
					}).fail(function(){
						
					});
				});
			},
			/**
			 * 获得聊天内容
			 * @param data
			 * @returns {String}
			 */
			getChatHistoryContent : function(data){
				var str = "";
				for(var i = data.length-1;i >= 0;i--){
					var obj = data[i];
					var isRight = obj.fromUser == loginUserInfo.userId ?true:false;
					var content ="";
					if(obj.recordType != "2"){
						content = obj.content;
					}else {
						var fileSize = startChat.fileSizeUnit(obj.fileSize);
						content="<div><p>"+obj.fileName+"<a>("+fileSize+")</a></p></div>"+"<button data-url='"+obj.fileUrl+"' data-name='"+obj.fileName+"' class='btn btn-xs btn-success'>下载</button>";
					}

					str +=startChat.createChatMessageHtml(obj.recordType,isRight,obj.fromTime,content,obj.fromUser);
				}
				return str;
			},
			/**
			 * 更新当天聊天对象的个数
			 */
			updateChatNum : function(){
				var count = this.chatWindowLeft.find("li").length;
				this.chatWindowMin.find("span.num").text(count);
			}
	};
	chatModule.init();
});