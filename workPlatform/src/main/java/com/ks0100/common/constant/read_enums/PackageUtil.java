package com.ks0100.common.constant.read_enums;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ks0100.common.util.PathUtil;

public class PackageUtil {

  /**
   * 返回包下所有的类
   * @param packagePath 包名
   * @return List<String> 包下所有的类
   * */
  public static List<String> getPackageClasses(String packagePath) {

	return getPackageClasses(packagePath, false);
  }

  /**
   * 返回包下所有的类
   * @param packagePath 包名全路径
   * @param classWithPath 返回全路径开关 true 自动带上包名
   * @return List<String> 包下所有的类
   * */
  public static List<String> getPackageClasses(String packagePath, boolean classWithPath) {

	List<String> classNames = getClassName(packagePath);
	List<String> result = new ArrayList<String>(classNames.size());
	String path = classWithPath ? packagePath + "." : "";
	for(String className : classNames) {
	  result.add(path + className.substring(className.lastIndexOf(".") + 1));
	}
	return result;
  }

  //注意路径
  private static List<String> getClassName(String packageName) {
	//String filePath = ClassLoader.getSystemResource("").getPath() + packageName.replace(".", "\\"); //用在java application 测试
	String filePath = PathUtil.ABSOLUTE_CLASS_PATH + "\\" + packageName.replace(".", "\\");//用在web项目
	List<String> fileNames = getClassName(filePath, null);
	return fileNames;
  }

  private static List<String> getClassName(String filePath, List<String> className) {
	List<String> myClassName = new ArrayList<String>();
	File file = new File(filePath);
	File[] childFiles = file.listFiles();
	for(File childFile : childFiles) {
	  if(childFile.isDirectory()) {
		myClassName.addAll(getClassName(childFile.getPath(), myClassName));
	  } else {
		String childFilePath = childFile.getPath();
		childFilePath = childFilePath.substring(childFilePath.indexOf("\\classes") + 9,
			childFilePath.lastIndexOf("."));
		childFilePath = childFilePath.replace("\\", ".");
		myClassName.add(childFilePath);
	  }
	}

	return myClassName;
  }

}
