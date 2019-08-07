package com.ks0100.common.util;

import java.util.Iterator;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class MailUtil {
	private static final String DEFAULT_ENCODING = "utf-8";
	private static Logger logger = LoggerFactory.getLogger(MailUtil.class);

	private JavaMailSender mailSender;
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	private Configuration freemarkerConfiguration;
	public void setFreemarkerConfiguration(Configuration freemarkerConfiguration) {
		this.freemarkerConfiguration = freemarkerConfiguration;
	}
	
	/**
	 * @param toEmails 	{email:personal}
	 * @param cc    	{email:personal}
	 * @param pars
	 * @param templateName
	 */
	public boolean sendMail(String from,String subject,Map<String,String> toEmails,Map<String,String> cc,Map<String,Object> pars,String templateName) {
		logger.debug("email info: ");
		logger.debug("	from	:"+ from);
		logger.debug("	subject	:"+ subject);
		
		boolean flag = true;
		
		try {
			MimeMessage msg = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(msg, true, DEFAULT_ENCODING);
			
			if(toEmails != null && !toEmails.isEmpty()) {
			    Iterator<String> it = toEmails.keySet().iterator();
			    while(it.hasNext()) {
			        String email = (String) it.next();
			        if(StringUtils.isNotBlank(email)){
			        	String personal = (String) toEmails.get(email);
				        if(StringUtils.isEmpty(personal)) {
				        	personal = email;
				        }
				        logger.debug("	toEmails: {email,personal}={"+email+","+ personal+"}");
				        helper.addTo(email, personal);
			        }
			    }
			}  
			
			
			
			if(cc != null && !cc.isEmpty()) {
			    Iterator<String> it = cc.keySet().iterator();
			    while(it.hasNext()) {
			        String email = (String) it.next();
			        if(StringUtils.isNotBlank(email)){
				        String personal = (String) cc.get(email);
				        if(StringUtils.isEmpty(personal)) {
				        	personal = email;
				        }
				        helper.addCc(email, personal);
				        logger.debug("	cc: {email,personal}={"+email+","+ personal+"}");
			        }
			    }
			}  
			helper.setFrom(from);
			helper.setSubject(subject);

			Template template = freemarkerConfiguration.getTemplate(templateName+".ftl",DEFAULT_ENCODING);
			String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, pars);
			logger.debug("	text: "+content);
			helper.setText(content, true);

		
//			Resource resource = new ClassPathResource("/email/attachment.txt");
//			File attachment = resource.getFile();
//			helper.addAttachment("attachment.txt", attachment);
			
			mailSender.send(msg);
		} catch (MessagingException e) {
			flag = false;
			logger.error("create error", e);
		} catch (Exception e) {
			flag = false;
			logger.error("send error", e); 
		}
		
		return flag;
	}
}
