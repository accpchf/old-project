package com.ks0100.wp.dto;

import java.util.ArrayList;
import java.util.List;

import com.ks0100.wp.entity.Attachment;

public class MeetingDto {

		// 会议id
		private int meetingId;
		
		//会议标题
		private String title;
		
		//会议时间
		private String beginTime;
		
		//会议地点
		private String place;
		
		//会议描述
		private String detail;
		
		//会议纪要
		private String record;
		
		private int projectId;
		
		private String projectName;
		
		//会议的参与者
		private List<Integer> users = new ArrayList<Integer>();
		
		//会议的附件
		private List<Attachment> attachments = new ArrayList<Attachment>();

		
		public int getProjectId() {
			return projectId;
		}

		public void setProjectId(int projectId) {
			this.projectId = projectId;
		}

		public String getProjectName() {
			return projectName;
		}

		public void setProjectName(String projectName) {
			this.projectName = projectName;
		}

		public int getMeetingId() {
			return meetingId;
		}

		public void setMeetingId(int meetingId) {
			this.meetingId = meetingId;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getBeginTime() {
			return beginTime;
		}

		public void setBeginTime(String beginTime) {
			this.beginTime = beginTime;
		}

		public String getPlace() {
			return place;
		}

		public void setPlace(String place) {
			this.place = place;
		}

		public String getDetail() {
			return detail;
		}

		public void setDetail(String detail) {
			this.detail = detail;
		}

		public String getRecord() {
			return record;
		}

		public void setRecord(String record) {
			this.record = record;
		}

		public List<Integer> getUsers() {
			return users;
		}

		public void setUsers(List<Integer> users) {
			this.users = users;
		}

		public List<Attachment> getAttachments() {
			return attachments;
		}

		public void setAttachments(List<Attachment> attachments) {
			this.attachments = attachments;
		}
		
		
}
