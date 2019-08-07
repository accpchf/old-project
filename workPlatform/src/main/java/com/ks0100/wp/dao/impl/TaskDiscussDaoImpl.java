package com.ks0100.wp.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.HibernateDaoImpl;
import com.ks0100.wp.dao.TaskDiscussDao;
import com.ks0100.wp.entity.TaskDiscuss;

@Repository
public class TaskDiscussDaoImpl extends HibernateDaoImpl<TaskDiscuss, Integer> implements TaskDiscussDao {

	public List<TaskDiscuss> findDiscussByTask(int taskId) {
		String hql = "from TaskDiscuss td where td.task.taskId = ?";
		return find(hql, taskId);
	}

	public void delTaskDiscuss(int taskId) {
		String sql = "delete from tbl_task_discuss where tbl_task_id = ?";
		createSQLQuery(sql, taskId).executeUpdate();
	}
}
