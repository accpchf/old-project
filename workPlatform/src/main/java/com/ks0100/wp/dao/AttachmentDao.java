package com.ks0100.wp.dao;

import java.util.List;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.entity.Attachment;

public interface AttachmentDao extends SimpleHibernateDao<Attachment, Integer> {
	/**
	 * 查询会议附件
	 *
	 * @param ids(值为all时，则查询所有会议相关的附件 )
	 * @return
	 * 创建日期：2014-12-25
	 * 修改说明：
	 * @author chengls
	 */
	List<Attachment> listMeetingFiles(String ids);

	void deleteAttachmentByFileUrl(String fileUrl);

	List<Attachment> findAttachmentByFileUrl(String fileUrl);

	void deleteAttachmentByRef(String ref_table, int ref_id);
	
	/**
	 * 通过关联表和类型查找所有附件
	 *
	 * @param tableName
	 * @param id
	 * @param fileType
	 * @return
	 * 创建日期：2014-12-25
	 * 修改说明：
	 * @author chengls
	 */
	List<Attachment> findAttachments(String tableName, int id, String fileType);
	
	void deleteAttachmentByIds(List<Integer> idList);
//	void deleteAttachmentByIds(String ids);

}
