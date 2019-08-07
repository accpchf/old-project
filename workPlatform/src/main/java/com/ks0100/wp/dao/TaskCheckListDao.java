package com.ks0100.wp.dao;

import java.util.List;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.TaskCheckList;

/**
 * 任务检查项数据访问接口
 * @author xie linming
 * @date 2014年12月26日
 */
public interface TaskCheckListDao extends SimpleHibernateDao<TaskCheckList,Integer> {
	/**
	 * 获取任务的检查项
	 * @param taskId
	 * @return
	 */
	List<TaskCheckList> findCheckList(int taskId);
	
	void updateTaskCheckList(int checkListId, String value, String columnName, int userId);
	
	int findCheckListsNumByStatus(int taskId,String status);
}
