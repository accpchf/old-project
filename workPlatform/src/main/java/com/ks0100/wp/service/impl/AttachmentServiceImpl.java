package com.ks0100.wp.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ks0100.common.constant.CommonConstant;
import com.ks0100.common.util.FTPClientTemplate;
import com.ks0100.common.util.PathUtil;
import com.ks0100.common.util.ReadPropertiesUtil;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.audit.ArgumentsForFile;
import com.ks0100.wp.audit.AuditServiceAnnotation;
import com.ks0100.wp.audit.AuditServiceAnnotation.ASPECTJ_TYPE;
import com.ks0100.wp.audit.AuditServiceAnnotation.USEAGE_TYPE;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.dao.AttachmentDao;
import com.ks0100.wp.dto.AttachmentDto;
import com.ks0100.wp.entity.Attachment;
import com.ks0100.wp.service.AttachmentService;

@Service
public class AttachmentServiceImpl implements AttachmentService {

	@Autowired
	private AttachmentDao attachmentDao;
	
/*	@Autowired
	private UserDao userDao;*/
	
	private SimpleDateFormat format = new SimpleDateFormat("/yyyy/MM/dd/HH/mm/SS");
	private DecimalFormat intFormat = new DecimalFormat("000");
	
	@Override
	 @AuditServiceAnnotation(useage=USEAGE_TYPE.u00618,argType1="com.ks0100.wp.audit.ArgumentsForFile")	 
	public void deleteAttachmentByFileUrl(ArgumentsForFile aff,String path, String fileUrl) {
		Attachment attachment = findAttachmentByFileUrl(fileUrl);
		if (attachment != null) {
			String filePath = path + CommonConstant.UPLOADFILEURL + attachment.getPath();
			//删除服务器上的文件
			File file = new File(filePath);
			if (file != null && file.exists()) {
				file.delete();
			}
		}
		attachmentDao.deleteAttachmentByFileUrl(fileUrl);
	}
	
	public Map<String, Object> saveFileToFtpServer( MultipartFile file,String fileType)throws Exception{
		Map<String, Object > map = new HashMap<String, Object>();
		
        FTPClientTemplate ftp = new FTPClientTemplate();
        ftp.setHost(ReadPropertiesUtil.getStringContextProperty("ftp.host"));
       // ftp.setPort(2121);
        ftp.setUsername(ReadPropertiesUtil.getStringContextProperty("ftp.username"));
        ftp.setPassword(ReadPropertiesUtil.getStringContextProperty("ftp.password"));
        String folderPath =createFolderPath(fileType);
        String fileName = file.getOriginalFilename();
        ftp.put(file.getOriginalFilename(),folderPath, file.getInputStream());
		map.put("fileName",fileName);
		map.put("filePath", folderPath + fileName);
		return map;
	}
	
	
	private String createFolderPath(String fileType){
		Date date = new Date();
		Random random = new Random();
		// 构建附件在服务器的保存路径 文件在服务器上保存的地址，按照:类型/年/月/日/小时/分/秒/毫秒/3位随机数/文件全名.
		return fileType + format.format(date) + "/" + date.getTime() + "/" + intFormat.format(random.nextInt(1000)) + "/";
		
	}
	/**
	 * 保存文件到服务器
	 *
	 * @param path
	 * @param file
	 * @param fileType
	 * @return
	 * @throws IOException
	 * 创建日期：2014-12-25
	 * 修改说明：
	 * @author chengls
	 */
	public Map<String, Object> saveFileToLocal( MultipartFile file, String fileType) throws Exception{
		File outFile = null;
		FileOutputStream out = null;
		InputStream in = null;
		Map<String, Object > map = new HashMap<String, Object>();
		try {
			
			//rootPath += CommonConstant.UPLOADFILEURL;
			// 文件名称
			String fileName = file.getOriginalFilename();
			
			String folderPath =createFolderPath( fileType);

			// 把文件写入服务器指定位置
			in = file.getInputStream();
			// 创建文件夹
			//outFile = new File(PathUtil.ABSOLUTE_WEB_PATH+CommonConstant.UPLOADFILEURL + folderPath);
			outFile = new File(PathUtil.ABSOLUTE_WEB_PATH+CommonConstant.UPLOADFILEURL +folderPath);
			if (!outFile.exists()) {
				outFile.mkdirs();
			}
			// 创建文件
			String filePath = folderPath + fileName;
			outFile = new File(PathUtil.ABSOLUTE_WEB_PATH+CommonConstant.UPLOADFILEURL + filePath);
			if (!outFile.exists()) {
				outFile.createNewFile();
			}
			out = new FileOutputStream(outFile);
			byte[] data = new byte[1024*1024];
			int i = 0;
			while ((i = in.read(data)) > 0) {
				out.write(data, 0, i);
			}
			out.flush();
			map.put("fileName", fileName);
			map.put("filePath", filePath);
		//	map.put("date", date);
		}catch(Exception ex){
			throw ex;
		}finally{
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
		return map;
	}
	
	@Override
	 @AuditServiceAnnotation(useage=USEAGE_TYPE.u00617,argType1="com.ks0100.wp.audit.ArgumentsForFile",argIndex2=3)	 
	public Attachment saveAttachment(ArgumentsForFile aff,MultipartFile file, String fileType,
			 String refTable, Integer refId, int parentId) throws Exception {
		//保存文件到服务器
		boolean isFTP = "true".equals(ReadPropertiesUtil
				.getStringContextProperty("ftp.isFTP")) ? true : false;
		Map<String, Object > map = null;
		if(isFTP){
			 map = saveFileToFtpServer( file, fileType);
		}else{
			 map = saveFileToLocal( file, fileType);
		}
		String fileName = (String) map.get("fileName");
		String filePath = (String) map.get("filePath");
		Date date = new Date();
		
		//保存附件信息到数据库
		Attachment attachment = new Attachment();
		attachment.setFileName(fileName);
		attachment.setFileType(fileType);
		attachment.setPath(filePath);
		Random random = new Random();
		//生成文件fileUrl
		String fileUrl = UUID.randomUUID().toString() + date.getTime() + intFormat.format(random.nextInt(1000));
		attachment.setFileUrl(fileUrl);
		
		if (parentId > 0) {
			Attachment am = new Attachment();
			am.setId(parentId);
			attachment.setParentAttachment(am);
		}
		
		if (StringUtils.trimToNull(refTable) != null && refId != null && refId > 0) {
			attachment.setRefTable(refTable);
			attachment.setRefId(refId);
		}
		ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		attachment.setCreatedBy(currentUser.getUserId());
		attachment.setUpdatedBy(currentUser.getUserId());
		attachmentDao.save(attachment);
		return attachment;
	}

	@Override
	public Attachment findAttachmentByFileUrl(String fileUrl) {
		if (StringUtils.trimToNull(fileUrl) == null) {
			return null;
		}
		List<Attachment> attachments = attachmentDao.findAttachmentByFileUrl(fileUrl);
		return attachments != null ? attachments.get(0) : null;
	}

	@Override
	public void deleteAttachmentByRef(String ref_table, int ref_id) {
		attachmentDao.deleteAttachmentByRef(ref_table, ref_id);
	}

	@Override
	public void deleteMeetingAttachment(String path, int meetingId) {
		//删除会议附件在服务器上的文件
		List<Attachment> attachments = attachmentDao.listMeetingFiles("all");
		for (Attachment attachment : attachments) {
			if (attachment.getRefId() != meetingId) {
				continue;
			}
			String filePath = path + CommonConstant.UPLOADFILEURL + attachment.getPath();
			//删除服务器上的文件
			File file = new File(filePath);
			if (file != null && file.exists()) {
				file.delete();
			}
		}
		//删除会议附件数据
		attachmentDao.deleteAttachmentByRef(BusinessConstant.TableNameConstant.TABLE_MEETING, meetingId);
		
	}


	
	@Override
	public AttachmentDto listFilesChildren(int projectId, int fileId) {
		List<Attachment> lists = attachmentDao.findAttachments(BusinessConstant.TableNameConstant.TABLE_PROJECT,  projectId, BusinessConstant.FileType.FILESAVETYPE_FILELIBRARY);
		//当前文件对象
		AttachmentDto attachmentDto = new AttachmentDto();
		//如果fileId为0，则查询根目录下面的文件
		if (fileId <= 0) {
			//当前目录为根目录，初始化根目录的基本信息
			attachmentDto.rootFolderInitData();
			
			List<AttachmentDto> children = new ArrayList<AttachmentDto>();
			//查找当前目录的子文件或子文件夹
			attachmentDto.setChildren(children);
			for (Attachment temp : lists) {
				//如果该文件或文件夹的父亲为空，则表示该文件或文件夹位于根目录下，则将其加入子文件中
				if (temp.getParentAttachment() == null) {
					AttachmentDto tempDto = new AttachmentDto();
					tempDto.setAttachment(temp);
					children.add(tempDto);
				}
			}
			AttachmentDto dto = new AttachmentDto();
			//当前目录为根目录，初始化根目录的父级链；
			attachmentDto.getParent().add(dto);
			dto.rootFolderInitData();
		}else {
			//如果fileId不为0，则查询该文件下面的文件
			for (Attachment temp : lists) {
				//从lists中找到当前对象，并添加其基本信息
				if (temp.getId() == fileId) {
					attachmentDto.setAttachment(temp);
				}
				
				//把lists中的对象中父亲id等于当前文件对象的id的文件加入当前文件的children中
				if (temp.getParentAttachment() != null && temp.getParentAttachment().getId() == fileId) {
					AttachmentDto tempDto = new AttachmentDto();
					tempDto.setAttachment(temp);
					attachmentDto.getChildren().add(tempDto);
				}
			}
			List<AttachmentDto> parentAtt = new ArrayList<AttachmentDto>(); 
			//初始化当前文件对象的父级链
			attachmentDto.setParent(parentAtt);
			Attachment current = attachmentDto.getAttachment();
			
			//通过从当前文件对象一直遍历至根目录下的文件，并把该链上的文件加入到父级链中
			while(current != null){
				AttachmentDto dto1 = new AttachmentDto();
				dto1.setAttachment(current);
				parentAtt.add(dto1);
				current = current.getParentAttachment();
			}
			
			AttachmentDto dto = new AttachmentDto();
			//根目录也是一链的一点，把根目录加入父级链
			parentAtt.add(dto);
			dto.rootFolderInitData();
			
			if (attachmentDto.getAttachment().getId() <= 0) {
				attachmentDto.getAttachment().setId(-1);
			}
		}
		return attachmentDto;
	}

	@Override
	public Attachment findAttachmentById(int fileId) {
		return attachmentDao.get(fileId);
/*		Map<String, Object > map = new HashMap<String, Object>();
		map.put("id", fileId);
		map.put("enabled", 1);
		return attachmentDao.get(map);*/
	}

	@SuppressWarnings("boxing")
	@Override
	 @AuditServiceAnnotation(useage=USEAGE_TYPE.u00621,argType1="com.ks0100.wp.entity.Attachment",argIndex2=2)
	public void updateFileName(Attachment attachment,String oldFileName) {
		ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		attachment.setUpdatedBy(currentUser.getUserId());
		this.attachmentDao.save(attachment);
	}

	@Override
	public void saveAttachment(Attachment attachment) {
		ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		attachment.setCreatedBy(currentUser.getUserId());
		attachment.setUpdatedBy(currentUser.getUserId());
		attachment.setEnabled(true);
		attachmentDao.save(attachment);
	}

	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00620,argType1="com.ks0100.wp.entity.Attachment",argIndex2=2)
	public void deleteAttachment(Attachment attachment, List<Attachment> childrenList) {
		
		if(attachment!=null){
			if(attachment.isFolder()){
				List<Integer> idList=new ArrayList<Integer>(); 
				idList.add(attachment.getId());
				for(Attachment a:childrenList){
					idList.add(a.getId());
				}
				attachmentDao.getListByIds(idList);
				attachmentDao.deleteAttachmentByIds(idList);
			}else{
				deleteAttachment(attachment);
			}
		}
		
	}
	
	public String[] findTreeIds(int rootId,int projectId){
		List<Attachment> lists = attachmentDao.findAttachments(BusinessConstant.TableNameConstant.TABLE_PROJECT, projectId, BusinessConstant.FileType.FILESAVETYPE_FILELIBRARY);
		String ids = "0," + rootId ;
		for (Attachment temp_attachment : lists) {
			Attachment current = temp_attachment;
			String temp = "";
			while(current != null){
				temp += "," + current.getId();
				if (current.getParentAttachment() != null && current.getParentAttachment().getId() == rootId) {
					ids += temp;
					break;
				}
				current = current.getParentAttachment();
			}
		}
		String[] idarray=ids.split(",");
		return idarray;
	}
	
	public List<Attachment> findTreeList(int rootId,int projectId){
		
		List<Integer> idList=new ArrayList<Integer>(); 
		idList.add(rootId);
		String[] idarray=findTreeIds( rootId, projectId);
		for(String id:idarray){
			idList.add(Integer.valueOf(id));
		}
		 List<Attachment> attachmentList=attachmentDao.getListByIds( idList);
		 return attachmentList;
	}
	
	public void deleteAttachment(Attachment attachment){
		ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		attachment.setUpdatedBy(currentUser.getUserId());
		attachment.setEnabled(false);
		attachmentDao.saveOrUpdate(attachment);
	}
	
	@Override
	public List<Map<String, Object>> listFolders(int projectId, int parentFolerId) {
		List<Attachment> lists = attachmentDao.findAttachments(BusinessConstant.TableNameConstant.TABLE_PROJECT,  projectId, BusinessConstant.FileType.FILESAVETYPE_FILELIBRARY);
		List<Map<String, Object>> folders = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = null;
		Attachment parentFoler = null;
		for (Attachment attachment : lists) {
			if (!attachment.isFolder()) {
				continue;			
			}
			if(attachment.getId() == parentFolerId){
				parentFoler = attachment;
			}
			map = new HashMap<String, Object>();
			map.put("id", attachment.getId());
			if (attachment.getParentAttachment() == null || attachment.getParentAttachment().getId() == 0) {
				map.put("pId", 0);
			}else {
				map.put("pId", attachment.getParentAttachment().getId());
			}
			map.put("name", attachment.getFileName());
			map.put("isParent", true);
			folders.add(map);
		}
		map = new HashMap<String, Object>();
		map.put("id", 0);
		map.put("pId", null);
		map.put("name", "根目录");
		map.put("isParent", true);
		map.put("open", true);
		folders.add(map);
		
		List<Integer> openIds = new ArrayList<Integer>();
		while(parentFoler != null){
			openIds.add(parentFoler.getId());
			parentFoler = parentFoler.getParentAttachment();
		}
		for (Map<String, Object> file : folders) {
			if(openIds.contains(file.get("id"))){
				file.put("open", true);
			}
		}
		return folders;
	}

	@AuditServiceAnnotation(useage=USEAGE_TYPE.u00622,argIndex1=1,argIndex2=2,aspectJ_type=ASPECTJ_TYPE.BEFORE)
	public void moveFile(Attachment attachment, Attachment destinationAttachment) {
	//	Attachment attachment = findAttachmentById(fId);
/*		Attachment pAtta = null;
		if (pId > 0 ) {
			pAtta = new Attachment();
			pAtta.setId(pId);
		}*/
		attachment.setParentAttachment(destinationAttachment);
		ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		attachment.setUpdatedBy(currentUser.getUserId());
		attachmentDao.update(attachment);
	}
	
/*	@Override
	public void moveFile(int fId, int pId) {
		Attachment attachment = findAttachmentById(fId);
		Attachment pAtta = null;
		if (pId > 0 ) {
			pAtta = new Attachment();
			pAtta.setId(pId);
		}
		attachment.setParentAttachment(pAtta);
		ShiroUser currentUser = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
		attachment.setUpdatedBy(currentUser.getUserId());
		attachmentDao.update(attachment);
	}*/

}
