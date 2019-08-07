require(["jquery", "highcharts"], function() {

    var panel = $("div[data-id='organizationTemplate']");
    var statistics = {
        init : function() {
            var showIndex = 1;
            this.staffTaskStatisticsChart = panel.find("#staffTaskStatisticsChart");//任务--成员
            this.projectTaskStatisticsChart = panel.find("#projectTaskStatisticsChart");//任务--项目
            this.teamTaskStatisticsChart = panel.find("#teamTaskStatisticsChart");//任务--团队
            this.staffWTimeStatisticsChart = panel.find("#staffWTimeStatisticsChart");//工时--成员
            this.projectWTimeStatisticsChart = panel.find("#projectWTimeStatisticsChart");//工时--项目
            this.teamWTimeStatisticsChart = panel.find("#teamWTimeStatisticsChart");//工时--团队
            this.color = ["#058DC7", "#99CC66", "#CCFF66", "#FF0000"];
            this.typeName = ["总数", "已完成", "未完成", "逾期"];
            this.typeNameByTime = ["计划工时", "实际工时"];
            this.setTaskEvent();//任务数
            this.setWorktimeEvent();//工时
            this.getUserStatisticsDataByTask(showIndex, true);
            this.getUserStatisticsDataByTime(showIndex, true);
            panel.find("#staffStatisticsMenu span").data("value", showIndex);
            panel.find("#staffWorkStatisticsMenu span").data("value", showIndex);
           
            
        },
        setTaskEvent : function() {
            var self = this;
            panel.find("#userStatistics ").on("click","li", function(){
            	var userStaType = $(this);
            	if(userStaType.hasClass("active")){
            		return false;
            	}
            	var type = panel.find("#staffStatisticsMenu span").data("value")
            	userStaType.siblings('li').removeClass('active')
            				.end().addClass('active');
            	if(userStaType.data("type") == "user"){
            		self.staffTaskStatisticsChart.show();
            		self.projectTaskStatisticsChart.hide();
            		self.teamTaskStatisticsChart.hide();
            		self.getUserStatisticsDataByTask(type, true);
            		return false;
            	}else if(userStaType.data("type") == "project"){
            		self.staffTaskStatisticsChart.hide();
            		self.projectTaskStatisticsChart.show();
            		self.teamTaskStatisticsChart.hide();
            		self.getProjectStatisticsData(type, true);
            		return false;
            	}else if(userStaType.data("type") == "team"){
            		self.staffTaskStatisticsChart.hide();
            		self.projectTaskStatisticsChart.hide();
            		self.teamTaskStatisticsChart.show();
            		self.getTeamStatisticsData(type, true);
            		return false;
            	}
            	
            });
            panel.on("click", "#staffStatisticsMenu", function() {
                // $(this).find(".icon-ok").hide();
                $(this).find("ul li:eq('" + $(this).find("span").data("value") + "')").find(".icon-ok").show();
                $(this).find("ul").show();
                return false;
            });
            panel.on("click", "#staffStatisticsMenu li", function() {
                if ($(this).index() == panel.find("#staffStatisticsMenu span").data("value")) {
                    $(this).closest("ul").hide();
                    return false;
                }
                var exprotType = panel.find("#userStatistics li.active").data('type'),
                	loadType = $(this).index();
				if(exprotType == "user"){//成员
					self.getUserStatisticsDataByTask(loadType, false);
				}else if(exprotType == "project"){//项目
					self.getProjectStatisticsData(loadType, false);
				}else if(exprotType == "team"){
					self.getTeamStatisticsData(loadType, false);
				}
                panel.find("#staffStatisticsMenu span").text($(this).text()).data("value", $(this).index());
                $(this).siblings().find(".icon-ok").hide();
                $(this).closest("ul").hide();
                return false;
            });
            $(document).on("click", function(event) {//关闭所有的浮出层
                panel.find("#staffStatisticsMenu").find("ul").hide();
            });
			panel.on("click","#xmTjExport",function(){//任务导出
				var type = panel.find("#staffStatisticsMenu span").data("value");
				var exprotType = panel.find("#userStatistics li.active").data('type');
				if(exprotType == "user"){//成员
					self.exportUsersTongJi(type);
				}else if(exprotType == "project"){//项目
					self.exportProjectsTongJi(type);
				}else if(exprotType == "team"){
					self.exportTeamTongJi(type);
				}
				return ;
			});
        },
        setWorktimeEvent : function(){
        	 var self = this;
             panel.find("#wTimeStatistics").on("click"," li", function(){
             	var userStaType = $(this);
             	if(userStaType.hasClass("active")){
             		return false;
             	}
             	var type = panel.find("#staffWorkStatisticsMenu span").data("value");
             	userStaType.siblings('li').removeClass('active')
             				.end().addClass('active');
             	if(userStaType.data("type") == "user"){
             		self.staffWTimeStatisticsChart.show();
             		self.projectWTimeStatisticsChart.hide();
             		self.teamWTimeStatisticsChart.hide();
             		self.getUserStatisticsDataByTime(type, true);
             		return false;
             	}else if(userStaType.data("type") == "project"){
             		self.staffWTimeStatisticsChart.hide();
             		self.projectWTimeStatisticsChart.show();
             		self.teamWTimeStatisticsChart.hide();
             		self.getProjectStatisticsDataByTime(type, true);
             		return false;
             	}else if(userStaType.data("type") == "team"){
             		self.staffWTimeStatisticsChart.hide();
             		self.projectWTimeStatisticsChart.hide();
             		self.teamWTimeStatisticsChart.show();
             		self.getTeamStatisticsDataByTime(type,true);
             		return false;
             	}
             	
             });
             panel.on("click", "#staffWorkStatisticsMenu", function() {
            	 $(this).find("ul li:eq('" + $(this).find("span").data("value") + "')").find(".icon-ok").show();
                 $(this).find("ul").show();
                 return false;
             });
             panel.on("click", "#staffWorkStatisticsMenu li", function() {
                 if ($(this).text() == panel.find("#staffWorkStatisticsMenu span").text()) {
                     $(this).closest("ul").hide();
                     return false;
                 }
                 var exprotType = panel.find("#wTimeStatistics li.active").data('type'),
                 	loadType = $(this).index();
  				if(exprotType == "user"){//成员
  					self.getUserStatisticsDataByTime(loadType, false);
  				}else if(exprotType == "project"){//项目
  					self.getProjectStatisticsDataByTime(loadType, false);
  				}else if(exprotType == "team"){
  					self.getTeamStatisticsDataByTime(loadType,false);
  				}
                 panel.find("#staffWorkStatisticsMenu span").text($(this).text()).data("value", $(this).index());
                 $(this).siblings().find(".icon-ok").hide();
                 $(this).closest("ul").hide();
                 return false;
             });
             $(document).on("click", function(event) {//关闭所有的浮出层
                 panel.find("#staffWorkStatisticsMenu").find("ul").hide();
             });
             panel.on("click","#xmTjExportByTime",function(){//工时导出
 				var type = panel.find("#staffWorkStatisticsMenu span").data("value");
 				var exprotType = panel.find("#wTimeStatistics li.active").data('type');
 				if(exprotType == "user"){//成员
 					self.exportUsersTongJiByTime(type);
 				}else if(exprotType == "project"){//项目
 					self.exportProjectsTongJiByTime(type);
 				}else if(exprotType == "team"){
 					self.exportTeamTongJiByTime(type);
 				}
 				return ;
 			});
        },

		exportUsersTongJi : function(type) {
			var orgId = panel.data("organizationid");
			window.location.href = context+"/org/exportOrganizationStatistics_user.htm?orgId=" + orgId + "&type="+type;
		},
		exportProjectsTongJi : function(type) {
			var orgId = panel.data("organizationid");
			window.location.href = context+"/org/exportOrganizationStatistics_project.htm?orgId=" + orgId + "&type="+type;
		},
		exportTeamTongJi : function(type) {
			var orgId = panel.data("organizationid");
			window.location.href = context+"/org/exportOrganizationStatistics_team_task.htm?orgId=" + orgId + "&type="+type;
		},
		exportUsersTongJiByTime : function(type) {
			var orgId = panel.data("organizationid");
			window.location.href = context+"/org/exportOrganizationStatistics_user_time.htm?orgId=" + orgId + "&type="+type;
		},
		exportProjectsTongJiByTime : function(type) {
			var orgId = panel.data("organizationid");
			window.location.href = context+"/org/exportOrganizationStatistics_project_time.htm?orgId=" + orgId + "&type="+type;
		},
		exportTeamTongJiByTime : function(type) {
			var orgId = panel.data("organizationid");
			window.location.href = context+"/org/exportOrganizationStatistics_team_time.htm?orgId=" + orgId + "&type="+type;
		},

        getUserStatisticsDataByTask : function(type, isFrist) {//获得人的统计数据 type:0:最近一周;1:最近一个月;2:所有
            var self = this;
            $.ajax({
                url : context + "/org/findOrganizationStatistics_user.htm",
                data : {
                    "orgId" : panel.data("organizationid"),
                    "loadType" : type
                },
                type : "post"
            }).done(function(result) {
                if (result && result.success && result.resultData) {
                    var data = result.resultData;
                    if (isFrist) {
                        var height = (data.length * 100 + 120) + "px";
                        panel.find("#staffTaskStatisticsChart").css({
                        	"min-width" : "400px",
                            "max-width":"400px",
                            "height" : height,
                            "margin-top" : "20px"
                        });
                        self.setUserStatisticsChartByTask(data);
                    } else {
                        self.refreshUserStatisticsChart(data);
                    }
                } else {
                    layer.msg(result.errormsg);
                }
            }).fail(function(result) {
                layer.msg("请求失败");
            });
        },
        refreshUserStatisticsChart : function(data) {
            var lastData = this.dataAssembledByTask(data);
            this.userStatisticsChart.series[0].setData(lastData.overdue);
            this.userStatisticsChart.series[1].setData(lastData.noDone);
            this.userStatisticsChart.series[2].setData(lastData.over);
            this.userStatisticsChart.series[3].setData(lastData.all);
        },
        dataAssembledByTask : function(data) {//任务 人员数据组装
            var retData = {};
            var all = [];
            //任务总数
            var over = [];
            //已完成的任务个数
            var noDone = [];
            //未完成的任务个数
            var overdue = [];
            //逾期的任务个数
            var name = [];

            $.each(data, function(key, value) {
                var taskCount = value.taskCount - value.noTaskCount;
                all.push(value.taskCount);
                over.push(taskCount < 0 ? 0 : taskCount);
                noDone.push(value.noTaskCount);
                overdue.push(value.tardyTaskCount);
                name.push(value.name);
            });
            retData.all = all;
            retData.over = over;
            retData.noDone = noDone;
            retData.overdue = overdue;
            retData.name = name;
            return retData;
        },
        dataAssembledByTime : function(data) {//工时 人员数据组装
            var retData = {};
            //计划工时
            var planDay = [];
            //实际工时
            var realityDay = [];
            var name = [];

            $.each(data, function(key, value) {
                planDay.push(value.planDay);
                realityDay.push(value.realityDay);
                name.push(value.name);
            });
            retData.planDay = planDay;
            retData.realityDay = realityDay;
            retData.name = name;
            return retData;
        },
        setUserStatisticsChartByTask : function(data) {
            var self = this;
            var seriesData = self.dataAssembledByTask(data);
            this.userStatisticsChart = new Highcharts.Chart({
                chart : {
                    renderTo : 'staffTaskStatisticsChart',
                    type : 'bar'
                },
                title : {
                    text : ''
                },
                xAxis : {
                    categories : seriesData.name,
                    title : {
                        text : null
                    },
                    labels : {
                        overflow : 'justify',
                        formatter : function() {
                            var name = this.value;
                            return self.getStrActualLen(name, 8);
                        }
                    }
                },
                yAxis : {
                    min : 0,
                    title : {
                        text : '任务个数',
                        align : 'high'
                    },
                    labels : {
                        overflow : 'justify'
                    },
                    allowDecimals : false
                },
                tooltip : {
                    shared : true,
                    formatter : function() {
                        var str = "";
                        var all = this.points[3].y;
                        str += '<b>' + this.x + ': ';
                        if (all > 0) {
                            str += '<br/>' + '<tspan style="fill:' + self.color[0] + '" x="8" dy="16">●</tspan>' + this.points[3].series.name + ': ' + this.points[3].y;
                            str += '<br/>' + '<tspan style="fill:' + self.color[1] + '" x="8" dy="16">●</tspan>' + this.points[2].series.name + ': ' + this.points[2].y + "个,占" + parseInt(this.points[2].y / all * 100) + "%";
                            str += '<br/>' + '<tspan style="fill:' + self.color[2] + '" x="8" dy="16">●</tspan>' + this.points[1].series.name + ': ' + this.points[1].y + "个,占" + (100 - parseInt((all - this.points[1].y) / all * 100)) + "%";
                            str += '<br/>' + '<tspan style="fill:' + self.color[3] + '" x="8" dy="16">●</tspan>' + this.points[0].series.name + ': ' + this.points[0].y + "个,占" + parseInt(this.points[0].y / all * 100) + "%";
                        } else {
                            str += '<br/>' + '<tspan style="fill:' + self.color[0] + '" x="8" dy="16">●</tspan>' + this.points[3].series.name + ': ' + this.points[3].y;
                            str += '<br/>' + '<tspan style="fill:' + self.color[1] + '" x="8" dy="16">●</tspan>' + this.points[2].series.name + ': ' + this.points[2].y;
                            str += '<br/>' + '<tspan style="fill:' + self.color[2] + '" x="8" dy="16">●</tspan>' + this.points[1].series.name + ': ' + this.points[1].y;
                            str += '<br/>' + '<tspan style="fill:' + self.color[3] + '" x="8" dy="16">●</tspan>' + this.points[0].series.name + ': ' + this.points[0].y;
                        }
                        return str;
                    }
                },
                plotOptions : {
                    bar : {
                        dataLabels : {
                            enabled : true
                        },
                        pointWidth : 13,
                        borderWidth : 0
                        //pointPadding: 2,
                    },
                    column : {
                        stacking : 'normal'
                    }
                },
                credits : {
                    enabled : false
                },
                series : [{
                    name : self.typeName[3],
                    data : seriesData.overdue,
                    color : self.color[3]
                }, {
                    name : self.typeName[2],
                    data : seriesData.noDone,
                    color : self.color[2]
                }, {
                    name : self.typeName[1],
                    data : seriesData.over,
                    color : self.color[1]
                }, {
                    name : self.typeName[0],
                    data : seriesData.all,
                    color : self.color[0]
                }]
            });
        },
        getProjectStatisticsData : function(type,isFrist) {//获得项目统计数据
            var self = this;
            $.ajax({
                url : context + "/org/findOrganizationStatistics_project.htm",
                data : {
                    "orgId" : panel.data("organizationid"),
                    "loadType" : type
                },
                type : "post"
            }).done(function(result) {
                if (result && result.success && result.resultData) {
                    var data = result.resultData;
                    if (isFrist) {
                    	 var height = (data.length * 100 + 120) + "px";
                         panel.find("#projectTaskStatisticsChart").css({
                         	"min-width" : "400px",
                             "max-width":"400px",
                             "height" : height,
                             "margin-top" : "20px"
                         });
                         self.setProjectStatisticsChart(data);
                    } else {
                        self.refreshProjectStatisticsChartByTask(data);
                    }
                   
                } else {
                    layer.msg(result.errormsg);
                }
            }).fail(function(result) {
                layer.msg("请求失败");
            });
        },
        getTeamStatisticsData : function(type,isFrist) {//获得团队统计数据
            var self = this;
            $.ajax({
                url : context + "/org/findOrganizationStatistics_team.htm",
                data : {
                    "orgId" : panel.data("organizationid"),
                    "loadType":type
                },
                type : "post"
            }).done(function(result) {
                if (result && result.success && result.resultData) {
                    var data = result.resultData;
                    if (isFrist) {
                    	var height = (data.length * 100 + 120) + "px";
                        panel.find("#teamTaskStatisticsChart").css({
                            "min-width" : "400px",
                            "max-width":"400px",
                            "height" : height,
                            "margin-top" : "20px"
                        });
                        self.setTeamStatisticsChart(data);
                   } else {
                       self.refreshTeamStatisticsChartByTask(data);
                   }
                } else {
                    layer.msg(result.errormsg);
                }
            }).fail(function(result) {
                layer.msg("请求失败");
            });
        },
        setProjectStatisticsChart : function(data) {
            var self = this;
            var seriesData = self.dataAssembledByTask(data);
            this.projectStatisticsChart = new Highcharts.Chart({
            	
                chart : {
                    renderTo : 'projectTaskStatisticsChart',
                    type : 'bar'
                },
                title : {
                    text : ''
                },
                xAxis : {
                    categories : seriesData.name,
                    title : {
                        text : null
                    },
                    labels : {
                        overflow : 'justify',
                        formatter : function() {
                            var name = this.value;
                            return self.getStrActualLen(name, 8);
                        }
                    }
                },
                yAxis : {
                    min : 0,
                    title : {
                        text : '任务个数',
                        align : 'high'
                    },
                    labels : {
                        overflow : 'justify'
                    },
                    allowDecimals : false
                },
                tooltip : {
                    shared : true,
                    formatter : function() {
                        var str = "";
                        var all = this.points[3].y;
                        str += '<b>' + this.x + ': ';
                        if (all > 0) {
                            str += '<br/>' + '<tspan style="fill:' + self.color[0] + '" x="8" dy="16">●</tspan>' + this.points[3].series.name + ': ' + this.points[3].y;
                            str += '<br/>' + '<tspan style="fill:' + self.color[1] + '" x="8" dy="16">●</tspan>' + this.points[2].series.name + ': ' + this.points[2].y + "个,占" + parseInt(this.points[2].y / all * 100) + "%";
                            str += '<br/>' + '<tspan style="fill:' + self.color[2] + '" x="8" dy="16">●</tspan>' + this.points[1].series.name + ': ' + this.points[1].y + "个,占" + (100 - parseInt((all - this.points[1].y) / all * 100)) + "%";
                            str += '<br/>' + '<tspan style="fill:' + self.color[3] + '" x="8" dy="16">●</tspan>' + this.points[0].series.name + ': ' + this.points[0].y + "个,占" + parseInt(this.points[0].y / all * 100) + "%";
                        } else {
                            str += '<br/>' + '<tspan style="fill:' + self.color[0] + '" x="8" dy="16">●</tspan>' + this.points[3].series.name + ': ' + this.points[3].y;
                            str += '<br/>' + '<tspan style="fill:' + self.color[1] + '" x="8" dy="16">●</tspan>' + this.points[2].series.name + ': ' + this.points[2].y;
                            str += '<br/>' + '<tspan style="fill:' + self.color[2] + '" x="8" dy="16">●</tspan>' + this.points[1].series.name + ': ' + this.points[1].y;
                            str += '<br/>' + '<tspan style="fill:' + self.color[3] + '" x="8" dy="16">●</tspan>' + this.points[0].series.name + ': ' + this.points[0].y;
                        }
                        return str;
                    }
                },
                plotOptions : {
                    bar : {
                        dataLabels : {
                            enabled : true
                        },
                        pointWidth : 13,
                        borderWidth : 0
                        //pointPadding: 2,
                    },
                    column : {
                        stacking : 'normal'
                    }
                },
                credits : {
                    enabled : false
                },
                series : [{
                    name : self.typeName[3],
                    data : seriesData.overdue,
                    color : self.color[3]
                }, {
                    name : self.typeName[2],
                    data : seriesData.noDone,
                    color : self.color[2]
                }, {
                    name : self.typeName[1],
                    data : seriesData.over,
                    color : self.color[1]
                }, {
                    name : self.typeName[0],
                    data : seriesData.all,
                    color : self.color[0]
                }]
            });
        },
        setTeamStatisticsChart : function(data) {
            var self = this;
            var seriesData = self.dataAssembledByTask(data);
            this.teamStatisticsChart = new Highcharts.Chart({
                chart : {
                    renderTo : 'teamTaskStatisticsChart',
                    type : 'bar'
                },
                title : {
                    text : ''
                },
                xAxis : {
                    categories : seriesData.name,
                    title : {
                        text : null
                    },
                    labels : {
                        overflow : 'justify',
                        formatter : function() {
                            var name = this.value;
                            return self.getStrActualLen(name, 8);
                        }
                    }
                },
                yAxis : {
                    min : 0,
                    title : {
                        text : '任务个数',
                        align : 'high'
                    },
                    labels : {
                        overflow : 'justify'
                    },
                    allowDecimals : false
                },
                tooltip : {
                    shared : true,
                    formatter : function() {
                        var str = "";
                        var all = this.points[3].y;
                        str += '<b>' + this.x + ': ';
                        if (all > 0) {
                            str += '<br/>' + '<tspan style="fill:' + self.color[0] + '" x="8" dy="16">●</tspan>' + this.points[3].series.name + ': ' + this.points[3].y;
                            str += '<br/>' + '<tspan style="fill:' + self.color[1] + '" x="8" dy="16">●</tspan>' + this.points[2].series.name + ': ' + this.points[2].y + "个,占" + parseInt(this.points[2].y / all * 100) + "%";
                            str += '<br/>' + '<tspan style="fill:' + self.color[2] + '" x="8" dy="16">●</tspan>' + this.points[1].series.name + ': ' + this.points[1].y + "个,占" + (100 - parseInt((all - this.points[1].y) / all * 100)) + "%";
                            str += '<br/>' + '<tspan style="fill:' + self.color[3] + '" x="8" dy="16">●</tspan>' + this.points[0].series.name + ': ' + this.points[0].y + "个,占" + parseInt(this.points[0].y / all * 100) + "%";
                        } else {
                            str += '<br/>' + '<tspan style="fill:' + self.color[0] + '" x="8" dy="16">●</tspan>' + this.points[3].series.name + ': ' + this.points[3].y;
                            str += '<br/>' + '<tspan style="fill:' + self.color[1] + '" x="8" dy="16">●</tspan>' + this.points[2].series.name + ': ' + this.points[2].y;
                            str += '<br/>' + '<tspan style="fill:' + self.color[2] + '" x="8" dy="16">●</tspan>' + this.points[1].series.name + ': ' + this.points[1].y;
                            str += '<br/>' + '<tspan style="fill:' + self.color[3] + '" x="8" dy="16">●</tspan>' + this.points[0].series.name + ': ' + this.points[0].y;
                        }
                        return str;
                    }
                },
                plotOptions : {
                    bar : {
                        dataLabels : {
                            enabled : true
                        },
                        pointWidth : 13,
                        borderWidth : 0
                        //pointPadding: 2,
                    },
                    column : {
                        stacking : 'normal'
                    }
                },
                credits : {
                    enabled : false
                },
                series : [{
                    name : self.typeName[3],
                    data : seriesData.overdue,
                    color : self.color[3]
                }, {
                    name : self.typeName[2],
                    data : seriesData.noDone,
                    color : self.color[2]
                }, {
                    name : self.typeName[1],
                    data : seriesData.over,
                    color : self.color[1]
                }, {
                    name : self.typeName[0],
                    data : seriesData.all,
                    color : self.color[0]
                }]
            });
        },
        getStrActualLen : function(str, len) {//字母汉字长度截取
            var resultStr = "";
            if (str == null || str.length == 0) {
                return "";
            }
            if (str.replace(/[^\x00-\xff]/g, "xx").length > len) {
                for (var i = 0; resultStr.replace(/[^\x00-\xff]/g, "xx").length < len; i++) {
                    resultStr = str.substring(0, i);
                }
                resultStr = resultStr + "...";
            } else {
                resultStr = str;
            }

            return resultStr;
        },
        
        getUserStatisticsDataByTime : function(type, isFrist) {//获得人的统计数据 type:0:最近一周;1:最近一个月;2:所有
            var self = this;
            $.ajax({
                url : context + "/org/findOrganizationTimeStatistics_user.htm",
                data : {
                    "orgId" : panel.data("organizationid"),
                    "loadType" : type
                },
                type : "post"
            }).done(function(result) {
                if (result && result.success && result.resultData) {
                    var data = result.resultData;
                    if (isFrist) {
                        var height = (data.length * 100 + 120) + "px";
                        panel.find("#staffWTimeStatisticsChart").css({
                        	 "min-width" : "400px",
                             "max-width":"400px",
                             "height" : height,
                             "margin-top" : "20px"
                        });
                        self.setUserStatisticsChartByTime(data);
                    } else {
                        self.refreshUserStatisticsChartByTime(data);
                    }
                } else {
                    layer.msg(result.errormsg);
                }
            }).fail(function(result) {
                layer.msg("请求失败");
            });
        },
        setUserStatisticsChartByTime : function(data) {
            var self = this;
            var seriesData = self.dataAssembledByTime(data);
            this.userStatisticsChartByTime = new Highcharts.Chart({
                chart : {
                    renderTo : 'staffWTimeStatisticsChart',
                    type : 'bar'
                },
                title : {
                    text : ''
                },
                xAxis : {
                    categories : seriesData.name,
                    title : {
                        text : null
                    },
                    labels : {
                        overflow : 'justify',
                        formatter : function() {
                            var name = this.value;
                            return self.getStrActualLen(name, 8);
                        }
                    }
                },
                yAxis : {
                    min : 0,
                    title : {
                        text : '工时',
                        align : 'high'
                    },
                    labels : {
                        overflow : 'justify'
                    },
                    allowDecimals : false
                },
                tooltip : {
                    shared : true,
                    formatter : function() {
                        var str = "";
                        str += '<b>' + this.x + ': ';
                        str += '<br/>' + '<tspan style="fill:' + self.color[0] + '" x="8" dy="16">●</tspan>' + this.points[0].series.name + ': ' + this.points[0].y+"天";
                        str += '<br/>' + '<tspan style="fill:' + self.color[1] + '" x="8" dy="16">●</tspan>' + this.points[1].series.name + ': ' + this.points[1].y+"天";
                        return str;
                    }
                },
                plotOptions : {
                    bar : {
                        dataLabels : {
                            enabled : true
                        },
                        pointWidth : 13,
                        borderWidth : 0
                        //pointPadding: 2,
                    },
                    column : {
                        stacking : 'normal'
                    }
                },
                credits : {
                    enabled : false
                },
                series : [{
                    name : self.typeNameByTime[0],
                    data : seriesData.planDay,
                    color : self.color[0]
                }, {
                    name : self.typeNameByTime[1],
                    data : seriesData.realityDay,
                    color : self.color[1]
                }]
            });
        },
        getProjectStatisticsDataByTime: function(type, isFrist){
        	 var self = this;
             $.ajax({
                 url : context + "/org/findOrganizationTimeStatistics_project.htm",
                 data : {
                     "orgId" : panel.data("organizationid"),
                     "loadType" : type
                 },
                 type : "post"
             }).done(function(result) {
                 if (result && result.success && result.resultData) {
                     var data = result.resultData;
                     if (isFrist) {
                    	 var height = (data.length * 100 + 120) + "px";
                         panel.find("#projectWTimeStatisticsChart").css({
                        	 "min-width" : "400px",
                             "max-width":"400px",
                             "height" : height,
                             "margin-top" : "20px"
                         });
                         self.setProjectStatisticsChartByTime(data);
                     } else {
                         self.refreshProjectStatisticsChartByTime(data);
                     }
                 } else {
                     layer.msg(result.errormsg);
                 }
             }).fail(function(result) {
                 layer.msg("请求失败");
             });
        },
        setProjectStatisticsChartByTime: function(data){
        	var self = this;
            var seriesData = self.dataAssembledByTime(data);
            this.projectStatisticsChartByTime = new Highcharts.Chart({
            	
                chart : {
                    renderTo : 'projectWTimeStatisticsChart',
                    type : 'bar'
                },
                title : {
                    text : ''
                },
                xAxis : {
                    categories : seriesData.name,
                    title : {
                        text : null
                    },
                    labels : {
                        overflow : 'justify',
                        formatter : function() {
                            var name = this.value;
                            return self.getStrActualLen(name, 8);
                        }
                    }
                },
                yAxis : {
                    min : 0,
                    title : {
                        text : '工时（天）',
                        align : 'high'
                    },
                    labels : {
                        overflow : 'justify'
                    },
                    allowDecimals : false
                },
                tooltip : {
                    shared : true,
                    formatter : function() {
                        var str = "";
                        str += '<b>' + this.x + ': ';
                        str += '<br/>' + '<tspan style="fill:' + self.color[0] + '" x="8" dy="16">●</tspan>' + this.points[0].series.name + ': ' + this.points[0].y+'天';
                        str += '<br/>' + '<tspan style="fill:' + self.color[1] + '" x="8" dy="16">●</tspan>' + this.points[1].series.name + ': ' + this.points[1].y+'天';
                        return str;
                    }
                },
                plotOptions : {
                    bar : {
                        dataLabels : {
                            enabled : true
                        },
                        pointWidth : 13,
                        borderWidth : 0
                        //pointPadding: 2,
                    },
                    column : {
                        stacking : 'normal'
                    }
                },
                credits : {
                    enabled : false
                },
                series : [{
                    name : self.typeNameByTime[0],
                    data : seriesData.planDay,
                    color : self.color[0]
                }, {
                    name : self.typeNameByTime[1],
                    data : seriesData.realityDay,
                    color : self.color[1]
                }]
            });
        },
        getTeamStatisticsDataByTime : function(type,isFrist){
        	var self = this;
            $.ajax({
                url : context + "/org/findOrganizationTimeStatistics_team.htm",
                data : {
                    "orgId" : panel.data("organizationid"),
                    "loadType" : type
                },
                type : "post"
            }).done(function(result) {
                if (result && result.success && result.resultData) {
                    var data = result.resultData;
                    if (isFrist) {
                    	var height = (data.length * 100 + 120) + "px";
                        panel.find("#teamWTimeStatisticsChart").css({
                            "min-width" : "400px",
                            "max-width":"400px",
                            "height" : height,
                            "margin-top" : "20px"
                        });
                        self.setTeamStatisticsChartByTime(data);
                        return false;
                    } else {
                        self.refreshTeamStatisticsChartByTime(data);
                    }
                } else {
                    layer.msg(result.errormsg);
                }
            }).fail(function(result) {
                layer.msg("请求失败");
            });
        },
        setTeamStatisticsChartByTime : function(data) {
            var self = this;
            var seriesData = self.dataAssembledByTime(data);
            this.teamStatisticsChartByTime = new Highcharts.Chart({
                chart : {
                    renderTo : 'teamWTimeStatisticsChart',
                    type : 'bar'
                },
                title : {
                    text : ''
                },
                xAxis : {
                    categories : seriesData.name,
                    title : {
                        text : null
                    },
                    labels : {
                        overflow : 'justify',
                        formatter : function() {
                            var name = this.value;
                            return self.getStrActualLen(name, 8);
                        }
                    }
                },
                yAxis : {
                    min : 0,
                    title : {
                        text : '工时（天）',
                        align : 'high'
                    },
                    labels : {
                        overflow : 'justify'
                    },
                    allowDecimals : false
                },
                tooltip : {
                    shared : true,
                    formatter : function() {
                        var str = "";
                        str += '<b>' + this.x + ': ';
                        str += '<br/>' + '<tspan style="fill:' + self.color[0] + '" x="8" dy="16">●</tspan>' + this.points[0].series.name + ': ' + this.points[0].y+'天';
                        str += '<br/>' + '<tspan style="fill:' + self.color[1] + '" x="8" dy="16">●</tspan>' + this.points[1].series.name + ': ' + this.points[1].y+'天';
                        return str;
                    }
                },
                plotOptions : {
                    bar : {
                        dataLabels : {
                            enabled : true
                        },
                        pointWidth : 13,
                        borderWidth : 0
                        //pointPadding: 2,
                    },
                    column : {
                        stacking : 'normal'
                    }
                },
                credits : {
                    enabled : false
                },
                series : [{
                    name : self.typeNameByTime[0],
                    data : seriesData.planDay,
                    color : self.color[0]
                }, {
                    name : self.typeNameByTime[1],
                    data : seriesData.realityDay,
                    color : self.color[1]
                }]
            });
        },
        refreshUserStatisticsChartByTime : function(data) {
            var lastData = this.dataAssembledByTime(data);
            this.userStatisticsChartByTime.series[0].setData(lastData.planDay);
            this.userStatisticsChartByTime.series[1].setData(lastData.realityDay);
        },
        refreshProjectStatisticsChartByTask:function(data){
        	var lastData = this.dataAssembledByTask(data);
            this.projectStatisticsChart.series[0].setData(lastData.overdue);
            this.projectStatisticsChart.series[1].setData(lastData.noDone);
            this.projectStatisticsChart.series[2].setData(lastData.over);
            this.projectStatisticsChart.series[3].setData(lastData.all);
        },
        refreshTeamStatisticsChartByTask:function(data){
        	var lastData = this.dataAssembledByTask(data);
            this.teamStatisticsChart.series[0].setData(lastData.overdue);
            this.teamStatisticsChart.series[1].setData(lastData.noDone);
            this.teamStatisticsChart.series[2].setData(lastData.over);
            this.teamStatisticsChart.series[3].setData(lastData.all);
        },
        refreshProjectStatisticsChartByTime : function(data) {
            var lastData = this.dataAssembledByTime(data);
            this.projectStatisticsChartByTime.series[0].setData(lastData.planDay);
            this.projectStatisticsChartByTime.series[1].setData(lastData.realityDay);
        },
        refreshTeamStatisticsChartByTime : function(data) {
            var lastData = this.dataAssembledByTime(data);
            this.teamStatisticsChartByTime.series[0].setData(lastData.planDay);
            this.teamStatisticsChartByTime.series[1].setData(lastData.realityDay);
        },
        
    };
    statistics.init();
});
