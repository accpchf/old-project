package com.ks0100.wp.dao;

import java.util.List;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.ProjectActionRecord;

public interface ProjectActionRecordDao extends SimpleHibernateDao<ProjectActionRecord, Integer> {
	
	/**
	 * 获取任务操作动态
	 * @param taskId
	 * @return
	 */
	List<ProjectActionRecord> listTaskActionRecords(int taskId);
	
	/**
	 * 删除任务动态
	 * @param taskId
	 */
	void delTaskActionRecord(int taskId);
	
	List<ProjectActionRecord>  findProjectDynamic(int projectId, String userIds,String types,String monday,String sunday);
	
	List<ProjectActionRecord> listProjectActionRecords(int projectId);
	
	int findTaskRecordNum(int taskId);

}
