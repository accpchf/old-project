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

@Entity
@Table(name = "tbl_task_discuss")
public class TaskDiscuss extends CommonEntity {

	private static final long serialVersionUID = -4973560526628646464L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int taskDiscussId;

	@Column(length = 600)
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tbl_task_id")
	private Task task;

	public int getTaskDiscussId() {
		return taskDiscussId;
	}

	public void setTaskDiscussId(int taskDiscussId) {
		this.taskDiscussId = taskDiscussId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

}
