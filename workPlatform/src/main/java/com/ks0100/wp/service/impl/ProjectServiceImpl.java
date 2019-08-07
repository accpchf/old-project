package com.ks0100.wp.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks0100.common.util.PictureUtil;
import com.ks0100.common.util.ReadPropertiesUtil;
import com.ks0100.common.util.TimeUtil;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.audit.AuditServiceAnnotation;
import com.ks0100.wp.audit.AuditServiceAnnotation.USEAGE_TYPE;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.constant.StatusEnums.ProjectSatus;
import com.ks0100.wp.dao.OrganizationDao;
import com.ks0100.wp.dao.ProjectDao;
import com.ks0100.wp.dao.TaskDao;
import com.ks0100.wp.dto.OrganizationDto;
import com.ks0100.wp.dto.ProjectDto;
import com.ks0100.wp.dto.UserDto;
import com.ks0100.wp.entity.Organization;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.Task;
import com.ks0100.wp.entity.TaskBoard;
import com.ks0100.wp.entity.Team;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.service.OrganizationService;
import com.ks0100.wp.service.ProjectService;
import com.ks0100.wp.service.TaskService;
import com.ks0100.wp.service.TeamService;
import com.ks0100.wp.service.UserByMobileService;

@Service
public class ProjectServiceImpl implements ProjectService {

	@Autowired
	private ProjectDao projectDao;
	@Autowired
	private TaskService taskService;
	@Autowired
	public OrganizationService organizationService;
	@Autowired
	public UserByMobileService userByMobileService;
	@Autowired
	private TeamService teamService;
	@Autowired
	private OrganizationDao organizationDao;
	@Autowired
	private TaskDao taskDao;
	
	private Logger log = Logger.getLogger(ProjectServiceImpl.class);
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> findAllProjectsByUser(int userId){
		Map<String, Object> map = new HashMap<String, Object>();
		ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
		List<Project>  userProjectList= listHomeProjects(user.getUserId());
	
		//个人项目
		List<Project> personProjects =new ArrayList<Project>();
		//常用项目
		List<Project> commonProjects =new ArrayList<Project>();
		//组织项目
		Map<Integer, Object> orgProjects = new HashMap<Integer, Object>();
		List<Organization> orgList=organizationService.listOrganizationsByUser(user.getUserId());
		
		for(Project p:userProjectList){
			if(p.isCommonUse() && !p.getStatus().equals(ProjectSatus.DELETE.getCode())){
				
				commonProjects.add(p);
			}
			if(p.getOrganization()==null){
				if(p.getStatus().equals(ProjectSatus.DOING.getCode())){
					personProjects.add(p);
				}
				
			}else{
				if(p.getStatus().equals(ProjectSatus.DOING.getCode())){
					int organizationId = p.getOrganization().getId();
					if(orgProjects.containsKey(organizationId)){
						Map<String, Object> map2= (Map<String, Object>)orgProjects.get(organizationId);
						List<Project> op = (List<Project>)map2.get("projectList");
						op.add(p);
						map2.put("projectList", op);

					}else{
						Map<String, Object> map2 = new HashMap<String, Object>();
						List<Project> projects = new ArrayList<Project>();
						projects.add(p);
						map2.put("org", p.getOrganization());
						map2.put("projectList", projects);
						orgProjects.put(p.getOrganization().getId(), map2);
					}
				}
			}
		}
		if(orgList!=null){
			for(Organization org:orgList){
				if(orgProjects.isEmpty() || !orgProjects.containsKey(org.getId())){
					Map<String, Object> map3 = new HashMap<String, Object>();
					map3.put("org", org);
					orgProjects.put(org.getId(), map3);
				}
			}
		}
		map.put("commonProjects", commonProjects);
		map.put("personProjects", personProjects);
		map.put("orgProjects", orgProjects);
		
		return map;
	}
	
	public Project findProjectBysql(int projectId){
		return projectDao.findProjectBysql(projectId);
	}
	public List<Project> listHomeProjects(int userId) {
		return projectDao.listHomeProjects(userId);
	}

	@Override
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00628,argType1="com.ks0100.wp.entity.Project")
	public boolean updateProject(Project p) {
		boolean flag = false;
		try{
			ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
			p.setUpdatedBy(user.getUserId());
			projectDao.saveOrUpdate(p);
			flag = true;
		}catch(Exception e){
			flag = false;
			log.error(e);
		}
		return flag;
	}

	@Override
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00627,argType1="com.ks0100.wp.entity.Project")
	public void createProject(Project pro) {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		pro.setCreatedBy(user.getUserId());
		pro.setUpdatedBy(user.getUserId());
		projectDao.save(pro);
		projectDao.saveUserPro(pro.getProjectId(), user.getUserId(),BusinessConstant.RoleConstant.PRJ_ADMIN);
		// 添加一个默认的任务板
		String boardName = ReadPropertiesUtil.getStringContextProperty("task_board_name");
		TaskBoard board = new TaskBoard();
		board.setName(boardName.replaceAll("\\s+", ""));
		taskService.addTaskBoard(pro.getProjectId(), board);
	}

	@Override
	public Project findProjectById(int proId) {
		return projectDao.get(proId);
	}

	@Override
	public void updateProCommonUse(int proId) {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		projectDao.updateProjectIsCommonUse(proId, user.getUserId());

	}

	@Override
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00641,argType1="com.ks0100.wp.entity.Project")
	public void deletePro(Project project) {
		project.setEnabled(false);
	}

	@Override
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00640,argIndex1=1,argIndex2 = 2,argIndex3 = 3)
	public void exitPro(User user,Project project, int type) {
		projectDao.exitPro(user.getUserId(),project.getProjectId());
	}

	@Override
	public String findCode(int pId) {
		Project pro = findProjectById(pId);
		String code = null;
		if (pro != null) {
			code = pro.getInvitationCode();
		}
		return code;
	}

	@Override
	public boolean existCode(String uuid) {
		List<Project> projects = projectDao.findBy("invitationCode", uuid);
		boolean isExist = false;
		if (projects != null && !projects.isEmpty()) {
			isExist = true;
		}
		return isExist;
	}

	public Map<String, Object> listProjectAllUser(int projectId) {
		Map<String, Object> tempMap = new HashMap<String, Object>();
		List<Map<String, Object>> users = projectDao.listProjectAllUser(projectId);
		for (Map<String, Object> map : users) {
			User user = new User();
			user.setUserId((Integer) map.get("id"));
			user.setName((String) map.get("name"));
			user.setLogo((String) map.get("logo"));
			user.setPrjRoleCode((String) map.get("prjRoleCode"));
			user.setUserEnabled((Boolean)map.get("userEnabled"));
			tempMap.put(String.valueOf(user.getUserId()), user);
		}
		return tempMap;
	}

	public Map<String, Object> listProjectUser(int projectId) {
		Map<String, Object> tempMap = new HashMap<String, Object>();
		List<Map<String, Object>> users = projectDao.listProjectUser(projectId);
		for (Map<String, Object> map : users) {
			User user = new User();
			user.setUserId((Integer) map.get("id"));
			user.setName((String) map.get("name"));
			user.setLogo((String) map.get("logo"));
			user.setPrjRoleCode((String) map.get("prjRoleCode"));
			if(map.get("orgId") != null){
				if (organizationDao.existOrgUser((Integer)map.get("orgId"), user.getUserId())) {
					tempMap.put(String.valueOf(user.getUserId()), user);
				}
			}else {
				tempMap.put(String.valueOf(user.getUserId()), user);
			}
		}
		return tempMap;
	}

	@Override
	public Project findByCode(String code) {
		return projectDao.findUniqueBy("invitationCode", code);
	}


	
	
	@Override
	public boolean eixstPro(int proId, int userId) {
		return projectDao.ExistUserPro(userId, proId);
	}

	@Override
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00636,argIndex1=1,argIndex2=2)
	public boolean removeTeamByPro(Team team , int proId) {
		return projectDao.removeTeamByPro(team.getTeamId(), proId);
	}

	@Override
	public boolean teamExistPro(int teamId, int proId) {
		return projectDao.teamExistPro(teamId, proId);
	}

	@Override
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00635,argIndex1=1,argIndex2=2)
	public void addTeamForPro(Team team, int proId) {
		projectDao.addTeamForPro(team.getTeamId(), proId);
	}

	@Override
	public List<Project> findProjectByOrganizationId(int orgId,String orgName) {
		List<Map<String,Object>> list = projectDao.findProjectByOrganizationId(orgId,orgName);
		List<Project> pro = new ArrayList<Project>();
		for(Map<String,Object> map:list)
		{
				if((Integer)map.get("id")!=null)
				{
					Project p = new Project();
					p.setProjectId((Integer)map.get("id"));
					p.setName((String)map.get("name"));
					p.setLogo((byte[])map.get("logo"));
					p.setLogoStr(PictureUtil.changeToBASE64EncoderStr(p.getLogo()));
					p.setStatus((String)map.get("status"));
					if(!pro.contains(p))
					{
						pro.add(p);
					}
				}	
		}
		
		return pro;
	}

	@Override
	public List<Project> findProByTeamId(int teamId) {
		List<Map<String,Object>> list = projectDao.findProjectByTeamId(teamId);
		
		
		List<Project> pros = new ArrayList<Project>();
		
		for(Map<String,Object> map:list)
		{	
			if((Integer)map.get("id")!=null)
			{
				Project p = new Project();
				p.setProjectId((Integer)map.get("id"));
				p.setLogo((byte[])map.get("logo"));
				p.setName((String)map.get("name"));
				p.setLogoStr(PictureUtil.changeToBASE64EncoderStr(p.getLogo()));
				if(!pros.contains(p))
				{
					pros.add(p);
				}
			}
		}
		return pros;
	}

	@Override
	public Project findProByuuid(String uuid) {
		return projectDao.findUniqueBy("uuid", uuid);
	}

	public Map<String, Object> findProjectStatistics(int projectId, int loadType) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(loadType == 0 || loadType == 2){
			List<Team> teams = teamService.listTeamByProId(projectId);
			// 项目团队数
			map.put("teams", teams.size());
		}
		if(loadType == 0 || loadType == 1 || loadType == 5){
			Project project = projectDao.findUniqueBy("projectId", projectId);
			List<Task> tasks = taskDao.findTasksByProject(projectId);
			if(project == null || project.getEndTime() == null) {
				map.put("days", null);
			}else {
				//long currentMillis = DateUtil.parseDate(DateUtil.currentDate()).getTime();
				//long endMillis = project.getEndTime().getTime();
				//long days = (endMillis - currentMillis) / (1000 * 60 * 60 * 24);
				 DateTime endTime = new DateTime( project.getEndTime());
				// 倒计时天数
				map.put("days", Days.daysBetween(new DateTime(), endTime).getDays());
			}
			int planDay = 0, realityday = 0;
			if(tasks != null && tasks.size() > 0 && !tasks.isEmpty()){
				for(Task task:tasks){
					DateTime createdDateTime = new DateTime(task.getCreatedTime()); 
					if(task.getDueTime() != null){
						DateTime dueDateTime = new DateTime(task.getDueTime()); 
						int planday1 = TimeUtil.daysBetween(createdDateTime, dueDateTime);
						planDay += planday1;
					}
					if(task.getCompleteTime() != null){
						DateTime completeDateTime = new DateTime(task.getCompleteTime()); 
						int realityday1 = TimeUtil.daysBetween(createdDateTime, completeDateTime); 
						realityday += realityday1;
					}
				}
			}
			map.put("planDay", planDay);
			map.put("realityday", realityday);
			
		}
		if(loadType == 0 || loadType == 3){
			Map<String, Object> taskStatistics = taskService.findTaskStatistics(projectId);
			// 任务完成状态统计
			map.put("taskStatistics", taskStatistics);
		}
		if(loadType == 0 || loadType == 4){
			Map<String, Object> weekStatistics = taskService.findTasksByWeek(projectId);
			// 本周进展
			map.put("weekStatistics", weekStatistics);
		}
		return map;
	}

	@Override
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00644,argIndex1=1,argIndex2=2)
	public void setAdmin(User user, int projectId) {
		projectDao.setAdmin(projectId, user.getUserId());
	}

	@Override
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00646,argIndex1=1,argIndex2=2)
	public void setSupervise(User user, int projectId) {
		projectDao.setSupervise(projectId, user.getUserId());
	}
	
	@Override
	public void setMember(User user, int projectId) {
		projectDao.setMember(projectId, user.getUserId());
	}

	public boolean ExistUserPro(User user, int prjId){
		return projectDao.ExistUserPro(user.getUserId(), prjId);
	}
	
	public boolean isExistUserPro(User user, int prjId){
		return projectDao.isExistUserPro(user.getUserId(), prjId);
	}
	
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00633,argIndex1=1,argIndex2=2,argIndex3=3)
	public boolean saveUserPro(User user, int prjId, String addForm){
		 return projectDao.saveUserPro(prjId, user.getUserId(),BusinessConstant.RoleConstant.PRJ_MEMBER);
	}
	
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00633,argIndex1=1,argIndex2=2,argIndex3=3)
	public boolean saveUserIfExist(User user, int prjId, String addForm){
		 return projectDao.saveUserIfExist(prjId, user.getUserId(),BusinessConstant.RoleConstant.PRJ_MEMBER);
	}

	@Override
	public List<Map<String,Object>> findProjectByStatus(int userId, String status, int orgId) {
		return projectDao.findProjectByStatus(userId, status, orgId);
	}

	@Override
	public Map<String, Object> findAllProjectsByUserForMobile(int userId,String status) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Project>  userProjectList= listHomeProjectsForMobile(userId,status);
	
		//个人项目
		List<ProjectDto> personProjects =new ArrayList<ProjectDto>();
		Map<String,Object> personMap = new HashMap<String, Object>();
 	    //组织项目
		List<OrganizationDto> orgProjects = new ArrayList<OrganizationDto>();
		List<Organization> orgList=organizationService.listOrganizationsByUser(userId);
		
		for(Organization o:orgList){
			List<ProjectDto> projectList = new ArrayList<ProjectDto>();
			for(Project p:userProjectList){
				if(p.getOrganization()==null){
					if(!personProjects.contains(p)){
						ProjectDto projectDto = new ProjectDto();
						projectDto.setProjectId(p.getProjectId());
						projectDto.setBeginTime(p.getBeginTime());
						projectDto.setEndTime(p.getEndTime());
						projectDto.setName(p.getName());
						projectDto.setStatus(p.getStatus());
						projectDto.setUuid(p.getUuid());
						personProjects.add(projectDto);
					}
				}else{
					if (o.getId() == p.getOrganization().getId()) {
						ProjectDto projectDto = new ProjectDto();
						projectDto.setProjectId(p.getProjectId());
						projectDto.setBeginTime(p.getBeginTime());
						projectDto.setEndTime(p.getEndTime());
						projectDto.setName(p.getName());
						projectDto.setStatus(p.getStatus());
						projectDto.setUuid(p.getUuid());
						if(!projectList.contains(projectDto)){
							projectList.add(projectDto);
						}
					}
				}
			}
			OrganizationDto  organizationDto = new OrganizationDto();
			organizationDto.setId(o.getId());
			organizationDto.setName(o.getName());
			organizationDto.setProjectList(projectList);
			orgProjects.add(organizationDto);
		}
		if(StringUtils.isNotBlank(status)){
			personMap.put("projectStatus",ProjectSatus.DOING.getCode());
		}
		personMap.put("projectList", personProjects);
		map.put("orgProjects", orgProjects);
		map.put("personProjects", personMap);
		return map;
	}

	@Override
	public List<Project> listHomeProjectsForMobile(int userId,String status) {
		return projectDao.listHomeProejctsForMobile(userId,status);
	}

	@Override
	public byte[] findLogoByProUuidForMobile(int id) {
		return projectDao.findLogoByProUuidForMobile(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> findProjectByStatusForMobile(int userId,
			String status, int orgId) {
		Map<String, Object> daoMap = projectDao.findProjectByStatusForMobile(userId, status, orgId);
		String projectStatus = (String)daoMap.get("status");
		List<Project> projectList = new ArrayList<Project>();
		Map<String, Object> resultMap = new HashMap<String,Object>();
		List<Map<String,Object>> list = (List<Map<String, Object>>)daoMap.get("list");
		for(Map<String, Object> map:list ){
			Project project = new Project();
			project.setProjectId((Integer)map.get("projectId"));
			project.setStatus((String)map.get("status"));
			project.setUuid((String)map.get("uuid"));
			project.setName((String)map.get("name"));
			if(!projectList.contains(project)){
				projectList.add(project);
			}
		}
		resultMap.put("projectStatus", projectStatus);
		resultMap.put("projectList", projectList);
		return resultMap;
	}

	@Override
	public Map<String, Object> findProjectInfoByProIdForMobile(int projectId) {
		List<Map<String, Object>> userList = projectDao.listProjectAllUser(projectId);
		List<Integer> memberList = new ArrayList<Integer>();
		Map<String, Object> info = new HashMap<String, Object>();
		for(Map<String, Object> map:userList){
			if(Collections.frequency(memberList, (Integer)map.get("id"))<1){
				memberList.add((Integer)map.get("id"));
			}
		}
		Project project = projectDao.findProByProIdForMobile(projectId);
		info.put("users", memberList);
		info.put("project", project);
		return info;
	}

	@Override
	public List<ProjectDto> findProjectByUserIdForMobile(int userId) {
		List<Map<String,Object>> proMaps = projectDao.findProjectByUserIdForMobile(userId);
		List<ProjectDto> projectDtos = new ArrayList<ProjectDto>();
		for(Map<String, Object> map:proMaps){
			ProjectDto projectDto = new ProjectDto();
			projectDto.setProjectId((Integer)map.get("id"));
			projectDto.setName((String)map.get("name"));
			if(Collections.frequency(projectDtos, projectDto)<1){
				projectDtos.add(projectDto);
			}
		}
		return projectDtos;
	}
	
	public List<UserDto> findUserByProjectIdForMobile(int projectId) {
		List<Map<String, Object>> userList = projectDao.listProjectAllUser(projectId);
		List<UserDto> userDtos = new ArrayList<UserDto>();
		for(Map<String, Object> map:userList){
			UserDto userDto = new UserDto();
			userDto.setUserId((Integer)map.get("id"));
			userDto.setUserName((String)map.get("name"));
			if(Collections.frequency(userDtos, userDto)<1){
				userDtos.add(userDto);
			}
		}
		return userDtos;
	}
}
