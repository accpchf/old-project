package com.ks0100.wp.dao;

import java.util.List;
import java.util.Map;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.Organization;

public interface OrganizationDao extends
		SimpleHibernateDao<Organization, Integer> {


	public List<Organization> listOrganizationsByUser(int userId);

	boolean addOrganizationUser(int userId, int oid, String roleCode);

	List<Map<String,Object>> findUserList(int id);

	boolean existOrgUser(int orgId, int userId);
	
	boolean delOrgUser(int orgId,int userId);
	
	void setAdmin(int orgId,int userId);
	
	void cancelAdmin(int orgId,int userId);
	
	int exitOrg(int orgId,int userId);
	
	boolean isExistOrgUser(int orgId,int userId);
	
	Organization findOrganizationByInvitationCode(String invitationCode);
//	void updateInviter(int oid, int inviterId);
	
	/**
	 * 获取组织人员统计情况
	 * @param orgId
	 * @return
	 */
	List<Map<String, Object>> findOrganizationStatistics_user(int orgId, int loadType);
	/**
	 * 获取组织项目统计情况
	 * @param orgId
	 * @return
	 */
	List<Map<String, Object>> findOrganizationStatistics_project(int orgId, int loadType);

	List<Map<String, Object>> findOrganizationStatistics_team(int orgId, int loadType);
	
	/**
	 * 设置或者取消组织监督员角色
	 * @param orgId
	 * @param userId
	 */
	void setAdministrator(int orgId,int userId, String roleCode);
	
	/**
	 * 添加用户如果数据库已有该记录
	 * @param userId
	 * @param orgId
	 * @return
	 */
	boolean addOrganizationUserExist(int userId,int orgId);
	
	
}
