package com.ks0100.wp.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks0100.common.util.ReadPropertiesUtil;
import com.ks0100.common.util.TimeUtil;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.audit.AuditServiceAnnotation;
import com.ks0100.wp.audit.AuditServiceAnnotation.ASPECTJ_TYPE;
import com.ks0100.wp.audit.AuditServiceAnnotation.USEAGE_TYPE;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.constant.BusinessConstant.PermitConstant;
import com.ks0100.wp.dao.AttachmentDao;
import com.ks0100.wp.dao.ProjectActionRecordDao;
import com.ks0100.wp.dao.TaskBoardDao;
import com.ks0100.wp.dao.TaskCheckListDao;
import com.ks0100.wp.dao.TaskDao;
import com.ks0100.wp.dao.TaskDiscussDao;
import com.ks0100.wp.dao.TaskLineDao;
import com.ks0100.wp.dto.ProjectDto;
import com.ks0100.wp.dto.RecordDto;
import com.ks0100.wp.dto.TaskCheckListDto;
import com.ks0100.wp.dto.TaskDetailDto;
import com.ks0100.wp.dto.TaskDto;
import com.ks0100.wp.dto.TaskLineDto;
import com.ks0100.wp.entity.Attachment;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.ProjectActionRecord;
import com.ks0100.wp.entity.Task;
import com.ks0100.wp.entity.TaskBoard;
import com.ks0100.wp.entity.TaskCheckList;
import com.ks0100.wp.entity.TaskDiscuss;
import com.ks0100.wp.entity.TaskLine;
import com.ks0100.wp.service.CommonForMobileService;
import com.ks0100.wp.service.ProjectService;
import com.ks0100.wp.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

	@Autowired
	private TaskBoardDao taskBoardDao;
	@Autowired
	private TaskLineDao taskLineDao;
	@Autowired
	private TaskDao taskDao;
	@Autowired
	private AttachmentDao attachmentDao;
	@Autowired
	private TaskDiscussDao taskDiscussDao;
	@Autowired
	private TaskCheckListDao taskCheckListDao;
	@Autowired
	private ProjectActionRecordDao projectActionRecordDao;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private CommonForMobileService commonForMobileService;

	public List<TaskLine> listTaskLines(int boardId, int roleType) {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		List<Map<String, Object>> mapList = taskLineDao.listTaskLineByBoard(user.getUserId(), boardId, roleType);
		List<TaskLine> tempLines = new ArrayList<TaskLine>();
		for (Map<String, Object> map : mapList) {
			TaskLine line = new TaskLine();
			line.setTaskLineId((Integer) map.get("lineId"));
			line.setName((String) map.get("lineName"));
			if (!tempLines.contains(line)) {
				tempLines.add(line);
			}
		}
		for (TaskLine taskLine : tempLines) {
			for (Map<String, Object> map : mapList) {
				if ((Integer) map.get("lineId") == taskLine.getTaskLineId() && (Integer) map.get("taskId") != null) {
					Task task = new Task();
					task.setTaskId((Integer) map.get("taskId"));
					task.setName((String) map.get("taskName"));
					task.setContent((String) map.get("taskContent"));
					task.setLevel((String) map.get("taskLevel"));
					task.setPriority((String) map.get("taskPriority"));
					task.setStatus((String) map.get("taskStatus"));
					if ((Integer) map.get("userId") != null)
						task.setExecutor((Integer) map.get("userId"));
					if (((String) map.get("taskStatus")).equals(BusinessConstant.TaskConstant.TASK_DONE)) {
						taskLine.getDoneTasks().add(task);
					} else {
						taskLine.getTasks().add(task);
					}
					if(SecurityUtils.getSubject().isPermitted(PermitConstant.TASK_CHAT_VIEW + ":" + task.getTaskId()) 
							|| SecurityUtils.getSubject().isPermitted(PermitConstant.TASK_BOARD_ADMIN_ACCESS + ":" + boardId)
							|| SecurityUtils.getSubject().isPermitted(PermitConstant.PRJ_HIGH_ACCESS + ":" + task.getProjectId())){
						task.getPermits().add("TASK_CHAT:VIEW");
					}
					if(SecurityUtils.getSubject().isPermitted(PermitConstant.TASK_SORT + ":" + task.getTaskId())
							|| SecurityUtils.getSubject().isPermitted(PermitConstant.TASK_BOARD_ADMIN_ACCESS + ":" + boardId)
							|| SecurityUtils.getSubject().isPermitted(PermitConstant.PRJ_ADMIN_ACCESS + ":" + task.getProjectId())){
						task.getPermits().add("TASK:SORT");
					}
					if(SecurityUtils.getSubject().isPermitted(PermitConstant.TASK_MOVE_TO_LINE + ":" + task.getTaskId())
							|| SecurityUtils.getSubject().isPermitted(PermitConstant.TASK_BOARD_ADMIN_ACCESS + ":" + boardId)
							|| SecurityUtils.getSubject().isPermitted(PermitConstant.PRJ_ADMIN_ACCESS + ":" + task.getProjectId())){
						task.getPermits().add("TASK:MOVE_TO_LINE");
					}
				}
			}
		}
		return tempLines;
	}
	
	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00658,aspectJ_type = ASPECTJ_TYPE.BEFORE, argIndex1 = 1,argIndex2 = 2)
	public void updateBoardAdmin(int boardId, int boardAdmin){
		taskBoardDao.updateBoardAdmin(boardId, boardAdmin);
	}
	
	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00655,aspectJ_type = ASPECTJ_TYPE.BEFORE, argIndex1 = 1)
	public void updateTaskLine(TaskLine line) {
		TaskLine taskLine = taskLineDao.get(line.getTaskLineId());
		taskLine.setName(line.getName());
		taskLine.setUpdatedBy(-1);
		taskLine.setUpdatedTime(new Date());
		taskLineDao.saveOrUpdate(taskLine);
	}

	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00654, aspectJ_type = ASPECTJ_TYPE.AFTER, argIndex1 = 1, argIndex2 = 2)
	public int addTaskLine(int targetLineId, String lineName) {
		TaskLine targetLine = taskLineDao.get(targetLineId);
		if (targetLine != null) {
			TaskLine line = new TaskLine();
			line.setName(lineName);
			line.setSort(targetLine.getSort() + 1);
			line.setTaskBoard(targetLine.getTaskBoard().getTaskBoardId());
			ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
			line.setCreatedBy(currentUser.getUserId());
			line.setUpdatedBy(currentUser.getUserId());
			// 修改排序
			taskLineDao.updateSortAdd_1(Integer.MAX_VALUE, targetLine.getSort() + 1, targetLine.getTaskBoard().getTaskBoardId());
			ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
			line.setCreatedBy(user.getUserId());
			taskLineDao.save(line);
			// 添加任务列_人员关联
			taskLineDao.addTaskLineUser(line.getTaskLineId(), currentUser.getUserId());
			return line.getTaskLineId();
		}
		return 0;
	}
	
	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00656, aspectJ_type = ASPECTJ_TYPE.BEFORE, argIndex1=1)
	public String delTaskLine(int taskLineId) {
		TaskLine taskLine = taskLineDao.get(taskLineId);
		int boardId = taskLine.getTaskBoard().getTaskBoardId();
		List<TaskLine> taskLines = taskLineDao.listTaskLines(boardId);
		if (taskLines.size() == 1)
			return ReadPropertiesUtil.getStringContextProperty("del_line_error");
		taskLine.setEnabled(false);
		taskLine.setSort(-1);
		taskLine.setUpdatedBy(-1);
		taskLine.setUpdatedTime(new Date());
		taskLineDao.saveOrUpdate(taskLine);
		return null;
	}

	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00600, aspectJ_type = ASPECTJ_TYPE.AFTER, argType1 = "com.ks0100.wp.entity.Task", argIndex2 = 5,argIndex3=4)
	public int addTask(Task task, List<Integer> partner, int executor, boolean visible, int parentTaskId) {
		Task maxSortTask = taskDao.findMaxSortTask(task.getTaskLine().getTaskLineId());
		ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		task.setCreatedBy(currentUser.getUserId());
		task.setUpdatedBy(currentUser.getUserId());
		int sort = maxSortTask != null ? maxSortTask.getSort() + 1 : 0;
		task.setSort(sort);
		if (!visible)
			task.setVisibleStatus(BusinessConstant.TaskConstant.TASK_VISIBLE_STATUS_PARTNER);
		if (parentTaskId > 0)
			task.setParentTask(parentTaskId);
		taskDao.save(task);
		// 添加任务_人员关联
		taskDao.addTaskUser(task.getTaskId(), currentUser.getUserId(), executor, partner);
		currentUser.getPermissions().clear();
		return task.getTaskId();
	}
	
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00651,aspectJ_type = ASPECTJ_TYPE.BEFORE,argType1="com.ks0100.wp.entity.Task")
	public void updateTaskStatus(Task task) {
		Task t = taskDao.get(task.getTaskId());
		t.setStatus(task.getStatus());
		if(task.getStatus().equals(BusinessConstant.TaskConstant.TASK_NO_PASS)){
			ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
			t.setNoPassTimes(t.getNoPassTimes()+1);
			SimpleDateFormat  sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
			String nopassReasonString = currentUser.getName() + sdf.format(new Date())+"将任务设置成不通过，不通过理由："+ task.getNoPassReason();
			if(t.getNoPassReason() != null){
				t.setNoPassReason(nopassReasonString+"|"+t.getNoPassReason());
			}else{
				t.setNoPassReason(nopassReasonString);
			}
			
		}
		if(t.getStatus().equals(BusinessConstant.TaskConstant.TASK_DONE)){
			t.setCompleteTime(new Date());
		}else{
			t.setCompleteTime(null);
		}
		//boolean status = Boolean.parseBoolean(task.getStatus());
		//t.setStatus(status ? BusinessConstant.TaskConstant.TASK_DONE : BusinessConstant.TaskConstant.TASK_NO);
		//t.setCompleteTime(status ? new Date() : null);
		t.setUpdatedBy(-1);
		t.setUpdatedTime(new Date());
		taskDao.update(t);
	}

	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00605, aspectJ_type = ASPECTJ_TYPE.BEFORE, argIndex1 = 1, argIndex2 = 2, argIndex3 = 3)
	public void updateTask(int taskId, String value, String columnName) {
		ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		taskDao.updateTask(taskId, value, columnName, currentUser.getUserId());
	}

	public Task findTaskData(int taskId) {
		Task task = taskDao.get(taskId);
		if (task != null) {
			task.setBoardId(task.getTaskLine().getTaskBoard().getTaskBoardId());
			List<Map<String, Object>> partners = taskDao.listPartner(task.getTaskId());
			for (Map<String, Object> map : partners) {
				String roleCode = (String) map.get("roleCode");
				if (BusinessConstant.RoleConstant.TASK_EXECUTOR.equals(roleCode)) {
					task.setExecutor((Integer) map.get("userId"));
				} else if (BusinessConstant.RoleConstant.TASK_MEMBER.equals(roleCode)) {
					task.getPartnerIds().add((Integer) map.get("userId"));
				} else if (BusinessConstant.RoleConstant.TASK_ADMIN.equals(roleCode)) {
					task.setAdmin((Integer) map.get("userId"));
				}
			}
			DateTime createdDateTime = new DateTime(task.getCreatedTime()); 
			if(task.getDueTime() != null){
				DateTime dueDateTime = new DateTime(task.getDueTime()); 
				int planday = TimeUtil.daysBetween(createdDateTime, dueDateTime);
				task.setPlanTime(planday+"天");
			}
			if(task.getCompleteTime() != null){
				DateTime completeDateTime = new DateTime(task.getCompleteTime()); 
				int realityday = TimeUtil.daysBetween(createdDateTime, completeDateTime); 
				task.setRealityTime(realityday +"天");
			}
			
			
           
		}
		return task;
	}
	
	public List<Integer> findTaskUser(int taskId) {
		List<Integer> userIds = new ArrayList<Integer>();
		List<Map<String, Object>> partners = taskDao.listPartner(taskId);
		for (Map<String, Object> map : partners) {
			if(!userIds.contains((Integer) map.get("userId"))){
				userIds.add((Integer) map.get("userId"));
			}
		}
		return userIds;
	}

	public List<TaskBoard> listTaskBoards(int projectId) {
		List<TaskBoard> boards = new ArrayList<TaskBoard>();
		List<Map<String, Object>> listTaskBoards = taskBoardDao.listTaskBoards(projectId);
		for (Map<String, Object> boardMap : listTaskBoards) {
			TaskBoard tb = new TaskBoard();
			tb.setTaskBoardId((Integer) boardMap.get("taskBoardId"));
			tb.setName((String) boardMap.get("name"));
			tb.setRemark((String) boardMap.get("remark"));
			if(boardMap.get("boardAdmin") != null)
				tb.setBoardAdmin((Integer) boardMap.get("boardAdmin"));
			if(!boards.contains(tb)){
				boards.add(tb);
			}
			
		}
		for (TaskBoard taskboard : boards) {
			for (Map<String, Object> map : listTaskBoards) {
				if ((Integer) map.get("taskBoardId") == taskboard.getTaskBoardId() && (Integer) map.get("taskLineId") != null) {
					TaskLine taskLine = new TaskLine();
					taskLine.setTaskLineId((Integer) map.get("taskLineId"));
					taskLine.setName((String) map.get("taskLineName"));
					List<TaskLine> taskLines = taskboard.getTaskLines();
					taskLines.add(taskLine);
					taskboard.setTaskLines(taskLines);
				}
			}
		}
		return boards;
	}
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00657,aspectJ_type = ASPECTJ_TYPE.AFTER, argIndex1=1   ,   argType2="com.ks0100.wp.entity.TaskBoard")
	public int addTaskBoard(int projectId, TaskBoard board) {
		board.setProject(projectId);
		ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		board.setCreatedBy(currentUser.getUserId());
		board.setUpdatedBy(currentUser.getUserId());
		taskBoardDao.save(board);
		// 添加任务板_用户关联
		taskBoardDao.addBoardUser(currentUser.getUserId(), board.getTaskBoardId(), BusinessConstant.RoleConstant.TASK_BOARD_ADMIN);
		// 添加默认任务列
		addDefaultTaskLine(board.getTaskBoardId());
		currentUser.getPermissions().clear();
		return board.getTaskBoardId();
	}

	public void addDefaultTaskLine(int taskBoardId) {
		String lineNames = ReadPropertiesUtil.getStringContextProperty("task_line_name");
		String[] names = lineNames.replaceAll("\\s+", "").split("[,，]");
		ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		int userId = currentUser.getUserId();
		for (int i = 0; i < names.length; i++) {
			TaskLine line = new TaskLine();
			line.setName(names[i]);
			line.setSort(i);
			line.setTaskBoard(taskBoardId);
			line.setCreatedBy(userId);
			line.setUpdatedBy(userId);
			taskLineDao.save(line);
			// 添加任务列_人员关联
			taskLineDao.addTaskLineUser(line.getTaskLineId(), userId);
		}
	}

	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00660,aspectJ_type = ASPECTJ_TYPE.BEFORE, argIndex1 = 1)
	public void updateTaskBoard(TaskBoard board) {
		TaskBoard tb = taskBoardDao.get(board.getTaskBoardId());
		tb.setName(board.getName());
		tb.setRemark(board.getRemark());
		tb.setUpdatedBy(-1);
		tb.setUpdatedTime(new Date());
		taskBoardDao.update(tb);
		
	}

	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00661,aspectJ_type = ASPECTJ_TYPE.BEFORE, argIndex1 = 1)
	public String delTaskBoard(int boardId) {
		TaskBoard tb = taskBoardDao.get(boardId);
		int projectId = tb.getProject().getProjectId();
		List<TaskBoard> boards = listTaskBoards(projectId);
		if (boards.size() == 1)
			return ReadPropertiesUtil.getStringContextProperty("del_board_error");
		tb.setEnabled(false);
		tb.setUpdatedBy(-1);
		tb.setUpdatedTime(new Date());
		taskBoardDao.update(tb);
		return null;
	}

	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00609, argIndex1 = 1, argIndex2 = 2)
	public int updateTaskExecutor(int taskId, int executor) {
		int taskExecutor = taskDao.findTaskExecutor(taskId);
		taskDao.updateTaskExecutor(taskId, executor, taskExecutor);
		return taskExecutor;
	}

	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00610, argIndex1 = 1, argIndex2 = 2, argIndex3 = 3)
	public void updateTaskPartner(int taskId, List<Integer> partner, boolean isSelected) {
		if (partner.size() >=1) {
			if(isSelected){
				List<Integer> ids = taskDao.listPartnerIds(taskId);
				partner.removeAll(ids);
				if(!partner.isEmpty()){
					taskDao.addTaskUser(taskId, 0, 0, partner);
				}
			}else{
				taskDao.updateTaskPartner(taskId, partner, isSelected);
			}
		} else {
			taskDao.updateTaskPartner(taskId, partner, isSelected);
		}
	}

	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00604, aspectJ_type = ASPECTJ_TYPE.BEFORE, argIndex1 = 1)
	public String delTask(int taskId) {
		Task task = listParentAndChildTask(taskId);
		if (task != null && !task.getChildren().isEmpty()) {
			return ReadPropertiesUtil.getStringContextProperty("del_task_error");
		}
		// 删除任务_人员关联
		taskDao.delTaskUser(taskId);
		// 删除任务回复
		taskDiscussDao.delTaskDiscuss(taskId);
		// 删除动态
		// projectActionRecordDao.delTaskActionRecord(taskId);
		taskDao.delete(taskId);
		return null;
	}

	public void moveTaskLine(int oneselfId, int targetId) {
		TaskLine oneselfLine = taskLineDao.get(oneselfId);
		int boardId = oneselfLine.getTaskBoard().getTaskBoardId();
		if (targetId > 0) {
			TaskLine targetLine = taskLineDao.get(targetId);
			if (oneselfLine.getSort() < targetLine.getSort()) {
				taskLineDao.updateSortSub_1(oneselfLine.getSort(), targetLine.getSort(), boardId);
				oneselfLine.setSort(targetLine.getSort());
			} else {
				taskLineDao.updateSortAdd_1(oneselfLine.getSort(), targetLine.getSort() + 1, boardId);
				oneselfLine.setSort(targetLine.getSort() + 1);
			}
		} else {
			taskLineDao.updateSortAdd_1(oneselfLine.getSort(), 0, boardId);
			oneselfLine.setSort(0);
		}
		taskLineDao.update(oneselfLine);
	}

	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00601, aspectJ_type = ASPECTJ_TYPE.BEFORE, argIndex1 = 3, argIndex2 = 4, argIndex3 = 1)
	public void moveTask(int oneselfId, int targetId, int oneselfLineId, int targetLineId) {
		Task oneselfTask = taskDao.get(oneselfId);
		if (oneselfLineId == targetLineId) {
			if (targetId > 0) {
				Task targetTask = taskDao.get(targetId);
				if (oneselfTask.getSort() < targetTask.getSort()) {
					taskDao.updateSortSub_1(oneselfTask.getSort(), targetTask.getSort(), oneselfLineId);
					oneselfTask.setSort(targetTask.getSort());
				} else {
					taskDao.updateSortAdd_1(oneselfTask.getSort(), targetTask.getSort() + 1, oneselfLineId);
					oneselfTask.setSort(targetTask.getSort() + 1);
				}
			} else {
				taskDao.updateSortAdd_1(oneselfTask.getSort(), 0, oneselfLineId);
				oneselfTask.setSort(0);
			}
		} else {
			if (targetId > 0) {
				Task targetTask = taskDao.get(targetId);
				taskDao.updateSortAdd_1(Integer.MAX_VALUE, targetTask.getSort() + 1, targetLineId);
				oneselfTask.setSort(targetTask.getSort() + 1);
			} else {
				if (oneselfLineId == 0) {
					Task task = taskDao.findMaxSortTask(targetLineId);
					oneselfTask.setSort(task == null ? 0 : task.getSort() + 1);
				} else if (oneselfLineId == -1) {
					TaskLine line = taskLineDao.findMinSortLine(targetLineId);
					targetLineId = line.getTaskLineId();
					Task task = taskDao.findMaxSortTask(line.getTaskLineId());
					oneselfTask.setSort(task == null ? 0 : task.getSort() + 1);
				} else {
					taskDao.updateSortAdd_1(Integer.MAX_VALUE, 0, targetLineId);
					oneselfTask.setSort(0);
				}
			}
			oneselfTask.setTaskLine(targetLineId);
		}
		taskDao.update(oneselfTask);
	}

	public List<Attachment> findAttachmentByTask(int taskId) {
		return attachmentDao.findAttachments(BusinessConstant.TableNameConstant.TABLE_TASK, taskId, BusinessConstant.FileType.FILESAVETYPE_TASK);
	}

	public void addTaskDiscuss(int taskId, String content) {
		TaskDiscuss td = new TaskDiscuss();
		td.setContent(content);
		td.setTask(taskId);
		td.setCreatedBy(-1);
		td.setUpdatedBy(-1);
		taskDiscussDao.save(td);
	}

	public List<TaskDiscuss> findTaskDiscuss(int taskId) {
		return taskDiscussDao.findDiscussByTask(taskId);
	}

	public Task listParentAndChildTask(int taskId) {
		Task task = null;
		List<Map<String, Object>> tasks = taskDao.listParentAndChildTask(taskId);
		if (!tasks.isEmpty()) {
			task = new Task();
			for (Map<String, Object> map : tasks) {
				if ((Integer) map.get("parentId") == null || (Integer) map.get("parentId") != taskId) {
					Task parentTask = new Task();
					parentTask.setTaskId((Integer) map.get("taskId"));
					parentTask.setContent((String) map.get("content"));
					if ((Integer) map.get("executor") != null)
						parentTask.setExecutor((Integer) map.get("executor"));
					task.setParentTask(parentTask);
				} else {
					Task t = new Task();
					t.setTaskId((Integer) map.get("taskId"));
					t.setContent((String) map.get("content"));
					if ((Integer) map.get("executor") != null)
						t.setExecutor((Integer) map.get("executor"));
					task.getChildren().add(t);
				}
			}
		}
		return task;
	}

	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00612, argIndex1 = 1, argIndex2 = 2)
	public int addCheckList(int taskId, String content) {
		TaskCheckList tcl = new TaskCheckList();
		tcl.setListContent(content);
		tcl.setTask(taskId);
		tcl.setCreatedBy(-1);
		tcl.setUpdatedBy(-1);
		taskCheckListDao.save(tcl);
		return tcl.getTaskCheckListId();
	}

	public List<TaskCheckList> findTaskCheckList(int taskId) {
		return taskCheckListDao.findCheckList(taskId);
	}

	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00614, aspectJ_type = ASPECTJ_TYPE.BEFORE, argIndex1 = 1, argIndex2 = 2, argIndex3 = 3)
	public void updateTaskCheckList(int checkListId, String value, String columnName) {
		ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		if ("status".equals(columnName))
			value = Boolean.parseBoolean(value) ? BusinessConstant.TaskConstant.TASK_CHECK_LIST_STATUS_DONE : BusinessConstant.TaskConstant.TASK_CHECK_LIST_STATUS_NO;
		taskCheckListDao.updateTaskCheckList(checkListId, value, columnName, currentUser.getUserId());
	}

	@AuditServiceAnnotation(useage = USEAGE_TYPE.u00613, aspectJ_type = ASPECTJ_TYPE.BEFORE, argIndex1 = 1)
	public void delTaskCheckList(int taskCheckListId) {
		taskCheckListDao.delete(taskCheckListId);
	}

	public List<ProjectActionRecord> listTaskActionRecords(int taskId) {
		return projectActionRecordDao.listTaskActionRecords(taskId);
	}

	public TaskLine findTaskLineById(int lineId) {
		return taskLineDao.get(lineId);
	}

	public TaskBoard findTaskBoardById(int boardId) {
		return taskBoardDao.get(boardId);
	}

	public Task findTaskById(int taskId) {
		return taskDao.get(taskId);
	}
	
	public TaskCheckList findCheckListById(int checkListId){
		return taskCheckListDao.get(checkListId);
	}

	public Map<String, Object> findTaskStatistics(int projectId) {
		Map<String, Object> map = new HashMap<String, Object>();
		int doneTaskCount = 0, tardyTaskCount = 0, doingTaskCount = 0, toPendingTaskCount = 0, noPassTaskAccount = 0 ;
		List<Task> tasks = taskDao.findTasksByProject(projectId);
		for (Task task : tasks) {
			if (task.getStatus().equals(BusinessConstant.TaskConstant.TASK_DONE)) {
				doneTaskCount++;
			}else if(task.getStatus().equals(BusinessConstant.TaskConstant.TASK_DONING)){
				doingTaskCount++;
				if (task.getDueTime() != null) {
					if (new DateTime(task.getDueTime()).plusDays(1).isBeforeNow()) {
						tardyTaskCount++;
					}
				}
			}else if(task.getStatus().equals(BusinessConstant.TaskConstant.TASK_NO_PASS)){
				noPassTaskAccount++;
				if (task.getDueTime() != null) {
					if (new DateTime(task.getDueTime()).plusDays(1).isBeforeNow()) {
						tardyTaskCount++;
					}
				}
			}else if(task.getStatus().equals(BusinessConstant.TaskConstant.TASK_TO_CHECK)){
				toPendingTaskCount++;
				if (task.getDueTime() != null) {
					if (new DateTime(task.getDueTime()).plusDays(1).isBeforeNow()) {
						tardyTaskCount++;
					}
				}
			}
		}
		map.put("taskCount", tasks.size());
		map.put("doneTaskCount", doneTaskCount);
		map.put("tocheckTaskCount", toPendingTaskCount);
		map.put("tardyTaskCount", tardyTaskCount);
		map.put("doingTaskCount", doingTaskCount);
		map.put("noPassTaskAccount", noPassTaskAccount);
		return map;
	}

	public Map<String, Object> findTasksByWeek(int projectId) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> noTaskCount = new HashMap<String, Object>();
		Map<String, Object> doneTaskCount = new HashMap<String, Object>();
		DateTime now = new DateTime();
		
		String startDate =now.dayOfWeek().withMinimumValue().toString("yyyy-MM-dd"); //DateUtil.formatDate(DateUtil.firstDayOfWeek());
		String endDate = now.dayOfWeek().withMaximumValue().toString("yyyy-MM-dd");//DateUtil.formatDate(DateUtil.lastDayOfWeek());
		List<Map<String, Object>> taskMaps = taskDao.findTasksByWeek(projectId, startDate, endDate);
		for (Map<String, Object> taskMap : taskMaps) {
			if (String.valueOf(taskMap.get("status")).equals(BusinessConstant.TaskConstant.TASK_DONE)) {
				doneTaskCount.put(String.valueOf(taskMap.get("completeTime")), taskMap.get("count"));
			} else {
				noTaskCount.put(String.valueOf(taskMap.get("dueTime")), taskMap.get("count"));
			}
		}
		map.put("dayOfWeek", now.getDayOfWeek());
		map.put("noTaskCount", noTaskCount);
		map.put("doneTaskCount", doneTaskCount);
		return map;
	}

	public Map<String, Object> findTaskOtherData(int projectId, int taskId) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<TaskBoard> boards = listTaskBoards(projectId);
		Map<String, Object> allUser = projectService.listProjectAllUser(projectId);
		Task task = findTaskById(taskId);
		int boardId = task.getTaskLine().getTaskBoard().getTaskBoardId();
		List<TaskLine> lines = taskLineDao.listTaskLines(boardId);
		map.put("boardId", boardId);
		map.put("boardList", boards);
		map.put("lineList", lines);
		map.put("userList", allUser);
		return map;
	}

	public Map<String, Object> findAllProjectTask(int roleType) {
		ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		Map<String, Object> map = new HashMap<String, Object>();
		List<Project> todayProjects = new ArrayList<Project>();
		List<Project> futureProjects = new ArrayList<Project>();
		List<Map<String, Object>> mapList = taskDao.findAllProjectTask(currentUser.getUserId(), roleType);
		Map<Integer, Project> projectTodayMap = new HashMap<Integer, Project>();
		Map<Integer, Project> projectFutureMap = new HashMap<Integer, Project>();
		for (Map<String, Object> tempMap : mapList) {
			if (tempMap.get("due_time") != null) {
				 DateTime now = new DateTime();// 取得当前时间  
				 DateTime a=new DateTime((Date) tempMap.get("due_time"));
				if (Days.daysBetween(a,now).getDays()==0 ) {
					packProject(todayProjects, projectTodayMap, tempMap);
				} else {
					packProject(futureProjects, projectFutureMap, tempMap);
				}
			} else {
				packProject(futureProjects, projectFutureMap, tempMap);
			}
		}
		map.put("todayProjects", todayProjects);
		map.put("futureProjects", futureProjects);
		return map;
	}

	private void packProject(List<Project> projects, Map<Integer, Project> projectMap, Map<String, Object> tempMap) {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		List<Object> delList = taskDao.findDiabledTaskByUseId(user.getUserId());
		if (projectMap.get((Integer) tempMap.get("projectId")) == null) {
			Project project = new Project();
			project.setProjectId((Integer) tempMap.get("projectId"));
			project.setName((String) tempMap.get("name"));
			projectMap.put(project.getProjectId(), project);
			projects.add(project);
		}
		Project project = projectMap.get((Integer) tempMap.get("projectId"));
		Task task = new Task();
		task.setTaskId((Integer) tempMap.get("taskId"));
		task.setContent((String) tempMap.get("content"));
		task.setPriority((String) tempMap.get("priority"));
		task.setLevel((String) tempMap.get("level"));
		task.setStatus((String) tempMap.get("status"));
		if(tempMap.get("executor") != null)
			task.setExecutor((Integer) tempMap.get("executor"));
		if(delList.isEmpty()){
			project.getTasks().add(task);
		}else{
			for(Object delId:delList){
				if(task.getTaskId()!=(Integer)delId){
					project.getTasks().add(task);
				}
			}
		}
		
	}

	@Override
	public void updateTaskAdmin(int taskId, int admin) {
		taskDao.updateTaskAdmin(taskId, admin);
	}

	@Override
	public List<TaskBoard> listTaskBoardsForMobile(int projectId) {
		List<TaskBoard> boards = new ArrayList<TaskBoard>();
		List<Map<String, Object>> listTaskBoards = taskBoardDao.listTaskBoards(projectId);
		for (Map<String, Object> boardMap : listTaskBoards) {
			TaskBoard tb = new TaskBoard();
			tb.setTaskBoardId((Integer) boardMap.get("taskBoardId"));
			tb.setName((String) boardMap.get("name"));
			if(!boards.contains(tb)){
				boards.add(tb);
			}
		}
		return boards;
	}

	@Override
	public List<TaskLineDto> listTaskLinesForMobile(int userId, int boardId, int roleType) {
		DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd HH:mm:ss"); 
		List<Map<String, Object>> mapList = taskLineDao.listTaskLineByBoard(userId, boardId, roleType);
		List<TaskLine> lineList = taskLineDao.listTaskLines(boardId);
		List<TaskLineDto> tempLines = new ArrayList<TaskLineDto>();
		for(TaskLine line:lineList){
			TaskLineDto dto = new TaskLineDto();
			dto.setTaskLineId(line.getTaskLineId());
			dto.setName(line.getName());
			dto.setTaskDtoList(null);
			if(Collections.frequency(tempLines, dto)<1){
				tempLines.add(dto);
			}
		}
		for (TaskLineDto taskLine : tempLines) {
			List<TaskDto> list = new ArrayList<TaskDto>();
			for (Map<String, Object> map : mapList) {
				if ((Integer) map.get("lineId") == taskLine.getTaskLineId() && (Integer) map.get("taskId") != null) {
					TaskDto task = new TaskDto();
					task.setStatus((String)map.get("taskStatus"));
					task.setTaskId((Integer) map.get("taskId"));
					task.setContent((String) map.get("taskContent"));
					task.setCreatedTime(new DateTime((Date)map.get("createdTime")).toString(format));
					task.setCheckListNum(taskCheckListDao.findCheckListsNumByStatus(task.getTaskId(), "00400")+taskCheckListDao.findCheckListsNumByStatus(task.getTaskId(), "00401"));
					task.setCheckListDoneNum(taskCheckListDao.findCheckListsNumByStatus(task.getTaskId(), "00401"));
					task.setRecordNum(projectActionRecordDao.findTaskRecordNum(task.getTaskId()));
					if(Collections.frequency(list,task)<1){
						list.add(task);
					}
				}
			}
			taskLine.setTaskDtoList(list);
		}
		return tempLines;
	}

	@Override
	public TaskDetailDto findTaskDetailForMobile(int taskId) {
		Task task = taskDao.get(taskId);
		TaskDetailDto dto = new TaskDetailDto();
		if (task != null) {
			List<Map<String, Object>> partners = taskDao.listPartner(task.getTaskId());
			for (Map<String, Object> map : partners) {
				String roleCode = (String) map.get("roleCode");
				if (BusinessConstant.RoleConstant.TASK_EXECUTOR.equals(roleCode)) {
					dto.setExecutorId((Integer) map.get("userId"));
					dto.setExecutorName((String)map.get("name"));
				} else if (BusinessConstant.RoleConstant.TASK_MEMBER.equals(roleCode)) {
					if(Collections.frequency(dto.getPartner(), (Integer)map.get("userId"))<1){
						dto.getPartner().add((Integer)map.get("userId"));
					}
				} else if (BusinessConstant.RoleConstant.TASK_ADMIN.equals(roleCode)) {
					dto.setAdminId((Integer) map.get("userId"));
					dto.setAdminName((String)map.get("name"));
				}
			}
			DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd HH:mm:ss");
			dto.setCreatedTime(new DateTime(task.getCreatedTime()).toString(format));
			dto.setEndTime(new DateTime(task.getDueTime()).toString(format));
			dto.setTaskId(task.getTaskId());
			dto.setContent(task.getContent());
			dto.setStatus(task.getStatus());
			dto.setPriority(task.getPriority());
			dto.setRemark(task.getRemark());
			List<TaskCheckList> checkLists = taskCheckListDao.findCheckList(taskId);
			List<TaskCheckListDto> checkDtoLists = new ArrayList<TaskCheckListDto>();
			if(checkLists.size()>0){
				for(TaskCheckList check:checkLists){
					TaskCheckListDto checkDto = new TaskCheckListDto();
					checkDto.setCheckListId(check.getTaskCheckListId());
					checkDto.setCheckListContent(check.getListContent());
					checkDto.setStatus(check.getStatus());
					if(Collections.frequency(checkDtoLists, checkDto)<1){
						checkDtoLists.add(checkDto);
					}
				}
				dto.setCheckLists(checkDtoLists);
			}
			Task task2 = listParentAndChildTask(taskId);
			if(task2!=null){
				if(task2.getParentTask()!=null){
					dto.setParentTaskContent(task2.getParentTask().getContent());
					dto.setParentTaskId(task2.getParentTask().getTaskId());
				}
				if(task2.getChildren().size()>0){
					List<TaskDto> taskDtos = new ArrayList<TaskDto>();
					for(Task childTask:task2.getChildren()){
						TaskDto taskDto = new TaskDto();
						taskDto.setContent(task.getContent());
						taskDto.setTaskId(childTask.getTaskId());
						if(Collections.frequency(taskDtos, taskDto)<1){
							taskDtos.add(taskDto);
						}
					}
					dto.setChildTaskList(taskDtos);
				}
			}
		}
		return dto;
	}

	@Override
	public List<RecordDto> listTaskActionRecordsForMobile(int taskId) {
		List<RecordDto> dtoList = new ArrayList<RecordDto>();
		List<ProjectActionRecord> records = projectActionRecordDao.listTaskActionRecords(taskId);
		DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd HH:mm:ss");
		for(ProjectActionRecord record:records){
			RecordDto dto = new RecordDto();
			dto.setId(record.getId());
			dto.setUserName(record.getUserName());
			dto.setUserId(record.getUserId());
			dto.setRecord(record.getRecord());
			dto.setCreatedTime(new DateTime(record.getCreatedTime()).toString(format));
			if(Collections.frequency(dtoList, dto)<1){
				dtoList.add(dto);
			}
		}
		return dtoList;
	}

	@Override
	public List<ProjectDto> listTaskByUserIdForMobile(int userId,int roleType,int taskStatus) {
		DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> taskList = taskDao.findTaskByUserIdForMobile(userId,roleType,taskStatus);
		List<ProjectDto> projectDtoList = new ArrayList<ProjectDto>();
		for(Map<String, Object> map:taskList){
			ProjectDto projectDto = new ProjectDto();
			projectDto.setProjectId((Integer)map.get("projectId"));
			projectDto.setName((String)map.get("projectName"));
			if(Collections.frequency(projectDtoList, projectDto)<1){
				projectDtoList.add(projectDto);
			}
		}
		for(Map<String, Object> map:taskList){
			for(ProjectDto dto:projectDtoList){
				TaskDto taskDto = new TaskDto();
				taskDto.setTaskId((Integer)map.get("id"));
				taskDto.setContent((String)map.get("content"));
				taskDto.setCreatedTime(new DateTime((Date)map.get("createdTime")).toString(format));
				taskDto.setCheckListNum(taskCheckListDao.findCheckListsNumByStatus(taskDto.getTaskId(), "00400")+taskCheckListDao.findCheckListsNumByStatus(taskDto.getTaskId(), "00401"));
				taskDto.setCheckListDoneNum(taskCheckListDao.findCheckListsNumByStatus(taskDto.getTaskId(), "00401"));
				taskDto.setRecordNum(projectActionRecordDao.findTaskRecordNum(taskDto.getTaskId()));
				taskDto.setStatus((String)map.get("status"));
				if(Collections.frequency(dto.getTaskDtos(), taskDto)<1){
					dto.getTaskDtos().add(taskDto);
				}
			}
		}
		return projectDtoList;
	}
}
