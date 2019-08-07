package com.ks0100.common.hibernate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

/**
 * 向ObjectMapper注册Hibernate4Module, Hibernate4使用Jackson 2, Hibernate3使用jackson-module-hibernate
 * @author xie linming
 * @date 2014年12月26日
 */
public class HibernateAwareObjectMapper extends ObjectMapper {

	private static final long serialVersionUID = 7958167810745447350L;

	public HibernateAwareObjectMapper() {
		Hibernate4Module hm = new Hibernate4Module();
		registerModule(hm);
	}

}
