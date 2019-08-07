package com.ks0100.wp.dao;

import java.util.List;
import java.util.Map;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.Team;

public interface TeamDao extends SimpleHibernateDao<Team,Integer>{

	List<Map<String, Object>> findByOrganizationId(int id, int UserId);

	/**
	 * 
	 * @param pId
	 * @return
	 */
	List<Map<String, Object>> findTeamsByProId(int proId);

	void addTeam(Team team);

	void updateTeam(Team team);

	Team findByTeamId(int id);

	int exit(int id);

	void delete(int id);

	List<Map<String, Object>> findUsersByTeamId(int id);
	
	void addPro(int teamId,int proId);
	
	void addUser(int userId,int teamId);
	
	void delUser(int userId,int teamId);
	
	/**
	 * 设置团队管理员权限
	 * @param teamId
	 * @param userId
	 */
	void setOrRemoveAdmin(int teamId, int userId, String rolecode);
}
