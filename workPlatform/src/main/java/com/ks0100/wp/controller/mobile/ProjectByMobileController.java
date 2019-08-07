package com.ks0100.wp.controller.mobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.wp.entity.ProjectActionRecord;
import com.ks0100.wp.entity.TaskBoard;
import com.ks0100.wp.service.CommonForMobileService;
import com.ks0100.wp.service.MeetingService;
import com.ks0100.wp.service.ProjectActionRecordService;
import com.ks0100.wp.service.ProjectService;
import com.ks0100.wp.service.TaskService;
import com.ks0100.wp.service.UserByMobileService;

@Controller
@RequestMapping("projectByMobile")
public class ProjectByMobileController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UserByMobileService userByMobileService;
	@Autowired
	private CommonForMobileService commonForMobileService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectActionRecordService projectActionRecordService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private MeetingService meetingService;
	
	private static String PROJECT_ID_ERROR = "projectId decryption failure";

	/**
	 * 获取所有的项目信息
	 * @param MOBILESESSIONID
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value="/home")
	public Map<String, Object> loginSuccessToIndex(String status,String MOBILESESSIONID) throws Exception{
		    if(StringUtils.isNotBlank(status)){
		    	status = commonForMobileService.getDecryptStr(status, MOBILESESSIONID);
		    }
			Map<String, Object> map = this.projectService.findAllProjectsByUserForMobile(commonForMobileService.getShiroUser(MOBILESESSIONID).getUserId(),status);
			Map<String, Object> result = new HashMap<String,Object>();
			result.put("personProjects", map.get("personProjects"));
			result.put("orgProjects", map.get("orgProjects"));
			return commonForMobileService.successResponseMapForMobile(result, MOBILESESSIONID);
	}
	
	
	/**
	 * 根据状态去查询项目
	 * @param MOBILESESSIONID
	 * @param orgId
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/findProByStatus")
	public Map<String, Object> findProjectByStatus(String MOBILESESSIONID,
			String orgId, String status) throws Exception {
		status = commonForMobileService.getDecryptStr(status, MOBILESESSIONID);
		if(StringUtils.isBlank(status)){
			return ResultDataJsonUtils.errorResponseResult("status decryption failure");
		}
		if(StringUtils.isNotBlank(orgId)){
			orgId = commonForMobileService.getDecryptStr(orgId, MOBILESESSIONID);
		}else{
			orgId = "0";
		}
		Map<String, Object> projectMap = projectService
				.findProjectByStatusForMobile(
						commonForMobileService.getShiroUser(MOBILESESSIONID)
								.getUserId(), status, Integer.parseInt(orgId));
		return commonForMobileService.successResponseMapForMobile(projectMap, MOBILESESSIONID);
	}
	
	
	/**
	 * 跳转到项目
	 * @param MOBILESESSIONID
	 * @param projectId
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/gotoProject")
	public Map<String, Object> gotoProject(String MOBILESESSIONID,String projectId) throws Exception{
		logger.info("-----------MOBILESESSIONID:"+MOBILESESSIONID);
		logger.info("---------------projectId:"+projectId);
		projectId = commonForMobileService.getDecryptStr(projectId, MOBILESESSIONID);
		logger.info("projectId:"+projectId);
		if(StringUtils.isBlank(projectId)){
			return ResultDataJsonUtils.errorResponseResult(PROJECT_ID_ERROR);
		}
		int id = Integer.parseInt(projectId);
		List<ProjectActionRecord> records = projectActionRecordService.listProjectActionRecords(id);
		Map<String, Object> info = projectService.findProjectInfoByProIdForMobile(id);
		List<TaskBoard> boards = taskService.listTaskBoardsForMobile(id); 
		
		info.put("records", records);
		info.put("boards", boards);
		return commonForMobileService.successResponseMapForMobile(info, MOBILESESSIONID);
	}
	
	/**
	 * 根据项目id查询未完成项目会议
	 * @param projectId
	 * @param MOBILESESSIONID
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "/findProjectFutureMeeting")
	public Map<String, Object> findProjectMeeting(String projectId,String MOBILESESSIONID) throws Exception{
		projectId = commonForMobileService.getDecryptStr(projectId, MOBILESESSIONID);
		
		if(StringUtils.isBlank(projectId)){
			return ResultDataJsonUtils.errorResponseResult(PROJECT_ID_ERROR);
		}
		return commonForMobileService.successResponseMapForMobile(meetingService.listFutureMeetingByProjectIdForMobile(Integer.parseInt(projectId)),MOBILESESSIONID);
	}
	
	@ResponseBody
	@RequestMapping(value = "/findProjectCompletedMeeting")
	public Map<String, Object> findProjectMeeting(String projectId,String pageIndex,String pageMax,String MOBILESESSIONID) throws Exception{
		projectId = commonForMobileService.getDecryptStr(projectId, MOBILESESSIONID);
		if(StringUtils.isBlank(projectId)){
			return ResultDataJsonUtils.errorResponseResult(PROJECT_ID_ERROR);
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
		meetingService.listCompletedMeetingByProjectIdForMobile(Integer.parseInt(projectId),Integer.parseInt(pageIndex), Integer.parseInt(pageMax));
		return commonForMobileService.successResponseMapForMobile(meetingService.listCompletedMeetingByProjectIdForMobile(Integer.parseInt(projectId),Integer.parseInt(pageIndex), Integer.parseInt(pageMax)),MOBILESESSIONID);
	}
	
	/**
	 * 获取项目统计
	 * 
	 * @param projectId
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/findProjectStatistics")
	public Map<String, Object> findProjectStatistics(String projectId,String MOBILESESSIONID) throws Exception{
		projectId = commonForMobileService.getDecryptStr(projectId, MOBILESESSIONID);
		if(StringUtils.isBlank(projectId)){
			return ResultDataJsonUtils.errorResponseResult(PROJECT_ID_ERROR);
		}
		Map<String, Object> counts = projectService.findProjectStatistics(Integer.parseInt(projectId), 0);
		return commonForMobileService.successResponseMapForMobile(counts, MOBILESESSIONID);
	}
}
