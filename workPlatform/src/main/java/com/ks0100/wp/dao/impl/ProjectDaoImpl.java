package com.ks0100.wp.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.HibernateDaoImpl;
import com.ks0100.common.util.PictureUtil;
import com.ks0100.wp.constant.BusinessConstant.RoleConstant;
import com.ks0100.wp.dao.ProjectDao;
import com.ks0100.wp.entity.Organization;
import com.ks0100.wp.entity.Project;

@Repository
@SuppressWarnings("unchecked")
public class ProjectDaoImpl extends HibernateDaoImpl<Project,Integer> implements ProjectDao {
	
	public List<Project> listHomeProjects(int userId){
		StringBuilder listHomeProjects_sql=new StringBuilder()
		.append("select distinct pp.id \"projectId\",pp.uuid ,pp.name ,")
		.append(" pp.logo , pp.oid  \"organization.id\" , pp.ouuid \"organization.uuid\",pp.oname \"organization.name\" ,pp.status status ")
		.append("from ")
		.append("(select p.id ,p.name ,p.uuid , ")
		.append("p.logo , null oid ,null oname,null ouuid ,p.status status ")
		.append("from  tbl_project p,tbl_user_project up ")
		.append("where p.tbl_organization_id is null ")
		.append("and  p.id=up.tbl_project_id  and up.tbl_user_id =:userId and p.enabled=1 and up.enabled=1  ")
		.append(" union ")
		.append("select p.id ,p.name ,p.uuid, ")
		.append("p.logo ,  o.id  oid ,o.name oname ,o.uuid ouuid , p.status status ")
		.append("from ")
		.append("tbl_project p ,tbl_user_project up, tbl_organization o,tbl_user_organization uo  ")
		.append("where ")
		.append("p.id=up.tbl_project_id ")
		.append(" and up.tbl_user_id =:userId and p.enabled=1 and up.enabled=1 ")
		.append("and  o.id=p.tbl_organization_id and uo.tbl_organization_id = o.id and o.enabled=1 ")
		.append("and uo.tbl_user_id=up.tbl_user_id and uo.enabled=1  ")
		.append(" union ")
		.append("select distinct  p.id,p.name,p.uuid,p.logo,o.id oid,o.name oname,o.uuid ouuid,p.status status ")
		.append("from tbl_project p , tbl_organization o,tbl_user_organization uo,tbl_project_team ")
		.append("pt,tbl_team t,tbl_user_team ut where ")
		.append(" p.enabled=1")
		.append(" and o.id=p.tbl_organization_id and uo.enabled =1 and uo.tbl_organization_id = o.id and o.enabled=1 ")
		.append("and p.id = pt.tbl_project_id and ut.tbl_team_id = pt.tbl_team_id and ut.tbl_user_id = :userId ) pp ")
		.append("order by  pp.oid ");
		
		Query  query = createSQLQuery(listHomeProjects_sql.toString());
		query.setParameter("userId", userId);
		List<Map<String, Object>> list=query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	    List<Integer> commonProjects = findCommonProjectByUserId(userId);
		
		List<Project> projectList=new ArrayList<Project>();
		Project project=null;
		Organization organization=null;
		if(!list.isEmpty()){
			for(Map<String, Object> map:list){
				project=new Project();
				project.setProjectId((Integer)map.get("projectId"));
				project.setStatus((String)map.get("status"));
				project.setName((String)map.get("name"));
				
				if(commonProjects.contains(project.getProjectId())){
					project.setCommonUse(true);
				}else{
					project.setCommonUse(false);
				}
				
				project.setLogo((byte[])map.get("logo"));
				project.setUuid((String)map.get("uuid"));
				if(project.getLogo()!=null&&project.getLogo().length>0){
					project.setLogoStr(PictureUtil.changeToBASE64EncoderStr(project.getLogo()));
				}
				
				Integer oid=(Integer)map.get("organization.id");
				if(oid!=null){
					organization=new Organization();
					organization.setId(oid);
					organization.setName((String)map.get("organization.name"));
					organization.setUuid((String)map.get("organization.uuid"));
					project.setOrganization(organization);
				}
				
				if(!projectList.contains(project)){
					projectList.add(project);
				}
			}
		}
		return projectList;
	}



	@Override
	public void updateProjectIsCommonUse(int projectId,int userId) {
		if(!findCommonProjectByUserId(userId).contains(projectId)){
			String sql = "insert into tbl_user_common_project (tbl_user_id,tbl_project_id) values (?,?) ";
			createSQLQuery(sql, userId, projectId).executeUpdate();
		}else{
			String sql = "delete from tbl_user_common_project where tbl_user_id=? and tbl_project_id=?";
			createSQLQuery(sql, userId, projectId).executeUpdate();
		}
	}

	@Override
	public boolean saveUserPro(int  proId, int userId, String roleCode) {
		String sql = "insert into tbl_user_project (tbl_user_id,tbl_project_id,tbl_role_code) values(?,?,?)";
		int i = this.createSQLQuery(sql, userId, proId, roleCode).executeUpdate();
		return i > 0 ? true : false;
	}
	
	public void exitPro(int userId,int proId){
		String sql = "update tbl_user_project set enabled = 0  where tbl_user_id = ? and tbl_project_id = ?";
		createSQLQuery(sql, userId, proId).executeUpdate();
	}

	/**
	 * 查找一个项目的参与者，包括人员项目表和团队人员表，且项目，人员，团队都属于一个组织下
	 */
	
			
			
			
	private static StringBuilder listProjectAllUser_sql=new StringBuilder()
		.append("select up.enabled userEnabled,IFNULL(up.tbl_role_code,'"+RoleConstant.PRJ_MEMBER+"') prjRoleCode,uu.* from tbl_user_project up right join ")
		.append("(")
		.append("select u.* from tbl_user u where u.id in( ")
		.append(" select u.id from tbl_user_project up, tbl_user u , tbl_project p where up.tbl_user_id = u.id ")
		.append(" and up.tbl_project_id = :projectId  and u.enabled = 1 and p.id= up.tbl_project_id ")
		.append(" and p.tbl_organization_id is null ")
		.append(" union ")
		.append(" select u.id from tbl_user_project up, tbl_user u , tbl_project p where up.tbl_user_id = u.id ")
		.append(" and up.tbl_project_id = :projectId  and u.enabled = 1 and p.id= up.tbl_project_id ")
		.append(" and exists(select 1 from tbl_organization o , tbl_user_organization uo where o.id=p.tbl_organization_id and uo. tbl_organization_id = o.id and uo.tbl_user_id=u.id) ")
		.append(" union ")
		.append(" select u.id from tbl_team t,tbl_user_team ut ,tbl_project p ,tbl_project_team pt ,tbl_user u ")
		.append(" where t.id=ut.tbl_team_id and u.id=ut.tbl_user_id and p.id=pt.tbl_project_id and pt.tbl_team_id=ut.tbl_team_id ")
		.append(" and p.id = :projectId  and u.enabled = 1 ")
		.append(" and  exists(select 1 from tbl_organization o , tbl_user_organization uo where o.id=p.tbl_organization_id and uo.tbl_organization_id = o.id and uo.tbl_user_id=u.id) ")
		.append("  )")
		.append(")uu ")
		.append("on up.tbl_user_id =uu.id ")
		.append("and up.tbl_project_id=:projectId ");
	
	public List<Map<String, Object>> listProjectAllUser(int projectId) {
	//	String sql = "select u.id, u.name, u.logo from tbl_user_project up, tbl_user u where up.tbl_user_id = u.id and up.tbl_project_id = ? and u.enabled = 1";
		Query query = createSQLQuery(listProjectAllUser_sql.toString());
		query.setParameter("projectId", projectId);
		return (List<Map<String, Object>>)query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}

	public Project findProjectBysql(int projectId){ 
		String sql=" SELECT id, name, description, begin_time beginTime, end_time endTime, logo ,"+
					" invitation_code invitationCode, invitation_enabled invitationEnabled , created_time createTime, updated_time updateTime, enabled, status, uuid "+
					" FROM tbl_project where id=:projectId ";
		Query query=createSQLQuery(sql);
		query.setParameter("projectId", projectId);
		Map<String, Object> map=(Map<String, Object>)query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).uniqueResult();
		Project project=new Project();
		if (map != null && map.get("id") != null) {
			project.setProjectId((Integer)map.get("id"));
			project.setName((String)map.get("name"));
			project.setDescription((String)map.get("description"));
			project.setBeginTime((Date)map.get("beginTime"));
			project.setEndTime((Date)map.get("endTime"));
			project.setLogo((byte[])map.get("logo"));
			project.setStatus((String)map.get("status"));
			project.setInvitationEnabled((Boolean)map.get("invitationEnabled"));
			if(project.getLogo()!=null&&project.getLogo().length>0){
				project.setLogoStr(PictureUtil.changeToBASE64EncoderStr(project.getLogo()));
			}
		}	
		return project;
	}
			
			
	private static StringBuilder listProjectUser_sql=new StringBuilder()
			.append("select u.*,up.tbl_role_code prjRoleCode,p.tbl_organization_id orgId from tbl_user_project up, tbl_user u , tbl_project p where up.tbl_user_id = u.id ")
			.append("and up.tbl_project_id = :projectId and up.enabled = 1 and u.enabled = 1 and p.id= up.tbl_project_id ");
//			.append("and exists(select 1 from tbl_organization o , tbl_user_organization uo where o.id=p.tbl_organization_id and uo. tbl_organization_id = o.id and uo.tbl_user_id=u.id) ");
	
	/**
	 * 仅通过项目人员表查找，项目人员
	 * @param projectId
	 * @return
	 */
	public List<Map<String, Object>> listProjectUser(int projectId){
		Query query = createSQLQuery(listProjectUser_sql.toString());
		query.setParameter("projectId", projectId);
		return (List<Map<String, Object>>)query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}
	
	@Override
	public boolean ExistUserPro(int userId, int proId) {
		String sql = "SELECT count(up.tbl_user_id) from tbl_user_project up where up.tbl_project_id = ? and up.tbl_user_id = ? and up.enabled = 1";
		Query query = createSQLQuery(sql, proId, userId);
		int count = ((Number) query.uniqueResult()).intValue();
		return count > 0 ? true : false;
	}



	@Override
	public void deleteUserPro(int proId) {
		String sql = "update tbl_user_project set enabled = 0 where tbl_project_id = ?";
		createSQLQuery(sql, proId).executeUpdate();
	}



	@Override
	public boolean removeTeamByPro(int teamId, int proId) {
		String sql = "delete from  tbl_project_team where tbl_project_id = ? and tbl_team_id = ?";
		int i = this.createSQLQuery(sql, proId ,teamId).executeUpdate();
		return i > 0 ? true : false;
	}



	@Override
	public boolean teamExistPro(int teamId, int proId) {
		String sql = "SELECT count(pt.tbl_team_id) from tbl_project p, tbl_project_team pt where pt.tbl_team_id = ? and pt.tbl_project_id= ? and pt.tbl_project_id = p.id and p.enabled = 1";
		Query query = createSQLQuery(sql, teamId, proId);
		int count = ((Number) query.uniqueResult()).intValue();
		return count > 0 ? true : false;
	}



	@Override
	public void addTeamForPro(int teamId, int proId) {
		String sql = "insert into tbl_project_team (tbl_team_id, tbl_project_id) values(?,?)";
		createSQLQuery(sql, teamId, proId).executeUpdate();;
	}



	@Override
	public List<Map<String,Object>> findProjectByOrganizationId(int orgId,String orgName) {
		String sql = "select p.id,p.name,p.logo,p.status from tbl_project p where p.name like :orgName and p.tbl_organization_id=:orgId and p.enabled = 1";
		 SQLQuery query = getSession().createSQLQuery(sql);
		 query.setParameter("orgName", "%"+orgName+"%");
		 query.setParameter("orgId",orgId);
		 return (List<Map<String, Object>>)query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}



	@Override
	public List<Map<String, Object>> findProjectByTeamId(int teamId) {
		String sql = "select p.id,p.name,p.logo from tbl_project p left join tbl_project_team pt on p.id=pt.tbl_project_id where pt.tbl_team_id=:teamId and p.enabled = 1";
		SQLQuery query = getSession().createSQLQuery(sql);
		 query.setParameter("teamId", teamId);
		 return (List<Map<String, Object>>)query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}



	@Override
	public void setAdmin(int projectId, int userId) {
			String sql = "update tbl_user_project set tbl_role_code = 'PRJ_ADMIN' where tbl_user_id=? and tbl_project_id=?";
			this.createSQLQuery(sql, userId,projectId).executeUpdate();
	}
	
	
	public void setMember(int projectId, int userId){
		String sql = "update tbl_user_project set tbl_role_code = 'PRJ_MEMBER' where tbl_user_id=? and tbl_project_id=?";
		this.createSQLQuery(sql, userId,projectId).executeUpdate();
	}



	@Override
	public void setSupervise(int projectId, int userId) {
			String sql = "update tbl_user_project set tbl_role_code = 'PRJ_SUPERVISER' where tbl_user_id=? and tbl_project_id=?";
			this.createSQLQuery(sql, userId,projectId).executeUpdate();
	}



	@Override
	public List<Map<String,Object>> findProjectByStatus(int userId, String status, int orgId) {
	    String sql = "select p.status, p.id projectId,p.name ,p.uuid ," +
				"p.logo from  tbl_project p ,tbl_user_project up " +
				" where p.status = :status and p.id=up.tbl_project_id  and up.tbl_user_id =:userId and up.enabled = 1 and p.enabled=1  ";
		if(orgId > 0){
			sql += " and p.tbl_organization_id =  "+ orgId;
		}else{
			sql += "and p.tbl_organization_id is null ";
		}
		
		sql +=" union";
		sql +=" select  p.status, p.id projectId,p.name,p.uuid,p.logo ";
		sql +="from tbl_project p,tbl_user_team ut,tbl_project_team pt,tbl_team t ";
		sql +="where t.id = ut.tbl_team_id and t.tbl_organization_id = :orgId and ut.tbl_team_id = pt.tbl_team_id and ut.tbl_user_id =:userId ";
		sql +="and p.id = pt.tbl_project_id and p.status = :status"; 
		
		Query  query = createSQLQuery(sql);
		query.setParameter("status", status);
		query.setParameter("userId", userId);
		query.setParameter("orgId", orgId);
		List<Map<String, Object>> list=query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		if(list!=null){
			for(Map<String, Object> map:list){
				if((byte[])map.get("logo")!=null&&((byte[])map.get("logo")).length>0){
					map.put("logoStr", PictureUtil.changeToBASE64EncoderStr((byte[])map.get("logo")));
				}
				if(findCommonProjectByUserId(userId).contains((Integer)map.get("projectId"))){
					map.put("commonUse", true);
				}else{
					map.put("commonUse", false);
				}
			}
		}
		return list;
	}



	@Override
	public List<Integer> findCommonProjectByUserId(int userId) {
		String sql = "select tbl_project_id from tbl_user_common_project where tbl_user_id = :userId";
		Query  query = createSQLQuery(sql);
		query.setParameter("userId", userId);
		List<Map<String, Object>> list=query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		List<Integer> commonPro = new ArrayList<Integer>();
		for(Map<String,Object> map:list){
			if(!map.isEmpty()){
				commonPro.add((Integer)map.get("tbl_project_id"));
			}
		}
		return commonPro;
	}



	@Override
	public List<Map<String, Object>> findProAdminByOrganizationId(int orgId) {
		String sql = "select count(up.tbl_user_id)  as 'number',p.id from tbl_user_project up,tbl_project p "+
					 "where up.tbl_project_id=p.id and up.tbl_role_code='PRJ_ADMIN' and up.enabled = 1 and p.status = '00700' and p.enabled = 1 "+
					 "and p.tbl_organization_id = ? GROUP BY p.id";
		Query query = createSQLQuery(sql, orgId);
		List<Map<String, Object>> list = query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		return list;
	}



	@Override
	public List<Map<String, Object>> findProByUserId(int userId) {
		String sql = "select tbl_project_id from tbl_user_project where tbl_user_id = :userId and tbl_role_code = 'PRJ_ADMIN' and enabled = 1";
		Query query = createSQLQuery(sql);
		query.setParameter("userId", userId);
		List<Map<String, Object>> list=query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		return list;
	}



	@Override
	public boolean isExistUserPro(int userId, int proId) {
		String sql = "SELECT count(up.tbl_user_id) from tbl_user_project up where up.tbl_project_id = ? and up.tbl_user_id = ? and up.enabled = 0";
		Query query = createSQLQuery(sql, proId, userId);
		int count = ((Number) query.uniqueResult()).intValue();
		return count > 0 ? true : false;
	}



	@Override
	public boolean saveUserIfExist(int proId, int userId, String roleCode) {
		String sql = "update tbl_user_project set tbl_role_code = ?,enabled = 1 where tbl_user_id=? and tbl_project_id=?";
		int i = this.createSQLQuery(sql, roleCode, userId, proId)
				.executeUpdate();
		return i > 0 ? true : false;
	}



	@Override
	public List<Project> listHomeProejctsForMobile(int userId,String status) {
		StringBuilder listHomeProjects_sql=new StringBuilder()
		.append("select distinct pp.id \"projectId\",pp.uuid ,pp.name ,")
		.append(" pp.oid  \"organization.id\" , pp.ouuid \"organization.uuid\",pp.oname \"organization.name\" ,pp.status status ")
		.append("from ")
		.append("(select p.id ,p.name ,p.uuid , ")
		.append(" null oid ,null oname,null ouuid ,p.status status ")
		.append("from  tbl_project p,tbl_user_project up ")
		.append("where p.tbl_organization_id is null ")
		.append("and p.id=up.tbl_project_id  and up.tbl_user_id =:userId and p.enabled=1  ")
		.append(" union ")
		.append("select p.id ,p.name ,p.uuid, ")
		.append(" o.id  oid ,o.name oname ,o.uuid ouuid , p.status status ")
		.append("from ")
		.append("tbl_project p ,tbl_user_project up, tbl_organization o,tbl_user_organization uo  ")
		.append("where ")
		.append("p.id=up.tbl_project_id ")
		.append(" and up.tbl_user_id =:userId and p.enabled=1 ")
		.append("and  o.id=p.tbl_organization_id and uo.tbl_organization_id = o.id and o.enabled=1 ")
		.append("and uo.tbl_user_id=up.tbl_user_id and uo.enabled=1  ")
		.append(" union ")
		.append("select distinct  p.id,p.name,p.uuid,o.id oid,o.name oname,o.uuid ouuid,p.status status ")
		.append("from tbl_project p , tbl_organization o,tbl_user_organization uo,tbl_project_team ")
		.append("pt,tbl_team t,tbl_user_team ut where ")
		.append(" p.enabled=1")
		.append(" and o.id=p.tbl_organization_id and uo.enabled =1 and uo.tbl_organization_id = o.id and o.enabled=1 ")
		.append("and p.id = pt.tbl_project_id and ut.tbl_team_id = pt.tbl_team_id and ut.tbl_user_id = :userId ) pp ");
		
		if(StringUtils.isNotBlank(status)){
			listHomeProjects_sql.append("where pp.status=:status ");
		}
		//else{
		//	listHomeProjects_sql.append("where pp.status='00700' ");
		//}
		
		listHomeProjects_sql.append("order by  pp.oid ");
		
		Query  query = createSQLQuery(listHomeProjects_sql.toString());
		query.setParameter("userId", userId);
		if(StringUtils.isNotBlank(status)){
			query.setParameter("status", status);
		}
		List<Map<String, Object>> list=query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		
		List<Project> projectList=new ArrayList<Project>();
		Project project=null;
		Organization organization=null;
		if(!list.isEmpty()){
			for(Map<String, Object> map:list){
				project=new Project();
				project.setProjectId((Integer)map.get("projectId"));
				project.setStatus((String)map.get("status"));
				project.setName((String)map.get("name"));
				project.setUuid((String)map.get("uuid"));
				Integer oid=(Integer)map.get("organization.id");
				if(oid!=null){
					organization=new Organization();
					organization.setId(oid);
					organization.setName((String)map.get("organization.name"));
					organization.setUuid((String)map.get("organization.uuid"));
					project.setOrganization(organization);
				}
				
				if(!projectList.contains(project)){
					projectList.add(project);
				}
			}
		}
		return projectList;
	}



	@Override
	public byte[] findLogoByProUuidForMobile(int id) {
		String sql = "select logo from tbl_project where id =? and enabled = 1";
		Query query = createSQLQuery(sql, id);
		List<Map<String, Object>> list=query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		byte[] logo = null;
		if(!list.isEmpty()){
			for(Map<String, Object> map:list){
				if(!map.isEmpty()){
					logo = (byte[])map.get("logo");
				}
			}
		}
		return logo;
	}



	@Override
	public  Map<String, Object> findProjectByStatusForMobile(int userId,
			String status, int orgId) {
		String sql = "select p.status, p.id projectId,p.name ,p.uuid" +
				" from  tbl_project p ,tbl_user_project up " +
				" where p.status = :status and p.id=up.tbl_project_id  and up.tbl_user_id =:userId and up.enabled = 1 and p.enabled=1  ";
		if(orgId > 0){
			sql += " and p.tbl_organization_id =  "+ orgId;
		}else{
			sql += "and p.tbl_organization_id is null ";
		}
		
		sql +=" union";
		sql +=" select  p.status, p.id projectId,p.name,p.uuid ";
		sql +="from tbl_project p,tbl_user_team ut,tbl_project_team pt,tbl_team t ";
		sql +="where t.id = ut.tbl_team_id and t.tbl_organization_id = :orgId and ut.tbl_team_id = pt.tbl_team_id and ut.tbl_user_id =:userId ";
		sql +="and p.id = pt.tbl_project_id and p.status = :status"; 
		
		Query  query = createSQLQuery(sql);
		query.setParameter("status", status);
		query.setParameter("userId", userId);
		query.setParameter("orgId", orgId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list());
		map.put("status", status);
		return map;
	}



	@Override
	public Project findProByProIdForMobile(int projectId) {
		String sql = " SELECT id, name, description, begin_time beginTime, end_time endTime, "
				+ " invitation_code invitationCode, invitation_enabled invitationEnabled , created_time createTime, updated_time updateTime, enabled, status, uuid "
				+ " FROM tbl_project where id=:projectId ";
		Query query = createSQLQuery(sql);
		query.setParameter("projectId", projectId);
		Map<String, Object> map = (Map<String, Object>) query
				.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP)
				.uniqueResult();
		Project project = new Project();
		if (map != null && map.get("id") != null) {
			project.setProjectId((Integer) map.get("id"));
			project.setName((String) map.get("name"));
			project.setDescription((String) map.get("description"));
			project.setBeginTime((Date) map.get("beginTime"));
			project.setEndTime((Date) map.get("endTime"));
			project.setStatus((String) map.get("status"));
			project.setInvitationEnabled((Boolean) map.get("invitationEnabled"));
		}
		return project;
	}



	@Override
	public List<Map<String, Object>> findProjectByUserIdForMobile(int userId) {
		String sql = "select p.name,p.id from tbl_project p,tbl_user_project up where up.tbl_project_id = p.id and p.enabled = 1 and up.tbl_user_id = :userId and p.status = '00700'";
		Query query = createSQLQuery(sql);
		query.setParameter("userId", userId);
		return query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
	}
}
