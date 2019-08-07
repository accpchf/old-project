package com.ks0100.wp.dao;

import java.util.List;
import java.util.Map;

import com.ks0100.common.hibernate.SimpleHibernateDao;
import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.entity.Meeting;

public interface MeetingDao extends SimpleHibernateDao<Meeting, Integer> {

  List<Meeting> listMeetingsSortByCreatedTime(int projectId);

  /**
   * 删除会议参与者
   *
   * @param meetingId
   * @param idsString（值为all时，则删除全部参与者）
   * 创建日期：2014-12-25
   * 修改说明：
   * @author chengls
   */
  void deleteMeetingUser(int meetingId, String idsString);

  List<Integer> findMeetingUser(int meetingId, String idsString);

  void addMeetingUser(int meetingId, String[] ids);

  void addMeetingUserRole(Meeting meeting, ShiroUser user, String roleCode);
  
  List<Map<String, Object>> listFutureMeetingByUserIdForMobile(int userId);
  
  List<Map<String, Object>> listFutureMeetingByProjectIdForMobile(int projectId);
  
  Map<String, Object> listCompletedMeetingByProjectIdForMobile(int projectId,int pageIndex,int pageMax);
  
  Map<String, Object> listCompletedMeetingByUserIdForMobile(int userId,int pageIndex,int pageMax);
  
  void updateMeetingUser(int meetingId, int[] ids);
}
