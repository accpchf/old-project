package com.ks0100.wp.service;

import java.util.List;
import java.util.Set;

import com.ks0100.wp.entity.User;

public interface LoginService {

	public User findUserByAccount(String account);
	
	public User findUserByActiveUuid(String Uuid);
	
	public User findUserByPasswordUuid(String Uuid);

	public void register(User user);

	public void saveUser(User user);

	public User findUser(int userId);
	
	public void updateUser(User user);

	public List<User> findUserByIds(String ids);
	
	public String findUserLogoByTaskId(int taskId);
	
	  /**
	   * 根据账号查找权限码
	   * @param account
	   * @return
	   */
	public  Set<String> findPermit(int userId);
}
