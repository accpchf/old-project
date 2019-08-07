/*
 * 任务版界面的主入口
 */
require(["jquery","script/taskBoard/taskEntrance","script/framework/switchingProject"],function($,taskEntrance,switchProject){
    
	taskEntrance.init();
	switchProject.init();
});