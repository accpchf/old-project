package com.ks0100.wp.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;



/**
 * The persistent class for the tbl_attachment database table.
 * 
 */
@Entity
@Table(name="tbl_attachment")
public class Attachment extends CommonEntity  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3967837027595200346L;


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	//附件名称
	@Column(name="file_name")
	private String fileName;
	
	//附件唯一标识
	@Column(name="file_url")
	private String fileUrl;
	
	//附件在服务器上的保存路径
	@Column
	private String path;
	
	//附件类型
	@Column(name="file_type")
	private String fileType;

	//附件关联表id
	@Column(name="ref_id")
	private int refId;

	//附件关联表
	@Column(name="ref_table")
	private String refTable;
	
	//判断当前数据是否是文件夹
	@Column(name="is_folder")
	private boolean isFolder;

	//父节点
	@ManyToOne
	@JoinColumn(name = "parent_attachment_id")
	private Attachment parentAttachment;



	public Attachment() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return this.fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}


	public int getRefId() {
		return this.refId;
	}

	public void setRefId(int refId) {
		this.refId = refId;
	}

	public String getRefTable() {
		return this.refTable;
	}

	public void setRefTable(String refTable) {
		this.refTable = refTable;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public boolean isFolder() {
		return isFolder;
	}

	public void setFolder(boolean isFolder) {
		this.isFolder = isFolder;
	}

	public Attachment getParentAttachment() {
		return parentAttachment;
	}

	public void setParentAttachment(Attachment parentAttachment) {
		this.parentAttachment = parentAttachment;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}



	
}