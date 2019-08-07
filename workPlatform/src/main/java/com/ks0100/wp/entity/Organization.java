package com.ks0100.wp.entity;

import java.util.ArrayList;
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


/**
 * The persistent class for the tbl_organization database table.
 * 
 */
@Entity
@Table(name="tbl_organization")
public class Organization extends CommonEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1804369733652578014L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "contacter")
	private String contacter;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "logo")
	private String logo;
	
	@Column(name="invitation_enabled")
	private boolean invitationEnabled;
	
	@Column(name="invitation_code")
	private String invitationCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inviter")
	private User inviter;
	
	/**
	 * 表明向手机端发送的一组项目的状态
	 */
	@Transient
	private String projectStatus;
	
	//@OneToMany(mappedBy="organization", fetch = FetchType.EAGER)
	@Transient
	private List<Project> projectList = new ArrayList<Project>();

	@Column(name = "uuid")
	private String uuid=UUID.randomUUID().toString();
	
	public Organization(int id,String name,String description,String contacter,String phone
			,String logo,boolean invitationEnabled,String invitationCode,User inviter,String uuid){
		this.id=id;
		this.name=name;
		this.description=description;
		this.contacter=contacter;
		this.phone=phone;
		this.logo=logo;
		this.invitationEnabled=invitationEnabled;
		this.invitationCode=invitationCode;
		this.inviter=inviter;
		this.uuid=uuid;
	}
	
	
	
	public String getProjectStatus() {
		return projectStatus;
	}

	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}

	public String getInvitationCode() {
		return invitationCode;
	}

	public void setInvitationCode(String invitationCode) {
		this.invitationCode = invitationCode;
	}

	public List<Project> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<Project> projectList) {
		this.projectList = projectList;
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
	
	public Organization() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContacter() {
		return this.contacter;
	}

	public void setContacter(String contacter) {
		this.contacter = contacter;
	}


	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLogo() {
		return this.logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isInvitationEnabled() {
		return invitationEnabled;
	}

	public void setInvitationEnabled(boolean invitationEnabled) {
		this.invitationEnabled = invitationEnabled;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}



}