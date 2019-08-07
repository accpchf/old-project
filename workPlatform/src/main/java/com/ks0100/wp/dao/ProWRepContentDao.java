package com.ks0100.wp.dao;

import java.util.List;
import java.util.Map;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.ProWRepContent;

public interface ProWRepContentDao extends SimpleHibernateDao<ProWRepContent, Integer>{

	/**
	 * 列出项目周报的上周周报内容和下周周报
	 * @param taskId
	 * @return
	 */
	List<Map<String, Object>> listParentAndChildProWeekRep(int pwrId);
	

}
