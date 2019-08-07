package com.ks0100.wp.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.HibernateDaoImpl;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.dao.TeamDao;
import com.ks0100.wp.entity.Team;

@Repository
public class TeamDaoImpl extends HibernateDaoImpl<Team, Integer> implements TeamDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findByOrganizationId(int id, int userId) {
		// TODO Auto-generated method stub
		String sql = "select t.id teamId,t.name teamName,u.id userId, u.name userName , ut.tbl_role_code userRole ," +
				"(select count(1) from tbl_project_team  pt where pt.tbl_team_id=t.id  )pnum, u.logo  " +
				"from tbl_user_team ut ,tbl_user u , tbl_user_organization uo ,tbl_team t" +
				" where " +
				"u.id=ut.tbl_user_id and u.id=uo.tbl_user_id " +
				"and uo.tbl_organization_id=:orgId and ut.tbl_team_id in " +
				"(select tbl_team_id from tbl_user_team where tbl_user_id =:userId) " +
				"and t.tbl_organization_id=:orgId and t.enabled=1 and ut.tbl_team_id=t.id order by t.id";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("orgId", id);	
		query.setParameter("userId", userId);
		
		return query.setResultTransformer(
				CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}

	@Override
	public void addTeam(Team team) {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		int id = user.getUserId();
		save(team);

		String sql = "insert into tbl_user_team (tbl_user_id,tbl_team_id,tbl_role_code) values (?,?,?)";
		this.createSQLQuery(sql, id, team.getTeamId(), "TEAM_ADMIN")
				.executeUpdate();
	}

	@Override
	public void updateTeam(Team team) {
		// TODO Auto-generated method stub
		update(team);
	}

	@Override
	public Team findByTeamId(int id) {
		// TODO Auto-generated method stub
		return get(id);
	}

	@Override
	public int exit(int id) {
		// TODO Auto-generated method stub
		String sql = "delete from tbl_user_team where tbl_team_id=:id";
		
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("id", id);
		return query.executeUpdate();
	}

	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub
		Team t = findByTeamId(id);
		delete(t);
		String userSql = "delete from tbl_user_team where tbl_team_id=:id";
		SQLQuery userQuery = getSession().createSQLQuery(userSql);
		userQuery.setParameter("id", id);
		userQuery.executeUpdate();
		
		
		String proSql = "delete from tbl_project_team where tbl_team_id=:id";
		SQLQuery proQuery = getSession().createSQLQuery(proSql);
		proQuery.setParameter("id", id);
		proQuery.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findUsersByTeamId(int id) {
		// TODO Auto-generated method stub
		String sql = "select u.id,u.name,u.logo,ut.tbl_role_code roleCode ,t.tbl_organization_id orgId from tbl_user u  ,tbl_user_team ut, tbl_team t where u.id=ut.tbl_user_id  and ut.tbl_team_id =:id and u.enabled = 1 and t.id = ut.tbl_team_id " ;
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("id", id);	
	
		return query.setResultTransformer(
				CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findTeamsByProId(int proId) {

		String sql = "select t.tbl_organization_id orgId, t.name teamName,t.id teamId,t.logo teamLogo,oo.id userId,oo.logo userLogo,oo.name userName " +
				"from tbl_project_team pt, tbl_team t LEFT JOIN " +
				"(select u.id,u.name,u.logo ,ut.tbl_team_id from tbl_user u ,tbl_user_team ut where ut.tbl_user_id = u.id)oo " +
				"on oo.tbl_team_id =t.id where t.id = pt.tbl_team_id and pt.tbl_project_id = :proId and t.enabled = 1";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("proId", proId);
		List<Map<String, Object>> list = query.setResultTransformer(
				CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		return list;
		}

	@Override
	public void addPro(int teamId, int proId) {
		// TODO Auto-generated method stub
		String sql = "insert into tbl_project_team (tbl_project_id,tbl_team_id) values (?,?)";
		this.createSQLQuery(sql, proId, teamId)
		.executeUpdate();
	}

	@Override
	public void addUser(int userId, int teamId) {
		String sql = "insert tbl_user_team (tbl_user_id,tbl_team_id,tbl_role_code) values (?,?,?)";
		this.createSQLQuery(sql, userId,teamId,"TEAM_MEMBER").executeUpdate();
	}

	@Override
	public void delUser(int userId, int teamId) {
		String sql = "delete from tbl_user_team where tbl_user_id=? and tbl_team_id=?";
		createSQLQuery(sql, userId, teamId).executeUpdate();
	}

	@Override
	public void setOrRemoveAdmin(int teamId, int userId, String rolecode) {
		String sql = "update tbl_user_team set tbl_role_code = ? where tbl_user_id=? and tbl_team_id=?";
		this.createSQLQuery(sql, rolecode, userId, teamId).executeUpdate();
	}

}
