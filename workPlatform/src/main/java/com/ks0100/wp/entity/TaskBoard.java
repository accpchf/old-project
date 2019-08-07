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
 * 任务板实体类
 * 
 * @author xie linming
 * @date 2014年12月1日
 */
@Entity
@Table(name = "tbl_task_board")
public class TaskBoard extends CommonEntity {

	private static final long serialVersionUID = -2142654214364190403L;

	// 任务板id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int taskBoardId;
	// 任务板名称
	@Column(length = 40)
	private String name;
	// 备注
	@Column(length = 600)
	private String remark;
	// 所属项目
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tbl_project_id")
	private Project project;
	@Transient
	private List<String> permits = new ArrayList<String>();
	
	@Transient
	private List<TaskLine> taskLines =  new ArrayList<TaskLine>();
	@Transient
	private int boardAdmin;

	public int getBoardAdmin() {
		return boardAdmin;
	}

	public void setBoardAdmin(int boardAdmin) {
		this.boardAdmin = boardAdmin;
	}

	public List<String> getPermits() {
		return permits;
	}

	public void setPermits(List<String> permits) {
		this.permits = permits;
	}

	public int getTaskBoardId() {
		return taskBoardId;
	}

	public void setTaskBoardId(int taskBoardId) {
		this.taskBoardId = taskBoardId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public void setProject(int projectId) {
		Project pro = new Project();
		pro.setProjectId(projectId);
		this.project = pro;
	}

	public List<TaskLine> getTaskLines() {
		return taskLines;
	}

	public void setTaskLines(List<TaskLine> taskLines) {
		this.taskLines = taskLines;
	}

}
