package com.ks0100.wp.service;


import static org.assertj.core.api.Assertions.assertThat;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.ks0100.wp.dao.ProjectDao;
import com.ks0100.wp.entity.Project;
import com.ks0100.wp.test.spring.SpringTransactionalTestCase;


@ContextConfiguration(locations = { "/spring/applicationContext.xml" })
public class ServiceTest extends SpringTransactionalTestCase{
	@Autowired
	public ProjectDao projectDao;
	
	@Test
	public void test(){
		boolean a=true;
		boolean b=false;
		assertThat(a).isNotEqualTo(b);
	}
	
	@Test
	public void test2() throws Exception{
		
		Project project = projectDao.findUniqueBy("uuid", "3ef042ea-cd44-4435-bc75-4b1205722173");
		ObjectMapper om=new ObjectMapper();
		String jsonStr=om.writeValueAsString(project);
		logger.info("jsonStr:"+jsonStr);
	}

	
	
}
