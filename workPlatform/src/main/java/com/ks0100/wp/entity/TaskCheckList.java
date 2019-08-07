package com.ks0100.wp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ks0100.wp.constant.BusinessConstant;

@Entity
@Table(name = "tbl_task_check_list")
public class TaskCheckList extends CommonEntity {

	private static final long serialVersionUID = -4305694088792984544L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int taskCheckListId;
	// 检查项内容
	@Column(name = "list_content")
	private String listContent;
	// 所属任务
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tbl_task_id")
	private Task task;
	// 状态
	@Column(length = 5)
	private String status = BusinessConstant.TaskConstant.TASK_CHECK_LIST_STATUS_NO;

	public int getTaskCheckListId() {
		return taskCheckListId;
	}

	public void setTaskCheckListId(int taskCheckListId) {
		this.taskCheckListId = taskCheckListId;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public void setTask(int taskId) {
		Task t = new Task();
		t.setTaskId(taskId);
		this.task = t;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getListContent() {
		return listContent;
	}

	public void setListContent(String listContent) {
		this.listContent = listContent;
	}

}
