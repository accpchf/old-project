package com.ks0100.wp.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.HibernateDaoImpl;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.dao.TaskLineDao;
import com.ks0100.wp.entity.TaskLine;

@Repository
@SuppressWarnings("unchecked")
public class TaskLineDaoImpl extends HibernateDaoImpl<TaskLine, Integer> implements TaskLineDao {

	public List<Map<String, Object>> listTaskLineByBoard(int userId, int taskBoardId, int roleType) {
		String roleCondition = "", visibleCondition = "";
		switch (roleType) {
			case 1:
				roleCondition = " and ut.tbl_role_code = '" + BusinessConstant.RoleConstant.TASK_EXECUTOR + "'";
				break;
			case 2:
				roleCondition = " and ut.tbl_role_code = '" + BusinessConstant.RoleConstant.TASK_MEMBER + "'";
				break;
			case 3:
				roleCondition = " and ut.tbl_role_code = '" + BusinessConstant.RoleConstant.TASK_ADMIN + "'";
				break;
			default:
				visibleCondition = "t.visible_status = '" + BusinessConstant.TaskConstant.TASK_VISIBLE_STATUS_ALL + "' or ";
				break;
		}
		String sql = "select tl.id lineId, tl.name lineName, t.id taskId, t.name taskName, t.content taskContent, t.status taskStatus,t.created_time createdTime, t.priority taskPriority, t.level taskLevel, ut.tbl_user_id userId"
				   + " from tbl_task_line tl left join tbl_task t on tl.id = t.tbl_task_line_id and t.enabled = 1 and"
				   + " (" + visibleCondition + "t.id in (select ut.tbl_task_id from tbl_user_task ut where ut.tbl_user_id = ?" + roleCondition + "))"
				   + " left join tbl_user_task ut on t.id = ut.tbl_task_id and ut.tbl_role_code = ?"
				   + " where tl.tbl_task_board_id = ? and tl.enabled = 1 order by tl.sort, t.sort";
		Query query = createSQLQuery(sql, userId, BusinessConstant.RoleConstant.TASK_EXECUTOR, taskBoardId);
		return (List<Map<String, Object>>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}

	public void addTaskLineUser(int lineId, int userId) {
		String sql = "insert into tbl_user_task_line values(:adminCode, :userId, :lineId)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", userId);
		map.put("lineId", lineId);
		map.put("adminCode", BusinessConstant.RoleConstant.TASK_LINE_ADMIN);
		createSQLQuery(sql, map).executeUpdate();
	}
	
	

	public void addDefaultTaskLine(int taskBoard, String lineNames, int userId) {
		StringBuilder sbSql = new StringBuilder();
		String[] names = lineNames.split("[,，]");
		if (names.length > 0) {
			sbSql.append("insert into tbl_task_line(name, sort, tbl_task_board_id, created_by, updated_by) values(" + names[0] + ",0,:boardId,:userId,:userId)");
			for (int i = 1; i < names.length; i++) {
				sbSql.append(",(" + names[i] + "," + i + ",:boardId,:userId,:userId)");
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("userId", userId);
			map.put("boardId", taskBoard);
			createSQLQuery(sbSql.toString(), map).executeUpdate();
		}
	}

	public List<TaskLine> listTaskLines(int boardId) {
		return find("from TaskLine tl where tl.taskBoard.taskBoardId = ? and tl.enabled = 1", boardId);
	}

	public TaskLine findMinSortLine(int boardId) {
		return findUnique("from TaskLine tl where tl.taskBoard.taskBoardId = ? and tl.enabled = 1 order by tl.sort", boardId);
	}

	public void updateSortAdd_1(int oneselfSort, int targetSort, int boardId) {
		String sql = "update tbl_task_line set sort = sort + 1 where sort >= ? and sort < ? and tbl_task_board_id = ?";
		createSQLQuery(sql, targetSort, oneselfSort, boardId).executeUpdate();
	}
	
	public void updateSortSub_1(int oneselfSort, int targetSort, int boardId) {
		String sql = "update tbl_task_line set sort = sort - 1 where sort > ? and sort <= ? and tbl_task_board_id = ?";
		createSQLQuery(sql, oneselfSort, targetSort, boardId).executeUpdate();
	}


}
