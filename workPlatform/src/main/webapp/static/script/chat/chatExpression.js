define(['jquery'],function(){
	var expression = {
			init : function(option){
				
				var defaults = {
						tab : "",//表情要放的Table 或者 Div
						ele:"",//textArea输入框
						col : 4,//一共有几列
						num : 16,//表情个数
						gifUrl : "/static/images/expression/",
						text : ["微笑","流泪","龇牙","害羞","睡","流汗","憨笑","No","再见","拥抱","月亮","强","握手","胜利","抱拳","Ok"]//图标解释
				};
				this.options = $.extend(defaults, option || {});
				if($(this.options.tab).length < 1 || $(this.options.ele).length  < 1 || this.options.text.length  < 1 || this.options.col > this.options.num){
//					try{
//						throw "数据错误！";
//					}catch(e){
						return false;
					//}
				}
				this.loadImg();
				this.setEvent();
			},
			/**
			 * 加载表情页面
			 */
			loadImg : function(){
				$(this.options.tab).find("tbody").append(this.createExpressionHtml());
			},
			/**
			 * 表情页面代码
			 * @returns {String}
			 */
			createExpressionHtml : function(){
				var str = "<tr>";
				var gifUrl = this.options.gifUrl;
				for(var i=0 ; i<this.options.num;i++){
					if(i%this.options.col == 0 && i){
						str+="</tr><tr>" + "<td class='face-img-td' data-index='"+i+"'><div class='wrapper-td'><img src='"+context+gifUrl+parseInt(i+1)+".gif' title='"+ this.options.text[i]+"'></div></td>";
					}else{
						str+="<td class='face-img-td' data-index='"+i+"'><div class='wrapper-td'><img src='"+context+gifUrl+parseInt(i+1)+".gif' title='"+ this.options.text[i]+"'></div></td>";
					}
				}
				str+="</tr>";
				return str;
			},
			/**
			 * 事件
			 */
			setEvent : function(){
				var self = this;
				/**
				 * 选中表情
				 */
				$(this.options.tab).on("click","td", function(){
					$(self.options.ele).val($(self.options.ele).val()+"[em_"+parseInt($(this).data("index")+1)+"]");
				});
				/**
				 * 点击其他区域关闭表情窗口
				 */
				$(window).on("click",function(event){
					$(self.options.tab).addClass("g-hide");
				});
			}
	};
	return expression;
});