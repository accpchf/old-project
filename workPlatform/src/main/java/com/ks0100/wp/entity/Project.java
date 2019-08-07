package com.ks0100.wp.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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

@Entity
@Table(name="tbl_project")
public class Project extends CommonEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1002796306022778798L;

	//项目Id
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int projectId;
	
	@Column(name = "name", length = 40, nullable = false)
	private String name;
	
	@Column(name = "description", length = 600)
	private String description;
	
	@Column(name = "begin_time", columnDefinition = "timestamp")
	private Date beginTime;
	
	@Column(name = "end_time", columnDefinition = "timestamp")
	private Date endTime;
	
	@Column(name = "logo")
	private byte[] logo;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "log")
	private String log;
	
	@Transient
	private String logoStr;
	
	@Column(name = "invitation_code", length = 600)
	private String invitationCode;
	
	@Column(name = "invitation_enabled")
	private boolean invitationEnabled;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tbl_organization_id")
	private Organization organization;
	
	@Transient
	private boolean commonUse;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inviter")
	private User inviter;
	
	@Column(name = "uuid")
	private String uuid=UUID.randomUUID().toString();
	
	@Transient
	private List<Task> tasks = new ArrayList<Task>();
	// 项目的任务总数
	@Transient
	private int taskCount = 0;
	// 项目已完成的任务数量
	@Transient
	private int noTaskCount = 0;
	// 项目逾期的任务数量
	@Transient
	private int tardyTaskCount = 0;
	
	@Transient
	private int planDay = 0;
	
	public int getPlanDay() {
		return planDay;
	}

	public void setPlanDay(int planDay) {
		this.planDay = planDay;
	}

	public int getRealityDay() {
		return realityDay;
	}

	public void setRealityDay(int realityDay) {
		this.realityDay = realityDay;
	}

	@Transient
	private int realityDay = 0;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLog() {
		return log;
	}
	

	public void setLog(String log) {
		this.log = log;
	}

	public User getInviter() {
		return inviter;
	}

	public void setInviter(User inviter) {
		this.inviter = inviter;
	}
	
	public void setInviter(int inviterId) {
		User user=new User();
		user.setUserId(inviterId);
		this.inviter = user;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Project(){
		
	}
	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}


	public boolean isInvitationEnabled() {
		return invitationEnabled;
	}

	public void setInvitationEnabled(boolean invitationEnabled) {
		this.invitationEnabled = invitationEnabled;
	}

	public String getInvitationCode() {
		return invitationCode;
	}

	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
	}

	public boolean isCommonUse() {
		return commonUse;
	}

	public void setCommonUse(boolean commonUse) {
		this.commonUse = commonUse;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLogoStr() {
		return logoStr;
	}

	public void setLogoStr(String logoStr) {
		this.logoStr = logoStr;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	public int getNoTaskCount() {
		return noTaskCount;
	}

	public void setNoTaskCount(int noTaskCount) {
		this.noTaskCount = noTaskCount;
	}

	public int getTardyTaskCount() {
		return tardyTaskCount;
	}

	public void setTardyTaskCount(int tardyTaskCount) {
		this.tardyTaskCount = tardyTaskCount;
	}
	
	
}
