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

@Entity
@Table(name="tbl_team")
public class Team extends CommonEntity{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3380877236028873034L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int teamId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "logo")
	private byte[] logo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tbl_organization_id")
	private Organization organization;
	
	@Transient
	private List<User> users = new ArrayList<User>();
	@Transient
	private int projectNum;
	
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
	
	@Transient
	private int realityDay = 0;
			

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

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	

	public int getProjectNum() {
		return projectNum;
	}

	public void setProjectNum(int projectNum) {
		this.projectNum = projectNum;
	}

	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

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
	
	
	
}
