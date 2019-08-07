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
@Table(name = "tbl_user_weekly_report_discuss")
public class UserWeekRepDiscuss extends CommonEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3552673362970030686L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int userWeekRepDiscussId;
	
	@Column(name = "content")
	private String content;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tbl_user_weekly_report_id")
	private UserWeekReport userWeekReport;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "critic")
	private User criticUser;

	public int getUserWeekRepDiscussId() {
		return userWeekRepDiscussId;
	}

	public void setUserWeekRepDiscussId(int userWeekRepDiscussId) {
		this.userWeekRepDiscussId = userWeekRepDiscussId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UserWeekReport getUserWeekReport() {
		return userWeekReport;
	}

	public void setUserWeekReport(UserWeekReport userWeekReport) {
		this.userWeekReport = userWeekReport;
	}

	public User getCriticUser() {
		return criticUser;
	}

	public void setCriticUser(User criticUser) {
		this.criticUser = criticUser;
	}
	public void setCriticUser(int criticId) {
		User user=new User();
		user.setUserId(criticId);
		this.criticUser = user;
	}
}
