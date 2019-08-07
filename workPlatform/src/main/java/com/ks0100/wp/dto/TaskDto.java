package com.ks0100.wp.dto;


/**
 * 封装任务列表dto
 * @author ctx_zhang tan
 *
 */
public class TaskDto {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = -73307738206206949L;
	//任务id
	private int taskId;
	//创建时间
	private String createdTime;
	//任务内容
	private String content;
	//任务步骤数量
	private int checkListNum;
	//已完成任务步骤数量
	private int checkListDoneNum;
	//记录总数
	private int recordNum;
	//任务状态
	private String status;
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getCheckListNum() {
		return checkListNum;
	}

	public void setCheckListNum(int checkListNum) {
		this.checkListNum = checkListNum;
	}

	public int getCheckListDoneNum() {
		return checkListDoneNum;
	}

	public void setCheckListDoneNum(int checkListDoneNum) {
		this.checkListDoneNum = checkListDoneNum;
	}

	public int getRecordNum() {
		return recordNum;
	}

	public void setRecordNum(int recordNum) {
		this.recordNum = recordNum;
	}
	
	
}
