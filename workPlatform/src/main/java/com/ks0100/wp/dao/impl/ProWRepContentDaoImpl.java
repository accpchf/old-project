package com.ks0100.wp.dao.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.SimpleHibernateDaoImpl;
import com.ks0100.wp.dao.ProWRepContentDao;
import com.ks0100.wp.entity.ProWRepContent;


@Repository
public  class ProWRepContentDaoImpl extends SimpleHibernateDaoImpl<ProWRepContent, Integer>
		implements ProWRepContentDao{

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> listParentAndChildProWeekRep(int pwrId) {
		String sql = "select p.id pwrId, pwrc.id proWRepContentId,pwrc.column1_content column1Content,pwrc.column2_content column2Content,pwrc.column3_content column3Content,pwrc.column4_content column4Content,pwrc.duty_user_ids dutyUserIds,pwrc.type type from (select pwr.id from tbl_project_weekly_report pwr where pwr.id =(select parent_id from tbl_project_weekly_report where id = :pwrId) " +
				"	or pwr.id = :pwrId ) p,tbl_project_w_report_content pwrc where p.id = pwrc.tbl_project_weekly_report_id";
		Query  query = getSession().createSQLQuery(sql);
		query.setParameter("pwrId", pwrId);
		List<Map<String, Object>> list = (List<Map<String, Object>>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		return list;
	}


}
