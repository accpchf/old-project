package com.ks0100.wp.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.shiro.SecurityUtils;

import com.ks0100.wp.ShiroUser;

@Entity
@Table(name="tbl_project_action_record")
public class ProjectActionRecord extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7133406088452241058L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "user_id")
	private int userId;
	
	@Column(name = "user_name")
	private String userName;
	
	private String record;
	
	@Column(name = "old_value")
	private String oldValue;
	
	@Column(name = "tbl_project_id")
	private int projectId;
	
	@Column(name = "show_by_moudle")
	private int showByMoudle;
	
	@Column(name = "useage")
	private String useage;
	
	// 创建时间
	@Column(name = "created_time", columnDefinition = "timestamp")
	private Date createdTime;
	
	// 创建者
	@Column(name = "created_by")
	private Integer createdBy;
	
	@Column(name = "task_id")
	private  Integer taskId;
	
	@Transient
	private String createdTimeString;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public int getShowByMoudle() {
		return showByMoudle;
	}

	public void setShowByMoudle(int showByMoudle) {
		this.showByMoudle = showByMoudle;
	}

	public String getUseage() {
		return useage;
	}

	public void setUseage(String useage) {
		this.useage = useage;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	/**
	 * 设置创建者
	 * @param id 值等于-1时为当前系统登录用户id
	 */
	public void setCreatedBy(Integer createdBy) {
		if(createdBy == -1){
			ShiroUser current = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
			createdBy = current.getUserId();
		}
		this.createdBy = createdBy;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getCreatedTimeString() {
		return createdTimeString;
	}

	public void setCreatedTimeString(String createdTimeString) {
		this.createdTimeString = createdTimeString;
	}
}
