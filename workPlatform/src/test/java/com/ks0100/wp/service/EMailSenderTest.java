package com.ks0100.wp.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.ks0100.common.util.MailUtil;
import com.ks0100.wp.test.spring.SpringTransactionalTestCase;

@ContextConfiguration(locations = { "/spring/applicationContext.xml" })
public class EMailSenderTest extends SpringTransactionalTestCase{

	@Autowired
	private MailUtil mailUtil;

	@Test
	public void testSendMail() {
		Map<String,Object> pars = new HashMap<String, Object>();
		pars.put("userName", "testadmin");
		pars.put("NO", 1000);
		Map<String,String> emails = new HashMap<String, String>();
		emails.put("1536021562@qq.com", "陈海峰");
		
		Map<String,String> cc = new HashMap<String, String>();
		cc.put("accpchf@qq.com", "陈海峰2");
		
		//mailUtil.sendMail("accpchf@qq.com","工作平台邀请",emails, cc, pars,"email_test");
	}

}
