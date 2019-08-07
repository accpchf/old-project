package com.ks0100.wp.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ks0100.wp.entity.Attachment;

public class AttachmentDto implements Serializable{

	private static final long serialVersionUID = 7293102070508775704L;
	
	//保存当前文件的基本信息
	private Attachment attachment = new Attachment();
	//保存当前文件的父亲目录，一直到根目录形如['三级','二级', '一级' ,'根目录']
	private List<AttachmentDto> parent = new ArrayList<AttachmentDto>();
	//保存当前文件的儿子节点
	private List<AttachmentDto> children = new ArrayList<AttachmentDto>();
	private Integer createdUserId;
	private Date createdTime;
	public List<AttachmentDto> getChildren() {
		return children;
	}
	public void setChildren(List<AttachmentDto> children) {
		this.children = children;
	}
	public List<AttachmentDto> getParent() {
		return parent;
	}
	public void setParent(List<AttachmentDto> parent) {
		this.parent = parent;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	public Integer getCreatedUserId() {
		return createdUserId;
	}
	public void setCreatedUserId(Integer createdUserId) {
		this.createdUserId = createdUserId;
	}
	public Attachment getAttachment() {
		return attachment;
	}
	public void setAttachment(Attachment attachment) {
		this.attachment = attachment;
	}
	/**
	 * 初始化根目录的基本信息
	 *
	 * 创建日期：2015-1-5
	 * 修改说明：
	 * @author chengls
	 */
	public void rootFolderInitData(){
		if (attachment == null) {
			attachment = new Attachment();
		}
		attachment.setId(0);
		attachment.setFileName("根目录");
		attachment.setFolder(true);
		attachment.setFileUrl("");
	}
}
