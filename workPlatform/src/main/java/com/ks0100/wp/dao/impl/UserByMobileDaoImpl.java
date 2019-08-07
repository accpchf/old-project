package com.ks0100.wp.dao.impl;

import java.util.Map;

import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.HibernateDaoImpl;
import com.ks0100.common.util.BeanMapper;
import com.ks0100.wp.dao.UserByMobileDao;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.entity.UserByMobile;

@Repository
public class UserByMobileDaoImpl extends HibernateDaoImpl<UserByMobile,Integer> implements UserByMobileDao{
	public UserByMobile findUserByMobileByAccount(String account){
		String sql ="select um.tbl_user_id userId," +
				//" um.service_public_key servicePublicKey," +
				//" um.service_private_key servicePrivateKey,um.mobile_public_key mobilePublicKey," +
				" um.service_keys serviceKeys ,um.login_random loginRandom from tbl_user u,tbl_user_by_mobile um where u.id=um.tbl_user_id and u.enabled=1 and u.account=:account ";
		Query  query = getSession().createSQLQuery(sql);
		query.setParameter("account", account.trim());
		@SuppressWarnings("unchecked")
		Map<String, Object> map=(Map<String, Object>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).uniqueResult();
		if(map==null)
			return null;
		else			
			return BeanMapper.map(map,UserByMobile.class);
	}

	@Override
	public User findUserByUserId(int userId) {
		String hql = "from User u where u.userId =? and u.enabled = 1";
		return (User)find(hql,userId).get(0);
	}
	
}
