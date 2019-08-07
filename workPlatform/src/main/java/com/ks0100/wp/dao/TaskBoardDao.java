package com.ks0100.wp.dao;

import java.util.List;
import java.util.Map;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.TaskBoard;

/**
 * 任务板数据访问接口
 * @author xie linming
 * @date 2014年12月2日
 */
public interface TaskBoardDao extends SimpleHibernateDao<TaskBoard,Integer>{
	
	/**
	 * 获取项目所有看板
	 * @param projectId
	 * @return
	 */
	List<Map<String, Object>> listTaskBoards(int projectId);
	
	/**
	 * 获取项目所有看板,返回数据给手机客户端
	 * @param projectId
	 * @return
	 */
	List<Map<String, Object>> listTaskBoardsForMobile(int projectId);
	
	/**
	 * 添加任务板_用户关联
	 * @param userId
	 * @param boardId
	 * @param roleCode
	 */
	void addBoardUser(int userId, int boardId, String roleCode);
	
	void updateBoardAdmin(int boardId, int boardAdmin);
	
}
