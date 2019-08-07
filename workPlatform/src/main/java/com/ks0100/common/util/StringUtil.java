package com.ks0100.common.util;

import java.util.Collection;
import java.util.Random;
import java.util.UUID;

import org.joda.time.DateTime;

/**
 * 字符串工具类
 * 
 */
public class StringUtil {

  /**
   * 将集合转换为拼接的字符串
   * 
   * @param collection
   *            字符串集合
   * @return 拼接后的字符串
   */
  public static String convertCollectionString(Collection<String> collection) {
	StringBuilder sb = new StringBuilder();
	if(!collection.isEmpty()) {
	  for(String str : collection) {
		sb.append("'" + str + "'").append(",");
	  }
	}
	if(sb.length() > 0)
	  sb.deleteCharAt(sb.length() - 1);
	return sb.toString();
  }

  /**
   * 将集合转换为拼接的字符串
   * 
   * @param collection
   *            字符串集合
   * @return 拼接后的字符串
   */
  public static String convertCollectionInteger(Collection<Integer> collection) {
	StringBuilder sb = new StringBuilder();
	if(!collection.isEmpty()) {
	  for(Integer integer : collection) {
		sb.append(integer).append(",");
	  }
	}
	if(sb.length() > 0)
	  sb.deleteCharAt(sb.length() - 1);
	return sb.toString();
  }

  /**
   * 获取随机数
   * 
   * @return
   */
  public static String getRandom() {
	Random r = new Random();
	DateTime dt1 = new DateTime();
	//String date = DateUtil.formatDatetime(Calendar.getInstance().getTime(), "yyyyMMddHHmmss");
	String lsh = String.valueOf(r.nextInt(999999));
	String result = dt1.getMillis() + lsh;
	return result;
  }

  /**
   * 获取uuid
   * @return
   */
  public static String getUuid() {
	return UUID.randomUUID().toString().replace("-", "");
  }

  public static String getFileMB(long byteFile) {
	if(byteFile == 0)
	  return "0MB";
	long mb = 1024L * 1024;
	return "" + byteFile / mb + "MB";
  }

}
