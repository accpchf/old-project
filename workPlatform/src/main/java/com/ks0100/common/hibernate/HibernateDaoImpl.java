package com.ks0100.common.hibernate;

import java.io.Serializable;

import org.hibernate.SessionFactory;

public  class HibernateDaoImpl <T, PK extends Serializable> extends SimpleHibernateDaoImpl<T, PK>{
	/**
	 * 
	 * public class UserDao extends HibernateDao<User, Long>{
	 * }
	 */
	public HibernateDaoImpl() {
		super();
	}

	/**
	 * 
	 * HibernateDao<User, Long> userDao = new HibernateDao<User, Long>(sessionFactory, User.class);
	 */
	public HibernateDaoImpl(final SessionFactory sessionFactory, final Class<T> entityClass) {
		super(sessionFactory, entityClass);
	}
}
