package com.ks0100.wp.dao;

import java.util.List;
import java.util.Map;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.Project;

public interface ProjectDao extends SimpleHibernateDao<Project,Integer>{
	
	/**
	 * 我所参与的所有的项目
	 * @param userId
	 * @return
	 */
	public List<Project> listHomeProjects(int userId);
	
	/**
	 * 查找所有的用项目 for mobile
	 * @param userId
	 * @return
	 */
	public List<Project> listHomeProejctsForMobile(int userId,String status);
	
	/**
	 * 根据proId和userId在项目和用户关联表中插入一条记录
	 * @param proId
	 * @param userId
	 * @return
	 */
	boolean saveUserPro(int proId, int userId, String roleCode);
	
	/**
	 * 更新用户是否常用
	 * @param projectId
	 * @param isCommonUse
	 * @param userId
	 */
	void updateProjectIsCommonUse(int projectId,int userId);
	
	List<Integer> findCommonProjectByUserId(int userId);
	
	/**
	 * 退出项目,即在项目和用户关联表中删除一项
	 * @param proId
	 * @param userId
	 */
	void exitPro(int userId, int proId);
	
	/**
	 * 查找一个项目的参与者，包括人员项目表和团队人员表，且项目，人员，团队都属于一个组织下
	 * @param projectId 项目id
	 * @return
	 */
	List<Map<String, Object>> listProjectAllUser(int projectId);
	
	/**
	 * 仅通过项目人员表查找，项目人员
	 * @param projectId
	 * @return
	 */
	List<Map<String, Object>> listProjectUser(int projectId);
	
	/**
	 * 根据项目Id和userId查询当前用户是否在项目中
	 * @param id
	 * @return
	 */
	boolean ExistUserPro(int userId, int proId);
	
	/**
	 * 根据项目Id和userId查询当前用户是否在项目中有记录
	 * @param id
	 * @return
	 */
	boolean isExistUserPro(int userId, int proId);
	
	/**
	 * 将成员添加到项目中,如果成员曾经在项目中
	 * @param proId
	 * @param userId
	 * @param roleCode
	 * @return
	 */
	boolean saveUserIfExist(int proId, int userId, String roleCode);

	/**
	 * 
	 */
	void deleteUserPro(int proId);
	/**
	 * 
	 */
	boolean removeTeamByPro(int teamId, int proId);
	
	boolean teamExistPro(int teamId, int proId);
	
	void addTeamForPro(int teamId, int proId);
	
	byte[] findLogoByProUuidForMobile(int id);
	
	List<Map<String,Object>> findProjectByTeamId(int teamId);
	
	List<Map<String,Object>> findProjectByOrganizationId(int orgId,String orgName);
	
	Project findProjectBysql(int projectId);
	/**
	 * 设置管理员
	 * @param projectId
	 * @param userId
	 * @param type
	 */
	void setAdmin(int projectId,int userId);
	/**
	 * 设置监督员
	 * @param projectId
	 * @param userId
	 * @param type
	 */
	void setSupervise(int projectId,int userId);
	
	/**
	 * 设置成员
	 * @param projectId
	 * @param userId
	 */
	void setMember(int projectId,int userId);
	
	/**
	 * 根据组织id查找与该成员关联的项目的管理员
	 * @param orgId
	 * @return
	 */
	List<Map<String,Object>> findProAdminByOrganizationId(int orgId);
	
	/**
	 * 根据用户id查询与之关联并且用户为管理员的项目
	 * @param userId
	 * @return
	 */
	List<Map<String,Object>> findProByUserId(int userId);
	
	
	List<Map<String,Object>> findProjectByStatus(int userId, String status, int orgId); 
	
	
	Map<String,Object> findProjectByStatusForMobile(int userId, String status, int orgId); 
	
	Project findProByProIdForMobile(int projectId);
	
	List<Map<String, Object>> findProjectByUserIdForMobile(int userId);
}
