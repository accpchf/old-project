package com.ks0100.wp.dao;

import java.util.List;
import java.util.Map;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.ProjectWeeklyReport;

public interface ProWeeklyRepDao extends SimpleHibernateDao<ProjectWeeklyReport, Integer>{
	
	void addProjectWeeklyReport();
	
	/**
	 * 查询项目周报
	 * @param monday
	 * @param projectId
	 * @return
	 */
	ProjectWeeklyReport findProWeekReport(String monday,int projectId);
	
	List<Map<String, Object>> findDateByPro(int projectId);
}
