package com.ks0100.wp.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 任务列
 * 
 * @author xie linming
 * @date 2014年12月1日
 */
@Entity
@Table(name = "tbl_task_line")
public class TaskLine extends CommonEntity {

	private static final long serialVersionUID = -7583148888464426634L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int taskLineId;
	// 任务列名称
	@Column(length = 40)
	private String name;
	// 排序
	@Column(columnDefinition = "tinyint")
	private int sort;
	// 所属任务板
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tbl_task_board_id")
	private TaskBoard taskBoard;
	
	// 未完成的任务
	//@OneToMany(mappedBy = "taskLine")
    @Transient
	private List<Task> tasks = new ArrayList<Task>();
    
    // 已完成的任务
    @Transient
    private List<Task> doneTasks = new ArrayList<Task>();

	public int getTaskLineId() {
		return taskLineId;
	}

	public void setTaskLineId(int taskLineId) {
		this.taskLineId = taskLineId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public TaskBoard getTaskBoard() {
		return taskBoard;
	}

	public void setTaskBoard(TaskBoard taskBoard) {
		this.taskBoard = taskBoard;
	}

	public void setTaskBoard(int taskBoardId) {
		TaskBoard board = new TaskBoard();
		board.setTaskBoardId(taskBoardId);
		this.taskBoard = board;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public List<Task> getDoneTasks() {
		return doneTasks;
	}

	public void setDoneTasks(List<Task> doneTasks) {
		this.doneTasks = doneTasks;
	}
	
	

}
