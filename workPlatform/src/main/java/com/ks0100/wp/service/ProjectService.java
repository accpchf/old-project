package com.ks0100.wp.service;

import java.util.List;
import java.util.Map;

import com.ks0100.wp.dto.ProjectDto;
import com.ks0100.wp.dto.UserDto;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.Team;
import com.ks0100.wp.entity.User;

public interface ProjectService {
	
	/**
	 * 个人参与的所有项目
	 * @param userId
	 * @return
	 */
	public Map<String, Object> findAllProjectsByUser(int userId);
	
	/**
	 * 个人参与的所有项目 for mobile
	 * @param userId
	 * @return
	 */
	public Map<String, Object> findAllProjectsByUserForMobile(int userId,String status);
	
	/**
	 * 个人参与的所有项目
	 * @param userId
	 * @return
	 */
	List<Project> listHomeProjects(int userId);
	
	/**
	 * 个人参与的所有项目 for mobile
	 * @param userId
	 * @return
	 */
	List<Project> listHomeProjectsForMobile(int userId,String status);
	
	/**
	 * 更新项目
	 * @param p
	 */
	boolean updateProject(Project p);
	/**
	 * 添加项目
	 * @param pro
	 */
	void createProject(Project pro);
	
	/**
	 * 根据proId查询项目
	 */
	Project findProjectById(int proId);
	
	void updateProCommonUse(int proId);
	
	/**
	 * 根据pId删除项目
	 */
	void deletePro(Project project);
	
	/**
	 * 退出项目
	 * @param proId
	 */
	void exitPro(User user,Project project, int type);
	/**
	 * 查找邀请code
	 * @param pId
	 * @return
	 */
	String findCode(int pId);
	
	/**
	 * 判断生成的url链接是否已经存在
	 */
	boolean existCode(String uuid);
	
	/**
	 * 获取项目人员
	 * @param projectId 项目id
	 * @return
	 */
	Map<String, Object> listProjectUser(int projectId);
	
	/**
	 * 获取项目人员,包括团队
	 * @param projectId 项目id
	 * @return
	 */
    Map<String, Object> listProjectAllUser(int projectId);
	/**
	 * 根据项目邀请code查询项目
	 * @param code
	 * @return
	 */
	Project findByCode(String code);
	
	//int addUser(int prjId, User user,String addFrom);
	
	//boolean addOrgsUser(int orgId,int proId, User user);
	
   // int canAddOrgsUser(int orgId, int proId, int userId);
	
	boolean eixstPro(int proId, int userId);
	
	List<Project> findProjectByOrganizationId(int orgId,String orgName);
	
	boolean removeTeamByPro(Team team, int proId);
	/**
	 * 判断组织中的团队是否已经存在项目中
	 * @param teamId
	 * @param proId
	 * @return
	 */
	boolean teamExistPro(int teamId, int proId);
	/**
	 * 根据项目Id和团队Id给项目中添加团队
	 * @param teamId
	 * @param proId
	 */
	void addTeamForPro(Team team, int proId);
	
	List<Project> findProByTeamId(int teamId);
	
	/**
	 * 根据项目uuid查询项目
	 */
	Project findProByuuid(String uuid);
	
	/**
	 * 获取项目统计
	 * @param projectId
	 * @return
	 */
	Map<String, Object> findProjectStatistics(int projectId, int loadType);
	
	Project findProjectBysql(int projectId);
	
	void setAdmin(User user,int projectId);
	
	void setSupervise(User user, int projectId);
	
	void setMember(User user, int projectId);
	
	public boolean ExistUserPro(User user, int prjId);
	
	public boolean saveUserPro(User user, int prjId,String addForm);
	
	public boolean isExistUserPro(User user,int prjId);
	
	public boolean saveUserIfExist(User user, int prjId,String addForm);
	
	public byte[] findLogoByProUuidForMobile(int id);
	
	
	List<Map<String,Object>> findProjectByStatus(int userId, String status, int orgId);
	
	Map<String, Object> findProjectByStatusForMobile(int userId, String status, int orgId);
	
	Map<String,Object> findProjectInfoByProIdForMobile(int projectId);
	
	List<ProjectDto> findProjectByUserIdForMobile(int userId);
	
	List<UserDto> findUserByProjectIdForMobile(int projectId);
}
