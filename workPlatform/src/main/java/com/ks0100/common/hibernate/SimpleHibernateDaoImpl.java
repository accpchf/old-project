package com.ks0100.common.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * 
 * @author haifeng
 */
@SuppressWarnings("unchecked")
public  class SimpleHibernateDaoImpl<T, PK extends Serializable> {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected SessionFactory sessionFactory;

	protected Class<T> entityClass;

	/**
	 * public class UserDao extends SimpleHibernateDao<User, Long>
	 */
	public SimpleHibernateDaoImpl() {
		this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
	}

	/**
	 * SimpleHibernateDao<User, Long> userDao = new SimpleHibernateDao<User, Long>(sessionFactory, User.class);
	 */
	public SimpleHibernateDaoImpl(final SessionFactory sessionFactory, final Class<T> entityClass) {
		this.sessionFactory = sessionFactory;
		this.entityClass = entityClass;
	}

	/**
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 */
	@Autowired
	public void setSessionFactory(final SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		logger.debug("------------------------------------------setSessionFactory");
	}

	/**
	 */
	public Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public void save(final T entity){
		Assert.notNull(entity, "entity  not null");
		getSession().save(entity);
		logger.debug("save entity: {}", entity);
	}
	
	public void update(final T entity) {
		Assert.notNull(entity, "entity  not null");
		getSession().saveOrUpdate(entity);
		logger.debug("update entity: {}", entity);
	}
	
	/**
	 */
	public void saveOrUpdate(final T entity) {
		Assert.notNull(entity, "entity  not null");
		getSession().saveOrUpdate(entity);
		logger.debug("saveOrUpdate entity: {}", entity);
	}

	/**
	 * 
	 */
	public void delete(final T entity) {
		Assert.notNull(entity, "entity not null");
		getSession().delete(entity);
		logger.debug("delete entity: {}", entity);
	}

	/**
	 */
	public void delete(final PK id) {
		Assert.notNull(id, "id not null");
		delete(get(id));
		logger.debug("delete entity {},id is {}", entityClass.getSimpleName(), id);
	}

	public void deleteByIdList(List<PK> parameter_ids) {
		StringBuilder sb = new StringBuilder();
		sb.append("delete from "+entityClass.getSimpleName()+" where "+getIdName()+" in (:ids)");
		Query query = getSession().createQuery(sb.toString());
		query.setParameterList("ids", parameter_ids);
		query.executeUpdate();
	}
	/**
	 *  lazy load
	 */
	public T load(final PK id) {
		Assert.notNull(id, "id not null");
		return (T) getSession().load(entityClass, id);
	}
	
	/**
	 */
	public T get(final PK id) {
		Assert.notNull(id, "id not null");
		return (T) getSession().get(entityClass, id);
	}
	
	public T get(Map<String, Object> parameter) {
        return (T) findUnique(buildHQL(parameter), parameter);
	}
	
	public List<T> getList(Map<String, Object> parameter) {
		//return find(Restrictions.in(getIdName(), ids));
		return find(buildHQL(parameter), parameter);
	}
	
	
	public  String buildHQL(Map<String, Object> parameter){
		StringBuilder sb = new StringBuilder();
		sb.append("from "+entityClass.getSimpleName()+" where 1=1");
		
        Set<Map.Entry<String, Object>> set = parameter.entrySet();
        for (Iterator<Map.Entry<String, Object>> it = set.iterator(); it.hasNext();) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it.next();
            //System.out.println("key："+entry.getKey() + "；value：" + entry.getValue());
            sb.append(" and "+entry.getKey()+"= "+entry.getValue());
            //sb.append(" and "+entry.getKey()+"= ?");
        }
        return sb.toString();
	}
	
	public List<T> getListByIds(List<PK> parameter_ids) {
		
		Criteria cr = getSession().createCriteria(entityClass);
		cr.add(Restrictions.in(getIdName(), parameter_ids));
		return cr.list();
	}
	
	public Object get(final PK id,Class<T> clazz){
		Assert.notNull(id, "id not null");
		return (Object) getSession().get(clazz, id);
	}
	
	
	/**
	 */
	public List<T> get(final Collection<PK> ids) {
		return find(Restrictions.in(getIdName(), ids));
	}

	/**
	 */
	public List<T> getAll() {
		return find();
	}

	/**
	 */
	public List<T> getAll(String orderByProperty, boolean isAsc) {
		Criteria c = createCriteria();
		if (isAsc) {
			c.addOrder(Order.asc(orderByProperty));
		} else {
			c.addOrder(Order.desc(orderByProperty));
		}
		return c.list();
	}

	/**
	 */
	public List<T> findBy(final String propertyName, final Object value) {
		Assert.hasText(propertyName, "propertyName not null");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return find(criterion);
	}

	/**
	 */
	public T findUniqueBy(final String propertyName, final Object value) {
		Assert.hasText(propertyName, "propertyName not null");
		Criterion criterion = Restrictions.eq(propertyName, value);
		return (T) createCriteria(criterion).uniqueResult();
	}

	/**
	 * 
	 */
	public <X> List<X> find(final String hql, final Object... values) {
		return createQuery(hql, values).list();
	}

	/**
	 * 
	 */
	public <X> List<X> find(final String hql, final Map<String, ?> values) {
		return createQuery(hql, values).list();
	}

	/**
	 * 
	 */
	public <X> X findUnique(final String hql, final Object... values) {
		return (X) createQuery(hql, values).setMaxResults(1).uniqueResult();
	}

	/**
	 * 
	 */
	public <X> X findUnique(final String hql, final Map<String, ?> values) {
		return (X) createQuery(hql, values).setMaxResults(1).uniqueResult();
	}

	/**
	 * 
	 */
	public int batchExecute(final String hql, final Object... values) {
		return createQuery(hql, values).executeUpdate();
	}

	/**
	 * 
	 */
	public int batchExecute(final String hql, final Map<String, ?> values) {
		return createQuery(hql, values).executeUpdate();
	}

	
	public SQLQuery createSQLQuery(final String queryString, final Object... values) {
		Assert.hasText(queryString, "queryString not null");
		SQLQuery sqlQuery = getSession().createSQLQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				sqlQuery.setParameter(i, values[i]);
			}
		}
		return sqlQuery;
	}
	
	public SQLQuery createSQLQuery(final String queryString, final Map<String, ?> values) {
		Assert.hasText(queryString, "queryString not null");
		SQLQuery sqlQuery = getSession().createSQLQuery(queryString);
		if (values != null) {
			sqlQuery.setProperties(values);
		}
		return sqlQuery;
	}
	
	/**
	 * 
	 */
	public Query createQuery(final String queryString, final Object... values) {
		Assert.hasText(queryString, "queryString not null");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query;
	}


	
	/**
	 * 
	 */
	public Query createQuery(final String queryString, final Map<String, ?> values) {
		Assert.hasText(queryString, "queryString not null");
		Query query = getSession().createQuery(queryString);
		if (values != null) {
			query.setProperties(values);
		}
		return query;
	}

	/**
	 * 
	 */
	public List<T> find(final Criterion... criterions) {
		return createCriteria(criterions).list();
	}

	/**
	 * 
	 */
	public T findUnique(final Criterion... criterions) {
		return (T) createCriteria(criterions).uniqueResult();
	}

	/**
	 * 
	 */
	public Criteria createCriteria(final Criterion... criterions) {
		Criteria criteria = getSession().createCriteria(entityClass);
		for (Criterion c : criterions) {
			criteria.add(c);
		}
		return criteria;
	}

	/**
	 */
	public void initProxyObject(Object proxy) {
		Hibernate.initialize(proxy);
	}

	/**
	 */
	public void flush() {
		getSession().flush();
	}

	public void evict(Object obj){
		//getSession().evict(arg0)
		getSession().evict(obj);
	}
	
	
	/**
	 */
	public Query distinct(Query query) {
		query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return query;
	}

	/**
	 */
	public Criteria distinct(Criteria criteria) {
		criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		return criteria;
	}

	/**
	 */
	public String getIdName() {
		ClassMetadata meta = getSessionFactory().getClassMetadata(entityClass);
		return meta.getIdentifierPropertyName();
	}

	/**
	 * 
	 */
	public boolean isPropertyUnique(final String propertyName, final Object newValue, final Object oldValue) {
		if (newValue == null || newValue.equals(oldValue)) {
			return true;
		}
		Object object = findUniqueBy(propertyName, newValue);
		return object == null;
	}
}