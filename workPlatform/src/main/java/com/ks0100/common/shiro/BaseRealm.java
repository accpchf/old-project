package com.ks0100.common.shiro;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ks0100.common.util.BeanMapper;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.service.LoginService;

/**
 * 重写安全框架的验证方法
 * 
 * @author xie linming
 * 
 */
public class BaseRealm extends AuthorizingRealm {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseRealm.class);
	
	@Autowired
	private LoginService loginService;

	/**
	 * 为通过身份验证的用户添加权限
	 */
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
		
		SimpleAuthorizationInfo authorizationInfo = null;
		ShiroUser shiroUser = (ShiroUser) principals.fromRealm(getName()).iterator().next();
		if(shiroUser != null && shiroUser.getUserId() > 0) {

			  Set<String> permissions = shiroUser.getPermissions();
			  authorizationInfo = new SimpleAuthorizationInfo();

			  if(permissions != null &&permissions.isEmpty()) {

				authorizationInfo.setStringPermissions(this.loginService.findPermit(shiroUser.getUserId()));
				Set<String> stringPermissions = authorizationInfo.getStringPermissions();
				LOGGER.info("-重新添加权限---------------------------------------");
				if(stringPermissions != null) {
				  for(String s : stringPermissions) {
					LOGGER.info(s);
					permissions.add(s);
				  }
				}
			  } else {
				authorizationInfo.setStringPermissions(permissions);
			  }

			}
		return authorizationInfo;
	}

	/**
	 * 对提交的用户进行身份验证
	 */
	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
		// 获取用户的身份令牌
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		if (StringUtils.isNotBlank(token.getUsername())) {
			User user = this.loginService.findUserByAccount(token.getUsername());
			// 无需与密码比对,shiro会自动完成比对过程,只需返回一个和令牌相关的正确的验证信息
			if (user != null) {
				ShiroUser shiroUser = BeanMapper.map(user, ShiroUser.class);
				if(shiroUser!=null){
					shiroUser.setUser(user);
				}
				// 如果密码比对失败,将抛出IncorrectCredentialsException异常
				return new SimpleAuthenticationInfo(shiroUser, user.getPassword(), getName());
			}
		}
		// 没有返回对应的SimpleAuthenticationInfo时,将抛出UnknownAccountException异常
		return null;
	}
	
	/**
	 * 更新用户授权信息缓存.
	 */
/*	public void clearCachedAuthorizationInfo(Object principal) {
		SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
		clearCachedAuthorizationInfo(principals);
	}*/
}
