package com.ks0100.wp.service;

import java.util.List;
import java.util.Map;

import com.ks0100.wp.ShiroUser;
import com.ks0100.wp.dto.MeetingDto;
import com.ks0100.wp.dto.MeetingListDto;
import com.ks0100.wp.entity.Meeting;

public interface MeetingService {
  List<Meeting> listMeetingsSortByBegingTime(int projectId) throws Exception;

  void deleteMeetingUser(int meetingId, int[] ids);

  void addMeetingUser(int meetingId, int[] ids);

  Meeting findMeetingById(int meetingId);
  
  MeetingDto findMeetingDetailByIdForMobile(int meetingId);

  void updateMeeting(Meeting temp);

  void saveMeeting(Meeting temp,int userId);
  
  void saveMeetingForMobile(Meeting meeting,ShiroUser user);

  void addMeetingUserRole(Meeting meeting, ShiroUser user, String roleCode);
  
  void updateMeetingUser(int meetingId, int[] ids);
  
  void updateMeetingForMobile(Meeting meeting);

  /**
   * 删除会议
   *
   * @param path(删除服务器上的附件时所用到的路径)
   * @param meetingId
   * 创建日期：2014-12-25
   * 修改说明：
   * @author chengls
   */
  void deleteMeeting(String path, Meeting meeting);
  
  List<MeetingListDto> listFutureMeetingByUserIdForMobile(int userId);
  
  List<MeetingListDto> listFutureMeetingByProjectIdForMobile(int projectId);
  
  Map<String, Object> listCompletedMeetingByUserIdForMobile(int userId,int pageIndex,int pageMax);
  
  Map<String, Object> listCompletedMeetingByProjectIdForMobile(int projectId,int pageIndex,int pageMax);

}
