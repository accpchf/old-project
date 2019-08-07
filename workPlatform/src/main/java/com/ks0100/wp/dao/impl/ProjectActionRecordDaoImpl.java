package com.ks0100.wp.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.HibernateDaoImpl;
import com.ks0100.wp.constant.StatusEnums.ActionRecord;
import com.ks0100.wp.dao.ProjectActionRecordDao;
import com.ks0100.wp.entity.ProjectActionRecord;

@Repository
public class ProjectActionRecordDaoImpl extends
		HibernateDaoImpl<ProjectActionRecord, Integer> implements ProjectActionRecordDao{
	private Logger log = LoggerFactory.getLogger(getClass());

	public List<ProjectActionRecord> listTaskActionRecords(int taskId) {
		String hql = "from ProjectActionRecord par where par.taskId = ? order by par.createdTime desc";
		return find(hql, taskId);
	}

	public void delTaskActionRecord(int taskId) {
		String sql = "delete from tbl_project_action_record where task_id = ?";
		createSQLQuery(sql, taskId).executeUpdate();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectActionRecord> findProjectDynamic(int projectId,String userIds, String types,String monday,String sunday) {
		sunday = sunday + " " +"23:59:59" ;
		if(userIds == ""){
			userIds = "''";
		}
		String task1="",task2="",task3 = "", task4 = "",document1="",document2="",meeting1="",meeting2="",
				meeting3="",meeting4="",weekreport1="",weekreport2="",
				project1="",project2="",project3 = "",project4="",task5="",task6="";
		if (types != "") {
			String type[] = types.split(",");
			for (String typeString:type) {
				if(Integer.parseInt(typeString) == 1){
					task1 = ActionRecord.TASK_CREATE.getCode();// "00600";
					task2 = ActionRecord.TASK_ATTACHMENT_DELETE.getCode();//"00618";
					task3 = ActionRecord.TASK_TO_CHECK.getCode();//"00651";
					task4 = ActionRecord.TASK_NO_PASS.getCode();//"00653";
					task5 = ActionRecord.TASK_LINE_CREATE.getCode();//00654
					task6 = ActionRecord.TASK_BOARD_DELETE.getCode();//00661
				}
				if(Integer.parseInt(typeString) == 2){
					document1 = ActionRecord.FLIE_LIB_UPLOAD.getCode();//"00619";
					document2 = ActionRecord.FLIE_LIB_MOVE.getCode();//"00622";
				}
				if(Integer.parseInt(typeString) == 3){
					meeting1 = ActionRecord.METTING_CREATE.getCode();//"00623";
					meeting2 = ActionRecord.METTING_DELETE.getCode();//"00624";
					meeting3 = ActionRecord.MEETING_FILE_UPLOAD.getCode();//"00642";
					meeting4 = ActionRecord.MEETING_FILE_DELETE.getCode();//"00643";
				}
				if(Integer.parseInt(typeString) == 4){
					weekreport1 = ActionRecord.PERSONAL_REPORT_CREATE.getCode();//"00625";
					weekreport2 = ActionRecord.PRJ_REPORT_CREATE.getCode();//"00626";
				}
				if(Integer.parseInt(typeString) == 5){
					project1 = ActionRecord.PRJ_CREATE.getCode();//"00627";
					project2 = ActionRecord.PRJ_DELETE.getCode();//"00641";
					project3 = ActionRecord.PRJ_ADMIN_CREATE.getCode();//"00644";
					project4 = ActionRecord.PRJ_STATUS_RESTART.getCode();//"00650";
				}
			}
		}
		String sql = "select * from tbl_project_action_record where show_by_moudle in (2,3) and tbl_project_id = :projectId and user_id in ("+userIds+") and (useage BETWEEN :task1 and :task2 or useage BETWEEN :task3 and :task4 or useage BETWEEN :task5 and :task6 or " +
					"useage BETWEEN :document1 and :document2 or useage BETWEEN :weekreport1 and :weekreport2 " +
					"or useage BETWEEN :project1 and :project2 or useage BETWEEN :project3 and :project4 or useage in ( :meeting1,:meeting2,:meeting3,:meeting4) )" +
					" and created_time BETWEEN :monday and :sunday  order by created_time desc" ;
		
		Query  query = getSession().createSQLQuery(sql);
		query.setParameter("task1", task1 != ""?task1:"");
		query.setParameter("task2", task2 != ""?task2:"");
		query.setParameter("task3", task3 != ""?task3:"");
		query.setParameter("task4", task4 != ""?task4:""); 
		query.setParameter("task5", task5 != ""?task5:""); 
		query.setParameter("task6", task6 != ""?task6:""); 
		query.setParameter("document1", document1 != ""?document1:"");
		query.setParameter("document2", document2 != ""?document2:"");
		query.setParameter("weekreport1", weekreport1 != ""?weekreport1:"");
		query.setParameter("weekreport2", weekreport2 != ""?weekreport2:"");
		query.setParameter("meeting1", meeting1 != ""?meeting1:"");
		query.setParameter("meeting2", meeting2 != ""?meeting2:"");
		query.setParameter("meeting3", meeting3 != ""?meeting3:"");
		query.setParameter("meeting4", meeting4 != ""?meeting4:"");
		query.setParameter("project1", project1 != ""?project1:"");
		query.setParameter("project2", project2 != ""?project2:"");
		query.setParameter("project3", project3 != ""?project3:"");
		query.setParameter("project4", project4 != ""?project4:"");
		query.setParameter("projectId", projectId);
		query.setParameter("monday", monday);
		query.setParameter("sunday", sunday);
		List<Map<String, Object>> list = (List<Map<String, Object>>) query.setResultTransformer(CriteriaSpecification.ALIAS_TO_ENTITY_MAP).list();
		List<ProjectActionRecord> actionRecords = new ArrayList<ProjectActionRecord>();
		ProjectActionRecord paRecord = null;
		for(Map<String, Object> map:list)
		{
			try{
				if (map != null) {
					paRecord = new ProjectActionRecord();
					BeanUtils.populate(paRecord,map);
					paRecord.setUserId((Integer)map.get("user_id"));
					paRecord.setUserName((String)map.get("user_name"));
					paRecord.setCreatedTime((Date)map.get("created_time"));
					actionRecords.add(paRecord);
				}
			}catch(Exception ex){
				log.error("error:", ex);
			}
		}
		return actionRecords;
	}

	@Override
	public List<ProjectActionRecord> listProjectActionRecords(int projectId) {
		String hql = "from ProjectActionRecord par where par.projectId = ? and (( par.useage >= ? and par.useage <= ? ) or par.useage = ? or par.useage = ?) order by par.createdTime desc";
		return find(hql, projectId, ActionRecord.PRJ_STATUS_SUSPEND.getCode(), ActionRecord.PRJ_STATUS_RESTART.getCode(), ActionRecord.PRJ_CREATE.getCode(),ActionRecord.PRJ_DELETE.getCode());
	}

	@Override
	public int findTaskRecordNum(int taskId) {
		String hql = "from ProjectActionRecord par where par.taskId = ? and (( par.useage >= ? and par.useage <= ? ) or par.useage = ? or par.useage = ?)";
		return find(hql,taskId, ActionRecord.PRJ_STATUS_SUSPEND.getCode(), ActionRecord.PRJ_STATUS_RESTART.getCode(), ActionRecord.PRJ_CREATE.getCode(),ActionRecord.PRJ_DELETE.getCode()).size();
	}


}
