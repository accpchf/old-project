package com.ks0100.wp.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.SimpleHibernateDaoImpl;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.constant.BusinessConstant.RoleConstant;
import com.ks0100.wp.dao.MeetingDao;
import com.ks0100.wp.entity.Meeting;

@Repository
public class MeetingDaoImpl extends SimpleHibernateDaoImpl<Meeting, Integer> implements MeetingDao {

  @Override
  public List<Meeting> listMeetingsSortByCreatedTime(int projectId) {
	String hql = "from Meeting m where m.project.projectId = ? order by m.createdTime desc";
	return find(hql, projectId);
  }

  @Override
  public void deleteMeetingUser(int meetingId, String idsString) {
	String sql = "delete from tbl_user_meeting where  tbl_meeting_id = " + meetingId;
	if(!"all".equals(StringUtils.trim(idsString))) {
	  sql += "  and tbl_user_id in (" + idsString + ")";
	}
	SQLQuery query = getSession().createSQLQuery(sql);
	query.executeUpdate();
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Integer> findMeetingUser(int meetingId, String idsString) {
	String sql = "select tbl_user_id from tbl_user_meeting where tbl_meeting_id = " + meetingId
		+ " and tbl_user_id in (" + idsString + ")";
	SQLQuery query = getSession().createSQLQuery(sql);
	return query.list();
  }

  @SuppressWarnings("boxing")
  @Override
  public void addMeetingUser(int meetingId, String[] ids) {
	if(ids == null || ids.length == 0) {
	  return;
	}
	final String sqlm = "insert into tbl_user_meeting(tbl_user_id,tbl_meeting_id, tbl_role_code) values";
	for(String id : ids) {
	  String v = "(%s, %d, '%s')";
	  String sql = sqlm + String.format(v, id, meetingId, RoleConstant.MEETING_MEMBER);
	  SQLQuery query = getSession().createSQLQuery(sql);
	  query.executeUpdate();
	}

  }

  @SuppressWarnings("boxing")
  @Override
  public void addMeetingUserRole(Meeting meeting, ShiroUser user, String roleCode) {
	String sqlM = "insert into tbl_user_meeting(tbl_user_id,tbl_meeting_id, tbl_role_code) values(%d, %d, '%s')";
	String sql = String.format(sqlM, user.getUserId(), meeting.getMeetingId(), roleCode);
	SQLQuery query = getSession().createSQLQuery(sql);
	query.executeUpdate();

  }

@SuppressWarnings("unchecked")
@Override
public List<Map<String, Object>> listFutureMeetingByUserIdForMobile(int userId) {
	DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd"); 
	String sql = "select p.name as projectName,pm.begin_time as beginTime,pm.id as meetingId,pm.title,pm.place from tbl_project p,tbl_project_meeting pm,tbl_user_meeting um where um.tbl_meeting_id = pm.id and p.id = pm.tbl_project_id and um.tbl_user_id = :userId and DATE_FORMAT(pm.begin_time,'%Y-%m-%d') >= '"+DateTime.now().toString(format)+"' order by pm.begin_time desc";
	Query  query = createSQLQuery(sql);
	query.setParameter("userId", userId);
	return query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
}

@Override
public Map<String,Object> listCompletedMeetingByUserIdForMobile(
		int userId, int pageIndex, int pageMax) {
	DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd"); 
	String sql = "select p.name as projectName,pm.begin_time as beginTime,pm.id as meetingId,pm.title,pm.place from tbl_project p,tbl_project_meeting pm,tbl_user_meeting um where um.tbl_meeting_id = pm.id and p.id = pm.tbl_project_id and um.tbl_user_id = :userId and DATE_FORMAT(pm.begin_time,'%Y-%m-%d') < '"+DateTime.now().toString(format)+"' order by pm.begin_time desc";
	Query  query = createSQLQuery(sql);
	query.setParameter("userId", userId);
	query.setFirstResult((pageIndex-1)*10);
	query.setMaxResults(pageMax);
	@SuppressWarnings("unchecked")
	List<Map<String, Object>> list = query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("pageIndex", pageIndex);
	map.put("list", list);
	return map;
}

@SuppressWarnings("unchecked")
@Override
public List<Map<String, Object>> listFutureMeetingByProjectIdForMobile(int projectId) {
	DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd"); 
	String sql = "select pm.begin_time as beginTime,pm.id as meetingId,pm.title,pm.place from tbl_project_meeting pm where pm.tbl_project_id =:projectId and DATE_FORMAT(pm.begin_time,'%Y-%m-%d') >= '"+DateTime.now().toString(format)+"' order by pm.created_time desc";
	Query  query = createSQLQuery(sql);
	query.setParameter("projectId", projectId);
	return query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
}

@Override
public Map<String, Object> listCompletedMeetingByProjectIdForMobile(
		int projectId, int pageIndex, int pageMax) {
	DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd"); 
	String sql = "select pm.begin_time as beginTime,pm.id as meetingId,pm.title,pm.place from tbl_project_meeting pm where pm.tbl_project_id =:projectId and DATE_FORMAT(pm.begin_time,'%Y-%m-%d') < '"+DateTime.now().toString(format)+"' order by pm.created_time desc";
	Query query = createSQLQuery(sql);
	query.setParameter("projectId", projectId);
	query.setFirstResult((pageIndex-1)*10);
	query.setMaxResults(pageMax);
	@SuppressWarnings("unchecked")
	List<Map<String, Object>> list = query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("pageIndex", pageIndex);
	map.put("list", list);
	return map;
}

@Override
public void updateMeetingUser(int meetingId, int[] ids) {
	String sql = "delete from tbl_user_meeting where tbl_meeting_id = " + meetingId;
	SQLQuery query = getSession().createSQLQuery(sql);
	query.executeUpdate();
	if(ids == null || ids.length == 0) {
		  return;
		}
		final String sqlm = "insert into tbl_user_meeting(tbl_user_id,tbl_meeting_id, tbl_role_code) values";
		for(int id : ids) {
		  String v = "(%s, %d, '%s')";
		  String addSql = sqlm + String.format(v, id, meetingId, RoleConstant.MEETING_MEMBER);
		  SQLQuery addQuery = getSession().createSQLQuery(addSql);
		  addQuery.executeUpdate();
		}
}

}
