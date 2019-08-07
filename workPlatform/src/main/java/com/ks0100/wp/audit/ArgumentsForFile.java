package com.ks0100.wp.audit;

/**
 * 用于上传文件的动态日志记录
 * @author chen haifeng
 *
 */
public class ArgumentsForFile {
	
	private String fileName;
	
	private long filesize;
	
	private int taskId;
	
	private int projectId;

	private String operateType;
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public int getProjectId() {
		return projectId;
	}

	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	
	
}
