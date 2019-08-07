package com.ks0100.wp.dao.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.HibernateDaoImpl;
import com.ks0100.common.util.BeanMapper;
import com.ks0100.wp.constant.BusinessConstant.PermitConstant;
import com.ks0100.wp.dao.UserDao;
import com.ks0100.wp.entity.User;

@Repository
public class UserDaoImpl extends HibernateDaoImpl<User,Integer> implements UserDao {
			
	@SuppressWarnings("rawtypes")
	public List findPermit(int userId) {
		String sql ="select CONCAT(permit_code,':',tbl_attachment_id)  from tbl_user_attachment ua, tbl_auth_role_permit rp where tbl_user_id=:userId and ua.tbl_role_code=role_code "+
					" union "+
					"select CONCAT(permit_code,':',tbl_organization_id)  from  tbl_user_organization uo, tbl_auth_role_permit rp where tbl_user_id=:userId and uo.tbl_role_code=role_code"+
					" union "+
					" select CONCAT(permit_code,':',tbl_meeting_id)  from tbl_user_meeting uo, tbl_auth_role_permit rp where tbl_user_id=:userId and uo.tbl_role_code=role_code "+
					" union "+
					" select CONCAT(permit_code,':',tbl_project_id)  from tbl_user_project  uo, tbl_auth_role_permit rp where tbl_user_id=:userId and uo.tbl_role_code=role_code and uo.enabled=1  "+
					" union "+
					" select CONCAT(permit_code,':',tbl_task_id)  from tbl_user_task  uo, tbl_auth_role_permit rp where tbl_user_id=:userId and uo.tbl_role_code=role_code "+
					" union "+
					" select CONCAT(permit_code,':',tbl_task_board_id)  from tbl_user_task_board  uo, tbl_auth_role_permit rp where tbl_user_id=:userId and uo.tbl_role_code=role_code "+
					" union "
					+" select CONCAT(permit_code,':',tbl_task_line_id)  from tbl_user_task_line uo, tbl_auth_role_permit rp where tbl_user_id=:userId and uo.tbl_role_code=role_code"+
					" union"+
					" select CONCAT(permit_code,':',tbl_team_id)  from tbl_user_team uo, tbl_auth_role_permit rp where tbl_user_id=:userId and uo.tbl_role_code=role_code "
					+" union "
					//团队成员，只有项目成员访问权限
					+" select CONCAT('"+PermitConstant.PRJ_ACCESS+"',':', pt.tbl_project_id) from tbl_user_team ut, tbl_project_team pt where ut.tbl_team_id=pt.tbl_team_id and ut.tbl_user_id=:userId ";
		Query query = createSQLQuery(sql);
		query.setParameter("userId", userId);
		return query.list();
	}
	  
	@SuppressWarnings("unchecked")
	public User findUserByAccount(String account)  {
		String sql = "select u.password_salt salt, u.id \"userId\",u.account \"account\",u.name \"name\",u.password \"password\",u.logo \"logo\",u.mobile \"mobile\",u.position \"position\"," +
				"u.gender \"gender\",u.active,u.active_uuid \"activeUuid\",u.password_uuid \"passwordUuid\", u.active_uuid_time \"activeUuidTime\", u.password_uuid_time \"passwordUuidTime\", s.status_content \"genderTxt\" " +
				" from tbl_user u,tbl_sys_status s "
				+ " where u.gender=s.status_id and u.enabled=1 and u.account= :account";
		Query  query = getSession().createSQLQuery(sql);
		query.setParameter("account", account.trim());
		Map<String, Object> map=(Map<String, Object>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).uniqueResult();
		if(map==null)
			return null;
		else			
			return BeanMapper.map(map,User.class);
		
	}
	
	
	@SuppressWarnings("unchecked")
	public User findUserByActiveUuid(String activeUuid)  {
		String sql = "select u.password_salt salt, u.id \"userId\",u.account \"account\",u.name \"name\",u.password \"password\",u.logo \"logo\",u.mobile \"mobile\",u.position \"position\"," +
				"u.gender \"gender\",u.active,u.active_uuid \"activeUuid\", u.password_uuid \"passwordUuid\",u.active_uuid_time \"activeUuidTime\", u.password_uuid_time \"passwordUuidTime\", s.status_content \"genderTxt\" " +
				" from tbl_user u,tbl_sys_status s "
				+ " where u.gender=s.status_id and u.enabled=1 and u.active_uuid= :activeUuid";
		Query  query = getSession().createSQLQuery(sql);
		query.setParameter("activeUuid", activeUuid.trim());
		Map<String, Object> map=(Map<String, Object>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).uniqueResult();
		if(map==null)
			return null;
		else			
			return BeanMapper.map(map,User.class);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> listUsersAboutMeetingByProject(int projectId) {
		String sql = "select u.id userId, u.*, um.tbl_meeting_id meetingId from tbl_user_meeting um, tbl_user u "+
						" where um.tbl_user_id=u.id and "+
						 " exists (select 1 from tbl_project_meeting m where m.id=um.tbl_meeting_id and m.tbl_project_id=:projectId  )"+
						" order by um.tbl_meeting_id ";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("projectId", projectId);
		List<Map<String, Object>> list =  (List<Map<String, Object>>)query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		return list;
	}

	@Override
	public List<User> findUserListByIds(String ids) {
		String hql = "from User u where u.userId in ("+ids+") and u.enabled = 1";
		return find(hql);
	}


	@SuppressWarnings("unchecked")
	@Override
	public User findUserByPasswordUuid(String passwordUuid) {
		String sql = "select u.password_salt salt, u.id \"userId\",u.account \"account\",u.name \"name\",u.password \"password\",u.logo \"logo\",u.mobile \"mobile\",u.position \"position\"," +
				"u.gender \"gender\",u.active,u.active_uuid \"activeUuid\",u.password_uuid \"paswordUuid\",u.active_uuid_time \"activeUuidTime\", u.password_uuid_time \"passwordUuidTime\", s.status_content \"genderTxt\" " +
				" from tbl_user u,tbl_sys_status s "
				+ " where u.gender=s.status_id and u.enabled=1 and u.password_uuid= :password_uuid";
		Query  query = getSession().createSQLQuery(sql);
		query.setParameter("password_uuid", passwordUuid.trim());
		Map<String, Object> map=(Map<String, Object>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).uniqueResult();
		if(map==null)
			return null;
		else			
			return BeanMapper.map(map,User.class);
	}

	@Override
	public String findUserLogoByTaskId(int taskId) {
		String sql = "select u.logo from tbl_user u,tbl_user_task ut where ut.tbl_user_id = u.id and ut.tbl_role_code = 'TASK_EXECUTOR' and ut.tbl_task_id = :taskId";
		Query query = getSession().createSQLQuery(sql);
		query.setParameter("taskId", taskId);
		@SuppressWarnings("unchecked")
		Map<String, Object> map=(Map<String, Object>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).uniqueResult();
		return (String)map.get("logo");
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> listUserAboutMeetingByMeetingId(
			int meetingId) {
		String sql = "select tbl_user_id from tbl_user_meeting where tbl_meeting_id =:meetingId";
		SQLQuery query = getSession().createSQLQuery(sql);
		query.setParameter("meetingId", meetingId);
		return query.list();
	}


/*	public void addUser(User user) {
		save(user);
	}

	@Override
	public void updateUser(User user) {
		update(user);
	}

	@Override
	public User getUserById(int id) {
		return unique("from User u where u.id=?", id);
	}*/
}
