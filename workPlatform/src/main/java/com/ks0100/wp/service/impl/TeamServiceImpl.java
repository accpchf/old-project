package com.ks0100.wp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.dao.OrganizationDao;
import com.ks0100.wp.dao.TeamDao;
import com.ks0100.wp.entity.Team;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.service.TeamService;

@Service
public class TeamServiceImpl implements TeamService {

	@Autowired
	private TeamDao teamDao;
	@Autowired
	private OrganizationDao organizationDao;
	@Override
	public List<Team> listTeamByOrganizationId(int id) {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		List<Map<String, Object>> list = teamDao.findByOrganizationId(id, user.getUserId());

		List<Team> teamList = new ArrayList<Team>();

		for (Map<String, Object> map : list) {
			Team t = new Team();
			t.setTeamId((Integer) map.get("teamId"));
			t.setName((String) map.get("teamName"));
			t.setProjectNum(Integer.parseInt(map.get("pnum").toString()));
			if (!teamList.contains(t)) {
				teamList.add(t);
			}
		}

		for (Team team : teamList) {
			for (Map<String, Object> map : list) {
				if ((Integer) map.get("teamId") == team.getTeamId() && (Integer) map.get("userId") != null) {
					User u = new User();
					u.setUserId((Integer) map.get("userId"));
					u.setName((String) map.get("userName"));
					u.setUserRole((String) map.get("userRole"));
					u.setLogo((String) map.get("logo"));
					team.getUsers().add(u);
				}
			}
		}
		
		return teamList;
	}

	@Override
	public void addTeam(Team team) {
		teamDao.addTeam(team);
	}

	@Override
	public void updateTeamName(Team team) {
		teamDao.updateTeam(team);
	}

	@Override
	public Team findByTeamId(int id) {
		return teamDao.findByTeamId(id);
	}

	@Override
	public boolean exit(int id) {
		return teamDao.exit(id) == 1 ? true : false;
	}

	@Override
	public void delete(int id) {
		teamDao.delete(id);
	}

	/**
	 * 根据项目查找项目下面的团队和团队中的人员
	 */

	@Override

	public List<User> listUserByTeamId(int id) {
		List<Map<String,Object>> list = teamDao.findUsersByTeamId(id);
		
		List<User> users = new ArrayList<User>();
		
		for(Map<String,Object> map:list)
		{
			if((Integer)map.get("id")!=null)
			{
				User u = new User();
				u.setUserId((Integer)map.get("id"));
				u.setName((String)map.get("name"));
				u.setLogo((String)map.get("logo"));
				u.setUserRole((String)map.get("roleCode"));
				if(map.get("orgId") != null && organizationDao.existOrgUser((Integer)map.get("orgId"), u.getUserId()) && !users.contains(u)){
					users.add(u);
				}
				
			}
		}
		
		return users;
	}


	public List<Team> listTeamByProId(int proId) {
		List<Map<String, Object>> list = teamDao.findTeamsByProId(proId);

		List<Team> teamList = new ArrayList<Team>();

		for (Map<String, Object> map : list) {
			Team t = new Team();
			t.setTeamId((Integer) map.get("teamId"));
			t.setName((String) map.get("teamName"));
			if (!teamList.contains(t)) {
				teamList.add(t);
			}
		}

		for (Team team : teamList) {
			for (Map<String, Object> map : list) {
				if ((Integer) map.get("teamId") == team.getTeamId() && (Integer) map.get("userId") != null) {
					User u = new User();
					u.setUserId((Integer) map.get("userId"));
					u.setName((String) map.get("userName"));
					u.setLogo((String) map.get("userLogo"));
					if(map.get("orgId") != null && organizationDao.existOrgUser((Integer)map.get("orgId"), u.getUserId())){
						team.getUsers().add(u);
					}
				}
			}
		}
		
		return teamList;
	}

	@Override
	public void addPro(int teamId, int proId) {
		teamDao.addPro(teamId, proId);
	}

	@Override
	public void addUser(int teamId, int userId) {
		teamDao.addUser(userId, teamId);
	}

	@Override
	public void delUser(int teamId, int userId) {
		teamDao.delUser(userId, teamId);
	}

	@Override
	public void setOrRemoveAdmin(int teamId, int userId, String rolecode) {
		teamDao.setOrRemoveAdmin(teamId, userId, rolecode);
	}

}
