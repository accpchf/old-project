package com.ks0100.wp.controller;

import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.common.ehcache.CacheUtil;
import com.ks0100.common.util.MailUtil;
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
@RequestMapping("/login")
public class LoginController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private LoginService loginService;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private ProjectService projectService;

	@Autowired
	private MailUtil mailUtil;
	/**
	 * 没有访问权限
	 * @return
	 */
	@RequestMapping("/unauthorized")
	public String unauthorized() {
		return "login/unauthorized";
	}

	/**
	 * 注销
	 *
	 * @return 创建日期：2014-12-4 修改说明：
	 * @author chengls
	 */
	@RequestMapping("/loginout")
	public String loginOut() {
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated()) {
			subject.logout();
		}
		return "login/login";
	}

	/**
	 * 修改个人基本信息
	 *
	 * @param user
	 * @return 创建日期：2014-12-3 修改说明：
	 * @author chengls
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/updatePersionInfo", method = RequestMethod.POST)
	public Map<String, Object> updatePersonalInfo(HttpServletRequest request,User user) throws Exception {
		ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject()
				.getPrincipal();
		User oldUser = loginService.findUserByAccount(user.getAccount());
		if (oldUser == null) {
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
					.getStringContextProperty("updateUser_notExistUser"));
		}
		oldUser.setName(user.getName());
		oldUser.setMobile(user.getMobile());
		oldUser.setPosition(user.getPosition());
		oldUser.setGender(user.getGender());

		// 转型为MultipartHttpRequest：
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		// 获得文件：
		MultipartFile file = multipartRequest.getFile("headerPic");
		// 获得文件名：
		// String filename = file.getOriginalFilename();
		// 获得输入流：
		if (file != null && file.getBytes() != null && file.getSize() > 0) {
			// InputStream input = file.getInputStream();
			PictureUtil pic = new PictureUtil(file.getInputStream());
			InputStream in = pic.toComprress();
			String logo = pic.changeToBASE64EncoderStr(in);
			pic.closeInputStream();
			oldUser.setLogo(logo);
		}
		loginService.saveUser(oldUser);
		currentUser.setName(oldUser.getName());
		currentUser.setMobile(oldUser.getMobile());
		currentUser.setPosition(oldUser.getPosition());
		currentUser.setGender(oldUser.getGender());
		currentUser.setLogo(oldUser.getLogo());

		if(CacheUtil.removePageEhcahce(CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm")){
			logger.debug("已经清理该页面缓存："+CacheUtil.PAGE_KEY_LOGIN_SUCCESSTOINDEX+user.getAccount()+".htm");
		}
		
		
		return ResultDataJsonUtils.successResponseResult(oldUser);
	}

	/**
	 * 修改个人安全信息（修改密码）
	 *
	 * @param user
	 * @return 创建日期：2014-12-3 修改说明：
	 * @author chengls
	 */
	@ResponseBody
	@RequestMapping(value = "/updateSafeInfo", method = RequestMethod.POST)
	public Map<String, Object> updateSafeInfo(String account, String password,
			String newpwd) {
		User oldUser = loginService.findUserByAccount(account);
		String password_cipherText1 = "";
		if (oldUser == null) {
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
					.getStringContextProperty("updateUser_notExistUser"));
		}else{
			password_cipherText1= new Md5Hash(password,account+oldUser.getSalt(),2).toBase64(); 
		}
		
		if (StringUtils.trimToNull(password) == null
				|| !password_cipherText1.equals(oldUser.getPassword())) {
			return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
					.getStringContextProperty("updateUser_pwdError"));
		}
		SecureRandomNumberGenerator secureRandomNumberGenerator=new SecureRandomNumberGenerator(); 
        String salt= secureRandomNumberGenerator.nextBytes().toHex(); 
        //组合username,两次迭代，对密码进行加密 
        String password_cipherText= new Md5Hash(newpwd,account+salt,2).toBase64(); 
        oldUser.setPassword(password_cipherText); 
        oldUser.setSalt(salt);   
//		oldUser.setPassword(newpwd);
		loginService.saveUser(oldUser);
		return ResultDataJsonUtils.successResponseResult();
	}

	
	
	/**
	 * 同意加入项目（当前已登录，直接加入项目）
	 *
	 * @param code
	 * @param account
	 * @param password
	 * @return 创建日期：2014-12-15 修改说明：
	 * @author zhangpengju
	 */
	@ResponseBody
	@RequestMapping(value = "/agreetojoinorg", method = RequestMethod.POST, params = "type=haslogin")
	public Map<String, Object> agreetojoinorg(String code) {
		Organization org = organizationService.findByCode(code);
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		return userAddOrg(user.getUser(), org);
	}

	private Map<String, Object> userAddOrg(User user, Organization org) {
		int isok = organizationService.addUser(org.getId(), user.getUserId(),
				BusinessConstant.RoleConstant.ORG_MEMBER);
		if (isok == 0) {
			return ResultDataJsonUtils
					.errorResponseResult(ReadPropertiesUtil
							.getStringContextProperty("org_isMember"),
							"error_2");
		}
		return ResultDataJsonUtils.successResponseResult();
	}

	
	/**
	 * 同意加入项目（当前已登录，直接加入项目）
	 *
	 * @param code
	 * @param account
	 * @param password
	 * @return 创建日期：2014-12-15 修改说明：
	 * @author zhangpengju
	 */
	@ResponseBody
	@RequestMapping(value = "/agreetojoinpro", method = RequestMethod.POST, params = "type=haslogin")
	public Map<String, Object> agreetojoinpro(String code) {
		Project pro = projectService.findByCode(code);
		ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		return userAddProject(user.getUser(), pro);
	}

	private Map<String, Object> userAddProject(User user, Project pro) {
		if (pro.getOrganization() != null) {
			if (organizationService.existOrgUser(pro.getOrganization().getId(),
					user.getUserId())) {
				int isok = addUser(pro.getProjectId(), user,
						AddProjectUserFrom.FROM_URL);
				if (isok == 0) {
					return ResultDataJsonUtils
							.errorResponseResult(
									ReadPropertiesUtil
											.getStringContextProperty("useAlreadyrInProject"),
									"error_1");
				} else if (isok == 2) {
					return ResultDataJsonUtils.errorResponseResult(
							ReadPropertiesUtil
									.getStringContextProperty("addFailure"),
							"error_3");
				}
			} else {
				return ResultDataJsonUtils.errorResponseResult(
						ReadPropertiesUtil
								.getStringContextProperty("userNoExistOrgs"),
						"error_0");
			}

		} else {
			int isok = addUser(pro.getProjectId(), user,
					AddProjectUserFrom.FROM_URL);
			if (isok == 0) {
				return ResultDataJsonUtils
						.errorResponseResult(
								ReadPropertiesUtil
										.getStringContextProperty("useAlreadyrInProject"),
								"error_1");
			} else if (isok == 2) {
				return ResultDataJsonUtils.errorResponseResult(
						ReadPropertiesUtil
								.getStringContextProperty("addFailure"),
						"error_3");
			}
		}
		return ResultDataJsonUtils.successResponseResult();
	}

	/**
	 * 跳至加入组织成功界面
	 *
	 * @return 创建日期：2014-12-8 修改说明：
	 * @author chengls
	 */
	@RequestMapping("/tojoinsuccess")
	public String toJoinSuccess(String name, ModelMap model) {
		model.put("orgName", name);
		return "invitedToJoin/joinSuccess";
	}

	/**
	 * 跳至加入项目成功界面
	 *
	 * @return 创建日期：2014-12-15 修改说明：
	 * @author zhangpengju
	 */
	@RequestMapping("/tojoinprosuccess")
	public String toJoinProSuccess(String name, ModelMap model) {
		model.put("proname", name);
		return "invitedToJoin/joinProSuccess";
	}

	/**
	 * 跳至已经存在项目界面
	 *
	 * @return 创建日期：2014-12-15 修改说明：
	 * @author zhangpengju
	 */
	@RequestMapping("/userexistpro")
	public String userexistpro(String name, ModelMap model) {
		model.put("proname", name);
		return "invitedToJoin/userexistpro";
	}

	/**
	 * 跳至已经存在组织界面
	 *
	 * @return 创建日期：2015-1-29 修改说明：
	 * @author zhangpengju
	 */
	@RequestMapping("/userexistOrg")
	public String userexistOrg(String name, ModelMap model) {
		model.put("orgname", name);
		return "invitedToJoin/userexistOrg";
	}

	/**
	 * 跳至不属于该项目所属组织界面
	 *
	 * @return 创建日期：2014-12-15 修改说明：
	 * @author zhangpengju
	 */
	@RequestMapping("/userNotBelongOrg")
	public String userNotBelongOrg(String name, ModelMap model) {
		model.put("proname", name);
		return "invitedToJoin/userNotBelongOrg";
	}

	/**
	 * 跳入加入组织失败界面
	 *
	 * @return 创建日期：2014-12-8 修改说明：
	 * @author chengls
	 */
	@RequestMapping("/tojoinfailure")
	public String toJoinFailure() {
		return "invitedToJoin/invitedUrlUnuse";
	}

	/**
	 * 跳入加入项目失败界面
	 *
	 * @return 创建日期：2014-12-15 修改说明：
	 * @author zhangpengju
	 */
	@RequestMapping("/tojoinProfailure")
	public String toJoinProFailure() {
		return "invitedToJoin/invitedProUrlUnuse";
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


}
