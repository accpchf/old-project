package com.ks0100.wp.service.impl;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks0100.common.util.BeanMapper;
import com.ks0100.wp.audit.AuditServiceAnnotation;
import com.ks0100.wp.audit.AuditServiceAnnotation.USEAGE_TYPE;
import com.ks0100.wp.dao.ProWRepContentDao;
import com.ks0100.wp.dao.ProWeeklyRepDao;
import com.ks0100.wp.dao.UserDao;
import com.ks0100.wp.dao.UserWeekRepDiscussDao;
import com.ks0100.wp.dao.UserWeekReportDao;
import com.ks0100.wp.entity.ProWRepContent;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.ProjectWeeklyReport;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.entity.UserWeekRepDiscuss;
import com.ks0100.wp.entity.UserWeekReport;
import com.ks0100.wp.service.WeekReportService;

@Service
public class WeekReportServiceImpl implements WeekReportService {
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private UserWeekRepDiscussDao uWeekRepDiscussDao;
	
	@Autowired
	private UserWeekReportDao uWeekReportDao;
	
	@Autowired
	private ProWeeklyRepDao pWeeklyRepDao;
	
	@Autowired
	private ProWRepContentDao pWRepContentDao;
	
	@Autowired
	private UserDao userDao;
	@Override
	public Map<Integer, UserWeekReport> listUserWeekReort(int userId, String monday) {
        
		List<Map<String, Object>> list = uWeekReportDao.findUserWeekReportByUserId(userId,monday);
		Map<Integer, UserWeekReport> mapUWReport = new HashMap<Integer, UserWeekReport>();
		for (Map<String, Object> map : list) {
			UserWeekReport uwp = new UserWeekReport();
			uwp.setUserWeekReportId((Integer) map.get("userWeekReportId"));
			uwp.setComment((String) map.get("comment"));
			uwp.setCompleteQuality((Short) map.get("completeQuality"));
			uwp.setTotalQuality((Short) map.get("totalQuality"));
			uwp.setUnCompleteQuality((Short)map.get("uncompleteQuality"));
			uwp.setMonday((Date)map.get("monday"));
			Project p = new Project();
			p.setProjectId((Integer)map.get("proId"));
			p.setName((String)map.get("proName"));
			uwp.setProject(p);
			if (!mapUWReport.containsKey(uwp.getUserWeekReportId())) {
				mapUWReport.put(uwp.getUserWeekReportId(), uwp);
			}
			
		}

		for (UserWeekReport uwp : mapUWReport.values()) {
			for (Map<String, Object> map : list) {
				if ((Integer) map.get("userWeekReportId") == uwp.getUserWeekReportId() && (Integer) map.get("userWRepDissId") != null) {
					UserWeekRepDiscuss uDiscuss = new UserWeekRepDiscuss();
					uDiscuss.setUserWeekRepDiscussId((Integer)map.get("userWRepDissId"));
					uDiscuss.setContent((String)map.get("disContent"));
					User u = new User();
					u.setUserId((Integer)map.get("userId"));
					u.setLogo((String)map.get("userLogo"));
					u.setName((String)map.get("userName"));
					uDiscuss.setCriticUser(u);
					uwp.getUwReportDisscuss().add(uDiscuss);
				}
			}
		}
		return mapUWReport;
	}

	public void addUserWeekReport() {
		uWeekReportDao.addUserWeekReport();
	}

	@Override
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00625, argIndex1 = 1,argIndex2 = 2)
	public void updateUserWRep(UserWeekReport uwr, boolean isFilledIn) {
		if(!uwr.isFilledIn()){
			uwr.setFilledIn(true);
		}
		uWeekReportDao.saveOrUpdate(uwr);
	}

	@Override
	public UserWeekReport findUWReport(int uwreportId) {
		return uWeekReportDao.get(uwreportId);
	}

	@Override
	public UserWeekReport updateuwReportTask(int uwreportId) {
		UserWeekReport uReport = uWeekReportDao.get(uwreportId);
		int userId = uReport.getUser().getUserId();
		int projectId = uReport.getPro().getProjectId();
		List<Map<String, Object>> maps = uWeekReportDao.findTaskQuality(userId, projectId, uReport.getMonday());
		for(Map<String, Object> obj :maps){
			uReport.setCompleteQuality(((BigInteger)obj.get("complete_count")).intValue());
			uReport.setUnCompleteQuality(((BigInteger)obj.get("due_count")).intValue());
			uReport.setTotalQuality(((BigInteger)obj.get("all_count")).intValue());
		}
		uWeekReportDao.saveOrUpdate(uReport);
		if(uwreportId > 0){
			List<Map<String, Object>> list = uWeekReportDao.findDiscussByUWRId(uwreportId);
			for (Map<String, Object> map : list) {
				if ((Integer) map.get("uWRId") != null ) {
					UserWeekRepDiscuss uDiscuss = new UserWeekRepDiscuss();
					uDiscuss.setUserWeekRepDiscussId((Integer)map.get("uWRId"));
					uDiscuss.setContent((String)map.get("disContent"));
					User u = new User();
					u.setUserId((Integer)map.get("criticId"));
					u.setLogo((String)map.get("criticLogo"));
					u.setName((String)map.get("criticName"));
					uDiscuss.setCriticUser(u);
					uReport.getUwReportDisscuss().add(uDiscuss);
				}
			}
		}
		
		return uReport;
	}

	@Override
	public Map<Integer, UserWeekReport> findUserWeekReportByProId(String userIds,
			String monday, int projectId) {
		List<Map<String, Object>> list = uWeekReportDao.findProUserWeekReportByProId(userIds, projectId, monday);
		Map<Integer, UserWeekReport> mapUWReport = new HashMap<Integer, UserWeekReport>();
		for (Map<String, Object> map : list) {
			UserWeekReport uwp = new UserWeekReport();
			uwp.setUserWeekReportId((Integer) map.get("userWeekReportId"));
			uwp.setComment((String) map.get("comment"));
			if((Short) map.get("completeQuality") != null){
				uwp.setCompleteQuality((Short) map.get("completeQuality"));
			}
			if((Short) map.get("totalQuality") != null){
				uwp.setTotalQuality((Short) map.get("totalQuality"));
			}
			if((Short) map.get("uncompleteQuality") != null){
				uwp.setUnCompleteQuality((Short)map.get("uncompleteQuality"));
			}
			Project p = new Project();
			if((Integer)map.get("projectId") != null){
				p.setProjectId((Integer)map.get("projectId"));
				p.setName((String)map.get("projectName"));
				uwp.setProject(p);
			}
			User u = new User();
			u.setUserId((Integer)map.get("userId"));
			u.setLogo((String)map.get("userLogo"));
			u.setName((String)map.get("userName"));
			uwp.setUser(u);
			if (!mapUWReport.containsKey(uwp.getUserWeekReportId()) ) {
				mapUWReport.put(uwp.getUserWeekReportId(), uwp);
			}
			
		}
			
		for (UserWeekReport uwp : mapUWReport.values()) {
			for (Map<String, Object> map : list) {
				if ((Integer) map.get("userWeekReportId") == uwp.getUserWeekReportId() && (Integer) map.get("userWRepDissId") != null) {
					UserWeekRepDiscuss uDiscuss = new UserWeekRepDiscuss();
					uDiscuss.setUserWeekRepDiscussId((Integer)map.get("userWRepDissId"));
					uDiscuss.setContent((String)map.get("disContent"));
					User u = new User();
					u.setUserId((Integer)map.get("criticId"));
					u.setLogo((String)map.get("criticLogo"));
					u.setName((String)map.get("criticName"));
					uDiscuss.setCriticUser(u);
					uwp.getUwReportDisscuss().add(uDiscuss);
				}
			}
		}
		return mapUWReport;
	}


	@Override
	public void addUWReportContent(UserWeekRepDiscuss uwrd) {
		uWeekRepDiscussDao.saveOrUpdate(uwrd);
	}

	@Override
	public void addProjectWeeklyReport() {
		pWeeklyRepDao.addProjectWeeklyReport();
		
	}

	@Override
	public ProjectWeeklyReport findProWeekReport(String monday, int projectId) {
		ProjectWeeklyReport pwr = pWeeklyRepDao.findProWeekReport(monday, projectId);
		if (pwr == null) {
			return null;
		}
		if(pwr.getCreatedTime() != null){
			String createdString = new SimpleDateFormat("yyyy-MM-dd").format(pwr.getCreatedTime());
			pwr.setCreatedString(createdString);
		}
		if(pwr.getDeliveryData() != null){
			String deliveryString = new SimpleDateFormat("yyyy-MM-dd").format(pwr.getDeliveryData());
			pwr.setDeliveryString(deliveryString);
		}
		List<Map<String, Object>> list = pWRepContentDao.listParentAndChildProWeekRep(pwr.getProjectWeeklyReportId());
		ProWRepContent pwrc = null;
		if(list != null && !list.isEmpty()){
			for(Map<String, Object> map :list){
				try{
					pwrc = BeanMapper.map(map, ProWRepContent.class);
					
					if( pwrc.getDutyUserIds() != null&&pwrc.getDutyUserIds().length()>0){
						List<Integer> idList=new ArrayList<Integer>(); 
						String[] userIds = pwrc.getDutyUserIds().split(",");
						for (int i = 0; i < userIds.length; i++) {
				            idList.add(Integer.parseInt(userIds[i]));
				        }
						List<User> users = userDao.getListByIds(idList);
						pwrc.setUsers(users);
					}
				}catch(Exception ex){
					logger.error("error:", ex);
				}
	 			
				if((Integer)map.get("pwrId")== pwr.getProjectWeeklyReportId()){
					List<ProWRepContent> listProWRepContents = pwr.getListProWRepContents();
					listProWRepContents.add(pwrc);
					pwr.setListProWRepContents(listProWRepContents);
				}else if((Integer)map.get("pwrId") == pwr.getParentProWeekRep().getProjectWeeklyReportId()){
					List<ProWRepContent> listProWRepContents = pwr.getListParentProWRepContents();
					listProWRepContents.add(pwrc);
					pwr.setListParentProWRepContents(listProWRepContents);
				}
			}
		}
		return pwr;
	}

	@Override
	public ProjectWeeklyReport findPWReport(int projectWeeklyReportId) {
		return pWeeklyRepDao.get(projectWeeklyReportId);
	}


	@Override
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00626,argIndex1=1,argIndex2=2)
	public void updateProjectWRep(ProjectWeeklyReport pwr,boolean isFilledIn) {
		if(!pwr.isFilledIn()){
			pwr.setFilledIn(true);
		}
		pWeeklyRepDao.saveOrUpdate(pwr);
	}

	@Override
	public void updateProWeekReportUser(ProWRepContent pwrc) {
		pWRepContentDao.saveOrUpdate(pwrc);
	}

	@Override
	public ProWRepContent findPWReportContentById(int pwrcId) {
		return pWRepContentDao.get(pwrcId);
	}

	@Override
	public void deletepwrContent(int pwrcId) {
		pWRepContentDao.delete(pwrcId);
	}

	@Override
	public List<String> findDate(int userId) {
		List<String> listStrings = new ArrayList<String>();
		List<Map<String, Object>> list = uWeekReportDao.findDate(userId);
		if (list != null && !list.isEmpty()) {
			for(Map<String, Object> map:list){
				String dateString = "";
				String monday = new SimpleDateFormat("yyyy.MM.dd").format((Date)map.get("monday"));
				String sundayString = new SimpleDateFormat("yyyy.MM.dd").format((Date)map.get("sunday"));
				dateString = monday+"~"+sundayString;
				listStrings.add(dateString);
			}
		}
		return listStrings;
	}

	@Override
	public List<String> findDateByProject(int projectId) {
		List<String> listStrings = new ArrayList<String>();
		List<Map<String, Object>> list = uWeekReportDao.findDateByPro(projectId);
		for(Map<String, Object> map:list){
			String dateString = "";
			String monday = new SimpleDateFormat("yyyy.MM.dd").format((Date)map.get("monday"));
			String sundayString = new SimpleDateFormat("yyyy.MM.dd").format((Date)map.get("sunday"));
			dateString = monday+"~"+sundayString;
			listStrings.add(dateString);
		}
		return listStrings;
	}

	@Override
	public List<String> findProjectWRepDate(int projectId) {
		List<String> listStrings = new ArrayList<String>();
		List<Map<String, Object>> list = pWeeklyRepDao.findDateByPro(projectId);
		for(Map<String, Object> map:list){
			String dateString = "";
			String monday = new SimpleDateFormat("yyyy.MM.dd").format((Date)map.get("monday"));
			String sundayString = new SimpleDateFormat("yyyy.MM.dd").format((Date)map.get("sunday"));
			dateString = monday+"~"+sundayString;
			listStrings.add(dateString);
		}
		return listStrings;
	}

	@Override
	public void createUserWeekReport(String monday, String sunday, int userId,
			int projectId) {
		uWeekReportDao.createUserWeekReport(monday, sunday, userId, projectId);
	}

}
