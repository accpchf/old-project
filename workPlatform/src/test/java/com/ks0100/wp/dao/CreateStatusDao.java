package com.ks0100.wp.dao;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import com.ks0100.common.constant.create_enums.CreateStatusEnums;
import com.ks0100.common.constant.create_enums.Status;
import com.ks0100.wp.test.spring.SpringContextTestCase;

@ContextConfiguration(locations = { "/spring/applicationContext.xml" })
public class CreateStatusDao extends SpringContextTestCase{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void findSysStatus(){
		 String sql = "   SELECT    status_id id,status_class statusClass,status_content content,status_content1 content1,status_desc sdesc,status_table stable,status_column scolumn,"
				 +" status_enum_name enumName,status_enum_variable enumVariable  FROM tbl_sys_status order by status_id  ";
		 
		 List<Status> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Status.class));
	     
/*	     for(Status s:list){
	    	 System.out.println("id:"+s.getId()+";statusClass:"+s.getStatusClass()+";content:"+s.getContent()+";content1:"+s.getContent1()+
	    			 ";sdesc:"+s.getSdesc()+";stable:"+s.getStable()+";scolumn:"+s.getScolumn()+";enumName:"+s.getEnumName()+";enumVariable:"+s.getEnumVariable());
	     }*/
		 //\\workPlatform\\src\\main\\java\\com\\zdksii\\pms\wp\\constant\\StatusEnums.java
	     CreateStatusEnums.createClass(CreateStatusEnums.toSqlFromText(list, "com.ks0100.wp.constant"),
	    		 "D:\\eclipse_workspace\\innovation\\workPlatform\\src\\main\\java\\com\\zdksii\\pms\\wp\\constant\\StatusEnums.java");
	}

}
