package com.ks0100.common.constant.tag;

import com.ks0100.common.constant.read_enums.EnumUtil;


public class EnumTag {

  private EnumTag() {
  }

  public static String getDescByCode(String code) {
	return EnumUtil.getEnumObject(code).getDesc();
  }

  public static String getTextByCode(String code) {
		return EnumUtil.getEnumObject(code).getText();
  }

  public static String getText1ByCode(String code) {
		return EnumUtil.getEnumObject(code).getText1();
  }
  
  public static String getTableByCode(String code) {
		return EnumUtil.getEnumObject(code).getTable();
  }

  public static String getColumnByCode(String code) {
		return EnumUtil.getEnumObject(code).getColumn();
  }
  
  public static String getClassNameByCode(String code) {
		return EnumUtil.getEnumObject(code).getClassName();
  }
  

}
