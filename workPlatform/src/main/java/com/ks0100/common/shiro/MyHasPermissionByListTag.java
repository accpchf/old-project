package com.ks0100.common.shiro;

import java.util.List;

import javax.servlet.jsp.JspException;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.tags.HasPermissionTag;

public class MyHasPermissionByListTag extends HasPermissionTag {
  private static final long serialVersionUID = -2437869152845673528L;

  private String name = "";

  @Override
  public String getName() {
	return this.name;
  }

  @Override
  public void setName(String name) {
	this.name = name;
  }

  private List<String> permitCodeAndIds;

  @Override
  public int onDoStartTag() throws JspException {
	List<String> permitCodeAndIds = getPermitCodeAndIds();
	boolean show = hasPermit(permitCodeAndIds);
	return show ? EVAL_BODY_INCLUDE : SKIP_BODY;
  }

  protected boolean hasPermit(List<String> permitCodeAndIds) {
	boolean hasPermit = false;
	if(permitCodeAndIds != null) {
	  for(String pi : permitCodeAndIds) {
		if(SecurityUtils.getSubject().isPermitted(pi)) {
		  hasPermit = true;
		  break;
		}
	  }
	}
	return hasPermit;
  }

  public List<String> getPermitCodeAndIds() {
	return this.permitCodeAndIds;
  }

  public void setPermitCodeAndIds(List<String> permitCodeAndIds) {
	this.permitCodeAndIds = permitCodeAndIds;
  }

}
