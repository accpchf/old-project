package com.ks0100.wp.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
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
import com.ks0100.common.util.MailUtil;
import com.ks0100.common.util.PictureUtil;
import com.ks0100.common.util.ReadPropertiesUtil;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.constant.BusinessConstant.PermitConstant;
import com.ks0100.wp.constant.BusinessConstant.RoleConstant;
import com.ks0100.wp.constant.StatusEnums.ProjectSatus;
import com.ks0100.wp.dto.TongJi;
import com.ks0100.wp.entity.Organization;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.Team;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.service.LoginService;
import com.ks0100.wp.service.OrganizationService;
import com.ks0100.wp.service.ProjectService;

@Controller
@RequestMapping("/org")
public class OrganizationController extends BaseController{

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private OrganizationService organizationService;

  @Autowired
  private LoginService login;

  @Autowired
  private ProjectService project;

  @Autowired
  private MailUtil mailUtil;

  @RequestMapping("/{uuid}/organization")
  public String home(@PathVariable("uuid")String uuid, ModelMap model) {
	  	ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
		user.getPermissions().clear();
		Organization org=organizationService.findOrgByuuid(uuid);
		if(!hasPermit(PermitConstant.ORG_MENU,org.getId())){
			return "/login/unauthorized";
		}
		model.put("organization", org);
		return "organization/organization";
  }

  @RequestMapping("/add")
  public String add() {
	return "organization/addOrg";
  }

  @ResponseBody
  @RequestMapping("/create")
  public Map<String, Object> add(Organization org) throws Exception {
	org.setName(org.getName().trim());
	org.setPhone(org.getPhone().trim());
	org.setContacter(org.getContacter().trim());
	ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	String uuid = null;
	if(organizationService.createOrganization(org, user.getUserId())) {
		if(CacheUtil.removePageEhcahce(CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm")){
			logger.debug("已经清理该页面缓存："+CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm");
		}
	  uuid = org.getUuid();
	}
	user.getPermissions().clear();
	
	return ResultDataJsonUtils.successResponseResult(uuid);
  }

  @RequestMapping(value = "/show")
  public String show(String id, ModelMap model) {
	model.put("detail", organizationService.findOrganizationById(Integer.parseInt(id)));
	return "organization/setting";
  }

  @ResponseBody
  @RequestMapping(value = "/modify", method = RequestMethod.POST)
  public Map<String, Object> modify(HttpServletRequest request, Organization org) throws Exception {

	Organization organization = organizationService.findOrganizationById(org.getId());

	organization.setName(org.getName().trim());
	organization.setDescription(org.getDescription());
	organization.setPhone(org.getPhone().trim());
	organization.setContacter(org.getContacter().trim());

	MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
	MultipartFile file = multipartRequest.getFile("orgPic");
	if(file != null && file.getBytes() != null && file.getSize() > 0) {
	  //InputStream input = file.getInputStream();
	  PictureUtil pic = new PictureUtil(file.getInputStream());
	  InputStream in = pic.toComprress();
	  String logo = pic.changeToBASE64EncoderStr(in);
	  pic.closeInputStream();
	  organization.setLogo(logo);
	}

	organizationService.updateOrganization(organization);

	return ResultDataJsonUtils.successResponseResult(organization);

  }

  @RequestMapping("/userList")
  public String userList(int id, ModelMap model) {
	List<Map<String, Object>> userList = organizationService.findUserList(id);
	model.put("userList", userList);
	model.put("orgId", id);
	return "organization/user";
  }

  @ResponseBody
  @RequestMapping(value = "/encrypt", method = RequestMethod.POST)
  public Map<String, Object> encrypt(String id, String type) {
	ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	String code = "";
	Organization org = organizationService.findOrganizationById(Integer.parseInt(id));
	if("open".equals(type)) {

	  boolean flag = true;
	  while(flag) {
		code = UUID.randomUUID().toString();
		if(!organizationService.existCode(code)) {
		  flag = false;
		}
	  }
	  org.setInvitationCode(code);
	  org.setInviter(user.getUserId());
	  org.setInvitationEnabled(true);

	} else if("close".equals(type)) {
	  org.setInvitationCode(null);
	  org.setInvitationEnabled(false);
	  org.setInviter(null);
	}
	organizationService.updateOrganization(org);
	return ResultDataJsonUtils.successResponseResult(ReadPropertiesUtil
		.getStringContextProperty("web.domain")
		+ BusinessConstant.INVITED_URL
		+ code
		+ CommonConstant.WEB_SUFFIX);
  }

  @ResponseBody
  @RequestMapping(value = "/getOrg", method = RequestMethod.POST)
  public Organization loadOrg(String code) {
	return organizationService.findByCode(code);
  }

  @SuppressWarnings("rawtypes")
  @RequestMapping(value = "/getUserList", method = RequestMethod.POST)
  public List UserList(int id) {
	return organizationService.findUserList(id);
  }

  @ResponseBody
  @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
  public Map<String, Object> resetPassword(int userId, String encryptedPassword, String password) {
	ShiroUser admin = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	User user = login.findUser(userId);
	SecureRandomNumberGenerator secureRandomNumberGenerator = new SecureRandomNumberGenerator();
	String salt = secureRandomNumberGenerator.nextBytes().toHex();
	//组合username,两次迭代，对密码进行加密 
	String password_cipherText = new Md5Hash(encryptedPassword, user.getAccount() + salt,2).toBase64();
	user.setPassword(password_cipherText);
	user.setSalt(salt);
	login.updateUser(user);

	Map<String, Object> pars = new HashMap<String, Object>();
	pars.put("userName", user.getName());
	pars.put("password", password);
	pars.put("admin", admin.getName());
	Map<String, String> emails = new HashMap<String, String>();
	emails.put(admin.getAccount(), admin.getName());

	if(mailUtil.sendMail(ReadPropertiesUtil.getStringContextProperty("mail.smtp.username"),
		"workbroad管理员重置密码邮件", emails, null, pars, BusinessConstant.EAMIL_FTL_ORGRESETPASSWORD)) {
	  return ResultDataJsonUtils.successResponseResult();
	} else {
	  return ResultDataJsonUtils.errorResponseResult("发送失败");
	}
  }

  @ResponseBody
  @RequestMapping(value = "/getCode", method = RequestMethod.POST)
  public String loadCode(int id) {
	String code = ReadPropertiesUtil.getStringContextProperty("web.domain")
		+BusinessConstant.INVITED_URL;
	if(organizationService.findCode(id) != null && !"".equals(organizationService.findCode(id))) {
	  return code + organizationService.findCode(id) + CommonConstant.WEB_SUFFIX;
	} else {
	  return null;
	}
  }

  @ResponseBody
  @RequestMapping(value = "/initOrgs", method = RequestMethod.POST)
  public Map<String, Object> initOrgs() {
	ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	List<Organization> orgs = organizationService.listOrganizationsByUser(user.getUserId());
	return ResultDataJsonUtils.successResponseResult(orgs);
  }

  @ResponseBody
  @RequestMapping("/delUser")
  public Map<String, Object> delUser(String orgId, String userId) {
	if(organizationService.JustOneAdminForPro(Integer.parseInt(userId), Integer.parseInt(orgId))){
		return ResultDataJsonUtils.errorResponseResult("不能删除该成员!因为该成员为某个项目的唯一管理员,如需删除,请将该成员的项目管理员角色移交给其他成员后删除.");
	}else{
		if(organizationService.delOrgUser(Integer.parseInt(orgId), Integer.parseInt(userId))) {
			  return ResultDataJsonUtils.successResponseResult();
			} else {
			  return ResultDataJsonUtils.errorResponseResult("移除成员失败");
			}
	}
  }

  @ResponseBody
  @RequestMapping("/setAdmin")
  public Map<String, Object> setAdmin(String orgId, String userId) {
	organizationService.setAdmin(Integer.parseInt(orgId), Integer.parseInt(userId));
	ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	user.getPermissions().clear();
	return ResultDataJsonUtils.successResponseResult();
  }
  
  /**
   * 设置为监督员
   * @param orgId
   * @param userId
   * @return
   */
  @ResponseBody
  @RequestMapping(value="/setAdministrator",method = RequestMethod.POST)
  public Map<String, Object> setAdministrator(int orgId, int userId) {
	organizationService.setAdministrator(orgId, userId, RoleConstant.ORG_SUPERVISER);
	ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	user.getPermissions().clear();
	return ResultDataJsonUtils.successResponseResult();
  }
  
  /**
   * 取消监督员角色
   * @param orgId
   * @param userId
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/removeAdministrator", method = RequestMethod.POST)
  public Map<String, Object> removeAdministrator(int orgId, int userId) {
	organizationService.setAdministrator(orgId, userId, RoleConstant.ORG_MEMBER);
	ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	user.getPermissions().clear();
	return ResultDataJsonUtils.successResponseResult();
  }


  @ResponseBody
  @RequestMapping("/cancelAdmin")
  public Map<String, Object> cancelAdmin(String orgId, String userId) {
	organizationService.cancelAdmin(Integer.parseInt(orgId), Integer.parseInt(userId));
	ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	user.getPermissions().clear();
	return ResultDataJsonUtils.successResponseResult();
  }

  @ResponseBody
  @RequestMapping("/addUserByEmail")
  public Map<String, Object> addUser(String orgId, String account) {
	int id = Integer.parseInt(orgId);

	User u = login.findUserByAccount(account);
	if(u == null) {
	  return ResultDataJsonUtils.errorResponseResult("查询不到该用户，请重新输入");
	} else {

	  int i = organizationService.addUser(id, u.getUserId(),
		  BusinessConstant.RoleConstant.ORG_MEMBER);

	  if(i == 0) {
		return ResultDataJsonUtils.errorResponseResult("该用户已加入组织");
	  }

	  if(i == 2) {
		return ResultDataJsonUtils.errorResponseResult("添加失败");
	  }

	  return ResultDataJsonUtils.successResponseResult(u);
	}
  }

  @ResponseBody
  @RequestMapping("/exit")
  public Map<String, Object> exit(String orgId) {
	if(organizationService.exit(Integer.parseInt(orgId))) {
	  return ResultDataJsonUtils.successResponseResult();
	} else {
	  return ResultDataJsonUtils.errorResponseResult("退出失败");
	}
  }

  @ResponseBody
  @RequestMapping("/findPro")
  public Map<String, Object> findPro(int orgId,String orgName, int teamId) {
	List<Project> pro = project.findProjectByOrganizationId(orgId,orgName);
	List<Project> delPro = new ArrayList<Project>();
	List<Project> teamPro = project.findProByTeamId(teamId);
	if(!pro.isEmpty()){
		for(Project p : pro) {
			if(!teamPro.isEmpty()){
				for(Project tp : teamPro) {
					if(p.getProjectId() == tp.getProjectId()) {
					  delPro.add(p);
					}
				  }
			}
			  if(!ProjectSatus.DOING.getCode().equals(p.getStatus())){
				  delPro.add(p);
			  }
			}
	}
	if(!delPro.isEmpty()){
		pro.removeAll(delPro);
	}
	return ResultDataJsonUtils.successResponseResult(pro);
  }

  /**
   * 获取的组织人员任务统计
   * @param orgId
   * @param loadType
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/findOrganizationStatistics_user", method = RequestMethod.POST)
  public Map<String, Object> findOrganizationStatistics_user(int orgId, int loadType) {
	List<User> users = organizationService.findOrganizationStatistics_user(orgId, loadType);
	return ResultDataJsonUtils.successResponseResult(users);
  }

  /**
   * 导出的组织人员任务统计
   * @param orgId
   * @param loadType
   * @return
   */
  @RequestMapping(value = "/exportOrganizationStatistics_user")
  public String exportOrganizationStatistics_user(HttpServletRequest request,
	  HttpServletResponse response) {
	//System.out.println("=============进入了成员导出页面================");
	String orgId = request.getParameter("orgId"), loadType = request.getParameter("type");
	List<User> users = this.organizationService.findOrganizationStatistics_user(
		Integer.parseInt(orgId), Integer.parseInt(loadType));
	List<TongJi> tdtjList = new ArrayList<TongJi>();
	for(User u : users) {
	  tdtjList.add(TongJi.parse(u));
	}
	String fileName = BusinessConstant.XLS_FOR_USER_EXPORT_FILENAME_TASK;
	try {
	  //fileName = URLEncoder.encode(BusinessConstant.XLS_FOR_USER_EXPORT_FILENAME, "UTF-8");
	  fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
	} catch(UnsupportedEncodingException ex) {
	  this.logger.error("error:", ex);
	}
	response.setContentType("application/vnd.ms-excel;charset=utf-8");
	response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0x0);

	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	InputStream is = classLoader.getResourceAsStream("template/excel/UsersTongJi_export.xls");

	Map<String, Object> beans = new HashMap<String, Object>();
	beans.put("users", tdtjList);
	XLSTransformer transformer = new XLSTransformer();
	HSSFWorkbook workbook;
	OutputStream out = null;
	try {
	  out = response.getOutputStream();
	  workbook = (HSSFWorkbook)transformer.transformXLS(is, beans);
	  workbook.write(out);
	} catch(ParsePropertyException ex) {
	  this.logger.error("error:", ex);
	} catch(InvalidFormatException ex) {
	  this.logger.error("error:", ex);
	} catch(IOException ex) {
	  this.logger.error("error:", ex);
	} finally {
	  try {
		if(out != null) {
		  out.flush();
		  out.close();
		}
	  } catch(IOException ex) {
		this.logger.error("error:", ex);
	  }
	}
	return null;
  }

  /**
   * 导出组织项目任务统计
   * @param orgId
   * @param loadType
   * @return
   */
  @RequestMapping(value = "/exportOrganizationStatistics_project")
  public Map<String, Object> exportOrganizationStatistics_project(HttpServletRequest request,
	  HttpServletResponse response) {
	//System.out.println("=============进入了项目导出页面================");
	String orgId = request.getParameter("orgId"), loadType = request.getParameter("type");
	List<Project> pros = this.organizationService.findOrganizationStatistics_project(
		Integer.parseInt(orgId), Integer.parseInt(loadType));
	List<TongJi> xmtjList = new ArrayList<TongJi>();
	for(Project p : pros) {
		System.out.println(p);
	  xmtjList.add(TongJi.parse(p));
	}
	String fileName = BusinessConstant.XLS_FOR_PROJECT_EXPORT_FILENAME_TASK;
	try {
	  //fileName = URLEncoder.encode(BusinessConstant.XLS_FOR_USER_EXPORT_FILENAME, "UTF-8");
	  fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
	} catch(UnsupportedEncodingException ex) {
	  this.logger.error("error:", ex);
	}
	response.setContentType("application/vnd.ms-excel;charset=utf-8");
	response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0x0);

	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	InputStream is = classLoader.getResourceAsStream("template/excel/ProjectsTongJi_export.xls");

	Map<String, Object> beans = new HashMap<String, Object>();
	beans.put("projects", xmtjList);
	XLSTransformer transformer = new XLSTransformer();
	HSSFWorkbook workbook;
	OutputStream out = null;
	try {
	  out = response.getOutputStream();
	  workbook = (HSSFWorkbook)transformer.transformXLS(is, beans);
	  workbook.write(out);
	} catch(ParsePropertyException ex) {
	  this.logger.error("error:", ex);
	} catch(InvalidFormatException ex) {
	  this.logger.error("error:", ex);
	} catch(IOException ex) {
	  this.logger.error("error:", ex);
	} finally {
	  try {
		if(out != null) {
		  out.flush();
		  out.close();
		}
	  } catch(IOException ex) {
		this.logger.error("error:", ex);
	  }
	}
	return null;
  }

  /**
   * 获取的组织项目任务统计
   * @param orgId
   * @param loadType
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/findOrganizationStatistics_project", method = RequestMethod.POST)
  public Map<String, Object> findOrganizationStatistics_project(int orgId, int loadType) {
	List<Project> pros = organizationService.findOrganizationStatistics_project(orgId, loadType);
	return ResultDataJsonUtils.successResponseResult(pros);
  }

  /**
   * 获取的团队任务统计
   * @param orgId
   * @param loadType
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/findOrganizationStatistics_team", method = RequestMethod.POST)
  public Map<String, Object> findOrganizationStatistics_team(int orgId, int loadType) {
	List<Team> teams = organizationService.findOrganizationStatistics_team(orgId, loadType);
	return ResultDataJsonUtils.successResponseResult(teams);
  }

  @RequestMapping(value = "/organizeStatistics")
  public String organizeStatistics(int orgId) {
	  if(!hasPermit(PermitConstant.ORG_STATISTICS_MENU,orgId)){
			return "/login/unauthorized";
		}
	  return "organization/organizeStatistics";
  }

  @ResponseBody
  @RequestMapping(value = "/deleteOrg", method = RequestMethod.POST)
  public Map<String, Object> deleteOrg(int orgId) {
	Organization org = organizationService.findOrganizationById(orgId);
	organizationService.deleteOrg(org);
	ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
	if(CacheUtil.removePageEhcahce(CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm")){
		logger.debug("已经清理该页面缓存："+CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm");
	}
	return ResultDataJsonUtils.successResponseResult();
  }
  /**
   * 获取的组织人员工时统计
   * @param orgId
   * @param loadType
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/findOrganizationTimeStatistics_user", method = RequestMethod.POST)
  public Map<String, Object> findOrganizationTimeStatistics_user(int orgId, int loadType) {
	List<User> users = organizationService.findOrganizationTimeStatistics_user(orgId, loadType);
	return ResultDataJsonUtils.successResponseResult(users);
  }
  
  /**
   * 获取的组织项目工时
   * @param orgId
   * @param loadType
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/findOrganizationTimeStatistics_project", method = RequestMethod.POST)
  public Map<String, Object> findOrganizationTimeStatistics_project(int orgId, int loadType) {
	List<Project> pros = organizationService.findOrganizationTimeStatistics_project(orgId, loadType);
	return ResultDataJsonUtils.successResponseResult(pros);
  }
  /**
   * 获取的团队项目工时
   * @param orgId
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/findOrganizationTimeStatistics_team", method = RequestMethod.POST)
  public Map<String, Object> findOrganizationTimeStatistics_team(int orgId, int loadType) {
	List<Team> teams = organizationService.findOrganizationTimeStatistics_team(orgId, loadType);
	return ResultDataJsonUtils.successResponseResult(teams);
  }
  
  @RequestMapping(value = "/exportOrganizationStatistics_team_task")
	public Map<String, Object> exportOrganizationStatistics_team(HttpServletRequest request, HttpServletResponse response, int orgId, int type) {
		List<Team> teams = organizationService.findOrganizationStatistics_team(orgId, type);
		List<TongJi> xmtjList = new ArrayList<TongJi>();
		for(Team t : teams) {
		  xmtjList.add(TongJi.parse(t));
		}
		String fileName = BusinessConstant.XLS_FOR_TEAM_EXPORT_FILENAME_TASK;
		try {
		  //fileName = URLEncoder.encode(BusinessConstant.XLS_FOR_USER_EXPORT_FILENAME, "UTF-8");
		  fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
		} catch(UnsupportedEncodingException ex) {
		  this.logger.error("error:", ex);
		}
		response.setContentType("application/vnd.ms-excel;charset=utf-8");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0x0);

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream("template/excel/TeamTongJi_export_task.xls");

		Map<String, Object> beans = new HashMap<String, Object>();
		beans.put("teams", xmtjList);
		XLSTransformer transformer = new XLSTransformer();
		HSSFWorkbook workbook;
		OutputStream out = null;
		try {
		  out = response.getOutputStream();
		  workbook = (HSSFWorkbook)transformer.transformXLS(is, beans);
		  workbook.write(out);
		} catch(ParsePropertyException ex) {
		  this.logger.error("error:", ex);
		} catch(InvalidFormatException ex) {
		  this.logger.error("error:", ex);
		} catch(IOException ex) {
		  this.logger.error("error:", ex);
		} finally {
		  try {
			if(out != null) {
			  out.flush();
			  out.close();
			}
		  } catch(IOException ex) {
			this.logger.error("error:", ex);
		  }
		}
		return null;
	  }
  /**
   * 导出组织人员工时
   * @param request
   * @param response
   * @param orgId
   * @param type
   * @return
   */
  @RequestMapping(value = "/exportOrganizationStatistics_user_time")
  public String exportOrganizationStatistics_user_time(HttpServletRequest request,
	  HttpServletResponse response, int orgId, int type) {
	List<User> users = organizationService.findOrganizationTimeStatistics_user(orgId, type);
	List<TongJi> tdtjList = new ArrayList<TongJi>();
	for(User u : users) {
	  tdtjList.add(TongJi.parse(u));
	}
	String fileName = BusinessConstant.XLS_FOR_USER_EXPORT_FILENAME_TIME;
	try {
	  fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
	} catch(UnsupportedEncodingException ex) {
	  this.logger.error("error:", ex);
	}
	response.setContentType("application/vnd.ms-excel;charset=utf-8");
	response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0x0);

	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	InputStream is = classLoader.getResourceAsStream("template/excel/UsersTongJi_export_time.xls");

	Map<String, Object> beans = new HashMap<String, Object>();
	beans.put("users", tdtjList);
	XLSTransformer transformer = new XLSTransformer();
	HSSFWorkbook workbook;
	OutputStream out = null;
	try {
	  out = response.getOutputStream();
	  workbook = (HSSFWorkbook)transformer.transformXLS(is, beans);
	  workbook.write(out);
	} catch(ParsePropertyException ex) {
	  this.logger.error("error:", ex);
	} catch(InvalidFormatException ex) {
	  this.logger.error("error:", ex);
	} catch(IOException ex) {
	  this.logger.error("error:", ex);
	} finally {
	  try {
		if(out != null) {
		  out.flush();
		  out.close();
		}
	  } catch(IOException ex) {
		this.logger.error("error:", ex);
	  }
	}
	return null;
  }
  /**
   * 导出组织项目工时
   * @param request
   * @param response
   * @param orgId
   * @param type
   * @return
   */
  @RequestMapping(value = "/exportOrganizationStatistics_project_time")
  public String exportOrganizationStatistics_project_time(HttpServletRequest request,
	  HttpServletResponse response, int orgId, int type) {
	List<Project> projects = organizationService.findOrganizationTimeStatistics_project(orgId, type);
	List<TongJi> tdtjList = new ArrayList<TongJi>();
	for(Project p : projects) {
	  tdtjList.add(TongJi.parse(p));
	  System.out.println(p);
	}
	
	String fileName = BusinessConstant.XLS_FOR_PROJECT_EXPORT_FILENAME_TIME;
	try {
	  fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
	} catch(UnsupportedEncodingException ex) {
	  this.logger.error("error:", ex);
	}
	response.setContentType("application/vnd.ms-excel;charset=utf-8");
	response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0x0);

	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	InputStream is = classLoader.getResourceAsStream("template/excel/ProjectsTongJi_export_time.xls");

	Map<String, Object> beans = new HashMap<String, Object>();
	beans.put("projects", tdtjList);
	XLSTransformer transformer = new XLSTransformer();
	HSSFWorkbook workbook;
	OutputStream out = null;
	try {
	  out = response.getOutputStream();
	  workbook = (HSSFWorkbook)transformer.transformXLS(is, beans);
	  workbook.write(out);
	} catch(ParsePropertyException ex) {
	  this.logger.error("error:", ex);
	} catch(InvalidFormatException ex) {
	  this.logger.error("error:", ex);
	} catch(IOException ex) {
	  this.logger.error("error:", ex);
	} finally {
	  try {
		if(out != null) {
		  out.flush();
		  out.close();
		}
	  } catch(IOException ex) {
		this.logger.error("error:", ex);
	  }
	}
	return null;
  }
  /**
   * 导出组织项目工时
   * @param request
   * @param response
   * @param orgId
   * @param type
   * @return
   */
  @RequestMapping(value = "/exportOrganizationStatistics_team_time")
  public String exportOrganizationStatistics_team_time(HttpServletRequest request,
	  HttpServletResponse response, int orgId, int type) {
	List<Team> teams = organizationService.findOrganizationTimeStatistics_team(orgId, type);
	List<TongJi> tdtjList = new ArrayList<TongJi>();
	for(Team t : teams) {
	  tdtjList.add(TongJi.parse(t));
	}
	String fileName = BusinessConstant.XLS_FOR_TEAM_EXPORT_FILENAME_TIME;
	try {
	  fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
	} catch(UnsupportedEncodingException ex) {
	  this.logger.error("error:", ex);
	}
	response.setContentType("application/vnd.ms-excel;charset=utf-8");
	response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0x0);

	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	InputStream is = classLoader.getResourceAsStream("template/excel/TeamTongJi_export_time.xls");

	Map<String, Object> beans = new HashMap<String, Object>();
	beans.put("teams", tdtjList);
	XLSTransformer transformer = new XLSTransformer();
	HSSFWorkbook workbook;
	OutputStream out = null;
	try {
	  out = response.getOutputStream();
	  workbook = (HSSFWorkbook)transformer.transformXLS(is, beans);
	  workbook.write(out);
	} catch(ParsePropertyException ex) {
	  this.logger.error("error:", ex);
	} catch(InvalidFormatException ex) {
	  this.logger.error("error:", ex);
	} catch(IOException ex) {
	  this.logger.error("error:", ex);
	} finally {
	  try {
		if(out != null) {
		  out.flush();
		  out.close();
		}
	  } catch(IOException ex) {
		this.logger.error("error:", ex);
	  }
	}
	return null;
  }

}
