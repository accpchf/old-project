package com.ks0100.wp.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务详情Dto
 * @author ctx_zhang tan
 *
 */
public class TaskDetailDto {
	
	//任务id
	private int taskId;
	//任务内容
	private String content;
	//创建时间
	private String createdTime;
	//结束时间
	private String endTime;
	
	private List<TaskCheckListDto> checkLists = new ArrayList<TaskCheckListDto>();
	
	private int parentTaskId;
	
	private String parentTaskContent;
	
	private List<TaskDto> childTaskList = new ArrayList<TaskDto>();
	
	private int executorId;
	
	private String executorName;
	
	private int adminId;
	
	private String adminName;
	
	private String priority;
	
	private String status;
	
	private List<Integer> partner = new ArrayList<Integer>();
	
	private String remark;
	
	

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public List<TaskCheckListDto> getCheckLists() {
		return checkLists;
	}

	public void setCheckLists(List<TaskCheckListDto> checkLists) {
		this.checkLists = checkLists;
	}
	
	public int getParentTaskId() {
		return parentTaskId;
	}

	public void setParentTaskId(int parentTaskId) {
		this.parentTaskId = parentTaskId;
	}

	public String getParentTaskContent() {
		return parentTaskContent;
	}

	public void setParentTaskContent(String parentTaskContent) {
		this.parentTaskContent = parentTaskContent;
	}

	public List<TaskDto> getChildTaskList() {
		return childTaskList;
	}

	public void setChildTaskList(List<TaskDto> childTaskList) {
		this.childTaskList = childTaskList;
	}

	public int getExecutorId() {
		return executorId;
	}

	public void setExecutorId(int executorId) {
		this.executorId = executorId;
	}

	public String getExecutorName() {
		return executorName;
	}

	public void setExecutorName(String executorName) {
		this.executorName = executorName;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<Integer> getPartner() {
		return partner;
	}

	public void setPartner(List<Integer> partner) {
		this.partner = partner;
	}
	
	
	
	
	
	

}
