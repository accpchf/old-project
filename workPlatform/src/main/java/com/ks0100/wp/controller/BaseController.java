package com.ks0100.wp.controller;

import java.util.List;

import org.apache.shiro.SecurityUtils;

import com.ks0100.wp.constant.BusinessConstant.PermitCodeAndId;

public class BaseController {

	
	/**
	 * 是否有权限
	 * @param permitCode
	 * @param id
	 * @return
	 */
	public boolean hasPermit(String permitCode,int id){
		permitCode=permitCode+":"+id;
		return SecurityUtils.getSubject().isPermitted(permitCode);
		
	}
	
	/**
	 * 判断是否拥有一个权限
	 * @param list
	 * @return
	 */
	public boolean hasPermit(List<PermitCodeAndId> list){
		boolean hasPermit=false;
		if(list!=null){
			for(PermitCodeAndId pi:list){
				if(SecurityUtils.getSubject().isPermitted(pi.getPermitCode()+":"+pi.getId())){
					hasPermit=true;
					break;
				}
			}
		}
		return hasPermit;
	}
	
	/**
	 * 是否有项目访问权限
	 * @param projectId
	 * @return
	 */
/*	public boolean hasProjectAccessPermit(String permitCode,int projectId){
		if(PermitConstant.PRJ_ACCESS.equals(permitCode)
				||PermitConstant.PRJ_HIGH_ACCESS.equals(permitCode)
				||PermitConstant.PRJ_SUPERVISER_ACCESS.equals(permitCode)
				||PermitConstant.PRJ_ADMIN_ACCESS.equals(permitCode)){
			permitCode=permitCode+":"+projectId;
			return SecurityUtils.getSubject().isPermitted(permitCode);
		}else{
			return false;
		}

	}*/

}
