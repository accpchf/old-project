package com.ks0100.wp.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ks0100.wp.service.WeekReportService;

@Component
public class WeeklyReportTask {
	
	@Autowired
	private WeekReportService wReportService;
	private static Logger logger = LoggerFactory.getLogger(WeeklyReportTask.class);
	
	
/*	
 *  生成个人周报
 *  未完成任务数表示执行者的任务截止日在本周，但是没有完成的总数。
 *  完成任务数表示执行者的任务完成时间在本周内的总数（总数指一个人在一个项目中的符合条件的任务总和）。
 *  未完成任务数+完成任务数=总任务数
 *
 */
	 
	public void createPersonalReport(){
		wReportService.addUserWeekReport();
		logger.info("-logger.info---------------createPersonalReport----------------------");
		
	}
	
    /**
     * 每周生成一次所有项目的周报数据模板，数据库不会允许时间重复
     */
	public void createProjectReport(){
		wReportService.addProjectWeeklyReport();
		logger.info("----------------createProjectReport----------------------");
	}
	

}
