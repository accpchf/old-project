/**
 * 任务版数据推送的模块
 */
define([ "jquery", 
         context+"/static/script/taskBoard/installHtml.js?_T="+(new Date()).valueOf(),
         ], function($, io, installHtml) {
//define([ "jquery", "socketio", "script/taskBoard/installHtml" ], function($, io, installHtml) {
//	var socket = io.connect("http://192.168.4.112:3000"), taskSocket = {};
//
//	// 操作区
//	var panel = $("div.main-content[data-id='projectTemplate']");
//	var projectId = panel.data("projectId");
//	var projectTaskContainer = panel.find(".main-task");
//	var switchButton = panel.find("#switchTaskBoard");
//
//	taskSocket.addTask = function(task) {
//		socket.emit("addTask", task);
//	};
//
//	var user = {
//		userId : loginUserInfo.userId,
//		account : loginUserInfo.account,
//		name : loginUserInfo.name,
//		projectId : projectId
//	};
//	socket.emit("joinUser", user);
//
//	socket.on("pushAddTask", function(task) {
//		if (panel.length > 0 && projectId == task.projectId) {// 是否在任务版界面
//			if (task.taskBoardId == switchButton.data("taskBoardId")) {// 是不是需要推送的任务版
//				projectTaskContainer.find("ul").children(
//						"li[data-id='" + task.taskLineId + "']").find(".taskList").append(installHtml.taskHtml(0, task));
//			}
//		}
//	});
var taskSocket = {
		addTask: function(task){
			
		}
};
	return taskSocket;
});