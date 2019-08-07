/**
人员搜索和人员选择的插件
**/
define(["jquery","jqueryUI","layer"],function(){
	var options= {};
	var personLayer={
		init : function(option){
			var defaults = {
					searchInput:false,//是否需要搜索框
					data : {},//人员数据
					everyone : false,//是否显示所有人
					claiming : false,//是否先是待认领,要选中未认领时，selectData有一个值为0就可以了
					selectData : [],//要选中的数据
					dom : "",
					container : "",
					isClose : false,//是否选中人员时，关闭弹出层
					eventType : ""
				};
			options = $.extend(defaults, option || {});
			this.createLayer();
		},
		createLayer : function(){
			var self = this;
			if(!options.dom || !options.container){
				return;
			}else{
				var position = options.dom.offset();
				var positionTop = position.top + options.dom.height()-$(window).scrollTop() + 10 + "px";
				var positionLeft = position.left + "px";
				//alert("positionTop = " + positionTop + "\n" + "positionLeft = " + positionLeft);
				$("div#selectUserLayer").closest(".xubox_layer").remove();
				$.layer({
					type: 1,
					//fix:false,
					title: false,
                    area: ['auto', 'auto'],
                    zIndex : 7000,
                    offset: [positionTop, positionLeft],
                    shade: [0],
                    border: [0],
                    closeBtn: [0],
					page : {
						html : self.createLayerHtml()
					},
					success : function(layero){
						self.layerEvent(layero);
						self.settingPosition(layero);
						document.documentElement.style.overflow = "hidden";
					},
					end : function(layero) {
						document.documentElement.style.overflow = "";
					}
				});
			}
		},
		settingPosition : function(layero){
            var winH = $(window).height(),winW = $(window).width(),
            optionsOffSet = $(options.dom).offset(),
            layeroH = layero.height(),layeroW = layero.width();
            if(layeroH > winH-optionsOffSet.top){
                $(layero).css("top",optionsOffSet.top-layeroH-$(window).scrollTop());
            }
            if(winW - optionsOffSet.left < layeroW){
                $(layero).css("left",optionsOffSet.left-layeroW-$(window).scrollLeft());
            }
        },
		layerEvent : function(layero){
			$(layero).on("click",function(){
				return false;
			});
			if(options.isClose){
				$(layero).find("li").on("click",function(event){
					var id=$(this).data("id");
					var li = $(this);
					if(options.eventType == ""){
						$(options.container).html("<a><img data-id='"+id+"' src='"+li.find("img").attr("src")+"' class='img-circle'>"+li.text()+"</a>");
						layer.close(layer.index);
					}else{
						options.dom.trigger(options.eventType,[{isSelect:true,ids:[id],callback:function(){
							$(options.container).html("<a><img data-id='"+id+"' src='"+li.find("img").attr("src")+"' class='img-circle little-pic'>"+li.text()+"</a>");
							layer.close(layer.index);
						}}]);
					}
					return false;
				});
			}else{
				$(layero).find("li").on("click",function(event){
					var li = $(this);
					if($(this).hasClass('allUsers')){
						$(options.container).prevAll().remove();
						var str="";
						var arrId = [];
						$.each(options.data,function(k,value){
							if(value.userEnabled){
								arrId.push(value.userId);
								str+="<li data-id='"+value.userId+"' title='"+value.name+"'><img src='"+value.logo+"'  class='img-circle'><a class='remove-member-handler'>×</a></li>";
							}
						});
						if(options.eventType == ""){
							$(options.container).closest("ul").prepend(str);
							layer.close(layer.index);
						}else{
							options.dom.trigger(options.eventType,[{isSelect:true,ids:arrId,callback:function(){
								$(options.container).closest("ul").prepend(str);
								layer.close(layer.index);
							}}]);
						}
					}else{
						var select=false;
						if($(this).find("i.icon-ok").is(":hidden")){
							select = true;
						}else{
							select = false;
						}
						if(options.eventType == ""){
							if(select){
								$(this).find("i.icon-ok").show();
								$(options.container).before("<li data-id='"+$(this).data("id")+"'  title='"+$(this).text()+"'><img src='"+$(this).find("img").attr("src")+"' class='img-circle'><a class='remove-member-handler'>×</a></li>");
							}else{
								$(options.container).closest("ul").find("li[data-id='"+$(this).data("id")+"']").remove();
								$(this).find("i.icon-ok").hide();
							}
						}else{
							options.dom.trigger(options.eventType,[{isSelect:select,ids:[li.data("id")],callback:function(){
								if(select){
									li.find("i.icon-ok").show();
									$(options.container).before("<li data-id='"+li.data("id")+"'  title='"+li.text()+"'><img src='"+li.find("img").attr("src")+"' class='img-circle'><a class='remove-member-handler'>×</a></li>");
								}else{
									$(options.container).closest("ul").find("li[data-id='"+li.data("id")+"']").remove();
									li.find("i.icon-ok").hide();
								}
							}}]);
						}
					}
					return false;
				});
			}
			if(options.searchInput){
				$(layero).on("change paste keyup", "input", function(){
					var value = $(this).val();
					if(value == ""){
						 $(layero).find("li").show();
					}else{
						$(layero).find("li").hide();
						$(layero).find("li:contains('"+value+"')").show();
					}
				});
			};
		},
		createLayerHtml : function(){
			var strHtml = "<div id='selectUserLayer' class='layer-sm-noscroll boardBackground floatLayer little-portrait'>";
			if(options.searchInput){
				strHtml+="<div class='menu-input'><input class=' form-control' type='text' data-type='name'></div>";
			};
			strHtml+="<ul class='vertical-scroll-min scroll-area'>";
			if(options.everyone){
				strHtml+="<li data-id='-1' class='allUsers'><i class='icon-users-1'></i>所有"+TEXT_CONFIG.chengyuan+"</li>";
			};
			if(options.claiming){
				if($.inArray("0", options.selectData) >= 0){
					strHtml+="<li data-id='0' class='noUser'><img src='/workPlatform/static/images/pic-shelters.png' class='img-circle' >待认领<i class='icon-ok'></i></li>";
				}else{
					strHtml+="<li data-id='0' class='noUser'><img src='/workPlatform/static/images/pic-shelters.png' class='img-circle' >待认领<i class='icon-ok' style='display: none;'></i></li>";
				}
			};
			if(!options.data || options.data.length < 1){

			}else{
				if(options.data){
					if(options.selectData.length > 0){
						$.each(options.data,function(k,value){
							var id = value.userId+"";
							if(value.userEnabled){
								if($.inArray(id, options.selectData) >= 0){
									strHtml+="<li data-id='"+id+"'><img src='"+value.logo+"' class='img-circle' > "+value.name+"<i class='icon-ok'></i></li>";
								}else{
									strHtml+="<li data-id='"+id+"'><img src='"+value.logo+"' class='img-circle' > "+value.name+"<i class='icon-ok' style='display: none;'></i></li>";
								}
							}
						});
					}else{
						$.each(options.data,function(k,value){
							if(value.userEnabled){
								var id = value.userId+"";
								strHtml+="<li data-id='"+id+"'><img src='"+value.logo+"' class='img-circle' '> "+value.name+"<i class='icon-ok' style='display: none'></i></li>";
							}
						});
					}
				}
			}
			strHtml+="</ul></div>";
			return strHtml;
		}
	};
	return personLayer;
});