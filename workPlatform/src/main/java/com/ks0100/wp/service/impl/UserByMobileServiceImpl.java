package com.ks0100.wp.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks0100.wp.dao.UserByMobileDao;
import com.ks0100.wp.dto.UserDto;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.entity.UserByMobile;
import com.ks0100.wp.service.UserByMobileService;
@Service
public class UserByMobileServiceImpl implements  UserByMobileService{
	@Autowired
	private UserByMobileDao userByMobileDao;
	
	public UserByMobile findUserByMobileByAccount(String account){
		if(StringUtils.isNotBlank(account)) {
			return userByMobileDao.findUserByMobileByAccount(account);
		}else
			return null;
	}
	
	public UserByMobile findUserByMobileByUserId(int userId){
		return userByMobileDao.get(userId);
	}
	
	public void save(UserByMobile um){
		userByMobileDao.saveOrUpdate(um);
	}

	@Override
	public UserDto findUserByUserId(int userId) {
		User user = userByMobileDao.findUserByUserId(userId);
		UserDto dto = new UserDto(user.getUserId(),user.getName(),user.getPosition(),user.getMobile(),user.getAccount());
		return dto;
	}
}
