package com.ks0100.wp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.common.util.ReadPropertiesUtil;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.constant.BusinessConstant.PermitConstant;
import com.ks0100.wp.constant.StatusEnums.PrjReportType;
import com.ks0100.wp.entity.ProWRepContent;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.ProjectWeeklyReport;
import com.ks0100.wp.entity.UserWeekRepDiscuss;
import com.ks0100.wp.entity.UserWeekReport;
import com.ks0100.wp.service.ProjectService;
import com.ks0100.wp.service.WeekReportService;

@Controller
@RequestMapping("/weeklyReport")
public class WeekReportController {
  private Logger logger = LoggerFactory.getLogger(getClass());
  @Autowired
  private WeekReportService wReportService;
  @Autowired
  public ProjectService projectService;
  /**
   * 页面菜单栏点击周报，加载周报界面
   * @return
   */
  @RequestMapping("/loadWeeklyReportview")
  public String loadWeekReportHtml(HttpServletRequest request, Model model) {
	String projectId = request.getParameter("projectId");
	Project project = projectService.findProjectById(Integer.parseInt(projectId));
	model.addAttribute("project", project);
	List<String> permitCodeAndIds=new ArrayList<String>();
	permitCodeAndIds.add(PermitConstant.PRJ_SUPERVISER_ACCESS+":"+projectId);
	permitCodeAndIds.add(PermitConstant.PRJ_ADMIN_ACCESS+":"+projectId);
	model.addAttribute("permitCodeAndIds", permitCodeAndIds);	
	return "weeklyReport/proUserWeeklyReport";
  }

  /**
   * 点击个人周报,跳转到个人周报界面
   * @return
   */
  @RequestMapping("/loadUserWeeklyReportview")
  public String loadUserWeekReportview() {
	return "weeklyReport/userWeeklyReport";
  }

  @ResponseBody
  @RequestMapping(value = "/initUserWeekReportInfo", method = RequestMethod.POST)
  public Map<String, Object> initUserWeekReportInfo() {
	ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	Map<String, Object> dataMap = new HashMap<String, Object>();
	List<String> dateList = wReportService.findDate(user.getUserId());
	if(dateList != null && !dateList.isEmpty()) {
	  String monday = dateList.get(0).split("~")[0];
	  Map<Integer, UserWeekReport> uwReport = wReportService.listUserWeekReort(user.getUserId(), monday);
	  dataMap.put("uwReport", uwReport);
	  dataMap.put("dateList", dateList);
	}
	return ResultDataJsonUtils.successResponseResult(dataMap);
  }

  //	@ResponseBody
  //	@RequestMapping(value = "/listUserWeekReort" ,method = RequestMethod.POST)
  //	public Map<String, Object> listUserWeekReort(UserWeekReport uwr){
  //		UserWeekReport uwReport = wReportService.findUWReport(uwr.getUserWeekReportId());
  //		uwReport.setComment(uwr.getComment());
  //		wReportService.updateUserWRep(uwReport);
  //		return ResultDataJsonUtils.successResponseResult();
  //	}
  @ResponseBody
  @RequestMapping(value = "/updateUserWRep", method = RequestMethod.POST)
  public Map<String, Object> updateUserWRep(UserWeekReport uwr) {
	UserWeekReport uwReport = wReportService.findUWReport(uwr.getUserWeekReportId());
	uwReport.setComment(uwr.getComment());
	wReportService.updateUserWRep(uwReport, uwReport.isFilledIn());
	return ResultDataJsonUtils.successResponseResult();
  }

  @ResponseBody
  @RequestMapping(value = "/findUWReport", method = RequestMethod.POST)
  public Map<String, Object> findUWReport(String monday) {
	ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	Map<Integer, UserWeekReport> uwReport = wReportService.listUserWeekReort(user.getUserId(),
		monday);
	return ResultDataJsonUtils.successResponseResult(uwReport);
  }

  @ResponseBody
  @RequestMapping(value = "/updateuwReportTask", method = RequestMethod.POST)
  public Map<String, Object> updateuwReportTask(int uwreportId) {
	UserWeekReport userWeekReport = wReportService.updateuwReportTask(uwreportId);
	return ResultDataJsonUtils.successResponseResult(userWeekReport);
  }

  @ResponseBody
  @RequestMapping(value = "/findUsersWReportByProId", method = RequestMethod.POST)
  public Map<String, Object> findUsersWReportByProId(String userIds, String monday, int projectId) {
	Map<Integer, UserWeekReport> uwReport = wReportService.findUserWeekReportByProId(userIds,
		monday, projectId);
	return ResultDataJsonUtils.successResponseResult(uwReport);
  }

  @ResponseBody
  @RequestMapping(value = "/initUsersWReportByProId", method = RequestMethod.POST)
  public Map<String, Object> initUsersWReportByProId(String userIds, int projectId) {
	Map<String, Object> dataMap = new HashMap<String, Object>();
	List<String> dateList = wReportService.findDateByProject(projectId);
	if(dateList != null && !dateList.isEmpty()) {
	  String monday = dateList.get(0).split("~")[0];
	  Map<Integer, UserWeekReport> uwReport = wReportService.findUserWeekReportByProId(userIds,
		  monday, projectId);
	  dataMap.put("uwReport", uwReport);
	  dataMap.put("dateList", dateList);
	}
	return ResultDataJsonUtils.successResponseResult(dataMap);
  }

  @ResponseBody
  @RequestMapping(value = "/adduwReportComment", method = RequestMethod.POST)
  public Map<String, Object> adduwReportComment(int userWeekReportId, UserWeekRepDiscuss uwrd) {
	uwrd.setUserWeekReport(wReportService.findUWReport(userWeekReportId));
	ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
	uwrd.setCriticUser(user.getUserId());
	wReportService.addUWReportContent(uwrd);
	return ResultDataJsonUtils.successResponseResult();
  }

  /**
   * 添加个人周报测试请求
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/test_addUserWeekReport", method = RequestMethod.POST)
  public Map<String, Object> addUserWeekReport() {
	try {
			wReportService.addUserWeekReport();
		} catch(ConstraintViolationException ex) {
		  logger.error("error:", ex);
		  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
			  .getStringContextProperty("user.report.duplicate"));
		}
	return ResultDataJsonUtils.successResponseResult();
  }

  /**
   * 添加项目周报测试请求
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/test_addProWeekRep", method = RequestMethod.POST)
  public Map<String, Object> addProjectWeeklyReport() {
	try {
	  wReportService.addProjectWeeklyReport();
	} catch(ConstraintViolationException ex) {
	  logger.error("error:", ex);
	  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
		  .getStringContextProperty("project.report.duplicate"));
	}
	return ResultDataJsonUtils.successResponseResult();
  }

  /**
   * 添加个人周报和项目周报的测试界面
   * @return
   */
  @RequestMapping("/test_addWeeklyReport")
  public String addWeekRep() {
	return "weeklyReport/test_addWeeklyReport";
  }

  @ResponseBody
  @RequestMapping(value = "/initProWeekRep", method = RequestMethod.POST)
  public Map<String, Object> findProWeekRep(int projectId) throws Exception {
	List<String> dataMap = wReportService.findProjectWRepDate(projectId);
	Map<String, Object> proWeekRep = new HashMap<String, Object>();
	if(dataMap != null && !dataMap.isEmpty()) {
	  String monday = dataMap.get(0).split("~")[0];
	  ProjectWeeklyReport pwr = wReportService.findProWeekReport(monday, projectId);
	  proWeekRep.put("dataMap", dataMap);
	  proWeekRep.put("pwr", pwr);
	}
	return ResultDataJsonUtils.successResponseResult(proWeekRep);
  }

  /**
   * 修改项目周报的阶段和状态
   * @param projectId
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/updateProWeekRep", method = RequestMethod.POST)
  public Map<String, Object> updateProWeekRep(int projectWeeklyReportId, String phase,
	  String status, String progress, String type) {
	ProjectWeeklyReport pwr = wReportService.findPWReport(projectWeeklyReportId);
	if(type.equals("phase")){
		pwr.setPhase(phase);
	}else if(type.equals("progress")){
		pwr.setProgress(Integer.parseInt(progress));
	}else{
		pwr.setStatus(status);
	}
	if(!pwr.isFilledIn()){
		ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
		pwr.setFilledUser(user.getUserId());
		pwr.setFilledIn(true);
		wReportService.updateProjectWRep(pwr, false);
	}
	wReportService.updateProjectWRep(pwr, pwr.isFilledIn());
	return ResultDataJsonUtils.successResponseResult();
  }

  @ResponseBody
  @RequestMapping(value = "/operatorJoninUser", method = RequestMethod.POST)
  public Map<String, Object> operatorJoninUser(String userIds, int pwrcId) {
	if(pwrcId <= 0) {
	  return ResultDataJsonUtils.errorResponseResult("");
	}
	ProWRepContent pwrc = wReportService.findPWReportContentById(pwrcId);
	pwrc.setDutyUserIds(userIds);
	wReportService.updateProWeekReportUser(pwrc);
	return ResultDataJsonUtils.successResponseResult();
  }

  @ResponseBody
  @RequestMapping(value = "/updateProWeekRepContent", method = RequestMethod.POST)
  public Map<String, Object> updateProWeekRepContent(int pwrcId, String content1, String content2,
	  String content3, int type) {
	if(pwrcId <= 0) {
	  return ResultDataJsonUtils.errorResponseResult("");
	}
	ProWRepContent pwrc = wReportService.findPWReportContentById(pwrcId);
	if(type == 1){
		pwrc.setColumn1Content(content1);
	}else if(type == 2){
		pwrc.setColumn2Content(content2);
	}else if(type == 3){
		pwrc.setColumn3Content(content3);
	}
	
	wReportService.updateProWeekReportUser(pwrc);
	return ResultDataJsonUtils.successResponseResult();
  }

  @ResponseBody
  @RequestMapping(value = "/saveProWeekRepContent", method = RequestMethod.POST)
  public Map<String, Object> saveProWeekRepContent(int pwrId, String content1, String content2,
	  String content3, String userIds, String type) {
	if(pwrId <= 0) {
	  return ResultDataJsonUtils.errorResponseResult("");
	}
	ProWRepContent pwrc = new ProWRepContent();
	ProjectWeeklyReport pwr = wReportService.findPWReport(pwrId);
	pwrc.setProjectWeeklyReport(pwr);
	pwrc.setColumn1Content(content1);
	pwrc.setColumn2Content(content2);
	pwrc.setColumn3Content(content3);
	pwrc.setType(type);
	pwrc.setDutyUserIds(userIds);
	wReportService.updateProWeekReportUser(pwrc);
	ProjectWeeklyReport pReport = pwrc.getProjectWeeklyReport();
	if(!pReport.isFilledIn()) {
		ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
		pReport.setFilledUser(user.getUserId());
		pReport.setFilledIn(true);
		wReportService.updateProjectWRep(pwr, false);
	}

	return ResultDataJsonUtils.successResponseResult(pwrc.getProWRepContentId());
  }

  @ResponseBody
  @RequestMapping(value = "/updateWorkContent", method = RequestMethod.POST)
  public Map<String, Object> updateWorkContent(int pwrcId, int pwrId, String content) {
	ProWRepContent pwrc;
	if(pwrcId <= 0) {
	  pwrc = new ProWRepContent();
	  pwrc.setProjectWeeklyReport(pwrId);
	  pwrc.setType(PrjReportType.WORK_SUMMARY.getCode());
	  pwrc.setColumn4Content(content);
	  wReportService.updateProWeekReportUser(pwrc);
	  ProjectWeeklyReport pReport = wReportService.findPWReport(pwrId);
	  if(!pReport.isFilledIn()) {
		pReport.setFilledIn(true);
		ShiroUser user = (ShiroUser)SecurityUtils.getSubject().getPrincipal();
		pReport.setFilledUser(user.getUserId());
		wReportService.updateProjectWRep(pReport, false);
	  }
	} else {
	  pwrc = wReportService.findPWReportContentById(pwrcId);
	  pwrc.setColumn4Content(content);
	  wReportService.updateProWeekReportUser(pwrc);
	}
	return ResultDataJsonUtils.successResponseResult(pwrc.getProWRepContentId());
  }

  /**
   * 根据每周的开始时间和项目Id查询项目周报
   * @param projectId
   * @return
   * @throws Exception
   */
  @ResponseBody
  @RequestMapping(value = "/findProWeekReport", method = RequestMethod.POST)
  public Map<String, Object> findProWeekReport(int projectId, String monday) {
	ProjectWeeklyReport pwr = wReportService.findProWeekReport(monday, projectId);
	return ResultDataJsonUtils.successResponseResult(pwr == null ? null : pwr);
  }

  @ResponseBody
  @RequestMapping(value = "/deletepwrContent", method = RequestMethod.POST)
  public Map<String, Object> deletepwrContent(int pwrcId) {
	wReportService.deletepwrContent(pwrcId);
	return ResultDataJsonUtils.successResponseResult();
  }

  /**
   * 对于周五未生成的周报的处理
   * @param pwrcId
   * @return
   */
  @ResponseBody
  @RequestMapping(value = "/createUserWeekLyReport", method = RequestMethod.POST)
  public Map<String, Object> createUserWeekLyReport(String monday, String sunday, int userId,
	  int projectId) {
	wReportService.createUserWeekReport(monday, sunday, userId, projectId);
	Map<Integer, UserWeekReport> uwReport = wReportService.findUserWeekReportByProId(
		String.valueOf(userId), monday, projectId);
	return ResultDataJsonUtils.successResponseResult(uwReport);
  }
}
