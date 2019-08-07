package com.ks0100.wp.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装组织列表dto
 * @author ctx_zhang tan
 *
 */
public class OrganizationDto {
	
	//组织id
	private int id;
	//组织名称
	private String name;
	//项目名称
	private List<ProjectDto> projectList = new ArrayList<ProjectDto>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ProjectDto> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<ProjectDto> projectList) {
		this.projectList = projectList;
	}

	
	
	
	
	
}
