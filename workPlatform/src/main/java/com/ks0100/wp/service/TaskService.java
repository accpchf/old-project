package com.ks0100.wp.service;

import java.util.List;
import java.util.Map;

import com.ks0100.wp.dto.ProjectDto;
import com.ks0100.wp.dto.RecordDto;
import com.ks0100.wp.dto.TaskDetailDto;
import com.ks0100.wp.dto.TaskLineDto;
import com.ks0100.wp.entity.Attachment;
import com.ks0100.wp.entity.ProjectActionRecord;
import com.ks0100.wp.entity.Task;
import com.ks0100.wp.entity.TaskBoard;
import com.ks0100.wp.entity.TaskCheckList;
import com.ks0100.wp.entity.TaskDiscuss;
import com.ks0100.wp.entity.TaskLine;

/**
 * 任务看板业务操作接口
 * @author xie linming
 * @date 2014年12月2日
 */
public interface TaskService {
	
	/**
	 * 获取看板的所有任务列(包含任务)信息
	 * @param boardId
	 * @return
	 */
	List<TaskLine> listTaskLines(int boardId, int roleType);
	
	/**
	 * 修改任务板管理员
	 * @param boardId
	 * @param boardAdmin
	 */
	void updateBoardAdmin(int boardId, int boardAdmin);
	
	/**
	 * 修改任务列
	 * @param line
	 */
	void updateTaskLine(TaskLine line);
	
	/**
	 * 添加任务列
	 * @param targetLineId 目标任务列id
	 * @param lineName 要添加的任务列名称
	 * @return
	 */
	int addTaskLine(int targetLineId, String lineName);
	
	/**
	 * 删除任务列
	 * @param taskLineId
	 */
	String delTaskLine(int taskLineId);
	
	/**
	 * 添加任务
	 * @param task 被添加的任务对象
	 * @param partner 参与者id数组
	 * @param executor 执行者
	 * @param visible 任务可见性, true为所有人可见,false仅参与者可见
	 * @parentTaskId 父级任务id, 为0是则创建顶级任务
	 * @return
	 */
	 int addTask(Task task, List<Integer> partner, int executor, boolean visible, int parentTaskId) ;
	
	/**
	 * 修改任务完成状态
	 * @param task
	 */
	void updateTaskStatus(Task task);

	/**
	 * 修改指定列名的值
	 * @param taskId
	 * @param value
	 * @param columnName
	 */
	void updateTask(int taskId, String value, String columnName);
	
	Task findTaskData(int taskId);
	
	TaskDetailDto findTaskDetailForMobile(int taskId);
	
	/**
	 * 获取任务相关人员
	 * @param taskId
	 * @return
	 */
	List<Integer> findTaskUser(int taskId);
	
	/**
	 * 获取项目所有看板
	 * @param projectId
	 * @return
	 */
	List<TaskBoard> listTaskBoards(int projectId);
	
	List<TaskBoard> listTaskBoardsForMobile(int projectId);
	
	List<TaskLineDto> listTaskLinesForMobile(int userId,int boardId, int roleType);
	
	List<ProjectDto> listTaskByUserIdForMobile(int userId,int roleType,int taskStatus);
	
	/**
	 * 添加任务看板
	 * @param projectId 所属项目id
	 * @param board
	 * @return
	 */
	int addTaskBoard(int projectId, TaskBoard board);
	
	/**
	 * 添加默认的任务列
	 * @param taskBoardId
	 */
	void addDefaultTaskLine(int taskBoardId);
	
	/**
	 * 修改任务板
	 * @param board
	 */
	void updateTaskBoard(TaskBoard board);
	
	String delTaskBoard(int boardId);
	
	/**
	 * 修改任务执行者
	 * @param taskId
	 * @param executor
	 */
	int updateTaskExecutor(int taskId, int executor);
	
	/**
	 * 修改任务参与者
	 * @param taskId
	 * @param partner
	 * @param isSelected
	 */
	void updateTaskPartner(int taskId, List<Integer> partner, boolean isSelected);
	
	String delTask(int taskId);
	
	/**
	 * 获取任务相关附件
	 * @param taskId
	 * @return
	 */
	List<Attachment> findAttachmentByTask(int taskId);
	
	/**
	 * 移动任务列
	 * @param oneselfId
	 * @param targetId
	 */
	void moveTaskLine(int oneselfId, int targetId);
	
	/**
	 * 移动任务
	 * @param oneselfId
	 * @param targetId
	 */
	void moveTask(int oneselfId, int targetId, int oneselfLineId, int targetLineId);
	
	/**
	 * 添加任务讨论
	 * @param taskId
	 */
	void addTaskDiscuss(int taskId, String content);
	
	List<TaskDiscuss> findTaskDiscuss(int taskId);
	
	/**
	 * 获取任务的父级任务和子任务
	 * @param taskId
	 * @return
	 */
	Task listParentAndChildTask(int taskId);
	
	int addCheckList(int taskId, String content);
	
	/**
	 * 获取任务检查项
	 * @param taskId
	 * @return
	 */
	List<TaskCheckList> findTaskCheckList(int taskId);
	
	void updateTaskCheckList(int taskId, String value, String columnName);

	void delTaskCheckList(int taskCheckListId);
	
	/**
	 * 获取任务动态
	 * @param taskId
	 * @return
	 */
	List<ProjectActionRecord> listTaskActionRecords(int taskId);
	
	List<RecordDto> listTaskActionRecordsForMobile(int taskId);
	
	Task findTaskById(int taskId);
	
	TaskLine findTaskLineById(int lineId);
	
	TaskBoard findTaskBoardById(int boardId);
	
	TaskCheckList findCheckListById(int checkListId);
	
	/**
	 * 任务完成状态统计
	 * @param projectId
	 * @return
	 */
	Map<String, Object> findTaskStatistics(int projectId);
	
	/**
	 * 本周任务进展
	 * @param projectId
	 * @return
	 */
	Map<String, Object> findTasksByWeek(int projectId);
	
	Map<String, Object> findTaskOtherData(int projectId, int taskId);
	/**
	 * 获取与我相关所有项目下今天及未来的所有任务
	 * @param roleType
	 * @return
	 */
	Map<String, Object> findAllProjectTask(int roleType);
	/**
	 * 更新任务的管理员
	 * @param taskId
	 * @param executor
	 * @return
	 */
	void updateTaskAdmin(int taskId, int admin);
}
