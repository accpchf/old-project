package com.ks0100.wp.service;

import java.util.List;
import java.util.Map;

import com.ks0100.wp.entity.Organization;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.Team;
import com.ks0100.wp.entity.User;

public interface OrganizationService {

	List<Organization> listOrganizationsByUser(int userId);

	int addUser(int orgId, int userId, String roleCode);

	boolean createOrganization(Organization org, int userId)  throws Exception;

	Organization findOrganizationById(int id);

	void updateOrganization(Organization org);

	List<Map<String, Object>> findUserList(int id);

	boolean existCode(String invitationCode);

	Organization findByCode(String invitationCode);

	String findCode(int id);

	boolean existUserOrg(int userId, int orgId);

	boolean delOrgUser(int orgId, int userId);
	
	void setAdmin(int orgId,int userId);
	
	void cancelAdmin(int orgId,int userId);
	
	boolean exit(int orgId);
	
	Organization findOrgByuuid(String uuid);
	
	boolean existOrgUser(int orgId,int userId);
	
	/**
	 * 组织人员任务统计
	 * @param orgId
	 * @return
	 */
	List<User> findOrganizationStatistics_user(int orgId, int loadType);
	/**
	 * 组织项目任务统计
	 * @param orgId
	 * @param loadType
	 * @return
	 */
	List<Project> findOrganizationStatistics_project(int orgId, int loadType);
	/**
	 * 删除组织
	 * @param org
	 */
	void deleteOrg(Organization org);

	List<Team> findOrganizationStatistics_team(int orgId, int loadType);
	
	/**
	 * 设置监督员
	 * @param orgId
	 * @param userId
	 */
	void setAdministrator(int orgId,int userId, String roleCode);
	
	/**
	 * 组织人员工时统计
	 * @param orgId
	 * @return
	 */
	List<User> findOrganizationTimeStatistics_user(int orgId, int loadType);
	
	/**
	 * 组织项目工时统计
	 * @param orgId
	 * @param loadType
	 * @return
	 */
	List<Project> findOrganizationTimeStatistics_project(int orgId, int loadType);
	/**
	 * 组织团队工时统计
	 * @param orgId
	 * @return
	 */
	List<Team> findOrganizationTimeStatistics_team(int orgId, int loadType);
	
	/**
	 * 判断用户是否为项目仅有的一个管理员
	 * @param userId
	 * @param orgId
	 * @return
	 */
	boolean JustOneAdminForPro(int userId,int orgId);
}
