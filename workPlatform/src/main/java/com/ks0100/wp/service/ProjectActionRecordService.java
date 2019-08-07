package com.ks0100.wp.service;

import java.util.List;
import java.util.Map;

import com.ks0100.wp.entity.ProjectActionRecord;

public interface ProjectActionRecordService {
   void save(ProjectActionRecord p);
   
   Map<String, List<ProjectActionRecord>> findProjectActionRecord(int projectId,String userIds,String types,String monday,String sunday);
   List<ProjectActionRecord> listProjectActionRecords(int projectId);
}
