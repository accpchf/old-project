package com.ks0100.wp.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tbl_user_by_mobile")
public class UserByMobile extends BaseEntity{
	/**
	 * 
	 */
	private static final long serialVersionUID = -170537383298185074L;
	
	@Id
	@Column(name = "tbl_user_id")
	private int userId;
	
/*	@Column(name = "service_public_key")
	private String servicePublicKey;
	
	@Column(name = "service_private_key")
	private String servicePrivateKey;
	
	@Column(name = "mobile_public_key")
	private String mobilePublicKey;*/
	
	@Column(name = "service_keys")
	private byte[] serviceKeys;
	
	@Column(name = "login_random")
	private String loginRandom;
	
	// 创建时间
	@Column(name = "created_time", columnDefinition = "timestamp")
	private Date createdTime;

	// 修改时间
	@Column(name = "updated_time", columnDefinition = "timestamp")
	private Date updatedTime;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	

	public byte[] getServiceKeys() {
		return serviceKeys;
	}

	public void setServiceKeys(byte[] serviceKeys) {
		this.serviceKeys = serviceKeys;
	}

	public String getLoginRandom() {
		return loginRandom;
	}

	public void setLoginRandom(String loginRandom) {
		this.loginRandom = loginRandom;
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
	
}
