package com.ks0100.wp.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.ks0100.common.hibernate.SimpleHibernateDaoImpl;
import com.ks0100.wp.constant.BusinessConstant;
import com.ks0100.wp.constant.StatusEnums.FileType;
import com.ks0100.wp.dao.AttachmentDao;
import com.ks0100.wp.entity.Attachment;

@Repository
public class AttachmentDaoImpl extends SimpleHibernateDaoImpl<Attachment, Integer> implements AttachmentDao {

	public List<Attachment> listMeetingFiles(String ids) {
		String temp = " ";
		if (!"all".equals(StringUtils.trim(ids))) {
			temp +=  " a.refId in ("+ ids+") and ";
		}
		String hql = "from Attachment a where " + temp + " a.refTable = '" + BusinessConstant.TableNameConstant.TABLE_MEETING + "' and a.fileType = '"+
				FileType.METTING.getCode()+"' and a.enabled = 1";
		return find(hql);
	}

	public void deleteAttachmentByFileUrl(String fileUrl) {
		String hql = "update tbl_attachment a set a.enabled = 0  where a.file_url = :fileUrl";
		getSession().createSQLQuery(hql).setParameter("fileUrl", fileUrl).executeUpdate();
	}

	public List<Attachment> findAttachmentByFileUrl(String fileUrl) {
		String hql = "from Attachment a where a.fileUrl = ? and a.enabled = 1";
		return find(hql, fileUrl);
	}

/*	public List<Attachment> findAttachmentsByIds(){
		
	}*/
	
	public List<Attachment> findAttachments(String tableName, int id, String fileType) {
		String hql = "from Attachment a where a.refTable = ? and a.refId = ? and a.fileType = ? and a.enabled = 1 order by a.isFolder desc";
		return find(hql, tableName, id, fileType);
	}

	@Override
	public void deleteAttachmentByRef(String ref_table, int ref_id) {
		String hql = "update tbl_attachment set enabled = 0  where ref_table = :refTable and ref_id = :refId";
		getSession().createSQLQuery(hql).setParameter("refTable", ref_table).setParameter("refId", ref_id).executeUpdate();
	}
	
	

	
	public void deleteAttachmentByIds(List<Integer> idList) {
		if (idList==null||idList.isEmpty()) {
			return;
		}
		String sql = "update Attachment set enabled = 0 where id in (:ids)";
		Query query = getSession().createQuery(sql);
		query.setParameterList("ids", idList);
		query.executeUpdate();
		
	}

}
