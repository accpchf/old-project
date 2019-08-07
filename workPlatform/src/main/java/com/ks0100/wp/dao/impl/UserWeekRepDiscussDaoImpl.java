package com.ks0100.wp.dao.impl;

import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.SimpleHibernateDaoImpl;
import com.ks0100.wp.dao.UserWeekRepDiscussDao;
import com.ks0100.wp.entity.UserWeekRepDiscuss;

@Repository
public class UserWeekRepDiscussDaoImpl extends SimpleHibernateDaoImpl<UserWeekRepDiscuss, Integer> implements
			UserWeekRepDiscussDao{

}
