package com.ks0100.wp.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.HibernateDaoImpl;
import com.ks0100.common.util.BeanMapper;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.constant.BusinessConstant.RoleConstant;
import com.ks0100.wp.dao.OrganizationDao;
import com.ks0100.wp.entity.Organization;

@Repository
@SuppressWarnings("unchecked")
public class OrganizationDaoImpl extends HibernateDaoImpl<Organization, Integer> implements OrganizationDao {

	@Override
	public List<Organization> listOrganizationsByUser(int userId) {

		String sql = "select o.uuid, o.id ,o.name,o.description,o.contacter,o.phone,o.logo,o.invitation_code \"invitationCode\", o.invitation_enabled \"invitationEnabled\"" + " "
				+ "from tbl_organization o, tbl_user_organization uo where o.id=uo.tbl_organization_id and uo.enabled=1 and uo.tbl_user_id=:userId and o.enabled = 1 order by o.id";

		Query query = getSession().createSQLQuery(sql);
		query.setParameter("userId", userId);
		List<Map<String, Object>> list = query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		List<Organization> orgList = new ArrayList<Organization>();
		if (list != null) {
			orgList = BeanMapper.mapList(list, Organization.class);
			/*
			 * for (Map<String, Object> map : list) {
			 * orgList.add(BeanMapper.map(map,Organization.class)); }
			 */
		}

		return orgList;

	}

	public Organization findOrganizationByInvitationCode(String invitationCode) {
		String hql = " select new Organization(o.id,o.name, o.description,o.contacter,o.phone,o.logo,o.invitationEnabled,o.invitationCode,o.inviter ,"
				+ " o.uuid ) from Organization as o where o.invitationCode = :invitationCode ";
		Query query = getSession().createQuery(hql);
		query.setParameter("invitationCode", invitationCode);
		return (Organization) query.uniqueResult();
	}

	public boolean addOrganizationUser(int userId, int oid, String roleCode) {

		String sql = "insert into tbl_user_organization (tbl_user_id,tbl_organization_id,tbl_role_code) values (?,?,?)";
		int i = this.createSQLQuery(sql, userId, oid, roleCode).executeUpdate();
		return i > 0 ? true : false;

	}

	@Override
	public List<Map<String, Object>> findUserList(int orgid) {

		String sql = "select u.id,u.account , u.active, u.name ,tbl_role_code\"role\",u.password ,u.logo ,u.mobile ,u.position , u.gender , s.status_content \"genderTxt\" "
				+ "from tbl_user_organization uo , tbl_user u  ," + " tbl_sys_status s where  u.gender=s.status_id and u.enabled=1 and uo.tbl_user_id=u.id and uo.enabled = 1 and uo.tbl_organization_id=:orgid";

		Query query = getSession().createSQLQuery(sql);
		query.setParameter("orgid", orgid);
		List<Map<String, Object>> list = query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();

		return list;
	}

	@Override
	public boolean existOrgUser(int orgId, int userId) {
		String sql = "select count(*) from tbl_user_organization where tbl_user_id=:userId and tbl_organization_id=:orgId and enabled = 1";
		Query query = getSession().createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("orgId", orgId);
		int count = ((Number) query.uniqueResult()).intValue();
		return count > 0 ? true : false;
	}

	@Override
	public boolean delOrgUser(int orgId, int userId) {
		String sql = "update tbl_user_organization set enabled = 0  where tbl_user_id=? and tbl_organization_id=?";
		int i = this.createSQLQuery(sql, userId, orgId).executeUpdate();
		String sqlString1 = "update tbl_user_project set enabled = 0 where tbl_user_id =? and tbl_project_id in (select p.id from tbl_project p where p.tbl_organization_id = ?)";
		this.createSQLQuery(sqlString1, userId, orgId).executeUpdate();
		String sqlString2 = "delete from tbl_user_team where tbl_user_id = ? and tbl_team_id in (select t.id from tbl_team t where t.tbl_organization_id = ?)";
		this.createSQLQuery(sqlString2, userId, orgId).executeUpdate();
		return i > 0 ? true : false;
	}

	@Override
	public void setAdmin(int orgId, int userId) {
		String sql = "update tbl_user_organization set tbl_role_code = '" + RoleConstant.ORG_ADMIN + "' where tbl_user_id=? and tbl_organization_id=?";
		this.createSQLQuery(sql, userId, orgId).executeUpdate();
	}

	@Override
	public void cancelAdmin(int orgId, int userId) {
		String sql = "update tbl_user_organization set tbl_role_code = '" + RoleConstant.ORG_MEMBER + "' where tbl_user_id=? and tbl_organization_id=?";
		this.createSQLQuery(sql, userId, orgId).executeUpdate();
	}

	@Override
	public int exitOrg(int orgId, int userId) {
		String sql = "update tbl_user_organization set enabled = 0  where tbl_user_id=? and tbl_organization_id=?";
		int i = this.createSQLQuery(sql, userId, orgId).executeUpdate();
		return i;
	}

	public List<Map<String, Object>> findOrganizationStatistics_user(int orgId, int loadType) {
		String dateCondition = "", startDate = "";
		DateTime now = new DateTime();
		switch (loadType) {
		case 0:
			startDate = now.minusDays(7).toString("yyyy-MM-dd");
			dateCondition = " and (t.due_time between '" + startDate + "' and curdate() or t.complete_time between '" + startDate + "' and curdate())";
			break;
		case 1:
			startDate = now.plusDays(1).minusMonths(1).toString("yyyy-MM-dd"); // DateUtil.formatDate(DateUtil.preMonthDate());
			dateCondition = " and (t.due_time between '" + startDate + "' and curdate() or t.complete_time between '" + startDate + "' and curdate())";
			break;
		default:
			break;
		}
		String sql = "select temp.*, t.id taskId, t.status taskStatus, t.created_time createdTime, t.due_time dueTime, t.complete_time completeTime from"
				+ " (select u.id userId, u.name userName from tbl_user u right join tbl_user_organization uo on u.id = uo.tbl_user_id where uo.tbl_organization_id = :orgId and uo.tbl_role_code <> :orgSuperviser and u.enabled = 1 union"
				+ " select u.id userId, u.name userName from tbl_user u right join tbl_user_team uTeam on u.id = uTeam.tbl_user_id and uTeam.tbl_team_id in (select id from tbl_team where tbl_organization_id = :orgId and enabled = 1) where u.enabled = 1)"
				+ " temp left join tbl_user_task ut on temp.userId = ut.tbl_user_id and ut.tbl_role_code = :roleCode left join tbl_task t on ut.tbl_task_id = t.id and t.enabled = 1 and t.tbl_project_id in (select id from tbl_project where tbl_organization_id = :orgId and enabled = 1)"
				+ dateCondition;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgSuperviser", BusinessConstant.RoleConstant.ORG_SUPERVISER);
		map.put("roleCode", BusinessConstant.RoleConstant.TASK_EXECUTOR);
		map.put("orgId", orgId);
		Query query = createSQLQuery(sql, map);
		return (List<Map<String, Object>>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}

	public List<Map<String, Object>> findOrganizationStatistics_project(int orgId, int loadType) {
		String dateCondition = "", startDate = "";
		DateTime now = new DateTime();
		switch (loadType) {
		case 0:
			startDate = now.minusDays(7).toString("yyyy-MM-dd");
			dateCondition = " and (t.due_time between '" + startDate + "' and curdate() or t.complete_time between '" + startDate + "' and curdate())";
			break;
		case 1:
			startDate = now.plusDays(1).minusMonths(1).toString("yyyy-MM-dd");
			dateCondition = " and (t.due_time between '" + startDate + "' and curdate() or t.complete_time between '" + startDate + "' and curdate())";
			break;
		default:
			break;
		}
		String sql = "select p.id projectId, p.name projectName, t.id taskId, t.status taskStatus, t.created_time createdTime, t.due_time dueTime, t.complete_time completeTime from tbl_project p "
				+ " left join tbl_task t on p.id = t.tbl_project_id and p.enabled = 1 and t.enabled = 1" + dateCondition + " where tbl_organization_id = :orgId";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgId", orgId);
		Query query = createSQLQuery(sql, map);
		return (List<Map<String, Object>>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}

	public List<Map<String, Object>> findOrganizationStatistics_team(int orgId,int loadType) {
		String dateCondition = "", startDate = "";
		DateTime now = new DateTime();
		switch (loadType) {
		case 0:
			startDate = now.minusDays(7).toString("yyyy-MM-dd");
			dateCondition = " and (t.due_time between '" + startDate + "' and curdate() or t.complete_time between '" + startDate + "' and curdate())";
			break;
		case 1:
			startDate = now.plusDays(1).minusMonths(1).toString("yyyy-MM-dd");
			dateCondition = " and (t.due_time between '" + startDate + "' and curdate() or t.complete_time between '" + startDate + "' and curdate())";
			break;
		default:
			break;
		}
		String sql = "select team.id teamId, team.name teamName, t.id taskId, t.status taskStatus, t.created_time createdTime, t.due_time dueTime, t.complete_time completeTime "
				+ "from tbl_team team left join tbl_project_team pt on team.id = pt.tbl_team_id "
				+ "left join tbl_project p on pt.tbl_project_id = p.id and p.enabled = 1 left join tbl_task t on p.id = t.tbl_project_id and t.enabled = 1 "
				+ dateCondition
				+ "where team.tbl_organization_id = :orgId and team.enabled = 1 ";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgId", orgId);
		Query query = createSQLQuery(sql, map);
		return (List<Map<String, Object>>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}

	@Override
	public void setAdministrator(int orgId, int userId, String roleCode) {
		String sql = "update tbl_user_organization set tbl_role_code = '" + roleCode + "' where tbl_user_id=? and tbl_organization_id=?";
		this.createSQLQuery(sql, userId, orgId).executeUpdate();
	}

	@Override
	public boolean isExistOrgUser(int orgId, int userId) {
		String sql = "select count(*) from tbl_user_organization where tbl_user_id=:userId and tbl_organization_id=:orgId and enabled = 0";
		Query query = getSession().createSQLQuery(sql);
		query.setParameter("userId", userId);
		query.setParameter("orgId", orgId);
		int count = ((Number) query.uniqueResult()).intValue();
		return count > 0 ? true : false;
	}

	@Override
	public boolean addOrganizationUserExist(int userId, int orgId) {
		String sql = "update tbl_user_organization set enabled = 1  where tbl_user_id=? and tbl_organization_id=?";
		int i = this.createSQLQuery(sql, userId, orgId).executeUpdate();
		return i>0?true:false;
	}


}
