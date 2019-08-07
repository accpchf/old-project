package com.ks0100.wp.dto;

public class UserDto {

	//用户id
	private int userId;
	
	//用户姓名
	private String userName;

	//用户职位
	private String position;
	
	//手机号
	private String phone;
	
	//账号
	private String account;
	
	
	public UserDto(){};
	
	public UserDto(int userId, String userName, String position,String phone,String account) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.position = position;
		this.phone = phone;
		this.account = account;
	}
	
	
	
	
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
	
	
}
