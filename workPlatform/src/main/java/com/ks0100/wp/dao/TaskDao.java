package com.ks0100.wp.dao;

import java.util.List;
import java.util.Map;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.Task;

/**
 * 任务数据访问接口
 * @author xie linming
 * @date 2014年12月12日
 */
public interface TaskDao extends SimpleHibernateDao<Task, Integer> {
	/**
	 * 获取列的排序最大task
	 * @param lineId
	 * @return
	 */
	Task findMaxSortTask(int lineId);
	
	/**
	 * 添加任务_人员关联
	 * @param taskId
	 * @param userId
	 * @param executor
	 * @param partner
	 */
	void addTaskUser(int taskId, int userId, int executor, List<Integer> partner);
	
	/**
	 * 获取任务相关人员
	 * @param taskId
	 * @return
	 */
	List<Map<String, Object>> listPartner(int taskId);
	
	/**
	 * 修改指定列名的值
	 * @param task
	 * @param columnName
	 */
	void updateTask(int taskId, Object value,  String columnName, int userId);
	
	/**
	 * 修改任务的执行者
	 * @param taskId
	 * @param executor
	 * @param operation
	 */
	void updateTaskExecutor(int taskId, int executor, int taskExecutor);
	
	/**
	 * 修改任务的参与者
	 * @param taskId
	 * @param partner
	 * @param isSelected
	 */
	void updateTaskPartner(int taskId, List<Integer> partner, boolean isSelected);
	
	int findTaskExecutor(int taskId);
	
	/**
	 * 获取任务参与者id
	 * @return
	 */
	List<Integer> listPartnerIds(int taskId);
	
	/**
	 * 删除任务_人员关联
	 * @param taskId
	 */
	void delTaskUser(int taskId);
	
	/**
	 * 修改排序 + 1
	 * @param sort
	 */
	void updateSortAdd_1(int oneselfSort, int targetSort, int lineId);
	
	/**
	 * 修改排序 - 1
	 * @param sort
	 */
	void updateSortSub_1(int oneselfSort, int targetSort, int lineId);
	
	/**
	 * 获取任务的父级任务和子任务
	 * @param taskId
	 * @return
	 */
	List<Map<String, Object>> listParentAndChildTask(int taskId);
	
	/**
	 * 获取项目的全部任务
	 * @param project
	 * @return
	 */
	List<Task> findTasksByProject(int projectId);
	
	/**
	 * 获取本周今天前已完成和今天后未完成的任务
	 * @param projectId
	 * @return
	 */
	List<Map<String, Object>> findTasksByWeek(int projectId, String startDate, String endDate);
	
	/**
	 * 获取与我相关所有项目下今天及未来的所有任务
	 * @param userId
	 * @param roleType
	 * @return
	 */
	List<Map<String, Object>> findAllProjectTask(int userId, int roleType);
	
	void updateTaskAdmin(int taskId, int admin);
	
	/**
	 * 根据用户id查找已经失效的任务
	 * @param userId
	 * @return
	 */
	List<Object> findDiabledTaskByUseId(int userId);
	
	/**
	 * 根据用户id查找所有任务
	 * @param userId
	 * @return
	 */
	List<Map<String, Object>> findTaskByUserIdForMobile(int userId,int roleType,int taskStatus);
	
}
