require(["jquery", "layer"], function () {
    var list = {
    		container : $("#organizationContainer")
    };
    var teamAndUserDiv = $("#teamAndUserDiv");
    teamAndUserDiv.find("#listDiv div[data-userid="+loginUserInfo.userId+"]").find('.icon-down-open-big').hide();

    list.init = function () {
        list.search();
        list.show();
        list.setEvent();
        list.showAllTeam();
        list.setOrRemoveAdmin();
    };
    list.setOrRemoveAdmin = function(){
    	layer.closeAll();
    	teamAndUserDiv.find(".icon-down-open-big").on("click", function(){
    		var dom = $(this),
    			userId=  dom.closest('div').data("userid"),
    			teamId = teamAndUserDiv.find("#listDiv").data("team-id");
    		var top = (dom.offset().top - document.body.scrollTop)+"px";
			var left = dom.offset().left+"px";
			var isOrNotAdmin = dom.siblings(".owner-mark").length > 0 ? "block":"none";
			var isOrNotAdmin2 = dom.siblings(".owner-mark").length <= 0 ? "block":"none";
			var html = $("<div id='layerDiv'>"+  
	    	"<div class='layer-lg boardBackground floatLayer'>"+
	        "<div class='layer-title'>"+
        	"<h5>"+TEXT_CONFIG.chengyuan+"菜单</h5>"+
            "<i class='icon-cancel-circled-outline'></i>"+
        "</div>"+
    	"<ul>"+
            "<li style='display:"+isOrNotAdmin2+"'><a id='setAdmin'> <i class='icon-user-outline'></i>设为管理员</a></li>"+
            "<li style='display:"+isOrNotAdmin+"'><a id='removeAdmin'> <i class='icon-user-outline'></i>移除管理员</a></li>"+
        "</ul>"+
    "</div>"+
    "</div>");
			$.layer({
			    type: 1,
			    shade: [0],
			    area: ['auto', 'auto'],
			    title: false,
			    border: [0],
			    closeBtn: false,
			    offset:[top,left],
			    fix:false,
			    page: {html:html.html()},
		    	success:function(layero)
		    	{
		    		$(layero).click(function(){
		    			return false;
		    		});
		    		
		    		$(layero).on("click",".icon-cancel-circled-outline",function(){
		    			layer.closeAll();
		    		});
		    		$(layero).on("click","#setAdmin",function(){
		    			$.ajax({
		    				url:context + "/team/setTeamAdmin.htm",
		    				data:{teamId:teamId,userId:userId},
		    				type:"post",
		    				success:function(resultData)
		    				{
		    					if(resultData && resultData.success){
		    						layer.closeAll();
		    						dom.after('<a class="owner-mark " data-title="拥有者"><i class="icon-crown"></i></a>');
		    					}else if(resultData && resultData.errormsg){
		    						layer.alert(resultData.errormsg, 1);
		    					}else{
		    						layer.alert('请求失败', 1);
		    					}
		    				}
		    			})
		    		});
		    		$(layero).on("click","#removeAdmin",function(){
		    			$.ajax({
		    				url:context + "/team/removeTeamAdmin.htm",
		    				data:{teamId:teamId,userId:userId},
		    				type:"post",
		    				success:function(resultData)
		    				{
		    					if(resultData && resultData.success){
		    						layer.closeAll();
		    						dom.siblings(".owner-mark").remove();
		    					}else if(resultData && resultData.errormsg){
		    						layer.alert(resultData.errormsg, 1);
		    					}else{
		    						layer.alert('请求失败', 1);
		    					}
		    				}
		    			})
		    		});
		    		
		    	}
			});
    	});
    	
    };

    list.setEvent = function () {
        $("#listDiv").on('click', '#teamUser', function () {
            if (!$("#teamUser").hasClass("select")) {
                $.ajax({
                    url: context + "/team/teamUser.htm",
                    data: {
                        teamId: $("#listDiv").data("team-id")
                    },
                    type: "post",
                    success: function (resultData) {
                        if (resultData && resultData.success) {
                        	var users = resultData.resultData.users;
                            if ($("#orgUser").hasClass("select")) {
                                $("#orgUser").removeClass("select");
                            }
                            $("#teamUser").addClass("select");
                            $("#listDiv").find("div.user-block").remove();
                            $("#teamUser").html("现有"+TEXT_CONFIG.tuandui+"人员(" + users.length + ")")
                            $.each(users, function (key, value) {
                            	var name = value.name;
                            	if(name.length > 4){
                            		name = name.substr(0,4)+"...";
                            	}
                            	var userDiv = $('<div class="user-block corner-6" data-userid=' + value.userId + '>' + '<img src=' + value.logo + ' class="img-circle"/>' 
                            			+ '<h4 title="value.name">' +name + '</h4></div>');
                            	if(resultData.resultData.type){
                            		userDiv.append("<i class='icon-down-open-big'></i>");
                            	}
                            	if(value.userId == loginUserInfo.userId){
                            		userDiv.find('.icon-down-open-big').remove();
                            	}
                                if(value.userRole=='TEAM_ADMIN'){
                                	userDiv.append('<a class="owner-mark " title="管理员" data-title="拥有者"><i class="icon-crown"></i></a>');
                                }
                            	$("#listDiv").append(userDiv);
                            	list.setOrRemoveAdmin();
                            });
                        }
                        else {
                            layer.alert('请求失败', 1);
                        }
                    }
                })
            }
        });

        $("#listDiv").on('click', '#orgUser', function () {
            if (!$("#orgUser").hasClass("select")) {
                $.ajax({
                    url: context + "/team/findUserFromOrg.htm",
                    data: {
                        orgId: $("#id").val(),
                        teamId: $("#listDiv").data("team-id")
                    },
                    type: "post",
                    success: function (resultData) {
                        if (resultData && resultData.success) {
                            if ($("#teamUser").hasClass("select")) {
                                $("#teamUser").removeClass("select");
                            }
                            var orgUsers = resultData.resultData.orgUsers;
                            var type = resultData.resultData.type;
                            $("#listDiv").find("div.user-block").remove();
                            $("#listDiv").attr("data-type", type?type:false);
                            $("#orgUser").addClass("select");
                            $.each(orgUsers, function (key, value) {
                            	var name=value.name;
                            	if(name.length >4){
                            		name = name.substr(0,4)+"...";
                            	}
                                if (value.belongTeam) {
                                    $("#listDiv").append('<div data-rolecode="'+value.userRole+'" class="user-block corner-6 select" data-userid=' + value.userId + '>' + '<img src=' + value.logo + ' class="img-circle"/>' + '<h4 title="'+value.name+'">' + name + '</h4>' + '<i class=" icon-ok-2"></i>' + '</div>');
                                }
                                else {
                                    $("#listDiv").append('<div data-rolecode="'+value.userRole+'" class="user-block corner-6" data-userid=' + value.userId + '>' + '<img  src=' + value.logo + ' class="img-circle"/>' + '<h4 title="'+value.name+'">' + name + '</h4>' + '</div>');
                                }

                            });

                        } else if (resultData && resultData.errormsg) {
                            layer.alert(resultData.errormsg, 1);
                        } else {
                            layer.alert('请求失败', 1);
                        }
                    }
                })
            }




            return false;
        });

        $("#listDiv").on('click', 'div.user-block', function () {
        	var thisUser = $(this);
        	if(!$("#orgUser").hasClass("select"))
        	{
        		return false;
        	}

            var dom = $(this);

            var params = {
                userId: $(this).data("userid"),
                teamId: $("#listDiv").data("team-id")
            }
            if(params.userId == loginUserInfo.userId){
            	return false;
            }
            if (dom.hasClass("select")) {
            	
            	var type = $("#listDiv").data("type");
            	if(!type && $(this).data("rolecode")=="TEAM_ADMIN"){
            		layer.alert("无权限");
            		return false;
            	}
                $.ajax({
                    url: context + "/team/delUser.htm",
                    data: params,
                    type: "post",
                    success: function (resultData) {
                        if (resultData && resultData.success) {
                        	var userNum = $("#teamUser").text().replace("现有"+TEXT_CONFIG.tuandui+"人员","").replace("(","").replace(")","")-1;
                            $("#teamUser").html("现有"+TEXT_CONFIG.tuandui+"人员(" + userNum + ")")
                            dom.removeClass("select");
                            dom.find("i.icon-ok-2").remove();
                            return;
                        } else {
                            layer.alert('请求失败', 1);
                        }
                    }
                });
            }else{
                $.ajax({
                    url: context + "/team/addUser.htm",
                    data: params,
                    type: "post",
                    success: function (resultData) {
                        if (resultData && resultData.success) {
                            var userNum = $("#teamUser").text().replace("现有"+TEXT_CONFIG.tuandui+"人员","").replace("(","").replace(")","")*1+1;
                            $("#teamUser").html("现有"+TEXT_CONFIG.tuandui+"人员(" + userNum + ")")
                            dom.addClass("select");
                            dom.find("h4").after('<i class=" icon-ok-2"></i>');
                            return false;
                        } else {
                            layer.alert('请求失败', 1);
                        }
                    }
                })
            }
            return false;
        });

        $("#teamDiv").on('mouseover', 'li', function () {
        	$(this).find("button").show();
        })

        $("#teamDiv").on('mouseout', 'li', function () {
        	$(this).find("button").hide();
        });

        $("#teamDiv").on('click', 'button', function () {
            var s = $(this);
            var params = {
                proId: $(this).parent().data('project-id'),
                teamId: $("#listDiv").data("team-id")
            }

            $.ajax({
                url: context + "/project/removeTeamByPro.htm",
                data: params,
                type: "post",
                success: function (resultData) {
                    if (resultData && resultData.success) {
                        s.parent().remove();
                        if ($("#show").children('li').length <= 0) {
                            $("#show").attr("hidden", true);
                            $("#info").show();
                        }
                        var proNum = $("#teamDiv").find("h4").html().replace(""+TEXT_CONFIG.tuandui+"参与的"+TEXT_CONFIG.xiangmu,"").replace("(","").replace(")","") - 1;
                        $("#teamDiv").find("h4").html(""+TEXT_CONFIG.tuandui+"参与的"+TEXT_CONFIG.xiangmu+"(" + proNum + ")");
                    } else if (resultData && resultData.errormsg) {
                        layer.alert(resultData.errormsg, 1);
                    } else {
                        layer.alert('请求失败', 1);
                    }
                }

            })


        })
    }

    list.show = function () {
        if ($("#show").children('li').length > 0) {
            $("#show").attr("hidden", false);
            $("#info").hide();
        }
        else {
            $("#show").attr("hidden", true);
        }
    }

    list.search = function () {
        $("#list").on("click", "li", function () {
            $("#search").focus();
            var name = $(this).find("a").html(),
                id = $(this).data("project-id"),
                src = $(this).find("img").attr("src");
            $.ajax({
                url: context + "/team/addPro.htm",
                data: {
                    proId: $(this).data("project-id"),
                    teamId: $("#listDiv").data("team-id")
                },
                type: "post",
                success: function (resultData) {
                    if (resultData && resultData.success) {
                        $("#info").hide();
                        $("#show").append('<li data-project-id=' + id + '><img src=' + src + '  class="corner-3"> <a>' + name + ' </a><button class="btn pull-right outer-borer" style="display:none">移除</button> </li>')
                        $("#list").attr("hidden", true);
                        $("#list").empty();
                        list.show();
                        var proNum = $("#teamDiv").find("h4").html().replace(""+TEXT_CONFIG.tuandui+"参与的"+TEXT_CONFIG.xiangmu,"").replace("(","").replace(")","")*1 + 1;
                        $("#teamDiv").find("h4").html(""+TEXT_CONFIG.tuandui+"参与的"+TEXT_CONFIG.xiangmu+"(" + proNum + ")");
                    } else if (resultData && resultData.errormsg) {
                        layer.alert(resultData.errormsg, 1);
                    } else {
                        layer.alert('请求失败', 1);
                    }
                }
            });
            return false;
        });
        $(window).on("click", function () {
            $("#list").empty();
            $("#list").attr("hidden", true);
            return false;
        });

        $("#search").keydown(function(e){
        	if(e.keyCode==13){
        	      if ($("#list").children('li').length <= 0) {
                      $.ajax({
                          url: context + "/org/findPro.htm",
                          data: {
                          	  orgId:$("#id").val(),
                              orgName: $("#search").val(),
                              teamId: $("#listDiv").data("team-id")
                          },
                          type: "post",
                          success: function (resultData) {

                              if (resultData && resultData.success) {
                                  if (resultData.resultData != "") {
                                      $("#list").attr("hidden", false);

                                      $.each(resultData.resultData, function (key, value) {
                                          $("#list").append('<li data-project-id=' + value.projectId + '><img src="'+ value.logoStr + '"   class="corner-3"> <a>' + value.name + ' </a></li>')
                                      })

                                  }
                              } else if (resultData && resultData.errormsg) {
                                  layer.alert(resultData.errormsg, 1);
                              } else {
                                  layer.alert('请求失败', 1);
                              }
                          }
                      })
                  }
        	}
        });
    },
    //
    list.showAllTeam = function () {
    	var self=this;
    	$('#allTeams').on('click',function(){
    		self.container.load(context+"/team/list.htm?id="+$("#id").val());
    		return false;
    	});
    },

    list.init();
})