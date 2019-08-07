package com.ks0100.wp.service;

import com.ks0100.wp.dto.UserDto;
import com.ks0100.wp.entity.UserByMobile;

public interface UserByMobileService {
	UserByMobile findUserByMobileByUserId(int userId);
	UserByMobile findUserByMobileByAccount(String account);
	void save(UserByMobile um);
	UserDto findUserByUserId(int userId);
}
