package com.ks0100.wp.dao;


import java.util.List;
import java.util.Map;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.User;

public interface UserDao extends SimpleHibernateDao<User,Integer>{
	
	User findUserByAccount(String account) ;
	
	User findUserByActiveUuid(String acitveUuid);
	
	User findUserByPasswordUuid(String passwordUuid);
	
	List<Map<String, Object>> listUsersAboutMeetingByProject(int projectId);
	
	List<Integer> listUserAboutMeetingByMeetingId(int meetingId);
	
	String findUserLogoByTaskId(int taskId);
	
/*	void addUser(User user);

	void updateUser(User user);
	
	User getUserById(int id);*/
	
	/**
	 * 根据用户Ids查询用户
	 * @param ids
	 * @return
	 */
	List<User> findUserListByIds(String ids);
	
	@SuppressWarnings("rawtypes")
	List findPermit(int userId);
}
