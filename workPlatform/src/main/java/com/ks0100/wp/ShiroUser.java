package com.ks0100.wp;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authz.SimpleAuthorizationInfo;

import com.ks0100.wp.entity.User;

public class ShiroUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8735132673967611113L;

	private SimpleAuthorizationInfo info = null;

	// 用户id
	private int userId;

	// 帐号
	private String account = "";

	// 姓名
	private String name = "";

	// 密码
	private String password;

	// 头像图标
	private String logo = "";

	// 电话
	private String mobile = "";

	// 性别
	private String gender = "";

	// 性别
	private String genderTxt = "";

	// 职位
	private String position = "";

	// 是否激活
	private boolean active;

	private User user;

	private boolean needUpdate;
	
	//用户权限
	 Set<String> permissions = new HashSet<String>();
	
	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}	 
	public SimpleAuthorizationInfo getInfo() {
		return info;
	}

	public void setInfo(SimpleAuthorizationInfo info) {
		this.info = info;
	}

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getGenderTxt() {
		return genderTxt;
	}

	public void setGenderTxt(String genderTxt) {
		this.genderTxt = genderTxt;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isNeedUpdate() {
		return needUpdate;
	}

	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
}
