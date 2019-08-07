package com.ks0100.wp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.constant.BusinessConstant.PermitConstant;
import com.ks0100.wp.constant.BusinessConstant.RoleConstant;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.Team;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.service.OrganizationService;
import com.ks0100.wp.service.ProjectService;
import com.ks0100.wp.service.TeamService;

@Controller
@RequestMapping("/team")
public class TeamController extends BaseController{

	@Autowired
	private TeamService teamService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private ProjectService projectService;

	@RequestMapping("/list")
	public String team(String id, ModelMap model) {
		model.put("list",
				teamService.listTeamByOrganizationId(Integer.parseInt(id)));
		model.put("orgId", id);
		return "organization/team";
	}

	@RequestMapping("/add")
	public String add() {
		return "team/add";
	}

	@ResponseBody
	@RequestMapping("/create")
	public Map<String, Object> add(String id, String name) {

		Team team = new Team();
		team.setName(name);
		team.setOrganization(organizationService.findOrganizationById(Integer
				.parseInt(id)));

		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();

		Map<String, Object> result = new HashMap<String, Object>();

		if (user != null) {
			int userId = user.getUserId();

			team.setCreatedBy(userId);

			teamService.addTeam(team);

			result.put("logo", user.getLogo());
			result.put("team", team);

			return ResultDataJsonUtils.successResponseResult(result);
		} else {
			return ResultDataJsonUtils.errorResponseResult("请重新登录");
		}

	}

	@ResponseBody
	@RequestMapping("/update")
	public Map<String, Object> updateName(Team team) {
		Team t = teamService.findByTeamId(team.getTeamId());
		t.setName(team.getName());
		teamService.updateTeamName(t);
		return ResultDataJsonUtils.successResponseResult(t.getName());
	}

	@ResponseBody
	@RequestMapping("/exit")
	public Map<String, Object> exit(int id) {
		if (teamService.exit(id)) {
			return ResultDataJsonUtils.successResponseResult();
		} else {
			return ResultDataJsonUtils.errorResponseResult("退出团队失败");
		}
	}

	@ResponseBody
	@RequestMapping("/del")
	public Map<String, Object> del(int id) {
		teamService.delete(id);
		return ResultDataJsonUtils.successResponseResult();
	}
	
	@ResponseBody
	@RequestMapping("/teamUser")
	public Map<String,Object> teamUser(int teamId)
	{
		List<User> users = teamService.listUserByTeamId(teamId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("users", users);
		Team team = teamService.findByTeamId(teamId);
		if(hasPermit(PermitConstant.ORG_ADMIN_ACCESS,team.getOrganization().getId())){
			map.put("type",true);
		}
 		return ResultDataJsonUtils.successResponseResult(map);
	}
	@RequestMapping("/userList")
	public String user(int id, ModelMap model) {
		List<User> users = teamService.listUserByTeamId(id);
		List<Project> pro = projectService.findProByTeamId(id);
		model.put("users", users);
		model.put("teamId", id);
		model.put("pro", pro);
		Team team=teamService.findByTeamId(new Integer(id));
		if(team!=null){
			List<String> permitCodeAndIds=new ArrayList<String>();
			permitCodeAndIds.add(PermitConstant.ORG_ADMIN_ACCESS+":"+team.getOrganization().getId());
			permitCodeAndIds.add(PermitConstant.ORG_TEAM_SET+":"+id);
			model.put("permitCodeAndIds", permitCodeAndIds);
			model.put("orgId", team.getOrganization().getId());
		}

		return "team/teamUser";
	}

	/**
	 * 查询当前项目中的团队
	 * 
	 * @param projectId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/proTeamList", method = RequestMethod.POST)
	public Map<String, Object> proTeamList(int projectId) {

		List<Team> team = teamService.listTeamByProId(projectId);
		return ResultDataJsonUtils.successResponseResult(team);
	}

	/**
	 * 项目设置页面查询当前组织的所有团队
	 * 
	 * @param orgId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/teamList", method = RequestMethod.POST)
	public Map<String, Object> teamList(int orgId, int proId) {
		List<Team> teams = teamService.listTeamByOrganizationId(orgId);
		Map<Integer, Object> teaMap = new HashMap<Integer, Object>();
		for (Team t : teams) {
			Map<String, Object> team = new HashMap<String, Object>();
			int teamId = t.getTeamId();
			team.put("team", t);
			if (projectService.teamExistPro(teamId, proId)) {
				team.put("existPro", true);
			} else {
				team.put("existPro", false);
			}
			teaMap.put(teamId, team);
		}
		return ResultDataJsonUtils.successResponseResult(teaMap);
	}

	@ResponseBody
	@RequestMapping(value = "/addPro")
	public Map<String, Object> addPro(int proId, int teamId) {
		teamService.addPro(teamId, proId);
		return ResultDataJsonUtils.successResponseResult();
	}

	@ResponseBody
	@RequestMapping(value = "/findUserFromOrg")
	public Map<String, Object> addUserFromOrg(int teamId, String orgId) {
		List<User> teamUsers = teamService.listUserByTeamId(teamId);
		List<Map<String, Object>> list = organizationService
				.findUserList(Integer.parseInt(orgId));

		List<User> orgUsers = new ArrayList<User>();

		for (Map<String, Object> map : list) {

			if ((Integer) map.get("id") != null) {

				User user = new User();
				user.setUserId((Integer) map.get("id"));
				user.setName((String) map.get("name"));
				user.setLogo((String) map.get("logo"));
				user.setBelongTeam(false);
				user.setUserRole((String)map.get("role"));

				if (!orgUsers.contains(user)) {
					orgUsers.add(user);
				}
			}
		}
		
		for(User a:orgUsers)
		{
			for(User b:teamUsers)
			{
				if(a.getUserId()==b.getUserId())
				{
					a.setUserRole(b.getUserRole());
					a.setBelongTeam(true);
				}
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		Team team = teamService.findByTeamId(teamId);
		if(hasPermit(PermitConstant.ORG_ADMIN_ACCESS,team.getOrganization().getId())){
			map.put("type",true);
		}
		map.put("orgUsers", orgUsers);
		return ResultDataJsonUtils.successResponseResult(map);
	}

	@ResponseBody
	@RequestMapping(value = "/addUser")
	public Map<String, Object> addUser(int teamId, int userId) {
		teamService.addUser(teamId, userId);
		return ResultDataJsonUtils.successResponseResult();
	}
	
	@ResponseBody
	@RequestMapping(value = "/delUser")
	public Map<String, Object> delUser(int teamId, int userId) {
		teamService.delUser(teamId, userId);
		return ResultDataJsonUtils.successResponseResult();
	}
	/**
	 * 设置团队的管理员
	 * @param teamId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/setTeamAdmin" , method = RequestMethod.POST)
	public Map<String, Object> setTeamAdmin(int teamId, int userId) {
		teamService.setOrRemoveAdmin(teamId, userId, RoleConstant.TEAM_ADMIN);
		ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
		user.getPermissions().clear();
		return ResultDataJsonUtils.successResponseResult();
	}
	/**
	 * 取消团队的管理员
	 * @param teamId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/removeTeamAdmin", method = RequestMethod.POST)
	public Map<String, Object> removeTeamAdmin(int teamId, int userId) {
		teamService.setOrRemoveAdmin(teamId, userId, RoleConstant.TEAM_MEMBER);
		ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
		user.getPermissions().clear();
		return ResultDataJsonUtils.successResponseResult();
	}
	/**
	 * 加载团队菜单页面
	 * @param id
	 * @param model
	 * @return
	 */
	@RequestMapping("/loadTeamMenu")
	public String teamMenu(int teamId, ModelMap model) {
		Team team = teamService.findByTeamId(teamId);
		if(team!=null){
			List<String> permitCodeAndIds=new ArrayList<String>();
			permitCodeAndIds.add(PermitConstant.ORG_ADMIN_ACCESS+":"+team.getOrganization().getId());
			permitCodeAndIds.add(PermitConstant.ORG_TEAM_SET+":"+teamId);
			model.put("permitCodeAndIds", permitCodeAndIds);
		}
		model.put("orgId", team.getOrganization().getId());
		return "team/teamMenu";
	}

	  
}
