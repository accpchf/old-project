package com.ks0100.wp.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.common.util.PathUtil;
import com.ks0100.common.util.ReadPropertiesUtil;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.audit.ArgumentsForFile;
import com.ks0100.wp.audit.AuditServiceAspectJ;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.constant.BusinessConstant.PermitConstant;
import com.ks0100.wp.constant.BusinessConstant.RoleConstant;
import com.ks0100.wp.entity.Attachment;
import com.ks0100.wp.entity.Meeting;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.service.AttachmentService;
import com.ks0100.wp.service.MeetingService;
import com.ks0100.wp.service.ProjectService;

@Controller
@RequestMapping("/meeting")
public class MeetingController {
  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private MeetingService meetingService;

  @Autowired
  private AttachmentService attachmentService;
  
  @Autowired
  public ProjectService projectService;

  /**
   * 项目页面菜单栏点击会议，加载会议界面
   *
   * @return
   * 创建日期：2014-12-9
   * 修改说明：
   * @author chengls
   */
  @RequestMapping("/loadmeetingview")
  public String loadMeetingHtml(Model model, final Integer projectId) {
	Project project = projectService.findProjectById(projectId.intValue());
	List<Meeting> meetings = null;
	try {
	  meetings = this.meetingService.listMeetingsSortByBegingTime(projectId.intValue());
	} catch(Exception e) {
	  this.logger.error("error:", e);
	  return "error/exceptionPage";
	}

	//	List<List<String>> permitCodeAndIdsSetMeetingList = new ArrayList<List<String>>();
	//	List<List<String>> permitCodeAndIdsUpdateMeetingList = new ArrayList<List<String>>();

	List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();

	final String PRJ_ADMIN_ACCESS = PermitConstant.PRJ_ADMIN_ACCESS + ":" + projectId;
	
	for(final Meeting meeting : meetings) {
	  mapList.add(new HashMap<String, Object>() {
		private static final long serialVersionUID = 5311869711908466833L;
		{
		  this.put("permitCodeAndIds_set_meeting", new ArrayList<String>() {
			private static final long serialVersionUID = 2761191320676951996L;
			{
			  this.add(PRJ_ADMIN_ACCESS);
			  this.add(PermitConstant.MEETING_SET + ":" + meeting.getMeetingId());
			}
		  });
		  this.put("permitCodeAndIds_update_meeting", new ArrayList<String>() {
			private static final long serialVersionUID = 5626686341978430592L;
			{
			  this.add(PRJ_ADMIN_ACCESS);
			  this.add(PermitConstant.MEETING_UPDATE + ":" + meeting.getMeetingId());
			}
		  });
		  this.put("meeting", meeting);
		}
	  });
	}
	model.addAttribute("project", project);
	model.addAttribute("mapList", mapList);
	//	for(Meeting meeting : meetings) {
	//		List<String> permitCodeAndIdsSetMeeting = new ArrayList<String>();
	//		permitCodeAndIdsSetMeeting.add(PermitConstant.PRJ_ADMIN_ACCESS + ":" + projectId);
	//		permitCodeAndIdsSetMeeting.add(PermitConstant.MEETING_SET + ":" + meeting.getMeetingId());
	//		permitCodeAndIdsSetMeetingList.add(permitCodeAndIdsSetMeeting);
	//		
	//		List<String> permitCodeAndIdsUpdateMeeting = new ArrayList<String>();
	//		permitCodeAndIdsUpdateMeeting.add(PermitConstant.PRJ_ADMIN_ACCESS + ":" + projectId);
	//		permitCodeAndIdsUpdateMeeting.add(PermitConstant.MEETING_UPDATE + ":" + meeting.getMeetingId());
	//		permitCodeAndIdsUpdateMeetingList.add(permitCodeAndIdsUpdateMeeting);
	//	}

	//	model.addAttribute("permitCodeAndIds_set_meeting", permitCodeAndIdsSetMeetingList.toArray(new List[meetings.size()]));
	//	model.addAttribute("permitCodeAndIds_update_meeting", permitCodeAndIdsUpdateMeetingList.toArray(new List[meetings.size()]));
	return "meeting/meeting";
  }

  /**
   * 获取会议界面初始化数据
   *
   * @return
   * 创建日期：2014-12-9
   * 修改说明：
   * @author chengls
   */
  @ResponseBody
  @RequestMapping(value = "/listmeetinginitdata", method = RequestMethod.POST)
  public Map<String, Object> listMeetingInitData(Model model, int projectId) {
	Map<String, Object> map = new HashMap<String, Object>();
	List<Meeting> meetings = null;
	try {
	  meetings = meetingService.listMeetingsSortByBegingTime(projectId);
	} catch(Exception e) {
	  logger.error("error:", e);
	  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
		  .getStringContextProperty("meeting_getInfoError"));
	}
	map.put("meetings", meetings);
	model.addAttribute("projectId", projectId);
	return ResultDataJsonUtils.successResponseResult(map);
  }

  /**
   * 添加或删除参与者
   *
   * @param operatorType(true为添加，false为删除)
   * @param ids
   * @param projectId
   * @return
   * 创建日期：2014-12-12
   * 修改说明：
   * @author chengls
   */
  @ResponseBody
  @RequestMapping(value = "/operatorJoninUser", method = RequestMethod.POST)
  public Map<String, Object> operatorJoninUser(boolean operatorType, @RequestParam(value = "ids[]")
  int[] ids, int meetingId) {
	if(ids == null || ids.length <= 0 || meetingId <= 0) {
	  return ResultDataJsonUtils.errorResponseResult("");
	}
	if(operatorType) {
	  meetingService.addMeetingUser(meetingId, ids);
	} else {
	  meetingService.deleteMeetingUser(meetingId, ids);
	}
	return ResultDataJsonUtils.successResponseResult();
  }

  /**
   * 删除会议附件
   *
   * @param filePath
   * @param request
   * @return
   * 创建日期：2014-12-15
   * 修改说明：
   * @author chengls
   */
  @ResponseBody
  @RequestMapping(value = "/deleteadjunct", method = RequestMethod.POST)
  public Map<String, Object> deleteMeetingAdjunct(String fileUrl, int projectId) {
	if(StringUtils.trimToNull(fileUrl) == null) {
	  return ResultDataJsonUtils.errorResponseResult(null);
	}
	Attachment att = attachmentService.findAttachmentByFileUrl(fileUrl);
	if(att != null) {
	  ArgumentsForFile aff = new ArgumentsForFile();
	  aff.setFileName(att.getFileName());
	  aff.setFilesize(0);
	  aff.setOperateType(AuditServiceAspectJ.OPERATE_TYPE_00643);
	  aff.setProjectId(projectId);

	  attachmentService.deleteAttachmentByFileUrl(aff, PathUtil.ABSOLUTE_WEB_PATH, fileUrl);
	}
	return ResultDataJsonUtils.successResponseResult();
  }

  /**
   * 添加附件
   *
   * @param request
   * @param meetingId
   * @return
   * 创建日期：2014-12-15
   * 修改说明：
   * @author chengls
   */
  @ResponseBody
  @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
  public Map<String, Object> addMeetingAdjunct(HttpServletRequest request, int meetingId,
	  int projectId) {
	MultipartHttpServletRequest mulRequest = (MultipartHttpServletRequest)request;
	MultipartFile file = mulRequest.getFile("upload_file");
	if(file == null || file.getSize() <= 0) {
	  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
		  .getStringContextProperty("attachment_uploadfail"));
	}
	Attachment attachment = null;
	try {
	  ArgumentsForFile aff = new ArgumentsForFile();
	  aff.setFileName(file.getOriginalFilename());
	  aff.setFilesize(file.getSize());
	  aff.setProjectId(projectId);
	  attachment = attachmentService.saveAttachment(aff, file,
		  BusinessConstant.FileType.FILESAVETYPE_MEETING,
		  BusinessConstant.TableNameConstant.TABLE_MEETING, meetingId, -1);
	} catch(Exception e) {
	  logger.error("error:", e);
	  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
		  .getStringContextProperty("attachment_uploadfail"));
	}
	return ResultDataJsonUtils.successResponseResult(attachment);
  }

  /**
   * 弹出添加页面
   *
   * @param model
   * @param projectId
   * @return
   * 创建日期：2014-12-16
   * 修改说明：
   * @author chengls
   */
  @RequestMapping("/toAddMeeting")
  public String toAddMeetingView(Model model, int projectId, int meetingId) {
	model.addAttribute("projectId", projectId);
	//model.addAttribute("meetingId", meetingId);
	Meeting meeting = null;
	if(meetingId > 0) {
	  meeting = meetingService.findMeetingById(meetingId);
	}
	if(meeting == null) {
	  meeting = new Meeting();
	  meeting.setMeetingId(meetingId);
	  meeting.setBeginTime(new Date());
	}
	model.addAttribute("meeting", meeting);
	return "meeting/addMeeting";
  }

  /**
   * 添加或修改会议
   *
   * @param projectId
   * @param meeting
   * @return
   * 创建日期：2014-12-24
   * 修改说明：
   * @author chengls
   */
  @ResponseBody
  @RequestMapping(value = "/addmeeting", method = RequestMethod.POST)
  public Map<String, Object> operatorMeeting(int projectId, Meeting meeting, String beginTimeString) {
	Meeting temp = null;
	SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
	Project project = new Project();
	project.setProjectId(projectId);
	try {
	  meeting.setBeginTime(format.parse(beginTimeString));
	} catch(ParseException e) {
	  return ResultDataJsonUtils.errorResponseResult("date_formatError");
	}
	if(meeting.getMeetingId() > 0) {
	  //修改会议操作
	  temp = meetingService.findMeetingById(meeting.getMeetingId());
	  if(temp == null || temp.getMeetingId() <= 0) {
		return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
			.getStringContextProperty("meeting_updateError_notExit"));
	  }
	  temp.setTitle(meeting.getTitle());
	  temp.setBeginTime(meeting.getBeginTime());
	  temp.setPlace(meeting.getPlace());
	  temp.setDetail(meeting.getDetail());
//	  temp.setRecord(meeting.getRecord());
	  temp.setProject(project);
	  this.meetingService.updateMeeting(temp);
	} else {
	  //新增会议操作
	  temp = new Meeting();
	  temp.setTitle(meeting.getTitle());
	  temp.setBeginTime(meeting.getBeginTime());
	  temp.setPlace(meeting.getPlace());
	  temp.setDetail(meeting.getDetail());
//	  temp.setRecord(meeting.getRecord());
	  temp.setProject(project);
	  ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	  this.meetingService.saveMeeting(temp,user.getUserId());
	  this.meetingService.addMeetingUserRole(temp, user, RoleConstant.MEETING_ADMIN);
	  user.getPermissions().clear();
	}
	return ResultDataJsonUtils.successResponseResult(temp);
  }

  /**
   * 删除会议
   *
   * @param meetingId
   * @return
   * 创建日期：2014-12-25
   * 修改说明：
   * @author chengls
   */
  @ResponseBody
  @RequestMapping(value = "/deletemeeting", method = RequestMethod.POST)
  public Map<String, Object> deleteMeeting(int meetingId) {
	Meeting meeting = meetingService.findMeetingById(meetingId);
	if(meeting != null) {
	  meetingService.deleteMeeting(PathUtil.ABSOLUTE_WEB_PATH, meeting);
	}
	return ResultDataJsonUtils.successResponseResult();
  }

  /**
   * 修改会议纪要
   *
   * @param meetingId
   * @param record
   * @return
   * 创建日期：2015-1-4
   * 修改说明：
   * @author chengls
   */
  @ResponseBody
  @RequestMapping(value = "updatemeetingrecord", method = RequestMethod.POST)
  public Map<String, Object> updateMeetingRecord(int meetingId, String record) {
	Meeting meeting = meetingService.findMeetingById(meetingId);
	if(meeting == null) {
	  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
		  .getStringContextProperty("meeting_updateError_notExit"));
	}
	meeting.setRecord(record);
	meetingService.updateMeeting(meeting);
	return ResultDataJsonUtils.successResponseResult(record);
  }
}
