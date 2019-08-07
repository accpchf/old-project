package com.ks0100.wp.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.HibernateDaoImpl;
import com.ks0100.wp.dao.TaskCheckListDao;
import com.ks0100.wp.entity.TaskCheckList;

@Repository
public class TaskCheckListDaoImpl extends HibernateDaoImpl<TaskCheckList, Integer> implements TaskCheckListDao {

	public List<TaskCheckList> findCheckList(int taskId) {
		String hql = "from TaskCheckList tcl where tcl.task.taskId = ? and tcl.enabled = 1";
		return find(hql, taskId);
	}

	public void updateTaskCheckList(int checkListId, String value, String columnName, int userId) {
		StringBuilder sbSql = new StringBuilder();
		if (!columnName.isEmpty()) {
			sbSql.append("update tbl_task_check_list set updated_by = ?, " + columnName + " = ? where id = ?");
			createSQLQuery(sbSql.toString(), userId, value, checkListId).executeUpdate();
		}
	}

	@Override
	public int findCheckListsNumByStatus(int taskId, String status) {
		String hql = "from TaskCheckList tcl where tcl.task.taskId = ? and tcl.status = ? and tcl.enabled = 1";
		return find(hql,taskId,status).size();
	}
}
