package com.ks0100.wp.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks0100.common.util.BeanMapper;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.audit.AuditServiceAnnotation;
import com.ks0100.wp.audit.AuditServiceAnnotation.USEAGE_TYPE;
import com.ks0100.wp.constant.StatusEnums.ActionRecord;
import com.ks0100.wp.dao.AttachmentDao;
import com.ks0100.wp.dao.MeetingDao;
import com.ks0100.wp.dao.UserDao;
import com.ks0100.wp.dto.MeetingDto;
import com.ks0100.wp.dto.MeetingListDto;
import com.ks0100.wp.entity.Attachment;
import com.ks0100.wp.entity.Meeting;
import com.ks0100.wp.entity.ProjectActionRecord;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.service.AttachmentService;
import com.ks0100.wp.service.MeetingService;
import com.ks0100.wp.service.ProjectActionRecordService;

@Service
public class MeetingServiceImpl implements MeetingService {
	
	@Autowired
	private MeetingDao meetingDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired 
	private AttachmentService attachmentService;
	
	@Autowired 
	private AttachmentDao attachmentDao;
	
	@Autowired
	private ProjectActionRecordService projectActionRecordService;
	
	@Override
	public List<Meeting> listMeetingsSortByBegingTime(int projectId) throws Exception {
		List<Meeting> meetings = meetingDao.listMeetingsSortByCreatedTime(projectId);
		List<Map<String, Object>> meetingUsers = userDao.listUsersAboutMeetingByProject(projectId);
		String ids = "0";
		for (Meeting meeting : meetings) {
			ids += "," + meeting.getMeetingId();
			meeting.setUsers(new ArrayList<User>());
			for (Map<String, Object> userMap : meetingUsers) {
				int meetingId = userMap != null && userMap.get("meetingId") != null 
						? Integer.parseInt(userMap.get("meetingId").toString()) : 0;
				if (meeting.getMeetingId() == meetingId) {
				//	User user = new User();
				//	BeanUtils.populate(user, userMap);
					meeting.getUsers().add(BeanMapper.map(userMap, User.class));
				}
			}
		}
		List<Attachment> meetingAttachments = attachmentDao.listMeetingFiles(ids);
		for (Meeting meeting : meetings) {
			for (Attachment attachment : meetingAttachments) {
				if (meeting.getMeetingId() == attachment.getRefId()) {
					meeting.getAttachments().add(attachment);
				}
			}
		}
		return meetings;
	}
	/**
	 * 删除会议参与者
	 * @see com.ks0100.wp.service.MeetingService#deleteMeetingUser(int, int[])
	 * 创建日期：2014-12-12
	 * 修改说明：
	 * @author chengls
	 */
	@Override
	public void deleteMeetingUser(int meetingId, int[] ids) {
		String idsString = "0";
		for (int i : ids) {
			idsString += "," + i;
		}
		meetingDao.deleteMeetingUser(meetingId, idsString);
	}
	@Override
	public void addMeetingUser(int meetingId, int[] ids) {
		String idsString = "0";
		for (int i : ids) {
			idsString += "," + i;
		}
		List<Integer> exsitIds = meetingDao.findMeetingUser(meetingId, idsString);
		for (Integer integer : exsitIds) {
			idsString = idsString.replaceAll("," + integer, "");
		}
		idsString = idsString.replaceAll("0,", "");
		meetingDao.addMeetingUser(meetingId, idsString.split(","));
	}
	@Override
	public Meeting findMeetingById(int meetingId) {
		List<Meeting> meetings = meetingDao.findBy("meetingId", meetingId);
		return meetings != null && !meetings.isEmpty() ? meetings.get(0) : null;
	}
	@Override
	public void updateMeeting(Meeting temp) {
		temp.setUpdatedBy(-1);
		meetingDao.update(temp);
	}
	
	@Override
    @AuditServiceAnnotation(useage=USEAGE_TYPE.u00623,argType1="com.ks0100.wp.entity.Meeting")	 
	public void saveMeeting(Meeting meeting,int userId) {
		meeting.setCreatedBy(userId);
		meeting.setUpdatedBy(userId);
		meetingDao.save(meeting);
	}
	@Override
	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00624,argType1="com.ks0100.wp.entity.Meeting")	 
	public void deleteMeeting(String path, Meeting meeting) {
		//删除会议的参与者
		meetingDao.deleteMeetingUser(meeting.getMeetingId(), "all");
		
		//删除会议附件
		attachmentService.deleteMeetingAttachment(path, meeting.getMeetingId());
		
		//Meeting meeting = new Meeting();
		//meeting.setMeetingId(meetingId);
		//删除会议
		meetingDao.delete(meeting);
	}
	@Override
	public void addMeetingUserRole(Meeting meeting, ShiroUser user, String roleCode) {
	  this.meetingDao.addMeetingUserRole(meeting, user, roleCode);
	}
	
	@Override
	public MeetingDto findMeetingDetailByIdForMobile(int meetingId) {
		MeetingDto dto = new MeetingDto();
		Meeting meeting  = findMeetingById(meetingId);
		List<Attachment> attachments = attachmentDao.listMeetingFiles(Integer.toString(meetingId));
		List<Integer> users = userDao.listUserAboutMeetingByMeetingId(meetingId);
		DateTimeFormatter format = DateTimeFormat .forPattern("yyyy年MM月dd日 HH时mm分");
		dto.setAttachments(attachments);
		dto.setUsers(users);
		dto.setBeginTime(new DateTime(meeting.getBeginTime()).toString(format));
		dto.setDetail(meeting.getDetail());
		dto.setMeetingId(meeting.getMeetingId());
		dto.setPlace(meeting.getPlace());
		dto.setTitle(meeting.getTitle());
		dto.setRecord(meeting.getRecord());
		dto.setProjectId(meeting.getProject().getProjectId());
		dto.setProjectName(meeting.getProject().getName());
		return dto;
	}
	@Override
	public List<MeetingListDto> listFutureMeetingByUserIdForMobile(int userId) {
		List<Map<String,Object>> list = meetingDao.listFutureMeetingByUserIdForMobile(userId);
		List<MeetingListDto> meetingDtoList = new ArrayList<MeetingListDto>();
		DateTimeFormatter format = DateTimeFormat .forPattern("yyyy年MM月dd日 HH时mm分");
		for(Map<String,Object> map:list){
			MeetingListDto dto = new MeetingListDto();
			dto.setMeetingId((Integer)map.get("meetingId"));
			dto.setProjectName((String)map.get("projectName"));
			dto.setPlace((String)map.get("place"));
			dto.setTitle((String)map.get("title"));
			dto.setBeginTime(new DateTime((Date)map.get("beginTime")).toString(format));
			if(Collections.frequency(meetingDtoList, dto)<1){
				meetingDtoList.add(dto);
			}
		}
		return meetingDtoList;
	}
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> listCompletedMeetingByUserIdForMobile(
			int userId, int pageIndex, int pageMax) {
		Map<String, Object> resultMap = meetingDao.listCompletedMeetingByUserIdForMobile(userId,pageIndex,pageMax);
		List<MeetingListDto> meetingDtoList = new ArrayList<MeetingListDto>();
	    Map<String, Object> result = new HashMap<String, Object>();
		DateTimeFormatter format = DateTimeFormat .forPattern("yyyy年MM月dd日 HH时mm分");
		for(Map<String,Object> map:(List<Map<String, Object>>)resultMap.get("list")){
			MeetingListDto dto = new MeetingListDto();
			dto.setMeetingId((Integer)map.get("meetingId"));
			dto.setProjectName((String)map.get("projectName"));
			dto.setPlace((String)map.get("place"));
			dto.setTitle((String)map.get("title"));
			dto.setBeginTime(new DateTime((Date)map.get("beginTime")).toString(format));
			if(Collections.frequency(meetingDtoList, dto)<1){
				meetingDtoList.add(dto);
			}
		}
		result.put("meetingList", meetingDtoList);
		result.put("index",resultMap.get("pageIndex"));
		return result;
	}
	@Override
	public List<MeetingListDto> listFutureMeetingByProjectIdForMobile(
			int projectId) {
		List<Map<String,Object>> list = meetingDao.listFutureMeetingByProjectIdForMobile(projectId);
		List<MeetingListDto> meetingDtoList = new ArrayList<MeetingListDto>();
		DateTimeFormatter format = DateTimeFormat .forPattern("yyyy年MM月dd日 HH时mm分");
		for(Map<String,Object> map:list){
			MeetingListDto dto = new MeetingListDto();
			dto.setMeetingId((Integer)map.get("meetingId"));
			dto.setPlace((String)map.get("place"));
			dto.setTitle((String)map.get("title"));
			dto.setBeginTime(new DateTime((Date)map.get("beginTime")).toString(format));
			if(Collections.frequency(meetingDtoList, dto)<1){
				meetingDtoList.add(dto);
			}
		}
		return meetingDtoList;
	}
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> listCompletedMeetingByProjectIdForMobile(
			int projectId, int pageIndex, int pageMax) {
		Map<String, Object> resultMap = meetingDao.listCompletedMeetingByProjectIdForMobile(projectId,pageIndex,pageMax);
		List<MeetingListDto> meetingDtoList = new ArrayList<MeetingListDto>();
	    Map<String, Object> result = new HashMap<String, Object>();
		DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd HH:mm:ss");
		for(Map<String,Object> map:(List<Map<String, Object>>)resultMap.get("list")){
			MeetingListDto dto = new MeetingListDto();
			dto.setMeetingId((Integer)map.get("meetingId"));
			dto.setPlace((String)map.get("place"));
			dto.setTitle((String)map.get("title"));
			dto.setBeginTime(new DateTime((Date)map.get("beginTime")).toString(format));
			if(Collections.frequency(meetingDtoList, dto)<1){
				meetingDtoList.add(dto);
			}
		}
		result.put("meetingList", meetingDtoList);
		result.put("index",resultMap.get("pageIndex"));
		return result;
	}
	@Override
	public void saveMeetingForMobile(Meeting meeting, ShiroUser user) {
		meeting.setCreatedBy(user.getUserId());
		meeting.setUpdatedBy(user.getUserId());
		meetingDao.save(meeting);
		ProjectActionRecord record = new ProjectActionRecord();
		record.setUserId(user.getUserId());
		record.setUserName(user.getName());
		record.setCreatedBy(user.getUserId());
		record.setUseage(ActionRecord.METTING_CREATE.getCode());
		record.setProjectId(meeting.getProject().getProjectId());
		record.setShowByMoudle(2);
		record.setRecord("创建了项目会议 " + meeting.getTitle());
		projectActionRecordService.save(record);
	}
	@Override
	public void updateMeetingUser(int meetingId, int[] ids) {
		meetingDao.updateMeetingUser(meetingId, ids);
	}
	@Override
	public void updateMeetingForMobile(Meeting meeting) {
		meetingDao.update(meeting);
	}
}
