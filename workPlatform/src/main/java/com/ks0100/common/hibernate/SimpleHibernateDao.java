package com.ks0100.common.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;

public interface SimpleHibernateDao <T, PK extends Serializable>{
	
	public void save(final T entity);//audit can aop
	
	public void delete(final T entity);//audit can aop
	
	public void update(final T entity);//audit can aop
	
	public void saveOrUpdate(final T entity);

	
	/**
	 */
	public void delete(final PK id);
	
	public T load(final PK id);
	
	/**
	 */
	public T get(final PK id);
	
	public T get(Map<String, Object> parameter);
	
	public Object get(final PK id,Class<T> clazz);
	
	public List<T> getList(Map<String, Object> parameter);
	/**
	 */
	public List<T> get(final Collection<PK> ids);
	
	/**
	 */
	public List<T> getAll();
	
	/**
	 */
	public List<T> getAll(String orderByProperty, boolean isAsc);
	
	/**
	 */
	public List<T> findBy(final String propertyName, final Object value);

	/**
	 */
	public T findUniqueBy(final String propertyName, final Object value);
	/**
	 * 
	 */
	public <X> List<X> find(final String hql, final Object... values);
	/**
	 * 
	 */
	public <X> List<X> find(final String hql, final Map<String, ?> values);
	/**
	 * 
	 */
	public <X> X findUnique(final String hql, final Object... values);

	/**
	 * 
	 */
	public <X> X findUnique(final String hql, final Map<String, ?> values) ;
	/**
	 * 
	 */
	public int batchExecute(final String hql, final Object... values);
	/**
	 * 
	 */
	public int batchExecute(final String hql, final Map<String, ?> values);
	/**
	 * 
	 */
	public Query createQuery(final String queryString, final Object... values);
	/**
	 * 
	 */
	public Query createQuery(final String queryString, final Map<String, ?> values);
	/**
	 * 
	 */
	public List<T> find(final Criterion... criterions);
	/**
	 * 
	 */
	public T findUnique(final Criterion... criterions);
	/**
	 */
	public Criteria createCriteria(final Criterion... criterions);
	/**
	 */
	public void initProxyObject(Object proxy);

	/**
	 */
	public Query distinct(Query query);
	/**
	 */
	public Criteria distinct(Criteria criteria);
	/**
	 */
	public String getIdName();
	/**
	 * 
	 */
	public boolean isPropertyUnique(final String propertyName, final Object newValue, final Object oldValue);
	public void evict(Object obj);
	/**
	 * 根据参数，如一组id 获取List
	 * @param parameterName
	 * @param parameter_ids
	 * @return
	 */
	public List<T> getListByIds(List<PK> parameter_ids) ;
	
	
	public void deleteByIdList (List<PK> parameter_ids);
}
