package com.ks0100.common.constant.read_enums;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SatatusEnumsRead {

  private static final Logger LOGGER = LoggerFactory.getLogger(SatatusEnumsRead.class);
  /**
   * 枚举类对应的包路径
   */
  public static final String PACKAGE_NAME = "com.ks0100.wp.constant";
  /**
   * 枚举接口类全路径
   */
  public static final String ENUM_MESSAGE_PATH = "com.ks0100.common.constant.read_enums.StatusEnumsInterface";

  /**
   * 枚举类对应的全路径集合
   */
  public static final List<String> ENUM_OBJECT_PATH = PackageUtil.getPackageClasses(PACKAGE_NAME, true);

  /**
   * 存放单个枚举对象 map常量定义
   */
  private static Map<String, StatusEnumsInterface> SINGLE_ENUM_MAP = null;
  /**
   * 所有枚举对象的 map
   */
  @SuppressWarnings("rawtypes")
  public static final Map<Class, Map<String, StatusEnumsInterface>> ENUM_MAP = new HashMap<Class, Map<String, StatusEnumsInterface>>();

  /**
   * 所有状态码为key的枚举对象map
   */
  public static final Map<String, StatusEnumsInterface> ENUM_KEY_MAP = new HashMap<String, StatusEnumsInterface>();

  private SatatusEnumsRead() {
  }

  /**
   * 加载所有枚举对象数据
   * @param  isFouceCheck 是否强制校验枚举是否实现了StatusEnumsInterface接口
   *
   * */
  @SuppressWarnings("rawtypes")
  static void initialEnumMap(boolean isFouceCheck) {
	//  Map<Class, Map<String, StatusEnumsInterface>> ENUM_MAP = new HashMap<Class, Map<String, StatusEnumsInterface>>();
	try {
	  for(String classname : ENUM_OBJECT_PATH) {
		Class<?> cls = null;
		cls = Class.forName(classname);
		Class<?>[] iter = cls.getInterfaces();
		boolean flag = false;
		if(isFouceCheck) {
		  for(Class cz : iter) {
			if(StatusEnumsInterface.class == cz) {
			  flag = true;
			  break;
			}
		  }
		}
		if(flag == isFouceCheck) {
		  SINGLE_ENUM_MAP = new HashMap<String, StatusEnumsInterface>();
		  initialSingleEnumMap(cls);
		  ENUM_MAP.put(cls, SINGLE_ENUM_MAP);
		}

	  }
	} catch(Exception e) {
	  LOGGER.error("Exception:", e);
	}
	// return ENUM_MAP;
  }

  /**
   * 加载每个枚举对象数据
   * @throws NoSuchMethodException 
   * @throws SecurityException 
   * @throws InvocationTargetException 
   * @throws IllegalAccessException 
   * @throws IllegalArgumentException 
   * */
  private static void initialSingleEnumMap(Class<?> cls) throws SecurityException,
	  NoSuchMethodException, IllegalArgumentException, IllegalAccessException,
	  InvocationTargetException {
	Method method = cls.getMethod("values");
	StatusEnumsInterface[] inter = (StatusEnumsInterface[])method.invoke(null, null);
	for(StatusEnumsInterface enumMessage : inter) {
	  SINGLE_ENUM_MAP.put(enumMessage.getCode(), enumMessage);
	  ENUM_KEY_MAP.put(enumMessage.getCode(), enumMessage);
	}
  }

}
