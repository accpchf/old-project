package com.ks0100.wp.controller;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.common.constant.CommonConstant;
import com.ks0100.common.ehcache.CacheUtil;
import com.ks0100.common.util.PathUtil;
import com.ks0100.common.util.PictureUtil;
import com.ks0100.common.util.ReadPropertiesUtil;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.constant.BusinessConstant.AddProjectUserFrom;
import com.ks0100.wp.constant.BusinessConstant.PermitConstant;
import com.ks0100.wp.constant.StatusEnums.ProjectSatus;
import com.ks0100.wp.dao.ProjectDao;
import com.ks0100.wp.entity.Organization;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.ProjectActionRecord;
import com.ks0100.wp.entity.Team;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.service.LoginService;
import com.ks0100.wp.service.OrganizationService;
import com.ks0100.wp.service.ProjectActionRecordService;
import com.ks0100.wp.service.ProjectService;
import com.ks0100.wp.service.TeamService;

@Controller
@RequestMapping("/project")

public class ProjectController extends BaseController {
	@Autowired
	public ProjectService projectService;
	@Autowired
	public OrganizationService organizationService;
	@Autowired
	private LoginService loginService;
	@Autowired
	private TeamService teamService;
	@Autowired
    private ProjectActionRecordService pRecordService;
	@Autowired
	private ProjectDao projectDao;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 统一改造页面上的链接，重定向到/loginSuccessToIndex url
	 * 
	 * @return 创建日期：2014-12-1 修改说明：
	 * @author chengls
	 */
	@RequestMapping("/home")
	public String home(ModelMap model,HttpServletRequest request) {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		return "redirect:/project/loginSuccessToIndex/"+user.getAccount()+".htm";
	}
	
	/**
	 * 登录成功后，跳至项目首页
	 * 
	 */
	@RequestMapping("/loginSuccessToIndex/{account}")
	public String loginSuccessToIndex(ModelMap model) {
		logger.debug("不使用缓存------------------------");
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		user.getPermissions().clear();
		Map<String, Object> map = this.projectService.findAllProjectsByUser(user.getUserId());
		model.put("commonProjects", map.get("commonProjects"));
		model.put("personProjects", map.get("personProjects"));
		model.put("orgProjects", map.get("orgProjects"));

		return "home/home";
	}
	
	/**
	 * 获得所有项目
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "boxing" })
	@ResponseBody
	@RequestMapping(value = "/getProjectsList", method = RequestMethod.GET)
	public Map<String, Object> getProjectsList() {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		Map<String, Object> map = this.projectService.findAllProjectsByUser(user.getUserId());

		// 去除无项目的组织
		if (map.get("orgProjects") != null) {

			Map<Integer, Organization> oldOrgProjects = (Map<Integer, Organization>) map.get("orgProjects");

			Map<Integer, Organization> newOrgProjects = new HashMap<Integer, Organization>();
			for (Map.Entry entry : oldOrgProjects.entrySet()) {
				Organization o = (Organization) entry.getValue();

				if (o.getProjectList() != null && !o.getProjectList().isEmpty()) {
					newOrgProjects.put(o.getId(), o);
				}
			}
			map.put("orgProjects", newOrgProjects);
		}

		return ResultDataJsonUtils.successResponseResult(map);
	}

	/**
	 * 转到项目页面
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "boxing" })
	@RequestMapping("/{uuid}/gotoProject")
	public String gotoProject(@PathVariable("uuid") String uuid, ModelMap model) {
		Project project = this.projectService.findProByuuid(uuid);
		
		if(!hasPermit(PermitConstant.PRJ_ACCESS, project.getProjectId())){
			return "/login/unauthorized";
		}
		
		if(ProjectSatus.DELETE.getCode().equals(project.getStatus())){
			return "/error/exceptionPage";
		}
		
		model.put("projectId", project.getProjectId());

		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		Map<String, Object> map = this.projectService.findAllProjectsByUser(user.getUserId());
		// 去除无项目的组织
		if (map.get("orgProjects") != null) {
			Map<Integer, Object> oldOrgProjects = (Map<Integer, Object>) map.get("orgProjects");
			Map<Integer, Object> newOrgProjects = new HashMap<Integer, Object>();
			for (Map.Entry entry : oldOrgProjects.entrySet()) {
				Map<String, Object> map2 = (Map<String, Object>) entry.getValue();
				if (map2.get("projectList") != null) {
					newOrgProjects.put((Integer) entry.getKey(), map2);
				}
			}
			map.put("orgProjects", newOrgProjects);
		}
		model.put("projectName", project.getName());
		model.put("commonProjects", map.get("commonProjects"));
		model.put("personProjects", map.get("personProjects"));
		model.put("orgProjects", map.get("orgProjects"));
		return "template/projectTemplate";
	}

	@ResponseBody
	@RequestMapping(value = "/isCoummonUse", method = RequestMethod.POST)
	public Map<String, Object> isCoummonUse(int projectId) {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		projectService.updateProCommonUse(projectId);
		if(CacheUtil.removePageEhcahce(CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm")){
			logger.debug("已经清理该页面缓存："+CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm");
		}
		return ResultDataJsonUtils.successResponseResult();
	}

	@RequestMapping("/addProject")
	public String addProject(ModelMap model) {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		List<Organization> orgs = organizationService.listOrganizationsByUser(user.getUserId());
		model.put("orgs", orgs);
		user.getPermissions().clear();
		if(CacheUtil.removePageEhcahce(CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm")){
			logger.debug("已经清理该页面缓存："+CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm");
		}
		return "project/addProject";
	}

	@ResponseBody
	@RequestMapping(value = "/addProSuccess", method = RequestMethod.POST)
	public Map<String, Object> addProSuccess(HttpServletRequest request, Project project, Integer organizationId, String imgId,String beginString,String endString) throws Exception {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		project.setName(project.getName().trim());
		//String status = getSysStatusCode("007", "进行中");
		project.setStatus(ProjectSatus.DOING.getCode());
		// 转型为MultipartHttpRequest：
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		// 获得文件：
		MultipartFile file = multipartRequest.getFile("logoFile");
		Format f = new SimpleDateFormat("yyyy-MM-dd");
		if (beginString != "" && beginString.length() > 0) {
			beginString = beginString.replaceAll("年", "-");
			beginString = beginString.replaceAll("月", "-");
			beginString = beginString.replaceAll("日", "-");
			Date beginTime = (Date) f.parseObject(beginString);
			project.setBeginTime(beginTime);
		}
		if (endString != "" && endString.length() > 0) {
			endString = endString.replaceAll("年", "-");
			endString = endString.replaceAll("月", "-");
			endString = endString.replaceAll("日", "-");
			Date endTime = (Date) f.parseObject(endString);
			project.setEndTime(endTime);
		}
		if (file != null) {
			if (file.getBytes() != null) {
				project.setLogo(file.getBytes());
			}
		} else {
			if (imgId == "" || imgId.length() == 0) {
				imgId = "project1";
			}
			String path = "/static/images/project_default/pic-" + imgId + ".jpg";
			PictureUtil p = new PictureUtil(PathUtil.ABSOLUTE_WEB_PATH + path);
			project.setLogo(p.changeToBytes());
			p.closeInputStream();
		}
		if (organizationId != null && organizationId > 0) {
			project.setOrganization(organizationService.findOrganizationById(organizationId));
		}
		projectService.createProject(project);
		user.getPermissions().clear();
		return ResultDataJsonUtils.successResponseResult(project.getUuid());
	}

	/*
	 * 删除项目
	 */
	@ResponseBody
	@RequestMapping(value = "/deletePro", method = RequestMethod.POST)
	public Map<String, Object> deletePro(int proId) {
		
		if(!hasPermit(PermitConstant.PRJ_SET,proId)){
			return ResultDataJsonUtils.errorResponseResult("您没有权限进行操作");
		}
		
		Project pro = projectService.findProjectById(proId);
		pro.setStatus(ProjectSatus.DELETE.getCode());
		projectService.updateProject(pro);
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		if(CacheUtil.removePageEhcahce(CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm")){
			logger.debug("已经清理该页面缓存："+CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm");
		}
		return ResultDataJsonUtils.successResponseResult(pro.getStatus());
	}

	/*
	 * 退出项目
	 */
	@ResponseBody
	@RequestMapping(value = "/exitPro", method = RequestMethod.POST)
	public Map<String, Object> exitPro(int proId) {
		
		if(!hasPermit(PermitConstant.PRJ_QUIT,proId)){
			return ResultDataJsonUtils.errorResponseResult("您没有权限进行操作");
		}
		
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		Project project = projectService.findProjectById(proId);
		User u = loginService.findUser(user.getUserId());
		projectService.exitPro(u, project, 1);
		if(CacheUtil.removePageEhcahce(CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm")){
			logger.debug("已经清理该页面缓存："+CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm");
		}
		return ResultDataJsonUtils.successResponseResult();
	}

	@ResponseBody
	@RequestMapping(value = "/updateProInfo", method = RequestMethod.POST)
	public Map<String, Object> updateProInfo(HttpServletRequest request, Project project,String beginString,String endString) throws Exception {
		if(!hasPermit(PermitConstant.PRJ_SET,project.getProjectId())){
			return ResultDataJsonUtils.errorResponseResult("您没有权限进行操作");
		}
		try{
			Project p = projectService.findProjectById(project.getProjectId());
			p.setName(project.getName().trim());
			p.setDescription(project.getDescription());

			// 转型为MultipartHttpRequest：
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			// 获得文件：
			MultipartFile file = multipartRequest.getFile("inputLogo");
			Format f = new SimpleDateFormat("yyyy-MM-dd");
			if (beginString != "" && beginString.length() > 0) {
				beginString = beginString.replaceAll("年", "-");
				beginString = beginString.replaceAll("月", "-");
				beginString = beginString.replaceAll("日", "-");
				Date beginTime = (Date) f.parseObject(beginString);
				p.setBeginTime(beginTime);
			} else {
				p.setBeginTime(null);
			}
			if (endString != "" && endString.length() > 0) {
				endString = endString.replaceAll("年", "-");
				endString = endString.replaceAll("月", "-");
				endString = endString.replaceAll("日", "-");
				Date endTime = (Date) f.parseObject(endString);
				p.setEndTime(endTime);
			} else {
				p.setEndTime(null);
			}
			if (file != null && file.getBytes() != null) {
				p.setLogo(file.getBytes());
			}
			boolean update_project_suceess = projectService.updateProject(p);
			if(update_project_suceess){
				return ResultDataJsonUtils.successResponseResult();
			}else{
				return ResultDataJsonUtils.errorResponseResult("更新失败");
			}
		}catch(Exception e) {
			return ResultDataJsonUtils.errorResponseResult("更新失败");
		}
		
		
	}

	@RequestMapping("/projectSetting")
	public String projectSetting(String proId, ModelMap model) {
		Project project = projectService.findProjectById(Integer.parseInt(proId));
		if (project.getLogo() != null && project.getLogo().length > 0) {
			project.setLogoStr(PictureUtil.changeToBASE64EncoderStr(project.getLogo()));
		}
		if (project.getOrganization() != null) {
			model.put("orgId", project.getOrganization().getId());
		} else {
			model.put("orgId", 0);
		}
		model.put("pro", project);
		if(project != null){
			List<ProjectActionRecord> projectActionRecords = pRecordService.listProjectActionRecords(project.getProjectId());
			model.put("projectActionRecords", projectActionRecords);
		}
		if(!hasPermit(PermitConstant.PRJ_SET,Integer.parseInt(proId))){
			return "project/projectReadOnly";
		}
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		if(CacheUtil.removePageEhcahce(CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm")){
			logger.debug("已经清理该页面缓存："+CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm");
		}
		return "project/projectSetting";
	}

	/*
	 * 初始化成员管理界面 拿到当前项目的url邀请链接
	 */
	@ResponseBody
	@RequestMapping(value = "/getCode", method = RequestMethod.POST)
	public Map<String, Object> getCode(int proId) {
		String resultCode = "";
		String code = ReadPropertiesUtil.getStringContextProperty("web.domain") + BusinessConstant.INVITEDPRO_URL;
		if (projectService.findCode(proId) != null) {
			resultCode = (code + projectService.findCode(proId) + CommonConstant.WEB_SUFFIX).replace(" ", "");
		}
		return ResultDataJsonUtils.successResponseResult(resultCode);
	}

	@ResponseBody
	@RequestMapping(value = "/encrypt", method = RequestMethod.POST)
	public Map<String, Object> encrypt(int proId, String type) {
		String code = "";
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		String resultCode = null;
		Project pro = projectService.findProjectById(proId);
		if ("open".equals(type)) {

			boolean flag = true;
			while (flag) {
				code = UUID.randomUUID().toString();
				if (!projectService.existCode(code)) {
					flag = false;
				}
			}
			String path = ReadPropertiesUtil.getStringContextProperty("web.domain") + BusinessConstant.INVITEDPRO_URL;
			resultCode = (path + code + CommonConstant.WEB_SUFFIX).replace(" ", "");
			pro.setInvitationCode(code);
			pro.setInvitationEnabled(true);
			pro.setInviter(user.getUserId());
		} else if ("close".equals(type)) {
			pro.setInvitationCode(null);
			pro.setInvitationEnabled(false);
			pro.setInviter(null);
		}
		if(projectService.updateProject(pro)){
			return ResultDataJsonUtils.successResponseResult(resultCode);
		}else{
			return ResultDataJsonUtils.errorResponseResult("修改url链接失败");
		}
		
	}

	/**
	 * 暂停项目
	 * 
	 * @param proId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/suspendPro", method = RequestMethod.POST)
	public Map<String, Object> suspendProject(int proId) {
		
		if(!hasPermit(PermitConstant.PRJ_SET,proId)){
			return ResultDataJsonUtils.errorResponseResult("您没有权限进行操作");
		}
		
		Project pro = projectService.findProjectById(proId);
		pro.setStatus(ProjectSatus.STOP.getCode());
		projectService.updateProject(pro);
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		if(CacheUtil.removePageEhcahce(CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm")){
			logger.debug("已经清理该页面缓存："+CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm");
		}
		return ResultDataJsonUtils.successResponseResult(pro.getStatus());
	}

	/**
	 * 完成项目
	 * 
	 * @param proId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/finishPro", method = RequestMethod.POST)
	public Map<String, Object> finishProject(int proId) {
		
		if(!hasPermit(PermitConstant.PRJ_SET,proId)){
			return ResultDataJsonUtils.errorResponseResult("您没有权限进行操作");
		}
		
		Project pro = projectService.findProjectById(proId);
		pro.setStatus(ProjectSatus.COMPLETE.getCode());
		projectService.updateProject(pro);
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		if(CacheUtil.removePageEhcahce(CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm")){
			logger.debug("已经清理该页面缓存："+CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm");
		}
		return ResultDataJsonUtils.successResponseResult(pro.getStatus());
	}

	/**
	 * 重启项目
	 * 
	 * @param proId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/restartPro", method = RequestMethod.POST)
	public Map<String, Object> restartProject(int proId) {
		
		if(!hasPermit(PermitConstant.PRJ_SET,proId)){
			return ResultDataJsonUtils.errorResponseResult("您没有权限进行操作");
		}
		
		Project pro = projectService.findProjectById(proId);
		pro.setStatus(ProjectSatus.DOING.getCode());
		projectService.updateProject(pro);
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		if(CacheUtil.removePageEhcahce(CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm")){
			logger.debug("已经清理该页面缓存："+CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm");
		}
		return ResultDataJsonUtils.successResponseResult(pro.getStatus());
	}

	/**
	 * 获取项目人员的
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/listProjectUser", method = RequestMethod.POST)
	public Map<String, Object> listProjectUser(int projectId) {
		Map<String, Object> userMap = projectService.listProjectUser(projectId);
		return ResultDataJsonUtils.successResponseResult(userMap);
	}

	/**
	 * 获取项目人员,包括团队
	 * 
	 * @param projectId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/listProjectAllUser", method = RequestMethod.POST)
	public Map<String, Object> listProjectAllUser(int projectId) {
		Map<String, Object> userMap = projectService.listProjectAllUser(projectId);
		return ResultDataJsonUtils.successResponseResult(userMap);
	}

	@ResponseBody
	@RequestMapping(value = "ToJoinProByEmail", method = RequestMethod.POST)
	public Map<String, Object> ToJoinProByEmail(String account, int proId) {
		User user = loginService.findUserByAccount(account);
		if (user == null) {
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("updateUser_notExistUser"), "error_10");
		}
		Project pro = projectService.findProjectById(proId);

		if (pro.getOrganization() != null) {
			if (organizationService.existOrgUser(pro.getOrganization().getId(), user.getUserId())) {
				int isok = addUser(pro.getProjectId(), user, AddProjectUserFrom.FROM_PROJECT_ADMIN);
				if (isok == 0) {
					return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("useAlreadyrInProject"), "error_1");
				} else if (isok == 2) {
					return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("addFailure"), "error_3");
				}
			} else {
				return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("userNoExistOrgs"), "error_0");
			}

		} else {
			int isok = addUser(pro.getProjectId(), user, AddProjectUserFrom.FROM_PROJECT_ADMIN);
			if (isok == 0) {
				return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("useAlreadyrInProject"), "error_1");
			} else if (isok == 2) {
				return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("addFailure"), "error_3");
			}
		}

		return ResultDataJsonUtils.successResponseResult(user);
	}

	/**
	 * 移除项目中的成员
	 * 
	 * @param userId
	 * @param proId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/removeUserByPro", method = RequestMethod.POST)
	public Map<String, Object> removeUserByPro(int userId, int proId) {
		
		if(!hasPermit(PermitConstant.PRJ_SET,proId)){
			return ResultDataJsonUtils.errorResponseResult("您没有权限进行操作");
		}
		
		Project project = projectService.findProjectById(proId);
		User user = loginService.findUser(userId);
		projectService.exitPro(user, project, 2);
		return ResultDataJsonUtils.successResponseResult();
	}

	/**
	 * 列出组织中的成员,并判断是否已在项目中
	 * 
	 * @param proId
	 * @param orgId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/userOrgList", method = RequestMethod.POST)
	public Map<String, Object> userList(int proId, int orgId) {
		List<Map<String, Object>> userList = organizationService.findUserList(orgId);
		Map<String, Object> user = projectService.listProjectUser(proId);
		Map<String, Object> usersOrg = new HashMap<String, Object>();
		if (user.size() == userList.size()) {
			usersOrg.put("allUserExist", true);
		} else {
			usersOrg.put("allUserExist", false);
		}
		for (Map<String, Object> map : userList) {
			Map<String, Object> users = new HashMap<String, Object>();
			int userId = (Integer) map.get("id");
			users.put("user", map);
			if (projectService.eixstPro(proId, userId)) {
				users.put("existPro", true);
			} else {
				users.put("existPro", false);
			}
			usersOrg.put(Integer.toString(userId), users);
		}
		return ResultDataJsonUtils.successResponseResult(usersOrg);
	}

	/**
	 * 删除项目和团队的关联关系
	 * 
	 * @param teamId
	 * @param proId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/removeTeamByPro", method = RequestMethod.POST)
	public Map<String, Object> removeTeamByPro(int teamId, int proId) {
		
		if(!hasPermit(PermitConstant.PRJ_SET,proId)){
			return ResultDataJsonUtils.errorResponseResult("您没有权限进行操作");
		}
		
		Team team = teamService.findByTeamId(teamId);
		if (projectService.removeTeamByPro(team, proId)) {
			return ResultDataJsonUtils.successResponseResult();
		} else {
			return ResultDataJsonUtils.errorResponseResult("移除失败");
		}

	}

	@ResponseBody
	@RequestMapping(value = "/addUserForPro", method = RequestMethod.POST)
	public Map<String, Object> addUserForPro(int userId, int proId) {
		
		if(!hasPermit(PermitConstant.PRJ_SET,proId)){
			return ResultDataJsonUtils.errorResponseResult("您没有权限进行操作");
		}
		try{
			User user = loginService.findUser(userId);
			int isok = addUser(proId, user, AddProjectUserFrom.FROM_PROJECT_ADMIN);
			if (isok == 2) {
				return ResultDataJsonUtils.errorResponseResult("添加失败");
			} else {
				return ResultDataJsonUtils.successResponseResult();
			}
		}catch(Exception e){
			return ResultDataJsonUtils.errorResponseResult("添加失败");
		}
	}

	@ResponseBody
	@RequestMapping(value = "/addTeamForPro", method = RequestMethod.POST)
	public Map<String, Object> addTeamForPro(int teamId, int proId) {
		
		if(!hasPermit(PermitConstant.PRJ_SET,proId)){
			return ResultDataJsonUtils.errorResponseResult("您没有权限进行操作");
		}
		try{
			Team team = teamService.findByTeamId(teamId);
			projectService.addTeamForPro(team, proId);
		}catch(Exception e) {
			  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("team_add_project_fail"));
		}
		
		return ResultDataJsonUtils.successResponseResult();
	}

	@ResponseBody
	@RequestMapping(value = "/addAllUserInOrg", method = RequestMethod.POST)
	public Map<String, Object> addAllUserInOrg(int orgId, int proId) {
		
		if(!hasPermit(PermitConstant.PRJ_SET,proId)){
			return ResultDataJsonUtils.errorResponseResult("您没有权限进行操作");
		}
		
		List<Map<String, Object>> userList = organizationService
				.findUserList(orgId);
		for (Map<String, Object> map : userList) {
			int userId = (Integer) map.get("id");
			User user = loginService.findUser(userId);
			addUser(proId, user, AddProjectUserFrom.FROM_PROJECT_ADMIN);
		}
		return ResultDataJsonUtils.successResponseResult();
	}

	/**
	 * 获取项目倒计时
	 * 
	 * @param projectId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/findProjectStatistics", method = RequestMethod.POST)
	public Map<String, Object> findProjectStatistics(int projectId, int loadType) {
		Map<String, Object> counts = projectService.findProjectStatistics(projectId, loadType);
		return ResultDataJsonUtils.successResponseResult(counts);
	}

	/**
	 * 设置为管理员权限
	 * 
	 * @param projectId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/setAdmin", method = RequestMethod.POST)
	public Map<String, Object> setAdmin(int projectId, int userId) {
		
		if(!hasPermit(PermitConstant.PRJ_SET,projectId)){
			return ResultDataJsonUtils.errorResponseResult("您没有权限进行操作");
		}
		
		User user = loginService.findUser(userId);
		projectService.setAdmin(user, projectId);
		return ResultDataJsonUtils.successResponseResult();
	}

	/**
	 * 设置为监督员权限
	 * @param projectId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/setSupervise", method = RequestMethod.POST)
	public Map<String, Object> setSupervise(int projectId, int userId) {
		
		if(!hasPermit(PermitConstant.PRJ_SET,projectId)){
			return ResultDataJsonUtils.errorResponseResult("您没有权限进行操作");
		}
		
		
		User user = loginService.findUser(userId);
		projectService.setSupervise(user, projectId);
		return ResultDataJsonUtils.successResponseResult();
	}
	
	/**
	 * 设置为成员权限
	 * @param projectId
	 * @param userId
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/setMember", method = RequestMethod.POST)
	public Map<String, Object> setMember(int projectId, int userId) {
		
		if(!hasPermit(PermitConstant.PRJ_SET,projectId)){
			return ResultDataJsonUtils.errorResponseResult("您没有权限进行操作");
		}
		
		
		User user = loginService.findUser(userId);
		projectService.setMember(user, projectId);
		return ResultDataJsonUtils.successResponseResult();
	}

	public int addUser(int prjId, User user, String addFrom) {
		if (!projectService.ExistUserPro(user, prjId)) {
			if(projectService.isExistUserPro(user, prjId)){
				if(projectService.saveUserIfExist(user, prjId, addFrom)){
					return 1; // 添加成功
				}else{
					return 2; // 添加失败
				}
			}else{
				if (projectService.saveUserPro(user, prjId, addFrom)) {
					return 1; // 添加成功
				} else {
					return 2; // 添加失败
				}
			}
			
		} else {
			return 0; // 数据已存在
		}
	}

/*	public String getSysStatusCode(String statusClass, String name) {
		if (statusClass != null && !"".equals(statusClass) && name != null && !"".equals(name)) {
			List<Map<String, Object>> statusList = sysStatusService.findCodeAndVal(statusClass);
			String status = null;
			for (Map<String, Object> map : statusList) {
				if (name.equals(map.get("value"))) {
					status = (String) map.get("code");
				}
			}
			return status;
		} else {
			return "";
		}
	}*/

	/**
	 * 根据项目状态查询项目
	 * 
	 * @param orgId
	 * @param status
	 * @return
	 */
	@RequestMapping("/findProjectByStatus")
	public String setOrRemoveSupervise(int orgId, String status, ModelMap model) {
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		List<Map<String, Object>> projectList = projectService.findProjectByStatus(user.getUserId(), status, orgId);
		model.put("projects", projectList);
		model.put("status", status);
		return "home/homeProject";
	}
}
