package com.ks0100.wp.dto;

public class TaskCheckListDto {

	private int checkListId;
	
	private String checkListContent;
	
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getCheckListId() {
		return checkListId;
	}

	public void setCheckListId(int checkListId) {
		this.checkListId = checkListId;
	}

	public String getCheckListContent() {
		return checkListContent;
	}

	public void setCheckListContent(String checkListContent) {
		this.checkListContent = checkListContent;
	}
	
	
}
