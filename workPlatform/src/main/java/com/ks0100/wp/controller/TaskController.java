package com.ks0100.wp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.common.constant.CommonConstant;
import com.ks0100.common.util.PathUtil;
import com.ks0100.common.util.ReadPropertiesUtil;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.audit.ArgumentsForFile;
import com.ks0100.wp.audit.AuditServiceAspectJ;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.constant.BusinessConstant.PermitConstant;
import com.ks0100.wp.entity.Attachment;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.ProjectActionRecord;
import com.ks0100.wp.entity.Task;
import com.ks0100.wp.entity.TaskBoard;
import com.ks0100.wp.entity.TaskCheckList;
import com.ks0100.wp.entity.TaskDiscuss;
import com.ks0100.wp.entity.TaskLine;
import com.ks0100.wp.service.AttachmentService;
import com.ks0100.wp.service.ProjectService;
import com.ks0100.wp.service.TaskService;

@Controller
@RequestMapping("/task")
public class TaskController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private TaskService taskService;
	@Autowired
	private AttachmentService attachmentService;
	@Autowired
	public ProjectService projectService;
	
	
	/**
	 * 加载任务板页面
	 * 
	 * @return
	 */
	@RequestMapping("/loadTaskBoard")
	public String loadTaskBoard(int projectId, Model model) {
		Project project = projectService.findProjectById(projectId);
		model.addAttribute("project", project);
		return "taskBoard/taskBoard";
	}
	
	/**
	 * 加载聊天界面
	 * @return
	 */
	@RequestMapping("/loadChat")
	public String loadChat() {
		return "chat/chat";
	}
	/**
	 * 聊天文件上传
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/chatUploadFile", method = RequestMethod.POST)
	public Map<String, Object> chatUploadFile(HttpServletRequest request) {

		MultipartHttpServletRequest mulRequest = (MultipartHttpServletRequest)request;
		MultipartFile file = mulRequest.getFile("chatFile");
		if (file == null || file.getSize() <= 0) {
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("attachment_uploadfail"));
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = attachmentService.saveFileToLocal(file, BusinessConstant.FileType.FILESAVETYPE_CHAT);
		} catch (Exception e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("attachment_uploadfail"));
		}
		return ResultDataJsonUtils.successResponseResult(map);
	}
	
	/**
	 * 加载任务详情页面
	 * @return
	 */
	@RequestMapping("/loadTaskInfo")
	public String loadTaskInfo() {
		return "taskBoard/task";
	}
	/**
	 * 加载只读的任务详情页面
	 * @return
	 */
	@RequestMapping("/loadReadOnlyTaskInfo")
	public String loadReadOnlyTaskInfo() {
		return "taskBoard/readOnlyTask";
	}
	/**
	 * 获取项目的所有任务板
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/listTaskBoard", method = RequestMethod.POST)
	public Map<String, Object> listTaskBoard(int projectId) {
		List<TaskBoard> boards = taskService.listTaskBoards(projectId);
		for (TaskBoard taskBoard : boards) {
			if(hasPermit(PermitConstant.PRJ_ADMIN_ACCESS,projectId)){
				taskBoard.getPermits().add("PRJ:ADMIN_ACCESS");
			}
			if(hasPermit(PermitConstant.TASK_BOARD_SET, taskBoard.getTaskBoardId()) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS, projectId)){
				taskBoard.getPermits().add(PermitConstant.TASK_BOARD_SET);
			}
			if(hasPermit(PermitConstant.TASK_BOARD_ADMIN_ACCESS, taskBoard.getTaskBoardId()) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS, projectId)){
				taskBoard.getPermits().add("TASK_LINE:EDIT");
				taskBoard.getPermits().add("TASK_LINE:MOVE");
				taskBoard.getPermits().add("TASK:MOVE_TO_SAME");
			}
		}
		return ResultDataJsonUtils.successResponseResult(boards);
	}
	
	/**
	 * 修改任务板管理员
	 */
	@ResponseBody
	@RequestMapping(value = "/updateBoardAdmin", method = RequestMethod.POST)
	public Map<String, Object> updateBoardAdmin(int boardId, int boardAdmin) {
		taskService.updateBoardAdmin(boardId, boardAdmin);
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		user.getPermissions().clear();
		return ResultDataJsonUtils.successResponseResult();
	}
	
	/**
	 * 获取看板的所有任务列(包含任务)信息
	 * @param boardId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/listTaskLines", method = RequestMethod.POST)
	public Map<String, Object> listTaskLines(int boardId, int roleType){
		List<TaskLine> lines = taskService.listTaskLines(boardId, roleType);
		return ResultDataJsonUtils.successResponseResult(lines);
	}

	/**
	 * 修改任务列
	 * 
	 * @param line
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateLineName", method = RequestMethod.POST)
	public Map<String, Object> updateLineName(TaskLine line) {
		taskService.updateTaskLine(line);
		return ResultDataJsonUtils.successResponseResult();
	}

	/**
	 * 添加任务列
	 * 
	 * @param line
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addLine", method = RequestMethod.POST)
	public Map<String, Object> addLine(int targetLineId, String name) {
		int taskLineId = taskService.addTaskLine(targetLineId, name);
		return ResultDataJsonUtils.successResponseResult(taskLineId);
	}

	/**
	 * 删除任务列
	 * 
	 * @param taskLineId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/delLine", method = RequestMethod.POST)
	public Map<String, Object> delLine(int taskLineId) {
		String resultStr = taskService.delTaskLine(taskLineId);
		if(resultStr != null){
			return ResultDataJsonUtils.errorResponseResult(resultStr);
		}
		return ResultDataJsonUtils.successResponseResult(resultStr);
	}
	
	/**
	 * 添加任务
	 * @param taskLineId
	 * @param taskContent
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addTask", method = RequestMethod.POST)
	public Map<String, Object> addTask(int projectId, int taskLineId, String content, @RequestParam("partner[]") List<Integer> partner, int executor, boolean visible, int parentTaskId){
		Task task=new Task();
		task.setContent(content);
		task.setTaskLine(taskLineId);
		task.setProjectId(projectId);
		int taskId = taskService.addTask(task, partner, executor, visible, parentTaskId);
		return ResultDataJsonUtils.successResponseResult(taskId);
	}
	
	/**
	 * 修改任务完成状态
	 * 
	 * @param task
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateTaskStatus", method = RequestMethod.POST)
	public Map<String, Object> updateTaskStatus(Task task) {
		taskService.updateTaskStatus(task);
		return ResultDataJsonUtils.successResponseResult();
	}

	/**
	 * 获取任务详细数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findTaskData", method = RequestMethod.POST)
	public Map<String, Object> findTaskData(int taskId){
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		user.getPermissions().clear();
		Task task = taskService.findTaskData(taskId);
		if(hasPermit(PermitConstant.TASK_BOARD_ADMIN_ACCESS, task.getBoardId()) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS, task.getProjectId()) 
				|| hasPermit(PermitConstant.TASK_DELETE, taskId)){
			task.getPermits().add("TASK:DELETE");
		}
		if(hasPermit(PermitConstant.TASK_BOARD_ADMIN_ACCESS, task.getBoardId()) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS, task.getProjectId()) 
				|| hasPermit(PermitConstant.TASK_SET_DATE, taskId)){
			task.getPermits().add("TASK:SET_DATE");
		}
		if(hasPermit(PermitConstant.TASK_BOARD_ADMIN_ACCESS, task.getBoardId()) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS, task.getProjectId()) 
				|| hasPermit(PermitConstant.TASK_PRIORITY, taskId)){
			task.getPermits().add("TASK:PRIORITY");
		}
		if(hasPermit(PermitConstant.TASK_BOARD_ADMIN_ACCESS, task.getBoardId()) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS, task.getProjectId()) 
				|| hasPermit(PermitConstant.TASK_SET, taskId)){
			task.getPermits().add("TASK:SET");
		}
		if(hasPermit(PermitConstant.TASK_BOARD_ADMIN_ACCESS, task.getBoardId()) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS, task.getProjectId()) 
				|| hasPermit(PermitConstant.TASK_UPDATE_EXECUTOR, taskId)){
			task.getPermits().add("TASK:UPDATE_EXECUTOR");
		}
		if(hasPermit(PermitConstant.TASK_BOARD_ADMIN_ACCESS, task.getBoardId()) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS, task.getProjectId()) 
				|| hasPermit(PermitConstant.TASK_UPDATE_MEMBER, taskId)){
			task.getPermits().add("TASK:UPDATE_MEMBER");
		}
		if(hasPermit(PermitConstant.TASK_BOARD_ADMIN_ACCESS, task.getBoardId()) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS, task.getProjectId()) 
				|| hasPermit(PermitConstant.TASK_MOVE_TO_LINE, taskId)){
			task.getPermits().add("TASK:MOVE_TO_LINE");
		}
		if(hasPermit(PermitConstant.TASK_BOARD_ADMIN_ACCESS, task.getBoardId()) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS, task.getProjectId()) 
				|| hasPermit(PermitConstant.TASK_MOVE_TO_BOARD, taskId)){
			task.getPermits().add("TASK:MOVE_TO_BOARD");
		}
		if(hasPermit(PermitConstant.TASK_BOARD_ADMIN_ACCESS, task.getBoardId()) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS, task.getProjectId()) 
				|| hasPermit(PermitConstant.TASK_PASS, taskId)){
			task.getPermits().add("TASK:PASS");
		}
		if(hasPermit(PermitConstant.TASK_BOARD_ADMIN_ACCESS, task.getBoardId()) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS, task.getProjectId()) 
				|| hasPermit(PermitConstant.TASK_NOT_PASS, taskId)){
			task.getPermits().add("TASK:NOT_PASS");
		}
		if(hasPermit(PermitConstant.TASK_BOARD_ADMIN_ACCESS, task.getBoardId()) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS, task.getProjectId()) 
				|| hasPermit(PermitConstant.TASK_UPDATE_ADMIN, taskId)){
			task.getPermits().add("TASK:UPDATE_ADMIN");
		}
		if(hasPermit(PermitConstant.TASK_COMMIT, taskId)){
			task.getPermits().add("TASK:COMMIT");
		}
		if(hasPermit(PermitConstant.TASK_BOARD_ADMIN_ACCESS, task.getBoardId()) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS, task.getProjectId()) 
				|| hasPermit(PermitConstant.TASK_VISIBLE, taskId) ){
			task.getPermits().add("TASK:VISIBLE");
		}
		return ResultDataJsonUtils.successResponseResult(task);
	}
	
	/**
	 * 获取任务相关人员
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findTaskUser", method = RequestMethod.POST)
	public Map<String, Object> findTaskUser(int taskId){
		List<Integer> userIds = taskService.findTaskUser(taskId);
		return ResultDataJsonUtils.successResponseResult(userIds);
	}
	
	/**
	 * 获取任务其他数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findTaskOtherData", method = RequestMethod.POST)
	public Map<String, Object> findTaskOtherData(int projectId, int taskId){
		Map<String, Object> otherData = taskService.findTaskOtherData(projectId, taskId);
		return ResultDataJsonUtils.successResponseResult(otherData);
	}
	
	/**
	 * 添加任务板
	 * @param projectId
	 * @param board
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addTaskBoard", method = RequestMethod.POST)
	public Map<String, Object> addTaskBoard(int projectId, TaskBoard board){
		int boardId = taskService.addTaskBoard(projectId, board);
		return ResultDataJsonUtils.successResponseResult(boardId);
	}
	
	/**
	 * 修改任务板
	 * @param projectId
	 * @param board
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateTaskBoard", method = RequestMethod.POST)
	public Map<String, Object> updateTaskBoard(TaskBoard board, int admin){
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		if(hasPermit(PermitConstant.TASK_BOARD_SET, board.getTaskBoardId()) && user.getUserId()==admin){
			taskService.updateTaskBoard(board);
			return ResultDataJsonUtils.successResponseResult();
		}else{
			return ResultDataJsonUtils.errorResponseResult("您没有权限");
		}
		
	}
	
	/**
	 * 删除任务板
	 * 
	 * @param taskLineId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/delTaskBoard", method = RequestMethod.POST)
	public Map<String, Object> delTaskBoard(int taskBoardId) {
//		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		TaskBoard taskboard = taskService.findTaskBoardById(taskBoardId);
		if(hasPermit(PermitConstant.TASK_BOARD_SET, taskBoardId) || hasPermit(PermitConstant.PRJ_ADMIN_ACCESS,taskboard.getProject().getProjectId())){
			String resultStr = taskService.delTaskBoard(taskBoardId);
			if(resultStr != null){
				return ResultDataJsonUtils.errorResponseResult(resultStr);
			}
			return ResultDataJsonUtils.successResponseResult(resultStr);
		}else{
			return ResultDataJsonUtils.errorResponseResult("您没有权限");
		}
	}
	
	/**
	 * 修改任务
	 * @param task
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateTask", method = RequestMethod.POST)
	public Map<String, Object> updateTask(int taskId, String value, String columnName) {
		taskService.updateTask(taskId, "".equals(value) ? null : value, columnName);
		return ResultDataJsonUtils.successResponseResult();
	}
	
	/**
	 * 修改任务执行者
	 * @param task
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateTaskExecutor", method = RequestMethod.POST)
	public Map<String, Object> updateTaskExecutor(int taskId, int executor) {
		taskService.updateTaskExecutor(taskId, executor);
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		user.getPermissions().clear();
		return ResultDataJsonUtils.successResponseResult();
	}
	/**
	 * 更新任务的管理员
	 * @param taskId
	 * @param executor
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateTaskAdmin", method = RequestMethod.POST)
	public Map<String, Object> updateTaskAdmin(int taskId, int admin) {
		taskService.updateTaskAdmin(taskId, admin);
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		user.getPermissions().clear();
		return ResultDataJsonUtils.successResponseResult();
	}
	/**
	 * 修改任务参与者
	 * @param task
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateTaskPartner", method = RequestMethod.POST)
	public Map<String, Object> updateTaskPartner(int taskId, @RequestParam("partner[]") List<Integer> partner, boolean isSelected) {
		taskService.updateTaskPartner(taskId, partner, isSelected);
		return ResultDataJsonUtils.successResponseResult();
	}

	/**
	 * 删除任务
	 * @param task
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/delTask", method = RequestMethod.POST)
	public Map<String, Object> delTask(int taskId) {
		String resultStr = taskService.delTask(taskId);
		if(resultStr != null){
			return ResultDataJsonUtils.errorResponseResult(resultStr);
		}
		return ResultDataJsonUtils.successResponseResult(resultStr);
	}
	
	/**
	 * 移动任务列
	 * @param oneselfId
	 * @param targetId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/moveTaskLine", method = RequestMethod.POST)
	public Map<String, Object> moveTaskLine(int oneselfId, int targetId) {
		taskService.moveTaskLine(oneselfId, targetId);
		return ResultDataJsonUtils.successResponseResult();
	}
	
	/**
	 * 移动任务
	 * @param oneselfId
	 * @param targetId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/moveTask", method = RequestMethod.POST)
	public Map<String, Object> moveTask(int oneselfId, int targetId, int oneselfLineId, int targetLineId) {
		taskService.moveTask(oneselfId, targetId, oneselfLineId, targetLineId);
		return ResultDataJsonUtils.successResponseResult();
	}
	
	/**
	 * 获取任务附件
	 * @param taskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findAttachments", method = RequestMethod.POST)
	public Map<String, Object> findAttachments(int taskId) {
		List<Attachment> attachments = taskService.findAttachmentByTask(taskId);
		return ResultDataJsonUtils.successResponseResult(attachments);
	}
	
	/**
	 * 添加任务讨论
	 * @param taskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addTaskDiscuss", method = RequestMethod.POST)
	public Map<String, Object> addTaskDiscuss(int taskId, String content) {
		taskService.addTaskDiscuss(taskId, content);
		return ResultDataJsonUtils.successResponseResult();
	}
	
	/**
	 * 获取任务回复
	 * @param taskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findTaskDiscuss", method = RequestMethod.POST)
	public Map<String, Object> findTaskDiscuss(int taskId) {
		List<TaskDiscuss> discuss = taskService.findTaskDiscuss(taskId);
		return ResultDataJsonUtils.successResponseResult(discuss);
	}
	
	/**
	 * 获取任务动态
	 * @param taskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/listTaskActionRecords", method = RequestMethod.POST)
	public Map<String, Object> listTaskActionRecords(int taskId) {
		List<ProjectActionRecord> records = taskService.listTaskActionRecords(taskId);
		return ResultDataJsonUtils.successResponseResult(records);
	}
	
	/**
	 * 获取父级任务及子任务
	 * @param taskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findParentAndChildTask", method = RequestMethod.POST)
	public Map<String, Object> findParentAndChildTask(int taskId) {
		Task task = taskService.listParentAndChildTask(taskId);
		return ResultDataJsonUtils.successResponseResult(task);
	}
	
	/**
	 * 添加检查项
	 * @param taskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addCheckList", method = RequestMethod.POST)
	public Map<String, Object> addCheckList(int taskId, String content) {
		int checkListId = taskService.addCheckList(taskId, content);
		return ResultDataJsonUtils.successResponseResult(checkListId);
	}
	
	/**
	 * 获取检查项
	 * @param taskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findTaskCheckList", method = RequestMethod.POST)
	public Map<String, Object> findTaskCheckList(int taskId) {
		List<TaskCheckList> checkList = taskService.findTaskCheckList(taskId);
		return ResultDataJsonUtils.successResponseResult(checkList);
	}
	
	/**
	 * 修改检查项
	 * @param taskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updateTaskCheckList", method = RequestMethod.POST)
	public Map<String, Object> updateTaskCheckList(int taskCheckListId, String value, String columnName) {
		taskService.updateTaskCheckList(taskCheckListId, value, columnName);
		return ResultDataJsonUtils.successResponseResult();
	}
	
	/**
	 * 删除检查项
	 * @param taskId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/delTaskCheckList", method = RequestMethod.POST)
	public Map<String, Object> delTaskCheckList(int taskCheckListId) {
		taskService.delTaskCheckList(taskCheckListId);
		return ResultDataJsonUtils.successResponseResult();
	}
	
	/**
	 * 任务上传附件
	 * @param task
	 * @return
	 * @throws IOException 
	 */
	@ResponseBody
	@RequestMapping(value = "/uploadTaskFile", method = RequestMethod.POST)
	public Map<String, Object> addTaskAdjunct(HttpServletRequest request, int taskId,int projectId) {
		MultipartHttpServletRequest mulRequest = (MultipartHttpServletRequest)request;
		MultipartFile file = mulRequest.getFile("taskFiles");
		if (file == null || file.getSize() <= 0) {
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("attachment_uploadfail"));
		}
		Attachment attachment = null;
		try {
			ArgumentsForFile aff=new ArgumentsForFile();
			aff.setFileName(file.getOriginalFilename());
			aff.setFilesize(file.getSize());
			aff.setProjectId(projectId);
			aff.setTaskId(taskId);
			attachment = attachmentService.saveAttachment(aff,file, BusinessConstant.FileType.FILESAVETYPE_TASK, BusinessConstant.TableNameConstant.TABLE_TASK, taskId, -1);
		} catch (Exception e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("attachment_uploadfail"));
		}
		return ResultDataJsonUtils.successResponseResult(attachment);
	}
	
	/**
	 * 文件下载
	 *
	 * @param request
	 * @param response
	 * @param filePath
	 * @throws Exception 
	 */
	@ResponseBody
	@RequestMapping(value = "/downloadTaskFile", method = RequestMethod.POST)
	public void downloadTaskFile(HttpServletResponse response, String fileUrl) throws Exception {
		
		Attachment attachment = attachmentService.findAttachmentByFileUrl(fileUrl);
		if (attachment == null) {
			return;
		}
		
		response.reset();
		response.setContentType("application/x-download");
		String userFileName = attachment.getFileName();
		userFileName = URLEncoder.encode(userFileName, "utf-8");
		response.addHeader("Content-Disposition", "attachment; filename=" + userFileName);
		String filePath = PathUtil.ABSOLUTE_WEB_PATH + CommonConstant.UPLOADFILEURL + "/" + attachment.getPath();
		File file = new File(filePath);
		if (file == null || !file.exists()) {
			return;
		}
		FileInputStream nowIn = null;
		OutputStream out = null;
		out = response.getOutputStream();
		
		nowIn = new FileInputStream(file);
		out = response.getOutputStream();
		byte[] b = new byte[1024];
        int i = 0;
		while((i = nowIn.read(b)) > 0){
			out.write(b, 0, i);
		}
		out.flush();
		nowIn.close();
		out.close();
	}
	/**
	 * 删除
	 *
	 * @param filePath
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deleteTaskAdjunct", method = RequestMethod.POST)
	public Map<String, Object> deleteTaskAdjunct(String fileUrl,int projectId) {
		if (StringUtils.trimToNull(fileUrl) == null) {
			return ResultDataJsonUtils.errorResponseResult(null);
		}
		
		Attachment att=attachmentService.findAttachmentByFileUrl(fileUrl);
		if(att!=null){
			ArgumentsForFile aff=new ArgumentsForFile();
			aff.setFileName(att.getFileName());
			aff.setTaskId(att.getRefId());
			aff.setOperateType(AuditServiceAspectJ.OPERATE_TYPE_00618);
			aff.setProjectId(projectId);
			attachmentService.deleteAttachmentByFileUrl(aff,PathUtil.ABSOLUTE_WEB_PATH, fileUrl);
		}
		
		return ResultDataJsonUtils.successResponseResult();
	}
	@RequestMapping("/loadNoPassReason")
	public String loadNoPassReason() {
		return "taskBoard/addNoPassReason";
	}
}
