package com.ks0100.wp.dao.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.SimpleHibernateDaoImpl;
import com.ks0100.wp.constant.StatusEnums.ProjectSatus;
import com.ks0100.wp.dao.ProWeeklyRepDao;
import com.ks0100.wp.entity.ProjectWeeklyReport;

@Repository
public class ProWeeklyRepDaoImpl extends SimpleHibernateDaoImpl<ProjectWeeklyReport, Integer> implements ProWeeklyRepDao{

	@Override
	public void addProjectWeeklyReport() {
		Calendar c = Calendar.getInstance();  
		c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String monday;
        monday = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String sunday = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()); 
		String sql = "INSERT INTO tbl_project_weekly_report (tbl_project_id, begin_time, end_time, parent_id) " +
				" select p.id  ,'"+monday+"','"+sunday+"'  , r.id parent  from tbl_project p left join " +
				"(  select tbl_project_id,id  from tbl_project_weekly_report " +
				" where begin_time=  DATE_SUB('"+monday+"',INTERVAL 7 DAY) " +
				"and end_time=DATE_SUB('"+sunday+"',INTERVAL 7 DAY) ) r " +
				"on p.id=r.tbl_project_id where p.status = ? and p.enabled=1 order by p.id  ";
      	createSQLQuery(sql, ProjectSatus.DOING.getCode()).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ProjectWeeklyReport findProWeekReport(String monday, int projectId) {
		String sql = "select pwr.tbl_user_id userId, p.end_time deliveryData ,pwr.id projectWeeklyReportId,pwr.phase phase ,pwr.parent_id parentProWeekRepId ," +
				"pwr.progress progress ,pwr.status status , pwr.created_time createdTime  from  tbl_project p ,tbl_project_weekly_report pwr " +
				"where p.id = pwr.tbl_project_id and p.id= :projectId and pwr.begin_time = :monday ";
		Query  query = getSession().createSQLQuery(sql);
		query.setParameter("monday", monday);
		query.setParameter("projectId", projectId);
		Map<String, Object> map = (Map<String, Object>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).uniqueResult();
		ProjectWeeklyReport pwr = null;
		if (map != null) {
			pwr = new ProjectWeeklyReport();
			pwr.setProjectWeeklyReportId((Integer)map.get("projectWeeklyReportId"));
			pwr.setPhase((String)map.get("phase"));
			pwr.setDeliveryData((Date)map.get("deliveryData"));
			pwr.setProgress((Byte)map.get("progress"));
			pwr.setStatus((String)map.get("status"));
			pwr.setCreatedTime((Date)map.get("createdTime"));
			if(map.get("userId") != null){
				pwr.setFilledUser((Integer)map.get("userId"));
			}
			
			if(map.get("parentProWeekRepId") != null){
				pwr.setParentProWeekRep((Integer) map.get("parentProWeekRepId"));
			}
			
		}	
		return pwr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findDateByPro(int projectId) {
		String sql = "select distinct  begin_time monday,end_time sunday from tbl_project_weekly_report where tbl_project_id = :projectId order by begin_time desc limit 0,4";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("projectId", projectId);
		List<Map<String, Object>> list = query.setResultTransformer(
				CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		return list;
	}

}
