package com.ks0100.wp.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ks0100.wp.constant.StatusEnums.UserGender;

/**
 * 用户实体类
 * 
 * @author xie linming
 * @date 2014年11月28日
 */
@Entity
@Table(name = "tbl_user")
public class User extends CommonEntity {

	private static final long serialVersionUID = -5074667473620540692L;

	// 用户id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int userId;

	// 帐号
	@Column(length = 40, nullable = false)
	private String account;
	
	//姓名
	@Column(length = 40)
	private String name;

	// 密码
	@Column(length = 40)
	private String password;

	// 头像图标
	@Column(length = 4000)
	private String logo;

	//电话
	@Column(length = 40)
	private String mobile;

	//性别
	@Column(length = 5)
	private String gender=UserGender.UNKNOW.getCode(); 
	
	// 职位
	@Column(length = 100)
	private String position;
	
	@Transient
	private String userRole;
	
	@Transient
	private boolean belongTeam;
	// 用户所执行的任务总数
	@Transient
	private int taskCount = 0;
	// 用户所执行的已完成的任务数量
	@Transient
	private int noTaskCount = 0;
	// 用户所执行的逾期的任务数量
	@Transient
	private int tardyTaskCount = 0;
	
	@Column(name = "password_salt", length = 50)
	private String salt;
	
	@Column(name = "active_uuid")
	private String activeUuid;
	
	@Column(name = "active")
	private boolean active;
	
	@Column(name="password_uuid")
	private String passwordUuid;
	
	@Column(name = "active_uuid_time", columnDefinition = "timestamp")
	private Date activeUuidTime;
	
	@Column(name = "password_uuid_time", columnDefinition = "timestamp")
	private Date passwordUuidTime;
	
	@Transient
	private int planDay = 0;
	
	@Transient
	private int realityDay = 0;
	
	/**
	 * 判断用户列表是否显示该用户
	 */
	@Transient
	private boolean userEnabled = true;
	
	
	
	public boolean getUserEnabled() {
		return userEnabled;
	}

	public void setUserEnabled(boolean userEnabled) {
		this.userEnabled = userEnabled;
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

	public Date getActiveUuidTime() {
		return activeUuidTime;
	}

	public void setActiveUuidTime(Date activeUuidTime) {
		this.activeUuidTime = activeUuidTime;
	}

	public Date getPasswordUuidTime() {
		return passwordUuidTime;
	}

	public void setPasswordUuidTime(Date passwordUuidTime) {
		this.passwordUuidTime = passwordUuidTime;
	}

	public String getPasswordUuid() {
		return passwordUuid;
	}

	public void setPasswordUuid(String passwordUuid) {
		this.passwordUuid = passwordUuid;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean getBelongTeam() {
		return belongTeam;
	}

	public String getActiveUuid() {
		return activeUuid;
	}

	public void setActiveUuid(String activeUuid) {
		this.activeUuid = activeUuid;
	}

	public void setBelongTeam(boolean belongTeam) {
		this.belongTeam = belongTeam;
	}

	//性别的翻译值（男或女）
	@Transient
	private String genderTxt;
	

	@Transient
	private int meetingId;
	
	@Transient
	private String prjRoleCode; //在项目中扮演的角色
	
	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getGenderTxt() {
		return genderTxt;
	}

	public void setGenderTxt(String genderTxt) {
		this.genderTxt = genderTxt;
	}


	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public int getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(int meetingId) {
		this.meetingId = meetingId;
	}

	public String getPrjRoleCode() {
		return prjRoleCode;
	}

	public void setPrjRoleCode(String prjRoleCode) {
		this.prjRoleCode = prjRoleCode;
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

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}


}
