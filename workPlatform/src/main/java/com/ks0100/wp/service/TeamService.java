package com.ks0100.wp.service;

import java.util.List;

import com.ks0100.wp.entity.Team;
import com.ks0100.wp.entity.User;

public interface TeamService {

	/**
	 * 根据项目Id查询团队及团队中的人员
	 * 
	 * @param proId
	 * @return
	 */
	List<Team> listTeamByProId(int proId);

	List<Team> listTeamByOrganizationId(int id);

	void addTeam(Team team);

	void updateTeamName(Team team);

	Team findByTeamId(int id);

	boolean exit(int id);

	void delete(int id);

	List<User> listUserByTeamId(int id);
	
	void addPro(int teamId,int proId);
	
	void addUser(int teamId,int userId);
	
	void delUser(int teamId,int userId);
	/**
	 * 设置团队的管理员角色
	 * @param teamId
	 * @param UserId
	 */
	void setOrRemoveAdmin(int teamId, int userId, String rolecode);

}
