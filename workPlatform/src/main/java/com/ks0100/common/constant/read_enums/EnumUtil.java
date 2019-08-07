package com.ks0100.common.constant.read_enums;

import com.ks0100.wp.constant.StatusEnums.PrjReportType;
import com.ks0100.wp.constant.StatusEnums.UserGender;

public class EnumUtil {
  /**
   * 根据code和枚举类型，返回枚举对象
   * @param value
   * @param clazz
   * */
  @SuppressWarnings("unchecked")
  public static <T extends StatusEnumsInterface> T getEnumObject(String code, Class<T> clazz) {
	if(SatatusEnumsRead.ENUM_MAP.isEmpty()) {
	  SatatusEnumsRead.initialEnumMap(true);
	}
	return (T)SatatusEnumsRead.ENUM_MAP.get(clazz).get(code);
  }

  /**
   * 获取code返回枚举对象
   * @param value
   * @param clazz
   * */
  public static StatusEnumsInterface getEnumObject(String code) {
	if(SatatusEnumsRead.ENUM_KEY_MAP.isEmpty()) {
	  SatatusEnumsRead.initialEnumMap(true);
	}
	return SatatusEnumsRead.ENUM_KEY_MAP.get(code);
  }
  
  

  //测试方法失败，注意PackageUtil private static List<String> getClassName(String packageName) 的路径问题，代码注释切换到java application 测试
  public static void main(String[] args) {
	        System.out.println(EnumUtil.getEnumObject("00901", PrjReportType.class).getText());
	        System.out.println(EnumUtil.getEnumObject("00802", UserGender.class).getText());
	        System.out.println(EnumUtil.getEnumObject("00800", UserGender.class).CLASS);
	  		System.out.println(EnumUtil.getEnumObject("00300").getText());
	        System.out.println(EnumUtil.getEnumObject("00301").getClassName());
	        System.out.println(EnumUtil.getEnumObject("00301").getColumn());
  }

}
