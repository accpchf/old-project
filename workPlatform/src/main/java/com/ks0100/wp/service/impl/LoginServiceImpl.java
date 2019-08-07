package com.ks0100.wp.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.dao.UserDao;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.service.LoginService;
@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	private UserDao userDao;

	  @SuppressWarnings("rawtypes")
	  public Set<String> findPermit(int userId) {
		Set<String> s = new HashSet<String>();
		List list = userDao.findPermit(userId);
		if(list != null) {
		  for(int i = 0; i < list.size(); i++) {
			s.add((String)list.get(i));
		  }
		}

		return s;
	  }
	  
	public User findUserByAccount(String account) {
		if (StringUtils.trimToNull(account) == null) {
			return null;
		}
		User user = userDao.findUserByAccount(account);
		return user;
	}

	@Override
	public void register(User user) {
		SecureRandomNumberGenerator secureRandomNumberGenerator=new SecureRandomNumberGenerator(); 
        String salt= secureRandomNumberGenerator.nextBytes().toHex(); 
        //组合username,两次迭代，对密码进行加密 
        String password_cipherText= new Md5Hash(user.getPassword(),user.getAccount()+salt,2).toBase64(); 
        user.setPassword(password_cipherText); 
        user.setSalt(salt);     
		userDao.save(user);
	}

	@Override
	public void saveUser(User user) {
		user.setUpdatedBy(-1);
		ShiroUser shiroUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		shiroUser.setNeedUpdate(true);
		userDao.saveOrUpdate(user);
	}
	
	public User findUser(int userId){
		return userDao.get(userId);
	}

	public List<User> findUserByIds(String ids) {
		return userDao.findUserListByIds(ids);
	}

	@Override
	public User findUserByActiveUuid(String Uuid) {
		return userDao.findUserByActiveUuid(Uuid);
	}

	@Override
	public void updateUser(User user) {
		userDao.update(user);
	}

	@Override
	public User findUserByPasswordUuid(String Uuid) {
		return userDao.findUserByPasswordUuid(Uuid);
	}

	@Override
	public String findUserLogoByTaskId(int taskId) {
		return userDao.findUserLogoByTaskId(taskId);
	}
}
