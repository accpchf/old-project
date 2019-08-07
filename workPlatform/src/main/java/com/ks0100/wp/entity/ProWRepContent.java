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

import org.codehaus.jackson.annotate.JsonIgnore;

@Entity
@Table(name = "tbl_project_w_report_content")
public class ProWRepContent extends CommonEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4751544512090384903L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int proWRepContentId;
	
	@Column(name = "column1_content")
	private String column1Content;
	
	@Column(name = "column2_content")
	private String column2Content;
	
	@Column(name = "column3_content")
	private String column3Content;
	
	@Column(name = "column4_content")
	private String column4Content;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tbl_project_weekly_report_id")
	private ProjectWeeklyReport projectWeeklyReport;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "duty_user_ids")
	private String dutyUserIds;

	@Transient
	private List<User> users = new ArrayList<User>();
	
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public int getProWRepContentId() {
		return proWRepContentId;
	}

	public void setProWRepContentId(int proWRepContentId) {
		this.proWRepContentId = proWRepContentId;
	}

	public String getColumn1Content() {
		return column1Content;
	}

	public void setColumn1Content(String column1Content) {
		this.column1Content = column1Content;
	}

	public String getColumn2Content() {
		return column2Content;
	}

	public void setColumn2Content(String column2Content) {
		this.column2Content = column2Content;
	}

	public String getColumn3Content() {
		return column3Content;
	}

	public void setColumn3Content(String column3Content) {
		this.column3Content = column3Content;
	}

	public String getColumn4Content() {
		return column4Content;
	}

	public void setColumn4Content(String column4Content) {
		this.column4Content = column4Content;
	}

	
	public String getType() {
		return type;
	}

	public ProjectWeeklyReport getProjectWeeklyReport() {
		return projectWeeklyReport;
	}

	public void setProjectWeeklyReport(ProjectWeeklyReport projectWeeklyReport) {
		this.projectWeeklyReport = projectWeeklyReport;
	}
	
	public void setProjectWeeklyReport(int ProWeekRepId) {
		ProjectWeeklyReport pwr = new ProjectWeeklyReport();
		pwr.setProjectWeeklyReportId(ProWeekRepId);
		this.projectWeeklyReport = pwr;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDutyUserIds() {
		return dutyUserIds;
	}

	public void setDutyUserIds(String dutyUserIds) {
		this.dutyUserIds = dutyUserIds;
	}
	
	
}
