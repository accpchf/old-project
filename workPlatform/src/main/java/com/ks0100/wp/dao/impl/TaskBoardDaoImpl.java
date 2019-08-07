package com.ks0100.wp.dao.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.HibernateDaoImpl;
import com.ks0100.wp.constant.BusinessConstant.RoleConstant;
import com.ks0100.wp.dao.TaskBoardDao;
import com.ks0100.wp.entity.TaskBoard;

@Repository
public class TaskBoardDaoImpl extends HibernateDaoImpl<TaskBoard,Integer> implements TaskBoardDao {

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> listTaskBoards(int projectId) {
		// String hql = "from TaskBoard tb where tb.project.projectId = ? and enabled = 1 order by tb.createdTime desc";
		// return find(hql, projectId);
		String sql = "select tbl.taskBoardId, tbl.name, tbl.remark, tbl.boardAdmin,tl.id taskLineId,tl.name taskLineName from " +
				"(select tb.id taskBoardId, tb.name, tb.remark, utb.tbl_user_id boardAdmin,tb.created_time " +
				"from tbl_task_board tb " +
				" left join tbl_user_task_board utb on tb.id = utb.tbl_task_board_id and utb.tbl_role_code = ?" +
						" where  tb.tbl_project_id = ? and tb.enabled = 1 ) tbl ,tbl_task_line tl " +
						"where tl.tbl_task_board_id = tbl.taskBoardId and tl.enabled =1" +
						" order by tl.sort";
		Query query = createSQLQuery(sql, RoleConstant.TASK_BOARD_ADMIN, projectId);
		return (List<Map<String, Object>>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}

	public void addBoardUser(int userId, int boardId, String roleCode) {
		String sql = "insert into tbl_user_task_board values(?, ?, ?)";
		createSQLQuery(sql, userId, roleCode, boardId).executeUpdate();
	}

	public void updateBoardAdmin(int boardId, int boardAdmin) {
		String sql = "update tbl_user_task_board set tbl_user_id = ? where tbl_task_board_id = ? and tbl_role_code = ?";
		createSQLQuery(sql, boardAdmin, boardId, RoleConstant.TASK_BOARD_ADMIN).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> listTaskBoardsForMobile(int projectId) {
		String sql = "select tbl.taskBoardId, tbl.name from " +
				"(select tb.id taskBoardId, tb.name " +
				"from tbl_task_board tb " +
				" left join tbl_user_task_board utb on tb.id = utb.tbl_task_board_id and utb.tbl_role_code = ?" +
						" where  tb.tbl_project_id = ? and tb.enabled = 1 ) tbl ,tbl_task_line tl where tl.tbl_task_board_id = tbl.taskBoardId" +
						" order by tbl.created_time desc";
		Query query = createSQLQuery(sql, RoleConstant.TASK_BOARD_ADMIN, projectId);
		return (List<Map<String, Object>>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}
}
