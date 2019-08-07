package com.ks0100.wp.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks0100.common.util.PathUtil;
import com.ks0100.common.util.PictureUtil;
import com.ks0100.common.util.TimeUtil;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.dao.OrganizationDao;
import com.ks0100.wp.dao.ProjectDao;
import com.ks0100.wp.entity.Organization;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.Team;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.service.OrganizationService;

@Service
public class OrganizationServiceImpl implements OrganizationService {
	@Autowired
	private OrganizationDao organizationDao;
	
	@Autowired
	private ProjectDao projectDao;
		
	public List<Organization> listOrganizationsByUser(int userId){
		return organizationDao.listOrganizationsByUser(userId);
	}

	
	@Override
	public boolean createOrganization(Organization org,int userId) throws Exception {
	//	ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		int i = (int) (Math.random()*6+1);//1-6随机数
		
		String path = "/static/images/org_default/organizeLogo" + i + ".jpg";
		
		File file = new File(PathUtil.ABSOLUTE_WEB_PATH+path);
		FileInputStream nowIn = null;
		nowIn = new FileInputStream(file);
		
		PictureUtil p=new PictureUtil();
	    String logo =p.changeToBASE64EncoderStr(nowIn);
	    
	    org.setLogo(logo);
	    
		org.setCreatedBy(userId);
		org.setUpdatedBy(userId);
		organizationDao.save(org);
		boolean f=false;
		if(org.getId()>0&&addUser(org.getId(),userId,BusinessConstant.RoleConstant.ORG_ADMIN)==1){
			f=true;
		}
		return f;
	}

	@Override
	public Organization findOrganizationById(int id) {
		return organizationDao.get(id); 
	}

	public boolean existOrgUser(int orgId,int userId){
		return organizationDao.existOrgUser(orgId, userId);
	}
	
	@Override
	public void updateOrganization(Organization org) {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		org.setUpdatedBy(user.getUserId());
		organizationDao.update(org);	
	}

	@Override
	public List<Map<String,Object>> findUserList(int id) {
		return organizationDao.findUserList(id);
	}

	@Override
	public boolean existCode(String invitationCode) {
		List<Organization> list= organizationDao.findBy("invitationCode", invitationCode);
		boolean f=false;
		if(list!=null&&!list.isEmpty()){
			f=true;
		}
		return f;
	}

	@Override
	public Organization findByCode(String invitationCode) {
		return organizationDao.findOrganizationByInvitationCode(invitationCode);
	//	return organizationDao.findUniqueBy("invitationCode", invitationCode);
	}

	@Override
	public String findCode(int id) {
		Organization o = findOrganizationById(id);
		String s=null;
		if(o!=null){
			s=o.getInvitationCode();
		}
		return s;
	}

	@Override
	public int addUser(int orgId, int userId, String roleCode) {
		if (!organizationDao.existOrgUser(orgId, userId)) {
			if (organizationDao.isExistOrgUser(orgId, userId)) {
				if (organizationDao.addOrganizationUserExist(userId, orgId)) {
					return 1; // 添加成功
				} else {
					return 2; // 添加失败
				}
			} else {
				if (organizationDao.addOrganizationUser(userId, orgId, roleCode)) {
					return 1; // 添加成功
				} else {
					return 2; // 添加失败
				}
			}
		} else {
			return 0; // 数据已存在
		}
	}


	@Override
	public boolean existUserOrg(int userId, int orgId) {
		return organizationDao.existOrgUser(orgId, userId);
	}


	@Override
	public boolean delOrgUser(int orgId, int userId) {
		return organizationDao.delOrgUser(orgId, userId);
	}


	@Override
	public void setAdmin(int orgId, int userId) {
		organizationDao.setAdmin(orgId, userId);
	}


	@Override
	public void cancelAdmin(int orgId, int userId) {
		organizationDao.cancelAdmin(orgId, userId);
	}


	@Override
	public boolean exit(int orgId) {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		return organizationDao.exitOrg(orgId, user.getUserId())==1?true:false;
	}


	@Override
	public Organization findOrgByuuid(String uuid) {
		return organizationDao.findUniqueBy("uuid", uuid);
	}


	public List<User> findOrganizationStatistics_user(int orgId, int loadType) {
		List<User> users = new ArrayList<User>();
		List<Map<String, Object>> statisticsInfo = organizationDao.findOrganizationStatistics_user(orgId, loadType);
		Map<Integer, User> userMap = new HashMap<Integer, User>();
		for (Map<String, Object> map : statisticsInfo) {
			if(userMap.get((Integer)map.get("userId")) == null){
				User user = new User();
				user.setUserId((Integer)map.get("userId"));
				user.setName((String)map.get("userName"));
				userMap.put(user.getUserId(), user);
				users.add(user);
			}
			if(map.get("taskId") != null){
				User user = userMap.get((Integer)map.get("userId"));
				user.setTaskCount(user.getTaskCount() + 1);
				if(!BusinessConstant.TaskConstant.TASK_DONE.equals((String)map.get("taskStatus"))){
					user.setNoTaskCount(user.getNoTaskCount() + 1);
					if (map.get("dueTime") != null && new DateTime((Date)map.get("dueTime")).plusDays(1).isBeforeNow()) {
						user.setTardyTaskCount(user.getTardyTaskCount() + 1);
					}
				}
			}
		}
		return users;
	}


	public List<Project> findOrganizationStatistics_project(int orgId, int loadType) {
		List<Project> pros = new ArrayList<Project>();
		List<Map<String, Object>> statisticsInfo = organizationDao.findOrganizationStatistics_project(orgId, loadType);
		Map<Integer, Project> proMap = new HashMap<Integer, Project>();
		for (Map<String, Object> map : statisticsInfo) {
			if(proMap.get((Integer)map.get("projectId")) == null){
				Project project = new Project();
				project.setProjectId((Integer)map.get("projectId"));
				project.setName((String)map.get("projectName"));
				proMap.put(project.getProjectId(), project);
				pros.add(project);
			}
			if(map.get("taskId") != null){
				Project project = proMap.get((Integer)map.get("projectId"));
				project.setTaskCount(project.getTaskCount() + 1);
				if(!BusinessConstant.TaskConstant.TASK_DONE.equals((String)map.get("taskStatus"))){
					project.setNoTaskCount(project.getNoTaskCount() + 1);
					if (map.get("dueTime") != null && new DateTime((Date)map.get("dueTime")).plusDays(1).isBeforeNow()) {
						project.setTardyTaskCount(project.getTardyTaskCount() + 1);
					}
				}
			}
		}
		return pros;
	}

	public List<Team> findOrganizationStatistics_team(int orgId, int loadType) {
		List<Team> teams = new ArrayList<Team>();
		List<Map<String, Object>> statisticsInfo = organizationDao.findOrganizationStatistics_team(orgId, loadType);
		Map<Integer, Team> teamMap = new HashMap<Integer, Team>();
		for (Map<String, Object> map : statisticsInfo) {
			if(teamMap.get((Integer)map.get("teamId")) == null){
				Team team = new Team();
				team.setTeamId((Integer)map.get("teamId"));
				team.setName((String)map.get("teamName"));
				teamMap.put(team.getTeamId(), team);
				teams.add(team);
			}
			if(map.get("taskId") != null){
				Team team = teamMap.get((Integer)map.get("teamId"));
				team.setTaskCount(team.getTaskCount() + 1);
				if(!BusinessConstant.TaskConstant.TASK_DONE.equals((String)map.get("taskStatus"))){
					team.setNoTaskCount(team.getNoTaskCount() + 1);
					if (map.get("dueTime") != null && new DateTime((Date)map.get("dueTime")).plusDays(1).isBeforeNow()) {
						team.setTardyTaskCount(team.getTardyTaskCount() + 1);
					}
				}
			}
		}
		return teams;
	}

	@Override
	public void deleteOrg(Organization org) {
		org.setEnabled(false);
	}


	@Override
	public void setAdministrator(int orgId, int userId, String roleCode) {
		organizationDao.setAdministrator(orgId, userId, roleCode);
	}


	@Override
	public List<User> findOrganizationTimeStatistics_user(int orgId, int loadType) {
		List<User> users = new ArrayList<User>();
		List<Map<String, Object>> statisticsInfo = organizationDao.findOrganizationStatistics_user(orgId, loadType);
		Map<Integer, User> userMap = new HashMap<Integer, User>();
		for (Map<String, Object> map : statisticsInfo) {
			if(userMap.get((Integer)map.get("userId")) == null){
				User user = new User();
				user.setUserId((Integer)map.get("userId"));
				user.setName((String)map.get("userName"));
				userMap.put(user.getUserId(), user);
				users.add(user);
			}
			if(map.get("taskId") != null){
				User user = userMap.get((Integer)map.get("userId"));
				DateTime createdDateTime = new DateTime(map.get("createdTime")); 
				if(map.get("dueTime") != null){
					DateTime dueDateTime = new DateTime(map.get("dueTime")); 
					int planday = TimeUtil.daysBetween(createdDateTime, dueDateTime);
					user.setPlanDay(user.getPlanDay()+planday);
				}
				if(map.get("completeTime") != null){
					DateTime completeDateTime = new DateTime(map.get("completeTime")); 
					int realityday = TimeUtil.daysBetween(createdDateTime, completeDateTime); 
					user.setRealityDay(user.getRealityDay()+realityday);
				}
			}
		}
		return users;
	}


	@Override
	public List<Project> findOrganizationTimeStatistics_project(int orgId, int loadType) {
		List<Project> pros = new ArrayList<Project>();
		List<Map<String, Object>> statisticsInfo = organizationDao.findOrganizationStatistics_project(orgId, loadType);
		Map<Integer, Project> proMap = new HashMap<Integer, Project>();
		for (Map<String, Object> map : statisticsInfo) {
			if(proMap.get((Integer)map.get("projectId")) == null){
				Project project = new Project();
				project.setProjectId((Integer)map.get("projectId"));
				project.setName((String)map.get("projectName"));
				proMap.put(project.getProjectId(), project);
				pros.add(project);
			}
			if(map.get("taskId") != null){
				Project project = proMap.get((Integer)map.get("projectId"));
				DateTime createdDateTime = new DateTime(map.get("createdTime")); 
				if(map.get("dueTime") != null){
					DateTime dueDateTime = new DateTime(map.get("dueTime")); 
					int planday = TimeUtil.daysBetween(createdDateTime, dueDateTime);
					project.setPlanDay(project.getPlanDay()+planday);
				}
				if(map.get("completeTime") != null){
					DateTime completeDateTime = new DateTime(map.get("completeTime")); 
					int realityday = TimeUtil.daysBetween(createdDateTime, completeDateTime); 
					project.setRealityDay(project.getRealityDay()+realityday);
				}
			}
		}
		return pros;
	}


	@Override
	public List<Team> findOrganizationTimeStatistics_team(int orgId, int loadType) {
		List<Team> teams = new ArrayList<Team>();
		List<Map<String, Object>> statisticsInfo = organizationDao.findOrganizationStatistics_team(orgId, loadType);
		Map<Integer, Team> teamMap = new HashMap<Integer, Team>();
		for (Map<String, Object> map : statisticsInfo) {
			if(teamMap.get((Integer)map.get("teamId")) == null){
				Team team = new Team();
				team.setTeamId((Integer)map.get("teamId"));
				team.setName((String)map.get("teamName"));
				teamMap.put(team.getTeamId(), team);
				teams.add(team);
			}
			if(map.get("taskId") != null){
				Team team = teamMap.get((Integer)map.get("teamId"));
				DateTime createdDateTime = new DateTime(map.get("createdTime")); 
				if(map.get("dueTime") != null){
					DateTime dueDateTime = new DateTime(map.get("dueTime")); 
					int planday = TimeUtil.daysBetween(createdDateTime, dueDateTime);
					team.setPlanDay(team.getPlanDay()+planday);
				}
				if(map.get("completeTime") != null){
					DateTime completeDateTime = new DateTime(map.get("completeTime")); 
					int realityday = TimeUtil.daysBetween(createdDateTime, completeDateTime); 
					team.setRealityDay(team.getRealityDay()+realityday);
				}
			}
		}
		return teams;
	}


	@Override
	public boolean JustOneAdminForPro(int userId, int orgId) {
		List<Map<String,Object>> proListByOrgId = projectDao.findProAdminByOrganizationId(orgId);
		List<Map<String,Object>> proListByUserId = projectDao.findProByUserId(userId);
		if(!proListByOrgId.isEmpty()&&!proListByUserId.isEmpty()){
			for(Map<String,Object> orgMap:proListByOrgId){
				for(Map<String,Object> userMap:proListByUserId){
					if(orgMap.get("id").equals(userMap.get("tbl_project_id"))){
						BigInteger number = (BigInteger) orgMap.get("number");
						if(number.intValue()==1){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
