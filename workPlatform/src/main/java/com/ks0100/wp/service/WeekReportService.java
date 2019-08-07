package com.ks0100.wp.service;

import java.util.List;
import java.util.Map;

import com.ks0100.wp.entity.ProWRepContent;
import com.ks0100.wp.entity.ProjectWeeklyReport;
import com.ks0100.wp.entity.UserWeekRepDiscuss;
import com.ks0100.wp.entity.UserWeekReport;


public interface WeekReportService {

	/**
	 * 查询我的周报
	 * @param userId
	 * @param year
	 * @param week
	 * @return
	 */
	Map<Integer, UserWeekReport> listUserWeekReort(int userId,String monday);
	
	/**
	 * 添加周报
	 */
	void addUserWeekReport();
	
	/**
	 * 更新人员周报
	 * @param uwr
	 */
	void updateUserWRep(UserWeekReport uwr, boolean isFilledIn);
	
	/**
	 * 根据周报Id查找个人周报
	 * @param uwreportId
	 * @return
	 */
	UserWeekReport findUWReport(int uwreportId);
	
	/**
	 * 更新个人周报中的完成任务数
	 * @param uwreportId
	 * @return
	 */
	
	UserWeekReport updateuwReportTask(int uwreportId);
	/**
	 * 根据项目Id查询项目用户周报
	 * @param userId
	 * @param year
	 * @param week
	 * @param proId
	 * @return
	 */
	Map<Integer, UserWeekReport> findUserWeekReportByProId(String userIds,String monday,int projectId);
	/**
	 * 给个人周报添加评论
	 */
	void addUWReportContent(UserWeekRepDiscuss uwrd);
	
	/**
	 * 添加项目周报
	 */
	void addProjectWeeklyReport();
	
	/**
	 * 根据项目id和开始时间来查询项目周报
	 * @param monday
	 * @param projectId
	 * @return
	 */
	 ProjectWeeklyReport findProWeekReport(String monday,int projectId);
	 /**
	  * 根据项目周报Id查询项目周报
	  * @param pwrId
	  * @return
	  */
	 ProjectWeeklyReport findPWReport(int projectWeeklyReportId);
	 /**
	  * 更新项目周报
	  * @param pwr
	  */
	 void updateProjectWRep(ProjectWeeklyReport pwr, boolean isFilledIn);
	 
	 /**
	  * 项目周报具体内容的更新
	  * @param pwrId
	  * @param ids
	  */
	 void updateProWeekReportUser(ProWRepContent pwrc);
	 
	 /**
	  * 根据项目周报具体内容Id查询项目周报具体内容
	  * @param pwrcId
	  * @return
	  */
	 ProWRepContent findPWReportContentById(int pwrcId);
	 
	 void deletepwrContent(int pwrcId);
	 
	 List<String> findDate(int userId);
	 
	 List<String> findDateByProject(int projectId);
	 
	 List<String> findProjectWRepDate(int projectId);
	 
	 void createUserWeekReport(String monday, String sunday, int userId, int projectId);
}
