package com.ks0100.wp.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.wp.constant.StatusEnums.UserGender;
import com.ks0100.wp.service.TaskService;

@Controller
@RequestMapping("/personal")
public class PersonalController {
	@Autowired
	private TaskService taskService;
	/**
	 * 加载我的任务页面
	 * 
	 * @return
	 */
	@RequestMapping("/personalTask")
	public String personalTask(Model model) {
		model.addAttribute("moudleName", "task");
		return "template/relatedMeTempalte";
	}
	
	@RequestMapping("/getPersonalSetting")
	public String getPersonalSetting() {
		return "personalSetting/personalSetting";
	}
	@RequestMapping("/loadPersonalTask")
	public String loadPersonalTask() {
		return "personalTask/personalTask";
	}
	@RequestMapping("/weeklyReport")
	public String weeklyReport(Model model) {
		model.addAttribute("moudleName", "report");
		return "template/relatedMeTempalte";
	}
	
	@ResponseBody
	@RequestMapping(value = "/findAllProjectTask", method = RequestMethod.POST)
	public Map<String, Object> findAllProjectTask(int roleType) {
		Map<String, Object> projectTask = taskService.findAllProjectTask(roleType);
		return ResultDataJsonUtils.successResponseResult(projectTask);
	}
	
	@ResponseBody
	@RequestMapping(value = "/listSysStatus")
	public Map<String, Object> listSysStatus() {
		List<Map<String, String>> list = UserGender.loadEnumList();
		return ResultDataJsonUtils.successResponseResult(list);
	}
}
