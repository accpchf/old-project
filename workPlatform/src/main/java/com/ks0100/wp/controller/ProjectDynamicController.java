package com.ks0100.wp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.wp.entity.ProjectActionRecord;
import com.ks0100.wp.service.ProjectActionRecordService;

@Controller
@RequestMapping("/dynamic")
public class ProjectDynamicController {

  @Autowired
  private ProjectActionRecordService pRecordService;

  @RequestMapping("/loadDynamicView")
  public String loadDynamicView() {
	return "dynamic/dynamic";
  }

  @ResponseBody
  @RequestMapping(value = "/initProjectDynamic", method = RequestMethod.POST)
  public Map<String, Object> initProjectDynamic(int projectId, String userIds, String types) {
	DateTime now = new DateTime();
	String monday1 = now.dayOfWeek().withMinimumValue().toString("yyyy.MM.dd");
	String sunday1 = now.dayOfWeek().withMaximumValue().toString("yyyy.MM.dd");
	List<String> dataList = new ArrayList<String>();
	for(int i = 0; i < 4; i++) {
	  String monday = now.dayOfWeek().withMinimumValue().plusWeeks(-i).toString("yyyy.MM.dd");
	  String sunday = now.dayOfWeek().withMaximumValue().plusWeeks(-i).toString("yyyy.MM.dd");
	  String weeks = monday + "~" + sunday;
	  dataList.add(weeks);
	}
	Map<String, Object> projectDynamicMap = new HashMap<String, Object>();
	Map<String, List<ProjectActionRecord>> pActionRecord = pRecordService.findProjectActionRecord(
		projectId, userIds, types, monday1, sunday1);
	projectDynamicMap.put("pActionRecord", pActionRecord);
	projectDynamicMap.put("dataList", dataList);
	return ResultDataJsonUtils.successResponseResult(projectDynamicMap);
  }

  @ResponseBody
  @RequestMapping(value = "/findProjectDynamic", method = RequestMethod.POST)
  public Map<String, Object> findProjectDynamic(int projectId, String userIds, String types,
	  String monday, String sunday) {
	Map<String, List<ProjectActionRecord>> pActionRecord = pRecordService.findProjectActionRecord(
		projectId, userIds, types, monday, sunday);
	return ResultDataJsonUtils.successResponseResult(pActionRecord);
  }
}
