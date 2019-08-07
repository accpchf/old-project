package com.ks0100.wp.controller.mobile;

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
import com.ks0100.wp.dto.ProjectDto;
import com.ks0100.wp.dto.RecordDto;
import com.ks0100.wp.dto.TaskDetailDto;
import com.ks0100.wp.dto.TaskLineDto;
import com.ks0100.wp.service.CommonForMobileService;
import com.ks0100.wp.service.TaskService;

@Controller
@RequestMapping("/taskByMobile")
public class TaskByMobileController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TaskService taskService;
	@Autowired
	private CommonForMobileService commonForMobileService;
	
	private static String TASK_ID_ERROR = "taskId decryption failure";
	
	/**
	 * 根据任务板id查询任务列
	 * @param boardId
	 * @param roleType
	 * @param MOBILESESSIONID
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/listTaskLine")
	public Map<String, Object> listTaskLineForMobile(String boardId,String roleType,String MOBILESESSIONID) throws Exception{
		logger.info("------------------boardId:"+boardId);
		logger.info("------------------sessionId:"+MOBILESESSIONID);
		try {
			boardId = commonForMobileService.getDecryptStr(boardId, MOBILESESSIONID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("-------------dencyptId:"+boardId);
		if(StringUtils.isBlank(boardId)){
			return ResultDataJsonUtils.errorResponseResult("boardId decryption failure");
		}
		List<TaskLineDto> taskLineList = taskService.listTaskLinesForMobile(commonForMobileService.getShiroUser(MOBILESESSIONID).getUserId(),Integer.parseInt(boardId), 0);
		return commonForMobileService.successResponseMapForMobile(taskLineList, MOBILESESSIONID);
	}
	
	/**
	 * 根据任务id跳转到任务详情
	 * @param taskId
	 * @param MOBILESESSIONID
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/gotoTask")
	public Map<String, Object> findTaskDetail(String taskId,String MOBILESESSIONID) throws Exception{
		logger.info("------------------taskId:"+taskId);
		logger.info("------------------sessionId:"+MOBILESESSIONID);
		try {
			taskId = commonForMobileService.getDecryptStr(taskId, MOBILESESSIONID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("-------------dencyptId:"+taskId);
		if(StringUtils.isBlank(taskId)){
			return ResultDataJsonUtils.errorResponseResult(TASK_ID_ERROR);
		}
		TaskDetailDto dto = taskService.findTaskDetailForMobile(Integer.parseInt(taskId));
		return commonForMobileService.successResponseMapForMobile(dto, MOBILESESSIONID);
	}
	
	/**
	 * 根据任务id查询任务动态
	 * @param taskId
	 * @param MOBILESESSIONID
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/findTaskRecord")
	public Map<String, Object> findTaskRecord(String taskId,String MOBILESESSIONID) throws Exception{
		taskId = commonForMobileService.getDecryptStr(taskId, MOBILESESSIONID);
		if(StringUtils.isBlank(taskId)){
			return ResultDataJsonUtils.errorResponseResult(TASK_ID_ERROR);
		}
		List<RecordDto> dto = taskService.listTaskActionRecordsForMobile(Integer.parseInt(taskId));
		return commonForMobileService.successResponseMapForMobile(dto, MOBILESESSIONID);
	}
	
	/**
	 * 根据不同的条件查询个人任务列表
	 * roleType 1为管理员 2为执行者 3为成员 其他为查询所有
	 * taskStatus 1为今日任务 2为未来任务 其他为查询所有
	 * @param userId
	 * @param roleType
	 * @param taskStatus
	 * @param MOBILESESSIONID
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/listPersonlTask")
	public Map<String, Object> listPesonlTaskForMobile(String userId,String roleType,String taskStatus,String MOBILESESSIONID) throws Exception{
		try {
			userId = commonForMobileService.getDecryptStr(userId, MOBILESESSIONID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(StringUtils.isBlank(roleType)){
			roleType = "0";
		}else{
			roleType = commonForMobileService.getDecryptStr(roleType, MOBILESESSIONID);
		}
		if(StringUtils.isBlank(taskStatus)){
			taskStatus = "0";
		}else{
			taskStatus = commonForMobileService.getDecryptStr(taskStatus, MOBILESESSIONID);
		}
		if(StringUtils.isBlank(userId)){
			return ResultDataJsonUtils.errorResponseResult("userId decryption failure");
		}
		List<ProjectDto> dto = taskService.listTaskByUserIdForMobile(Integer.parseInt(userId),Integer.parseInt(roleType),Integer.parseInt(taskStatus));
		return commonForMobileService.successResponseMapForMobile(dto, MOBILESESSIONID);
	}
}
