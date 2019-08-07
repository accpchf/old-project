package com.ks0100.wp.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.HibernateDaoImpl;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.constant.StatusEnums.ProjectSatus;
import com.ks0100.wp.dao.TaskDao;
import com.ks0100.wp.entity.Task;

@Repository
@SuppressWarnings("unchecked")
public class TaskDaoImpl extends HibernateDaoImpl<Task, Integer> implements TaskDao {

	public Task findMaxSortTask(int lineId) {
		String hql = "from Task t where t.taskLine.taskLineId = ? order by t.sort desc";
		return findUnique(hql, lineId);
	}

	public void addTaskUser(int taskId, int userId, int executor, List<Integer> partner) {
		StringBuilder sbSql = new StringBuilder("insert into tbl_user_task values");
		if (userId != 0) {
			sbSql.append("(:userId, :taskId, :adminCode),");
		}
		if (executor != 0) {
			sbSql.append("(:executor, :taskId, :executorCode),");
		}
		for (int id : partner) {
			if (id != 0) {
				sbSql.append("(" + id + ", :taskId, :partnerCode),");
			}
		}
		sbSql.deleteCharAt(sbSql.length() - 1);
		if (!sbSql.toString().endsWith("values")) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", userId);
			map.put("taskId", taskId);
			map.put("adminCode", BusinessConstant.RoleConstant.TASK_ADMIN);
			map.put("executor", executor);
			map.put("executorCode", BusinessConstant.RoleConstant.TASK_EXECUTOR);
			map.put("partnerCode", BusinessConstant.RoleConstant.TASK_MEMBER);
			createSQLQuery(sbSql.toString(), map).executeUpdate();
		}
	}

	public List<Map<String, Object>> listPartner(int taskId) {
		String sql = "select u.name,ut.tbl_user_id userId, ut.tbl_role_code roleCode from tbl_user_task ut, tbl_user u where ut.tbl_task_id = ? and u.id = ut.tbl_user_id";
		Query query = createSQLQuery(sql, taskId);
		return (List<Map<String, Object>>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}

	public void updateTask(int taskId, Object value, String columnName, int userId) {
		StringBuilder sbSql = new StringBuilder();
		if (!columnName.isEmpty()) {
			sbSql.append("update tbl_task set updated_by = ?, " + columnName + " = ? where id = ?");
			createSQLQuery(sbSql.toString(), userId, value, taskId).executeUpdate();
		}
	}

	public void updateTaskExecutor(int taskId, int executor, int taskExecutor) {
		String sql = null;
		if (taskExecutor > 0) {
			if (executor > 0) {
				sql = "update tbl_user_task set tbl_user_id = :executor where tbl_task_id = :taskId and tbl_role_code = :roleCode";
			} else {
				sql = "delete from tbl_user_task where tbl_task_id = :taskId and tbl_role_code = :roleCode";
			}
		} else {
			sql = "insert into tbl_user_task values(:executor, :taskId, :roleCode)";
		}
		if (!sql.isEmpty()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("executor", executor);
			map.put("taskId", taskId);
			map.put("roleCode", BusinessConstant.RoleConstant.TASK_EXECUTOR);
			createSQLQuery(sql, map).executeUpdate();
		}
	}

	public void updateTaskPartner(int taskId, List<Integer> partner, boolean isSelected) {
		String sql = null;
		if (isSelected) {
			sql = "insert into tbl_user_task values(:userId, :taskId, :roleCode)";
		} else {
			sql = "delete from tbl_user_task where tbl_user_id = :userId and tbl_task_id = :taskId and tbl_role_code = :roleCode";
		}
		if (!sql.isEmpty()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", partner.get(0));
			map.put("taskId", taskId);
			map.put("roleCode", BusinessConstant.RoleConstant.TASK_MEMBER);
			createSQLQuery(sql, map).executeUpdate();
		}
	}

	public int findTaskExecutor(int taskId) {
		String sql = "select tbl_user_id from tbl_user_task where tbl_task_id = ? and tbl_role_code = ?";
		Query query = createSQLQuery(sql, taskId, BusinessConstant.RoleConstant.TASK_EXECUTOR);
		Object result = query.uniqueResult();
		return result != null ? (Integer) result : 0;
	}

	public List<Integer> listPartnerIds(int taskId) {
		String sql = "select tbl_user_id from tbl_user_task where tbl_task_id = ? and tbl_role_code = ?";
		Query query = createSQLQuery(sql, taskId, BusinessConstant.RoleConstant.TASK_MEMBER);
		return query.list();
	}

	public void delTaskUser(int taskId) {
		String sql = "delete from tbl_user_task where tbl_task_id = ?";
		createSQLQuery(sql, taskId).executeUpdate();
	}
	
	public void updateSortAdd_1(int oneselfSort, int targetSort, int lineId) {
		String sql = "update tbl_task set sort = sort + 1 where sort >= ? and sort < ? and tbl_task_line_id = ?";
		createSQLQuery(sql, targetSort, oneselfSort, lineId).executeUpdate();
	}
	
	public void updateSortSub_1(int oneselfSort, int targetSort, int lineId) {
		String sql = "update tbl_task set sort = sort - 1 where sort > ? and sort <= ? and tbl_task_line_id = ?";
		createSQLQuery(sql, oneselfSort, targetSort, lineId).executeUpdate();
	}

	public List<Map<String, Object>> listParentAndChildTask(int taskId) {
		String sql = "select t.id taskId, t.content, t.parent_task_id parentId, ut.tbl_user_id executor from tbl_task t left join tbl_user_task ut"
				   + " on t.id = ut.tbl_task_id and ut.tbl_role_code = :roleCode where t.id = (select parent_task_id from tbl_task where id = :taskId) or t.parent_task_id = :taskId and t.enabled = 1";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", BusinessConstant.RoleConstant.TASK_EXECUTOR);
		map.put("taskId", taskId);
		Query query = createSQLQuery(sql, map);
		return (List<Map<String, Object>>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}

	public List<Task> findTasksByProject(int projectId) {
		return find("from Task t where t.projectId = ?", projectId);
	}

	public List<Map<String, Object>> findTasksByWeek(int projectId, String startDate, String endDate) {
		// mysql计算本周开始日期和结束日期(按周日为一周的最后一天计算)
		// curdate() - INTERVAL DAYOFWEEK(curdate()) - 2 DAY;
		// curdate() + INTERVAL 6 - (DAYOFWEEK(curdate()) - 2) DAY
		String sql = "select count(1) count, DAYOFWEEK(due_time) dueTime, DAYOFWEEK(complete_time) completeTime, status from tbl_task where tbl_project_id = :projectId and status != :okStatus and due_time between curdate() and :endDate group by due_time union "
				   + "select count(1) count, DAYOFWEEK(due_time) dueTime, DAYOFWEEK(complete_time) completeTime, status from tbl_task where tbl_project_id = :projectId and status = :okStatus and complete_time between :startDate and curdate() group by complete_time";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("projectId", projectId);
		map.put("okStatus", BusinessConstant.TaskConstant.TASK_DONE);
		map.put("startDate", startDate);
		map.put("endDate", endDate);
		Query query = createSQLQuery(sql, map);
		return (List<Map<String, Object>>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}

	public List<Map<String, Object>> findAllProjectTask(int userId, int roleType) {
		String roleCode = "";
		switch (roleType){
			case 1:
				roleCode = " and ut.tbl_role_code = '" + BusinessConstant.RoleConstant.TASK_EXECUTOR + "'";
				break;
			case 2:
				roleCode = " and ut.tbl_role_code = '" + BusinessConstant.RoleConstant.TASK_MEMBER + "'";
				break;
			case 3:
				roleCode = " and ut.tbl_role_code = '" + BusinessConstant.RoleConstant.TASK_ADMIN + "'";
				break;
			default:
				break;
		}
		String sql = "select DISTINCT temp.*, ut.tbl_user_id executor from (select t.id taskId, t.content, t.priority, t.level, t.status, t.due_time, p.id projectId, p.name"
				   + " from tbl_task t,tbl_task_line tl, tbl_project p,tbl_user_project up, tbl_organization o where t.tbl_project_id = p.id and tl.id = t.tbl_task_line_id and tl.enabled = 1 and up.tbl_user_id = ? and up.tbl_project_id = p.id and up.enabled =1  and  p.status = ? and "
				   + " (p.tbl_organization_id is null or ( o.enabled = 1  and o.id = p.tbl_organization_id )) and p.enabled = 1 and t.enabled = 1 and (t.due_time >= curdate() or t.due_time is null)"
				   + " and t.id in (select ut.tbl_task_id from tbl_user_task ut where ut.tbl_user_id = ?" + roleCode + ")) temp left join tbl_user_task ut on temp.taskId = ut.tbl_task_id and ut.tbl_role_code = ?";
		Query query = createSQLQuery(sql,userId,ProjectSatus.DOING.getCode(), userId, BusinessConstant.RoleConstant.TASK_EXECUTOR);
		return (List<Map<String, Object>>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}

	@Override
	public void updateTaskAdmin(int taskId, int admin) {
		String sql = "UPDATE tbl_user_task SET tbl_user_id =:admin  WHERE tbl_task_id = :taskId and tbl_role_code = :roleCode";
		if (!sql.isEmpty()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("admin", admin);
			map.put("taskId", taskId);
			map.put("roleCode", BusinessConstant.RoleConstant.TASK_ADMIN);
			createSQLQuery(sql, map).executeUpdate();
		}
	}

	@Override
	public List<Object> findDiabledTaskByUseId(int userId) {
		String sql = "select DISTINCT id from tbl_task t,tbl_user_task ut,tbl_user_project up "+
					 "where up.enabled = 0 and up.tbl_user_id = ut.tbl_user_id and "+
					 "up.tbl_project_id = t.tbl_project_id"+
					 " and ut.tbl_task_id = id and up.tbl_user_id = ?";
		return createSQLQuery(sql, userId).list();
	}

	/**
	 * roleType 1为管理员 2为执行者 3为成员 其他为查询所有
	 * taskStatus 1为今日任务 2为未来任务
	 */
	@Override
	public List<Map<String, Object>> findTaskByUserIdForMobile(int userId,int roleType,int taskStatus) {
		DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd"); 
		StringBuilder sb = new StringBuilder("select DISTINCT p.name as projectName,p.id as projectId, t.id,t.content,t.created_time as createdTime,t.status from tbl_task t,tbl_user_task ut,tbl_project p where ut.tbl_task_id = t.id and t.tbl_project_id = p.id and ut.tbl_user_id =? ");
		switch (roleType) {
		case 1:
			sb.append("and ut.tbl_role_code = 'TASK_ADMIN'");
			break;
		case 2:
			sb.append("and ut.tbl_role_code = 'TASK_EXECUTOR'");
			break;
		case 3:
			sb.append("and ut.tbl_role_code = 'TASK_MEMBER'");
			break;
		default:
			break;
		}
		
		switch (taskStatus) {
		case 1:
			sb.append(" and t.enabled = 1 and DATE_FORMAT(t.due_time,'%Y-%m-%d') = '"+DateTime.now().toString(format)+"' order by t.created_time desc");
			break;
		case 2:
			sb.append(" and t.enabled = 1 and DATE_FORMAT(t.due_time,'%Y-%m-%d') > '"+DateTime.now().toString(format)+"' order by t.created_time desc");
			break;
		default:
			sb.append(" and t.enabled = 1 order by t.created_time desc");
			break;
		}
		Query query = createSQLQuery(sb.toString(), userId);
		return (List<Map<String, Object>>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}

}
