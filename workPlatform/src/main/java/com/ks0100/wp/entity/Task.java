package com.ks0100.wp.entity;

import java.util.ArrayList;
import java.util.Date;
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

import com.ks0100.wp.constant.BusinessConstant;

/**
 * 任务实体类
 * 
 * @author xie linming
 * @date 2014年12月1日
 */
@Entity
@Table(name = "tbl_task")
public class Task extends CommonEntity {

	private static final long serialVersionUID = -73307738206206949L;

	// 任务id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int taskId;
	// 任务名称
	@Column(length = 100)
	private String name;

	// 任务内容
	@Column(length = 600)
	private String content;
	// 排序
	@Column(columnDefinition = "tinyint")
	private int sort;
	// 父级任务
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_task_id")
	private Task parentTask;
	// 所属任务列
	@ManyToOne
	@JoinColumn(name = "tbl_task_line_id")
	private TaskLine taskLine;
	// 所属项目
	@Column(name = "tbl_project_id")
	private int projectId;
	// 优先级
	@Column(length = 5)
	private String priority = BusinessConstant.TaskConstant.TASK_PRIORITY;
	// 等级
	@Column(length = 5)
	private String level = BusinessConstant.TaskConstant.TASK_LEVEL;
	@Column(name = "due_time", columnDefinition = "timestamp")
	private Date dueTime;
	@Column(name = "complete_time", columnDefinition = "timestamp")
	private Date completeTime;
	// 任务状态
	@Column(length = 5)
	private String status = BusinessConstant.TaskConstant.TASK_DONING;
	// 任务可见性
	@Column(name = "visible_status")
	private String visibleStatus = BusinessConstant.TaskConstant.TASK_VISIBLE_STATUS_ALL;
	@Column
	private String remark;

	// 任务执行者id
	@Transient
	private int executor;
	// 任务管理员
	@Transient
	private int admin;

	

	// 参与者ids
	@Transient
	private List<Integer> partnerIds = new ArrayList<Integer>();
	// 子任务
	@Transient
	private List<Task> children = new ArrayList<Task>();
	@Transient
	private int lineId;
	@Transient
	private int boardId;
	@Transient
	private String planTime;
	@Transient
	private String realityTime;
	
	
	
	public String getPlanTime() {
		return planTime;
	}
	@Transient
	private List<String> permits = new ArrayList<String>();
	
	@Column(name = "no_pass_reason")
	private String noPassReason;
	
	@Column(name = "no_pass_times")
	private int noPassTimes;

	public void setPlanTime(String planTime) {
		this.planTime = planTime;
	}

	public String getRealityTime() {
		return realityTime;
	}

	public void setRealityTime(String realityTime) {
		this.realityTime = realityTime;
	}

	public List<String> getPermits() {
		return permits;
	}

	public void setPermits(List<String> permits) {
		this.permits = permits;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getExecutor() {
		return executor;
	}

	public void setExecutor(int executor) {
		this.executor = executor;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public Task getParentTask() {
		return parentTask;
	}

	public void setParentTask(Task parentTask) {
		this.parentTask = parentTask;
	}

	public void setParentTask(int parentTaskId) {
		Task parent = new Task();
		parent.setTaskId(parentTaskId);
		this.parentTask = parent;
	}

	public TaskLine getTaskLine() {
		return taskLine;
	}

	public void setTaskLine(TaskLine taskLine) {
		this.taskLine = taskLine;
	}

	public void setTaskLine(int taskLineId) {
		TaskLine line = new TaskLine();
		line.setTaskLineId(taskLineId);
		this.taskLine = line;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Date getDueTime() {
		return dueTime;
	}

	public void setDueTime(Date dueTime) {
		this.dueTime = dueTime;
	}

	public Date getCompleteTime() {
		return completeTime;
	}

	public void setCompleteTime(Date completeTime) {
		this.completeTime = completeTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVisibleStatus() {
		return visibleStatus;
	}

	public void setVisibleStatus(String visibleStatus) {
		this.visibleStatus = visibleStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<Integer> getPartnerIds() {
		return partnerIds;
	}

	public void setPartnerIds(List<Integer> partnerIds) {
		this.partnerIds = partnerIds;
	}

	public List<Task> getChildren() {
		return children;
	}

	public void setChildren(List<Task> children) {
		this.children = children;
	}

	public int getLineId() {
		return lineId;
	}

	public void setLineId(int lineId) {
		this.lineId = lineId;
	}

	public int getBoardId() {
		return boardId;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}
	
	public int getAdmin() {
		return admin;
	}

	public void setAdmin(int admin) {
		this.admin = admin;
	}

	public String getNoPassReason() {
		return noPassReason;
	}

	public void setNoPassReason(String noPassReason) {
		this.noPassReason = noPassReason;
	}

	public int getNoPassTimes() {
		return noPassTimes;
	}

	public void setNoPassTimes(int noPassTimes) {
		this.noPassTimes = noPassTimes;
	}
	
}
