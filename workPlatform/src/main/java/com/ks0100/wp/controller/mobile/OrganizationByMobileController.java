package com.ks0100.wp.controller.mobile;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.entity.Team;
import com.ks0100.wp.entity.User;
import com.ks0100.wp.service.CommonForMobileService;
import com.ks0100.wp.service.OrganizationService;

@RequestMapping("/orgByMobile")
@Controller
public class OrganizationByMobileController {
	
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private CommonForMobileService commonForMobileService;
	
	private static String ORG_ID_ERROR = "orgId decryption failure";
	
	private static String LOAD_TYPE_ERROR = "loadType decryption failure";
	
	 /**
	   * 获取的组织人员任务统计
	   * @param orgId
	   * @param loadType
	   * @return
	   */
	  @ResponseBody
	  @RequestMapping("/findOrganizationStatistics_user")
	  public Map<String, Object> findOrganizationStatistics_user(String orgId, String loadType,String MOBILESESSIONID) throws Exception{
		orgId = commonForMobileService.getDecryptStr(orgId, MOBILESESSIONID);
		if(StringUtils.isBlank(orgId)){
			return ResultDataJsonUtils.errorResponseResult(ORG_ID_ERROR);
		}
		loadType = commonForMobileService.getDecryptStr(loadType, MOBILESESSIONID);
		if(StringUtils.isBlank(loadType)){
			return ResultDataJsonUtils.errorResponseResult(LOAD_TYPE_ERROR);
		}
		List<User> users = organizationService.findOrganizationStatistics_user(Integer.parseInt(orgId), Integer.parseInt(loadType));
		return commonForMobileService.successResponseMapForMobile(users, MOBILESESSIONID);
	  }
	  
	  /**
	   * 获取的组织项目任务统计
	   * @param orgId
	   * @param loadType
	   * @return
	   */
	  @ResponseBody
	  @RequestMapping("/findOrganizationStatistics_project")
	  public Map<String, Object> findOrganizationStatistics_project(String orgId, String loadType,String MOBILESESSIONID) throws Exception{
		orgId = commonForMobileService.getDecryptStr(orgId, MOBILESESSIONID);
		if (StringUtils.isBlank(orgId)) {
			return ResultDataJsonUtils.errorResponseResult(ORG_ID_ERROR);
		}
		loadType = commonForMobileService.getDecryptStr(loadType,
				MOBILESESSIONID);
		if (StringUtils.isBlank(loadType)) {
			return ResultDataJsonUtils.errorResponseResult(LOAD_TYPE_ERROR);
		}
		List<Project> pros = organizationService.findOrganizationStatistics_project(Integer.parseInt(orgId), Integer.parseInt(loadType));
		return commonForMobileService.successResponseMapForMobile(pros, MOBILESESSIONID);
	  }

	  /**
	   * 获取的团队任务统计
	   * @param orgId
	   * @param loadType
	   * @return
	   */
	  @ResponseBody
	  @RequestMapping("/findOrganizationStatistics_team")
	  public Map<String, Object> findOrganizationStatistics_team(String orgId, String loadType,String MOBILESESSIONID) throws Exception{
		orgId = commonForMobileService.getDecryptStr(orgId, MOBILESESSIONID);
		if (StringUtils.isBlank(orgId)) {
			return ResultDataJsonUtils.errorResponseResult(ORG_ID_ERROR);
		}
		loadType = commonForMobileService.getDecryptStr(loadType,
				MOBILESESSIONID);
		if (StringUtils.isBlank(loadType)) {
			return ResultDataJsonUtils.errorResponseResult(LOAD_TYPE_ERROR);
		}
		List<Team> teams = organizationService.findOrganizationStatistics_team(Integer.parseInt(orgId), Integer.parseInt(loadType));
		return commonForMobileService.successResponseMapForMobile(teams, MOBILESESSIONID);
	  }
	  
	  /**
	   * 获取的组织人员工时统计
	   * @param orgId
	   * @param loadType
	   * @return
	   */
	  @ResponseBody
	  @RequestMapping("/findOrganizationTimeStatistics_user")
	  public Map<String, Object> findOrganizationTimeStatistics_user(String orgId, String loadType,String MOBILESESSIONID) throws Exception{
		orgId = commonForMobileService.getDecryptStr(orgId, MOBILESESSIONID);
		if (StringUtils.isBlank(orgId)) {
			return ResultDataJsonUtils.errorResponseResult(ORG_ID_ERROR);
		}
		loadType = commonForMobileService.getDecryptStr(loadType,
				MOBILESESSIONID);
		if (StringUtils.isBlank(loadType)) {
			return ResultDataJsonUtils.errorResponseResult(LOAD_TYPE_ERROR);
		}
		List<User> users = organizationService.findOrganizationTimeStatistics_user(Integer.parseInt(orgId), Integer.parseInt(loadType));
		return commonForMobileService.successResponseMapForMobile(users, MOBILESESSIONID);
	  }
	  
	  /**
	   * 获取的组织项目工时
	   * @param orgId
	   * @param loadType
	   * @return
	   */
	  @ResponseBody
	  @RequestMapping("/findOrganizationTimeStatistics_project")
	  public Map<String, Object> findOrganizationTimeStatistics_project(String orgId, String loadType,String MOBILESESSIONID) throws Exception{
		orgId = commonForMobileService.getDecryptStr(orgId, MOBILESESSIONID);
		if (StringUtils.isBlank(orgId)) {
			return ResultDataJsonUtils.errorResponseResult(ORG_ID_ERROR);
		}
		loadType = commonForMobileService.getDecryptStr(loadType,
				MOBILESESSIONID);
		if (StringUtils.isBlank(loadType)) {
			return ResultDataJsonUtils.errorResponseResult(LOAD_TYPE_ERROR);
		}
		List<Project> pros = organizationService.findOrganizationTimeStatistics_project(Integer.parseInt(orgId), Integer.parseInt(loadType));
		return commonForMobileService.successResponseMapForMobile(pros, MOBILESESSIONID);
	  }
	  /**
	   * 获取的团队项目工时
	   * @param orgId
	   * @return
	   */
	  @ResponseBody
	  @RequestMapping("/findOrganizationTimeStatistics_team")
	  public Map<String, Object> findOrganizationTimeStatistics_team(String orgId, String loadType,String MOBILESESSIONID) throws Exception{
		  orgId = commonForMobileService.getDecryptStr(orgId, MOBILESESSIONID);
			if (StringUtils.isBlank(orgId)) {
				return ResultDataJsonUtils.errorResponseResult(ORG_ID_ERROR);
			}
			loadType = commonForMobileService.getDecryptStr(loadType,
					MOBILESESSIONID);
			if (StringUtils.isBlank(loadType)) {
				return ResultDataJsonUtils.errorResponseResult(LOAD_TYPE_ERROR);
			}
		List<Team> teams = organizationService.findOrganizationTimeStatistics_team(Integer.parseInt(orgId), Integer.parseInt(loadType));
		return commonForMobileService.successResponseMapForMobile(teams, MOBILESESSIONID);
	  }
}
