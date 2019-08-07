package com.ks0100.wp.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 封装项目详情dto
 * @author ctx_zhang tan
 *
 */
public class ProjectDto {
	
	//项目id
	private int projectId;
	//项目名称
	private String name;
	//项目开始时间
	private Date beginTime;
	//项目结束时间
	private Date endTime;
	//项目状态
	private String status;
	//项目uuid
	private String uuid;
	
	private List<TaskDto> taskDtos = new ArrayList<TaskDto>();
	

	public List<TaskDto> getTaskDtos() {
		return taskDtos;
	}

	public void setTaskDtos(List<TaskDto> taskDtos) {
		this.taskDtos = taskDtos;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
