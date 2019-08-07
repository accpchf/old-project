package com.ks0100.wp.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.common.util.MailUtil;
import com.ks0100.common.util.PathUtil;
import com.ks0100.common.util.PictureUtil;
import com.ks0100.common.util.ReadPropertiesUtil;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.constant.BusinessConstant.AddProjectUserFrom;
import com.ks0100.wp.entity.Organization;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.service.LoginService;
import com.ks0100.wp.service.OrganizationService;
import com.ks0100.wp.service.ProjectService;

@Controller
@RequestMapping("/user")
public class UserController {

	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private LoginService loginService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private MailUtil mailUtil;

	@Autowired
	private ProjectService projectService;

	/**
	 * 跳至注册成功界面
	 * 
	 * @param account
	 * @param model
	 * @return
	 */
	@RequestMapping("/registerSuccess")
	public String registerSuccess(String account, ModelMap model) {
		model.put("name", account);
		return "login/registerSuccess";
	}

	@RequestMapping("/resetPasswordNext")
	public String resetPasswordNext(String account, ModelMap model) {
		model.put("name", account);
		return "login/resetPasswordNext";
	}

	/**
	 * 跳至重置密码界面
	 * 
	 * @return
	 */
	@RequestMapping("/resetPassword")
	public String resetPassword() {
		return "login/resetPassword";
	}

	@ResponseBody
	@RequestMapping(value = "/activateAccount", method = RequestMethod.POST)
	public Map<String, Object> activateAccount(String account) {
		User user = loginService.findUserByAccount(account);
		if (user != null && user.getActive() == false && StringUtils.isNotBlank(user.getActiveUuid())) {
			Map<String, Object> pars = new HashMap<String, Object>();
			pars.put("userName", user.getName());
			String href = ReadPropertiesUtil.getStringContextProperty("web.domain") + BusinessConstant.ACTIVE_URL;
			href += "?activeUuid=" + user.getActiveUuid();
			pars.put("href", href);
			Map<String, String> emails = new HashMap<String, String>();
			emails.put(user.getAccount(), user.getName());

			if (mailUtil.sendMail(ReadPropertiesUtil.getStringContextProperty("mail.smtp.username"), BusinessConstant.EAMTL_TITTLE_ACTIVE, emails, null, pars, BusinessConstant.EMAIL_FTL_ACTIVE)) {
				return ResultDataJsonUtils.successResponseResult(ReadPropertiesUtil.getStringContextProperty("active_email_success"));
			} else {
				return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("email_failure"));
			}
		} else {
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("link_failure"));
		}
	}

	/**
	 * 激活账户
	 * 
	 * @param activeUuid
	 * @return
	 */
	@RequestMapping(value = "/activate", method = RequestMethod.GET)
	public String activate(String activeUuid) {
		User user = loginService.findUserByActiveUuid(activeUuid);
		if (user != null && user.getActive() == false) {
			if (isTimeOut(new DateTime(user.getActiveUuidTime()), new DateTime())) {
				if (user.getActive() == false && StringUtils.isNotBlank(user.getActiveUuid())) {
					user.setActive(true);
					user.setActiveUuid(null);
					user.setActiveUuidTime(null);
					loginService.updateUser(user);
					return "login/accountSuccess";
				} else {
					return "login/activateFail";
				}
			} else {
				return "login/activateOverdue";
			}
		} else {
			return "login/activateFail";
		}

	}

	@ResponseBody
	@RequestMapping(value = "/reset", method = RequestMethod.POST)
	public Map<String, Object> reset(String account) {
		User user = loginService.findUserByAccount(account);
		if (user != null && user.getActive()) {
			String uuid = null;
			do {
				uuid = UUID.randomUUID().toString();
			} while (loginService.findUserByPasswordUuid(uuid) != null);
			user.setPasswordUuid(uuid);
			user.setPasswordUuidTime(new Date());
			loginService.updateUser(user);
			if (StringUtils.isNotBlank(user.getPasswordUuid())) {
				Map<String, Object> pars = new HashMap<String, Object>();
				pars.put("userName", user.getName());
				String href = ReadPropertiesUtil.getStringContextProperty("web.domain") + BusinessConstant.RESET_URL;
				href += "?passwordUuid=" + user.getPasswordUuid();
				pars.put("href", href);
				Map<String, String> emails = new HashMap<String, String>();
				emails.put(user.getAccount(), user.getName());

				if (mailUtil.sendMail(ReadPropertiesUtil.getStringContextProperty("mail.smtp.username"), BusinessConstant.EAMTL_TITTLE_RESET, emails, null, pars, BusinessConstant.EAMIL_FTL_RESETPASSWORD)) {
					return ResultDataJsonUtils.successResponseResult(ReadPropertiesUtil.getStringContextProperty("reset_email_success"));
				} else {
					return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("email_failure"));
				}
			}
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("link_failure"));
		} else if (user != null && !user.getActive()) {
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("reset_email_error"));
		} else {
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("updateUser_notExistUser"));
		}

	}

	@RequestMapping("/toResetPassword")
	public String resetPassword(String passwordUuid, ModelMap model) {
		User user = loginService.findUserByPasswordUuid(passwordUuid);
		if (user != null) {
			if (isTimeOut(new DateTime(user.getPasswordUuidTime()), new DateTime())) {
				model.put("account", user.getAccount());
				return "login/resetPassword2";

			} else {
				return "login/accountOverdue";
			}
		} else {
			return "login/accountFail";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
	public Map<String, Object> updatePassword(String account, String password) {
		User user = loginService.findUserByAccount(account);
		if (StringUtils.isNotBlank(user.getPasswordUuid())) {
			SecureRandomNumberGenerator secureRandomNumberGenerator = new SecureRandomNumberGenerator();
			String salt = secureRandomNumberGenerator.nextBytes().toHex();
			// 组合username,两次迭代，对密码进行加密
			String password_cipherText = new Md5Hash(password, account + salt, 2).toBase64();
			user.setPassword(password_cipherText);
			user.setSalt(salt);
			user.setPasswordUuid(null);
			user.setPasswordUuidTime(null);
			loginService.updateUser(user);
			return ResultDataJsonUtils.successResponseResult();
		} else {
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("update_pwd_failure"));
		}
	}

	@RequestMapping("/passwordSuccess")
	public String passwordSuccess() {
		return "login/passwordSuccess";
	}

	public static boolean isTimeOut(DateTime d1, DateTime d2) {
		return d2.isBefore(d1.plusDays(7));
	}

	/**
	 * 跳至登录界面
	 * 
	 * @return 创建日期：2014-12-1 修改说明：
	 * @author chengls
	 */
	@RequestMapping("/toLogin")
	public String tologin() {
		return "login/login";
	}

	/**
	 * 跳至注册界面
	 * 
	 * @return 创建日期：2014-12-1 修改说明：
	 * @author chengls
	 */
	@RequestMapping("/toRegister")
	public String toRegister(String name, ModelMap model) {
		model.put("name", name.replace("'", ""));
		return "login/register";
	}

	
	
	/**
	 * 处理登录请求
	 * 
	 * @param user
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@ResponseBody
	@RequestMapping(value = "/loginIn", method = RequestMethod.POST)
	public Map loginIn(User user) {
		User user1 = loginService.findUserByAccount(user.getAccount());
		if (user1 != null) {
			// 组合username,两次迭代，对密码进行加密
			String password_cipherText = new Md5Hash(user.getPassword(), user.getAccount() + user1.getSalt(), 2).toBase64();
			user.setPassword(password_cipherText);
			user.setSalt(user1.getSalt());
		}
		// 收集身份/凭据信息
		AuthenticationToken token = new UsernamePasswordToken(user.getAccount(), user.getPassword().toCharArray());
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			subject.logout();
		}
		try {
			if (!subject.isAuthenticated()) {
				// 提交身份/凭据信息
				subject.login(token);
			}
		} catch (UnknownAccountException e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("login_commonError"));
		} catch (IncorrectCredentialsException e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("login_commonError"));
		} catch (Exception e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("login_commonError"));
		}
		subject.hasRole("system");
		return ResultDataJsonUtils.successResponseResult();
	}

	/**
	 * 处理注册请求
	 * 
	 * @param user
	 * @return 创建日期：2014-12-1 修改说明：
	 * @author chengls
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/registerIn", method = RequestMethod.POST)
	public Map<String, Object> registeProgress(User user, String organizename) throws Exception {
		User user2 = loginService.findUserByAccount(user.getAccount());
		if (user2 != null) {
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("register_userHasExist"));
		}

		int i = (int) (Math.random() * 7 + 1);// 1-7随机数
		String path = "/static/images/header/" + i + ".jpg";

		PictureUtil p = new PictureUtil(PathUtil.ABSOLUTE_WEB_PATH + path);
		String logo = p.changeToBASE64EncoderStr();
		p.closeInputStream();
		user.setLogo(logo);
		user.setActive(false);
		String uuid = null;
		do {
			uuid = UUID.randomUUID().toString();
		} while (loginService.findUserByActiveUuid(uuid) != null);
		user.setActiveUuid(uuid);
		user.setActiveUuidTime(new Date());
		loginService.register(user);

		
		// 创建组织
		if (StringUtils.trimToNull(organizename) != null) {
			Organization org = new Organization();
			org.setName(organizename);
			// org.setCreatedBy(user.getUserId());
			organizationService.createOrganization(org, user.getUserId());
		}
		if (user.getActive() == false && StringUtils.isNotBlank(user.getActiveUuid())) {
			Map<String, Object> pars = new HashMap<String, Object>();
			pars.put("userName", user.getName());
			String href = ReadPropertiesUtil.getStringContextProperty("web.domain") +  BusinessConstant.ACTIVE_URL;
			href += "?activeUuid=" + user.getActiveUuid();
			pars.put("href", href);
			Map<String, String> emails = new HashMap<String, String>();
			emails.put(user.getAccount(), user.getName());

			boolean flag = mailUtil.sendMail(ReadPropertiesUtil.getStringContextProperty("mail.smtp.username"), "workboard激活邮件", emails, null, pars, BusinessConstant.EMAIL_FTL_ACTIVE);

			if (!flag) {
				return ResultDataJsonUtils.errorResponseResult(user.getName()+"已经注册成功，但是发送给您的激活邮件失败，您必须确保您的邮箱真实有效性，之后您可以登录系统，在个人设置中再次发送激活邮件。");
			}
		}

//		if (StringUtils.trimToNull(organizename) == null) {
////			request.setAttribute("version", CommonConstant.VERSION) ;
////			request.setAttribute("enable",true) ;
////			request.getRequestDispatcher("/WEB-INF/resource/jsp/version/version.jsp")
////					.forward(request, response);
////			response.sendRedirect(request.getContextPath()+"/resource/jsp/login/login.htm");
//			return ResultDataJsonUtils.successResponseResult();
//		}

		

		return ResultDataJsonUtils.successResponseResult();

	}

	/**
	 * 登陆并同意加入组织(当前未登陆，需要登陆)
	 * 
	 * @param code
	 * @param account
	 * @param password
	 * @return 创建日期：2014-12-8 修改说明：
	 * @author chengls
	 */
	@ResponseBody
	@RequestMapping(value = "/agreetojoinorg", method = RequestMethod.POST, params = "type=nologin")
	public Map<String, Object> agreeToJoinOrgAndLogin(String code, String account, String password) {
		User user = new User();
		user.setAccount(account);
		user.setPassword(password);
		User user1 = loginService.findUserByAccount(account);
		if (user1 != null) {
			// 组合username,两次迭代，对密码进行加密
			String password_cipherText = new Md5Hash(password, account + user1.getSalt(), 2).toBase64();
			user.setPassword(password_cipherText);
			user.setSalt(user1.getSalt());
		}
		// 登陆 收集身份/凭据信息
		AuthenticationToken token = new UsernamePasswordToken(user.getAccount(), user.getPassword());
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			subject.logout();
		}
		try {
			if (!subject.isAuthenticated()) {
				// 提交身份/凭据信息
				subject.login(token);
			}
		} catch (UnknownAccountException e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("login_commonError"));
		} catch (IncorrectCredentialsException e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("login_commonError"));
		} catch (Exception e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("login_commonError"));
		}
		// 登陆成功，判断链接有效性
		Organization organization = organizationService.findByCode(code);
		if (organization == null) {
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("orgUrl_errorOrUnuserful"), "error_1");
		}
		// 调用加入组织方法
		ShiroUser user2 = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		return userAddOrg(user2.getUser(), organization);
	}

	/**
	 * 登陆并同意加入项目(当前未登陆，需要登陆)
	 * 
	 * @param code
	 * @param account
	 * @param password
	 * @return 创建日期：2014-12-15 修改说明：
	 * @author zhangpengju
	 */
	@ResponseBody
	@RequestMapping(value = "/agreetojoinpro", method = RequestMethod.POST, params = "type=nologin")
	public Map<String, Object> agreeToJoinProAndLogin(String code, String account, String password) {
		User user = new User();
		user.setPassword(password);
		user.setAccount(account);
		User user1 = loginService.findUserByAccount(account);
		if (user1 != null) {
			// 组合username,两次迭代，对密码进行加密
			String password_cipherText = new Md5Hash(password, account + user1.getSalt(), 2).toBase64();
			user.setPassword(password_cipherText);
			user.setSalt(user1.getSalt());
		}
		// 登陆 收集身份/凭据信息
		AuthenticationToken token = new UsernamePasswordToken(user.getAccount(), user.getPassword());
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			subject.logout();
		}
		try {
			if (!subject.isAuthenticated()) {
				// 提交身份/凭据信息
				subject.login(token);
			}
		} catch (UnknownAccountException e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("login_commonError"));
		} catch (IncorrectCredentialsException e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("login_commonError"));
		} catch (Exception e) {
			logger.error("error:", e);
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("login_commonError"));
		}
		// 登陆成功，判断链接有效性
		Project pro = projectService.findByCode(code);
		if (pro == null) {
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("proUrl_errorOrUnuserful"), "error_2");
		}
		ShiroUser user2 = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		return userAddProject(user2.getUser(), pro);
	}

	private Map<String, Object> userAddProject(User user, Project pro) {
		if (pro.getOrganization() != null) {
			if (organizationService.existOrgUser(pro.getOrganization().getId(), user.getUserId())) {
				int isok = addUser(pro.getProjectId(), user, AddProjectUserFrom.FROM_URL);
				if (isok == 0) {
					return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("useAlreadyrInProject"), "error_1");
				} else if (isok == 2) {
					return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("addFailure"), "error_3");
				}
			} else {
				return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("userNoExistOrgs"), "error_0");
			}

		} else {
			int isok = addUser(pro.getProjectId(), user, AddProjectUserFrom.FROM_URL);
			if (isok == 0) {
				return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("useAlreadyrInProject"), "error_1");
			} else if (isok == 2) {
				return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("addFailure"), "error_3");
			}
		}
		return ResultDataJsonUtils.successResponseResult();
	}

	private Map<String, Object> userAddOrg(User user, Organization org) {
		int isok = organizationService.addUser(org.getId(), user.getUserId(), BusinessConstant.RoleConstant.ORG_MEMBER);
		if (isok == 0) {
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil.getStringContextProperty("org_isMember"), "error_2");
		}
		return ResultDataJsonUtils.successResponseResult();
	}

	public int addUser(int prjId, User user, String addFrom) {
		if (!projectService.ExistUserPro(user, prjId)) {
			if (projectService.saveUserPro(user, prjId, addFrom)) {
				return 1; // 添加成功
			} else {
				return 2; // 添加失败
			}
		} else {
			return 0; // 数据已存在
		}
	}

	/**
	 * 通过url请求邀请成员加入组织
	 * 
	 * @param code
	 * @return 创建日期：2014-12-5 修改说明：
	 * @author chengls
	 */
	@RequestMapping("/invitedToJoinOrg/{code}")
	public String invitedToJoinOrg(@PathVariable("code") String code, Model model) {
		// 若链接失效，跳至链接失效界面
		if (StringUtils.trimToNull(code) == null) {
			model.addAttribute("errorMsg", ReadPropertiesUtil.getStringContextProperty("login_notExistUser"));
			return "invitedToJoin/invitedUrlUnuse";
		}
		Organization org = organizationService.findByCode(code);

		if (org == null) {
			model.addAttribute("errorMsg", ReadPropertiesUtil.getStringContextProperty("orgUrl_errorOrUnuserful"));
			return "invitedToJoin/invitedUrlUnuse";
		}

		// 若链接可用，则跳至同意并登录界面，并把是否登录参数和邀请组织参数传给该页面
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			model.addAttribute("isLogin", true);
		}
		model.addAttribute("org", org);

		model.addAttribute("code", code);

		return "invitedToJoin/invitedToJoin";
	}

	/**
	 * 通过url请求邀请成员加入项目
	 * 
	 * @param code
	 * @return 创建日期：2014-12-15 修改说明：
	 * @author zhangpengju
	 */
	@RequestMapping("/invitedToJoinPro/{code}")
	public String invitedToJoinPro(@PathVariable("code") String code, Model model) {
		// 若链接失效，跳至链接失效界面
		if (StringUtils.trimToNull(code) == null) {
			model.addAttribute("errorMsg", ReadPropertiesUtil.getStringContextProperty("login_notExistUser"));
			return "invitedToJoin/invitedProUrlUnuse";
		}
		Project pro = projectService.findByCode(code);
		if (pro == null) {
			model.addAttribute("errorMsg", ReadPropertiesUtil.getStringContextProperty("proUrl_errorOrUnuserful"));
			return "invitedToJoin/invitedProUrlUnuse";
		}
		String name = pro.getInviter().getName();
		// 若链接可用，则跳至同意并登录界面，并把是否登录参数和邀请项目参数传给该页面
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			model.addAttribute("isLogin", true);
		}
		model.addAttribute("pro", pro);
		model.addAttribute("code", code);
		model.addAttribute("inviter", name);

		return "invitedToJoin/invitedToJoinPro";
	}

}
