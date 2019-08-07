define(["jquery", "socketio","script/chat/startChat"],function($,io,startChat){
	
	var socket = io.connect(nodeJsUrl);
	var chatSocket = {
		init :function(id,name){
			this.roomId = id;
			this.joinRoom({roomIds: id+"",userName: loginUserInfo.userId});
			this.listenPushData();
		},
		/**
		 * 获得当前的io连接，可以判断是否连接等
		 * @returns
		 */
		getConnectionIo : function(){
			return socket;
		},
		/**
		 * 加入聊天室
		 * @param data
		 */
		joinRoom: function (data) {
	        socket.emit('joinRoom', data);
	    },
	    /**
	     * 聊天发送信息
	     * @param data
	     */
		sendMessage : function(data){
			data.roomId = this.roomId;
			data.fromUser = loginUserInfo.userId;
			socket.emit('sendMsg', data);
		},
		/**
		 * 断开连接
		 */
		disconnection: function () {
			socket.emit('disconnect');
	    },
	    /**
	     * 监听窗口是否有消息推送
	     */
		listenPushData : function(){
			socket.off('broadcastMsg');
			socket.on('broadcastMsg', function(data){
				
				//当前的聊天窗口
				var chatWindow = $("#zhcs_OA_chatWindow");
				//当前聊天窗口的top区域
				var chatWindowTop = $("#zhcs_OA_chatWindowTop",chatWindow);
				//是否存在弹出窗口，而且打开的是否是当前任务的聊天窗口
				if(chatWindow.length > 0 && chatWindowTop.data("groupId") == data.roomId){
					//如果聊天内容是文件
					if(data.recordType == 2){
						data.content="<div><p>"+data.fileName+"<a>("+startChat.fileSizeUnit(data.fileSize)+")</a></p></div>"+"<button class='btn btn-xs btn-success'>下载</button>";
					}
					var chatWindowContent;//聊天内容区
					var chatWindowContentLeft;//左边正在聊天的列表
					var chatWindowContentRight;//如果是群的话，群内部人员列表
					
					//属于私聊
					if(data.toUser){
						chatWindowContentRight = $("#chatWindow_right1"+data.roomId);
						chatWindowContentLeft = $("#chatWindow_left0"+data.roomId+data.fromUser);
						//要推送的用户窗口已经打开
						if(chatWindowContentLeft.length > 0){
							//要推送的用户窗口是否处于激活状态
							if(chatWindowContentLeft.hasClass("current")){
								
							}else{//要推送的用户窗口已经打开但是没有激活 左边消息个数提示
								var textNum = chatWindowContentLeft.find("span.badge").text();
								var count = textNum?textNum:0;
								chatWindowContentLeft.find("span.badge").text(parseInt(count)+1);
							}
						}else{//要推送的用户窗口没打开 ，右边群消息个数提示
							var li = chatWindowContentRight.find("li.friends-item[data-id='"+data.fromUser+"']");
							var textNum = li.find("span.badge").text();
							var count = textNum?textNum:0;
							li.find("span.badge").text(parseInt(count)+1);//未读信息数量
							
							chatWindow.find("#zhcs_OA_chatWindowContent").append(startChat.createChatWindowContent("0", data.fromUser, data,data.roomId));
						}
						chatWindowContent = chatWindow.find("ul#chatWindow_content"+0+data.roomId+startChat.mergeId(data.fromUser,loginUserInfo.userId));
					}else{//属于群聊
						chatWindowContentLeft = $("#chatWindow_left1"+data.roomId+data.roomId);
						//如果群聊窗口存在
						if(chatWindowContentLeft.length > 0){
							//如果群聊窗口激活
							if(chatWindowContentLeft.hasClass("current")){
								
							}else{//如果群聊窗口没有激活
								var textNum = chatWindowContentLeft.find("span.badge").text();
								var count = textNum?textNum:0;
								chatWindowContentLeft.find("span.badge").text(parseInt(count)+1);
							}
							chatWindowContent = chatWindow.find("ul#chatWindow_content"+1+data.roomId);
						}else{//如果群聊窗口不存在
							return false;
						}
					}
					var chatModule;
					//如果是接收到的消息是自己发送的消息
					if(data.fromUser==loginUserInfo.userId){
						chatModule = $(startChat.createChatMessageHtml(data.recordType, true, data.fromTime, data.content,data.fromUser)).appendTo(chatWindowContent);
					}else{
						chatModule = $(startChat.createChatMessageHtml(data.recordType, false, data.fromTime, data.content,data.fromUser)).appendTo(chatWindowContent);
					}
					chatWindowContent.animate({scrollTop: chatWindowContent[0].scrollHeight}, 300);
					 
					//如果是文件加上对应的url，便于之后下载
					if(data.fileUrl != ""){
						chatModule.find("button.btn-success").data("url",data.fileUrl).data("name",data.fileName);
					}
					
				}
			});
		}
	};
	return chatSocket;
});