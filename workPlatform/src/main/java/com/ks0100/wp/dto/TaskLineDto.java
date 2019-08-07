package com.ks0100.wp.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装任务列dto
 * @author ctx_zhang tan
 *
 */
public class TaskLineDto {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = -7583148888464426634L;

	// 任务列id
	private int taskLineId;
	// 任务列名称
	private String name;
	// 包含任务
	private List<TaskDto> taskDtoList = new ArrayList<TaskDto>();
	
	public int getTaskLineId() {
		return taskLineId;
	}
	public void setTaskLineId(int taskLineId) {
		this.taskLineId = taskLineId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<TaskDto> getTaskDtoList() {
		return taskDtoList;
	}
	public void setTaskDtoList(List<TaskDto> taskDtoList) {
		this.taskDtoList = taskDtoList;
	}
}
