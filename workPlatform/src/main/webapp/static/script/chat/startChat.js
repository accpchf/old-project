define(["jquery","jqueryUI","layer"], function($){
	
	var taskVisibleStatus = "00300";//任务所有人可见状态码
	/**
	 * 聊天简要说明
	 * 1.聊天窗口分为下面几个部分：
	 *    1.1 top 主要显示聊天对面名称，最小化和关闭按钮
	 *    1.2 left 主要是当前的正处于聊天状态的人员列表
	 *    1.3 center 
	 *        1.3.1 聊天显示的内容区
	 *        1.3.2 发送消息图片区域
	 *    1.4 群内人员列表 根据任务的类型显示不一样
	 * 2.任务的类型分为   0：私聊(仅参与者可以聊天)  1：群聊（项目中的所有人都可以聊天）
	 * 3.聊天内容分为    0:文字 1:图片 2:文件
	 */
	var chartInterface = {
			/**
			 * 初始化 聊天窗口的ID"zhcs_OA_chatWindow"，意思为“智慧城市协同办公聊天窗口”,页面其他地方不要使用此ID
			 * @param targetId 被选择聊天的id
			 * @param visible 任务是否所有人可见
			 * @param type 被选择聊天对象的类型，1:群聊，0:个人聊
			 * @param url 获得聊天的人员的数据
			 */
			init : function(targetId,visible,type,url,name){
				var self = this;
				var status = self.checkChatWindowStatus(targetId,type);
				switch(status){
					case "000"://没有聊天窗口
					case "110"://有聊天窗口但是需要聊天的窗口没有打开
						self.openChatWindow(targetId,visible,type,url,name);
						break;
					case "100"://有聊天窗口而且处于最小化状态，还有需要聊天的窗口没有打开
						$("div#zhcs_OA_chatWindow").show();
						self.openChatWindow(targetId,visible,type,url,name);
						break;
					case "101"://有聊天窗口并且需要聊天的窗口，只不过处于隐藏状态
						$("div#zhcs_OA_chatWindow").show();
						self.activatesCurrentDialog("",targetId,type,name);
						break;
					case "111"://已经打开
						self.activatesCurrentDialog("",targetId,type,name);
						break;
				}
			},
			/**
			 * 激活当前聊天对象
			 * @param groupId
			 * @param targetId
			 * @param type
			 * @param name
			 */
			activatesCurrentDialog : function(groupId,targetId,type,name){
				var chatWindow = $("div#zhcs_OA_chatWindow");
				var left = chatWindow.find("#zhcs_OA_chatWindowLeft");
				var content = chatWindow.find("#zhcs_OA_chatWindowContent");
				var right = chatWindow.find("#zhcs_OA_chatWindowRight");
				var currentDialog = left.find("#chatWindow_left"+type+groupId+targetId);
				if(currentDialog.hasClass("current")){//如果已经激活，则不需要处理
				}else{//如果没有激活，需要激活处理
					this.refreshChatWindowTop(chatWindow,{type:type,name:name,targetId:targetId});
					currentDialog.addClass("current").siblings().removeClass("current");
					if(type == "1"){//群聊
						right.show().find("#chatWindow_right"+type+groupId+targetId).show().siblings().hide();
						content.find("#chatWindow_content"+type+targetId).show().siblings().hide();
					}else{
						right.hide();
						content.find("#chatWindow_content"+type+groupId+this.mergeId(targetId,loginUserInfo.userId)).show().siblings().hide();
					}
					
				}
			},
			/**
			 * 单聊时的id组合，两人的id比较大的放前面小的放后面，保证两端的id一致
			 * @param a
			 * @param b
			 * @returns {String}
			 */
			mergeId : function(a,b){
				if(a>b){
					return ""+a+b;
				}else{
					return ""+b+a;
				}
				
			},
			/**
			 * 获得聊天窗口的状态
			 * @param targetId
			 * @param type
			 * @returns status
			 */
			checkChatWindowStatus : function(targetId,type){
				var chatWindow = $("div#zhcs_OA_chatWindow");
				var status;
				//状态为3位数字 
				//第一位数字 0:没有聊天窗口；1:有聊天窗口
				//第二位数字 0:聊天影藏；1:有聊天显示
				//第三位数字 0:当前的窗口没有打开；1:当前的窗口已经打开
				
				if(chatWindow.length > 0){//当前窗口存在
					if(chatWindow.is(":hidden")){//当前窗口是最小化
						if(chatWindow.find("#zhcs_OA_chatWindowLeft").find("#chatWindow_left"+type+targetId).length > 0){
							status = "101";
						}else{
							status = "100";
						}
					}else{//当前窗口是正在处于显示状态
						if(chatWindow.find("#zhcs_OA_chatWindowLeft").find("#chatWindow_left"+type+targetId).length > 0){
							status = "111";
						}else{
							status = "110";
						}
					}
				}else{//当前没有聊天窗口
					status = "000";
				}
				return status;
			},
			/**
			 * 通过Id获得用户的logo和name
			 * @param id
			 */
			getUserInfo : function(id){
	        	var retUser = {logo:"",name:""};
	        	var user = window.projectUserList[id];
	        	if(id>0){
	        		if(user){
	        			retUser.logo = user.logo;
	        			retUser.name = user.name;
					}
	        	}
	        	return retUser;
	        },
	        /**
	         * 聊天窗口右边的群组人员
	         * @param userIds
	         * @param isAll
	         * @param targetId
	         * @returns {String}
	         */
	        createChatWindowRight : function(userIds,visible,targetId){
	        	var self = this;
	        	var str = "";
				str += "<div class='chatGroup' id='chatWindow_right1"+targetId+"' data-groupId='"+targetId+"'>";
				str += "<ul class='friends_list'>";
				if(visible  == taskVisibleStatus){//该任务所有人可见，那么所有人都有聊天权限
					$.each(window.projectUserList,function(key,value){
						str += "<li class='friends-item' data-id='"+key+"'>";
						str += "<a class='img'><img src='"+value.logo+"' class='img-circle'></a>";
						str += "<a class='item-title'>"+value.name+"</a>";
						str += "<span class='badge'></span></li>";
					});
				}else{//该任务仅参与者可见，那么只有参与者可以聊天
					$.each(userIds,function(key,value){
						var user = self.getUserInfo(value);
						str += "<li class='friends-item' data-id='"+value+"'>";
						str += "<a class='img'><img src='"+user.logo+"' class='img-circle'></a>";
						str += "<a class='item-title'>"+user.name+"</a>";
						str += "<span class='badge'></span></li>";
					});
				}
				
				str += "</ul></div>";
				
				return str;
	        },
	        /**
	         * 加载右边群组
	         * @param dom
	         * @param userIds
	         * @param visible
	         * @param type
	         * @param targetId
	         */
	        loadChatWindowRight : function(dom,userIds,visible,type,targetId){
	        	var right = dom.find("#zhcs_OA_chatWindowRight");
	        	if(type == "0"){//如果是私聊的话，没有右边的人员列表，群聊的话有人员列表
	        		right.hide();
	        	}else{
	        		right.show().find("div.chatGroup").hide();
	        		right.append(this.createChatWindowRight(userIds, visible, targetId));
	        	}
	        	
	        },
	        /**
	         * 创建当前的聊天区域
	         * @param type
	         * @param id
	         * @param data
	         * @param groupId
	         * @returns {String}
	         */
	        createChatWindowContent : function(type,id,data,groupId){
	        	var str = "";
	        	
	        	if(type == "0"){
	        		str += "<ul id='chatWindow_content"+type+groupId+this.mergeId(id,loginUserInfo.userId)+"'";
	        	}else{
	        		str += "<ul id='chatWindow_content"+type+id+"'";
	        	}
	        	if(data){
	        		str += " style='display:none;'>";
	        	}else{
	        		str += ">";
	        	}
	        	
	        	str += "<li class='more-message'><a><i class=' icon-clock-1'></i>查看更多消息</a></li>";
//	        	str += "<li><h6>2014-12-03</h6></li>";
	        	
	        	str += "</ul>";
	        	return str;
	        },
	        /**
	         * 加载当前的聊天区域
	         * @param dom
	         * @param type
	         * @param id
	         * @param data
	         * @param groupId
	         */
	        loadChatWindowContent : function(dom,type,id,data,groupId){
	        	var content = dom.find("#zhcs_OA_chatWindowContent");
	        	var currentDialog = content.find("#chatWindow_content"+type+groupId+this.mergeId(id,loginUserInfo.userId));
	        	
	        	content.find("ul").hide();
	        	if(currentDialog.length > 0){
	        		currentDialog.show();
	        	}else{
		        	content.append(this.createChatWindowContent(type, id, data,groupId));
	        	}
	        	
	        },
	        /**
	         * 刷新加载聊天窗口顶部
	         * @param dom
	         * @param data
	         */
	        refreshChatWindowTop : function(dom,data){
	        	var top = dom.find("#zhcs_OA_chatWindowTop");
	        	top.data("type",data.type);
	        	top.data("id",data.targetId);
	        	
	        	if(data.type == "1"){
	        		top.find("div.titles img").hide();
	        	}else{
	        		top.find("div.titles img").show().attr("src",this.getUserInfo(data.targetId).logo);
	        	}
	        	var span = top.find("div.titles span");
	        	span.text(data.name.length <= 30 ? data.name : data.name.substr(0, 30) + "...");
	        	span.prop("title", data.name);
	        },
	        /**
	         * 创建左边聊天对象
	         * @param id
	         * @param type
	         * @param name
	         * @param groupId
	         * @returns {String}
	         */
	        createChatWindowLeft : function(id,type,name,groupId){
	        	var self = this;
	        	var logo = self.getUserInfo(id).logo;
	        	var str = "";
	        	
	        	if(type == "0"){
	        		str += "<li class='friends-item current' id='chatWindow_left"+type+groupId+id+"' data-id='"+id+"'  data-type='"+type+"'>";
	        		str += "<a class='img'><img src='"+logo+"' class='img-circle'></a>";
	        	}else{
	        		str += "<li class='friends-item current' id='chatWindow_left"+type+id+id+"' data-id='"+id+"' data-type='"+type+"'>";
	        		str += "<a class='img'><img src='' class='img-circle' style='display:none'></a>";
	        	}
	        	str += "<a class='item-title'>"+name+"</a>";
	        	str += "<i class=' icon-cancel-circled' title='关闭'  style='display:none'></i>";
	        	str += "<span class='badge'></span>";
	        	str += "</li>";
	        	return str;
	        },
	        /**
	         * 加载左边聊天对象
	         * @param dom
	         * @param id
	         * @param type
	         * @param name
	         * @param groupId
	         */
	        loadChatWindowLeft : function(dom,id,type,name,groupId){
	        	var left = dom.find("#zhcs_OA_chatWindowLeft");
	        	
	        	left.find("ul li").removeClass("current");
	        	left.find("ul").append(this.createChatWindowLeft(id, type, name,groupId));
	        	if(left.find("ul li").length > 1){
		        	left.show();
	        	}else{
	        		left.hide();
	        	}
	        },
	        /**
	         * 加载聊天窗口界面
	         * @param userIds
	         * @param isAll
	         * @param name
	         * @param targetId
	         */
	        loadChatWindow : function(groupId,userIds,visible,type,name,targetId){
	        	var self = this;
	        	
	        	var chatWindow = $("div#zhcs_OA_chatWindow");
	        	if(chatWindow.length > 0){//聊天窗口已经存在
	        		self.refreshChatWindowTop(chatWindow,{type:type,name:name,targetId:targetId});
	        		self.loadChatWindowLeft(chatWindow,targetId,type,name,groupId);
	        		self.loadChatWindowContent(chatWindow,type, targetId, null,groupId);
	        		self.loadChatWindowRight(chatWindow,userIds,visible,type,targetId);
	        	}else{
	        		var html = $("<div></div>");
		        	html.load(context+"/task/loadChat.htm",function(){
		        		self.refreshChatWindowTop(html,{type:type,name:name,targetId:targetId});
		        		self.loadChatWindowLeft(html,targetId,type,name,groupId);
		        		self.loadChatWindowContent(html,type, targetId, null,groupId);
		        		self.loadChatWindowRight(html,userIds,visible,type,targetId);
						$(document.body).append(html.html());
						var top = $("#zhcs_OA_chatWindowTop");
						top.on("click","a.icon-cancel",function(){
							$(this).closest("div#zhcs_OA_chatWindow").remove();
							return false;
						});
						top.data("groupId",targetId);
			        	top.data("type",type);
			        	top.data("id",targetId);
					});
	        	}
	        },
			/**
			 * 打开聊天窗口
			 * @param targetId
			 * @param visible
			 * @param type
			 * @param url
			 * @param name
			 */
			openChatWindow : function(targetId,visible,type,url,name){
				var self = this;
				if(visible  == taskVisibleStatus){//任务所有人可见
					self.loadChatWindow("",null,visible,type,name,targetId);
				}else{//任务仅参与者可见，获得参与者数据
					$.ajax({
	    				type: 'post',
	    				data: {taskId: targetId},
	    				url: context + '/task/findTaskUser.htm'
	    			}).done(function(data){
	    				if(data.resultData && data.success){
	    					var userIds = data.resultData;
	    					var flg = false;
	    					
	    					$.each(userIds,function(k,v){
	    						if(v == loginUserInfo.userId){
	    							flg = true;
	    							return false;
	    						}
	    					});
	    					if(flg){
	    						self.loadChatWindow("",userIds,visible,type,name,targetId);
	    					}else{
	    						layer.msg("不属于聊天人员");
	    					}
	    					
	    				}else{
	                        layer.msg("请求失败");
	                    }
	    			}).fail(function(){
	    				layer.msg("请求失败");
	    			});
				}
			},
			/**
			 * 聊天内容模板
			 * @param type 0:文字 1:图片 2:文件
			 * @param isRight 是否是自己
			 * @param time 发送的时间
			 * @param content 发送的内容
			 * @param fromUserId 是谁发送的消息
			 * @returns {String}
			 */
			createChatMessageHtml : function(type,isRight,time,content,fromUserId){
				var str = "";
				if(isRight){
					str+="<li class='last right'>";
					str+="<p class='time'>"+new Date(time).format('yyyy-MM-dd hh:mm:ss')+"</p>";
				}else{
					str+="<li class='last left'>";
					str+="<p class='time'><a>"+this.getUserInfo(fromUserId).name+"&nbsp;</a>"+new Date(time).format('yyyy-MM-dd hh:mm:ss')+"</p>";
				}
				if(type == 0){
					str+="<div class='post-content text'>";
					str+="<p>"+ content.replace(/\[em_([1-9]|[1][0-6])\]/gi,'<img src="'+context+'/static/images/expression/$1.gif" border="0" />')+"</p>";
				}else if(type == 1){
					str+="<div class='post-content picture'>";
					str+="<img src='"+content+"'>";
				}else{
					str+="<div class='post-content file'>";
					str+=content;
				}
				
				str+="</div></li>";
				return str;
			},
			/**
			 * 判断文件大小，设置单位
			 */
			fileSizeUnit : function(fileSize){
				if (fileSize >= 1024 * 1024)  //大于1MB
					fileSize = (Math.round(fileSize * 100 / (1024 * 1024)) / 100).toString() + 'MB';  
				else  
					fileSize = (Math.round(fileSize * 100 / 1024) / 100).toString() + 'KB';
				return fileSize;
			}
	};
	return chartInterface;
});