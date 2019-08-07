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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Table(name = "tbl_project_weekly_report")
public class ProjectWeeklyReport extends CommonEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4139706461248626263L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int projectWeeklyReportId;
	
	@Column(name = "begin_time", columnDefinition = "timestamp")
	private Date beginTime;
	
	@Column(name = "end_time", columnDefinition = "timestamp")
	private Date endTime;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "phase")
	private String phase;
	
	@Column(name = "progress")
	private int progress;
	
	@JsonIgnore
	@OneToOne
	@JoinColumn(name = "parent_id")
	private ProjectWeeklyReport parentProWeekRep;
	
	@Transient
	private Date deliveryData;

	@ManyToOne
	@JoinColumn(name = "tbl_project_id")
	private Project project;
	
	@Transient
	private String createdString;
	
	@Transient
	private String deliveryString;
	
	@Column(name = "filled_in")
	private boolean filledIn;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tbl_user_id")
	private User filledUser;

	@Transient
	private List<ProWRepContent> listProWRepContents = new ArrayList<ProWRepContent>();
	
	@Transient
	private List<ProWRepContent> listParentProWRepContents = new ArrayList<ProWRepContent>();
	
	
	public int getProjectWeeklyReportId() {
		return projectWeeklyReportId;
	}

	public void setProjectWeeklyReportId(int projectWeeklyReportId) {
		this.projectWeeklyReportId = projectWeeklyReportId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public boolean isFilledIn() {
		return filledIn;
	}

	public void setFilledIn(boolean filledIn) {
		this.filledIn = filledIn;
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

	public ProjectWeeklyReport getParentProWeekRep() {
		return parentProWeekRep;
	}

	public void setParentProWeekRep(ProjectWeeklyReport parentProWeekRep) {
		this.parentProWeekRep = parentProWeekRep;
	}

	public void setParentProWeekRep(int parentProWeekRepId) {
		ProjectWeeklyReport pwr = new ProjectWeeklyReport();
		pwr.setProjectWeeklyReportId(parentProWeekRepId);
		this.parentProWeekRep = pwr;
	}
	public Date getDeliveryData() {
		return deliveryData;
	}

	public void setDeliveryData(Date deliveryData) {
		this.deliveryData = deliveryData;
	}


	public String getDeliveryString() {
		return deliveryString;
	}

	public void setDeliveryString(String deliveryString) {
		this.deliveryString = deliveryString;
	}

	public String getCreatedString() {
		return createdString;
	}

	public void setCreatedString(String createdString) {
		this.createdString = createdString;
	}

	public List<ProWRepContent> getListProWRepContents() {
		return listProWRepContents;
	}

	public void setListProWRepContents(List<ProWRepContent> listProWRepContents) {
		this.listProWRepContents = listProWRepContents;
	}

	public List<ProWRepContent> getListParentProWRepContents() {
		return listParentProWRepContents;
	}

	public void setListParentProWRepContents(
			List<ProWRepContent> listParentProWRepContents) {
		this.listParentProWRepContents = listParentProWRepContents;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	

	public User getFilledUser() {
		return filledUser;
	}

	public void setFilledUser(User filledUser) {
		this.filledUser = filledUser;
	}
	public void setFilledUser(int filledUserId) {
		User user=new User();
		user.setUserId(filledUserId);
		this.filledUser = user;
	}

}
