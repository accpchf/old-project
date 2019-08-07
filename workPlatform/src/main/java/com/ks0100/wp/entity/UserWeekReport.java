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

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Table(name = "tbl_user_weekly_report")
public class UserWeekReport extends CommonEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4354432680155454847L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int userWeekReportId;
	@Column(name = "complete_quantity")
	private int completeQuality;
	
	@Column(name = "uncomplete_quantity")
	private int unCompleteQuality;
	
	@Column(name = "total_quantity")
	private int totalQuality;
	
	@Column(name = "comment")
	private String comment;
	
	@Column(name = "filled_in")
	private boolean filledIn;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tbl_project_id")
	private Project pro;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tbl_user_id")
	private User user;
	
	@Column(name = "begin_time")
	private Date monday;
	
	@Column(name = "end_time")
	private Date sunday;
	
	@Transient
	private List<UserWeekRepDiscuss> uwReportDisscuss = new ArrayList<UserWeekRepDiscuss>();
	
	@Transient
	private Project project;
	
	public Project getPro() {
		return pro;
	}

	public void setPro(Project pro) {
		this.pro = pro;
	}

	public int getUserWeekReportId() {
		return userWeekReportId;
	}

	public void setUserWeekReportId(int userWeekReportId) {
		this.userWeekReportId = userWeekReportId;
	}


	public int getCompleteQuality() {
		return completeQuality;
	}

	public void setCompleteQuality(int completeQuality) {
		this.completeQuality = completeQuality;
	}

	public int getUnCompleteQuality() {
		return unCompleteQuality;
	}

	public void setUnCompleteQuality(int unCompleteQuality) {
		this.unCompleteQuality = unCompleteQuality;
	}

	public int getTotalQuality() {
		return totalQuality;
	}

	public void setTotalQuality(int totalQuality) {
		this.totalQuality = totalQuality;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public boolean isFilledIn() {
		return filledIn;
	}

	public void setFilledIn(boolean filledIn) {
		this.filledIn = filledIn;
	}

	public List<UserWeekRepDiscuss> getUwReportDisscuss() {
		return uwReportDisscuss;
	}

	public void setUwReportDisscuss(List<UserWeekRepDiscuss> uwReportDisscuss) {
		this.uwReportDisscuss = uwReportDisscuss;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Date getMonday() {
		return monday;
	}

	public void setMonday(Date monday) {
		this.monday = monday;
	}

	public Date getSunday() {
		return sunday;
	}

	public void setSunday(Date sunday) {
		this.sunday = sunday;
	}
}
