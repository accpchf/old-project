package com.ks0100.wp.dao;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.entity.UserByMobile;

public interface UserByMobileDao extends SimpleHibernateDao<UserByMobile,Integer>{
	UserByMobile findUserByMobileByAccount(String account);
	User findUserByUserId(int userId);
}
