package com.ks0100.wp;

import java.io.Serializable;

import com.ks0100.wp.entity.UserByMobile;

public class MobileUser implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7896471595980338185L;
	private ShiroUser shiroUser;
	private UserByMobile userByMobile;
	
	public ShiroUser getShiroUser() {
		return shiroUser;
	}
	public void setShiroUser(ShiroUser shiroUser) {
		this.shiroUser = shiroUser;
	}
	public UserByMobile getUserByMobile() {
		return userByMobile;
	}
	public void setUserByMobile(UserByMobile userByMobile) {
		this.userByMobile = userByMobile;
	}
	
	
}
