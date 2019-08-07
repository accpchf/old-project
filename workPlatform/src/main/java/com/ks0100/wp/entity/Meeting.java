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

import org.apache.shiro.SecurityUtils;

import com.ks0100.wp.ShiroUser;

/**
 * 项目会议实体类
 *
 * 创建日期：2014-12-9
 * @author chengls
 */
@Entity
@Table(name = "tbl_project_meeting")
public class Meeting extends BaseEntity{

	private static final long serialVersionUID = 3731427574348898874L;
	// 会议id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int meetingId;
	
	//会议标题
	@Column(length = 40)
	private String title;
	
	//会议时间
	@Column(name = "begin_time")
	private Date beginTime;
	
	//会议地点
	@Column(length = 40)
	private String place;
	
	//会议描述
	@Column(length = 200)
	private String detail;
	
	//会议纪要
	@Column(length = 4000)
	private String record;
	
	// 创建者
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by")
	private User createdBy;

	// 修改者
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by")
	private User updatedBy;

	// 创建时间
	@Column(name = "created_time", columnDefinition = "timestamp")
	private Date createdTime;

	// 修改时间
	@Column(name = "updated_time", columnDefinition = "timestamp")
	private Date updatedTime;
	
	//会议关联项目
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tbl_project_id")
	private Project project;
	
	//会议的参与者
	@Transient
	private List<User> users = new ArrayList<User>();
	
	//会议的附件
	@Transient
	private List<Attachment> attachments = new ArrayList<Attachment>();
	
	

	public int getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(int meetingId) {
		this.meetingId = meetingId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}
	/**
	 * 设置创建者
	 * @param id 值等于-1时为当前系统登录用户id
	 */
	public void setCreatedBy(int id) {
		if(id == -1){
			ShiroUser current = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
			id = current.getUserId();
		}
		User user = new User();
		user.setUserId(id);
		this.createdBy  = user;
	}
	/**
	 * 设置修改者
	 * @param id 值等于-1时为当前系统登录用户id
	 */
	public void setUpdatedBy(int id) {
		if(id == -1){
			ShiroUser current = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
			id = current.getUserId();
		}
		User user = new User();
		user.setUserId(id);
		this.updatedBy = user;
	}
}
