package com.ks0100.wp.dao;

import java.util.List;
import java.util.Map;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.TaskLine;

/**
 * 任务列数据访问接口
 * @author xie linming
 * @date 2014年12月4日
 */
public interface TaskLineDao extends SimpleHibernateDao<TaskLine,Integer>{
	/**
	 * 获取看板下的任务列(包含任务)
	 * @param taskBoardId
	 * @return
	 */
	List<Map<String, Object>> listTaskLineByBoard(int userId, int taskBoardId, int roleType);
	
	/**
	 * 修改排序 + 1
	 * @param sort
	 */
	void updateSortAdd_1(int oneselfSort, int targetSort, int boardId);
	
	/**
	 * 修改排序 - 1
	 * @param sort
	 */
	void updateSortSub_1(int oneselfSort, int targetSort, int boardId);
	
	/**
	 * 添加任务列_人员关联
	 * @param lineId
	 * @param userId
	 */
	void addTaskLineUser(int lineId, int userId);
	
	/**
	 * 添加默认的任务列
	 * @param taskBoard
	 * @param lineNames
	 */
	void addDefaultTaskLine(int taskBoard, String lineNames, int userId);
	
	List<TaskLine> listTaskLines(int boardId);
	
	/**
	 * 获取任务板最小排序的任务列
	 * @param boardId
	 * @return
	 */
	TaskLine findMinSortLine(int boardId);

}
