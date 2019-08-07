package com.ks0100.wp.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.SimpleHibernateDaoImpl;
import com.ks0100.wp.constant.StatusEnums.ProjectSatus;
import com.ks0100.wp.dao.UserWeekReportDao;
import com.ks0100.wp.entity.UserWeekReport;

@Repository
public class UserWeekReportDaoImpl extends SimpleHibernateDaoImpl<UserWeekReport, Integer>
		implements UserWeekReportDao {
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findUserWeekReportByUserId(int userId, String monday) {
		String sql = "select distinct uwr.id userWeekReportId ,uwr.comment,uwr.complete_quantity completeQuality ," +
				"uwr.total_quantity totalQuality ,uwr.uncomplete_quantity uncompleteQuality ,uwr.begin_time monday," +
				"p.id proId, p.name proName,o.id userWRepDissId,o.content disContent," +
				"o.userId userId,o.name userName,o.logo userLogo " +
				"from tbl_user_weekly_report uwr left join " +
				"(select uwrd.created_time, uwrd.id,uwrd.tbl_user_weekly_report_id, uwrd.content,u.id  userId,u.name,u.logo from tbl_user u," +
				"tbl_user_weekly_report_discuss uwrd " +
				"where u.id = uwrd.critic) o  on uwr.id = o.tbl_user_weekly_report_id ,tbl_project p, tbl_user u " +
				"where p.status = :status and p.id=uwr.tbl_project_id  and p.enabled=1 and uwr.tbl_user_id = :userId and uwr.begin_time = :monday order by o.created_time";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("monday", monday);
		query.setParameter("status", ProjectSatus.DOING.getCode());
		List<Map<String, Object>> list = query.setResultTransformer(
				CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		return list;
	}

	@Override
	public void addUserWeekReport() {
		String sql = "INSERT INTO tbl_user_weekly_report "+
					 "(tbl_user_id, tbl_project_id, begin_time, end_time, uncomplete_quantity,complete_quantity,  total_quantity) "+
					 "select u.id, rr.pid ,subdate(CURRENT_DATE(),date_format(CURRENT_DATE(),'%w')-1) monday, "+
					 "    subdate(CURRENT_DATE(),date_format(CURRENT_DATE(),'%w')-7) sunday,"+
					 "    rr.due_count,rr.complete_count , rr.all_count  from tbl_user u left    join (	 "+
						 " select r.pid,r.uid,r.due_count,r.complete_count , (r.due_count+r.complete_count) all_count   from "+
						 "("+
						 "  select   p.id pid,ut.tbl_user_id uid ,  t. tbl_project_id,"+
						 " count( case when  (WEEK(CURRENT_DATE(),1)=WEEK(t.due_time,1) and t.complete_time is null  ) then 1 end ) due_count,"+
						 "  count( case when  WEEK(CURRENT_DATE(),1)=WEEK(t.complete_time,1)   then 1 end) complete_count "+
						 " from tbl_task t,tbl_user_task ut,tbl_project p, tbl_user u "+
						 " where "+
						 "  p.status = ? and t.tbl_project_id=p.id and t.id=ut.tbl_task_id and "+
						 "  (  (WEEK(CURRENT_DATE(),1)=WEEK(t.due_time,1) and t.complete_time is null  ) "+
						 "  or  WEEK(CURRENT_DATE(),1)=WEEK(t.complete_time,1) ) "+
						 "  and ut.tbl_role_code='TASK_EXECUTOR' and p.enabled=1  and ut.tbl_user_id=u.id and u.enabled=1 "+
						 "  group by p.id ,ut.tbl_user_id "+
						 ")r "+
					  " )  rr  on 	u.id=rr.uid where  u.enabled=1 "+
					 "";
		createSQLQuery(sql,ProjectSatus.DOING.getCode()).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findTaskQuality(int userId, int projectId, Date monday) {
		String sql = "select r.due_count,r.complete_count , (r.due_count+r.complete_count) all_count   from " +
				" (" +
				"select count( case when  (WEEK(:monday,1)=WEEK(t.due_time,1)  and t.complete_time is null  ) then 1 end ) due_count," +
				"count( case when  WEEK(:monday,1)=WEEK(t.complete_time,1)   then 1 end) complete_count " +
				"from tbl_task t,tbl_user_task ut,tbl_project p, tbl_user u " +
				"where " +
				"t.tbl_project_id=p.id and t.id=ut.tbl_task_id and " +
				"((  WEEK(:monday,1)=WEEK(t.due_time,1) and t.complete_time is null  ) " +
				"or  WEEK(:monday,1)=WEEK(t.complete_time,1) ) " +
				"and ut.tbl_role_code='TASK_EXECUTOR' and p.enabled=1  and ut.tbl_user_id=u.id and u.enabled=1 and u.id =:userId and tbl_project_id = :projectId" +
				")r ";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("monday", monday);
		query.setParameter("projectId", projectId);
		List<Map<String, Object>> list = query.setResultTransformer(
				CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		return list;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findProUserWeekReportByProId(
			String userIds, int projectId, String monday) {
		if(userIds == ""){
			userIds = String.valueOf(0);
		}
		String sql = "select distinct  uwreport.*,p.name projectName from (select uwr.tbl_project_id projectId , u.id userId,u.name userName,u.logo userLogo, uwr.id userWeekReportId,uwr.comment,uwr.complete_quantity completeQuality, " +
				"uwr.total_quantity totalQuality ,uwr.uncomplete_quantity uncompleteQuality ,uwr.begin_time monday, " +
				"uwr.end_time sunday,o.id userWRepDissId ,o.content disContent , " +
				"o.userId criticId ,o.name criticName ,o.logo  criticLogo,o.created_time  " +
				"from tbl_user_weekly_report uwr left join " +
				"(select uwrd.created_time, uwrd.id,uwrd.tbl_user_weekly_report_id, uwrd.content,u.id  userId,u.name,u.logo from tbl_user u," +
				"tbl_user_weekly_report_discuss uwrd " +
				"where u.id = uwrd.critic ) o  on uwr.id = o.tbl_user_weekly_report_id , tbl_user u " +
				"where u.id in ( "+userIds+" ) and uwr.tbl_user_id = u.id and uwr.begin_time = :monday and (uwr.tbl_project_id = :projectId )) uwreport LEFT JOIN tbl_project p on p.id = uwreport.projectId " +
						"order by uwreport.created_time";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("monday", monday);
		query.setParameter("projectId", projectId);
		List<Map<String, Object>> list = (List<Map<String, Object>>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		return list;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findDate(int userId) {
		String sql = "select distinct  u.begin_time monday,u.end_time sunday from tbl_project p, tbl_user_weekly_report u where u.tbl_user_id = :userId and u.tbl_project_id is not null and p.id = u.tbl_project_id and p.status = :status  order by u.begin_time desc limit 0,4";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("status", ProjectSatus.DOING.getCode());
		List<Map<String, Object>> list = query.setResultTransformer(
				CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		return list;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findDateByPro(int projectId) {
		String sql = "select distinct  begin_time monday,end_time sunday from tbl_user_weekly_report where (tbl_project_id = :projectId or tbl_project_id is null) order by begin_time desc limit 0,4";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("projectId", projectId);
		List<Map<String, Object>> list = query.setResultTransformer(
				CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		return list;
	}

	@Override
	public void createUserWeekReport(String monday,String sunday,int userId, int projectId) {
		String sqlString = "delete from  tbl_user_weekly_report where tbl_user_id = ? and begin_time = ? and tbl_project_id is null";
		createSQLQuery(sqlString, userId, monday).executeUpdate();
		String sql = "INSERT INTO tbl_user_weekly_report "+
				 "(tbl_user_id, tbl_project_id, begin_time, end_time, uncomplete_quantity,complete_quantity,  total_quantity) "+
				 "select u.id, rr.pid ,?, "+
				 "    ?,"+
				 "    rr.due_count,rr.complete_count , rr.all_count  from tbl_user u left    join (	 "+
					 " select r.pid,r.uid,r.due_count,r.complete_count , (r.due_count+r.complete_count) all_count   from "+
					 "("+
					 "  select   p.id pid,ut.tbl_user_id uid ,  t. tbl_project_id,"+
					 " count( case when  (WEEK('"+sunday+"',1)=WEEK(t.due_time,1) and t.complete_time is null  ) then 1 end ) due_count,"+
					 "  count( case when  WEEK('"+sunday+"',1)=WEEK(t.complete_time,1)   then 1 end) complete_count "+
					 " from tbl_task t,tbl_user_task ut,tbl_project p, tbl_user u "+
					 " where "+
					 "  t.tbl_project_id=p.id and t.id=ut.tbl_task_id and "+
					 "  (  (WEEK('"+sunday+"',1)=WEEK(t.due_time,1) and t.complete_time is null  ) "+
					 "  or  WEEK('"+sunday+"',1)=WEEK(t.complete_time,1) ) "+
					 "  and ut.tbl_role_code='TASK_EXECUTOR' and p.enabled=1 and p.id = ?  and ut.tbl_user_id=u.id and u.enabled=1 "+
					 "  group by p.id ,ut.tbl_user_id "+
					 ")r "+
				  " )  rr  on u.id = ? and	u.id=rr.uid where  u.enabled=1 and u.id="+userId;
		createSQLQuery(sql,monday,sunday,projectId,userId).executeUpdate();
	}

	@Override
	public List<Map<String, Object>> findDiscussByUWRId(int pwrId) {
		String sql = "select uwrd.created_time, uwrd.id uWRId, uwrd.content disContent,u.id  criticId,u.name criticName,u.logo criticLogo " +
				"from tbl_user u,tbl_user_weekly_report_discuss uwrd " +
				"where u.id = uwrd.critic and uwrd.tbl_user_weekly_report_id = :pwrId";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("pwrId", pwrId);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		return list;
	}

	
}
