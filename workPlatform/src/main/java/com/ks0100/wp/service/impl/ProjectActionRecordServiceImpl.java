package com.ks0100.wp.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks0100.wp.dao.ProjectActionRecordDao;
import com.ks0100.wp.entity.ProjectActionRecord;
import com.ks0100.wp.service.ProjectActionRecordService;

@Service
public class ProjectActionRecordServiceImpl implements ProjectActionRecordService {
	
	@Autowired
	private ProjectActionRecordDao projectActionRecordDao;
	public void save(ProjectActionRecord r){
		projectActionRecordDao.saveOrUpdate(r);
	}
	@Override
	public Map<String, List<ProjectActionRecord>> findProjectActionRecord(int projectId,
			String userIds, String types, String monday, String sunday) {
		List<ProjectActionRecord> list= projectActionRecordDao.findProjectDynamic(projectId, userIds, types, monday, sunday);
		Map<String, List<ProjectActionRecord>> parMap = new LinkedHashMap<String, List<ProjectActionRecord>>();
		for(ProjectActionRecord par :list){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(par.getCreatedTime());
			String dateString = new SimpleDateFormat("yyyy.MM.dd").format(calendar.getTime());
			String time = new SimpleDateFormat("HH:mm:ss").format(calendar.getTime());
			par.setCreatedTimeString(time);
			if(parMap.containsKey(dateString)){
				parMap.get(dateString).add(par);
			}else{
				List<ProjectActionRecord> parList = new ArrayList<ProjectActionRecord>();
				parList.add(par);
				parMap.put(dateString, parList);
			}
		}
		return parMap;
	}
	
	@Override
	public List<ProjectActionRecord> listProjectActionRecords(int projectId) {
		DateTimeFormatter format = DateTimeFormat .forPattern("yyyy-MM-dd HH:mm:ss");
		List<ProjectActionRecord> list = projectActionRecordDao.listProjectActionRecords(projectId);
		for(ProjectActionRecord record : list){
			record.setCreatedTimeString(new DateTime(record.getCreatedTime()).toString(format));
		}
		return list;
	}
}
