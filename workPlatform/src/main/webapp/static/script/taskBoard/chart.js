/*
 * 图表的模块
 */
define(["jquery","highcharts"],function($){
	//图表模块
	var panel = $("div.main-content[data-id='projectTemplate']");
	
    var chartModel = {
        init : function(){
        	
        	this.projectChartContainer = $(".project-chart",panel);
        	this.getData(0);
        	this.setEvent();
        },
        setEvent : function(){
        	var self = this;
        	panel.on("click","#refreshTaskStatistics",function(){
        		self.getData(3);
        	});
			panel.on("click","#refreshWeekTaskChart",function(){
				self.getData(4);   		
			});
			panel.on("click","#refreshCountdownStatistics", function(){
				self.getData(5);
			});
        },
        getData : function(type){
        	var self = this;
        	$.ajax({
        		type: "post",
        		url: context + "/project/findProjectStatistics.htm",
        		data: {projectId : panel.data("projectId"), loadType: type}
        	}).done(function(result){
        		if(result && result.success && result.resultData){
        			var data = result.resultData;
        			switch(type){
	        			case 0 : 
	        				self.setCountdownValue(data);
	            			self.setUserNumValue(data);
	            			self.setTaskStatisticsValue(data);
	            			self.setWeekTaskChartValue(data);
	        				break;
	        			case 3 :
	        				self.setTaskStatisticsValue(data);
	        				break;
	        			case 4 :
	        				self.setWeekTaskChartValue(data);
	        				break;
	        			case 5 :
	        				self.setCountdownValue(data);
	        				break;
        			}
        		}else{
        			layer.msg(result.errormsg);
        		}
        	}).fail(function(){
        		layer.msg("请求失败");
        	});
        },
        setCountdownValue : function(data){//倒计时
        	if(data.days != null){
        		if(data.days > 0){
            		this.projectChartContainer.find("#projectCountdown").html("还有<span>"+data.days+"</span>天");
            	}else if(data.days == 0){
            		this.projectChartContainer.find("#projectCountdown").text("今天结束");
            	}else{
            		this.projectChartContainer.find("#projectCountdown").html("逾期<span class='red'>"+data.days*-1+"</span>天");
            	}
        	}else{
        	}
        	if(data.planDay != null){
        		this.projectChartContainer.find("#planDay").html(data.planDay+"天");
        	}
        	if(data.realityday != null){
        		this.projectChartContainer.find("#realityDay").html(data.realityday+"天");
        	}
        },
        setUserNumValue : function(data){//团队人数
        	var count = 0;//人数
        	$.each(window.projectUserList,function(){
        		count++;
        	});
        	if(data.teams > 0){
        		this.projectChartContainer.find("#projectUserNum").html(data.teams+"个"+TEXT_CONFIG.tuandui+"</br>共计<span>"+count+"</span>人");
        	}else{
        		this.projectChartContainer.find("#projectUserNum").html("共计<span>"+count+"</span>人");
        	}
        },
//        refreshTaskStatisticsChart : function(data){
//        	this.taskStatistics.series[0].setData(this.organizeTaskStatisticsData(data));
//        },
        organizeTaskStatisticsData : function(weekTaskList){//组装项目统计数据
        	var data = [];
        	var color = ["#058DC7","#99CC66","#CCFF66","#ffff00","#ff0000","#ff00ff"];//柱子颜色设定
        	data[0] = {y:weekTaskList.taskCount,color:color[0]};
        	data[1] = {y:weekTaskList.doneTaskCount,color:color[1]};
        	data[2] = {y:weekTaskList.doingTaskCount,color:color[2]};
        	data[3] = {y:weekTaskList.tocheckTaskCount,color:color[3]};
        	data[4] = {y:weekTaskList.noPassTaskAccount,color:color[4]};
        	data[5] = {y:weekTaskList.tardyTaskCount,color:color[5]};
        	return data;
        },
        setTaskStatisticsValue : function(data){//设定任务统计图表
        	var weekTaskList = data.taskStatistics;//后台取得的本周进度数据
        	var seriesData = [];//图表的数据结构[{y:120,color: "#058DC7"}, {y:100,color: "#99CC66"}, {y:10,color: "#CCFF66"}, {y:10,color: "#FF0000"}]
        	
        	if(weekTaskList){
        		seriesData = this.organizeTaskStatisticsData(weekTaskList);
        	}

        	var xName = ['总数', '已完成','进行中','待审核', '不通过', '逾期'];
        	
        	this.taskStatistics = new Highcharts.Chart({//任务统计
                chart: {
                    renderTo: 'taskStatistics',
                    type: 'column'
                },
                title: {
                    text: '任务统计',
                    style: {"font-size": "16px","color": "#333","margin": "10px 4px","font-weight": "600"}
                },
                xAxis: {
                    categories: xName
                },
                yAxis: {
                    title: {
                        text: ''
                    }
                },
                tooltip: {
                    formatter: function() {
                    	var str = "";
                    	if(weekTaskList.taskCount == 0){
                    		str = '<b>'+ this.x +': '+ this.y;
                    	}else{
                    		if(xName[0] == this.x){
                        		str = '<b>'+ this.x +': '+ this.y;
                        	}else if(xName[1] == this.x){
                        		str = '<b>'+ this.x +': '+ this.y + "个,占" +(100-parseInt((weekTaskList.taskCount-this.y)/weekTaskList.taskCount*100))+"%";
                        	}else{
                        		str = '<b>'+ this.x +': '+ this.y + "个,占" +parseInt(this.y/weekTaskList.taskCount*100)+"%";
                        	}
                    	}
                        return str;
                    }
                },
                legend: {
                    enabled : false
                },
                series: [
                  {
                	data: seriesData
                }]
            });
        	
        },
        setWeekTaskChartValue : function(data){//设定本周进度图表
        	var weekData = data.weekStatistics;
        	var week = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
        	$("#weekTaskChart").empty();
        	var undoneArr = [0,0,0,0,0,0,0];
        	var overArr = [0,0,0,0,0,0,0];
        	if(weekData){
        		
        		if(weekData.dayOfWeek == 1){
        			week[0] = "今天";
        		}else{
        			week[weekData.dayOfWeek-1] = "今天";
        		}
        		$.each(weekData.noTaskCount,function(key,value){
        			if(key == 1){
        				undoneArr[6] = value;
        			}else{
        				undoneArr[key-2] = value;
        			}
        		});
        		$.each(weekData.doneTaskCount,function(key,value){
        			if(key == 1){
        				overArr[6] = value;
        			}else{
        				overArr[key-2] = value;
        			}
        		});
        	}
        	var weekTaskChart = new Highcharts.Chart({//本周计划
                chart: {
                    renderTo: 'weekTaskChart',
                    type: 'column'
                },
                title: {
                    text: '本周进度',
                    style: {"font-size": "16px","color": "#333","margin": "10px 4px","font-weight": "600"}
                },
                xAxis: {
                    categories: week
                },
                yAxis: {
                    title: {
                        text: ''
                    }
                },
                tooltip: {
                    formatter: function() {
                    	var str = "";
                    	var all = this.point.stackTotal;
                    	if(this.x == "今天"){
                    		if(all > 0){
                    			if(this.series.name == "待完成"){
                    				str = '<b>'+ this.x +'</b><br/>'+ this.series.name +': '+ this.y + "个,占" +(100-parseInt((all-this.y)/all*100))+"%";
                    			}else{
                    				str = '<b>'+ this.x +'</b><br/>'+ this.series.name +': '+ this.y + "个,占" +parseInt(this.y/all*100)+"%";
                    			}
                    		}else{
                    			str = '<b>'+ this.x +'</b><br/>'+ this.series.name +': '+ this.y + "个";
                    		}
                    	}else{
                    		str = '<b>'+ this.x +'</b><br/>'+ this.series.name +': '+ this.y + "个";
                    	}
                        return str;
                    }
                },
                plotOptions: {
                    column: {
                        stacking: 'normal'
                    }
                },
                legend: {
                    align: 'right',
                    x: -10,
                    verticalAlign: 'top',
                    y: 20,
                    floating: true,
	                    backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColorSolid) || 'white',
                    borderColor: '#CCC',
                    borderWidth: 1,
                    shadow: false
                },
                series: [{
                	color : '#6AF9C4',
                    name: '待完成',
                    data: undoneArr
                }, {
                	color : '#FFF263',
                    name: '已完成',
                    data: overArr
                }]
            });
        }
    };
    return chartModel;
});