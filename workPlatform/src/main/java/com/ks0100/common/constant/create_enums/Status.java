package com.ks0100.common.constant.create_enums;

public class Status {

  private String id;
  private String statusClass;
  private String content;
  private String content1;
  private String sdesc;
  private String stable;
  private String scolumn;
  private String enumName;
  private String enumVariable;

  public Status(){
	  
  }
  /*
   * SELECT  CONCAT(status_id,',', status_class,',', status_content,',',ifnull(status_content1,'null'),',', status_desc,',',status_table,',',status_column,',', status_enum_name,',', status_enum_variable) 
   FROM tbl_sys_status;
   */
  public Status(String[] temp) {
	this.id = temp[0];
	this.statusClass = temp[1];
	this.content = temp[2];
	this.content1 = temp[3].equals("null") ? "" : temp[3];//如果数据库里是空值，转换为null字符串
	this.sdesc = temp[4];
	this.stable = temp[5];
	this.scolumn = temp[6];
	this.enumName = temp[7];
	this.enumVariable = temp[8];
  }

  public String getId() {
	return id;
  }

  public void setId(String id) {
	this.id = id;
  }

  public String getStatusClass() {
	return statusClass;
  }

  public void setStatusClass(String statusClass) {
	this.statusClass = statusClass;
  }

  public String getContent() {
	return content;
  }

  public void setContent(String content) {
	this.content = content;
  }


  public String getSdesc() {
	return sdesc;
}

public void setSdesc(String sdesc) {
	this.sdesc = sdesc;
}

public String getStable() {
	return stable;
}

public void setStable(String stable) {
	this.stable = stable;
}


  public String getScolumn() {
	return scolumn;
}

public void setScolumn(String scolumn) {
	this.scolumn = scolumn;
}

public String getEnumName() {
	return enumName;
  }

  public void setEnumName(String enumName) {
	this.enumName = enumName;
  }

  public String getEnumVariable() {
	return enumVariable;
  }

  public void setEnumVariable(String enumVariable) {
	this.enumVariable = enumVariable;
  }

  public String getContent1() {
	return content1;
  }

  public void setContent1(String content1) {
	this.content1 = content1;
  }

}
