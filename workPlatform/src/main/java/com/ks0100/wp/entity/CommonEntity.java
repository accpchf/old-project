package com.ks0100.wp.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.apache.shiro.SecurityUtils;

import com.ks0100.wp.ShiroUser;

@MappedSuperclass
public abstract class CommonEntity extends BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8230646172369177519L;

	// 创建者
	@Column(name = "created_by")
	private Integer createdBy;
	
	// 修改者
	@Column(name = "updated_by")
	private Integer updatedBy;

	// 创建时间
	@Column(name = "created_time", columnDefinition = "timestamp")
	private Date createdTime;

	// 修改时间
	@Column(name = "updated_time", columnDefinition = "timestamp")
	private Date updatedTime;

	//可用不可用,默认可用为1
	@Column(name = "enabled")
	private boolean enabled = true;

	public Integer getCreatedBy() {
		return createdBy;
	}

	/**
	 * 设置创建者
	 * @param id 值等于-1时为当前系统登录用户id
	 */
	public void setCreatedBy(Integer createdBy) {
		if(createdBy == -1){
			ShiroUser current = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
			createdBy = current.getUserId();
		}
		this.createdBy = createdBy;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}
	
	/**
	 * 设置修改者
	 * @param id 值等于-1时为当前系统登录用户id
	 */
	public void setUpdatedBy(Integer updatedBy) {
		if(updatedBy == -1){
			ShiroUser current = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
			updatedBy = current.getUserId();
		}
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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
