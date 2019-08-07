package com.ks0100.wp.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.UserWeekReport;

public interface UserWeekReportDao extends SimpleHibernateDao<UserWeekReport, Integer> {

	/**
	 * 根据用户Id查询用户的周报及周报下面的评论
	 * @param userId
	 * @return
	 */
	List<Map<String, Object>> findUserWeekReportByUserId(int userId, String monday);
	
	void addUserWeekReport();
	
	List<Map<String, Object>> findTaskQuality(int userId, int projectId, Date monday);
	/**
	 * 根据项目Id查询项目成员的周报
	 * @param userIds
	 * @param projectId
	 * @param year
	 * @param week
	 * @return
	 */
	List<Map<String, Object>> findProUserWeekReportByProId(String userIds ,int projectId , String monday);
	
	List<Map<String, Object>> findDate(int userId);
	List<Map<String, Object>> findDateByPro(int projectId);
	/**
	 * 创建项目周报
	 * @param userId
	 * @param projectId
	 */
	void createUserWeekReport(String monday, String sunday, int userId, int projectId);
	
	/**
	 * 根据成员周报的Id去查询成员周报的评论
	 * @param pwrId
	 * @return
	 */
	List<Map<String, Object>> findDiscussByUWRId(int pwrId);
}
