package com.ks0100.wp.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.ks0100.wp.audit.ArgumentsForFile;
import com.ks0100.wp.dto.AttachmentDto;
import com.ks0100.wp.entity.Attachment;

public interface AttachmentService {
	void deleteAttachmentByFileUrl(ArgumentsForFile aff,String path, String fileUrl);
	
	/**
	 * 
	 *
	 * @param file文件流
	 * @param fileType 文件保存类型，00500文件库文件，00501会议文件，00502任务附件，00503聊天文件
	 * @param path 当前项目路径
	 * @param refTable 附件保存关联表，没有可为null
	 * @param refId 附件关联id
	 * @param parentId 文件父目录Id
	 * 创建日期：2014-12-15
	 * 修改说明：
	 * @author chengls
	 * @throws Exception 
	 */
	Attachment saveAttachment(ArgumentsForFile aff,MultipartFile file, String fileType, String refTable, Integer refId, int parentId) throws Exception;

	Attachment findAttachmentByFileUrl(String fileUrl);
	
	/**
	 * 通过关联表关系删除附件
	 *
	 * @param ref_table
	 * @param ref_id
	 * 创建日期：2014-12-25
	 * 修改说明：
	 * @author chengls
	 */
	void deleteAttachmentByRef(String ref_table, int ref_id);

	void deleteMeetingAttachment(String path, int meetingId);

	/**
	 * 通过项目Id和文件Id获取子文件
	 *
	 * @param projectId
	 * @param fileId
	 * @return
	 * 创建日期：2014-12-25
	 * 修改说明：
	 * @author chengls
	 */
	AttachmentDto listFilesChildren(int projectId, int fileId);

	Attachment findAttachmentById(int fileId);
	
	void updateFileName(Attachment attachment,String oldFileName);
	
	void saveAttachment(Attachment attachment);

	/**
	 * 通过文件夹删除其下所有文件
	 *
	 * @param folderId
	 * @param projectId
	 * 创建日期：2014-12-26
	 * 修改说明：
	 * @author chengls
	 */
	//void deleteAttachmentByFolderId(int folderId, int projectId);

	/**
	 * 删除文件或文件夹，如果是文件设置childrenList为null,如果是文件夹需要先查询该文件夹下面所有的子文件夹和子文件
	 * @param attachment
	 * @param childrenList
	 */
	void deleteAttachment(Attachment attachment, List<Attachment> childrenList) ;
	 
	List<Map<String, Object>> listFolders(int projectId, int parentFolerId);

	void moveFile(Attachment attachment, Attachment destinationAttachment);
	//void moveFile(int fId, int pId);
	
	/**
	 * 获取一个文件夹下所有子文件和子文件夹
	 * @param parentId
	 * @param projectId
	 * @return
	 */
	List<Attachment> findTreeList(int rootId,int projectId);
	/**
	 * 获取一个文件夹下所有子文件和子文件夹的id
	 * @param parentId
	 * @param projectId
	 * @return
	 */
	String[] findTreeIds(int rootId,int projectId);
	
	Map<String, Object> saveFileToLocal( MultipartFile file, String fileType) throws Exception;

	Map<String, Object> saveFileToFtpServer( MultipartFile file,String fileType)throws Exception;
}
