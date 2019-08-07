package com.ks0100.wp.dao;

import java.util.List;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.TaskDiscuss;

/**
 * 任务讨论数据访问接口
 * 
 * @author xie linming
 * @date 2014年12月24日
 */
public interface TaskDiscussDao extends SimpleHibernateDao<TaskDiscuss,Integer> {
	
	/**
	 * 获取任务的所有回复
	 * @param taskId
	 * @return
	 */
 	List<TaskDiscuss> findDiscussByTask(int taskId);
 	
 	/**
 	 * 删除项目回复
 	 * @param taskId
 	 */
 	void delTaskDiscuss(int taskId);
}
