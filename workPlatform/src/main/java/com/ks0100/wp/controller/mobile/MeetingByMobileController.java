package com.ks0100.wp.controller.mobile;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.common.constant.CommonConstant;
import com.ks0100.common.util.FTPClientTemplate;
import com.ks0100.common.util.PathUtil;
import com.ks0100.common.util.ReadPropertiesUtil;
import com.ks0100.wp.audit.ArgumentsForFile;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.constant.BusinessConstant.RoleConstant;
import com.ks0100.wp.entity.Attachment;
import com.ks0100.wp.entity.Meeting;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.service.AttachmentService;
import com.ks0100.wp.service.CommonForMobileService;
import com.ks0100.wp.service.MeetingService;
import com.ks0100.wp.service.ProjectService;

@Controller
@RequestMapping("/meetingByMobile")
public class MeetingByMobileController {

	@Autowired
	private MeetingService meetingService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private CommonForMobileService commonForMobileService;
	@Autowired
	private AttachmentService attachmentService;
	
	private  static String MEETING_ID_ERROR = "meetingId decryption failure";
	
	@ResponseBody
	@RequestMapping("/gotoMeeting")
	public Map<String, Object> findMeetingDetail(String meetingId,String MOBILESESSIONID) throws Exception{
		meetingId = commonForMobileService.getDecryptStr(meetingId, MOBILESESSIONID);
		if(StringUtils.isBlank(meetingId)){
			return ResultDataJsonUtils.errorResponseResult(MEETING_ID_ERROR);
		}
		return commonForMobileService.successResponseMapForMobile(meetingService.findMeetingDetailByIdForMobile(Integer.parseInt(meetingId)), MOBILESESSIONID);
	}
	
	@ResponseBody
	@RequestMapping("/listFuturePersonlMeeting")
	public Map<String, Object> listFuturePersonlMeeting(String userId,String MOBILESESSIONID) throws Exception{
		userId = commonForMobileService.getDecryptStr(userId, MOBILESESSIONID);
		if(StringUtils.isBlank(userId)){
			return ResultDataJsonUtils.errorResponseResult("userId decryption failure");
		}
		return commonForMobileService.successResponseMapForMobile(meetingService.listFutureMeetingByUserIdForMobile(Integer.parseInt(userId)),MOBILESESSIONID);
	}
	
	@ResponseBody
	@RequestMapping("/listCompletedPersonlMeeting")
	public Map<String, Object> listCompletedPersonlMeeting(String userId,String pageIndex,String pageMax,String MOBILESESSIONID) throws Exception{
		userId = commonForMobileService.getDecryptStr(userId, MOBILESESSIONID);
		if(StringUtils.isBlank(userId)){
			return ResultDataJsonUtils.errorResponseResult("userId decryption failure");
		}
		if(StringUtils.isBlank(pageIndex)){
			pageIndex = "1";
		}else{
			pageIndex = commonForMobileService.getDecryptStr(pageIndex, MOBILESESSIONID);
		}
		if(StringUtils.isBlank(pageMax)){
			pageMax = "10";
		}else{
			pageMax = commonForMobileService.getDecryptStr(pageMax, MOBILESESSIONID);
		}
		return commonForMobileService.successResponseMapForMobile(meetingService.listCompletedMeetingByUserIdForMobile(Integer.parseInt(userId),Integer.parseInt(pageIndex),Integer.parseInt(pageMax)),MOBILESESSIONID);
	}
	
	@ResponseBody
	@RequestMapping("/initProjectList")
	public Map<String, Object> initProjectList(String MOBILESESSIONID) throws Exception{
		return commonForMobileService.successResponseMapForMobile(projectService.findProjectByUserIdForMobile(commonForMobileService.getShiroUser(MOBILESESSIONID).getUserId()), MOBILESESSIONID);
	}
	
	@ResponseBody
	@RequestMapping("/initUserList")
	public Map<String, Object> initUserList(String projectId,String MOBILESESSIONID) throws Exception{
		projectId = commonForMobileService.getDecryptStr(projectId, MOBILESESSIONID);
		if(StringUtils.isBlank(projectId)){
			return ResultDataJsonUtils.errorResponseResult("projectId decryption failure");
		}
		return commonForMobileService.successResponseMapForMobile(projectService.findUserByProjectIdForMobile(Integer.parseInt(projectId)), MOBILESESSIONID);
	}
	
	@ResponseBody
	@RequestMapping("/addOrUpdateMeeting")
	public Map<String, Object> addMeeting(String meetingId,String title,String detail,String place,String beginTimeString,String projectId,String ids,String MOBILESESSIONID) throws Exception {
		Project project = new Project();
		Meeting meeting = new Meeting();
		title = commonForMobileService.getDecryptStr(title, MOBILESESSIONID);
		if(StringUtils.isEmpty(title)){
			return ResultDataJsonUtils.errorResponseResult("title decryption failure");
		}
		meeting.setTitle(title);
		place = commonForMobileService.getDecryptStr(place, MOBILESESSIONID);
		if(StringUtils.isEmpty(place)){
			return ResultDataJsonUtils.errorResponseResult("place decryption failure");
		}
		meeting.setPlace(place);
		detail = commonForMobileService.getDecryptStr(detail, MOBILESESSIONID);
		if(StringUtils.isEmpty(detail)){
			return ResultDataJsonUtils.errorResponseResult("detail decryption failure");
		}
		meeting.setDetail(detail);
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
		project.setProjectId(Integer.parseInt(commonForMobileService.getDecryptStr(projectId, MOBILESESSIONID)));
		meeting.setProject(project);
		meeting.setBeginTime(format.parse(commonForMobileService.getDecryptStr(beginTimeString, MOBILESESSIONID)));
		ids = commonForMobileService.getDecryptStr(ids, MOBILESESSIONID);
		ids = ids.replaceAll("\"", "");
		ids = ids.replaceAll("]", "");
		ids = ids.replace("[", "");
		String[] stringList = ids.split(",");
		int [] result = new int[stringList.length];
		for(int i=0;i<stringList.length;i++){
			result[i] = Integer.parseInt(stringList[i].trim());
		}
		if(StringUtils.isNotBlank(meetingId)){
			meetingId = commonForMobileService.getDecryptStr(meetingId, MOBILESESSIONID);
			meeting.setMeetingId(Integer.parseInt(meetingId));
			meeting.setUpdatedBy(commonForMobileService.getShiroUser(MOBILESESSIONID).getUser());
			meetingService.updateMeetingForMobile(meeting);
			meetingService.updateMeetingUser(meeting.getMeetingId(), result);
		}else{
			meetingService.saveMeetingForMobile(meeting,commonForMobileService.getShiroUser(MOBILESESSIONID));
			meetingService.addMeetingUserRole(meeting, commonForMobileService.getShiroUser(MOBILESESSIONID), RoleConstant.MEETING_ADMIN);
			meetingService.addMeetingUser(meeting.getMeetingId(), result);
		}
		return commonForMobileService.successResponseMapForMobile(meeting.getMeetingId(), MOBILESESSIONID);
	}

	@ResponseBody
	@RequestMapping("/upload")
	public Map<String, Object> uploadFile(HttpServletRequest request,String meetingId,String projectId,String MOBILESESSIONID) throws Exception{
		meetingId = commonForMobileService.getDecryptStr(meetingId, MOBILESESSIONID);
		if(StringUtils.isBlank(meetingId)){
			return ResultDataJsonUtils.errorResponseResult(MEETING_ID_ERROR);
		}
		projectId = commonForMobileService.getDecryptStr(projectId, MOBILESESSIONID);
		if(StringUtils.isBlank(projectId)){
			return ResultDataJsonUtils.errorResponseResult("projectId decryption failure");
		}
	    CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		if(multipartResolver.isMultipart(request)){
			MultipartHttpServletRequest mRequest =  (MultipartHttpServletRequest)request;
			Iterator<String> itre = mRequest.getFileNames();
			while(itre.hasNext()){
				MultipartFile file = mRequest.getFile(itre.next());
				if(file==null||file.getSize()<0){
					return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
							  .getStringContextProperty("attachment_uploadfail"));
				}
				try {
				  ArgumentsForFile aff = new ArgumentsForFile();
				  aff.setFileName(file.getOriginalFilename());
				  aff.setFilesize(file.getSize());
				  aff.setProjectId(Integer.parseInt(projectId));
				  attachmentService.saveAttachment(aff, file,
					  BusinessConstant.FileType.FILESAVETYPE_MEETING,
					  BusinessConstant.TableNameConstant.TABLE_MEETING, Integer.parseInt(meetingId), -1);
				} catch(Exception e) {
				  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
					  .getStringContextProperty("attachment_uploadfail"));
				}
			}
		}
		return commonForMobileService.successResponseMapForMobile("upload success", MOBILESESSIONID);
	}
	
	
	@ResponseBody
	@RequestMapping("/addOrUpdateMeetingRecord")
	public Map<String, Object> addOrUpdateMeetingRecord(String record,String meetingId,String MOBILESESSIONID) throws Exception{
		meetingId = commonForMobileService.getDecryptStr(meetingId, MOBILESESSIONID);
		if(StringUtils.isBlank(meetingId)){
			return ResultDataJsonUtils.errorResponseResult(MEETING_ID_ERROR);
		}
		record = commonForMobileService.getDecryptStr(record, MOBILESESSIONID);
		if(StringUtils.isBlank(record)){
			return ResultDataJsonUtils.errorResponseResult("record decryption failure");
		}
		Meeting meeting = meetingService.findMeetingById(Integer.parseInt(meetingId));
		meeting.setRecord(record);
		meeting.setUpdatedBy(commonForMobileService.getShiroUser(MOBILESESSIONID).getUser());
		meetingService.updateMeetingForMobile(meeting);
		return commonForMobileService.successResponseMapForMobile("add record success", MOBILESESSIONID);
	}
	
	@ResponseBody
	@RequestMapping("/download")
	public void downLoad(HttpServletResponse response, String fileUrl,
			String MOBILESESSIONID) throws Exception {
		fileUrl = commonForMobileService.getDecryptStr(fileUrl, MOBILESESSIONID);
		boolean isFTP = "true".equals(ReadPropertiesUtil
				.getStringContextProperty("ftp.isFTP")) ? true : false;
		OutputStream out = null;
		if (isFTP) {
			FTPClientTemplate ftp = new FTPClientTemplate();
			ftp.setHost(ReadPropertiesUtil.getStringContextProperty("ftp.host"));
			ftp.setUsername(ReadPropertiesUtil
					.getStringContextProperty("ftp.username"));
			ftp.setPassword(ReadPropertiesUtil
					.getStringContextProperty("ftp.password"));
			try {
				Attachment attachment = attachmentService
						.findAttachmentByFileUrl(fileUrl);
				if (attachment == null) {
					throw new Exception();
				}

				response.reset();
				response.setContentType("application/x-download; charset=utf-8");
				String userFileName = attachment.getFileName();
				userFileName = URLEncoder.encode(userFileName, "utf-8");
				response.addHeader("Content-Disposition",
						"attachment;filename=" + userFileName);
				out = response.getOutputStream();
				String filePath = attachment.getPath();
				ftp.get("/" + filePath, out);
			} catch (Exception e) {
				throw new Exception();
			} finally {
				out.flush();
				out.close();
				out = null;
			}

		} else {
			FileInputStream nowIn = null;
			try {
				Attachment attachment = attachmentService
						.findAttachmentByFileUrl(fileUrl);
				if (attachment == null) {
					throw new Exception();
				}
				String filePath = PathUtil.ABSOLUTE_WEB_PATH
						+ CommonConstant.UPLOADFILEURL + "/"
						+ attachment.getPath();
				File file = new File(filePath);
				if (file == null || !file.exists()) {
					throw new Exception();
				}

				response.reset();
				response.setContentLength((int)file.length());
				response.setContentType("application/x-download; charset=utf-8");
				String userFileName = attachment.getFileName();
				userFileName = URLEncoder.encode(userFileName, "utf-8");
				response.addHeader("Content-Disposition",
						"attachment;filename=" + userFileName);
				nowIn = new FileInputStream(file);
				out = response.getOutputStream();
				byte[] b = new byte[1024 * 1024];
				int i = 0;
				while ((i = nowIn.read(b)) > 0) {
					out.write(b, 0, i);
				}
			} catch (Exception ex) {
				throw ex;
			} finally {
				out.flush();
				nowIn.close();
				out.close();
				nowIn = null;
				out = null;
			}
		}
	}
}
