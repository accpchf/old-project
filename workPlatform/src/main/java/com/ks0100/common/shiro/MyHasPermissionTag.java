package com.ks0100.common.shiro;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.tags.HasPermissionTag;

public class MyHasPermissionTag extends HasPermissionTag {

  /**
   * 
   */
  private static final long serialVersionUID = 1211485172615925490L;
  private Integer objectId;

  public MyHasPermissionTag() {
  }

  @Override
  public int onDoStartTag() throws JspException {
	String p = getName();
	Integer id = getObjectId();
	boolean show = showTagBody(p, id);
	return show ? EVAL_BODY_INCLUDE : SKIP_BODY;
  }

  protected boolean showTagBody(String p, Integer id) {
	if(StringUtils.isNotBlank(p) && id != null && id.intValue() > 0) {
	  return isPermitted(p + ":" + id);
	}
	return false;
  }

  public Integer getObjectId() {
	return this.objectId;
  }

  public void setObjectId(Integer objectId) {
	this.objectId = objectId;
  }

}
