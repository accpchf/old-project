package com.ks0100.wp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.ks0100.common.ResultDataJsonUtils;
import com.ks0100.common.constant.CommonConstant;
import com.ks0100.common.util.FTPClientTemplate;
import com.ks0100.common.util.PathUtil;
import com.ks0100.common.util.ReadPropertiesUtil;
import com.ks0100.wp.audit.ArgumentsForFile;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.constant.BusinessConstant.PermitConstant;
import com.ks0100.wp.dto.AttachmentDto;
import com.ks0100.wp.entity.Attachment;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.service.AttachmentService;
import com.ks0100.wp.service.ProjectService;

@Controller
@RequestMapping("/filelibrary")
public class FileLibraryController extends BaseController {
  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  AttachmentService attachmentService;
  @Autowired
  public ProjectService projectService;
  /**
   * 项目菜单页面，点击文件库，加载文件库页面
   *
   * @return
   * 创建日期：2014-12-25
   * 修改说明：
   * @author chengls
   */
  @RequestMapping("/loadfilelibraryview")
  public String loadFileLibraryView(HttpServletRequest request, ModelMap model) {
	String projectId = request.getParameter("projectId");
	Project project = projectService.findProjectById(Integer.parseInt(projectId));
	if(!hasPermit(BusinessConstant.PermitConstant.PRJ_ACCESS, Integer.parseInt(projectId))) {
	  return "/login/unauthorized";
	}
	model.put("project", project);
	List<String> permitCodeAndIds = new ArrayList<String>();
	permitCodeAndIds.add(PermitConstant.PRJ_ADMIN_ACCESS + ":" + projectId);
	permitCodeAndIds.add(PermitConstant.PRJ_SET + ":" + projectId);
	model.put("permitCodeAndIds", permitCodeAndIds);
	return "fileLibrary/fileLibrary";
  }

  /**
   * 通过父级文件id获取所有子文件
   *
   * @param projectId
   * @param fileId
   * @return
   * 创建日期：2014-12-25
   * 修改说明：
   * @author chengls
   */
  @ResponseBody
  @RequestMapping(value = "/listFiles", method = RequestMethod.POST)
  public Map<String, Object> listFilesChildren(int projectId, int fileId) {
	AttachmentDto attachmentDto = attachmentService.listFilesChildren(projectId, fileId);
	if(attachmentDto.getAttachment().getId() < 0) {
	  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
		  .getStringContextProperty("fileLibarary_dataIsOld"));
	}
	return ResultDataJsonUtils.successResponseResult(attachmentDto);
  }

  /**
   * 新建,修改文件或文件夹
   *
   * @param fileId
   * @param fileName
   * @return
   * 创建日期：2014-12-25
   * 修改说明：
   * @author chengls
   */
  @ResponseBody
  @RequestMapping(value = "operatorfilelibrary", method = RequestMethod.POST)
  public Map<String, Object> operateFileLibrary(int fileId, String fileName, int projectId,
	  int folderId) {
	Attachment attachment = null;
	if(StringUtils.trimToNull(fileName) == null) {
	  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
		  .getStringContextProperty("fileLibrary_nameNotNull"));
	}
	if(fileId <= 0) {//新建文件夹
	  if(hasDuplicateFolderName(fileName, projectId, folderId)) {
		return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
			.getStringContextProperty("fileLibrary_folderName.duplicate"));
	  }
	  attachment = new Attachment();
	  attachment.setFolder(true);
	  attachment.setFileName(fileName);
	  attachment.setFileType(BusinessConstant.FileType.FILESAVETYPE_FILELIBRARY);
	  if(folderId > 0) {
		Attachment am = new Attachment();
		am.setId(folderId);
		attachment.setParentAttachment(am);
	  }
	  attachment.setRefId(projectId);
	  attachment.setRefTable(BusinessConstant.TableNameConstant.TABLE_PROJECT);
	  this.attachmentService.saveAttachment(attachment);
	} else {//修改文件夹和文件名
	  attachment = this.attachmentService.findAttachmentById(fileId);
	  if(attachment.isFolder() && hasDuplicateFolderName(fileName, projectId, folderId)) {
		return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
			.getStringContextProperty("fileLibrary_folderName.duplicate"));
	  }
	  if(attachment == null || attachment.getId() <= 0) {
		return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
			.getStringContextProperty("fileLibarary_dataIsOld"));
	  }
	  String oldfileName = new String(attachment.getFileName());
	  attachment.setFileName(fileName);
	  this.attachmentService.updateFileName(attachment, oldfileName);
	}
	return ResultDataJsonUtils.successResponseResult();
  }

  private boolean hasDuplicateFolderName(String fileName, int projectId, int folderId) {
	List<Map<String, Object>> fList = this.attachmentService.listFolders(projectId, folderId);
	for(Map<String, Object> map : fList) {
	  Object pId = map.get("pId");
	  Object name = map.get("name");
	  if(pId == null || name == null) {
		continue;
	  }
	  if(name.toString().equalsIgnoreCase(fileName) && Integer.parseInt(pId.toString()) == folderId) {
		return true;
	  }
	}
	return false;
  }

  /**
   * 添加文件
   *
   * @param request
   * @param meetingId
   * @return
   * 创建日期：2014-12-15
   * 修改说明：
   * @author chengls
   */
  @ResponseBody
  @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
  public Map<String, Object> addFileLibraryAdjunct(HttpServletRequest request, int folderId,
	  int projectId) {
	MultipartHttpServletRequest mulRequest = (MultipartHttpServletRequest)request;
	MultipartFile file = mulRequest.getFile("upload_file");
	if(file == null || file.getSize() <= 0) {
	  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
		  .getStringContextProperty("attachment_uploadfail"));
	}
	Attachment attachment = null;
	try {
	  ArgumentsForFile aff = new ArgumentsForFile();
	  aff.setFileName(file.getOriginalFilename());
	  aff.setFilesize(file.getSize());
	  aff.setProjectId(projectId);
	  attachment = attachmentService.saveAttachment(aff, file,
		  BusinessConstant.FileType.FILESAVETYPE_FILELIBRARY,
		  BusinessConstant.TableNameConstant.TABLE_PROJECT, projectId, folderId);
	} catch(Exception e) {
	  logger.error("error:", e);
	  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
		  .getStringContextProperty("attachment_uploadfail"));
	}
	return ResultDataJsonUtils.successResponseResult(attachment);
  }

  /**
   * 通过ftp上传文件
   * @param response
   * @param fileUrl
   */
  @RequestMapping(value = "/downloadfile", method = RequestMethod.POST)
  public void downLoad(HttpServletResponse response, String fileUrl) throws Exception {
	boolean isFTP = "true".equals(ReadPropertiesUtil.getStringContextProperty("ftp.isFTP")) ? true
		: false;
	OutputStream out = null;
	if(isFTP) {
	  FTPClientTemplate ftp = new FTPClientTemplate();
	  ftp.setHost(ReadPropertiesUtil.getStringContextProperty("ftp.host"));
	  ftp.setUsername(ReadPropertiesUtil.getStringContextProperty("ftp.username"));
	  ftp.setPassword(ReadPropertiesUtil.getStringContextProperty("ftp.password"));
	  try {
		Attachment attachment = attachmentService.findAttachmentByFileUrl(fileUrl);
		if(attachment == null) {
		  throw new Exception();
		}

		response.reset();
		response.setContentType("application/x-download; charset=utf-8");
		String userFileName = attachment.getFileName();
		userFileName = URLEncoder.encode(userFileName, "utf-8");
		response.addHeader("Content-Disposition", "attachment;filename=" + userFileName);
		//response.addHeader("Content-Disposition", "inline;filename=" + userFileName);
		out = response.getOutputStream();

		String filePath = attachment.getPath();

		ftp.get("/" + filePath, out);
	  } catch(Exception e) {
		  throw new Exception();
	  }finally {
			out.flush();
			out.close();
			out = null;
	}
	  
	} else {
	  FileInputStream nowIn = null;
	  try {
		Attachment attachment = attachmentService.findAttachmentByFileUrl(fileUrl);
		if(attachment == null) {
		  throw new Exception();
		}
		String filePath = PathUtil.ABSOLUTE_WEB_PATH + CommonConstant.UPLOADFILEURL + "/"
			+ attachment.getPath();
		File file = new File(filePath);
		if(file == null || !file.exists()) {
		  throw new Exception();
		}

		response.reset();
		response.setContentType("application/x-download; charset=utf-8");
		String userFileName = attachment.getFileName();
		userFileName = URLEncoder.encode(userFileName, "utf-8");
		response.addHeader("Content-Disposition", "attachment;filename=" + userFileName);
		//response.addHeader("Content-Disposition", "inline;filename=" + userFileName);
		nowIn = new FileInputStream(file);
		out = response.getOutputStream();
		byte[] b = new byte[1024 * 1024];
		int i = 0;
		while((i = nowIn.read(b)) > 0) {
		  out.write(b, 0, i);
		}
	  } catch(Exception ex) {
		throw ex;
	  } finally {
		out.flush();
		nowIn.close();
		out.close();
		nowIn = null;
		out = null;
	  }
	}

  }

  /**
   * 通过文件夹id删除当前文件夹下面所有的文件
   *
   * @param folderId
   * @param projectId
   * @return
   * 创建日期：2014-12-26
   * 修改说明：
   * @author chengls
   */
  @ResponseBody
  @RequestMapping(value = "deletefilelibrary", method = RequestMethod.POST)
  public Map<String, Object> deleteFileLibraryAdjunct(int folderId, int projectId) {

	Attachment attachment = attachmentService.findAttachmentById(folderId);
	if(attachment != null) {
	  if(attachment.isFolder()) {
		List<Attachment> childrenList = attachmentService.findTreeList(folderId, projectId);
		attachmentService.deleteAttachment(attachment, childrenList);
	  } else {
		attachmentService.deleteAttachment(attachment, null);
	  }
	}
	return ResultDataJsonUtils.successResponseResult();
  }

  /**
   * 弹出移动页面
   *
   * @return
   * 创建日期：2014-12-26
   * 修改说明：
   * @author chengls
   */
  @RequestMapping(value = "openfilemoveview")
  public String toFileMoveView(Model model, String fromFileName) {
	model.addAttribute("fromFileName", fromFileName);
	return "fileLibrary/fileMove";
  }

  /**
   * 移动时，获取被移动的文件夹列表（树形）
   *
   * @param projectId
   * @return
   * 创建日期：2014-12-29
   * 修改说明：
   * @author chengls
   */
  @ResponseBody
  @RequestMapping(value = "listfolders", method = RequestMethod.POST)
  public Map<String, Object> listFolders(int projectId, int parentFolerId) {
	List<Map<String, Object>> lists = attachmentService.listFolders(projectId, parentFolerId);
	return ResultDataJsonUtils.successResponseResult(lists);
  }

  /**
   * 移动文件夹
   *
   * @param fId待移动的文件或文件夹的id
   * @param pId移动目的目录的id
   * @return
   * 创建日期：2014-12-29
   * 修改说明：
   * @author chengls
   */
  @ResponseBody
  @RequestMapping(value = "movefile", method = RequestMethod.POST)
  public Map<String, Object> moveFile(int fId, int pId) {
	if(fId == pId) {
	  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
		  .getStringContextProperty("fileLibrary_move_illegal"));
	}

	Attachment attachment = attachmentService.findAttachmentById(fId);

	Attachment destinationAttachment = attachmentService.findAttachmentById(pId);

	if(attachment == null) {
	  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
		  .getStringContextProperty("fileLibarary_dataIsOld"));
	} else if(pId > 0 && destinationAttachment == null) {
	  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
		  .getStringContextProperty("fileLibarary_dataIsOld"));
	}

	String[] idArray = attachmentService.findTreeIds(fId, attachment.getRefId());
	if(pId > 0 && Arrays.asList(idArray).contains(String.valueOf(pId))) {
	  return ResultDataJsonUtils.errorResponseResult(ReadPropertiesUtil
		  .getStringContextProperty("fileLibrary_move_illegal"));
	}

	attachmentService.moveFile(attachment, destinationAttachment);
	return ResultDataJsonUtils.successResponseResult();
  }

}
