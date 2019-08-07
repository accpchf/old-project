package com.ks0100.common.constant.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.ks0100.wp.constant.StatusEnums.ProjectSatus;

public class StatusTag extends TagSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 894779771904317257L;


	private String status;



	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public StatusTag() {
	}

	@Override
	public int doStartTag() throws JspException {
		String status = getStatus();
		boolean show = false;
		if (status.equals(ProjectSatus.DOING.getCode())) {
			show = true;
		}
		return show ? EVAL_BODY_INCLUDE : SKIP_BODY;
	}


}
